package org.vishia.gral.swt;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralArea9Panel;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWidget;
import org.vishia.util.Debugutil;

public class SwtPanelArea9 extends SwtGridPanel{

  SwtPanelArea9(GralArea9Panel panelg) {
    super(panelg, '9', 0);
    super.panelSwtImpl.addControlListener(this.resizeArea9Listener);

  }

  
  protected ControlListener resizeArea9Listener = new ControlListener()
  { @Override public void controlMoved(ControlEvent e) 
    { //do nothing if moved.
    }
  
    @Override public void controlResized(ControlEvent e) 
    { 
      //Widget wparent = e.widget; //it is the SwtCanvas because this method is assigned only there.
      //Control parent = wparent;
      for(GralWidget widg1: SwtPanelArea9.this.gralPanel.getWidgetsToResize()){
        if(widg1._wdgImpl !=null) {
          widg1._wdgImpl.setPosBounds();
        } else {
          Debugutil.stop();
        }
        //widg1.gralMng().resizeWidget(widg1, 0, 0);
      }
      //validateFrameAreas();  //calculates the size of the areas newly and redraw.
    }
    
  };



}
