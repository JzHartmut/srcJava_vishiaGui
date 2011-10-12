package org.vishia.gral.cfg;

/**This package contains classes which implements configuration capabilities for a GUI.
 * The configuration can be done
 * <ul>
 * <li>by a script, parsed with ZBNF: {@link GralCfgZbnf}.
 * <li>by setting data into {@link GralCfgData} with any java program and run 
 *   {@link GralCfgBuilder#buildGui(org.vishia.msgDispatch.LogMessage, int)}.
 * <li>by editing fields in the GUI representation itself, using {@link GralCfgDesigner}.
 * </ul>
 * The GUI configuration is presented with data in {@link GralCfgData} in any case. 
 * They can be written into a script using {@link GralCfgWriter}, which can be re-read with the {@link GralCfgZbnf}
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
