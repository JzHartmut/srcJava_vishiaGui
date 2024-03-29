package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.vishia.gral.base.GralCanvasStorage;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.Debugutil;



/**
 * This is a org.eclipse.swt.widgets.Composite. It can contain some GUI-Elements
 * like Button, Text, Label, Table etc from org.eclipse.swt.widgets. But
 * additional a grid is shown as background. This class is imaginary for the
 * {@link org.vishia.gral.swt.SwtMng} to show the grid for positions.
 *
 * @author Hartmut Schorrig
 *
 */
public class SwtGridPanel extends SwtPanel { //XXXSwtCanvasStorePanel {

  /**Version, history and license.
   * <ul>
   * <li>2022-09-25 refactoring of Grid lines. 
   *   Because the text is cleaned up with a defined code style, some more formally changes are done. 
   *   Especially more final dedications. 
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
   * If you intent to use this source without publishing its usage, you can get a
   * second license subscribing a special contract with the author.
   *
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final String sVersion = "2022-09-25";

  private static final long serialVersionUID = 6448419343757106982L;

  public SwtGridPanel(final GralPanelContent wdgg, final char whatis, final int style) {
    super(wdgg);
    final SwtMng swtMng = (SwtMng) wdgg.gralMng()._mngImpl;
    final Composite parent = SwtMng.getSwtParent(wdgg.pos());
    GralMng gralMng = wdgg.gralMng();
    
    StringBuilder sLog = new StringBuilder();
    
    //final Rectangle areaParent = parent.getClientArea();
//    if (wdgg.isTabbed()) {
//      final TabFolder tabFolder = new TabFolder(parent, 0);
//      //faulty: super.panelSwtImpl.setBounds(areaParent);  // The tab folder should fill the whole area. Without the setBounds the
//                                                 // TabFolder is not visible.
//      swtMng.setPosAndSizeSwt(wdgg.pos(), tabFolder,0,0);  //area of tabFolder adequate pos
//      SwtMng.storeGralPixBounds(this, tabFolder);          // store the pixel size in the ImplAccess level
//      final Font fontTab = new Font(swtMng.displaySwt, "Arial", 10, SWT.ITALIC);
//      tabFolder.setFont(fontTab);
//      super.panelSwtImpl = tabFolder;            // typed access in SwtPanel
//      super.wdgimpl = tabFolder;                 // unified access in GraLWidget
//      sLog.append("new swt.TabFolder "); wdgg.pos().toString(sLog);
//    } 
//    else {
      GralCanvasStorage canvasStore = wdgg.canvas();
      Composite panel;
      if(canvasStore !=null) {
        final SwtWdgCanvas swtCanvas = new SwtWdgCanvas(this, swtMng, canvasStore, parent, style);
        panel = swtCanvas;
        swtCanvas.addPaintListener(swtCanvas.paintListener);
      } else {
        panel = new Composite(parent, style);
      }
      super.panelSwtImpl = panel;             // typed access in SwtPanel
      super.wdgimpl = panel;                  // unified access in GraLWidget 
      
      if(whatis !='9') {
        //panel.addControlListener(this.resizeItemListener);
      }
      //panel.addFocusListener(super.focusListener);
      sLog.append("new swt.Panel "); wdgg.pos().toString(sLog);
      panel.setData(this);
      panel.setLayout(null);
      //this.XXXcurrColor = panel.getForeground();
      panel.setBackground(swtMng.getColorImpl(wdgg.getBackColor(0)));
//    }
    //
//    if (parent instanceof TabFolder) {                     // This panel should be used as Tab of the parent TabFolder
//      final TabItem tab = new TabItem((TabFolder) parent, SWT.None);
//      if(wdgg._panel.labelTab !=null) {
//        tab.setText(wdgg._panel.labelTab);
//      } else {
//        tab.setText(wdgg.getName());
//      }
//      sLog.insert(0, ":new swt TabItem ").insert(0, tab.getText());
//      tab.setControl(this.panelSwtImpl);
//      Rectangle areaTab;
//      final GralWidgetBase_ifc parentPanelifc = wdgg.pos().parent;
//      final GralPanelContent parentPanel = (GralPanelContent) parentPanelifc;
//      final GralPanelContent.ImplAccess parentImplAccess = (GralPanelContent.ImplAccess) parentPanel.getImplAccess();
//      areaTab = super.panelSwtImpl.getClientArea();
//      @SuppressWarnings("unused") Rectangle boundsTab = super.panelSwtImpl.getBounds();
//      if (parentPanel._panel.pixelTab == 0) {              // on the first tab panel
//        final Rectangle areaParent = parent.getBounds();
//        parentPanel._panel.pixelTab = (short) (areaParent.height - areaTab.height);
//        //super.panelSwtImpl.setBounds(0, parentPanel._panel.pixelTab, parentImplAccess.pixBounds.dx, parentImplAccess.pixBounds.dy - parentPanel._panel.pixelTab);
//      } else {
//        super.panelSwtImpl.setBounds(0, parentPanel._panel.pixelTab, parentImplAccess.pixBounds.dx, parentImplAccess.pixBounds.dy - parentPanel._panel.pixelTab);
//      }
//    } else {
      swtMng.setPosAndSizeSwt(wdgg.pos(), super.panelSwtImpl, 0,0);  //area of panel adequate pos
    if(wdgg.name.equals("tabFavorsAll1")) {
      Debugutil.stop(); }
      super.panelSwtImpl.setVisible(wdgg.isVisible());
      //super.panelSwtImpl.setBounds(areaParent);
      swtMng.listVisiblePanels_add(wdgg);
//    }
    SwtMng.logBounds(sLog, super.panelSwtImpl);
    gralMng.log.sendMsg(GralMng.LogMsg.newPanel, sLog);
    
    SwtMng.storeGralPixBounds(this, super.panelSwtImpl);
  }


  /**The derived class from {@link org.eclipse.swt.widgets.Canvas} contains
   * specific overridden operations. Combination of Canvas and Grid.
   */
  protected static class XXXSwtCanvasGridPanel extends SwtWdgCanvas {
    /**
     * Reference to the GralWidget implementation class, which's SwtGridPanel#w
     *
     */
    private final SwtGridPanel wdgi;

    XXXSwtCanvasGridPanel(final SwtGridPanel storeMng, final Composite parent, final int style) {
      super(null, (SwtMng)storeMng.mngImpl, storeMng.gralPanel().canvas(), parent, style);
      this.wdgi = storeMng;

    }

    @Override public void drawBackground ( final GC g, final int x, final int y, final int dx, final int dy ) {
      // NOTE: forces stack overflow because calling of this routine recursively:
      // super.paint(g);
      
      GralMng gralMng = this.wdgi.gralMng();
      int xPixel = gralMng.propertiesGui().xPixelUnit();   // Note: the properties are only available in the implementation
      int yPixel = gralMng.propertiesGui().yPixelUnit();
      SwtMng swtMng = SwtMng.swtMng(this.wdgi);
      if(this.wdgi._panel.colorGridLine !=null) {          // grid only given if colorGridLine is given, then all should be given
        final Color color1 = swtMng.getColorImpl( this.wdgi._panel.colorGridLine);
        final Color color2 = swtMng.getColorImpl( this.wdgi._panel.colorGridLine2);
        int xGridStep = this.wdgi.gralPanel._panel.xGrid * xPixel;   
        int xGrid = xGridStep;
        int xS1 = this.wdgi.gralPanel._panel.xGrid2;
        while (xGrid < dx) {
          if (--xS1 <= 0) {
            xS1 = this.wdgi.gralPanel._panel.xGrid2;
            g.setForeground(color2);
          } else {
            g.setForeground(color1);
          }
          g.drawLine(xGrid, 0, xGrid, dy);
          xGrid += xGridStep;
        }
        int yGridStep = this.wdgi.gralPanel._panel.yGrid * yPixel;
        int yGrid = yGridStep;
        int yS1 = this.wdgi.gralPanel._panel.yGrid2;
        while (yGrid < dy) {
          if (--yS1 <= 0) {
            yS1 = this.wdgi.gralPanel._panel.yGrid2;
            g.setForeground(color2);
          } else {
            g.setForeground(color1);
          }
          g.drawLine(0, yGrid, dx, yGrid);
          yGrid += yGridStep;
        }
      }
      super.drawBackground(g, x, y, dx, dy);
    }
  }

  void stop () {
  } // debug

}
