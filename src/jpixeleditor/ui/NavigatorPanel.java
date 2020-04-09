package jpixeleditor.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import jpixeleditor.main.Main.Theme;

@SuppressWarnings("serial")
public class NavigatorPanel extends Panel
{
	public static Panel navContainer;
	public static Navigator navigator;
	
	public NavigatorPanel()
	{
		super(Theme.THEME_BACK_LIGHT);
		
		setPreferredSize(new Dimension(200, 200));
		
		navContainer = new Panel(Theme.THEME_BACK_LIGHT);
		navContainer.setPreferredSize(new Dimension(200, 200));
		navContainer.setLayout(new BorderLayout());
		
		navigator = new Navigator();
		
		navContainer.add(navigator, BorderLayout.CENTER);
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.PAGE_START;
		c.weighty = 1;
		add(navContainer, c);
	}
	
//	@Override public void paintComponent(Graphics g)
//	{
//		Graphics2D g2d = (Graphics2D)g;
//		
//		// g2d.setColor(Colour.toAWTColor(Theme.THEME_ACCENT));
//		// g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
//	}
}