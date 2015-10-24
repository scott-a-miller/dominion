/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import com.brokenmodel.dominion.cards.*;

import java.util.*;

/**
 * 
 */
public class SimplePlayer implements Player {
  private int playerId = AiPlayerIds.nextId();
  private String name;
  private PlayerState myState;
  protected List<Class<? extends Card>> gainPriority = new ArrayList<Class<? extends Card>>();
  protected List<Class<? extends Card>> discardPriority = new ArrayList<Class<? extends Card>>();
  
  public SimplePlayer(String name) {
    this.name = name;
    
    gainPriority.add(Province.class);
    gainPriority.add(Gold.class);
    gainPriority.add(Silver.class);
    
    discardPriority.add(CurseCard.class);
    discardPriority.add(VictoryCard.class);
    discardPriority.add(Copper.class);
    discardPriority.add(Silver.class);
    discardPriority.add(ActionCard.class);
    discardPriority.add(Gold.class);
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

  public void turnStarting(VisibleTurnState turnState) { }
  
  public Card chooseFromHandToTopOfDeck(Class<? extends Card> type, VisibleTurnState turnState) {
    for (Card card : myState.getHand()) {
      if (type.isAssignableFrom(card.getClass())) {
        return card;
      }
    }
    return null;
  }
  
  public ReactionCard chooseToRevealReaction(AttackCard attackCard, VisibleTurnState turnState) {
    for (Card card : myState.getHand()) {
      if (ReactionCard.class.isAssignableFrom(card.getClass())) {
        return (ReactionCard)card;
      }
    }
    return null;
  }
  
  public List<Card> chooseCardsToReplace(VisibleTurnState turnState) {
    return new ArrayList<Card>(0);
  }
  
  public boolean chooseToShuffle(VisibleTurnState turnState) {
    return true;
  }
  
  public List<Card> chooseCardsToTrash(int max, VisibleTurnState turnState) {
    List<Card> trashed = new ArrayList<Card>();
    for (Card card : myState.getHand()) {
      if ((card instanceof Estate) || (card instanceof Copper)) {
        trashed.add(card);
        if (trashed.size() == max) {
          break;
        }
      }
    }
    return trashed;
  }
  
  public Card chooseCardToGain(List<Card> cards, VisibleTurnState turnState) {
    return chooseFromPriority(cards, gainPriority);
  }
  
  public boolean chooseToKeepCardInHand(Card card, VisibleTurnState turnState) {
    return false;
  }
  
  public List<Card> chooseCardsToKeepInHand(int max, VisibleTurnState turnState) {
    int neededToDiscard = myState.getHand().size() - max;   
    List<Card> cardsToDiscard = new ArrayList<Card>();
    List<Card> cardsToKeep = new ArrayList<Card>(myState.getHand());
    if (neededToDiscard <= 0) {
      return cardsToDiscard;
    }
    
    for (Class<? extends Card> type : discardPriority) {
      for (Card card : myState.getHand()) {
        if (type.isInstance(card)) {
          cardsToDiscard.add(card);
          cardsToKeep.remove(card);
          if (neededToDiscard <= cardsToDiscard.size()) {
            break;
          }
        }
      }
      if (neededToDiscard <= cardsToDiscard.size()) {
        break;
      }
    }

    for (int i = cardsToKeep.size()-1; i >= max; i--) {
      cardsToKeep.remove(i);
    }
    
    return cardsToKeep;
  }

  public TreasureCard chooseTreasureToUpgrade(VisibleTurnState state) {    
    for (Card card : myState.getHand()) {
      if (card instanceof Copper) {
        return (Copper)card;
      }
    }
    
    for (Card card : myState.getHand()) {
      if (card instanceof Silver) {
        return (Silver)card;
      }
    }
    
    for (Card card : myState.getHand()) {
      if (card instanceof TreasureCard) {
        return (TreasureCard)card;
      }
    }
    
    return null;
  }

  public boolean trashCardForDiscount(Card card, int discount, VisibleTurnState turnState) {
    return true;
  }
  
  public Card chooseCardToUpgrade(int upgradeValue, VisibleTurnState turnState) {
    return null;
  }
  
  public boolean chooseDiscardOrPutBackOnTop(Card card, VisiblePlayerState playerState, VisibleTurnState turnState) {
    boolean good = (card instanceof Gold) || (card instanceof Silver) || (card instanceof ActionCard); 
    if (playerState.getName().equals(myState.getPlayer().getName())) {
      return good;
    }
    else {
      return !good;
    }
  }
  
  public TreasureCard chooseCardToTrashOrKeep(TreasureCard card1, TreasureCard card2, VisiblePlayerState victimState, VisibleTurnState turnState) {
    TreasureCard bestCard = card2;
    if (card1.getValue() >= card2.getValue()) {
      bestCard = card1;
    }
    if (bestCard.getValue() > 1) {
      return bestCard;
    }
    else {
      return null;
    }
  }
  
  public boolean chooseToKeep(TreasureCard card, VisibleTurnState turnState, VisiblePlayerState victimState) {
    return card.getValue() > 1;
  }
  
  public ActionCard chooseActionToDoublePlay(VisibleTurnState turnState) {
    for (Card card : myState.getHand()) {
      if (card instanceof ActionCard) {
        return (ActionCard)card;
      }
    }
    return null;
  }

  public ActionCard chooseActionToPlay(VisibleTurnState turnState) {
    for (Card card : myState.getHand()) {
      if (card instanceof ActionCard) {
        return (ActionCard)card;
      }
    }
    return null;
  }

  protected Card chooseFromPriority(List<Card> available, List<Class<? extends Card>> types) {
    for (Class<? extends Card> type : types) {
      for (Card card : available) {
        if (type.isInstance(card)) {
          return card;
        }
      }
    }
    return null;
  }
}
