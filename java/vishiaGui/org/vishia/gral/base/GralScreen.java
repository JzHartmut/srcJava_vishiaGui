package org.vishia.gral.base;

import java.util.List;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GralWidget.ImplAccess;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralPanel_ifc;

/**Dummy implementation of the Screen as top level panel.
 * Only to prevent a null pointer. May be used more elaborately in future
 * @author Hartmut Schorrig, LPGL license.
 * @since 2022-08
 *
 */
public class GralScreen implements GralPanel_ifc {

  private final GralMng gralMng;
  
  
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

  @Override public void setVisibleStateWidget ( boolean visible ) {
    
  }

  @Override public void setFocus () {
  }

  @Override public void setFocus ( int delay, int latest ) {
  }

  @Override public boolean isInFocus () {
    return false;
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

  @Override public void repaint () {
 
    
  }

  @Override public void repaint ( int delay, int latest ) {
 
    
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

  @Override public void setPrimaryWidget (
      GralWidget widg ) {
 
    
  }

  @Override public GralWidget getPanelWidget () {
 
    return null;
  }

  @Override public List<GralWidget> getWidgetList () {
 
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
