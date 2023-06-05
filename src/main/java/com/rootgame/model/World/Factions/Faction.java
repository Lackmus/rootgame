package com.rootgame.model.World.Factions;

import java.util.ArrayList;
import java.util.List;

import com.rootgame.model.NPC.NPC;
import com.rootgame.model.World.WorldObjects.WorldObject;

public class Faction {
    // var name, List isAtWarWith, List NPCs, List WorldObjects
    private String name;
    private List<String> isAtWarWith;
    private List<NPC> NPCs;
    private List<WorldObject> WorldObjects;
    
    public Faction(String name) {
        this.name = name;
        this.isAtWarWith = new ArrayList<String>();
        this.NPCs = new ArrayList<NPC>();
        this.WorldObjects = new ArrayList<WorldObject>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIsAtWarWith() {
        return isAtWarWith;
    }

    public List<NPC> getNPCs() {
        return NPCs;
    }

    public List<WorldObject> getWorldObjects() {
        return WorldObjects;
    }
    
    //add

    public void addIsAtWarWith(String isAtWarWith) {
        this.isAtWarWith.add(isAtWarWith);
    }

    public void addNPC(NPC npc) {
        this.NPCs.add(npc);
    }

    public void addWorldObject(WorldObject worldObject) {
        this.WorldObjects.add(worldObject);
    }

    //remove

    public void removeIsAtWarWith(String isAtWarWith) {
        this.isAtWarWith.remove(isAtWarWith);
    }

    public void removeNPC(NPC npc) {
        this.NPCs.remove(npc);
    }

    public void removeWorldObject(WorldObject worldObject) {
        this.WorldObjects.remove(worldObject);
    }

    //contains

    public boolean containsIsAtWarWith(String isAtWarWith) {
        return this.isAtWarWith.contains(isAtWarWith);
    }

    public boolean containsNPC(NPC npc) {
        return this.NPCs.contains(npc);
    }

    public boolean containsWorldObject(WorldObject worldObject) {
        return this.WorldObjects.contains(worldObject);
    }

    // is empty 

    public boolean isIsAtWarWithEmpty() {
        return this.isAtWarWith.isEmpty();
    }

    public boolean isNPCsEmpty() {
        return this.NPCs.isEmpty();
    }

    public boolean isWorldObjectsEmpty() {
        return this.WorldObjects.isEmpty();
    }

    // equals and hashcode

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Faction))
            return false;
        Faction faction = (Faction) obj;
        return name.equals(faction.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    // toString

    @Override
    public String toString() {
        return name;
    }
}
