/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

/**
 * 
 */
public interface ReactionCard extends Card {
  public boolean react(TurnState state, PlayerState victimState, AttackCard card);
}
