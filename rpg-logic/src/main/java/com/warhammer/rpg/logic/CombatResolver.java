package com.warhammer.rpg.logic;

import com.warhammer.rpg.model.Enemy;
import com.warhammer.rpg.model.Weapon;

/**
 * Combat resolution logic for Warhammer Fantasy 2e
 */
public class CombatResolver {
    
    private final DiceRoller diceRoller;
    
    public CombatResolver(DiceRoller diceRoller) {
        this.diceRoller = diceRoller;
    }
    
    public CombatResolver() {
        this(new DiceRoller());
    }
    
    /**
     * Resolve an attack between attacker and defender
     */
    public AttackResult resolveAttack(Enemy attacker, Enemy defender, Weapon attackerWeapon) {
        // Hit test - d100 vs Weapon Skill
        int hitRoll = diceRoller.rollD100();
        boolean hit = hitRoll <= attacker.stats().weaponSkill();
        
        if (!hit) {
            return new AttackResult(false, 0, hitRoll, 0, 0, false);
        }
        
        // Damage roll
        int damageRoll = diceRoller.rollD10();
        int totalDamage = calculateDamage(attacker.stats().strength(), attackerWeapon.damage(), damageRoll);
        
        // Toughness test for defender
        int toughnessRoll = diceRoller.rollD100();
        boolean toughnessPass = toughnessRoll <= defender.stats().toughness();
        
        // Apply damage (reduced by toughness)
        int finalDamage = toughnessPass ? Math.max(1, totalDamage - 1) : totalDamage;
        
        return new AttackResult(true, finalDamage, hitRoll, damageRoll, toughnessRoll, toughnessPass);
    }
    
    /**
     * Calculate damage based on Strength + Weapon Damage + Roll
     */
    private int calculateDamage(int strength, int weaponDamage, int roll) {
        return strength + weaponDamage + (roll / 2); // Simplified damage calculation
    }
    
    /**
     * Resolve parry attempt
     */
    public boolean resolveParry(Enemy defender) {
        int parryRoll = diceRoller.rollD100();
        return parryRoll <= defender.stats().weaponSkill();
    }
    
    /**
     * Calculate initiative for combat order
     */
    public int rollInitiative(Enemy enemy) {
        return diceRoller.rollD10() + enemy.stats().agility();
    }
    
    /**
     * Test against a characteristic
     */
    public CharacteristicTestResult testCharacteristic(int characteristic, int modifier) {
        int roll = diceRoller.rollD100();
        int target = characteristic + modifier;
        boolean success = roll <= target;
        
        // Determine degree of success/failure
        int degrees = Math.abs(roll - target) / 10;
        
        return new CharacteristicTestResult(success, roll, target, degrees);
    }
    
    /**
     * Result of an attack
     */
    public record AttackResult(
        boolean hit,
        int damage,
        int hitRoll,
        int damageRoll,
        int toughnessRoll,
        boolean toughnessPass
    ) {}
    
    /**
     * Result of a characteristic test
     */
    public record CharacteristicTestResult(
        boolean success,
        int roll,
        int target,
        int degrees
    ) {}
}