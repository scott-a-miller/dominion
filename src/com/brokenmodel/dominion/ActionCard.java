/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

/**
 * 
 */
public interface ActionCard extends Card {
  public abstract void performAction(TurnState state);
}