/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Laboratory extends AbstractCard implements ActionCard {
  public Laboratory(int cardId) {
    super(cardId, 5);
  }
  
  public void performAction(TurnState state) {
    state.addAction();
    state.getPlayerState().draw(2);
  }
}
