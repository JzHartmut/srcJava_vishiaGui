package org.vishia.gral.base;

import java.io.IOException;

import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralTextBox_ifc;

public abstract class GralTextBox extends GralTextField implements Appendable, GralTextBox_ifc
{
  
  /**Version and history
   * <ul>
   * <li>2012-01-06 Hartmut chg: The {@link #append(CharSequence)} etc. methods are implemented
   *   in this super class instead in the graphic layer implementation classes. Therefore
   *   the methods {@link #appendTextInGThread(CharSequence)} and {@link #setTextInGThread(CharSequence)}
   *   are defined here to implement in the graphic layer. The set- and apppend methods are <b>threadsafe</b> now.
   * </ul>
   */
  @SuppressWarnings("hiding")
  public final static int version = 0x20120106;
  
  /**Buffer for new text which is set or appended in another thread than the graphic thread.
   * This buffer is empty if the graphic thread has processed the {@link GralDispatchCallbackWorker}
   * after calling {@link #append(CharSequence)} or {@link #setText(CharSequence)}.
   * It is filled only temporary.
   */
  private StringBuffer newText = new StringBuffer();
  
  protected GralTextBox(String name, char whatis, GralWidgetMng mng)
  { super(name, whatis, mng);
  }

  /**Sets the text to the widget, invoked only in the graphic thread.
   * This method have to be implemented in the Graphic implementation layer.
   * @param text The text which should be shown in the widget.
   */
  protected abstract void setTextInGThread(CharSequence text);
  
  /**Appends the text to the current text in the widget, invoked only in the graphic thread.
   * This method have to be implemented in the Graphic implementation layer.
   * @param text The text which should be appended and shown in the widget.
   */
  protected abstract void appendTextInGThread(CharSequence text);

  /**Append the text, able to call threadsafe in any thread.
   * If the thread is the graphic thread, the text will be appended to the current text
   * of the widget immediately. But if the thread is any other one, the text will be stored
   * in a StringBuilder and the graphic thread will be waked up with the {@link #appendTextViewTrail}
   * dispatch listener.
   * 
   * @see java.lang.Appendable#append(java.lang.CharSequence)
   */
  @Override public final Appendable append(CharSequence arg0) throws IOException
  { if(Thread.currentThread().getId() == windowMng.getThreadIdGui()){
      appendTextInGThread(arg0);
    } else {
      synchronized(newText){
        boolean hasText = newText.length() >0;
        newText.append(arg0);
        if(!hasText){  //elsewhere it is added already.
          windowMng.addDispatchOrder(appendTextViewTrail);
        }
      }
    }
    return this;
  }

  /**Append a single char, able to call threadsafe in any thread.
   * @see #append(CharSequence)
   * 
   * @see java.lang.Appendable#append(java.lang.CharSequence)
   */
  @Override public final Appendable append(char arg0) throws IOException
  { if(Thread.currentThread().getId() == windowMng.getThreadIdGui()){
      String ss = "" + arg0;
      appendTextInGThread(ss);
    } else {
      synchronized(newText){
        boolean hasText = newText.length() >0;
        newText.append(arg0);
        if(!hasText){  //elsewhere it is added already.
          windowMng.addDispatchOrder(appendTextViewTrail);
        }
      }
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

  /**Sets the text, able to call threadsafe in any thread.
   * If the thread is the graphic thread, the text will be set in the widget immediately. 
   * But if the thread is any other one, the text will be stored
   * in a StringBuilder and the graphic thread will be waked up with the {@link #setText}
   * dispatch listener.
   * 
   * @see org.vishia.gral.ifc.GralTextField_ifc#setText(java.lang.CharSequence)
   */
  @Override public final void setText(CharSequence arg)
  {
    if(Thread.currentThread().getId() == windowMng.getThreadIdGui()){
      setTextInGThread(arg);
    } else {
      synchronized(newText){
        newText.setLength(0);
        newText.append(arg);
        windowMng.addDispatchOrder(setText);
      }//synchronized
    }
  }

  /**Offers the method to append the stored text in the graphic thread.
   * The text is stored in {@link #newText}. The {@link #appendTextInGThread(CharSequence)}
   * is called with the content of {@link #newText}. The content of newText is delete then.
   * It is invoked under mutex.
   */
  private final GralDispatchCallbackWorker appendTextViewTrail = new GralDispatchCallbackWorker("GralTextBox.appendTextViewTrail")
  { @Override public final void doBeforeDispatching(boolean onlyWakeup)
    { synchronized(newText){
        if(newText.length() >0){
          appendTextInGThread(newText);
          viewTrail();
          newText.setLength(0);
        }
        windowMng.removeDispatchListener(this);
      }
    }
  };
  
  
  /**Offers the method to set the stored text in the graphic thread.
   * The text is stored in {@link #newText}. The {@link #setTextInGThread(CharSequence)}
   * is called with the content of {@link #newText}. The content of newText is delete then.
   * It is invoked under mutex.
   */
  private final GralDispatchCallbackWorker setText = new GralDispatchCallbackWorker("GralTextBox.setText")
  { @Override public final void doBeforeDispatching(boolean onlyWakeup)
    { synchronized(newText){
        if(newText.length() >0){
          setTextInGThread(newText);
          newText.setLength(0);
        }
        windowMng.removeDispatchListener(this);
      }
    }
  };
  
  

}
