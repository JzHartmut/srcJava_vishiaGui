package org.vishia.gral.cfg;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.ifc.GuiPanelMngBuildIfc;
import org.vishia.gral.ifc.WidgetCfg_ifc;


/**This class contains all configuration data for the appearance of the GUI.
 * It can be filled by {@link org.vishia.gral.cfg.GuiCfgZbnf} or others.
 * It is changed by user handling on GUI.
 * It can be written out in a new configuration file
 * 
 * @author Hartmut Schorrig
 *
 */
public final class GuiCfgData
{
  
  /**ZBNF: DataReplace::= <$?key> = <$-/\.?string> ;
   * Class for temporary instance to capture key and string. */
  public final static class DataReplace
  { public String key; public String string; 
  }//class DataReplace
  
  
  /**ZBNF: Element::= ... ;
   * Class for instance to capture and store one element. */
  public final static class GuiCfgElement implements Cloneable, WidgetCfg_ifc
  { 
    /**The previous element is necessary because non complete coordinates are resolved with previous.
     * The next element is need to build a queue in order of the text. */
    GuiCfgElement previous, next;
    
    final GuiCfgData itsCfgData;
    
    /**The content is set in textual form too. It is because [<?Element>...] was written */
    private String content;
    
    /**The position is set in textual form too. It is because [<?Element>...] was written */
    private String positionString;
    
    /**ZBNF: Position coordinates will be filled from [<?position>.... 
     * The instance contains only that position data, which are found in the textual config file. 
     * It is important to rewrite only that data, which are contained in the originally one. */
    GuiCfgPosition positionInput = new GuiCfgPosition();
    
    /**This obj contains all position data. Missed position data in the {@link #positionInput}
     * are completed by knowledge of the position of the previous elements. */
    GuiCfgPosition position = new GuiCfgPosition();
    
    WidgetTypeBase widgetType;
    
    GuiCfgElement(GuiCfgData itsCfgData)
    { this.itsCfgData = itsCfgData;
    }
    
    public GuiCfgElement clone(){ 
      GuiCfgElement newObj = null;
      try{ newObj = (GuiCfgElement)super.clone(); 
        newObj.position = new GuiCfgPosition(); //use a new empty instance in cloned object, empty data.
        newObj.positionInput = positionInput.clone(); ///use a new cloned instance (use data).
        newObj.widgetType = widgetType.clone(); ///use a new cloned instance (use data).
      } catch(CloneNotSupportedException exc){ assert(false); }
      newObj.previous = this;  //link it in queue after this.
      newObj.next = next;
      next = newObj;
      return newObj; 
    }
    
    /**ZBNF: <?position> */
    public GuiCfgPosition new_position(){ return positionInput; }  
    
    /**ZBNF: <?position> */
    public void set_position(String val) { positionString = val; }
   
    /**ZBNF: <?position> */
    public void set_position(GuiCfgPosition val) {  } //is set only
   
    /**ZBNF: Text::= */
    public GuiCfgText new_Text()
    { GuiCfgText widgetType1 = new GuiCfgText(this); 
      this.widgetType = widgetType1; 
      return widgetType1;
    }
    
    /**ZBNF: Text::= */
    public void set_Text(GuiCfgText data){  }
    
    public WidgetTypeBase new_InputFile()
    { GuiCfgInputFile widgt = new GuiCfgInputFile(this);
      this.widgetType = widgt;
      return widgt;
    }
    
    public void set_InputFile(WidgetTypeBase data) {  }
    
    public WidgetTypeBase new_Button()
    { GuiCfgButton widgt = new GuiCfgButton(this);
      this.widgetType = widgt;
      return widgt;
    }
    
    public void set_Button(WidgetTypeBase data) {  }
    
    /**ZBNF: Led::= */
    public GuiCfgLed new_Led()
    { GuiCfgLed widgetType1 = new GuiCfgLed(this); 
      this.widgetType = widgetType1; 
      return widgetType1;
    }
    
    /**ZBNF: Text::= */
    public void set_Led(GuiCfgLed data){  }
    
    /**ZBNF: Line::= */
    public GuiCfgLine new_Line()
    { GuiCfgLine widgetType1 = new GuiCfgLine(this); 
      this.widgetType = widgetType1; 
      return widgetType1;
    }
    
    /**ZBNF: Line::= */
    public void set_Line(GuiCfgLine data){  }
    
    /**ZBNF: Imagefile::= */
    public GuiCfgImage new_Imagefile()
    { GuiCfgImage widgetType1 = new GuiCfgImage(this); 
      this.widgetType = widgetType1; 
      return widgetType1;
    }
    
    /**ZBNF: ShowField::= */
    public void set_Imagefile(GuiCfgImage data){  }
    
    /**ZBNF: ShowField::= */
    public GuiCfgShowField new_ShowField()
    { GuiCfgShowField widgetType1 = new GuiCfgShowField(this); 
      this.widgetType = widgetType1; 
      return widgetType1;
    }
    
    /**ZBNF: ShowField::= */
    public void set_ShowField(GuiCfgShowField data){  }
    
    /**ZBNF: Table::= */
    public GuiCfgTable new_Table()
    { GuiCfgTable widgetType1 = new GuiCfgTable(this); 
      this.widgetType = widgetType1; 
      return widgetType1;
    }
    
    /**ZBNF: Table::= */
    public void set_Table(GuiCfgTable data){  }
    
    /**ZBNF: Curveview::= */
    public GuiCfgCurveview new_Curveview()
    { GuiCfgCurveview widgetType1 = new GuiCfgCurveview(this); 
      this.widgetType = widgetType1; 
      return widgetType1;
    }
    
    /**ZBNF: Curveview::= */
    public void set_Curveview(GuiCfgCurveview data){  }
    
  }//class GuiCfgElement
  
  
  /**ZBNF: position::= ... ;
   * Class for instance to capture and store the position in an element. */
  public final static class GuiCfgPosition implements Cloneable
  {
    public String panel;
    public boolean yPosRelative;
    public int yPos = -1, yPosFrac;
    public int ySizeDown, ySizeFrac;
    boolean yIncr_;
    
    public boolean xPosRelative;
    public int xPos = -1, xPosFrac;
    public int xWidth, xSizeFrac;
    boolean xIncr_ = true;
    
    public void set_xIncr(){ xIncr_ = true; yIncr_ = false; }
    public void set_yIncr(){ yIncr_ = true; xIncr_ = false; }
    public void set_xOwnSize(){ xWidth = Integer.MAX_VALUE; }
    public void set_yOwnSize(){ ySizeDown = Integer.MAX_VALUE; }
    
    protected GuiCfgPosition clone()
    { GuiCfgPosition clone = null;
      try{ clone = (GuiCfgPosition)super.clone(); } 
      catch(CloneNotSupportedException exc){ assert(false); }
      return clone;
    }
    
    /**Sets a position element. It is able to call from a configuration input or gui input.
     * @param what use y, x, h, w for pos-y, pos-x, height, wight. All other chars causes an IllegalArgumentException.
     * @param sVal String given Value in ZBNF-syntax-form ::=[< ?posRelative> &+]< #?val>[ \. <#?frac> ]. Fault inputs causes return false.
     *        It should not have leeding or trailing spaces! Trim outside. 
     *        It is admissible that the string is empty, then no action is done.
     * @return true on success. False if the sVal contains numberFormat errors. True on empty sVal
     */
    public boolean setPosElement(char what, String sVal)
    { boolean ok = true;
      final int val; final int frac;
      if(sVal.length() >=0){
        boolean posRelativ = sVal.charAt(0)=='&';
        int pos1 = posRelativ ? 1: 0;
        if(sVal.charAt(pos1) == '+'){
          pos1 +=1;   //skip over a '+', it disturbs Integer.parseInt
        }
        int posPoint = sVal.indexOf('.');
        try{
          if(posPoint >=0){
            val = Integer.parseInt(sVal.substring(pos1, posPoint));   
            frac = Integer.parseInt(sVal.substring(posPoint +1));   
          } else {
            val = Integer.parseInt(sVal.substring(pos1));
            frac = 0;
          }
          switch(what){
            case 'y': yPos = val; yPosFrac = frac; yPosRelative = posRelativ; break;
            case 'x': xPos = val; xPosFrac = frac; xPosRelative = posRelativ; break;
            case 'h': ySizeDown = val; ySizeFrac = frac; break;
            case 'w': xWidth = val; xSizeFrac = frac; break;
          }
        } catch(NumberFormatException exc){ ok = false; }
      }
      return ok;
    }
    
  }//class Position
  
  
  /**This is the base class of all widget types for the GUI. It contains common elements.
   */
  public static class WidgetTypeBase implements Cloneable
  {
    private final GuiCfgElement itsElement;
    
    public String name, text;
    public String cmd, userAction, info, showMethod, format, type;
    
    public GuiCfgColor colorName = null, color0 = null, color1 = null;
    

    public WidgetTypeBase(GuiCfgElement itsElement){ this.itsElement = itsElement; }
    
    public GuiCfgColor new_colorName(){ return colorName = new GuiCfgColor(); }
    
    public void set_colorName(GuiCfgColor value){}
    
    public GuiCfgColor new_color0(){ return color0 = new GuiCfgColor(); }
    
    public void set_color0(GuiCfgColor value){}
    
    public GuiCfgColor new_color1(){ return color1 = new GuiCfgColor(); }
    
    public void set_color1(GuiCfgColor value){}

    protected WidgetTypeBase clone()
    { WidgetTypeBase clone = null;
      try{ clone = (WidgetTypeBase)super.clone(); } 
      catch(CloneNotSupportedException exc){ assert(false); }
      return clone;
    }

  }//class WidgetTypeBase
  
  
  /**ZBNF: Text::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgText extends WidgetTypeBase implements Cloneable
  {
    public String size = "B";
    public GuiCfgText(GuiCfgElement itsElement){ super(itsElement); }
  }
  
  
  /**ZBNF: Text::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgLed extends WidgetTypeBase implements Cloneable
  {
    //public String size = "B";
    public GuiCfgLed(GuiCfgElement itsElement){ super(itsElement); }
  }
  
  
  
  /**ZBNF: Line::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgLine extends WidgetTypeBase implements Cloneable
  {
    List<GuiCfgCoord> coords = new LinkedList<GuiCfgCoord>();
    
    public GuiCfgLine(GuiCfgElement itsElement){ super(itsElement); }
    
    public void set_coord(String value){}
    
    public GuiCfgCoord new_coord(){ return new GuiCfgCoord(); }
    
    public void add_coord(GuiCfgCoord value){ coords.add(value); }
  }
  
  
  /**ZBNF: |<?coord> <#f?x>, <#f?y>
   */
  public final static class GuiCfgCoord
  {
    public float x,y;
  }
  

  /**ZBNF: Text::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgImage extends WidgetTypeBase implements Cloneable
  {
    public String size = "B";
    
    String file_;
    
    public GuiCfgImage(GuiCfgElement itsElement){ super(itsElement); }
  
    public void set_file(String value){ file_ = value; }
  }
  
  
  
  /**ZBNF: ShowField::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgShowField extends WidgetTypeBase implements Cloneable
  {
    
    public GuiCfgShowField(GuiCfgElement itsElement){ super(itsElement); }
  }
  
  
  /**ZBNF: ShowField::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgInputFile extends WidgetTypeBase implements Cloneable
  {
    
    public GuiCfgInputFile(GuiCfgElement itsElement){ super(itsElement); }
  }
  
  
  /**ZBNF: Button::= ... ;
   * Class for instance to capture and store the Button data. */
  public final static class GuiCfgButton extends WidgetTypeBase implements Cloneable
  {
    
    public GuiCfgButton(GuiCfgElement itsElement){ super(itsElement); }
  }
  
  
  
  /**ZBNF: Table::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgTable extends WidgetTypeBase implements Cloneable
  {
    
    public GuiCfgTable(GuiCfgElement itsElement){ super(itsElement); }

    public int height;
    
    private final List<Integer> columnWidths = new LinkedList<Integer>();
    
    public void set_columnWidth(int val){ columnWidths.add(val); }
    
    public void set_text(String value){ super.text = value; }
  }
  
  
  /**ZBNF: Table::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgCurveview extends WidgetTypeBase implements Cloneable
  {
    public int nrofPoints;
    
    List<GuiCfgCurveLine> lines = new LinkedList<GuiCfgCurveLine>();
    
    /**The element is created if the text if {@link #set_line(String)} is invoked 
     * before {@link #newGuiElement} is invoked. The association is temporary valid for the current element. */
    private GuiCfgCurveLine newLine;
    
    
    public GuiCfgCurveview(GuiCfgElement itsElement){ super(itsElement); }

    
    /**ZBNF: DataReplace: CurveView::= .... <?line> */
    public GuiCfgCurveLine new_line()
    { 
      if(newLine == null){ newLine = new GuiCfgCurveLine(null); }
      return newLine; 
    }  
    
    /**ZBNF: DataReplace: < Element> */
    public void set_Element(String val)
    { 
      if(newLine == null){ newLine = new GuiCfgCurveLine(null); }
      newLine.content = val;
      //NOTE: the newGuiElement will be returned to fill in in new_Element()
    }
    
    
    
    /**From ZBNF: DataReplace: < DataReplace> */
    public void add_line(GuiCfgCurveLine value)
    { lines.add(value);
      newLine = null;
    } 
    
    
  }
  

  /**ZBNF: {<?line> ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgCurveLine extends WidgetTypeBase implements Cloneable
  {
    String content;
    
    public GuiCfgCurveLine(GuiCfgElement itsElement){ super(itsElement); }
    
    public String name;
    public int colorValue = -1;
    public float offset, scale;
    public int nullLine;
  }
  
  
  public final static class GuiCfgColor
  {
    public String color;
  }
  
  
  final static class GuiCfgPanel
  {
    final String name;
    final List<GuiCfgElement> listElements = new LinkedList<GuiCfgElement>();
    GuiCfgPanel(String name){ this.name = name; }
  }
  
  
  GuiCfgElement firstElement = null;
  
  private GuiCfgElement actualElement = null;
  
  /**ZBNF: DataReplace::= <$?key> = <$-/\.?string> ;
   * Temporary instance to capture key and string. */
  private final DataReplace dataReplaceTempInstance = new DataReplace();
  
  
  /**The element is created if the text if {@link #set_Element(String)} is invoked 
   * before {@link #newGuiElement} is invoked. The association is temporary valid for the current element. */
  private GuiCfgElement newGuiElement;
  
  /**Map of replacements of paths to data. Filled from ZBNF: DataReplace::= <$?key> = <$-/\.?string> */
  public final Map<String, String> dataReplace = new TreeMap<String,String>();

  
  
  
  /**TODO widgets sorted to panels and tabs!
   * 
   */
  private final List<GuiCfgElement> listElementsInTextfileOrder = new LinkedList<GuiCfgElement>();
  
  GuiCfgPanel actPanel;
  
  /**Map of replacements of paths to data. Filled from ZBNF: DataReplace::= <$?key> = <$-/\.?string> */
  public final Map<String, GuiCfgPanel> idxPanels = new TreeMap<String,GuiCfgPanel>();

  
  public GuiCfgData()
  {
    
  }
  
  
  /**ZBNF: size( <#?ySize> , <#?xSize> ) */
  public void set_ySize(int value)
  {
    
  }
  
  /**ZBNF: size( <#?ySize> , <#?xSize> ) */
  public void set_xSize(int value)
  {
    
  }
  
  
  /**From ZBNF: DataReplace: < DataReplace> */
  public DataReplace new_DataReplace(){ return dataReplaceTempInstance; }
  
  /**From ZBNF: DataReplace: < DataReplace> */
  public void set_DataReplace(DataReplace value)
  { dataReplace.put(value.key, value.string);
  } 
  
  
  /**ZBNF: DataReplace: < Element> */
  public GuiCfgElement new_Element()
  { 
    if(newGuiElement == null){ newGuiElement = new GuiCfgElement(this); }
    if(firstElement ==null){
      firstElement = newGuiElement;
    }
    if(actualElement !=null){
      actualElement.next = newGuiElement;
    }
    newGuiElement.previous = actualElement;  //may be null
    actualElement = newGuiElement;
    return newGuiElement; 
  }  
  
  /**ZBNF: DataReplace: < Element> */
  public void set_Element(String val)
  { 
    if(newGuiElement == null){ newGuiElement = new GuiCfgElement(this); }
    newGuiElement.content = val;
    //NOTE: the newGuiElement will be returned to fill in in new_Element()
  }
  
  
  
  /**From ZBNF: DataReplace: < DataReplace> */
  public void set_Element(GuiCfgElement value)
  { String sPanel = value.positionInput.panel;
    if(value.widgetType != null && value.widgetType.text !=null && value.widgetType.text.equals("wd:yCos"))
      stop();
    if(sPanel == null){ //the last panel is used furthermore.
      if(actPanel == null){ 
        actPanel = new GuiCfgPanel("$");
      }
      sPanel = actPanel.name;
      value.position.panel = sPanel;
    } else { //a panel is given.
      actPanel = idxPanels.get(sPanel); 
      if(actPanel == null){ //first time use that:
        actPanel = new GuiCfgPanel(sPanel);
        idxPanels.put(sPanel, actPanel);
      }
    }
    actPanel.listElements.add(value);      //list of elements in panels   
    listElementsInTextfileOrder.add(value);  //list of elements in text file
    newGuiElement = null;
  } 
  
  
  
  
  
  void processConfiguration(final GuiPanelMngBuildIfc panel)
  {
    
  }

  
  public String replacePathPrefix(String path, String[] target)
  {
    String pathRet = path;
    int posSep = path.indexOf(':');
    if(posSep >=0){
      String sRepl = dataReplace.get(path.substring(0, posSep));
      if(sRepl !=null){
        pathRet = sRepl + path.substring(posSep+1);  //Note: sRepl may contain a ':', its the device.
      }
      posSep = pathRet.indexOf(':');  //after replace or if it isn't replaced
      if(posSep >=0){
        target[0] = pathRet.substring(0, posSep);
        pathRet = pathRet.substring(posSep+1);
      }
    }
    return pathRet;
  }
  
  
  void stop(){}
}
