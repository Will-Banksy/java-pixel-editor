package jpixeleditor.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JColorChooser;

import jpixeleditor.Main;
import jpixeleditor.ui.ToolTipsHandler.TooltipLocation;
import jpixeleditor.utils.Colour;
import jpixeleditor.utils.EditorTools;

@SuppressWarnings("serial")
public class ColourSelectorButton extends ButtonsBase
{
	public boolean primary;
	public TexturePaint tp;
	
	ColourSelectorPopup popup;
	
	public ColourSelectorButton(boolean primary)
	{
		super();
		this.primary = primary;
		
		try
		{
			if(Canvas.bgImage == null)
			{
				Canvas.bgImage = ImageIO.read(Main.class.getResource("/Transparency-Dark200by200.png"));
			}
			tp = new TexturePaint(Canvas.bgImage, new Rectangle(0, 0, 200, 200));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if(primary)
		{
			String tooltip = "<html><b>Primary Colour</b>\nUse with left mouse button";
			ToolTipsHandler.addTooltipTo(this, tooltip, TooltipLocation.EAST);
		}
		else
		{
			String tooltip = "<html><b>Secondary Colour</b>\nUse with right mouse button";
			ToolTipsHandler.addTooltipTo(this, tooltip, TooltipLocation.EAST);
		}
		
		addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0)
			{
//				if(primary)
//				{
//					Color newColor = JColorChooser.showDialog(Main.windowFrame, "Choose Primary Colour", Colour.toAWTColor(EditorTools.primaryColour));
//					if(newColor != null)
//					{
//						EditorTools.primaryColour = Colour.fromAWTColor(newColor);
//					}
//				}
//				else
//				{
//					Color newColor = JColorChooser.showDialog(Main.windowFrame, "Choose Secondary Colour", Colour.toAWTColor(EditorTools.secondaryColour));
//					if(newColor != null)
//					{
//						EditorTools.secondaryColour = Colour.fromAWTColor(newColor);
//					}
//				}
				
				// This is an action that will be attached to a button in the popup to open the Java JColorChooser dialog for more advanced colour selection
				Action action = new AbstractAction() {
					@Override public void actionPerformed(ActionEvent e)
					{
						if(primary)
						{
							popup.setVisible(false);
							Color newCol = JColorChooser.showDialog(Main.windowFrame, "Pick a colour", Colour.toAWTColor(EditorTools.primaryColour));
							if(newCol != null)
							{
								EditorTools.primaryColour = Colour.fromAWTColor(newCol);
							}
						}
						else
						{
							popup.setVisible(false);
							Color newCol = JColorChooser.showDialog(Main.windowFrame, "Pick a colour", Colour.toAWTColor(EditorTools.secondaryColour));
							if(newCol != null)
							{
								EditorTools.secondaryColour = Colour.fromAWTColor(newCol);
							}
						}
					}
				};
				
				// Try a popup menu to display a simpler colour selector
				popup = new ColourSelectorPopup(primary, action);
				
				popup.show(primary ? ColourSelectorPanel.csb1 : ColourSelectorPanel.csb2, -ColourSelectorPopup.SHADOW_SIZE * 2 + 60, -ColourSelectorPopup.SHADOW_SIZE * 2);
			}
		});
	}
	
	@Override public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setPaint(tp);
		g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 5, 5);
		
		if(primary)
		{
			g2d.setColor(Colour.toAWTColor(EditorTools.primaryColour));
		}
		else
		{
			g2d.setColor(Colour.toAWTColor(EditorTools.secondaryColour));
		}
		
		
		g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 5, 5);
	}
}
