package jpixeleditor.tools;

import java.awt.event.MouseEvent;

import jpixeleditor.tools.Tool.MouseButton;
import jpixeleditor.ui.Canvas;
import jpixeleditor.utils.Colour;
import jpixeleditor.utils.EditorTools;
import jpixeleditor.utils.PaintHandler;
import jpixeleditor.utils.PaintHandler.DrawTo;

public class Rectangle extends Tool
{
	public Rectangle(int id)
	{
		super(id);
		
		name = "Rectangle";
		description = "Draws rectangles";
		keyShortcut = "R";
		iconPath = "/Rectangle.png";
		sizeMatters = true;
	}
	
	@Override public void onMousePressed(MouseEvent me)
	{
		super.onMousePressed(me);
		
		if(currMouseButton != MouseButton.RIGHT && currMouseButton != MouseButton.LEFT)
			return;
		
		int col = getColour();
		
		if(Colour.getAlpha(col) == 0)
		{
			col = Canvas.SELECTION_COLOUR;
		}
		
		boolean changed1to1 = false;
		if(me.isShiftDown() && !settings.oneToOneRatio)
		{
			settings.oneToOneRatio = true;
			changed1to1 = true;
		}
		
		PaintHandler.paint(curr.x, curr.y, col, DrawTo.OVERLAY, EditorTools.brushSize, false);
		
		if(changed1to1)
			settings.oneToOneRatio = false;
	}
	
	@Override public void onMouseDragged(MouseEvent me)
	{
		super.onMouseDragged(me);
		
		if(currMouseButton != MouseButton.RIGHT && currMouseButton != MouseButton.LEFT)
			return;
		
		int col = getColour();
		
		if(Colour.getAlpha(col) == 0)
		{
			col = Canvas.SELECTION_COLOUR;
		}
		
		boolean changed1to1 = false;
		if(me.isShiftDown() && !settings.oneToOneRatio)
		{
			settings.oneToOneRatio = true;
			changed1to1 = true;
		}
		
		PaintHandler.drawRectangle(start, curr, col, DrawTo.OVERLAY, EditorTools.brushSize, settings);
		
		if(changed1to1)
			settings.oneToOneRatio = false;
	}
	
	@Override public void onMouseReleased(MouseEvent me)
	{
		super.onMouseReleased(me);
		
		if(currMouseButton != MouseButton.RIGHT && currMouseButton != MouseButton.LEFT)
			return;
		
		int col = getColour();
		
		boolean changed1to1 = false;
		if(me.isShiftDown() && !settings.oneToOneRatio)
		{
			settings.oneToOneRatio = true;
			changed1to1 = true;
		}
		
		PaintHandler.drawRectangle(start, curr, col, DrawTo.CANVAS, EditorTools.brushSize, settings);
		
		if(changed1to1)
			settings.oneToOneRatio = false;
	}
}
