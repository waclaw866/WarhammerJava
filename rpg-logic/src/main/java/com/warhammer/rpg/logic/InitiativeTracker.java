package com.warhammer.rpg.logic;

import com.warhammer.rpg.model.Enemy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Tracks initiative order for combat encounters
 */
public class InitiativeTracker {
    
    private final List<InitiativeEntry> entries;
    private int currentTurn;
    
    public InitiativeTracker() {
        this.entries = new ArrayList<>();
        this.currentTurn = 0;
    }
    
    /**
     * Add an enemy to initiative with rolled initiative value
     */
    public void addEntry(Enemy enemy, int initiative) {
        entries.add(new InitiativeEntry(enemy, initiative));
        sortByInitiative();
    }
    
    /**
     * Add enemy and automatically roll initiative
     */
    public void addEntry(Enemy enemy, CombatResolver combatResolver) {
        int initiative = combatResolver.rollInitiative(enemy);
        addEntry(enemy, initiative);
    }
    
    /**
     * Sort entries by initiative (highest first)
     */
    private void sortByInitiative() {
        entries.sort(Comparator.comparingInt(InitiativeEntry::initiative).reversed());
    }
    
    /**
     * Get current active entry
     */
    public InitiativeEntry getCurrentEntry() {
        if (entries.isEmpty()) {
            return null;
        }
        return entries.get(currentTurn % entries.size());
    }
    
    /**
     * Advance to next turn
     */
    public void nextTurn() {
        currentTurn++;
    }
    
    /**
     * Get all entries in initiative order
     */
    public List<InitiativeEntry> getAllEntries() {
        return List.copyOf(entries);
    }
    
    /**
     * Remove entry (e.g., when enemy dies)
     */
    public void removeEntry(String enemyId) {
        entries.removeIf(entry -> entry.enemy().id().equals(enemyId));
        // Adjust current turn if necessary
        if (currentTurn >= entries.size() && !entries.isEmpty()) {
            currentTurn = 0;
        }
    }
    
    /**
     * Reset initiative tracker
     */
    public void reset() {
        entries.clear();
        currentTurn = 0;
    }
    
    /**
     * Get current round number
     */
    public int getCurrentRound() {
        if (entries.isEmpty()) {
            return 0;
        }
        return (currentTurn / entries.size()) + 1;
    }
    
    /**
     * Check if tracker is empty
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }
    
    /**
     * Initiative entry combining enemy and their initiative roll
     */
    public record InitiativeEntry(Enemy enemy, int initiative) {}
}