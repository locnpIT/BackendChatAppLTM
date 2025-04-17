package com.nguyenphuocloc.ltmchatapp.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Kích hoạt WebSocket message handling, được hỗ trợ bởi message broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Autowired
    private JwtChannelInterceptor jwtChannelInterceptor;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký endpoint "/ws" cho client kết nối WebSocket tới.
        // "/ws" là điểm cuối mà client sẽ thực hiện WebSocket handshake.
        // withSockJS() cung cấp fallback option cho trình duyệt không hỗ trợ WebSocket gốc.
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Cho phép tất cả các origin (thay * bằng origin cụ thể của frontend trong production)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Cấu hình message broker để định tuyến message từ client này sang client khác.

        // Destination prefix cho các message được gửi từ client đến server (để được xử lý bởi @MessageMapping).
        // Ví dụ: Client gửi đến "/app/chat.sendMessage"
        registry.setApplicationDestinationPrefixes("/app");

        // Destination prefix cho các message được gửi từ server đến client (subscribe).
        // Server sẽ gửi message đến các topic bắt đầu bằng "/topic".
        // Client sẽ lắng nghe trên các destination này (ví dụ: "/topic/room/123").
        // enableSimpleBroker sử dụng một simple memory-based message broker.
        // Bạn cũng có thể cấu hình external broker như RabbitMQ hoặc ActiveMQ ở đây.
        registry.enableSimpleBroker("/topic");

        // (Optional) Cấu hình prefix cho user-specific destinations (gửi tin nhắn riêng)
        // registry.setUserDestinationPrefix("/user");
    }
}