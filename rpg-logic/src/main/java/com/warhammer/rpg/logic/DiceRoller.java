package com.warhammer.rpg.logic;

import java.util.Random;

/**
 * Dice rolling utility for Warhammer Fantasy 2e
 */
public class DiceRoller {
    
    private final Random random;
    
    public DiceRoller() {
        this.random = new Random();
    }
    
    public DiceRoller(long seed) {
        this.random = new Random(seed);
    }
    
    /**
     * Roll a d100 (percentile dice)
     */
    public int rollD100() {
        return random.nextInt(100) + 1;
    }
    
    /**
     * Roll a d10
     */
    public int rollD10() {
        return random.nextInt(10) + 1;
    }
    
    /**
     * Roll a d6
     */
    public int rollD6() {
        return random.nextInt(6) + 1;
    }
    
    /**
     * Roll multiple dice and sum the results
     */
    public int rollMultiple(int numDice, int sides) {
        int total = 0;
        for (int i = 0; i < numDice; i++) {
            total += random.nextInt(sides) + 1;
        }
        return total;
    }
    
    /**
     * Roll with modifier
     */
    public int rollD100WithModifier(int modifier) {
        return Math.max(1, Math.min(100, rollD100() + modifier));
    }
    
    /**
     * Test if a d100 roll succeeds against a target number
     */
    public boolean testAgainst(int targetNumber) {
        return rollD100() <= targetNumber;
    }
    
    /**
     * Test with modifier
     */
    public boolean testAgainstWithModifier(int targetNumber, int modifier) {
        return rollD100WithModifier(modifier) <= targetNumber;
    }
}