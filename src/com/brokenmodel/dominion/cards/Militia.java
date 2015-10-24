/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import java.util.*;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Militia extends AbstractAttackCard implements ActionCard {
  public Militia(int cardId) {
    super(cardId, 4);
  }
  
  public void performAction(TurnState state) {
    state.addCostDiscount(2);
  }

  public void attack(TurnState state, PlayerState victimState) {
    List<Card> cardsToKeep = victimState.getPlayer().chooseCardsToKeepInHand(3, new VisibleTurnState(state, victimState));
    List<Card> cardsToDiscard = new ArrayList<Card>();
    for (Card handCard : victimState.getHand()) {
      if (cardsToKeep.indexOf(handCard) < 0) {
        cardsToDiscard.add(handCard);
      }
    }
    for (Card card : cardsToDiscard) {
      state.getGameState().fireEvent(victimState.getPlayer().getName() + " discards " + card);
      victimState.discard(card);
    }
    state.fireEvent(new GameStateEvent(victimState.getPlayer(), GameStateEvent.Type.DISCARDED, victimState.getPlayer(), cardsToDiscard));
  }
}
