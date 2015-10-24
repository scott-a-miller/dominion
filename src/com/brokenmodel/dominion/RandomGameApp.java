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
public class RandomGameApp {
  public static void main(String[] args) {
    int gameCount = 1000;
    for (int j = 0; j < gameCount; j++) {
      int playerCount = 4;
      List<Player> players = new ArrayList<Player>(playerCount);
      for (int i = 0; i < playerCount; i++) {
        players.add(new RandomPlayer());
      }
      final GameState game = new GameState(1, players, new RandomConfiguration());    
      game.startGame(true);   
      if (game.getException() != null) {
        game.getException().printStackTrace();
        System.exit(1);
      }
      else {
        /**
        StringBuilder sb = new StringBuilder();
        for (PlayerState winner : game.getWinners()) {
          sb.append(winner.getPlayer().getName());
          sb.append("\n");
        }
        System.out.println(sb.toString());
        */
      }
    }
  }
}
