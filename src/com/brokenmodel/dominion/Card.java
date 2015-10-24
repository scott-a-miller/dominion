/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

/**
 * 
 */
public interface Card {
  public abstract int getCardId();
  public abstract int getCost();
  public boolean trashAfterPlaying();
}
