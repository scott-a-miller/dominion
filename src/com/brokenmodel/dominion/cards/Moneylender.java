/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Moneylender extends AbstractCard implements ActionCard {
  public Moneylender(int cardId) {
    super(cardId, 4);
  }
  
  public void performAction(TurnState state) {
    Copper copper = null;
    for (Card card : state.getPlayerState().getHand()) {
      if (card instanceof Copper) {
        copper = (Copper)card;
        break;
      }
    }
    if (copper != null) {
      if (state.getPlayerState().getPlayer().trashCardForDiscount(copper, 3, new VisibleTurnState(state, state.getPlayerState()))) {
        state.getPlayerState().trash(copper);
        state.addCostDiscount(3);
      }
    }
  }
}
