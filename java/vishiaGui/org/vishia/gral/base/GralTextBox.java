
package org.vishia.gral.base;

import java.io.IOException;

//import java.util.Map;
//import java.util.TreeMap;
//
//import org.eclipse.swt.graphics.Point;
//import org.vishia.gral.ifc.GralColor;
//import org.vishia.gral.ifc.GralFont;
//import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralTextBox_ifc;
//import org.vishia.gral.ifc.GralTextFieldUser_ifc;
//import org.vishia.util.KeyCode;

public class GralTextBox extends GralTextField implements Appendable, GralTextBox_ifc
{

  /**Version and history
   * <ul>
   * <li>2022-01-31 Hartmut chg: {@link #append(CharSequence)} without IOException.
   *   It is possible in Java though the interface defines an IOException. Here not necessary and more simple for application.
   * <li>2022-01-31 Hartmut chg: Some stuff is commented now, because it is all in GralTextField,
   *   because SwtTextFieldWrapper contains all necessities. Not an extra implementation class.
   * <li>2014-08-16 Hartmut chg: Now Implementation uses the same class, as GralTextField, inheritance was done before.
   *   It is very more simple. Same only additional features for GralTextBox and GralTextField
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
  public final static String sVersion = "2022-01-28";



//  Map<Integer, Integer> posLines;


  /**Constructs a gral text box.
   * @param refPos contains the parent position, correct with posName for further usage. 
   *   The internal used GralPos is cloned. This refPos is not referenced in the instance.
   * @param posName Position and Name of the field. Maybe null if it is not need in management by name
   *   See {@link GralWidget#GralWidget(String, char)}.
   * @param property password, editable, maybe left empty.
   */
  public GralTextBox(GralPos refPos, String posName, Type... property)
  { super(refPos, posName, property);
    super.newText = new StringBuffer();
  }

  /**
   * @param refPos contains the parent position, correct with posName for further usage. 
   *   The internal used GralPos is cloned. This refPos is not referenced in the instance.
   * @param posName Position and Name of the field. Maybe null if it is not need in management by name
   *   See {@link GralWidget#GralWidget(String, char)}.
   * @param editable true: can edit. It is also possible to set it with {@link #setEditable(boolean)}
   * @param prompt a prompt, also able to set with {@link #setPrompt(String, String)} but here concentrated.
   * @param promptStylePosition 't' ...
   * @param property password, editable, maybe left empty.
   */
  public GralTextBox(GralPos refPos, String posName, boolean editable, String prompt, char promptStylePosition, Type... property)
  { super(refPos, posName, property);
    super.newText = new StringBuffer();
    char[] prompt1 = new char[1];
    prompt1[0] = promptStylePosition;
    this.setPrompt(prompt, new String(prompt1));
    this.setEditable(editable);

  }

//  public GralTextBox(String name, Type... property) {
//    this(null, name, property);
//  }


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
  @Override public final Appendable append(CharSequence arg0)
  { synchronized(this.newText) {
      this.newText.append(arg0);
      this.dyda.setChanged(GraphicImplAccess.chgAddText | GraphicImplAccess.chgViewTrail);
      redraw();
    }
    return this;
  }

  /**Append a single char, able to call threadsafe in any thread.
   * @see #append(CharSequence)
   *
   * @see java.lang.Appendable#append(java.lang.CharSequence)
   */
  @Override public final Appendable append(char arg0)
  { synchronized(this.newText) {
    this.newText.append(arg0);
    this.dyda.setChanged(GraphicImplAccess.chgAddText | GraphicImplAccess.chgViewTrail);
    redraw();
  }
  return this;
}

  /**Append a sub char sequence, able to call threadsafe in any thread.
   * @see #append(CharSequence)
   *
   * @see java.lang.Appendable#append(java.lang.CharSequence, int, int)
   */
  @Override public final Appendable append(CharSequence arg0, int arg1, int arg2)
  {
    append(arg0.subSequence(arg1, arg2));
    return this;
  }



//  @Override public void setTextStyle(GralColor color, GralFont font)
//  {
//    dyda.textFont = font;
//    dyda.textColor = color;
//    dyda.setChanged(GralWidget.ImplAccess.chgColorText);
//    if(_wdgImpl !=null){
//      repaint();
//    }
//  }



  @Override public void setEditable(boolean editable){
    this.dyda.setChanged(editable ? GraphicImplAccess.chgEditable : GraphicImplAccess.chgNonEditable);
    if(this._wdgImpl !=null){
      redraw();
    }
  }



  @Override public int getNrofLines(){ return 0; }  //TODO


//  @Override public int getCursorPos() { return super.caretPos = ((GraphicImplAccess)_wdgImpl).getCurrentCaretPos(); }
//
//  public LineColumn getCursorLineColumn() {
//    super.caretPos = ((GralTextBox.GraphicImplAccess)_wdgImpl).getCurrentCaretPos();
//    int lineNr = ((GralTextBox.GraphicImplAccess)_wdgImpl).getCurrentCaretLinePos();
//    //if(this.posLines == null) { posLines = new TreeMap<Integer, Integer>(); }
//    LineColumn ret = new LineColumn(lineNr, super.caretPos);
//    return ret;
//  }

  @Override public void viewTrail()
  {
    this.dyda.setChanged(GraphicImplAccess.chgViewTrail);
    if(this._wdgImpl !=null){
      redraw();
    }

  }


  public static class XXXLineColumn {
    final int line, col;

    public XXXLineColumn(int line, int column) {
      this.line = line;
      this.col = column;
    }
  }



//  public abstract class GraphicImplAccess extends GralTextField.GraphicImplAccess  //GralWidget.ImplAccess
//  implements GralWidgImpl_ifc
//  {
//    public static final int chgCursor = 0x200, chgEditable = 0x400, chgNonEditable = 0x800
//        , chgViewTrail = 0x1000, chgAddText = 0x2000;
//
//
//    protected GraphicImplAccess(GralWidget widgg)
//    {
//      super(widgg);
//    }
//
//    protected String getAndClearNewText(){ String ret; synchronized(newText){ ret = newText.toString(); newText.setLength(0); } return ret; }
//
//    protected int caretPos(){ return GralTextBox.this.caretPos; }
//
//    protected void caretPos(int newPos){ GralTextBox.this.caretPos = newPos; }
//
//    protected GralTextFieldUser_ifc user(){ return GralTextBox.this.user; }
//
//    protected abstract int getCurrentCaretPos();
//    protected abstract int getCurrentCaretLinePos();
//
//  }

}
