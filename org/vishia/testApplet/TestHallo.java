package org.vishia.testApplet;

import java.applet.Applet;
import java.awt.Graphics;

public class TestHallo extends Applet
{
  @Override public void paint( Graphics g )
  {
    g.drawString( "Hello World", 100, 40 );
  }
}
