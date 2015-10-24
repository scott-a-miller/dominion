/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class AiPlayerIds {  
  private static AtomicInteger nextId = new AtomicInteger(0);
  private AiPlayerIds() { }  
  public static int nextId() {
    return nextId.decrementAndGet();
  }
}
