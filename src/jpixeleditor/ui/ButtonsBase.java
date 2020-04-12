package jpixeleditor.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;

import jpixeleditor.Main;
import jpixeleditor.utils.Colour;

@SuppressWarnings("serial")
public class ButtonsBase extends JButton
{
	public boolean selected = false;
	public boolean paintText = false;
	
	public ButtonsBase()
	{
		super();
		
		setPreferredSize(new Dimension(50, 50));
		
		setContentAreaFilled(false);
		setBorderPainted(false);
		
//		setToolTipText("Text");
		
//		ToolTipManager.sharedInstance().setInitialDelay(0);
//		ToolTipManager.sharedInstance().setReshowDelay(0);
//		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE); // As Integer.MAX_VALUE is 2147483647, that means that the tooltip will take 2147483.647 seconds to disappear, or about 596.52 hours. That's long enough I'd say
	}
	
	public ButtonsBase(String text)
	{
		super(text);
	}
	
//	@Override public JToolTip createToolTip()
//	{
////		JToolTip tooltip = new JToolTip() {
////			@Override public void paintComponent(Graphics g)
////			{
////				Graphics2D g2d = (Graphics2D)g;
////				
////				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
////				
////				g2d.setColor(Colour.toAWTColor(Colour.changeAlpha(Main.Theme.THEME_BACK_DARK, -25)));
////				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
////			}
////		};
////		
////		tooltip.setOpaque(false);
//		
//		ToolTip tooltip = new ToolTip(this);
//		
//		return tooltip;
//	}
	
//	@Override public Point getToolTipLocation(MouseEvent me)
//	{
//		return new Point(getWidth() + 20, (int)(getHeight() * 0.5f));
//	}
	
	@Override public void paintComponent(Graphics g)
	{
		//super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if(getMousePosition() != null)
		{
			g2d.setColor(Colour.toAWTColor(Main.Theme.THEME_BUTTON_HOVER));
		}
		else
		{
			g2d.setColor(Colour.toAWTColor(Main.Theme.THEME_BUTTON));
		}
		
		g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
		
		if(selected)
		{
			g2d.setColor(Colour.toAWTColor(Main.Theme.THEME_ACCENT));
			
			g2d.setStroke(new BasicStroke(2));
			g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 5, 5);
		}
		
		if(!getText().isEmpty() && paintText)
		{
			g2d.setColor(Color.WHITE);
			g2d.drawString(getText(), 14, getHeight() / 2 + 5);
		}
	}
}
