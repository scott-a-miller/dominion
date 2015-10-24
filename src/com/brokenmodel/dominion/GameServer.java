/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.util.*;

/**
 *
 */
public class GameServer {
  private static GameServer instance = new GameServer();
  public static GameServer getInstance() {
    return instance;
  }
  
  private int nextGameId = 1;
  
  private HashMap<Integer, GameState> gamesById = new HashMap<Integer, GameState>();
  
  private HashMap<Integer, StatefulInterfacePlayer> playersInGames = 
    new HashMap<Integer, StatefulInterfacePlayer>();
  
  private HashMap<Integer, GameState> gameStatesByPlayers =
    new HashMap<Integer, GameState>();
  
  private LinkedList<PlayerInfo> waitingPlayers = new LinkedList<PlayerInfo>();
  
  private class PlayerInfo {
    public int id;
    public String name;
    public int minHumanCount;
    public int maxHumanCount;
    public PlayerInfo(int id, String name, int minHumanCount, int maxHumanCount) {
      this.id = id;
      this.name = name;
      this.minHumanCount = minHumanCount;
      this.maxHumanCount = maxHumanCount;
    }
  }
  
  private GameServer() { }
  
  public synchronized GameState getGameById(int gameId) {
    return gamesById.get(gameId);
  }
  
  public synchronized void removeGame(int gameId) {
    GameState gameState = gamesById.get(gameId);
    if (gameState != null) {
      gamesById.remove(gameState.getGameId());
      for (PlayerState playerState : gameState.getPlayerStates()) {
        if (playerState.getPlayer() instanceof StatefulInterfacePlayer) {
          StatefulInterfacePlayer otherPlayer = (StatefulInterfacePlayer)playerState.getPlayer();
          if (gameStatesByPlayers.get(otherPlayer.getPlayerId()) != null) {
            gameStatesByPlayers.remove(otherPlayer.getPlayerId());
            playersInGames.remove(otherPlayer.getPlayerId());
          }
        }
      }
    }
  }
   
  public synchronized int getPlayingCount() {
    return playersInGames.size();
  }
  
  public synchronized int getWaitingCount() {
    return waitingPlayers.size();
  }
  
  public synchronized Integer getMinHumanCount(int playerId) {
    for (PlayerInfo waitingPlayer : waitingPlayers) {
      if (waitingPlayer.id == playerId) {
        return waitingPlayer.minHumanCount;
      }
    }
    return null;
  }

  public synchronized Integer getMaxHumanCount(int playerId) {
    for (PlayerInfo waitingPlayer : waitingPlayers) {
      if (waitingPlayer.id == playerId) {
        return waitingPlayer.maxHumanCount;
      }
    }
    return null;
  }

  public synchronized StatefulInterfacePlayer createOrRejoinMultiPlayerGame(int playerId, String name, int minHumanCount, int maxHumanCount) {    
    PlayerInfo info = null;
    for (PlayerInfo waitingPlayer : waitingPlayers) {
      if (waitingPlayer.id == playerId) {
        info = waitingPlayer;
      }
    }
    if (info != null) {
      info.name = name;
      info.minHumanCount = minHumanCount;
      info.maxHumanCount = maxHumanCount;
    }
    else {
      info = new PlayerInfo(playerId, name, minHumanCount, maxHumanCount);
      waitingPlayers.add(info);
    }
        
    StatefulInterfacePlayer myInterface = null;
    for (int i = 4; i >= 2; i--) {
      List<PlayerInfo> gamePlayers = new ArrayList<PlayerInfo>(4);
      List<PlayerInfo> removedPlayers = new ArrayList<PlayerInfo>();
      for (PlayerInfo waiter : waitingPlayers) {
        if (i >= waiter.minHumanCount && i <= waiter.maxHumanCount) {
          gamePlayers.add(waiter);
          if (gamePlayers.size() == i) {
            for (PlayerInfo gamePlayer : gamePlayers) {
              removedPlayers.add(gamePlayer);
            }
            myInterface = createOrRejoinMultiPlayerGame(waiter.id, waiter.name, gamePlayers, new RandomConfiguration());
          }
        }
      }
      for (PlayerInfo playerToRemove : removedPlayers) {
        waitingPlayers.remove(playerToRemove);
      }
    }
    
    return myInterface;
  }
    
  public synchronized void exitGame(int playerId) {
    StatefulInterfacePlayer player = playersInGames.get(playerId);
    if (player != null) { 
      playersInGames.remove(playerId);
    }
    GameState gameState = gameStatesByPlayers.get(playerId);
    if (gameState != null) {
      gameStatesByPlayers.remove(playerId);
      if (gameState.isSinglePlayer()) {
        gameState.stopGame();
        removeGame(gameState.getGameId());
      }
      else {
        for (PlayerState playerState : gameState.getPlayerStates()) {
          if (playerState.getPlayer() instanceof StatefulInterfacePlayer) {
            StatefulInterfacePlayer playerInterface = (StatefulInterfacePlayer)playerState.getPlayer();          
            if (playerInterface.getPlayerId() == playerId) {
              playerInterface.useBackupPlayer();
              playerState.setPlayer(playerInterface.getBackupPlayer());
            }
          }
        }
      }
    }          
  }
  
  public synchronized StatefulInterfacePlayer getPlayerInGame(int playerId) {
    return playersInGames.get(playerId);
  }

  public synchronized StatefulInterfacePlayer rejoinGameOrCreate(int playerId, String name) {
    return rejoinGameOrCreate(playerId, name, null, false);
  }
  
  public synchronized StatefulInterfacePlayer rejoinGameOrCreate(
      int playerId, 
      String name,
      GameConfiguration configuration,
      boolean testing) {
    StatefulInterfacePlayer player = getPlayerInGame(playerId);
    if (player != null) {
      return player;
    }
    return createSinglePlayerGame(3, playerId, name, configuration, testing);
  }  
  
  public synchronized StatefulInterfacePlayer joinNewGame(int playerId, String name) {
    return joinNewGame(playerId, name, null, false);
  }
  
  public synchronized StatefulInterfacePlayer joinNewGame(
      int playerId, 
      String name, 
      GameConfiguration configuration, 
      boolean testing) {
    playersInGames.remove(playerId);
    return createSinglePlayerGame(3, playerId, name, configuration, testing); 
  }
  
  public synchronized StatefulInterfacePlayer createOrRejoinMultiPlayerGame(
      int playerId,
      String name,
      List<PlayerInfo> playerInfos,
      GameConfiguration configuration) {
    StatefulInterfacePlayer currentPlayer = getPlayerInGame(playerId);
    if (currentPlayer != null) {
      return currentPlayer;
    }
    
    StatefulInterfacePlayer myPlayer = null;
    List<StatefulInterfacePlayer> players = new ArrayList<StatefulInterfacePlayer>();
    List<SimplePlayer> backupPlayers = new ArrayList<SimplePlayer>();
    for (PlayerInfo playerInfo : playerInfos) {
      SimplePlayer backupPlayer = new SimplePlayer("Backup Player");
      StatefulInterfacePlayer player = new StatefulInterfacePlayer(playerInfo.id, playerInfo.name, backupPlayer);
      if (player.getPlayerId() == playerId) {
        myPlayer = player;
      }
      players.add(player);
      backupPlayers.add(backupPlayer);
    }
    
    if (configuration == null) {
      configuration = new RandomConfiguration();
    }
    
    GameState game = new GameState(nextGameId++, new ArrayList<Player>(players), configuration);
    
    for (int i = 0; i < players.size(); i++) {
      StatefulInterfacePlayer player = players.get(i);
      SimplePlayer backupPlayer = backupPlayers.get(i);
      game.addListener(player);
      player.setLock(game.getLock(), game.getLockCondition());
      backupPlayer.setMyState(game.getPlayerStates().get(i));
      playersInGames.put(player.getPlayerId(), player);
      gameStatesByPlayers.put(player.getPlayerId(), game);
    }

    gamesById.put(game.getGameId(), game);
    game.startGame();
    
    return myPlayer;
  }
  
  public synchronized StatefulInterfacePlayer createSinglePlayerGame(
      int otherPlayerCount, 
      int playerId, 
      String name, 
      GameConfiguration configuration,
      boolean testing) {
    StatefulInterfacePlayer player = getPlayerInGame(playerId);
    if (player != null) {
      return player;
    }
    
    List<Player> players = new ArrayList<Player>();
    for (int i = 0; i < otherPlayerCount; i++) {
      if (testing) {
        players.add(new RandomPlayer());
      }
      else {
        players.add(new SimplePlayer("Simple Player " + (i+1)));
      }
    }
    player = new StatefulInterfacePlayer(playerId, name, null);
    players.add(player);
    
    if (configuration == null) {
      configuration = new RandomConfiguration();
    }
    
    GameState game = new GameState(nextGameId++, players, configuration);
    
    Iterator<PlayerInfo> waitingIter = waitingPlayers.iterator();
    while (waitingIter.hasNext()) {
      if (playerId == waitingIter.next().id) {
        waitingIter.remove();
        break;
      }
    }
    
    game.addListener(player);
    player.setLock(game.getLock(), game.getLockCondition());

    gamesById.put(game.getGameId(), game);
    game.startGame();
    
    playersInGames.put(playerId, player);
    gameStatesByPlayers.put(playerId, game);
    
    return player;
  }
  
  public synchronized List<GameState> getGames() {
    return new ArrayList<GameState>(gamesById.values());
  }
}
