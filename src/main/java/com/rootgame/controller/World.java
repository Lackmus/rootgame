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
    private List<WorldObject> settlements;   // List of settlement objects
    private List<WorldObject> paths;           // List of Path objects

    private int mapX;                   // Width of the world
    private int mapY;                   // Height of the world
    
    private List<String> settlementNames; // List of settlement names
    private List<String> factions;     // List of factions
    
    public World (int mapX, int mapY) {
        settlementNames = LoadedModule.getCityNames();
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
        System.out.println("Drawing Map...");
        this.worldObjectList = list;
        for (ListUpdateListener listener : listeners) {
          listener.listUpdated(new ListUpdateEvent(this, list));
        }
        System.out.println("Map drawn.");
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
            do {
                clearWorld();
                generateWorldData();    
            } while ((!setNeighbours() || !generateFactions()) && retries++ < 1000);
            
            setPaths();
            populateWorld();
            fillWorldList();
            notifyListeners(getWorldObjectList());
           
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("World generated. " + retries + " retries.\n" );
    }
    
    /**
     * This function generates world data by filling settlements on a map with names at specific
     * coordinates.
     */
    private void generateWorldData() throws Exception {
        System.out.println("Generating World Data...");
        GenerateWorld.fillWorld(settlements, mapX, mapY, settlementNames);
        System.out.println("World Data generated.");
    }
    
    /**
     * This function sets the neighbors of settlements and paths in a generated world.
     * 
     * @return A boolean value is being returned.
     */
    private boolean setNeighbours() throws Exception {
        System.out.println("Setting Neighbours...");
        if (GenerateWorld.setNeighbours(settlements)){
            System.out.println("Neighbours set.");
            return true;
        }
        System.out.println("Neighbours could not be set.");
        return false;
    }

    private void setPaths() throws Exception {
        System.out.println("Setting Paths...");
        GenerateWorld.setPaths(settlements, paths);
        System.out.println("Paths set.");
    }
           
    /**
     * This function generates factions for a world based on settlements and paths.
     * 
     * @return A boolean value is being returned.
     */
    private boolean generateFactions() throws Exception {
        System.out.println("Generating Factions...");
        if (GenerateWorld.generateFactions(settlements, paths, factions)){
            System.out.println("Factions generated.");
            return true;
        }
        System.out.println("Factions could not be generated.");
        return false;
    }
    
    /**
     * This function populates a world with settlements and paths using a helper method called
     * "populateWorld".
     */
    private void populateWorld() throws Exception {
        System.out.println("Populating world...");
        GenerateWorld.populateWorld(settlements, paths);
        System.out.println("World populated.");
    }

   

    public void evolveWorld() {
        System.out.println("Evolving world...");
        for (int i = 0; i < 1; i++)    {  
            try{  
                EvolveWorld.evolveWorld(worldObjectList);
                notifyListeners(worldObjectList); 
            } catch (Exception e) {  
                e.printStackTrace();
                break;
            }
        }
        System.out.println("Evolved");

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
     * The function clears the world by removing all objects, settlements, and paths.
     */
    public void clearWorld() {
        worldObjectList.clear();
        settlements.clear();
        paths.clear();
    }   

    /**
     * The function adds all settlements and paths to a world object list.
     */
    public void fillWorldList() {
        worldObjectList.addAll(settlements);
        worldObjectList.addAll(paths);
    }
    

    /**
     * This Java function returns a settlement object from a list of settlement objects based on a given
     * name.
     * 
     * @param name A String representing the name of the settlements to be retrieved.
     * @return The method is returning a settlement object with the specified name. If no settlement object
     * with the specified name is found in the settlement list, the method returns null.
     */
    public WorldObject getSettlement (String name) {
        for (WorldObject settlement : settlements) {
            if (settlement.getName().equals(name)) {
                return settlement;
            }
        }
        return null;
    }

    
    /**
     * The function returns a list of settlement objects.
     * 
     * @return A List of settlement objects is being returned.
     */
    public List<WorldObject> getSettlements() {
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
     * class, including its settlements.
     * 
     * @return A string representation of an object of the class "World", which includes the value of
     * the "settlements" field.
     */
    @Override
    public String toString() {
        return "World{" + "settlements=" + settlements + ", paths=" + paths + "}";
    }    
}