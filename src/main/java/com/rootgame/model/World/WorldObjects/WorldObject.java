package com.rootgame.model.World.WorldObjects;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.rootgame.model.NPC.NPC;

public abstract class WorldObject {

    protected String name;
    protected int x;
    protected int y;
    protected List<NPC> npcs;
    protected String faction;
    protected Set<WorldObject> neighbours;
    protected int distance;
    protected int combatStrength;
    protected int marketValue;
    protected int loyalty;
    protected List<WorldObject> fightList;
    protected boolean besieged;
    protected int siegeTimer;
    protected boolean ruined;
    protected int ruinTimer;
    protected boolean isCapital;
    protected int currentPopulation;
    protected String description;
    protected WorldObjectType type;
    protected List<String> neighbourStrings; 

    public WorldObject(WorldObjectType type, String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        faction = null;
        combatStrength = -1;
        marketValue = -1;
        loyalty = -1;
        distance = -1;
        besieged = false;
        siegeTimer = -1;
        ruined = false;
        ruinTimer = -1;
        isCapital = false;
        currentPopulation = -1;
        description = "null";
        this.type = type;
        
        neighbours = new HashSet<>();
        npcs = new LinkedList<>();
        fightList = new LinkedList<>();
        neighbourStrings = new LinkedList<>();
    }

    /*
     * WorldObject Methods
     */

    public WorldObjectType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WorldObject getWorldObject(String name) {
        if (this.name.equals(name)) {
            return this;
        }
        return null;
    }

    public WorldObject addAll(WorldObject worldObject) {
        return this;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public void clearFactions(){
        this.faction = null;
    }

    public int getDistance() {
        return distance;
    }

    public int getDistance(WorldObject worldObject) {
        return (int) Math.sqrt(Math.pow(Math.abs(x - worldObject.getX()), 2) + Math.pow(Math.abs(y - worldObject.getY()), 2));
    }

    /*
     * status Methods
     */

    public void setCapital(boolean isCapital) {
        this.isCapital = isCapital;
    }

    public boolean isCapital() {
        return isCapital;
    }

    public int getCombatStrength() {
        return combatStrength;
    }

    public void updateCombatStrength() {
        if(currentPopulation > 5)
            this.combatStrength = currentPopulation - 5;
        else
            this.combatStrength = 1;
    }

    public void setCombatStrength(int combatStrength) {
        this.combatStrength = combatStrength;
    }

    public int getMarketValue() {
        return marketValue;
    }

    public void updateMarketValue() {
        if(currentPopulation > 10)
            this.marketValue = currentPopulation - 10;
        else
            this.marketValue = 5;
    }

    public void setMarketValue(int marketValue) {
        this.marketValue = marketValue;
    }

    public int getLoyalty() {
        return loyalty;
    }

    public void setLoyalty(int loyalty) {
    }

    /*
     * Siege Methods
     */

    public boolean isBesieged() {
        return besieged;
    }

    public void setBesieged(boolean besieged){
        this.besieged = besieged;
    }

    public abstract void updateBesieged();

    public int getSiegeTimer() {
        return siegeTimer;
    }

    public void setSiegeTimer(int siegeTimer) {
        this.siegeTimer = siegeTimer;
    }

    /*
     * Ruin Methods
     */
    public boolean isRuined() {
        return ruined;
    }

    public void setRuined(boolean ruined) {
        this.ruined = ruined;
    }

    public void setRuinTimer(int ruinTimer) {
        this.ruinTimer = ruinTimer;
    }

    public abstract void updateRuined();

    public int getRuinTimer() {
        return ruinTimer;
    }

    /*
     * Population Methods
     */
    public int getCurrentPopulation() {
        return currentPopulation;
    }

    public void setCurrentPopulation(int currentPopulation) {
        this.currentPopulation = currentPopulation;
    }

    public abstract void growPopulation();
    
    /*
     * NPC Methods
     */

    public void addNPC(NPC createdNPC) {
        npcs.add(createdNPC);
    }

    public void removeNPC(NPC npc) {
        npcs.remove(npc);
    }

    public void setNPCDestination(NPC npc, WorldObject destination) {
        NPC npcToMove = getNPC(npc);
        npcToMove.setDestination(destination);
    }

    public void moveNPCs() {
        for (NPC npc : npcs) {
            npc.moveNPC();
        }
    }
        
    public NPC getNPC(NPC npc) {
        return npcs.stream().filter(npc::equals).findFirst().orElse(null);
    }

    public void setNPCs(List<NPC> npcs) {
        this.npcs = npcs;
    }

    public List<NPC> getNPCs() {
        return npcs;
    }

  

    /*
     * Neighbour Methods
     */

     public boolean isNeighbour(WorldObject worldObject) {
        return neighbours.contains(worldObject);
    }

    public List<WorldObject> getNeighbours() {
        return new LinkedList<>(neighbours);
    }

    public boolean addNeighbour(WorldObject neighbour) {

        if (neighbours.size() == 4) {
            return false;
        }
        if (neighbour.getNeighbours().size() == 4 && !neighbour.getNeighbours().contains(this)){
            return false;
        }

        if (!neighbours.add(neighbour)) {
            return false;
        }
        
        if(!neighbour.hasType(WorldObjectType.PATH))
            neighbourStrings.add(neighbour.getName());
        return true;
    }

    public boolean removeNeighbour(WorldObject neighbour) {
        if (neighbours.remove(neighbour)) {
            return true;
        }
        return false;
    }

    /*
     * utilitiy Methods
     */

    public boolean hasType(WorldObjectType type) {
        return this.type.equals(type);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     * This is an implementation of the equals method in Java that checks if two Clearing objects are
     * equal based on their name attribute.
     * 
     * @param obj The "obj" parameter is an object of type Object, which is the superclass of all Java
     * classes. It is used to compare the current Clearing object with another object to check if they
     * are equal.
     * @return A boolean value is being returned, indicating whether the current object is equal to the
     * object passed as an argument.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WorldObject)) {
            return false;
        }
        WorldObject other = (WorldObject) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        }
        return this.name.equals(other.name) 
            && this.x == other.getX()
            && this.y == other.getY();
    }

    public void clear() {
        npcs.clear();
        neighbours.clear();
    }

    public List<String> getNeighbourStrings() {
        return neighbourStrings;
    }

    public String neighboursToString() {
       return neighbourStrings.toString();
    }

    @Override
    public String toString() {
        return name;
    }

    public enum Type{
        SETTLEMENT,
        PATH;
    }
       
}
