/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Scott
 *
 */
public class StatefulInterfacePlayer extends AbstractPlayer implements GameStateListener {
  public static enum State {
    WAITING,
    EVENT_AVAILABLE,
    GAME_OVER,
    EXCEPTION,
    CARDS_TO_REPLACE,
    CARDS_TO_TRASH,
    CARDS_TO_KEEP_IN_HAND,
    TREASURE_TO_UPGRADE,
    FROM_HAND_TO_BACK_ON_TOP,
    ACTION_TO_DOUBLE_PLAY,
    ACTION_TO_PLAY,
    REACTION_CARD,
    TRASH_FOR_DISCOUNT_OR_NOT,
    CARD_TO_UPGRADE,
    SHUFFLE_OR_NOT,
    CARD_TO_GAIN,
    KEEP_CARD_IN_HAND_OR_NOT,
    DISCARD_OR_PUT_BACK_ON_TOP,
    CARD_TO_TRASH_OR_KEEP,
    KEEP_OR_NOT
  }
    
  private List<GameStateEvent> events = new LinkedList<GameStateEvent>();
  
  private State state = State.WAITING;
  private List<Card> available;
  private List<Card> selectedCards;
  private VisibleTurnState turnState;
  private Boolean selectedChoice;
  private VisiblePlayerState victimState;
  private int discount = -1;
  private int upgradeValue = -1;
  private int max = -1;
  private Exception exception;  
  private String exceptionMessage;
  
  private Player backupPlayer;
  private boolean shouldTimeout = false;
  private boolean timeoutExceeded = false;
  private BackupTask backupTask;
  private Timer backupTimer = new Timer(true);
  private int startTimeout = 1200000;
  private int nextTimeout = startTimeout;
 
  private ReentrantLock lock;
  private Condition lockCondition;
  
  public StatefulInterfacePlayer(int playerId, String name, Player backupPlayer) {
    super(playerId, name);
    this.backupPlayer = backupPlayer;
  }
  
  public void setLock(ReentrantLock lock, Condition lockCondition) {
    this.lock = lock;
    this.lockCondition = lockCondition;
  }
  
  public ReentrantLock getLock() {
    return lock;
  }
  
  public Condition getLockCondition() { 
    return lockCondition;
  }
  
  public State getState() {
    lock.lock(); 
    try {
      return state;
    }
    finally {
      lock.unlock();
    }
  }

  public void eventOccurred(GameStateEvent event) {
    lock.lock(); 
    try {
      if (event.getType() != null) {
        switch (event.getType()) {
        case GAME_END: state = State.GAME_OVER;
        break;
        case EXCEPTION: state = State.EXCEPTION;
        exception = event.getException();
        exceptionMessage = event.getMessage();
        break;
        }
      }
      events.add(event);
    }
    finally {
      lock.unlock();
    }
  }  
  
  public void useBackupPlayer() {
    lock.lock(); 
    try {
      timeoutExceeded = true;
      nextTimeout = 0;
      lockCondition.signalAll();
    }
    finally {
      lock.unlock();
    }
  }
  
  public void turnStarting(VisibleTurnState turnState) {
    lock.lock(); 
    try {
      this.turnState = turnState;
    }
    finally {
      lock.unlock();
    }
  }
  
  private List<Card> waitForSelectedCards(List<? extends Card> available, int max, VisibleTurnState turnState, State state) {
    this.max = max;
    this.available = new ArrayList<Card>(available);
    List<Card> cards = waitForSelectedCards(turnState, state);
    this.available = null;
    this.max = -1;
    return cards;
  }

  private Card waitForSelectedCard(List<? extends Card> available, VisibleTurnState turnState, State state) {
    this.available = new ArrayList<Card>(available);
    List<Card> cards = waitForSelectedCards(turnState, state);
    if (timeoutExceeded) {
      return null;
    }
    this.available = null;
    if (cards.size() == 0) {
      return null;
    }
    else {
      return cards.get(0);
    }
  }

  private List<Card> waitForSelectedCards(List<? extends Card> available, VisibleTurnState turnState, State state) {
    this.available = new ArrayList<Card>(available);
    List<Card> cards = waitForSelectedCards(turnState, state);
    this.available = null;
    return cards;
  }

  private Boolean waitForSelectedChoice(List<? extends Card> available, VisibleTurnState turnState, State state) {
    this.available = new ArrayList<Card>(available);
    Boolean choice = waitForSelectedChoice(turnState, state);
    this.available = null;
    return choice;
  }

  private Boolean waitForSelectedChoice(VisibleTurnState turnState, State newState) {
    this.turnState = turnState;
    this.state = newState;
    this.selectedChoice = null;
        
    scheduleBackup();
    
    while (selectedChoice == null && !turnState.isStopping() && !timeoutExceeded) {
      try {
        lockCondition.await();
      }
      catch (InterruptedException ie) {
        
      }
    }

    if (turnState.isStopping()) {
      throw new RuntimeException("The game has been terminated.");
    }
    else if (timeoutExceeded) {
      return Boolean.FALSE;
    }

    this.state = State.WAITING;
    Boolean choice = this.selectedChoice;
    this.selectedChoice = null;
    return choice;
  }
  
  private List<Card> waitForSelectedCards(VisibleTurnState turnState, State newState) {
    this.turnState = turnState;
    this.state = newState;
    this.selectedCards = null;
    
    scheduleBackup();
    
    while (selectedCards == null && !turnState.isStopping() && !timeoutExceeded) { 
      try {
        lockCondition.await();
      }
      catch (InterruptedException ie) {
      }
    }
    
    if (turnState.isStopping()) {
      throw new RuntimeException("The game has been terminated.");
    }
    else if (timeoutExceeded) {
      return null;
    }
    
    this.state = State.WAITING;
    List<Card> cards = new ArrayList<Card>(this.selectedCards);
    this.selectedCards = null;
    return cards;
  }
  
  public List<Card> chooseCardsToReplace(List<Card> available, VisibleTurnState turnState) {
    lock.lock(); 
    try {
      List<Card> cards = waitForSelectedCards(available, turnState, State.CARDS_TO_REPLACE);
      if (checkTimeout()) {
        return backupPlayer.chooseCardsToReplace(turnState);
      }
      else {
        return cards;
      }
    }
    finally {
      lock.unlock();
    }
  }
  
  public List<Card> chooseCardsToTrash(List<Card> available, int max, VisibleTurnState turnState) {
    lock.lock(); 
    try {
      List<Card> cards = waitForSelectedCards(available, max, turnState, State.CARDS_TO_TRASH);
      if (checkTimeout()) {
        return backupPlayer.chooseCardsToTrash(max, turnState);
      }
      else {
        while (cards.size() > max) {
          cards = waitForSelectedCards(available, max, turnState, State.CARDS_TO_TRASH);
          if (checkTimeout()) {
            return backupPlayer.chooseCardsToTrash(max, turnState);
          }
        }
      }
      return cards;
    }
    finally {
      lock.unlock();
    }
  }

  public List<Card> chooseCardsToKeepInHand(List<Card> available, int max, VisibleTurnState turnState) {
    lock.lock(); 
    try {
      List<Card> cards = waitForSelectedCards(available, max, turnState, State.CARDS_TO_KEEP_IN_HAND);
      if (checkTimeout()){ 
        return backupPlayer.chooseCardsToKeepInHand(max, turnState);
      }
      else {
        while (cards.size() > max) {
          cards = waitForSelectedCards(available, max, turnState, State.CARDS_TO_KEEP_IN_HAND);
          if (checkTimeout()) {
            return backupPlayer.chooseCardsToKeepInHand(max, turnState);
          }
        }
      }
      return cards;
    }
    finally {
      lock.unlock();
    }
  }

  public TreasureCard chooseTreasureToUpgrade(List<TreasureCard> available, VisibleTurnState turnState) {
    lock.lock(); 
    try {
      TreasureCard card = (TreasureCard)waitForSelectedCard(available, turnState, State.TREASURE_TO_UPGRADE);
      if (checkTimeout()) {
        return backupPlayer.chooseTreasureToUpgrade(turnState);
      }
      else {
        return card;
      }
    }
    finally {
      lock.unlock();
    }
  }

  public Card chooseFromHandToTopOfDeck(Class<? extends Card> type, List<Card> available, VisibleTurnState turnState) {
    lock.lock(); 
    try {
      Card card = waitForSelectedCard(available, turnState, State.FROM_HAND_TO_BACK_ON_TOP);
      if (checkTimeout()) {
        return backupPlayer.chooseFromHandToTopOfDeck(type, turnState);
      }
      else {
        return card;
      }
    }
    finally {
      lock.unlock();
    }
  }

  public ReactionCard chooseToRevealReaction(List<ReactionCard> available, 
      AttackCard attackCard, VisibleTurnState turnState) {
    lock.lock(); 
    try {
      ReactionCard card = (ReactionCard) waitForSelectedCard(available, turnState, State.REACTION_CARD);
      if (checkTimeout()) {
        return backupPlayer.chooseToRevealReaction(attackCard, turnState);
      }
      else {
        return card;
      }
    }
    finally {
      lock.unlock();
    }
  }

  public Card chooseCardToUpgrade(List<Card> available, int upgradeValue, VisibleTurnState turnState) {
    lock.lock(); 
    try {
      this.upgradeValue = upgradeValue;
      Card card = waitForSelectedCard(available, turnState, State.CARD_TO_UPGRADE);
      this.upgradeValue = -1;
      if (checkTimeout()) {
        return backupPlayer.chooseCardToUpgrade(upgradeValue, turnState);
      }
      else {
        return card;
      }
    }
    finally {
      lock.unlock();
    }
  }

  public ActionCard chooseActionToDoublePlay(List<ActionCard> available, VisibleTurnState turnState) {
    lock.lock(); 
    try {
      ActionCard card = (ActionCard)waitForSelectedCard(available, turnState, State.ACTION_TO_DOUBLE_PLAY);
      if (checkTimeout()) {
        return backupPlayer.chooseActionToDoublePlay(turnState);
      }
      else {
        return card;
      }
    }
    finally {
      lock.unlock();
    }
  }

  public ActionCard chooseActionToPlay(List<ActionCard> available, VisibleTurnState turnState) {
    lock.lock(); 
    try {
      ActionCard card = (ActionCard)waitForSelectedCard(available, turnState, State.ACTION_TO_PLAY);
      if (checkTimeout()){ 
        return backupPlayer.chooseActionToPlay(turnState);
      }
      else {
        return card;
      }
    }
    finally {
      lock.unlock();
    }
  }

  public boolean chooseToShuffle(VisibleTurnState turnState) {
    lock.lock(); 
    try {      
      boolean choice = waitForSelectedChoice(turnState, State.SHUFFLE_OR_NOT);
      if (checkTimeout()) {
        return backupPlayer.chooseToShuffle(turnState);
      }
      else {
        return choice;
      }
    }
    finally {
      lock.unlock();
    }
  }

  public Card chooseCardToGain(List<Card> available, VisibleTurnState turnState) {
    lock.lock(); 
    try {
      Card card = waitForSelectedCard(available, turnState, State.CARD_TO_GAIN);
      if (checkTimeout()){ 
        return backupPlayer.chooseCardToGain(available, turnState);
      }
      else {
        return card;
      }
    }
    finally {
      lock.unlock();
    }
  }

  public boolean chooseToKeepCardInHand(Card card, VisibleTurnState turnState) {
    lock.lock(); 
    try {
      List<Card> available = new ArrayList<Card>(1);
      available.add(card);
      boolean choice = waitForSelectedChoice(available, turnState, State.KEEP_CARD_IN_HAND_OR_NOT);
      if (checkTimeout()) {
        return backupPlayer.chooseToKeepCardInHand(card, turnState);
      }
      else {
        return choice;
      }
    }
    finally {
      lock.unlock();
    }
  }

  public boolean trashCardForDiscount(Card card, int discount, VisibleTurnState turnState) {
    lock.lock(); 
    try {
      this.discount = discount;
      List<Card> available = new ArrayList<Card>(1);
      available.add(card);
      Boolean choice = waitForSelectedChoice(available, turnState, State.TRASH_FOR_DISCOUNT_OR_NOT);
      discount = -1;
      if (checkTimeout()) {
        return backupPlayer.trashCardForDiscount(card, discount, turnState);
      }
      else {
        return choice;
      }
    }
    finally {
      lock.unlock();
    }
  }

  public boolean chooseDiscardOrPutBackOnTop(Card card, VisiblePlayerState playerState, VisibleTurnState turnState) {
    lock.lock(); 
    try {
      this.victimState = playerState;
      List<Card> available = new ArrayList<Card>(1);
      available.add(card);
      boolean choice = waitForSelectedChoice(available, turnState, State.DISCARD_OR_PUT_BACK_ON_TOP);
      victimState = null;
      if (checkTimeout()) {
        return backupPlayer.chooseDiscardOrPutBackOnTop(card, playerState, turnState);
      }
      else {
        return choice;
      }
    }
    finally {
      lock.unlock();
    }
  }

  public TreasureCard chooseCardToTrashOrKeep(TreasureCard card1, TreasureCard card2, VisiblePlayerState victimState,
      VisibleTurnState turnState) {
    lock.lock(); 
    try {
      this.victimState = victimState;
      List<Card> available = new ArrayList<Card>(1);
      available.add(card1);
      available.add(card2);
      TreasureCard card = (TreasureCard)waitForSelectedCard(available, turnState, State.CARD_TO_TRASH_OR_KEEP);
      victimState = null;
      if (checkTimeout()) {
        return backupPlayer.chooseCardToTrashOrKeep(card1, card2, victimState, turnState);
      }
      else {
        return card;
      }
    }
    finally {
      lock.unlock();
    }
  }

  public boolean chooseToKeep(TreasureCard card, VisibleTurnState turnState, VisiblePlayerState victimState) {
    lock.lock(); 
    try {
      this.victimState = victimState;
      List<Card> available = new ArrayList<Card>(1);
      available.add(card);
      boolean choice = waitForSelectedChoice(available, turnState, State.KEEP_OR_NOT);
      this.victimState = null;
      if (checkTimeout()) {
        return backupPlayer.chooseToKeep(card, turnState, victimState);
      }
      else {
        return choice;
      }
    }
    finally {
      lock.unlock();
    }
  }
  
  public void sendMessage(String message) { 
    turnState.fireEvent(new GameStateEvent(this, GameStateEvent.Type.MESSAGE, this, (Card)null, message));
  }
  
  public List<Card> getSelectedCards() {
    lock.lock(); 
    try {
      if (selectedCards == null) {
        return null;
      }
      return new ArrayList<Card>(selectedCards);
    }
    finally {
      lock.unlock();
    }
  }

  public void setSelectedCard(Card card) {
    List<Card> cards = new ArrayList<Card>();
    if (card != null) {
      if (!isAvailable(card)) {
        throw new RuntimeException("Selected card: " + card.toString() + "(" + card.getCardId() + ")" +
            "is not available");
      }
      cards.add(card);
    }    
    setSelectedCards(cards);
  }
  
  public void setSelectedCards(List<Card> selectedCards) {
    lock.lock(); 
    try {
      if (selectedCards != null) {
        this.selectedCards = new ArrayList<Card>(selectedCards);
        for (Card card : selectedCards) {
          if (!isAvailable(card)) {
            throw new RuntimeException("Selected card: " + card.toString() + "(" + card.getCardId() + ")" +
              "is not available");
          }
        }
        lockCondition.signalAll();
      }
      else {
        this.selectedCards = null;
      }
    }
    finally {
      lock.unlock();
    }
  }

  public Boolean getSelectedChoice() {
    lock.lock(); 
    try {
      return selectedChoice;
    }
    finally {
      lock.unlock();
    }
  }

  public boolean isSelected(Card card) {
    lock.lock(); 
    try {
      if (selectedCards == null) {
        return false;
      }
      else {
        for (Card selectedCard : selectedCards) {
          if (card.getCardId() == selectedCard.getCardId()) {
            return true;
          }
        }
      }
      return false;
    }
    finally {
      lock.unlock();
    }
  }
  
  public boolean isAvailable(Card card) {
    lock.lock(); 
    try {
      if (available == null) {
        throw new RuntimeException("Checking for available card and the available list is null");
      }
      else {
        for (Card availableCard : available) {
          if (card.getCardId() == availableCard.getCardId()) {
            return true;
          }
        }
      }
      return false;
    }
    finally {
      lock.unlock();
    }
  }
  
  public Card getCard(int id) {
    return turnState.getCard(id);
  }
  
  public void setSelectedChoice(Boolean selectedChoice) {
    lock.lock(); 
    try {
      this.selectedChoice = selectedChoice;
      lockCondition.signalAll();
    }
    finally {
      lock.unlock();
    }
  }

  public List<Card> getAvailable() {
    lock.lock(); 
    try {  
      if (available == null) {
        return null;
      }
      return new ArrayList<Card>(available);
    }
    finally {
      lock.unlock();
    }
  }
  
  public int getDiscount() {
    lock.lock(); 
    try {
      return discount;
    }
    finally {
      lock.unlock();
    }
  }

  public GameStateEvent popEvent() {
    lock.lock(); 
    try {
      if (events.size() > 0) {
        return events.remove(0);
      }
      else {
        return null;
      }
    }
    finally {
      lock.unlock();
    }
  }
  
  public List<GameStateEvent> getEvents() {
    lock.lock(); 
    try {      
      List<GameStateEvent> myEvents = new ArrayList<GameStateEvent>(events);
      return myEvents;
    }
    finally {
      lock.unlock();
    }
  }

  public int getMax() {
    lock.lock(); 
    try {
      return max;
    }
    finally {
      lock.unlock();
    }
  }
  
  public VisibleTurnState getTurnState() {
    lock.lock(); 
    try {
      return turnState;
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
  
  public String getExceptionMessage() {
    lock.lock(); 
    try {
      return exceptionMessage;
    }
    finally {
      lock.unlock();
    }
  }
  
  public int getUpgradeValue() {
    lock.lock(); 
    try {
      return upgradeValue;
    }
    finally {
      lock.unlock();
    }
  }

  public Player getBackupPlayer() {
    return backupPlayer;
  }
  
  public VisiblePlayerState getVictimState() {
    lock.lock(); 
    try {
      return victimState;
    }
    finally {
      lock.unlock();
    }             
  }  
  
  private boolean checkTimeout() {
    lock.lock(); 
    try {
      if (timeoutExceeded) {
        turnState.fireEvent(new GameStateEvent("Timeout exceeded - using backup for " + getName()));
        backupTask = null;
        nextTimeout = nextTimeout / 2;
        turnState.fireEvent(new GameStateEvent("Next timeout will be " + (nextTimeout / 1000) + " seconds."));
        timeoutExceeded = false;
        state = State.WAITING;
        return true;
      }
      else {
        nextTimeout = startTimeout;
        descheduleBackup();
        return false;
      }
    }
    finally {
      lock.unlock();
    }
  }
  
  private void scheduleBackup() {
    lock.lock(); 
    try {
      if (backupPlayer != null) {
        if (backupTask != null) {
          backupTask.cancel();
          backupTask = null;
        }
        
        shouldTimeout = true;
        if (nextTimeout >= 1000) {
          backupTask = new BackupTask();
          backupTimer.schedule(backupTask, nextTimeout);
        }
        else {
          timeoutExceeded = true;
        }
      }
    }
    finally {
      lock.unlock();
    }
  }
  
  private void descheduleBackup() {
    lock.lock(); 
    try {
      shouldTimeout = false;
      if (backupTask != null) {
        backupTask.cancel();
        backupTask = null;
      }
    }
    finally {
      lock.unlock();
    }
  }
  
  private class BackupTask extends TimerTask {
    public void run() {
      lock.lock(); 
      try {
        if (shouldTimeout) {
          timeoutExceeded = true;
          lockCondition.signalAll();
        }
      }
      finally {
        lock.unlock();
      }
    }
  }
}
