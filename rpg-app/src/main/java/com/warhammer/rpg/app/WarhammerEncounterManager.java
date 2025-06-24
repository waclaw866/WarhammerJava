package com.warhammer.rpg.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main JavaFX Application for Warhammer Fantasy 2e Encounter Manager
 */
public class WarhammerEncounterManager extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
            WarhammerEncounterManager.class.getResource("/fxml/main-view.fxml")
        );
        
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        
        // Add CSS styling
        scene.getStylesheets().add(
            getClass().getResource("/css/warhammer-style.css").toExternalForm()
        );
        
        stage.setTitle("Warhammer Fantasy 2e Encounter Manager");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        
        // Set application icon if available
        try {
            Image icon = new Image(getClass().getResourceAsStream("/images/warhammer-icon.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            // Icon not found, continue without it
        }
        
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}