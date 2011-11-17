package org.vishia.commander;

import java.io.File;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralGridPos;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;

/**This class contains all functionality to execute copy and move for The.file.Commander.
 * @author Hartmut Schorrig
 *
 */
public class CopyCmd
{
  protected final JavaCmd main;

  
  GralWindow_ifc windConfirmCopy;
  
  GralGridPos posWindConfirmCopy;
  
  //GralWidget widgdCopyFrom, widgdCopyTo;
  
  GralTextField_ifc widgCopyFrom, widgCopyTo;
  
  GralWidget widgdOverwrite, widgdOverwriteReadOnly, widgdOverwriteHidden;

  GralWidget widgdProgress;
  
  CopyCmd(JavaCmd main)
  { this.main = main;
  }
  
  
  /**Builds the content of the confirm-copy window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowConfirmCopy()
  {
    main.gralMng.selectPanel("primaryWindow"); //"output"); //position relative to the output panel
    //System.out.println("CopyWindow frame: " + main.gralMng.pos.panel.getPixelPositionSize().toString());
    //panelMng.setPosition(1, 30+GralGridPos.size, 1, 40+GralGridPos.size, 1, 'r');
    main.gralMng.setPosition(-19, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    

    posWindConfirmCopy = main.gralMng.getPositionInPanel();
    windConfirmCopy = main.gralMng.createWindow("copyWindow", "confirm copy", GralWindow.windConcurrently);
    //System.out.println(" window: " + main.gralMng.pos.panel.getPixelPositionSize().toString());
    
    main.gralMng.setPosition(2, GralGridPos.size -2, 1, GralGridPos.size +45, 0, 'd');
    main.gralMng.addText("source path/file:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    widgCopyFrom = main.gralMng.addTextField("copyFrom", true, null, 't');
    main.gralMng.addText("destination path/file:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    widgCopyTo = main.gralMng.addTextField("copyTo", true, null, 't');
    
    main.gralMng.setPosition(GralGridPos.refer+3.5f, GralGridPos.size -3, GralGridPos.same, GralGridPos.size + 15, 0, 'r');
    widgdOverwrite = main.gralMng.addSwitchButton("copyOverwrite", null, null, null, null, "overwrite", "wh", "gn");
    widgdOverwrite = main.gralMng.addSwitchButton("copyOverwriteReadonly", null, null, null, null, "overwr readonly", "wh", "gn");
    widgdOverwrite = main.gralMng.addSwitchButton("copyOverwriteHidden", null, null, null, null, "overwr hidden", "wh", "gn");

    main.gralMng.setPosition(GralGridPos.refer+3.5f, GralGridPos.samesize, 1, 6, 0, 'r');
    main.gralMng.addButton("copyEsc", actionConfirmCopy, "esc", null, null, "esc");
    main.gralMng.setPosition(GralGridPos.same, GralGridPos.size-2, 7, -7, 0, 'r');
    widgdProgress = main.gralMng.addValueBar("copyProgress", null, null);
    //main.gralMng.setPosition(GralGridPos.same, GralGridPos.size-3, -6, -1, 0, 'r');
    main.gralMng.setPosition(-1, GralGridPos.size-3, -6, -1, 0, 'r');
    main.gralMng.addButton("copyOk", actionConfirmCopy, "ok", null, null, "OK");
  
  }
  
  
  
  /**Opens the confirm-copy window and fills its fields to ask the user whether confirm.
   * @param dst The path which is selected as destination. It may be a directory or a file
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void confirmCopy(File dst, File src)
  { String sSrc, sDstDir, sDstFile;
    if(!dst.isDirectory()){ 
      sDstDir = FileSystem.getCanonicalPath(dst.getParentFile());
      sDstFile = "/" + src.getName();
    } else {
      sDstDir = "";
      sDstFile = FileSystem.getCanonicalPath(dst);
    }
    sSrc = FileSystem.getCanonicalPath(src);
    widgCopyFrom.setText(sSrc);
    widgCopyTo.setText(sDstDir + sDstFile);
    main.gralMng.setWindowsVisible(windConfirmCopy, posWindConfirmCopy);

  }
  
  
  
  GralUserAction actionConfirmCopy = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget widgg, Object... params)
    { if(key == KeyCode.mouse1Up){
        if(widgg.sCmd.equals("ok")){
          GralButton widgButton = (GralButton)widgg;
          widgButton.setText("stop");
          //widgg.setValue(GralPanelMngWorking_ifc.cmdSet, 0, "stop");
          stop();
        } else if(widgg.sCmd.equals("esc")){
          main.gralMng.setWindowsVisible(windConfirmCopy, null); //set it invisible.
        }
      }
      return true;
    }
  };
  
  void stop(){}
  
}
