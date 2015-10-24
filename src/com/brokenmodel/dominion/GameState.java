/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.brokenmodel.dominion.cards.*;

/**
 * 
 */
public class GameState {
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition lockCondition = lock.newCondition();
    
  public final Random rand = new SecureRandom();
  
  private final int gameId;
  private final String configName;
  private final List<PlayerState> playerStates = new ArrayList<PlayerState>();
  private final List<Class<? extends Card>> kingdomTypes = new ArrayList<Class<? extends Card>>();
  private final List<Class<? extends Card>> allTypes = new ArrayList<Class<? extends Card>>();
  private final List<Class<? extends Card>> standardTypes = new ArrayList<Class<? extends Card>>();
  private final HashMap<Integer, Card> cardMap = new HashMap<Integer, Card>();

  private List<Card> trash = new ArrayList<Card>();
  
  private HashMap<Class<? extends Card>, List<Card>> availableCards = new HashMap<Class<? extends Card>, List<Card>>();
  
  private Exception exception;
  
  private boolean started = false;
  private boolean stopping = false;
  private boolean stopped = false;
  private boolean gameOver = false;
  
  private int nextCardId = 1;
  private int turn;
  
  private Class[] cardConstructor = new Class[]{Integer.TYPE};
  
  private List<PlayerState> winners;
  
  private GameThread gameThread;
  private List<GameStateListener> listeners = new ArrayList<GameStateListener>();
  
  public GameState(int gameId, List<Player> players, GameConfiguration config) {
    this.gameId = gameId;

    List<PlayerState> initialStates = new ArrayList<PlayerState>();
    for (Player player : players) {      
      List<Card> initialDeck = new ArrayList<Card>(7);
      for (int i = 0; i < 3; i++) {
        initialDeck.add(createCard(Estate.class));
      }
      for (int i = 0; i < 7; i++) {
        initialDeck.add(createCard(Copper.class));
      }
      initialStates.add(new PlayerState(this, player, initialDeck));
    }

    List<PlayerState> remainingStates = new ArrayList<PlayerState>(initialStates);
    while (remainingStates.size() > 0) {
      PlayerState chosenState = remainingStates.remove(rand.nextInt(remainingStates.size()));
      playerStates.add(chosenState);
    }
    
    this.kingdomTypes.addAll(config.getKingdomTypes());
    this.configName = config.getName();    
    
    standardTypes.add(Copper.class);
    standardTypes.add(Silver.class);
    standardTypes.add(Gold.class);
    
    standardTypes.add(Estate.class);
    standardTypes.add(Duchy.class);
    standardTypes.add(Province.class);
    
    standardTypes.add(Curse.class);
    
    allTypes.addAll(standardTypes);
    allTypes.addAll(kingdomTypes);
    
    int playerCount = playerStates.size();
    
    int victoryCardCount = playerCount < 3 ? 8 : 12;
    makeAvailable(Duchy.class, victoryCardCount);
    makeAvailable(Province.class, victoryCardCount);
    makeAvailable(Estate.class, victoryCardCount);
    
    makeAvailable(Copper.class, 60 - (7 * playerCount));
    makeAvailable(Silver.class, 40);
    makeAvailable(Gold.class, 30);
    
    int curseCount = 10;
    switch (playerCount) {
    case 2: curseCount = 10; break;
    case 3: curseCount = 20; break;
    case 4: curseCount = 30; break;
    }
    makeAvailable(Curse.class, curseCount);
    
    for (Class<? extends Card> type : kingdomTypes) {
      makeAvailable(type, type == Gardens.class ? victoryCardCount : 10);
    }
    
    for (PlayerState playerState : playerStates) {
      playerState.draw(5);
    }
  }
  
  private Card createCard(Class<? extends Card> type) {
    try {
      Card card = type.getConstructor(cardConstructor).newInstance(new Object[]{new Integer(nextCardId++)});
      cardMap.put(card.getCardId(), card);
      return card;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }    
  }
  
  private void makeAvailable(Class<? extends Card> type, int count) {
    List<Card> available = new ArrayList<Card>(count);
    for (int i = 0; i < count; i++) {
      available.add(createCard(type));
    }
    availableCards.put(type, available);
  }
  
  public int getGameId() {
    return gameId;
  }
  
  public Card getCard(int cardId) {    
    return cardMap.get(cardId);
  }
    
  public boolean isAvailable(Class<? extends Card> type) {
    lock.lock();
    try {
      return availableCards.get(type).size() > 0;
    }
    finally {
      lock.unlock();
    }
  }
  
  /** does not remove the card */
  private Card getTopCard(Class<? extends Card> type) {
    lock.lock();
    try {
      List<Card> cards = availableCards.get(type); 
      if (cards.size() > 0) {
        return availableCards.get(type).get(cards.size()-1);
      }
      else {
        return null;
      }
    }
    finally {
      lock.unlock();
    }
  }
  
  /** does remove the card **/
  public Card takeTopCard(Class<? extends Card> type) {
    lock.lock();
    try {
      Card card = getTopCard(type);
      if (card == null) {
        throw new RuntimeException("No top card to take for: " + type);
      }
      else {
        takeCard(card);
        return card;
      }
    }
    finally {
      lock.unlock();
    }
  }
  
  public List<Class<? extends Card>> getAllTypes() {
    return new ArrayList<Class<? extends Card>>(allTypes);
  }
  
  public List<Class<? extends Card>> getStandardTypes() {
    return new ArrayList<Class<? extends Card>>(standardTypes);
  }

  public int numberAvailable(Card card) {
    lock.lock(); 
    try {
      return availableCards.get(card.getClass()).size();
    }
    finally {
      lock.unlock();
    }
  }
  
  public int numberAvailable(Class<? extends Card> type) {
    lock.lock(); 
    try {
      return availableCards.get(type).size();
    }
    finally {
      lock.unlock();
    }
  }
  
  public List<Card> getAvailableCards(List<Class<? extends Card>> types) {
    lock.lock(); 
    try {
      List<Card> topCards = new ArrayList<Card>();
      for (Class<? extends Card> type : types) {
        Card topCard = getTopCard(type);
        if (topCard != null) {
          topCards.add(topCard);
        }
      }
      return topCards;
    }
    finally {
      lock.unlock();
    }
  }
  
  public List<Card> getAvailableCards() {
    return getAvailableCards(allTypes);
  }
  
  public List<Card> getAvailableStandardCards() {
    return getAvailableCards(standardTypes);
  }

  public List<Card> getAvailableKingdomCards() {
    return getAvailableCards(kingdomTypes);
  }

  public List<Card> availableCards(int maximumCost) {
    lock.lock(); 
    try { 
      List<Card> allCards = getAvailableCards();
      List<Card> cards = new ArrayList<Card>();
      for (Card card : allCards) {
        if (card.getCost() <= maximumCost) {
          cards.add(card);
        }
      }
      return cards;
    }
    finally {
      lock.unlock();
    }
  }
  
  public void takeCard(Card card) {
    lock.lock(); 
    try {
      if (card != getTopCard(card.getClass())) {
        throw new RuntimeException("Tried to take non-top card");
      }
      else {
        List<Card> cards = availableCards.get(card.getClass()); 
        cards.remove(cards.size()-1);
      }
    }
    finally {
      lock.unlock();
    }
  }
    
  public void addToTrash(Card card) {
    lock.lock(); 
    try {
      trash.add(card);
    }
    finally {
      lock.unlock();
    }
  }
  
  public List<Class<? extends Card>> getKingdomTypes() {
    lock.lock(); 
    try {
      return new ArrayList<Class<? extends Card>>(kingdomTypes);
    }
    finally {
      lock.unlock();
    }
  }
  
  public List<PlayerState> getPlayerStates() {
    lock.lock(); 
    try {
      return new ArrayList<PlayerState>(playerStates);
    }
    finally {
      lock.unlock();
    }
  }
  
  public List<PlayerState> getAllPlayerStatesInTurnOrder(TurnState turnState) {
    lock.lock(); 
    try {
      List<PlayerState> orderedStates = getOtherPlayerStatesInTurnOrder(turnState);
      orderedStates.add(turnState.getPlayerState());
      return orderedStates;
    }
    finally {
      lock.unlock();
    }
  }

  public List<PlayerState> getOtherPlayerStatesInTurnOrder(PlayerState playerState) {
    lock.lock(); 
    try {
      List<PlayerState> orderedStates = new ArrayList<PlayerState>();
      int i = 0;
      while (playerStates.get(i) != playerState) {
        i++;
      }
      i++;
      while (playerStates.get(i % playerStates.size()) != playerState) {
        orderedStates.add(playerStates.get(i % playerStates.size()));
        i++;
      }
      return orderedStates;
    }
    finally {
      lock.unlock();
    }
  }
  
  public List<PlayerState> getOtherPlayerStatesInTurnOrder(TurnState turnState) {
    lock.lock(); 
    try {
      return getOtherPlayerStatesInTurnOrder(turnState.getPlayerState());
    }
    finally {
      lock.unlock();
    }
  }

  public List<PlayerState> getWinners() {
    lock.lock(); 
    try {
      return new ArrayList<PlayerState>(winners);
    }
    finally {
      lock.unlock();
    }
  }
 
  public boolean isGameOver() {
    lock.lock();
    try {
      return gameOver;
    }
    finally {
      lock.unlock();
    }
  }
  
  
  private boolean checkGameOver() {
    lock.lock(); 
    try {
      if (!isAvailable(Province.class)) {
        return true;
      }
      int emptyPiles = 0;
      for (int i = 0; i < allTypes.size(); i++) {
        if (!isAvailable(allTypes.get(i))) {
          emptyPiles++;
        }
      }
      
      return emptyPiles >= 3;
    }
    finally {
      lock.unlock();
    }
  }
  
  public String getState() {
    lock.lock(); 
    try {
      if (isStopped()) {
        return "Stopped";
      }
      else if (isGameOver()) {
        return "Game Over";
      }
      else if (isStarted()){ 
        return "Playing";
      }
      else {
        return "Starting";
      }
    }
    finally {
      lock.unlock();
    }
  }
  
  public boolean isStarted() {
    lock.lock();
    try {
      return started;
    }
    finally {
      lock.unlock();
    }
  }
  
  public boolean isStopping() {
    lock.lock();
    try {
      return stopping;
    }
    finally {
      lock.unlock();
    }
  }
  
  public boolean isStopped() {
    lock.lock();
    try {
      return stopped;
    }
    finally {
      lock.unlock();
    }
  }
  
  public void stopGame() {
    lock.lock(); 
    try {
      stopping = true;
      lockCondition.signalAll();
    }
    finally {
      lock.unlock();
    }
  }
  
  public boolean isSinglePlayer() {
    lock.lock(); 
    try {
      int count = 0;
      for (PlayerState playerState : playerStates) {
        if (playerState.getPlayer() instanceof StatefulInterfacePlayer) {
          count++;
        }
      }
      return count == 1;
    }
    finally {
      lock.unlock();
    }
  }
  
  public void startGame() {
    startGame(false);
  }
  
  public void startGame(boolean useCurrentThread) {
    lock.lock();
    try {
      if (!started) {
        gameThread = new GameThread();
        if (useCurrentThread) {
          gameThread.run();
        }
        else {
          gameThread.start();
        }
      }
    }
    finally {
      lock.unlock();
    }
  }

  public Exception getException() { 
    lock.lock();
    try {
      return exception;
    }
    finally {
      lock.unlock();
    }
  }
  
  private class GameThread extends Thread {
    public void run() {
      lock.lock(); 
      try {
        fireEvent(GameStateEvent.Type.GAME_START, "Game starting with " + playerStates.size() + " players.\n" +
            "Using configuration: " + configName);
        started = true;
        turn = 0;
        while (!stopping && !checkGameOver()) {
          TurnState turnState = new TurnState(playerStates.get(turn % playerStates.size()));
          PlayerState playerState = turnState.getPlayerState();
          Player player = playerState.getPlayer();
          for (PlayerState aPlayerState : playerStates) {
            aPlayerState.turnStarting(turnState);
          }
          
          while (turnState.hasAction()) {
            ActionCard actionCard = player.chooseActionToPlay(new VisibleTurnState(turnState, playerState));
            if (actionCard == null) {
              break;
            }
            else {
              turnState.useAction();                
              turnState.playedCard(actionCard);
              fireEvent(new GameStateEvent(player, GameStateEvent.Type.PLAYED, player, actionCard));                 
              playerState.remove(actionCard);
              actionCard.performAction(turnState);          
              if (actionCard instanceof AttackCard) {
                ((AttackCard)actionCard).attack(turnState);
              }
            }
          }
          
          int totalValue = turnState.getCostDiscount();
          for (Card card : playerState.getHand()) {
            if (card instanceof TreasureCard) {
              totalValue += ((TreasureCard)card).getValue();
            }
          }
          fireEvent(player.getName() + " ends with value: " + totalValue);
          
          while (turnState.hasBuy()) {
            List<Card> availableCards = GameState.this.availableCards(totalValue);
            Card card = player.chooseCardToGain(availableCards, new VisibleTurnState(turnState, playerState));
            if (card == null) {
              break;
            }
            else {
              turnState.useBuy();
              totalValue -= card.getCost();
              GameState.this.takeCard(card);
              playerState.discard(card);
              fireEvent(new GameStateEvent(player, GameStateEvent.Type.BOUGHT, player, card));
            }
          }
          
          playerState.discardHand();
         
          for (Card playedCard : turnState.getPlayedCards()) {
            if (playedCard.trashAfterPlaying()) {
              playerState.trash(playedCard);
            }
            else {
              playerState.discard(playedCard);
            }
          }
          
          playerState.draw(5);
          
          fireEvent(GameStateEvent.Type.TURN_END, player.getName() + " turn finished." +
              "\tScore: " + playerState.getScore() + "\tDeck: " + playerState.getTotalDeckSize());
          
          playerState.addTurnTaken();
          turn++;
        }
        
        if (stopping) {
          fireEvent(GameStateEvent.Type.GAME_STOPPED, "Game stopped.");
        }
        else {
          StringBuilder sb = new StringBuilder();
          sb.append("Game ends on turn: " + (turn / playerStates.size() + 1) + "\n");
          
          int maxScore = 0;
          List<PlayerState> playersWithMax = new ArrayList<PlayerState>();
          for (PlayerState playerState : playerStates) {
            if (maxScore < playerState.getScore()) {
              maxScore = playerState.getScore();
              playersWithMax = new ArrayList<PlayerState>();
              playersWithMax.add(playerState);
            }
            else if (maxScore == playerState.getScore()) {
              playersWithMax.add(playerState);
            }
          }
          
          if (maxScore == 0) {
            sb.append("NO ONE HAS ANY POINTS!\n\n");
          }
          else {
            int minTurnsTaken = Integer.MAX_VALUE;
            List<PlayerState> playersWithMin = new ArrayList<PlayerState>();
            for (PlayerState playerState : playersWithMax) {
              if (playerState.getTurnsTaken() < minTurnsTaken) {
                minTurnsTaken = playerState.getTurnsTaken();
                playersWithMin = new ArrayList<PlayerState>();
                playersWithMin.add(playerState);
              }
              else if (playerState.getTurnsTaken() == minTurnsTaken) {
                playersWithMin.add(playerState);
              }
            }
            if (playersWithMin.size() == 1) {
              sb.append("WINNER IS: " + playersWithMin.get(0).getPlayer().getName() + "\n\n");
            }
            else {
              sb.append("WINNERS ARE: \n");
              for (PlayerState playerState : playersWithMin) {
                sb.append("  " + playerState.getPlayer().getName() + "\n");
              }
            }
            winners = playersWithMin;
          }
          
          for (PlayerState playerState : playerStates) {
            sb.append(playerState.getPlayer().getName());
            sb.append("\tScore: ");
            sb.append(playerState.getScore());
            sb.append("\tDeck: ");
            sb.append(playerState.getTotalDeckSize());
            sb.append("\n");
          }
          
          
          for (PlayerState playerState : playerStates) {
            sb.append("\n");
            sb.append(playerState.getPlayer().getName());
            sb.append("\n");
            for (Class<? extends Card> type : allTypes) {
              int count = playerState.getCount(type);
              if (count > 0) {
                try {
                  sb.append("\t");
                  sb.append(type.getSimpleName());
                  sb.append(": ");
                  sb.append(count);
                  sb.append("\n");
                } catch (Exception e) { throw new RuntimeException(e);}
              }
            }
          }          
          
          fireEvent(GameStateEvent.Type.GAME_END, sb.toString());
        }
        
        gameOver = checkGameOver();
        stopped = stopping;
        stopping = false;
        started = false;
      }
      catch (Exception e) {
        if (stopping) {
          fireEvent(GameStateEvent.Type.GAME_END, "The game was terminated prematurely.");
        }
        else {
          exception = e;
          fireEvent(e.toString(), e);
          fireEvent(GameStateEvent.Type.GAME_END, "Game ended due to exception");
        }
      }
      finally {
        lock.unlock();
      }
    }
  }
  
  public int getTurnNumber() {    
    lock.lock(); 
    try {
      return turn;
    }
    finally {
      lock.unlock();
    }
  }
  
  public ReentrantLock getLock() {
    return lock;
  }
  
  public Condition getLockCondition() {
    return lockCondition;
  }
  
  public void addListener(GameStateListener listener) {
    lock.lock(); 
    try {
      listeners.add(listener);
    }
    finally {
      lock.unlock();
    }
  }
    
  public void fireEvent(String message, Exception e) {
    lock.lock(); 
    try {
      fireEvent(new GameStateEvent(GameStateEvent.Type.EXCEPTION, message, e));
    }
    finally {
      lock.unlock();
    }
  }
  
  public void fireEvent(GameStateEvent.Type type, String message) {
    lock.lock(); 
    try {
      fireEvent(new GameStateEvent(type, message));
    }
    finally {
      lock.unlock();
    }
  }
  
  public void fireEvent(GameStateEvent event) {
    lock.lock(); 
    try {
      List<GameStateListener> listenersCopy = new ArrayList<GameStateListener>(listeners);
      for (int i = listenersCopy.size()-1; i >= 0; i--) { 
        listenersCopy.get(i).eventOccurred(event);
      }
    }
    finally {
      lock.unlock();
    }
  }
  
  public void fireEvent(String message) {
    lock.lock(); 
    try {
      fireEvent(new GameStateEvent(message));
    }
    finally {
      lock.unlock();
    }
  }
}
