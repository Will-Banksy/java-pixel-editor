package jpixeleditor.tools;

import java.awt.event.MouseEvent;

import jpixeleditor.ui.CanvasContainer;
import jpixeleditor.ui.ColourSelectorPanel;
import jpixeleditor.utils.EditorTools;

public class Pipette extends Tool
{
	public Pipette(int id)
	{
		super(id);
		
		name = "Pipette";
		description = "Picks a colour from the canvas";
		keyShortcut = "O";
		iconPath = "/Pipette.png";
	}
	
	@Override public void onMouseClicked(MouseEvent me)
	{
		super.onMouseClicked(me);
		
		int newCol = CanvasContainer.canvas.surface.gridColours[curr.x][curr.y];
		
		switch(currMouseButton)
		{
			case LEFT:
				EditorTools.primaryColour = newCol;
				ColourSelectorPanel.csb1.repaint();
				return;
				
			case RIGHT:
				EditorTools.secondaryColour = newCol;
				ColourSelectorPanel.csb2.repaint();
				return;
				
			default:
				return;
		}
	}
}
