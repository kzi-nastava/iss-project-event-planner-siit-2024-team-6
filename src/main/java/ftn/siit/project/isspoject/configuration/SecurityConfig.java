package ftn.siit.project.isspoject.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Отключение CSRF, если не требуется
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll() // Разрешить доступ к API
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Разрешить OPTIONS запросы
                        .anyRequest().authenticated() // Все остальные запросы требуют авторизации
                );

        return http.build();
    }
}
