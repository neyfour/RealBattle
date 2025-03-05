package Controller;

import Model.Player;
import GameService.GameSession;
import GameService.GameSessionManager;
import Model.Enemy;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/game/{sessionId}")
public class GameWebSocket {
    
    @Inject
    private GameSessionManager sessionManager;
    
    private static Map<String, Session> clients = new ConcurrentHashMap<>();
    
    @OnOpen
    public void onOpen(Session session, @PathParam("sessionId") String sessionId) {
        clients.put(session.getId(), session);
        
        // Associate this WebSocket session with the game session
        GameSession gameSession = sessionManager.getSession(sessionId);
        if (gameSession != null) {
            // Send initial game state
            sendGameState(session, gameSession);
        } else {
            sendError(session, "Game session not found");
        }
    }
    
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("sessionId") String sessionId) {
        try {
            JsonReader reader = Json.createReader(new StringReader(message));
            JsonObject jsonMessage = reader.readObject();
            String action = jsonMessage.getString("action");
            
            GameSession gameSession = sessionManager.getSession(sessionId);
            if (gameSession == null) {
                sendError(session, "Game session not found");
                return;
            }
            
            switch (action) {
                case "join":
                    String playerName = jsonMessage.getString("playerName");
                    String playerType = jsonMessage.getString("playerType");
                    
                    Player player = new Player(
                        session.getId(),
                        playerName,
                        Player.PlayerType.valueOf(playerType.toUpperCase()),
                        sessionId
                    );
                    
                    gameSession.addPlayer(player);
                    break;
                    
                case "start":
                    gameSession.startGame();
                    break;
                    
                case "move":
                    // Handle player movement
                    float x = (float) jsonMessage.getJsonNumber("x").doubleValue();
                    float y = (float) jsonMessage.getJsonNumber("y").doubleValue();
                    float z = (float) jsonMessage.getJsonNumber("z").doubleValue();
                    
                    // Find player and update position
                    for (Player p : gameSession.getPlayers()) {
                        if (p.getId().equals(session.getId())) {
                            p.move(x, y, z);
                            break;
                        }
                    }
                    break;
                    
                case "attack":
                    // Handle player attack
                    String targetId = jsonMessage.getString("targetId");
                    
                    // Find player and target
                    Player attacker = null;
                    for (Player p : gameSession.getPlayers()) {
                        if (p.getId().equals(session.getId())) {
                            attacker = p;
                            break;
                        }
                    }
                    
                    if (attacker != null) {
                        // Find target enemy
                        for (Enemy e : gameSession.getEnemies()) {
                            if (e.getId().equals(targetId)) {
                                attacker.attack(e);
                                break;
                            }
                        }
                    }
                    break;
            }
            
            // Process game turn if the game is active
            if (gameSession.getState() == GameSession.GameState.ACTIVE) {
                gameSession.processGameTurn();
                // Broadcast updated game state to all players in this session
                broadcastGameState(gameSession);
            }
            
        } catch (Exception e) {
            sendError(session, "Error processing message: " + e.getMessage());
        }
    }
    
    @OnClose
    public void onClose(Session session, @PathParam("sessionId") String sessionId) {
        clients.remove(session.getId());
        
        GameSession gameSession = sessionManager.getSession(sessionId);
        if (gameSession != null) {
            gameSession.removePlayer(session.getId());
            
            // If game is still active, broadcast the update
            if (gameSession.getState() == GameSession.GameState.ACTIVE) {
                broadcastGameState(gameSession);
            }
            
            // If no players left, remove the session
            if (gameSession.getPlayers().isEmpty()) {
                sessionManager.removeSession(sessionId);
            }
        }
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
        clients.remove(session.getId());
        throwable.printStackTrace();
    }
    
    private void sendGameState(Session session, GameSession gameSession) {
        // Convert game state to JSON and send
        JsonObject gameState = createGameStateJson(gameSession);
        session.getAsyncRemote().sendText(gameState.toString());
    }
    
    private void broadcastGameState(GameSession gameSession) {
        JsonObject gameState = createGameStateJson(gameSession);
        String gameStateJson = gameState.toString();
        
        for (Player player : gameSession.getPlayers()) {
            Session playerSession = clients.get(player.getId());
            if (playerSession != null && playerSession.isOpen()) {
                playerSession.getAsyncRemote().sendText(gameStateJson);
            }
        }
    }
    
    private JsonObject createGameStateJson(GameSession gameSession) {
        // Create a JSON representation of the game state
        JsonObjectBuilder gameStateBuilder = Json.createObjectBuilder()
            .add("sessionId", gameSession.getId())
            .add("state", gameSession.getState().toString());
        
        // Add players
        JsonArrayBuilder playersArray = Json.createArrayBuilder();
        for (Player player : gameSession.getPlayers()) {
            playersArray.add(Json.createObjectBuilder()
                .add("id", player.getId())
                .add("name", player.getName())
                .add("type", player.getType().toString())
                .add("health", player.getHealth())
                .add("attack", player.getAttack())
                .add("defense", player.getDefense())
                .add("x", player.getX())
                .add("y", player.getY())
                .add("z", player.getZ())
            );
        }
        gameStateBuilder.add("players", playersArray);
        
        // Add enemies
        JsonArrayBuilder enemiesArray = Json.createArrayBuilder();
        for (Enemy enemy : gameSession.getEnemies()) {
            enemiesArray.add(Json.createObjectBuilder()
                .add("id", enemy.getId())
                .add("name", enemy.getName())
                .add("type", enemy.getType().toString())
                .add("health", enemy.getHealth())
                .add("attack", enemy.getAttack())
                .add("defense", enemy.getDefense())
                .add("x", enemy.getX())
                .add("y", enemy.getY())
                .add("z", enemy.getZ())
            );
        }
        gameStateBuilder.add("enemies", enemiesArray);
        
        return gameStateBuilder.build();
    }
    
    private void sendError(Session session, String errorMessage) {
        JsonObject error = Json.createObjectBuilder()
            .add("type", "error")
            .add("message", errorMessage)
            .build();
        
        session.getAsyncRemote().sendText(error.toString());
    }
}

