package com.rootgame.View;

import java.util.List;

import com.rootgame.model.NPC.FactionRaces;
import com.rootgame.model.NPC.NPC;
import com.rootgame.model.World.WorldObjects.WorldObject;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import javafx.scene.layout.BorderPane;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MapPane2 {

    private ObjectProperty<WorldObject> selectedObject = new SimpleObjectProperty<>();
    
    public MapPane2() {
    }

    public void drawMap2(Pane mapPane, List<WorldObject> worldObjectList) {
        mapPane.getChildren().clear();

        for (WorldObject worldObject : worldObjectList) {
            if (worldObject.getName().contains("Path")) {
                drawPath(mapPane, worldObject);
            } else {
                drawClearing(mapPane, worldObject);
            }
        }
    }

    private void drawPath(Pane mapPane, WorldObject worldObject) {
        int[] start = {worldObject.getNeighbours().get(0).getX(), worldObject.getNeighbours().get(0).getY()};
        int[] end = {worldObject.getNeighbours().get(1).getX(), worldObject.getNeighbours().get(1).getY()};
        Color color = FactionRaces.getFactionColor(worldObject.getFaction());

        Line line = new Line(start[0], start[1], end[0], end[1]);
        line.setStroke(color);
        line.setStrokeWidth(1);
        line.setMouseTransparent(true);
        mapPane.getChildren().add(line);

        Circle circle = createCircle(worldObject.getX(), worldObject.getY(), 5, color);
        circle.setOnMouseClicked(event -> showPathInfo(worldObject));
        mapPane.getChildren().add(circle);
    }

    private void drawClearing(Pane mapPane, WorldObject worldObject) {
        int x = worldObject.getX();
        int y = worldObject.getY();
        Color color = FactionRaces.getFactionColor(worldObject.getFaction());

        Circle circle = createCircle(x, y, 10, color);
        circle.setOnMouseClicked(event -> showClearingInfo(worldObject));
        mapPane.getChildren().add(circle);
    }


    private Circle createCircle(double x, double y, double radius, Color color) {
        Circle circle = new Circle(x, y, radius);
        circle.setFill(color);
        return circle;
    }

    private void showPathInfo(WorldObject worldObject) {
        BorderPane root = new BorderPane();
        ComboBox<NPC> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList(worldObject.getNPCs()));

        VBox vBox = createInfoVBox(worldObject);
        vBox.getChildren().addAll(
                new Label("Neighbours: " + worldObject.getNeighbours().stream().map(WorldObject::getName).reduce("", (a, b) -> a + " " + b)),
                new Label("NPCs:"),
                comboBox
        );

        Stage stage = createStage("Path Info", root);

        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showNPCDescription(newValue);
            }
        });

        root.setCenter(vBox);
        selectedObject.addListener((observable, oldValue, newValue) -> {
            if (newValue == null || !newValue.getName().contains("Path")) {
                stage.close();
            }
        });
    }

    private void showClearingInfo(WorldObject worldObject) {
        BorderPane root = new BorderPane();
        ComboBox<NPC> comboBox = new ComboBox<>();
        TextArea textArea = new TextArea();
        textArea.setStyle("-fx-alignment: left;");
        textArea.setPrefRowCount(5);
        textArea.setWrapText(true);

        VBox vBox = createInfoVBox(worldObject);
        vBox.getChildren().addAll(
                new Label("Neighbours: " + worldObject.getNeighbours().stream().map(WorldObject::getName).reduce("", (a, b) -> a + " " + b)),
                new Label("NPCs:"),
                comboBox,
                textArea
        );

        Stage stage = createStage("Clearing Info", root);

        comboBox.setItems(FXCollections.observableArrayList(worldObject.getNPCs()));
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                textArea.setText(newValue.getDescription());
            }
        });

        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            NPC selectedNPC = comboBox.getSelectionModel().getSelectedItem();
            if (selectedNPC != null) {
                selectedNPC.setDescription(newValue);
            }
        });

        selectedObject.addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.getName().contains("Path")) {
                stage.close();
            }
        });
    }

    private void showNPCDescription(NPC npc) {
        Stage stage = new Stage();
        stage.setTitle("NPC Description");

        BorderPane root = new BorderPane();
        TextArea textArea = new TextArea();
        textArea.setText(npc.getDescription());
        textArea.setStyle("-fx-alignment: left;");
        textArea.setPrefRowCount(5);
        textArea.setWrapText(true);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            npc.setDescription(newValue);
        });

        root.setCenter(textArea);
        stage.setScene(new Scene(root, 300, 200));
        stage.show();
    }

    private Stage createStage(String title, BorderPane root) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 500, 300));
        stage.show();
        return stage;
    }

    private VBox createInfoVBox(WorldObject worldObject) {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.getChildren().addAll(
                new Text(worldObject.getName() + " " + worldObject.getFaction()),
                new Label("")
        );
        return vBox;
    }



}
