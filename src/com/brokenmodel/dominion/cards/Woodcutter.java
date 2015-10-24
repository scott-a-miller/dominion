/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Woodcutter extends AbstractCard implements ActionCard {
  public Woodcutter(int cardId) {
    super(cardId, 3);
  }
  
  public void performAction(TurnState state) {
    state.addBuy();
    state.addCostDiscount(2);
  }
}
