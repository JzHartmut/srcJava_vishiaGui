package org.vishia.gral.area9;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPrimaryWindow;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.widget.TextBoxGuifc;
import org.vishia.mainCmd.MainCmd;
import org.vishia.mainCmd.MainCmd_ifc;

public abstract class GuiMainAreaBase implements GuiMainAreaifc
{
  public final MainCmd mainCmd;
  
  protected GralPrimaryWindow gralDevice;
  
  /**Area settings for output. */
  protected String outputArea;

  protected GralPanelContent outputPanel;
  
  protected TextBoxGuifc outputBox;
  
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
  
  @Override public TextBoxGuifc getOutputBox(){ return outputBox; }

  
  protected GralRectangle convertArea(String area)
  { int x1,x2,y1,y2;
    x1 = "ABC".indexOf(area.charAt(0));
    if(x1 < 0){x1 = "ABC".indexOf(area.charAt(1));}
    x2 = "ABC".indexOf(area.charAt(2));
    if(x2 < 0){x2 = "ABC".indexOf(area.charAt(3));}
    y1 = "123".indexOf(area.charAt(0));
    if(y1 < 0){y1 = "123".indexOf(area.charAt(1));}
    y2 = "123".indexOf(area.charAt(2));
    if(y2 < 0){y2 = "123".indexOf(area.charAt(3));}
    
    GralRectangle ret = new GralRectangle(x1+1, y1+1, x2-x1+1, y2-y1+1);
    return ret;
  }


}
