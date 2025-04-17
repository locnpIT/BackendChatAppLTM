package com.nguyenphuocloc.ltmchatapp.Config;

import com.nguyenphuocloc.ltmchatapp.Entity.User;
import com.nguyenphuocloc.ltmchatapp.JWT.JwtUtility;
import com.nguyenphuocloc.ltmchatapp.JWT.JwtValidationExceptionn;
import com.nguyenphuocloc.ltmchatapp.Repository.UserRepository;
import com.nguyenphuocloc.ltmchatapp.Security.CustomUserDetails;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
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
    public Message<?> preSend(@Nonnull Message<?> message, @Nonnull MessageChannel channel) {
        try {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
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
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(authentication);

               
                        accessor.setUser(authentication);

                        log.info("Xac thuc WebSocket thanh cong cho user: {}", username);
                    } else {
                        log.warn(" Khong tim thay user trong database: {}", username);
                    }
                }
            } else {
                log.warn(" Khong co header Authorization trong STOMP message");
            }
        } catch (JwtValidationExceptionn ex) {
            log.error(" JWT het han hoac khong hop le: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error(" Loi khi xu ly WebSocket Auth: {}", ex.getMessage());
        }

        return message;
    }
}
