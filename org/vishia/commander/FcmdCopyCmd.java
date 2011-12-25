package org.vishia.commander;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.vishia.commander.target.FcmdtTarget_ifc;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPos;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.FileSelector;
import org.vishia.util.FileRemote;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;

/**This class contains all functionality to execute copy and move for The.file.Commander.
 * @author Hartmut Schorrig
 *
 */
public class FcmdCopyCmd
{
  protected final Fcmd main;

  
  GralWindow_ifc windConfirmCopy;
  
  GralPos posWindConfirmCopy;
  
  //GralWidget widgdCopyFrom, widgdCopyTo;
  
  GralTextField_ifc widgCopyFrom, widgCopyDirDst, widgCopyNameDst, widgCopyState;
  
  GralWidget widgdOverwrite, widgdOverwriteReadOnly, widgdOverwriteHidden;

  GralWidget widgdProgress;
  
  List<String> listFileSrc;
  
  FcmdCopyCmd(Fcmd main)
  { this.main = main;
  }
  
  
  /**Builds the content of the confirm-copy window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowConfirmCopy()
  {
    main.gralMng.selectPanel("primaryWindow"); //"output"); //position relative to the output panel
    //System.out.println("CopyWindow frame: " + main.gralMng.pos.panel.getPixelPositionSize().toString());
    //panelMng.setPosition(1, 30+GralGridPos.size, 1, 40+GralGridPos.size, 1, 'r');
    main.gralMng.setPosition(-28, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    

    posWindConfirmCopy = main.gralMng.getPositionInPanel();
    windConfirmCopy = main.gralMng.createWindow("copyWindow", "confirm copy", GralWindow.windConcurrently);
    //System.out.println(" window: " + main.gralMng.pos.panel.getPixelPositionSize().toString());
    
    main.gralMng.setPosition(2, GralPos.size -2, 1, GralPos.size +45, 0, 'd');
    main.gralMng.addText("source:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    widgCopyFrom = main.gralMng.addTextField("copyFrom", false, null, "t");
    main.gralMng.addText("destination dir path:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    widgCopyDirDst = main.gralMng.addTextField("copyDirDst", true, null, "t");
    main.gralMng.addText("destination filename:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    main.gralMng.setPosition(GralPos.refer + 2, GralPos.size -2, 1, 25, 0, 'r');
    widgCopyNameDst = main.gralMng.addTextField("copyNameDst", true, null, "t");
    main.gralMng.setPosition(GralPos.refer, GralPos.size -2, 26, 0, 0, 'r');
    widgCopyState = main.gralMng.addTextField("copyStatus", false, null, "t");
    
    main.gralMng.setPosition(GralPos.refer+3.5f, GralPos.size -3, 1, 15, 0, 'r');
    widgdOverwrite = main.gralMng.addButton("copyOverwrite", null, null, null, null, "overwrite this");
    main.gralMng.setPosition(GralPos.same, GralPos.size -3, 16, 30, 0, 'r');
    widgdOverwrite = main.gralMng.addSwitchButton("copyOverwrite", null, null, null, null, "overwrite all", "wh", "gn");
    main.gralMng.setPosition(GralPos.same, GralPos.size -3, 31, -1, 0, 'r');
    widgdOverwrite = main.gralMng.addSwitchButton("copyOverwriteReadonly", null, null, null, null, "overwr all readonly", "wh", "gn");

    main.gralMng.setPosition(GralPos.refer+3.5f, GralPos.size -3, 1, 15, 0, 'r');
    widgdOverwrite = main.gralMng.addButton("copyOverwrite", null, null, null, null, "skip");
    main.gralMng.setPosition(GralPos.same, GralPos.size -3, 16, 30, 0, 'r');
    widgdOverwrite = main.gralMng.addButton("copyOverwrite", null, null, null, null, "skip dir");
    
    main.gralMng.setPosition(-4, -1, 1, 6, 0, 'r');
    main.gralMng.addButton("copyEsc", actionDoCopy, "esc", null, null, "esc");
    main.gralMng.setPosition(-2, -0.5f, 7, -7, 0, 'r');
    widgdProgress = main.gralMng.addValueBar("copyProgress", null, null);
    //main.gralMng.setPosition(GralGridPos.same, GralGridPos.size-3, -6, -1, 0, 'r');
    main.gralMng.setPosition(-1, GralPos.size-3, -6, -1, 0, 'r');
    main.gralMng.addButton("copyOk", actionDoCopy, "ok", null, null, "OK");
  
  }
  
  
  
  
  
  /**
   * Key F5 for copy command. Its like Norton Commander.
   */
  GralUserAction actionConfirmCopy = new GralUserAction()
  {
    /**Opens the confirm-copy window and fills its fields to ask the user whether confirm.
     * @param dst The path which is selected as destination. It may be a directory or a file
     * @param src The path which is selected as source. It may be a directory or a file.
     */
    @Override
    public boolean userActionGui(int key, GralWidget infos,
        Object... params)
    { String sSrc, sDstName, sDstDir;
      main.findLastFocusedFileTables();
     
      if(main.lastFocusedFileTables[0] == null){
        sSrc = sDstName = sDstDir = "?";
      } else {
        List<String> filesSrc = main.lastFocusedFileTables[0].getSelectedFiles();
        if(filesSrc !=null && filesSrc.size() >0){
          final String srcDir = main.lastFocusedFileTables[0].getCurrentDir();
          listFileSrc = main.lastFocusedFileTables[0].getSelectedFiles();
          sSrc = srcDir + "*"; 
          sDstName = "*";
        } else {
          FileRemote srcFile = main.lastFocusedFileTables[0].getSelectedFile();
          sSrc = srcFile.getParent() + "/" + srcFile.getName();
          sDstName = srcFile.getName();
        }
        if(main.lastFocusedFileTables[1] == null){
          sDstName = sDstDir = "?";
        } else {
          FileRemote dstFile = main.lastFocusedFileTables[1].getSelectedFile();
          sDstDir = dstFile.getParent();
        }
      }
      widgCopyFrom.setText(sSrc);
      widgCopyDirDst.setText(sDstDir);
      widgCopyNameDst.setText(sDstName);
      main.gralMng.setWindowsVisible(windConfirmCopy, posWindConfirmCopy);
      return true;
   }  };
  
  
  
  
  GralUserAction actionDoCopy = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget widgg, Object... params)
    { if(key == KeyCode.mouse1Up){
        if(widgg.sCmd.equals("ok")){
          boolean bOk = true; //doCopy();
          String sSrc = widgCopyFrom.getText();
          String sDstDir = widgCopyDirDst.getText();
          String sDstName = widgCopyNameDst.getText();
          String sCmdParam = sSrc + '\n' + sDstDir + sDstName;
          if(sSrc.contains("*")){
            for(String srcName: listFileSrc){
              sCmdParam += srcName + "\n";
            }
          }
          main.target.cmdTarget(FcmdtTarget_ifc.copyFile, sCmdParam);
          if(bOk){
            main.gralMng.setWindowsVisible(windConfirmCopy, null); //set it invisible.
          } else {
            GralButton widgButton = (GralButton)widgg;
            widgButton.setText("stop");
            //widgg.setValue(GralPanelMngWorking_ifc.cmdSet, 0, "stop");
            stop();
          }
        } else if(widgg.sCmd.equals("esc")){
          main.gralMng.setWindowsVisible(windConfirmCopy, null); //set it invisible.
        }
      }
      return true;
    }
  };
  
  void stop(){}
  
}
