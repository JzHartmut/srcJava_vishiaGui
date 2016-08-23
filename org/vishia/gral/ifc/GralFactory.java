package org.vishia.gral.ifc;

import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.msgDispatch.LogMessage;

/**This is the super class for a factory class which allows usage of any graphical base system
 * such as AWT, Swing, SWT or other for the GRAL GRaphic Adaption Layer
 * @author Hartmut Schorrig
 *
 */
public abstract class GralFactory
{

  /**Version, history and license.
   * <ul>
   * <li>2016-07-16 Hartmut new: {@link #createGraphic(GralWindow, char, LogMessage, String)} as non abstract static method
   *   creates the graphic, for several implementation platforms by "AWT", "SWT" or special factory class.
   * <li>2015-05-01 Hartmut now a abstract class instead interface.
   * <li>2011-06-00 Hartmut created
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
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
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 20150501;



  /**Creates and opens the primary window of the application.
   * @param log
   * @param sTitle
   * @param sizeShow
   * @param left
   * @param top
   * @param xSize
   * @param ySize
   * @return the window
   * @deprecated use {@link #createGraphic(GralWindow, char, int, int, int, int, LogMessage)}
   */
  public final GralWindow createWindow(LogMessage log, String sTitle, char sizeShow, int left, int top, int xSize, int ySize)
  {
    GralMng.create(log);
    int windProps = GralWindow_ifc.windResizeable;
    GralWindow window = new GralWindow("!", "primaryWindow", sTitle, windProps);
    createWindow(window, sizeShow, left, top, xSize, ySize);
    return window;
  }
  

  /**Creates and opens the primary window of the application with a given GralWindow instance.
   * @param sizeShow
   * @param left
   * @param top
   * @param xSize
   * @param ySize
   * @return the window
   * @deprecated use {@link #createGraphic(GralWindow, char, int, int, int, int, LogMessage)}
   */
  @Deprecated public final void createWindow(GralWindow windowg, char sizeShow, int left, int top, int xSize, int ySize)
  {
    GralMng mng = GralMng.get();
    mng.setPrimaryWindow(windowg); //checks whether called firstly.
    LogMessage log = mng.log;    
    //The graphicthread creates the Swt Window.
    //SwtPrimaryWindow swtWindow = SwtPrimaryWindow.create(log, sTitle, sizeShow, left, top, xSize, ySize);
    GralGraphicThread gralGraphicThread = createGraphic(windowg, sizeShow, log);
    synchronized(gralGraphicThread){
      while(gralGraphicThread.getThreadIdGui() == 0){
        try{ gralGraphicThread.wait(1000);} catch(InterruptedException exc){}
      }
    }
  }

  /**This method should intitialize the implementation layer manager, primary window and the graphic thread.
   * @param windowg
   * @param sizeShow
   * @param left
   * @param top
   * @param xSize
   * @param ySize
   * @param log
   * @return
   */
  protected abstract GralGraphicThread createGraphic(GralWindow windowg, char sizeShow, LogMessage log);
  
  
  
  
  
  
  public static GralGraphicThread createGraphic(GralWindow windowg, char sizeShow, LogMessage log, String implementor) { 
    GralMng mng = GralMng.get();
    mng.setPrimaryWindow(windowg); //checks whether called firstly.
    GralGraphicThread gralThread = null;
    final String sNameFactoryClass;
    if(implementor.equals("SWT")) { sNameFactoryClass = "org.vishia.gral.swt.SwtFactory"; }
    else if(implementor.equals("AWT")) { sNameFactoryClass = "org.vishia.gral.awt.AwtFactory"; }
    else { sNameFactoryClass = implementor; }
    GralFactory factory;
    try{ 
      //Class<GralFactory> classfactory = ClassLoader.getSystemClassLoader().loadClass(sNameFactoryClass);
      Class<?> classfactory = Class.forName(sNameFactoryClass);
      Object oFactory = classfactory.newInstance();
      factory = (GralFactory)oFactory;           //Exception if faulty type.
    }catch(Exception exc){
      String sError = "class not found or faulty: " + sNameFactoryClass;
      System.err.println(sError);
      throw new RuntimeException(sError, exc);
    }
    try {
      gralThread = factory.createGraphic(windowg, sizeShow, log);
    } catch(Exception exc) {
      String sError = "Exception initializing graphic: " + exc.getMessage();
      System.err.println(sError);
      throw new RuntimeException(sError, exc);
    }
    return gralThread;
  }
  
  
  
  
  
  
  
  
}
