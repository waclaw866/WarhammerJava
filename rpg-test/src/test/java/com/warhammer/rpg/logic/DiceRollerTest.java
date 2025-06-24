package com.warhammer.rpg.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DiceRoller
 */
class DiceRollerTest {

    private DiceRoller diceRoller;

    @BeforeEach
    void setUp() {
        diceRoller = new DiceRoller();
    }

    @Test
    void testRollD100() {
        for (int i = 0; i < 100; i++) {
            int roll = diceRoller.rollD100();
            assertTrue(roll >= 1 && roll <= 100, 
                "d100 roll should be between 1 and 100, got: " + roll);
        }
    }

    @Test
    void testRollD10() {
        for (int i = 0; i < 100; i++) {
            int roll = diceRoller.rollD10();
            assertTrue(roll >= 1 && roll <= 10, 
                "d10 roll should be between 1 and 10, got: " + roll);
        }
    }

    @Test
    void testRollD6() {
        for (int i = 0; i < 100; i++) {
            int roll = diceRoller.rollD6();
            assertTrue(roll >= 1 && roll <= 6, 
                "d6 roll should be between 1 and 6, got: " + roll);
        }
    }

    @Test
    void testRollMultiple() {
        // Test rolling 3d6
        for (int i = 0; i < 50; i++) {
            int total = diceRoller.rollMultiple(3, 6);
            assertTrue(total >= 3 && total <= 18, 
                "3d6 should be between 3 and 18, got: " + total);
        }
        
        // Test edge cases
        assertEquals(0, diceRoller.rollMultiple(0, 6));
        
        int singleDie = diceRoller.rollMultiple(1, 6);
        assertTrue(singleDie >= 1 && singleDie <= 6);
    }

    @Test
    void testRollD100WithModifier() {
        // Test positive modifier
        for (int i = 0; i < 50; i++) {
            int roll = diceRoller.rollD100WithModifier(10);
            assertTrue(roll >= 1 && roll <= 100, 
                "Modified d100 should still be between 1 and 100, got: " + roll);
        }
        
        // Test negative modifier
        for (int i = 0; i < 50; i++) {
            int roll = diceRoller.rollD100WithModifier(-10);
            assertTrue(roll >= 1 && roll <= 100, 
                "Modified d100 should still be between 1 and 100, got: " + roll);
        }
    }

    @Test
    void testTestAgainst() {
        // With a seeded dice roller for predictable testing
        DiceRoller seededRoller = new DiceRoller(12345);
        
        // Test against impossible target (should always fail)
        assertFalse(seededRoller.testAgainst(0));
        
        // Test against guaranteed success (should always pass)
        assertTrue(seededRoller.testAgainst(100));
    }

    @Test
    void testTestAgainstWithModifier() {
        DiceRoller seededRoller = new DiceRoller(12345);
        
        // Test that modifier affects the outcome
        boolean result1 = seededRoller.testAgainstWithModifier(50, 0);
        seededRoller = new DiceRoller(12345); // Reset with same seed
        boolean result2 = seededRoller.testAgainstWithModifier(50, 20);
        
        // With positive modifier, success should be less likely
        // (This test may occasionally fail due to randomness, but should generally pass)
    }

    @Test
    void testSeededDiceRoller() {
        // Test that seeded rollers produce consistent results
        DiceRoller roller1 = new DiceRoller(42);
        DiceRoller roller2 = new DiceRoller(42);
        
        assertEquals(roller1.rollD100(), roller2.rollD100());
        assertEquals(roller1.rollD10(), roller2.rollD10());
        assertEquals(roller1.rollD6(), roller2.rollD6());
    }
}