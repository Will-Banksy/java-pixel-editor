package jpixeleditor.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import jpixeleditor.main.Main;
import jpixeleditor.tools.Colour;
import jpixeleditor.tools.EditorTools;
import jpixeleditor.tools.Helper;

@SuppressWarnings("serial")
public class Canvas_Old extends Panel
{
	public class DrawingSurface
	{
		public int gridWidth = 32;
		public int gridHeight = 32;
		
		//public int[][] prevGridColours; // Original plan to improve performance: Only redraw pixels that have changed
		public int[][] gridColours;
		public int[][] gridOverlay;
		
		public Canvas_Old canvas;
		
		public DrawingSurface(Canvas_Old canvas)
		{
			this.canvas = canvas;
			
			setGridDims(gridWidth, gridHeight);
			
			/*gridColours = new int[gridWidth][gridHeight];
			gridOverlay = new int[gridWidth][gridHeight];
			
			for(int i = 0; i < gridWidth; i++)
			{
				for(int j = 0; j < gridHeight; j++)
				{
					gridColours[i][j] = Colour.TRANSPARENT;
					gridOverlay[i][j] = Colour.TRANSPARENT;
				}
			}*/
		}
		
		public void setGridDims(int width, int height)
		{
			gridWidth = width;
			gridHeight = height;
			gridColours = new int[gridWidth][gridHeight];
			gridOverlay = new int[gridWidth][gridHeight];
			
			for(int i = 0; i < gridWidth; i++)
			{
				for(int j = 0; j < gridHeight; j++)
				{
					gridColours[i][j] = Colour.TRANSPARENT;
					gridOverlay[i][j] = Colour.TRANSPARENT;
				}
			}
		}
		
		public Dimension getGridDims()
		{
			return new Dimension(gridWidth, gridHeight);
		}
		
		public Point canvasToGrid(int x, int y)
		{
			int newX = (int)(Helper.map(x, 0, canvas.getWidth(), 0, gridWidth));
			int newY = (int)(Helper.map(y, 0, canvas.getHeight(), 0, gridHeight));
			return new Point(newX, newY);
		}
		
		public Point gridToCanvas(int x, int y)
		{
			int newX = (int)(Helper.map(x, 0, gridWidth, 0, canvas.getWidth()));
			int newY = (int)(Helper.map(y, 0, gridHeight, 0, canvas.getHeight()));
			return new Point(newX, newY);
		}
		
		public void clearOverlay()
		{
			for(int i = 0; i < gridWidth; i++)
			{
				for(int j = 0; j < gridHeight; j++)
				{
					gridOverlay[i][j] = Colour.TRANSPARENT;
				}
			}
		}
	}

	public static class Zoom
	{
		// Currently stores the mouse position when zoomed
		int zoomX;
		int zoomY;
		
		float scale;
		
		public static final float MAX_ZOOM = 100f;
		public static final float MIN_ZOOM = 1f;
		
		public Zoom(int dx, int dy, float scale)
		{
			zoomX = dx;
			zoomY = dy;
			this.scale = scale;
		}
	}
	
	public static BufferedImage bgImage = null;
	private TexturePaint tp = null;
	
	public DrawingSurface surface;
	
	public Point mls_prevPoint;
	public Point mls_startPoint;
	
	public boolean drawMouse = true;
	
	public static final Color MOUSE_COLOUR = Colour.toAWTColor(Colour.toIntARGB(128, 255, 255, 255));
	
	public Zoom zoom = null;// = new Zoom(0, 0, 1f); // Need to start at the appropriate zoom level - 1 is not that
	public Rectangle prevRect = null;
	public Point offset = null;
	
	public Canvas_Old()
	{
		try
		{
			if(bgImage == null)
			{
				bgImage = ImageIO.read(Main.class.getResource("/Transparency-Dark200by200.png"));
			}
			
			// Create a new TexturePaint object, set the size of the rectangle to 200 * 200 so that it is displayed at that size and when I draw it it tiles, and the position to (0, 0) so it draws from (0, 0) in the image
			tp = new TexturePaint(bgImage, new Rectangle(0, 0, 200, 200));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		surface = new DrawingSurface(this);
		
		offset = new Point(0, 0);
		
		// On these, I get a NullPointerException if it's a middle mouse click
		addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent arg0)
			{
				
			}

			@Override public void mouseEntered(MouseEvent arg0)
			{
				
			}

			@Override public void mouseExited(MouseEvent arg0)
			{
				repaint();
			}

			@Override public void mousePressed(MouseEvent arg0)
			{
				if(EditorTools.selectedTool.triggerType == EditorTools.ToolInfo.ON_PRESS)
				{
					EditorTools.doToolAction_Press(arg0.getPoint(), arg0);
				}
				else if(EditorTools.selectedTool.triggerType == EditorTools.ToolInfo.ON_HOLD)
				{
					EditorTools.doToolAction_Hold(arg0.getPoint(), mls_prevPoint, arg0);
					mls_prevPoint = arg0.getPoint();
				}
				else if(EditorTools.selectedTool.triggerType == EditorTools.ToolInfo.ON_RELEASE)
				{
					mls_startPoint = arg0.getPoint();
					EditorTools.updatePreview_Release(arg0.getPoint(), arg0.getPoint(), arg0);
				}
				drawMouse = false;
				repaint();
			}

			@Override public void mouseReleased(MouseEvent arg0)
			{
				if(EditorTools.selectedTool.triggerType == EditorTools.ToolInfo.ON_HOLD)
				{
					EditorTools.doToolAction_Hold(arg0.getPoint(), mls_prevPoint, arg0);
					repaint();
				}
				else if(EditorTools.selectedTool.triggerType == EditorTools.ToolInfo.ON_RELEASE)
				{
					EditorTools.doToolAction_Release(mls_startPoint, arg0.getPoint(), arg0);
					
				}
				mls_prevPoint = null;
				mls_startPoint = null;
				
				drawMouse = true;
				repaint();
			}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			@Override public void mouseDragged(MouseEvent arg0)
			{
				if(EditorTools.selectedTool.triggerType == EditorTools.ToolInfo.ON_HOLD)
				{
					EditorTools.doToolAction_Hold(arg0.getPoint(), mls_prevPoint, arg0);
					mls_prevPoint = arg0.getPoint();
					repaint();
				}
				else if(EditorTools.selectedTool.triggerType == EditorTools.ToolInfo.ON_RELEASE)
				{
					EditorTools.updatePreview_Release(mls_startPoint, arg0.getPoint(), arg0);
					repaint();
				}
			}

			@Override public void mouseMoved(MouseEvent arg0)
			{
				repaint();
			}
		});
	}
	
	@Override public void setLocation(Point pos)
	{
		setLocation(pos.x, pos.y);
	}
	
	@Override public void setLocation(int x, int y) // This doesn't work
	{
		int currX = getX() - offset.x;
		int currY = getY() - offset.y;
		
		Dimension size = getSize();
		
		int dx = x - currX;
		int dy = y - currY;
		
		offset.x = dx;
		offset.y = dy;
		
		if(size.width > Main.canvasPanel.getWidth())
		{
			if(x + offset.x > 0)
			{
				offset.x = -x; // 0 - x
			}
			if(x + size.width + offset.x < Main.canvasPanel.getWidth())
			{
				offset.x = Main.canvasPanel.getWidth() - x;
			}
		}
		else
		{
			offset.x = 0;
		}
		if(size.height > Main.canvasPanel.getHeight())
		{
			if(y + offset.y > 0)
			{
				offset.y = -y; // 0 - x
			}
			if(y + size.height + offset.y < Main.canvasPanel.getHeight())
			{
				offset.y = Main.canvasPanel.getHeight() - y;
			}
		}
		else
		{
			offset.y = 0;
		}
		
		System.out.println("offset.x: " + offset.x + "  offset.y: " + offset.y);
	}
	
	@Override public void setBounds(int x, int y, int width, int height) // How well this works is almost magical
	{
		if(surface.gridWidth == surface.gridHeight) // Use the slightly faster method that works for just squares
		{
			if(Main.canvasPanel.getWidth() > Main.canvasPanel.getHeight()) // CC width greater than CC height
			{
				int size = Main.canvasPanel.getHeight();
				width = size;
				height = size;
				x = (int)((Main.canvasPanel.getWidth() - Main.canvasPanel.getHeight()) * 0.5f);
				y = 0;
			}
			else if(Main.canvasPanel.getWidth() < Main.canvasPanel.getHeight()) // CC height greater than CC width
			{
				int size = Main.canvasPanel.getWidth();
				width = size;
				height = size;
				x = 0;
				y = (int)((Main.canvasPanel.getHeight() - Main.canvasPanel.getWidth()) * 0.5f);
			}
			else // CC width and height equal
			{
				width = Main.canvasPanel.getWidth();
				height = Main.canvasPanel.getHeight();
				x = 0;
				y = 0;
			}
		}
		else // Use the slightly slower method that works for everything
		{
			// Calculate the separate scale factors for width and height
			double sfWidth = (double)Main.canvasPanel.getWidth() / (double)surface.gridWidth;
			double sfHeight = (double)Main.canvasPanel.getHeight() / (double)surface.gridHeight;
			
			// Get the minimum scale factor. This is the one we will scale by
			double scale = Math.min(sfWidth, sfHeight);
			
			// Calculate the individual width and heigt
			width = (int)((double)surface.gridWidth * scale);
			height = (int)((double)surface.gridHeight * scale);
			
			// Calculate the x and y position of the canvas
			x = (Main.canvasPanel.getWidth() - width) / 2;
			y = (Main.canvasPanel.getHeight() - height) / 2;
		}
		
		if(prevRect == null)
		{
			// Use Helper to find the scale that fits the canvas to the available space
			float scale = Helper.bestScale(Main.canvasPanel.getBounds(), new Rectangle(0, 0, surface.gridWidth, surface.gridHeight));
			zoom = new Zoom(0, 0, Helper.constrain(scale, Zoom.MIN_ZOOM, Zoom.MAX_ZOOM));
			
			// Get the scaled dimensions
			width = (int)(surface.gridWidth * zoom.scale);
			height = (int)(surface.gridHeight * zoom.scale);
			
			// It makes sense that if the canvas size is bigger than the CanvasContainer, that the canvas position be set to zero (for each dimension)
			if(width < Main.canvasPanel.getWidth())
			{
				x = Main.canvasPanel.getWidth() / 2 - width / 2;
			}
			else
			{
				x = 0;
			}
			if(height < Main.canvasPanel.getHeight())
			{
				y = Main.canvasPanel.getHeight() / 2 - height / 2;
			}
			else
			{
				y = 0;
			}
			prevRect = new Rectangle(x, y, width, height);
		}
		else if(zoom != null && (offset.x == 0 && offset.y == 0))
		{
			// Successful Zooming Algorithm!!!
			// Helped massively by GeoGebra - Use it more!
			
			// Put mouse position into a point
			Point mouse = new Point(zoom.zoomX, zoom.zoomY);
			
			// Get the scaled dimensions
			int newWidth = (int)(surface.gridWidth * zoom.scale);
			int newHeight = (int)(surface.gridHeight * zoom.scale);
			
			// Get the ratio of the size of the new rectangle to the size of the previous rectangle
			float zoomRatio = (float)newWidth / prevRect.width;
			
			// Calculate the new coordinates - calculation (newPos = mouse - (mouse - previous rectangle position) * zoom ratio)
			int newX = mouse.x - (int)((mouse.x - prevRect.x) * zoomRatio);
			int newY = mouse.y - (int)((mouse.y - prevRect.y) * zoomRatio);
			
			// Compile everything together into a new rectangle
			Rectangle res = new Rectangle(newX, newY, newWidth, newHeight);
			
			// Then just do some logic to make the zooming work how I want
			if(res.width < Main.canvasPanel.getWidth()) // res width is less than CanvasContainer width
			{
				res.x = (Main.canvasPanel.getWidth() - res.width) / 2; // Centre res
			}
			else if(res.width > Main.canvasPanel.getWidth()) // res width is greater than CanvasContainer width
			{
				if(res.x > 0) // If res left side position is greater than CanvasContainer left side, make it equal to CanvasContainer left side
				{
					res.x = 0;
				}
				if(res.x + res.width < Main.canvasPanel.getWidth()) // If res right side position is less than CanvasContainer right side, make it equal to CanvasContainer right side
				{
					res.x = Main.canvasPanel.getWidth() - res.width;
				}
			}
			if(res.height < Main.canvasPanel.getHeight()) // res height is less than CanvasContainer width
			{
				res.y = (Main.canvasPanel.getHeight() - res.height) / 2; // Centre res
			}
			else if(res.height > Main.canvasPanel.getHeight()) // res height is greater than CanvasContainer height
			{
				if(res.y > 0) // If res top side position is greater than CanvasContainer top side, make it equal to CanvasContainer top side
				{
					res.y = 0;
				}
				if(res.y + res.height < Main.canvasPanel.getHeight()) // If res bottom side position is less than CanvasContainer bottom side, make it equal to CanvasContainer bottom side
				{
					res.y = Main.canvasPanel.getHeight() - res.height;
				}
			}
			
			x = res.x;
			y = res.y;
			width = res.width + offset.x;
			height = res.height + offset.y;
			
			prevRect = res;
		}
		if(zoom == null)
		{
			throw new NullPointerException("'zoom' should not be null at this point. As it is, something has gone wrong");
		}
		super.setBounds(x, y, width, height);
	}
	
	@Override public void paintComponent(Graphics g) // Performance notes: Performance seems to be good, even at higher resolutions, unless using semi-transparent colours
	{
		Graphics2D g2d = (Graphics2D)g;
		
		// Use the TexturePaint to paint the background onto the canvas, tiling it if necessary
		g2d.setPaint(tp);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		// Check if the scale is not equal to 1
		// If the scale isn't 1 - Do normal drawing of canavs - Some pixels will be rendered bigger than others (should be an even enough distribution of these different-sized pixels though)
		// If the scale is 1 - Do perfect drawing of canvas, because the scale is 1 it makes it easier
		if(zoom != null && zoom.scale != 1f)
		{
			for(int i = 0; i < surface.gridWidth; i++)
			{
				for(int j = 0; j < surface.gridHeight; j++)
				{
					Point p = surface.gridToCanvas(i, j); // Get the top-left corner of the rectangle
					Point toP = surface.gridToCanvas(i + 1, j + 1); // To ensure no gaps between pixels, the width and height are set to the distance between this coordinate and the next
					int width = toP.x - p.x;
					int height = toP.y - p.y;
					
					if(width != 0 && height != 0) // width and height should always be greater than 0, but it doesn't hurt to include this to optimize for if they aren't
					{
						if(Colour.getAlpha(surface.gridColours[i][j]) != 0) // That sped up performance a lot more than I thought it would
						{
							g2d.setColor(Colour.toAWTColor(surface.gridColours[i][j]));
							g2d.fillRect(p.x, p.y, width, height);
						}
						if(Colour.getAlpha(surface.gridOverlay[i][j]) != 0)
						{
							g2d.setColor(Colour.toAWTColor(surface.gridOverlay[i][j]));
							g2d.fillRect(p.x, p.y, width, height);
						}
					}
				}
			}
		}
		else
		{
			// This is just a little more precise, as I don't have to accommodate for scales other than 1
			// That means I can ensure that every single pixel is drawn to the screen
			// This does make a visible difference
			
			int width = 1;
			int height = 1;
			
			for(int i = 0; i < surface.gridWidth; i++)
			{
				for(int j = 0; j < surface.gridHeight; j++)
				{
					Point p = new Point(i, j); // Put coordinates into Point
					
					if(Colour.getAlpha(surface.gridColours[i][j]) != 0) // That sped up performance a lot more than I thought it would
					{
						g2d.setColor(Colour.toAWTColor(surface.gridColours[i][j]));
						g2d.fillRect(p.x, p.y, width, height);
					}
					if(Colour.getAlpha(surface.gridOverlay[i][j]) != 0)
					{
						g2d.setColor(Colour.toAWTColor(surface.gridOverlay[i][j]));
						g2d.fillRect(p.x, p.y, width, height);
					}
				}
			}
		}
		
		if(drawMouse)
			showMousePosition(g2d);
		
		if(!Helper.compareMatrices(surface.gridColours, NavigatorPanel.navigator.gridColours))
		{
			// Set the grid and repaint the navigator
			NavigatorPanel.navigator.setGrid(surface.gridWidth, surface.gridHeight, surface.gridColours);
			NavigatorPanel.navigator.repaint();
		}
	}
	
	private void showMousePosition(Graphics2D g2d)
	{
		Point mouse = getMousePosition();
		if(mouse != null)
		{
			Point mouseOnGrid = surface.canvasToGrid(mouse.x, mouse.y);
			Point p = surface.gridToCanvas(mouseOnGrid.x, mouseOnGrid.y);
			Point toP = surface.gridToCanvas(mouseOnGrid.x + 1, mouseOnGrid.y + 1);
			int width = toP.x - p.x;
			int height = toP.y - p.y;
			
			if(EditorTools.brushSize == 1 || !EditorTools.selectedTool.sizeMatters)
			{
				g2d.setColor(MOUSE_COLOUR);
				g2d.fillRect(p.x, p.y, width, height);
			}
			else
			{
				if(EditorTools.brushSize > 1 && EditorTools.brushSize <= 5)
				{
					if(EditorTools.selectedTool.settings.circleBrush && EditorTools.brushSize > 2)
					{
						int startX = mouseOnGrid.x - (int)Math.floor((float)(EditorTools.brushSize - 1) / 2f);
						int startY = mouseOnGrid.y - (int)Math.floor((float)(EditorTools.brushSize - 1) / 2f);
						
						Point[] points = Helper.circleInBounds(startX, startY, EditorTools.brushSize - 1, EditorTools.brushSize - 1, true);
						
						for(int i = 0; i < points.length; i++)
						{
							if(points[i].x >= 0 && points[i].x < surface.getGridDims().width && points[i].y >= 0 && points[i].y < surface.getGridDims().height)
							{
								Point pos = surface.gridToCanvas(points[i].x, points[i].y);
								
								Point toPos = surface.gridToCanvas(points[i].x + 1, points[i].y + 1);
								width = toPos.x - pos.x;
								height = toPos.y - pos.y;
								
								g2d.setColor(MOUSE_COLOUR);
								g2d.fillRect(pos.x, pos.y, width, height);
							}
						}
					}
					else
					{
						int i = mouseOnGrid.x;
						int j = mouseOnGrid.y;
						// The start is negative half of the one less than the brush size
						int start = -(int)Math.floor((float)(EditorTools.brushSize - 1) / 2f);
						
						// Loop from the input coords plus start to the input coords plus start plus the brush size
						for(int x = i + start; x < i + start + EditorTools.brushSize; x++)
						{
							for(int y = j + start; y < j + start + EditorTools.brushSize; y++)
							{
								// Check where you are trying to paint is within the bounds
								if(x >= 0 && x < surface.getGridDims().width && y >= 0 && y < surface.getGridDims().height)
								{
									Point pos = surface.gridToCanvas(x, y);
									
									Point toPos = surface.gridToCanvas(x + 1, y + 1);
									width = toPos.x - pos.x;
									height = toPos.y - pos.y;
									
									g2d.setColor(MOUSE_COLOUR);
									g2d.fillRect(pos.x, pos.y, width, height);
								}
							}
						}
					}
				}
			}
		}
	}
}
