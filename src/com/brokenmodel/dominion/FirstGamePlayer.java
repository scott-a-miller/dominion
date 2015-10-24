/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.util.*;

import com.brokenmodel.dominion.cards.*;

/**
 * @author Scott
 *
 */
public class FirstGamePlayer extends SimplePlayer {
  private List<Class<? extends Card>> playPriority = new ArrayList<Class<? extends Card>>();
  
  private boolean hasVillage;
  private boolean hasSmithy;
  private boolean hasMine;
  private boolean hasCellar;
  
  public FirstGamePlayer(String name) {
    super(name);    
    
    playPriority.add(Village.class);
    playPriority.add(Cellar.class);
    playPriority.add(Mine.class);
    playPriority.add(Smithy.class);
    
    gainPriority.remove(Silver.class);
    gainPriority.add(Duchy.class);
    gainPriority.add(Silver.class);
  }

  public ActionCard chooseActionToPlay(VisibleTurnState turnState) {
    for (Class<? extends Card> type : playPriority) {
      for (Card card : turnState.getHand()) {
        if (type.isInstance(card)) {
          return (ActionCard)card;
        }
      }
    }
    return super.chooseActionToPlay(turnState);
  }

  public Card chooseCardToGain(List<Card> cards, VisibleTurnState turnState) {
    if (!hasMine) {
      for (Card card : cards) {
        if (card instanceof Mine) {
          hasMine = true;
          return card;
        }
      }
    }
    if (!hasSmithy) {
      for (Card card : cards) {
        if (card instanceof Smithy) {
          hasSmithy = true;
          return card;
        }
      }
    }
    if (!hasVillage) {
      for (Card card : cards) {
        if (card instanceof Village) {
          hasVillage = true;
          return card;
        }
      }
    }
    if (!hasCellar) {
      for (Card card : cards) {
        if (card instanceof Cellar) {
          hasCellar = true;
          return card;
        }
      }
    }
    return super.chooseCardToGain(cards, turnState);
  }
  
  
}
