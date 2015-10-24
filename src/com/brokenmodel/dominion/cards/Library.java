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
public class Library extends AbstractCard implements ActionCard {
  public Library(int cardId) {
    super(cardId, 5);
  }
  
  public void performAction(TurnState state) {
    List<Card> asideCards = new ArrayList<Card>();
    while (state.getPlayerState().getHand().size() < 7) {
      Card card = state.getPlayerState().draw();
      if (card == null) {
        break;
      }
      if (card instanceof ActionCard) {
        if (!state.getPlayerState().getPlayer().chooseToKeepCardInHand(card, new VisibleTurnState(state, state.getPlayerState()))) {
          state.getPlayerState().remove(card);
          asideCards.add(card);
        }
      }
    }
    for (Card card : asideCards) {
      state.getPlayerState().discard(card);
    }
    state.fireEvent(new GameStateEvent(state.getPlayerState().getPlayer(), GameStateEvent.Type.SET_ASIDE, state.getPlayerState().getPlayer(), asideCards));
  }
}
