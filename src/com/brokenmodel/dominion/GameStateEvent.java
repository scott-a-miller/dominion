/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.io.*;
import java.util.*;

/**
 *
 */
public class GameStateEvent {
  public static enum Type {
    GAME_START,
    TURN_END,
    GAME_END,
    GAME_STOPPED,
    EXCEPTION,
    PLAYED,
    DOUBLE_PLAYED,
    BOUGHT,
    GAINED,
    ADDED_TO_HAND,
    ADDED_TO_TOP_OF_DECK,
    DISCARDED,
    REPLACED,
    SHUFFLED,
    TRASHED,
    SET_ASIDE,
    UPGRADED,
    STOLE,
    REACTED,
    MESSAGE,
  }
  
  private Type type;
  private String message;
  private Exception exception;
  private List<Card> cards;
  private Player sourcePlayer;
  private Player player;
  
  public GameStateEvent(Type type, String message, Exception e) {
    this.type = type;
    this.exception = e;
    this.message = message;
  }

  public GameStateEvent(Player sourcePlayer, Type type, Player player) {
    this.sourcePlayer = sourcePlayer;
    this.type = type;
    this.player = player;
    this.message = player.getName() + " " + type;
  }

  public GameStateEvent(Player sourcePlayer, Type type, Player player, Card card, String message) {
    this.sourcePlayer = sourcePlayer;
    this.type = type;
    this.cards = new ArrayList<Card>(1);
    if (card != null) {
      this.cards.add(card);
    }
    this.player = player;
    this.message = message;
  }

  public GameStateEvent(Player sourcePlayer, Type type, Player player, Card card) {
    this.sourcePlayer = sourcePlayer;
    this.type = type;
    this.cards = new ArrayList<Card>(1);
    if (card != null) {
      this.cards.add(card);
    }
    this.player = player;
    this.message = player.getName() + " " + type + " " + toString(cards);
  }

  public GameStateEvent(Player sourcePlayer, Type type, Player player, List<Card> cards, String message) {
    this.sourcePlayer = sourcePlayer;
    this.type = type;
    this.cards = new ArrayList<Card>(cards);
    this.player = player;
    this.message = message;
  }
  
  public GameStateEvent(Player sourcePlayer, Type type, Player player, List<Card> cards) {
    this.sourcePlayer = sourcePlayer;
    this.type = type;
    this.cards = new ArrayList<Card>(cards);
    this.player = player;
    this.message = player.getName() + " " + type + " " + toString(cards);
  }

  public GameStateEvent(Type type, String message) {
    this.type = type;
    this.message = message;
  }
  
  public GameStateEvent(String message) {
    this.message = message;
  }
  
  public Player getSourcePlayer() {
    return sourcePlayer;
  }
  
  public Type getType() {
    return type;
  }
  
  public String getMessage() {
    return message;
  }
  
  public List<Card> getCards() {
    return cards;
  }

  public Player getPlayer() {
    return player;
  }
  
  public Card getCard() {
    if (cards.size() > 0) {
      return cards.get(0);
    }
    else {
      return null;
    }
  }
  
  public Exception getException() {
    return exception;
  }
  
  public String toString() {
    if (exception != null) {
      StringWriter sw = new StringWriter();
      exception.printStackTrace(new PrintWriter(sw));
      return sw.toString();
    }
    return message;
  }
  
  public String prettyCardString() {
    if (cards == null && cards.size() == 0) {
      return "nothing";
    }
    StringBuilder sb = new StringBuilder();
    for (Card card : cards) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(card.toString());
    }
    return sb.toString();
  }
  
  private String toString(List<Card> cards) {
    if (cards.size() == 0) {
      return "nothing";
    }
    else if (cards.size() == 1) {
      return cards.get(0).toString() + "-" + cards.get(0).getCardId();
    }
    else {
      StringBuilder sb = new StringBuilder();
      sb.append(cards.get(0).toString());
      sb.append("-");
      sb.append(cards.get(0).getCardId());
      for (int i = 1; i < cards.size(); i++) {
        sb.append(",");
        sb.append(cards.get(i).toString());
        sb.append("-");
        sb.append(cards.get(i).getCardId());
      }
      return sb.toString();
    }
  }
}
