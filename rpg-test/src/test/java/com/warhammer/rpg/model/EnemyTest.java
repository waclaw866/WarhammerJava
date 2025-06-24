package com.warhammer.rpg.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for Enemy model
 */
class EnemyTest {

    private BasicStatBlock goblinStats;
    private List<Ability> goblinAbilities;
    private Enemy goblin;

    @BeforeEach
    void setUp() {
        goblinStats = new BasicStatBlock(3, 3, 3, 3, 3, 2, 2, 2, 1, 1);
        goblinAbilities = Arrays.asList(
            new Ability("Cowardly", "Must pass Will Power test to stand and fight if outnumbered")
        );
        goblin = new Enemy("goblin", "Goblin", goblinStats, goblinAbilities, "Short Sword", 1);
    }

    @Test
    void testEnemyCreation() {
        assertNotNull(goblin);
        assertEquals("goblin", goblin.id());
        assertEquals("Goblin", goblin.name());
        assertEquals(goblinStats, goblin.stats());
        assertEquals(1, goblin.abilities().size());
        assertEquals("Short Sword", goblin.weaponName());
        assertEquals(1, goblin.currentWounds());
    }

    @Test
    void testEnemyWithInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Enemy("", "Goblin", goblinStats, goblinAbilities, "Short Sword", 1));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Enemy(null, "Goblin", goblinStats, goblinAbilities, "Short Sword", 1));
    }

    @Test
    void testEnemyWithInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Enemy("goblin", "", goblinStats, goblinAbilities, "Short Sword", 1));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Enemy("goblin", null, goblinStats, goblinAbilities, "Short Sword", 1));
    }

    @Test
    void testEnemyWithNullStats() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Enemy("goblin", "Goblin", null, goblinAbilities, "Short Sword", 1));
    }

    @Test
    void testHasAbility() {
        assertTrue(goblin.hasAbility("Cowardly"));
        assertFalse(goblin.hasAbility("Brutal"));
        assertTrue(goblin.hasAbility("cowardly")); // Case insensitive
    }

    @Test
    void testGetAbility() {
        Ability ability = goblin.getAbility("Cowardly");
        assertNotNull(ability);
        assertEquals("Cowardly", ability.name());
        
        assertNull(goblin.getAbility("Nonexistent"));
    }

    @Test
    void testIsAlive() {
        assertTrue(goblin.isAlive());
        
        Enemy deadGoblin = goblin.withCurrentWounds(0);
        assertFalse(deadGoblin.isAlive());
    }

    @Test
    void testTakeDamage() {
        Enemy damagedGoblin = goblin.takeDamage(1);
        assertFalse(damagedGoblin.isAlive());
        assertEquals(0, damagedGoblin.currentWounds());
        
        // Original enemy should be unchanged
        assertTrue(goblin.isAlive());
        assertEquals(1, goblin.currentWounds());
    }

    @Test
    void testHeal() {
        Enemy damagedGoblin = goblin.takeDamage(1);
        Enemy healedGoblin = damagedGoblin.heal(1);
        
        assertTrue(healedGoblin.isAlive());
        assertEquals(1, healedGoblin.currentWounds());
        
        // Can't heal above max wounds
        Enemy overHealedGoblin = healedGoblin.heal(5);
        assertEquals(1, overHealedGoblin.currentWounds()); // Max is 1
    }

    @Test
    void testWithCurrentWounds() {
        Enemy modifiedGoblin = goblin.withCurrentWounds(0);
        assertEquals(0, modifiedGoblin.currentWounds());
        
        // Negative wounds should be set to 0
        Enemy negativeWoundsGoblin = goblin.withCurrentWounds(-5);
        assertEquals(0, negativeWoundsGoblin.currentWounds());
    }

    @Test
    void testDefaultValues() {
        Enemy simpleEnemy = new Enemy("simple", "Simple", goblinStats, null, null, 0);
        
        assertTrue(simpleEnemy.abilities().isEmpty());
        assertEquals("", simpleEnemy.weaponName());
        assertEquals(1, simpleEnemy.currentWounds()); // Should default to max wounds
    }
}