package com.warhammer.rpg.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warhammer.rpg.model.Enemy;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Repository for loading and managing enemy data from JSON
 */
public class EnemyRepository {
    
    private final ObjectMapper objectMapper;
    private List<Enemy> enemies;
    
    public EnemyRepository() {
        this.objectMapper = new ObjectMapper();
        loadEnemies();
    }
    
    /**
     * Load enemies from JSON file
     */
    private void loadEnemies() {
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("data/enemies.json")) {
            
            if (inputStream == null) {
                throw new RuntimeException("Could not find enemies.json in resources");
            }
            
            this.enemies = objectMapper.readValue(inputStream, new TypeReference<List<Enemy>>() {});
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to load enemies data", e);
        }
    }
    
    /**
     * Get all enemies
     */
    public List<Enemy> getAllEnemies() {
        return List.copyOf(enemies);
    }
    
    /**
     * Find enemy by ID
     */
    public Optional<Enemy> findById(String id) {
        return enemies.stream()
                .filter(enemy -> enemy.id().equals(id))
                .findFirst();
    }
    
    /**
     * Find enemy by name
     */
    public Optional<Enemy> findByName(String name) {
        return enemies.stream()
                .filter(enemy -> enemy.name().equalsIgnoreCase(name))
                .findFirst();
    }
    
    /**
     * Find enemies by ability
     */
    public List<Enemy> findByAbility(String abilityName) {
        return enemies.stream()
                .filter(enemy -> enemy.hasAbility(abilityName))
                .toList();
    }
    
    /**
     * Find enemies with minimum weapon skill
     */
    public List<Enemy> findByMinimumWeaponSkill(int minWS) {
        return enemies.stream()
                .filter(enemy -> enemy.stats().weaponSkill() >= minWS)
                .toList();
    }
    
    /**
     * Get enemy count
     */
    public int getEnemyCount() {
        return enemies.size();
    }
}