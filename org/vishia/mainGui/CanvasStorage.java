package org.vishia.mainGui;

import java.util.concurrent.ConcurrentLinkedQueue;


/**The canvas storage is a storage, which stores orders to paint. This orders are painted
 * if the paint-routine from the windows-system is invoked. An application can call
 * forex the {@link #drawLine(Color, int, int, int, int)}-routine to draw a line. This line
 * will be drawn immediately, if the graphical window or widget gets the redraw-request.
 * Firstly the line will be stored only. 
 * @author Hartmut Schorrig
 *
 */
public abstract class CanvasStorage
{
	/**Data class to store an order.
	 */
	public static class PaintOrder
	{
		PaintOrder(int paintWhat, int x1, int y1, int x2, int y2, ColorGui color) {
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
		
		public final ColorGui color;
	}

	public final static int paintLine = 0xee;
	
	/**List of all orders to paint in {@link #drawBackground(GC, int, int, int, int)}.
	 * 
	 */
	public final ConcurrentLinkedQueue<PaintOrder> paintOrders = new ConcurrentLinkedQueue<PaintOrder>();
	
	
	
	/**Accepts a order to draw a line. The coordinates are stored only. 
	 * This method can be called in any thread. It is thread-safe.
	 * @param color
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void drawLine(ColorGui color, int x1, int y1, int x2, int y2){
		PaintOrder order = new PaintOrder(paintLine, x1,y1,x2,y2, color);
		paintOrders.add(order);  //paint it when drawBackground is invoked.
	}
	

	public abstract void redraw();
	
}
