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
public class Thief extends AbstractAttackCard implements ActionCard {
  public Thief(int cardId) {
    super(cardId, 4);
  }
  
  public void performAction(TurnState state) { }

  public void attack(TurnState state, PlayerState victimState) {
    TreasureCard tCard1 = null;
    TreasureCard tCard2 = null;
    
    Card trashedCard = null;
    Card stolenCard = null;
    List<Card> discardedCards = new ArrayList<Card>(2);
    
    Card card1 = victimState.reveal();
    if (card1 != null) {
      if (!(card1 instanceof TreasureCard)) {
        victimState.discard(card1);
        discardedCards.add(card1);
        card1 = null;
      }
      else {
        tCard1 = (TreasureCard)card1;
      }
    }
    
    Card card2 = victimState.reveal();
    if (card2 != null) {
      if (!(card2 instanceof TreasureCard)) {
        victimState.discard(card2);
        discardedCards.add(card2);
        card2 = null;
      }
      else {
        tCard2 = (TreasureCard)card2;
      }
    }
    
    if (discardedCards.size() > 0) {
      state.fireEvent(new GameStateEvent(null, GameStateEvent.Type.DISCARDED, victimState.getPlayer(), discardedCards));
      discardedCards.clear();
    }
    
    TreasureCard trash = null;
    if (tCard1 == null) {
      if (tCard2 != null) {
        trash = tCard2;
      }
    }
    else {
      if (tCard2 == null) {
        trash = tCard1;
      }
      else {
        trash = state.getPlayerState().getPlayer().chooseCardToTrashOrKeep(tCard1, tCard2, 
            new VisiblePlayerState(victimState), new VisibleTurnState(state, state.getPlayerState()));
      }
    }
    
    if (trash != null) {
      if (state.getPlayerState().getPlayer().chooseToKeep(trash, new VisibleTurnState(state, state.getPlayerState()), new VisiblePlayerState(victimState))) {
        state.getPlayerState().discard(trash);
        stolenCard = trash;
      }
      else {
        trashedCard = trash;
      }
    }
    if (card1 != null && card1 != trash) {
      victimState.discard(card1);
      discardedCards.add(card1);
    }
    if (card2 != null && card2 != trash) {
      victimState.discard(card2);
      discardedCards.add(card2);
    }
    
    if (stolenCard != null) {
      state.fireEvent(new GameStateEvent(state.getPlayerState().getPlayer(), GameStateEvent.Type.STOLE, victimState.getPlayer(), stolenCard,
          state.getPlayerState().getPlayer().getName() + " stole " + stolenCard + " from " + victimState.getPlayer().getName()));
    }    
    if (trashedCard != null) {
      state.fireEvent(new GameStateEvent(state.getPlayerState().getPlayer(), GameStateEvent.Type.TRASHED, victimState.getPlayer(), trashedCard,
          state.getPlayerState().getPlayer().getName() + " trashed " + trashedCard + " from " + victimState.getPlayer().getName()));
    }
    if (discardedCards.size() > 0) {
      state.fireEvent(new GameStateEvent(state.getPlayerState().getPlayer(), GameStateEvent.Type.DISCARDED, victimState.getPlayer(), discardedCards));  
    }
  }
}
