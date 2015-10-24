/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

/**
 * A player state that's only visible to everyone
 */
public class VisiblePlayerState {
  private PlayerState playerState;
  
  public VisiblePlayerState(PlayerState playerState) {
    this.playerState = playerState;
  }
  
  public int getPlayerId() {
    return playerState.getPlayer().getPlayerId();
  }
  
  public String getName() {
    return playerState.getPlayer().getName();
  }
  
  public int getTotalDeckSize() {
    return playerState.getTotalDeckSize();
  }
  
  public int getDeckSize() {
    return playerState.getDeck().size();
  }
  
  public int getHandSize() {
    return playerState.getHand().size();
  }
  
  public int getDiscardSize() {
    return playerState.getDiscard().size();
  }
  
  public int getScore() {
    return playerState.getScore();
  }
  
  public boolean isMyTurn() {
    return playerState.isMyTurn();
  }
  
  public Card getTopOfDiscard() {
    if (getDiscardSize() > 0) {
      return playerState.getDiscard().get(playerState.getDiscard().size()-1);
    }
    else {
      return null;
    }    
  }
}
