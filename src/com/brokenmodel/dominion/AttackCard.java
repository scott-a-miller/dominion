/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

/**
 * 
 */
public interface AttackCard extends Card {
  public void attack(TurnState state);
  public void attack(TurnState state, PlayerState victimState);
}
