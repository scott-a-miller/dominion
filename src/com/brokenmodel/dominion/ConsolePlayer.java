/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.io.*;
import java.util.*;

/**
 * @author Scott
 *
 */
public class ConsolePlayer extends AbstractPlayer {
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  public ConsolePlayer(int playerId, String name) {
    super(playerId, name);
  }

  public void turnStarting(VisibleTurnState turnState) { }
  
  public ReactionCard chooseToRevealReaction(List<ReactionCard> available, AttackCard attackCard, VisibleTurnState turnState) {
    System.out.println("You are being attacked by a: " + attackCard);
    System.out.println("Choose a card to react with:");
    return selectCard(available, true, turnState);
  }

  public List<Card> chooseCardsToReplace(List<Card> available, VisibleTurnState turnState) {
    System.out.println("Choose card to replace from your hand:");
    return selectCards(available, available.size(), turnState);
  }

  public boolean chooseToShuffle(VisibleTurnState turnState) {
    System.out.println("Would you like to shuffle?");
    return selectBoolean(turnState);
  }

  public List<Card> chooseCardsToTrash(List<Card> available, int max, VisibleTurnState turnState) {
    System.out.println("Select cards to trash from your hand:");
    return selectCards(available, max, turnState);
  }

  public Card chooseCardToGain(List<Card> cards, VisibleTurnState turnState) {
    System.out.println("Select a card to gain:");
    return selectCard(cards, true, turnState);
  }

  public boolean chooseToKeepCardInHand(Card card, VisibleTurnState turnState) {
    System.out.println("Would you like to keep " + card + " in your hand?");
    return selectBoolean(turnState);
  }

  public List<Card> chooseCardsToKeepInHand(List<Card> available, int max, VisibleTurnState turnState) {
    System.out.println("Choose the cards to keep in your hand:");
    return selectCards(available, max, turnState);
  }

  public TreasureCard chooseTreasureToUpgrade(List<TreasureCard> available, VisibleTurnState turnState) {
    System.out.println("Choose a treasure card to upgrade:");
    return selectCard(available, true, turnState);
  }

  public boolean trashCardForDiscount(Card card, int discount, VisibleTurnState turnState) {
    System.out.println("Would you like to trash a " + card + " for a discount of " + discount + "?");
    return selectBoolean(turnState);
  }

  public Card chooseCardToUpgrade(List<Card> available, int upgradeValue, VisibleTurnState turnState) {
    System.out.println("Choose a card to upgrade:");
    return selectCard(available, true, turnState);
  }

  public boolean chooseDiscardOrPutBackOnTop(Card card, VisiblePlayerState playerState, VisibleTurnState turnState) {
    String name = playerState.getName();
    if (name.equals(getName())) {
      name = "yourself";
    }
    System.out.println("For " + name + ": would you like to put back " + card + " on top of their library?");
    return selectBoolean(turnState);
  }

  public TreasureCard chooseCardToTrashOrKeep(TreasureCard card1, TreasureCard card2, VisiblePlayerState victimState, VisibleTurnState turnState) {
    System.out.println("For " + victimState.getName() + ": which card would you like to trash or keep?");
    List<TreasureCard> cards = new ArrayList<TreasureCard>(2);
    cards.add(card1);
    cards.add(card2);
    return selectCard(cards, false, turnState);
  }

  public boolean chooseToKeep(TreasureCard card, VisibleTurnState turnState, VisiblePlayerState victimState) {
    if (victimState != null) {
      System.out.println("Would you like to keep: " + card + " from " + victimState.getName());
    }
    else {
      System.out.println("Would you like to keep: " + card);
    }
    return selectBoolean(turnState);
  }

  public ActionCard chooseActionToDoublePlay(List<ActionCard> available, VisibleTurnState turnState) {
    System.out.println("Which card would you like to double play:");
    return selectCard(available, true, turnState);
  }

  public ActionCard chooseActionToPlay(List<ActionCard> available, VisibleTurnState turnState) {
    System.out.println("Which card would you like to play:");
    return selectCard(available, true, turnState);
  }

  private boolean selectBoolean(VisibleTurnState state) {
    while (true) {
      try {
        System.out.println("y or n (h for hand):");
        String line = reader.readLine();
        if (line.length() > 0) {
          char c = line.charAt(0);
          c = Character.toLowerCase(c);
          if (c == 'y') {
            return true;
          }
          else if (c == 'n') {
            return false;
          }
          else if (c == 'h') {
            showHand(state);
          }
          else {
            System.out.println("Que?");
          }
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  private void showHand(VisibleTurnState state) {
    for (Card card : state.getHand()) {
      System.out.println(card);
    }
  }
  
  private <T extends Card> List<T> selectCards(List<T> available, int max, VisibleTurnState state) {
    List<T> stillAvailable = new ArrayList<T>(available);
    List<T> cards = new ArrayList<T>();
    while (stillAvailable.size() > 0 && cards.size() < max) {
      T card = selectCard(stillAvailable, true, state);
      if (card == null) {
        break;
      }
      else {
        stillAvailable.remove(card);
        cards.add(card);
      }
    }
    return cards;   
  }
  
  private <T extends Card> T selectCard(List<T> available, boolean noneOk, VisibleTurnState state) {
    int i = 1;
    System.out.println("  h. Show hand");
    if (noneOk) {
      System.out.println("  0. None");
    }
    for (Card card : available) {
      System.out.println("  " + i + ". " + card.toString());
      i++;
    }
    
    while (true) {
      try {
        String line = reader.readLine();
        if (line.length() > 0 && Character.toLowerCase(line.charAt(0)) == 'h') {
          showHand(state);
        }
        else {
          int number = Integer.parseInt(line);
          if (number == 0 && noneOk) {
            return null;
          }
          if (number < 1 || number > available.size()) {
            System.out.println("Select one of the numbers above.");
          }
          else {
            return available.get(number - 1);
          }
        }
      }
      catch (Exception e) {
        System.out.println("Que?");
      }
    }
  }
  
  public Card chooseFromHandToTopOfDeck(Class<? extends Card> type, List<Card> available, VisibleTurnState turnState) {
    System.out.println("Choose a card to put on top of your deck: ");
    return selectCard(available, false, turnState);
  }
}
