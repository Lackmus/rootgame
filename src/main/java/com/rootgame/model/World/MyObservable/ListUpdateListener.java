package com.rootgame.model.World.MyObservable;

import java.util.EventListener;

public interface ListUpdateListener extends EventListener {
  
  void listUpdated(ListUpdateEvent e);
}
