package jpixeleditor.tools;

import java.awt.event.MouseEvent;

import jpixeleditor.utils.SelectionHandler;

public class RectangularSelection extends SelectorTool
{
	public RectangularSelection(int id)
	{
		super(id);
		
		name = "Rectangular Selection";
		description = "Selects a rectangular region";
		keyShortcut = "S";
		iconPath = "/Select_Rect.png";
	}
	
	@Override public void onMousePressed(MouseEvent me)
	{
		super.onMousePressed(me);
		
		SelectionHandler.drawSelectionRectangle(start, curr, false, true, settings);
	}
	
	@Override public void onMouseDragged(MouseEvent me)
	{
		super.onMouseDragged(me);
		
		SelectionHandler.drawSelectionRectangle(start, curr, false, true, settings);
	}
	
	@Override public void onMouseReleased(MouseEvent me)
	{
		super.onMouseReleased(me);
		
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
		
		SelectionHandler.drawSelectionRectangle(start, curr, rightClick, false, settings);
	}

	@Override public boolean triggersOnClick()
	{
		return false;
	}
}
