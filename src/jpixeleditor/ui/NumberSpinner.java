package jpixeleditor.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import jpixeleditor.main.Main;
import jpixeleditor.tools.Colour;

@SuppressWarnings("serial")
public class NumberSpinner extends Panel
{
	public interface IntegerChangeListener
	{
		public abstract void integerChanged(int newInt, int oldInt);
	}
	
	public static class NumberBounds
	{
		public static enum BoundType
		{
			REVERT,
			CONSTRAIN
		}
		
		private int min, max;
		private BoundType type;
		
		public NumberBounds(int min, int max, BoundType type)
		{
			this.min = min;
			this.max = max;
			this.type = type;
		}
		
		/**
		 * If {@code num} is out of the bounds specified in this {@code NumberBounds} object, then it sets it to a value dependent on the BoundType of this {@code NumberBounds} object
		 * @param newNum
		 * @param oldNum
		 * @return The number inside the bounds specified inside this object
		 */
		public int toBounds(int newNum, int oldNum)
		{
			if(newNum > min && newNum < max)
			{
				return newNum;
			}
			
			switch(type)
			{
				case CONSTRAIN:
					return newNum < min ? min : (newNum > max ? max : newNum);
				case REVERT:
					return oldNum;
				default:
					return newNum;
			}
		}
	}
	
	private int value;
	public NumberBounds bounds;
	public IntegerChangeListener iLi;
	private NumberTextField field;
	
	public NumberSpinner(IntegerChangeListener iLi, int initial, NumberBounds bounds)
	{
		super(Main.Theme.THEME_BACK_LIGHT);
		
		this.bounds = bounds;
		this.iLi = iLi;
		value = initial;
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		field = new NumberTextField(0, this);
		c.gridheight = 2;
		c.fill = GridBagConstraints.BOTH;
		add(field, c);
		
		c.insets = new Insets(2, 0, 0, 2);
		c.gridheight = 1;
		c.gridx = 1;
		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 0.5;
		add(new NumberChangeButton(this, true), c);
		
		c.insets = new Insets(0, 0, 2, 2);
		c.gridy = 1;
		c.weighty = 0.5;
		add(new NumberChangeButton(this, false), c);
	}
	
	public int getValue()
	{
		return value;
	}
	
	public void setValue(int newValue, int oldValue)
	{
		value = bounds.toBounds(newValue, oldValue);
		iLi.integerChanged(value, oldValue);
		field.setText(String.valueOf(value));
		field.repaint();
	}
	
	@Override public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setColor(Colour.toAWTColor(Main.Theme.THEME_BACK_DARK));
		g2d.setStroke(new BasicStroke(2));
		
		g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);
	}
}

@SuppressWarnings("serial")
class NumberTextField extends JTextField
{
	private String oldString;
	
	NumberTextField(int initialValue, NumberSpinner base)
	{
		super(String.valueOf(initialValue), 4);
		
		oldString = String.valueOf(initialValue);
		
		addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent evt)
			{
				try
				{
					int newValue = Math.round(Float.valueOf((String)getText()));
					int oldValue = Math.round(Float.valueOf((String)oldString));
					int boundedValue = base.bounds.toBounds(newValue, oldValue);
					
					String newString = String.valueOf(boundedValue);
					
					base.setValue(boundedValue, oldValue);
					
					oldString = newString;
				}
				catch(NumberFormatException e)
				{
					setText((String)oldString);
				}
			}
		});
		
		setBackground(Colour.toAWTColor(Main.Theme.THEME_BACK_LIGHT));
		setForeground(Color.WHITE);
		setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
	}
}

@SuppressWarnings("serial")
class NumberChangeButton extends ButtonsBase
{
	private static ImageIcon upArrow = null;
	private static ImageIcon downArrow = null;
	
	private boolean incrementor;
	
	public NumberChangeButton(NumberSpinner base, boolean incrementor)
	{
		super();
		
		this.incrementor = incrementor;
		
		setPreferredSize(new Dimension(16, 8));
		
		if(upArrow == null)
		{
			upArrow = new ImageIcon(Main.class.getResource("/Up_Arrow.png"));
		}
		if(downArrow == null)
		{
			downArrow = new ImageIcon(Main.class.getResource("/Down_Arrow.png"));
		}
		
		Action action = null;
		
		if(incrementor)
		{
			action = new AbstractAction() {
				@Override public void actionPerformed(ActionEvent e)
				{
					base.setValue(base.getValue() + 1, base.getValue());
				}
			};
		}
		else
		{
			action = new AbstractAction() {
				@Override public void actionPerformed(ActionEvent e)
				{
					base.setValue(base.getValue() - 1, base.getValue());
				}
			};
		}

		Timer timer = new Timer(100, action);
		timer.setInitialDelay(0);
		
		addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent me)
			{
				timer.restart();
			}
			
			@Override public void mouseExited(MouseEvent me)
			{
				// mouseHeld = false;
				repaint();
			}
			
			@Override public void mouseReleased(MouseEvent me)
			{
				if(timer.isRunning())
				{
					timer.stop();
				}
			}
			
			@Override public void mouseEntered(MouseEvent me)
			{
				
			}
		});
	}
	
	@Override public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		
		Point centre = new Point(getWidth() / 2, getHeight() / 2);
		
		if(incrementor)
		{
			upArrow.paintIcon(this, g2d, centre.x - upArrow.getIconWidth() / 2, centre.y - upArrow.getIconHeight() / 2);
		}
		else
		{
			downArrow.paintIcon(this, g2d, centre.x - downArrow.getIconWidth() / 2, centre.y - downArrow.getIconHeight() / 2);
		}
	}
}