package com.rootgame.model.World.WorldObjects;

public class WorldObjectFactory {
    
    /**
     * The function creates a new world object based on the given type, name, and coordinates.
     * 
     * @param type The type of the world object to be created (either SETTLEMENT or PATH).
     * @param name The name of the world object being created.
     * @param x The x-coordinate of the location where the WorldObject will be created.
     * @param y The y-coordinate of the location where the WorldObject will be created.
     * @return The method `createWorldObject` returns a `WorldObject` instance, which can be either a
     * `Settlement` or a `Path` object depending on the `WorldObjectType` parameter passed to the
     * method.
     */
    public static WorldObject createWorldObject(WorldObjectType type, String name, int x, int y) {
        switch (type) {
            case SETTLEMENT:
                return new Settlement(type ,name, x, y);
            case PATH:
                return new Path(type, name, x, y);
            default:
                throw new IllegalArgumentException("Invalid object type: " + type);
        }
    }
}
