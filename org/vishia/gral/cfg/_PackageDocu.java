package org.vishia.gral.cfg;

/**This package contains classes which implements configuration capabilities for a GUI.
 * The configuration can be done
 * <ul>
 * <li>by a script, parsed with ZBNF: {@link GuiCfgZbnf}.
 * <li>by setting data into {@link GuiCfgData} with any java program and run 
 *   {@link GuiCfgBuilder#buildGui(org.vishia.msgDispatch.LogMessage, int)}.
 * <li>by editing fields in the GUI representation itself, using {@link GuiCfgDesigner}.
 * </ul>
 * The GUI configuration is presented with data in {@link GuiCfgData} in any case. 
 * They can be written into a script using {@link GuiCfgWriter}, which can be re-read with the {@link GuiCfgZbnf}
 * to restore the data. The GUI configuration is not presented with any Java program then.
 * <br><br>
 * This cfg package is an alternative against writing a Java-program to build a GUI.
 *       
 * @author Hartmut Schorrig
 *
 */
public class _PackageDocu
{
  static final int version = 0x20110928;
}
