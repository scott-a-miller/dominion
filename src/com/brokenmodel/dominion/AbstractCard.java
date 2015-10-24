/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

/**
 * 
 */
public class AbstractCard implements Card {
  private int cardId;
  private int cost;
  
  public AbstractCard(int cardId, int cost) {
    this.cardId = cardId;
    this.cost = cost;
  }
  
  public int getCardId() {
    return cardId;
  }
  
  public int getCost() {
    return cost;
  }
  
  public String toString() {
    return getClass().getSimpleName();
  }
  
  public boolean trashAfterPlaying() {
    return false;
  }
}
