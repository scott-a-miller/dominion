/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Duchy extends AbstractCard implements VictoryCard {
  public Duchy(int cardId) {
    super(cardId, 5);
  }
  
  public int getPoints(PlayerState playerState) {
    return 3;
  }
}
