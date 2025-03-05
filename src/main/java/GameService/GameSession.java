package GameService;

import Model.Enemy;
import Model.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameSession {
    
    public enum GameState {
        WAITING, // Waiting for players to join
        ACTIVE,  // Game is in progress
        ENDED    // Game has ended
    }
    
    private final String id;
    private GameState state;
    private final List<Player> players;
    private final List<Enemy> enemies;
    
    public GameSession() {
        this.id = UUID.randomUUID().toString();
        this.state = GameState.WAITING;
        this.players = new CopyOnWriteArrayList<>();
        this.enemies = new CopyOnWriteArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public GameState getState() {
        return state;
    }
    
    public void setState(GameState state) {
        this.state = state;
    }
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public List<Enemy> getEnemies() {
        return enemies;
    }
    
    public void addPlayer(Player player) {
        players.add(player);
    }
    
    public void removePlayer(String playerId) {
        players.removeIf(p -> p.getId().equals(playerId));
    }
    
    public void startGame() {
        if (players.size() > 0) {
            // Generate enemies based on player count
            generateEnemies();
            state = GameState.ACTIVE;
        }
    }
    
    private void generateEnemies() {
        // Clear existing enemies
        enemies.clear();
        
        // Generate enemies based on player count and difficulty
        int enemyCount = Math.max(1, players.size() * 2);
        
        for (int i = 0; i < enemyCount; i++) {
            Enemy.EnemyType type;
            // Distribute enemy types
            if (i % 3 == 0) {
                type = Enemy.EnemyType.KNIGHT;
            } else if (i % 3 == 1) {
                type = Enemy.EnemyType.WIZARD;
            } else {
                type = Enemy.EnemyType.GOBLIN;
            }
            
            Enemy enemy = new Enemy(
                "enemy-" + UUID.randomUUID().toString(),
                type.name().toLowerCase() + "-" + (i + 1),
                type
            );
            
            // Position enemies randomly in the game world
            float x = (float) (Math.random() * 100 - 50);
            float z = (float) (Math.random() * 100 - 50);
            enemy.move(x, 0, z);
            
            enemies.add(enemy);
        }
    }
    
    public void processGameTurn() {
        // Process enemy AI
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                enemy.decideAction(players);
            }
        }
        
        // Check game end conditions
        checkGameEndConditions();
    }
    
    private void checkGameEndConditions() {
        // Check if all players are dead
        boolean allPlayersDead = true;
        for (Player player : players) {
            if (player.isAlive()) {
                allPlayersDead = false;
                break;
            }
        }
        
        if (allPlayersDead) {
            state = GameState.ENDED;
            return;
        }
        
        // Check if all enemies are dead
        boolean allEnemiesDead = true;
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                allEnemiesDead = false;
                break;
            }
        }
        
        if (allEnemiesDead) {
            state = GameState.ENDED;
        }
    }
}

