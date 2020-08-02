package jpixeleditor.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import jpixeleditor.utils.Colour;

@SuppressWarnings("serial")
public class Panel extends JPanel
{
	public int colour = Colour.WHITE;
	
	public Panel()
	{
		super();
	}
	
	public Panel(int col)
	{
		colour = col;
	}
	
	public void setColour(int col)
	{
		colour = col;
	}
	
	@Override public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		g2d.setBackground(Colour.toAWTColor(colour));
		g2d.clearRect(0, 0, getWidth(), getHeight());
	}
}
