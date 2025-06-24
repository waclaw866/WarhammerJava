package com.warhammer.rpg.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * Enemy includes stats, abilities, weapon, and metadata
 */
public record Enemy(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("stats") BasicStatBlock stats,
    @JsonProperty("abilities") List<Ability> abilities,
    @JsonProperty("weaponName") String weaponName,
    @JsonProperty("currentWounds") int currentWounds
) {
    
    public Enemy {
        // Validation
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Enemy ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Enemy name cannot be null or empty");
        }
        if (stats == null) {
            throw new IllegalArgumentException("Enemy stats cannot be null");
        }
        if (abilities == null) {
            abilities = new ArrayList<>();
        }
        if (weaponName == null) {
            weaponName = "";
        }
        if (currentWounds <= 0) {
            currentWounds = stats.wounds();
        }
    }
    
    /**
     * Check if enemy has a specific ability
     */
    public boolean hasAbility(String abilityName) {
        return abilities.stream()
                .anyMatch(ability -> ability.name().equalsIgnoreCase(abilityName));
    }
    
    /**
     * Get ability by name
     */
    public Ability getAbility(String abilityName) {
        return abilities.stream()
                .filter(ability -> ability.name().equalsIgnoreCase(abilityName))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Check if enemy is alive
     */
    public boolean isAlive() {
        return currentWounds > 0;
    }
    
    /**
     * Create a copy with updated wounds
     */
    public Enemy withCurrentWounds(int newWounds) {
        return new Enemy(id, name, stats, abilities, weaponName, Math.max(0, newWounds));
    }
    
    /**
     * Apply damage to enemy
     */
    public Enemy takeDamage(int damage) {
        return withCurrentWounds(currentWounds - damage);
    }
    
    /**
     * Heal enemy
     */
    public Enemy heal(int healing) {
        int maxWounds = stats.wounds();
        return withCurrentWounds(Math.min(maxWounds, currentWounds + healing));
    }
}