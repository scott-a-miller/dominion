/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Chancellor extends AbstractCard implements ActionCard {
  public Chancellor(int cardId) {
    super(cardId, 3);
  }
  
  public void performAction(TurnState state) {
    if (state.getPlayerState().getPlayer().chooseToShuffle(new VisibleTurnState(state, state.getPlayerState()))) {
      state.getPlayerState().shuffle();
      state.fireEvent(new GameStateEvent(state.getPlayerState().getPlayer(), GameStateEvent.Type.SHUFFLED, state.getPlayerState().getPlayer()));
    }
    state.addCostDiscount(2);
  }
}
