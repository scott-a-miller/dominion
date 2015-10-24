/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.util.*;

/**
 * 
 */
public interface Player {
  public int getPlayerId();
  public String getName();
  public void setMyState(PlayerState state);
  public PlayerState getMyState();

  public void turnStarting(VisibleTurnState startingState);
  public Card chooseFromHandToTopOfDeck(Class<? extends Card> type, VisibleTurnState turnState);
  public ReactionCard chooseToRevealReaction(AttackCard attackCard, VisibleTurnState turnState);
  public List<Card> chooseCardsToReplace(VisibleTurnState turnState);
  public boolean chooseToShuffle(VisibleTurnState turnState);
  public List<Card> chooseCardsToTrash(int max, VisibleTurnState turnState);
  public Card chooseCardToGain(List<Card> cards, VisibleTurnState turnState);
  public boolean chooseToKeepCardInHand(Card card, VisibleTurnState turnState);
  public List<Card> chooseCardsToKeepInHand(int max, VisibleTurnState turnState);
  public TreasureCard chooseTreasureToUpgrade(VisibleTurnState turnState);
  public boolean trashCardForDiscount(Card card, int discount, VisibleTurnState turnState);
  public Card chooseCardToUpgrade(int upgradeValue, VisibleTurnState turnState);
  public boolean chooseDiscardOrPutBackOnTop(Card card, VisiblePlayerState playerState, VisibleTurnState turnState);
  public TreasureCard chooseCardToTrashOrKeep(TreasureCard card1, TreasureCard card2, VisiblePlayerState victimState, VisibleTurnState turnState);
  public boolean chooseToKeep(TreasureCard card, VisibleTurnState turnState, VisiblePlayerState victimState);
  public ActionCard chooseActionToDoublePlay(VisibleTurnState turnState);
  public ActionCard chooseActionToPlay(VisibleTurnState turnState);
}
