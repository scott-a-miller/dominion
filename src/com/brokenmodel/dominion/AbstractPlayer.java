/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.util.*;

/**
 * 
 */
public abstract class AbstractPlayer implements Player {
  protected String name;
  protected PlayerState myState;
  protected int playerId;
   
  public AbstractPlayer(int playerId, String name) {
    this.playerId = playerId;
    this.name = name;
  }
  
  public int getPlayerId() {
    return playerId;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public void setMyState(PlayerState state) {
    this.myState = state;
  }
  
  public PlayerState getMyState() {
    return myState;
  }

  public List<Card> chooseCardsToReplace(VisibleTurnState turnState) {
    if (myState.getHand().size() == 0) {
      return myState.getHand();
    }
    return chooseCardsToReplace(new ArrayList<Card>(myState.getHand()), turnState);
  }
  
  public abstract List<Card> chooseCardsToReplace(List<Card> available, VisibleTurnState turnState);
  
  public List<Card> chooseCardsToTrash(int max, VisibleTurnState turnState) {
    if (myState.getHand().size() == 0) {
      return myState.getHand();
    }
    if (myState.getHand().size() < max) {
      max = myState.getHand().size();
    }
    List<Card> trashedCards = chooseCardsToTrash(new ArrayList<Card>(myState.getHand()), max, turnState);
    while (trashedCards.size() > max) {
      trashedCards = chooseCardsToTrash(new ArrayList<Card>(myState.getHand()), max, turnState);
    }
    return trashedCards;
  }
  
  public abstract List<Card> chooseCardsToTrash(List<Card> available, int max, VisibleTurnState turnState);
  
  public List<Card> chooseCardsToKeepInHand(int max, VisibleTurnState turnState) {
    if (myState.getHand().size() == 0) {
      return myState.getHand();
    }
    if (myState.getHand().size() < max) {
      max = myState.getHand().size();
    }
    return chooseCardsToKeepInHand(new ArrayList<Card>(myState.getHand()), max, turnState);
  }
  
  public abstract List<Card> chooseCardsToKeepInHand(List<Card> available, int max, VisibleTurnState turnState);
  
  public TreasureCard chooseTreasureToUpgrade(VisibleTurnState turnState) {
    List<TreasureCard> available = new ArrayList<TreasureCard>();
    for (Card card : myState.getHand()) {
      if (TreasureCard.class.isInstance(card)) {
        available.add((TreasureCard)card);
      }
    }
    if (available.size() == 0) {
      return null;
    }    
    return chooseTreasureToUpgrade(available, turnState);
  }
  
  public abstract TreasureCard chooseTreasureToUpgrade(List<TreasureCard> available, VisibleTurnState turnState);
  
  public Card chooseFromHandToTopOfDeck(Class<? extends Card> type, VisibleTurnState turnState) {
    List<Card> available = new ArrayList<Card>();
    for (Card card : myState.getHand()) {
      if (type.isInstance(card)) {
        available.add(card);
      }
    }
    if (available.size() == 0) {
      return null;
    }
    return chooseFromHandToTopOfDeck(type, available, turnState);    
  }
  
  public abstract Card chooseFromHandToTopOfDeck(Class<? extends Card> type, List<Card> available, VisibleTurnState turnState);
  
  public ReactionCard chooseToRevealReaction(AttackCard attackCard, VisibleTurnState turnState) {
    List<ReactionCard> available = new ArrayList<ReactionCard>();
    for (Card card : myState.getHand()) {
      if (card instanceof ReactionCard) {
        available.add((ReactionCard)card);
      }
    }
    if (available.size() == 0) {
      return null;
    }
    return chooseToRevealReaction(available, attackCard, turnState);
  }
  
  public abstract ReactionCard chooseToRevealReaction(List<ReactionCard> available, AttackCard attackCard, VisibleTurnState turnState);
    
  public Card chooseCardToUpgrade(int upgradeValue, VisibleTurnState turnState) {
    if (myState.getHand().size() == 0) {
      return null;
    }
    return chooseCardToUpgrade(new ArrayList<Card>(myState.getHand()), upgradeValue, turnState);
  }
  
  public abstract Card chooseCardToUpgrade(List<Card> available, int upgradeValue, VisibleTurnState turnState);
  
  public ActionCard chooseActionToDoublePlay(VisibleTurnState turnState) {
    List<ActionCard> available = new ArrayList<ActionCard>();
    for (Card card : turnState.getHand()) {
      if (card instanceof ActionCard) {
        available.add((ActionCard)card);
      }
    }
    if (available.size() == 0) {
      return null;
    }
    return chooseActionToDoublePlay(available, turnState);
  }
  
  public abstract ActionCard chooseActionToDoublePlay(List<ActionCard> available, VisibleTurnState turnState);
  
  public ActionCard chooseActionToPlay(VisibleTurnState turnState) {
    List<ActionCard> available = new ArrayList<ActionCard>();
    for (Card card : turnState.getHand()) {
      if (card instanceof ActionCard) {
        available.add((ActionCard)card);
      }
    }
    if (available.size() == 0) {
      return null;
    }
    return chooseActionToPlay(available, turnState);
  }
  
  public abstract ActionCard chooseActionToPlay(List<ActionCard> available, VisibleTurnState turnState);
}
