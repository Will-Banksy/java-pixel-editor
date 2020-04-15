package jpixeleditor.tools;

import java.awt.event.MouseEvent;

import jpixeleditor.tools.Tool.MouseButton;
import jpixeleditor.ui.Canvas;
import jpixeleditor.utils.Colour;
import jpixeleditor.utils.EditorTools;
import jpixeleditor.utils.PaintHandler;
import jpixeleditor.utils.PaintHandler.DrawTo;

public class Line extends Tool
{
	public Line(int id)
	{
		super(id);
		
		name = "Line";
		description = "Draws lines";
		keyShortcut = "L";
		iconPath = "/Line.png";
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
		
		PaintHandler.paint(curr.x, curr.y, col, DrawTo.OVERLAY, EditorTools.brushSize, settings.circleBrush);
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
		
		PaintHandler.drawLine(start, curr, col, DrawTo.OVERLAY, EditorTools.brushSize, settings);
	}
	
	@Override public void onMouseReleased(MouseEvent me)
	{
		super.onMouseReleased(me);
		
		if(currMouseButton != MouseButton.RIGHT && currMouseButton != MouseButton.LEFT)
			return;
		
		int col = getColour();
		
		PaintHandler.drawLine(start, curr, col, DrawTo.CANVAS, EditorTools.brushSize, settings);
	}
}
