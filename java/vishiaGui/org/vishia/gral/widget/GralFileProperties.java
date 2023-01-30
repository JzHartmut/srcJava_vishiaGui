package org.vishia.gral.widget;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;

import org.vishia.event.EventConsumer;
import org.vishia.event.EventSource;
import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralSwitchButton;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetBase;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.FileFunctions;
import org.vishia.util.KeyCode;

/**This comprehensive widget contains all to show and change properties of a File
 * given as FileRemote.
 * @author Hartmut Schorrig
 *
 */
public class GralFileProperties extends GralWidgetBase {

  /**Version, history and license.
   * <ul>
   * <li>2023-01-30 some enhancements, in progress 
   * <li>2023-01-15 Now it is an independent common usable class especially for {@link GralFileSelector}
   * <li>2011 Hartmut created with The.file.Commander
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public final static String version = "2023-01-15";
  
  GralColor colorChanged = GralColor.getColor("pye");
  GralColor colorUnchanged = GralColor.getColor("wh");
  GralColor colorWrong = GralColor.getColor("pma");
  GralColor colorGrayed = GralColor.getColor("gr");
  
  
  final String buttonFilePropsChg = "change file";

  final String buttonFilePropsChanging = "changing ...";

  final String buttonFilePropsCopying = "copying ...";

  final String buttonFilePropsRetry = "retry";

  final String buttonFilePropsAbort = "abort change";

  final String buttonFilePropsOk = "change done";

  final String buttonFilePropsCopy = "copy file";

  final String buttonFilePropsChgRecurisve = "change recursive";

  final String buttonFilePropsGetAll = "get all properties";

  final String buttonFilePropsCntLen = "count length all files in dir";

  GralWindow_ifc windFileProps;
  
  GralPanelContent panel;
  
  GralTextField widgName, widgNameNew, widgDir, widgLink, widgDate, widgLength;
  
  GralButton[] widgRd, widgWr, widgEx;
  GralButton widgUID, widgGID, widgSticky;
  GralButton widgHidden, widgDirectory;
  
  /**Action button. */
  GralButton widGetAllProps, widgChgRecurs, widgChgFile, widgCopyFile, widgDelFile, widgRename, widgCreateDirFile;
  
  /**This action is called after changing the file.
   * See {@link #setActionRefresh(GralUserAction)}.
   */
  GralUserAction actionRefresh;
  
  DateFormat formatDate;
  
  final String sWrAble = "wr / ?rd", sRdOnly = "rd / ?wr";
  
  final String sHidden = "hidden / ?", sNonHidden = "non hidden / ?";
  
  final String sSubdir = "recursive / ?", sNonSubdir = "non recurs/ ?";
  
  static final String sCmdChg = "change", sCmdCopy = "copy", sCmdChgRecurs = "chgRecurs"
    , sCmdQuit = "quit", sCmdAbort = "abort";
  
  /**True if it is a unix system. It checks the "OS" environment variable. */
  boolean bUnixSystem;
  
  /**True then the window is opened. Write acutal file into. */
  boolean isVisible;
  
  String sNameNew;
  
  /**The time stamp string how written on {@link #showFileInfos(FileRemote)} 
   * It is compared with maybe changed content in the {@link #widgDate}.
   * Only if the content is changed, a new time stamp will be set.
   */
  String sDateOriginal;
  
  /**The flags how written on {@link #showFileInfos(FileRemote)} 
   * It is compared with maybe changed content in the {@link #widgRd} etc..
   * Only if the buttons are changed, a new time stamp will be set.
   */
  int nFlagsOriginal;
  
  /**True while a change commission is send and no answer is received yet. */
  //boolean busyChanging;
  
  /**The file given on {@link #showFileInfos(FileRemote, GralFileSelector)}. */
  FileRemote actFile, actDir;

  
  /**The fileSelector given on {@link #showFileInfos(FileRemote, GralFileSelector)}. 
   * used for refresh.*/
  GralFileSelector fileSelector;
  
  
  /**
   * 
   */
  final FileRemote.CallbackEvent evChg;
  
  final FileRemote.CallbackEvent evBack;
  
  final FileRemote.CallbackEvent evCntLen;
 
  
  
  
  /**Ctor either with a given new window or to create a Panel
   * @param refPos If wind is given, it is the refPos related to the wind.mainPanel,
   *   elsewhere a reference for given sPosName in a panel to create a sub panel. 
   * @param sPosName if wind is given, the name of its mainPanel, else maybe with position String
   * @param wind null, then a new Panel for this content will be created.
   */
  public GralFileProperties(GralPos refPos, String sPosName, GralWindow wind) {
    super(refPos, sPosName, null);
    this.windFileProps = wind;
    if(wind == null) {
      this.panel = new GralPanelContent(refPos, sPosName, null);
    } else {
      this.panel = wind.mainPanel;                         // given panel, refPosP not used, it is the whole panel
    }
    this.formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String sUnix = System.getenv("OS");
    if(sUnix !=null){
      sUnix = sUnix.toUpperCase();
      if(sUnix.contains("WINDOWS")){
        this.bUnixSystem = false;
      } else {
        this.bUnixSystem = true;
      }
    } else {
      this.bUnixSystem = System.getenv("WINDIR") == null;
    }
    this.evBack = new FileRemote.CallbackEvent(this.evSrc, null, null, this.callbackChgProps, null, this.evSrc);
    this.evChg = new FileRemote.CallbackEvent(this.evSrc, null, null, null, null, this.evSrc);
    this.evCntLen = new FileRemote.CallbackEvent(this.evSrc, null, null, this.callbackCntLen, null, this.evSrc);
    this.widgLink = new GralTextField(refPos, "@3.5-3.2++, 1..-1=link-" + this.name, "symbolic link", "t");
    this.widgDir = new GralTextField(refPos, "dir-" + this.name, "directory path", "t");
    this.widgName = new GralTextField(refPos, "@+,1..-9=name-" + this.name, "filename", "t", GralTextField.Type.editable);
    this.widgNameNew = new GralTextField(refPos, "name-" + this.name, "rename | copy to | mkdir/file ", "t", GralTextField.Type.editable);
    this.widgNameNew.specifyActionChange(null, this.actionsetNameToRenameCopy, null);
    this.widgLength = new GralTextField(refPos, "@+, 1..20=length-" + this.name, "file-length", "t");
    this.widgLength.specifyActionChange("ct DirBytes", this.actionBtnCntLen, null);
    this.widgDate = new GralTextField(refPos, "@+,1..18=data-" + this.name, "last modified", "t", GralTextField.Type.editable);
    //    GralColor colorBack = main.gui.gralMng.propertiesGui.colorBackground_;
    GralColor colorText = GralColor.getColor("bk");
    int ii;
    GralColor colorOn = GralColor.getColor("lgn");
    GralColor colorOff = GralColor.getColor("wh");
    GralColor colorDis = GralColor.getColor("gr");
    String textOn = "X", textOff = " ", textDis = "?";
    
    //widgDirectory = main.gralMng.addCheckButton("FileProp:btndir", textOn, textOff, textDis, colorOn, colorOff, colorDis);
    //main.gralMng.addText("directory"); 
    
    //bUnixSystem = true;
    if(bUnixSystem){
//      main.gui.gralMng.setPosition(20, GralPos.size -2, 10, 28, 'd');
//      widGetAllProps = new GralButton("buttonFilePropsGetAll", actionButton, "getAll", null, main.idents.buttonFilePropsGetAll);
//
//      main.gui.gralMng.setPosition(20, GralPos.size -2, 1, GralPos.size +2, 'd');
//      new GralText("rd");
//      widgRd = new GralButton[3];
//      for(ii=0; ii < 3; ++ii){
//        widgRd[ii] = new GralCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
//        //widgReadonly[ii] = main.gralMng.addSwitchButton("FileProp:btnro", "X", " ", GralColor.getColor("wh"), GralColor.getColor("lgn"));
//      }
//      main.gui.gralMng.setPosition(20, GralPos.size -2, 4, GralPos.size +2, 'd');
//      new GralText("wr"); 
//      widgWr = new GralButton[3];
//      for(ii=0; ii < 3; ++ii){
//        widgWr[ii] = new GralCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
//      }
//      main.gui.gralMng.setPosition(20, GralPos.size -2, 7, GralPos.size +2, 'd');
//      new GralText("ex");
//      widgEx = new GralButton[3];
//      for(ii=0; ii < 3; ++ii){
//        widgEx[ii] = new GralCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
//      }
//      main.gui.gralMng.setPosition(22, GralPos.size -2, 10, GralPos.size +6, 'd');
//      new GralText("owner");
//      new GralText("group");
//      new GralText("all");
//      main.gui.gralMng.setPosition(22, GralPos.size -2, 18, GralPos.size +2, 'r', 0.2f);
//      widgUID = new GralCheckButton("FileProp:btnUID", textOn, textOff, textDis, colorOn, colorOff, colorDis);
//      new GralText("UID"); 
//      main.gui.gralMng.setPosition(24, GralPos.size -2, 18, GralPos.size +2, 'r', 0.2f);
//      widgGID = new GralCheckButton("FileProp:btnGID", textOn, textOff, textDis, colorOn, colorOff, colorDis);
//      new GralText("GID"); 
//      main.gui.gralMng.setPosition(26, GralPos.size -2, 18, GralPos.size +2, 'r', 0.2f);
//      widgSticky = new GralCheckButton("FileProp:btnSticky", textOn, textOff, textDis, colorOn, colorOff, colorDis);
//      new GralText("sticky"); 
    } else {
      new GralLabel(refPos, "@23-2,1+2++1=rd-" + this.name, "rd", 1);
      new GralLabel(refPos, "wr");
      new GralLabel(refPos, "ex");
      new GralLabel(refPos, "hidden"); 
      this.widgRd = new GralButton[1];
      this.widgWr = new GralButton[1];
      this.widgEx = new GralButton[1];
      this.widgRd[0] = new GralSwitchButton(refPos, "@25-2,1+2++1=btnro" + this.name, textOff, textOn, textDis, colorOff, colorOn,  colorDis);
      this.widgWr[0] = new GralSwitchButton(refPos, "btnro2" + this.name, textOff, textOn, textDis, colorOff, colorOn, colorDis);
      this.widgEx[0] = new GralSwitchButton(refPos, "btnro3" + this.name, textOff, textOn, textDis, colorOff, colorOn, colorDis);
      this.widgHidden = new GralSwitchButton(refPos, "btnhidden-" + this.name, textOff, textOn, textDis, colorOff, colorOn, colorDis);
    }
    this.widgDelFile = new GralButton(refPos, "@10.3-3++0.5,-8.5..-0.5=btnDel" + this.name, "delete", this.actionButton);
    this.widgCreateDirFile = new GralButton(refPos, "btnCreate" + this.name, "mkdir/ file", this.actionButton);
    this.widgRename =  new GralButton(refPos, "@+,-17..-9++0.5=btnRename" + this.name, "rename", this.actionButton);
    this.widgCopyFile =  new GralButton(refPos, "buttonFilePropsCopy" + this.name, sCmdCopy, this.actionButton);
    this.widgCopyFile.setCmd(sCmdCopy);
    this.widgChgRecurs =   new GralButton(refPos, "@+3.5-3++0.5,-17..-0.5=buttonFilePropsChgRecursive" + this.name, sCmdChgRecurs, this.actionButton);
    this.widgChgRecurs.setCmd(sCmdChgRecurs);
    this.widgChgFile = new GralButton(refPos, "buttonFilePropsChg" + this.name, sCmdChg,  this.actionButton);
    this.widgChgFile.setCmd(sCmdChg);
  }


  /**Builds the content of the file property window. 
   * @param refPosP a position related to the position of the window. It won't be changed.
   * @param sPosName position of the window, maybe relative to given refPosP
   * @param sTitle of the window
   */
  public static GralFileProperties createWindow(GralPos refPosP, String sPosName, String sTitle) { 
    GralPos refPos = new GralPos(refPosP);                 // copy the refPos, it is changed for the window.    
    int windProps = GralWindow_ifc.windConcurrently | GralWindow_ifc.windResizeable;
    GralWindow wind = new GralWindow(refPos, sPosName + "Window", sTitle, windProps);
    wind.pos().setSize(34,40, null);
    wind.setVisible(true);
    GralPanelContent panel = wind.mainPanel;
    String name = panel.name;
    //Note: the refPos describes the whole panel in the window. name is the namePanel without maybe position string
    return new GralFileProperties(refPos, name, wind);
  }
  
  
  public void setActionRefresh(GralUserAction actionRefresh) {
    this.actionRefresh = actionRefresh;
  }
  
  
  /**Opens the view window and fills its content.
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void openDialog(FileRemote src, GralFileSelector fileSelector)
  { //String sSrc, sTrash;
    isVisible = true;
    showFileInfos(src, fileSelector);
    windFileProps.setFocus(); //WindowVisible(true);

  }
  
  
  /**This can be called from outside to show the properties of the given file.
   * No access to the file system is done, all properties are gotten from the FileRemote instance.
   *
   * @param src the file
   */
  public void showFileInfos(FileRemote src, GralFileSelector fileSelector){
    if(this.windFileProps !=null) {
      this.isVisible = this.windFileProps.isVisible();
    }
    if(this.isVisible) { 
      if(this.evChg.isOccupied()){
        this.widgChgFile.setText("abort");
        this.widgChgFile.setCmd(sCmdAbort);
      } else {
        this.actFile = src;
        this.actDir = src.getParentFile();
        this.fileSelector = fileSelector;
        //TODO don't access the file system without user activity!!! need: a refresh button!!
        this.widgChgFile.setText("change file");
        this.widgChgRecurs.setText("change recursive");
        this.widgCopyFile.setText("copy file");
        this.widgName.setText(src.getName());
        this.widgName.setBackColor(this.colorUnchanged, -1);
        this.widgDir.setText(src.getParent());
        this.sDateOriginal = this.formatDate.format(new Date(src.lastModified()));
        this.widgDate.setText(this.sDateOriginal);
        this.widgDate.setBackColor(this.colorUnchanged, -1);
        String sLength;
        long length = src.length();
        if(length==0 && src.isDirectory()) {
          sLength = "Enter / Mouse to count";
        } else {
          sLength = "" + length;
        }
        if(length >= 10000 && length < 10000000){
          sLength += " = " + length/1000 + "k";
        } else if( length >= 10000000){
          sLength += " = " + length/1000000 + "M";
        }
        this.widgLength.setText(sLength);
        if(src.isSymbolicLink()){
          this.widgLink.setText(FileFunctions.getCanonicalPath(src));
        } else {
          this.widgLink.setText("");
        }
        this.nFlagsOriginal = src.getFlagsTested();
        
        this.widgRd[0].setState((this.nFlagsOriginal & FileRemote.mCanRead)!=0 ? GralButton.State.On : GralButton.State.Off);
        this.widgEx[0].setState((this.nFlagsOriginal & FileRemote.mExecute)!=0 ? GralButton.State.On : GralButton.State.Off);
        this.widgWr[0].setState((this.nFlagsOriginal & FileRemote.mCanWrite)!=0 ? GralButton.State.On : GralButton.State.Off);
        if(this.bUnixSystem){
          this.widgRd[1].setState(GralButton.State.Disabled);
          this.widgRd[2].setState(GralButton.State.Disabled);
          this.widgWr[1].setState(GralButton.State.Disabled);
          this.widgWr[2].setState(GralButton.State.Disabled);
          this.widgEx[1].setState(GralButton.State.Disabled);
          this.widgEx[2].setState(GralButton.State.Disabled);
          this.widgSticky.setState(GralButton.State.Disabled);
          this.widgUID.setState(GralButton.State.Disabled);
          this.widgGID.setState(GralButton.State.Disabled);
        }
        this.widgHidden.setState((this.nFlagsOriginal & FileRemote.mHidden)!=0 ? GralButton.State.On : GralButton.State.Off);
        //widgDirectory.setState(src.isDirectory() ? GralButton.State.On : GralButton.State.Off);
        setRename();
      }
    }
  }
  
  
  void setRename() {
    String name = GralFileProperties.this.widgName.getText();
    String nameNew = GralFileProperties.this.widgNameNew.getText();
    int pos1 = nameNew.indexOf('*');
    int posDot = nameNew.lastIndexOf('.');
    final String sRename;
    if(pos1 >= 0) {                                        // found *
      if(pos1 ==0 && posDot == 1) { // *.ext
        posDot = name.lastIndexOf('.');
        if(posDot >0) {
          sRename = name.substring(0, posDot+1) + nameNew.substring(2);
        } else {
          sRename = name + nameNew.substring(1);
        }
      } else {
        String name1 = posDot >=0 ? name.substring(0, posDot) : name;  // use name or name.ext
        sRename = nameNew.substring(0, pos1) + name1 + nameNew.substring(pos1+1); 
      }
      this.widgNameNew.setBackColor(GralFileProperties.this.colorChanged, -1);;
    } else if(nameNew.length()>0) {
      sRename = nameNew;
      this.widgNameNew.setBackColor(GralFileProperties.this.colorChanged, -1);;
    } else {
      sRename = null;   // dont change the name
    }
    this.sNameNew = sRename;
    if(sRename !=null && !this.actFile.isSymbolicLink()) {
      this.widgLink.setText("=>" + sRename);
    }
  }
  
  
  boolean doActionButtons ( GralWidget btn ) {
    int noMask = 0;
    boolean bAbort = false;
    if(btn.sCmd !=null && btn.sCmd.equals(sCmdAbort)){
      if(this.evChg.occupy(this.evSrc, this.callbackChgProps, null, true)){
        this.widgChgFile.setText(this.buttonFilePropsChg);
        btn.sCmd = sCmdChg;
      } else {
        System.err.println("chg properties hangs");
      }
    } else if(btn == this.widgChgFile || btn == this.widgChgRecurs) {
      String sDate = GralFileProperties.this.widgDate.getText();
      long date;
      if(sDate.equals(this.sDateOriginal)) { date = 0; }      // date is not changed
      else {
        try{ 
          Date date1 = GralFileProperties.this.formatDate.parse(sDate);
          date = date1.getTime();
        } catch(ParseException exc){
          date = 0;
          this.widgDate.setBackColor(this.colorWrong, -1);
        }
      }
      int flags = this.nFlagsOriginal;
      switch(GralFileProperties.this.widgRd[0].getState()){
        case Off:       flags &= ~FileRemote.mCanRead; break;
        case On:       flags |= FileRemote.mCanRead; break;
      }
      switch(GralFileProperties.this.widgWr[0].getState()){
        case Off:       flags &= ~FileRemote.mCanWrite; break;
        case On:       flags |= FileRemote.mCanWrite; break;
      }
      switch(GralFileProperties.this.widgEx[0].getState()){
        case Off:       flags &= ~FileRemote.mExecute; break;
        case On:       flags |= FileRemote.mExecute; break;
      }
      if(GralFileProperties.this.bUnixSystem){
        switch(GralFileProperties.this.widgRd[1].getState()){
          case Off:       flags &= ~FileRemote.mCanReadGrp; break;
          case On:       flags |= FileRemote.mCanReadGrp; break;
        }
        switch(GralFileProperties.this.widgWr[1].getState()){
          case Off:       flags &= ~FileRemote.mCanWriteGrp; break;
          case On:       flags |= FileRemote.mCanWriteGrp; break;
        }
        switch(this.widgEx[1].getState()){
          case Off:       flags &= ~FileRemote.mExecuteGrp; break;
          case On:       flags |= FileRemote.mExecuteGrp; break;
        }
        switch(this.widgRd[2].getState()){
          case Off:       flags &= ~FileRemote.mCanReadAny; break;
          case On:       flags |= FileRemote.mCanReadAny; break;
        }
        switch(this.widgWr[2].getState()){
          case Off:       flags &= ~FileRemote.mCanWriteAny; break;
          case On:       flags |= FileRemote.mCanWriteAny; break;
        }
        switch(this.widgEx[2].getState()){
          case Off:       flags &= ~FileRemote.mExecuteAny; break;
          case On:       flags |= FileRemote.mExecuteAny; break;
        }
      }
      switch(this.widgHidden.getState()){
        case Off:       flags &= ~FileRemote.mHidden; break;
        case On:        flags |=  FileRemote.mHidden; break;
      }
      if(btn.sCmd.equals(sCmdChg)){
        if(this.evChg.occupy(this.evSrc, this.callbackChgProps, null, true)){
          //cmds with callback
          this.widgChgFile.setText(this.buttonFilePropsChanging);
          int mask = (flags ^ this.nFlagsOriginal);
          this.actFile.chgProps(this.sNameNew, mask, flags, date, this.evChg);
          //TODO main.refreshFilePanel(actFile.getParentFile());  //refresh the panel if the directory is shown there
        } else { bAbort = true; }
        //
      } else if(btn.sCmd.equals(sCmdChgRecurs)){
        if(this.evChg.occupy(this.evSrc, this.callbackChgProps, null, true)){
          //cmds with callback
          this.widgChgRecurs.setText(this.buttonFilePropsChanging);
          int mask = ~(flags ^ this.nFlagsOriginal);
          this.actFile.chgPropsRecursive(mask, flags, date, this.evChg);
        } else { bAbort = true; }
        //
      }
    }
    else if(btn == this.widgCopyFile){
      if(this.evChg.occupy(this.evSrc, this.callbackChgProps, null, true)){
        if(name !=null && !name.equals(this.actFile.getName())){
          this.widgCopyFile.setText(this.buttonFilePropsCopying);
          FileRemote fileNew = this.actFile.getParentFile().child(name);
          this.actFile.copyTo(fileNew, this.evChg, FileRemote.modeCopyReadOnlyOverwrite | FileRemote.modeCopyCreateYes | FileRemote.modeCopyExistAll);
        } else {
          this.widgCopyFile.setText("copy - name?");
        }
      } else { bAbort = true; }
    }
    else if(btn == this.widgRename){
      if(this.evChg.occupy(this.evSrc, this.callbackChgProps, null, true)){
        if(name !=null && !name.equals(this.actFile.getName())){
          this.widgCopyFile.setText(this.buttonFilePropsCopying);
          this.widgName.setBackColor(this.colorGrayed, -1);
          this.actFile.chgProps(this.sNameNew, 0, 0, 0, this.evChg);
        } else {
          this.widgCopyFile.setText("rename ?");
        }
      } else { bAbort = true; }
    }
    else if(btn == this.widgDelFile){
      if(this.evChg.occupy(this.evSrc, this.callbackChgProps, null, true)){
        this.widgName.setBackColor(this.colorGrayed, -1);
        this.actFile.delete(this.evChg);
      } else { bAbort = true; }
    }
    else if(btn == this.widgCreateDirFile){
      if(this.evChg.occupy(this.evSrc, this.callbackChgProps, null, true)){
        if(name !=null && !name.equals(this.actFile.getName())){
          this.widgCopyFile.setText(this.buttonFilePropsCopying);
          FileRemote fileNew = this.actFile.getParentFile().child(name);
          this.actFile.copyTo(fileNew, this.evChg, FileRemote.modeCopyReadOnlyOverwrite | FileRemote.modeCopyCreateYes | FileRemote.modeCopyExistAll);
        } else {
          this.widgCopyFile.setText("copy - name?");
        }
      } else { bAbort = true; }
    }
    if(bAbort){
      this.widgChgFile.setText(this.buttonFilePropsAbort);
      this.widgChgFile.sCmd = sCmdAbort;
    }
    return true;
  }

  EventSource evSrc = new EventSource("FcmdFileProps"){
    @Override public void notifyDequeued(){
      
    }
    @Override public void notifyConsumed(int ctConsumed){}
    @Override public void notifyRelinquished(int ctConsumed){}
  };



  /**Action for all buttons of 'file properties' window: 
   */
  GralUserAction actionButton = new GralUserAction("actionBtnCntLen")
  {
    @Override public boolean userActionGui(int keyCode, GralWidget btn, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){  //not on mouse down but on mouse up.
        return doActionButtons( btn );
      }
      return false;
      // /
    }
  };



  GralUserAction actionInvisible = new GralUserAction("actionInvisible")
  { @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { GralFileProperties.this.isVisible = false;
      return true;
    }
  };

  


  EventConsumer callbackChgProps = new EventConsumer() { 
    @Override public int processEvent(EventObject evP) { 
      FileRemote.CallbackEvent ev = (FileRemote.CallbackEvent)evP;  // type adaption
      if(ev.getCmd() == FileRemote.CallbackCmd.done){
        FileRemote newFile = ev.getFileSrc();
        if(newFile == GralFileProperties.this.actFile && newFile.exists()) {   // file itself is not changed (not renamed, not deleted)
        //if(GralFileProperties.this.actFile.exists()) {
          showFileInfos(GralFileProperties.this.actFile, GralFileProperties.this.fileSelector);
          GralFileProperties.this.widgChgFile.setText(GralFileProperties.this.buttonFilePropsOk);
          GralFileProperties.this.fileSelector.showFile(GralFileProperties.this.actFile);
        }
        else {               // file is deleted or renamed
          FileRemote fileShow = newFile == null ? GralFileProperties.this.actDir : newFile;
          GralFileProperties.this.fileSelector.forcefillIn(fileShow, false);
        }
        if(false && GralFileProperties.this.actionRefresh !=null) {
          GralFileProperties.this.actionRefresh.exec(KeyCode.shiftAlt, GralFileProperties.this.windFileProps);
        }
        String sNewName = GralFileProperties.this.widgNameNew.getText();
        if(sNewName.indexOf('*') <0) {
          GralFileProperties.this.widgNameNew.setText("");
          GralFileProperties.this.widgNameNew.setBackColor(GralFileProperties.this.colorUnchanged, -1);;
        }
        GralFileProperties.this.fileSelector.setFocus();   // focus back to GralFileSelector 
      } else {
        GralFileProperties.this.widgChgFile.setText(GralFileProperties.this.buttonFilePropsRetry);
      }
      ev.relinquish();
      if(GralFileProperties.this.actionRefresh !=null) {
        GralFileProperties.this.actionRefresh.exec(KeyCode.shiftAlt, GralFileProperties.this.windFileProps);
      }
      return 1;
    } 
  
     @Override public String toString(){ return "FcmdFileProps-callbackChgProps"; }

  };


  
  
  /**Action for Key F2 for view command. 
   */
  GralUserAction actionBtnCntLen = new GralUserAction("actionBtnCntLen")
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        GralFileProperties.this.widgLength.setText("counting ...");
        if( 0 != GralFileProperties.this.evCntLen.occupyRecall(100, GralFileProperties.this.evSrc, GralFileProperties.this.callbackCntLen, null, true)
         && GralFileProperties.this.actFile !=null 
          ){
          GralFileProperties.this.actFile.countAllFileLength(GralFileProperties.this.evCntLen);
        }
      }
      return true;
  } };
  
  final EventConsumer callbackCntLen = new EventConsumer()
  { @Override public int processEvent(EventObject evP)
    { FileRemote.CallbackEvent ev = (FileRemote.CallbackEvent)evP;
      if(ev.getCmd() == FileRemote.CallbackCmd.done){
        String sLen = "" + ev.nrofBytesAll;
        GralFileProperties.this.widgLength.setText(sLen);
      } else {
        GralFileProperties.this.widgLength.setText("error count bytes");
      }
      ev.relinquish();
      return 1;
    } 
  
    @Override public String toString(){ return "FcmdFileProps - callback cnt length"; }

  };


  final GralUserAction actionsetNameToRenameCopy = new GralUserAction("actionsetNameToRenameCopy") {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params) {
      String sText = GralFileProperties.this.widgNameNew.getText();
      if( (keyCode == KeyCode.enter || keyCode == KeyCode.mouse2Double) && sText.length()==0  || keyCode == (KeyCode.ctrl + 'n') ) {
        GralFileProperties.this.widgNameNew.setText(GralFileProperties.this.widgName.getText());
      }
      else if( keyCode == KeyCode.enter || keyCode == KeyCode.focusLost) {
        setRename();
      }
      return true;
  } };
  
  
  
  
  @Override public void setFocus () {
    // TODO Auto-generated method stub
    
  }

  @Override public void setFocus ( int delay, int latest ) {
    // TODO Auto-generated method stub
    
  }

  @Override public boolean isInFocus () {
    // TODO Auto-generated method stub
    return false;
  }

  @Override public boolean isVisible () {
    return this.isVisible;
  }

  @Override public void setFocusedWidget ( GralWidgetBase_ifc widg ) {
    // TODO Auto-generated method stub
    
  }

  @Override public GralWidgetBase_ifc getFocusedWidget () {
    return null;
  }

  @Override public boolean setVisible ( boolean visible ) {
    this.isVisible = visible;
    if(this.windFileProps !=null) {
      this.windFileProps.setVisible(visible);
    } else {
      this.panel.setVisible(visible);
    }
    return true;
  }

  @Override public boolean createImplWidget_Gthread () throws IllegalStateException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override public void removeImplWidget_Gthread () {
    // TODO Auto-generated method stub
    
  }

}
