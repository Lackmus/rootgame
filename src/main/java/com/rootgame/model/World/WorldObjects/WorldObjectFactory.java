package com.rootgame.model.World.WorldObjects;

public class WorldObjectFactory {
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
