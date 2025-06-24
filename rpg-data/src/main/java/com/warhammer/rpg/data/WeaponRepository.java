package com.warhammer.rpg.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warhammer.rpg.model.Weapon;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Repository for loading and managing weapon data from JSON
 */
public class WeaponRepository {
    
    private final ObjectMapper objectMapper;
    private List<Weapon> weapons;
    
    public WeaponRepository() {
        this.objectMapper = new ObjectMapper();
        loadWeapons();
    }
    
    /**
     * Load weapons from JSON file
     */
    private void loadWeapons() {
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("data/weapons.json")) {
            
            if (inputStream == null) {
                throw new RuntimeException("Could not find weapons.json in resources");
            }
            
            this.weapons = objectMapper.readValue(inputStream, new TypeReference<List<Weapon>>() {});
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to load weapons data", e);
        }
    }
    
    /**
     * Get all weapons
     */
    public List<Weapon> getAllWeapons() {
        return List.copyOf(weapons);
    }
    
    /**
     * Find weapon by ID
     */
    public Optional<Weapon> findById(String id) {
        return weapons.stream()
                .filter(weapon -> weapon.id().equals(id))
                .findFirst();
    }
    
    /**
     * Find weapon by name
     */
    public Optional<Weapon> findByName(String name) {
        return weapons.stream()
                .filter(weapon -> weapon.name().equalsIgnoreCase(name))
                .findFirst();
    }
    
    /**
     * Find weapons by trait
     */
    public List<Weapon> findByTrait(String trait) {
        return weapons.stream()
                .filter(weapon -> weapon.hasTrait(trait))
                .toList();
    }
    
    /**
     * Get weapon count
     */
    public int getWeaponCount() {
        return weapons.size();
    }
}