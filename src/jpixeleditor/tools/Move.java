package jpixeleditor.tools;

import java.awt.Point;
import java.awt.event.MouseEvent;

import jpixeleditor.tools.Tool.MouseButton;
import jpixeleditor.ui.Canvas;
import jpixeleditor.ui.Canvas.DrawingSurface;
import jpixeleditor.ui.CanvasContainer;
import jpixeleditor.utils.Colour;

public class Move extends Tool
{
	public static Point canvasContentOffset = null;// If null, not using Move tool to move canvas content
	
	public Move(int id)
	{
		super(id);
		
		name = "Move";
		description = "Moves the canvas content\nWhile dragging with one button, click with the other to return to original position";
		keyShortcut = "M";
		iconPath = "/Hand.png";
	}
	
	@Override public void onMouseReleased(MouseEvent me)
	{
		super.onMouseReleased(me);
		
		if(currMouseButton != MouseButton.RIGHT && currMouseButton != MouseButton.LEFT)
			return;
		
		Canvas canvas = CanvasContainer.canvas;
		DrawingSurface surface = canvas.surface;
		
		Point offset = new Point(curr.x - start.x, curr.y - start.y);
		
		// Temporary array to store the translated grid - can't put it immediately into surface.gridColours because then we might be deleting data
		int[][] temp = new int[surface.gridWidth][surface.gridHeight];
		// Quick 2d loop to move all the grid colours by the offset
		for(int i = 0; i < surface.gridWidth; i++)
		{
			for(int j = 0; j < surface.gridHeight; j++)
			{
				// Find where we'll be getting the colour for this pixel from
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
		
		canvasContentOffset = null;
	}
	
	@Override public void onMouseDragged(MouseEvent me)
	{
		super.onMouseDragged(me);
		
		if(currMouseButton != MouseButton.RIGHT && currMouseButton != MouseButton.LEFT)
			return;
		
		canvasContentOffset = new Point(curr.x - start.x, curr.y - start.y);
	}
}
