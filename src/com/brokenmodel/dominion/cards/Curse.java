/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Curse extends AbstractCard implements CurseCard {
  public Curse(int cardId) {
    super(cardId, 0);
  }
  
  public int getPoints(PlayerState state) {
    return -1;
  }
}
