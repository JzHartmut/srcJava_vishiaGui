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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vishia.gral.area9.GuiMainAreaBase;
import org.vishia.gral.area9.GuiMainAreaifc;
import org.vishia.gral.ifc.GuiDispatchCallbackWorker;
import org.vishia.gral.ifc.UserActionGui;
import org.vishia.gral.widget.TextBoxGuifc;
import org.vishia.gral.widget.WidgetCmpnifc;
import org.vishia.gral.widget.Widgetifc;
import org.vishia.mainCmd.MainCmd;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.util.MinMaxTime;
import org.vishia.windows.WindowMng;


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

  
  final MainCmd mainCmd;
  
	public interface GuiBuild
	{
		/**Called in the build phase
		 * @param gui
		 */
		void build(Display gui);
		
		/**Called in the dispatch-event phase. In this phase, calls to graphic elements can be done
		 * or the graphic can be changed. */
		void change();
	}
	
	
  /**Queue of dispatchListeners. Any instance will be invoked in the dispatch-loop.
   * See {@link #addDispatchListener(Runnable)}. 
   * Hint: A dispatch-listener can be run only one time, than it should delete itself in its run-method.*/
  ConcurrentLinkedQueue<GuiDispatchCallbackWorker> dispatchListeners = new ConcurrentLinkedQueue<GuiDispatchCallbackWorker>();
  
  /**Set if any external event is set. Then the dispatcher shouldn't sleep after finishing dispatching. 
   * This is important if the external event occurs while the GUI is busy in the operation-system-dispatching loop.
   */
  private AtomicBoolean extEventSet = new AtomicBoolean(false);

  /**Queue of orders for building the graphic. Any instance will be invoked 
   * after initializing the graphic and before entry in the dispatch-loop.
   * See {@link #addGuiBuildOrder(Runnable)}. */
  ConcurrentLinkedQueue<Runnable> buildOrders = new ConcurrentLinkedQueue<Runnable>();
  

	
	
	
  /**This thread can (not have to be) used for all graphic actions. If the user calls
   * 
   * 
   */
  Thread guiThread = new Thread("GUI-SWT")
  {
  	@Override public void run()
  	{
  		initGrafic(sTitle, 0, 0, xSize, ySize);
      dispatch();
  		//synchronized(this){ notify(); }  //to weak up waiting on configGrafic().
  	}
  };
	
  /**All main components of the Display in each FrameArea. */
  private Widgetifc[][] componentFrameArea = new Widgetifc[3][3];

  /**A little control to capture the mouse position for movement of area borders. */
  private Control[] yAreaMover = new Control[2];
  
  /**Position of the FrameArea borders in percent. 
   * [0] is always 0, [1] and [2] are the given borders, [3] is always 100.
   * It is because lower and higher bound should be accessed always without tests.
   * Use area +1, because it is a Off-by-one problem */
  private byte xpFrameArea[] = new byte[4],
               ypFrameArea[] = new byte[4];
  
  /**spread of each frame area in x direction.
   * It it is -1, than the area is occupied by another area.
   * If it is 0, the area is free.
   * 1..3 are the number of areas in horizontal direction.
   */
  private byte[][][] dxyFrameArea = new byte[3][3][2]; 
  
  /**True if the startup is done. */
  boolean bStarted = false; 
 
  boolean bWaitStart = false;
  
  /**Size of display window. */
  int xSize,ySize;
  /**left and top edge of display-window. */
  int xPos, yPos;
  
  /**Title of display window. */
  String sTitle;
  
  
  /**Number of pixels per percent unit of size, set if {@link #validateFrameAreas()} was called. */
  private float pixelPerXpercent = 1, pixelPerYpercent =1;  
  
  /** The interface to the application. */
  //private final MainApplicationWin_ifc application;
  
  /**If true, than the application is to be terminate. */
  protected boolean bTerminated = false;

  protected Display guiDevice;
  
  private boolean isWakedUpOnly;
  
  /**The id of the thread, which created the display. 
   * It is to check whether gui-commands should be queued or not. */
  //protected long idThreadGui;
  
  /** The frame of the Window in the GUI (Graphical Unit Interface)*/
  protected Shell graphicFrame;

  final protected StringBuffer sbWriteInfo = new StringBuffer(1000);  //max. 1 line!
  
  /** If it is set, the writeInfo is redirected to this.*/
  protected TextBoxGuifc textAreaOutput = null;
  
  /**The file menuBar is extendable. */
  private Menu menuBar;
  
  /**The file menu is extendable. */
  private Menu menuFile;
  
  /**The output menu is extendable. */
  private Menu menuOutput;
  
  /**The help menu is extendable. */
  private Menu menuHelp;
  
  private static class MenuEntry
  {
    String name;
    /**If it is a superior menu item, the menu below. Else null. */
    Menu menu;
    Map<String, MenuEntry> subMenu;
  }
  
  Map<String, MenuEntry> menus = new TreeMap<String, MenuEntry>();
  
  
  //protected JScrollPane textAreaOutputPane;
  
  /** Paint Methoden */
  //private Canvas paintArea = null;
  
  /** If it is set, it is a area for some Buttons, edit windows and others.*/
  private Composite mainDialog = null;
  
  /** Current Directory for file choosing. */
  File currentDirectory = null;
  
  UserActionGui actionFile;
  
  /**Set on call of {@link #setStandardMenus(File)} to add in in the graphic thread. */
  private boolean bSetStandardMenus;
  
  Queue<String> outputTexts = new ConcurrentLinkedQueue<String>();
  
  
  /** set to true to exit in main*/
	private boolean bExit = false;
	
	
	/**The windows-closing event handler. It is used private only, but public set because documentation. */
	public final class WindowsCloseListener implements Listener{
		/**Invoked when the window is closed; it sets {@link #bExit}, able to get with {@link #isRunning()}.
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 */
		@Override public void handleEvent(Event event) {
			bExit = true;
		}
	}

	/**Instance of windowsCloseHandler. */
	private final WindowsCloseListener windowsCloseListener = new WindowsCloseListener(); 
	
	class ActionFileOpen implements SelectionListener
  { @Override public void widgetSelected(SelectionEvent e)
    { 
      FileDialog fileChooser = new FileDialog(graphicFrame);
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
      String[] sHelpText = new String[mainCmd.listHelpInfo.size()];
      int ix = 0;
      for(String line: mainCmd.listHelpInfo){
        sHelpText[ix++] = line;
      }
      InfoBox helpDlg = new InfoBox(graphicFrame, "Help", sHelpText, false);//main.writeInfoln("action!");
      helpDlg.open();
      helpDlg.setVisible(true);
      stop();
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
      String[] sText = new String[mainCmd.listAboutInfo.size()];
      int ix = 0;
      for(String line: mainCmd.listAboutInfo){
        sText[ix++] = line;
      }
      InfoBox aboutDlg = new InfoBox(graphicFrame, "...about", sText, false);//main.writeInfoln("action!");
      
      aboutDlg.setVisible(true);
      stop();
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
  

  
  class ActionUserMenuItem implements SelectionListener
  { 
    final UserActionGui action;
    
    public ActionUserMenuItem(UserActionGui action)
    { this.action = action;
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
      // TODO Auto-generated method stub
      
    }
  
    @Override
    public void widgetSelected(SelectionEvent e)
    { action.userActionGui("", null, (Object[])null);
    }
  }
  
  
  
  ShellListener mainComponentListerner = new ShellListener()
  {

    		
		@Override
		public void shellActivated(ShellEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void shellClosed(ShellEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void shellDeactivated(ShellEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void shellDeiconified(ShellEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void shellIconified(ShellEvent e) {
			// TODO Auto-generated method stub
			
		}
    
  };
  
  
  
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
  
  public MainCmdSwt(MainCmd cmdP, GralDeviceSwt gralDevice) //String[] args)
  { //super(args);
    super(gralDevice);
    mainCmd = cmdP;
  }
  
  /**Sets the title and size before initialization.
   * @param sTitle
   * @param xSize
   * @param ySize
   */
  public void setTitleAndSize(String sTitle, int left, int top, int xSize, int ySize)
  { //assert(!bStarted);
		this.xSize = xSize;
		this.ySize = ySize;
		this.xPos = left;
		this.yPos = top;
		this.sTitle = sTitle;
		if(bStarted){
			if(xSize < 0 || ySize < 0){
				graphicFrame.setFullScreen(true);
			} else {
			  graphicFrame.setBounds(left,top, xSize, ySize );  //Start position.
			}  
		  graphicFrame.setText(sTitle);
		}
  }
  
  /**Initialize, in the graphic thread. 
   * @deprecated.
   * @param sTitle
   */
  protected final void initGrafic(String sTitle)
  { initGrafic(sTitle, 100, 100, 800, 500);
  }
    
  protected final void initGrafic(String sTitle, int left, int top, int xSize, int ySize)
  {
  	if(!bStarted){
  		setTitleAndSize(sTitle, left, top, xSize, ySize);
  		gralDevice.guiThreadId = Thread.currentThread().getId();
      guiDevice = new Display ();
      guiDevice.addFilter(SWT.Close, windowsCloseListener);
      graphicFrame = new Shell(guiDevice); //, SWT.ON_TOP | SWT.MAX | SWT.TITLE);
      graphicFrame.open();
    	
      //graphicFramePos = new Position(graphicFrame.getContentPane());
      //graphicFramePos.set(0,0,xSize,ySize);
      // main = this;
      graphicFrame.setText(sTitle);
      //graphicFrame.getContentPane().setLayout(new BorderLayout());
      //graphicFrame.addWindowListener(new WindowClosingAdapter(true));
      //graphicFrame.setSize( xSize, ySize );
      if(xSize == -1 || ySize == -1){
      	graphicFrame.setFullScreen(true);
      } else {
        graphicFrame.setBounds(left,top, xSize, ySize );  //Start position.
      }
      graphicFrame.setVisible( true ); 

      //graphicFrame.getContentPane().setLayout(new FlowLayout());
      graphicFrame.setLayout(null);
      graphicFrame.addShellListener(mainComponentListerner);
      graphicFrame.addControlListener(resizeListener);
      setFrameAreaBorders(30,70,30,70);
      if(bSetStandardMenus){
        setStandardMenusGThread(currentDirectory, actionFile);
      }
      if(outputArea != null){
        int xArea = outputArea.charAt(0) - 'A' +1;
        int yArea = outputArea.charAt(1) - '0';
        int dxArea = outputArea.charAt(2) - 'A' +1 - xArea +1;
        int dyArea = outputArea.charAt(3) - '0' - yArea +1;
        addOutputFrameArea(xArea, yArea, dxArea, dyArea);
      }
      for(Runnable build: buildOrders){
        build.run();
      }
      synchronized(guiThread){
        if(bWaitStart){
          guiThread.notify(); 
        }
        bStarted = true;
      }
    } else throw new IllegalArgumentException("graphic is configured already, do it one time only.");
  	
  }
  
  
  /**Sets the divisions of the frame. The frame is divide into 9 parts,
   * where two horizontal and two vertical lines built them:
   * <pre>
   * +=======+===============+===========+
   * |       |               |           | 
   * +-------+---------------+-----------+ 
   * |       |               |           | 
   * |       |               |           | 
   * +-------+---------------+-----------+ 
   * |       |               |           | 
   * |       |               |           | 
   * +=======+===============+===========+
   * </pre>
   * 
   * @param x1p percent from left for first vertical divide line.
   * @param x2p percent from left for second vertical divide line.
   * @param y1p percent from left for first horizontal divide line.
   * @param y2p percent from left for first horizontal divide line.
   */
  @Override public void setFrameAreaBorders(int x1p, int x2p, int y1p, int y2p)
  { xpFrameArea[0] = 0;
    xpFrameArea[1] = (byte)x1p;
    xpFrameArea[2] = (byte)x2p;
    xpFrameArea[3] = 100;
    ypFrameArea[0] = 0;
    ypFrameArea[1] = (byte)y1p;
    ypFrameArea[2] = (byte)y2p;
    ypFrameArea[3] = 100;
    if(bStarted)
    { validateFrameAreas();
    }
  }
  
  
  
  /**Starts the execution of the graphic initializing in a own thread.
   * The following sets should be called already:
   * <pre>
   * setTitleAndSize("SES_GUI", 800, 600);  //This instruction should be written first to output syntax errors.
     setStandardMenus(new File("."));
     setOutputArea("A3C3");        //whole area from mid to bottom
     setFrameAreaBorders(20, 80, 75, 80);
     </pre>
   * They sets only some values, no initializing of the graphic is executed till now.
   * <br><br>
   * The graphic thread inside this class {@link #guiThread} initializes the graphic.
   * All other GUI-actions should be done only in the graphic-thread. 
   * The method {@link #addDispatchListener(Runnable)} is to be used therefore.
   * <br><br>
   * The method to use an own thread for the GUI is not prescribed. The initialization
   * and all actions can be done in the users thread too. But then, the user thread
   * have to be called {@link #dispatch()} to dispatch the graphic events. It is busy with it than.   
   * @return true if started
   */
  public boolean startGraphicThread(){
  	if(bStarted) throw new IllegalStateException("it is started already.");
  	guiThread.start();
  	synchronized(guiThread){ 
    	while(!bStarted){
    	  bWaitStart = true;
    	  try{guiThread.wait(5000);} catch(InterruptedException exc){}
    	}
    	bWaitStart =false;
    }
  	return bStarted;
  }
  
  
  /**Sets a Component into a defined area. See {@link #setFrameAreaBorders(int, int, int, int)}.
	 * It should be called only in the GUI-Thread.
	 * @param xArea 1 to 3 for left, middle, right
	 * @param yArea 1 to 3 for top, middle, bottom
	 * @param dxArea 1 to 3 for 1 field to 3 fields to right.
	 * @param dyArea 1 to 3 for 1 field to 3 field to bottom
	 * @param component The component.
	 * @throws IndexOutOfBoundsException if the arguments are false or the area is occupied already.
	 */
	@Override public final void addFrameArea(int xArea, int yArea, int dxArea, int dyArea, Widgetifc component)
	throws IndexOutOfBoundsException
	{ //int idxArea = (x -1) + 3 * (y -1);
	  //Composite component = new Composite(graphicFrame, SWT.NONE);
		if(  xArea <1 || xArea > componentFrameArea[0].length
	    || dxArea < 1
	    || xArea+dxArea-2 > componentFrameArea[0].length
	    || yArea <1 || yArea > componentFrameArea.length
	    || dyArea < 1
	    || yArea+dyArea-1 > componentFrameArea.length
	    ) 
	    throw new IndexOutOfBoundsException("failed argument");
	  for(int idxArea = xArea-1; idxArea <= xArea + dxArea -2; idxArea++)
	  { for(int idyArea = yArea-1; idyArea <= yArea + dyArea -2; idyArea++)
	    { if(dxyFrameArea[idyArea][idxArea][0] != 0) throw new IndexOutOfBoundsException("area occupied already");
	    }
	  }
	  for(int idxArea = xArea-1; idxArea <= xArea + dxArea -2; idxArea++)
	  { for(int idyArea = yArea-1; idyArea <= yArea + dyArea -2; idyArea++)
	    { dxyFrameArea[idyArea][idxArea][0] = -1; //ocuupy it.
	    }
	  }
	  dxyFrameArea[yArea-1][xArea-1][0] = (byte)dxArea;
	  dxyFrameArea[yArea-1][xArea-1][1] = (byte)dyArea;
	  //JScrollPane scrollPane = new JScrollPane(component);
	  //scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	  //scrollPane.setViewportView(component);
	  componentFrameArea[yArea-1][xArea-1] = component; //scrollPane;
	  setBoundsForFrameArea(xArea-1, yArea-1);
	  //scrollPane.validate();
	  //graphicFrame.add(component); //scrollPane);
	  if(yAreaMover[1] == null){
	  	yAreaMover[1] = new Canvas(graphicFrame, SWT.None);
	  	yAreaMover[1].setSize(10,10);
	  	yAreaMover[1].setBackground(guiDevice.getSystemColor(SWT.COLOR_GREEN));
	  }
	  if(bStarted)
	  { validateFrameAreas();
	  }
	  ((Control)component.getWidget()).addMouseListener(mouseListener);
	  //return component;
	}

	
	public final void setStandardMenus(File openStandardDirectory, UserActionGui actionFile)
	{ this.currentDirectory = openStandardDirectory;
	  this.actionFile = actionFile;
    this.bSetStandardMenus = true;
		
	}
	
	
	/** Adss some standard meues in the menue bar.
   * 
   * @param openStandardDirectory may be null or a directory as default for "file-open" menue.
   */
  
  public final void setStandardMenusGThread(File openStandardDirectory, UserActionGui actionFile)
  { this.currentDirectory = openStandardDirectory;
    this.actionFile = actionFile;
    { //create the menue
      menuBar = new Menu(graphicFrame, SWT.BAR);
      graphicFrame.setMenuBar(menuBar);
      { MenuItem menuItemFile = new MenuItem(menuBar, SWT.CASCADE);
        menuItemFile.setText("File");
        menuItemFile.setAccelerator(SWT.CONTROL | 'F');
        menuFile = new Menu(graphicFrame, SWT.DROP_DOWN);
        menuItemFile.setMenu(menuFile);
      	
        if(openStandardDirectory != null)
        { { MenuItem item = new MenuItem(menuFile, SWT.None); item.setText("Open");
            item.setAccelerator(SWT.CONTROL | 'O');
            item.addSelectionListener(this.new ActionFileOpen());
            //menuFile.add(item);
          }
          { MenuItem item = new MenuItem(menuFile, SWT.None); item.setText("Close");
            item.setAccelerator(SWT.CONTROL | 'C');
            item.addSelectionListener(this.new ActionFileClose());
            //menuFile.add(item);
          }
          { MenuItem item = new MenuItem(menuFile, SWT.None); item.setText("Save");
            item.setAccelerator(SWT.CONTROL | 'S');
            item.addSelectionListener(this.new ActionFileSave());
            //menuFile.add(item);
          }
        }  
        { MenuItem item = new MenuItem(menuFile, SWT.None); item.setText("Exit");
          item.setAccelerator(SWT.CONTROL | 'X');
          //item.addActionListener(this.new ActionFileSave());
          //menuFile.add(item);
        }
        
      }
      { MenuItem menuItemHelp = new MenuItem(menuBar, SWT.CASCADE);
        menuItemHelp.setText("Help");
        menuItemHelp.setAccelerator(SWT.CONTROL | 'H');
        menuHelp = new Menu(graphicFrame, SWT.DROP_DOWN);
        menuItemHelp.setMenu(menuHelp);
      	{ MenuItem item = new MenuItem(menuHelp, SWT.None); item.setText("Help");
          item.setAccelerator(SWT.CONTROL | 'H');
          item.addSelectionListener(this.new ActionHelp());
          //menuHelp.add(item);
        }
        { MenuItem item = new MenuItem(menuHelp, SWT.None); item.setText("About");
          item.setAccelerator(SWT.CONTROL | 'A');
          item.addSelectionListener(this.new ActionAbout());
          //menuHelp.add(item);
        }
        
      }
      //graphicFrame.setJMenuBar(menuBar);
      //graphicFrame.setVisible( true );
      graphicFrame.update();
    }
    
  }
  
  
  @Override public void addMenuItemGThread(String namePath, UserActionGui action)
  {
    String[] names = namePath.split("/");
    Menu parentMenu = menuBar;
    Map<String, MenuEntry> menustore = menus;
    int ii;
    for(ii=0; ii<names.length-1; ++ii){
      String name = names[ii];
      MenuEntry menuEntry = menustore.get(name);
      if(menuEntry == null){
        menuEntry = new MenuEntry();
        menustore.put(name, menuEntry);
        menuEntry.name = name;
        menuEntry.subMenu = new TreeMap<String, MenuEntry>();
        MenuItem item = new MenuItem(parentMenu, SWT.CASCADE);
        item.setText(name);
        menuEntry.menu = new Menu(graphicFrame, SWT.DROP_DOWN);
        item.setMenu(menuEntry.menu);
      }
      menustore = menuEntry.subMenu;
      parentMenu = menuEntry.menu;
    }
    String name = names[ii];
    MenuItem item = new MenuItem(parentMenu, SWT.None); 
    item.setText(name);
    //item.setAccelerator(SWT.CONTROL | 'S');
    item.addSelectionListener(this.new ActionUserMenuItem(action));
    ///
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
  
  
  /**Adds a order for building the gui. The order will be execute one time after intializing the Graphic
   * and before entry in the dispatch loop. After usage the orders will be removed,
   * to force garbage collection for the orders.
   * @param order
   */
  public void addGuiBuildOrder(Runnable order)
  { buildOrders.add(order);
  }
  
  /**Adds a listener, which will be called in the dispatch loop.
   * @param listener
   */
  @Override public void addDispatchListener(GuiDispatchCallbackWorker listener)
  { dispatchListeners.add(listener);
		//it is possible that the GUI is busy with dispatching and doesn't sleep yet.
    //therefore:
    extEventSet.getAndSet(true);
  	guiDevice.wake();  //to wake up the GUI-thread, to run the listener at least one time.
  }
  
  
  
  /**Removes a listener, which was called in the dispatch loop.
   * @param listener
   */
  public void removeDispatchListener(GuiDispatchCallbackWorker listener)
  { dispatchListeners.remove(listener);
  }
  
  
  /** Adds a complete pull down menu at the last but one position.
   * In standard menu technic the first position is the file menu and the
   * last position is the help menu. Both are added by calling addStandardMenus().
   * The user menus should be added between this both pull-down menus. 
   * @param menu A user's menu.
   */
  protected void addMenu(Menu menu, String text, char accelerator)
  {
    Menu menuBar = graphicFrame.getMenuBar();
    int nrofMenus = menuBar.getItemCount();  //ComponentCount();
    
    MenuItem menuItem = new MenuItem(menuBar, SWT.DROP_DOWN);
    menuItem.setText(text);
    menuItem.setAccelerator(SWT.CONTROL | accelerator);
    menuItem.setMenu(menu);
    graphicFrame.update();
  }
  
  
  /** Adds a complete pull down menu before the last position.
   * In standard menu technic the first position is the file menu and the
   * last position is the help menu. Both are added by calling addStandardMenus().
   * The user menus should be added between this both pull-down menus. 
   * @param menu A user's menu.
   */
  protected Menu addMenu(String text, char accelerator)
  {
    Menu menuBar = graphicFrame.getMenuBar();
    int nrofMenus = menuBar.getItemCount();  //ComponentCount();
    
    MenuItem menuItem = new MenuItem(menuBar, SWT.DROP_DOWN);
    menuItem.setText(text);
    menuItem.setAccelerator(SWT.CONTROL | accelerator);
    Menu menu = new Menu(graphicFrame, SWT.DROP_DOWN);
    menuItem.setMenu(menu);
    //graphicFrame.update();
    return menu;
  }

  
  /** Adds a menu instance to a present pull-down-menu at the last but one position.
   * This method is ...
   * @param menuItem A user's menuItem.
   * 
  protected void addMenuItem(MenuItem menuItem, int idx)
  {
    Menu menuBar = graphicFrame.getMenuBar();
    MenuItem[] items = menuBar.getItems();
    MenuItem barItem = items[idx];
    Menu menuBarEntry = barItem.getMenu();
    int nrofMenuItems = menuBarEntry.getItemCount();
    menuItem.set
    menu.add(menuItem, nrofMenuItems -1);
    graphicFrame.validate();
  }
   */
  
  
  
  /**
   * @param menuItem
  protected void addFileMenuItem(JMenuItem menuItem)
  {
    int nrofMenuItems = menuFile.getMenuComponentCount(); //  .getComponentCount();
    menuFile.add(menuItem, nrofMenuItems -1);
    graphicFrame.validate();
    
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
    graphicFrame.validate();
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
  protected void addOutputFrameArea(int xArea, int yArea, int dxArea, int dyArea)
  { int widgetStyle = SWT.H_SCROLL | SWT.V_SCROLL;
  	textAreaOutput = new WidgetsSwt.TextBoxSwt(graphicFrame, widgetStyle);
    //textAreaPos = new Position(textAreaOutput);
    //textAreaPos.x = x; textAreaPos.y = y; textAreaPos.dx = dx; textAreaPos.dy = dy; 
    //textAreaOutput.setSize(350,100); //graphicFrame.get)
    //textAreaOutput.setBounds(x, y, dx,dy);
    ((WidgetsSwt.TextBoxSwt)textAreaOutput).text.setFont(new Font(guiDevice, "Monospaced",11, SWT.NORMAL));
    textAreaOutput.append("output...\n");
    //textAreaOutputPane = new JScrollPane(textAreaOutput, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    //pane.setSize(800,300);
    addFrameArea(xArea, yArea, dxArea, dyArea, textAreaOutput);
    
    //textAreaPos.setBounds(textAreaOuptutPane, graphicFramePos);
    //graphicFrame.getContentPane().add(textAreaOuptutPane);
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
    
    graphicFrame.update();
  }

/*
  protected void setJPanel(JPanel panel)
  { 
	  if (panel == null)
		  return;
	  
	  paintArea = panel;
	  graphicFrame.getContentPane().add(paintArea);
	  //graphicFrame.validate();
	  paintArea.repaint();
  }
*/  
  
  protected void validateFrameAreas()
  {
    if(!bStarted) return;
    Point size = graphicFrame.getSize();
    int xWidth = size.x -6; //graphicFrame.getWidth();
    int yWidth = size.y -53; //graphicFrame.getHeight() - 50;  //height of title and menu TODO calculate correctly
    //Control content = graphicFrame.getContentPane();
    //xWidth = content.getWidth();
    //yWidth = content.getHeight();
    pixelPerXpercent = xWidth / 100.0F;
    pixelPerYpercent = yWidth / 100.0F;
    

    for(int idxArea = 0; idxArea <= 2; idxArea++)
    { for(int idyArea = 0; idyArea <= 2; idyArea++)
      { Widgetifc component = componentFrameArea[idyArea][idxArea];
        if(component !=null)
        { Control control = (Control)component.getWidget();
          setBoundsForFrameArea(idxArea, idyArea);
          control.update();
          Rectangle bounds = control.getBounds();
          control.redraw(bounds.x, bounds.y, bounds.width, bounds.height, true);
    } } }
    for(int ixMover = 0; ixMover < yAreaMover.length; ++ixMover){
    	if(yAreaMover[ixMover] != null){
    		int yp = (int)(ypFrameArea[ixMover+1] * pixelPerYpercent);
    		//TODO it doesn't work yet, not visible, why?:
    		yAreaMover[ixMover].setBounds(10,yp,10,10);
    		yAreaMover[ixMover].update(); 		
    		yAreaMover[ixMover].redraw(); 		
      }
    }
    //graphicFrame.update();
    //graphicFrame.redraw();
    //graphicFrame.update();
    
  }
 
  
  private static class CntSleep
  {
  	long lastTime;
  	long minMillisecBetweenSleep= Long.MAX_VALUE;
  	long maxMillisecBetweenSleep= 0;
  	double avMillisecBetweenSleep = 0.0;
  	double percentActive = 0.0;
  };
  
  private CntSleep cntSleep;
  
  
  MinMaxTime checkTimes = new MinMaxTime();
  
  
  public void wakeup(){
  	guiDevice.wake();
  	isWakedUpOnly = true;
  }
  
  public boolean isWakedUpOnly(){ return isWakedUpOnly; }
  
  
  public void dispatch()
  {
  	checkTimes.init();
  	checkTimes.adjust();
  	checkTimes.cyclTime();
		while (! (bExit = graphicFrame.isDisposed ())) {
			while (guiDevice.readAndDispatch ()){
				//isWakedUpOnly = false;  //after 1 event, it may be wakeUp, set if false.
			}
			checkTimes.calcTime();
			isWakedUpOnly = false;
			//System.out.println("dispatched");
			if(!extEventSet.get()) {
				guiDevice.sleep ();
			}
			if(!bExit){
				extEventSet.set(false); //the list will be tested!
		  	if(isWakedUpOnly)
		  		stop();
				//it may be waked up by the operation system or by calling Display.wake().
		  	//if wakeUp() is called, isWakedUpOnly is set.
				checkTimes.cyclTime();
				String line;
				while((line = outputTexts.poll())!=null){
					writeDirectly(line, MainCmd.kInfoln_writeInfoDirectly);
				}
				for(GuiDispatchCallbackWorker listener: dispatchListeners){
				  //use isWakedUpOnly for run as parameter?
					///System.out.println("BeforeDispatch");
					listener.doBeforeDispatching(isWakedUpOnly);	
					///System.out.println("BeforeDispatch-ready");
				}
			}	
		}
		guiDevice.dispose ();
	  bExit = true;
  }
  
  
  
  
  public boolean isRunning(){ return !bExit; }
  
  
	  /**Exits the cmdline application with the maximum of setted exit error level.
	  This method should be called only on end of the application, never inside. If the user will abort
	  the application from inside, he should throw an exception instead. So an comprising application
	  may catch the exception and works beyond.
	  This method is not member of MainCmd_Ifc and is setted protected, because
	  the users Main class (UserMain in the introcuction) only should call exit.
	*/
	@Override public void exit()
	{ if(!bExit && !guiDevice.isDisposed()){ 
		  guiDevice.dispose();
	  }  
		System.exit(mainCmd.getExitErrorLevel());
	}
  
  
  
  /**Sets the bounds for the component, which is localized at the given area.
   * @param idxArea
   * @param idyArea
   */
  private void setBoundsForFrameArea(int idxArea, int idyArea)
  { Point size = graphicFrame.getSize();
    int xWidth = size.x -6; //graphicFrame.getWidth();
    int yWidth = size.y -53; //graphicFrame.getHeight() - 50;  //height of title and menu TODO calculate correctly
    //Control content = graphicFrame.getContentPane();
    //xWidth = content.getWidth();
    //yWidth = content.getHeight();
    pixelPerXpercent = xWidth / 100.0F;
    pixelPerYpercent = yWidth / 100.0F;
    int xf1 = xpFrameArea[idxArea];  //percent left
    int yf1 = ypFrameArea[idyArea];
    int dxf = dxyFrameArea[idyArea][idxArea][0]; //nr of occupied areas
    int dyf = dxyFrameArea[idyArea][idxArea][1];
    int xf2 = xpFrameArea[idxArea + dxf]; //percent right
    int yf2 = ypFrameArea[idyArea + dyf];
    
    //calculate pixel size for the component:
    int xp = (int)(xf1  * pixelPerXpercent);
    int yp = (int)(yf1  * pixelPerYpercent);
    int dxp = (int) ((xf2-xf1) * pixelPerXpercent);
    int dyp = (int) ((yf2-yf1) * pixelPerYpercent);
    Control component = (Control)componentFrameArea[idyArea][idxArea].getWidget();
    component.setBounds(xp,yp,dxp,dyp-6);
  }
  
  
  
  
  /** Overloads MainCmd.writeDirectly. This method may be overloaded by the user
   * if it has a better way to show infos.*/
  protected void writeDirectly(String sInfo, short kind)  //##a
  { if(textAreaOutput != null){
      if(Thread.currentThread().getId() == gralDevice.guiThreadId){
	  	  if((kind & MainCmd.mNewln_writeInfoDirectly) != 0)
	      { textAreaOutput.append("\n");
	      }
	      textAreaOutput.append(sInfo);
	      int nrofLines = textAreaOutput.getNrofLines();
	      textAreaOutput.viewTrail();
	      //textAreaOutput.setCaretPosition(nrofLines-1);
	      graphicFrame.update();
      } else {  
        //queue the text
      	outputTexts.add(sInfo);
      	guiDevice.wake();
      	extEventSet.set(true);
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
    { textAreaOutput.append("\nEXCEPTION: " + sInfo + exception.getMessage());
      textAreaOutput.viewTrail();
      graphicFrame.update();  //update in TextBox...
    }
    else mainCmd.writeErrorDirectly(sInfo, exception);     
  }
  
  @Override public MainCmd_ifc getMainCmd(){ return mainCmd; }
  
  /** Sets the graphic frame, called inside the derived class. 
   *  The derived class has to organize the graphical frame.
   *  @param frame The graphical frame.
   */
	protected final void setGraphicFrame(Shell frame)
  { this.graphicFrame = frame;
  }

  
  /** Gets the graphical frame. */
  public final Shell getitsGraphicFrame(){ return graphicFrame; }
  
  /** Gets the graphical frame. */
  public final Composite getContentPane()
  {
    return graphicFrame; //.getContentPane();
  }
  
  public Display getDisplay(){ return guiDevice; }
  
  //public final Device getGuiDevice(){ return guiDevice.get
  
  
  /**This method have to be called by user if the layout of the application is set. */
  public final void validateGraphic()
  { validateFrameAreas();
	  if(!bStarted){
		  bStarted = true;
		  //guiThread.start();
	  }
		
  }
  
  /** Adds a graphical component
  public final void addGraphicComponent(JComponent comp)
  { graphicFrame.getContentPane().add(comp);
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

                           