package jpixeleditor.tools;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

import jpixeleditor.ui.Panel;

public class Helper
{
	public static float map(float val, float start1, float stop1, float start2, float stop2)
	{
		return start2 + (stop2 - start2) * ((val - start1) / (stop1 - start1));
	}
	
	public static Rectangle constrainRect(Rectangle container, Rectangle rect)
	{
		if(rect.x < container.x)
		{
			rect.x = container.x;
		}
		if(rect.y < container.y)
		{
			rect.y = container.y;
		}
		if(rect.width + rect.x > container.width + container.x)
		{
			rect.width = container.width + container.x - rect.x;
		}
		if(rect.height + rect.y > container.height + container.y)
		{
			rect.height = container.height + container.y - rect.y;
		}
		return rect;
	}
	
	public static float constrain(float val, float min, float max)
	{
		if(val < min)
		{
			val = min;
		}
		if(val > max)
		{
			val = max;
		}
		return val;
	}
	
    public static float getMax(float... num) // Basically like params in C#. I didn't know Java had this!
    {
    	float max = num[0];
    	for(int i = 0; i < num.length; i++)
    	{
    		if(num[i] > max)
    		{
    			max = num[i];
    		}
    	}
    	return max;
    }
    
    public static float getMin(float... num) // Basically like params in C#. I didn't know Java had this!
    {
    	float min = num[0];
    	for(int i = 0; i < num.length; i++)
    	{
    		if(num[i] < min)
    		{
    			min = num[i];
    		}
    	}
    	return min;
    }
    
    public static double getMax(double... num) // Basically like params in C#. I didn't know Java had this!
    {
    	double max = num[0];
    	for(int i = 0; i < num.length; i++)
    	{
    		if(num[i] > max)
    		{
    			max = num[i];
    		}
    	}
    	return max;
    }
    
    public static double getMin(double... num) // Basically like params in C#. I didn't know Java had this!
    {
    	double min = num[0];
    	for(int i = 0; i < num.length; i++)
    	{
    		if(num[i] < min)
    		{
    			min = num[i];
    		}
    	}
    	return min;
    }
    
    public static boolean hasNonZeroValue(int[][] arr)
    {
    	for(int i = 0; i < arr.length; i++)
    	{
    		for(int j = 0; j < arr[i].length; j++)
    		{
    			if(arr[i][j] != 0)
    			{
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    public static <T> T[] concatArrays(T[] arr1, T[] arr2)
    {
    	T[] both = Arrays.copyOf(arr1, arr1.length + arr2.length);
    	System.arraycopy(arr2, 0, both, arr1.length, arr2.length);
    	return both;
    }
    
    /**
     * Finds an array of points on a line using Bresenham's Line Algorithm. Wiki: https://en.wikipedia.org/wiki/Bresenham's_line_algorithm
     * @param gridStart
     * @param gridEnd
     * @return A Point[] representing the points on the line from gridStart to gridEnd
     */
    public static Point[] plotLine(Point gridStart, Point gridEnd, boolean uniformLine)
    {
    	ArrayList<Point> points = new ArrayList<Point>();
    	
    	if(!uniformLine)
    	{
	    	int x0 = gridStart.x;
	    	int y0 = gridStart.y;
	    	int x1 = gridEnd.x;
	    	int y1 = gridEnd.y;
	    	
	    	float dx = Math.abs(x1 - x0);
	        float sx = x0 < x1 ? 1 : -1;
	        float dy = -Math.abs(y1 - y0);
	        float sy = y0 < y1 ? 1 : -1;
	        float err = dx + dy;  // error value e_xy
	        
	        // System.out.println("dx: " + dx + " sx: " + sx + " dy: " + dy + " sy: " + sy + "err: " + err);
	        while (true)   // loop
	        {
	            points.add(new Point(x0, y0));//plot(x0, y0);
	            if (x0 == x1 && y0 == y1)
	            	break;
	            
	            float e2 = 2 * err;
	            if (e2 >= dy)
	            {
	                err += dy; // e_xy+e_x > 0
	                x0 += sx;
	            }
	            if (e2 <= dx) // e_xy+e_y < 0
	            {
	                err += dx;
	                y0 += sy;
	            }
	        }
	    	
	    	Point[] pointsArr = points.toArray(new Point[points.size()]);
	    	
	    	return pointsArr;
    	}
    	else
    	{
    		// This was taken from Piskel's source code
    		
    		int x0 = gridStart.x;
	    	int y0 = gridStart.y;
	    	int x1 = gridEnd.x;
	    	int y1 = gridEnd.y;
	    	
	        float dx = Math.abs(x1 - x0) + 1;
	        float dy = Math.abs(y1 - y0) + 1;

	        float sx = (x0 < x1) ? 1 : -1;
	        float sy = (y0 < y1) ? 1 : -1;

	        float ratio = Math.max(dx, dy) / Math.min(dx, dy);
	        
	        int pixelStep = Math.round(ratio);// || 0; Absolutely NO idea what '|| 0' is but it was in piskel's source code. || is OR in JS. It must just be a JS oddity. It seems to have no effect anyway

	        if (pixelStep > Math.min(dx, dy)) {
	          pixelStep = (int)Double.POSITIVE_INFINITY;
	        }

	        float maxDistance = (float)Point2D.distance(x0, y0, x1, y1);

	        int x = x0;
	        int y = y0;
	        int i = 0;
	        while (true)
	        {
	          i++;

	          points.add(new Point(x, y));
	          if (Point2D.distance(x0, y0, x, y) >= maxDistance)
	          {
	            break;
	          }

	          boolean isAtStep = i % pixelStep == 0; // Apparently you can use 'var' to declare variables in Java??! I prefer not to though
	          if (dx >= dy || isAtStep)
	          {
	            x += sx;
	          }
	          if (dy >= dx || isAtStep)
	          {
	            y += sy;
	          }
	        }
	    	
	    	Point[] pointsArr = points.toArray(new Point[points.size()]);
	    	
	    	return pointsArr;
    	}
    }
    
    /*
     * getUniformLinePixels : function (x0, x1, y0, y1) {
      var pixels = [];

      x1 = pskl.utils.normalize(x1, 0);
      y1 = pskl.utils.normalize(y1, 0);

      var dx = Math.abs(x1 - x0) + 1;
      var dy = Math.abs(y1 - y0) + 1;

      var sx = (x0 < x1) ? 1 : -1;
      var sy = (y0 < y1) ? 1 : -1;

      var ratio = Math.max(dx, dy) / Math.min(dx, dy);
      // in pixel art, lines should use uniform number of pixels for each step
      var pixelStep = Math.round(ratio) || 0;

      if (pixelStep > Math.min(dx, dy)) {
        pixelStep = Infinity;
      }

      var maxDistance = pskl.utils.Math.distance(x0, x1, y0, y1);

      var x = x0;
      var y = y0;
      var i = 0;
      while (true) {
        i++;

        pixels.push({'col': x, 'row': y});
        if (pskl.utils.Math.distance(x0, x, y0, y) >= maxDistance) {
          break;
        }

        var isAtStep = i % pixelStep === 0;
        if (dx >= dy || isAtStep) {
          x += sx;
        }
        if (dy >= dx || isAtStep) {
          y += sy;
        }
      }

      return pixels;
    }
    
    a.normalize = function(a,b)
    {
    	return void 0 === a || null === a ? b : a
    }
     */
    
    public static float sq(float val)
    {
    	return val * val;
    }
    
    public static Point[] circleInBounds(int i, int j, int width, int height, boolean fill)
    {
    	ArrayList<Point> points = new ArrayList<Point>();
    	
    	int x0 = i;
		int y0 = j;
		int x1 = i + width;
		int y1 = j + height;
		
		int xb, yb, xc, yc;

		// Calculate height
		yb = yc = (y0 + y1) / 2;
		int qb = (y0 < y1) ? (y1 - y0) : (y0 - y1);
		int qy = qb;
		int dy = qb / 2;
		if (qb % 2 != 0)
			// Bounding box has even pixel height
			yc++;

		// Calculate width
		xb = xc = (x0 + x1) / 2;
		int qa = (x0 < x1) ? (x1 - x0) : (x0 - x1);
		int qx = qa % 2;
		int dx = 0;
		long qt = (long)qa * qa + (long)qb * qb - 2L * qa * qa * qb;
		if (qx != 0)
		{
			// Bounding box has even pixel width
			xc++;
			qt += 3L*qb*qb;
		}

		// Start at (dx, dy) = (0, b) and iterate until (a, 0) is reached
		while (qy >= 0 && qx <= qa)
		{
			// Draw the new points
			if (!fill)
			{
				//drawPoint(xb-dx, yb-dy);
				//paint(xb-dx, yb - dy, colour, onOverlay, brushSize, false);
				points.add(new Point(xb - dx, yb - dy));
				if (dx != 0 || xb != xc)
				{
					//drawPoint(xc+dx, yb-dy);
					//paint(xc + dx, yb - dy, colour, onOverlay, brushSize, false);
					points.add(new Point(xc + dx, yb - dy));
					if (dy != 0 || yb != yc)
					{
						//drawPoint(xc+dx, yc+dy);
						//paint(xc + dx, yc + dy, colour, onOverlay, brushSize, false);
						points.add(new Point(xc + dx, yc + dy));
					}
				}
				if (dy != 0 || yb != yc)
				{
					//drawPoint(xb-dx, yc+dy);
					//paint(xb - dx, yc + dy, colour, onOverlay, brushSize, false);
					points.add(new Point(xb - dx, yc + dy));
				}
			}

			// If a (+1, 0) step stays inside the ellipse, do it
			if (qt + 2L * qb * qb * qx + 3L * qb * qb <= 0L || qt + 2L * qa * qa * qy - (long)qa * qa <= 0L)
			{
				qt += 8L* qb * qb + 4L * qb * qb * qx;
				dx++;
				qx += 2;
				
			}// If a (0, -1) step stays outside the ellipse, do it
			else if (qt - 2L * qa * qa * qy + 3L * qa * qa > 0L)
			{
				if (fill)
				{
					//drawRow(xb - dx, xc + dx, yc + dy, colour, onOverlay);
					points.addAll(getRow(xb - dx, xc + dx, yc + dy));
					if (dy != 0 || yb != yc)
					{
						//drawRow(xb - dx, xc + dx, yb - dy, colour, onOverlay);
						points.addAll(getRow(xb - dx, xc + dx, yb - dy));
					}
				}
				qt += 8L * qa * qa - 4L * qa * qa * qy;
				dy--;
				qy -= 2;
			}// Else step (+1, -1)
			else
			{
				if (fill)
				{
					//drawRow(xb - dx, xc + dx, yc + dy, colour, onOverlay);
					points.addAll(getRow(xb - dx, xc + dx, yc + dy));
					if (dy != 0 || yb != yc)
					{
						//drawRow(xb - dx, xc + dx, yb - dy, colour, onOverlay);
						points.addAll(getRow(xb - dx, xc + dx, yb - dy));
					}
				}
				qt += 8L * qb * qb + 4L * qb * qb * qx + 8L * qa * qa - 4L * qa * qa * qy;
				dx++;
				qx += 2;
				dy--;
				qy -= 2;
			}
		}// End of while loop
    	return points.toArray(new Point[points.size()]);
    }
    
    public static ArrayList<Point> getRow(int startx, int endx, int y)
    {
    	ArrayList<Point> points = new ArrayList<Point>();
    	
    	int startX = Math.min(startx, endx);
    	int endX = Math.max(startx, endx);
    	
    	for(int i = startX; i <= endX; i++)
    	{
    		points.add(new Point(i, y));
    	}
    	
    	return points;
    }
    
    public static String arrayAsCommaSeparatedList(String[] arr)
    {
    	StringBuilder sb = new StringBuilder();
    	
    	for(int i = 0; i < arr.length; i++)
    	{
    		sb.append(arr[i]);
    		
    		if(i < arr.length - 1)
    		{
    			sb.append(", ");
    		}
    	}
    	
    	return sb.toString();
    }
    
    public static void printMatrix(boolean[][] m)
    {
    	System.out.println("Matrix:");
    	for(int i = 0; i < m.length; i++)
    	{
    		for(int j = 0; j < m[i].length; j++)
    		{
    			System.out.print(" ");
    			System.out.print(m[j][i] ? 1 : 0);
    			System.out.print(" ");
    			if(j == m[i].length - 1)
    			{
    				System.out.println();
    			}
    		}
    	}
    	System.out.println("Matrix printed");
    }
    
    public static Point[] getPoints(Rectangle rect)
    {
    	Point[] points = { new Point(rect.x, rect.y),
    						new Point(rect.x + rect.width, rect.y),
    						new Point(rect.x, rect.y + rect.height),
    						new Point(rect.x + rect.width, rect.y + rect.height) };
    	return points;
    }
    
    public static Point2D.Float[] getPoints2D(Rectangle rect)
    {
    	Point2D.Float[] points = { new Point2D.Float(rect.x, rect.y),
    			new Point2D.Float(rect.x + rect.width, rect.y),
    			new Point2D.Float(rect.x, rect.y + rect.height),
    			new Point2D.Float(rect.x + rect.width, rect.y + rect.height) };
    	return points;
    }
    
    public static Rectangle constructRect(Point[] points)
    {
    	if(points.length != 4)
    	{
    		throw new IllegalArgumentException("Cannot construct a rectangle with more or less than 4 points");
    	}
    	
    	int x = (int)getMin(points[0].x, points[1].x, points[2].x, points[3].x);
    	int y = (int)getMin(points[0].y, points[1].y, points[2].y, points[3].y);
    	
    	int endX = (int)getMax(points[0].x, points[1].x, points[2].x, points[3].x);
    	int endY = (int)getMax(points[0].y, points[1].y, points[2].y, points[3].y);
    	
    	int width = endX - x;
    	int height = endY - y;
    	
    	return new Rectangle(x, y, width, height);
    }
    
    public static Rectangle constructRect(Point2D.Float[] points)
    {
    	if(points.length != 4)
    	{
    		throw new IllegalArgumentException("Cannot construct a rectangle with more or less than 4 points");
    	}
    	
    	int x = (int)getMin(points[0].getX(), points[1].getX(), points[2].getX(), points[3].getX());
    	int y = (int)getMin(points[0].getY(), points[1].getY(), points[2].getY(), points[3].getY());
    	
    	int endX = (int)getMax(points[0].getX(), points[1].getX(), points[2].getX(), points[3].getX());
    	int endY = (int)getMax(points[0].getY(), points[1].getY(), points[2].getY(), points[3].getY());
    	
    	int width = endX - x;
    	int height = endY - y;
    	
    	return new Rectangle(x, y, width, height);
    }
    
    public static float lerp(float start, float stop, float amt)
	{
		return start + (stop - start) * amt;
	}
    
    public static Point centre(Rectangle rect)
    {
    	return new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
    }
    
    /**
     * Scales a rectangle about point (x, y)
     * @param rect
     * @param x
     * @param y
     * @param scale
     * @return The rectangle scaled by scale about point (x, y)
     */
	public static Rectangle scaleRect(Rectangle rect, int x, int y, float scale)
	{
		Point origin = new Point(x, y);
		
		Point[] points = Helper.getPoints(rect);
		for(int i = 0; i < points.length; i++)
		{
			Vector2 toPt = new Vector2(points[i].x - origin.x, points[i].y - origin.y);
			toPt.mult(scale);
			
			points[i].x = (int)toPt.x + origin.x;
			points[i].y = (int)toPt.y + origin.y;
		}
		Rectangle res = Helper.constructRect(points);
		return res;
	}
	
	public static Point lerp(Point start, Point end, float amt)
	{
		return new Point
		(
				(int)lerp(start.x, end.x, amt),
				(int)lerp(start.y, end.y, amt)
		);
	}
	
	public static float bestScale(Rectangle container, Rectangle rect)
	{
		// Calculate the separate scale factors for width and height
		double sfWidth = (double)container.width / (double)rect.width;
		double sfHeight = (double)container.height / (double)rect.height;
		
		// Get the minimum scale factor. This is the one we will scale by
		double scale = Math.min(sfWidth, sfHeight);
		
		return (float)scale;
	}
	
	// Compare matrices - return true if they are all the same, false if else
	public static boolean compareMatrices(int[][] m1, int[][] m2)
	{
		if(m1.length != m2.length)
		{
			return false;
		}
		
		for(int i = 0; i < m1.length; i++)
		{
			if(m1[i].length != m2[i].length)
			{
				return false;
			}
			
			for(int j = 0; j < m1[i].length; j++)
			{
				if(m1[i][j] != m2[i][j])
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static Point constrainTo(Point p, Rectangle rect)
	{
		if(p.x < rect.x)
		{
			p.x = rect.x;
		}
		else if(p.x > rect.x + rect.width)
		{
			p.x = rect.x + rect.width;
		}
		if(p.y < rect.y)
		{
			p.y = rect.y;
		}
		else if(p.y > rect.y + rect.height)
		{
			p.y = rect.y + rect.height;
		}
		return p;
	}
	
	/**
	 * Converts an Integer into a String representation of the Integer in hexadecimal
	 * @param num
	 * @param fixedStringLen - The length of the output string. If the length of the hex equivalent of num is shorter than this, then it adds zeros on to the start of the output String. If it is less, then it returns the string unchanged
	 * @return The hexadecimal representation of num in the form of a String
	 */
	public static String toHexString(int num, int fixedStringLen)
	{
		String hex = Integer.toHexString(num);
		
		if(hex.length() < fixedStringLen)
		{
			int zeroPaddingLen = fixedStringLen - hex.length();
			hex = repeatString("0", zeroPaddingLen) + hex;
		}
		return hex;
	}
	
	public static String repeatString(String str, int count)
	{
	    StringBuilder res = new StringBuilder();
	    for (int i = 0; i < count; i++)
	    {
	        res.append(str);
	    }
	    return res.toString();
	}

	public static int hexToInt(String hex, int fixedStringLen)
	{
		if(hex.length() < fixedStringLen) // If the hex thing is shorter than fixedStringLen, then add the neccessary number of zeros to the end of it
		{
			int zeroPaddingLen = fixedStringLen - hex.length();
			hex = hex + repeatString("0", zeroPaddingLen);
		}
		
		int res = Integer.parseUnsignedInt(hex, 16); // Need to parse it as an unsigned integer, or Errors galore
		
		return res;
	}
	
	/**
	 * Splits the string every time a '\n' appears, into multiple JLabels (with the text coloured according to {@code labelCol}) it then lays out in separate lines in a Panel, and are horizontally aligned according to {@code alignmentX}. This is basically something to make '\n' work in JLabels
	 * @param str
	 * @param alignmentX Can be any value between 0 and 1, can use predefinitions in Component. (Example: Component.LEFT_ALIGNMENT)
	 * @param labelCol
	 * @param panelCol
	 * @return A Panel component with the colour specified in {@code panelCol}, containing the necessary JLabels layed out vertically
	 */
	public static Panel splitIntoLabels(String str, float alignmentX, int labelCol, int panelCol) // It works! Not that I wasn't expecting that...
	{
		String[] strs = str.split("\n");
		
		Panel container = new Panel(panelCol);
		
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		for(int i = 0; i < strs.length; i++)
		{
			JLabel label = new JLabel(strs[i]);
			label.setForeground(Colour.toAWTColor(labelCol));
			label.setAlignmentX(alignmentX);
			container.add(label);
		}
		
		return container;
	}
	
	public static boolean[][] copyMatrix(boolean[][] src, boolean[][] dest)
	{
		if(src.length != dest.length)
		{
			throw new IllegalArgumentException("Matrices must have equal dimensions");
		}
		
		for(int i = 0; i < src.length; i++)
		{
			if(src[i].length != dest[i].length)
			{
				throw new IllegalArgumentException("Matrices must have equal dimensions");
			}
			
			for(int j = 0; j < src[i].length; j++)
			{
				dest[i][j] = src[i][j];
			}
		}
		
		return dest;
	}

	public static Integer[][] copyMatrix(Integer[][] src, Integer[][] dest)
	{
		if(src.length != dest.length)
		{
			throw new IllegalArgumentException("Matrices must have equal dimensions");
		}
		
		for(int i = 0; i < src.length; i++)
		{
			if(src[i].length != dest[i].length)
			{
				throw new IllegalArgumentException("Matrices must have equal dimensions");
			}
			
			for(int j = 0; j < src[i].length; j++)
			{
				dest[i][j] = src[i][j];
			}
		}
		
		return dest;
	}
	
	public static boolean isNextTo(Point p1, Point p2)
	{
//		boolean samePlace = p1.x == p2.x && p1.y == p2.y;
//		if(samePlace)
//			return true;
		boolean upOne = p1.x == p2.x && p1.y - 1 == p2.y;
		if(upOne)
			return true;
		boolean downOne = p1.x == p2.x && p1.y + 1 == p2.y;
		if(downOne)
			return true;
		boolean leftOne = p1.x - 1 == p2.x && p1.y == p2.y;
		if(leftOne)
			return true;
		boolean rightOne = p1.x + 1 == p2.x && p1.y == p2.y;
		
		return rightOne;
	}
	
	interface OutOfBoundsAction
	{
		abstract void hasPoint(Point p);
	}
	
	/**
	 * Plots a line from start to end, if a point on that line is outside the constraints, it constrains it
	 * @param start
	 * @param end
	 * @param constraints
	 * @return An array of points on the line from start to end
	 */
	public static Point[] plotLine_InsideConstraints(Point start, Point end, Rectangle constraints)
	{
		ArrayList<Point> points = new ArrayList<Point>();

		int x0 = start.x;
		int y0 = start.y;
		int x1 = end.x;
		int y1 = end.y;

		float dx = Math.abs(x1 - x0);
		float sx = x0 < x1 ? 1 : -1;
		float dy = -Math.abs(y1 - y0);
		float sy = y0 < y1 ? 1 : -1;
		float err = dx + dy;  // error value e_xy

		// System.out.println("dx: " + dx + " sx: " + sx + " dy: " + dy + " sy: " + sy + "err: " + err);
		while (true)   // loop
		{
			Point p = new Point(x0, y0);
			if(constraints.contains(p))
				points.add(p);
			
			if (x0 == x1 && y0 == y1)
				break;

			float e2 = 2 * err;
			if (e2 >= dy)
			{
				err += dy; // e_xy+e_x > 0
				x0 += sx;
			}
			if (e2 <= dx) // e_xy+e_y < 0
			{
				err += dx;
				y0 += sy;
			}
		}

		Point[] pointsArr = points.toArray(new Point[points.size()]);

		return pointsArr;
	}
	
	/**
	 * Used with {@link #getGroupingInfo}, for the lasso select tool. Stores information about groups within a Matrix
	 * @author william-banks
	 *
	 */
	public static class Group
	{
		// These will store the number of pixels a group is connected to above and below it
		int numAbove = -1;
		int numBelow = -1;
		
		// The start and end indexes, inclusive. Will actually be either side of where the group starts and ends
		int startIndex = -1;
		int endIndex = -1;
		
		// These booleans will store if the group has any connected pixels above/below it. Don't really need the count usually TODO: Add the option to not count to relevant methods
		boolean hasAbove = false;
		boolean hasBelow = false;
		
		public Group(int start)
		{
			startIndex = start;
		}
		
		@Override public String toString()
		{
			return ("GROUP: " + "NumAbove: " + numAbove + " NumBelow: " + numBelow + " start: " + startIndex + " end: " + endIndex);
		}
	}
	
	/**
	 * Should now be correct. Returns an array of ArrayLists of type Group
	 * @param selected
	 * @param doCount - Whether this will bother to count the number of pixels above and below a group - If false, will leave them at the default values and set the booleans instead
	 * @return One ArrayList for each row in the input matrix, with an ArrayList of Groups detailing information about each group
	 */
	public static ArrayList<Group>[] getGroupingInfo(boolean[][] selected, boolean doCount)
	{
		@SuppressWarnings("unchecked")
		ArrayList<Group>[] groupings = (ArrayList<Group>[])Array.newInstance(new ArrayList<Group>().getClass(), selected.length);
		
		for(int i = 0; i < selected.length; i++)
		{
			ArrayList<Group> groups = new ArrayList<Group>();
			boolean inGroup = false;
			boolean prevInGroup = false;
			int currGroupIndex = -1;
			
			for(int j = 0; j < selected[i].length + 1; j++)
			{
				// If the i is out of bounds of selected, then this is false, otherwise, the value at the position (i, j)
				inGroup = (j < selected[i].length) ? selected[i][j] : false;
				
				if(inGroup && !prevInGroup)
				{
					// If we've entered a new group, add 1 onto the currGroupIndex and add a Group onto the groups ArrayList, with start position at j - 1
					currGroupIndex++;
					groups.add(new Group(j - 1));
				}
				else if(!inGroup && prevInGroup)
				{
					// If we've just exited a group, then the end index of the current group will be j
					groups.get(currGroupIndex).endIndex = j;
					
					int start = groups.get(currGroupIndex).startIndex;
					int end = j;
					
					if(doCount)
					{
						int aboveCount = 0;
						int belowCount = 0;
						
						// Loop through the start to end of the current group, looking at pixels above and below each index along the way, and if they are true, adds 1 onto aboveCount or belowCount
						for(int c = start; c <= end; c++)
						{
							if(c >= 0 && c < selected[i].length)
							{
								if(i - 1 >= 0 && i - 1 < selected.length)
								{
									aboveCount += selected[i - 1][c] ? 1 : 0;
								}
								if(i + 1 >= 0 && i + 1 < selected.length)
								{
									belowCount += selected[i + 1][c] ? 1 : 0;
								}
							}
						}
						
						// Set the numBelow and numAbove of the current group to the values just found
						groups.get(currGroupIndex).numAbove = aboveCount;
						groups.get(currGroupIndex).numBelow = belowCount;
					}
					else
					{
						boolean hasAbove = false;
						boolean hasBelow = false;
						
						// Loop through the start to end of the current group, looking at pixels above and below each index along the way, and if they are true, sets hasAbove or hasBelow to true
						// If hasAbove and hasBelow are both true, then the loop breaks
						for(int c = start; c <= end; c++)
						{
							if(c >= 0 && c < selected[i].length)
							{
								if(i - 1 >= 0 && i - 1 < selected.length)
								{
									hasAbove = hasAbove || selected[i - 1][c];
									if(hasAbove && hasBelow)
										break;
								}
								if(i + 1 >= 0 && i + 1 < selected.length)
								{
									hasBelow = hasBelow || selected[i + 1][c];
									if(hasAbove && hasBelow)
										break;
								}
							}
						}
						
						// Set the hasAbove and hasBelow to the booleans just found
						groups.get(currGroupIndex).hasAbove = hasAbove;
						groups.get(currGroupIndex).hasBelow = hasBelow;
					}
				}
				
				prevInGroup = inGroup;
			}
			// System.out.println(groups);
			groupings[i] = groups;
		}
		return groupings;
	}
	
	public static ArrayList<Group> getGroupingInfo(boolean[][] selected, int i, boolean doCount)
	{
		ArrayList<Group> groups = new ArrayList<Group>();
		boolean inGroup = false;
		boolean prevInGroup = false;
		int currGroupIndex = -1;
		
		for(int j = 0; j < selected[i].length + 1; j++)
		{
			// If the i is out of bounds of selected, then this is false, otherwise, the value at the position (i, j)
			inGroup = (j < selected[i].length) ? selected[i][j] : false;
			
			if(inGroup && !prevInGroup)
			{
				// If we've entered a new group, add 1 onto the currGroupIndex and add a Group onto the groups ArrayList, with start position at j - 1
				currGroupIndex++;
				groups.add(new Group(j - 1));
			}
			else if(!inGroup && prevInGroup)
			{
				// If we've just exited a group, then the end index of the current group will be j
				groups.get(currGroupIndex).endIndex = j;
				
//				int start = groups.get(currGroupIndex).startIndex;
//				int end = j;
//				
//				if(doCount)
//				{
//					int aboveCount = 0;
//					int belowCount = 0;
//					
//					// Loop through the start to end of the current group, looking at pixels above and below each index along the way, and if they are true, adds 1 onto aboveCount or belowCount
//					for(int c = start; c <= end; c++)
//					{
//						if(c >= 0 && c < selected[i].length)
//						{
//							if(i - 1 >= 0 && i - 1 < selected.length)
//							{
//								aboveCount += selected[i - 1][c] ? 1 : 0;
//							}
//							if(i + 1 >= 0 && i + 1 < selected.length)
//							{
//								belowCount += selected[i + 1][c] ? 1 : 0;
//							}
//						}
//					}
//					
//					// Set the numBelow and numAbove of the current group to the values just found
//					groups.get(currGroupIndex).numAbove = aboveCount;
//					groups.get(currGroupIndex).numBelow = belowCount;
//				}
//				else
//				{
//					boolean hasAbove = false;
//					boolean hasBelow = false;
//					
//					// Loop through the start to end of the current group, looking at pixels above and below each index along the way, and if they are true, sets hasAbove or hasBelow to true
//					// If hasAbove and hasBelow are both true, then the loop breaks
//					for(int c = start; c <= end; c++)
//					{
//						if(c >= 0 && c < selected[i].length)
//						{
//							if(i - 1 >= 0 && i - 1 < selected.length)
//							{
//								hasAbove = hasAbove || selected[i - 1][c];
//								if(hasAbove && hasBelow)
//									break;
//							}
//							if(i + 1 >= 0 && i + 1 < selected.length)
//							{
//								hasBelow = hasBelow || selected[i + 1][c];
//								if(hasAbove && hasBelow)
//									break;
//							}
//						}
//					}
//					
//					// Set the hasAbove and hasBelow to the booleans just found
//					groups.get(currGroupIndex).hasAbove = hasAbove;
//					groups.get(currGroupIndex).hasBelow = hasBelow;
//				}
			}
			
			prevInGroup = inGroup;
		}
		return groups;
	}
	
	public static void drawPoint(int[] imgData, int index, int col, boolean calcComposite)
	{
		if(calcComposite)
			imgData[index] = Colour.composite(imgData[index], col);
		else
			imgData[index] = col;
	}
}