package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralPanel_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;

/**This is a org.eclipse.swt.widgets.Composite. 
 * It can contain some GUI-Elements like Button, Text, Label, Table etc from org.eclipse.swt.widgets.
 * But additional a grid is shown as background. 
 * This class is imaginary for the {@link org.vishia.gral.swt.SwtMng}
 * to show the grid for positions.
 * 
 * @author Hartmut Schorrig
 *
 */
public class SwtGridPanel extends SwtCanvasStorePanel
{
	
  /**Version, history and license.
   * <ul>
   * <li>2016-09-02 Hartmut chg: {@link #SwtGridPanel(GralPanelContent, Composite, int, Color, int, int, int, int, GralMng)} is no more invoked with a parent
   *   but the parent is gotten by itself in the constructor from the pos().panel. 
   *   Reason: The panel maybe set only after construction of the {@link GralWidget.ImplAccess}. It is not known before. 
   * <li>2011-06-00 Hartmut created
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
  public static final String sVersion = "2016-09-02";

	private static final long serialVersionUID = 6448419343757106982L;
	
  int xG, yG;
	
  int xS, yS;
  
	public SwtGridPanel(GralPanelContent wdgg, Composite xxxparent, int style, Color backGround, int xG, int yG, int xS, int yS, GralMng gralMng)
	{ super(wdgg);
	  GralWidget_ifc parentPanelifc = wdgg.pos().parent;
	  //GralPanelContent parentPanel = (GralPanelContent)parentPanelifc;
	  Composite parent = (Composite)parentPanelifc.getImpl().getWidgetImplementation();  
    Rectangle areaParent = parent.getClientArea();     
	  if(widgg.isTabbed()) {
      GralWidget.ImplAccess swtPanelifc = parentPanelifc.getImpl();
      final SwtPanel swtPanel;
      if(swtPanelifc instanceof SwtSubWindow) {
        //swtPanel = ((SwtSubWindow)swtPanelifc).swtPanel;   // access the SwtPanel data beside the SwtSubWindow.ImplAccess 
      } else {
        //swtPanel = null; //TODO
      }
      //parent = swtPanel.tabFolder;                         // from the GralPanel
      TabFolder tabFolder = (TabFolder)parent;             // The parent panel must be a Tab folder.
      TabItem tab = new TabItem(tabFolder, SWT.None);
      tab.setText(wdgg.getName());
      //The parent of the composite of a tab is the composite, which contains the TabFolder, not the TabFolder itself.
      //The TabFolder is not a swt.Control.
      this.swtCanvas = new SwtCanvasGridPanel(this, parent, style);
      tab.setControl(this.swtCanvas);
//      if(parentPanel._panel.pixelTab ==0) {                      // the first tab:
//        Rectangle areaTab = this.swtCanvas.getClientArea();// has automatically the max. size
//        parentPanel._panel.pixelTab = (short)(areaFolder.height - areaTab.height);
//      } else {
//      }
//      this.swtCanvas.setBounds(150, 150+parentPanel._panel.pixelTab, areaFolder.width-10, areaFolder.height -130 - parentPanel._panel.pixelTab );
    }
	  else {
     this.swtCanvas = new SwtCanvasGridPanel(this, parent, style);
     this.swtCanvas.setBounds(areaParent);
	  }
  	super.panelSwtImpl = this.swtCanvas;
    this.swtCanvas.addControlListener(this.resizeItemListener);
    this.swtCanvas.setData(this);
    this.swtCanvas.setLayout(null);
    this.currColor = this.swtCanvas.getForeground();
    this.swtCanvas.addPaintListener(this.swtCanvas.paintListener);
    if(backGround !=null) { this.swtCanvas.setBackground(backGround); }
	  setGridWidth(xG, yG, xS, yS);
	  this.swtCanvas.setVisible(true);  
	}
	
	public void setGridWidth(int xG, int yG, int xS, int yS)
	{
		this.xG = xG; this.yG = yG;
		this.xS = xS; this.yS = yS;
	}
	
	
	
	
	
	protected static class SwtCanvasGridPanel extends SwtCanvas
	{
	  private final SwtGridPanel mng;
	  
	  SwtCanvasGridPanel(SwtGridPanel storeMng, Composite parent, int style)
	  { super(storeMng, parent, style);
	    this.mng = storeMng;
	    
	  }
	  
	  
    @Override
    public void drawBackground(GC g, int x, int y, int dx, int dy) {
    	//NOTE: forces stack overflow because calling of this routine recursively: super.paint(g);
    	Color colorBack = getBackground();
    	Device device = colorBack.getDevice();
    	Color color1 = new Color(device, colorBack.getRed() ^ 0x08, colorBack.getGreen() ^ 0x08, colorBack.getBlue() ^0x08);
    	Color color2 = new Color(device, colorBack.getRed() ^ 0x10, colorBack.getGreen() ^ 0x10, colorBack.getBlue() ^0x10);
    	int xGrid = mng.xG;
    	int xS1 = mng.xS;
    	while(xGrid < dx){
    		if(--xS1 <=0){
    			xS1 = mng.xS; g.setForeground(color2);
    		} else { g.setForeground(color1);
    		}
    		g.drawLine(xGrid, 0, xGrid, dy);
    		xGrid += mng.xG;
    	}
    	int yGrid = mng.yG;
    	int yS1 = mng.yS;
    	while(yGrid < dy){
    		if(--yS1 <=0){
    			yS1 = mng.yS; g.setForeground(color2);
    		} else { g.setForeground(color1);
    		}
    		g.drawLine(0, yGrid, dx, yGrid);
    		yGrid += mng.yG;
    	}
    	super.drawBackground(g, x, y, dx, dy);
    }	
	}
	
	
	
  void stop(){} //debug
  
}
