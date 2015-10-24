/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.util.*;

/**
 * 
 */
public class TurnState {
  private final GameState gameState;
  private final PlayerState playerState;
  
  private int actions = 1;
  private int buys = 1;
  private int costDiscount = 0;
  private List<Card> playedCards = new ArrayList<Card>();
  
  public TurnState(PlayerState playerState) {
    this.playerState = playerState;
    this.gameState = playerState.getGameState();
  }
  
  public PlayerState getPlayerState() {
    return playerState;
  }
  
  public GameState getGameState() { 
    return gameState;
  }
  
  public boolean hasAction() {
    gameState.getLock().lock(); 
    try {
      return actions > 0;
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void addAction() {
    gameState.getLock().lock();  
    try {
      setActions(actions + 1);
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void addActions(int count) {
    gameState.getLock().lock();  
    try {
      setActions(actions + count);
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void useAction() {
    gameState.getLock().lock();  
    try {
      if (hasAction()) {
        setActions(actions - 1);
      }
      else {
        throw new RuntimeException("Tried to use action when none available");
      }
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public int getActions() {
    gameState.getLock().lock();  
    try {
      return actions;
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public boolean isSinglePlayer() {
    return gameState.isSinglePlayer();
  }
  
  public void setActions(int actions) {
    gameState.getLock().lock();  
    try {
      this.actions = actions;
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public boolean hasBuy() {
    gameState.getLock().lock();   
    try {
      return buys > 0;
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void addBuy() {
    gameState.getLock().lock();   
    try {
      setBuys(buys + 1);
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void addBuys(int count) {
    gameState.getLock().lock();   
    try {
      setBuys(buys + count);
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void useBuy() {
    gameState.getLock().lock();   
    try {
      if (hasBuy()) {
        setBuys(buys - 1);
      }
      else {
        throw new RuntimeException("Tried to use buy when none available");
      }
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public int getBuys(){
    gameState.getLock().lock();   
    try {
      return buys;
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void setBuys(int buys) {
    gameState.getLock().lock();   
    try {
      this.buys = buys;
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public int getCostDiscount() {
    gameState.getLock().lock();   
    try { 
      return costDiscount;
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void setCostDiscount(int discount) {
    gameState.getLock().lock();   
    try { 
      this.costDiscount = discount;
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void addCostDiscount(int count) {
    gameState.getLock().lock();   
    try {
      setCostDiscount(costDiscount + count);
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void removeCostDiscount(int count) {
    gameState.getLock().lock();   
    try {
      if (count > costDiscount) {
        throw new RuntimeException("Tried to reduce discount below available amount");
      }
      setCostDiscount(costDiscount - count);
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public List<Card> getPlayedCards() {
    gameState.getLock().lock();   
    try {
      return new ArrayList<Card>(playedCards);
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void playedCard(Card card) {
    gameState.getLock().lock();   
    try {
      playedCards.add(card);
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void playedCards(List<Card> cards) {
    gameState.getLock().lock();   
    try {
      for (Card card : cards) {
        playedCard(card);
      }
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void fireEvent(GameStateEvent event) {
    gameState.fireEvent(event);
  }
  
  public boolean isGameOver() {
    return gameState.isGameOver();
  }
  
  public boolean isStopping() {
    return gameState.isStopping();
  }
}
