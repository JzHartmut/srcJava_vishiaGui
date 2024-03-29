package org.vishia.commander;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralValueBar;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.FileCompare;
import org.vishia.util.KeyCode;


public final class FcmdFilesCp {

  /**Version, history and license
   * <ul>
   * <li>2012-04-17 Compares only mid and right panel.
   * <li>2011-12-00 Hartmut creation.
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL ist not appropriate for a whole software product,
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
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public final static int version = 20120417;
  

  final Fcmd main;

  GralWindow_ifc windConfirmCompare;

  GralTextField_ifc widgPath1, widgPath2;
  
  GralValueBar widgProgression;
  
  /**Buttons. */
  GralButton widgSyncWalk, widgGetDir, widgCompare;


  
  /**Composition of the comparer. */
  final FileCompare comparer = new FileCompare(0, null, 0);
  
  /**List for results of comparison as tree. */
  List<FileCompare.Result> result = new LinkedList<FileCompare.Result>();
  
  /**List for results of comparison as index sorted to local file paths. */
  final Map<String, FileCompare.Result> idxFilepath4Result = new TreeMap<String, FileCompare.Result>();
  
  /**The both selected file cards which are used for comparison.
   * For that file cards a synchronous walk through files may be done.  
   */
  FcmdFileCard card1, card2;
  
  /**The both directories where the comparison was started. */
  FileRemote file1, file2;
  
  FcmdFilesCp(Fcmd main){
    this.main = main;
  }
  
  void buildGraphic(){
    main.gui.menuBar.addMenuItem("menuFilesCpBar", main.idents.menuFilesCpBar, actionConfirmCp);
    main.gui.gralMng.selectPanel("primaryWindow");
    main.gui.gralMng.setPosition(-19, 0, -47, 0, 'r'); //right buttom, about half less display width and hight.
    
    windConfirmCompare = main.gui.gralMng.createWindow("windConfirmCompare", main.idents.windConfirmCompare, GralWindow.windConcurrently);
    //System.out.println(" window: " + main.gralMng.pos.panel.getPixelPositionSize().toString());
    
    main.gui.gralMng.setPosition(4, GralPos.size -3.5f, 1, -1, 'd', 0.5f);
    widgPath1 = main.gui.gralMng.addTextField("comparePath1", true, "compare:", "t");
    widgPath1.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.folderCmp.");
    widgPath2 = main.gui.gralMng.addTextField("comparePath2", true, "with:", "t");
    widgPath2.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.folderCmp.");
    
    main.gui.gralMng.setPosition(-1, GralPos.size - 3, 1, GralPos.size + 8, 'r',2);
    main.gui.gralMng.addButton(null, actionFilesCp, "esc", null, "esc");
    //widgSyncWalk = main.gralMng.addSwitchButton(null, null, "sync", null, null, "sync", "wh", "gn");
    //widgSyncWalk.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.folderCmp.");
    widgGetDir = main.gui.gralMng.addButton(null, actionFilesCp, "get", null, "get dir");
    widgGetDir.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.folderCmp.");
    widgCompare = main.gui.gralMng.addButton(null, actionFilesCp, "cp", null, "compare");
    widgCompare.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.folderCmp.");
    widgCompare.setPrimaryWidgetOfPanel();
  }
  
  
  
  
  
  
  
  private void buildIdxResult(FileCompare.Result item, int recursion){
    if(recursion > 100) 
      throw new IllegalArgumentException("too many deepness of file tree");
    String relpath;
    if(item.file1 !=null){
      int posRel = file1.getAbsolutePath().length() +1;  //after / starts the local path
      relpath = item.file1.getAbsolutePath().substring(posRel);
    } else {
      int posRel = file2.getAbsolutePath().length() +1;  //after / starts the local path
      relpath = item.file2.getAbsolutePath().substring(posRel);
    }
    idxFilepath4Result.put(relpath, item);
    if(item.subFiles !=null && item.subFiles.size() >0){
      for(FileCompare.Result sub: item.subFiles){
        buildIdxResult(sub, recursion +1);
      }
    }
  }
  
  
  
  /**Action if button "get dirs" is pressed. 
   * 
   */
  void setDirs(){
    FcmdFileCard[] lastFileCards = main.getLastSelectedFileCards();
    card1 = lastFileCards[0];
    card2 = lastFileCards[1];
    if(card1 !=null && card2 !=null){
      //card1.otherFileCardtoSync = card2;  //cross connect this file cards.
      //card2.otherFileCardtoSync = card1;
      file1 = card1.currentFile();
      file2 = card2.currentFile();
      card1.sDirSync = file1.getAbsolutePath();
      card2.sDirSync = file2.getAbsolutePath();
      card1.zDirSync = card1.sDirSync.length();
      card2.zDirSync = card2.sDirSync.length();
      widgPath1.setText(card1.sDirSync);
      widgPath2.setText(card2.sDirSync);
    } else {
      //if(card1 !=null){ card1.otherFileCardtoSync = null; }
      //if(card2 !=null){ card2.otherFileCardtoSync = null; }
      card1 = card2 = null;
      widgPath1.setText("");
      widgPath2.setText("");
    }
    widgCompare.setText("compare");
    widgCompare.sCmd = "cp";

  }
  
  
  
  
  GralUserAction actionConfirmCp = new GralUserAction("actionConfirmCp") {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    {
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        setDirs();
        windConfirmCompare.setFocus(); //setWindowVisible(true);
      }
      return true;
  } };

  
  GralUserAction actionFilesCp = new GralUserAction("actionFilesCp") {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    {
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(infos.sCmd.equals("get")){
          setDirs();
        } else
        if(infos.sCmd.equals("cp")){
          //filesCp();
        } else if(infos.sCmd.equals("stop")){
          widgCompare.setText("stopped");
          widgCompare.sCmd = "stop";
            //TODO
        } else if(infos.sCmd.equals("esc")){
          windConfirmCompare.setFocus(); //setWindowVisible(false);
        }
      }
      return true;
  } };
  
  
}
