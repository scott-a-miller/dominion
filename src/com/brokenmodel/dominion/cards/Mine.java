/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import java.util.*;

import com.brokenmodel.dominion.*;

/**
 * This implementation will need to change if other treasure cards are added.
 */
public class Mine extends AbstractCard implements ActionCard {
  public Mine(int cardId) {
    super(cardId, 5);
  }
  
  public void performAction(TurnState state) {
    Card card = state.getPlayerState().getPlayer().chooseTreasureToUpgrade(new VisibleTurnState(state, state.getPlayerState()));
    if (card != null) {
      Card replacement = null;
      if (card instanceof Copper) {
        if (state.getGameState().isAvailable(Silver.class)) {
          replacement = state.getGameState().takeTopCard(Silver.class);
        }
      }
      else if ((card instanceof Silver) || (card instanceof Gold)) {
        if (state.getGameState().isAvailable(Gold.class)) {
          replacement = state.getGameState().takeTopCard(Gold.class);
        }
      }
      else {
        throw new RuntimeException("Not one of the three known treasure cards");
      }
      if (replacement != null) {
        state.getPlayerState().trash(card);
        state.getPlayerState().addToHand(replacement);
        
        List<Card> eventCards = new ArrayList<Card>(2);
        eventCards.add(card);
        eventCards.add(replacement);
        state.fireEvent(new GameStateEvent(state.getPlayerState().getPlayer(), GameStateEvent.Type.UPGRADED, state.getPlayerState().getPlayer(), eventCards,
            state.getPlayerState().getPlayer().getName() + " upgraded " + card + " for " + replacement));
      }
    }
  }
}
