
package org.vishia.guiViewCfg ;

import org.vishia.byteData.*;


public class MsgItems_h
{



  public static class MsgItem extends ByteDataAccess
  {
    
    protected static final int kSizevalues = 4;
    

    /**Index of the data element*/
    public static final int
      kIdxtimestamp = 0
      , kIdxtimeMillisec = 0 + 4
      , kIdxmode_typeVal = 0 + 4 + 2
      , kIdxident = 0 + 4 + 2 + 2
      , kIdxvalues = 0 + 4 + 2 + 2 + 4
      , kIdxAfterLast = 0 + 4 + 2 + 2 + 4 + 4 * 4;
    ; /*xsl: all Data from struct in headerfile converted to position indices */

    
    
    

    
      
      
      
    /** Constructs the data management class*/
    public MsgItem()
    { 
    }

    /** Constructs as a child inside another ByteDataAccess*/
    public MsgItem(ByteDataAccess parent, int idxChildInParent)
    { try{ assignAtIndex(idxChildInParent, parent); }
      catch(IllegalArgumentException exc)
      { //it won't be have any exception because specifyLengthElement() inside this class is the only source for it.
      }
    }
    



    public int specifyLengthElementHead()
    { return kIdxAfterLast;  //NOTE all are head bytes, no dynamic!
    }

    protected void specifyEmptyDefaultData()
    {
      for(int ii=idxBegin; ii < idxEnd; ii++)
      { super.data[ii] = 0;
      }
    }

    protected int specifyLengthElement() throws IllegalArgumentException
    {
      return kIdxAfterLast;
    }

        
	    public void set_timestamp(int val)
	    { //type of struct-attribut is int32
	      setInt32(kIdxtimestamp, val);
	    }
	      
      public int get_timestamp()
      { //type of struct-attribut is int32
        return getInt32(kIdxtimestamp);
      }
        
	    public void set_timeMillisec(short val)
	    { //type of struct-attribut is int16
	      setInt16(kIdxtimeMillisec, val);
	    }
	      
      public short get_timeMillisec()
      { //type of struct-attribut is int16
        return (short)getInt16(kIdxtimeMillisec);
      }
        
	    public void set_mode_typeVal(short val)
	    { //type of struct-attribut is int16
	      setInt16(kIdxmode_typeVal, val);
	    }
	      
      public short get_mode_typeVal()
      { //type of struct-attribut is int16
        return (short)getInt16(kIdxmode_typeVal);
      }
        
	    public void set_ident(int val)
	    { //type of struct-attribut is int32
	      setInt32(kIdxident, val);
	    }
	      
      public int get_ident()
      { //type of struct-attribut is int32
        return getInt32(kIdxident);
      }
        
	    public void set_values(int val, int idx)
	    { //type of struct-attribut is int32
	      setInt32(kIdxvalues, idx, 4, val);
	    }
	      
      public int get_values(int idx)
      { //type of struct-attribut is int32
        return getInt32(kIdxvalues, idx, 4);
      }
        
      public final static int size_values = 4;
          
      /**Because the method has fix childs, the assignDataToFixChilds method is overridden to apply to all fix childs. */
      @Override protected void assignDataToFixChilds() throws IllegalArgumentException
      {
      
      }

      
  }




  public static class MsgItems extends ByteDataAccess
  {
    
    protected static final int kSizemsgItems = 20;
    

    /**Index of the data element*/
    public static final int
      kIdxfileHead = 0
      , kIdxnrofMsg = 0 
      , kIdxdummy = 0 + 2
      , kIdxmsgItems = 0 + 2 + 2
      , kIdxAfterLast = 0 + 2 + 2 + MsgItem.kIdxAfterLast * 20;
    ; /*xsl: all Data from struct in headerfile converted to position indices */

    
    
    

    
      
      
      
    /** Constructs the data management class*/
    public MsgItems()
    { 
    }

    /** Constructs as a child inside another ByteDataAccess*/
    public MsgItems(ByteDataAccess parent, int idxChildInParent)
    { try{ assignAtIndex(idxChildInParent, parent); }
      catch(IllegalArgumentException exc)
      { //it won't be have any exception because specifyLengthElement() inside this class is the only source for it.
      }
    }
    



    public int specifyLengthElementHead()
    { return kIdxAfterLast;  //NOTE all are head bytes, no dynamic!
    }

    protected void specifyEmptyDefaultData()
    {
      for(int ii=idxBegin; ii < idxEnd; ii++)
      { super.data[ii] = 0;
      }
    }

    protected int specifyLengthElement() throws IllegalArgumentException
    {
      return kIdxAfterLast;
    }

        
        
	      //note: method to set fileHead, not able to generate.
	        
      //note: method to set fileHead, not able to generate.
        
	    public void set_nrofMsg(short val)
	    { //type of struct-attribut is int16
	      setInt16(kIdxnrofMsg, val);
	    }
	      
      public short get_nrofMsg()
      { //type of struct-attribut is int16
        return (short)getInt16(kIdxnrofMsg);
      }
        
	    public void set_dummy(short val)
	    { //type of struct-attribut is int16
	      setInt16(kIdxdummy, val);
	    }
	      
      public short get_dummy()
      { //type of struct-attribut is int16
        return (short)getInt16(kIdxdummy);
      }
        
    public final MsgItem msgItems = new MsgItem(); //(this, kIdxmsgItems);  //embedded structure
        
	      //note: method to set msgItems, not able to generate.
	        
      //note: method to set msgItems, not able to generate.
        
      /**Because the method has fix childs, the assignDataToFixChilds method is overridden to apply to all fix childs. */
      @Override protected void assignDataToFixChilds() throws IllegalArgumentException
      {
      
        //NOTE: use super.data etc to prevent false using of a local element data. super is ByteDataAccess.    
        //NOTE: use super.data etc to prevent false using of a local element data. super is ByteDataAccess.    
        msgItems.assignData(super.data, super.idxEnd, super.idxBegin + kIdxmsgItems);  //embedded structure
        msgItems.setBigEndian(super.bBigEndian);
      }

      /**Because the method has fix childs, the setBigEndian method is overridden to apply the endian to all fix childs. */
      @Override public void setBigEndian(boolean val)
      { super.setBigEndian(val);
      
          
        msgItems.setBigEndian(val);  //embedded structure
          
      }
      
  }


}
