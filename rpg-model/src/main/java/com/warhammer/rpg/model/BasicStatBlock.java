package com.warhammer.rpg.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Basic implementation of StatBlock using Java record
 * for Warhammer Fantasy 2e character attributes
 */
public record BasicStatBlock(
    @JsonProperty("weaponSkill") int weaponSkill,
    @JsonProperty("ballisticSkill") int ballisticSkill,
    @JsonProperty("strength") int strength,
    @JsonProperty("toughness") int toughness,
    @JsonProperty("agility") int agility,
    @JsonProperty("intelligence") int intelligence,
    @JsonProperty("willPower") int willPower,
    @JsonProperty("fellowship") int fellowship,
    @JsonProperty("attacks") int attacks,
    @JsonProperty("wounds") int wounds
) implements StatBlock {

    @Override
    public int getWeaponSkill() {
        return weaponSkill;
    }

    @Override
    public int getBallisticSkill() {
        return ballisticSkill;
    }

    @Override
    public int getStrength() {
        return strength;
    }

    @Override
    public int getToughness() {
        return toughness;
    }

    @Override
    public int getAgility() {
        return agility;
    }

    @Override
    public int getIntelligence() {
        return intelligence;
    }

    @Override
    public int getWillPower() {
        return willPower;
    }

    @Override
    public int getFellowship() {
        return fellowship;
    }

    @Override
    public int getAttacks() {
        return attacks;
    }

    @Override
    public int getWounds() {
        return wounds;
    }
}