/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Witch extends AbstractAttackCard implements ActionCard {
  public Witch(int cardId) {
    super(cardId, 5);
  }
  
  public void performAction(TurnState state) {
    state.getPlayerState().draw(2);
  }

  public void attack(TurnState state, PlayerState victimState) {
    if (state.getGameState().isAvailable(Curse.class)) {
      Card card = state.getGameState().takeTopCard(Curse.class);
      victimState.discard(card);
    }
  }
}
