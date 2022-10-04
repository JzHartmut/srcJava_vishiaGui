package org.vishia.gral.swt;

import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.msgDispatch.LogMessage;

public class SwtFactory extends GralFactory
{
  
  
  /**Initializes the {@link SwtGraphicThread#SwtGraphicThread(GralWindow, char, LogMessage)}
   * and waits for execution of 
   * 
   * {@link GralMng#runGraphicThread()} (set a breakpoint here to step into)
   * 
   * till the point where the {@link GralGraphicThread#orderList}
   * is started to run. Then the {@link GralGraphicThread#waitForStart()} is fulfilled,
   * and this operation returns with the {@link GralGraphicThread} instance to use for further orders.
   * The orders are given to the GraphicThread usual by {@link GralWidget#repaint()} requested with the changed data of the widget.
   * It means nothing is necessary with the GralGraphicThread from user level.
   */
  @Override public void createGraphic(GralWindow windowg, char sizeShow, LogMessage log){
    SwtMng swtMng = new SwtMng(windowg.gralMng(), sizeShow, log);
    
  }


}
