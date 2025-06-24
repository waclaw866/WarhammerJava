package com.warhammer.rpg.app.controller;

import com.warhammer.rpg.data.EnemyRepository;
import com.warhammer.rpg.data.WeaponRepository;
import com.warhammer.rpg.logic.CombatResolver;
import com.warhammer.rpg.logic.DiceRoller;
import com.warhammer.rpg.logic.InitiativeTracker;
import com.warhammer.rpg.model.Enemy;
import com.warhammer.rpg.model.Weapon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main controller for the Warhammer Encounter Manager
 */
public class MainController implements Initializable {

    // Repositories
    private EnemyRepository enemyRepository;
    private WeaponRepository weaponRepository;
    private CombatResolver combatResolver;
    private InitiativeTracker initiativeTracker;
    private DiceRoller diceRoller;

    // FXML Controls - Bestiary Tab
    @FXML private TableView<Enemy> bestiaryTable;
    @FXML private TableColumn<Enemy, String> enemyNameColumn;
    @FXML private TableColumn<Enemy, Integer> enemyWSColumn;
    @FXML private TableColumn<Enemy, Integer> enemyBSColumn;
    @FXML private TableColumn<Enemy, Integer> enemySColumn;
    @FXML private TableColumn<Enemy, Integer> enemyTColumn;
    @FXML private TableColumn<Enemy, Integer> enemyWoundsColumn;

    // FXML Controls - Weapons Tab
    @FXML private TableView<Weapon> weaponsTable;
    @FXML private TableColumn<Weapon, String> weaponNameColumn;
    @FXML private TableColumn<Weapon, Integer> weaponDamageColumn;
    @FXML private TableColumn<Weapon, String> weaponTraitsColumn;

    // FXML Controls - Encounter Tab
    @FXML private ListView<String> encounterList;
    @FXML private TextArea combatLog;
    @FXML private Label currentTurnLabel;

    // Dice Rolling
    @FXML private Label lastRollLabel;
    @FXML private Button rollD100Button;
    @FXML private Button rollD10Button;
    @FXML private Button rollD6Button;

    // Enemy details
    @FXML private TextArea enemyDetailsArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize repositories and services
        enemyRepository = new EnemyRepository();
        weaponRepository = new WeaponRepository();
        combatResolver = new CombatResolver();
        initiativeTracker = new InitiativeTracker();
        diceRoller = new DiceRoller();

        // Setup tables
        setupBestiaryTable();
        setupWeaponsTable();
        setupEncounterControls();

        // Load initial data
        loadBestiaryData();
        loadWeaponsData();

        // Setup event handlers
        setupEventHandlers();
    }

    private void setupBestiaryTable() {
        enemyNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        enemyWSColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().stats().weaponSkill()).asObject());
        enemyBSColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().stats().ballisticSkill()).asObject());
        enemySColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().stats().strength()).asObject());
        enemyTColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().stats().toughness()).asObject());
        enemyWoundsColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().stats().wounds()).asObject());
    }

    private void setupWeaponsTable() {
        weaponNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        weaponDamageColumn.setCellValueFactory(new PropertyValueFactory<>("damage"));
        weaponTraitsColumn.setCellValueFactory(new PropertyValueFactory<>("traits"));
    }

    private void setupEncounterControls() {
        combatLog.setEditable(false);
        combatLog.setWrapText(true);
        enemyDetailsArea.setEditable(false);
        enemyDetailsArea.setWrapText(true);
    }

    private void loadBestiaryData() {
        ObservableList<Enemy> enemies = FXCollections.observableArrayList(
            enemyRepository.getAllEnemies()
        );
        bestiaryTable.setItems(enemies);
    }

    private void loadWeaponsData() {
        ObservableList<Weapon> weapons = FXCollections.observableArrayList(
            weaponRepository.getAllWeapons()
        );
        weaponsTable.setItems(weapons);
    }

    private void setupEventHandlers() {
        // Bestiary table selection
        bestiaryTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    displayEnemyDetails(newSelection);
                }
            });

        // Dice rolling buttons
        rollD100Button.setOnAction(e -> rollDice(100));
        rollD10Button.setOnAction(e -> rollDice(10));
        rollD6Button.setOnAction(e -> rollDice(6));
    }

    private void displayEnemyDetails(Enemy enemy) {
        StringBuilder details = new StringBuilder();
        details.append("=== ").append(enemy.name()).append(" ===\n\n");
        
        details.append("Statistics:\n");
        details.append("WS: ").append(enemy.stats().weaponSkill()).append("  ");
        details.append("BS: ").append(enemy.stats().ballisticSkill()).append("  ");
        details.append("S: ").append(enemy.stats().strength()).append("  ");
        details.append("T: ").append(enemy.stats().toughness()).append("\n");
        details.append("Ag: ").append(enemy.stats().agility()).append("  ");
        details.append("Int: ").append(enemy.stats().intelligence()).append("  ");
        details.append("WP: ").append(enemy.stats().willPower()).append("  ");
        details.append("Fel: ").append(enemy.stats().fellowship()).append("\n");
        details.append("A: ").append(enemy.stats().attacks()).append("  ");
        details.append("W: ").append(enemy.stats().wounds()).append("\n\n");
        
        if (!enemy.weaponName().isEmpty()) {
            details.append("Weapon: ").append(enemy.weaponName()).append("\n\n");
        }
        
        if (!enemy.abilities().isEmpty()) {
            details.append("Abilities:\n");
            enemy.abilities().forEach(ability -> 
                details.append("• ").append(ability.name())
                       .append(": ").append(ability.description()).append("\n"));
        }
        
        enemyDetailsArea.setText(details.toString());
    }

    private void rollDice(int sides) {
        int result;
        String diceType;
        
        switch (sides) {
            case 100 -> {
                result = diceRoller.rollD100();
                diceType = "d100";
            }
            case 10 -> {
                result = diceRoller.rollD10();
                diceType = "d10";
            }
            case 6 -> {
                result = diceRoller.rollD6();
                diceType = "d6";
            }
            default -> {
                result = 0;
                diceType = "unknown";
            }
        }
        
        String rollText = String.format("%s: %d", diceType, result);
        lastRollLabel.setText(rollText);
        
        // Add to combat log
        appendToCombatLog("Rolled " + rollText);
    }

    @FXML
    private void addToEncounter() {
        Enemy selectedEnemy = bestiaryTable.getSelectionModel().getSelectedItem();
        if (selectedEnemy != null) {
            // Add to initiative tracker
            int initiative = combatResolver.rollInitiative(selectedEnemy);
            initiativeTracker.addEntry(selectedEnemy, initiative);
            
            // Update encounter list
            updateEncounterList();
            
            appendToCombatLog(String.format("Added %s to encounter (Initiative: %d)", 
                selectedEnemy.name(), initiative));
        } else {
            showAlert("No Selection", "Please select an enemy from the bestiary first.");
        }
    }

    @FXML
    private void clearEncounter() {
        initiativeTracker.reset();
        updateEncounterList();
        appendToCombatLog("Encounter cleared");
        currentTurnLabel.setText("No active encounter");
    }

    @FXML
    private void nextTurn() {
        if (!initiativeTracker.isEmpty()) {
            initiativeTracker.nextTurn();
            updateCurrentTurn();
            appendToCombatLog("--- Next Turn ---");
        }
    }

    private void updateEncounterList() {
        ObservableList<String> encounterEntries = FXCollections.observableArrayList();
        
        initiativeTracker.getAllEntries().forEach(entry -> {
            String entryText = String.format("%s (Init: %d, W: %d/%d)", 
                entry.enemy().name(),
                entry.initiative(),
                entry.enemy().currentWounds(),
                entry.enemy().stats().wounds());
            encounterEntries.add(entryText);
        });
        
        encounterList.setItems(encounterEntries);
        updateCurrentTurn();
    }

    private void updateCurrentTurn() {
        var currentEntry = initiativeTracker.getCurrentEntry();
        if (currentEntry != null) {
            currentTurnLabel.setText(String.format("Current: %s (Round %d)", 
                currentEntry.enemy().name(), 
                initiativeTracker.getCurrentRound()));
        } else {
            currentTurnLabel.setText("No active encounter");
        }
    }

    private void appendToCombatLog(String message) {
        combatLog.appendText(message + "\n");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}