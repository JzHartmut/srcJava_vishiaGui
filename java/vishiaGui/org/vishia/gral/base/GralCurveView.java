package org.vishia.gral.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.vishia.byteData.VariableAccessWithIdx;
import org.vishia.byteData.VariableAccess_ifc;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.curves.WriteCurve_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralCurveViewTrack_ifc;
import org.vishia.gral.ifc.GralCurveView_ifc;
import org.vishia.gral.ifc.GralSetValue_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.mainCmd.Report;
import org.vishia.mainCmd.ReportWrapperLog;
import org.vishia.util.Assert;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;
import org.vishia.util.Removeable;
import org.vishia.util.TimedValues;
import org.vishia.util.Timeshort;
import org.vishia.zbnf.ZbnfJavaOutput;
import org.vishia.zbnf.ZbnfParser;


/**Curve representation for timed values. It is the base class for all implementation.
 * 
 * <ul>
 * <li> {@link #setSample(float[], int)}: Write out with new measured values.
 * </ul>
 * @see GralCurveViewTrack_ifc.
 * @author Hartmut Schorrig
 *
 */
public class GralCurveView extends GralWidget implements GralCurveView_ifc
{
  
  /**Version, history and license.
   * <ul>
   * <li>2024-03-02 Hartmut important bugfix in {@link #setSample(float[], int, int)}: 
   *   Since a long time the variable testStopWr was active and had prevented write data while refresh graphic runs. 
   *   This was only a debug helper, but was not obviously in normal operations (usual),
   *   but with very fast telegrams from a running simulation it was obviously. 
   * <li>2022-09-28 Hartmut deal with timeShort from the measurement resp. value source.
   *   The absolute time is either associated here from the {@link System#currentTimeMillis()} 
   *   or also possible for a given absolute time relation for the values outside of the current values.
   * <li>2018-09-20 Hartmut new feature on {@link #readCurveCsvHeadline(File)}, a variable which is not yet in cfg is disabled.
   *   Elsewhere not prepared stuff is shown in the graphic. It can be enabled and scaled by the user afterwards.
   *   The importance is, the variable will be inserted as signal (track) automatically
   * <li>2018-09-20 Hartmut improved for using in InspcCurveViewApp.
   *   <ul>
   *   <li>initTrack(...) renamed to {@link #addTrack(String, String, GralColor, int, int, float, float)}
   *   <li>{@link #searchTrack(String)} if data are read from csv for existing configuration.
   *   <li>{@link #readCurve(File)} now expects names of variable in the first line in any case, not specific format.
   *   <li>{@link #readCurveCsvHeadline(File)} can replace the correct variables in the configuration or builds new tracks
   *     for non existing configurated tracks.
   *   </ul>
   * <li>2016-07-03 Hartmut chg: {@link GraphicImplAccess} as base class of the implementing class SwtCurveView adequate to the  new concept: 
   *   An implementing widget is derived from its derived class of {@link GralWidget.ImplAccess}. Therefore only that base class implements the GralWidgetImpl_ifc.
   * <li>2016-03-06 Hartmut chg: {@link #refreshFromVariable(VariableContainer_ifc)}: Handling of timeshort: If the timeshort starts with a lesser value
   *   it seems to be a new simulation starting with 0. Then the time difference to the last simulation is calculated and added to the timeshort. 
   *   It sets a new {@link #setTimePoint(long, int, float)} to prevent overflow later. Initially or if {@link #cleanBuffer()} was invoked the timeshort counts from 0.
   *   Therewith the simulation results have an exact relative time but with the absolute timestamp of the PC's time though the simulation does not supply an absolute time.
   *   Before that change a bug is detected in {@link #prepareIndicesDataForDrawing(int, int, int, boolean)}: If the buffer from right side contains a greater short time, 
   *   the indices return only 1 point. It is a bug in this function, not corrected yet because there are not greater times from right to left with the change above.
   *   TODO fix it though.     
   * <li>2016-01-24 Hartmut chg: {@link CommonCurve#lastTimeShort}: Don't store points with the same time stamp.
   * <li>2016-01-24 Hartmut bugfix: {@link #applySettings(String)} after read cfg: remove previous {@link CommonCurve#timeVariable}, may be a new one! 
   * <li>2015-07-12 Hartmut new: {@link Track#setVisible(int)} to control the visibility of tracks.
   * <li>2015-07-12 Hartmut new: {@link Track#groupTrackScale(GralCurveViewTrack_ifc)} for groups of tracks with same scaling.
   * <li>2014-05-20 Hartmut new in {@link #setSample(float[], int)}: write a point only if at least one variable was refreshed.
   * <li>2014-03-14 Hartmut new: {@link CommonCurve#timeVariable} for time from target. 
   * <li>2014-02-03 Hartmut new: {@link CommonCurve#bFreeze}: freeze as common property of more as one GralCurveView. Constructor argument.
   * <li>2014-01-29 Hartmut new: Comment in Datapath supported. For nice presentation in list on long variable paths. 
   * <li>2013-11-19 Hartmut new: {@link #redraw(int, int)} overridden forces paint of the whole curve
   *   by setting {@link #bPaintAllCmd}, whereby in {@link #setSample(float[], int)} super.repaint is invoked, 
   *   which does not set {@link #bPaintAllCmd} and paints therefore only the new data. 
   * <li>2013-07-25 Hartmut new AutoSave capabilities.
   * <li>2013-05-19 Hartmut new: {@link GralCurveViewMouseAction}
   * <li>2013-05-19 Hartmut new: {@link #selectTrack(int, int, int, int)} with ctrl and left mouse pressed
   * <li>2013-05-14 Hartmut new: {@link Track#getIxTrack()}
   * <li>2013-03-27 Hartmut bugfix: The {@link #bNewGetVariables} have to be set in {@link #setDataPath(String)} and 
   *   {@link #applySettings(String)} because this methods sets a new variable which should be searched. 
   *   All other changes are gardening and comments.
   * <li>2013-01-25 Hartmut improved: Error message in {@link #refreshFromVariable(VariableContainer_ifc)} only on
   *   paint-complete of the view. If the implementation receives a paintAll command by mouseClick on the graphic
   *   or because the graphic is shown the first time, the {@link #setPaintAllCmd()} is invoked there. The variable
   *   {@link #bNewGetVariables} is set, that causes the error message.
   *   Before: no error message was created, the error was obscure. 
   * <li>2012-08-11 Hartmut now grid with  timestamps
   * <li>2012-06-08 Hartmut new: {@link #applySettings(String)} and {@link #writeSettings(Appendable)} for saving
   *   and getting the configuration of curve view from a file or another text.
   * <li>2012-04-01 Hartmut new: Using {@link VariableAccessWithIdx} to access values.
   * <li>2012-03-25 Hartmut chg: Some routines from SWT moved to this because there are independent.
   * <li>2012-03-17 Hartmut chg: All track-associated data now in the {@link Track} inner class.
   * <li>2012-02-25 Hartmut new: All data have a short timestamp. The x-pixel are calculated with timestamp,
   *   not only with currently storage.
   * <li>2012-02-25 Hartmut new: Zoom functinality with timestamp and cursors
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
  public final static int version = 20240202;
  
  
  
  
  public class CommonCurve {

    /**If true, then the display is freezed.
     */
    public boolean bFreeze = false;
    

    /**If set, then use this variable to get the short time.
     * 
     */
    public VariableAccess_ifc timeVariable;
    
    /**If set, the {@link #timeVariable} will be initialized.
     * 
     */
    public String timeDatapath;

    
    
  }
  
  
  
  
  
  
  /**Instances of this class were be created only temporary while transfer a parse result of {@link GralCurveView#applySettings(String)}
   * The instance holds values for {@link ZbnfJavaOutput} to apply to a track 
   * calling {@link GralCurveView#addTrack(String, String, GralColor, int, int, float, float)}
   * inside {@link GralCurveView.ZbnfSetCurve#add_Track(ZbnfSetTrack)}.
   */
  public static class ZbnfSetTrack {
    /**From Zbnf component with semantic <?name>. */
    public String name;
    /**From Zbnf component with semantic <?datapath>. */
    public String datapath;
    /**Color and style from Zbnf component with semantic <?color>. */
    GralColor color_; int style_ = 0;
    /**From Zbnf component with semantic <?nullLine>. */
    public int nullLine;
    /**From Zbnf component with semantic <?scale>. */
    public float scale;
    /**From Zbnf component with semantic <?offset>. */
    public float offset;
    /**From Zbnf component with semantic <?color>. */
    public void set_color(String color){ color_ = GralColor.getColor(color.trim()); }
  }
  
  
  
  /**The instance of this class {@link GralCurveView#zbnfSetCurve} will be used only temporary while transfer a parse result 
   * of {@link GralCurveView#applySettings(String)}
   * The instance contains only the capabilty to get the track info from the parse result
   * and to call {@link GralCurveView#addTrack(String, String, GralColor, int, int, float, float)}
   * inside {@link #add_Track(ZbnfSetTrack)}.
   */
  public class ZbnfSetCurve{
    /**From Zbnf component with semantic <?Track>. */
    public ZbnfSetTrack new_Track(){
      return new ZbnfSetTrack();
    }
    /**From Zbnf component with semantic <?Track>. */
    public void add_Track(ZbnfSetTrack track){
      addTrack(track.name, track.datapath, track.color_, track.style_, track.nullLine, track.scale, track.offset);
    }
    
    public void set_timeDatapath(String val){
      GralCurveView.this.common.timeDatapath = val;
    }
    
  }
  
  final ZbnfSetCurve zbnfSetCurve = new ZbnfSetCurve();

  
  /**Instances of this class contain the scaling for one ore more tracks. 
   * One instance if referenced from maybe one or more as one track.
   */
  public static class TrackScale
  {
    /**The scale for 10 percent of view without zoom.. */
    public float yScale;
    
    /**The value of input data which is shown at the 0-line. */
    public float yOffset;
    
    //public float yFactor;
    
    /**The percent from 0..100 where the 0-line is presented. */
    public int y0Line;
    
    
    @Override public TrackScale clone(){ 
      try{ return (TrackScale)super.clone(); }
      catch(CloneNotSupportedException exc){
        TrackScale ret = new TrackScale();
        ret.yScale = this.yScale;
        ret.yOffset = this.yOffset;
        ret.y0Line = this.y0Line;
        return ret;
      }
    }
  }
  
  
  
  /**All track values, also time stamps in a Gral independent data class.*/
  protected TimedValues _tracksValue; 
  
  /**All track values, also time stamps in a Gral independent data class.*/
  public TimedValues tracksValue() { return this._tracksValue; }
  
  
  
  
  /**The describing and the actual data of one track (one curve)
   */
  public final static class Track implements GralCurveViewTrack_ifc, GralSetValue_ifc {
    public final String name;
    
    /**This is also for debug access to see the time to the values.
     * 
     */
    final TimedValues timeValues;
    
    /**Contains all values maybe in different formats double, float, int, short
     * and also the name of the tracke, min and max. 
     * The time for the values are stored in {@link TimedValues#timeShort}
     */
    public final TimedValues.Track valueTrack;
    
    /**The index of the track in the List of tracks. 
     * It is the correspondent index in the float parameter for {@link GralCurveView#setSample(float[], int)}
     */
    private final int ixList;
    
    private final GralCurveView outer;
    
    private String sDataPath;
    
    /**This value is set in {@link GralCurveView#refreshFromVariable(VariableContainer_ifc)}
     * after {@link #setDataPath(String)} is given. 
     */
    protected VariableAccess_ifc variable;
    
    private Object oContent;
    
    /**Index of a variable
     * 
     */
    private int dataIx;
    
    public float YYYactValue;
    
    /**Reference to the scaling to show the track. More as one track can build a scale group 
     * which refers the same instance. */
    public TrackScale scale;
    
    /**The color of the line. */
    public GralColor lineColor;
    
    /**The brightness of the line. It is used to show the selected line. */
    public int lineWidth = 1;
    
    
    /**The state to show. 0=hidden, don't show, but the values are stored.
     * 1= show normal. 2= show lifted out (selected).
     */
    public int showSelected = 1;
    //public float y0Pix;
    
    /**last values for paint. 
     * The current paint goes from lastValueY[1] to the current point.
     * The lastValueY[0] is used to repaint the last curve peace while shifting draw content.
     */
    protected final int[] XXXlastValueY = new int[2];

    int identLastSelect;
    
    /**The value from last draw, do not calculate twice. */
    public int ypixLast;
    
    public Track(GralCurveView outer, String name, int ixList){ 
      this.outer = outer; this.name = name; this.ixList = ixList; 
      this.timeValues = outer._tracksValue;
      TimedValues.Track valueTrack = outer._tracksValue.getTrack(name);
      if(valueTrack ==null) {                              // use an existing ValueTrack
        valueTrack = outer._tracksValue.addTrack(name, 'F');// or create it here for all.
      }
      this.valueTrack = valueTrack;
    }

    @Override public void setContentInfo(Object content) { oContent = content; }

    @Override public Object getContentInfo() { return oContent;  }

    @Override public void setDataPath(String sDataPath) { 
      this.sDataPath = sDataPath; 
      this.variable = null;
      outer.bNewGetVariables= true;  //force searching the variable
    }

    @Override public String getDataPath() { return sDataPath; }

    @Override public int getDataIx() { return dataIx; }

    @Override public void setDataIx(int dataIx) { this.dataIx = dataIx; }

    @Override public void setValue(float value) { this.YYYactValue = value; }

    @Override public void setLongValue(long value) { this.YYYactValue = value; }

    @Override public void setValue(Object[] value) { } //TODO this.actValue = value[0]; }

    /**Maybe used for scaling, yet unused here.
     * @see org.vishia.gral.ifc.GralSetValue_ifc#setMinMax(float, float)
     */
    @Override public void setMinMax(float minValue, float maxValue) {
      this.valueTrack.min = minValue; this.valueTrack.max = maxValue;
    }

    @Override public int getLinePercent(){ return scale.y0Line; }
   
    @Override public float getOffset(){ return scale.yOffset; }
    
    @Override public float getScale7div(){ return scale.yScale;  }

    @Override public GralColor getLineColor(){ return lineColor; }

    
    /**Change the scaling of a track.
     * @param trackNr Number of the track in order of creation, 0 ist the first.
     * @param scale7div value per division
     * @param offset value, which is shown at line0
     * @param line0 percent of 0-line in graphic.
     */
    public void setTrackScale(float scale7div, float offset, int line0){
      scale.yScale = scale7div;
      scale.yOffset = offset;
      scale.y0Line = line0;
    }

    
    /**Sets the scaling to sharing with another track. Changing one of this tracks with 
     * {@link #setTrackScale(float, float, int)} influences all shared tracks immediately.
     * All shared tracks refer the same instance of {@link TrackScale}.
     * @param from
     */
    @Override public void groupTrackScale(GralCurveViewTrack_ifc from){ scale = ((Track)from).scale; }
    
    @Override public boolean isGroupedTrackScale(GralCurveViewTrack_ifc with){ 
      if(with == null) return false;
      return scale == ((Track)with).scale;
    }
    
    /**Sets the scaling to a own instance of {@link TrackScale}. The track is independent now. */
    @Override public void ungroupTrackScale(){
      TrackScale oldScale = this.scale;
      this.scale = oldScale.clone();
    }
    
    @Override public float getValueCursorLeft(){ return getValueCursor(((GraphicImplAccess)this.outer._wdgImpl).xpCursor1); }
    
    
    
    @Override public float getValueCursorRight(){ return getValueCursor(((GraphicImplAccess)this.outer._wdgImpl).xpCursor2); }
    
    
    private float getValueCursor(int cursor){ 
      float value; ///
      if(cursor >=0 && cursor < ((GraphicImplAccess)this.outer._wdgImpl)._dataimpl.dataIxToShow.length){
        try{
          int ixData = ((GraphicImplAccess)this.outer._wdgImpl).getDataIxFromPixelRight(cursor);
          value = this.valueTrack.getFloat(ixData); 
        } catch(Exception exc){
          value = 77777.7f;
        }
      } else {
        value = 777777.7f;
      }
      return value;
    }
    
    
    
    @Override public float getValueLast(){ return 0; }
    @Override public float getValueMin(){ return 0; }
    @Override public float getValueMax(){ return 0; }


    
    
    
    /**
     * @see org.vishia.gral.ifc.GralCurveViewTrack_ifc#setLineProperties(org.vishia.gral.ifc.GralColor, int, int)
     */
    @Override public void setLineProperties(GralColor color, int width, int pattern){ 
      if(color !=null) { lineColor = color; } 
      if(width >0) { lineWidth = width;}
    }

    @Override public void setVisible(int mode){ showSelected = mode; }
    
    @Override public int getVisible(){ return showSelected; }
    
    
    
    
    @Override public void setText(CharSequence text){
      System.err.println("GralCurveView - setText not supported; Widget = " + name + "; text=" + text);
    }
   
    
    
    @Override public String toString(){ return "Track: " + name + "(" + sDataPath + "," + (variable !=null ? variable.toString(): "variable = null") + ")"; }
    
  }
  
  /**Inner class for time organisation. */
  public static class TimeOrganisation{
    
    Timeshort absTime = new Timeshort();
    
    /**It is counted while the pair {@link #absTime_short} and {@link #absTime} is set newly.
     * The pair is consistent only if this counter is the same before and after read.
     */
    private volatile int ctTimeSet;
    
    /**The millisec per 1 timeshort step. If it remains 1.0 the timeshort is a integer millisecond value.
     * If the timeshort is a step counter this should be the sampling time for one step of timeshort. 
     */
    float msPerTimeshort = 1.0f;
    
    /**The last timeshort from the timeVariable. To detect newly simulation if it starts with a lesser value. */ 
    int timeshortLast;

    /**Value to add to the time stamp to get a continuous time for some simulations one after another. */
    int timeshortAdd;

    public int lastShortTimeDateInCurve;
    
    /**Short time stamp of the oldest stored point. */
    public int firstShortTimeDateInCurve;
    
    public int nrofPixelForTimestep;
    
    /**Division type.
     * <ul>
     * <li>'2': 100  2   4   6   8  10  12  14  16  18 120
     * <li>'1': 100 1  2  3  4  5  6  7  8  9  200
     * <li>'5': 100  5  10  15  20  25  30  35  40  45 150
     * </ul>
     */
    //public char divType;
    
    
    /**Number of pixel per 1 fine division in vertical lines. The time for 1 division is a rounded number.
     * The number of pixel have to be float, because it is cummulated. */
    float pixelPerTimeDiv, pixelPerTimeFineDiv;
    
    //int divPerBoldDiv = 10;
    
    /**Number of millisec between 2 fine divisions. It is a number of millisec able to divide by 10, 5 or 2. */
    int millisecPerDiv = 100, millisecPerFineDiv =10;
    
    /**Number of shorttime units for 1 pixel.
     * A time unit may be 1 millisecond for currently showing curves.
     * This value may be e.g. 100 to show 30 seconds in 300 pixel or e.g. 10 to show 5 seconds in 500 pixel.  
     * This value may be given in another unit for example 1 microseconds. It should be the same value unit
     * as used in {@link #setSample(float[], int)}.
     */
    public float timePerPixel = 1.0f;
    
    
    /**The reciprocal of {@link #timePerPixel}. The number of pixel for 1 short time step. It is less 1.0 often. */
    public float pixel7time = 1.0f;
    
    /**Number of time shortTime steps to showing the curve in the current view.
     * If this value is given (>=0) then the {@link #timePerPixel} will be calculated from this
     * regarding the current graphic size.
     * This value should be given in the same value unit as used for {@link #setSample(float[], int)}.
     */
    public int timeSpread = 50000;
    
    ///
    /**The last tested time to produce vertical lines for time division. */
    public int timeLeftShowing;
    
    public long timeAbsOnLastStrongDiv;
    
    //public String sTimeAbsSec;
    
    /**Accumulated nr of pixel written with short drawing after a strong division.
     * It is used to write the number after a proper time.
     */
    public int pixelWrittenAfterStrongDiv;
    
    /**Pixel position from right for fine divisions of time lines (vertical lines) and normal divisions. 
     * This array is filled newly whenever any draw or paint action is done. It is prepared in the routine
     * {@link #prepareIndicesDataForDrawing(int, int, int)} and used in the draw routine of the implementation level.
     */
    public final int[] xPixelTimeDiv = new int[50], xPixelTimeDivFine = new int[200];
    
    
    /**The text written at the time divisions. It is a mm:ss, ss.SSS or hh::mm */
    public final String[] sTimeAbsDiv = new String[50];
    
    
    int[] millisecPerFineDivVariants = new int[]{  1,  2,  5,  10,  20,  50,  100,  200,  500,  1000,  2000,  5000,  10000,  20000,  60000,  120000,  600000, 1200000,  3600000, 18000000};
    
    
    
    /**Milliseconds per division for number of pixel per fine division between 12..30. 
     *
     */
    int[] millisecPerDivVariants =     new int[]{  5, 10, 20,  50, 100, 200,  500, 1000, 2000,  5000, 10000, 20000,  60000, 120000, 300000,  600000, 3600000, 7200000, 18000000, 72000000};
    
    /**Calculates the divisions with known {@link #timePerPixel}.
     * This routine should be called whenever the display zoom will be changed.
     * It sets {@link #divType}, {@link #pixelPerTimeFineDiv}, {@link #millisecPerFineDiv}
     */
    public void calc(){
      int millisec20pixel = (int)(12 * timePerPixel * absTime.millisec7short());  //millisec for 12 pixel
      boolean bFound = false;
      for(int ii = 0; ii < millisecPerDivVariants.length; ++ii){  //search in [5 10 20 ...] etc. millisecPerDivisions 
        if(millisecPerFineDivVariants[ii] >= millisec20pixel){
          millisecPerDiv = millisecPerDivVariants[ii];            //sets millisecPerDiv and ..FineDiv
          millisecPerFineDiv = millisecPerFineDivVariants[ii];
          bFound = true;
          break;
        }
      }
      if(bFound) {
        pixelPerTimeDiv =     millisecPerDiv     / (timePerPixel * absTime.millisec7short());  //number of pixel per division, should be appropriate 48..150
        pixelPerTimeFineDiv = millisecPerFineDiv / (timePerPixel * absTime.millisec7short());  //number of pixel per division, should be appropriate 12..30
      } else {
        //use a fix value to prevent any failure calculations.
        pixelPerTimeDiv = 150;
        pixelPerTimeFineDiv = 30;
      }
      /*
      double millisecExp20pixel = Math.log10(millisec20pixel);
      int millisecExpInt20pixel = (int)millisecExp20pixel;
      int milli10sec = (int)Math.pow(10, millisecExpInt20pixel);
      float pixelMilli10sec = milli10sec / timePerPixel;
      if(pixelMilli10sec < 4){
        divType = '5';
        pixelPerTimeFineDiv = 5 * pixelMilli10sec;
        millisecPerFineDiv = milli10sec * 5;
      } else if(pixelMilli10sec < 10){
        divType = '2';
        pixelPerTimeFineDiv = 2 * pixelMilli10sec;
        millisecPerFineDiv = milli10sec * 2;
      } else{ //10..20
        divType = '1';
        pixelPerTimeFineDiv = pixelMilli10sec;
        millisecPerFineDiv = milli10sec;
      } 
      if(millisecPerFineDiv <=2000 ){
        millisecPerDiv = 10 * millisecPerFineDiv;  //up to 10 sec step
        pixelPerTimeDiv = 10 * pixelPerTimeFineDiv;
      } else if(millisecPerFineDiv <=20000 ){
        millisecPerDiv = 6 * millisecPerFineDiv;  //up to 10 sec step
        pixelPerTimeDiv = 6 * pixelPerTimeFineDiv;
      } else {
        millisecPerDiv = 10 * millisecPerFineDiv;  //minute steps.
        pixelPerTimeDiv = 10 * pixelPerTimeFineDiv;
      }
      */
    }

  }
  
  public TimeOrganisation _timeorg = new TimeOrganisation();
  
  
  /**All stuff for data in the non-implementing level.
   *
   */
  public static class DataOrganisation
  {
    /**Index in the {@link Track#values} where the last auto write was done.
     * See {@link GralCurveView#timeInitAutoSave()}. */
    public int ixDataStartAutoSave, ixDataEndAutoSave;
    
    
    public int ixDataStartSave, ixDataEndSave;
    
    /**The number of array elements which are used in {@link GraphicImplAccess#ixDataShownX} {@link GraphicImplAccess#dataIxToShow} for the current view.
     * Either the display size is less, then this is less then 2000. It is the usual case.
     * Or the number of available data are less, the it is lesser than the pixel size of the curve. 
     */
    protected int zPixelDataShown;

    
    /**Set it true if first time the data remains the buffer size and starts from 0.
     * If bHasWrapped == false then the data starts from 0. 
     * If bHasWrapped == true then the old data starts from ixDend, and goes wrapping till ixDend-1.
     * */
    public boolean bHasWrapped = false;
    
    /**Write index of values, increment by {@link #setSample(float[], int)}.
     * The index refers to the last written value.
     * Initially it is -1, the first write will be increment ?? to prevent any exception while values are not set before.
     */
    public int ixDEnd;

    /**Current number of values in the data field. 
     * If less values are set ({@link #setSample(float[])}, 
     * then the nrofValues is less than values.length.
     * Else it is ==values.length. */
    public int nrofValues = 0;

    /**Deepness of the storage of values to present.
     * It is a power of 2 anytime.
     */
    public final int maxNrofXValues;
    

    DataOrganisation(int maxnrofXValues) {
      this.maxNrofXValues = maxnrofXValues;
    }
    
  }
  
  protected final DataOrganisation _dataOrg;
  

  
  protected static class SaveOrganisation{
    
    /** Values to save for autosave. */
    public int nrofValuesAutoSave;
    
    
    public int ctValuesAutoSave;
    
    //boolean bAutoSave;
  }
  
  protected SaveOrganisation saveOrg = new SaveOrganisation();
  
  
  
  
  public static class PixelOrganisation
  {
    /**Size of the curve range in pixel. Set at any time on last draw action. */
    public int xPixelCurve, yPixelCurve;
    
  }

  /**All tracks to return for filling. It has the same members in the same order like {@link #listTracks}*/
  //protected final List<GralSetValue_ifc> listTrackSet = new LinkedList<GralSetValue_ifc>();
  
  
  public final CommonCurve common;

  public boolean bDbgNewX = true, bDbgAssertX=false;

  /**True then saves values.  */
  public boolean bActive;



  private boolean bNewGetVariables = true;


  /**A short timestamp for the values in {@link GralCurveView.Track#values}.
   * It maybe a millisecond timestamp. Then about 2000000 seconds are able to display.
   * It maybe a counter of step times, then per cfg the length of Tstep should be known. 
   * See {@link TimeOrganisation#msPerTimeshort} 
   * 
   * This array and the {@link Track#actValue} array are filled wrapping.
   * <br><br> 
   * A negative difference between 2 successive time stamps designates the time break point between newer values
   * and the successive currently points to the newest one in ascending direction (past to future).
   * Initially this array will be filled with successive values from 0 to negatives in step -1 
   * to designate a time break at all points. 
   * <br><br>
   * At startup of filling 3 data points this array is filled for example with
   * <pre>
   * -1234, -1245, -1256, -3, -4, -5, ... 
   * </pre>
   * In this example the wrapping time stamps are negative but successive ascending of course.
   * The not used 4. point is accepted as a correct point faulty, it produces a presentation to the value 0
   * because nothing is stored in data in 1253 time units (which may be milliseconds). This faulty presentation
   * is only present on startup of graphic and only if the difference is in range of presentation width,
   * see {@link #prepareIndicesDataForDrawing(int, int)}
   */
  //public final int[] timeValues;


  /**Distance of nrofValues for one vertical strong grid line.
   * This value is used to limit nrofValuesForGrid. */
  public int gridDistanceStrongY;



  public boolean testStopWr;


 

  public int newSamples;


  protected int nrofValuesForGrid;



  /**All tracks. Anytime if the tracks are changed a new List is created. It is because the list can be used in another thread yet in an iterator. */
  public List<Track> listTracks = new ArrayList<Track>();


  protected final GralCurveViewMouseAction mouseAction = new GralCurveViewMouseAction();


  /**This action is called whenever a cursor position is changed. */
  private GralUserAction actionMoveCursor;


  /**This action is called whenever a track was selected. */
  private GralUserAction actionSelectTrack;


  
  

  /**Constructs the CurveView comprehensive widget.
   * @param refPos reference position will also be changed by given position in sPosName, will be cloned for the widget.
   * @param sName widget name or also "@<position>: name" for cursor positioning relative or absolute.
   * @param maxNrofXvaluesP deepness of values, max. 16000000 (16 Mega), usual 10000 or such. This value will be increased to the next power of 2.
            Not used if tracksValues are given.
   * @param curveVariables null or possible given Variable set for more as one curve view
   * @param tracksValues null or possible given Track Values from another CurveView or such.
   */
  public GralCurveView(GralPos refPos, String sPosName, int maxNrofXvaluesP, CommonCurve curveVariables, TimedValues tracksValues)
  {
    super(refPos, sPosName, 'c');
    this.common = curveVariables == null ? new CommonCurve() : curveVariables;
    int maxNrofXvaluesT = tracksValues !=null ? tracksValues.getLength() : maxNrofXvaluesP; 
    int maxNrofXvalues1 = 1;
    int shIxData1 = 32;
    while(maxNrofXvalues1 < maxNrofXvaluesT){
      maxNrofXvalues1 <<=1L;
      shIxData1 -=1;
    }
    //this.shIxiData = shIxData1;
    this._dataOrg = new DataOrganisation(maxNrofXvalues1);
    //this.adIxData = 0x40000000 / (this._dataOrg.maxNrofXValues >>2);  //NOTE: integer division, all >>2
//    this.mIxData = ~(this.adIxData -1); //all bits which have to be used, mask out lower bits.
//    this.mIxiData = this._dataOrg.maxNrofXValues -1;  //e.g. from 0x1000 to 0xfff
    this._dataOrg.ixDEnd = -1; //initial write position, first increment to 0.
    this._tracksValue = tracksValues !=null ? tracksValues       // use given track values
                     : new TimedValues(this._dataOrg.maxNrofXValues);    // create here the trackValues.

    cleanBuffer();
    saveOrg.nrofValuesAutoSave = (int)(this._dataOrg.maxNrofXValues * 0.75);
    //values = new float[maxNrofXvalues][nrofTracks];
    //setPanelMng(mng);
    initMenuContext();
    setActionMouse(mouseAction, 0);

    //mng.registerWidget(this);
  }

  @Deprecated public GralCurveView(String sName, int maxNrofXvaluesP, CommonCurve common) {
    this((GralPos)null, sName, maxNrofXvaluesP, common, null);
  }  
  
  public void cleanBuffer()
  {
    for(int ix = 0; ix < this._dataOrg.maxNrofXValues; ++ix){
      //timeValues[ix] = ix;  //store succession of time values to designate it as empty.  
    }
    this._tracksValue.cleanSetCapacity(this._dataOrg.maxNrofXValues);
    this._timeorg.calc();
    this._timeorg.timeshortAdd = 0;
    this._timeorg.timeshortLast = 0;
    this._timeorg.absTime.clean();
    if(super._wdgImpl !=null) {
      GraphicImplAccess wdgi = (GraphicImplAccess)super._wdgImpl;
      wdgi.ixDdraw = this._dataOrg.ixDEnd =0;
      wdgi.ixDataCursor1 = wdgi.ixDataCursor2 = 0;
      wdgi._dataimpl.ixDshowRight = 0;
      Arrays.fill(wdgi._dataimpl.dataIxToShow, 0);
    }
  }
  
  
  
  
  /**It will be called after construction of the implementation graphic in the derived ctor.
   * 
   */
  public void initMenuContext(){
    GralMenu menuCurve = getContextMenu();
    //menuCurve.addMenuItemGthread("pause", "pause", null);
    menuCurve.addMenuItem("refresh", this.actionPaintAll);
    menuCurve.addMenuItem("go", this.actionGo);
    menuCurve.addMenuItem("show All", this.actionShowAll);
    //menuCurve.addMenuItemGthread("zoomOut", "zoom in", null);
    menuCurve.addMenuItem("zoomBetweenCursor", "zoom between Cursors", this.actionZoomBetweenCursors);
    menuCurve.addMenuItem("zoomOut", "zoom out", this.actionZoomOut);
    menuCurve.addMenuItem("cleanBuffer", "clean Buffer", this.actionCleanBuffer);
    //menuCurve.addMenuItemGthread("zoomOut", "to left", null);
    //menuCurve.addMenuItemGthread("zoomOut", "to right", null);
    
  }
  
  
  public CommonCurve getCommonData(){ return common; }
  
  /**This action will be called whenever a cursor position is changed. */
  public void setActionMoveCursor(GralUserAction action){ actionMoveCursor = action; }
  
  
  /**This action will be called whenever a track was selected. The selection will be done by
   * pressing the left mouse with ctrl on a curve view point. */
  public void setActionTrackSelected(GralUserAction action){ actionSelectTrack = action; }
  
  
  /**Creates and adds a track of the curve view. Note: The current {@link #listTracks} instance won't be changed.
   * Instead, a new ArrayList is created here with the content of listTracks and the new track.
   * This is because the {@link #listTracks} instance may be used in a iterator in another thread.
   * That is in {@link #setSample(float[], int)} which can be invoked in a cyclically or receiving thread,
   * or in {@link #refreshFromVariable(VariableContainer_ifc)} which is invoked in the Inspector Gui cyclically
   * to get values from target. If during operation a track was add by the users operation on GUI, it should not disturb the current process.
   * 
   * 
   * This routine should be called only one time per track to create a new one.
   * in the order of tracks.
   * Note: renamed from initTrack, 2018-09
   * @param sNameTrack
   * @param sDataPath
   * @param color
   * @param style
   * @param nullLine
   * @param scale
   * @param offset
   * @return The track instance.
   */
  public Track addTrack(String sNameTrack, String sDataPath, GralColor color, int style
      , int nullLine, float scale, float offset)
  {
    //Track track = initTrack(sNameTrack, sDataPath, color, style, nullLine, scale, offset, listTracksNew);
    Track track = new Track(this, sNameTrack, listTracks.size());
    track.scale = new TrackScale();
    track.sDataPath =sDataPath;
    track.variable = null;                                 // will be found and set in {@link GralCurveView#refreshFromVariable(VariableContainer_ifc)
    track.scale.y0Line = nullLine;
    track.scale.yOffset = offset;
    track.scale.yScale = scale;
    track.lineColor = color == null ? GralColor.getColor("rd") : color;
    bNewGetVariables = true;  //to force re-read of all variables.
    //
    //add the track, but build a new List
    List<Track> listTracksNew = new ArrayList<Track>();
    listTracksNew.addAll(this.listTracks); //Atomic access, iterate in local referenced list.
    listTracksNew.add(track);
    listTracks = listTracksNew;
    return track;
  }


  


  
  /**Searches and returns the track from listTrack per name.
   * @param name
   * @return null if not found.
   */
  private Track searchTrack(String name) {
    for(Track e: listTracks) {
      if(e.sDataPath.equals(name)) return e;
    }
    return null;
  }
  
  
  
  public void setTimePerPixel(int time){
    _timeorg.timePerPixel = time;
  }
  
  
  public void setTimeSpread(int time){
    if(time <= 0) throw new IllegalArgumentException("GralCurveView.setTimeSpread - value should >0");
    _timeorg.timeSpread = time; 
  }
  
  
  
  public float getdTimeCursors() {
    GraphicImplAccess impl = (GraphicImplAccess) this._wdgImpl;
    int dpCursor = impl.xpCursor2 - impl.xpCursor1;
    if(dpCursor <0) { dpCursor = -dpCursor; }
    return dpCursor / _timeorg.pixel7time;
  }
  
  
  /**Returns the path of the time variable if given or null.
   */
  public String getTimeVariable(){ return common.timeDatapath; }
  
  
  /**This list describes the data paths in that order, which should be regard
   * calling {@link #setSample(float[])}.
   * return a new List which should not modify. Modify has no sense because the list is not used inside this class.
   */
  public List<GralSetValue_ifc> getTracks(){ 
    List<GralSetValue_ifc> ret = new LinkedList<GralSetValue_ifc>();
    List<Track> listTracks1 = listTracks; //Atomic access, iterate in local referenced list.
    for(Track track : listTracks1){
      ret.add(track);
    }
    return ret; 
  }
  
  
  /**This Iterable describes the data paths in that order, which should be regard
   * calling {@link #setSample(float[])}.
   */
  public Iterable<? extends GralCurveViewTrack_ifc> getTrackInfo(){ return listTracks; }
  
  
  
  /**Returns that track which was selected by set cursor at last.
   * @return null if a track was not selected up to now, elsewhere one of the created tracks.
   */
  public Track getTrackSelected(){ return _wdgImpl == null ? null : ((GraphicImplAccess)_wdgImpl).trackSelected; }
  
  
  public boolean shouldAutosave(){
    return saveOrg.ctValuesAutoSave >= saveOrg.nrofValuesAutoSave;
  }
  
  
  public long timeRight(){
    int ixData = ((GraphicImplAccess)super._wdgImpl)._dataimpl.dataIxToShow[0];  //right
    int timeShort1 = this._tracksValue.getTimeShort(ixData );
    //synchronized()
    return _timeorg.absTime.absTimeshort(timeShort1);
    
  }
  
  
  
  @Override public void setTimePoint(long date, int timeshort, float millisecPerTimeshort){
    _timeorg.absTime.setTimePoint(date, timeshort, millisecPerTimeshort);
  }

  
  /**
   * @see org.vishia.gral.ifc.GralCurveView_ifc#setSample(float[], int)
   */
  @Override public void setSample(float[] values, int timeShort, int timeShortAdd) {
//    if(testStopWr) // this is not proper, disturb the storing of data, maybe only really for debug
//      return;  //only for debug test.
    //if(++ixDataWr >= this._dataOrg.maxNrofXValues){ ixDataWr = 0; } //wrap arround.
    if( ++saveOrg.ctValuesAutoSave > saveOrg.nrofValuesAutoSave) { 
      saveOrg.ctValuesAutoSave = saveOrg.nrofValuesAutoSave;  //no more.
    }
    if( ++this._dataOrg.ixDEnd >= this._dataOrg.maxNrofXValues) {
      this._dataOrg.ixDEnd = 0; this._dataOrg.bHasWrapped = true;
    }
    if(!common.bFreeze && super._wdgImpl !=null){
      ((GraphicImplAccess)super._wdgImpl)._dataimpl.ixDshowRight = this._dataOrg.ixDEnd;
    }
    this.newSamples +=1;  //information for paint event
    this.nrofValuesForGrid +=1;
    if(nrofValuesForGrid > this._dataOrg.maxNrofXValues + gridDistanceStrongY){
      nrofValuesForGrid -= gridDistanceStrongY;  //prevent large overflow.
    }
    int ixSource = -1;
    int ixWr =  this._dataOrg.ixDEnd;
    //
    //
    //assign the argument values to the track.values
    //
    for(Track track: listTracks){ 
      if(ixSource < values.length -1){
        float val = values[++ixSource];  //write in the values.
        track.valueTrack.setFloat(ixWr, val);  //write in the values.
        if(track.valueTrack.min > val ){ track.valueTrack.min = val; }
        if(track.valueTrack.max < val ){ track.valueTrack.max = val; }
      } else {
        track.valueTrack.setFloat(ixWr, 0);
      }
    }
    //
    int timeLast = this._tracksValue.getsetTimeShort(ixWr, timeShort, timeShortAdd);
    
    _timeorg.lastShortTimeDateInCurve = timeShort;
    if(this._dataOrg.nrofValues < this._dataOrg.maxNrofXValues){
      if(this._dataOrg.nrofValues ==0){
        _timeorg.firstShortTimeDateInCurve = timeShort;
      }
      this._dataOrg.nrofValues +=1;
    } else {
      _timeorg.firstShortTimeDateInCurve = timeLast;
      if(super._wdgImpl !=null) ((GraphicImplAccess)(super._wdgImpl)).nrofDataShift.incrementAndGet(); //shift data in graphic.
    }

    if(!common.bFreeze){
      synchronized(this){
        if(super._wdgImpl !=null) ((GraphicImplAccess)(super._wdgImpl)).redrawBecauseNewData = true;
      }
      super.redraw(50,100);
    }
  }

  
  
  /**The specific for this refresh is, that all variables are quest with the same time. 
   * If any variable is not refreshed, then it is not able to recognize.
   * Maybe later write curves in another kind. 
   * It may be assumed that all variables are updated simultaneously.
   * @see org.vishia.gral.base.GralWidget#refreshFromVariable(org.vishia.byteData.VariableContainer_ifc)
   */
  @Override public void refreshFromVariable(VariableContainer_ifc container){
    if(bActive){
      List<Track> listTracks1 = listTracks; //Atomic access, iterate in local referenced list.
      float[] values = new float[listTracks1.size()];
      int ixTrack = -1;
      int[] timeShortAbsLastFromVariable = null;
      boolean bDifferentTime = false;
      boolean bRefreshed = false; //set to true if at least one variable is refreshed.
      for(Track track: listTracks1){
        if(track.variable ==null && bNewGetVariables){ //no variable known, get it.
          String sDataPath = track.getDataPath();
          if(sDataPath !=null){
            if(sDataPath.startsWith("-")){ //it is a comment
              int posEnd = sDataPath.indexOf('-',1) +1;
              sDataPath = sDataPath.substring(posEnd);  //after second '-', inclusive first '-' if no second found.
            }
            String sPath2 = sDataPath.trim();
            if(!sDataPath.startsWith("#")){ //don't regard commented line
              String sPath = gralMng.getReplacerAlias().replaceDataPathPrefix(sPath2);  //replaces only the alias:
              track.variable = container.getVariable(sPath);
              if(track.variable == null){
                System.err.printf("GralCurveView - variable not found; %s in curveview: %s\n", sPath, super.name);
              }
            }
          }
        }
        final float value;
        
        if(track.variable !=null ){
          int[] timeShort = track.variable.getLastRefreshTimeShort();
          if(timeShort !=null && timeShort[0] !=0) {
            if(timeShortAbsLastFromVariable == null) { timeShortAbsLastFromVariable = timeShort; }
            else if(timeShort != timeShortAbsLastFromVariable) {      // has another time 
              //maybe todo  possibility to mark a curve as old
              bDifferentTime = timeShortAbsLastFromVariable[0] != timeShort[0];
              if((timeShort[0] - timeShortAbsLastFromVariable[0] ) >0) {
                timeShortAbsLastFromVariable[0] = timeShort[0];   //use the last value
                timeShortAbsLastFromVariable[1] = timeShort[1];
              }
            }
          }
          if(track.variable.isRefreshed()){
            bRefreshed = true;
          }
          if(track.getDataPath().startsWith("xxx:"))
            stop();
          value = track.variable.getFloat();
          track.variable.requestValue(System.currentTimeMillis());
        } else {
          value = 0;
        }
        values[++ixTrack] = value;
      }  
      bNewGetVariables = false;
      final long timeyet = System.currentTimeMillis();
      final int timeshort, timeshortAdd;
      if(this.common.timeDatapath !=null && this.common.timeVariable ==null){
        String sPath = gralMng.getReplacerAlias().replaceDataPathPrefix(this.common.timeDatapath);  //replaces only the alias:
        this.common.timeVariable = container.getVariable(sPath);
      }
      if(this.common.timeVariable !=null){
        //the time variable should contain a relative time stamp. It is the short time.
        //Usual it is in 1 ms-step. To use another step width, a mechanism in necessary.
        //1 ms in 32 bit are ca. +- 2000000 seconds or ~ +-23 days as longest time for differences.
        timeshort = this.common.timeVariable.getInt() + this._timeorg.timeshortAdd;
        this.common.timeVariable.requestValue(timeyet);
        if(this._timeorg.absTime.isCleaned()) {
          setTimePoint(timeyet, timeshort, 1.0f);  //the first time set.
          timeshortAdd = 0;
        }
        else if((timeshort - this._timeorg.timeshortLast) <0 || this._timeorg.absTime.isCleaned()) {
          //new simulation time:
          timeshortAdd = this._timeorg.absTime.timeshort4abstime(timeyet);
          this._timeorg.timeshortAdd += timeshortAdd; 
          setTimePoint(timeyet, timeshort, 1.0f);  //for later times set the timePoint newly to keep actual.
        } else {
          timeshortAdd = 0;
        }
      } else if(timeShortAbsLastFromVariable !=null) {
        timeshort = timeShortAbsLastFromVariable[0];
        timeshortAdd = timeShortAbsLastFromVariable[1];
        if(this._timeorg.absTime.isCleaned()) {
          setTimePoint(timeyet, timeshort + timeshortAdd, 1.0f);  //set always a timePoint if abstime is not given.
        }
      } else {
        timeshort = (int)timeyet;  //The milliseconds from absolute time.
        timeshortAdd = 0;
        setTimePoint(timeyet, timeshort, 1.0f);  //set always a timePoint if not data time is given.
      }
      if(bRefreshed && timeshort != this._timeorg.timeshortLast) {
        //don't write points with the same time, ignore seconds.
        setSample(values, timeshort, timeshortAdd);
        //System.out.println("setSample");
        this._timeorg.timeshortLast = timeshort;
      }
    }
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

  @Override public void activate(boolean activate){ bActive = activate; }
  
  @Override public boolean isActiv(){ return bActive; }
  
  @Override public boolean isFreezed(){ return common.bFreeze; }
  

  
  
  
  /**Applies the content of a configuration file or from a configuration string.
   * Each line contains one track. 
   * TODO detail syntax. 
   * see {@link GralCurveView_ifc#applySettings(String)}
   * @see org.vishia.gral.ifc.GralCurveView_ifc#applySettings(java.lang.String)
   */
  @Override public boolean applySettings(String in){
    boolean bOk;
    try{
      //variate syntax in test... 
      Report console = new ReportWrapperLog(gralMng.log());
      ZbnfParser parser = new ZbnfParser(console);
      parser.setSyntax(syntaxSettings);
      bOk = parser.parse(in);
      if(!bOk){
        console.writeError(parser.getSyntaxErrorReport());
      } else {
        this.common.timeDatapath = null;
        this.common.timeVariable = null;
        listTracks = new ArrayList<Track>();
        //listTrackSet.clear();
        ZbnfJavaOutput setData = new ZbnfJavaOutput(console);
        setData.setContent(zbnfSetCurve.getClass(), zbnfSetCurve, parser.getFirstParseResult());
        bNewGetVariables = true;  //to force searching variables.
      }
    } catch(Exception exc){
      System.err.println("GralCurveView.writeSettings() - unexpected IOException;" + exc.getMessage());
      bOk = false;
    }
    return bOk;
  }
  
  
  
  @Override public void writeSettings(Appendable out){
    List<Track> listTracks1 = listTracks; //Atomic access, iterate in local referenced list.
    for(Track track: listTracks1){
      if(track.sDataPath !=null){
        try{
          out.append("track ").append(track.name).append(":");
          out.append(" datapath=").append(track.sDataPath);
          out.append(", color=").append(track.lineColor.toString());
          out.append(", scale=").append(Float.toString(track.scale.yScale) );
          out.append(", offset=").append(Float.toString(track.scale.yOffset));
          out.append(", 0-line-percent=").append(Integer.toString(track.scale.y0Line));
          out.append(";\n");
        } catch(IOException exc){
          System.err.println("GralCurveView.writeSettings() - unexpected IOException;" + exc.getMessage());
        }
      }
    }
  }
  
  
  public CharSequence timeInitSaveViewArea(){
    int ixData;
    if(super._wdgImpl ==null) return "--no graphic--";
    _dataOrg.ixDataStartSave = ((GraphicImplAccess)super._wdgImpl)._dataimpl.dataIxToShow[_dataOrg.zPixelDataShown-1];
    int timeShort1 = this._tracksValue.getTimeShort(this._dataOrg.ixDataStartSave);
    _dataOrg.ixDataEndSave = ((GraphicImplAccess)super._wdgImpl)._dataimpl.dataIxToShow[0];
    if(!common.bFreeze){
      //running curve, autosave starts after it.
      _dataOrg.ixDataEndAutoSave = _dataOrg.ixDataEndSave;  //atomic access, the actual write pointer.
      saveOrg.ctValuesAutoSave = 0;
    }
    return buildDate(_dataOrg.ixDataStartSave, _dataOrg.ixDataEndSave);
    //return timeorg.absTime.absTimeshort(timeShort1);
  }
  

  
  
  @Override public CharSequence timeInitAutoSave(){
    _dataOrg.ixDataStartAutoSave = _dataOrg.ixDataEndAutoSave;  //from last end, now start.
    //threadsafe:
    _dataOrg.ixDataEndAutoSave = this._dataOrg.ixDEnd;  //atomic access, the actual write pointer.
    saveOrg.ctValuesAutoSave = 0;  //maybe non-threadsafe, counts only the time for next save.
    return buildDate(_dataOrg.ixDataStartAutoSave, _dataOrg.ixDataEndAutoSave);
    //
    //int timeShort1 = timeValues[(dataOrg.ixDataStartAutoSave >> shIxiData) & mIxiData];
    //return timeorg.absTime.absTimeshort(timeShort1);  //The appropriate start time.
    
  }
  
  
  
  
  private CharSequence buildDate(int ixStart, int ixEnd){
    int timeShortStart = this._tracksValue.getTimeShort(ixStart);
    int timeShortEnd = this._tracksValue.getTimeShort(ixEnd);
    long timeStart = _timeorg.absTime.absTimeshort(timeShortStart);
    long timeEnd = _timeorg.absTime.absTimeshort(timeShortEnd);
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    DateFormat formatEnd = new SimpleDateFormat("_HH-mm-ss");
    String sNameFile = format.format(new Date(timeStart)) + "_to" + formatEnd.format(new Date(timeEnd));
    return sNameFile;
  }
  

  
  
  
  /**Reads a curve ///
   * 
   */
  public void readCurve(File file)
  throws IOException
  {
    InputStream ifile = new FileInputStream(file);
    //read first bytes to detect format
    byte[] buffer = new byte[256];
    int zBytes = ifile.read(buffer);
    ifile.close();
    String firstLine = new String(buffer,0, zBytes);
    readCurveCsvHeadline(file);
    //timeLastSave = System.currentTimeMillis();
    
  }
  

  
  private void readCurveCsvHeadline(File file)
  throws IOException
  {
    String sLine;
    BufferedReader ifile = new BufferedReader(new FileReader(file));
    //first head line
    //setTimePoint(System.currentTimeMillis(), 0, 0.156f);
    setTimePoint(0, 0, 0.050f);
    //line with channels
    sLine = ifile.readLine();
    //List<Track> listTracksNew = new ArrayList<Track>();  ////
    //listTrackSet.clear();
    int ixInList = -1;
    String[] signals = sLine.split(";");                   // analyze first line, signal names
    int[] dataIx = new int[signals.length +10];
    String[] colors = {"bk","rd","gn","bl","or","rd1","rd2","gn1","gn2","bk","rd","gn","bl","or","rd1","rd2","gn1"};
    int ix = -1;
    int ixColor = 0;
    boolean first_timesh = false;
    for(String signal1: signals){                          // signal names from first line
      String signal = signal1.trim();
      if(signal.equals("timesh") && ix == -1) {
        first_timesh = true;
      } else {
        Track track = searchTrack(signal);
        if(track ==null) {                                 // signal found which is yet unknown
          GralColor color = GralColor.getColor(colors[ixColor]);
          track = addTrack(signal, signal, color, 1, 50, 1.0f, 0.0f); //, listTracksNew);
          track.setVisible(0);                             // a gray track, display in select list, but not in graph
          if(ixColor < colors.length-1) {ixColor +=1;}
        }
        dataIx[++ix] = track.ixList; //getDataIx();
      }
    }
    //listTracks = listTracksNew;
    this._dataOrg.ixDEnd = -1; //initial write position, first increment to 0.
    float[] fvalues = new float[listTracks.size() + 10];
    int timeshort = 0;
    while( (sLine = ifile.readLine())!=null){
      timeshort +=1;  //increment per default if no column for timeshort
      String[] values = sLine.split(";");
      int ixCol = -1;
      boolean bFirstCol = true;
      for(String value1: values){
        String sValue = value1.trim();
        sValue = sValue.replace(',', '.');  //accept , as fractional separator, convert to .
        if(first_timesh && bFirstCol) {
          try{ timeshort = Integer.valueOf(sValue); }
          catch(NumberFormatException exc) {
            timeshort +=0;  //use incremented
          }
        } else {
          float value;
          try{ value = Float.valueOf(sValue); }
          catch(NumberFormatException exc){ 
            value = 0.099999999f; 
          }
          int ixData = dataIx[++ixCol];
          fvalues[ixData] = value;
        }
        bFirstCol = false;
      }
      setSample(fvalues, timeshort, 0);
    }
    ifile.close();
  }
  

  
  
  
  /* (non-Javadoc) Writes the curve to the given interface, it is an exporter class.
   * @see org.vishia.gral.ifc.GralCurveView_ifc#writeCurve(org.vishia.curves.WriteCurve_ifc, org.vishia.gral.ifc.GralCurveView_ifc.ModeWrite)
   */
  @Override public void writeCurve(WriteCurve_ifc out, ModeWrite mode){
    //int ctValues = this.nrofValues -1;  //read first, may be increment in next step
    int ixDataStart, ixDataEnd;
    switch(mode){
      case currentView:{
        ixDataEnd = _dataOrg.ixDataEndSave; 
        ixDataStart = _dataOrg.ixDataStartSave;
        /*
        int xRight = 0; //xpCursor2
        //This operation should be done in the graphic thread because ixDataShown is volatile.
        int xLeft = dataOrg.xPixelCurve-1;  //xpCursor1
        if(xLeft >=0 && xLeft < ixDataShown.length){
          ixDataStart = ixDataShown[xLeft];
        } else {
          ixDataStart = ixDataWr - (ctValues << shIxiData);  //read only one time, the index start from.
        }
        if(xRight >=0 && xRight < ixDataShown.length){
          ixDataEnd = ixDataShown[xRight];
        } else {
          ixDataEnd = ixDataWr;   //never used.
        }
        */
      } break;
      case autoSave: {  //NOTE: values are set in timeInitAutoSave()
        ixDataEnd = _dataOrg.ixDataEndAutoSave; 
        ixDataStart = _dataOrg.ixDataStartAutoSave;
      }break;
      default:
        ixDataStart = ixDataEnd = 0;
    }
    //todo in another thread!
    writeCurve(out, ixDataStart, ixDataEnd);
  }
  
  
  
  
  
  
  /**Writes.
   * @param out
   * @param ixDataStart 32-bit-wrapping index
   * @param ixDataEnd
   */
  private void writeCurve(WriteCurve_ifc out, int ixDataStart, int ixDataEnd) 
  //throws IOException
  { boolean bOk = true;
    try{
      out.writeCurveTimestamp(new Timeshort(_timeorg.absTime));
      if(!bOk){
        out.writeCurveError("absolute time error");
      } else {
        int ixTrack = -1;
        List<Track> listTracks1 = listTracks; //Atomic access, iterate in local referenced list.
        int nrofTracks = listTracks1.size();
        for(Track track: listTracks1){
          String sName = track.name;
          String sPath = track.getDataPath();
          GralColor color = track.getLineColor();
          String sColor = color.getColorName();
          out.setTrackInfo(nrofTracks, ++ixTrack, sPath, sName, sColor, track.getScale7div(), track.getOffset(), track.getLinePercent());
        }
        float[] record = new float[listTracks1.size()];
        int ix = ixDataStart;
        int timeshortLast = this._tracksValue.getTimeShort(ix);
        out.writeCurveStart(timeshortLast);
        int ixData = ixDataStart;
        int ctValues = this._dataOrg.nrofValues -1;  //read first, may be increment in next step
        while((ixData != ixDataEnd && --ctValues >=0)){
          ix = ixData;
          ixTrack = -1;
          for(Track track: listTracks1){
            record[++ixTrack] = track.valueTrack.getFloat(ix);
          }
          int timeshort = this._tracksValue.getTimeShort(ix);
          if((timeshort - timeshortLast)<0){
            //This is a older value since the last one,
            //it means it is the first value, all others are overwritten.
            out.writeCurveStart(timeshort);
          }
          out.writeCurveRecord(timeshort, record);
          timeshortLast = timeshort;
          ixData += 1;
          if(ixData >= this._dataOrg.maxNrofXValues) { ixData = 0; }
        }
        out.writeCurveFinish();
      }   
    }catch(IOException exc){
      System.err.println(Assert.exceptionInfo("GralCurveView- exception", exc, 0, 4));
    }
  }
  
  
  /**Forces repaint of the whole curves. It sets the internal flag {@link #bPaintAllCmd} which is checked
   * in the paint event routine. In opposite {@link #setSample(float[], int)} invokes
   * super.repaint of {@link GralWidget#redraw(int, int)} without setting that flag. Therefore the graphic
   * will be shifted to left only with paint of only the new data.
   * @see org.vishia.gral.base.GralWidget#redraw(int, int)
   */
  @Override public void redraw(int delay, int latest){
    System.out.println("GralCurveView.Info - repaint all Trigger;");
    if(_wdgImpl == null) return;
    ((GraphicImplAccess)_wdgImpl).bPaintAllCmd = true;      //used in implementation level to force a paint of the whole curves.
    bNewGetVariables= true;   //used to get faulty variables newly with an error message.
    super.redraw(delay, latest);
  }
  
  
  
  public void cassert(boolean cond) {
    if(!cond) {
      Debugutil.stop();
    }
  }
  




  /**All tracks to return for filling. It has the same members in the same order like {@link #listTracks}*/
  //protected final List<GralSetValue_ifc> listTrackSet = new LinkedList<GralSetValue_ifc>();
  
  
  public abstract static class GraphicImplAccess extends GralWidget.ImplAccess
  implements GralWidgImplAccess_ifc, Removeable {
    
    
    /**All stuff for data in the implementing level.
    *
    */
    protected static class DataOrganisationImpl {
      /**The number of pixel for the current data point from the current pixel to left.
       * [0] is the number of pixel for the right point.
       * This array is parallel to {@link #ixDataShownX} {@link #dataIxToShow}. 
       */
      public final int[] nrofPixel4data = new int[2000];

      /**The index in data for each shown pixel, from right to left on graphic.
       * [0] is right. Use <code>(ixData >> shIxiData) & mIxiData</code> to calculate the real data index.
       * the used length is the number of pixel. 2000 are enough for a large representation.
       * This array is filled newly whenever any draw or paint action is done. It is prepared in the routine
       * {@link #prepareIndicesDataForDrawing(int, int, int)} and used in the drawTrack routine of the implementation level.
       * The field contains old indices if the size of drawing is less then the size of window.
       */
      public final int[] dataIxToShow = new int[2000];

      /**The index to show values, it increments with ixWrValues
       * After this index old data begins, it is the wrapping point. 
       */
      public int ixDshowRight = 0;

    
    }
    
    

    
    
    
    /**The Gral data, _wg for better findability in debug*/
    protected final GralCurveView widgg, _wg;
    
    protected final GralCurveView.DataOrganisation _data;
    
    protected final DataOrganisationImpl _dataimpl = new DataOrganisationImpl();
    
    protected final PixelOrganisation pixelOrg = new PixelOrganisation();

    /**The track which is selected by the last setCursor. */
    protected Track trackSelected;

     
    //protected int ixLineInit = 0;
    
    //protected final float[][] values;
    
    /**last x-positions for paint. 
     * The lastPositionX[0] is used to repaint the last curve peace while shifting draw content.
     * Where, xShift (number of shifted pixel) is considered by subtraction.
     */
    protected final int[] lastPositionX = new int[2];


    /**The cary over of time which is not used for the current point.
     * 
     */
    protected int timeCaryOverNewValue;

    /**Index of the last drawn values. 
     * The index refers to the last drawn value. */
    protected int ixDdraw = 0;

    /**The actual number of values which are not shown because its time difference is too small
     * to show in graphic as new point. It is counted only for debugging.
     */
    protected int nrofValuesLessViewPart;

    /**Pixel from right for the cursor1 and cursor2. If -1 then the cursor is unused yet.
     */
    protected int xpCursor1 = -1;

    /**Pixel from right for the cursor1 and cursor2. If -1 then the cursor is unused yet.
     */
    protected int xpCursor2 = -1;

    /**New Pixel position for a cursor1 and cursor2. If >=0 then a new position is given.
     */
    protected int xpCursor1New = -1;

    /**New Pixel position for a cursor1 and cursor2. If >=0 then a new position is given.
     */
    protected int xpCursor2New = -1;

    /**During mouse move, internal use. */
    protected boolean bMouseDownCursor1;

    /**During mouse move, internal use. */
    protected boolean bMouseDownCursor2;

    protected final static int cmdSetCursor = -2;

    /**Position of cursor in the data. */
    protected int ixDataCursor1;

    /**Position of cursor in the data. */
    protected int ixDataCursor2;

    /**Distance of nrofValues for one vertical grid line (strong or not strong). */
    protected int gridYdstance;

    /**Distance of percent of y-view for one vertical grid line (strong or not strong). */
    protected float gridXdistance;

    /**period of strong lines. */
    protected int gridXstrongPeriod;

    /**period of strong lines. */
    protected int gridYstrongPeriod;

    protected GralColor gridColorGral; // = new Color(getDisplay(), 0, 255, 255);

    protected GralColor gridColorGralStrong; // = new Color(getDisplay(), 0, 255, 255);

    /**Zoom factor. If zoom = 1.0, the curve will be shown in the current canvas area.
     * It the zoom-factor is greater 1.0, not all points will be shown. 
     * The origin point of the values is given with {@link #xOrigin} and {@link #yOrigin}.
     */
    //protected float xZoom = 1.0F, yZoom= 1.0F;
    
    /**Origin point from 0.0 to 1.0 for the zoomed area. */
    //protected float xOrigin = 0.0F, yOrigin = 0.0F;
    
    protected GralColor colorBackGral; // = new Color(getDisplay(), 0xff, 0xff, 0xff);

    protected boolean focusChanged = false;  //it doesn't work

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

    protected boolean bRedrawAll;

    private int ctLastSelected;

    /**Set to true to force a paint all. */
    protected boolean bPaintAllCmd = false;

    protected GraphicImplAccess(GralCurveView outer){ 
      super(outer);
      this.widgg = this._wg = outer;
      this._data = outer._dataOrg;
      this.pixelOrg.xPixelCurve = 0;
      this.pixelOrg.yPixelCurve = 0;

    }

    /**Forces that the next repaint paints the whole graphic.
     * This method should be invoked if the conditions for the graphic are changed, for example
     * changed colors for lines. 
     */
    protected void setPaintAllCmd(){
      System.out.println("GralCurveView.Info - paintallTrigger;");
      bPaintAllCmd = true;      //used in implementation level to force a paint of the whole curves.
      widgg.bNewGetVariables= true;   //used to get faulty variables newly with an error message.
    }

    /**Gets the index in data with given pixel position in the graphic.
     * The wrapping index in the data is contained in {@link #ixDataShownX} {@link #dataIxToShow}.
     * @param ixw pixel position countered from right side.
     * @return
     */
    protected int getDataIxFromPixelRight(int ixPixelFromRight){
      return this._dataimpl.dataIxToShow[ixPixelFromRight];
    }

    /**Determines and switches a curve to select by mouse click.
     * The curve should be at least 10 pixel near the mouse position.
     * <br>
     * Algorithm: From mouse position the index in the data are calculated: {@link #getIxDataFromPixelRightX(int)}.
     * Any track  is checked: The value (float) is read, the scaling is used to calculate the y-Position.
     * Then the distance between mouse and track is calculated.
     *   
     * @param xpos Mouse position on click in pixel
     * @param ypos
     * @param xsize size of the curve view widget.
     * @param ysize
     * @return true if a track is selected. false if the position is more as 10 pixels far of any track.
     */
    protected boolean selectTrack(int xpos, int ypos, int xsize, int ysize){
      int ixData = getDataIxFromPixelRight(xsize - xpos);  //index countered from right to left
      int minFound = 10;           //at least 10 pixel near found curve.
      int maxDiffLastSelected = 0;
      Track foundTrack = null;
      ctLastSelected +=1;
      List<Track> listTracks1 = widgg.listTracks; //Atomic access, iterate in local referenced list.
      for(Track track: listTracks1){  //NOTE: break inside.
        float val = track.valueTrack.getFloat(ixData);
        float yFactor = ysize / -10.0F / track.scale.yScale;  //y-scaling
        float y0Pix = (1.0F - track.scale.y0Line/100.0F) * ysize; //y0-line
        
        int yp = (int)((val - track.scale.yOffset) * yFactor + y0Pix);
        int diff = Math.abs(yp - ypos);
        int diffLastSelected = ctLastSelected - track.identLastSelect;
        if(diff < minFound && diffLastSelected > maxDiffLastSelected){
          foundTrack = track;
          maxDiffLastSelected = diffLastSelected;
          //minFound = diff;
        }
      }
      if(foundTrack !=null){
        foundTrack.identLastSelect = ctLastSelected;
        trackSelected = foundTrack;
        if(widgg.actionSelectTrack !=null){
          widgg.actionSelectTrack.exec(0, widgg, trackSelected);
        }
      }
      return foundTrack !=null;
    }

    protected void setCursors(int xPos){
      //System.out.println("middle");
      int xr = pixelOrg.xPixelCurve - xPos;  //from right
      if(xpCursor1 < 0){  //the right cursor
        xpCursor1New = xr;
        bMouseDownCursor1 = true;
        bMouseDownCursor2 = false;
      } else if(xpCursor2 < 0){
        xpCursor2New = xr;
        bMouseDownCursor2 = true;
        bMouseDownCursor1 = false;
      } else { //decide which cursor
        //use the new changed value if not processed in graphic yet or the current cursor.
        int x1 = xpCursor1New >=0 ? xpCursor1New: xpCursor1;
        int x2 = xpCursor2New >=0 ? xpCursor2New: xpCursor2;
        if(x1 < x2){
          int xp = x1;      //swap
          xpCursor1New = x2;
          xpCursor2New = xp;
          //System.out.printf("GralCurveView - setCursors - swap; xC1=%d; xC2=%d\n", x2, xp);
        }
        int xm = (x1 + x2) /2;
        if(xr > xm){ //more left
          xpCursor1New = xr;
          bMouseDownCursor1 = true;
          bMouseDownCursor2 = false;
          //System.out.printf("GralCurveView - setCursors; cursor1=%d\n", xr);
        } else {
          xpCursor2New = xr;
          bMouseDownCursor2 = true;
          bMouseDownCursor1 = false;
          //System.out.printf("GralCurveView - setCursors; cursor2=%d\n", xr);
        }
      }
      bRedrawAll = true;
      widgg.redraw(0,0);
      //repaint(50,100);
      if(widgg.actionMoveCursor !=null){
        widgg.actionMoveCursor.exec(0, widgg);
      }
    }

    protected void moveCursor(int xMousePixel){
      int xr = pixelOrg.xPixelCurve - xMousePixel;  //from right
      System.out.println("" + xr);
      if(xr <0) {
        Debugutil.stop();
      }
      if(bMouseDownCursor1){
        xpCursor1New = xr;  //from right;
        //System.out.println("SwtCurveView.mouseMove - cursor1; xr=" + xr);
        widgg.redraw(50,100);
      } else if(bMouseDownCursor2){
        xpCursor2New = xr;  //from right;
        //System.out.println("SwtCurveView.mouseMove - cursor2; xr=" + xr);
        widgg.redraw(50,100);
      } else {
        //System.out.println("SwtCurveView.mouseMove x,y=" + e.x + ", " + e.y);
          
      }
      if(widgg.actionMoveCursor !=null){
        widgg.actionMoveCursor.exec(0, widgg);
      }
    }

    /**Zooms the curve presentation with same index right with a lesser time spread. 
     * If the curve presentation is running yet, a finer solution in the present is given. 
     * Note that in this case the right index is the actual write index.*/
    protected void zoomToPresent(){
      if(xpCursor1 >=0){
        this.ixDataCursor1 =this._dataimpl.dataIxToShow[xpCursor1];
      }
      if(xpCursor2 >=0){
        this.ixDataCursor2 =this._dataimpl.dataIxToShow[xpCursor2];
      }
      xpCursor1New = xpCursor2New = cmdSetCursor;  
      if(widgg._timeorg.timeSpread > 100) { widgg._timeorg.timeSpread /=2; }
      else { widgg._timeorg.timeSpread = 100; }
      bPaintAllCmd = true;
      widgg.redraw(100, 200);
    }

    /**Zooms the curve presentation with same index right with a greater time spread. 
     * If the curve presentation is running yet, a broader solution in the present is given. 
     * Note that in this case the right index is the actual write index.*/
    protected void zoomToPast(){
      //zoom out
      int maxTimeSpread = widgg._timeorg.lastShortTimeDateInCurve - widgg._timeorg.firstShortTimeDateInCurve;
      if(xpCursor1 >=0){
        this.ixDataCursor1 = this._dataimpl.dataIxToShow[xpCursor1];
      }
      if(xpCursor2 >=0){
        this.ixDataCursor2 =this._dataimpl.dataIxToShow[xpCursor2];
      }
      xpCursor1New = xpCursor2New = cmdSetCursor;  
      if(widgg._timeorg.timeSpread < 0x3fffffff) { widgg._timeorg.timeSpread *=2; }
      else { widgg._timeorg.timeSpread = 0x7fffffff; }
      bPaintAllCmd = true;
      widgg.redraw(100, 200);
    
    }

    /**Zooms between the given vertical cursors.
     * The time spread is calculated so that the cursors are places on the same data indices at 
     * 1/10 and 9/10 of the presentation range.
     */
    protected void zoomBetweenCursors(){
      if(this.xpCursor1 < this.xpCursor2) {
        int c1 = this.xpCursor1;
        this.xpCursor1 = this.xpCursor2;
        this.xpCursor2 = c1;
      }
      this.ixDataCursor1 =this._dataimpl.dataIxToShow[this.xpCursor1];
      this.ixDataCursor2 =this._dataimpl.dataIxToShow[this.xpCursor2];  // can be y ixDataCursor1 if wrapping between
      int ixDataBetweenCursor = this.ixDataCursor2 - this.ixDataCursor1;
      if(ixDataBetweenCursor < 0) { ixDataBetweenCursor += this.widgg._dataOrg.maxNrofXValues; }
      widgg.cassert(ixDataBetweenCursor >=0 && ixDataBetweenCursor < this.widgg._dataOrg.maxNrofXValues);
      int ixDShow2 = ixDataCursor2 + (ixDataBetweenCursor / 10);
      if(ixDShow2 >= this.widgg._dataOrg.maxNrofXValues) { ixDShow2 -= this.widgg._dataOrg.maxNrofXValues; }
      this._dataimpl.ixDshowRight = ixDShow2;
      widgg.cassert(this._dataimpl.ixDshowRight >=0 && this._dataimpl.ixDshowRight < this.widgg._dataOrg.maxNrofXValues);
      int ixiData1 =this._dataimpl.dataIxToShow[this.xpCursor1];
      int ixiData1d =this._dataimpl.dataIxToShow[this.xpCursor1] -this._dataimpl.dataIxToShow[this.xpCursor1+2];
      int ixiData2 =this._dataimpl.dataIxToShow[this.xpCursor2];
      int time1 = widgg._tracksValue.getTimeShort(ixiData1);
      int time2 = widgg._tracksValue.getTimeShort(ixiData2);
      int timed1Pix = widgg._tracksValue.getTimeShort(ixiData1) - widgg._tracksValue.getTimeShort(ixiData1+1);
      
      assert(time2 >= time1);
      if(time2 == time1) {
        time2 = time1 +1;
      }
      if((time2 - time1)>0){
        widgg._timeorg.timeSpread = this._wg.bDbgNewX ? (time2 - time1) * 10/8 : (time2 - time1) * 10/8;
        widgg.cassert(widgg._timeorg.timeSpread >0);
      } else {
        widgg.stop();
        assert(false);
      }
      xpCursor1New = xpCursor2New = cmdSetCursor;  
      widgg.redraw(100, 200);
    
    }

    /**Unzooms between the given vertical cursors.
     * The cursors will be set newly in graphic at the same data indices.
     * The middle value of both cursors will be at the same position.
     * The time spread is multiplied by the value 5.
     * If the right border of presentation will be in the future, the {@link #ixDataShowRightX}
     * will be greater than {@link #ixDataWrX}, then the presentation will be adjusted to the current
     * right {@link #ixDataWrX} and the cursor positions are changed adequate.
     *
     */
    protected void cursorUnzoom()
    {
      int xpCursorMid = (xpCursor1 + xpCursor2) /2;
      int ixDMid =this._dataimpl.dataIxToShow[xpCursorMid];              //from Pixel value of cursor to data index
      //int timeMid = timeValues[(ixDataMid >> shIxiData) & mIxiData];
      //int timeRight = timeValues[(ixDataShowRight >> shIxiData) & mIxiData];
      //int dtime = timeRight - timeMid;
      //Assert.check(dtime > 0);
      int dixDshowRight = this._dataimpl.ixDshowRight - ixDMid;
      if(dixDshowRight < 0) { dixDshowRight += this.widgg._dataOrg.maxNrofXValues; }
      int ixDshowRightNew = ixDMid + dixDshowRight *5;
      if(ixDshowRightNew >= this.widgg._dataOrg.maxNrofXValues) { ixDshowRightNew -= this.widgg._dataOrg.maxNrofXValues; }
      this._dataimpl.ixDshowRight = ixDshowRightNew;
      //ixDataCursor1 = ixDataShown[xpCursor1];  //from Pixel value of cursor to data index
      //ixDataCursor2 = ixDataShown[xpCursor2];
      //ixDataShowRight = ixDataCursor2 + (((ixDataCursor2 - ixDataCursor1)));
      if(widgg._timeorg.timeSpread < (0x7ffffff5/5)) { widgg._timeorg.timeSpread *=5; }
      else { widgg._timeorg.timeSpread = 0x7fffffff; }
      /*
      int ixiData2 = (ixDataCursor2 >> shIxiData) & mIxiData;
      int time1 = timeValues[(ixDataCursor1 >> shIxiData) & mIxiData];
      int time2 = timeValues[ixiData2];
      timeSpread = (time2 - time1) * 5;
      */
      Assert.check(widgg._timeorg.timeSpread >0);
      xpCursor1New = xpCursor2New = cmdSetCursor;  
      widgg.redraw(100, 200);
    }
    
    
    
    protected void showAll()
    {
     
      this.ixDataCursor1 =this._dataimpl.dataIxToShow[xpCursor1];
      this.ixDataCursor2 =this._dataimpl.dataIxToShow[xpCursor2];  // can be y ixDataCursor1 if wrapping between
//      int ixDataBetweenCursor = this.ixDataCursor2 - this.ixDataCursor1;
//      if(ixDataBetweenCursor < 0) { ixDataBetweenCursor += this.widgg._dataOrg.maxNrofXValues; }
//      widgg.cassert(ixDataBetweenCursor >=0 && ixDataBetweenCursor < this.widgg._dataOrg.maxNrofXValues);
//      int ixDShow2 = ixDataCursor2 + (ixDataBetweenCursor / 10);
//      if(ixDShow2 >= this.widgg._dataOrg.maxNrofXValues) { ixDShow2 -= this.widgg._dataOrg.maxNrofXValues; }
      this._dataimpl.ixDshowRight = this._data.ixDEnd;
      int time2 = widgg._tracksValue.getTimeShort(this._dataimpl.ixDshowRight-1);
      int time1 = widgg._tracksValue.getTimeShort(this._data.bHasWrapped ? this._data.ixDEnd : 0);
      //int timed1Pix = widgg._tracksValue.getTimeShort(ixiData1) - widgg._tracksValue.getTimeShort(ixiData1+1);
      
      assert(time2 > time1);
      if((time2 - time1)>0){
        widgg._timeorg.timeSpread = (time2 - time1);
        widgg.cassert(widgg._timeorg.timeSpread >0);
      } else {
        widgg.stop();
        assert(false);
      }
      xpCursor1New = xpCursor2New = cmdSetCursor;  
      widgg.redraw(100, 200);

      
      
      
      
      
      
      
      
//      for(int ix = 0; ix < this._dataOrg.maxNrofXValues; ++ix){
//        //timeValues[ix] = ix;  //store succession of time values to designate it as empty.  
//      }
//      if(super._wdgImpl !=null) {
//        GraphicImplAccess wdgi = (GraphicImplAccess)super._wdgImpl;
//        //
//        wdgi.ixDdraw = 0; 
//        wdgi.ixDataCursor1 = wdgi.ixDataCursor2 = 0;
//        wdgi._dataimpl.ixDshowRight = this._dataOrg.nrofValues;
//      }
    }
    
    


    /**Shifts the curve presentation to the present (actual values).
     * If the TODO
     */
    protected void viewToPresentOrGoIrRefresh()
    {
      if(widgg.common.bFreeze){
        //assume that the same time is used for actual shown data spread as need
        //for the future.
        
        //int timeRight = timeValues[(ixDataShowRight >> shIxiData) & mIxiData];
        //int timeRightNew = timeRight + timeorg.timeSpread * 7/8;
        
        //
        //This measures the ixData spread regarded to the beginning of data. Only valid if the data has an equal distance.
        //TODO better algorithm: search from right to same time distance.
        int ixdDataSpread =this._dataimpl.ixDshowRight -this._dataimpl.dataIxToShow[pixelOrg.xPixelCurve * 5/8];
        if((this._dataimpl.ixDshowRight - this._data.ixDEnd)<0 && (this._dataimpl.ixDshowRight - this._data.ixDEnd + ixdDataSpread) >=0){
          //right end reached.
          this._dataimpl.ixDshowRight = this._data.ixDEnd;
          widgg.common.bFreeze = false;
        } else {
          this._dataimpl.ixDshowRight += ixdDataSpread;
        }
        //ixDataShowRight += ixdDataSpread;
        //if((ixDataShowRight - ixDataWr) > 0 && (ixDataShowRight - ixDataWr) < ixdDataSpread * 2) {
          //right end reached.
          //ixDataShowRight = ixDataWr;
          //common.bFreeze = false;
          //ixDataShowRight1 = ixDataWr + ixdDataSpread;
        //}
        //ixDataShowRight += ixDataShown[0] - ixDataShown[nrofValuesShow-1]; 
        widgg.redraw(100, 200);
    
      } else {
        setPaintAllCmd();  //refresh
      }
      //System.out.println("right-bottom");
    }

    /**Shifts the curve presentation to the past. If the presentation is running, it is stopped firstly.
     * If it is stopped, the data index on 20% of current presentation will be adjusted to the right border.
     * It means 20% are presented overlapping. The time spread is not changed.
     */
    protected void stopAndViewToPast()
    {
      if(!widgg.common.bFreeze){ 
        widgg.common.bFreeze = true;
        //now ixDataShow remain unchanged.
      } else {
        int ixDataSpread = this._dataimpl.ixDshowRight - this._dataimpl.dataIxToShow[pixelOrg.xPixelCurve * 5/10];
        if(ixDataSpread <0) { 
          if(this._data.bHasWrapped) { ixDataSpread += this.widgg._dataOrg.maxNrofXValues; } // wraps
          else { ixDataSpread = 0; } //TODO test
        }
        //int ixDataShowRight1 = ixDataShown[nrofValuesShow * 7/8];
        //int ixdDataSpread = ixDataShowRight - ixDataShowRight1;
        this._dataimpl.ixDshowRight -= ixDataSpread;  //shifts the index of right curve to the left in data.
        if((this._dataimpl.ixDshowRight - this._data.ixDEnd) < 0 && (this._data.ixDEnd - this._dataimpl.ixDshowRight) < ixDataSpread) {
          //left end reached.
          this._dataimpl.ixDshowRight = (this._data.bHasWrapped ? this._data.ixDEnd :0) + ixDataSpread;  // remain left from begin.
        }
      }
      widgg.redraw(100, 200);
      //System.out.println("left-bottom");
    
    }

    protected void mouseSelectCursur(int xMousePixel, int yMousePixel){
      
    }

    /**prepares indices of data.
     * All data have a timestamp. the xpixel-values are calculated from the timestamp.
     * <ul><li>It fills {@link #ixDataShownX} {@link #dataIxToShow}  array with the indices to the data for each x pixel from right to left.
     * If there are more as one data point for one pixel, the step width between each [...] is >1,
     * If there are more as one x-pixel for one data record, the same data index is written more as one 
     * <li>
     * It uses the {@link GralCurveView#_tracksValue} and their {@link TimedValues#timeShort} to get the timestamp of the time 
     * values starting from param ixDataRight
     * for the right pixel of view. The ixDataRight will be decremented. It is a wrapping index with
     * step width of {@link #adIxData}. If the timestamp of any next index is greater than the last one,
     * it is a indication that any newer data are reached after wrapping. Then the preparation stops.
     * <br><br>
     * It fills {@link #ixDataShownX}  {@link #dataIxToShow} with indices to data per x pixel point.
     * If there are the same data for more as one pixel (zoomed near), then {@link #ixDataShownX}  {@link #dataIxToShow} will contain
     * the same index. The presentation can decide whether it should be shown with the same level (stepwise)
     * or with any linear approximation
     * 
     * @param ixDataRightX Index in data array for the right point. 
     *   It is a index which wraps around full integer range, see {@link #adIxData}.
     * @param ixData2 the index in the data array for the right point on curve.  
     * @param xViewPart width of the spread to prepare in pixel. The number of pixel from new prepare point to right.
     * @return nrof pixel to draw. It is xViewPart if enough data are available, elsewhere less. 
     */
    protected int prepareIndicesDataForDrawing(int ixD2, int xViewPart, int timePart, boolean bPaintAll){
      System.arraycopy(this._dataimpl.dataIxToShow, 0, this._dataimpl.dataIxToShow, xViewPart, this._dataimpl.dataIxToShow.length - xViewPart);  // shift the data to left, 0 = right position, 
      if(bPaintAll){                                                                             // xViewPart = start of new part from right to end right (=0)
        widgg._dataOrg.zPixelDataShown = 0;
      } else if(widgg._dataOrg.zPixelDataShown + xViewPart >= pixelOrg.xPixelCurve){ 
        widgg._dataOrg.zPixelDataShown = pixelOrg.xPixelCurve;  // the whole curve fills.
      } else {
        widgg._dataOrg.zPixelDataShown += xViewPart;
      }
      //
      //    time,   time2;   ......timeRight     the time stamps in data
      //    ixData, ixData2, ..... ixDataRight   the 32-bit-index to data
      //    ixD                              the really index to data
      //
      this._dataimpl.dataIxToShow[0] = ixD2;
      //System.out.println("GralCurveView - prepareIndices; timePart=" + timePart + "; timexPart=" + xViewPart * timePerPixel + "; xPart=" + xViewPart);
      int ixData = ixD2;
      int ixData2= ixD2;
      int ixD = ixData;
      int ixd1pix = this._dataimpl.dataIxToShow[this.xpCursor1+1] - this._dataimpl.dataIxToShow[this.xpCursor1+2]; //~ ixData diff 1 pixel
      int ixp2 = 0;
      int ixp = 0;                                         // pixel from right to left
      int nrofPixel4Data =0;
      final int timeRight = widgg._tracksValue.getTimeShort(ixD); //timestamp of the right value.
      //
      if(xViewPart > 100){
        widgg._timeorg.timeLeftShowing = timeRight - (int)((xViewPart +1) * widgg._timeorg.timePerPixel);
      }
      //calculate absolute time from shorttime:
      long millisecAbs = widgg._timeorg.absTime.absTimeshort(timeRight);
      int milliSec2Div = (int)(millisecAbs % widgg._timeorg.millisecPerDiv);                                 //how many millisec to the next division
      int milliSec2FineDiv = milliSec2Div % ( widgg._timeorg.millisecPerFineDiv);
      float pixel2FineDiv = milliSec2FineDiv * widgg._timeorg.pixel7time / widgg._timeorg.absTime.millisec7short(); //how many pixel to the next fine division line
      float pixel2Div = milliSec2Div * widgg._timeorg.pixel7time / widgg._timeorg.absTime.millisec7short();         //how many pixel to the next division line 
      int ixPixelTimeDiv =-1;
      int ixPixelTimeDivFine =-1;  //sets widgg.timeorg.sTimeAbsDiv[...], widgg.timeorg.xPixelTimeDivFine[...] TODO clean arrays before, better for debugging or set to -1 for stop point, see affter while-loop
      //===================================================== calculate the grid
      while(pixel2FineDiv < xViewPart ){ //&& nrofPixel4Data >=0){
        if(Math.abs(pixel2Div - pixel2FineDiv) < 3){
          //strong division
          int xPixel = (int)(pixel2Div + 1.5); //  (xTimeDiv * widgg.timeorg.pixel7time + 0.5f) ;
          widgg._timeorg.xPixelTimeDiv[++ixPixelTimeDiv] = xPixel;
          widgg._timeorg.timeAbsOnLastStrongDiv = millisecAbs - milliSec2Div; //(long)(timeRight - xTimeDiv - widgg.timeorg.absTime_short + widgg.timeorg.absTime);
          if( widgg._timeorg.millisecPerFineDiv <= 100 ){ //less 1 sec for strong division:
            float millisec = (( widgg._timeorg.timeAbsOnLastStrongDiv) % 60000) / 1000.0f;
            widgg._timeorg.sTimeAbsDiv[ixPixelTimeDiv] = String.format("% 2.3f", millisec);
            
          } else if( widgg._timeorg.millisecPerFineDiv < 1000){  //less 10 sec for strong division, but more 1 sec
            long seconds = (( widgg._timeorg.timeAbsOnLastStrongDiv / 1000) % 60);
            //System.out.println("time " + milliSec2Div + ", "  + timeRight + ", " + widgg.timeorg.timeAbsOnLastStrongDiv);
            widgg._timeorg.sTimeAbsDiv[ixPixelTimeDiv] = "" + seconds;
          } else if( widgg._timeorg.millisecPerFineDiv < 10000){  //less 10 sec for strong division, but more 1 sec
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            String sTime = format.format(new Date( widgg._timeorg.timeAbsOnLastStrongDiv));
            widgg._timeorg.sTimeAbsDiv[ixPixelTimeDiv] = sTime;
          } else {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            String sTime = format.format(new Date( widgg._timeorg.timeAbsOnLastStrongDiv));
            widgg._timeorg.sTimeAbsDiv[ixPixelTimeDiv] = sTime;
          }
          widgg._timeorg.pixelWrittenAfterStrongDiv = xPixel;
          pixel2Div += widgg._timeorg.pixelPerTimeDiv;
          milliSec2Div += widgg._timeorg.millisecPerDiv;
        } else {
          int xPixel =  (int)(pixel2FineDiv + 1.5f); //(int)(xTimeDivFine * widgg.timeorg.pixel7time + 0.5f) ;
          widgg._timeorg.xPixelTimeDivFine[++ixPixelTimeDivFine] = xPixel;
          //System.out.println("" + xPixel);
        }
        pixel2FineDiv += widgg._timeorg.pixelPerTimeFineDiv;
        //xTimeDivFine += widgg.timeorg.shortTimePerDiv;
      }
      widgg._timeorg.timeLeftShowing = timeRight;  //for next call.
      //
      widgg._timeorg.xPixelTimeDiv[++ixPixelTimeDiv] = -1; //stopPoint;
      widgg._timeorg.xPixelTimeDivFine[++ixPixelTimeDivFine] = -1; //stopPoint;
      int time2 = timeRight;  //start time
      int time = timeRight;
      int dtime2 = 0;
      //int dtime = 0;
      //xViewPart = nrof pixel from right
      //ixp1 counts from 0... right to left
      //int nrofValues1 = nrofValues;
      //===================================================== Fills ixDataShown with the index to the data for each pixel.
      while(ixp < xViewPart                                // ixp is pixel count from right = 0 to left = xViewPart = nrofXpixel of curve
           && dtime2 <=0 
           && nrofPixel4Data >=0  //130328
           ){ // && ixp1 >= ixp2){ //singularly: ixp1 < ixp2 if a faulty timestamp is found.
        do{ //----------------------------------------------- all values per 1 pixel
          if( (--ixData) <0) { 
            if(this._data.bHasWrapped) { 
              ixData += this.widgg._dataOrg.maxNrofXValues; 
            } else { 
              ixData = 0;
              //wrapped but data are not wrapped:
              dtime2 = 1;  //same like time crack
            } 
          } else {
            ixD = ixData;
            time = widgg._tracksValue.getTimeShort(ixD);   //timestamp of that data point 
            dtime2 = time - time2;    //difference time from the last one. It is negative.
            //dtime = time9 - time; //offset to first right point
            if((dtime2) <0){  //from rigth to left, dtime2 <0 is expected
              int dtime0 = timeRight - time; //time difference to the right pointt
              int ixp3 = (int)(dtime0 * widgg._timeorg.pixel7time + 0.5f);  //calculate pixel offset from right, right =0 
              if(ixp3 > xViewPart){   //next time stamp is in the past of requested view
                ixp3 = xViewPart;     //no more than requested nr of points. 
              }
              nrofPixel4Data += (ixp3 - ixp);
              if(ixp3 < ixp)
                Debugutil.stop();
              ixp = ixp3; 
              if(xpCursor1New == cmdSetCursor && ixData == ixDataCursor1){
                xpCursor1New = ixp;                  //set cursor xp if the data index is gotten.
              }
              if(xpCursor2New == cmdSetCursor && ixData == ixDataCursor2){
                xpCursor2New = ixp;
              }
            }
          }
        } while( ixp == ixp2   //all values for the same 1 pixel
              && dtime2 <0     //stop at time crack to newer values.
            //&& nrofValues >0
              //&& ixData != ixDataRight  //no more as one wrap around, if dtime == 0 or is less
               );
        //--------------------------------------------------- fill 1 or more pixel postion in dataIxToShow, 
        while(ixp2 < ixp) {                                // More if more pixel for one data point, ixp2 is counter
          this._dataimpl.dataIxToShow[ixp2] = ixData;                //same index to more as one point.
          this._dataimpl.nrofPixel4data[ixp2] = --nrofPixel4Data;
          ixp2 +=1;
        }
        if(widgg._dataOrg.zPixelDataShown < ixp2){
          widgg._dataOrg.zPixelDataShown = ixp2;    //max. value of shown data.
        }
        ixData2 = ixData;
      } 
      if(ixp < xViewPart){
        //System.out.println("GralCurveView large xViewPart");
      }
      //ixDataShown[++ixp2] = -1;      //the last
      return ixp;
    }
    
  } 
 
 
 
 
 
 
 
  protected class GralCurveViewMouseAction implements GralMouseWidgetAction_ifc {

    @Override public void mouse1Down(int key, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg)
    { 
      if(xMousePixel < xWidgetSizePixel / 20 && yMousePixel > yWidgetSizePixel * 95/100){ 
        //left side bottom
        System.out.println("GralCurveView-Mousedown left bottom");
        ((GraphicImplAccess)_wdgImpl).stopAndViewToPast(); 
      } else if(xMousePixel > xWidgetSizePixel * 95 / 100 && yMousePixel > yWidgetSizePixel * 95/100){ 
        //right side bottom
        //System.out.println("GralCurveView-Mousedown rigth bottom");
        ((GraphicImplAccess)_wdgImpl).viewToPresentOrGoIrRefresh(); 
      } else if(xMousePixel < xWidgetSizePixel / 20 && yMousePixel < yWidgetSizePixel / 20){ 
        //left side top
        System.out.println("GralCurveView-Mousedown left top");
        ((GraphicImplAccess)_wdgImpl).zoomToPast(); 
      } else if(xMousePixel > xWidgetSizePixel * 95 / 100 && yMousePixel < yWidgetSizePixel / 20){ 
        //right side top
        //System.out.println("GralCurveView-Mousedown rigth top");
        ((GraphicImplAccess)_wdgImpl).zoomToPresent(); 
      } else {
        if(!((GraphicImplAccess)_wdgImpl).selectTrack(xMousePixel, yMousePixel, xWidgetSizePixel, yWidgetSizePixel)){
          ((GraphicImplAccess)_wdgImpl).setCursors(xMousePixel);
        }
        //if((key & KeyCode.mAddKeys) ==KeyCode.ctrl){
        //}
      }
    }

    @Override public void mouse1Up(int key, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg)
    {
      ((GraphicImplAccess)_wdgImpl).bMouseDownCursor1 = ((GraphicImplAccess)_wdgImpl).bMouseDownCursor2 = false;
      //System.out.println("GralCurveView - MouseUp; ");
    }

    @Override
    public void mouse2Down(int key, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel,
        GralWidget widgg)
    {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void mouse2Up(int key, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel,
        GralWidget widgg)
    {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void mouse1Double(int keyCode, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg)
    {
    }

    @Override public boolean mouseMoved(int xMousePixel, int yMousePixel, int xWidgetPixelSize, int yWidgetPixelSize)
    {
      ((GraphicImplAccess)_wdgImpl).moveCursor(xMousePixel);
      return true;
    }

    
  }
  
  public GralUserAction actionZoomBetweenCursors = new GralUserAction("actionZoomBetweenCursors"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        ((GraphicImplAccess)_wdgImpl).zoomBetweenCursors();
      }
      return true;
    }
  };

  public GralUserAction actionZoomOut = new GralUserAction("actionZoomOut"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        ((GraphicImplAccess)_wdgImpl).cursorUnzoom();
      }
      return true;
    }
  };


  public GralUserAction actionCleanBuffer = new GralUserAction("actionCleanBuffer"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        cleanBuffer();
      }
      return true;
    }
  };

  public GralUserAction actionShowAll = new GralUserAction("actionShowAll"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        ((GraphicImplAccess)_wdgImpl).showAll();
      }
      return true;
    }
  };


  public GralUserAction actionGo = new GralUserAction("actionGo"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        ((GraphicImplAccess)_wdgImpl)._dataimpl.ixDshowRight = GralCurveView.this._dataOrg.ixDEnd;
        common.bFreeze = false;
      }
      return true;
    }
  };

  public GralUserAction actionPaintAll = new GralUserAction("actionPaintAll"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        ((GraphicImplAccess)_wdgImpl).setPaintAllCmd();
      }
      return true;
    }
  };

}
