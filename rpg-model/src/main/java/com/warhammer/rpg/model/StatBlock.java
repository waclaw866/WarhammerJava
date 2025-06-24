package com.warhammer.rpg.model;

/**
 * Interface defining character attributes for Warhammer Fantasy 2e
 */
public interface StatBlock {
    int getWeaponSkill();
    int getBallisticSkill();
    int getStrength();
    int getToughness();
    int getAgility();
    int getIntelligence();
    int getWillPower();
    int getFellowship();
    int getAttacks();
    int getWounds();
}