package com.rootgame.model.World.WorldObjects;

public class Path extends WorldObject {

    public Path(WorldObjectType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    private void setDistance(WorldObject settlementA, WorldObject settlementB){
        this.distance = settlementA.getDistance(settlementB);
    }

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

        return true;
    }

    @Override
    public boolean removeNeighbour(WorldObject worldObject) {
        if (worldObject == null) {
            System.out.println("Cannot remove null neighbour");
            return false;
        }      
        return getNeighbours().remove(worldObject);
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
