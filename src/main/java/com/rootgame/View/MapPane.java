package com.rootgame.View;

import java.util.List;
import java.util.stream.Collectors;

import com.rootgame.model.NPC.FactionRaces;
import com.rootgame.model.NPC.NPC;
import com.rootgame.model.World.WorldObjects.WorldObject;
import com.rootgame.model.World.WorldObjects.WorldObjectType;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MapPane {

    /**
     * This function draws a map on a pane and adds mouse click event handlers to display information
     * about the world objects on the map.
     * 
     * @param mapPane A JavaFX Pane object where the map will be drawn.
     * @param worldObjectList A list of WorldObject objects that represent the objects to be drawn on
     * the map.
     */
    public static void drawMap(Pane mapPane, List<WorldObject> worldObjectList) {
        System.out.println("Drawing map");
        for (WorldObject worldObject : worldObjectList) {
            if (worldObject.hasType(WorldObjectType.PATH)) {
                drawPath(mapPane, worldObject);
            } 
        }
        for (WorldObject worldObject : worldObjectList) {
            if (worldObject.hasType(WorldObjectType.SETTLEMENT)) {
                drawClearing(mapPane, worldObject);
            } 
        }
    }

    private static void drawPath(Pane mapPane, WorldObject worldObject) {
        int[] start = {worldObject.getNeighbours().get(0).getX(), worldObject.getNeighbours().get(0).getY() };
        int[] end = {worldObject.getNeighbours().get(1).getX(), worldObject.getNeighbours().get(1).getY() };
        
        int x = worldObject.getX();
        int y = worldObject.getY();
        Color color = FactionRaces.getFactionColor(worldObject.getFaction());

        Line line = new Line(start[0], start[1], end[0], end[1]);
        line.setStroke(color);
        line.setStrokeWidth(1);
        line.setMouseTransparent(true);

        mapPane.getChildren().add(line);

        Circle circle = drawCircle(mapPane, x, y, color,5);
        circle.setOnMouseClicked(event -> showPathInfo(worldObject));
        circle.setOnMouseEntered(event -> {
            handleMouseEntered(circle, x, y, mapPane, worldObject);
        });
    }

    private static void drawClearing(Pane mapPane, WorldObject worldObject) {       

        int x = worldObject.getX();
        int y = worldObject.getY();
        Color color = FactionRaces.getFactionColor(worldObject.getFaction());

        Circle circle = drawCircle(mapPane, x, y, color,10);
        circle.setOnMouseClicked(event -> showClearingInfo(worldObject));
        circle.setOnMouseEntered(event -> {
            handleMouseEntered(circle, x, y, mapPane, worldObject);
        }); 
    }

    private static void handleMouseEntered(Circle circle, double x, double y, Pane mapPane, WorldObject worldObject) {
        Text text = new Text(worldObject.getName());
        double textWidth = text.getLayoutBounds().getWidth();
        // get circle color
        Color color = (Color) circle.getFill();
        
        // change circle radius and color only when mouse is over the circle
        circle.setRadius(circle.getRadius() + 2);
        circle.setFill(Color.WHITE);
        circle.setStroke(color);
        circle.setStrokeWidth(2);


        StackPane container = new StackPane(text);
        container.setMouseTransparent(true);
        container.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: white; ");
        container.setPrefWidth(textWidth + 20);
        container.setLayoutX(x - container.getPrefWidth() / 2);
        container.setLayoutY(y - 40);
    
        Boolean isLeft = false;
        Boolean isRight = false;
        Boolean isTop = false;
        Boolean isBottom = false;
        
        while ((isRight = container.getLayoutX() + container.getPrefWidth() > mapPane.getWidth())
            || (isLeft = container.getLayoutX()  < 0)
            || (isTop = container.getLayoutY() + container.getPrefHeight() > mapPane.getHeight())
            || (isBottom = container.getLayoutY() - container.getPrefHeight() < 0)) {
            if (mapPane.getWidth() < container.getPrefWidth() || mapPane.getHeight() < container.getPrefHeight()) {
                break;
            } 
            if (isRight) {
                container.setLayoutX(container.getLayoutX() - 1);
            } else if (isLeft) {
                container.setLayoutX(container.getLayoutX() + 1);
            } else if (isTop) {
                container.setLayoutY(container.getLayoutY() - 1);
            } else if (isBottom) {
                container.setLayoutY(container.getLayoutY() + 1);
            }
        }
        mapPane.getChildren().add(container);
        circle.setOnMouseExited(exitEvent  -> {
            mapPane.getChildren().remove(container);
            circle.setRadius(circle.getRadius() - 2);
            circle.setFill(color);
            circle.setStrokeWidth(1);
        });
    }

    

    private static Circle drawCircle(Pane mapPane, double centerX, double centerY, Color color, double radius) {
        Circle circle = new Circle(centerX, centerY, radius);
        circle.setFill(color);
        mapPane.getChildren().add(circle);
        return circle;
    }

    private static void showPathInfo(WorldObject worldObject) {
        Stage stage = new Stage();
        stage.setTitle("Path");

        ComboBox<NPC> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(worldObject.getNPCs());

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.getChildren().addAll(
                new Text("Distance: " + String.format("%.2f", (double) worldObject.getDistance() / 100) + " km"),
                new Label(worldObject.getFaction()),
                new Label("NPCs:"),
                comboBox
        );

        BorderPane root = new BorderPane();
        root.setTop(new ToolBar());
        root.setCenter(vBox);

        Scene scene = new Scene(root, 500, 100);
        stage.setScene(scene);
        stage.show();
    }

    private static void showClearingInfo(WorldObject worldObject) {
        Stage stage = new Stage();
        stage.setTitle("Settlement");

        TextArea textArea = new TextArea(worldObject.getDescription());
        textArea.setStyle("-fx-alignment: left;");
        textArea.setPrefHeight(200);
        textArea.setPrefRowCount(5);
        textArea.setWrapText(true);

        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            worldObject.setDescription(newValue);
        });

        Text neighbourText = new Text(
            worldObject.getNeighbours().stream()
            .map(WorldObject::getName)
            .collect(Collectors.joining(", "))
        );

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.getChildren().addAll(
            new Text("Name:    " + worldObject.getName()),
            new Text("Faction: " + worldObject.getFaction()),
            new Text(""),
            new Text("Neighbours:"),
            neighbourText,
            new Label(""),
            new Label("Description:"),
            textArea
        );

        BorderPane root = new BorderPane();
        root.setTop(new ToolBar());
        root.setCenter(vBox);

        Scene scene = new Scene(root, 350, 300);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }
}   


        //ComboBox<NPC> comboBox = new ComboBox<>();


 /*
        Boolean npcListBoolean = worldObject.getNPCs().size() > 0;
        if (npcListBoolean) {
            comboBox.getItems().addAll(worldObject.getNPCs());
            comboBox.getSelectionModel().selectFirst();
            comboBox.setOnAction(e -> {
                textArea.setText(comboBox.getValue().getDescription());
            });

            textArea.setText(comboBox.getSelectionModel().getSelectedItem().getDescription());
            textArea.textProperty().addListener((observable, oldValue, newValue) -> {
                comboBox.getSelectionModel().getSelectedItem().setDescription(newValue);
            });
        }
        */

                    //npcListBoolean ? comboBox : new Label("No NPCs here."),
            //npcListBoolean ? textArea : new Label("")


    /* 
        stage.setOnShown(e -> {
            double width = neighbourText.getLayoutBounds().getWidth() + 40;
             
            if (npcListBoolean) {
                textArea.setPrefWidth(comboBox.getWidth());
                textArea.setMaxWidth(comboBox.getWidth());
                stage.setWidth(Math.max(width, comboBox.getWidth() + 20));
            } else { 
                stage.setWidth(Math.max(width, 240));
                stage.setHeight(200);
            }
        });*/