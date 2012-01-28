package org.vishia.gral.awt;

import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Font;
import java.awt.Color;
import java.awt.Label;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TextArea;
import java.awt.TextField;
import java.io.InputStream;
import java.util.Set;
import java.util.Map.Entry;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralDispatchCallbackWorker;
import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralHtmlBox;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralPanelActivated_ifc;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFileDialog_ifc;
import org.vishia.gral.ifc.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralPos;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.swt.SwtMenu;
import org.vishia.gral.widget.GralValueBar;
import org.vishia.msgDispatch.LogMessage;

public class AwtWidgetMng extends GralWidgetMng implements GralGridBuild_ifc, GralPanelMngWorking_ifc
{
  
  final AwtProperties propertiesGuiAwt; 
  
  /**Creates an instance.
   * @param guiContainer The container where the elements are stored in.
   * @param width in display-units for the window's width, the number of pixel depends from param displaySize.
   * @param height in display-units for the window's height, the number of pixel depends from param displaySize.
   * @param displaySize character 'A' to 'E' to determine the size of the content 
   *        (font size, pixel per cell). 'A' is the smallest, 'E' the largest size. Default: use 'C'.
   */
  public AwtWidgetMng(GralGraphicThread device, AwtProperties propertiesGui
    , VariableContainer_ifc variableContainer
    , LogMessage log
    )
  { super(device, propertiesGui, variableContainer, log);
    this.propertiesGuiAwt = propertiesGui;
  }


  
  /** Adds a text field for showing or editing a text value.
   * 
   * @param sName The registering name
   * @param width Number of grid units for length
   * @param editable true than edit-able, false to show content 
   * @param prompt If not null, than a description label is shown
   * @param promptStylePosition Position and size of description label:
   *   upper case letter: normal font, lower case letter: small font
   *   'l' left, 't' top (above field) 
   * @return
   */
  @Override public GralTextField addTextField(String name, boolean editable, String prompt, String promptStylePosition)
  { Container parent = (Container)pos.panel.getPanelImpl();
    AwtTextField widg = new AwtTextField(name, editable ? 'T' : 'S', this, parent);
    widg.setPanelMng(this);
    widg.widgetAwt.setFont(propertiesGuiAwt.stdInputFont);
    widg.widgetAwt.setEditable(editable);
    widg.widgetAwt.setBackground(propertiesGuiAwt.colorAwt(GralColor.getColor("wh")));
    //widg.widgetAwt.addFocusListener(focusListener);

    //widg.widgetAwt.addMouseListener(mouseClickForInfo);
    int x =-1, y=-1; 
    if(x >=0 && y >=0){
      //edit.setBounds(x, y, dx * properties.xPixelUnit(), 2* properties.yPixelUnit());
    } else {
      //widget.setSize(xIncr * propertiesGui.xPixelUnit()-1, 2* propertiesGui.yPixelUnit()-1);
    }
    //
    if(prompt != null && promptStylePosition.startsWith("t")){
      setNextPosition();
      final Font promptFont;
      char sizeFontPrompt;
      GralRectangle boundsAll, boundsPrompt, boundsField;
      final GralPos posPrompt = new GralPos(), posField = new GralPos();
      boundsAll = calcWidgetPosAndSize(this.pos, 800, 600, 100, 20);
      float ySize = pos.height();
      //float xSize = pos.width();
          posPrompt.setPosition(this.pos, GralPos.same, ySize * 0.37f + GralPos.size, GralPos.same, GralPos.same, 0, '.');
          posField.setPosition(this.pos, GralPos.refer + ySize * 0.37f, GralPos.same, GralPos.same, GralPos.same, 0, '.');
      promptFont = propertiesGuiAwt.smallPromptFont;
      boundsPrompt = calcWidgetPosAndSize(posPrompt, boundsAll.dx, boundsAll.dy, 10,100);
      boundsField = calcWidgetPosAndSize(posField, boundsAll.dx, boundsAll.dy, 10,100);
      Label wgPrompt = new Label();
      parent.add(wgPrompt);
      wgPrompt.setFont(promptFont);
      wgPrompt.setText(prompt);
      /*TODO
      Point promptSize = wgPrompt.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
      if(promptSize.x > boundsPrompt.dx){
        boundsPrompt.dx = promptSize.x;  //use the longer value, if the prompt text is longer as the field.
      }
      wgPrompt.setBounds(boundsPrompt.x, boundsPrompt.y, boundsPrompt.dx, boundsPrompt.dy+1);
      widgetSwt.setBounds(boundsField.x, boundsField.y, boundsField.dx, boundsField.dy);
      posUsed = true;
      */
    } else {
      //without prompt
      setPosAndSize_(widg.widgetAwt);
    }
    //
    if(widg.name !=null && widg.name.charAt(0) == '$'){
      widg.name = sCurrPanel + widg.name.substring(1);
    }
    //link the widget with is information together.
    widg.widgetAwt.setData(widg);
    if(widg.name !=null){
      if(!editable){
        showFields.put(widg.name, widg);
      }
    }
    registerWidget(widg);
    return widg; 
  
  }
  
  
  

  
  /** Adds a text box for showing or editing a text in multi lines.
   * 
   * @param sName The registering name
   * @param width Number of grid units for length
   * @param editable true than edit-able, false to show content 
   * @param prompt If not null, than a description label is shown
   * @param promptStylePosition Position and size of description label:
   *   upper case letter: normal font, lower case letter: small font
   *   'l' left, 't' top (above field) 
   * @return
   */
  @Override public GralTextBox addTextBox(String name, boolean editable, String prompt, char promptStylePosition)
  { Container parent = (Container)pos.panel.getPanelImpl();
    AwtTextBox widgetSwt = new AwtTextBox(name, parent, 0, this);
    GralWidget widgetInfo = widgetSwt;
    widgetInfo.setPanelMng(this);
    //Text widgetSwt = new Text(((PanelSwt)pos.panel).getPanelImpl(), SWT.MULTI);
    widgetSwt.textFieldSwt.setFont(propertiesGuiAwt.stdInputFont);
    widgetSwt.textFieldSwt.setEditable(editable);
    if(editable)
      stop();
    widgetSwt.textFieldSwt.setBackground(propertiesGuiAwt.colorAwt(GralColor.getColor("pbl")));
    //widgetSwt.textFieldSwt.addMouseListener(mouseClickForInfo);
    setPosAndSize_(widgetSwt.textFieldSwt);
    if(prompt != null && promptStylePosition == 't'){
      final int yPixelField;
      final Font promptFont;
      int ySize = (int)(pos.height());
      switch(ySize){
      case 3:  promptFont = propertiesGuiAwt.smallPromptFont;
               yPixelField = propertiesGuiAwt.yPixelUnit() * 2 -3;
               break;
      case 2:  promptFont = propertiesGuiAwt.smallPromptFont;
               yPixelField = (int)(1.5F * propertiesGui.yPixelUnit());
               break;
      default: promptFont = propertiesGuiAwt.smallPromptFont;
               yPixelField = propertiesGui.yPixelUnit() * 2 -3;
      }//switch
      Rectangle boundsField = widgetSwt.textFieldSwt.getBounds();
      Rectangle boundsPrompt = new Rectangle(boundsField.x, boundsField.y-3  //occupy part of field above, only above the normal letters
        , boundsField.width, boundsField.height );
      
      if(promptStylePosition == 't'){ 
        boundsPrompt.height -= (yPixelField -4);
        boundsPrompt.y -= 1;
        
        boundsField.y += (boundsField.height - yPixelField );
        boundsField.height = yPixelField;
      }
      Label wgPrompt = new Label();
      parent.add(wgPrompt);
      wgPrompt.setFont(promptFont);
      wgPrompt.setText(prompt);
      /*
      Point promptSize = wgPrompt.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
      if(promptSize.x > boundsPrompt.width){
        boundsPrompt.width = promptSize.x;  //use the longer value, if the prompt text is longer as the field.
      }
      */
      widgetSwt.textFieldSwt.setBounds(boundsField);
      wgPrompt.setBounds(boundsPrompt);
    } 
    //
    if(widgetInfo.name !=null && widgetInfo.name.charAt(0) == '$'){
      widgetInfo.name = sCurrPanel + widgetInfo.name.substring(1);
    }
    //link the widget with is information together.
    widgetSwt.textFieldSwt.setData(widgetInfo);
    if(widgetInfo.name !=null){
      if(!editable){
        showFields.put(widgetInfo.name, widgetInfo);
      }
    }
    registerWidget(widgetInfo);
    return widgetSwt; 

  }

    

  @Override public GralHtmlBox addHtmlBox(String name){
    return null;
  }
  
  
  
  @Override public GralButton addButton(
    String sName
  , GralUserAction action
  , String sCmd
  , String sShowMethod
  , String sDataPath
  , String sButtonText
    //, int height, int width
    //, String sCmd, String sUserAction, String sName)
  )
  {
    int ySize = (int)pos.height();
    int xSize = (int)pos.width();
    char size = ySize > 3? 'B' : 'A';
    if(sName == null){ sName = sButtonText; }
    AwtButton widgButton = new AwtButton(sName, this, (Container)pos.panel.getPanelImpl(), 0, size);
    widgButton.setActionChange(action);  //maybe null
    widgButton.setText(sButtonText);
    widgButton.setPanelMng(this);
    widgButton.sCmd = sCmd;
    widgButton.setShowMethod(sShowMethod);
    widgButton.sDataPath = sDataPath;
    registerWidget(widgButton);
    return widgButton;
  }
  

  @Override
  public Object addCurveViewY(String sName, int nrofXvalues, int nrofTracks)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralWidget addFocusAction(String sName, GralUserAction action, String sCmdEnter,
    String sCmdRelease)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addFocusAction(GralWidget widgetInfo, GralUserAction action, String sCmdEnter,
    String sCmdRelease)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Object addImage(String sName, InputStream imageStream, int height, int width, String sCmd)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralWidget addLed(String sName, String sShowMethod, String sDataPath)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addLine(int colorValue, float xa, float ya, float xe, float ye)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public GralWidget addSlider(String sName, GralUserAction action, String sShowMethod,
    String sDataPath)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralButton addSwitchButton(String sName, GralUserAction action, String sCmd,
    String sShowMethod, String sDataPath, String sButtonText, String color0, String color1)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralTable addTable(String sName, int height, int[] columnWidths)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralWidget addText(String sText, char size, int color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralWidget addText(String sText, int origin, GralColor textColor, GralColor BackColor)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralValueBar addValueBar(String sName, String sShowMethod, String sDataPath)
  {
    // TODO Auto-generated method stub
    return null;
  }

  
  public GralMenu addPopupMenu(String sName){
    //Control panelSwt = (Control)pos.panel.getPanelImpl();
    //SwtMenu menu = new SwtMenu(sName, panelSwt, this);
    return null; //menu;
  }

  
  
  @Override
  public GralPanelContent createCompositeBox(String name)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralFileDialog_ifc createFileDialog()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralPanelContent createGridPanel(String namePanel, GralColor backGround, int xG, int yG,
    int xS, int yS)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralTabbedPanel addTabbedPanel(String namePanel, GralPanelActivated_ifc user,
    int properties)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralWindow createWindow(String name, String title, int windPros)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<Entry<String, GralWidget>> getShowFields()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralDispatchCallbackWorker getTheGuiChangeWorker()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean remove(GralPanelContent compositeBox)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean remove(GralWidget widget)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void repaint()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void repaintCurrentPanel()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void selectPanel(String sName)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setColorGridCurveViewY(String sName, int backgroundColor, int[] colorLines, char grid)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setLineCurveView(String sNameView, int trackNr, String sNameLine, String sVariable,
    int colorValue, int style, int y0Line, float yScale, float yOffset)
  {
    // TODO Auto-generated method stub
    
  }

  @Override public Color getColorImpl(GralColor color) { return propertiesGuiAwt.colorAwt(color); }

  @Override
  public String getValue(String sName)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getValueFromWidget(GralWidget widgetDescr)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String insertInfo(String name, int ident, String content)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void redrawWidget(String sName)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void resizeWidget(GralWidget widgd, int xSizeParent, int ySizeParent)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public String setInfo(GralWidget widgd, int cmd, int ident, Object visibleInfo, Object userData)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setSampleCurveViewY(String sName, float[] values)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean setWindowsVisible(GralWindow_ifc window, GralPos atPos)
  {
    // TODO Auto-generated method stub
    return false;
  }
  
  
  
  /**Places a current component with knowledge of the current positions and the spreads of the component on graphic.
   * @param component The component to place.
   */
  void setBounds_(Component component)
  { setPosAndSize_(component);
    //setBounds_(component, 0,0, 0, 0);
  }
  
  
  
  
  protected void setPosAndSize_(Component component)
  { setPosAndSize_(component, 0,0);
  }  
  
  protected void setPosAndSize_(Component component, int widthwidgetNat, int heigthWidgetNat)
  {
    setNextPosition();
    Component parentComp = component.getParent();
    //Rectangle pos;
    final GralRectangle rectangle;
    if(parentComp == null){
      rectangle = calcWidgetPosAndSize(pos, 800, 600, widthwidgetNat, heigthWidgetNat);
    } else {
      final Rectangle parentSize = parentComp.getBounds();
      rectangle = calcWidgetPosAndSize(pos, parentSize.width, parentSize.height, widthwidgetNat, heigthWidgetNat);
    }
    component.setBounds(rectangle.x, rectangle.y, rectangle.dx, rectangle.dy );
       
  }
  


  void stop(){}



  @Override
  protected String setInfoGthread(GralWidget_ifc widget, int cmd, int ident,
      Object info, Object data) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
