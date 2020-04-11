package jpixeleditor.tools;

import java.awt.event.MouseEvent;

import jpixeleditor.utils.PaintHandler;

public class Replace extends Tool
{
	public Replace(int id)
	{
		super(id);
		
		name = "Colour Replace";
		description = "Replaces all pixels of one colour with another";
		keyShortcut = "P";
		iconPath = "/Replace.png";
	}
	
	@Override public void onMouseClicked(MouseEvent me)
	{
		super.onMouseClicked(me);
		
		int col = getColour();
		
		PaintHandler.replace(curr, col, settings);
	}
}
