/****************************************************************************/
/* Copyright/Copyleft: 
 * 
 * For this source the LGPL Lesser General Public License, 
 * published by the Free Software Foundation is valid.
 * It means:
 * 1) You can use this source without any restriction for any desired purpose.
 * 2) You can redistribute copies of this source to everybody.
 * 3) Every user of this source, also the user of redistribute copies 
 *    with or without payment, must accept this license for further using.
 * 4) But the LPGL ist not appropriate for a whole software product,
 *    if this source is only a part of them. It means, the user 
 *    must publish this part of source,
 *    but don't need to publish the whole source of the own product.
 * 5) You can study and modify (improve) this source 
 *    for own using or for redistribution, but you have to license the
 *    modified sources likewise under this LGPL Lesser General Public License.
 *    You mustn't delete this Copyright/Copyleft inscription in this source file.    
 *
 * @author www.vishia.de/Java
 * @version 2006-06-15  (year-month-day)
 * list of changes: 
 * 2009-03-07: Hartmut: bugfix: setOutputWindow() 
 * 2006-05-00: Hartmut Schorrig www.vishia.de creation
 *
 ****************************************************************************/

package org.vishia.mainGuiSwt;

import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.area9.GuiMainAreaBase;
import org.vishia.gral.area9.GuiMainAreaifc;
import org.vishia.gral.base.GralSubWindow;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWindowMng;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralGridPos;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralTextBox_ifc;
import org.vishia.gral.widget.InfoBox;
import org.vishia.mainCmd.MainCmd;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.msgDispatch.LogMessage;


//import java.awt.event.*;
//import java.util.*;  //List
//import javax.swing.*;

/**
<h1>class MainCmdSwt - Description</h1>
<font color="0x00ffff">
  Diese abstrakte Klasse dient als Basisklasse f�r alle grafischen Applikationen (GUI = Graphical User Interface),
  die �ber eine cmdLine gestartet werden. Diese Klasse basiert wiederum auf MainCmd.
  Diese Klasse enth�lt neben MainCmd folgende Leistungseigenschaften:
  <ul>
  <li>Bereitstellen eines JFrame, Rahmen f�r das Gesamtfenster.</li>
  <li>Bereitstellen eines Output-Textbereiches, der anstelle Konsolenausgaben verwendet werden kann.
      Die Methoden writeInfo() und writeInfoln() aus MainCmd werden hierher geleitet.
      Dieser Outputbereich muss vom Anwender initialisiert werden, auf seine Verwendung kann auch verzichtet
      werden, falls das nicht notwendig ist.</li>
  <li>Bereitstellen eines leeren Men�s mit einem FileMen� mit Exit und einem Hilfemen� mit "about"</li>
  <li>Bereitstellen eines leeren Dialog-Containers</li>
  </ul>
</font>
<hr/>
<pre>
date       who      change
2006-01-07 HarmutS  initial revision
*
</pre>
<hr/>


*/

//public abstract class MainCmdSwt extends MainCmd implements GuiMainAreaifc
public class MainCmdSwt extends GuiMainAreaBase implements GuiMainAreaifc
{
  
  
  /**Version history:
   * <ul>
   * <li>2011-05-01 Hartmut chg: The method switchToWindowOrStartCmdLine(...) is a poor windows-based method
   *     and doesn't run under Linux. It is removed here, instead add in org.vishia.windows.WindowMng.java 
   * <li>All other changes from 2010 and in the past
   * </ul>
   * 
   */
  final static int version = 0x20110502;

  
	
  private final PrimaryWindowSwt swtWindow;
	
	
	
  
  /**The file menu is extendable. */
  private Menu menuFile;
  
  /**The output menu is extendable. */
  private Menu menuOutput;
  
  /**The help menu is extendable. */
  private Menu menuHelp;
  
  //protected JScrollPane textAreaOutputPane;
  
  /** Paint Methoden */
  //private Canvas paintArea = null;
  
  /** If it is set, it is a area for some Buttons, edit windows and others.*/
  private Composite mainDialog = null;

	class ActionFileOpen implements SelectionListener
  { @Override public void widgetSelected(SelectionEvent e)
    { 
      FileDialog fileChooser = new FileDialog(swtWindow.graphicThreadSwt.windowSwt);
      if(currentDirectory != null) { fileChooser.setFileName(currentDirectory.getAbsolutePath()); }
      String sFile = fileChooser.open();
      if(sFile != null){
        actionFile.userActionGui("open", null, sFile);
        //actionFileOpen(file);
        //ActionEvent eventCreated = new ActionEvent(this, 0x0, "open " + sTelgFile);
      }  
      
    }
  
  
  	@Override public void widgetDefaultSelected(SelectionEvent e)
  	{ 
  	}
  }

  
  class ActionFileClose implements SelectionListener
  { 
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void widgetSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
		}
  }

  
  class ActionFileSave implements SelectionListener
  {
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
      actionFile.userActionGui("save", null, "");
		}
	
		@Override
		public void widgetSelected(SelectionEvent e) {
      actionFile.userActionGui("save", null, "");
		}
  }

  
  class ActionHelp implements SelectionListener
  { 
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void widgetSelected(SelectionEvent e) {
      //String[] sHelpText = new String[mainCmd.listHelpInfo.size()];
      if(infoHelp == null){
        GralWidgetMng gralMng = getGralMng();
        try{
          gralMng.selectPanel("output");
          gralMng.setPosition(-40,0,0,0,0,'.');
          infoHelp = InfoBox.create(gralMng, "Help", "Help");
          for(String line: mainCmd.listHelpInfo){
            infoHelp.append(line).append("\n");
            //sHelpText[ix++] = line;
          }
        } catch(Exception exc){ gralMng.writeLog(0, exc); }
      }
      infoHelp.setWindowVisible(true);
		}
  	
  }

  
  class ActionAbout implements SelectionListener
  { 
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void widgetSelected(SelectionEvent e)
    {
	    if(infoAbout == null){
	      GralWidgetMng gralMng = getGralMng();
	      try{
	        gralMng.selectPanel("output");
          gralMng.setPosition(-20,0,-40,0,0,'.');
          infoAbout = InfoBox.create(getGralMng(), "about", "about");
          for(String line: mainCmd.listAboutInfo){
            infoAbout.append(line).append("\n");
            //sHelpText[ix++] = line;
          }
        } catch(Exception exc){ gralMng.writeLog(0, exc); }
        
      }
      infoAbout.setWindowVisible(true);
    }
  }

  
  class ActionClearOutput implements SelectionListener
  { 
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void widgetSelected(SelectionEvent e)
    { textAreaOutput.setText("--clean--\n");
    }
  }
  

  
  
  
  
  ControlListener resizeListener = new ControlListener()
  {	@Override	public void controlMoved(ControlEvent e) 
		{ //do nothing if moved.
		}

		@Override	public void controlResized(ControlEvent e) 
		{ validateFrameAreas();  //calculates the size of the areas newly and redraw.
		}
  	
  };
  
  
  
  
  MouseListener mouseListener = new MouseListener()
  {
    int captureAreaDivider;
    
		@Override	public void mouseDoubleClick(MouseEvent e) 
		{ //do nothing
		}

		@Override	public void mouseDown(MouseEvent e) 
		{ 
			int yf1 = ypFrameArea[1];
	    int yf2 = ypFrameArea[2];
	    int xf1 = xpFrameArea[1]; //percent right
	    int xf2 = xpFrameArea[2]; //percent right
			if(e.x < 20){
		    //calculate pixel size for the component:
		    int y1 = (int)(yf1  * pixelPerYpercent);
		    int y2 = (int)(yf1  * pixelPerYpercent);
		    if(e.y > y1-20 && e.y < y1 + 20){
		      captureAreaDivider = 1;    	
		    }
		  }
		}

		@Override	public void mouseUp(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
  	
  };
  


  
  /*	
  protected MainCmdWin(String[] args, MainApplicationWin_ifc application)
  { super(args);
    this.application = application;
  }
  */
  
  public MainCmdSwt(MainCmd cmdP, GralWindowMng guiDevice, GralSubWindow window, String sOutputArea) //String[] args)
  { //super(args);
    //assert(false);
    super(cmdP, guiDevice, window); //gralDevice);
  
    swtWindow = (PrimaryWindowSwt)guiDevice; //PrimaryWindowSwt.create(cmdP.getLogMessageOutputConsole());
    //super.gralDevice = swtWindow;
    this.outputArea = sOutputArea;
    guiDevice.addDispatchListener(initOutputArea); 
    guiDevice.addDispatchListener(writeOutputTextDirectly);
  }
  
  
  
  public GralWindowMng getPrimaryWindow(){ return swtWindow; }
  

  GralDispatchCallbackWorker initOutputArea = new GralDispatchCallbackWorker(){
    @Override public void doBeforeDispatching(boolean onlyWakeup)
    {
      swtWindow.graphicThreadSwt.windowSwt.addControlListener(resizeListener);
      setFrameAreaBorders(30,70,30,70);
      if(bSetStandardMenus){
        setStandardMenusGThread(currentDirectory, actionFile);
      }
      if(outputArea != null){
        GralRectangle area = convertArea(outputArea);
        /*
        int xArea = outputArea.charAt(0) - 'A' +1;
        int yArea = outputArea.charAt(1) - '0';
        int dxArea = outputArea.charAt(2) - 'A' +1 - xArea +1;
        int dyArea = outputArea.charAt(3) - '0' - yArea +1;
        */
        outputPanel = addOutputFrameArea(area.x, area.y, area.dx, area.dy);
        gralDevice.gralMng.registerPanel(outputPanel);
        swtWindow.removeDispatchListener(this);
        countExecution();
      }
    }
  };

  
  
  GralDispatchCallbackWorker writeOutputTextDirectly = new GralDispatchCallbackWorker()
  { @Override public void doBeforeDispatching(boolean onlyWakeup)
    { String line;
      while((line = outputTexts.poll())!=null){
        writeDirectly(line, MainCmd.kInfoln_writeInfoDirectly);
      }
    }
  };
  
  
  
  /**Exits the cmdline application with the maximum of setted exit error level.
  This method should be called only on end of the application, never inside. If the user will abort
  the application from inside, he should throw an exception instead. So an comprising application
  may catch the exception and works beyond.
  This method is not member of MainCmd_Ifc and is setted protected, because
  the users Main class (UserMain in the introcuction) only should call exit.
*/
@Override public void exit()
{ 
  swtWindow.terminate();  
  System.exit(mainCmd.getExitErrorLevel());
}


	public final void setStandardMenus(File openStandardDirectory, GralUserAction actionFile)
	{ this.currentDirectory = openStandardDirectory;
	  this.actionFile = actionFile;
    this.bSetStandardMenus = true;
		
	}
	
	
	/** Adss some standard meues in the menue bar.
   * 
   * @param openStandardDirectory may be null or a directory as default for "file-open" menue.
   */
  
  public final void setStandardMenusGThread(File openStandardDirectory, GralUserAction actionFile)
  { this.currentDirectory = openStandardDirectory;
    this.actionFile = actionFile;
    { //create the menue
      swtWindow.addMenuItemGThread("&File/&Open", this.new ActionFileOpen());
      swtWindow.addMenuItemGThread("&File/&Close", this.new ActionFileClose());
      swtWindow.addMenuItemGThread("&File/&Save", this.new ActionFileSave());
      //swtWindow.addMenuItemGThread("&File/E&xit", this.new ActionFileOpen());
      swtWindow.addMenuItemGThread("&Help/&Help", this.new ActionHelp());
      swtWindow.addMenuItemGThread("&Help/&About", this.new ActionAbout());
      //swtWindow.graphicThreadSwt.setJMenuBar(menuBar);
      //swtWindow.graphicThreadSwt.setVisible( true );
      swtWindow.graphicThreadSwt.windowSwt.update();
    }
    
  }
  
  
  @Override public void addMenuItemGThread(String namePath, GralUserAction action)
  { swtWindow.addMenuItemGThread(namePath, action);
  }
  
  
  
  
  /**returns the file menu to add something to it. The file menu is created 
   * if {@link #addStandardMenus(File)} is called.
   * @return null if no standard menus are activated.
   */
  protected Menu getFileMenu(){ return menuFile; }
  
  /**returns the help menu to add something to it. The help menu is created 
   * if {@link #addStandardMenus(File)} is called.
   * @return null if no standard menus are activated.
   */
  protected Menu getHelpMenu(){ return menuHelp; }
  
  /**returns the output menu to add something to it. The output menu is created 
   * if {@link #setOutputWindow()} is called.
   * @return null if no output window is activated.
   */
  protected Menu getOutputMenu(){ return menuOutput; }
  
  
  /** Adds a complete pull down menu at the last but one position.
   * In standard menu technic the first position is the file menu and the
   * last position is the help menu. Both are added by calling addStandardMenus().
   * The user menus should be added between this both pull-down menus. 
   * @param menu A user's menu.
   */
  protected void XXXaddMenu(Menu menu, String text, char accelerator)
  {
    Menu menuBar = swtWindow.graphicThreadSwt.windowSwt.getMenuBar();
    int nrofMenus = menuBar.getItemCount();  //ComponentCount();
    
    MenuItem menuItem = new MenuItem(menuBar, SWT.DROP_DOWN);
    menuItem.setText(text);
    menuItem.setAccelerator(SWT.CONTROL | accelerator);
    menuItem.setMenu(menu);
    swtWindow.graphicThreadSwt.windowSwt.update();
  }
  
  
  /**Adds a listener, which will be called in the dispatch loop.
   * @param listener
   */
  @Override public void addDispatchListener(GralDispatchCallbackWorker listener)
  { swtWindow.addDispatchListener(listener);
  }
  
  
  
  @Override public void removeDispatchListener(GralDispatchCallbackWorker listener)
  { swtWindow.removeDispatchListener(listener);
  }
  
  
  /** Adds a complete pull down menu before the last position.
   * In standard menu technic the first position is the file menu and the
   * last position is the help menu. Both are added by calling addStandardMenus().
   * The user menus should be added between this both pull-down menus. 
   * @param menu A user's menu.
   */
  protected Menu XXXaddMenu(String text, char accelerator)
  {
    Menu menuBar = swtWindow.graphicThreadSwt.windowSwt.getMenuBar();
    int nrofMenus = menuBar.getItemCount();  //ComponentCount();
    
    MenuItem menuItem = new MenuItem(menuBar, SWT.DROP_DOWN);
    menuItem.setText(text);
    menuItem.setAccelerator(SWT.CONTROL | accelerator);
    Menu menu = new Menu(swtWindow.graphicThreadSwt.windowSwt, SWT.DROP_DOWN);
    menuItem.setMenu(menu);
    //swtWindow.graphicThreadSwt.update();
    return menu;
  }

  
  /** Adds a menu instance to a present pull-down-menu at the last but one position.
   * This method is ...
   * @param menuItem A user's menuItem.
   * 
  protected void addMenuItem(MenuItem menuItem, int idx)
  {
    Menu menuBar = swtWindow.graphicThreadSwt.getMenuBar();
    MenuItem[] items = menuBar.getItems();
    MenuItem barItem = items[idx];
    Menu menuBarEntry = barItem.getMenu();
    int nrofMenuItems = menuBarEntry.getItemCount();
    menuItem.set
    menu.add(menuItem, nrofMenuItems -1);
    swtWindow.graphicThreadSwt.validate();
  }
   */
  
  
  
  /**
   * @param menuItem
  protected void addFileMenuItem(JMenuItem menuItem)
  {
    int nrofMenuItems = menuFile.getMenuComponentCount(); //  .getComponentCount();
    menuFile.add(menuItem, nrofMenuItems -1);
    swtWindow.graphicThreadSwt.validate();
    
  }
   */
  
  
  
  /**inserts a new menu item in the file menu.
   * @param parent The menu to add it. Use {@link getFileMenu()} or {@link getHelpMenu()} 
   *        to add something to standard menus created with {@link #addStandardMenus(File)}.
   * @param position in menu, 1 is first, 0 is last, -1 is one before last etc. 
   * @param sText Text visible
   * @param mnemonic hotkey
   * @param action The action listener
   * @return the menu item to add something else or disable/enable etc. directly 
  protected JMenuItem addMenuItem(JMenu parent, int position, String sText, char mnemonic, ActionListener action)
  { JMenuItem menuItem = new JMenuItem(sText);
    menuItem.setMnemonic(mnemonic);
    menuItem.addActionListener(action);
    addMenuItem(parent, position, menuItem);
    return menuItem;
  }  
   */
    
  /**adds any menuItem.
   * @param parent The menu to add it. Use {@link getFileMenu()} or {@link getHelpMenu()} 
   *        to add something to standard menus created with {@link #addStandardMenus(File)}.
   * @param position in menu, 1 is first, 0 is last, -1 is one before last etc. 
   * @param menuItem The item to add.
  protected void addMenuItem(JMenu parent, int position, JMenuItem menuItem)  
  { int positionInMenu;
    if(position >=1)
    { positionInMenu = position -1;  //0 is first.
    }
    else
    { int nrofMenuItems = parent.getMenuComponentCount(); //  .getComponentCount();
      positionInMenu = nrofMenuItems + position;
      if(positionInMenu < 0)throw new IndexOutOfBoundsException(" fault position =" + positionInMenu);
    }
    parent.add(menuItem, positionInMenu);
    swtWindow.graphicThreadSwt.validate();
  }
   */
  

  
  
  /**adds any checkboxmenuItem. The item is of the returned type. 
   * The state of the item is unchecked. 
   * @param parent The menu to add it. Use {@link getFileMenu()} or {@link getHelpMenu()} 
   *        to add something to standard menus created with {@link #addStandardMenus(File)}.
   * @param position in menu, 1 is first, 0 is last, -1 is one before last etc. 
   * @param menuItem The item to add.
   * @param sText Text visible
   * @param mnemonic hotkey
   * @param action The action listener
   * @return
  protected JCheckBoxMenuItem addCheckBoxMenuItem(JMenu parent, int position, String sText, char mnemonic, ChangeListener action)
  { JCheckBoxMenuItem menuItem;
    menuItem = new JCheckBoxMenuItem(sText);
    //menuItem.setSelected(true);
    menuItem.addChangeListener(action);
    addMenuItem(parent, position, menuItem);
    return menuItem;
  }
   */
  
  
  
  
  protected void setOutputWindow()
  { addOutputFrameArea(1, 3, 3, 1);   //x, y, dx, dy);
  }
  
  
  
  /**Sets the output window to a defined area. .
   * Adds the edit-menu too. 
   * @param xArea 1 to 3 for left, middle, right, See {@link #setFrameAreaBorders(int, int, int, int)}
   * @param yArea 1 to 3 for top, middle, bottom
   * @param dxArea 1 to 3 for 1 field to 3 fields to right.
   * @param dyArea 1 to 3 for 1 field to 3 field to bottom
   */
  protected GralPanelContent addOutputFrameArea(int xArea, int yArea, int dxArea, int dyArea)
  { int widgetStyle = SWT.H_SCROLL | SWT.V_SCROLL;
    TextPanelSwt textPanel = new TextPanelSwt("output", swtWindow.graphicThreadSwt.windowSwt, widgetStyle, swtWindow.gralMng); //gralDevice);
    super.outputBox = textPanel.textAreaOutput;
    //textAreaOutput = new WidgetsSwt.TextBoxSwt(swtWindow.graphicThreadSwt, widgetStyle);
    //textAreaPos = new Position(textAreaOutput);
    //textAreaPos.x = x; textAreaPos.y = y; textAreaPos.dx = dx; textAreaPos.dy = dy; 
    //textAreaOutput.setSize(350,100); //swtWindow.graphicThreadSwt.get)
    //textAreaOutput.setBounds(x, y, dx,dy);
    //((WidgetsSwt.TextBoxSwt)textAreaOutput).text.setFont(new Font(swtWindow.guiDevice, "Monospaced",11, SWT.NORMAL));
    try{ textPanel.append("output...\n"); } catch(IOException exc){}
    //textAreaOutputPane = new JScrollPane(textAreaOutput, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    //pane.setSize(800,300);
    addFrameArea(xArea, yArea, dxArea, dyArea, textPanel);
    
    //textAreaPos.setBounds(textAreaOuptutPane, swtWindow.graphicFramePos);
    //swtWindow.graphicThreadSwt.getContentPane().add(textAreaOuptutPane);
/*
    { menuOutput = new JMenu("Edit");//##a
      menuOutput.setMnemonic('E');
      addMenu(menuOutput);
      { JMenuItem item = new JMenuItem("Clean");
        item.setMnemonic(KeyEvent.VK_N);
        item.addActionListener(this.new ActionClearOutput());
        menuOutput.add(item);
      }
      { JMenuItem item = new JMenuItem("Copy");
        item.setMnemonic(KeyEvent.VK_C);
        item.addActionListener(this.new ActionAbout());
        menuOutput.add(item);
      }
    }
*/      
    
    swtWindow.graphicThreadSwt.windowSwt.update();
    return textPanel;
  }

  
  
  
  
  
/*
  protected void setJPanel(JPanel panel)
  { 
	  if (panel == null)
		  return;
	  
	  paintArea = panel;
	  swtWindow.graphicThreadSwt.getContentPane().add(paintArea);
	  //swtWindow.graphicThreadSwt.validate();
	  paintArea.repaint();
  }
*/  
  
  
  
  /** Overloads MainCmd.writeDirectly. This method may be overloaded by the user
   * if it has a better way to show infos.*/
  protected void writeDirectly(String sInfo, short kind)  //##a
  { if(textAreaOutput != null){
      if(Thread.currentThread().getId() == gralDevice.graphicThread.guiThreadId){
	  	  try{
          if((kind & MainCmd.mNewln_writeInfoDirectly) != 0)
  	      { textAreaOutput.append("\n");
  	      }
  	      textAreaOutput.append(sInfo);
  	      int nrofLines = textAreaOutput.getNrofLines();
  	      textAreaOutput.viewTrail();
  	      //textAreaOutput.setCaretPosition(nrofLines-1);
  	      swtWindow.graphicThreadSwt.windowSwt.update();
	  	  } catch(IOException exc){ getGralMng().writeLog(0, exc); }
      } else {  
        //queue the text
      	outputTexts.add(sInfo);
      	swtWindow.wakeup();
      }
  	}  
    else mainCmd.writeDirectly(sInfo, kind);     
  }
  
  /** Overloads MainCmd.writeDirectly. This method may be overloaded by the user
   * if it has a better way to show infos.
   * Writes an error line by console application directly to System.err.println
      with the String "EXCEPTION: " before and the exception message.
      The user can overwrite this method in the derived class of MainCmd to change to kind of output.
      @param sInfo Text to write in the new line after "EXCEPTION: ".
      @param exception Its getMessage will be written.
  */
  protected void writeErrorDirectly(String sInfo, Exception exception)
  { if(textAreaOutput != null)
    { try{
        textAreaOutput.append("\nEXCEPTION: " + sInfo + exception.getMessage());
        textAreaOutput.viewTrail();
        swtWindow.graphicThreadSwt.windowSwt.update();  //update in TextBox...
      }catch(IOException exc){ }
    }
    else mainCmd.writeErrorDirectly(sInfo, exception);     
  }

	
  @Override public void redraw(){  swtWindow.redraw(); }


  
  /** Gets the graphical frame. */
  public final Shell getitsGraphicFrame(){ return swtWindow.graphicThreadSwt.windowSwt; }
  
  /** Gets the graphical frame. */
  public final Composite getContentPane()
  {
    return swtWindow.graphicThreadSwt.windowSwt; //.getContentPane();
  }
  
  public Display getDisplay(){ return swtWindow.graphicThreadSwt.displaySwt; }
  
  //public final Device getGuiDevice(){ return guiDevice.get
  
  
  /**This method have to be called by user if the layout of the application is set. */
  public final void validateGraphic()
  { validateFrameAreas();
  }
  
  /** Adds a graphical component
  public final void addGraphicComponent(JComponent comp)
  { swtWindow.graphicThreadSwt.getContentPane().add(comp);
  }
  */
  
  
  @Override public boolean isRunning(){ return swtWindow.isRunning(); }

  
  
  /*
  @Override public boolean buildMainWindow(String sTitle, int x, int y, int dx, int dy)
  { return swtWindow.buildMainWindow(sTitle, x, y, dx, dy);
  }
  */
  

  
  /** This method is called when a standard file open dialog is done and a file
   * was selected. The user have to overload this method in its derived application class.
   * @param sFileName full path of selected file
   */
  public void actionFileOpen(File file)
  {
    mainCmd.writeInfo("action file open:" + file.getName());
  }
  



  void stop()
  { //to set breakpoint
  }




  @Override
  public boolean isWindowsVisible()
  { return swtWindow.isWindowsVisible();
  }




  @Override  public void setWindowVisible(boolean visible)
  { swtWindow.setWindowVisible(visible);
  }




  @Override public long getThreadIdGui()
  { return swtWindow.getThreadIdGui();
  }

  /*
  @Override public void buildMainWindow(String sTitle, int left, int top, int xSize, int ySize)
  { swtWindow.buildMainWindow(sTitle, left, top, xSize, ySize);
  }
  */
  
  //@Override public void addGuiBuildOrder(Runnable order){ swtWindow.addGuiBuildOrder(order); }


  @Override public GralWidgetMng getGralMng()
  { return gralDevice.gralMng;
  }
  
  @Override public GralRectangle getPixelPositionSize(){ return window.getPixelPositionSize(); }

  
}
















class GuiActionExit //implements ActionListener
{
  /**Association to the main class*/
  MainCmdSwt main;

  GuiActionExit(MainCmdSwt mainP) { this.main = mainP;}

  public void actionPerformed( int e) //ActionEvent e )
  { System.exit(0);
  }
}



class GuiActionAbout //implements ActionListener
{
  /**Association to the main class*/
  final MainCmdSwt main;

  GuiActionAbout(MainCmdSwt mainP) { main = mainP;}

  public void actionPerformed( int e) //ActionEvent e )
  { main.mainCmd.writeAboutInfo();
  }


  



}

                           