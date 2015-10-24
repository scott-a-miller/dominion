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
public class SinglePlayerGameApp {
  public static void main(String[] args) {
    int playerCount = 4;
    List<Player> players = new ArrayList<Player>(playerCount);
    for (int i = 0; i < playerCount-1; i++) {
      players.add(new SimplePlayer("Player " + (i+1)));
    }
    players.add(new ConsolePlayer(1, "Scott"));
    GameState game = new GameState(1, players, BasicConfigurations.ADVENTURER);    
    game.addListener(new ConsoleListener());    
    game.addListener(new FileListener(new File("output.txt")));
    game.startGame();
  }
}
