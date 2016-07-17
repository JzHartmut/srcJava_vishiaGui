package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**This is a commonly use-able tranverse listener for all widgets in SWT.
 * Note that the system's (Windows-, Linux-) traverse mechanism is executed without this listener
 * with that operation systems standards.
 * @author Hartmut Schorrig
 *
 */
public class SwtTraverseListener implements TraverseListener
{

  @Override public void keyTraversed(TraverseEvent e)
  {
    int key = e.keyCode;
    int traverse = e.detail;
    if(traverse == SWT.TRAVERSE_RETURN) return;
    Control widgs = (Control)e.widget;
    Composite panel = widgs.getParent();
    Control[] allwidg = panel.getChildren();
    for(int ix = 0; ix < allwidg.length; ++ix){
      if(allwidg[ix] == widgs){
        if(traverse == SWT.TRAVERSE_TAB_NEXT || traverse == SWT.TRAVERSE_ARROW_NEXT) {
          ix+=1; if(ix >= allwidg.length){ ix = 0; }
        } else if(traverse == SWT.TRAVERSE_TAB_PREVIOUS || traverse == SWT.TRAVERSE_ARROW_PREVIOUS) {
          ix-=1; if(ix <0){ ix =  allwidg.length-1; }
        }
        else return;  //other keys: do nothing.
        Control nextwidg = allwidg[ix];
        nextwidg.setFocus();
        break;
      }
    }
    
  }
  
}
  

