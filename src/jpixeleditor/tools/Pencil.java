package jpixeleditor.tools;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import jpixeleditor.tools.Tool.MouseButton;
import jpixeleditor.utils.EditorTools;
import jpixeleditor.utils.PaintHandler;
import jpixeleditor.utils.MyMap.MyMapEntry;
import jpixeleditor.utils.PaintHandler.DrawTo;

public class Pencil extends Tool
{
	private ArrayList<MyMapEntry<Point, Integer>> currentStroke;
	
	public Pencil(int id)
	{
		super(id);
		
		name = "Pencil";
		description = "Draws pixels";
		keyShortcut = "P";
		iconPath = "/Pencil.png";
		sizeMatters = true;
	}
	
	@Override public void onMousePressed(MouseEvent me)
	{
		super.onMousePressed(me);
		
		if(currMouseButton != MouseButton.RIGHT && currMouseButton != MouseButton.LEFT)
			return;
		
		currentStroke = new ArrayList<MyMapEntry<Point, Integer>>();
		
		int col = getColour();
		
		PaintHandler.paint(curr.x, curr.y, col, DrawTo.CANVAS, EditorTools.brushSize, settings.circleBrush);
	}
	
	@Override public void onMouseDragged(MouseEvent me)
	{
		super.onMouseDragged(me);
		
		if(currMouseButton != MouseButton.RIGHT && currMouseButton != MouseButton.LEFT)
			return;
		
		int col = getColour();
		
		if(settings.pixelPerfect)
		{
			PaintHandler.drawLine_PixelPerfect(prev, curr, col, DrawTo.CANVAS, EditorTools.brushSize, settings, currentStroke);
		}
		else
		{
			PaintHandler.drawLine(prev, curr, col, DrawTo.CANVAS, EditorTools.brushSize, settings);
		}
	}
	
	@Override public void onMouseReleased(MouseEvent me)
	{
		super.onMouseReleased(me);
		
		if(currMouseButton != MouseButton.RIGHT && currMouseButton != MouseButton.LEFT)
			return;
		
		int col = getColour();
		
		if(settings.pixelPerfect)
		{
			PaintHandler.drawLine_PixelPerfect(prev, curr, col, DrawTo.CANVAS, EditorTools.brushSize, settings, currentStroke);
		}
		else
		{
			PaintHandler.drawLine(prev, curr, col, DrawTo.CANVAS, EditorTools.brushSize, settings);
		}
	}
}
