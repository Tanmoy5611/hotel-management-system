package be.kdg.prog5.hotels.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        // ADMIN pages
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Only ADMIN can delete hotels, rooms, guests
                        .requestMatchers(
                                "/hotels/add",
                                "/rooms/add",
                                "/hotels/*/delete",
                                "/rooms/*/delete"
                        ).hasRole("ADMIN")

                        // signed-in staff/admin only for Guests
                        .requestMatchers(
                                "/guests",
                                "/guests/**"
                        ).hasAnyRole("USER", "ADMIN")


                        // Public pages accessible without login
                        .requestMatchers(
                                "/",
                                "/home",
                                "/login",
                                "/hotels",
                                "/hotels/*",
                                "/rooms",
                                "/rooms/*",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()

                        // REST API rules
                        // anyone can GET data
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()

                        // only logged users can modify data
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

                // logout configuration
                .logout(logout -> logout
                        .logoutSuccessUrl("/home")
                        .permitAll()
                )


                // additional security headers
                .headers(headers -> headers
                        // Prevents clickjacking attacks
                        .frameOptions(frame -> frame.sameOrigin())
                        // Prevents browsers from guessing file types incorrectly (Protects against MIME sniffing attacks)
                        .contentTypeOptions(content -> {
                        })
                );

        return http.build();
    }
}