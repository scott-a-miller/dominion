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
public class Feast extends AbstractCard implements ActionCard {
  public Feast(int cardId) {
    super(cardId, 4);
  }
  
  public boolean trashAfterPlaying() {
    return true;
  }

  public void performAction(TurnState state) {
    List<Card> cards = state.getGameState().availableCards(5);
    Card card = state.getPlayerState().getPlayer().chooseCardToGain(cards, new VisibleTurnState(state, state.getPlayerState()));
    if (card != null) {
      state.getPlayerState().gainCard(card);
      state.fireEvent(new GameStateEvent(state.getPlayerState().getPlayer(), GameStateEvent.Type.GAINED, state.getPlayerState().getPlayer(), card));
    }
  }
}
