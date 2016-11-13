package org.vishia.gral.test;

import java.text.ParseException;

import org.vishia.gral.cfg.GralCfgWindow;

;

public class HelloWorldScript
{
  static String script = //possible to give the script as loaded text from a text file!
    "@primaryWindow, 3-2,2+5: Text(helloLabel, \"Hello World\"); \n"
  + "@7-3,10+12: Button(TestButton, \"press me\", action=actionTestButton); \n";

  public static void main(String[] args){
    
    try{ GralCfgWindow.createWindow("HelloWorldWindow", " hello world ", script, null, null);
    } catch(ParseException exc) {
      System.err.println("cannot create window because error in config file: " + exc.getMessage());
    }
  }

}
