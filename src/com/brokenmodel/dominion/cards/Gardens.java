/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion.cards;

import com.brokenmodel.dominion.AbstractCard;
import com.brokenmodel.dominion.PlayerState;
import com.brokenmodel.dominion.VictoryCard;

/**
 * 
 */
public class Gardens extends AbstractCard implements VictoryCard {
  public Gardens(int cardId) {
    super(cardId, 4);
  }
  
  public int getPoints(PlayerState playerState) {
    return playerState.getTotalDeckSize() / 10;
  }
}
