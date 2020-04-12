package jpixeleditor.utils;

import java.awt.Point;
import java.util.ArrayList;

import jpixeleditor.ui.Canvas;
import jpixeleditor.ui.CanvasContainer;
import jpixeleditor.ui.Canvas.DrawingSurface;
import jpixeleditor.ui.ToolConfigPanel.ToolSettings;
import jpixeleditor.utils.MyMap.MyMapEntry;

public class PaintHandler
{
	// Create a Queue class, which is basically an ArrayList stripped down to the essentials for adding to a queue and removing from a queue
	// This is used to contain points, but really it could be made into a generic class
	static class Queue
	{
		private ArrayList<Point> points;

		Queue()
		{
			points = new ArrayList<Point>();
		}

		// Add to the queue
		void push(int x, int y)
		{
			points.add(new Point(x, y));
		}

		// Fetch and remove from the queue
		Point pop()
		{
			if(points.size() > 0)
			{
				return points.remove(0);
			}
			else
			{
				return null;
			}
		}

		// Check if queue is empty
		boolean isEmpty()
		{
			return points.isEmpty();
		}
	}
	
	public static enum DrawTo
	{
		CANVAS,
		OVERLAY,
		SELECTION
	}
	
	public static void drawLine(Point start, Point end, int colour, DrawTo location, int brushSize, ToolSettings settings)
	{
		Point[] line = Helper.plotLine(start, end, settings.oneToOneRatio);
		
		for(int i = 0; i < line.length; i++)
		{
			paint(line[i].x, line[i].y, colour, location, brushSize, settings.circleBrush);
		}
	}
	
	public static void drawRectangle(Point start, Point end, int colour, DrawTo location, int brushSize, ToolSettings settings)
	{
		if(settings.oneToOneRatio)
		{
			float sx = start.x < end.x ? 1 : -1;
			float sy = start.y < end.y ? 1 : -1;
			
			float maxDist = Math.max(Math.abs(end.x - start.x), Math.abs(end.y - start.y));
			
			int rectSize = Math.round(maxDist);
			
			end = new Point((int)(start.x + (rectSize * sx)), (int)(start.y + (rectSize * sy)));
		}
		
		if(settings.fill)
		{
			int startX = Math.min(start.x, end.x);
			int startY = Math.min(start.y, end.y);
			int endX = Math.max(start.x, end.x);
			int endY = Math.max(start.y, end.y);
			
			for(int i = startX; i <= endX; i++)
			{
				for(int j = startY; j <= endY; j++)
				{
					if(i == startX || i == endX || j == startY || j == endY)
					{
						paint(i, j, colour, location, brushSize, false);
					}
					else
					{
						paint(i, j, colour, location, 1, false);
					}
				}
			}
		}
		else
		{
			Point[][] lines = { Helper.plotLine(start, new Point(start.x, end.y), false),
								Helper.plotLine(start, new Point(end.x, start.y), false),
								Helper.plotLine(new Point(end.x, start.y), end, false),
								Helper.plotLine(new Point(start.x, end.y), end, false) };
	
			for(int i = 0; i < lines.length; i++)
			{
				for(int j = 0; j < lines[i].length; j++)
				{
					paint(lines[i][j].x, lines[i][j].y, colour, location, brushSize, settings.circleBrush);
				}
			}
		}
	}
	
	public static void drawEllipse(Point start, Point end, int colour, DrawTo location, int brushSize, ToolSettings settings)
	{
		if(settings.oneToOneRatio)
		{
			float sx = start.x < end.x ? 1 : -1;
			float sy = start.y < end.y ? 1 : -1;
			
			float maxDist = Math.max(Math.abs(end.x - start.x), Math.abs(end.y - start.y));
			
			int rectSize = Math.round(maxDist);
			
			end = new Point((int)(start.x + (rectSize * sx)), (int)(start.y + (rectSize * sy)));
		}
		
		int x0 = start.x;
		int y0 = start.y;
		int x1 = end.x;
		int y1 = end.y;
		boolean fill = settings.fill;
		
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
			if (true)//!fill)
			{
				//drawPoint(xb-dx, yb-dy);
				paint(xb-dx, yb - dy, colour, location, brushSize, false);
				if (dx != 0 || xb != xc)
				{
					//drawPoint(xc+dx, yb-dy);
					paint(xc + dx, yb - dy, colour, location, brushSize, false);
					if (dy != 0 || yb != yc)
					{
						//drawPoint(xc+dx, yc+dy);
						paint(xc + dx, yc + dy, colour, location, brushSize, false);
					}
				}
				if (dy != 0 || yb != yc)
				{
					//drawPoint(xb-dx, yc+dy);
					paint(xb - dx, yc + dy, colour, location, brushSize, false);
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
					drawRow(xb - dx, xc + dx, yc + dy, colour, location);
					if (dy != 0 || yb != yc)
					{
						drawRow(xb - dx, xc + dx, yb - dy, colour, location);
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
					drawRow(xb - dx, xc + dx, yc + dy, colour, location);
					if (dy != 0 || yb != yc)
					{
						drawRow(xb - dx, xc + dx, yb - dy, colour, location);
					}
				}
				qt += 8L * qb * qb + 4L * qb * qb * qx + 8L * qa * qa - 4L * qa * qa * qy;
				dx++;
				qx += 2;
				dy--;
				qy -= 2;
			}
		}// End of while loop
	}
	
	public static void drawRow(int xstart, int xend, int y, int colour, DrawTo location)
	{
		drawLine(new Point(xstart, y), new Point(xend, y), colour, location, 1, new ToolSettings());
	}
	
	public static void paint(int i, int j, int colour, DrawTo location, int brushSize, boolean circleBrush)
	{
		Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
		switch(brushSize)
		{
			case 1:
				// Check where you are trying to paint is within the bounds
				if(i >= 0 && i < surface.getGridDims().width && j >= 0 && j < surface.getGridDims().height)
				{
					switch(location)
					{
						case CANVAS:
							surface.gridColours[i][j] = colour;
							break;
							
						case OVERLAY:
							surface.gridOverlay[i][j] = colour;
							break;
							
						default:
							break;
					}
				}
				break;
			
			default:
				if(brushSize > 1 && brushSize <= 5)
				{
					if(circleBrush && brushSize > 2)
					{
						int startX = i - (int)Math.floor((float)(brushSize - 1) / 2f);
						int startY = j - (int)Math.floor((float)(brushSize - 1) / 2f);
						int endX = startX + brushSize - 1;
						int endY = startY + brushSize - 1;
						
						ToolSettings settings = new ToolSettings();
						settings.fill = true;
						
						drawEllipse(new Point(startX, startY), new Point(endX, endY), colour, location, 1, settings);
					}
					else
					{
						// The start is negative half of the one less than the brush size
						int start = -(int)Math.floor((float)(brushSize - 1) / 2f);
						
						// Loop from the input coords plus start to the input coords plus start plus the brush size
						for(int x = i + start; x < i + start + brushSize; x++)
						{
							for(int y = j + start; y < j + start + brushSize; y++)
							{
								// Check where you are trying to paint is within the bounds
								if(x >= 0 && x < surface.getGridDims().width && y >= 0 && y < surface.getGridDims().height)
								{
									switch(location)
									{
										case CANVAS:
											surface.gridColours[x][y] = colour;
											break;
											
										case OVERLAY:
											surface.gridOverlay[x][y] = colour;
											break;
											
										default:
											break;
									}
								}
							}
						}
					}
				}
				break;
		}
	}
	
	public static void fill(Point point, int colour, ToolSettings settings)
	{
		// This algorithm is significantly faster than the previous one
		
		Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
		
		// Get tolerance from settings
		int tolerance = settings.tolerance;
		
		// These are sort of aliases. I only have these to reduce calls to DrawingSurface.getGridDims
		int numX = surface.getGridDims().width;
		int numY = surface.getGridDims().height;
		
		// The target colour is the colour at the current point
		int targetCol = surface.gridColours[point.x][point.y];
		
		// Redacted because tolerance is now a thing
//		if(targetCol == colour)
//		{
//			return;
//		}

		//surface.gridColours[i][j] = colour;
		
		// Need to keep track of what pixels have been coloured/filled
		boolean[][] filled = new boolean[numX][numY];

		Queue q = new Queue();
		
		q.push(point.x, point.y);
		
		while(!q.isEmpty()) // The problem seems to be that 
		{
			// While the queue is not empty, get a coordinate from the queue and fill it
			Point p = q.pop();
			
//			// This fixes the issue. I think I know what it was: There were multiple of the same point in the added to the queue.
//			// Since neither have been used, filled at that point was (quite rightfully) false.
//			if(filled[p.x][p.y])
//			{
//				continue;
//			}
//			filled[p.x][p.y] = true;
			
			surface.gridColours[p.x][p.y] = colour;
			
			// And then check each pixel immediately to every side of the current one
			// When adding to the queue, make sure you fill that pixel as filled to avoid unnecessary iteration
			if(checkValidity(p.x + 1, p.y, targetCol, tolerance, filled))
			{
				q.push(p.x + 1, p.y);
				filled[p.x + 1][p.y] = true;
			}
			if(checkValidity(p.x - 1, p.y, targetCol, tolerance, filled))
			{
				q.push(p.x - 1, p.y);
				filled[p.x - 1][p.y] = true;
			}
			if(checkValidity(p.x, p.y + 1, targetCol, tolerance, filled))
			{
				q.push(p.x, p.y + 1);
				filled[p.x][p.y + 1] = true;
			}
			if(checkValidity(p.x, p.y - 1, targetCol, tolerance, filled))
			{
				q.push(p.x, p.y - 1);
				filled[p.x][p.y - 1] = true;
			}
			
			// This means it will check the pixels immediately diagonally from it as well, if ToolSettings.fill8Way is enabled
			if(settings.fill8Way)
			{
				if(checkValidity(p.x + 1, p.y + 1, targetCol, tolerance, filled))
				{
					q.push(p.x + 1, p.y + 1);
					filled[p.x + 1][p.y + 1] = true;
				}
				if(checkValidity(p.x - 1, p.y - 1, targetCol, tolerance, filled))
				{
					q.push(p.x - 1, p.y - 1);
					filled[p.x - 1][p.y - 1] = true;
				}
				if(checkValidity(p.x - 1, p.y + 1, targetCol, tolerance, filled))
				{
					q.push(p.x - 1, p.y + 1);
					filled[p.x - 1][p.y + 1] = true;
				}
				if(checkValidity(p.x + 1, p.y - 1, targetCol, tolerance, filled))
				{
					q.push(p.x + 1, p.y - 1);
					filled[p.x + 1][p.y - 1] = true;
				}
			}
			
			//System.out.println(q.points.size());
			//Helper.printMatrix(filled);
		}
	}
	
	private static boolean checkValidity(int i, int j, int targetColour, int tolerance, boolean[][] filled)
	{
		DrawingSurface surface = CanvasContainer.canvas.surface;
		if(i >= 0 && i < surface.getGridDims().width && j >= 0 && j < surface.getGridDims().height)
		{
			if(canFill(targetColour, surface.gridColours[i][j], tolerance) || (Colour.getAlpha(surface.gridColours[i][j]) == 0 && Colour.getAlpha(targetColour) == 0))
			{
				if(!filled[i][j])
				{
					return true;
				}
				//System.out.println("Filled Matrix [i][j]: " + filled[i][j]);
			}
		}
		return false;
	}
		/* Previous Algorithm:
		int[][] filledPixels = new int[numX][numY];
		filledPixels[i][j] = 1;

		boolean filledPixel = false;
		while(true)
		{
			filledPixel = false;
			for(int pi = 0; pi < numX; pi++)
			{
				for(int pj = 0; pj < numY; pj++)
				{
					if(filledPixels[pi][pj] == 1)
					{
						// Check the pixel each side
						if(pi + 1 >= 0 && pi + 1 < numX && pj >= 0 && pj < numY)
						{
							if(canFill(targetCol, surface.gridColours[pi + 1][pj], tolerance) || (Colour.getAlpha(surface.gridColours[pi + 1][pj]) == 0 && Colour.getAlpha(targetCol) == 0))
							{
								surface.gridColours[pi + 1][pj] = colour;
								filledPixels[pi + 1][pj] = 1;
								filledPixel = true;
							}
						}
						if(pi - 1 >= 0 && pi - 1 < numX && pj >= 0 && pj < numY)
						{
							if(canFill(targetCol, surface.gridColours[pi - 1][pj], tolerance) || (Colour.getAlpha(surface.gridColours[pi - 1][pj]) == 0 && Colour.getAlpha(targetCol) == 0))
							{
								surface.gridColours[pi - 1][pj] = colour;	
								filledPixels[pi - 1][pj] = 1;
								filledPixel = true;
							}
						}
						if(pi >= 0 && pi < numX && pj + 1 >= 0 && pj + 1 < numY)
						{
							if(canFill(targetCol, surface.gridColours[pi][pj + 1], tolerance) || (Colour.getAlpha(surface.gridColours[pi][pj + 1]) == 0 && Colour.getAlpha(targetCol) == 0))
							{
								surface.gridColours[pi][pj + 1] = colour;
								filledPixels[pi][pj + 1] = 1;
								filledPixel = true;
							}
						}
						if(pi >= 0 && pi < numX && pj - 1 >= 0 && pj - 1 < numY)
						{
							if(canFill(targetCol, surface.gridColours[pi][pj - 1], tolerance) || (Colour.getAlpha(surface.gridColours[pi][pj - 1]) == 0 && Colour.getAlpha(targetCol) == 0))
							{
								surface.gridColours[pi][pj - 1] = colour;
								filledPixels[pi][pj - 1] = 1;
								filledPixel = true;
							}
						}
					}
				}
			}

			//System.out.println(filledPixel);
			
			
			if(!filledPixel)
			{
				break;
			}
		}
	}*/
	
	public static void replace(Point point, int colour, ToolSettings settings)
	{
		Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
		
		int targetCol = surface.gridColours[point.x][point.y];
		
		for(int i = 0; i < surface.getGridDims().width; i++)
		{
			for(int j = 0; j < surface.getGridDims().height; j++)
			{
				if(canFill(targetCol, surface.gridColours[i][j], settings.tolerance) || (Colour.getAlpha(surface.gridColours[i][j]) == 0 && Colour.getAlpha(targetCol) == 0))
				{
					surface.gridColours[i][j] = colour;
				}
			}
		}
	}
	
	public static boolean canFill(int targetColour, int colour, int tolerance)
	{
		int diff = Colour.getDifference(targetColour, colour, true);
		return Helper.map(diff, 0, 510, 0, 255) <= tolerance;
	}
	
	public static void putPixel(int i, int j, int colour, DrawTo location)
	{
		Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
		if(i >= 0 && i < surface.getGridDims().width && j >= 0 && j < surface.getGridDims().height)
		{
			switch(location)
			{
				case CANVAS:
					surface.gridColours[i][j] = colour;
					break;
					
				case OVERLAY:
					surface.gridOverlay[i][j] = colour;
					break;
					
				default:
					break;
			}
		}
	}
	
	public static void drawLine_PixelPerfect(Point start, Point end, int colour, DrawTo location, int brushSize, ToolSettings settings, ArrayList<MyMapEntry<Point, Integer>> currStroke)
	{
		ArrayList<MyMapEntry<Point, Integer>> stroke = currStroke;
		DrawingSurface surface = CanvasContainer.canvas.surface;
		
		if(brushSize != 1 || stroke == null)
		{
			drawLine(start, end, colour, location, brushSize, settings);
			return;
		}
		else if(stroke.isEmpty())
		{
			// This is the code from drawLine - Except it also puts values in the stroke MyMap
			Point[] line = Helper.plotLine(start, end, settings.oneToOneRatio);
			
			// Need to add to the stroke ArrayList - but we don't want identical positions right next to each other in the ArrayList or it'll mess up the pixel perfect algorithm
			Point prevPoint = null;
			for(int i = 0; i < line.length; i++)
			{
				if(true/*surface.contains(line[i].x, line[i].y)*/)
				{
					if(prevPoint != null)
					{
						if(prevPoint.equals(line[i]))
							continue;
					}
					stroke.add(new MyMapEntry<Point, Integer>(line[i], surface.contains(line[i].x, line[i].y) ? Integer.valueOf(surface.gridColours[line[i].x][line[i].y]) : null));
					paint(line[i].x, line[i].y, colour, location, brushSize, settings.circleBrush);
					prevPoint = line[i];
				}
			}
			return;
		}
		
		Point[] points = Helper.plotLine(start, end, false);
		
//		if(Helper.isNextTo(secondLast.getKey(), points[0]))
//		{
////			Point[] temp = new Point[points.length - 1];
////			System.arraycopy(points, 1, temp, 0, temp.length);
////			points = temp;
//			Point p = secondLast.getKey();
//			surface.gridColours[p.x][p.y] = secondLast.getValue().intValue(); 
//		}
		
		Point prevPoint = stroke.get(stroke.size() - 1).getKey();
		for(int i = 0; i < points.length; i++)
		{
			if(/*surface.contains(points[i].x, points[i].y) && */!points[i].equals(prevPoint))
			{
				stroke.add(new MyMapEntry<Point, Integer>(points[i], surface.contains(points[i].x, points[i].y) ? Integer.valueOf(surface.gridColours[points[i].x][points[i].y]) : null));
				paint(points[i].x, points[i].y, colour, location, 1, settings.circleBrush);
				prevPoint = points[i];
			}
		}
		
		Point[] path = new Point[stroke.size()];
		
		for(int i = 0; i < path.length; i++)
		{
			path[i] = stroke.get(i).getKey();
		}
		
//		Point[] path = stroke.getKeyArray();
		
		for(int c = 1; c < path.length - 1; c++)
		{
			// What is in this if statement I got from: https://rickyhan.com/jekyll/update/2018/11/22/pixel-art-algorithm-pixel-perfect.html
			if(	  ((path[c-1].x == path[c].x || path[c-1].y == path[c].y)
				&& (path[c+1].x == path[c].x || path[c+1].y == path[c].y)
				&&  path[c-1].x != path[c+1].x
				&&  path[c-1].y != path[c+1].y))
			{
				if(stroke.get(c).getValue() != null) // We want the stroke to take into account points off the canvas (Otherwise you may get strange behaviour when you leave and re-enter the canvas)
				{
					surface.gridColours[path[c].x][path[c].y] = stroke.get(c).getValue().intValue();
					stroke.remove(c);
				}
			}
		}
	}
	
	// If interdisperse, then it only uses the primary colour, and which mouse button you press determines where the colour ends up
	public static void drawLine_Dither(Point start, Point end, int col1, int col2, boolean rightClick, int brushSize, ToolSettings settings)
	{
		DrawingSurface surface = CanvasContainer.canvas.surface;
		Point[] points = Helper.plotLine(start, end, false);
		
		if(brushSize == 1)
		{
			for(int i = 0; i < points.length; i++)
			{
				if(surface.contains(points[i].x, points[i].y))
				{
					if(rightClick)
					{
						// This determines whether to put col1 or col2
						if((points[i].x + points[i].y) % 2 == 0 && settings.interdisperse)
						{
							surface.gridColours[points[i].x][points[i].y] = col2;
						}
						else if((points[i].x + points[i].y) % 2 != 0)
						{
							surface.gridColours[points[i].x][points[i].y] = col1;
						}
					}
					else
					{
						// This determines whether to put col1 or col2
						if((points[i].x + points[i].y) % 2 == 0)
						{
							surface.gridColours[points[i].x][points[i].y] = col1;
						}
						else if(settings.interdisperse)
						{
							surface.gridColours[points[i].x][points[i].y] = col2;
						}
					}
				}
			}
		}
		else if(brushSize > 1 && brushSize <= 5)
		{
			for(int index = 0; index < points.length; index++)
			{
				// The start is negative half of the one less than the brush size
				int start1 = -(int)Math.floor((float)(brushSize - 1) / 2f);
				
				int i = points[index].x;
				int j = points[index].y;
				
				// Loop from the input coords plus start to the input coords plus start plus the brush size
				for(int x = i + start1; x < i + start1 + brushSize; x++)
				{
					for(int y = j + start1; y < j + start1 + brushSize; y++)
					{
						// Check where you are trying to paint is within the bounds
						if(surface.contains(x, y))
						{
							if(rightClick)
							{
								// This determines whether to put col1 or col2
								if((x + y) % 2 == 0 && settings.interdisperse)
								{
									surface.gridColours[x][y] = col2;
								}
								else if((x + y) % 2 != 0)
								{
									surface.gridColours[x][y] = col1;
								}
							}
							else
							{
								// This determines whether to put col1 or col2
								if((x + y) % 2 == 0)
								{
									surface.gridColours[x][y] = col1;
								}
								else if(settings.interdisperse)
								{
									surface.gridColours[x][y] = col2;
								}
							}
						}
					}
				}
			}
		}
	}
}