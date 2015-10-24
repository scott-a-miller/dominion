/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 *
 */
public class Moat extends AbstractCard implements ReactionCard, ActionCard {
  public Moat(int cardId) {
    super(cardId, 2);
  }

  public boolean react(TurnState state, PlayerState victimState, AttackCard card) {
    state.fireEvent(new GameStateEvent(victimState.getPlayer(), GameStateEvent.Type.REACTED, victimState.getPlayer(), this));
    return true;
  }

  public void performAction(TurnState state) {
    state.getPlayerState().draw(2);
  }

}
