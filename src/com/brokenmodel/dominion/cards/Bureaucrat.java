/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Bureaucrat extends AbstractAttackCard implements ActionCard {
  public Bureaucrat(int cardId) {
    super(cardId, 4);
  }
  
  public void performAction(TurnState state) {
    if (state.getGameState().isAvailable(Silver.class)) {
      Card card = state.getGameState().takeTopCard(Silver.class);
      state.getPlayerState().addToTopOfDeck(card);
      state.fireEvent(new GameStateEvent(state.getPlayerState().getPlayer(), GameStateEvent.Type.ADDED_TO_TOP_OF_DECK, state.getPlayerState().getPlayer(), card));
    }
  }

  public void attack(TurnState state, PlayerState victimState) {
    Card card = victimState.getPlayer().chooseFromHandToTopOfDeck(VictoryCard.class, new VisibleTurnState(state, victimState));
    if (card != null) {
      victimState.addToTopOfDeck(card);
      state.fireEvent(new GameStateEvent(victimState.getPlayer(), GameStateEvent.Type.ADDED_TO_TOP_OF_DECK, victimState.getPlayer(), card));
    }
  }
}
