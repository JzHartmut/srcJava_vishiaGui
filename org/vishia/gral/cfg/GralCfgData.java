package org.vishia.gral.cfg;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.vishia.gral.ifc.GralMngBuild_ifc;


/**This class contains all configuration data for the appearance of the GUI.
 * It can be filled by {@link org.vishia.gral.cfg.GralCfgZbnf} or others.
 * It is changed by user handling on GUI.
 * It can be written out in a new configuration file
 * 
 * @author Hartmut Schorrig
 *
 */
public final class GralCfgData
{
  
  /**Version and history
   * <ul>
   * <li>2012-04-22 Hartmut support {@link #new_Type()}.
   * <li>2012-02-25 Hartmut chg {@link GuiCfgCurveLine#colorValue} = -1 initially to check whether it is given,
   *   see {@link GralCfgBuilder#buildWidget(GralCfgElement)}
   * <li>2011-06-00 Hartmut created: The old concept was evaluating ZBNF parse result of cfg file manually,
   *   now usage of {@link org.vishia.zbnf.ZbnfJavaOutput} to write result info in this class.
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
  public static final int version = 20120422;
  
  /**ZBNF: DataReplace::= <$?key> = <$-/\.?string> ;
   * Class for temporary instance to capture key and string. */
  public final static class DataReplace
  { public String key; public String string; 
  }//class DataReplace
  
  
  /**ZBNF: Element::= ... ;
   * Class for instance to capture and store one element. */
  //public final static class GuiCfgElement 
  
  /**ZBNF: position::= ... ;
   * Class for instance to capture and store the position in an element. */
  //public final static class GuiCfgPosition{  }//class Position
  
  
  /**This is the base class of all widget types for the GUI. It contains common elements.
   */
  public static class WidgetTypeBase implements Cloneable
  {
    private final GralCfgElement itsElement;

    /**See {@link org.vishia.gral.base.GralWidget#whatIs}. */
    final char whatIs;


    /**From ZBNF-parser param::=<?> ...name = <""?name> etc. values are set if it is parsed. */
    public String name, text, cmd, userAction, info, showMethod, format, type, prompt, promptPosition;
    
    /**From ZBNF-parser param::=<?> ...dropFiles = <""?name> etc. values are set if it is parsed. */
    public String dragFiles, dropFiles, dragText, dropText;
    
    //public GuiCfgColor colorName = null; 
    public GuiCfgColor color0 = null, color1 = null;
    
    /**From ZBNF-parser param::=<?> ...<?dragFiles> etc. boolean values are set if <?name> is parsed. */
    
    public WidgetTypeBase(GralCfgElement itsElement, char whatIs){ 
      this.itsElement = itsElement; 
      this.whatIs = whatIs;
    }
    
    public GuiCfgColor new_colorName(){ return color0 = new GuiCfgColor(); }
    
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

    /**Sets all fields which are not determined by this instance from any other instance,
     * especially from a type instance.
     * @param src source for all values which are not determined in this yet.
     */
    public void setFromType(WidgetTypeBase src){
      if(text ==null){ text = src.text; }
      if(cmd ==null){ cmd = src.cmd; }
      if(userAction ==null){ userAction = src.userAction; }
      if(info ==null){ info = src.info; }
      if(showMethod ==null){ showMethod = src.showMethod; }
      if(prompt ==null){ prompt = src.prompt; }
      if(promptPosition !=null){ promptPosition = src.promptPosition;}
      if(dragFiles ==null){ dragFiles = src.dragFiles; }
      if(dropFiles ==null){ dropFiles = src.dropFiles; }
      if(dragText ==null){ dragText = src.dragText; }
      //if(colorName ==null){ colorName = src.colorName; }
      if(color0 ==null){ color0 = src.color0; }
      if(color1 ==null){ color1 = src.color1; }
    }
    
    
  }//class WidgetTypeBase
  
  
  /**ZBNF: Text::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgType extends WidgetTypeBase implements Cloneable
  {
    public String typeName;
    public GuiCfgType(){ super(null, '*'); }
  }
  
  
  /**ZBNF: Text::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgText extends WidgetTypeBase implements Cloneable
  {
    public String size = "B";
    public int colorValue;
    public GuiCfgText(GralCfgElement itsElement){ super(itsElement, 'S'); }
    public void XXXset_colorValue(int value){
      //colorName = 
    }
  }
  
  
  /**ZBNF: Text::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgLed extends WidgetTypeBase implements Cloneable
  {
    //public String size = "B";
    public GuiCfgLed(GralCfgElement itsElement){ super(itsElement, 'D'); }
  }
  
  
  
  /**ZBNF: Line::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgLine extends WidgetTypeBase implements Cloneable
  {
    List<GuiCfgCoord> coords = new LinkedList<GuiCfgCoord>();
    
    public GuiCfgLine(GralCfgElement itsElement){ super(itsElement, 'I'); }
    
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
    
    public GuiCfgImage(GralCfgElement itsElement){ super(itsElement, 'i'); }
  
    public void set_file(String value){ file_ = value; }
  }
  
  
  
  /**ZBNF: ShowField::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgShowField extends WidgetTypeBase implements Cloneable
  {
    
    public GuiCfgShowField(GralCfgElement itsElement){ super(itsElement, 'S'); }
  }
  
  
  /**ZBNF: ShowField::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgInputFile extends WidgetTypeBase implements Cloneable
  {
    
    public GuiCfgInputFile(GralCfgElement itsElement){ super(itsElement, 'F'); }
  }
  
  
  /**ZBNF: Button::= ... ;
   * Class for instance to capture and store the Button data. */
  public final static class GuiCfgButton extends WidgetTypeBase implements Cloneable
  {
    final boolean bSwitch;
    public GuiCfgButton(GralCfgElement itsElement){ super(itsElement, 'B'); bSwitch = false; }
    public GuiCfgButton(GralCfgElement itsElement, boolean bSwitch){ super(itsElement, 'B'); this.bSwitch = bSwitch; }
  }
  
  
  
  /**ZBNF: Table::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgTable extends WidgetTypeBase implements Cloneable
  {
    
    public GuiCfgTable(GralCfgElement itsElement){ super(itsElement, 'l'); }

    public int height;
    
    private final List<Integer> columnWidths = new LinkedList<Integer>();
    
    public void set_columnWidth(int val){ columnWidths.add(val); }
    
    public void set_text(String value){ super.text = value; }
    
    public List<Integer> getColumnWidths(){ return columnWidths; } 
  }
  
  
  /**ZBNF: Table::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgCurveview extends WidgetTypeBase implements Cloneable
  {
    public int nrofPoints;
    
    public boolean activate;
    
    List<GuiCfgCurveLine> lines = new LinkedList<GuiCfgCurveLine>();
    
    /**The element is created if the text if {@link #set_line(String)} is invoked 
     * before {@link #newGuiElement} is invoked. The association is temporary valid for the current element. */
    private GuiCfgCurveLine newLine;
    
    
    public GuiCfgCurveview(GralCfgElement itsElement){ super(itsElement, 'c'); }

    
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
    
    public GuiCfgCurveLine(GralCfgElement itsElement){ super(itsElement, 'C'); }
    
    public int colorValue = 0;
    public float offset, scale;
    public int nullLine;
  }
  
  
  public final static class GuiCfgColor
  {
    public String color;
  }
  
  
  
  GralCfgElement firstElement = null;
  
  private GralCfgElement actualElement = null;
  
  /**ZBNF: DataReplace::= <$?key> = <$-/\.?string> ;
   * Temporary instance to capture key and string. */
  private final DataReplace dataReplaceTempInstance = new DataReplace();
  
  
  /**The element is created if the text if {@link #set_Element(String)} is invoked 
   * before {@link #newGuiElement} is invoked. The association is temporary valid for the current element. */
  private GralCfgElement newGuiElement;
  
  /**Map of replacements of paths to data. Filled from ZBNF: DataReplace::= <$?key> = <$-/\.?string> */
  public final Map<String, String> dataReplace = new TreeMap<String,String>();

  Map<String, WidgetTypeBase> idxTypes = new TreeMap <String, WidgetTypeBase>();
  
  
  /**TODO widgets sorted to panels and tabs!
   * 
   */
  private final List<GralCfgElement> listElementsInTextfileOrder = new LinkedList<GralCfgElement>();
  
  GralCfgPanel actPanel;
  
  /**Map of replacements of paths to data. Filled from ZBNF: DataReplace::= <$?key> = <$-/\.?string> */
  private final Map<String, GralCfgPanel> idxPanels = new TreeMap<String,GralCfgPanel>();

  
  public GralCfgData()
  {
    
  }
  
  
  public Set<Map.Entry<String, GralCfgPanel>> getPanels(){return idxPanels.entrySet(); } 
  
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
  
  
  /**ZBNF: DataReplace: < ?Element >[ | | ] */
  public GralCfgElement new_Element()
  { 
    if(newGuiElement == null){ newGuiElement = new GralCfgElement(this); }
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
    if(newGuiElement == null){ newGuiElement = new GralCfgElement(this); }
    newGuiElement.content = val;
    //NOTE: the newGuiElement will be returned to fill in in new_Element()
  }
  
  /**ZBNF: Type::= typeName ( param ); */
  public GralCfgData.GuiCfgType new_Type()
  { GralCfgData.GuiCfgType widgt = new GralCfgData.GuiCfgType();
    return widgt;
  }

  
  /**ZBNF: Type::= typeName ( param ); */
  public void add_Type(GralCfgData.GuiCfgType data){  
    idxTypes.put(data.typeName, data);
  }
  

  
  /**From ZBNF: DataReplace: < DataReplace> */
  public void set_Element(GralCfgElement value)
  { String sPanel = value.positionInput.panel;
    if(value.widgetType != null && value.widgetType.text !=null && value.widgetType.text.equals("wd:yCos"))
      stop();
    if(sPanel == null){ //the last panel is used furthermore.
      if(actPanel == null){ 
        actPanel = new GralCfgPanel("$");
      }
      sPanel = actPanel.name;
      value.setPanel(sPanel);
    } else { //a panel is given.
      actPanel = idxPanels.get(sPanel); 
      if(actPanel == null){ //first time use that:
        actPanel = new GralCfgPanel(sPanel);
        idxPanels.put(sPanel, actPanel);
      }
    }
    actPanel.listElements.add(value);      //list of elements in panels   
    listElementsInTextfileOrder.add(value);  //list of elements in text file
    newGuiElement = null;
  } 
  
  
  
  
  
  void XXXprocessConfiguration(final GralMngBuild_ifc panel)
  {
    
  }

  
  public String XXXreplacePathPrefix(String path, String[] target)
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
