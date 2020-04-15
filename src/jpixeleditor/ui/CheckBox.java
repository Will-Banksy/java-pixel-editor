package jpixeleditor.ui;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.border.EmptyBorder;

import jpixeleditor.Main;

@SuppressWarnings("serial")
public class CheckBox extends JCheckBox
{
//	private class CheckBoxIcon implements Icon
//	{
//		@Override public void paintIcon(Component c, Graphics g, int x, int y)
//		{
//		    ButtonModel buttonModel = ((AbstractButton)c).getModel();
//		    boolean selected = buttonModel.isSelected();
//		    
//		    Graphics2D g2d = (Graphics2D)g;
//		    
//		    g2d.setColor(Color.WHITE);//Colour.toAWTColor(Main.Theme.THEME_BACK_DARK));
//		    g2d.setStroke(new BasicStroke(2));
//		    
//		    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		    
//		    if(selected)
//		    {
//		    	g2d.setColor(Colour.toAWTColor(Main.Theme.THEME_ACCENT));
//		    	g2d.fillRoundRect(0, 0, getIconWidth() - 5, getIconHeight(), 8, 8);
//		    	
//		    	g2d.setColor(Color.WHITE);
//		    	g2d.drawLine(3, getIconHeight() / 2, getIconWidth() / 2 - 4, getIconHeight() / 2 + 4);
//		    	g2d.drawLine(getIconWidth() / 2 - 4, getIconHeight() / 2 + 4, getIconWidth() - 9, 5);
//		    }
//		    else
//		    {
//		    	g2d.drawRoundRect(1, 1, getIconWidth() - 7, getIconHeight() - 3, 6, 6);
//		    }
//		}
//
//		@Override public int getIconWidth()
//		{
//			return 24;
//		}
//
//		@Override public int getIconHeight()
//		{
//			return 20;
//		}
//	}
	
	public CheckBox()
	{
		super();
		init();
	}

	public CheckBox(Action a)
	{
		super(a);
		init();
	}

	public CheckBox(Icon icon, boolean selected)
	{
		super(icon, selected);
		init();
	}

	public CheckBox(Icon icon)
	{
		super(icon);
		init();
	}

	public CheckBox(String text, boolean selected)
	{
		super(text, selected);
		init();
	}

	public CheckBox(String text, Icon icon, boolean selected)
	{
		super(text, icon, selected);
		init();
	}

	public CheckBox(String text, Icon icon)
	{
		super(text, icon);
		init();
	}

	public CheckBox(String text)
	{
		super(text);
		init();
	}
	
	private void init()
	{
		setIcon(new ImageIcon(Main.class.getResource("/CheckBox_Unchecked.png")));
		setRolloverIcon(new ImageIcon(Main.class.getResource("/CheckBox_Unchecked.png")));
		setSelectedIcon(new ImageIcon(Main.class.getResource("/CheckBox_Checked.png")));
		setBorder(new EmptyBorder(new Insets(0, 5, 0, 5)));
	}
}
