package org.vishia.gral.test;

import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.gral.widget.GralPlotArea;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;

/**A window which contains a {@link GralPlotArea} for simple plot in a window for example able to use in JZcmd.
 * @author Hartmut Schorrig
 *
 */
public class GralPlotWindow
{

    /**Version, history and license.
   * <ul>
   * <li>2015-09-26 Hartmut creation.   
   * </ul>
   * <br><br>
   * <b>Copyright/Copyleft</b>:
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut@vishia.org
   * 
   */
  public static final String sVersion = "2015-09-26";

  final GralMng gralMng = GralMng.get();
  
  final GralPlotArea canvas = new GralPlotArea("0..0,0..0", "+canvas");
  
  
  public static GralPlotWindow create(String sTitle){
    GralPlotWindow obj = new GralPlotWindow();
    GralFactory gralFactory = new SwtFactory();
    LogMessage log = new LogMessageStream(System.out);
    GralWindow wind = gralFactory.createWindow(log, sTitle, 'B', 150, 10,1000, 800);
    obj.gralMng.gralDevice.addDispatchOrder(obj.initGraphic);
    obj.initGraphic.awaitExecution(1, 0);
    return obj;  
  }

  
  public void waitForClose(){
    while(gralMng.gralDevice.isRunning()){
      try{ Thread.sleep(100);} 
      catch (InterruptedException e)
      { //dialogZbnfConfigurator.terminate();
      }
    }

  }
  
  
  /**To use all methods of GralCanvasArea.
   */
  public GralPlotArea canvas(){ return canvas; }
    
    
    
  GralGraphicTimeOrder initGraphic = new GralGraphicTimeOrder("GralArea9Window.initGraphic"){
    @Override public void executeOrder()
    {
      canvas.setToPanel(null);      
      //gralMng.addTextField();
    } };

  
}
