package ftn.siit.project.isspoject.configuration;

import ftn.siit.project.isspoject.security.auth.RestAuthenticationEntryPoint;
import ftn.siit.project.isspoject.security.auth.TokenAuthenticationFilter;
import ftn.siit.project.isspoject.service.external.CustomUserDetailsService;
import ftn.siit.project.isspoject.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;


@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
public class WebSecurityConfig{

    // Обработчик, который возвращает статус 401 (Unauthorized),
// если клиент с неправильным именем пользователя или паролем пытается получить доступ к ресурсу.
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    // Внедряем реализацию класса TokenUtils для работы с JWT токенами в TokenAuthenticationFilter.
    @Autowired
    private TokenUtils tokenUtils;

    // Сервис, используемый для получения данных о пользователях приложения.
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    // Реализация PasswordEncoder с использованием функции хеширования BCrypt.
// По умолчанию BCrypt выполняет 10 раундов хеширования переданного значения.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // 1. Указываем, какой сервис использовать для извлечения данных о пользователе,
        // который хочет аутентифицироваться. AuthenticationManager автоматически вызовет метод loadUserByUsername() этого сервиса.
        authProvider.setUserDetailsService(userDetailsService());
        // 2. Указываем, через какой энкодер пропустить пароль, полученный от клиента,
        // чтобы хеш, полученный в результате алгоритма хеширования, можно было сравнить с тем, что хранится в базе (так как пароли в базе не хранятся в открытом виде).
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Регистрируем AuthenticationManager, который выполнит аутентификацию пользователя.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Определяем права доступа для запросов к определённым URL или маршрутам.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf((csrf) -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(restAuthenticationEntryPoint));
        http.authorizeHttpRequests(request -> {
            request.requestMatchers(new AntPathRequestMatcher("/api/users/login")).permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/users/profile")).authenticated()
                    .requestMatchers(new AntPathRequestMatcher("/api/users")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/users/quick-register")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/events/event-types")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("api/events/**/event-type")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/users/profile/password-change")).authenticated()
                    .requestMatchers(new AntPathRequestMatcher("/api/admins/event-types")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/admins/event-types/**")).hasRole("ADMIN")
                    .requestMatchers(new AntPathRequestMatcher("/api/admins/categories")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/admins/event-types/**/**")).hasRole("ADMIN")
                    .requestMatchers(new AntPathRequestMatcher("/api/organizers/**")).hasRole("ORGANIZER")
                    .requestMatchers(new AntPathRequestMatcher("/api/organizers/events")).hasRole("ORGANIZER")
                    .requestMatchers(new AntPathRequestMatcher("/api/organizers/events/**")).hasRole("ORGANIZER")
                    .requestMatchers(new AntPathRequestMatcher("/api/organizers/events/**/getAgendaPDF")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/events/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/offers/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/offers/**/favorite")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/events/**/favorite")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/events/**/getInfoPDF")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/events/**/event-types-by-category")).hasAnyRole("ADMIN","PROVIDER")
                    .requestMatchers(new AntPathRequestMatcher("/api/events/**/getEventStatisticsPDF")).hasAnyRole("ADMIN","ORGANIZER")
                    .requestMatchers(new AntPathRequestMatcher("/api/events/**/getOrganizer")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/organizers/events/**/activities")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/api/organizers/events/**/activity")).hasRole("ORGANIZER")
                    .requestMatchers(new AntPathRequestMatcher("/api/organizers/events/**/activity/**")).hasRole("ORGANIZER")
                    .requestMatchers(new AntPathRequestMatcher("/api/providers/**/my-services")).authenticated()
                    .requestMatchers(new AntPathRequestMatcher("/api/providers/product")).hasAnyRole("ADMIN","PROVIDER")
                    .requestMatchers(new AntPathRequestMatcher("/api/providers/my-products")).hasRole("PROVIDER")
//                    .requestMatchers(new AntPathRequestMatcher("/api/users")).permitAll()
//                    .requestMatchers(new AntPathRequestMatcher("/api/**")).permitAll() my-products
                    // Разрешаем доступ к маршруту /error для более удобных сообщений об ошибках.""admin@a.a" /api/organizers/
                    .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()
//                    .requestMatchers(new AntPathRequestMatcher("/api/whoami")).hasRole("USER")
                    .anyRequest().authenticated();
        });
        // Добавляем фильтр для проверки JWT токенов перед UsernamePasswordAuthenticationFilter.
        http.addFilterBefore(new TokenAuthenticationFilter(tokenUtils, userDetailsService()), UsernamePasswordAuthenticationFilter.class);
        http.authenticationProvider(authenticationProvider());
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Аутентификация будет игнорироваться для указанных путей (для ускорения доступа к ресурсам).
        // Запросы, которые соответствуют web.ignoring().antMatchers(), не имеют доступа к SecurityContext.
        // Разрешаем POST запросы на маршруте /auth/login, для всех других HTTP методов вернётся ошибка 401 Unauthorized.
        return (web) -> web.ignoring()
                // Разрешаем доступ к статическим ресурсам приложения.
                .requestMatchers(HttpMethod.GET, "/", "/webjars/*", "/*.html", "favicon.ico",
                        "/*/*.html", "/*/*.css", "/*/*.js");
    }

    // Настройка CORS.
// Подробнее: https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("POST", "PUT", "GET", "OPTIONS", "DELETE", "PATCH")); // или просто "*"
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
