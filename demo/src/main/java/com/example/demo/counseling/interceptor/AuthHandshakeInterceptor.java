package com.example.demo.counseling.interceptor;

import com.example.demo.counseling.model.entity.ConsultationSession;
import com.example.demo.counseling.repository.ConsultationSessionRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Value("${app.jwt.secret-key}")
    private String secretKeyString;

    @Autowired
    private ConsultationSessionRepository sessionRepository;

    private JwtParser getJwtParser() {
        SecretKey key = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(key).build();
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        String token = getTokenFromRequest(request);
        Long sessionId = getSessionIdFromRequest(request);

        if (!validateToken(token)) {
            return false;
        }

        String userId = getUserIdFromToken(token);
        String role = getRoleFromToken(token);

        ConsultationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        boolean hasPermission = false;
        if ("user".equals(role)) {
            hasPermission = session.getUserId().equals(Long.parseLong(userId));
        } else if ("counselor".equals(role)) {
            hasPermission = session.getCounselorId().equals(Long.parseLong(userId));
        }

        if (!hasPermission) {
            return false;
        }

        attributes.put("sessionId", sessionId);
        attributes.put("participantId", role + "_" + userId);
        attributes.put("senderRole", role);
        attributes.put("senderId", userId);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 握手后处理
    }

    private boolean validateToken(String token) {
        try {
            Jws<Claims> claims = getJwtParser().parseSignedClaims(token);
            return !claims.getPayload().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private String getUserIdFromToken(String token) {
        return getJwtParser().parseSignedClaims(token).getPayload().getSubject();
    }

    private String getRoleFromToken(String token) {
        return (String) getJwtParser().parseSignedClaims(token).getPayload().get("role");
    }

    private String getTokenFromRequest(ServerHttpRequest request) {
        // 从请求参数或header中获取token
        String query = request.getURI().getQuery();
        if (query != null && query.contains("token=")) {
            return query.split("token=")[1].split("&")[0];
        }
        return null;
    }

    private Long getSessionIdFromRequest(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        String sessionIdStr = path.substring(path.lastIndexOf('/') + 1);
        return Long.parseLong(sessionIdStr);
    }
}
