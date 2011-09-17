package org.vishia.gral.area9;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPrimaryWindow;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.mainCmd.MainCmd;
import org.vishia.mainCmd.MainCmd_ifc;

public abstract class GuiMainAreaBase implements GuiMainAreaifc
{
  public final MainCmd mainCmd;
  
  protected GralPrimaryWindow gralDevice;
  
  /**Area settings for output. */
  protected String outputArea;

  protected GralPanelContent outputPanel;
  
  /** Current Directory for file choosing. */
  protected File currentDirectory = null;
  
  protected GralUserAction actionFile;
  
  /**Set on call of {@link #setStandardMenus(File)} to add in in the graphic thread. */
  protected boolean bSetStandardMenus;
  
  protected Queue<String> outputTexts = new ConcurrentLinkedQueue<String>();
  
  

  /**Sets the output window to a defined area. .
   * Adds the edit-menu too. 
   * @param xArea 1 to 3 for left, middle, right, See {@link #setFrameAreaBorders(int, int, int, int)}
   * @param yArea 1 to 3 for top, middle, bottom
   * @param dxArea 1 to 3 for 1 field to 3 fields to right.
   * @param dyArea 1 to 3 for 1 field to 3 field to bottom
   */
  public void setOutputArea(String area){
    outputArea = area;
  }

  public GuiMainAreaBase(MainCmd mainCmd){ this.mainCmd = mainCmd; }

  public GuiMainAreaBase(MainCmd mainCmd, GralPrimaryWindow guiDevice)
  {
    super();
    this.mainCmd = mainCmd;
    this.gralDevice = guiDevice;
  }

  @Override public MainCmd_ifc getMainCmd(){ return mainCmd; }
  
  @Override public GralPanelContent getOutputPanel(){ return outputPanel; } ///
  


}
