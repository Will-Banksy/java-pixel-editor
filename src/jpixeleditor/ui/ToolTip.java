package jpixeleditor.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToolTip;

import jpixeleditor.Main;

@SuppressWarnings("serial")
public class ToolTip extends JToolTip
{
	public ToolTip(JComponent owner)
	{
		super();
		
		setOpaque(false);
		
		DropShadowBorder border = new DropShadowBorder(4, Color.BLACK, 0.3f, false);
		border.drawArrow = false;
		setBorder(border);
		
		setLayout(new BorderLayout());
		
		Panel panel = new Panel(Main.Theme.THEME_BACK_DARK);
		panel.setLayout(new BorderLayout());
		
		panel.add(new JLabel("<html><font color='ffffff'>Hello, I am a tooltip</font>"), BorderLayout.CENTER);
		
		add(panel, BorderLayout.CENTER);
	}
	
	@Override public void paintComponent(Graphics g)
	{
//		System.out.println(getParent());
//		
//		System.out.println(getParent().getComponentZOrder(this));
		
//		//super.paintComponent(g);
//		
//		Graphics2D g2d = (Graphics2D)g;
//		
//		//AlphaComposite comp = AlphaComposite.getInstance(AlphaComposite.SRC, 0);
//		
//		//g2d.setComposite(comp);
//		//g2d.clearRect(0, 0, getWidth(), getHeight());
//		
//		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		
//		g2d.setColor(Colour.toAWTColor(Colour.changeAlpha(Main.Theme.THEME_BACK_DARK, -25)));
//		g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
	}
}
