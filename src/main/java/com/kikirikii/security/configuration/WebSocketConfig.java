package com.kikirikii.security.configuration;

import com.kikirikii.security.authorization.JwtAuthorizationToken;
import com.kikirikii.security.model.UserContext;
import com.kikirikii.security.token.BearerJwtToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
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
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, ApplicationListener<AuthenticationSuccessEvent> {
    private static Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private SecurityProperties securitySettings;

    @Deprecated
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
                    accessor.setUser(authenticate(accessor));
                    logger.info("CONNECT to " + accessor.getLogin());

                } else if(StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                    UsernamePasswordAuthenticationToken user = simpUser.apply(accessor);
                    logger.info("DISCONNECT from " + (user != null ? user.getPrincipal() : null));

                } else if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    UsernamePasswordAuthenticationToken user = simpUser.apply(accessor);
                    logger.info("SUBSCRIBE from " + (user != null ? user.getPrincipal() : null));
                }

//                dumpHeaders(message, accessor);

                return message;
            }
        });
    }

    private UsernamePasswordAuthenticationToken authenticate(StompHeaderAccessor accessor) {
        try {
            List<String> bearerToken = accessor.getNativeHeader("X-Authorization");
            BearerJwtToken jwtToken = new BearerJwtToken(bearerToken.get(0));

            Jws<Claims> jwsClaims = jwtToken.parseClaims(securitySettings.getTokenSigningKey());
            String subject = jwsClaims.getBody().getSubject();
            List<String> scopes = jwsClaims.getBody().get("scopes", List.class);
            List<GrantedAuthority> authorities = scopes.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return new UsernamePasswordAuthenticationToken(
                    subject, null,
                    authorities);

        } catch (Exception e) {
            logger.info("WEBSOCKET authentication failed for " +
                    accessor.getLogin() + " " + e.getMessage());
        }
        return null;
    }

    private void dumpHeaders(Message<?> message, StompHeaderAccessor accessor) {
        UsernamePasswordAuthenticationToken user = simpUser.apply(accessor);
        logger.info(accessor.getCommand().name() + " from " + (user != null ? user.getPrincipal() : null));
        MessageHeaders headers = message.getHeaders();
        headers.keySet().iterator().forEachRemaining(key -> logger.info(key + ": " + headers.get(key)));
    }

    @Deprecated
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

    private Function<StompHeaderAccessor, UsernamePasswordAuthenticationToken> simpUser = accessor -> (UsernamePasswordAuthenticationToken)accessor.getHeader("simpUser");
}