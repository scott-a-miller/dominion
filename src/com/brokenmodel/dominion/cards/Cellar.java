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
public class Cellar extends AbstractCard implements ActionCard {
  public Cellar(int cardId) {
    super(cardId, 2);
  }
  
  public void performAction(TurnState state) {
    List<Card> cards = state.getPlayerState().getPlayer().chooseCardsToReplace(new VisibleTurnState(state, state.getPlayerState()));
    for (Card card : cards) {
      state.getPlayerState().discard(card);
    }
    state.getPlayerState().draw(cards.size());
    state.addAction();
    state.fireEvent(new GameStateEvent(state.getPlayerState().getPlayer(), GameStateEvent.Type.REPLACED, state.getPlayerState().getPlayer(), cards));
  }
}
