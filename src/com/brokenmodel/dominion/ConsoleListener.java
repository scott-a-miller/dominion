/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

/**
 * @author Scott
 *
 */
public class ConsoleListener implements GameStateListener {
  public void eventOccurred(GameStateEvent event) {
    System.out.println(event.toString());
  }
}
