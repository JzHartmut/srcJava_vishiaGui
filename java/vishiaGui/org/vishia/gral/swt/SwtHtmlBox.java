package org.vishia.gral.swt;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GralHtmlBox;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPanel_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;

public class SwtHtmlBox extends GralHtmlBox.ImplAccess
{
  
  
  /**Version, history and license.
   * <ul>
   * <li>2012-07-13 Hartmut chg: It is registered in the mng yet, therefore resize works.
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
  public static final int version = 20120713;

  Browser boxSwt;
  
  String lastUrl="no-help", lastUrlOk ="no-help";
  
  public boolean bActiv;
  
  public SwtHtmlBox(GralHtmlBox box, SwtMng mngSwt)
  { super(box);
    //SwtMng mngSwt = (SwtMng)(GralMng.get()).impl;
    GralWidgetBase_ifc panel = widgg.pos().parent;
    Composite parent;
    if(panel == null){
      parent = mngSwt.displaySwt.getActiveShell();
    } else {
      parent = (Composite)(panel.getImplAccess().getWidgetImplementation());
      //parent = ((SwtPanel)panel.getWidgetImplementation()).panelComposite; //(Composite)(panel.getPanelImpl());
    }
    try {
      this.boxSwt = new Browser(parent,0);
      super.wdgimpl = this.boxSwt;
      //mng.registerWidget(this);
      mngSwt.setPosAndSizeSwt(box.pos(), boxSwt, 0, 0);
    } catch(Throwable exc){
      System.err.println("can't create SWT-Browser");
      exc.printStackTrace(System.err);
    }
    bActiv = false;
  }

  
  static SwtHtmlBox createHtmlBox(GralHtmlBox widgg, SwtMng mng){
    return new SwtHtmlBox(widgg, mng);
    //
  }

  
  
  
  @Override public void setUrl(String url){
    //this.w
    lastUrl = url;
    if(bActiv){
      boolean bOk = boxSwt.setUrl(url);
      if(bOk){
        lastUrlOk = url;
      } else {
        boxSwt.setUrl(lastUrlOk);
      }
    }
  }

  
  
  @Override public void activate(){
    if(boxSwt !=null){
      bActiv = true;
      setUrl(lastUrl);
    }
  }
  

  
  
  @Override
  public void removeWidgetImplementation()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean setFocusGThread()
  {
    // TODO Auto-generated method stub
    return false;
  }
  
  @Override public void setVisibleGThread(boolean bVisible) { super.setVisibleState(bVisible); boxSwt.setVisible(bVisible); }


  @Override public void redrawGthread(){
    if(boxSwt !=null){ boxSwt.redraw(); }
  }


  
  @Override public Object getWidgetImplementation(){ return boxSwt; }
  
  
  


  @Override
  public void setBoundsPixel(int x, int y, int dx, int dy)
  {
    // TODO Auto-generated method stub
    
  }

  @Override public GralRectangle getPixelPositionSize()
  {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public void updateValuesForAction() {
    // TODO Auto-generated method stub
    
  }


  
  

}
