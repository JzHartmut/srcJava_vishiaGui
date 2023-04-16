package org.vishia.gral.base;

import java.util.List;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GralWidget.ImplAccess;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralPanel_ifc;
import org.vishia.gral.ifc.GralWidgetBase_ifc;

/**Dummy implementation of the Screen as top level panel.
 * Only to prevent a null pointer. May be used more elaborately in future
 * @author Hartmut Schorrig, LPGL license.
 * @since 2022-08
 *
 */
public class GralScreen implements GralPanel_ifc {

  private final GralMng gralMng;
  
  
  private GralWindow focusedWindow;
  
  public GralScreen(GralMng gralMng) {
    this.gralMng = gralMng;
  }

  @Override public String getName () {
    return "screen";
  }

  @Override public String getDataPath () {
    return null;
  }

  @Override public void setCmd ( String cmd ) {
  }

  @Override public String getCmd () {
    return null;
  }

  @Override public void setData ( Object data ) {
  }

  @Override public Object getData () {
    return null;
  }

  @Override public ActionChange getActionChange ( ActionChangeWhen when ) {
    return null;
  }


  @Override public GralMng gralMng () {
    return this.gralMng;
  }

  @Override public void setFocus () {
    this.focusedWindow.setFocus();
  }

  @Override public void setFocus ( int delay, int latest ) {
    this.focusedWindow.setFocus(delay, latest);
  }

  @Override public boolean isInFocus () {
    return false;
  }

  /**This operation can be called from the implementation graphic if the focus is gained for an implementation widget.
   * It is also called for all parents of a widget if a child gets the focus.
   * The specific widget implementation can override this operation to do specifics.
   * It should call super.focused(bGained) to forward the focus to parents.
   * @param bGained
   */
  @Override public void focused(boolean bGained) {
    // does nothing here.
  }

  @Override public boolean isGraphicDisposed () {
    return false;
  }

  @Override public boolean setVisible ( boolean visible ) {
    return false;
  }

  @Override public boolean isVisible () {
    return false;
  }

  @Override public void setEditable ( boolean editable ) {
  }

  @Override public boolean isEditable () {
    return false;
  }

  @Override public boolean isNotEditableOrShouldInitialize () {
    return false;
  }

  @Override public GralColor setBackgroundColor ( GralColor color ) {
    return null;
  }

  @Override public GralColor setForegroundColor (
      GralColor color ) {
    return null;
  }

  @Override public void setBackColor ( GralColor color,
      int ix ) {
    
  }

  @Override public GralColor getBackColor ( int ix ) {
 
    return null;
  }

  @Override public void setLineColor ( GralColor color,
      int ix ) {
 
    
  }

  @Override public void setTextColor ( GralColor color ) {
 
    
  }

  @Override public void setText ( CharSequence text ) {
 
    
  }

  @Override public void setHtmlHelp ( String url ) {
 
    
  }

  @Override public ImplAccess getImplAccess () {
 
    return null;
  }

  @Override public void redraw () {
 
    
  }

  @Override public void redraw ( int delay, int latest ) {
 
    
  }

  @Override public void setBoundsPixel ( int x, int y,
      int dx, int dy ) {
 
    
  }

  @Override public void setDataPath ( String sDataPath ) {
 
    
  }

  @Override public boolean isChanged (
      boolean setUnchanged ) {
 
    return false;
  }

  @Override public long setContentIdent ( long ident ) {
 
    return 0;
  }

  @Override public long getContentIdent () {
 
    return 0;
  }

  @Override public Object getContentInfo () {
 
    return null;
  }

  @Override public void refreshFromVariable (
      VariableContainer_ifc container ) {
 
    
  }

  @Override public void refreshFromVariable (
      VariableContainer_ifc container, long timeAtleast,
      GralColor colorRefreshed, GralColor colorOld ) {
 
    
  }

  @Override public boolean remove () {
 
    return false;
  }

  @Override public void setFocusedWidget ( GralWidgetBase_ifc widg ) {
    assert(widg instanceof GralWindow);
    this.focusedWindow = (GralWindow) widg;
  }

  @Override public GralWidgetBase_ifc getFocusedWidget() { 
    return this.focusedWindow;
  }


  
  
  @Override public GralWidget getPanelWidget () {
 
    return null;
  }

  @Override public List<GralWidgetBase> getWidgetList () {
 
    return null;
  }

  @Override public GralPos pos () {
 
    return null;
  }

  @Override public GralCanvasStorage canvas () {
 
    return null;
  }

  @Override public void removeWidget ( GralWidget widg ) {
 
    
  }

 
  @Override public Object getImplWidget () {
    // TODO Auto-generated method stub
    return null;
  }

  
}
