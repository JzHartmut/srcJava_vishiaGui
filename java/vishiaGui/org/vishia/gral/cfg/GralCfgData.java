package org.vishia.gral.cfg;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.vishia.gral.ifc.GralColor;
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
   * <li>2022-09-23 {@link GuiCfgWidget#set_data(String)} now sets default also the name. 
   *   Hence Show(data=path); is sufficient for a simple show field. The path is also the name for access. 
   * <li>2022-08 not elaborately changed, same data, toString() operations. 
   * <li>2014-02-24 Hartmut new element help now also in config.
   * <li>2013-12-02 Hartmut new conditional configuration. 
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
 
  
  
  public final class Conditional
  {
    public boolean condition;
    
    
    final Conditional parentLevel;
    
    public Conditional(GralCfgData cfgData, Conditional parent){
      //super(cfgData);
      parentLevel = parent;
    }
    
  }
  
  
  
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
  
  
  /**This is the base class of all configuration data for one widget. It contains common elements.
   * It is used as destination for the parser of the configuration file.
   */
  public static class GuiCfgWidget implements Cloneable
  {
    /**Backward aggregation to the config element queue, to the item.*/
    private final GralCfgElement itsElement;

    /**See {@link org.vishia.gral.base.GralWidget#whatIs}. */
    final char whatIs;

    public boolean editable;
    
    /**From ZBNF-parser param::=<?> ...name = <""?name> etc. values are set if it is parsed. 
     * The name can alternatively also given in {@link GralCfgElement#name}. 
     */
    public String name, text, cmd, userAction, mouseAction, data, showMethod, format, type, prompt, promptPosition, help;
    
    /**From ZBNF-parser param::=<?> ...dropFiles = <""?name> etc. values are set if it is parsed. */
    public String dragFiles, dropFiles, dragText, dropText;
    
    //public GuiCfgColor colorName = null; 
    public GuiCfgColor color0 = null, color1 = null;
    
    /**From ZBNF-parser param::=<?> ...<?dragFiles> etc. boolean values are set if <?name> is parsed. */
    
    public GuiCfgWidget(GralCfgElement itsElement, char whatIs){ 
      this.itsElement = itsElement; 
      this.whatIs = whatIs;
    }
    
    /**<code> &lt;""?text> | &lt;$-/\.:?text></code> is the non designated form of a parameter.
     * Use it as name if the name is not given, and also as data path. 
     * <br>
     * Example Show(myVar) use the text as {@link {@link #data} and {@link #name}. It is the simple form. 
     * Also set the field {@link #text}
     * @param val
     */
    public void set_text(String val) {
//      if(this.data == null) { this.data = val; }
//      if(this.name ==null) { this.name = val; }  // default name, will be overridden if name= is given.
      this.text = val;
    }
    
    public void set_data(String val){ 
      this.data = val;                           // the data path, usual use a variable
      if(this.name ==null) { this.name = val; }  // default name, will be overridden if name= is given.
    }
    
    public void set_help(String sHelp){ this.help = sHelp; }
    
    public GuiCfgColor new_colorName(){ return color0 = new GuiCfgColor(); }
    
    public void set_colorName(GuiCfgColor value){}
    
    public GuiCfgColor new_color0(){ return color0 = new GuiCfgColor(); }
    
    public void set_color0(GuiCfgColor value){}
    
    public GuiCfgColor new_color1(){ return color1 = new GuiCfgColor(); }
    
    public void set_color1(GuiCfgColor value){}

    @Override
    protected GuiCfgWidget clone()
    { GuiCfgWidget clone = null;
      try{ clone = (GuiCfgWidget)super.clone(); } 
      catch(CloneNotSupportedException exc){ assert(false); }
      return clone;
    }

    /**Sets all fields which are not determined by this instance from any other instance,
     * especially from a type instance.
     * @param src source for all values which are not determined in this yet.
     */
    public void setFromType(GuiCfgWidget src){
      if(text ==null){ text = src.text; }
      if(cmd ==null){ cmd = src.cmd; }
      if(userAction ==null){ userAction = src.userAction; }
      if(data ==null){ data = src.data; }
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
    
    @Override public String toString() { return "Widget name=: " + this.name; }
    
  }//class WidgetTypeBase
  
  
  /**ZBNF: Text::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgType extends GuiCfgWidget implements Cloneable
  {
    public String typeName;
    public GuiCfgType(){ super(null, '*'); }

    @Override public String toString() { return "Widget Type...: "; }
  }
  
  

  /**ZBNF: Text::= ... ;
   */
  public final static class GuiCfgText extends GuiCfgWidget implements Cloneable
  {
    public String size = "B";
    public int colorValue;
    public GuiCfgText(GralCfgElement itsElement){ super(itsElement, 'S'); }
    public void XXXset_colorValue(int value){
      //colorName = 
    }
    @Override public String toString() { return "Textfield: " + super.text; }
  }
  
  
  /**ZBNF: Text::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgLed extends GuiCfgWidget implements Cloneable
  {
    //public String size = "B";
    public GuiCfgLed(GralCfgElement itsElement){ super(itsElement, 'D'); }
    
    @Override
    public void set_data(String val){ this.data = val; }

    @Override public String toString() { return "Led: "; }

  }
  
  
  
  /**ZBNF: Line::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgLine extends GuiCfgWidget implements Cloneable
  {
    List<GuiCfgCoord> coords = new LinkedList<GuiCfgCoord>();
    
    public GuiCfgLine(GralCfgElement itsElement){ super(itsElement, 'I'); }
    
    public void set_coord(String value){}
    
    public GuiCfgCoord new_coord(){ return new GuiCfgCoord(); }
    
    public void add_coord(GuiCfgCoord value){ coords.add(value); }

    @Override public String toString() { return "Line: "; }
  }
  
  
  /**ZBNF: |<?coord> <#f?x>, <#f?y>
   */
  public final static class GuiCfgCoord
  {
    public float x,y;
  }
  

  /**ZBNF: Text::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgImage extends GuiCfgWidget implements Cloneable
  {
    public String size = "B";
    
    String file_;
    
    public GuiCfgImage(GralCfgElement itsElement){ super(itsElement, 'i'); }
  
    public void set_file(String value){ file_ = value; }

    @Override public String toString() { return "Image: "; }
  }
  
  
  
  /**ZBNF: ShowField::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgShowField extends GuiCfgWidget implements Cloneable
  {
    
    public GuiCfgShowField(GralCfgElement itsElement){ super(itsElement, 'S'); }
    
    /**For Show(varx) use the text as {@link {@link #data} and #name. It is the simple form. 
     * Also set the field {@link #text}
     * @param val
     */
    @Override public void set_text(String val) {
      if(this.data == null) { this.data = val; }
      this.text = val;
    }
  }
  
  
  /**ZBNF: ShowField::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgInputFile extends GuiCfgWidget implements Cloneable
  {
    
    public GuiCfgInputFile(GralCfgElement itsElement){ super(itsElement, 'F'); }

    @Override public String toString() { return "InputFile: "; }

  }
  
  
  /**ZBNF: Button::= ... ;
   * Class for instance to capture and store the Button data. */
  public final static class GuiCfgButton extends GuiCfgWidget implements Cloneable
  {
    final boolean bSwitch;
    public GuiCfgButton(GralCfgElement itsElement){ super(itsElement, 'B'); bSwitch = false; }
    public GuiCfgButton(GralCfgElement itsElement, boolean bSwitch){ super(itsElement, 'B'); this.bSwitch = bSwitch; }

    @Override public String toString() { return "Switch-Button: "; }
  }
  
  
  
  /**ZBNF: Table::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgTable extends GuiCfgWidget implements Cloneable
  {
    
    public GuiCfgTable(GralCfgElement itsElement){ super(itsElement, 'l'); }

    public int height;
    
    private final List<Integer> columnWidths = new LinkedList<Integer>();
    
    public void set_columnWidth(int val){ columnWidths.add(val); }
    
    public void set_text(String value){ super.text = value; }
    
    public List<Integer> getColumnWidths(){ return columnWidths; } 

    @Override public String toString() { return "Table: "; }
  }
  
  
  /**ZBNF: Table::= ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgCurveview extends GuiCfgWidget implements Cloneable
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
    

    @Override public String toString() { return "CurveView: "; }

  }
  

  /**ZBNF: {<?line> ... ;
   * Class for instance to capture and store the Table data. */
  public final static class GuiCfgCurveLine extends GuiCfgWidget implements Cloneable
  {
    String content;
    
    public GuiCfgCurveLine(GralCfgElement itsElement){ super(itsElement, 'C'); }
    
    public int colorValue = 0;
    public float offset, scale;
    public int nullLine;

    @Override public String toString() { return "CurveView-Line: "; }

  }
  
  
  public final static class GuiCfgColor
  {
    public GralColor color;
    
    public void set_color(String val) { 
      this.color = GralColor.getColor(val);   //hint: does not throw, use magenta color on error
    }

    @Override public String toString() { return "Color: " + this.color.toString(); }

  }
  
  
  final List<String> cfgConditions;
  
  
//  GralCfgElement firstElement = null;
  
//  private GralCfgElement actualElement = null;
  
  private Conditional actualConditional;
  
  /**ZBNF: DataReplace::= <$?key> = <$-/\.?string> ;
   * Temporary instance to capture key and string. */
  private final DataReplace dataReplaceTempInstance = new DataReplace();
  
  
  /**The element is created if the text if {@link #set_Element(String)} is invoked 
   * before {@link #newGuiElement} is invoked. The association is temporary valid for the current element. */
  private GralCfgElement newGuiElement;
  
  /**Map of replacements of paths to data. Filled from ZBNF: DataReplace::= <$?key> = <$-/\.?string> */
  public final Map<String, String> dataReplace = new TreeMap<String,String>();

  Map<String, GuiCfgWidget> idxTypes = new TreeMap <String, GuiCfgWidget>();
  
  
  /**TODO widgets sorted to panels and tabs!
   * 
   */
  final List<GralCfgElement> listElementsInTextfileOrder = new ArrayList<GralCfgElement>();
  
  //GralCfgPanel actPanel;
  
  /**The current window where 
   * 
   */
  GralCfgWindow currWindow;
  
  /**The current panel where a widget is associated to, with or without panel definition.
   * A panel definition determines this currPanel with the new current one.
   */
  GralCfgPanel currPanel;
  
  /**Map of panels. Filled via {@link #add_Element(GralCfgElement)}  */
  private final Map<String, GralCfgPanel> idxPanels = new TreeMap<String,GralCfgPanel>();

  
  /**Map of panels. Filled via {@link #add_Element(GralCfgElement)}  */
  private final Map<String, GralCfgElement> idxWindow = new TreeMap<String,GralCfgElement>();

  
  public GralCfgData(List<String> conditions)
  {
    this.cfgConditions = conditions;
  }
  
  
  public Set<Map.Entry<String, GralCfgPanel>> getPanels(){return this.idxPanels.entrySet(); } 
  
  public Set<Map.Entry<String, GralCfgElement>> getWindows(){return this.idxWindow.entrySet(); } 
  
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
  public GralCfgData new_Conditional()
  { Conditional conditional = new Conditional(this, actualConditional);
    actualConditional = conditional;
    return this; 
  }  

  
  /**ZBNF: DataReplace: < ?Element >[ | | ] */
  public GralCfgData new_ElseConditional()
  { Conditional conditional = new Conditional(this, actualConditional);
    actualConditional = conditional;
    return this; 
  }  

  
  /**It is called on end of conditional block.
   * @param val
   */
  public void add_Conditional(GralCfgData val){
    actualConditional = actualConditional.parentLevel;
  }
  
  
  public void set_ifCondition(String cond){ 
    
    actualConditional.condition = cfgConditions.contains(cond);
  }

  
  public void set_elseCondition(){ 
    
    actualConditional.condition = !actualConditional.condition;
  }

  

   
  /**ZBNF: filled on "GuiDialogZbnfControlled::= ... <Element>"
   * or also on "Conditional::=  ... <Element>"
   * It is the second level of syntax or the third one if Conditional. */
  public GralCfgElement new_Element()
  { 
    if(newGuiElement == null){ newGuiElement = new GralCfgElement(); }
    //
    if(actualConditional ==null || actualConditional.condition){
//      if(firstElement ==null){
//        firstElement = newGuiElement;
//      }
//      //GralCfgElement actual1 = actualConditional == null ? actualElement : actualConditional.actualElement;
//      if(actualElement !=null){
//        actualElement.next = newGuiElement;
//      }
//      newGuiElement.previous = actualElement;  //may be null
//      actualElement = newGuiElement;
    }
    return newGuiElement; 
  }  

  
  /**ZBNF: filled on "GuiDialogZbnfControlled::= ... <Element>"
   * or also on "Conditional::=  ... <Element>"
   * It is the second level of syntax or the third one if Conditional. 
   * <br>
   * It checks the content:
   * @throws ParseException 
   * 
   * */
  public void add_Element(GralCfgElement value)
  { 
    if(actualConditional ==null || actualConditional.condition){
      String sPanel = value.panel;
      final String name;                                   // The name of the cfgElement have two destinations.
      if(value.name == null) {                             // expected given with syntax (... name = [<""?name>|<$-/?name>] )
        name = value.name = value.widgetType.name;         // then store also here.
      } else {   //------------------------------------or---  given with syntay <*=:?positionString> [ = <$-/?name>]
        name = value.name;
        if(value.widgetType.name !=null && ! value.widgetType.name.equals(value.name)) {
          throw new IllegalArgumentException("only one name should be given or name should be the same: " + value.toString());
        } else {
          value.widgetType.name = value.name;              // store it also in the widgettype
        }
      }
      // name clarified.
      //
//      if(value.widgetType != null && value.widgetType.text !=null && value.widgetType.text.equals("wd:yCos"))
//        stop();
      //===================================================== Select type of widget, consider Window, Panel
      if (value.widgetType instanceof GralCfgWindow){      // definitive given window
        this.idxWindow.put(name, value);
        this.currWindow = (GralCfgWindow)value.widgetType;
      } else { //-------------------------------------------- not a window
        if(this.currWindow ==null) {                       // Window not given, create a default window of not given.  
          GralCfgElement cfgWindow = new GralCfgElement();
          this.currWindow = new GralCfgWindow(null);
          cfgWindow.widgetType = this.currWindow;
          this.currWindow.name = "mainWindow";
          this.idxWindow.put(this.currWindow.name, cfgWindow);
        }
        //
        if(value.widgetType instanceof GralCfgPanel) {     // definitive given panel for example an Area9Panel
          GralCfgPanel newPanel = (GralCfgPanel)value.widgetType;
          if(this.currWindow.panelWin == null) {              // it is the panel of the window
            this.currWindow.panelWin = newPanel;
          } else {                                         // it is a sub panel in a panel
            this.currPanel.listElements.add(value);        // add the GralCfgElement which is the newPanel
          }
          this.currPanel = newPanel;                       // now use it for furthermore
        } else {
          //------------------------------------------------- other widget, on a panel
          if(this.currPanel == null) {              // default panel in the window
            String nameDefaultPanel = this.currWindow.name;
            if(nameDefaultPanel.endsWith("Window")) { nameDefaultPanel = nameDefaultPanel.substring(0, nameDefaultPanel.length()-6);}
            else if(nameDefaultPanel.endsWith("Wind")) { nameDefaultPanel = nameDefaultPanel.substring(0, nameDefaultPanel.length()-4);}
            else if(nameDefaultPanel.endsWith("Win")) { nameDefaultPanel = nameDefaultPanel.substring(0, nameDefaultPanel.length()-3);}
            this.currPanel = new GralCfgPanel(nameDefaultPanel, this.currWindow);
            this.idxPanels.put(nameDefaultPanel, this.currPanel);
            assert(this.currWindow.panelWin == null);  // elsewhere this.currPanel is not null
            this.currWindow.panelWin = this.currPanel;
          }
          //
          if(sPanel == null){                              //the last panel is used furthermore.
            sPanel = this.currPanel.name;
            value.setPanel(sPanel);
          } else {  //======================================= a panel is given.
            GralCfgPanel panel = this.idxPanels.get(sPanel); 
            if(panel == null){                             // an unknown panel name forces a tab in the given window panel. Since 2010
              if(this.currWindow.panelWin.listElements.size() >0) {
                GralCfgPanel tab1 = this.currWindow.panelWin; // if the current panel contains already content
                String nameTabPanel = this.currWindow.panelWin.name + "-Tabbed"; // then create instead a new empty panel for the window
                GralCfgPanel tabbedPanel = new GralCfgPanel(nameTabPanel, this.currWindow);
                this.idxPanels.put(nameTabPanel, tabbedPanel);
                tabbedPanel.listTabs.add(tab1);  // and add the previous current panel as tab panel in the current window:
                this.currWindow.panelWin = tabbedPanel;  // exchange the panel of the window with the new tabbedPanel
              }
              panel = new GralCfgPanel(sPanel, this.currWindow); //the tab pannel
              this.currWindow.panelWin.listTabs.add(panel);
              this.idxPanels.put(sPanel, panel);
            }
            this.currPanel = panel;
            this.currWindow = panel.window;              // another window is selected with the panel
          }
          this.currPanel.listElements.add(value);      //list of elements in panels   
        }
      }
      this.listElementsInTextfileOrder.add(value);  //list of elements in text file
    }
    newGuiElement = null;
  } 
  
  

  
  /**ZBNF: DataReplace: < Element> */
  public void set_Element(String val)
  { 
    if(newGuiElement == null){ newGuiElement = new GralCfgElement(); }
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
