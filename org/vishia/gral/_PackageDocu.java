package org.vishia.gral;

import org.vishia.commander.LeftMidRightPanel;
import org.vishia.gral.area9.GralArea9Window;
import org.vishia.gral.cfg.GralCfgDesigner;
import org.vishia.gral.cfg.GralCfgZbnf;
import org.vishia.gral.ifc.GralGridBuild_ifc;
import org.vishia.gral.widget.CommandSelector;
import org.vishia.gral.widget.FileSelector;
import org.vishia.gral.widget.SelectList;
import org.vishia.guiInspc.InspcGui;

/**The package gral is a GRaphic Adaption Layer.
 * Why it is necessary:
 * <ul>
 * <li>The graphic systems such as Swing or SWT (eclipse) or a Windows API has the same fundamental concepts to work,
 *   but there are some differences. If a user works for example in Java-Swing, ones sources are not compatible
 *   with SWT or another graphic system for example from QT. There are too may special knowledge necessary 
 *   and dependencies existing of the graphic platform.
 * <li>A user software should be independently of the graphic platform if only standard requirements are used.
 *   Only for high-specific solutions the full bandwidth of the graphical base system may be need.
 * <li>The graphic programming should be easy to do.
 * </ul>
 * The gral supports some independent concepts to fit that requests:
 * <ul>
 * <li>A frame for a standard GUI application, which supports up to 9 areas in the application window for input, output etc.
 *   {@link GralArea9Window}, {@link org.vishia.gral.area9._PackageDocu}
 * <li>A System to place widgets with grid coordinates, allowing larger and smaller presentation with proper fonts etc.
 *   The user should not deal with pixel positions.
 *   {@link org.vishia.gral.gridPanel._PackageDocu}
 * <li>A system to build the graphic from a script. {@link GralCfgZbnf}
 * <li>The possibility to edit the appearance of GUI on runtime: {@link GralCfgDesigner}
 * <li>Animated graphic, especially for process data viewing: {@link InspcGui}
 * <li> 
 * </ul>     
 * <b>Overview</b>:
 * <img src="../../../img/Overview_gral.png"><br>
 * The user creates the instances for the window and the Gral manager using a factory class for the choiced graphic base.
 * Then the appearance of GUI can be created using the {@link GralGridBuild_ifc}.
 * <br>
 * After them the user can work with its GUI.
 * 
 * <br><br>
 * <b>Example ussage of a org.eclipse.swt.widgets.Table from the GUI application 'JavaCommander'</b>:
 * <img src="../../../img/JCmd_gralWidget_SwtTable.png"><br>
 * The ObjectModelDiagram shows the application 'JavaCommander'. That application uses some tables to select files, commandes
 * and selection objects. The classes {@link LeftMidRightPanel.FcmdFavorTable}, {@link CommandSelector} and {@link FileSelector}
 * are the 3 representants of selection lists from side of application. 
 * They have its implementation of a {@link SelectList}, which is a base class which supports navigaton in a list.
 * The   
 * The adaption to a basic system is present yet only for SWT. Adaption to Swing is planned.
 * @author Hartmut Schorrig
 *
 */
public class _PackageDocu
{

  public final static int version = 0x20111001;
}
