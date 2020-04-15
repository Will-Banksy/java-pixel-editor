package jpixeleditor.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import jpixeleditor.Main;
import jpixeleditor.ui.ToolTipsHandler.TooltipLocation;
import jpixeleditor.utils.Colour;
import jpixeleditor.utils.EditorTools;

@SuppressWarnings("serial")
public class BrushSizeSelectorPanel extends Panel
{
	public class BrushSizeSelectorButton extends ButtonsBase
	{
		public int size;
		
		public BrushSizeSelectorButton(int size)
		{
			super();
			this.size = size;
			
			setPreferredSize(new Dimension(20, 20));
			
			if(size == EditorTools.brushSize)
			{
				selected = true;
			}
		}
		
		@Override public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			
			Graphics2D g2d = (Graphics2D)g;
			
			Point centre = new Point(getWidth() / 2, getHeight() / 2);
			
			if(selected)
			{
				g2d.setColor(Colour.toAWTColor(Main.Theme.THEME_ACCENT));
			}
			else
			{
				g2d.setColor(Color.BLACK);
			}
			
			g2d.fillRect(centre.x - size, centre.y - size, size * 2, size * 2);
		}
	}
	
	public BrushSizeSelectorPanel()
	{
		super();
		BrushSizeSelectorButton[] btns = new BrushSizeSelectorButton[5];
		
		setLayout(new GridLayout(1, 5));
		setPreferredSize(new Dimension(100, 20));
		setColour(Main.Theme.THEME_BACK_LIGHT);
		
		for(int i = 0; i < btns.length; i++)
		{
			btns[i] = new BrushSizeSelectorButton(i + 1);
			add(btns[i]);
			
			ToolTipsHandler.addTooltipTo(this, btns[i], "Select brush size,\n1 to 5 pixels", TooltipLocation.EAST);
		}
		
		for(BrushSizeSelectorButton btn : btns)
		{
			btn.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent arg0)
				{
					for(BrushSizeSelectorButton b : btns)
					{
						if(b.selected)
						{
							b.selected = false;
							repaint();
						}
					}
					btn.selected = true;
					
					EditorTools.brushSize = btn.size;
				}
			});
		}
	}
}
