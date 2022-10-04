package org.vishia.gral.ifc;

import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWidget;
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
   * <li>2022-09-14 chg  {@link #createGraphic(GralWindow, char, LogMessage)} now public, this should be the main way to create.
   * <li>2017-01-15 Hartmut chg in deprecated createWindow, see comment there. 
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
    //@date 2017-01-15 a "!" is faulty yet, use a sensible position string 
    GralWindow window = new GralWindow("@10+100,30+150", "primaryWindow", sTitle, windProps);
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
    mng.registerWindow(windowg); //checks whether called firstly.
    LogMessage log = mng.log;    
    //The graphicthread creates the Swt Window.
    //SwtPrimaryWindow swtWindow = SwtPrimaryWindow.create(log, sTitle, sizeShow, left, top, xSize, ySize);
//    GralGraphicThread gralGraphicThread = createGraphic(windowg, sizeShow, log);
//    synchronized(gralGraphicThread){
//      while(gralGraphicThread.getThreadIdGui() == 0){
//        try{ gralGraphicThread.wait(1000);} catch(InterruptedException exc){}
//      }
//    }
  }

  /**This method should initialize the whole implementation graphic with the given GralWindow 
   * and all {@link GralPanelContent} and all {@link GralWidget} into. 
   * <br>
   * It means it is the new concept since ~2016/18 established for all in 2022: 
   * <ul><li>First create the graphic independent graphic description data.
   * <li>2th create the proper Factory
   * <li>3th with this operation create the implementation graphic as a whole.
   * <ul>
   * Using specific GralFactory implementors can enable also other graphic systems.
   * Examples see {@link org.vishia.guiViewCfg.ViewCfg}.
   * <br>
   * It creates the implementation graphic manager, primary window and the graphic thread.
   * @param windowg
   * @param sizeShow 'A' ... 'G' for the size, pixel per grid
   * @param log a log necessary?
   * @return the Graphic Thread can be used to add {@link GralGraphicThread#addDispatchOrder(org.vishia.gral.base.GralGraphicTimeOrder)}.
   */
  public abstract void createGraphic(GralMng gralMng, char sizeShow);
  
  
  
  
  
  
  /**First call to initialize the graphic.
   * use<pre>
   * GralWindow primaryWindow = new GralWindow(posWindow1, "primaryWindow", sTitle1, windProps);
   * GralFactory.createGraphic(primaryWindow, sizeShow, log, "SWT");
   * </pre> 
   * With the argument "implementor" it searches a proper {@link GralFactory} class:
   * {@link org.vishia.gral.swt.SwtFactory} or {@link org.vishia.gral.awt.AwtFactory} and calls 
   * {@link GralFactory#createGraphic(GralWindow, char, LogMessage)}
   * This creates a {@link GralGraphicThread} which is returned and can be used to add further orders.
   * <br>
   * The argument "windowg" given {@link GralWindow} is used to create the whole graphic implementation
   * as all widgets are referenced from the {@link GralWindow}.
   * This is done for SWT in {@link org.vishia.gral.swt.SwtGraphicThread#initGraphic()}.  In the graphic thread the {@link GralGraphicThread#runGraphicThread()} is executed which initializes the graphic, the main Window and all underlying widgets.
   * @param windowg
   * @param sizeShow
   * @param log
   * @param implementor null for "SWT", "SWT" or "AWT",
   * @return
   */
  public static GralMng createGraphic(GralWindow windowg, char sizeShow, LogMessage log, String implementor) { 
    GralMng mng = GralMng.get();
    mng.registerWindow(windowg); //checks whether called firstly.
    final String sNameFactoryClass;
    if(implementor == null) { implementor = "SWT"; }
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
    try { //this start the GralGraphicThread, see run(). There the primaryWindow will be created.
      factory.createGraphic(mng, sizeShow);
    } catch(Exception exc) {
      String sError = "Exception initializing graphic: " + exc.getMessage();
      System.err.println(sError);
      throw new RuntimeException(sError, exc);
    }
    return mng;
  }
  
  
  
  
  
  
  
  
}
