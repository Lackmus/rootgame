package com.rootgame.model.World.WorldObjects;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Path extends WorldObject {

    private WorldObject[] neighbours;

    public Path(WorldObjectType type, String name, int x, int y) {
        super(type, name, x, y);
        neighbours = new Settlement[2];
    }
    
    private void setDistance(WorldObject clearingA, WorldObject clearingB){
        this.distance = clearingA.getDistance(clearingB);
    }

    @Override
    public List<WorldObject> getNeighbours() {
        return new LinkedList<>(Set.of(neighbours));
    }

    @Override
    public boolean addNeighbour(WorldObject worldObject) {
        if (neighbours[0] == null) {
            neighbours[0] = worldObject;
        } else if (neighbours[1] == null) {
            neighbours[1] = worldObject;
        } else {
            System.out.println("Path already has two neighbours");
            return false;
        }
        if (neighbours[0] != null && neighbours[1] != null) {
            setDistance(neighbours[0], neighbours[1]);
        }
        return true;
    }

    @Override
    public boolean removeNeighbour(WorldObject worldObject) {
        if (neighbours[0] == worldObject) {
            neighbours[0] = null;
        } else if (neighbours[1] == worldObject) {
            neighbours[1] = null;
        } else {
            System.out.println("Path does not have this neighbour");
            return false;
        }
        return true;
    }

    @Override
    public boolean isNeighbour(WorldObject worldObject) {
        return neighbours[0] == worldObject || neighbours[1] == worldObject;
    }

    @Override
    public Path getPathTo(WorldObject neighbour) {
        if (neighbours[0] == neighbour) {
            return this;
        } else if (neighbours[1] == neighbour) {
            return this;
        } else {
            System.out.println("Path does not have this neighbour");
            return null;
        }
    }

    @Override
    public void setPath(WorldObject neighbour, WorldObject path) {
    }

    @Override
    public List <WorldObject> getPaths() {
        return new LinkedList<>();
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
