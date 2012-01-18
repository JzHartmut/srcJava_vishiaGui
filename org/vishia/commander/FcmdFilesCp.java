package org.vishia.commander;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPos;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralValueBar;
import org.vishia.util.FileCompare;
import org.vishia.util.FileRemote;
import org.vishia.util.KeyCode;

public class FcmdFilesCp {

  
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
  
  /**List for results of comparison as index sorted to local paths. */
  final Map<String, FileCompare.Result> idxResult = new TreeMap<String, FileCompare.Result>();
  
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
    main.gui.addMenuItemGThread("menuFilesCp", main.idents.menuFilesCp, actionConfirmCp);
    main.gralMng.selectPanel("primaryWindow");
    main.gralMng.setPosition(-19, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    
    windConfirmCompare = main.gralMng.createWindow("windConfirmCompare", main.idents.windConfirmCompare, GralWindow.windConcurrently);
    //System.out.println(" window: " + main.gralMng.pos.panel.getPixelPositionSize().toString());
    
    main.gralMng.setPosition(4, GralPos.size -3.5f, 1, -1, 0, 'd', 0.5f);
    widgPath1 = main.gralMng.addTextField("comparePath1", true, "compare:", "t");
    widgPath2 = main.gralMng.addTextField("comparePath2", true, "with:", "t");
    
    main.gralMng.setPosition(-1, GralPos.size - 3, 1, GralPos.size + 8, 0, 'r',2);
    main.gralMng.addButton(null, actionFilesCp, "esc", null, null, "esc");
    widgSyncWalk = main.gralMng.addSwitchButton(null, null, "sync", null, null, "sync", "wh", "gn");
    widgGetDir = main.gralMng.addButton(null, actionFilesCp, "get", null, null, "get dir");
    widgCompare = main.gralMng.addButton(null, actionFilesCp, "cp", null, null, "compare");
    widgCompare.setPrimaryWidgetOfPanel();
  }
  
  
  void filesCp(){
    if(main.lastFileCards.size() <2) return;
    widgCompare.setText("stop");
    card1 = main.lastFileCards.get(0);
    card2 = main.lastFileCards.get(1);
    file1 = card1.currentFile;
    file2 = card2.currentFile;
    result.clear();
    FileCompare.Result result1 = new FileCompare.Result(file1, file2);
    comparer.compare(result1, null, 0);
    result = result1.subFiles;
    for(FileCompare.Result item: result){
      buildIdxResult(item, 0);
    }
    windConfirmCompare.setWindowVisible(false);
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
    idxResult.put(relpath, item);
    if(item.subFiles !=null && item.subFiles.size() >0){
      for(FileCompare.Result sub: item.subFiles){
        buildIdxResult(sub, recursion +1);
      }
    }
  }
  
  
  
  void setDirs(){
    if(main.lastFileCards.size() >=2){
      card1 = main.lastFileCards.get(0);
      card2 = main.lastFileCards.get(1);
      card1.otherFileCardtoSync = card2;  //cross connect this file cards.
      card2.otherFileCardtoSync = card1;
      file1 = card1.currentFile;
      file2 = card2.currentFile;
      card1.sDirSync = file1.getAbsolutePath();
      card2.sDirSync = file2.getAbsolutePath();
      card1.zDirSync = card1.sDirSync.length();
      card2.zDirSync = card2.sDirSync.length();
      widgPath1.setText(card1.sDirSync);
      widgPath2.setText(card2.sDirSync);
    }
  }
  
  
  
  
  GralUserAction actionConfirmCp = new GralUserAction() {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    {
      if(key == KeyCode.menuEntered || key == KeyCode.mouse1Up){
        windConfirmCompare.setWindowVisible(true);
      }
      return true;
  } };

  
  GralUserAction actionFilesCp = new GralUserAction() {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    {
      if(key == KeyCode.mouse1Up){
        if(infos.sCmd.equals("get")){
          setDirs();
        } else if(infos.sCmd.equals("cp")){
          filesCp();
        } else if(infos.sCmd.equals("stop")){
          widgCompare.setText("stopped");
          widgCompare.sCmd = "stop";
            //TODO
        } else if(infos.sCmd.equals("esc")){
          windConfirmCompare.setWindowVisible(false);
        }
      }
      return true;
  } };
  
  
}
