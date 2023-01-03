package org.vishia.gral.swing;

import java.io.IOException;
import java.io.InputStream;

import org.vishia.gral.base.GralCurveView;
import org.vishia.gral.base.GralCurveView.CommonCurve;
import org.vishia.gral.base.GralHtmlBox;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFileDialog_ifc;
import org.vishia.gral.ifc.GralPanel_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.msgDispatch.LogMessage;

public class SwingMng  extends GralMng.ImplAccess {

  /**Creates an instance.
   * @param guiContainer The container where the elements are stored in.
   * @param width in display-units for the window's width, the number of pixel depends from param displaySize.
   * @param height in display-units for the window's height, the number of pixel depends from param displaySize.
   * @param displaySize character 'A' to 'E' to determine the size of the content 
   *        (font size, pixel per cell). 'A' is the smallest, 'E' the largest size. Default: use 'C'.
   */
  protected SwingMng(GralMng gralMng
  , char displaySize//, VariableContainer_ifc variableContainer
  , LogMessage log) { 
    super(gralMng);
    super.sizeCharProperties = displaySize;
        //pos().x.p1 = 0; //start-position
    //pos().y.p1 = 4 * propertiesGui.yPixelUnit();

    
    //displaySwt.addFilter(SWT.KeyDown, mainKeyListener);
    
    startThread();

  }


  
  
  @Override public Object getCurrentPanel () {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public void createImplWidget_Gthread ( GralWidget widgg ) {
    // TODO Auto-generated method stub
    
  }

  @Override protected GralMenu createContextMenu ( GralWidget widgg ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public boolean remove ( GralPanel_ifc compositeBox ) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override protected GralMenu createMenuBar ( GralWindow windg ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public GralWindow createWindow ( String name, String title, int windProps ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override protected void createSubWindow ( GralWindow windowGral ) throws IOException {
    // TODO Auto-generated method stub
    
  }

   @Override public GralWidget addText ( String sText, char size, int color ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public GralHtmlBox addHtmlBox ( String name ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public Object addImage ( String sName, InputStream imageStream, int height, int width, String sCmd ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public GralWidget addSlider ( String sName, GralUserAction action, String sShowMethod, String sDataPath ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public GralCurveView addCurveViewY ( String sName, int nrofXvalues, CommonCurve common ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public GralWidget addFocusAction ( String sName, GralUserAction action, String sCmdEnter, String sCmdRelease ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public void addFocusAction ( GralWidget widgetInfo, GralUserAction action, String sCmdEnter, String sCmdRelease ) {
    // TODO Auto-generated method stub
    
  }

  @Override public GralTable addTable ( String sName, int height, int[] columnWidths ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public GralFileDialog_ifc createFileDialog () {
    // TODO Auto-generated method stub
    return null;
  }

  @Override protected GralMenu XXXaddPopupMenu ( String sName ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public GralRectangle calcWidgetPosAndSize ( GralPos pos, int widthwidgetNat, int heigthWidgetNat ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public String getValueFromWidget ( GralWidget widgd ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public Object getColorImpl ( GralColor color ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override protected void redrawWidget ( String sName ) {
    // TODO Auto-generated method stub
    
  }

  @Override protected void resizeWidget ( GralWidget widgd, int xSizeParent, int ySizeParent ) {
    // TODO Auto-generated method stub
    
  }

  @Override protected void setSampleCurveViewY ( String sName, float[] values ) {
    // TODO Auto-generated method stub
    
  }

  @Override protected boolean showContextMenuGthread ( GralWidget widg ) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override public void reportContent ( Appendable out ) throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override public void finishInit () {
    // TODO Auto-generated method stub
    
  }

  @Override protected void initGraphic () {
    // TODO Auto-generated method stub
    
  }

  @Override protected void closeImplGraphic () {
    // TODO Auto-generated method stub
    
  }

  @Override protected boolean dispatchOsEvents () {
    // TODO Auto-generated method stub
    return false;
  }

  @Override protected void graphicThreadSleep () {
    // TODO Auto-generated method stub
    
  }

  @Override public void wakeup () {
    // TODO Auto-generated method stub
    
  }

  @Override protected boolean XXXsetWindowsVisible ( GralWindow_ifc window, GralPos atPos ) {
    // TODO Auto-generated method stub
    return false;
  }

}
