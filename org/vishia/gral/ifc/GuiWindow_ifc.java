package org.vishia.gral.ifc;

import java.io.File;

import org.vishia.mainCmd.MainCmd_ifc;


/**This interface represents a basic main window with title bar in the gral (graphical adaption layer).
 *
 * @author Hartmut Schorrig
 *
 */
public interface GuiWindow_ifc
{
  /**Sets the title and the pixel size for the whole window. */
  void setTitleAndSize(String sTitle, int left, int top, int xSize, int ySize);
  
  
  /**Starts the execution of the graphic initializing and handling in a own thread.
   * The following sets should be called already:
   * <pre>
   * setTitleAndSize("Title bar text", 800, 600);  //This instruction should be written first to output syntax errors.
     setStandardMenus(new File("."));
     setOutputArea("A3C3");        //whole area from mid to bottom
     setFrameAreaBorders(20, 80, 75, 80);
     </pre>
   * They sets only some values, no initializing of the graphic is executed till now.
   * <br><br>
   * The graphic thread inside this class {@link #guiThread} initializes the graphic.
   * All other GUI-actions should be done only in the graphic-thread. 
   * The method {@link #addDispatchListener(Runnable)} is to be used therefore.
   * <br><br>
   * The method to use an own thread for the GUI is not prescribed. The initialization
   * and all actions can be done in the users thread too. But then, the user thread
   * have to be called {@link #dispatch()} to dispatch the graphic events. It is busy with it than.   
   * @return true if started
   */
  boolean startGraphicThread();

  
  /**Returns true if the graphical application runs. 
   * @return
   */
  boolean isRunning();
  
  void exit();
  
  /**Activates the menu bar with the standard entries, especially the file open entry.
   * Note: This method should only be called in the same thread where the graphic runs.
   * 
   * @param openStandardDirectory If given, then this is the default directory for file open.
   * @param actionFile Action on file menu entries.
   */
  void setStandardMenusGThread(File openStandardDirectory, UserActionGui actionFile);
  
  /**Adds a listener, which will be called in the dispatch loop.
   * @param listener
   */
  void addDispatchListener(GuiDispatchCallbackWorker listener);
  
  
  /**Removes a listener, which was called in the dispatch loop.
   * @param listener
   */
  public void removeDispatchListener(GuiDispatchCallbackWorker listener);
  
  
  MainCmd_ifc getMainCmd();
  
  
  /**Returns the Frame class of the underlying graphic.
   * SWT: Shell, swing: TODO
   * @return
   */
  Object getitsGraphicFrame();
  
  



}
