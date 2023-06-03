package com.rootgame.model.World.MyObservable;

import java.util.EventObject;
import java.util.List;

import com.rootgame.model.World.WorldObjects.WorldObject;

public class ListUpdateEvent extends EventObject {

  private static final long serialVersionUID = 1L;
  
  private List<WorldObject> list;

  public ListUpdateEvent(Object source, List<WorldObject> list) {
    super(source);
    this.list = list;
  }

  public List<WorldObject> getList() {
    return list;
  }
}