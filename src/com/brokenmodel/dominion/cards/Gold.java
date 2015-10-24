/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Gold extends AbstractCard implements TreasureCard {
  public Gold(int cardId) {
    super(cardId, 6);
  }
  
  public int getValue() {
    return 3;
  }
}
