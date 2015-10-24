/**
 * 
 */
package com.brokenmodel.dominion;

import java.util.*;

import com.brokenmodel.dominion.cards.*;

/**
 * @author Scott
 *
 */
public class SlightlyBetterPlayer extends SimplePlayer {
  protected List<Class<? extends Card>> endGameGainPriority = new ArrayList<Class<? extends Card>>();
  
  public SlightlyBetterPlayer(String name) {
    super(name);
    
    endGameGainPriority.add(Province.class);
    endGameGainPriority.add(Duchy.class);
    endGameGainPriority.add(Estate.class);
  }

  public Card chooseCardToGain(List<Card> cards, VisibleTurnState turnState) {    
    if (turnState.numberAvailable(Province.class) < 2) {
      return chooseFromPriority(cards, endGameGainPriority);
    }
    else {
      return chooseFromPriority(cards, gainPriority);
    }
  }  
}
