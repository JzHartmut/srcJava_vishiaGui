package org.vishia.gral.ifc;

import java.util.concurrent.ConcurrentLinkedQueue;



/**The canvas storage is a storage, which stores orders to paint. This orders are painted
 * if the paint-routine from the windows-system is invoked. An application can call
 * forex the {@link #drawLine(Color, int, int, int, int)}-routine to draw a line. This line
 * will be drawn immediately, if the graphical window or widget gets the redraw-request.
 * Firstly the line will be stored only. 
 * @author Hartmut Schorrig
 *
 */
public class GralCanvasStorage implements GralCanvas_ifc
{
	/**Data class to store an order.
	 */
	public static class PaintOrder
	{
		PaintOrder(int paintWhat, int x1, int y1, int x2, int y2, GralColor color) {
			super();
			this.paintWhat = paintWhat;
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.color = color;
		}

		/**One of the static int of this class. Determines what to paint. */
		public final int paintWhat;
		
		/**Coordinates. */
		public final int x1,y1,x2,y2;
		
		public final GralColor color;
	}//class PaintOrder

	
	public static class PaintOrderImage extends PaintOrder
	{
	  public final GralImageBase image;
	  public final int dxImage, dyImage;
	  PaintOrderImage(GralImageBase image, int line, int column, int heigth, int width, GralRectangle pixelImage)
	  { super(paintImage, line, column, heigth, width, null);
	    this.image = image;
      this.dxImage = pixelImage.dx;
      this.dyImage = pixelImage.dy;
	  }
	}//class PaintOrderImage
	
	public final static int paintLine = 0xee, paintImage = 0x1ae;
	
	/**List of all orders to paint in {@link #drawBackground(GC, int, int, int, int)}.
	 * 
	 */
	public final ConcurrentLinkedQueue<PaintOrder> paintOrders = new ConcurrentLinkedQueue<PaintOrder>();
	
	
	
	/**Accepts a order to draw a line. The coordinates are stored only. 
	 * This method can be called in any thread. It is thread-safe.
	 * @param color
	 * @param x1 TODO yet it is pixel coordinates, use GralGrid coordinates.
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void drawLine(GralColor color, int x1, int y1, int x2, int y2){
		PaintOrder order = new PaintOrder(paintLine, x1,y1,x2,y2, color);
		paintOrders.add(order);  //paint it when drawBackground is invoked.
	}
	

	public void drawImage(GralImageBase image, int x, int y, int dx, int dy, GralRectangle imagePixelSize)
	{
    PaintOrder order = new PaintOrderImage(image, x, y, dx, dy, imagePixelSize);
    paintOrders.add(order);  //paint it when drawBackground is invoked.
	}


  @Override
  public void drawText(String text)
  {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void setTextStyle(GralColor color, GralFont font, int origin)
  {
    // TODO Auto-generated method stub
    
  }
	
	
	//public abstract void redraw();
	
}
