package Model;

import java.util.Random;

/**
 *
 * @author amine
 */
public class Player extends Character {
    private PlayerType type;
    private String sessionId;
    private static final Random random = new Random();
    
    public enum PlayerType {
        WARRIOR(100, 10, 8),
        MAGE(70, 15, 3),
        ARCHER(85, 12, 5);
        
        private final int health;
        private final int attack;
        private final int defense;
        
        PlayerType(int health, int attack, int defense) {
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
    
    public Player(String id, String name, PlayerType type, String sessionId) {
        super(id, name, type.health, type.attack, type.defense);
        this.type = type;
        this.sessionId = sessionId;
    }
    
    public PlayerType getType() {
        return type;
    }
    
    public void setType(PlayerType type) {
        this.type = type;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    @Override
    public void attack(Character target) {
        // Basic attack with some randomness
        int damage = getAttack() + random.nextInt(5);
        target.takeDamage(damage);
    }
    
    // Special ability based on player type
    public void useSpecialAbility(Character target) {
        switch (type) {
            case WARRIOR:
                // Heavy strike - double damage
                int damage = getAttack() * 2;
                target.takeDamage(damage);
                break;
            case MAGE:
                // Fireball - ignores defense
                target.setHealth(target.getHealth() - getAttack());
                break;
            case ARCHER:
                // Critical shot - 50% more damage
                damage = (int)(getAttack() * 1.5) + random.nextInt(3);
                target.takeDamage(damage);
                break;
        }
    }
}

