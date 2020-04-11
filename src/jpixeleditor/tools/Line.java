package jpixeleditor.tools;

import java.awt.event.MouseEvent;

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
		
		int col = getColour();
		
		PaintHandler.paint(curr.x, curr.y, col, DrawTo.OVERLAY, EditorTools.brushSize, settings.circleBrush);
	}
	
	@Override public void onMouseDragged(MouseEvent me)
	{
		super.onMouseDragged(me);
		
		int col = getColour();
		
		PaintHandler.drawLine(prev, curr, col, DrawTo.OVERLAY, EditorTools.brushSize, settings);
	}
	
	@Override public void onMouseReleased(MouseEvent me)
	{
		super.onMouseReleased(me);
		
		int col = getColour();
		
		PaintHandler.drawLine(prev, curr, col, DrawTo.CANVAS, EditorTools.brushSize, settings);
	}
}
