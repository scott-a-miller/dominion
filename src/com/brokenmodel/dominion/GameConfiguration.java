/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.util.List;

/**
 * A configuration for a game
 */
public interface GameConfiguration {
  public String getName();
  public List<Class<? extends Card>> getKingdomTypes();  
}
