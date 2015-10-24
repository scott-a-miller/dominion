/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Village extends AbstractCard implements ActionCard {
  public Village(int cardId) {
    super(cardId, 3);
  }
  
  public void performAction(TurnState state) {
    state.getPlayerState().draw();
    state.addActions(2);
  }
}
