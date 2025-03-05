package GameService;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class GameSessionManager {
    
    private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();
    
    public GameSession createSession() {
        GameSession session = new GameSession();
        sessions.put(session.getId(), session);
        return session;
    }
    
    public GameSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }
    
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
    
    public Map<String, GameSession> getAllSessions() {
        return sessions;
    }
}

