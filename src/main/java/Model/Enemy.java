package Model;

import java.util.Random;

/**
 *
 * @author amine
 */
public class Enemy extends Character {
    private EnemyType type;
    private static final Random random = new Random();

    public enum EnemyType {
        GOBLIN(50, 5, 3),
        WIZARD(70, 12, 4),
        KNIGHT(100, 8, 8);
        
        private final int health;
        private final int attack;
        private final int defense;
        
        EnemyType(int health, int attack, int defense) {
            this.health = health;
            this.attack = attack;
            this.defense = defense;
        }
        
        public int getHealth() {
            return health;
        }
        
        public int getAttack() {
            return attack;
        }
        
        public int getDefense() {
            return defense;
        }
    }
    
    public Enemy(String id, String name, EnemyType type) {
        super(id, name, type.health, type.attack, type.defense);
        this.type = type;
    }
    
    public EnemyType getType() {
        return type;
    }
    
    public void setType(EnemyType type) {
        this.type = type;
    }
    
    @Override
    public void attack(Character target) {
        // Basic attack with some randomness
        int damage = getAttack() + random.nextInt(5);
        target.takeDamage(damage);
    }
    
    // AI behavior method
    public void decideAction(java.util.List<Player> players) {
        // Simple AI: attack the player with the lowest health
        Player weakestPlayer = null;
        int lowestHealth = Integer.MAX_VALUE;
        
        for (Player player : players) {
            if (player.isAlive() && player.getHealth() < lowestHealth) {
                lowestHealth = player.getHealth();
                weakestPlayer = player;
            }
        }
        
        if (weakestPlayer != null) {
            attack(weakestPlayer);
        }
    }
}

