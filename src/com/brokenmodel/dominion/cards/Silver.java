/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Silver extends AbstractCard implements TreasureCard {
  public Silver(int cardId) {
    super(cardId, 3);
  }
  
  public int getValue() {
    return 2;
  }
}
