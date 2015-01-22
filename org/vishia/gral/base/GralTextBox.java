package org.vishia.gral.base;

import java.io.IOException;

import org.eclipse.swt.graphics.Point;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralTextBox_ifc;
import org.vishia.gral.ifc.GralTextFieldUser_ifc;
import org.vishia.util.KeyCode;

public class GralTextBox extends GralTextField implements Appendable, GralTextBox_ifc
{
  
  /**Version and history
   * <ul>
   * <li>2014-08-16 Hartmut chg: GrapTextBox not abstract, using GraphicImplAccess like new concept of all GralWidgets. 
   * <li>2012-01-06 Hartmut chg: The {@link #append(CharSequence)} etc. methods are implemented
   *   in this super class instead in the graphic layer implementation classes. Therefore
   *   the methods {@link #appendTextInGThread(CharSequence)} and {@link #setTextInGThread(CharSequence)}
   *   are defined here to implement in the graphic layer. The set- and apppend methods are <b>threadsafe</b> now.
   * </ul>
   *
   * <b>Copyright/Copyleft</b>:
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
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are indent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  @SuppressWarnings("hiding")
  public final static String sVersion = "2014-08-16";
  
  /**Buffer for new text which is set or appended in another thread than the graphic thread.
   * This buffer is empty if the graphic thread has processed the {@link GralGraphicTimeOrder}
   * after calling {@link #append(CharSequence)} or {@link #setText(CharSequence)}.
   * It is filled only temporary.
   */
  private StringBuffer newText = new StringBuffer();
  
  public GralTextBox(String name)
  { super(name);
  }

  /**Sets the text to the widget, invoked only in the graphic thread.
   * This method have to be implemented in the Graphic implementation layer.
   * @param text The text which should be shown in the widget.
   */
  //protected abstract void setTextInGThread(CharSequence text);
  
  /**Appends the text to the current text in the widget, invoked only in the graphic thread.
   * This method have to be implemented in the Graphic implementation layer.
   * @param text The text which should be appended and shown in the widget.
   */
  //protected abstract void appendTextInGThread(CharSequence text);

  /**Append the text, able to call threadsafe in any thread.
   * If the thread is the graphic thread, the text will be appended to the current text
   * of the widget immediately. But if the thread is any other one, the text will be stored
   * in a StringBuilder and the graphic thread will be waked up with the {@link #appendTextViewTrail}
   * dispatch listener.
   * 
   * @see java.lang.Appendable#append(java.lang.CharSequence)
   */
  @Override public final Appendable append(CharSequence arg0) throws IOException
  { synchronized(newText) {
      newText.append(arg0);
      dyda.setChanged(GraphicImplAccess.chgAddText | GraphicImplAccess.chgViewTrail);
      repaint();
    }
    return this;
  }

  /**Append a single char, able to call threadsafe in any thread.
   * @see #append(CharSequence)
   * 
   * @see java.lang.Appendable#append(java.lang.CharSequence)
   */
  @Override public final Appendable append(char arg0) throws IOException
  { synchronized(newText) {
    newText.append(arg0);
    dyda.setChanged(GraphicImplAccess.chgAddText | GraphicImplAccess.chgViewTrail);
    repaint();
  }
  return this;
}

  /**Append a sub char sequence, able to call threadsafe in any thread.
   * @see #append(CharSequence)
   * 
   * @see java.lang.Appendable#append(java.lang.CharSequence, int, int)
   */
  @Override public final Appendable append(CharSequence arg0, int arg1, int arg2) throws IOException
  {
    append(arg0.subSequence(arg1, arg2));
    return this;
  }


  
  @Override public void setTextStyle(GralColor color, GralFont font)
  {
    dyda.textFont = font;
    dyda.textColor = color;
    dyda.setChanged(GralWidget.ImplAccess.chgColorText);
    if(_wdgImpl !=null){
      repaint();
    }
  }
  

  
  @Override public void setEditable(boolean editable){
    dyda.setChanged(editable ? GraphicImplAccess.chgEditable : GraphicImplAccess.chgNonEditable);
    if(_wdgImpl !=null){
      repaint();
    }
  }


  
  @Override public int getNrofLines(){ return 0; }  //TODO


  @Override public void viewTrail()
  {
    dyda.setChanged(GraphicImplAccess.chgViewTrail);
    if(_wdgImpl !=null){
      repaint();
    }
    
  }

  
  
  public abstract class GraphicImplAccess extends GralTextField.GraphicImplAccess  //GralWidget.ImplAccess
  implements GralWidgImpl_ifc
  {
    public static final int chgCursor = 0x200, chgEditable = 0x400, chgNonEditable = 0x800
        , chgViewTrail = 0x1000, chgAddText = 0x2000;

    
    protected GraphicImplAccess(GralWidget widgg, GralMng mng)
    {
      super(widgg, mng);
    }
    
    protected String getAndClearNewText(){ String ret; synchronized(newText){ ret = newText.toString(); newText.setLength(0); } return ret; }
    
    protected int caretPos(){ return GralTextBox.this.caretPos; }
    
    protected void caretPos(int newPos){ GralTextBox.this.caretPos = newPos; }
    
    protected GralTextFieldUser_ifc user(){ return GralTextBox.this.user; }

  }

}
