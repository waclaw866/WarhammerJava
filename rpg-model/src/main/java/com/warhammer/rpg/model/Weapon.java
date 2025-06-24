package com.warhammer.rpg.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Weapon details including base damage and traits
 */
public record Weapon(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("damage") int damage,
    @JsonProperty("traits") String traits
) {
    
    public Weapon {
        // Validation
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Weapon ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Weapon name cannot be null or empty");
        }
        if (damage < 0) {
            throw new IllegalArgumentException("Weapon damage cannot be negative");
        }
        if (traits == null) {
            traits = "";
        }
    }
    
    /**
     * Check if weapon has a specific trait
     */
    public boolean hasTrait(String trait) {
        return traits.toLowerCase().contains(trait.toLowerCase());
    }
    
    /**
     * Get all traits as array
     */
    public String[] getTraitsArray() {
        if (traits.trim().isEmpty()) {
            return new String[0];
        }
        return traits.split(",\\s*");
    }
}