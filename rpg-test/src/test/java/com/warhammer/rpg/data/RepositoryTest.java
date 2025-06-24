package com.warhammer.rpg.data;

import com.warhammer.rpg.model.Enemy;
import com.warhammer.rpg.model.Weapon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

/**
 * Integration tests for data repositories
 */
class RepositoryTest {

    private EnemyRepository enemyRepository;
    private WeaponRepository weaponRepository;

    @BeforeEach
    void setUp() {
        enemyRepository = new EnemyRepository();
        weaponRepository = new WeaponRepository();
    }

    @Test
    void testEnemyRepositoryLoadsData() {
        List<Enemy> enemies = enemyRepository.getAllEnemies();
        
        assertNotNull(enemies);
        assertFalse(enemies.isEmpty());
        assertTrue(enemies.size() >= 3); // Should have at least Goblin, Orc, Skaven
        
        // Verify we have expected enemies
        assertTrue(enemies.stream().anyMatch(e -> e.name().equals("Goblin")));
        assertTrue(enemies.stream().anyMatch(e -> e.name().equals("Orc")));
        assertTrue(enemies.stream().anyMatch(e -> e.name().equals("Skaven Clanrat")));
    }

    @Test
    void testWeaponRepositoryLoadsData() {
        List<Weapon> weapons = weaponRepository.getAllWeapons();
        
        assertNotNull(weapons);
        assertFalse(weapons.isEmpty());
        assertTrue(weapons.size() >= 5); // Should have at least 5 weapons
        
        // Verify we have expected weapons
        assertTrue(weapons.stream().anyMatch(w -> w.name().equals("Short Sword")));
        assertTrue(weapons.stream().anyMatch(w -> w.name().equals("Spear")));
        assertTrue(weapons.stream().anyMatch(w -> w.name().equals("Crossbow")));
    }

    @Test
    void testFindEnemyById() {
        Optional<Enemy> goblin = enemyRepository.findById("goblin");
        
        assertTrue(goblin.isPresent());
        assertEquals("Goblin", goblin.get().name());
        assertEquals("goblin", goblin.get().id());
    }

    @Test
    void testFindEnemyByIdNotFound() {
        Optional<Enemy> notFound = enemyRepository.findById("nonexistent");
        
        assertFalse(notFound.isPresent());
    }

    @Test
    void testFindEnemyByName() {
        Optional<Enemy> orc = enemyRepository.findByName("Orc");
        
        assertTrue(orc.isPresent());
        assertEquals("orc", orc.get().id());
        assertEquals("Orc", orc.get().name());
        
        // Test case insensitive
        Optional<Enemy> orcLowerCase = enemyRepository.findByName("orc");
        assertTrue(orcLowerCase.isPresent());
    }

    @Test
    void testFindWeaponById() {
        Optional<Weapon> sword = weaponRepository.findById("short-sword");
        
        assertTrue(sword.isPresent());
        assertEquals("Short Sword", sword.get().name());
        assertEquals(1, sword.get().damage());
    }

    @Test
    void testFindWeaponByName() {
        Optional<Weapon> crossbow = weaponRepository.findByName("Crossbow");
        
        assertTrue(crossbow.isPresent());
        assertEquals("crossbow", crossbow.get().id());
        assertTrue(crossbow.get().hasTrait("ranged"));
    }

    @Test
    void testFindWeaponsByTrait() {
        List<Weapon> oneHandedWeapons = weaponRepository.findByTrait("one-handed");
        
        assertNotNull(oneHandedWeapons);
        assertFalse(oneHandedWeapons.isEmpty());
        
        // All returned weapons should have the trait
        oneHandedWeapons.forEach(weapon -> 
            assertTrue(weapon.hasTrait("one-handed")));
    }

    @Test
    void testFindEnemiesByAbility() {
        List<Enemy> cowardlyEnemies = enemyRepository.findByAbility("Cowardly");
        
        assertNotNull(cowardlyEnemies);
        assertFalse(cowardlyEnemies.isEmpty());
        
        // All returned enemies should have the ability
        cowardlyEnemies.forEach(enemy -> 
            assertTrue(enemy.hasAbility("Cowardly")));
    }

    @Test
    void testFindEnemiesByMinimumWeaponSkill() {
        List<Enemy> skilledEnemies = enemyRepository.findByMinimumWeaponSkill(4);
        
        assertNotNull(skilledEnemies);
        
        // All returned enemies should have WS >= 4
        skilledEnemies.forEach(enemy -> 
            assertTrue(enemy.stats().weaponSkill() >= 4));
    }

    @Test
    void testRepositoryCounts() {
        assertTrue(enemyRepository.getEnemyCount() > 0);
        assertTrue(weaponRepository.getWeaponCount() > 0);
        
        assertEquals(enemyRepository.getAllEnemies().size(), enemyRepository.getEnemyCount());
        assertEquals(weaponRepository.getAllWeapons().size(), weaponRepository.getWeaponCount());
    }

    @Test
    void testEnemyStatBlocks() {
        List<Enemy> enemies = enemyRepository.getAllEnemies();
        
        // Verify all enemies have valid stat blocks
        enemies.forEach(enemy -> {
            assertNotNull(enemy.stats());
            assertTrue(enemy.stats().weaponSkill() > 0);
            assertTrue(enemy.stats().ballisticSkill() > 0);
            assertTrue(enemy.stats().strength() > 0);
            assertTrue(enemy.stats().toughness() > 0);
            assertTrue(enemy.stats().agility() > 0);
            assertTrue(enemy.stats().intelligence() > 0);
            assertTrue(enemy.stats().willPower() > 0);
            assertTrue(enemy.stats().fellowship() > 0);
            assertTrue(enemy.stats().attacks() > 0);
            assertTrue(enemy.stats().wounds() > 0);
        });
    }

    @Test
    void testWeaponProperties() {
        List<Weapon> weapons = weaponRepository.getAllWeapons();
        
        // Verify all weapons have valid properties
        weapons.forEach(weapon -> {
            assertNotNull(weapon.id());
            assertFalse(weapon.id().trim().isEmpty());
            assertNotNull(weapon.name());
            assertFalse(weapon.name().trim().isEmpty());
            assertTrue(weapon.damage() >= 0);
            assertNotNull(weapon.traits());
        });
    }
}