package org.vishia.mainGuiSwt;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.vishia.gral.base.GralWidgetMng;

/**This is a org.eclipse.swt.widgets.Composite. 
 * It can contain some GUI-Elements like Button, Text, Label, Table etc from org.eclipse.swt.widgets.
 * But additional a grid is shown as background. 
 * This class is imaginary for the {@link org.vishia.mainGuiSwt.SwtWidgetMng}
 * to show the grid for positions.
 * 
 * @author Hartmut Schorrig
 *
 */
public class GridPanelSwt extends CanvasStorePanelSwt
{
	
	private static final long serialVersionUID = 6448419343757106982L;
	
  int xG, yG;
	
  int xS, yS;
  
	public GridPanelSwt(String namePanel, Composite parent, int style, Color backGround, int xG, int yG, int xS, int yS, GralWidgetMng gralMng)
	{ super(namePanel, gralMng);
	  swtCanvas = new SwtCanvasGridPanel(this, parent, style);
    super.panelComposite = swtCanvas;
    swtCanvas.setData(this);
    swtCanvas.setLayout(null);
    currColor = swtCanvas.getForeground();
    swtCanvas.addPaintListener(paintListener);
    swtCanvas.addControlListener(resizeItemListener);
    swtCanvas.setBackground(backGround);
	  setGridWidth(xG, yG, xS, yS);
	}
	
	public void setGridWidth(int xG, int yG, int xS, int yS)
	{
		this.xG = xG; this.yG = yG;
		this.xS = xS; this.yS = yS;
	}
	
	
	
	
	
	protected static class SwtCanvasGridPanel extends SwtCanvas
	{
	  private final GridPanelSwt mng;
	  
	  SwtCanvasGridPanel(GridPanelSwt storeMng, Composite parent, int style)
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
