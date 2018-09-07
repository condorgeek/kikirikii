package com.kikirikii.security.configuration;

import com.kikirikii.security.authorization.JwtAuthorizationToken;
import com.kikirikii.security.model.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Collections;
import java.util.UUID;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, ApplicationListener<AuthenticationSuccessEvent> {
    private static Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private ApplicationContext context;

    private UsernamePasswordAuthenticationToken principal;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/websocket/test").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        registration.interceptors(new ChannelInterceptorAdapter() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    accessor.setUser(getPrincipal());
                    logger.info("CONNECT to " + principal.getName());
                } else if(StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                    logger.info("DISCONNECT from " + principal.getName());
                }
                return message;
            }
        });
    }

    private UsernamePasswordAuthenticationToken getPrincipal() {
        if (principal == null) {
            // TODO should throw an exception and force a new client login ?
            this.principal = new UsernamePasswordAuthenticationToken(
                    UUID.randomUUID().toString(), null,
                    Collections.singleton((GrantedAuthority) () -> "ROLE_USER")
            );
        }
        return principal;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        if (event.getSource() instanceof JwtAuthorizationToken) {
            UserContext userContext = (UserContext) ((JwtAuthorizationToken) event.getSource()).getPrincipal();

            this.principal = new UsernamePasswordAuthenticationToken(
                    userContext.getUsername(), null, userContext.getAuthorities());
        }
    }

}