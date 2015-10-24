/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Smithy extends AbstractCard implements ActionCard {
  public Smithy(int cardId) {
    super(cardId, 4);
  }
  
  public void performAction(TurnState state) {
    state.getPlayerState().draw(3);
  }
}
