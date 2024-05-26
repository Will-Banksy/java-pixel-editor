package jpixeleditor.tools;

import java.awt.event.MouseEvent;
import jpixeleditor.utils.EditorTools;
import jpixeleditor.utils.PaintHandler;

public class Dither extends Tool
{
	public Dither(int id)
	{
		super(id);
		
		name = "Dither";
		description = "Draws the primary colour interdispersed with the secondary colour\nRight click to switch";
		keyShortcut = "D";
		iconPath = "/Dither.png";
		sizeMatters = true;
	}
	
	@Override public void onMousePressed(MouseEvent me)
	{
		super.onMousePressed(me);
		
		if(currMouseButton != MouseButton.RIGHT && currMouseButton != MouseButton.LEFT)
			return;
		
		int col1 = EditorTools.primaryColour;
		int col2 = EditorTools.secondaryColour;
		
		boolean rightClick = currMouseButton == MouseButton.RIGHT;
		
		PaintHandler.drawLine_Dither(start, curr, col1, col2, rightClick, EditorTools.brushSize, settings);
	}
	
	@Override public void onMouseDragged(MouseEvent me)
	{
		super.onMouseDragged(me);
		
		if(currMouseButton != MouseButton.RIGHT && currMouseButton != MouseButton.LEFT)
			return;
		
		int col1 = EditorTools.primaryColour;
		int col2 = EditorTools.secondaryColour;
		
		boolean rightClick = currMouseButton == MouseButton.RIGHT;
		
		PaintHandler.drawLine_Dither(prev, curr, col1, col2, rightClick, EditorTools.brushSize, settings);
	}
	
	@Override public void onMouseReleased(MouseEvent me)
	{
		super.onMouseReleased(me);
		
		if(currMouseButton != MouseButton.RIGHT && currMouseButton != MouseButton.LEFT)
			return;
		
		int col1 = EditorTools.primaryColour;
		int col2 = EditorTools.secondaryColour;
		
		boolean rightClick = currMouseButton == MouseButton.RIGHT;
		
		PaintHandler.drawLine_Dither(prev, curr, col1, col2, rightClick, EditorTools.brushSize, settings);
	}
}
