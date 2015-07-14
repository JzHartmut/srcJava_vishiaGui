/**The package gral is a GRaphic Adaption Layer.
 * Why it is necessary:
 * <ul>
 * <li>The graphic systems such as Swing or SWT (eclipse) or a Windows API has the same fundamental concepts to work,
 *   but there are some differences. If a user works for example in Java-Swing, ones sources are not compatible
 *   with SWT or another graphic system for example from QT. There are too may special knowledge necessary 
 *   and dependencies existing of the graphic platform.
 * <li>A user software should be independent of the graphic platform if only standard requirements are used.
 *   Only for high-specific solutions the full bandwidth of the graphical base system may be need.
 * <li>The graphic programming should be easy to do.
 * </ul>
 * The gral supports some independent concepts to fit that requests:
 * <ul>
 * <li>A frame for a standard GUI application, which supports up to 9 areas in the application window for input, output etc.
 *   {@link org.vishia.gral.area9.GralArea9Window}, {@link org.vishia.gral.area9}
 * <li>A System to place widgets with grid coordinates, allowing larger and smaller presentation with proper fonts etc.
 *   The user should not deal with pixel positions.
 *   {@link org.vishia.gral.base.GralPos}
 * <li>A system to build the graphic from a script. {@link org.vishia.gral.cfg}
 * <li>The possibility to edit the appearance of GUI on runtime: {@link org.vishia.gral.cfg.GralCfgDesigner}
 * <li>Set values for all widgets in another thread than the graphic thread and invoke repaint:
 *   {@link org.vishia.gral.ifc.GralWidget_ifc}.
 * <li>Animated graphic, especially for process data viewing: {@link org.vishia.guiInspc.InspcGui}
 * <li> 
 * </ul>
 * Important classes:
 * <ul>
 * <li>{@link org.vishia.gral.base.GralWidget}: Base class for all user widgets.
 * <li>{@link org.vishia.gral.base.GralPos}: Information about positioning
 * <li>{@link org.vishia.gral.base.GralMng}: Manager for organization, singleton.
 * <li>{@link org.vishia.gral.test.HelloWorld}: Example
 * <li>{@link org.vishia.gral.ifc.GralFactory}: Factory interface
 * <li>{@link org.vishia.gral.awt.AwtFactory}: The Factory for AWT (and swing)
 * <li>{@link org.vishia.gral.swt.SwtFactory}: The Factory for SWT (Eclipse Graphic widget toolkit)
 * <li>{@link org.vishia.gral.base.GralMenu}: The menus both pull-down and right-mouse (context).
 * <li>{@link org.vishia.gral.base.GralWindow}: A window, the main or sub windows, dialog boxes.
 * <li>{@link org.vishia.gral.base.GralGraphicTimeOrder}: Order initialized from another thread. 
 * <li>{@link org.vishia.gral.area9.GralArea9Window}: Window with 9 areas configurable for typical using
 * 
 * </ul>
 * Widgets:
 * <ul>
 * <li>{@link org.vishia.gral.base.GralTextField}:
 * <li>{@link org.vishia.gral.base.GralTextBox}:
 * <li>{@link org.vishia.gral.base.GralHtmlBox}:
 * <li>{@link org.vishia.gral.base.GralTable}: Table and tree representation.
 * <li>{@link org.vishia.gral.base.GralButton}:
 * <li>{@link org.vishia.gral.base.GralLed}:
 * <li>{@link org.vishia.gral.base.GralPanelContent}:
 * <li>{@link org.vishia.gral.base.GralTabbedPanel}:
 * <li>{@link org.vishia.gral.base.GralSlider}:
 * <li>{@link org.vishia.gral.base.GralValueBar}:
 * <li>{@link org.vishia.gral.base.GralCurveView}:
 * <li>
 * <li> 
 * </ul>     
 * <b>Overview</b>:
 * <img src="../../../img/Overview_gral.png"><br>
 * The user creates the instances for the window and the Gral manager using a factory class for the choiced graphic base.
 * Then the appearance of GUI can be created using the {@link GralMngBuild_ifc}.
 * <br>
 * After them the user can work with its GUI.
 * 
 * <br><br>
 * <b>Example ussage of a org.eclipse.swt.widgets.Table from the GUI application 'JavaCommander'</b>:
 * <img src="../../../img/JCmd_gralWidget_SwtTable.png"><br>
 * The ObjectModelDiagram shows the application 'JavaCommander'. That application uses some tables to select files, commandes
 * and selection objects. The classes {@link FcmdLeftMidRightPanel.FcmdFavorCard}, {@link GralCommandSelector} and {@link GralFileSelector}
 * are the 3 representations of selection lists from side of application. 
 * They have its implementation of a {@link GralSelectList}, which is a base class which supports navigaton in a list.
 * The   
 * The adaption to a basic system is present yet only for SWT. Adaption to Swing is planned.
 * @author Hartmut Schorrig
 *
 */
package org.vishia.gral;
