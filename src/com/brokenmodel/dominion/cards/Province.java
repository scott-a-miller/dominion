/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Province extends AbstractCard implements VictoryCard {
  public Province(int cardId) {
    super(cardId, 8);
  }
  
  public int getPoints(PlayerState playerState) {
    return 6;
  }
}
