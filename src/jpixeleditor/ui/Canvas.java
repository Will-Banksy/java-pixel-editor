package jpixeleditor.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import jpixeleditor.Main;
import jpixeleditor.tools.FreeSelection;
import jpixeleditor.tools.Move;
import jpixeleditor.tools.SelectorTool;
import jpixeleditor.utils.Colour;
import jpixeleditor.utils.EditorTools;
import jpixeleditor.utils.Helper;
import jpixeleditor.utils.MyMap;
import jpixeleditor.utils.MyMap.MyMapEntry;

@SuppressWarnings("serial")
public class Canvas extends Panel
{
	public class DrawingSurface
	{
		public int gridWidth = 32;
		public int gridHeight = 32;
		
		//public int[][] prevGridColours; // Original plan to improve performance: Only redraw pixels that have changed
		public int[][] gridColours;
		public int[][] gridOverlay;
		
		public Canvas canvas;
		
		public DrawingSurface(Canvas canvas)
		{
			this.canvas = canvas;
			
			setGridDims(gridWidth, gridHeight);
		}
		
		public void setGridDims(int width, int height)
		{
			gridWidth = width;
			gridHeight = height;
			gridColours = new int[gridWidth][gridHeight];
			gridOverlay = new int[gridWidth][gridHeight];
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
		
		public boolean contains(int i, int j)
		{
			if(i >= 0 && i < gridWidth && j >= 0 && j < gridHeight)
			{
				return true;
			}
			
			return false;
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
		// Offset from CanvasContainer (0, 0) defining position of top-left-hand corner
		int offX;
		int offY;
		
		// Scale of pixels
		float scale;
		
		// The old scaled width and height
		Dimension prevDims;
		
		public static final float MAX_ZOOM = 100f;
		public static final float MIN_ZOOM = 1f;
		
		public Zoom(int ox, int oy, float scale, Canvas canvas)
		{
			offX = ox;
			offY = oy;
			this.scale = scale;
			
			prevDims = new Dimension((int)(canvas.surface.gridWidth * this.scale), (int)(canvas.surface.gridHeight * this.scale));
		}
		
		public void zoom(int intoX, int intoY, float newScale)
		{
			// An all-new zooming algorithm, all created with the help of GeoGebra!
			
			scale = newScale;
			
			Canvas canvas = CanvasContainer.canvas;
			CanvasContainer cc = Main.canvasPanel;
			
			// Get the scaled dimensions
			int scaledWidth = (int)(canvas.surface.gridWidth * scale);
			int scaledHeight = (int)(canvas.surface.gridHeight * scale);
			
			// Get the ratio between the new scale and the previous scale
			float scaleRatio = (float)scaledWidth / (float)prevDims.width;
			
			// Move the offsets towards zoomed into point
			offX = (int)Helper.lerp(offX, intoX, 1 - scaleRatio);
			offY = (int)Helper.lerp(offY, intoY, 1 - scaleRatio);
			
			// Now of course we can't always just have the canvas floating around, we need to make it so that when it's smaller than the CanvasContainer it is centred
			// We also need it so that if it's bigger than the CanvasContainer it's left-hand side is below 0 and it's right-hand side is above the CanvasContainer width, and the same for the top and bottom
			if(scaledWidth < cc.getWidth())
			{
				offX = (int)((cc.getWidth() * 0.5f) - (scaledWidth * 0.5f));
			}
			else if(scaledWidth > cc.getWidth())
			{
				if(offX > 0)
				{
					offX = 0;
				}
				if(offX + scaledWidth < cc.getWidth())
				{
					offX = cc.getWidth() - scaledWidth;
				}
			}
			// --
			if(scaledHeight < cc.getHeight())
			{
				offY = (int)((cc.getHeight() * 0.5f) - (scaledHeight * 0.5f));
			}
			else if(scaledHeight > cc.getHeight())
			{
				if(offY > 0)
				{
					offY = 0;
				}
				if(offY + scaledHeight < cc.getHeight())
				{
					offY = cc.getHeight() - scaledHeight;
				}
			}
			
			// Set the previous dimensions to be used in the next zooming calculation
			prevDims.width = scaledWidth;
			prevDims.height = scaledHeight;
		}
	}
	
	public static BufferedImage bgImage = null;
	private TexturePaint tp = null;
	
	public DrawingSurface surface;
	
	// These will now be grid coordinates rather than location of mouse, so that when zooming, where you initially clicked isn't moving around the grid coordinates
	public Point mls_prevPoint;
	public Point mls_startPoint;
	
	public boolean drawMouse = true;
	
	public static final int MOUSE_COLOUR = Colour.toIntARGB(128, 255, 255, 255);
	public static final int SELECTION_COLOUR = Colour.toIntARGB(128, 255, 255, 255);
	public static final int SELECTION_COLOUR_DARK = Colour.toIntARGB(128, 0, 0, 0);
	public static final int SELECTION_COLOUR_EXTRA_LIGHT = Colour.toIntARGB(192, 255, 255, 255); // 192 is halfway between 128 and 255
	
	public Zoom zoom = null;// = new Zoom(0, 0, 1f); // Need to start at the appropriate zoom level - 1 is not that
	public Rectangle prevRect = null;
	public Point offset = null;
	
//	/**
//	 * Used for nice selection - if the selection was not empty when the mouse was pressed (left click only), this is true. Set to false when the mouse is clicked
//	 */
//	boolean prevSelectionNotEmpty = false;
//	// Both of these will be in terms of the grid
//	public Point selectionMoveOrigin = null;
//	public Point selectionPrevPoint = null;
//	boolean isDraggingSelection = false;
//	boolean isMovingSelectionContent = false;
//	public Integer[][] grabbedPixels = null;
//	public MyMap<Point, Integer> grabbedPixelsMap = new MyMap<Point, Integer>(); // TODO: Delete the MyMap class and rename/remove MyMapEntry too - Use some other map implementation. Hashmaps are fast, but I had problems with them apparently
	// HashMaps were not working like I expected, so I just made my own Map thing. Without extending/implementing Map or anything of course
	/*
	 * Grab pixels - put in map (Point corresponding to position, Integer to colour). Point.equals checks whether the x and y variables are equal, so shouldn't have any nasty behaviour
	 * Move pixels (while dragging) - move pixels in map
	 * Move pixels (stopped dragging) - move pixels in map
	 * Set pixels - get all pixels from map and put them where their Point specifies
	 */
	
	// Need this for drawing pixel-perfect lines (Point = position of pixel, Integer = previousColour of pixel)
//	public ArrayList<MyMapEntry<Point, Integer>> currentStroke;
	
//	public ArrayList<Point> selectPreview = new ArrayList<Point>();
	
	public Point debugPoint = null;
	
	/*
	 * -- NOTES FOR FUTURE --
	 * 
	 * - Adjustable 'Mirrors' : https://github.com/Orama-Interactive/Pixelorama/issues/133
	 * - Make selection persist through to using other tools - make other tools only work in selected region - If doing this would probably have to implement marching ants as a way of showing the selection instead of my current method
	 * - Spline/Bezier tool - Like in GraphicsGale
	 * - Ellipse - Add option to draw from centre
	 * 
	 * -- FIXME : Fix everything
	 */
	
	public Canvas()
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
		
		addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent me)
			{
//				if(prevSelectionNotEmpty && SwingUtilities.isLeftMouseButton(me))
//				{
//					finishSelection();
//					repaint();
//					
//					prevSelectionNotEmpty = false;
//				}
//				
//				prevSelectionNotEmpty = false;
				EditorTools.selectedTool.onMouseClicked(me);
				System.out.println("Click");
			}

			@Override public void mouseEntered(MouseEvent me)
			{
				
			}

			@Override public void mouseExited(MouseEvent me)
			{
				repaint();
			}

			@Override public void mousePressed(MouseEvent me)
			{
//				Point mouseOnGrid = surface.canvasToGrid(me.getX(), me.getY());
//				if(surface.gridSelection.containsKey(new Point(mouseOnGrid.x, mouseOnGrid.y)) && SwingUtilities.isLeftMouseButton(me))
//				{
//					isDraggingSelection = true;
//					selectionMoveOrigin = mouseOnGrid;
//					selectionPrevPoint = mouseOnGrid;
//					
//					if(me.isShiftDown()/* && !isMovingSelectionContent*/)
//					{
//						isMovingSelectionContent = true;
////						grabbedPixels = surface.grabPixels();
//						if(grabbedPixelsMap.isEmpty())
//						{
//							grabbedPixelsMap = surface.getSelectedPixelMap();
//						}
//						else
//						{
//							grabbedPixelsMap.concat(surface.getSelectedPixelMap()); // This means if you are already dragging stuff around, and you put your selection over some pixels, if you left-click + shift you can pick them up as well!
//						}
//					}
//					
//					return;
//				}
//				
//				// This, and what is in mouseClicked makes selection work nicely
//				if(EditorTools.selectedTool.isSelector) // Also added listener for mouseClicked on CanvasContainer so that when you click out of the canvas it clears the selection too
//				{
//					if(!surface.selectionEmpty())
//					{
//						if(SwingUtilities.isLeftMouseButton(me))
//						{
////							surface.clearSelection();
////							if(isMovingSelectionContent)
////							{
//////								surface.setPixels(grabbedPixels, selectionOffset.x, selectionOffset.y);
////								surface.setPixelMap(grabbedPixelsMap);
////								grabbedPixelsMap = null;
////								isMovingSelectionContent = false;
////							}
//							finishSelection();
//							
//							if(EditorTools.selectedTool.triggerType == EditorTools.ToolInfo.ON_PRESS)
//								return;
//							
//							prevSelectionNotEmpty = true;
//						}
//					}
//				}
				
//				if(EditorTools.selectedTool.info.triggerType == EditorTools.ToolInfo.ON_PRESS)
//				{
//					EditorTools.doToolAction_Press(me.getPoint(), me);
//				}
//				else if(EditorTools.selectedTool.info.triggerType == EditorTools.ToolInfo.ON_HOLD)
//				{
//					currentStroke = new ArrayList<MyMapEntry<Point, Integer>>();
//					if(mls_prevPoint != null)
//						EditorTools.doToolAction_Hold(me.getPoint(), mls_prevPoint, me);
//					mls_prevPoint = surface.canvasToGrid(me.getPoint().x, me.getPoint().y);
//				}
//				else if(EditorTools.selectedTool.info.triggerType == EditorTools.ToolInfo.ON_RELEASE)
//				{
//					mls_startPoint = surface.canvasToGrid(me.getPoint().x, me.getPoint().y);
//					EditorTools.updatePreview_Release(mls_startPoint, me.getPoint(), me);
//				}
				EditorTools.selectedTool.onMousePressed(me);
				
				drawMouse = false;
				repaint();
			}

			@Override public void mouseReleased(MouseEvent me)
			{
//				if(isDraggingSelection)
//				{
//					Point mouseOnGrid = surface.canvasToGrid(me.getX(), me.getY());
////					selectionOffset = new Point(mouseOnGrid.x - selectionMoveOrigin.x, mouseOnGrid.y - selectionMoveOrigin.y);
//					
//					int offX = mouseOnGrid.x - selectionPrevPoint.x;
//					int offY = mouseOnGrid.y - selectionPrevPoint.y;
//					
//					surface.updateSelection(offX, offY);
//					if(isMovingSelectionContent)
//					{
//						surface.moveMap(grabbedPixelsMap, offX, offY);
//					}
//					
//					selectionPrevPoint = null;
////					surface.updateSelection(selectionOffset.x, selectionOffset.y);
//					
////					if(isMovingSelectionContent)
////					{
//////						surface.moveBy(grabbedPixels, selectionOffset.x, selectionOffset.y);
////						surface.moveMap(grabbedPixelsMap, selectionOffset.x, selectionOffset.y);
////					}
//					repaint();
//					
//					isDraggingSelection = false;
//					
//					return;
//				}
				
//				if(EditorTools.selectedTool.info.triggerType == EditorTools.ToolInfo.ON_HOLD)
//				{
//					if(mls_prevPoint != null)
//						EditorTools.doToolAction_Hold(me.getPoint(), mls_prevPoint/*surface.gridToCanvas(mls_prevPoint.x, mls_prevPoint.y)*/, me);
//				}
//				else if(EditorTools.selectedTool.info.triggerType == EditorTools.ToolInfo.ON_RELEASE)
//				{
//					if(mls_startPoint != null)
//						EditorTools.doToolAction_Release(mls_startPoint, me.getPoint(), me);
//				}
//				mls_prevPoint = null;
//				mls_startPoint = null;
//				if(currentStroke != null) currentStroke.clear(); // Clear it, because that is always better than setting it to null. Setting things to null is probably very bad practice
				EditorTools.selectedTool.onMouseReleased(me);
				System.out.println("Release");
				
				drawMouse = true;
				repaint();
			}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			@Override public void mouseDragged(MouseEvent me)
			{
//				if(isDraggingSelection)
//				{
//					Point mouseOnGrid = surface.canvasToGrid(me.getX(), me.getY());
//					int offX = mouseOnGrid.x - selectionPrevPoint.x;
//					int offY = mouseOnGrid.y - selectionPrevPoint.y;
//					surface.updateSelection(offX, offY);
////					if(isMovingSelectionContent)
////					{
////						surface.moveMap(grabbedPixelsMap, offX, offY); // This seems to do double move, if that makes sense
////					}
//					selectionPrevPoint = mouseOnGrid;
//					
//					repaint();
//					
//					return;
//				}
				
//				if(EditorTools.selectedTool.info.triggerType == EditorTools.ToolInfo.ON_HOLD)
//				{
//					if(mls_prevPoint != null)
//						EditorTools.doToolAction_Hold(me.getPoint(), mls_prevPoint, me);
//					mls_prevPoint = surface.canvasToGrid(me.getPoint().x, me.getPoint().y);
//					repaint();
//				}
//				else if(EditorTools.selectedTool.info.triggerType == EditorTools.ToolInfo.ON_RELEASE)
//				{
//					if(mls_startPoint != null)
//						EditorTools.updatePreview_Release(mls_startPoint, me.getPoint(), me);
//					repaint();
//				}
				EditorTools.selectedTool.onMouseDragged(me);
				
				repaint();
			}

			@Override public void mouseMoved(MouseEvent me)
			{
				repaint();
			}
		});
		
		// Implement shortcuts for deleting the pixels under the current selection
		Action deleteSelection = new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e)
			{
				for(Point p : SelectorTool.selection)
				{
					if(surface.contains(p.x, p.y))
					{
						surface.gridColours[p.x][p.y]= Colour.TRANSPARENT;
					}
				}
				repaint();
			}
		};
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"), "DeleteSelection");
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("BACK_SPACE"), "DeleteSelection");
		getActionMap().put("DeleteSelection", deleteSelection);
		
		// While this does work, it restarts this instance rather than running a new one, except it does't close this window
		// What I want is something to actually run a new instance of this program in a new JVM
		// Runtime.getRuntime().exec(String) seems promising (where String is what is run in the command line)
//		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control N"), "StartNew");
//		getActionMap().put("StartNew", new AbstractAction() {
//			@Override public void actionPerformed(ActionEvent e)
//			{
//				// Main.main(new String[0]);
//				try
//				{
//					Process proc = Runtime.getRuntime().exec("xed"); // Runs xed (Text Editor)
//				} catch (IOException e1)
//				{
//					e1.printStackTrace();
//				}
//			}
//		});
	}
	
//	/**
//	 * Finishes the selection - clears the selection, sets the appropriate variables, places and clears any dragged content, stuff like that
//	 */
//	public void finishSelection()
//	{
//		surface.clearSelection();
//		if(grabbedPixelsMap != null)
//		{
//			surface.setPixelMap(grabbedPixelsMap);
//			grabbedPixelsMap.clear();
//			isMovingSelectionContent = false;
//		}
//		isDraggingSelection = false;
//	}
	
	public void resetBounds()
	{
		setBounds(0, 0, 0, 0);
	}
	
	@Override public void setBounds(int x, int y, int width, int height) // How well this works is almost magical
	{
		// If zoom is null, create a new Zoom object with the biggest scale that fits the available space
		if(zoom == null)
		{
			float scale = Helper.bestScale(new Rectangle(0, 0, Main.canvasPanel.getWidth(), Main.canvasPanel.getHeight()), new Rectangle(0, 0, surface.gridWidth, surface.gridHeight));
			zoom = new Zoom(0, 0, scale, this);
		}
		
		// Zoom in by nothing so that if the offsets aren't where they are supposed to be, this rectifies it
		zoom.zoom(0, 0, zoom.scale);
		
		// Set the x, y to the zoom offsets
		x = zoom.offX;
		y = zoom.offY;
		// Set the width and height to the scaled dimensions. Don't need to calculate them again if they have already been calculated
		width = zoom.prevDims.width;
		height = zoom.prevDims.height;
		
		// Call setBounds in the superclass to actually have the changes take effect
		super.setBounds(x, y, width, height);
	}
	
	private int calcColour(int initCol, boolean light, boolean extraLight)
	{
		// Using Colour.compose is so much faster than getting java to do the alpha composing, which begs the question, what is java doing??
		if(extraLight)
		{
			return Colour.composite(initCol, SELECTION_COLOUR_EXTRA_LIGHT);
		}
		else if(light)
		{
			int argb = Colour.composite(initCol, SELECTION_COLOUR);

			return argb;
		}
		else // If dark
		{
			int argb = Colour.composite(initCol, SELECTION_COLOUR_DARK);
			
			return argb;
		}
	}
	
	// Performance is currently fine
	@Override public void paintComponent(Graphics g)
	{
		// TODO: Fix - When zoomed in very far into a larger image, pixels stop being drawn, if your view is far enough away from the top-left corner of the canvas
		
		// NOTE: DO NOT use VolatileImage, had some periods of extreme slowness when doing that
		
		BufferedImage bfi = new BufferedImage(surface.gridWidth, surface.gridHeight, BufferedImage.TYPE_INT_ARGB);
		BufferedImage navImg = new BufferedImage(surface.gridWidth, surface.gridHeight, BufferedImage.TYPE_INT_ARGB);
		bfi.setAccelerationPriority(1); // Make it of the utmost importance to use all available resources to 'accelerate' this image (see javadoc for method for details)
		
		int[] imgData = ((DataBufferInt)bfi.getRaster().getDataBuffer()).getData();
		int[] navImgData = ((DataBufferInt)navImg.getRaster().getDataBuffer()).getData();
		
		// int index = x + y * width; // The equation for getting the 1D index from a 2D array
		
		Graphics2D g2d = bfi.createGraphics();
		
		// Use the TexturePaint to paint the background onto the canvas, tiling it if necessary
		Graphics2D g2dc = (Graphics2D)g;
		g2dc.setPaint(tp);
		g2dc.fillRect(0, 0, getWidth(), getHeight());
		
		// Create a matrix of all selected points
		boolean[][] selected = new boolean[surface.gridWidth][surface.gridHeight];
		boolean[][] selectPrev = new boolean[surface.gridWidth][surface.gridHeight];
		boolean[][] grabbed = new boolean[surface.gridWidth][surface.gridHeight];
		
		ArrayList<MyMapEntry<Point, Integer>> grabbedList = SelectorTool.grabbedPixelsMap;
		
		ArrayList<Point> lassoPath = FreeSelection.lassoPath;
		ArrayList<Point> pathClose = FreeSelection.pathClose;
		
		// Just doing this to avoid doing multiple loops, which would most likely be slightly slower and more code
		int loopTo = (int)Helper.getMax(SelectorTool.selection.size(), FreeSelection.lassoPath.size(), FreeSelection.pathClose.size(), SelectorTool.selectionPreview.size(), grabbedList.size());
		for(int i = 0; i < loopTo; i++)
		{
			// Add all points from the relevant ArrayLists to the relevant 2d arrays/matrices
			if(i < lassoPath.size())
			{
				int x = lassoPath.get(i).x;
				int y = lassoPath.get(i).y;
				if(surface.contains(x, y))
				{
					selectPrev[x][y] = true;
				}
			}
			if(i < pathClose.size())
			{
				int x = pathClose.get(i).x;
				int y = pathClose.get(i).y;
				if(surface.contains(x, y))
				{
					selectPrev[x][y] = true;
				}
			}
			if(i < SelectorTool.selection.size())
			{
				// Cloning because otherwise if we apply the offset it changes the original
				Point p = (Point)SelectorTool.selection.get(i).clone();
				if(SelectorTool.selectionOffset != null)
				{
					p.x += SelectorTool.selectionOffset.x;
					p.y += SelectorTool.selectionOffset.y;
				}
				if(surface.contains(p.x, p.y))
				{
					selected[p.x][p.y] = true; 
				}
			}
			if(i < SelectorTool.selectionPreview.size())
			{
				int x = SelectorTool.selectionPreview.get(i).x;
				int y = SelectorTool.selectionPreview.get(i).y;
				if(surface.contains(x, y))
				{
					selectPrev[x][y] = true;
				}
			}
			if(i < grabbedList.size())
			{
				Point p = (Point)grabbedList.get(i).getKey().clone();
				if(SelectorTool.selectionOffset != null)
				{
					p.x += SelectorTool.selectionOffset.x;
					p.y += SelectorTool.selectionOffset.y;
				}
				if(surface.contains(p.x, p.y))
				{
					grabbed[p.x][p.y] = true;
				}
			}
		}
		
		// Check zoom is not null
		if(zoom != null)// && zoom.scale != 1f)
		{
			for(int i = 0; i < surface.gridWidth; i++)
			{
				for(int j = 0; j < surface.gridHeight; j++)
				{
					int posX = i;
					int posY = j;
					
					// If using the move tool (canvasContentOffset is not null) then shift the positions by the offset
					if(Move.canvasContentOffset != null)
					{
						posX = i - Move.canvasContentOffset.x;
						posY = j - Move.canvasContentOffset.y;
						
						// Return if we are out of bounds
						if(!surface.contains(posX, posY))
							continue;
					}
					
					if(Colour.getAlpha(surface.gridColours[posX][posY]) != 0) // That sped up performance a lot more than I thought it would
					{
						// Write directly to the Raster data - Definitely some speed increase there
						int index = i + j * surface.gridWidth; // Get index in the data array

						imgData[index] = surface.gridColours[posX][posY];
						
						// Set pixel in navImg - what we're going to be giving the navigator to draw
						navImgData[index] = imgData[index];
					}
					if(Colour.getAlpha(surface.gridOverlay[posX][posY]) != 0)
					{
						// The reason we SET the colour here is because that gives a much better 'preview' of what will happen
						int index = i + j * surface.gridWidth; // Get index in the data array

						imgData[index] = surface.gridOverlay[posX][posY];
					}
					
					boolean doDraw = false;
					boolean dark = false;
					boolean light = false;
					boolean extraLight = false;
					
					// Selected shouldn't be affected, as there shouldn't be any selection when using Move tool. May change this in future
					if(selected[i][j]) // We really want to be SETTING the colour of the pixels here. So lets make up some way of doing that
					{
						if(grabbed[i][j])
							dark = true;
						else
							light = true;
						doDraw = true;
					}
					if(selectPrev[i][j])
					{
						if(light)
							extraLight = true;
						else if(!dark)
							light = true;
						doDraw = true;
					}
					
					if(doDraw)
					{
						int index = i + j * surface.gridWidth; // Get index in the data array
						
						int col = calcColour(imgData[index], light, extraLight);
						
						imgData[index] = col;
					}
				}
			}
			
			if(debugPoint != null)
			{
				int i = debugPoint.x;
				int j = debugPoint.y;
				
				Helper.drawPoint(imgData, i + j * surface.gridWidth, Colour.toIntARGB(128, 255, 0, 0), true);
			}
		}
		
		if(drawMouse)
			showMousePosition(g2d, imgData);
		
		// Just use the Graphics2D.scale method instead of messing around with all the image resizing and stuff
		// It automatically does all the clipping and stuff as well, so this is the best performance option for drawing the scaled image
		g2dc.scale(zoom.scale, zoom.scale);
		g2dc.drawImage(bfi, 0, 0, null);
		
		if(!Helper.compareMatrices(surface.gridColours, NavigatorPanel.navigator.gridColours))
		{
			// Set the grid and repaint the navigator
			NavigatorPanel.navigator.setGrid(surface.gridWidth, surface.gridHeight, surface.gridColours);
			NavigatorPanel.navigator.img = navImg;
			NavigatorPanel.navigator.repaint();
		}
		
		g2d.dispose();
	}
	
	private void showMousePosition(Graphics2D g2d, int[] imgData)
	{
		Point mouse = getMousePosition();
		if(mouse != null)
		{
			Point p = surface.canvasToGrid(mouse.x, mouse.y);
			
			if(EditorTools.brushSize == 1 || !EditorTools.selectedTool.sizeMatters)
			{
				Helper.drawPoint(imgData, p.x + p.y * surface.gridWidth, MOUSE_COLOUR, true);
			}
			else
			{
				if(EditorTools.brushSize > 1 && EditorTools.brushSize <= 5)
				{
					if(EditorTools.selectedTool.settings.circleBrush && EditorTools.brushSize > 2)
					{
						int startX = p.x - (int)Math.floor((float)(EditorTools.brushSize - 1) / 2f);
						int startY = p.y - (int)Math.floor((float)(EditorTools.brushSize - 1) / 2f);
						
						Point[] points = Helper.circleInBounds(startX, startY, EditorTools.brushSize - 1, EditorTools.brushSize - 1, true);
						
						for(int i = 0; i < points.length; i++)
						{
							if(points[i].x >= 0 && points[i].x < surface.getGridDims().width && points[i].y >= 0 && points[i].y < surface.getGridDims().height)
							{
								// g2d.fillRect(pos.x, pos.y, width, height);
								Helper.drawPoint(imgData, points[i].x + points[i].y * surface.gridWidth, MOUSE_COLOUR, true);
							}
						}
					}
					else
					{
						int i = p.x;
						int j = p.y;
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
									Helper.drawPoint(imgData, x + y * surface.gridWidth, MOUSE_COLOUR, true);
								}
							}
						}
					}
				}
			}
		}
	}
}