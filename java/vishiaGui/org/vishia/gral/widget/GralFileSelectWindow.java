package org.vishia.gral.widget;

import java.io.File;

import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFileDialog_ifc;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.KeyCode;


/**A window for search-in-file dialogue.
 * It is instantiated calling {@link GralFileSelector#createWindowConfirmSearchGthread(GralMngBuild_ifc)}.
 * The user can invoke {@link #confirmSearchInFiles(GralFileSelector, Appendable)} to open that window. 
 *
 */
public class GralFileSelectWindow implements GralFileDialog_ifc
{
  /**Version, history and license. The version number is a date written as yyyymmdd as decimal number.
   * Changes:
   * <ul>
   * <li>2022-12-11 Hartmut new {@link #closeDialog()} systematically because {@link #openDialog(FileRemote, String, boolean, GralUserAction)} is given. 
   * <li>2013-03-28 Hartmut implements {@link GralFileDialog_ifc} because it should be so. TODO tuning with the
   *   other possibility {@link org.vishia.gral.swt.SwtFileDialog} which uses the standard file dialog of windows or linux
   * <li>2013-03-28 Hartmut creating, it was a static inner class of {@link GralFileSelector}
   * </ul>
   * <br><br> 
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
  //@SuppressWarnings("hiding")
  public final static int version = 20130328;

  
  private final GralWindow_ifc wind;

  public final GralFileSelector fileSelector;
  
  private final GralTextField widgFilename;
      
  private final GralButton widgButtonOk;
  
  
  GralUserAction actionOkForUser;
  
  private File dir;
  
  /**Creates the window to confirm search in files. This window can be created only one time
   * for all file panels, if the application has more as one. On activating the directory
   * and the file panel to show results should be given. But only one search process can be run
   * simultaneously.
   * @return The created window.
   */
   public GralFileSelectWindow(String name, GralMng mng){
     mng.selectPanel("primaryWindow");
     mng.setPosition(-24, 0, -67, 0, 1, 'r'); //right buttom, about half less display width and hight.
     this.wind = mng.createWindow(name + "Window", "select file", GralWindow_ifc.windExclusive | GralWindow_ifc.windResizeable );
     //mng.setPosition(0, -3, 0, 0, 0, 'd', 0.0f);
     String posName = "@0..-3,0..0=" + name;
     this.fileSelector = new GralFileSelector(mng.refPos(), posName + "-selelector", 100, new int[]{2,0,-6,-12}, true);
     //fileSelector.setToPanel(mng);
     this.fileSelector.specifyActionOnFileSelected(this.actionSelectFile);
     this.fileSelector.setActionOnEnterFile(this.actionOk);
     String posText = "@-3..0, 0..-9=" + name +"-fname";
     this.widgFilename = mng.addTextField(posText, true, null, null);
     mng.setPosition(-3, 0, -8, GralPos.size + 7, 0, 'r',1);
     String posBtnOk = "@-3..0, -8..-1=" + name + "-ok";
     this.widgButtonOk = new GralButton(mng.refPos(), posBtnOk, "Ok", this.actionOk);
     
   }
   
   
  
  
  /**Prepares the action and appearance and shows the window.
   * The param actionSelect is called if the user presses the Button on right bottom. The button is marked with
   * "select" or "write" depending on the param bForWrite. For actionSelect the method 
   * {@link GralUserAction#exec(int, GralWidget_ifc, Object...)} is called with null for the widget (a widget should not
   *   necessary for the user) but with the selected File(s) as Object parameter. 
   *   If one file is selected the first Object is instanceof File. If more as one files are selected
   *   a List<File> or File[] is provided. The user should check this type.
   *    
   * @param startDir
   * @param sTitle shown in title bar of the window
   * @param bForWrite true then the name field is editable and "Save" is shown on button. False then the name Field
   *   is readonly and "select" is shown on button
   * @param actionSelect Action which is called on Button ok. The first Object of exec(...,object) is the selected File
   *   or a File[] or List<File> if more as one file is selected. 
   */
  public void openDialog(FileRemote startDir, String sTitle, boolean bForWrite, GralUserAction actionSelect){
    this.wind.setTitle(sTitle);
    this.actionOkForUser = actionSelect;
    if(bForWrite){
      this.widgButtonOk.setText("write");
    } else {
      this.widgButtonOk.setText("select");
    }
    this.fileSelector.fillIn(startDir, false);
    this.wind.setWindowVisible(true);
    this.fileSelector.setFocus();
  }
  
  
  /**Set the window invisible, after select.
   * 
   */
  public void closeDialog ( ) {
    this.wind.setWindowVisible(false);
  }
  
  @Override
  public boolean open(String sTitle, int mode)
  {
    // TODO Auto-generated method stub
    return false;
  }




  @Override
  public String show(String sBaseDir, String sLocalDir, String sMask, String sTitle)
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  
  @Override
  public String[] getMultiSelection()
  {
    // TODO Auto-generated method stub
    return null;
  }




  @Override
  public String getSelection()
  {
    // TODO Auto-generated method stub
    return null;
  }


  
  public void closeWindow(){ 
    wind.closeWindow();
  }
  
  
  /**Action of OK button or enter button. It returns the file in the shown directory
   * with the name written in the widgFilename-textfield.
   * It is not the selected file. But the textfield is filled with the name of the selected file 
   * if a file was selected in the table.
   * 
   */
  GralUserAction actionOk = new GralUserAction("GralFileSelector-actionOk"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){  //supress both mouse up and down reaction
        FileRemote dir = fileSelector.getCurrentDir();
        String sFilename = widgFilename.getText();
        FileRemote file;
        if(sFilename.length()==0 || sFilename.equals("..")){
          file = dir;
        } else {
          file = dir.child(sFilename);
        }
        actionOkForUser.exec(KeyCode.menuEntered, null, file); 
      }
      return true;
    }
  };



  GralUserAction actionSelectFile = new GralUserAction("GralFileSelector-actionSelectFile"){
    /**The action called from {@link GralTable}.
     * @param params [0] is the Table line. The content of table cells are known here,
     *   because it is the file table itself. The {@link GralTableLine_ifc#getUserData()}
     *   returns the {@link FileRemote} file Object.
     * @see org.vishia.gral.ifc.GralUserAction#userActionGui(int, org.vishia.gral.base.GralWidget, java.lang.Object[])
     */
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      GralTableLine_ifc line = (GralTableLine_ifc) params[0];
      String sFileCell = line.getCellText(GralFileSelector.kColFilename);
      Object oData = line.getUserData();
      if(false && oData instanceof File){
        String sName = ((File)oData).getName();
        widgFilename.setText(sName);
      } else {
        widgFilename.setText(sFileCell);
      }
      return true;
    }
  };




}
