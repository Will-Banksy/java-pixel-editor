package jpixeleditor.tools;

import java.awt.event.MouseEvent;

import jpixeleditor.utils.EditorTools;
import jpixeleditor.utils.PaintHandler;
import jpixeleditor.utils.PaintHandler.DrawTo;

public class Ellipse extends Tool
{
	public Ellipse(int id)
	{
		super(id);
		
		name = "Ellipse";
		description = "Draws ellipses (Ovals)";
		keyShortcut = "C";
		iconPath = "/Ellipse.png";
	}
	
	@Override public void onMousePressed(MouseEvent me)
	{
		super.onMousePressed(me);
		
		int col = getColour();
		
		PaintHandler.paint(curr.x, curr.y, col, DrawTo.OVERLAY, EditorTools.brushSize, false);
	}
	
	@Override public void onMouseDragged(MouseEvent me)
	{
		super.onMouseDragged(me);
		
		int col = getColour();
		
		PaintHandler.drawEllipse(start, curr, col, DrawTo.OVERLAY, EditorTools.brushSize, settings);
	}
	
	@Override public void onMouseReleased(MouseEvent me)
	{
		super.onMouseReleased(me);
		
		int col = getColour();
		
		PaintHandler.drawEllipse(start, curr, col, DrawTo.CANVAS, EditorTools.brushSize, settings);
	}
}
