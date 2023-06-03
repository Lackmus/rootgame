package com.rootgame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.rootgame.View.MapPane;
import com.rootgame.model.NPC.FactionRaces;
import com.rootgame.model.World.World;
import com.rootgame.model.World.MyObservable.ListUpdateEvent;
import com.rootgame.model.World.MyObservable.ListUpdateListener;

public class App extends Application implements ListUpdateListener{
    private Pane mapPane;
    private World world;

    @Override
    public void start(Stage primaryStage) { 
        System.out.println("Java version: " + System.getProperty("java.version"));
        FactionRaces.loadModule("Root");
        primaryStage.setTitle("Map Application");
        mapPane = new Pane();
        mapPane.setPrefSize(500, 500);
        world = new World(500, 500);
        world.addListUpdateListener(this);
        world.generateWorld();
        MapPane.drawMap(mapPane,world.getWorldObjectList());

        BorderPane root = new BorderPane(); 
        ToolBar toolBar = createToolBar();
        root.setTop(toolBar);
        root.setCenter(mapPane);

        Scene scene = new Scene(root, 500, 550);
        primaryStage.setScene(scene);
        primaryStage.show();

        // fix the scene size and prevent it from being resized
        primaryStage.setResizable(false);
    }

    private ToolBar createToolBar() {

        Button generateButton = new Button("Generate");
        generateButton.setOnAction(event -> {
            world.generateWorld();      
        });
        buttonShadow(generateButton);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            world.saveWorld();
            showInfoAlert("World Saved", "The world has been saved.");
        });
        buttonShadow(saveButton);

        Button loadButton = new Button("Load");
        loadButton.setOnAction(event -> {
            world.loadWorld();
            showInfoAlert("World Loaded", "The world has been loaded.");
        });
        buttonShadow(loadButton);

        Button evolveButton = new Button("Evolve");
        evolveButton.setOnAction(event -> {
            world.evolveWorld();
            showInfoAlert("World evolved", "The world has been evolved.");
        });
        buttonShadow(evolveButton);

        ToolBar toolBar = new ToolBar();
        toolBar.getItems().addAll(generateButton, saveButton, loadButton, evolveButton);
        return toolBar;
    }

    private void buttonShadow(Button button) {
        Effect shadow = new DropShadow();
        button.setOnMouseEntered(e -> button.setEffect(shadow));
        button.setOnMouseExited(e -> button.setEffect(null));
    }

    @Override
    public void listUpdated(ListUpdateEvent e) {
        mapPane.getChildren().clear();                              
        MapPane.drawMap(mapPane, e.getList());  
    }

    private void showInfoAlert(String title, String message) {
        Alert infoAlert = new Alert(AlertType.INFORMATION);
        infoAlert.setTitle(title);
        infoAlert.setHeaderText(null);
        infoAlert.setContentText(message);
        infoAlert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}