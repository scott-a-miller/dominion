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
public class Remodel extends AbstractCard implements ActionCard {
  public Remodel(int cardId) {
    super(cardId, 4);
  }
  
  public void performAction(TurnState state) {
    int upgradeValue = 2;
    VisibleTurnState visibleState = new VisibleTurnState(state, state.getPlayerState());
    Card trash = state.getPlayerState().getPlayer().chooseCardToUpgrade(upgradeValue, visibleState);
    if (trash != null) {
      List<Card> cards = state.getGameState().availableCards(trash.getCost() + upgradeValue);
      Card card = state.getPlayerState().getPlayer().chooseCardToGain(cards, visibleState);
      if (card != null) {
        state.getPlayerState().trash(trash);
        state.getPlayerState().gainCard(card);
        List<Card> eventCards = new ArrayList<Card>(2);
        eventCards.add(trash);
        eventCards.add(card);
        state.fireEvent(new GameStateEvent(state.getPlayerState().getPlayer(), GameStateEvent.Type.UPGRADED, state.getPlayerState().getPlayer(), eventCards,
            state.getPlayerState().getPlayer().getName() + " upgraded " + trash + " for " + card));
      }      
    }
  }
}
