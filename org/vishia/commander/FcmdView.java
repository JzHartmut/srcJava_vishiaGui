package org.vishia.commander;

import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.FileRemote;

/**All functionality of view (F3-Key) and quick view. 
 * @author Hartmut Schorrig
 * */
public class FcmdView
{
  protected final JavaCmd main;

  GralWindow_ifc windView;

  
  
  public FcmdView(JavaCmd main)
  { this.main = main;
  }
  
  
  /**Builds the content of the confirm-delete window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowConfirmDelete()
  {
    main.gralMng.selectPanel("primaryWindow");
    main.gralMng.setPosition(10, 0, 10, 0, 1, 'r'); //right buttom, about half less display width and hight.
    int windProps = GralWindow.windConcurrently | GralWindow.windHasMenu;
    GralWindow wind =  main.gralMng.createWindow("windView", "view - The.file.Commander", windProps);
    wind.addMenuItemGThread("view-Search", "&Edit/&Search", actionOpenView);
    wind.addMenuItemGThread("view-Search", "&Edit/set &Writeable", actionOpenView);
    wind.addMenuItemGThread("view-Search", "&View/&Hex-Byte", actionOpenView);
    wind.addMenuItemGThread("view-Search", "&View/text-&Windows", actionOpenView);
    wind.addMenuItemGThread("view-Search", "&View/text-&UTF", actionOpenView);
    wind.addMenuItemGThread("view-Search", "&View/text-&ASCII-7", actionOpenView);
    wind.addMenuItemGThread("view-Search", "&View/text-&Encoding", actionOpenView);
    windView = wind; 
    windView.setWindowVisible(false);
    //windView1.
  }

  
  
  
  /**Opens the view window and fills its content.
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void view(FileRemote src)
  { String sSrc, sTrash;
    windView.setWindowVisible(true);

  }
  

  /**Action for Key F3 for view command. Its like Norton Commander.
   */
  GralUserAction actionOpenView = new GralUserAction()
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { view(null);
      return true;
      // /
    }
  };


  
}
