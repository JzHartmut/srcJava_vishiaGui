package org.vishia.commander;

import java.io.File;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.KeyCode;

public class FcmdStatus
{
  /**Version, history and license
   * <ul>
   * <li>2012-10-27 Hartmut created
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
  public static final int version = 20121027;
  
  protected final Fcmd main;

  
  GralWindow_ifc windStatus;
  
  GralButton widgCopy, widgEsc;
  
  
  public FcmdStatus(Fcmd main)
  { this.main = main;
  }


  
  /**Builds the content of the file property window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindow()
  { main._gralMng.selectPanel("primaryWindow");
    main._gralMng.setPosition(-30, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    int windProps = GralWindow.windConcurrently;
    GralWindow window =  main._gralMng.createWindow("windStatus", "Status - The.file.Commander", windProps);
    windStatus = window; 
    main._gralMng.setPosition(3.5f, GralPos.size -3, 1, GralPos.size +5, 0, 'd');
    widgCopy = main._gralMng.addButton("sCopy", main.copyCmd.actionConfirmCopy, "copy");
    widgEsc = main._gralMng.addButton("dirBytes", actionButton, "esc");
  }

  /**Opens the view window and fills its content.
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void openDialog()
  {
    if(main.copyCmd.listEvCopy.size() >0){
      widgCopy.setBackColor(GralColor.getColor("rd"), 0);
    } else {
      widgCopy.setBackColor(GralColor.getColor("wh"), 0);
    }
    
    windStatus.setWindowVisible(true);

  }
  
  
  /**Action for OK. 
   */
  GralUserAction actionButton = new GralUserAction("actionButton")
  {
    @Override public boolean exec(int keyCode, GralWidget_ifc widg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        if(widg == widgEsc){
          windStatus.closeWindow();
        }
      }
      return true;
  } };

  
  /**Action for Key F2 for view command. 
   */
  GralUserAction actionOpenDialog = new GralUserAction("actionOpenDialog")
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        openDialog();
      }
      return true;
    }
  };


  
}
