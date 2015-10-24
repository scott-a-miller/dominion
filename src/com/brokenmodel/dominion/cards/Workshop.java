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
public class Workshop extends AbstractCard implements ActionCard {
  public Workshop(int cardId) {
    super(cardId, 3);
  }
  
  public void performAction(TurnState state) {
    List<Card> availableCards = state.getGameState().availableCards(4);
    Card card = state.getPlayerState().getPlayer().chooseCardToGain(availableCards, new VisibleTurnState(state, state.getPlayerState()));
    if (card != null) {
      state.getGameState().takeCard(card);
      state.fireEvent(new GameStateEvent(state.getPlayerState().getPlayer(), GameStateEvent.Type.GAINED, state.getPlayerState().getPlayer(), card));
      state.getPlayerState().discard(card);
    }
  }
}
