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
public class Adventurer extends AbstractCard implements ActionCard {
  public Adventurer(int cardId) {
    super(cardId, 6);
  }
    
  public void performAction(TurnState state) {
    PlayerState playerState = state.getPlayerState();
    int revealedTreasures = 0;
    
    List<Card> handCards = new ArrayList<Card>();
    List<Card> discardCards = new ArrayList<Card>();
    
    for (Card card = playerState.reveal(); revealedTreasures < 2 && card != null; card = playerState.reveal()) {
      if (card instanceof TreasureCard) {
        playerState.addToHand(card);
        handCards.add(card);
        revealedTreasures++;
      }
      else {
        discardCards.add(card);
      }
    }
    
    for (Card card : discardCards) {
      playerState.discard(card);
    }
    
    state.fireEvent(new GameStateEvent(playerState.getPlayer(), GameStateEvent.Type.ADDED_TO_HAND, playerState.getPlayer(), handCards));
    state.fireEvent(new GameStateEvent(playerState.getPlayer(), GameStateEvent.Type.DISCARDED, playerState.getPlayer(), discardCards));
  }
}
