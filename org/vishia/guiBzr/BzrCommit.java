package org.vishia.guiBzr;


import org.vishia.mainCmd.MainCmd_ifc;

public class BzrCommit
{
  final MainData mainData;
  
  final MainCmd_ifc mainCmdifc;
  
  public BzrCommit(MainCmd_ifc mainCmdifc, MainData mainData)
  { this.mainCmdifc = mainCmdifc;
    this.mainData = mainData;
  }



}
