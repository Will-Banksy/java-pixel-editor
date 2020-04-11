package jpixeleditor.tools;

import java.awt.event.MouseEvent;

import jpixeleditor.utils.EditorTools;
import jpixeleditor.utils.SelectionHandler;

public class ColourSelection extends Tool
{
	public ColourSelection(int id)
	{
		super(id);
		
		name = "Colour Selection";
		description = "Selects a colour";
		keyShortcut = "V";
		iconPath = "/Colour_Wand.png";
	}
	
	@Override public void onMouseClicked(MouseEvent me)
	{
		super.onMouseClicked(me);
		
		if(currMouseButton == MouseButton.LEFT)
		{
			SelectionHandler.colourSelect(curr, false, EditorTools.selectedTool.settings);
		}
		else if(currMouseButton == MouseButton.RIGHT)
		{
			SelectionHandler.colourSelect(curr, true, EditorTools.selectedTool.settings);
		}
	}
}
