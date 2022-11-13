package org.vishia.commander;

import org.vishia.gral.base.GralArea9Panel;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWindow;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;

public class FcmdGui {

  final GralMng gralMng = new GralMng(new LogMessageStream(System.out));
  
  
  final GralWindow fcmdWindow = this.gralMng.addWindow("@screen,10+80, 20+120=mainWin", "The.file.Commander");
  
  final GralArea9Panel area9 = this.gralMng.addArea9Panel("@mainWin = area9");
 
  final GralTextBox outputBox = this.gralMng.addTextBox("@area9,C1C3=outputBox", true, null, '\0');
  
  
  
  GralTextBox getOutputBox() { return this.outputBox; }

  LogMessage log() { return this.gralMng.log; }
  
  void writeError(String text) { this.gralMng.log.writeError(text); }
  
  void setFrameAreaBorders(int x1p, int x2p, int xRange, int y1p, int y2p, int yRange) { 
    this.area9.setFrameAreaBorders(x1p, x2p, xRange, y1p, y2p, yRange); 
  }
}
