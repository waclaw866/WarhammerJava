package com.warhammer.rpg.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Traits or rules text affecting gameplay
 */
public record Ability(
    @JsonProperty("name") String name,
    @JsonProperty("description") String description
) {
    
    public Ability {
        // Validation
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ability name cannot be null or empty");
        }
        if (description == null) {
            description = "";
        }
    }
}