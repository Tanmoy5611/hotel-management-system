package be.kdg.prog5.hotels.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@EnableCaching
public class ApplicationConfig {
    // Enables Spring @Async and caching support for the Week 12 features
}