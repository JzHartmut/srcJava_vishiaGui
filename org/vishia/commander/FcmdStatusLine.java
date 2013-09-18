package org.vishia.commander;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.KeyCode;

public class FcmdStatusLine
{
 
  /**Version, history and license
   * <ul>
   * <li>2012-03-09 Hartmut new: {@link #widgSyncInfo}
   * <li>2012-03-04 Hartmut created as extra class. Functionality was in {@link FcmdFileCard} (setText)
   *   and {@link FcmdButtons} (initialization of widgets) 
   * </ul>
   * 
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
   * 
   * 
   */
  public static final int version = 0x20120309;

  
  private final Fcmd main;
  
  boolean showBackslash = false;
  
  GralTextField widgFileInfo, widgFilePath, widgRunInfo, widgSyncInfo;
  
  final DateFormat formatDateInfo = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
  

  String sPath = "";
  
  FcmdStatusLine(Fcmd main){
    this.main = main;
  }
  
  
  
  void buildGraphic(){
    main.gralMng.setPosition(0, 2, 0, 0, 1, 'r');
    widgFilePath = main.gralMng.addTextField(main.nameTextFieldFilePath, false, null, null);
    widgFilePath.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.layout.pathCurr.");
    widgFilePath.setDragEnable(main.actionDragFileFromStatusLine, KeyCode.dragFiles);
    GralMenu menuWidg = widgFilePath.getContextMenu();
    menuWidg.addMenuItemGthread("menuContextShowBackslash", main.idents.menuContextShowBackslash, actionShowBackslash);
    menuWidg.addMenuItemGthread("menuContextShowSlash", main.idents.menuContextShowSlash, actionShowSlash);
    main.gralMng.setPosition(2, 4, 0, 9.8f, 1, 'r');
    widgRunInfo = main.gralMng.addTextField(main.nameTextFieldRunInfo, false, null, null);
    widgRunInfo.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.layout.pathCurr.");
    main.gralMng.setPosition(2, 4, 10, -8, 1, 'r');
    widgFileInfo = main.gralMng.addTextField(main.nameTextFieldInfo, false, null, null);
    widgFileInfo.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.layout.pathCurr.");
    main.gralMng.setPosition(2, 4, -8, 0, 1, 'r');
    widgSyncInfo = main.gralMng.addTextField(main.nameTextFieldInfo, false, null, null);
    //widgSyncInfo.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.layout.pathCurr.");

  }
  
  
  void setFileInfo(FileRemote file){
    long lastModified = file.lastModified();
    String sDate = formatDateInfo.format(new Date(lastModified));
    String sLenShort = //String.format("", file.length)
      file.length() >= (1024 * 1024) ? String.format("%2.3f MByte = %d Byte", file.length()/(1024 * 1024.0), file.length()) :
      file.length() >=    1024 ? String.format("%3.2f kByte = %d Byte", file.length()/1024.0, file.length()) :
      String.format("%3d Byte", file.length());  
    StringBuilder info = new StringBuilder(100);
    info.append(sDate)/*.append(" = ").append(lastModified)*/.append(", length= ").append(sLenShort);
    if(file instanceof FileRemote){
      FileRemote filer = file;
      info.append(" ").append(filer.ident()).append(" flags=0x")
      .append(Integer.toHexString(filer.getFlags()));
      if(file.cmprResult !=null){
        if(file.isDirectory()){
          info.append(" selfiles=").append(file.cmprResult.nrofFilesSelected());
        } else {
          info.append(" sel=").append(Integer.toHexString(file.cmprResult.getMark()));
        }
      }
    }
    main.statusLine.widgFileInfo.setText(info);
    sPath = file.getAbsolutePath();
    if(showBackslash){
      sPath = sPath.replace('/', '\\');
    }
    main.statusLine.widgFilePath.setText(sPath);
    if(main.filePropsCmd.isVisible){
      main.filePropsCmd.showFileInfos(file);
    }
    main.viewCmd.quickView();
    
  }
  
  GralUserAction actionShowBackslash = new GralUserAction(){
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params) {
      showBackslash = true;
      sPath = sPath.replace('/', '\\');
      main.statusLine.widgFilePath.setText(sPath);
      return true;
    }    
  };

  GralUserAction actionShowSlash = new GralUserAction(){
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params) {
      showBackslash = false;
      sPath = sPath.replace('\\', '/');
      main.statusLine.widgFilePath.setText(sPath);
      return true;
    }    
  };



}
