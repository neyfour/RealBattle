package Model;

import java.io.Serializable;

/**
 *
 * @author amine
 */
public abstract class Character implements Serializable {
    private String id;
    private String name;
    private int health;
    private int attack;
    private int defense;
    private float x, y, z; // 3D position
    
    public Character(String id, String name, int health, int attack, int defense) {
        this.id = id;
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getHealth() {
        return health;
    }
    
    public void setHealth(int health) {
        this.health = health;
    }
    
    public int getAttack() {
        return attack;
    }
    
    public void setAttack(int attack) {
        this.attack = attack;
    }
    
    public int getDefense() {
        return defense;
    }
    
    public void setDefense(int defense) {
        this.defense = defense;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getZ() {
        return z;
    }
    
    public abstract void attack(Character target);
    
    public void takeDamage(int damage) {
        int actualDamage = Math.max(1, damage - defense);
        health -= actualDamage;
        if (health < 0) health = 0;
    }
    
    public boolean isAlive() {
        return health > 0;
    }
    
    public void move(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

