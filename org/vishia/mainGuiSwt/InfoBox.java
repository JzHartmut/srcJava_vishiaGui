package org.vishia.mainGuiSwt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.ifc.GuiWindowMng_ifc;
import org.vishia.gral.ifc.GuiWindow_ifc;


/**This class is a simple Info box containing a text. The class can instantiated at any time,
 * the instance can be shown if necessary. The Info box based on javax.swing.JDiaglog.
 * It is a full functional window. It may be modal or not. 
 * <br><br>
 * TODO: The user should be set non-modal per mouse-click if it is opened modal. 
 */
public class InfoBox extends Composite implements GuiWindowMng_ifc
{

  private static final long serialVersionUID = 392806902552552727L;

  String sTitle;
  Shell parentShell;
  
  boolean modal;
 
  /**Initializes.
   * @param sTitle Title line of windows.
   * @param textLines Text lines. All Strings are placed left side.
   *        The font used is the text standard font for the selected size, see {@link PropertiesGuiSwt}.
   * @param modal true than the window should be closed to force further working on the parent window.
   */
  public InfoBox (Shell parent, String sTitle, String[] textLines, boolean modal) {
    super (parent, 0);
    this.parentShell = parent;
    this.sTitle = sTitle;
    this.modal = modal;
    int maxWidth = 0;
    int yPos = 0;
    int dy = 18;
    for(String line: textLines){
      Label label = new Label(this, SWT.LEAD);
      label.setText(line);
      Point sizeLabel = label.getSize();
      label.setBounds(0, yPos, sizeLabel.x, sizeLabel.y);
      //this.add(label);
      yPos += sizeLabel.y;
      if(maxWidth < sizeLabel.x){ maxWidth = sizeLabel.x; }
    }  
    Point size = getSize();
    //setSize(size.width, size.height+50);
    //setSize(maxWidth, yPos+50);
    setBounds(100,100, maxWidth + 20, yPos+50);
    //setModalityType(modal ? ModalityType.TOOLKIT_MODAL : ModalityType.MODELESS);
    this.update();
  }

  public Object open () {
    //Shell parent = getParent();
    Shell shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    shell.setText(sTitle);
    // Your code goes here (widget creation, set result, etc).
    shell.open();
    Display display = parentShell.getDisplay();
    while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
    }
    return "";
 }
  
  
  
  
  static Composite showInfo(Shell frame, String sTitle, String[] textLines, boolean modal)
  {
  	InfoBox dlg = new InfoBox(frame, sTitle, textLines, modal);//main.writeInfoln("action!");
    dlg.open();
    dlg.setVisible(true);
    return dlg;  
  }

  @Override
  public void setWindowVisible(boolean visible)
  {
    setVisible(visible);
    
  }

  @Override
  public boolean isWindowsVisible()
  {
    return isVisible();
  }
  
}
