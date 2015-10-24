/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Copper extends AbstractCard implements TreasureCard {
  public Copper(int cardId) {
    super(cardId, 0);
  }
  
  public int getValue() {
    return 1;
  }
}
