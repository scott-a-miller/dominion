/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Festival extends AbstractCard implements ActionCard {
  public Festival(int cardId) {
    super(cardId, 5);
  }
  
  public void performAction(TurnState state) {
    state.addActions(2);
    state.addBuy();
    state.addCostDiscount(2);
  }
}
