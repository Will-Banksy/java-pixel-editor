package jpixeleditor.tools;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import jpixeleditor.main.Main;
import jpixeleditor.tools.PaintHandler.DrawTo;
import jpixeleditor.ui.Canvas;
import jpixeleditor.ui.CanvasContainer;
import jpixeleditor.ui.ColourSelectorPanel;
import jpixeleditor.ui.Canvas.DrawingSurface;
import jpixeleditor.ui.ToolConfigPanel.ToolSettings;

public class EditorTools
{
	public static class ToolInfo
	{
		public static final int ON_RELEASE = 0; // Does action once when mouse is released
		public static final int ON_HOLD = 1; // Does action continually when mouse is held
		public static final int ON_PRESS = 2; // Does action once when mouse is pressed
		
		public static final int ID_PENCIL = 0;
		public static final int ID_ERASER = 1;
		public static final int ID_BUCKET = 2;
		public static final int ID_REPLACE = 3;
		public static final int ID_LINE = 4;
		public static final int ID_RECTANGLE = 5;
		public static final int ID_ELLIPSE = 6;
		public static final int ID_PIPETTE = 7;
		public static final int ID_SELECT_RECT = 8;
		public static final int ID_SELECT_FREE = 9;
		public static final int ID_SELECT_MAGIC = 10;
		public static final int ID_SELECT_COLOUR = 11;
		public static final int ID_MOVE = 12;
		public static final int ID_DITHER = 13;
		
		public int id;
		public String name;
		public int triggerType;
		public boolean sizeMatters; // This defines whether the mouse shows as the brush size on the grid or as a single pixel regardless of brush size
		public boolean isSelector; // Is this tool used to select stuff? (Ex. Rectangular Selection Tool)
		
		public ToolSettings settings = null; // The config settings for the tool
		
		public ToolInfo(int id, String name, int triggerType, boolean sizeMatters, boolean isSelector)
		{
			this.id = id;
			this.name = name;
			this.triggerType = triggerType;
			this.sizeMatters = sizeMatters;
			this.isSelector = isSelector;
			
			settings = new ToolSettings();
		}
		
		public void setSettings(ToolSettings settings)
		{
			this.settings = settings;
		}
	}
	
	public Canvas canvas;
	
	public EditorTools(Canvas canvas)
	{
		this.canvas = canvas;
	}
	
	public static final int NUM_TOOLS = 14;
	
	public static final ToolInfo TOOL_PENCIL = new ToolInfo(ToolInfo.ID_PENCIL, "Pencil", ToolInfo.ON_HOLD, true, false);
	public static final ToolInfo TOOL_ERASER = new ToolInfo(ToolInfo.ID_ERASER, "Eraser", ToolInfo.ON_HOLD, true, false);
	public static final ToolInfo TOOL_BUCKET = new ToolInfo(ToolInfo.ID_BUCKET, "Fill Bucket", ToolInfo.ON_PRESS, false, false);
	public static final ToolInfo TOOL_REPLACE = new ToolInfo(ToolInfo.ID_REPLACE, "Colour Replace", ToolInfo.ON_PRESS, false, false);
	public static final ToolInfo TOOL_LINE = new ToolInfo(ToolInfo.ID_LINE, "Line", ToolInfo.ON_RELEASE, true, false);
	public static final ToolInfo TOOL_RECTANGLE = new ToolInfo(ToolInfo.ID_RECTANGLE, "Rectangle", ToolInfo.ON_RELEASE, true, false);
	public static final ToolInfo TOOL_ELLIPSE = new ToolInfo(ToolInfo.ID_ELLIPSE, "Ellipse", ToolInfo.ON_RELEASE, true, false);
	public static final ToolInfo TOOL_PIPETTE = new ToolInfo(ToolInfo.ID_PIPETTE, "Pipette", ToolInfo.ON_PRESS, false, false);
	public static final ToolInfo TOOL_SELECT_RECT = new ToolInfo(ToolInfo.ID_SELECT_RECT, "Rectangular Selection", ToolInfo.ON_RELEASE, false, true);
	public static final ToolInfo TOOL_SELECT_FREE = new ToolInfo(ToolInfo.ID_SELECT_FREE, "Free Selection", ToolInfo.ON_RELEASE, false, true);
	public static final ToolInfo TOOL_SELECT_MAGIC = new ToolInfo(ToolInfo.ID_SELECT_MAGIC, "Magic Selection", ToolInfo.ON_PRESS, false, true);
	public static final ToolInfo TOOL_SELECT_COLOUR = new ToolInfo(ToolInfo.ID_SELECT_COLOUR, "Magic Colour Selection", ToolInfo.ON_PRESS, false, true);
	public static final ToolInfo TOOL_MOVE = new ToolInfo(ToolInfo.ID_MOVE, "Move", ToolInfo.ON_RELEASE, false, false);
	public static final ToolInfo TOOL_DITHER = new ToolInfo(ToolInfo.ID_DITHER, "Dither", ToolInfo.ON_HOLD, true, false);
	
	// Defaults
	public static ToolInfo selectedTool = TOOL_PENCIL;
	public static int primaryColour = Colour.BLACK;
	public static int secondaryColour = Colour.TRANSPARENT;
	
	public static int brushSize = 1;
	
	public static void switchTool(int toolID)
	{
		selectedTool = getByID(toolID);
		Main.configPanel.switchPanel(toolID);
	}
	
	public static ToolInfo getByID(int id)
	{
		switch(id)
		{
			case ToolInfo.ID_PENCIL:
				return TOOL_PENCIL;
			
			case ToolInfo.ID_ERASER:
				return TOOL_ERASER;
			
			case ToolInfo.ID_BUCKET:
				return TOOL_BUCKET;
			
			case ToolInfo.ID_REPLACE:
				return TOOL_REPLACE;
			
			case ToolInfo.ID_LINE:
				return TOOL_LINE;
			
			case ToolInfo.ID_RECTANGLE:
				return TOOL_RECTANGLE;
			
			case ToolInfo.ID_ELLIPSE:
				return TOOL_ELLIPSE;
				
			case ToolInfo.ID_PIPETTE:
				return TOOL_PIPETTE;
				
			case ToolInfo.ID_SELECT_RECT:
				return TOOL_SELECT_RECT;
				
			case ToolInfo.ID_SELECT_FREE:
				return TOOL_SELECT_FREE;
				
			case ToolInfo.ID_SELECT_MAGIC:
				return TOOL_SELECT_MAGIC;
				
			case ToolInfo.ID_SELECT_COLOUR:
				return TOOL_SELECT_COLOUR;
				
			case ToolInfo.ID_MOVE:
				return TOOL_MOVE;
				
			case ToolInfo.ID_DITHER:
				return TOOL_DITHER;
				
			default:
				throw new IllegalArgumentException("'id' is not a value that corresponds to a tool id");
		}
	}
	
	public static void doToolAction_Press(Point pos, MouseEvent me)
	{
		switch(selectedTool.id)
		{
			case ToolInfo.ID_BUCKET:
				if(true) // So I can reuse variable names
				{
					// All this code is pulled straight from a previous attempt at a pixel editor
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					Point posOnGrid = surface.canvasToGrid(pos.x, pos.y);
					int col = primaryColour;
					if(SwingUtilities.isRightMouseButton(me))
					{
						col = secondaryColour;
					}
					else if(!SwingUtilities.isLeftMouseButton(me))
					{
						break;
					}
					
					PaintHandler.fill(posOnGrid, col, selectedTool.settings);
				}
				break;
			
			case ToolInfo.ID_REPLACE:
				if(true)
				{
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					Point posOnGrid = surface.canvasToGrid(pos.x, pos.y);
					int col = primaryColour;
					if(SwingUtilities.isRightMouseButton(me))
					{
						col = secondaryColour;
					}
					else if(!SwingUtilities.isLeftMouseButton(me))
					{
						break;
					}
					
					PaintHandler.replace(posOnGrid, col, selectedTool.settings);
				}
				break;
				
			case ToolInfo.ID_PIPETTE:
				if(true)
				{
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					Point posOnGrid = surface.canvasToGrid(pos.x, pos.y);
					
					int newCol = surface.gridColours[posOnGrid.x][posOnGrid.y];
					
					if(SwingUtilities.isLeftMouseButton(me))
					{
						primaryColour = newCol;
						ColourSelectorPanel.csb1.repaint();
					}
					else if(SwingUtilities.isRightMouseButton(me))
					{
						secondaryColour = newCol;
						ColourSelectorPanel.csb2.repaint();
					}
				}
				break;
				
			case ToolInfo.ID_SELECT_MAGIC:
				if(true)
				{
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					Point posOnGrid = surface.canvasToGrid(pos.x, pos.y);
					
					if(SwingUtilities.isLeftMouseButton(me))
					{
						SelectionHandler.magicSelect(posOnGrid.x, posOnGrid.y, false, EditorTools.selectedTool.settings);
					}
					else if(SwingUtilities.isRightMouseButton(me))
					{
						SelectionHandler.magicSelect(posOnGrid.x, posOnGrid.y, true, EditorTools.selectedTool.settings);
					}
				}
				break;
				
			case ToolInfo.ID_SELECT_COLOUR:
				if(true)
				{
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					Point posOnGrid = surface.canvasToGrid(pos.x, pos.y);
					
					if(SwingUtilities.isLeftMouseButton(me))
					{
						SelectionHandler.colourSelect(posOnGrid, false, EditorTools.selectedTool.settings);
					}
					else if(SwingUtilities.isRightMouseButton(me))
					{
						SelectionHandler.colourSelect(posOnGrid, true, EditorTools.selectedTool.settings);
					}
				}
				break;
		}
	}
	
	public static void doToolAction_Release(Point startPos, Point endPos, MouseEvent me)
	{
		switch(selectedTool.id)
		{
			case ToolInfo.ID_LINE:
				if(true)
				{
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					int col = primaryColour;
					if(SwingUtilities.isRightMouseButton(me))
					{
						col = secondaryColour;
					}
					else if(!SwingUtilities.isLeftMouseButton(me))
					{
						break;
					}
					
					surface.clearOverlay();
					
					Point startOnGrid = startPos;//surface.canvasToGrid(startPos.x, startPos.y);
					Point endOnGrid = surface.canvasToGrid(endPos.x, endPos.y);
					
					PaintHandler.drawLine(startOnGrid, endOnGrid, col, DrawTo.CANVAS, brushSize, selectedTool.settings);
				}
				break;
			
			case ToolInfo.ID_RECTANGLE:
				if(true)
				{
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					int col = primaryColour;
					if(SwingUtilities.isRightMouseButton(me))
					{
						col = secondaryColour;
					}
					else if(!SwingUtilities.isLeftMouseButton(me))
					{
						break;
					}
					
					surface.clearOverlay();
					
					Point startOnGrid = startPos;//surface.canvasToGrid(startPos.x, startPos.y);
					Point endOnGrid = surface.canvasToGrid(endPos.x, endPos.y);
					
					PaintHandler.drawRectangle(startOnGrid, endOnGrid, col, DrawTo.CANVAS, brushSize, selectedTool.settings);
				}
				break;
			
			case ToolInfo.ID_ELLIPSE:
				if(true)
				{
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					int col = primaryColour;
					if(SwingUtilities.isRightMouseButton(me))
					{
						col = secondaryColour;
					}
					else if(!SwingUtilities.isLeftMouseButton(me))
					{
						break;
					}
					
					surface.clearOverlay();
					
					Point startOnGrid = startPos;//surface.canvasToGrid(startPos.x, startPos.y);
					Point endOnGrid = surface.canvasToGrid(endPos.x, endPos.y);
					
					PaintHandler.drawEllipse(startOnGrid, endOnGrid, col, DrawTo.CANVAS, brushSize, selectedTool.settings);
				}
				break;
			
			case ToolInfo.ID_SELECT_RECT:
				if(true)
				{
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					
					boolean remove = false;
					if(SwingUtilities.isRightMouseButton(me))
					{
						remove = true;
					}
					else if(!SwingUtilities.isLeftMouseButton(me))
					{
						break;
					}
					
					surface.clearOverlay();
					
					Point startOnGrid = startPos;//surface.canvasToGrid(startPos.x, startPos.y);
					Point endOnGrid = surface.canvasToGrid(endPos.x, endPos.y);
					
					SelectionHandler.drawSelectionRectangle(startOnGrid, endOnGrid, remove, false, selectedTool.settings);
				}
				break;
				
			case ToolInfo.ID_SELECT_FREE:
				if(true)
				{
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					
					if(!SwingUtilities.isLeftMouseButton(me) && !SwingUtilities.isRightMouseButton(me))
					{
						break;
					}
					
					Point startOnGrid = startPos;
					Point endOnGrid = surface.canvasToGrid(endPos.x, endPos.y);
					
					SelectionHandler.drawFreeSelection(startOnGrid, endOnGrid, SwingUtilities.isRightMouseButton(me), false, selectedTool.settings);
				}
				break;
				
			case ToolInfo.ID_MOVE:
				if(true)
				{
					Canvas canvas = CanvasContainer.canvas;
					DrawingSurface surface = CanvasContainer.canvas.surface;
					
					Point startOnGrid = startPos;
					Point endOnGrid = surface.canvasToGrid(endPos.x, endPos.y);
					
					Point offset = new Point(endOnGrid.x - startOnGrid.x, endOnGrid.y - startOnGrid.y);
					
					// Won't call a PaintHandler method for this - I'll just do it here
					
					int[][] temp = new int[surface.gridWidth][surface.gridHeight];
					for(int i = 0; i < surface.gridWidth; i++)
					{
						for(int j = 0; j < surface.gridHeight; j++)
						{
							int newI = i - offset.x;
							int newJ = j - offset.y;
							
							if(surface.contains(newI, newJ))
							{
								temp[i][j] = surface.gridColours[newI][newJ];
							}
							else
							{
								temp[i][j] = Colour.TRANSPARENT;
							}
						}
					}
					surface.gridColours = temp;
					
					canvas.canvasContentOffset = null;
				}
				break;
		}
	}
	
	public static void doToolAction_Hold(Point currentPos, Point prevPos, MouseEvent me)
	{
		switch(selectedTool.id)
		{
			case ToolInfo.ID_PENCIL:
				if(true)
				{
					// Select the colour based on what button is being pressed
					int col = primaryColour;
					if(SwingUtilities.isRightMouseButton(me))
					{
						col = secondaryColour;
					}
					else if(!SwingUtilities.isLeftMouseButton(me))
					{
						break;
					}
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					Point currOnGrid = surface.canvasToGrid(currentPos.x, currentPos.y);
					
					if(prevPos == null)
					{
						// If there is no previous point, just paint a single pixel at the mouse
						PaintHandler.paint(currOnGrid.x, currOnGrid.y, col, DrawTo.CANVAS, brushSize, getByID(ToolInfo.ID_PENCIL).settings.circleBrush);
					}
					else
					{
						// Else, draw a line between the current and previous position
						Point prevOnGrid = prevPos;//surface.canvasToGrid(prevPos.x, prevPos.y);
						
						if(getByID(ToolInfo.ID_PENCIL).settings.pixelPerfect)
						{
							PaintHandler.drawLine_PixelPerfect(prevOnGrid, currOnGrid, col, DrawTo.CANVAS, brushSize, getByID(ToolInfo.ID_PENCIL).settings);
						}
						else
						{
							PaintHandler.drawLine(prevOnGrid, currOnGrid, col, DrawTo.CANVAS, brushSize, getByID(ToolInfo.ID_PENCIL).settings);
						}
					}
				}
				break;
			
			case ToolInfo.ID_ERASER:
				if(true)
				{
					int col1 = Colour.TRANSPARENT;
					Canvas.DrawingSurface surface1 = CanvasContainer.canvas.surface;
					Point currOnGrid1 = surface1.canvasToGrid(currentPos.x, currentPos.y);
					
					if(prevPos == null)
					{
						// If there is no previous point, just paint a single pixel at the mouse
						PaintHandler.paint(currOnGrid1.x, currOnGrid1.y, col1, DrawTo.CANVAS, brushSize, getByID(ToolInfo.ID_ERASER).settings.circleBrush);
					}
					else
					{
						// Else, get a line of grid coordinates from prevOnGrid to currOnGrid and paint all of those
						Point prevOnGrid = prevPos;//surface1.canvasToGrid(prevPos.x, prevPos.y);
						
						PaintHandler.drawLine(prevOnGrid, currOnGrid1, col1, DrawTo.CANVAS, brushSize, getByID(ToolInfo.ID_ERASER).settings);
					}
				}
				break;
				
			case ToolInfo.ID_DITHER:
				if(true)
				{
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					int col1 = EditorTools.primaryColour;
					int col2 = EditorTools.secondaryColour;
					
					Point currOnGrid = surface.canvasToGrid(currentPos.x, currentPos.y);
					Point prevOnGrid;
					
					if(prevPos == null)
						prevOnGrid = currOnGrid;
					else
						prevOnGrid = prevPos;
					
					if(SwingUtilities.isLeftMouseButton(me))
					{
						PaintHandler.drawLine_Dither(prevOnGrid, currOnGrid, col1, col2, false, EditorTools.brushSize, EditorTools.selectedTool.settings);
					}
					else if(SwingUtilities.isRightMouseButton(me))
					{
						PaintHandler.drawLine_Dither(prevOnGrid, currOnGrid, col1, col2, true, EditorTools.brushSize, EditorTools.selectedTool.settings);
					}
				}
				break;
		}
	}
	
	public static void updatePreview_Release(Point startPos, Point endPos, MouseEvent me)
	{
		switch(selectedTool.id)
		{
			case ToolInfo.ID_LINE:
				if(true)
				{
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					int col = primaryColour;
					if(SwingUtilities.isRightMouseButton(me))
					{
						col = secondaryColour;
					}
					else if(!SwingUtilities.isLeftMouseButton(me))
					{
						break;
					}
					if(Colour.getAlpha(col) == 0)
					{
						col = Colour.toIntARGB(128, 255, 255, 255);
					}
					
					surface.clearOverlay();
					
					Point startOnGrid = startPos;//surface.canvasToGrid(startPos.x, startPos.y);
					Point endOnGrid = surface.canvasToGrid(endPos.x, endPos.y);
					
					PaintHandler.drawLine(startOnGrid, endOnGrid, col, DrawTo.OVERLAY, brushSize, selectedTool.settings);
				}
				break;
			
			case ToolInfo.ID_RECTANGLE:
				if(true)
				{
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					int col = primaryColour;
					if(SwingUtilities.isRightMouseButton(me))
					{
						col = secondaryColour;
					}
					else if(!SwingUtilities.isLeftMouseButton(me))
					{
						break;
					}
					if(Colour.getAlpha(col) == 0)
					{
						col = Colour.toIntARGB(128, 255, 255, 255);
					}
					
					surface.clearOverlay();
					
					Point startOnGrid = startPos;//surface.canvasToGrid(startPos.x, startPos.y);
					Point endOnGrid = surface.canvasToGrid(endPos.x, endPos.y);
					
					PaintHandler.drawRectangle(startOnGrid, endOnGrid, col, DrawTo.OVERLAY, brushSize, selectedTool.settings);
				}
				break;
			
			case ToolInfo.ID_ELLIPSE:
				if(true)
				{
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					int col = primaryColour;
					if(SwingUtilities.isRightMouseButton(me))
					{
						col = secondaryColour;
					}
					else if(!SwingUtilities.isLeftMouseButton(me))
					{
						break;
					}
					if(Colour.getAlpha(col) == 0)
					{
						col = Colour.toIntARGB(128, 255, 255, 255);
					}
					
					surface.clearOverlay();
					
					Point startOnGrid = startPos;//surface.canvasToGrid(startPos.x, startPos.y);
					Point endOnGrid = surface.canvasToGrid(endPos.x, endPos.y);
					
					PaintHandler.drawEllipse(startOnGrid, endOnGrid, col, DrawTo.OVERLAY, brushSize, selectedTool.settings);
				}
				break;
			
			case ToolInfo.ID_SELECT_RECT:
				if(true)
				{
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					
					if(!SwingUtilities.isLeftMouseButton(me) && !SwingUtilities.isRightMouseButton(me))
					{
						break;
					}
					
					surface.clearOverlay();
					
					Point startOnGrid = startPos;//surface.canvasToGrid(startPos.x, startPos.y);
					Point endOnGrid = surface.canvasToGrid(endPos.x, endPos.y);
					
					SelectionHandler.drawSelectionRectangle(startOnGrid, endOnGrid, false, true, selectedTool.settings);
				}
				break;
				
			case ToolInfo.ID_SELECT_FREE:
				if(true)
				{
					Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
					
					if(!SwingUtilities.isLeftMouseButton(me) && !SwingUtilities.isRightMouseButton(me))
					{
						break;
					}
					
					Point startOnGrid = startPos;
					Point endOnGrid = surface.canvasToGrid(endPos.x, endPos.y);
					
					SelectionHandler.drawFreeSelection(startOnGrid, endOnGrid, SwingUtilities.isRightMouseButton(me), true, selectedTool.settings);
				}
				break;
				
			case ToolInfo.ID_MOVE:
				if(true)
				{
					Canvas canvas = CanvasContainer.canvas;
					
					Point startOnGrid = startPos;
					Point endOnGrid = canvas.surface.canvasToGrid(endPos.x, endPos.y);
					
					canvas.canvasContentOffset = new Point(endOnGrid.x - startOnGrid.x, endOnGrid.y - startOnGrid.y);
				}
		}
	}
}
