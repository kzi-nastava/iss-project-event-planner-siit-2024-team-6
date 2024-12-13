package ftn.siit.project.isspoject.security.auth;
import ftn.siit.project.isspoject.util.TokenUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// Фильтр, который будет перехватывать КАЖДЫЙ запрос клиента к серверу
// (кроме маршрутов, указанных в WebSecurityCustomizer webSecurityCustomizer(web)).
// Фильтр проверяет, существует ли JWT-токен в заголовке Authorization запроса, поступающего от клиента.
// Если токен существует, проверяется его валидность. Если всё в порядке, создаётся аутентификация
// и добавляется в SecurityContextHolder, чтобы данные о пользователе были доступны в других частях приложения, где они необходимы.
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private TokenUtils tokenUtils;

    private UserDetailsService userDetailsService;

    protected final Log LOGGER = LogFactory.getLog(getClass());

    public TokenAuthenticationFilter(TokenUtils tokenHelper, UserDetailsService userDetailsService) {
        this.tokenUtils = tokenHelper;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String username;
        // 1. Извлечение JWT-токена из запроса
        String authToken = tokenUtils.getToken(request);

        try {

            if (authToken != null && !authToken.equals("")) {
                // 2. Извлечение имени пользователя из токена
                username = tokenUtils.getUsernameFromToken(authToken);

                if (username != null) {

                    // 3. Получение данных пользователя на основе имени
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 4. Проверка валидности переданного токена
                    if (tokenUtils.validateToken(authToken, userDetails)) {

                        // 5. Создание аутентификации
                        TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
                        authentication.setToken(authToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }

        }
        catch (ExpiredJwtException ex) {
            LOGGER.debug("Срок действия токена истёк!");
        }
        // Передача запроса далее следующему фильтру
        chain.doFilter(request, response);
    }
}
