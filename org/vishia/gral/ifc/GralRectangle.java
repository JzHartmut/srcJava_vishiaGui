package org.vishia.gral.ifc;

public class GralRectangle
{
  public int x,y,dx,dy;

  public GralRectangle(int x, int y, int dx, int dy)
  { this.x = x; this.y = y; this.dx = dx; this.dy = dy;
  }
  
  @Override public String toString(){
    return "GralRectangle(" + x + " + " + dx + ", " + y + " + " + dy + ")";
  }
}
