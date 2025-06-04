package com.wizard.core.config;

import com.wizard.core.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("🔧 [SECURITY] Configuring Security Filter Chain...");
        
        http
            // ✅ CRÍTICO: Deshabilitar CSRF - Principal causa del 403
            .csrf(csrf -> {
                csrf.disable();
                log.info("🔒 [SECURITY] CSRF protection DISABLED");
            })
            
            // ✅ CONFIGURAR RUTAS PERMITIDAS
            .authorizeHttpRequests(auth -> {
                auth
                    // Endpoints públicos - NO requieren autenticación
                    .requestMatchers(
                        "/ping",                    // Ping básico
                        "/api/auth/**",            // ✅ CORRECTO: /api/auth/login
                        "/api/test/**",  // Endpoints de testing
                         "/api/**",
                        "/api/pacs/**",
                         "/api/users/**",   
                        "/swagger-ui/**",          // Swagger UI
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/configuration/**",
                        "/error",                  // ✅ IMPORTANTE: Spring Boot error page
                        "/actuator/health"         // Health check
                    ).permitAll()
                    
                    // Todo lo demás requiere autenticación
                    .anyRequest().authenticated();
                    
                log.info("🔓 [SECURITY] Public endpoints configured: /ping, /api/auth/**, /api/test/**");
            })
            
            // ✅ STATELESS para API REST
            .sessionManagement(sess -> {
                sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                log.info("📝 [SECURITY] Session management: STATELESS");
            })
            
            // ✅ Deshabilitar form login para API REST
            .formLogin(form -> form.disable())
            
            // ✅ Deshabilitar HTTP Basic
            .httpBasic(basic -> basic.disable());
            
        log.info("✅ [SECURITY] Security Filter Chain configured successfully");
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        log.info("🔐 [SECURITY] Creating AuthenticationManager...");
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("🔒 [SECURITY] Creating BCryptPasswordEncoder...");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        log.info("👤 [SECURITY] Configuring UserDetailsService...");
        return userDetailsService;
    }
}