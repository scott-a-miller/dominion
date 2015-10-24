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
public class Chapel extends AbstractCard implements ActionCard {
  public Chapel(int cardId) {
    super(cardId, 2);
  }
  
  public void performAction(TurnState state) {
    List<Card> cards = state.getPlayerState().getPlayer().chooseCardsToTrash(4, new VisibleTurnState(state, state.getPlayerState()));
    for (Card card : cards) {
      state.getPlayerState().trash(card);
    }
    if (cards.size() > 0) {
      state.fireEvent(new GameStateEvent(state.getPlayerState().getPlayer(), GameStateEvent.Type.TRASHED, state.getPlayerState().getPlayer(), cards)); 
    }
  }
}
