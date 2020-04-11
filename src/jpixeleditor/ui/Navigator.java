package jpixeleditor.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import jpixeleditor.main.Main;
import jpixeleditor.main.Main.Theme;
import jpixeleditor.tools.Colour;
import jpixeleditor.tools.Helper;

@SuppressWarnings("serial")
public class Navigator extends Panel
{
	public int gridWidth = 32, gridHeight = 32;
	public int gridColours[][];
	private TexturePaint tp = null;
	BufferedImage img;
	
	public Rectangle view;
	
	// For dragging the view
	Point origin = null;
	Point offset = null;
	boolean canDrag = false;
	boolean updateViewRect = true;
	
	public Navigator()
	{
		super();
		
		setGridDims(gridWidth, gridHeight);
		
		if(Canvas.bgImage == null)
		{
			try
			{
				Canvas.bgImage = ImageIO.read(Main.class.getResource("/Transparency-Dark200by200.png"));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		tp = new TexturePaint(Canvas.bgImage, new Rectangle(0, 0, 200, 200));
		
		updateViewRect();
		
		// Add mouse listeners for dragging the view rectangle around. Look in Processing sketch Dragging_Rect for code to help with this
		// Also keep in mind this won't work until I make the movement of the view rectangle actually move the canvas
		addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent me)
			{
				if(view.contains(me.getPoint()))
				{
					origin = me.getPoint();
					offset = new Point(origin.x - view.x, origin.y - view.y);
					canDrag = true;
					updateViewRect = false;
				}
			}
			
			@Override public void mouseReleased(MouseEvent me)
			{
				// Just set these both to null, why not
				origin = null;
				offset = null;
				canDrag = false;
				updateViewRect = true;
			}
		});
		
		addMouseMotionListener(new MouseAdapter() {
			@Override public void mouseDragged(MouseEvent me)
			{
				if(canDrag)
				{
					// Move the view rectangle
					view.x += me.getX() - origin.x;
					view.y += me.getY() - origin.y;
					
					// Set the origin to the mouse position
					origin.move(me.getX(), me.getY());
					
					// Constrain the view rectangle to the bounds of this component
					if(view.x < 0)
					{
						origin.x = offset.x;
						view.x = 0;
					}
					if(view.y < 0)
					{
						origin.y = offset.y;
						view.y = 0;
					}
					if(view.x + view.width > getWidth())
					{
						view.x = getWidth() - view.width;
						origin.x = view.x + offset.x; 
					}
					if(view.y + view.height > getHeight())
					{
						view.y = getHeight() - view.height;
						origin.y = view.y + offset.y;
					}
					
					// TODO: Make this more accurate, if possible. Is not accurate on larger images
					
					// We need to scale the point (0, 0) down with the view rectangle, because then (0, 0) will be the offset we need for the Canvas
					
					// Get the 0, which is between the view position and the view position plus it's dimension, and 0 and the dimension of the CanvasContainer dimension
					int scaledPointX = (int)(Helper.map(0, view.x, view.x + view.width, 0, Main.canvasPanel.getWidth() + 1));
					int scaledPointY = (int)(Helper.map(0, view.y, view.y + view.height, 0, Main.canvasPanel.getHeight() + 1));
					
					// Do some checks to remove error margins
					if(view.x == 0)
					{
						scaledPointX = 0;
					}
					else if(view.x == getWidth() - view.width)
					{
						scaledPointX = Main.canvasPanel.getWidth() - CanvasContainer.canvas.getWidth();
					}
					if(view.y == 0)
					{
						scaledPointY = 0;
					}
					else if(view.y == getHeight() - view.height)
					{
						scaledPointY = Main.canvasPanel.getHeight() - CanvasContainer.canvas.getHeight();
					}
					
					// Set the offsets to negative this scaled point
					CanvasContainer.canvas.zoom.offX = scaledPointX;
					CanvasContainer.canvas.zoom.offY = scaledPointY;
					
					// Reset the bounds of the canvas to update it's position. This also repaints it
					CanvasContainer.canvas.resetBounds();
					
					// Repaint this Navigator to show the view rectangle being dragged
					repaint();
				}
			}
		});
	}
	
	public void updateViewRect() // TODO: Make this more accurate, if possible. Is not accurate on larger images
	{
		Rectangle canvas = CanvasContainer.canvas.getBounds();
		Dimension canvasContainer = Main.canvasPanel.getSize();
		
		//int x = (int)Helper.map(val, start1, stop1, start2, stop2)
		
		int x = (int)Helper.map(0, canvas.x, canvas.x + canvas.width, 0, getWidth());
		int y = (int)Helper.map(0, canvas.y, canvas.y + canvas.height, 0, getHeight());
		
		int x2 = (int)Helper.map(canvasContainer.width, canvas.x, canvas.x + canvas.width, 0, getWidth());
		int y2 = (int)Helper.map(canvasContainer.height, canvas.y, canvas.y + canvas.height, 0, getHeight());
		
		int width = x2 - x;
		int height = y2 - y;
		
		view = new Rectangle(x, y, width, height);//Helper.constrainRect(getBounds(), new Rectangle(x, y, width, height));
	}

	public void setGridDims(int width, int height)
	{
		gridWidth = width;
		gridHeight = height;
		gridColours = new int[gridWidth][gridHeight];
		
		for(int i = 0; i < gridWidth; i++)
		{
			for(int j = 0; j < gridHeight; j++)
			{
				gridColours[i][j] = Colour.TRANSPARENT;
			}
		}
	}
	
	public void setGrid(int gridWidth, int gridHeight, int[][] gridColours)
	{
		// If either grid dimension is different to the one in this Navigator, then update the grid dimensions
		if(gridWidth != this.gridWidth || gridHeight != this.gridHeight)
		{
			this.gridWidth = gridWidth;
			this.gridHeight = gridHeight;
			this.gridColours = new int[gridWidth][gridHeight];
			
			// Loop through the grid, setting the colours of this Navigators gridColours to the corresponding input gridColours
			for(int i = 0; i < gridWidth; i++)
			{
				for(int j = 0; j < gridHeight; j++)
				{
					this.gridColours[i][j] = gridColours[i][j];
				}
			}
			
			setBounds(0, 0, 0, 0);
		}
		else
		{
			// Loop through the grid, setting the colours of this Navigators gridColours to the corresponding input gridColours
			for(int i = 0; i < gridWidth; i++)
			{
				for(int j = 0; j < gridHeight; j++)
				{
					this.gridColours[i][j] = gridColours[i][j];
				}
			}
		}
	}
	
	public Point canvasToGrid(int x, int y)
	{
		int newX = Math.round(Helper.map(x, 0, getWidth(), 0, gridWidth));
		int newY = Math.round(Helper.map(y, 0, getHeight(), 0, gridHeight));
		return new Point(newX, newY);
	}
	
	public Point gridToCanvas(int x, int y)
	{
		int newX = Math.round(Helper.map(x, 0, gridWidth, 0, getWidth()));
		int newY = Math.round(Helper.map(y, 0, gridHeight, 0, getHeight()));
		return new Point(newX, newY);
	}
	
	@Override public void setBounds(int x, int y, int width, int height)
	{
		// Calculate the separate scale factors for width and height
		double sfWidth = (double)NavigatorPanel.navContainer.getWidth() / (double)gridWidth;
		double sfHeight = (double)NavigatorPanel.navContainer.getHeight() / (double)gridHeight;
		
		// Get the minimum scale factor. This is the one we will scale by
		double scale = Math.min(sfWidth, sfHeight);
		
		// Calculate the individual width and height
		width = (int)((double)gridWidth * scale);
		height = (int)((double)gridHeight * scale);
		
		// Calculate the x and y position of the canvas
		x = (NavigatorPanel.navContainer.getWidth() - width) / 2;
		y = (NavigatorPanel.navContainer.getHeight() - height) / 2;
		
		super.setBounds(x, y, width, height);
	}
	
	@Override public void paintComponent(Graphics g) // TODO: Needs to be made quicker/more efficient the same way Canvas was
	{
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setPaint(tp);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		float scale = (float)getWidth() / (float)CanvasContainer.canvas.surface.gridWidth;
		g2d.scale(scale, scale);
		g2d.drawImage(img, 0, 0, null);
		
		g2d.setTransform(new AffineTransform());
		
		if(updateViewRect)
			updateViewRect();
		
		// I don't make these changes to Rectangle view so view can be correct, instead of constrained
		int vx = view.x < 0 ? 0 : view.x;
		int vy = view.y < 0 ? 0 : view.y;
		int vw = view.x + view.width > getWidth() - 1 ? getWidth() - 1 - vx : view.width;
		int vh = view.y + view.height > getHeight() - 1 ? getHeight() - 1 - vy : view.height;
		
		if(vx != 0 || vy != 0 || vw != getWidth() - 1 || vh != getHeight() - 1)
		{
			g2d.setColor(Colour.toAWTColor(Theme.THEME_ACCENT));
			g2d.drawRect(vx, vy, vw, vh);
		}
	}
}
