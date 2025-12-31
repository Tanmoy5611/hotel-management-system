package be.kdg.prog3.hotels;

import be.kdg.prog3.hotels.web.converters.StringToLocalDateConverter;
import be.kdg.prog3.hotels.web.interceptors.HistoryInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final StringToLocalDateConverter converter;
    private final HistoryInterceptor historyInterceptor;

    // Constructor injection
    public WebConfig(StringToLocalDateConverter converter, HistoryInterceptor historyInterceptor) {
        this.converter = converter;
        this.historyInterceptor = historyInterceptor;
    }

    // Converter Registration
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(converter);
    }

    @Bean
    public LocaleResolver localeResolver() {
        // create Cookie-based locale resolver
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);  // Default to English
        resolver.setCookieName("lang");   // Remember user choice in cookie
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        // Intercepts requests and changes language if ?lang=xx is present
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // register the interceptor so it works for all URLs
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(historyInterceptor);
    }
}