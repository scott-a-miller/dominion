/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.util.*;

import com.brokenmodel.dominion.cards.*;

/**
 *
 */
public class BasicConfigurations {
  public static final GameConfiguration singleCard(final Class<? extends Card> type) {
    return new GameConfiguration() {
      public List<Class<? extends Card>> getKingdomTypes() {
        List<Class<? extends Card>> types = new ArrayList<Class<? extends Card>>(1);
        types.add(type);
        return types;
      }
      public String getName() {
        return type.getSimpleName();
      }
    };
  }
  
  public static final GameConfiguration FIRST_GAME = new GameConfiguration() {
    public List<Class<? extends Card>> getKingdomTypes() {
      List<Class<? extends Card>> types = new ArrayList<Class<? extends Card>>();
      types.add(Cellar.class);
      types.add(Market.class);
      types.add(Militia.class);
      types.add(Mine.class);
      types.add(Moat.class);
      types.add(Remodel.class);
      types.add(Smithy.class);
      types.add(Village.class);
      types.add(Woodcutter.class);
      types.add(Workshop.class);
      return types;
    }
    public String getName() {
      return "First Game";
    }
  };

  public static final GameConfiguration THIEF = new GameConfiguration() {
    public List<Class<? extends Card>> getKingdomTypes() {
      List<Class<? extends Card>> types = new ArrayList<Class<? extends Card>>();
      types.add(Cellar.class);
      types.add(Market.class);
      types.add(Militia.class);
      types.add(Mine.class);
      types.add(Moat.class);
      types.add(Thief.class);
      types.add(Smithy.class);
      types.add(Village.class);
      types.add(Woodcutter.class);
      types.add(Workshop.class);
      return types;
    }
    public String getName() {
      return "Thief";
    }
  };
  
  public static final GameConfiguration ADVENTURER = new GameConfiguration() {
    public List<Class<? extends Card>> getKingdomTypes() {
      List<Class<? extends Card>> types = new ArrayList<Class<? extends Card>>();
      types.add(Cellar.class);
      types.add(Market.class);
      types.add(Militia.class);
      types.add(Mine.class);
      types.add(Moat.class);
      types.add(Adventurer.class);
      types.add(Smithy.class);
      types.add(Village.class);
      types.add(Woodcutter.class);
      types.add(Workshop.class);
      return types;
    }
    public String getName() {
      return "Adventurer";
    }
  };
}
