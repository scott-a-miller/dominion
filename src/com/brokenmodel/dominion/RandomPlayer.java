/**
 * 
 */
package com.brokenmodel.dominion;

import java.util.*;

/**
 * @author Scott
 *
 */
public class RandomPlayer extends AbstractPlayer {
  private Random rand = new Random();
  
  public RandomPlayer() { 
    super(AiPlayerIds.nextId(), "Random Player");
    setName("Random Player (" + (getPlayerId()*-1) + ")");
  }

  private boolean randomChoice() {
    return rand.nextInt(2) == 1;
  }
  
  private Card randomCard(List<? extends Card> available, boolean nullOk) {    
    if (available.size() == 0) {
      if (nullOk) {
        return null;
      }
      else {
        throw new RuntimeException("Forced to choose non-null from nothing");
      }
    }

    if (!nullOk) {
      return available.get(rand.nextInt(available.size()));
    }
    else {
      int idx = rand.nextInt(available.size()+1);
      if (idx == available.size()) {
        return null;
      }
      else {
        return available.get(idx);
      }
    }
  }
  
  private List<Card> randomCards(List<Card> available) {
    return randomCards(available, -1);
  }
  
  private List<Card> randomCards(List<Card> available, int exactCount) {
    if (exactCount < 0) {
      exactCount = rand.nextInt(available.size()); 
    }
    List<Card> remaining = new ArrayList<Card>(available);
    List<Card> cards = new ArrayList<Card>();
    for (int i = 0; i < exactCount; i++) {
      cards.add(remaining.remove(rand.nextInt(remaining.size())));
    }
    return cards;
  }
  
  public List<Card> chooseCardsToReplace(List<Card> available, VisibleTurnState turnState) {
    return randomCards(available);
  }

  public List<Card> chooseCardsToTrash(List<Card> available, int max, VisibleTurnState turnState) {
    return randomCards(available);
  }

  public List<Card> chooseCardsToKeepInHand(List<Card> available, int max, VisibleTurnState turnState) {
    return randomCards(available, max);
  }

  public TreasureCard chooseTreasureToUpgrade(List<TreasureCard> available, VisibleTurnState turnState) {
    return (TreasureCard)randomCard(available, true);
  }

  public Card chooseFromHandToTopOfDeck(Class<? extends Card> type, List<Card> available, VisibleTurnState turnState) {
    return randomCard(available, false);
  }

  public ReactionCard chooseToRevealReaction(List<ReactionCard> available, AttackCard attackCard, VisibleTurnState turnState) {
    return (ReactionCard)randomCard(available, true);
  }

  public Card chooseCardToUpgrade(List<Card> available, int upgradeValue, VisibleTurnState turnState) {
    return randomCard(available, true);
  }

  public ActionCard chooseActionToDoublePlay(List<ActionCard> available, VisibleTurnState turnState) {
    return (ActionCard)randomCard(available, true);
  }

  public ActionCard chooseActionToPlay(List<ActionCard> available, VisibleTurnState turnState) {
    return (ActionCard)randomCard(available, true);
  }

  public void turnStarting(VisibleTurnState startingState) { }

  public boolean chooseToShuffle(VisibleTurnState turnState) {
    return randomChoice();
  }

  public Card chooseCardToGain(List<Card> cards, VisibleTurnState turnState) {
    List<Card> cardsWithCost = new ArrayList<Card>();
    for (Card card : cards) {
      if (card.getCost() > 0) {
        cardsWithCost.add(card);
      }
    }
    return randomCard(cardsWithCost, true);
  }

  public boolean chooseToKeepCardInHand(Card card, VisibleTurnState turnState) {
    return randomChoice();
  }

  public boolean trashCardForDiscount(Card card, int discount, VisibleTurnState turnState) {
    return randomChoice();
  }

  public boolean chooseDiscardOrPutBackOnTop(Card card, VisiblePlayerState playerState, VisibleTurnState turnState) {
    return randomChoice();
  }

  public TreasureCard chooseCardToTrashOrKeep(TreasureCard card1, TreasureCard card2, VisiblePlayerState victimState, VisibleTurnState turnState) {
    if (randomChoice()) {
      return card1;
    }
    else {
      return card2;
    }
  }

  public boolean chooseToKeep(TreasureCard card, VisibleTurnState turnState, VisiblePlayerState victimState) {
    return randomChoice();
  }
}
