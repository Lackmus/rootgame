package com.rootgame.controller.MyObservable;

import java.util.EventListener;

/**
 * This interface defines a method to be called when a list is updated. This method is typically
 * implemented by a class that wants to be notified when changes are made to a list.
 * 
 * @see ListUpdateEvent
 */
public interface ListUpdateListener extends EventListener {
  
  void listUpdated(ListUpdateEvent e);
}
