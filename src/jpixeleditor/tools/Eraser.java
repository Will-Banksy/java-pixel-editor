package jpixeleditor.tools;

import java.awt.event.MouseEvent;
import jpixeleditor.utils.Colour;
import jpixeleditor.utils.EditorTools;
import jpixeleditor.utils.PaintHandler;
import jpixeleditor.utils.PaintHandler.DrawTo;

public class Eraser extends Tool
{
	public Eraser(int id)
	{
		super(id);
		
		name = "Eraser";
		description = "Erases pixels";
		keyShortcut = "E";
		iconPath = "/Eraser.png";
	}
	
	@Override public void onMousePressed(MouseEvent me)
	{
		super.onMousePressed(me);
		
		int col = Colour.TRANSPARENT;
		
		PaintHandler.paint(curr.x, curr.y, col, DrawTo.CANVAS, EditorTools.brushSize, settings.circleBrush);
	}
	
	@Override public void onMouseDragged(MouseEvent me)
	{
		super.onMouseDragged(me);
		
		int col = Colour.TRANSPARENT;
		
		PaintHandler.drawLine(prev, curr, col, DrawTo.CANVAS, EditorTools.brushSize, settings);
	}
	
	@Override public void onMouseReleased(MouseEvent me)
	{
		super.onMouseReleased(me);
		
		int col = Colour.TRANSPARENT;
		
		PaintHandler.drawLine(prev, curr, col, DrawTo.CANVAS, EditorTools.brushSize, settings);
	}
}
