package ftn.siit.project.isspoject.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/socket-subscriber")
                .enableSimpleBroker("/socket-publisher");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/socket") // Android client
                .setAllowedOriginPatterns("*"); // Wildcard for dev, restrict in prod

        registry.addEndpoint("/socket-web") // Angular browser SockJS fallback
                .setAllowedOrigins("http://localhost:4200")
                .withSockJS();
    }

}
