package com.bobfull.chat.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.ChannelRegistration;
import com.bobfull.chat.security.ChatOutboundAuthorizationInterceptor;
import com.bobfull.chat.security.ChatStompInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final List<String> allowedOrigins;
    private final ChatStompInterceptor chatStompInterceptor;
    private final ChatOutboundAuthorizationInterceptor chatOutboundAuthorizationInterceptor;

    public WebSocketConfig(@Value("${cors.allowed-origins}") List<String> allowedOrigins,
                           ChatStompInterceptor chatStompInterceptor,
                           ChatOutboundAuthorizationInterceptor chatOutboundAuthorizationInterceptor) {
        this.allowedOrigins = allowedOrigins;
        this.chatStompInterceptor = chatStompInterceptor;
        this.chatOutboundAuthorizationInterceptor = chatOutboundAuthorizationInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(allowedOrigins.toArray(String[]::new));
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatStompInterceptor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatOutboundAuthorizationInterceptor);
    }
}
