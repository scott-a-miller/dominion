/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Spy extends AbstractAttackCard implements ActionCard {
  public Spy(int cardId) {
    super(cardId, 4);
  }
  
  public void performAction(TurnState state) {
    state.getPlayerState().draw();
    state.addAction();
    spy(state, state.getPlayerState());
  }

  public void attack(TurnState state, PlayerState victimState) {
    spy(state, victimState);
  }
  
  private void spy(TurnState state, PlayerState playerState) {
    Card card = playerState.reveal();
    if (card != null) {
      if (state.getPlayerState().getPlayer().chooseDiscardOrPutBackOnTop(card, new VisiblePlayerState(playerState), 
          new VisibleTurnState(state, state.getPlayerState()))) {
        playerState.discard(card);
        state.fireEvent(new GameStateEvent(state.getPlayerState().getPlayer(), GameStateEvent.Type.DISCARDED, playerState.getPlayer(), card));
      }
      else {
        playerState.addToTopOfDeck(card);
      }
    }
  }
}
