package jpixeleditor.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import jpixeleditor.main.Main;
import jpixeleditor.tools.EditorTools;
import jpixeleditor.ui.ToolTipsHandler.TooltipLocation;

@SuppressWarnings("serial")
public class ToolButton extends ButtonsBase
{
	EditorTools.ToolInfo tool;
	BufferedImage icon = null;
	
	public ToolButton(EditorTools.ToolInfo tool)
	{
		this.tool = tool;
	}
	
	public void initialise(Action selectThisAction)
	{
		String filename = null;
		String toolName = "";
		String description = "";
		String toolKey = "";
		
		switch(tool.id)
		{
			case EditorTools.ToolInfo.ID_PENCIL:
				filename = "/Pencil.png";
				toolName = EditorTools.TOOL_PENCIL.name;
				description = "Draws pixels";
				toolKey = "P";
				break;
			
			case EditorTools.ToolInfo.ID_ERASER:
				filename = "/Eraser.png";
				toolName = EditorTools.TOOL_ERASER.name;
				description = "Erases pixels";
				toolKey = "E";
				break;
			
			case EditorTools.ToolInfo.ID_BUCKET:
				filename = "/Bucket.png";
				toolName = EditorTools.TOOL_BUCKET.name;
				description = "Fills in an area with a colour";
				toolKey = "B";
				break;
			
			case EditorTools.ToolInfo.ID_REPLACE:
				filename = "/Replace.png";
				toolName = EditorTools.TOOL_REPLACE.name;
				description = "Replaces all pixels of one colour with another";
				toolKey = "A";
				break;
			
			case EditorTools.ToolInfo.ID_LINE:
				filename = "/Line.png";
				toolName = EditorTools.TOOL_LINE.name;
				description = "Draws lines";
				toolKey = "L";
				break;
			
			case EditorTools.ToolInfo.ID_RECTANGLE:
				filename = "/Rectangle.png";
				toolName = EditorTools.TOOL_RECTANGLE.name;
				description = "Draws rectangles";
				toolKey = "R";
				break;
				
			case EditorTools.ToolInfo.ID_ELLIPSE:
				filename = "/Ellipse.png";
				toolName = EditorTools.TOOL_ELLIPSE.name;
				description = "Draws Ellipses";
				toolKey = "C";
				break;
				
			case EditorTools.ToolInfo.ID_PIPETTE:
				filename = "/Pipette.png";
				toolName = EditorTools.TOOL_PIPETTE.name;
				description = "Selects a colour from the canvas";
				toolKey = "O";
				break;
				
			case EditorTools.ToolInfo.ID_SELECT_RECT:
				filename = "/Select_Rect.png";
				toolName = EditorTools.TOOL_SELECT_RECT.name;
				description = "Selects a rectangle\nClick to clear selection";
				toolKey = "S";
				break;
				
			case EditorTools.ToolInfo.ID_SELECT_FREE:
				filename = "/Select_Free.png";
				toolName = EditorTools.TOOL_SELECT_FREE.name;
				description = "Selects a region\nClick to clear selection";
				toolKey = "H";
				break;
				
			case EditorTools.ToolInfo.ID_SELECT_MAGIC:
				filename = "/Wand.png";
				toolName = EditorTools.TOOL_SELECT_MAGIC.name;
				description = "Magically selects a region\nClick to clear selection";
				toolKey = "Z";
				break;
				
			case EditorTools.ToolInfo.ID_SELECT_COLOUR:
				filename = "/Colour_Wand.png";
				toolName = EditorTools.TOOL_SELECT_COLOUR.name;
				description = "Magically selects a colour\nClick to clear selection";
				toolKey = "V";
				break;
				
			case EditorTools.ToolInfo.ID_MOVE:
				filename = "/Hand.png";
				toolName = EditorTools.TOOL_MOVE.name;
				description = "Moves the canvas content";
				toolKey = "M";
				break;
				
			case EditorTools.ToolInfo.ID_DITHER:
				filename = "/Dither.png";
				toolName = EditorTools.TOOL_DITHER.name;
				description = "Draws the primary colour interdispersed with the secondary colour\nRight click to switch";
				toolKey = "D";
				break;
		}
		
		String tooltip = "<html><b>" + toolName + "</b>" + " (" + toolKey + ")" + "\n" + description;
		ToolTipsHandler.addTooltipTo(this, tooltip, TooltipLocation.EAST);
		
		// Add an entry to the input map for when this component is in a focused window. First argument is the KeyStroke, second the index in the ActionMap that the keystroke will run the action in
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(toolKey), "SelectThis");
		getActionMap().put("SelectThis", selectThisAction);
		
		// Also add a listener to execute this action when the button is clicked
		addActionListener(selectThisAction);
		
		try
		{
			icon = ImageIO.read(Main.class.getResource(filename));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		
		//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.drawImage(icon, getWidth() / 2 - icon.getWidth() / 2, getHeight() / 2 - icon.getHeight() / 2, icon.getWidth(), icon.getHeight(), null);
	}
}
