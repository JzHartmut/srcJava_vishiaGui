package org.vishia.gral.swt;



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
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;




public class SwtCurveView extends GralCurveView
{
  
  /**Version, history and license.
   * <ul>
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

  private final CurveView curveSwt;
  
  private final Image cursorStore1, cursorStore2;
  
  

  protected Color gridColor, gridColorStrong;
  
  protected final Color colorCursor, colorBack;
  

  
  public SwtCurveView(String sName, GralPos pos, SwtMng mng, int nrofXvalues, int nrofTracks)
  {
    super(sName, mng, nrofXvalues, nrofTracks);
    
    GralRectangle bounds = mng.calcWidgetPosAndSize(pos, 800, 600);
    Composite panelSwt = (Composite)pos.panel.getPanelImpl();
    curveSwt = this.new CurveView(panelSwt, bounds.dx, bounds.dy, nrofXvalues, nrofTracks);
    curveSwt.setSize(bounds.dx, bounds.dy);
    curveSwt.setBounds(bounds.x, bounds.y, bounds.dx, bounds.dy);
    //mng.setBounds_(curveSwt); //, dyGrid, dxGrid);
    curveSwt.setGridVertical(10, 5);   //10 data-points per grid line, 50 data-points per strong line.
    curveSwt.setGridHorizontal(50.0F, 5);  //10%-divisions, with 5 sub-divisions
    cursorStore1 = new Image(panelSwt.getDisplay(), 1, 2000);
    cursorStore2 = new Image(panelSwt.getDisplay(), 1, 2000);
    
    gridColor = new Color(curveSwt.getDisplay(), 192, 255, 255);
    gridColorStrong = new Color(curveSwt.getDisplay(), 128, 255, 255);
    colorCursor = new Color(curveSwt.getDisplay(), 64, 64, 64);
    colorBack = new Color(curveSwt.getDisplay(), 0xff, 0xff, 0xff);
    super.initMenuContext();
  }
  
  
  @Override public void repaintGthread(){
    curveSwt.redraw();
  }

  @Override public GralWidgetGthreadSet_ifc getGthreadSetifc(){ return gThreadSet; }

  @Override public Object getWidgetImplementation(){ return curveSwt; }
  

  
  @Override
  public void removeWidgetImplementation()
  {
    // TODO Auto-generated method stub
    
  }

  /**Draws one track using the {@link ixDataShown} indices to the data.
   * @param g graphic context from SWT
   * @param size of the panel in pixel
   * @param track data
   * @param iTrack Index of track, only used for debugging or test outputs.
   * @param ixixDataLast The end index in {@link ixDataShown} for this presentation.
   */
  private void drawTrack(GC g, Point size, Track track, int iTrack, int ixixDataLast){
    int ixixiData = 0;
    //float pixelFromRight = 0;
    int xp2 = size.x -1;
    int xp1 = xp2;
    int ixData2 = ixDataShown[ixixiData];
    int nrofPixel4Data = SwtCurveView.super.nrofPixel4data[ixixiData];
    int ixData = ixData2;
    int ixD = (ixData >> shIxiData) & mIxiData; //real index in data
    //
    float yFactor = size.y / -10.0F / track.yScale;  //y-scaling
    float y0Pix = (1.0F - track.y0Line/100.0F) * size.y; //y0-line
    float yF = track.values[ixD];
    int time2 = timeValues[ixD];
    int time1;
    int yp9 = (int)( (yF - track.yOffset) * yFactor + y0Pix);
    int yp2 = yp9;  //right value
    int yp1; //left value
    int ixData1;
    Color lineColor = track.lineColor !=null ? (Color)itsMng.getColorImpl(track.lineColor) : ((SwtProperties)itsMng.propertiesGui).colorBackground;
    if(iTrack == 0){
      //System.out.println("SwtCurveView-drawTrack-start(y0Pix=" + y0Pix + ", yFactor=" + yFactor + ", y=" + yF + ")");
    }
    //
    while( ixixiData < ixixDataLast){ //for all gotten ixData
      ixixiData += nrofPixel4Data +1;
      ixData1 = ixDataShown[ixixiData];
      //ixData1 = ixDataShown[(int)pixelFromRight];
      
      xp1 -= nrofPixel4Data +1;
      if(ixData != ixData1) {
        int yp1min = Integer.MAX_VALUE, yp1max = Integer.MIN_VALUE;
        int nrofYp = 0;
        yp1 = 0;
        do{ //all values per 1 pixel
          ixData -= adIxData;
          ixD = (ixData >> shIxiData) & mIxiData;
          int yp11;
          //if(ixData == ixDataDraw){
          if(ixixiData >= ixixDataLast){
              yp11 = track.ypixLast;
          } else {
            yF = track.values[ixD];
            time1 = timeValues[ixD];
            int dTime = time2 - time1;
            //pixelFromRight += dTime * pixel7time;
            yp11 = (int)( (yF - track.yOffset) * yFactor + y0Pix);
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
        nrofPixel4Data = SwtCurveView.super.nrofPixel4data[ixixiData];
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
      timeCaryOverNewValue = (int)(timeDiff - xViewPart * timeorg.timePerPixel);
      xViewLastF -= xViewPart;
      if(xView != 0){
        //TODO what is if only a part of control is shown
        stop();
      }
      xp0 = xView + dxView - xViewPart;
      if(xpCursor1 >= 0){ //xViewPart){  //only if the cursor is in the shifted area:
        //restore graphic under cursor
        g.drawImage(cursorStore1, size.x - xpCursor1, 0);
        //System.out.println("cursor1 " + xpCursor1);
      }
      if(xpCursor2 >= 0){ //xViewPart){  //only if the cursor is in the shifted area:
        //restore graphic under cursor
        g.drawImage(cursorStore2, size.x - xpCursor2, 0);
        //System.out.println("cursor2 " + xpCursor2);
      }
      g.copyArea(xView + xViewPart, yView, dxView - xViewPart , dyView -5, xView, yView, false);
      //
      timeorg.pixelWrittenAfterStrongDiv += xViewPart;
      
      //System.out.println("SwtCurveView - draw - shift graphic;" + xViewPart);
      //System.arraycopy(ixDataShown, 0, ixDataShown, xViewPart, size.x - xViewPart);
      testHelp.ctRedrawPart +=1;
    } else if(xViewPart >=size.x){ 
      //too many new values. Show all
      xViewPart = size.x;
      xp0 = 0;
      testHelp.ctRedrawAllShift +=1;
      xViewLastF = 0.0F;
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
   * @param ixDataRight
   * @param xViewPart
   * @param timeDiff
   * @param xp0
   */
  private void drawRightOrAll(GC g, Point size, int xView, int dxView, int yView, int dyView, int ixDataRight, int xViewPart, int timeDiff, int xp0){
    g.setBackground(colorBack);
    //fill, clear the area either from 0 to end or from size.x - xView to end,
    g.fillRectangle(xp0, yView, xViewPart, dyView);  //fill the current background area
    { //draw horizontal grid
      float yG = dyView / gridDistanceX;
      int yS = gridStrongPeriodX;
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
    //prepare indices of data.
    int ixixDataLast = prepareIndicesDataForDrawing(ixDataRight, xViewPart, timeDiff);
    // 
    //write time divisions:
    g.setForeground(gridColor);
    g.setLineWidth(1);
    g.setLineStyle(SWT.LINE_DOT);
    for(int ii=1; ii <=9; ++ii){  //draw the horizontal grid
      int y = (int)(size.y /10.0f * ii);
      g.drawLine(size.x - xViewPart, y, size.x, y);
      
    }
    //
    int ixPixelTimeDiv =-1;
    int xPixelTimeDiv1;                     //draw the vertical fine grid, the time divisions
    while((xPixelTimeDiv1 = timeorg.xPixelTimeDivFine[++ixPixelTimeDiv]) >=0) {
      g.drawLine(size.x - xPixelTimeDiv1, 0, size.x - xPixelTimeDiv1, size.y);
      //System.out.println("draw " + xPixelTimeDiv);
    }
    g.setForeground(gridColorStrong);
    g.setLineWidth(1);
    ixPixelTimeDiv =-1;                     //draw the vertical grid, strong lines.
    while((xPixelTimeDiv1 = timeorg.xPixelTimeDiv[++ixPixelTimeDiv]) >=0) {
      g.drawLine(size.x - xPixelTimeDiv1, 0, size.x - xPixelTimeDiv1, size.y);
      if(xPixelTimeDiv1 > 30){
        g.setForeground((Color)itsMng.getColorImpl(GralColor.getColor("bk")));
        g.drawText(timeorg.sTimeAbsDiv[ixPixelTimeDiv], size.x - 6 - xPixelTimeDiv1, size.y - 25);
        g.setForeground(gridColorStrong);
        timeorg.pixelWrittenAfterStrongDiv = Integer.MIN_VALUE;
      }
      //System.out.println("draw " + xPixelTimeDiv);
    }
    //write all tracks.
    g.setLineStyle(SWT.LINE_SOLID);
    int iTrack = 0;
    for(Track track: listTracks){
      //draw line per track
      drawTrack(g, size, track, iTrack, ixixDataLast);
      iTrack +=1;
    } //for listlines
    ixDataDraw = ixDataRight;
    //
    if(timeorg.pixelWrittenAfterStrongDiv > 30){
      g.drawText(timeorg.sTimeAbsDiv[0], size.x - 6 - timeorg.pixelWrittenAfterStrongDiv, size.y - 25);
      timeorg.pixelWrittenAfterStrongDiv = Integer.MIN_VALUE;
    }
    //set the cursors
    if(xpCursor1New >=0){
      xpCursor1 = xpCursor1New;
      xpCursor1New = -1;
    }
    if(xpCursor2New >=0){
      xpCursor2 = xpCursor2New;
      xpCursor2New = -1;
    }
    if(xpCursor1 >=0){
      int xpCursor = size.x - xpCursor1;
      g.copyArea(cursorStore1, xpCursor, 0);
      g.setForeground(colorCursor);
      g.setLineWidth(1);
      g.drawLine(xpCursor, 0, xpCursor, size.y);
    }
    if(xpCursor2 >=0){
      int xpCursor = size.x - xpCursor2;
      g.copyArea(cursorStore2, xpCursor, 0);
      //if(xViewPart >= size.x){
        g.setForeground(colorCursor);
        g.setLineWidth(1);
        g.drawLine(xpCursor, 0, xpCursor, size.y);
      //}
    }

  }
  
  
  
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
  protected void drawBackground(GC g, Point size, int xView, int yView, int dxView, int dyView) {
    //NOTE: forces stack overflow because calling of this routine recursively: super.paint(g);
    try{
      boolean redrawBecauseNewData1 = super.redrawBecauseNewData;
      super.redrawBecauseNewData = false;  //it is done.
      //
      //detect how many new data are given. Because the data are written in another thread,
      //the number of data, the write index are accessed only one time from this
      //Note that ixDataShowRight is set by ixDataWr if the curve is running.
      int ixDataRight = super.ixDataShowRight; 
      sizepos.xPixelCurve = size.x;
      sizepos.yPixelCurve = size.y;
      @SuppressWarnings("hiding")
      TimeOrganisation timeorg = SwtCurveView.this.timeorg;
      timeorg.timePerPixel = (float)timeorg.timeSpread / (size.x +1); //nr of time units per pixel. 
      timeorg.pixel7time = (float)(size.x +1) / timeorg.timeSpread; //nr of pixel per time unit.
      int xViewPart = -1;   //nr of new pixel if only a part is drawn
      final int xp0;  //left point to draw.
      final int timeDiff; //time for new values.
      testHelp.xView =xView; testHelp.yView =yView; testHelp.dxView =dxView; testHelp.dyView =dyView;
      //
      if(!bFreeze && !super.bPaintAllCmd && redrawBecauseNewData1) {
        //paint only a part of the curve to save calculation time.
        //The curve will be shifted to left.
        //
        if(ixDataRight != ixDataDraw){
          testStopWr = true;
          stop();
        }  
        int timeLast = timeValues[(ixDataDraw >> shIxiData) & mIxiData];
        int timeNow = timeValues[(ixDataRight >> shIxiData) & mIxiData];
        timeDiff = timeNow - timeLast + timeCaryOverNewValue;  //0 if nothing was written.
        xViewPart = (int)(timeorg.pixel7time * timeDiff + 0.0f);
        if(xViewPart > size.x){
          xViewPart = size.x;   //for example if no data were received in the past, then timeDiff is hi.
        }
        xp0 = drawShiftAreaToLeft(g, size, xView, dxView, yView, dyView, xViewPart, timeDiff);
      } else { //paintall
        timeorg.calc();
        //System.out.println("SwtCurveView - paintall;" + bFreeze + bPaintAllCmd + redrawBecauseNewData1);
        xViewPart = size.x;
        timeCaryOverNewValue = 0;
        timeDiff = (int)(timeorg.timePerPixel * xViewPart);
        xp0 = 0;
        super.bPaintAllCmd = false; //accepted, done
        testHelp.ctRedrawAll +=1;
        //xViewLast = 0;
        xViewLastF = 0.0F;
        nrofDataShift.set(0);
      }
      newSamples = 0;  //next call of setSample will cause only draw of less area.
      
      //draw grid and 0-lines:
      if(xViewPart >0) { //only if anything is to draw
        //only if a new point should be drawn.
        try{Thread.sleep(2);} catch(InterruptedException exc){}
        drawRightOrAll(g, size, xView, dxView, yView, dyView, ixDataRight, xViewPart, timeDiff, xp0);
        
      } else { //xViewPart == 0
        //This is is normal case if a new value in data has a too less new timestamp.
        //It can't be shown. Await the next values. Any value will be have a more newer timestamp
        //with a great-enough time difference to show it.
        super.nrofValuesLessViewPart +=1;
        //System.out.println("SwtCurveView - xViewPart=0");
      }
      if(nrofValues >0){  //don't work if no data are stored.
        int ixDataShow2 = ((ixDataWr - ixDataDraw)  >> shIxiData) & mIxiData;  //index of data which are shown right
        int ixDataShow1 = ((ixDataWr - ixDataShown[size.x])  >> shIxiData) & mIxiData; //index of data which are shown left
        float ixDataRel2 = (float)ixDataShow2 / maxNrofXValues;  //value 0..1 which range of buffer is shown 
        float ixDataRel1 = (float)ixDataShow1 / maxNrofXValues;
        int iPixRange2 = size.x - (int)(size.x * ixDataRel2);  //Position shown range right in pixel
        int iPixRange1 = size.x - (int)(size.x * ixDataRel1);  //left
        //int ixDWr = (ixDataWr >> shIxiData) & mIxiData;
        //System.out.println("SwtCurveView.spread; " + ixDataRel1 + ".." + ixDataRel2);
        g.setLineWidth(5);
        g.setForeground((Color)itsMng.getColorImpl(GralColor.getColor("ye")));
        g.drawLine(0, size.y -3, iPixRange1, size.y -3);  //left not shown range.
        g.drawLine(iPixRange2, size.y -3, size.x, size.y -3);  //right non shown range.
        g.setForeground((Color)itsMng.getColorImpl(GralColor.getColor("dgr")));
        //g.setAlpha(128);
        g.drawLine(iPixRange1, size.y -3, iPixRange2, size.y -3);  //shown range
      }
    } catch(Exception exc){
      StackTraceElement[] stack = exc.getStackTrace();
      System.err.println("SwtCurveView-paint-exception; " + exc.getMessage() + ";" + stack[0].getFileName() + ":" + stack[0].getLineNumber());
      stop();
    }
    //g.drawString(""+xShift+ ":"+ xViewLast + ":" + nrofDataShift.get(), 200, dyView-28);
    //g.drawString("xx", 200, dyView-16);
    focusChanged = false; //paint only last area by next paint event without focus event.
    testStopWr = false;
  } 

  
  /**Implementation of the graphic thread widget set interface. */
  GralWidgetGthreadSet_ifc gThreadSet = new GralWidgetGthreadSet_ifc(){

    @Override public void clearGthread()
    { // TODO Auto-generated method stub
    }

    @Override public void insertGthread(int pos, Object visibleInfo, Object data)
    { // TODO Auto-generated method stub
    }

    @Override public void redrawGthread()
    { // TODO Auto-generated method stub
    }

    @Override public void setBackGroundColorGthread(GralColor color)
    { // TODO Auto-generated method stub
    }

    @Override public void setForeGroundColorGthread(GralColor color)
    { // TODO Auto-generated method stub
    }

    @Override public void setTextGthread(String text, Object data)
    { // TODO Auto-generated method stub
    }
  };
  
  
  private class CurveView extends Canvas
  {
    
    //private final CurveView widgSwt;

    //final Canvas canvas;
    
    //private final Color[] lineColors;
    
    
    public CurveView(Composite parent, int xPixel, int yPixel, int nrofXvalues,
        int nrofTracks){
      super(parent, org.eclipse.swt.SWT.NO_SCROLL|org.eclipse.swt.SWT.NO_BACKGROUND);
      setData("Control", this);
      setSize(xPixel, yPixel);  //the size may be changed later by drag the window.
      //this.xPixel = xPixel;
      //this.yPixel = yPixel;
      //lineColors = new Color[nrofTracks];
      Color defaultColor = new Color(getDisplay(), 255,0,0);
      for(int iTrack=0; iTrack < nrofTracks; ++iTrack){
        //lineColors[iTrack] = defaultColor;
      }
      addPaintListener(paintListener);
      addFocusListener(focusListener);
      addMouseListener(mouseListenerCurve);
      //addMouseListener(mouseLeftButtonListener);
      //addMouseMoveListener(mouseMoveListener);
      //widgSwt = this;
    }
    
    

    
    public void setGridVertical(int dataPointsBetweenGridLines, int periodStrongLine){
      gridDistanceY = dataPointsBetweenGridLines;
      gridStrongPeriodY = periodStrongLine;
      gridDistanceStrongY = dataPointsBetweenGridLines * periodStrongLine;
    }
    
    
    /**Set distance for horizontal grid lines.
     * @param percentY percent of y-view for grid lines. For example 50.0: divide y-axis in 50 steps. 
     * @param periodStrongLine period for strong lines For example 5, any 5. line is stroke.
     */
    public void setGridHorizontal(float percentY, int periodStrongLine){
      gridDistanceX = percentY;
      gridStrongPeriodX = periodStrongLine;
    }
    
    
    
    public void redrawData(){
      redrawBecauseNewData = true;
      redraw();
    }
    
    
    
    
    
    
    
    PaintListener paintListener = new PaintListener()
    {

      @Override public void paintControl(PaintEvent e) {
        GC gc = e.gc;
        drawBackground(e.gc, e.x, e.y, e.width, e.height);
      }
      
    };

    
    
    FocusListener focusListener = new FocusListener()
    { @Override public void focusGained(FocusEvent e) {
        focusChanged = true;
      }

      @Override public void focusLost(FocusEvent e) {
        focusChanged = true;
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
    }    
  }

  
  /**This class is only used to store values to inspect. The Inspector is a tool which works with
   * reflection and with it internal variable can be visited in runtime. See {@link org.vishia.inspector.Inspector}.
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
    
    /**Coordinates while redraw. {@link CurveView#drawBackground(GC, int, int, int, int)}. */
    int xView, yView, dxView, dyView;
  } 
  TestHelp testHelp = new TestHelp();

  
  SwtGralMouseListener.MouseListenerUserAction mouseListenerCurve = new SwtGralMouseListener.MouseListenerUserAction( super.mouseAction);
  

  void stop(){}  
  
}
