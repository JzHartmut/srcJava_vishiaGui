package org.vishia.commander;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralLed;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPos;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralFileSelector;
import org.vishia.util.FileRemote;
import org.vishia.util.KeyCode;

public class FcmdFileProps
{
  protected final Fcmd main;

  GralWindow_ifc windFileProps;
  GralTextField_ifc widgName;
  GralLed widgDirectory;
  GralButton widgReadonly, widgHidden, widgChgName;
  
  final String sWrAble = "wr / ?rd", sRdOnly = "rd / ?wr";
  
  final String sHidden = "hidden / ?", sNonHidden = "non hidden / ?";
  
  final String sChgNameBtnFileProps = "set name";
  
  public FcmdFileProps(Fcmd main)
  { this.main = main;
  }
  
  
  /**Builds the content of the confirm-delete window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowConfirmMk()
  {
    main.gralMng.selectPanel("primaryWindow");
    main.gralMng.setPosition(-19, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    int windProps = GralWindow.windConcurrently;
    GralWindow window =  main.gralMng.createWindow("windProp", "file properties - The.file.Commander", windProps);
    windFileProps = window; 
    main.gralMng.setPosition(3, GralPos.size -3, 1, -1, 0, 'd');
    main.gralMng.addText("name:", 0, GralColor.getColor("bk"), GralColor.getColor("wh"));
    widgName = main.gralMng.addTextField("name", false, "filename", "t");
    main.gralMng.setPosition(6, GralPos.size -2, 1, GralPos.size +8, 0, 'r', 2);
    widgReadonly = main.gralMng.addButton("FileProp:btnro", actionButton, "rdwr", null, null, sWrAble);
    widgHidden = main.gralMng.addButton("FileProp:btnhi", actionButton, "hidden", null, null, sWrAble);
    //widgDirectory = main.gralMng.addLed("FileProp:ledDir", null, null);
    main.gralMng.setPosition(6, GralPos.size -2, -10, GralPos.size +9, 0, 'r');
    widgChgName = main.gralMng.addButton("FileProp:btnname", actionButton, "name", null, null, sChgNameBtnFileProps);
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



  /**Action for Key F2 for view command. 
   */
  GralUserAction actionButton = new GralUserAction()
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { if(keyCode == KeyCode.mouse1Up){
      
      }
      return true;
      // /
    }
  };



  
}
