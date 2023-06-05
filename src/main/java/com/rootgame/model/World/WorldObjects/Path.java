package com.rootgame.model.World.WorldObjects;

public class Path extends WorldObject {

    public Path(WorldObjectType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    private void setDistance(WorldObject settlementA, WorldObject settlementB){
        this.distance = settlementA.getDistance(settlementB);
    }

    /**
     * This function adds a neighbouring WorldObject to a list and sets the distance between the two
     * objects if there are two neighbours in the list.
     * 
     * @param worldObject The parameter "worldObject" is an object of the class WorldObject that is
     * being added as a neighbour to the current object.
     * @return The method returns a boolean value.
     */
    @Override
    public boolean addNeighbour(WorldObject worldObject) {
        if (worldObject == null) {
            return false;
        }
        
        if (neighbours.size() == 2) {
            return false;
        } 

        if(!neighbours.add(worldObject)){
            return false;
        }

        if (neighbours.size() == 2) {
            setDistance(getNeighbours().get(0), getNeighbours().get(1));
        }

        neighbourStrings.add(worldObject.getName());
        return true;
    }

    /**
     * This function removes a neighbour from a list of neighbours and returns true if successful.
     * 
     * @param worldObject The parameter "worldObject" is an object of type WorldObject that represents
     * a neighboring object to be removed from the current object's list of neighbors.
     * @return A boolean value is being returned.
     */
    @Override
    public boolean removeNeighbour(WorldObject worldObject) {
        if (worldObject == null) {
            System.out.println("Cannot remove null neighbour");
            return false;
        }      
        if(!neighbours.remove(worldObject))
            return false;

        neighbourStrings.remove(worldObject.getName());
        return true;
    }

    @Override
    public void updateBesieged() {
        
    }

    @Override
    public void updateRuined() {
        
    }

    @Override
    public void growPopulation() {
        
    }

}
