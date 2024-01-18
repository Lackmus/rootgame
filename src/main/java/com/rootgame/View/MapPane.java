package com.rootgame.view;

import java.util.List;

import com.rootgame.model.LoadedModule;
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
        for (WorldObject worldObject : worldObjectList) {
            if (worldObject.hasType(WorldObjectType.PATH)) {
                drawPath(mapPane, worldObject);
            } 
        }
        for (WorldObject worldObject : worldObjectList) {
            if (worldObject.hasType(WorldObjectType.SETTLEMENT)) {
                drawSettlement(mapPane, worldObject);
            } 
        }
    }

    private static void drawPath(Pane mapPane, WorldObject worldObject) {
        int[] start = {worldObject.getNeighbours().get(0).getX(), worldObject.getNeighbours().get(0).getY() };
        int[] end = {worldObject.getNeighbours().get(1).getX(), worldObject.getNeighbours().get(1).getY() };
        
        int x = worldObject.getX();
        int y = worldObject.getY();
        Color color = LoadedModule.getFactionColor(worldObject.getFaction());

        /* 
        if ( worldObject.getNPCs().stream().anyMatch(npc -> npc.getType().equals(NPCType.CARAVAN))) {
            color = Color.BLACK;
        }
        */
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

    
    /**
     * This function draws a settlement on a map pane, with a larger circle representing the settlement
     * and a smaller black circle inside representing a capital.
     * 
     * @param mapPane The mapPane parameter is a Pane object that represents the container where the
     * settlement will be drawn. It is used to add the circle shapes representing the settlement to the
     * mapPane.
     * @param worldObject An object representing a settlement in the game world. It contains
     * information such as the settlement's coordinates (x and y), faction, and whether it is a capital
     * or not. It also has a list of NPCs (non-player characters) associated with the settlement.
     */
    private static void drawSettlement(Pane mapPane, WorldObject worldObject) {       

        int x = worldObject.getX();
        int y = worldObject.getY();
        Color color = LoadedModule.getFactionColor(worldObject.getFaction());

        /*
        if ( worldObject.getNPCs().stream().anyMatch(npc -> npc.getType().equals(NPCType.CARAVAN))) {
            color = Color.BLACK;
        }
        */

        Circle circle = drawCircle(mapPane, x, y, color,10);
        // if is capital draw a black circle inside
        if (worldObject.hasType(WorldObjectType.SETTLEMENT) && worldObject.isCapital()) {
            Circle innerCircle = drawCircle(mapPane, x, y, Color.BLACK, 5);
            innerCircle.setMouseTransparent(true);
        }
        
        
        circle.setOnMouseClicked(event -> showSettlementInfo(worldObject));
        circle.setOnMouseEntered(event -> {
            handleMouseEntered(circle, x, y, mapPane, worldObject);
        }); 
    }

    /**
     * The function handles the mouse entering a circle by increasing its radius, changing its color,
     * and displaying a text container above it, and handles the mouse exiting the circle by removing
     * the text container and restoring the circle's original properties.
     * 
     * @param circle The circle parameter is an instance of the Circle class, which represents a circle
     * shape in JavaFX. It is used to visually represent an object on a map.
     * @param x The x-coordinate of the mouse pointer when it enters the circle.
     * @param y The parameter "y" in the handleMouseEntered method represents the y-coordinate of the
     * mouse pointer when it enters the circle.
     * @param mapPane The mapPane parameter is a Pane object that represents the container where the
     * circles and text containers are displayed.
     * @param worldObject The `worldObject` parameter is an object that represents a world entity. It
     * likely contains information such as the name of the object, its position, and other relevant
     * data.
     */
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
    
        adjustContainer(mapPane, container);
        
        mapPane.getChildren().add(container);
        circle.setOnMouseExited(exitEvent  -> {
            mapPane.getChildren().remove(container);
            circle.setRadius(circle.getRadius() - 2);
            circle.setFill(color);
            circle.setStrokeWidth(1);
        });
    }

    /**
     * The function adjusts the position of a container within a map pane to ensure it is fully
     * visible.
     * 
     * @param mapPane The mapPane parameter is a Pane object that represents the container where the
     * map is displayed. It is used to determine the dimensions of the mapPane and to compare them with
     * the dimensions of the container.
     * @param container The container is a StackPane that holds the content that needs to be adjusted
     * within the mapPane.
     */
    private static void adjustContainer(Pane mapPane, StackPane container){
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
    }

    /**
     * The function "drawCircle" creates a circle with the specified center coordinates, color, and
     * radius, adds it to a pane, and returns the circle object.
     * 
     * @param mapPane The mapPane parameter is a Pane object that represents the container where the
     * circle will be drawn.
     * @param centerX The x-coordinate of the center of the circle.
     * @param centerY The centerY parameter represents the y-coordinate of the center of the circle.
     * @param color The "color" parameter is of type Color and represents the fill color of the circle.
     * @param radius The radius parameter is the length of the line segment from the center of the
     * circle to any point on its circumference.
     * @return The method is returning a Circle object.
     */
    private static Circle drawCircle(Pane mapPane, double centerX, double centerY, Color color, double radius) {
        Circle circle = new Circle(centerX, centerY, radius);
        circle.setFill(color);
        mapPane.getChildren().add(circle);
        return circle;
    }

    /**
     * The function creates a JavaFX stage that displays information about a WorldObject, including its
     * distance, faction, and a dropdown menu of NPCs.
     * 
     * @param worldObject The `worldObject` parameter is an instance of the `WorldObject` class.
     */
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

    /**
     * The function "showSettlementInfo" creates a GUI window to display information about a world
     * object, including its name, faction, NPCs, neighbors, and description.
     * 
     * @param worldObject The `worldObject` parameter is an instance of the `WorldObject` class. It
     * represents a settlement in the world and contains information such as its name, faction, NPCs
     * (non-player characters) present in the settlement, and its neighbors (other settlements nearby).
     */
    private static void showSettlementInfo(WorldObject worldObject) {
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
            worldObject.neighboursToString()
        );

        Text actualNeighbour = new Text(
            worldObject.getNeighbours().toString()
        );

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.getChildren().addAll(
            new Text("Name:    " + worldObject.getName()),
            new Text("Faction: " + worldObject.getFaction()),
            new Text(worldObject.getNPCs().toString()),
            new Text("Neighbours:"),
            neighbourText,
            actualNeighbour,
            new Label(""),
            new Label("Description:"),
            textArea
        );

        BorderPane root = new BorderPane();
        root.setTop(new ToolBar());
        root.setCenter(vBox);

        Scene scene = new Scene(root, 500, 500);//350, 300
        stage.setScene(scene);
        stage.show();
        stage.setResizable(true);
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