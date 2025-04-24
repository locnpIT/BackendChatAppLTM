package com.nguyenphuocloc.ltmchatapp.Config;

import com.nguyenphuocloc.ltmchatapp.Entity.User;
import com.nguyenphuocloc.ltmchatapp.JWT.JwtUtility;
import com.nguyenphuocloc.ltmchatapp.JWT.JwtValidationExceptionn;
import com.nguyenphuocloc.ltmchatapp.Repository.UserRepository;
import com.nguyenphuocloc.ltmchatapp.Security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtility jwtUtility;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        // Chỉ xử lý xác thực khi command là CONNECT hoặc SEND
        if (command == StompCommand.CONNECT || command == StompCommand.SEND) {
            try {
                List<String> authHeaders = accessor.getNativeHeader("Authorization");

                if (authHeaders != null && !authHeaders.isEmpty()) {
                    String bearerToken = authHeaders.get(0);

                    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                        String token = bearerToken.substring(7);

                        var claims = jwtUtility.validateAccessToken(token);
                        String username = claims.get("username", String.class);

                        Optional<User> userOptional = userRepository.findByUsername(username);
                        if (userOptional.isPresent()) {
                            User user = userOptional.get();
                            CustomUserDetails userDetails = new CustomUserDetails(user);

                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities()
                                    );

                            // Set Authentication cho SecurityContext
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            // Set Principal cho STOMP accessor
                            accessor.setUser(authentication);

                            log.info("[{}] ✅ Authenticated user '{}', sessionId: {}", command, username, accessor.getSessionId());
                        } else {
                            log.warn("[{}] ❌ User '{}' not found in DB", command, username);
                        }
                    } else {
                        log.warn("[{}] ❌ Invalid Authorization format", command);
                    }
                } else {
                    log.warn("[{}] ❌ Missing Authorization header", command);
                }
            } catch (JwtValidationExceptionn ex) {
                log.error("[{}] ❌ JWT validation failed: {}", command, ex.getMessage());
            } catch (Exception ex) {
                log.error("[{}] ❌ Unexpected error during WebSocket Auth: {}", command, ex.getMessage());
            }
        }

        return message;
    }
}
