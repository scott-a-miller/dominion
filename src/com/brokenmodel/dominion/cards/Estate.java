/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.*;

/**
 * 
 */
public class Estate extends AbstractCard implements VictoryCard {
  public Estate(int cardId) {
    super(cardId, 2);
  }
  
  public int getPoints(PlayerState playerState) {
    return 1;
  }
}
