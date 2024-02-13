package com.rootgame.model.NPC;

import java.util.LinkedList;
import java.util.Queue;

import com.rootgame.model.World.WorldObjects.WorldObject;

public class NPC {
    
    private String name;
    private String description;
    private String species;
    private String faction;
    private int combatStrength;
    private int marketValue;
    private int loyalty;
    private NPCType npcType;
    private WorldObject currentLocation;
    private WorldObject origin;
    private WorldObject destination;
    private Queue<WorldObject> destinationPath;
    private boolean moved;

    public NPC(String name, String species, NPCType npcType, String faction, WorldObject origin) {
        this.name = name;
        this.species = species;
        this.faction = faction;
        this.npcType = npcType;
        this.origin = origin;
        this.currentLocation = origin;
        this.destination = currentLocation;
        this.destinationPath = new LinkedList<>();
        description = "This is a dicripion of " + name + ".";  
        combatStrength = 0;
        marketValue = 0;
        loyalty = 0;
        moved = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    } 

    public String getFaction() {
        return faction;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public boolean hasMoved() {
        return moved;
    }

    /** 
     *  Set the faction of the NPC. 50% chance of being the faction passed in, 50% chance of being neutral.
     *  If the NPC is the faction passed in, set loyalty to 100, otherwise set loyalty to 0.
     *   
     * @param faction
     * 
     */
    public void setFaction(String faction) {
        this.faction = faction;
    }

    public int getCombatStrength() {
        return combatStrength;
    }

    public int getMarketValue() {
        return marketValue;
    }

    public int getLoyalty() {
        return loyalty;
    }

    public void setType(NPCType npcType) {
        this.npcType = npcType;
    }

    public NPCType getType() {
        return npcType;
    }

    public String getSpecies() {
        return species;
    }  

    public  void setCombatStrength(int combatStrength){
        this.combatStrength = combatStrength;
    }
    public void setMarketValue(int marketValue){
        this.marketValue = marketValue;
    }
    public void setLoyalty(int loyalty){
        this.loyalty = loyalty;
    }

    public WorldObject getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(WorldObject currentLocation) {
        this.currentLocation = currentLocation;
    }

    public WorldObject getDestination() {
        return destination;
    }

    public void addDestination(WorldObject destination){
        if(!destinationPath.contains(destination))
            destinationPath.add(destination);
    }

    public void setDestinationPath(Queue<WorldObject> destinationPath) {
        this.destinationPath = destinationPath;
    }

    public void clearDestinationPath(){
        destinationPath.clear();
    }

    public void setDestination(WorldObject destination){
        this.destination = destination;
    };

    /**
     * This function moves an NPC to a new location along a predetermined path.
     */
    public void moveNPC(){
        if (destinationPath.size() > 0 && !hasMoved()){
            origin = currentLocation;
            currentLocation = destinationPath.poll();
            currentLocation.addNPC(this);
            origin.removeNPC(this);
            System.out.println("NPC: " + name + " moved from " + getOrigin().getName() + " to " + currentLocation.getName());
        }
    }

    public WorldObject getOrigin() {
        return origin;
    }

    public void setOrigin(WorldObject origin) {
        this.origin = origin;
    }

    public Queue<WorldObject> getDestinationPath() {
        return destinationPath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof NPC))
            return false;
        if (obj == this)
            return true;
        return this.name.equals(((NPC) obj).getName()) 
            && this.species.equals(((NPC) obj).getSpecies())
            && this.faction.equals(((NPC) obj).getFaction())
            && this.npcType.equals(((NPC) obj).getType())
            && this.origin.equals(((NPC) obj).getOrigin());
    }
    
    @Override
    public String toString() {
        String loyaltyString = this.loyalty == -1 ? "" : this.loyalty < 25 ? "disloyal" : this.loyalty < 50 ? "unreliable" : this.loyalty < 75 ? "loyal" : "devoted";        
        return npcType + ", " + faction + ", Loyalty: " + loyaltyString +  
            "\nName: " + name + ", Species: " + species +  
            (!destinationPath.isEmpty() ? "\nDestination: " + destinationPath.peek() : "") + 
            (!origin.equals(currentLocation) ? "\nOrigin: " + origin.getName() : "") + "\n";    
    }

    
}
