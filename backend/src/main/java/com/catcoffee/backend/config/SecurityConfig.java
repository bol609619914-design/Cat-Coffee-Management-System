package com.catcoffee.backend.config;

import com.catcoffee.backend.security.JwtAuthenticationFilter;
import com.catcoffee.backend.security.RestAccessDeniedHandler;
import com.catcoffee.backend.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties({SecurityProperties.class, StorageProperties.class})
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/auth/refresh",
                                "/uploads/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/dashboard").hasAuthority("dashboard:view")
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/password/change").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/cats/**").hasAuthority("cat:read")
                        .requestMatchers(HttpMethod.POST, "/api/v1/cats").hasAuthority("cat:write")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/cats/**").hasAuthority("cat:delete")
                        .requestMatchers(HttpMethod.GET, "/api/v1/drinks/**").hasAuthority("drink:read")
                        .requestMatchers(HttpMethod.POST, "/api/v1/drinks").hasAuthority("drink:write")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/drinks/**").hasAuthority("drink:delete")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tables/**").hasAuthority("table:read")
                        .requestMatchers(HttpMethod.POST, "/api/v1/tables").hasAuthority("table:write")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tables/**").hasAuthority("table:delete")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reservations/**").hasAuthority("reservation:read")
                        .requestMatchers(HttpMethod.POST, "/api/v1/reservations").hasAuthority("reservation:write")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/reservations/**").hasAuthority("reservation:delete")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/**").hasAuthority("order:read")
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders").hasAuthority("order:write")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/**").hasAuthority("order:delete")
                        .requestMatchers(HttpMethod.GET, "/api/v1/system/users/**").hasAuthority("system:user:read")
                        .requestMatchers(HttpMethod.POST, "/api/v1/system/users").hasAuthority("system:user:write")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/system/users/**").hasAuthority("system:user:delete")
                        .requestMatchers(HttpMethod.GET, "/api/v1/system/roles/**").hasAuthority("system:role:read")
                        .requestMatchers(HttpMethod.POST, "/api/v1/system/roles").hasAuthority("system:role:write")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/system/roles/**").hasAuthority("system:role:delete")
                        .requestMatchers(HttpMethod.GET, "/api/v1/system/permissions/**").hasAuthority("system:permission:read")
                        .requestMatchers(HttpMethod.POST, "/api/v1/system/permissions").hasAuthority("system:permission:write")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/system/permissions/**").hasAuthority("system:permission:delete")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> User.withUsername(username)
                .password("{noop}")
                .authorities("NO_PERMISSION")
                .build();
    }
}
