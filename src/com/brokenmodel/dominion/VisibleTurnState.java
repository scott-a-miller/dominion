/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The turn state as visible to a particular player
 */
public class VisibleTurnState {
  private TurnState turnState;
  private PlayerState myState;
  
  public VisibleTurnState(TurnState turnState, PlayerState myState) {
    this.turnState = turnState;
    this.myState = myState;
  }
  
  public VisiblePlayerState getCurrentPlayerState() {
    return new VisiblePlayerState(turnState.getPlayerState());
  }
  
  public List<VisiblePlayerState> getPlayerStates() { 
    List<PlayerState> playerStates = turnState.getGameState().getPlayerStates();
    List<VisiblePlayerState> states = new ArrayList<VisiblePlayerState>();
    for (PlayerState state : playerStates) {
      states.add(new VisiblePlayerState(state));
    }    
    return states;
  }
  
  /**
   * Players other than the one from this perspective in turn order
   */
  public List<VisiblePlayerState> getOtherPlayerStates() {
    List<VisiblePlayerState> states = new ArrayList<VisiblePlayerState>();
    for (PlayerState state : turnState.getGameState().getOtherPlayerStatesInTurnOrder(myState)) {
      states.add(new VisiblePlayerState(state));
    }
    return states;
  }
  
  public int getActions() { 
    return turnState.getActions();
  }
  
  public int getBuys() {
    return turnState.getBuys();
  }
  
  public List<Card> getHand() {
    return new ArrayList<Card>(myState.getHand());
  }
  
  public int getTotalDeckSize() {
    return myState.getTotalDeckSize();
  }
  
  public int getDeckSize() {
    return myState.getDeck().size();
  }
  
  public int getHandSize() {
    return myState.getHand().size();
  }
  
  public boolean isSinglePlayerGame() {
    return myState.isSinglePlayerGame();
  }

  public int getDiscardSize() {
    return myState.getDiscard().size();
  }
  
  public List<Card> getPlayedCards() {
    return turnState.getPlayedCards();
  }
  
  public boolean isStopping() {
    return turnState.isStopping();
  }
  
  public boolean isGameOver() {
    return turnState.isGameOver();
  }
  
  public int getMyPlayerId() {
    return myState.getPlayer().getPlayerId();    
  }
  
  public boolean isMyTurn() {
    return getMyPlayerId() == getCurrentPlayerState().getPlayerId();
  }
  
  public List<PlayerState> getWinners() {
    return turnState.getGameState().getWinners();
  }
  
  public int getScore() {
    return myState.getScore();
  }
  
  public List<Class<? extends Card>> getAllTypes() {
    return myState.getGameState().getAllTypes();
  }
  
  public List<Card> getAvailableKingdomCards() {
    return myState.getGameState().getAvailableKingdomCards();
  }
  
  public List<Card> getAvailableStandardCards() {
    return myState.getGameState().getAvailableStandardCards();
  }
  
  public Card getCard(int id) {
    return myState.getGameState().getCard(id);
  }
  
  public int numberAvailable(Card card) {
    return myState.getGameState().numberAvailable(card);
  }
  
  public int numberAvailable(Class<? extends Card> type) {
    return myState.getGameState().numberAvailable(type);
  }
  
  public Card getTopOfDiscard() {
    if (getDiscardSize() > 0) {
      return myState.getDiscard().get(myState.getDiscard().size()-1);
    }
    else {
      return null;
    }    
  }
  
  public void fireEvent(GameStateEvent event) {
    myState.getGameState().fireEvent(event);
  }
  
  public ReentrantLock getLock() {
    return turnState.getGameState().getLock();
  }
  
  public Condition getLockCondition() {
    return turnState.getGameState().getLockCondition();
  }
}
