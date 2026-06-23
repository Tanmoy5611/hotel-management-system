package be.kdg.prog5.hotels.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableMethodSecurity            // Enables method-level security
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // define which pages are accessible to which roles
                .authorizeHttpRequests(auth -> auth

                        // ADMIN pages (hasRole)
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Only ADMIN can delete hotels, rooms, guests
                        .requestMatchers(
                                "/hotels/add",
                                "/rooms/add",
                                "/hotels/*/delete",
                                "/rooms/*/delete"
                        ).hasRole("ADMIN")

                        // signed-in (hasANyRole) staff/admin only for Guests
                        .requestMatchers(
                                "/guests",
                                "/guests/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // Public (permitAll) pages are read-only: anonymous users may view them, but mutation methods stay protected
                        .requestMatchers(HttpMethod.GET,
                                "/",
                                "/home",
                                "/search",
                                "/login",
                                "/hotels",
                                "/hotels/*",
                                "/rooms",
                                "/rooms/*"
                        ).permitAll()

                        // Static frontend assets are always public
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/fonts/**",
                                "/images/**",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()

                        // REST API rules
                        // anyone can GET data (permitAll)
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()

                        // Week 10: permitAll only so the separate Client project can create guests without login
                        .requestMatchers(HttpMethod.POST, "/api/guests").permitAll()

                        // only logged users (authenticated) can modify data
                        .requestMatchers(HttpMethod.POST, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()

                        // everything else requires login
                        .anyRequest().authenticated()
                )

                // custom login page configuration
                .formLogin(form -> form
                        .loginPage("/login")
                        // after login redirect to home
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )

                // API clients need a status code instead of an HTML login-page redirect
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, exception) -> {
                            if (request.getRequestURI().startsWith(request.getContextPath() + "/api/")) {
                                response.setStatus(HttpStatus.FORBIDDEN.value());
                            } else {
                                response.sendRedirect(request.getContextPath() + "/login");
                            }
                        })
                )

                // Week 10: CSRF is ignored only for this endpoint because it is called by the separate Client project
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        antMatcher(HttpMethod.POST, "/api/guests")
                ))

                // logout configuration
                .logout(logout -> logout
                        .logoutSuccessUrl("/home")
                        .permitAll()
                )

                // additional security headers
                .headers(headers -> headers     // improves browser security
                        // Prevents clickjacking (tricking through fake invisible iframe) attacks
                        .frameOptions(frame -> frame.sameOrigin())
                        // Prevents browsers from guessing file types incorrectly (Protects against MIME sniffing attacks)
                        .contentTypeOptions(Customizer.withDefaults())
                );

        return http.build();
    }
}