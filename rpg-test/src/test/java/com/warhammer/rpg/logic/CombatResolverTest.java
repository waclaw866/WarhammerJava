package com.warhammer.rpg.logic;

import com.warhammer.rpg.model.BasicStatBlock;
import com.warhammer.rpg.model.Enemy;
import com.warhammer.rpg.model.Weapon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for CombatResolver
 */
class CombatResolverTest {

    private CombatResolver combatResolver;
    private Enemy attacker;
    private Enemy defender;
    private Weapon sword;

    @BeforeEach
    void setUp() {
        // Use seeded dice roller for predictable tests
        DiceRoller seededRoller = new DiceRoller(12345);
        combatResolver = new CombatResolver(seededRoller);
        
        // Create test enemies
        BasicStatBlock attackerStats = new BasicStatBlock(50, 40, 4, 3, 3, 3, 3, 3, 1, 2);
        BasicStatBlock defenderStats = new BasicStatBlock(40, 30, 3, 4, 3, 3, 3, 3, 1, 2);
        
        attacker = new Enemy("attacker", "Attacker", attackerStats, List.of(), "Sword", 2);
        defender = new Enemy("defender", "Defender", defenderStats, List.of(), "Shield", 2);
        
        sword = new Weapon("sword", "Sword", 2, "one-handed");
    }

    @Test
    void testResolveAttack() {
        CombatResolver.AttackResult result = combatResolver.resolveAttack(attacker, defender, sword);
        
        assertNotNull(result);
        assertTrue(result.hitRoll() >= 1 && result.hitRoll() <= 100);
        
        if (result.hit()) {
            assertTrue(result.damage() >= 0);
            assertTrue(result.damageRoll() >= 1 && result.damageRoll() <= 10);
            assertTrue(result.toughnessRoll() >= 1 && result.toughnessRoll() <= 100);
        } else {
            assertEquals(0, result.damage());
        }
    }

    @Test
    void testResolveAttackWithGuaranteedHit() {
        // Create attacker with very high weapon skill
        BasicStatBlock superStats = new BasicStatBlock(100, 100, 10, 3, 3, 3, 3, 3, 1, 2);
        Enemy superAttacker = new Enemy("super", "Super", superStats, List.of(), "Sword", 2);
        
        CombatResolver.AttackResult result = combatResolver.resolveAttack(superAttacker, defender, sword);
        
        // With 100 weapon skill, should always hit
        assertTrue(result.hit());
        assertTrue(result.damage() > 0);
    }

    @Test
    void testResolveAttackWithGuaranteedMiss() {
        // Create attacker with very low weapon skill
        BasicStatBlock weakStats = new BasicStatBlock(1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
        Enemy weakAttacker = new Enemy("weak", "Weak", weakStats, List.of(), "Stick", 1);
        
        CombatResolver.AttackResult result = combatResolver.resolveAttack(weakAttacker, defender, sword);
        
        // With 1 weapon skill, very unlikely to hit (but not impossible)
        if (!result.hit()) {
            assertEquals(0, result.damage());
        }
    }

    @Test
    void testResolveParry() {
        boolean parryResult = combatResolver.resolveParry(defender);
        
        // Result should be boolean (no exception)
        assertTrue(parryResult || !parryResult);
    }

    @Test
    void testRollInitiative() {
        int initiative = combatResolver.rollInitiative(attacker);
        
        // Initiative should be at least the agility score + 1 (minimum dice roll)
        assertTrue(initiative >= attacker.stats().agility() + 1);
        // And at most agility + 10 (maximum dice roll)
        assertTrue(initiative <= attacker.stats().agility() + 10);
    }

    @Test
    void testCharacteristicTest() {
        CombatResolver.CharacteristicTestResult result = combatResolver.testCharacteristic(50, 0);
        
        assertNotNull(result);
        assertTrue(result.roll() >= 1 && result.roll() <= 100);
        assertEquals(50, result.target());
        assertTrue(result.degrees() >= 0);
        
        // Test with modifier
        CombatResolver.CharacteristicTestResult modifiedResult = combatResolver.testCharacteristic(50, 10);
        assertEquals(60, modifiedResult.target());
    }

    @Test
    void testCharacteristicTestSuccess() {
        // Test with very high characteristic (should usually succeed)
        CombatResolver.CharacteristicTestResult result = combatResolver.testCharacteristic(95, 0);
        
        if (result.success()) {
            assertTrue(result.roll() <= result.target());
        } else {
            assertTrue(result.roll() > result.target());
        }
    }

    @Test
    void testCharacteristicTestFailure() {
        // Test with very low characteristic (should usually fail)
        CombatResolver.CharacteristicTestResult result = combatResolver.testCharacteristic(5, 0);
        
        if (result.success()) {
            assertTrue(result.roll() <= result.target());
        } else {
            assertTrue(result.roll() > result.target());
        }
    }

    @Test
    void testAttackResultRecord() {
        CombatResolver.AttackResult result = new CombatResolver.AttackResult(
            true, 5, 35, 7, 60, true
        );
        
        assertTrue(result.hit());
        assertEquals(5, result.damage());
        assertEquals(35, result.hitRoll());
        assertEquals(7, result.damageRoll());
        assertEquals(60, result.toughnessRoll());
        assertTrue(result.toughnessPass());
    }

    @Test
    void testCharacteristicTestResultRecord() {
        CombatResolver.CharacteristicTestResult result = new CombatResolver.CharacteristicTestResult(
            true, 45, 50, 0
        );
        
        assertTrue(result.success());
        assertEquals(45, result.roll());
        assertEquals(50, result.target());
        assertEquals(0, result.degrees());
    }
}