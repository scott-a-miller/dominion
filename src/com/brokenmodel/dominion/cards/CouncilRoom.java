/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class CouncilRoom extends AbstractCard implements ActionCard {
  public CouncilRoom(int cardId) {
    super(cardId, 5);
  }
  
  public void performAction(TurnState state) {
    PlayerState myState = state.getPlayerState();
    myState.draw(4);
    state.addBuy();
    for (PlayerState playerState : state.getGameState().getPlayerStates()) {
      if (playerState != myState) {
        playerState.draw();
      }
    }
  }
}
