package jpixeleditor.tools;

import java.awt.event.MouseEvent;

import jpixeleditor.utils.EditorTools;
import jpixeleditor.utils.SelectionHandler;

public class ShapeSelection extends Tool
{
	public ShapeSelection(int id)
	{
		super(id);
		
		name = "Shape Selection";
		description = "Selects a shape";
		keyShortcut = "Z";
		iconPath = "/Wand.png";
	}
	
	@Override public void onMouseClicked(MouseEvent me)
	{
		super.onMouseClicked(me);
		
		if(currMouseButton == MouseButton.LEFT)
		{
			SelectionHandler.magicSelect(curr.x, curr.y, false, EditorTools.selectedTool.settings);
		}
		else if(currMouseButton == MouseButton.RIGHT)
		{
			SelectionHandler.magicSelect(curr.x, curr.y, true, EditorTools.selectedTool.settings);
		}
	}
}
