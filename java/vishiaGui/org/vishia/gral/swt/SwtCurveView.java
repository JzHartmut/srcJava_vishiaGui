package org.vishia.gral.swt;



import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.vishia.gral.base.GralCurveView;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.util.Assert;
import org.vishia.util.Debugutil;




public class SwtCurveView extends GralCurveView.GraphicImplAccess
{
  
  /**Version, history and license.
   * <ul>
   * <li>2016-07-03 Hartmut chg: Now derived from {@link GralCurveView.GraphicImplAccess} adequate to the  new concept: 
   *   An implementing widget is derived from its derived class of {@link GralWidget.ImplAccess}. Therefore only that base class implements the GralWidgetImpl_ifc.
   * <li>2014-02-03 Hartmut new: {@link CommonCurve#bFreeze}: freeze as common property of more as one GralCurveView. Constructor argument.
   * <li>2013-05-19 Hartmut new: Usage of the common {@link SwtGralMouseListener} and 
   *   implementation of the special functionality in the superclass {@link GralCurveView.GralCurveViewMouseAction}. 
   * <li>2012-08-11 Hartmut now grid with  timestamps
   * <li>2012-03-25 Hartmut improved zoom
   * <li>2012-03-17 Hartmut some improvements in paint routine.
   * <li>2012-02-26 Hartmut A lot of details, see {@link GralCurveView}
   * <li>2012-02-21 Hartmut Now the CurveView works in the new environment. Some adjustments necessary yet.
   * <li>2011-06-00 Hartmut New concept of GralWidget etc and new Configuration concept
   *   with {@link org.vishia.gral.cfg.GralCfgBuilder}. The old GuiDialogZbnfControlled.class
   *   was not use nevermore. But the CurveView was not adapted for that.
   * <li>2010-03-00 Hartmut The curve view was development as basic feature.
   * </ul>
   * <br><br>
   * <b>Copyright/Copyleft</b>:
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
   * 
   */
  @SuppressWarnings("hiding")
  public final static int version = 20120317;

  private final CurveViewSwtWidget curveSwt;
  
  private final Image cursorStore1, cursorStore2;
  
  

  protected Color gridColor, gridColorStrong;
  
  protected final Color colorCursor, colorBack;
  
  long timeRepaintLast, timeRepaintCall;

  
  public SwtCurveView(GralCurveView widgg, SwtMng mng) //String sName, GralPos pos, SwtMng mng, int nrofXvalues, GralCurveView.CommonCurve common)
  {
    super(widgg);
    //super(sName, mng.mng, nrofXvalues, common);
    mouseListenerCurve = new SwtGralMouseListener.MouseListenerGralAction();
  
    GralRectangle bounds = mng.calcWidgetPosAndSize(widgg.pos(), 700, 600);
    //Composite panelSwt = (Composite)widgg.pos().parent.getImpl().getWidgetImplementation();
    Composite panelSwt = (Composite)SwtMng.getSwtImpl(widgg.pos().parent);
    curveSwt = this.new CurveViewSwtWidget(panelSwt, bounds.dx, bounds.dy, this._dataOrg1.maxNrofXValues);
    curveSwt.setData(this);
    curveSwt.setSize(bounds.dx, bounds.dy);
    curveSwt.setBounds(bounds.x, bounds.y, bounds.dx, bounds.dy);
    //mng.setBounds_(curveSwt); //, dyGrid, dxGrid);
    curveSwt.setGridVertical(10, 5);   //10 data-points per grid line, 50 data-points per strong line.
    curveSwt.setGridHorizontal(50.0F, 5);  //10%-divisions, with 5 sub-divisions
    cursorStore1 = new Image(panelSwt.getDisplay(), 1, 2000);
    cursorStore2 = new Image(panelSwt.getDisplay(), 1, 2000);
    
    gridColor = new Color(curveSwt.getDisplay(), 192, 192, 192);
    gridColorStrong = new Color(curveSwt.getDisplay(), 64, 64, 64);
    colorCursor = new Color(curveSwt.getDisplay(), 64, 64, 64);
    colorBack = new Color(curveSwt.getDisplay(), 0xff, 0xff, 0xff);
  }
  
  
  @Override public void redrawGthread(){
    int chg = dyda().getChanged();  //impl.getChanged();
    int acknChg = 0;
    if((chg & chgVisible)!=0){
      acknChg |= chgVisible;
      curveSwt.setVisible(true);
      setFocusGThread();
    }
    if((chg & chgInvisible)!=0){
      acknChg |= chgInvisible;
      curveSwt.setVisible(false);
    }
    dyda().acknChanged(acknChg);
    this.timeRepaintCall = System.currentTimeMillis(); //nanoTime();
    //System.out.println("repaint req");
    curveSwt.redraw();
  }


    
  
  /**Draws one track using the {@link ixDataShown} indices to the data.
   * @param g graphic context from SWT
   * @param size of the panel in pixel
   * @param track data
   * @param iTrack Index of track, only used for debugging or test outputs.
   * @param ixixDataLast The end index in {@link ixDataShown} for this presentation.
   */
  private void drawTrack(GC g, Point size, GralCurveView.Track track, int iTrack, int ixixDataLast){
    int ixixiData = 0;                                     // index in data index array
    //float pixelFromRight = 0;
    int xp2 = size.x -1;                                   // end x coord of point
    int xp1 = xp2;                                         // start x coord of point
    int ixData2 = super.ixDataShownX[ixixiData];            // data index,
    int nrofPixel4Data = super.nrofPixel4data[ixixiData];  // 0 = one point, 1.. more graphic points per one value 
    int ixData = ixData2;                                  // index of left data value for one point.
    int ixD = (ixData >> widgg.shIxiData) & widgg.mIxiData; //real index in data
    //
    float yFactor = size.y / -10.0F / track.scale.yScale;  //y-scaling
    float y0Pix = (1.0F - track.scale.y0Line/100.0F) * size.y; //y0-line
    float yF = track.valueTrack.getFloat(ixD);             // current, here start value
    int time2 = this.widgg.tracksValue().getTimeShort(ixD);
    int time1;
    int yp9 = (int)( (yF - track.scale.yOffset) * yFactor + y0Pix);
    int yp2 = yp9;  //right value
    int yp1; //left value
    int ixData1;
    Color lineColor = track.lineColor !=null ? (Color)widgg.gralMng()._mngImpl.getColorImpl(track.lineColor) : ((SwtProperties)widgg.gralMng().propertiesGui).colorBackground;
    if(iTrack == 0){
      //System.out.println("SwtCurveView-drawTrack-start(y0Pix=" + y0Pix + ", yFactor=" + yFactor + ", y=" + yF + ")");
    }
    //
    while( ixixiData < ixixDataLast){ //for all gotten ixData
      ixixiData += nrofPixel4Data +1;                      // get next value to left
      ixData1 = super.ixDataShownX[ixixiData];
      //ixData1 = ixDataShown[(int)pixelFromRight];
      
      xp1 -= nrofPixel4Data +1;                            // next end point of next value 
      if(ixData != ixData1) {   // should always true
        int yp1min = Integer.MAX_VALUE, yp1max = Integer.MIN_VALUE;
        int nrofYp = 0;
        yp1 = 0;
        do{                                                // use all values per 1 pixel
          ixData -= widgg.adIxData;                        // to next point
          ixD = (ixData >> widgg.shIxiData) & widgg.mIxiData;
          int yp11;
          //if(ixData == ixDataDraw){
          if(ixixiData >= ixixDataLast){
              yp11 = track.ypixLast;
          } else {
            yF = track.valueTrack.getFloat(ixD);
            time1 = widgg.tracksValue().getTimeShort(ixD);
            int dTime = time2 - time1;
            //pixelFromRight += dTime * pixel7time;
            yp11 = (int)( (yF - track.scale.yOffset) * yFactor + y0Pix);
          }
          yp1 += yp11;  //build middle value or init first.
          if(ixData != ixData1){  //more as one value on the same xp
            if(yp1min > yp11){ yp1min = yp11; }
            if(yp1max < yp11){ yp1max = yp11; }
          }
          nrofYp +=1;
        } while(ixData != ixData1); // iData != ixDrawValues); //all values per 1 pixel
        g.setForeground(lineColor);
        g.setLineWidth(track.lineWidth);
        if(nrofYp > 1){ //more as one value on the same xp
          g.drawLine(xp1, yp1min, xp1, yp1max);  //draw vertical line to show range.
          yp1 = yp1 / nrofYp;
        }
        g.drawLine(xp2, yp2, xp1, yp1);
        if(iTrack == 0){
          //System.out.println("SwtCurveView-drawTrack(" + xp2 + "+" + (xp1 - xp2) + ", " + yp2 + "+" + (yp1 - yp2) + ")");
        }
        xp2 = xp1; //next xp from right to left.
        yp2 = yp1;
        nrofPixel4Data = super.nrofPixel4data[ixixiData];
        ixData2 = ixData1;
      } else {
      }
    } //while(iData != ixDrawValues);
    track.ypixLast = yp9;
    
  }
  
  
  
  /**Shifts the left draw area to left because only a right part of the curve should be drawn.
   * It is to save calculation time
   */
  private int drawShiftAreaToLeft(GC g, Point size, int xView, int dxView, int yView, int dyView, int xViewPart, int timeDiff){
    final int xp0;
    testHelp.ctRedrawBecauseNewData +=1;
    //
    //calculate the number of x-pixel to shift in graphic to left and the width of the range to paint new:
    //
    //Shift the graphic if the reason of redraw is only increment samples
    //and the number the values are shifted at least by that number, which is mapped to 1 pixel.
    if(xViewPart >0 && xViewPart < size.x){
      super.timeCaryOverNewValue = (int)(timeDiff - xViewPart * widgg._timeorg.timePerPixel);
      super.xViewLastF -= xViewPart;
      if(xView != 0){
        //TODO what is if only a part of control is shown
        stop();
      }
      xp0 = xView + dxView - xViewPart;
      if(super.xpCursor1 >= 0){ //xViewPart){  //only if the cursor is in the shifted area:
        //restore graphic under cursor
        g.drawImage(cursorStore1, size.x - super.xpCursor1, 0);
        //System.out.println("cursor1 " + xpCursor1);
      }
      if(super.xpCursor2 >= 0){ //xViewPart){  //only if the cursor is in the shifted area:
        //restore graphic under cursor
        g.drawImage(cursorStore2, size.x - super.xpCursor2, 0);
        //System.out.println("cursor2 " + xpCursor2);
      }
      g.copyArea(xView + xViewPart, yView, dxView - xViewPart , dyView -5, xView, yView, false);
      //
      widgg._timeorg.pixelWrittenAfterStrongDiv += xViewPart;
      
      //System.out.println("SwtCurveView - draw - shift graphic;" + xViewPart);
      //System.arraycopy(ixDataShown, 0, ixDataShown, xViewPart, size.x - xViewPart);
      testHelp.ctRedrawPart +=1;
    } else if(xViewPart >=size.x){ 
      //too many new values. Show all
      xViewPart = size.x;
      xp0 = 0;
      testHelp.ctRedrawAllShift +=1;
      super.xViewLastF = 0.0F;
    } else { //xViewPart <=0
      //don't paint.
      //System.out.println("SwtCurveView - draw - don't shift graphic;" + xViewPart);
      xViewPart = 0;
      xp0 = size.x;
    }
    return xp0;
  }
  
  
  
  /**Draws the curves.
   * @param g
   * @param size
   * @param xView
   * @param dxView
   * @param yView
   * @param dyView
   * @param ixDataRightX
   * @param ixD2 right point of displayed curve in data.
   * @param xViewPart
   * @param timeDiff
   * @param xp0
   * @param bPaintAll
   */
  private void drawRightOrAll(GC g, Point size, int xView, int dxView, int yView, int dyView
      , int ixDataRightX, int ixD2, int xViewPart, int timeDiff, int xp0, boolean bPaintAll){
    g.setBackground(colorBack);
    //fill, clear the area either from 0 to end or from size.x - xView to end,
    g.fillRectangle(xp0, yView, xViewPart, dyView);  //fill the current background area
    { //draw horizontal grid
      float yG = dyView / super.gridXdistance;
      int yS = super.gridXstrongPeriod;
      /*TODO
      while(yGridF < dyView){
        int yGrid = (int)yGridF;
        if(--yS1 <=0){
          yS1 = yS; g.setForeground(gridColorStrong);
        } else { g.setForeground(gridColor);
        }
        g.drawLine(xViewLast, yGrid, dxView, yGrid);
        yGridF += yG;
      }
      */
    } 
    //  
    //prepare indices of data. Fills {@link #ixDataShown}
    int ixixDataLast = super.prepareIndicesDataForDrawing(ixDataRightX, ixD2, xViewPart, timeDiff, bPaintAll);
    // 
    //write time divisions:
    g.setForeground(gridColor);
    g.setLineWidth(1);
    g.setLineStyle(SWT.LINE_DOT);
    for(int ii=1; ii <=49; ++ii){  //draw the horizontal grid
      int y = (int)(size.y /50.0f * ii);
      g.drawLine(size.x - xViewPart, y, size.x, y);        // draws fine grid x lines
      
    }
    g.setForeground(gridColorStrong);
    g.setLineWidth(1);
    g.setLineStyle(SWT.LINE_DOT);
    for(int ii=1; ii <=9; ++ii){  //draw the horizontal grid
      int y = (int)(size.y /10.0f * ii);
      g.drawLine(size.x - xViewPart, y, size.x, y);        // draws grid x lines
      
    }
    //
    g.setForeground(gridColor);
    g.setLineWidth(1);
    int ixPixelTimeDiv =-1;
    int xPixelTimeDiv1;                     //draw the vertical fine grid, the time divisions
    while((xPixelTimeDiv1 = widgg._timeorg.xPixelTimeDivFine[++ixPixelTimeDiv]) >=0) {
      g.drawLine(size.x - xPixelTimeDiv1, 0, size.x - xPixelTimeDiv1, size.y);
      //System.out.println("draw " + xPixelTimeDiv);
    }
    g.setForeground(gridColorStrong);
    g.setLineWidth(1);
    ixPixelTimeDiv =-1;                     //draw the vertical grid, strong lines.
    while((xPixelTimeDiv1 = widgg._timeorg.xPixelTimeDiv[++ixPixelTimeDiv]) >=0) {
      g.drawLine(size.x - xPixelTimeDiv1, 0, size.x - xPixelTimeDiv1, size.y);
      if(xPixelTimeDiv1 > 30){
        g.setForeground((Color)widgg.gralMng()._mngImpl.getColorImpl(GralColor.getColor("bk")));
        g.drawText(widgg._timeorg.sTimeAbsDiv[ixPixelTimeDiv], size.x - 6 - xPixelTimeDiv1, size.y - 25);
        g.setForeground(gridColorStrong);
        widgg._timeorg.pixelWrittenAfterStrongDiv = Integer.MIN_VALUE;
      }
      //System.out.println("draw " + xPixelTimeDiv);
    }
    //write all tracks.
    g.setLineStyle(SWT.LINE_SOLID);
    int iTrack = 0;
    List<GralCurveView.Track> listTracks1 = widgg.listTracks; //Atomic access, iterate in local referenced list.
    for(GralCurveView.Track track: listTracks1){
      //draw line per track
      if(track.showSelected !=0) {
        drawTrack(g, size, track, iTrack, ixixDataLast);
      }
      iTrack +=1;
    } //for listlines
    super.ixDataDrawX = ixDataRightX;
    super.ixDdraw = ixD2;
    //
    if(widgg._timeorg.pixelWrittenAfterStrongDiv > 30){
      g.drawText(widgg._timeorg.sTimeAbsDiv[0], size.x - 6 - widgg._timeorg.pixelWrittenAfterStrongDiv, size.y - 25);
      widgg._timeorg.pixelWrittenAfterStrongDiv = Integer.MIN_VALUE;
    }
    //set the cursors
    if(super.xpCursor1New >=0){
      super.xpCursor1 = super.xpCursor1New;
      super.xpCursor1New = -1;
    }
    if(super.xpCursor2New >=0){
      super.xpCursor2 = super.xpCursor2New;
      super.xpCursor2New = -1;
    }
    if(super.xpCursor1 >=0){
      int xpCursor = size.x - super.xpCursor1;
      g.copyArea(cursorStore1, xpCursor, 0);
      g.setForeground(colorCursor);
      g.setLineWidth(1);
      g.drawLine(xpCursor, 0, xpCursor, size.y);
    }
    if(super.xpCursor2 >=0){
      int xpCursor = size.x - super.xpCursor2;
      g.copyArea(cursorStore2, xpCursor, 0);
      //if(xViewPart >= size.x){
        g.setForeground(colorCursor);
        g.setLineWidth(1);
        g.drawLine(xpCursor, 0, xpCursor, size.y);
      //}
    }

  }
  
  
  
  /**This routine is called from overridden {@link CurveViewSwtWidget#drawBackground(GC, int, int, int, int)}  
   * @see org.eclipse.swt.widgets.Canvas#drawBackground(org.eclipse.swt.graphics.GC, int, int, int, int)
   * It is called in this class in {@link #paintListener} in the {@link PaintListener#paintControl(PaintEvent)} method.
   * It draws the whole content.
   * <br><br>
   * Because of saving calculation time there will be drawn only a small peace on right side of the area
   * with the new data normally. The rest inclusive grid lines, curves, text is moved to left. But if the whole
   * window should be refreshed, the whole widget is drawn newly.
   * 
   */
  protected void drawBackground(GC g, Point size, int xView, int yView, int dxView, int dyView) {
    //NOTE: forces stack overflow because calling of this routine recursively: super.paint(g);
    try{
      boolean redrawBecauseNewData1, redrawAll;
      synchronized(this){
        redrawBecauseNewData1 = super.redrawBecauseNewData;
        redrawAll = super.bRedrawAll;
        if(!redrawBecauseNewData1){
          Debugutil.stop();
        }
        super.bRedrawAll = false;  //it is done
        super.redrawBecauseNewData = false;  //it is done.
      }
      int ixd1pix = super.dataIxToShow[super.xpCursor1+1] - super.dataIxToShow[super.xpCursor1+2]; //~ ixData diff 1 pixel
      //
      //detect how many new data are given. Because the data are written in another thread,
      //the number of data, the write index are accessed only one time from this
      //Note that ixDataShowRight is set by ixDataWr if the curve is running.
      this.widgg.cassert(Math.abs((super.ixDataDrawX >> this.widgg.shIxiData)- super.ixDdraw) <= 5*ixd1pix);
      this.widgg.cassert((Math.abs((super.ixDataShowRightX >> this.widgg.shIxiData) - super.ixDshowRight)) <= 5*ixd1pix);
      final int ixDataRightX = super.ixDataShowRightX; 
      final int ixDRight = super.ixDshowRight;             // seems to be ixDshowRight is volatile
      super.pixelOrg.xPixelCurve = size.x;
      super.pixelOrg.yPixelCurve = size.y;
      @SuppressWarnings("hiding")
      GralCurveView.TimeOrganisation timeorg = widgg._timeorg;
      timeorg.timePerPixel = (float)timeorg.timeSpread / (size.x +1); //nr of time units per pixel. 
      timeorg.pixel7time = (float)(size.x +1) / timeorg.timeSpread; //nr of pixel per time unit.
      int xViewPart = -1;   //nr of new pixel if only a part is drawn
      final int xp0;  //left point to draw.
      final int timeDiff; //time for new values.
      testHelp.xView =xView; testHelp.yView =yView; testHelp.dxView =dxView; testHelp.dyView =dyView;
      //
      final boolean bPaintAll;
      if(!widgg.common.bFreeze && !super.bPaintAllCmd && !redrawAll ) { //redrawBecauseNewData1) {
        //paint only a part of the curve to save calculation time.
        //The curve will be shifted to left.
        //
        bPaintAll = false;
        if(ixDataRightX != super.ixDataDrawX){
          this.widgg.testStopWr = true;
          stop();
        }  
        int timeLast = this.widgg.tracksValue().getTimeShort(this._wg.bDbgNewX ? super.ixDdraw : (super.ixDataDrawX >> this.widgg.shIxiData) & this.widgg.mIxiData);
        int timeNow = this.widgg.tracksValue().getTimeShort(this._wg.bDbgNewX ? ixDRight : (ixDataRightX >> this.widgg.shIxiData) & this.widgg.mIxiData);
        timeDiff = timeNow - timeLast + super.timeCaryOverNewValue;  //0 if nothing was written.
        xViewPart = (int)(timeorg.pixel7time * timeDiff + 0.0f);
        if(xViewPart > size.x){
          xViewPart = size.x;   //for example if no data were received in the past, then timeDiff is hi.
        }
        xp0 = drawShiftAreaToLeft(g, size, xView, dxView, yView, dyView, xViewPart, timeDiff);
      } else { //paintall
        bPaintAll = true;
        timeorg.calc();
        //System.out.println("SwtCurveView - paintall;" + bFreeze + bPaintAllCmd + redrawBecauseNewData1);
        xViewPart = size.x;
        super.timeCaryOverNewValue = 0;
        timeDiff = (int)(timeorg.timePerPixel * xViewPart);
        xp0 = 0;
        super.bPaintAllCmd = false; //accepted, done
        testHelp.ctRedrawAll +=1;
        //xViewLast = 0;
        super.xViewLastF = 0.0F;
        super.nrofDataShift.set(0);
      }
      widgg.newSamples = 0;  //next call of setSample will cause only draw of less area.
      
      //draw grid and 0-lines:
      if(xViewPart >0) { //only if anything is to draw
        //only if a new point should be drawn.
        try{Thread.sleep(2);} catch(InterruptedException exc){}
        if(ixDRight != super.ixDshowRight) {
          Debugutil.stop();
        }
        drawRightOrAll(g, size, xView, dxView, yView, dyView, ixDataRightX, ixDRight, xViewPart, timeDiff, xp0, bPaintAll);
        
      } else { //xViewPart == 0
        //This is is normal case if a new value in data has a too less new timestamp.
        //It can't be shown. Await the next values. Any value will be have a more newer timestamp
        //with a great-enough time difference to show it.
        super.nrofValuesLessViewPart +=1;
        //System.out.println("SwtCurveView - xViewPart=0");
      }
      if(this._dataOrg1.nrofValues >0){  //don't work if no data are stored.
        int ixDataShow2 = this._wg.bDbgNewX ? this.widgg.ixDEnd - super.ixDdraw :((widgg.ixDataWrX - super.ixDataDrawX)  >> widgg.shIxiData) & widgg.mIxiData;  //index of data which are shown right
        int ixDataShow1 = this._wg.bDbgNewX ? this.widgg.ixDEnd - super.dataIxToShow[size.x] : ((widgg.ixDataWrX - super.ixDataShownX[size.x])  >> widgg.shIxiData) & widgg.mIxiData; //index of data which are shown left
        float ixDataRel2 = (float)ixDataShow2 / this._dataOrg1.maxNrofXValues;  //value 0..1 which range of buffer is shown 
        float ixDataRel1 = (float)ixDataShow1 / this._dataOrg1.maxNrofXValues;
        int iPixRange2 = size.x - (int)(size.x * ixDataRel2);  //Position shown range right in pixel
        int iPixRange1 = size.x - (int)(size.x * ixDataRel1);  //left
        //int ixDWr = (ixDataWr >> shIxiData) & mIxiData;
        //System.out.println("SwtCurveView.spread; " + ixDataRel1 + ".." + ixDataRel2);
        g.setLineWidth(5);
        g.setForeground((Color)widgg.gralMng()._mngImpl.getColorImpl(GralColor.getColor("ye")));
        g.drawLine(0, size.y -3, iPixRange1, size.y -3);  //left not shown range.
        g.drawLine(iPixRange2, size.y -3, size.x, size.y -3);  //right non shown range.
        g.setForeground((Color)widgg.gralMng()._mngImpl.getColorImpl(GralColor.getColor("dgr")));
        //g.setAlpha(128);
        g.drawLine(iPixRange1, size.y -3, iPixRange2, size.y -3);  //shown range
      }
    } catch(Exception exc){
      CharSequence sError = Assert.exceptionInfo("SwtCurveView.draw - exception", exc, 0, 20, true);
      System.err.append(sError).append('\n');
    }
    //g.drawString(""+xShift+ ":"+ xViewLast + ":" + nrofDataShift.get(), 200, dyView-28);
    //g.drawString("xx", 200, dyView-16);
    super.focusChanged = false; //paint only last area by next paint event without focus event.
    widgg.testStopWr = false;
  } 

  
  
  private class CurveViewSwtWidget extends Canvas
  {
    
    //private final CurveView widgSwt;

    //final Canvas canvas;
    
    //private final Color[] lineColors;
    
    
    public CurveViewSwtWidget(Composite parent, int xPixel, int yPixel, int nrofXvalues){
      super(parent, org.eclipse.swt.SWT.NO_SCROLL|org.eclipse.swt.SWT.NO_BACKGROUND);
      setData("Control", this);
      setSize(xPixel, yPixel);  //the size may be changed later by drag the window.
      //this.xPixel = xPixel;
      //this.yPixel = yPixel;
      //lineColors = new Color[nrofTracks];
      Color defaultColor = new Color(getDisplay(), 255,0,0);
      addPaintListener(paintListener);
      addFocusListener(focusListener);
      addMouseListener(mouseListenerCurve);
      //addMouseListener(mouseLeftButtonListener);
      //addMouseMoveListener(mouseMoveListener);
      //widgSwt = this;
    }
    
    

    
    public void setGridVertical(int dataPointsBetweenGridLines, int periodStrongLine){
      SwtCurveView.this.gridYdstance = dataPointsBetweenGridLines;
      SwtCurveView.this.gridYstrongPeriod = periodStrongLine;
      widgg.gridDistanceStrongY = dataPointsBetweenGridLines * periodStrongLine;
    }
    
    
    /**Set distance for horizontal grid lines.
     * @param percentY percent of y-view for grid lines. For example 50.0: divide y-axis in 50 steps. 
     * @param periodStrongLine period for strong lines For example 5, any 5. line is stroke.
     */
    public void setGridHorizontal(float percentY, int periodStrongLine){
      SwtCurveView.this.gridXdistance = percentY;
      SwtCurveView.this.gridXstrongPeriod = periodStrongLine;
    }
    
    
    
    public void redrawData(){
      SwtCurveView.this.redrawBecauseNewData = true;
      redraw();
    }
    
    
    
    
    
    
    
    /**The paint event is forced by an handling on operation system, for example hide or show a window, resize etc. pp.
     * It is also forced by new data, from calling of {@link #redrawGthread()}. 
     */
    PaintListener paintListener = new PaintListener()
    {
//      boolean show = true;
      
      @Override public void paintControl(PaintEvent e) {
        GC gc = e.gc;
        long time1 = System.currentTimeMillis(); //nanoTime();
        long dCycle = time1 - timeRepaintLast;
        long dCall = time1 - SwtCurveView.this.timeRepaintCall;
        drawBackground(e.gc, e.x, e.y, e.width, e.height);
        long dCalc = (SwtCurveView.this.timeRepaintLast = System.currentTimeMillis()) - time1;
//        if(this.show) {System.out.printf("curveViewGT %d: %d + %d\n", dCycle, dCall, dCalc); }
      }
      
    };

    
    
    FocusListener focusListener = new FocusListener()
    { @Override public void focusGained(FocusEvent e) {
      SwtCurveView.this.focusChanged = true;
      }

      @Override public void focusLost(FocusEvent e) {
        SwtCurveView.this.focusChanged = true;
      }
    }; 

    
    /**This routine overrides 
     * @see org.eclipse.swt.widgets.Canvas#drawBackground(org.eclipse.swt.graphics.GC, int, int, int, int)
     * It is called in this class in {@link #paintListener} in the {@link PaintListener#paintControl(PaintEvent)} method.
     * It draws the whole content.
     * <br><br>
     * Because of saving calculation time there will be drawn only a small peace on right side of the area
     * with the new data normally. The rest inclusive grid lines, curves, text is moved to left. But if the whole
     * window should be refreshed, the whole widget is drawn newly.
     * 
     */
    @Override public void drawBackground(GC g, int xView, int yView, int dxView, int dyView) {
      //NOTE: forces stack overflow because calling of this routine recursively: super.paint(g);
      SwtCurveView.this.drawBackground(g, getSize(), xView, yView, dxView, dyView);
      SwtMng.storeGralPixBounds(SwtCurveView.this, this);
    }    
  }

  
  /**This class is only used to store values to inspect. The Inspector is a tool which works with
   * reflection and with it internal variable can be visited in runtime. See {@link org.vishia.inspectorTarget.Inspector}.
   */
  @SuppressWarnings("unused")
  private static class TestHelp{
    /**Counts any redraw action for complete redraw, to see how often.  */
    int ctRedrawAll;
    /**Counts any redraw action for complete redraw because nrof shift>100, to see how often.  */
    int ctRedrawAllShift;
    
    /**Counts any redraw action for partial redraw, to see how often.  */
    int ctRedrawPart;
    
    /**Ct if redraw only because new data. */
    int ctRedrawBecauseNewData;
    
    /**Coordinates while redraw. {@link CurveViewSwtWidget#drawBackground(GC, int, int, int, int)}. */
    int xView, yView, dxView, dyView;
  } 
  TestHelp testHelp = new TestHelp();

  
  final SwtGralMouseListener.MouseListenerGralAction mouseListenerCurve;
  

  void stop(){}


  @Override
  public boolean setFocusGThread()
  {
    // TODO Auto-generated method stub
    return false;
  }


  @Override
  public void setVisibleGThread(boolean bVisible)
  {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void removeWidgetImplementation()
  {
    // TODO Auto-generated method stub
    
  }


  @Override public Object getWidgetImplementation(){ return curveSwt; }


  @Override
  public void setBoundsPixel(int x, int y, int dx, int dy)
  {
    curveSwt.setBounds(x, y, dx, dy);
    bRedrawAll = true;
    curveSwt.redraw();
  }


  @Override
  public GralRectangle getPixelPositionSize()
  {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public boolean remove()
  {
    // TODO Auto-generated method stub
    return false;
  }


  @Override
  public void updateValuesForAction() {
    // TODO Auto-generated method stub
    
  }  
  
}
