package org.vishia.commander;

import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.FileSelector;
import org.vishia.util.FileRemote;

/**The F7-Key mkdir is used for mkfile too. This class builds the mkDirFile- dialog window
 * and contains its functionality.
 * @author Hartmut Schorrig
 *
 */
public class FcmdMkDirFile
{
  protected final Fcmd main;

  GralWindow_ifc windMk;

  
  
  public FcmdMkDirFile(Fcmd main)
  { this.main = main;
  }
  
  
  /**Builds the content of the confirm-delete window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowConfirmMk()
  {
    main.gralMng.selectPanel("primaryWindow");
    main.gralMng.setPosition(-19, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    int windProps = GralWindow.windConcurrently;
    GralWindow window =  main.gralMng.createWindow("windMk", "mkdir/file - The.file.Commander", windProps);
    windMk = window; 
 
  }
  
  
  /**Opens the view window and fills its content.
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void dialogMkDirFile(FileRemote src)
  { String sSrc, sTrash;
    windMk.setWindowVisible(true);

  }
  

  /**Action for Key F3 for view command. Its like Norton Commander.
   */
  GralUserAction actionOpenDialog = new GralUserAction()
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { dialogMkDirFile(null);
      return true;
      // /
    }
  };




}
