/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * This implementation will need to change if other treasure cards are added.
 */
public class ThroneRoom extends AbstractCard implements ActionCard {
  public ThroneRoom(int cardId) {
    super(cardId, 4);
  }
  
  public void performAction(TurnState state) {
    ActionCard card = state.getPlayerState().getPlayer().chooseActionToDoublePlay(new VisibleTurnState(state, state.getPlayerState()));
    if (card != null) {
      state.getPlayerState().remove(card);
      state.playedCard(card);
      state.fireEvent(new GameStateEvent(state.getPlayerState().getPlayer(), GameStateEvent.Type.DOUBLE_PLAYED, state.getPlayerState().getPlayer(), card));
      for (int i = 0; i < 2; i++) {
        card.performAction(state);
        if (card instanceof AttackCard) {
          ((AttackCard)card).attack(state);
        }
      }      
    }
  }
}
