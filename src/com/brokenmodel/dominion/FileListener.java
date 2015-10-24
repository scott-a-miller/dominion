/**
 * Copyright 2008 brokenmodel.com. All rights reserved.
 * Use is subject to license terms.
 */
package com.brokenmodel.dominion;

import java.io.*;

/**
 * @author Scott
 *
 */
public class FileListener implements GameStateListener {
  private File file;
  private BufferedWriter writer;
  public FileListener(File file) {
    this.file = file;
  }  
  
  public void eventOccurred(GameStateEvent event) {
    try {
      if (writer != null) {
        writer.write(event.toString());
        writer.newLine();
      }
      if (event.getType() != null) {
        switch(event.getType()) {
        case GAME_START:
          writer = new BufferedWriter(new FileWriter(file));
          writer.write(event.toString());
          writer.newLine();
          break;
        case GAME_END:
        case GAME_STOPPED:
          writer.close();
          writer = null;
          break;
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }    
  }
}
