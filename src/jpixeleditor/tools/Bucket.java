package jpixeleditor.tools;

import java.awt.event.MouseEvent;
import jpixeleditor.utils.PaintHandler;

public class Bucket extends Tool
{
	public Bucket(int id)
	{
		super(id);
		
		name = "Fill Bucket";
		description = "Fills in an area";
		keyShortcut = "B";
		iconPath = "/Bucket.png";
	}
	
	@Override public void onMouseClicked(MouseEvent me)
	{
		super.onMouseClicked(me);
		
		int col = getColour();
		
		PaintHandler.fill(curr, col, settings);
	}
}
