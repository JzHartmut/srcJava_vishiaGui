package org.vishia.commander;

import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.FileSelector;
import org.vishia.util.FileRemote;

public class FcmdFileProps
{
  protected final JavaCmd main;

  GralWindow_ifc windFileProps;

  
  
  public FcmdFileProps(JavaCmd main)
  { this.main = main;
  }
  
  
  /**Builds the content of the confirm-delete window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowConfirmMk()
  {
    main.gralMng.selectPanel("primaryWindow");
    main.gralMng.setPosition(-19, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    int windProps = GralWindow.windConcurrently;
    GralWindow window =  main.gralMng.createWindow("windMk", "file properties - The.file.Commander", windProps);
    windFileProps = window; 
 
  }
  
  
  /**Opens the view window and fills its content.
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void openDialog(FileRemote src)
  { String sSrc, sTrash;
    windFileProps.setWindowVisible(true);

  }
  
  /**Action for Key F2 for view command. 
   */
  GralUserAction actionOpenDialog = new GralUserAction()
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { openDialog(null);
      return true;
      // /
    }
  };




  
}
