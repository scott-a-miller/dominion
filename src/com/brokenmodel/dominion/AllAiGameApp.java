/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.util.*;

/**
 * @author Scott
 *
 */
public class AllAiGameApp {
  public static void main(String[] args) {
    int gameCount = 1000;
    int playerCount = 4;
    float[] winCounts = new float[playerCount];
    long totalTurns = 0l;
    List<Player> players = new ArrayList<Player>(playerCount);
    for (int i = 0; i < playerCount; i++) {
      players.add(new SlightlyBetterPlayer("Player " + (i+1) + " (simple)"));
    }    
    for (int i = 0; i < gameCount; i++) {
      GameState game = new GameState(1, players, BasicConfigurations.FIRST_GAME);    
      game.startGame(true);
      for (PlayerState state : game.getWinners()) {
        int stateIdx = game.getPlayerStates().indexOf(state);
        winCounts[stateIdx] += (1f / (float)game.getWinners().size());
      }
      totalTurns += game.getTurnNumber();
    }
    
    System.out.println("Avg turns: " + ((float)totalTurns / (float)gameCount / 4f)); 
    for (int i = 0; i < winCounts.length; i++) {
      System.out.println("Wins: " + winCounts[i]);
    }
  }
}
