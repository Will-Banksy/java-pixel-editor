package jpixeleditor.tools;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import jpixeleditor.tools.Tool.MouseButton;
import jpixeleditor.utils.SelectionHandler;

public class FreeSelection extends SelectorTool
{
	public static ArrayList<Point> lassoPath = new ArrayList<Point>();
	public static ArrayList<Point> pathClose = new ArrayList<Point>();

	public FreeSelection(int id)
	{
		super(id);
		
		name = "Free Selection";
		description = "Selects a region";
		keyShortcut = "H";
		iconPath = "/Select_Free.png";
	}
	
	@Override public void onMousePressed(MouseEvent me)
	{
		super.onMousePressed(me);
		
		if(!shouldDoToolAction)
			return;
		
		SelectionHandler.drawFreeSelection(start, curr, false, true, settings);
	}
	
	@Override public void onMouseDragged(MouseEvent me)
	{
		super.onMouseDragged(me);
		
		if(!shouldDoToolAction)
			return;
		
		SelectionHandler.drawFreeSelection(start, curr, false, true, settings);
	}
	
	@Override public void onMouseReleased(MouseEvent me)
	{
		super.onMouseReleased(me);
		
		if(!shouldDoToolAction)
			return;
		
		boolean rightClick = false;
		
		switch(currMouseButton)
		{
			case LEFT:
				break;
				
			case RIGHT:
				rightClick = true;
				break;
				
			default:
				return;
		}
		
		SelectionHandler.drawFreeSelection(start, curr, rightClick, false, settings);
	}

	@Override public boolean triggersOnClick()
	{
		return false;
	}
}
