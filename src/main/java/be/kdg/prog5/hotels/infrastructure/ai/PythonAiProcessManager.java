package be.kdg.prog5.hotels.infrastructure.ai;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class PythonAiProcessManager {

    private static final Logger log = LoggerFactory.getLogger(PythonAiProcessManager.class);

    // Auto-start keeps the user workflow simple: running MainApplication also starts AI
    private final boolean autoStart;

    // Paths are configured so the service can run from the project root without hard-coded absolute paths
    private final Path serviceDirectory;
    private final Path configuredPythonExecutable;

    // Health endpoint is used to avoid starting a duplicate Python process
    private final URI healthUri;
    private final int startupTimeoutSeconds;
    private final HttpClient httpClient;

    // Stored only when Spring starts the process so it can be stopped on shutdown
    private Process aiProcess;

    public PythonAiProcessManager(@Value("${hotel.ai.service.auto-start:true}") boolean autoStart,
                                  @Value("${hotel.ai.service.working-directory:hotel-ai-service}") String serviceDirectory,
                                  @Value("${hotel.ai.service.python-executable:hotel-ai-service/.venv/bin/python3}") String pythonExecutable,
                                  @Value("${hotel.ai.service.base-url:http://localhost:8001}") String baseUrl,
                                  @Value("${hotel.ai.service.startup-timeout-seconds:20}") int startupTimeoutSeconds) {
        this.autoStart = autoStart;
        this.serviceDirectory = Path.of(serviceDirectory).toAbsolutePath().normalize();
        this.configuredPythonExecutable = Path.of(pythonExecutable).toAbsolutePath().normalize();
        this.healthUri = URI.create(baseUrl + "/health");
        this.startupTimeoutSeconds = startupTimeoutSeconds;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startPythonAiService() {
        // Tests disable auto-start so they do not depend on Python or open a port
        if (!autoStart) {
            log.info("Python AI service auto-start is disabled.");
            return;
        }

        // If the developer already started uvicorn manually, reuse that service
        if (isHealthy()) {
            log.info("Python AI service is already running at {}", healthUri);
            return;
        }

        // Missing service folder should not crash the Spring application
        if (!Files.isDirectory(serviceDirectory)) {
            log.warn("Python AI service directory does not exist: {}", serviceDirectory);
            return;
        }

        String pythonExecutable = resolvePythonExecutable();
        List<String> command = new ArrayList<>();
        // Run FastAPI through python -m uvicorn so the selected virtualenv controls imports
        command.add(pythonExecutable);
        command.add("-m");
        command.add("uvicorn");
        command.add("app.main:app");
        command.add("--host");
        command.add("127.0.0.1");
        command.add("--port");
        command.add("8001");

        try {
            // Working directory must be hotel-ai-service so app.main can be imported correctly
            ProcessBuilder processBuilder = new ProcessBuilder(command)
                    .directory(serviceDirectory.toFile())
                    .redirectErrorStream(true);
            aiProcess = processBuilder.start();
            streamProcessOutput(aiProcess);
            waitUntilHealthy();
        } catch (IOException ex) {
            // Startup failures are logged because the UI already handles the service as unavailable
            log.warn("Could not start Python AI service. Run pip install -r hotel-ai-service/requirements.txt once.", ex);
        }
    }

    @PreDestroy
    public void stopPythonAiService() {
        // Only stop the child process created by this Spring instance
        if (aiProcess != null && aiProcess.isAlive()) {
            log.info("Stopping Python AI service started by Spring.");
            aiProcess.destroy();
        }
    }

    private String resolvePythonExecutable() {
        // Prefer the configured virtualenv path for reproducible dependencies
        if (Files.isExecutable(configuredPythonExecutable)) {
            return configuredPythonExecutable.toString();
        }

        // Fallback supports the normal project-local venv even if the property changed
        Path venvPython = serviceDirectory.resolve(".venv/bin/python3");
        if (Files.isExecutable(venvPython)) {
            return venvPython.toString();
        }

        log.warn("No hotel-ai-service .venv Python executable found. Falling back to system python3.");
        return "python3";
    }

    private void waitUntilHealthy() {
        // Polling avoids sending chat or recommendation requests before FastAPI is ready
        long deadline = System.nanoTime() + Duration.ofSeconds(startupTimeoutSeconds).toNanos();

        while (System.nanoTime() < deadline) {
            if (isHealthy()) {
                log.info("Python AI service started at {}", healthUri);
                return;
            }

            if (aiProcess != null && !aiProcess.isAlive()) {
                // If Python exits early, waiting longer cannot make the service healthy
                log.warn("Python AI service process stopped before becoming healthy.");
                return;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                // Preserve interrupt status so Spring shutdown remains responsive
                Thread.currentThread().interrupt();
                return;
            }
        }

        log.warn("Python AI service did not become healthy within {} seconds.", startupTimeoutSeconds);
    }

    private boolean isHealthy() {
        try {
            // Lightweight GET request checks service readiness without invoking ML endpoints
            HttpRequest request = HttpRequest.newBuilder(healthUri)
                    .timeout(Duration.ofSeconds(2))
                    .GET()
                    .build();
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                // Do not swallow thread interruption when health checks are cancelled
                Thread.currentThread().interrupt();
            }
            return false;
        }
    }

    private void streamProcessOutput(Process process) {
        // Pipe Python logs into Spring logs so one terminal shows both services
        Thread outputThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Prefix logs to make Python output easy to identify inside bootRun
                    log.info("[hotel-ai-service] {}", line);
                }
            } catch (IOException ex) {
                log.debug("Stopped reading Python AI service output.", ex);
            }
        }, "hotel-ai-service-output");
        outputThread.setDaemon(true);
        outputThread.start();
    }
}