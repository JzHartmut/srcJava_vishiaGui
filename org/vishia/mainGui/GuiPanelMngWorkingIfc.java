package org.vishia.mainGui;

import java.util.List;
import java.util.Map;

/**This is a unique interface for the GUI-panel-manager to work with it.
 * To build the graphical application see {@link GuiPanelMngBuildIfc}. 
 * @author Hartmut Schorrig
 *
 */
public interface GuiPanelMngWorkingIfc {

	/**Returns the list of all widgets with its informations. 
	 * The graphical representation of the widgets is unknown here.
	 * If it should be used, the graphic implementation should be known
	 * and a adequate instanceof-test and cast is necessary. 
	 */
	List<WidgetDescriptor<?>> getListCurrWidgets();
	
  /**Inserts a textual information at any widget. The widget may be for example:
   * <ul>
   * <li>a Table: Than a new line will be inserted or appended. 
   *     The content associated to the cells are separated with a tab-char <code>'\t'</code>.
   *     The line number is identified by the ident. 
   * <li>a Tree: Than a new leaf is insert after the leaf, which is identified by the ident.
   * <li>a Text-edit-widget: Than a text is inserted in the field.
   * </ul>
   * The insertion is written into a queue, which is red in another thread. 
   * It may be possible too, that the GUI is realized in another module, maybe remote.
   * It means, that a few milliseconds should be planned before the change appears.
   * If the thread doesn't run or the remote receiver isn't present, 
   * than the queue may be overflowed or the request may be lost.
   *    
   * @param name The name of the widget, which was given by the add...()-Operation
   * @param ident A identifying number. It meaning depends on the kind of widget.
   *        0 means, insert on top.  Integer.MAXVALUE means, insert after the last element (append).
   * @param content The content to insert.
   * @return
   */
  String insertInfo(String name, int ident, String content);

  /**Sets the color of background of the widget, if possible.
   * @param name The name of the widget, which was given by the add...()-Operation
   * @ix may be a line number of table or an position identifier. 0 if unused for the kind of widget. 
   * @param colorValue blue, green and red in the bits 23..16, 15..8 and 7..0. 
   *        opaque in bits 31..24 if possible. 
   */
  void setBackColor(String name, int ix, int colorValue);
  
  /**Sets the color of background of the widget, if possible.
   * @param name The name of the widget, which was given by the add...()-Operation
   * @ix may be a line number of table or an position identifier. 0 if unused for the kind of widget. 
   * @param colorValue blue, green and red in the bits 23..16, 15..8 and 7..0. 
   *        opaque in bits 31..24 if possible. 
   */
  void setBackColor(WidgetDescriptor<?> widgetDescr, int ix, int colorValue);
  
  
  void setLed(WidgetDescriptor<?> widgetDescr, int colorBorder, int colorInner);
  
  /**Adds a sampling value set.
   * @param sName The registered name
   * @param values The values.
   */
  void setSampleCurveViewY(String sName, float[] values);
  
  /**Forces the redrawing for all set samples. It should be called after { @link #setSampleCurveViewY(String, float[])}.
   * @param sName The name of the widget.
   */
  void redrawWidget(String sName);
  
	/**Gets the value to the named color. It is a method of the graphic.
	 * @param sName supported: red, green, blue, yellow
	 * @return 3 bytes intensity: bit23..16 blue, bit15..8: green, bit 7..0 red. 
	 */
	int getColorValue(String sName);
	
	/**Gets the value from a widget.
	 * @param sName The name of the widget. The widget will be searched per name.
	 * @return null if the named widget isn't found or the widget is not able to input.
	 */
	String getValue(String sName);

	/**Gets the value from a widget with given descriptor
	 * @param widgetDescr The widget.
	 * @return null if the widget isn't able to input.
	 */
	String getValueFromWidget(WidgetDescriptor<?> widgetDescr);
}
