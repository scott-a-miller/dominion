/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.util.*;

/**
 * 
 */
public class PlayerState {
  private final GameState gameState;
  private Player player;
  
  private List<Card> deck = new ArrayList<Card>();
  private List<Card> discard = new ArrayList<Card>();
  private List<Card> hand = new ArrayList<Card>();
  private int turnsTaken = 0;
  private boolean myTurn;
  
  public PlayerState(GameState gameState, Player player, List<Card> initialDeck) {
    this.gameState = gameState;
    this.player = player;
    this.player.setMyState(this);
    
    deck = new ArrayList<Card>(initialDeck);

    shuffle();
  }
   
  public void turnStarting(TurnState turnState) {
    gameState.getLock().lock();
    try {
      player.turnStarting(new VisibleTurnState(turnState, this));
      myTurn = turnState.getPlayerState() == this;
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public GameState getGameState() {
    return gameState;
  }

  public Player getPlayer() {
    gameState.getLock().lock(); 
    try {
      return player;
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void setPlayer(Player player) {
    gameState.getLock().lock();  
    try {
      this.player = player;
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  /**
   * Pops the top card - removing it from the deck, but not adding it to the hand; this card
   * is in neverland - so be sure it's added back somewhere when done
   */
  public Card reveal() {
    gameState.getLock().lock();  
    try {
      if (deck.isEmpty()) {
        if (discard.isEmpty()) {
          return null;
        }
        else {
          shuffle();
          return reveal();
        }
      }
      else {
        return deck.remove(deck.size()-1);
      }
    }
    finally {
      gameState.getLock().unlock();
    }
  }

  public void draw(int count) {
    gameState.getLock().lock();  
    try {
      for (int i = 0; i < count; i++) {
        draw();
      }
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  /** adds to hand */
  public Card draw() {
    gameState.getLock().lock();  
    try {
      if (deck.isEmpty()) {
        if (!discard.isEmpty()) {
          shuffle();
          return draw();
        }
        return null;
      }
      else {
        Card card = deck.remove(deck.size()-1); 
        hand.add(card);
        return card;
      }
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void remove(Card card) {
    if (card == null) {
      throw new RuntimeException("Cannot remove null");
    }
    
    gameState.getLock().lock();  
    try {
      deck.remove(card);
      hand.remove(card);
      discard.remove(card);
    }
    finally {
      gameState.getLock().unlock();
    }
  }
    
  public void addToHand(Card card) {
    if (card == null) {
      throw new RuntimeException("Cannot add null to hand");
    }
    
    gameState.getLock().lock();  
    try { 
      deck.remove(card);
      discard.remove(card);
      hand.add(card);
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void addToTopOfDeck(Card card) {
    if (card == null) {
      throw new RuntimeException("Cannot add null to top of deck");
    }
    
    gameState.getLock().lock();  
    try {
      hand.remove(card);
      discard.remove(card);
      deck.add(card);
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void gainCard(Card card) {
    if (card == null) {
      throw new RuntimeException("Cannot gain null");
    }
    
    gameState.getLock().lock();  
    try {
      gameState.takeCard(card);
      discard.add(card);
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void discard(Card card) {
    if (card == null) {
      throw new RuntimeException("Cannot discard null");
    }
    
    gameState.getLock().lock();  
    try {
      deck.remove(card);
      hand.remove(card);
      discard.add(card);
    }
    finally {
      gameState.getLock().unlock();
    }
  }

  public void discard(List<Card> cards) {
    if (cards == null) {
      throw new RuntimeException("Cannot discard null");
    }
    
    gameState.getLock().lock();  
    try {
      for (Card card : cards) {
        discard(card);
      }
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void discardHand() {
    gameState.getLock().lock();  
    try {
      for (Card card : new ArrayList<Card>(hand)) {
        discard(card);
      }
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void trash(Card card) {
    if (card == null) {
      throw new RuntimeException("Cannot trash null");
    }
    gameState.getLock().lock();  
    try {
      discard.remove(card);
      deck.remove(card);
      hand.remove(card);
      gameState.addToTrash(card);
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public void shuffle() {
    gameState.getLock().lock();  
    try {
      gameState.fireEvent(player.getName() + " is shuffling " + discard.size() + " with " + deck.size());
      LinkedList<Card> cards = new LinkedList<Card>(deck);
      cards.addAll(discard);
      
      deck.clear();
      discard.clear();
      
      while (!cards.isEmpty()) {
        Card card = cards.remove(gameState.rand.nextInt(cards.size())); 
        deck.add(card);
      }
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public List<Card> getDeck() {
    gameState.getLock().lock();  
    try {
      return new ArrayList<Card>(deck);
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public List<Card> getDiscard() {
    gameState.getLock().lock();  
    try {
      return new ArrayList<Card>(discard);
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public List<Card> getHand() {
    gameState.getLock().lock();  
    try {
      return hand;
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public List<Card> getAllCards() {
    gameState.getLock().lock();  
    try {
      List<Card> cards = new ArrayList<Card>();
      cards.addAll(deck);
      cards.addAll(hand);
      cards.addAll(discard);
      return cards;
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public int getTotalDeckSize() {
    gameState.getLock().lock();  
    try {
      return deck.size() + discard.size() + hand.size();
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public int getScore() {
    gameState.getLock().lock();  
    try {
      int total = 0;
      for (Card card : getAllCards()) {
        if (card instanceof VictoryCard) {
          total += ((VictoryCard)card).getPoints(this);
        }
        else if (card instanceof CurseCard) {
          total += ((CurseCard)card).getPoints(this);
        }
      }
      return total;
    }
    finally {
      gameState.getLock().unlock();
    }
  }
  
  public int getCount(Class<? extends Card> type) {
    int count = 0;
    for (Card card : getAllCards()){ 
      if (type.isInstance(card)) {
        count++;
      }
    }
    return count;
  }
  
  public boolean isSinglePlayerGame() {
    return gameState.isSinglePlayer();
  }
  
  public boolean isMyTurn() {
    return myTurn;
  }
  
  public void addTurnTaken() {
    turnsTaken++;
  }
  
  public int getTurnsTaken() {
    return turnsTaken;
  }
}
