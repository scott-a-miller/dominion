/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.util.*;

import com.brokenmodel.dominion.cards.*;

/**
 * @author Scott
 *
 */
public class RandomConfiguration implements GameConfiguration {
  private static List<Class<? extends Card>> possibleTypes = new ArrayList<Class<? extends Card>>();
  static {
    possibleTypes.add(Adventurer.class);
    possibleTypes.add(Bureaucrat.class);
    possibleTypes.add(Cellar.class);
    possibleTypes.add(Chancellor.class);
    possibleTypes.add(Chapel.class);
    possibleTypes.add(CouncilRoom.class);
    possibleTypes.add(Feast.class);
    possibleTypes.add(Festival.class);
    possibleTypes.add(Gardens.class);
    possibleTypes.add(Laboratory.class);
    possibleTypes.add(Library.class);
    possibleTypes.add(Market.class);
    possibleTypes.add(Militia.class);
    possibleTypes.add(Mine.class);
    possibleTypes.add(Moat.class);
    possibleTypes.add(Moneylender.class);
    possibleTypes.add(Remodel.class);
    possibleTypes.add(Smithy.class);
    possibleTypes.add(Spy.class);
    possibleTypes.add(Thief.class);
    possibleTypes.add(ThroneRoom.class);
    possibleTypes.add(Village.class);
    possibleTypes.add(Witch.class);
    possibleTypes.add(Woodcutter.class);
    possibleTypes.add(Workshop.class);
  }

  private List<Class<? extends Card>> kingdomTypes = new ArrayList<Class<? extends Card>>(10);
  
  public RandomConfiguration() {
    Random rand = new Random();
    List<Class<? extends Card>> availableTypes = new LinkedList<Class<? extends Card>>(possibleTypes);
    while (kingdomTypes.size() < 10) {
      kingdomTypes.add(availableTypes.remove(rand.nextInt(availableTypes.size())));      
    }
    Collections.sort(kingdomTypes, new Comparator<Class<? extends Card>>() {
      public int compare(Class<? extends Card> o1, Class<? extends Card> o2) {
        return o1.getSimpleName().compareTo(o2.getSimpleName());
      }      
    });
  }
  
  public String getName() {
    return "Random";
  }

  public List<Class<? extends Card>> getKingdomTypes() {
    return new ArrayList<Class<? extends Card>>(kingdomTypes);
  }
}
