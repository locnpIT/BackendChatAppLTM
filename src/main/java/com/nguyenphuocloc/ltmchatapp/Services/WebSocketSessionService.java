package com.nguyenphuocloc.ltmchatapp.Services;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketSessionService {

    private ConcurrentHashMap<String, String> userSessionMap = new ConcurrentHashMap<>(); // Map sessionId -> roomId

    // Thêm user vào phòng
    public void addUserToRoom(String sessionId, String roomId) {
        userSessionMap.put(sessionId, roomId);
    }

    // Xóa user khi disconnect
    public void removeUserFromRoom(String sessionId) {
        userSessionMap.remove(sessionId);
    }

    // Lấy phòng mà user tham gia (dựa vào sessionId)
    public String getRoomBySessionId(String sessionId) {
        return userSessionMap.get(sessionId);
    }
}
