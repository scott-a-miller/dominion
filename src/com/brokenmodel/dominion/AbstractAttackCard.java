/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.util.*;

/**
 *
 */
public abstract class AbstractAttackCard extends AbstractCard implements AttackCard {
  public AbstractAttackCard(int cardId, int cost) {
    super(cardId, cost);
  }
  
  public void attack(TurnState state) {
    List<PlayerState> playerStates = state.getGameState().getOtherPlayerStatesInTurnOrder(state);
    for (PlayerState victimState : playerStates) {
      ReactionCard reaction = victimState.getPlayer().chooseToRevealReaction(this, new VisibleTurnState(state, victimState));
      if (reaction == null || !reaction.react(state, victimState, this)) {
        attack(state, victimState);
      }
    }
  }
}
