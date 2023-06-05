package com.rootgame.controller;

import java.util.ArrayList;
import java.util.List;

import com.rootgame.controller.MyObservable.ListUpdateEvent;
import com.rootgame.controller.MyObservable.ListUpdateListener;
import com.rootgame.model.LoadedModule;
import com.rootgame.model.World.EvolveWorld;
import com.rootgame.model.World.GenerateWorld;
import com.rootgame.model.World.WorldObjects.WorldObject;


public class World {

    private ArrayList<ListUpdateListener> listeners;
    
    private List<WorldObject> worldObjectList;
    private List<WorldObject> settlements;   // List of Clearing objects
    private List<WorldObject> paths;           // List of Path objects

    private int mapX;                   // Width of the world
    private int mapY;                   // Height of the world
    
    private List<String> clearingNames; // List of clearing names
    private List<String> factions;     // List of factions
    
    public World (int mapX, int mapY) {
        clearingNames = LoadedModule.getCityNames();
        factions = LoadedModule.getFactionList();
        listeners = new ArrayList<>();
        this.mapX = mapX;
        this.mapY = mapY;
        System.out.println(mapX + " " + mapY);
        
        worldObjectList = new ArrayList<>();
        settlements = new ArrayList<>();
        paths = new ArrayList<>();   
        
    }

    /**
     * This function adds a ListUpdateListener to a list of listeners.
     * 
     * @param listener The parameter "listener" is an object of the type ListUpdateListener, which is
     * an interface that defines a method to be called when a list is updated. This method is typically
     * implemented by a class that wants to be notified when changes are made to a list. The
     * addListUpdateListener method adds
     */
    public void addListUpdateListener(ListUpdateListener listener) {
        listeners.add(listener);
    }

    /**
     * This function removes a ListUpdateListener from a list of listeners.
     * 
     * @param listener The listener parameter is an object of type ListUpdateListener that represents
     * the listener to be removed from the list of listeners.
     */
    public void removeListUpdateListener(ListUpdateListener listener) {
        listeners.remove(listener);
    }

    /**
     * This function notifies all listeners of a list update event with the updated list as a
     * parameter.
     */
    
    public void notifyListeners(List<WorldObject> list) {
        this.worldObjectList = list;
        for (ListUpdateListener listener : listeners) {
          listener.listUpdated(new ListUpdateEvent(this, list));
        }
    }

    /**
     * This function generates a world by clearing it, generating world data, setting neighbors,
     * generating factions, populating the world, filling the world list, and notifying listeners, with
     * a maximum of 1000 retries.
     */
    public void generateWorld() {
        System.out.println("Generating world...");
        int retries = 0;
        try {
            
            System.out.println("Setting Neighbours and Factions...");
            do {
                clearWorld();
                generateWorldData();    
            } while ((!setNeighbours() || !generateFactions()) && retries++ < 1000);
            
            System.out.println("Setting Paths...");
            setPaths();

            System.out.println("Populating world...");
            populateWorld();

            fillWorldList();

            System.out.println("Drawing Map...");
            notifyListeners(getWorldObjectList());
           
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("World generated. " + retries + " retries.\n" );
    }
    
    /**
     * This function generates world data by filling clearings on a map with names at specific
     * coordinates.
     */
    private void generateWorldData() throws Exception {
        GenerateWorld.fillWorld(settlements, mapX, mapY, clearingNames);
    }
    
    /**
     * This function sets the neighbors of clearings and paths in a generated world.
     * 
     * @return A boolean value is being returned.
     */
    private boolean setNeighbours() throws Exception {
        return GenerateWorld.setNeighbours(settlements);
    }

    private void setPaths() throws Exception {
        GenerateWorld.setPaths(settlements, paths);
    }
           
    /**
     * This function generates factions for a world based on clearings and paths.
     * 
     * @return A boolean value is being returned.
     */
    private boolean generateFactions() throws Exception {
        return GenerateWorld.setFactionsToWorld(settlements, paths, factions);
    }
    
    /**
     * This function populates a world with clearings and paths using a helper method called
     * "populateWorld".
     */
    private void populateWorld() throws Exception {
        GenerateWorld.populateWorld(settlements, paths);
    }

   

    public void evolveWorld() {
        System.out.println("Evolving world...");
        
        EvolveWorld.evolveWorld(worldObjectList);
        notifyListeners(worldObjectList); 
    }

    /**
     * This Java function saves a list of world objects to an XML file named "world.xml".
     */
    public void saveWorld() {
       SaveWorld.saveToJSON("world.json", worldObjectList);
    }

    /**
     * This function loads a world by clearing it, loading an XML file, filling a world list, and
     * notifying listeners.
     */
    public void loadWorld() {
        clearWorld();
        LoadWorld.loadFromJson("world.json", settlements, paths);
        fillWorldList();
        notifyListeners(getWorldObjectList());
    }

    /**
     * The function clears the world by removing all objects, clearings, and paths.
     */
    public void clearWorld() {
        worldObjectList.clear();
        settlements.clear();
        paths.clear();
    }   

    /**
     * The function adds all clearings and paths to a world object list.
     */
    public void fillWorldList() {
        worldObjectList.addAll(settlements);
        worldObjectList.addAll(paths);
    }
    

    /**
     * This Java function returns a Clearing object from a list of Clearing objects based on a given
     * name.
     * 
     * @param name A String representing the name of the clearing to be retrieved.
     * @return The method is returning a Clearing object with the specified name. If no Clearing object
     * with the specified name is found in the clearings list, the method returns null.
     */
    public WorldObject getClearing (String name) {
        for (WorldObject settlement : settlements) {
            if (settlement.getName().equals(name)) {
                return settlement;
            }
        }
        return null;
    }

    
    /**
     * The function returns a list of Clearing objects.
     * 
     * @return A List of Clearing objects is being returned.
     */
    public List<WorldObject> getClearings() {
        return settlements;
    }

    /**
     * This function returns a list of Path objects.
     * 
     * @return A List of Path objects is being returned.
     */
    public List<WorldObject> getPaths() {
        return paths;
    }

    /**
     * This function returns a list of WorldObject.
     * 
     * @return A List of WorldObject objects is being returned.
     */
    public List<WorldObject> getWorldObjectList() {
        return worldObjectList;
    }
    
    /**
     * This is a Java override function that returns a string representation of an object of the World
     * class, including its clearings.
     * 
     * @return A string representation of an object of the class "World", which includes the value of
     * the "clearings" field.
     */
    @Override
    public String toString() {
        return "World{" + "clearings=" + settlements + ", paths=" + paths + "}";
    }    
}