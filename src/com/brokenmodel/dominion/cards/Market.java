/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Market extends AbstractCard implements ActionCard {
  public Market(int cardId) {
    super(cardId, 5);
  }
  
  public void performAction(TurnState state) {
    state.getPlayerState().draw();
    state.addAction();
    state.addBuy();
    state.addCostDiscount(1);
  }
}
