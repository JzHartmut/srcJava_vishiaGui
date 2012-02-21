package org.vishia.gral.widget;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralSetValue_ifc;
import org.vishia.gral.ifc.GralWidget;


public abstract class GralCurveView extends GralWidget
{
  
  /**Version and history
   * <ul>
   * <li>2012-02-21 Hartmut Now the CurveView works in the new environment. Some adjustments necessary yet.
   * <li>2011-06-00 Hartmut New concept of GralWidget etc and new Configuration concept
   *   with {@link org.vishia.gral.cfg.GralCfgBuilder}. The old GuiDialogZbnfControlled.class
   *   was not use nevermore. But the CurveView was not adapted for that.
   * <li>2010-03-00 Hartmut The curve view was development as basic feature.
   * </ul>
   */
  public final static int version = 0x20120222;
  
  public static class Line implements GralSetValue_ifc {
    public final String name;
    public String sDataPath;
    
    private Object oContent;
    
    private int dataIx;
    
    public float actValue;
    
    float min, max;
    
    public Line(String name){ this.name = name; }

    @Override public void setContentInfo(Object content) { oContent = content; }

    @Override public Object getContentInfo() { return oContent;  }

    @Override public void setDataPath(String sDataPath) { this.sDataPath = sDataPath; }

    @Override public String getDataPath() { return sDataPath; }

    @Override public int getDataIx() { return dataIx; }

    @Override public void setDataIx(int dataIx) { this.dataIx = dataIx; }

    @Override public void setValue(float value) { this.actValue = value; }

    @Override public void setMinMax(float minValue, float maxValue) {
      this.min = minValue; this.max = maxValue;
    }
  }
  
  protected Line[] lines;
  
  
  protected final List<GralSetValue_ifc> listlines = new LinkedList<GralSetValue_ifc>();
  
  
  /**This list describes the data pathes in that order, which should be regard
   * calling {@link #setSample(float[])}.
   * 
   */
  //protected final List<String> listDataPaths = new LinkedList<String>();
  
  
  protected int ixLineInit = 0;
  
  protected final float[][] values;
  
  /**The percent from 0..100 where the 0-line is presented. */
  protected final int[] y0Line;
  
  /**The pixel value for y0Line refered to pixel-height. */
  protected final float[] y0Pix;
  
  
  
  /**The value of input data which is shown at the 0-line. */
  protected final float[] yOffset;
  
  /**The factor to multiply for 1 pixel. for 10 percent.. */
  protected final float[] yFactor;
  
  /**The scale for 10 percent of view without zoom.. */
  protected final float[] yScale;

  /**last values for paint. 
   * The current paint goes from lastValueY[track][1] to the current point.
   * The lastValueY[track][0] is used to repaint the last curve peace while shifting draw content.
   */
  protected final int[][] lastValueY;
  
  /**last x-positions for paint. 
   * The lastPositionX[0] is used to repaint the last curve peace while shifting draw content.
   * Where, xShift (number of shifted pixel) is considered by subtraction.
   */
  protected final int[] lastPositionX = new int[2];
  
  /**The last index to data, to detect grid line. */
  protected int iDataLast;
  
  protected final GralColor[] lineColorsGral;
  
  
  /**Current number of values in the data field. 
   * If less values are set ({@link #setSample(float[])}, 
   * then the nrofValues is less than values.length.
   * Else it is ==values.length. */
  protected int nrofValues = 0;
  
  
  protected int nrofValuesForGrid;
  
  
  /**Distance of nrofValues for one vertical grid line (strong or not strong). */
  protected int gridDistanceY;
  
  /**Distance of percent of y-view for one vertical grid line (strong or not strong). */
  protected float gridDistanceX;
  
  /**period of strong lines. */
  protected int gridStrongPeriodX, gridStrongPeriodY;
  
  /**Distance of nrofValues for one vertical strong grid line.
   * This value is used to limit nrofValuesForGrid. */
  protected int gridDistanceStrongY;
  
  protected GralColor gridColorGral; // = new Color(getDisplay(), 0, 255, 255);
  
  protected GralColor gridColorGralStrong; // = new Color(getDisplay(), 0, 255, 255);
  
  /**Zoom factor. If zoom = 1.0, the curve will be shown in the current canvas area.
   * It the zoom-factor is greater 1.0, not all points will be shown. 
   * The origin point of the values is given with {@link #xOrigin} and {@link #yOrigin}.
   */
  protected float xZoom = 1.0F, yZoom= 1.0F;
  
  /**Origin point from 0.0 to 1.0 for the zoomed area. */
  protected float xOrigin = 0.0F, yOrigin = 0.0F;
  
  protected GralColor colorBackGral; // = new Color(getDisplay(), 0xff, 0xff, 0xff);

  
  protected boolean focusChanged = false;  //it doesn't work
  
  /**Set to true to force a paint all. */
  protected boolean paintAllCmd = false;
  
  protected int newSamples;
  
  /**last point in x where values were drawn. */
  protected float xViewLastF = 0;
  
  
  
  /**Number of iData-indices, which are shifted in the {@link #values}. 
   * This number have to be shifted in the pixel area if a draw-all is not requested. 
   * This field is set to 0 if the draw action is done.*/
  protected AtomicInteger nrofDataShift = new AtomicInteger(0);
  
  protected float nrofDataShiftFracPart = 0.0F;
  
  /**Set true if {@link #redrawData()} is called. Then only the area for new data is drawn 
   * in the {@link #drawBackground(GC, int, int, int, int)}-routine.
   * 
   */
  protected boolean redrawBecauseNewData;
  
  




  public GralCurveView(String sName, GralWidgetMng mng, int nrofXvalues, int nrofTracks)
  {
    super(sName, 'c', mng);
    lines = new Line[nrofTracks];
    values = new float[nrofXvalues][nrofTracks];
    y0Line = new int[nrofTracks];
    y0Pix = new float[nrofTracks];
    yOffset = new float[nrofTracks];
    yFactor = new float[nrofTracks];
    yScale = new float[nrofTracks];
    lastValueY = new int[nrofTracks][2];
    lineColorsGral = new GralColor[nrofTracks];
    setPanelMng(mng);
    mng.registerWidget(this);
    for(int iTrack=0; iTrack < nrofTracks; ++iTrack){
      y0Line[iTrack] = (iTrack+1) * (100 / (nrofTracks+1));
      yOffset[iTrack] = 0.0F;
      yScale[iTrack] = 10.0F;
      //yFactor[iTrack] = yPixel / 10.0F / yScale[iTrack];
      //lineColors[iTrack] = defaultColor;
    }
  }

  
  /**Initializes a line of the curve view.
   * This routine should be called after construction, only one time per line
   * in the order of lines.
   * @param sNameLine
   * @param sDataPath
   * @param colorValue
   * @param style
   * @param nullLine
   * @param scale
   * @param offset
   */
  abstract public void initLine(String sNameLine, String sDataPath, int colorValue, int style
      , int nullLine, float scale, float offset);

  
  
  /**This list describes the data paths in that order, which should be regard
   * calling {@link #setSample(float[])}.
   */
  public List<GralSetValue_ifc> getLines(){ return listlines; }
  
  
  /**Adds a sampling value set.
   * <br><br> 
   * This method can be called in any thread. It updates only data,
   * a GUI-call isn't done. But the method is not thread-safe. 
   * If more as one threads writes data, an external synchronization should be done
   * which may encapsulate more as only this call.
   * <br><br> 
   * The forcing redraw should be triggered outside. It may be triggered only
   * if more as one samples are set. The redraw-call have to be execute in the GUI-thread.
   * Hint: use {@link org.vishia.mainGuiSwt.MainCmdSwt#addDispatchOrder(Runnable)}
   * to force redraw for this component. The Runnable-method should call widget.redraw().
   * @param sName The registered name
   * @param values The values.
   */
  public abstract void setSample(float[] newValues);

  
  @Override
  public Object getWidgetImplementation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  

  
  @Override
  public void removeWidgetImplementation()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  
  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { //widgetSwt.setBounds(x,y,dx,dy);
  }

  @Override
  public boolean setFocus()
  {
    // TODO Auto-generated method stub
    return false;
  }
  

}
