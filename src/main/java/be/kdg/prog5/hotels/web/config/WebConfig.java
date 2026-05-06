package be.kdg.prog5.hotels.web.config;

import be.kdg.prog5.hotels.web.converters.StringToLocalDateConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

// configures Spring MVC behavior such as converters, interceptors, and internationalization.
@Configuration         // marks this class as a Spring configuration class that defines beans and MVC setup
public class WebConfig implements WebMvcConfigurer {

    private final StringToLocalDateConverter converter;

    // Constructor injection
    public WebConfig(StringToLocalDateConverter converter) {
        this.converter = converter;
    }

    // Converter Registration
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(converter);
    }

    // enables internationalization using cookies instead of sessions
    @Bean
    public LocaleResolver localeResolver() {
        // create Cookie-based locale resolver
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);  // Default to English
        resolver.setCookieName("lang");   // Remember user choice in cookie
        resolver.setCookieMaxAge(60 * 60 * 24 * 30); // remember language for 30 days
        return resolver;
    }

    // interceptor switches the UI language based on a request parameter
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        // Intercepts requests and changes language if ?lang=xx is present
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    // Interceptors run before controllers to add cross-cutting behavior
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // register the interceptor so it works for all URLs
        registry.addInterceptor(localeChangeInterceptor());
    }

    // Week 10: allow only the separate webpack Client app to call the REST API from the browser
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:9000")
                .allowedMethods("GET", "POST")
                .allowedHeaders("*");
    }
}