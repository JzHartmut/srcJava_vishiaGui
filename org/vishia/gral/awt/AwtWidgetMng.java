package org.vishia.gral.awt;

import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.InputStream;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralCurveView;
import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralHtmlBox;
import org.vishia.gral.base.GralLed;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralMenu._GraphicImpl;
import org.vishia.gral.base.GralPanelActivated_ifc;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFileDialog_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralHorizontalSelector;
import org.vishia.gral.widget.GralLabel;
import org.vishia.msgDispatch.LogMessage;

public class AwtWidgetMng extends GralMng.ImplAccess // implements GralMngBuild_ifc, GralMng_ifc
{
  
  final AwtProperties propertiesGuiAwt; 
  
  //final Frame mainWindowAwt;
  
  
  /**The standard listener or action for mouse events, able to assign to all widgets which does not need an extra mouse behaviour. */
  final AwtGralMouseListener.MouseListenerGralAction mouseStdAction = new AwtGralMouseListener.MouseListenerGralAction(null);
  
  /**Creates an instance.
   * @param guiContainer The container where the elements are stored in.
   * @param width in display-units for the window's width, the number of pixel depends from param displaySize.
   * @param height in display-units for the window's height, the number of pixel depends from param displaySize.
   * @param displaySize character 'A' to 'E' to determine the size of the content 
   *        (font size, pixel per cell). 'A' is the smallest, 'E' the largest size. Default: use 'C'.
   */
  public AwtWidgetMng(AwtProperties propertiesGui
    //, VariableContainer_ifc variableContainer
    , LogMessage log
    )
  { super(GralMng.get(), propertiesGui);
    //mainWindowAwt = window;
    this.propertiesGuiAwt = propertiesGui;
    //mainWindowAwt.addKeyListener(mainKeyListener);
  }

  
  
  @Override public Container getCurrentPanel(){ return (Container)pos().panel.getWidgetImplementation(); }

  
  public Container getWidgetsPanel(GralWidget widg){ 
    GralPos pos = widg.pos();
    if(pos == null) { pos = pos(); } //from GralMng
    return ((Container)pos.panel.getWidgetImplementation()); 
  }



  @Override public void createImplWidget_Gthread(GralWidget widgg){
    if(widgg instanceof GralLabel){
      new AwtLabel((GralLabel)widgg, this);
    } else if(widgg instanceof GralTextField){
      new AwtTextField((GralTextField)widgg, this);
    } else if(widgg instanceof GralHorizontalSelector<?>){
      //SwtHorizontalSelector swtSel = new SwtHorizontalSelector(this, (GralHorizontalSelector<?>)widgg);
      mng.registerWidget(widgg);
    } else if(widgg instanceof GralTable<?>){
      //AwtTable.addTable((GralTable<?>)widgg, this);
    } else if(widgg instanceof GralButton){
      new AwtButton((GralButton)widgg, this);
    }
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
  //@Override 
  public GralTextField addTextField(String name, boolean editable, String prompt, String promptStylePosition)
  { Container parent = getCurrentPanel();
    AwtTextField widg = new AwtTextField(name, editable ? 'T' : 'S', this, parent);
    widg.setPanelMng(mng);
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
      mng.setNextPosition();
      final Font promptFont;
      char sizeFontPrompt;
      GralRectangle boundsAll, boundsPrompt, boundsField;
      final GralPos posPrompt = new GralPos(), posField = new GralPos();
      boundsAll = mng.calcWidgetPosAndSize(this.pos(), 800, 600, 100, 20);
      float ySize = pos().height();
      //float xSize = pos.width();
          posPrompt.setPosition(this.pos(), GralPos.same, ySize * 0.37f + GralPos.size, GralPos.same, GralPos.same, 0, '.');
          posField.setPosition(this.pos(), GralPos.refer + ySize * 0.37f, GralPos.same, GralPos.same, GralPos.same, 0, '.');
      promptFont = propertiesGuiAwt.smallPromptFont;
      boundsPrompt = mng.calcWidgetPosAndSize(posPrompt, boundsAll.dx, boundsAll.dy, 10,100);
      boundsField = mng.calcWidgetPosAndSize(posField, boundsAll.dx, boundsAll.dy, 10,100);
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
      *//*
    } else {
      //without prompt
      setPosAndSize_(widg.widgetAwt);
    }
    //
    if(widg.name !=null && widg.name.charAt(0) == '$'){
      widg.name = sCurrPanel() + widg.name.substring(1);
    }
    //link the widget with is information together.
    widg.widgetAwt.setData(widg);
    if(widg.name !=null){
      if(!editable){
        mng.registerShowField(widg);
      }
    }
    mng.registerWidget(widg);
    return widg; 
  
  }
  */
  
  

  
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
  //@Override 
  public GralTextBox addTextBox(String name, boolean editable, String prompt, char promptStylePosition)
  { Container parent = (Container)pos().panel.getWidgetImplementation();
    AwtTextBox widgetSwt = new AwtTextBox(name, parent, 0, this);
    GralWidget widgetInfo = widgetSwt;
    widgetInfo.setPanelMng(mng);
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
      int ySize = (int)(pos().height());
      switch(ySize){
      case 3:  promptFont = propertiesGuiAwt.smallPromptFont;
               yPixelField = propertiesGuiAwt.yPixelUnit() * 2 -3;
               break;
      case 2:  promptFont = propertiesGuiAwt.smallPromptFont;
               yPixelField = (int)(1.5F * mng.propertiesGui.yPixelUnit());
               break;
      default: promptFont = propertiesGuiAwt.smallPromptFont;
               yPixelField = mng.propertiesGui.yPixelUnit() * 2 -3;
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
      widgetSwt.textFieldSwt.setBounds(boundsField);
      wgPrompt.setBounds(boundsPrompt);
    } 
    //
    if(widgetInfo.name !=null && widgetInfo.name.charAt(0) == '$'){
      widgetInfo.name = sCurrPanel() + widgetInfo.name.substring(1);
    }
    //link the widget with is information together.
    widgetSwt.textFieldSwt.setData(widgetInfo);
    if(widgetInfo.name !=null){
      if(!editable){
        mng.registerShowField(widgetInfo);
      }
    }
    mng.registerWidget(widgetInfo);
    return widgetSwt; 

  }
  */
    

  @Override public GralHtmlBox addHtmlBox(String name){
    return null;
  }
  
  
  @Override public GralCurveView addCurveViewY(String sName, int nrofXvalues, GralCurveView.CommonCurve common)
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
  public GralWidget addSlider(String sName, GralUserAction action, String sShowMethod,
    String sDataPath)
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
  
  public void add(GralTable<?> table){}


  @Override @Deprecated
  public GralWidget addText(String sText, char size, int color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  
  

  
  @Override protected GralMenu XXXaddPopupMenu(String sName){
    //Control panelSwt = (Control)pos.panel.getPanelImpl();
    //SwtMenu menu = new SwtMenu(sName, panelSwt, this);
    return null; //menu;
  }

  
  @Override protected GralMenu createContextMenu(GralWidget widg){
    Component widgSwt = (Component)widg._wdgImpl.getWidgetImplementation();
    GralMenu menu = new GralMenu(); new AwtMenu(widg, widgSwt, mng);  //TODO
    PopupMenu menuAwt = (PopupMenu)menu.getMenuImpl();
    widgSwt.add(menuAwt);
    menuAwt.show(widgSwt, 10, 10);
    return menu;
  }
 
  
  
  @Override protected GralMenu createMenuBar(GralWindow windg){
    Frame windowAwt = (Frame)windg.getWidgetImplementation();
    GralMenu menu = new GralMenu(); new AwtMenu(windg, windowAwt, mng);  //TODO
    return menu;
  }
 
  
  


  
  
  @Override public GralPanelContent createCompositeBox(String name)
  {
      //Composite box = new Composite(graphicFrame, 0);
      Container box = new Container();
      Container parent = (Container)pos().panel.getWidgetImplementation();
      
      parent.add(box);
      setPosAndSize_(mng.getPosOldPositioning(), box);
      Dimension size = box.getSize();
      GralPanelContent panel = new AwtPanel(name, mng, box);
      //mng.registerPanel(panel);
      return panel;
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

  
  @Override public void createSubWindow(GralWindow windowGral){
    AwtSubWindow windowSwt = new AwtSubWindow(windowGral);
    //new SwtSubWindow(name, swtDevice.displaySwt, title, windProps, this);
    GralRectangle rect = calcPositionOfWindow(windowGral.pos());
    windowSwt.window.setBounds(rect.x, rect.y, rect.dx, rect.dy );
    //window.window.redraw();
    //window.window.update();
    windowGral._wdgImpl = windowSwt;

  }
  
  /**Calculates the position as absolute value on screen from a given position inside a panel.
   * @param posWindow contains any {@link GralPos#panel}. Its absolute position will be determined.
   *   from that position and size the absolute postion will be calculate, with this given grid positions
   *   inside the panel. 
   * @return Absolute pixel coordinate.
   */
  GralRectangle calcPositionOfWindow(GralPos posWindow)
  {
    final GralRectangle windowFrame;
    if(posWindow.panel !=null) {
      Object awtWidg = posWindow.panel.getWidgetImplementation();
      Window parentFrame = (Frame)awtWidg; //((SwtPanel)(swtWidg)).panelComposite; //(Control)posWindow.panel.getPanelImpl();
      Point loc;
      windowFrame = getPixelUseableAreaOfWindow(posWindow.panel);
    } else {
      windowFrame = new GralRectangle(0,0,800,600);
    }
    int dxFrame = 400, dyFrame = 300;  //need if posWindow has coordinates from right or in percent
    Rectangle rectParent = null;
    /*
    if(parentFrame == window){
      dxFrame = windowFrame.dx; dyFrame = windowFrame.dy;
    } else {
      rectParent = parentFrame.getBounds();
      dxFrame = rectParent.width; dyFrame = rectParent.height;
    }
    */
    final GralRectangle rectangle = mng.calcWidgetPosAndSize(posWindow, dxFrame, dyFrame, 400, 300);
    rectangle.x += windowFrame.x;
    rectangle.y += windowFrame.y;
    /*
    //
    while ( parentFrame != window){ //The Shell is the last parentFrame
      //the bounds are relative to its container. Get all parent container and add all positions
      //until the shell is reached.
      rectParent = parentFrame.getBounds();
      rectangle.x += rectParent.x;
      rectangle.y += rectParent.y;
      parentFrame = parentFrame.getParent();
    }
    */
    return rectangle;
  }
  

  
  GralRectangle getPixelUseableAreaOfWindow(GralWidget widgg)
  { Object oControl = widgg._wdgImpl.getWidgetImplementation();
    Frame control = (Frame)oControl;
    Frame window = control;
    Rectangle rectWindow = window.getBounds();
    Rectangle rectWindowArea = rectWindow; //window.getClientArea();  //it is inclusive the menu bar.
    //Problem: the x and y of client are are 0, it may bettet that they are the left top corner
    //inside the shell window.
    //assume that the client area is on bottom of the shell. Calculate top position:
    int dxBorder = rectWindow.width - rectWindowArea.width;
    int xPos = rectWindow.x + dxBorder/2;
    int dyTitleMenu = (rectWindow.height - rectWindowArea.height) - dxBorder;  //border and title bar
    MenuBar menu = window.getMenuBar();
    if(menu !=null){
      //assume that the menu has the same hight as title bar, there is not a way to determine it else
      dyTitleMenu *=2;  
    }
    int yPos = rectWindow.y + dxBorder/2 + dyTitleMenu;
    GralRectangle ret = new GralRectangle(xPos, yPos, rectWindowArea.width, rectWindowArea.height - dyTitleMenu);
    return ret;
  }
  



  @Override
  public boolean remove(GralPanelContent compositeBox)
  {
    // TODO Auto-generated method stub
    return false;
  }



  @Override public Color getColorImpl(GralColor color) { return propertiesGuiAwt.colorAwt(color); }

  @Override
  public String getValueFromWidget(GralWidget widgetDescr)
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
    Object owidg = widgd._wdgImpl.getWidgetImplementation();
    int test = 6;
    if(owidg !=null){
      Component swtWidget = (Component)owidg;
      GralPanelContent panel = widgd.pos().panel;
      GralRectangle size = panel._wdgImpl.getPixelPositionSize(); //PixelSize();
      GralRectangle posSize = mng.calcWidgetPosAndSize(widgd.pos(), size.dx, size.dy, 0, 0);
      //Note: the swtWidget may have a resizeListener, see there.
      swtWidget.setBounds(posSize.x, posSize.y, posSize.dx, posSize.dy );
      swtWidget.repaint();
    }
  }

  @Override
  public void setSampleCurveViewY(String sName, float[] values)
  {
    // TODO Auto-generated method stub
    
  }

  
  
  /**Places a current component with knowledge of the current positions and the spreads of the component on graphic.
   * @param component The component to place.
   */
  void setBounds_(GralPos pos, Component component)
  { setPosAndSize_(pos, component);
    //setBounds_(component, 0,0, 0, 0);
  }
  
  
  
  
  protected void setPosAndSize_(GralPos pos, Component component)
  { setPosAndSizeAwt(pos, component, 0,0);
  }  
  
  /**
   * @param component
   * @param widthwidgetNat
   * @param heigthWidgetNat
   *
   * NOTE: 2015-07-13: This method is set to unused because it uses the mng-position additional to the constructor of GralWidget.
   * This is the old concept which is in conflict with the usuage there.
   */
  protected void XXXsetPosAndSize_(Component component, int widthwidgetNat, int heigthWidgetNat)
  {
    mng.setNextPosition();
    Component parentComp = component.getParent();
    //Rectangle pos;
    final GralRectangle rectangle;
    if(parentComp == null){
      rectangle = mng.calcWidgetPosAndSize(pos(), 800, 600, widthwidgetNat, heigthWidgetNat);
    } else {
      final Rectangle parentSize = parentComp.getBounds();
      rectangle = mng.calcWidgetPosAndSize(pos(), parentSize.width, parentSize.height, widthwidgetNat, heigthWidgetNat);
    }
    component.setBounds(rectangle.x, rectangle.y, rectangle.dx, rectangle.dy );
       
  }
  

  
    
  /**Set bounds of a AWT component with a given position.
   * This method is package-private for SWT-implementation.
   * @param posP The Position for the component.
   * @param component The AWT-widget.
   * @param widthwidgetNat The natural size of the component.
   * @param heigthWidgetNat The natural size of the component.
   */
  void setPosAndSizeAwt(GralPos posP, Component component, int widthwidgetNat, int heigthWidgetNat)
  {
    GralRectangle rectangle = calcWidgetPosAndSizeAwt(posP, component, widthwidgetNat, heigthWidgetNat);
    //on SWT it invokes the resize listener if given.
    Component parentComp = component.getParent();
    //if(parentComp instanceof Frame){
      rectangle.y += 50;  //title and menu bar
    //}
    component.setBounds(rectangle.x, rectangle.y, rectangle.dx, rectangle.dy );
       
  }
  


  
  
  
  /**Calculates the bounds of a widget with a given pos independent of this {@link #pos}.
   * This method is a part of the implementing GralMng because the GralPos is not implemented for
   * any underlying graphic system and the {@link #propertiesGuiSwt} are used.
   * It is possible to tune the bounds after calculation, for example to enhance the width if a text
   * is larger then the intended position. 
   * @param pos The position.
   * @param widthwidgetNat The natural size of the component.
   * @param heigthWidgetNat The natural size of the component.
   * @return A rectangle with position and size.
   */
  @Override public GralRectangle calcWidgetPosAndSize(GralPos pos, int widthwidgetNat, int heigthWidgetNat){
    Component parentComp = (Component)pos.panel.getWidgetImplementation();
    //Rectangle pos;
    final GralRectangle rectangle;
    final Rectangle parentSize;
    if(parentComp == null){
      parentSize = new Rectangle(0,0,800, 600);
    /*
    } else if(parentComp instanceof Shell) {
      parentSize = ((Shell)parentComp).getClientArea();
    */
    } else {
      parentSize = parentComp.getBounds();
    }
    return pos.calcWidgetPosAndSize(mng.propertiesGui, parentSize.width, parentSize.height, widthwidgetNat, heigthWidgetNat);
  }
  
  
  
  
  
  
  /**Calculates the bounds of a SWT component with a given position independent of {@link #pos}.
   * This method is package-private for SWT-implementation.
   * It is possible to tune the bounds after calculation, for example to enhance the width if a text
   * is larger then the intended position. 
   * @param pos The position.
   * @param component The SWT-widget.
   * @param widthwidgetNat The natural size of the component.
   * @param heigthWidgetNat The natural size of the component.
   * @return A rectangle with position and size.
   * @deprecated, use {@link #calcWidgetPosAndSizeSwt(GralPos, int, int)}
   */
  GralRectangle calcWidgetPosAndSizeAwt(GralPos pos, Component component, int widthwidgetNat, int heigthWidgetNat){
    Component parentComp = component.getParent();
    //Rectangle pos;
    final GralRectangle rectangle;
    final Rectangle parentSize;
    if(parentComp == null){
      parentSize = new Rectangle(0,0,800, 600);
    } else if(parentComp instanceof Frame) {
      parentSize = ((Frame)parentComp).getBounds(); // ??getAccessibleContext().  ??getClientArea();
      parentSize.height -= 50;
    } else {
      parentSize = parentComp.getBounds();
    }
    return mng.calcWidgetPosAndSize(pos, parentSize.width, parentSize.height, widthwidgetNat, heigthWidgetNat);
  }
  

  
  

  @Override public boolean showContextMenuGthread(GralWidget widg) {
    boolean bOk;
    Component awtWidg = (Component)widg._wdgImpl.getWidgetImplementation();
    Menu contextMenu = null; //awtWidg.get;
    if(contextMenu == null){
      bOk = false;
    } else {
      //Rectangle pos = swtWidg.getBounds();
      GralRectangle pos = AwtWidgetHelper.getPixelPositionSize(awtWidg);
      //contextMenu.setLocation(pos.x + pos.dx, pos.y + pos.dy);
      //contextMenu.setVisible(true);
      bOk = true;
    }
    return bOk;
  }
  
  


  KeyListener XXXXX_mainKeyListener = new KeyListener(){

    @Override
    public void keyPressed(KeyEvent e) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void keyReleased(KeyEvent e) {
      // TODO Auto-generated method stub
      stop();
    }

    @Override
    public void keyTyped(KeyEvent e) {
      // TODO Auto-generated method stub
      
    }
    
  };
  
  /**Universal focus listener to register which widgets were in focus in its order,
   * to set htmlHelp and to invoke the {@link GralWidget#setActionFocused(GralUserAction)}.
   */
  protected class AwtMngFocusListener implements FocusListener
  {
    GralMng.GralMngFocusListener gralFocus;
    
    AwtMngFocusListener(){
      gralFocus = GralMng.get().new GralMngFocusListener();
    }
    
    @Override public void focusLost(FocusEvent ev)
    { GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(((AwtWidget)ev.getComponent()).getData());
      gralFocus.focusLostGral(widgg);
    }
    
    @Override public void focusGained(FocusEvent ev)
    { GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(((AwtWidget)ev.getComponent()).getData());
      gralFocus.focusGainedGral(widgg);
    }
  }
  
  /**The package private universal focus listener. */
  protected AwtMngFocusListener focusListener = new AwtMngFocusListener();



  void stop(){}



  @Override
  public boolean XXXsetWindowsVisible(GralWindow_ifc window, GralPos atPos)
  {
    // TODO Auto-generated method stub
    return false;
  }





  
}
