package jpixeleditor.ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import jpixeleditor.Main.Theme;
import jpixeleditor.ui.NumberSpinner.IntegerChangeListener;
import jpixeleditor.ui.NumberSpinner.NumberBounds;
import jpixeleditor.ui.ToolTipsHandler.TooltipLocation;
import jpixeleditor.utils.EditorTools;
import jpixeleditor.utils.EditorTools.ToolInfo;

@SuppressWarnings("serial")
public class ToolConfigPanel extends Panel
{
	public static final String TOOLCONFIG_PENCIL = "Config: Pencil";
	public static final String TOOLCONFIG_ERASER = "Config: Eraser";
	public static final String TOOLCONFIG_BUCKET = "Config: Bucket";
	public static final String TOOLCONFIG_REPLACE = "Config: Replace";
	public static final String TOOLCONFIG_LINE = "Config: Line";
	public static final String TOOLCONFIG_RECTANGLE = "Config: Rectangle";
	public static final String TOOLCONFIG_ELLIPSE = "Config: Ellipse";
	public static final String TOOLCONFIG_PIPETTE = "Config: Pipette";
	public static final String TOOLCONFIG_SELECT_RECT = "Config: Rectangular Selection";
	public static final String TOOLCONFIG_SELECT_FREE = "Config: Free Selection";
	public static final String TOOLCONFIG_SELECT_MAGIC = "Config: Magic Selection";
	public static final String TOOLCONFIG_SELECT_COLOUR = "Config: Magic Colour Selection";
	public static final String TOOLCONFIG_MOVE = "Config: Move";
	public static final String TOOLCONFIG_DITHER = "Config: Dither";
	
	public ToolConfigPanel()
	{
		CardLayout cl = new CardLayout();
		setLayout(cl);
		
		Panel[] cards = initialiseCards();
		
		for(int i = 0; i < cards.length; i++)
		{
			add(cards[i], getToolConfigName(i));
		}
	}
	
	public void switchPanel(int toolID)
	{
		CardLayout cl = (CardLayout)getLayout();
		
		cl.show(this, getToolConfigName(toolID));
	}
	
	private String getToolConfigName(int i)
	{
		switch(i)
		{
			case ToolInfo.ID_PENCIL:
				return TOOLCONFIG_PENCIL;
				
			case ToolInfo.ID_ERASER:
				return TOOLCONFIG_ERASER;
				
			case ToolInfo.ID_BUCKET:
				return TOOLCONFIG_BUCKET;
				
			case ToolInfo.ID_REPLACE:
				return TOOLCONFIG_REPLACE;
				
			case ToolInfo.ID_LINE:
				return TOOLCONFIG_LINE;
				
			case ToolInfo.ID_RECTANGLE:
				return TOOLCONFIG_RECTANGLE;
				
			case ToolInfo.ID_ELLIPSE:
				return TOOLCONFIG_ELLIPSE;
				
			case ToolInfo.ID_PIPETTE:
				return TOOLCONFIG_PIPETTE;
				
			case ToolInfo.ID_SELECT_RECT:
				return TOOLCONFIG_SELECT_RECT;
				
			case ToolInfo.ID_SELECT_FREE:
				return TOOLCONFIG_SELECT_FREE;
				
			case ToolInfo.ID_SELECT_MAGIC:
				return TOOLCONFIG_SELECT_MAGIC;
				
			case ToolInfo.ID_SELECT_COLOUR:
				return TOOLCONFIG_SELECT_COLOUR;
				
			case ToolInfo.ID_MOVE:
				return TOOLCONFIG_MOVE;
				
			case ToolInfo.ID_DITHER:
				return TOOLCONFIG_DITHER;
				
			default:
				throw new IllegalArgumentException("'i' is not a tool id");
		}
	}

	public Panel[] initialiseCards()
	{
		Panel[] panels = {  pencilPanel(),
							eraserPanel(),
							bucketPanel(),
							replacePanel(),
							linePanel(),
							rectanglePanel(),
							ellipsePanel(),
							pipettePanel(),
							selectRectPanel(),
							selectFreePanel(),
							selectMagicPanel(),
							selectColourPanel(),
							movePanel(),
							ditherPanel() };
		
		return panels;
	}
	
	private Panel pencilPanel()
	{
		Panel panel = new Panel(Theme.THEME_BACK_LIGHT);
		panel.setBorder(new EmptyBorder(new Insets(4, 0, 4, 0)));
		
		CheckBox circleBrush = new CheckBox("Elliptical Brush");
		circleBrush.setForeground(Color.WHITE);
		
		circleBrush.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0)
			{
				CheckBox box = (CheckBox)arg0.getSource();
				EditorTools.tools[EditorTools.ToolInfo.ID_PENCIL].settings.circleBrush = box.isSelected();
			}
		});
		
		CheckBox pixelPerfect = new CheckBox("Pixel Perfect");
		pixelPerfect.setForeground(Color.WHITE);
		
		pixelPerfect.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0)
			{
				CheckBox box = (CheckBox)arg0.getSource();
				EditorTools.tools[EditorTools.ToolInfo.ID_PENCIL].settings.pixelPerfect = box.isSelected();
			}
		});
		
		panel.setLayout(new FlowLayout());
		
		panel.add(new JLabel("<html><strong color='white'>" + EditorTools.TOOL_PENCIL.name + ": </strong></html>"));
		
		panel.add(circleBrush);
		
		panel.add(pixelPerfect);
		
		return panel;
	}
	
	private Panel eraserPanel()
	{
		Panel panel = new Panel(Theme.THEME_BACK_LIGHT);
		panel.setBorder(new EmptyBorder(new Insets(4, 0, 4, 0)));
		
		CheckBox circleBrush = new CheckBox("Elliptical Eraser");
		circleBrush.setForeground(Color.WHITE);
		
		circleBrush.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0)
			{
				CheckBox box = (CheckBox)arg0.getSource();
				EditorTools.tools[EditorTools.ToolInfo.ID_ERASER].settings.circleBrush = box.isSelected();
			}
		});
		
		panel.setLayout(new FlowLayout());
		
		panel.add(new JLabel("<html><strong color='white'>" + EditorTools.TOOL_ERASER.name + ": </strong></html>"));
		
		panel.add(circleBrush);
		
		return panel;
	}
	
	private Panel bucketPanel()
	{
		Panel panel = new Panel(Theme.THEME_BACK_LIGHT);
		
		IntegerChangeListener iLi = new IntegerChangeListener() {
			@Override public void integerChanged(int newInt, int oldInt)
			{
				EditorTools.tools[EditorTools.ToolInfo.ID_BUCKET].settings.tolerance = newInt;
			}
		};
		NumberBounds bounds = new NumberBounds(0, 255, NumberBounds.BoundType.CONSTRAIN);
		NumberSpinner tolerance = new NumberSpinner(iLi, EditorTools.tools[EditorTools.ToolInfo.ID_BUCKET].settings.tolerance, bounds);
		
		CheckBox fill8Way = new CheckBox("8-Way Fill");
		fill8Way.setForeground(Color.WHITE);
		
		fill8Way.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0)
			{
				CheckBox box = (CheckBox)arg0.getSource();
				EditorTools.tools[EditorTools.ToolInfo.ID_BUCKET].settings.fill8Way = box.isSelected();
			}
		});
		
		panel.setLayout(new FlowLayout());
		
		panel.add(new JLabel("<html><strong color='white'>" + EditorTools.TOOL_BUCKET.name + ": </strong></html>"));

		panel.add(new JLabel("<html><font color='white'>Tolerance: </font></html>")); // Turns out you can use html???!
		panel.add(tolerance);
		
		panel.add(fill8Way);
		
		return panel;
	}
	
	private Panel replacePanel()
	{
		Panel panel = new Panel(Theme.THEME_BACK_LIGHT);
		
		IntegerChangeListener iLi = new IntegerChangeListener() {
			@Override public void integerChanged(int newInt, int oldInt)
			{
				EditorTools.tools[EditorTools.ToolInfo.ID_REPLACE].settings.tolerance = newInt;
			}
		};
		NumberBounds bounds = new NumberBounds(0, 255, NumberBounds.BoundType.CONSTRAIN);
		NumberSpinner tolerance = new NumberSpinner(iLi, EditorTools.tools[EditorTools.ToolInfo.ID_REPLACE].settings.tolerance, bounds);
		
		panel.setLayout(new FlowLayout());
		
		panel.add(new JLabel("<html><strong color='white'>" + EditorTools.TOOL_REPLACE.name + ": </strong></html>"));
		
		panel.add(new JLabel("<html><font color='white'>Tolerance: </font></html>")); // It's probably better to set the foreground colour of a component though. But this is really useful!
		panel.add(tolerance);
		
		return panel;
	}
	
	private Panel linePanel()
	{
		Panel panel = new Panel(Theme.THEME_BACK_LIGHT);
		panel.setBorder(new EmptyBorder(new Insets(4, 0, 4, 0)));
		
		CheckBox circleBrush = new CheckBox("Elliptical Brush");
		circleBrush.setForeground(Color.WHITE);
		
		circleBrush.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0)
			{
				CheckBox box = (CheckBox)arg0.getSource();
				EditorTools.tools[EditorTools.ToolInfo.ID_LINE].settings.circleBrush = box.isSelected();
			}
		});
		
		CheckBox oneToOneRatio = new CheckBox("Uniform Line");
		oneToOneRatio.setForeground(Color.WHITE);
		
		oneToOneRatio.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0)
			{
				CheckBox box = (CheckBox)arg0.getSource();
				EditorTools.tools[EditorTools.ToolInfo.ID_LINE].settings.oneToOneRatio = box.isSelected();
			}
		});
		
		ToolTipsHandler.addTooltipTo(oneToOneRatio, "Hold Shift to use this temporarily", TooltipLocation.SOUTH);
		
		panel.setLayout(new FlowLayout());
		
		panel.add(new JLabel("<html><strong color='white'>" + EditorTools.TOOL_LINE.name + ": </strong></html>"));

		panel.add(circleBrush);
		panel.add(oneToOneRatio);
		
		return panel;
	}
	
	private Panel rectanglePanel()
	{
		Panel panel = new Panel(Theme.THEME_BACK_LIGHT);
		panel.setBorder(new EmptyBorder(new Insets(4, 0, 4, 0)));
		
		CheckBox oneToOneRatio = new CheckBox("1:1 Ratio");
		oneToOneRatio.setForeground(Color.WHITE);
		
		oneToOneRatio.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0)
			{
				CheckBox box = (CheckBox)arg0.getSource();
				EditorTools.tools[EditorTools.ToolInfo.ID_RECTANGLE].settings.oneToOneRatio = box.isSelected();
			}
		});
		
		ToolTipsHandler.addTooltipTo(oneToOneRatio, "Hold Shift to use this temporarily", TooltipLocation.SOUTH);
		
		CheckBox fill = new CheckBox("Fill");
		fill.setForeground(Color.WHITE);
		
		fill.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0)
			{
				CheckBox box = (CheckBox)arg0.getSource();
				EditorTools.tools[EditorTools.ToolInfo.ID_RECTANGLE].settings.fill = box.isSelected();
			}
		});
		
		panel.setLayout(new FlowLayout());
		
		panel.add(new JLabel("<html><strong color='white'>" + EditorTools.TOOL_RECTANGLE.name + ": </strong></html>"));

		panel.add(oneToOneRatio);
		panel.add(fill);
		
		return panel;
	}
	
	private Panel ellipsePanel()
	{
		Panel panel = new Panel(Theme.THEME_BACK_LIGHT);
		panel.setBorder(new EmptyBorder(new Insets(4, 0, 4, 0)));
		
		CheckBox oneToOneRatio = new CheckBox("1:1 Ratio");
		oneToOneRatio.setForeground(Color.WHITE);
		
		oneToOneRatio.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0)
			{
				CheckBox box = (CheckBox)arg0.getSource();
				EditorTools.tools[EditorTools.ToolInfo.ID_ELLIPSE].settings.oneToOneRatio = box.isSelected();
			}
		});
		
		ToolTipsHandler.addTooltipTo(oneToOneRatio, "Hold Shift to use this temporarily", TooltipLocation.SOUTH);
		
		CheckBox fill = new CheckBox("Fill");
		fill.setForeground(Color.WHITE);
		
		fill.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0)
			{
				CheckBox box = (CheckBox)arg0.getSource();
				EditorTools.tools[EditorTools.ToolInfo.ID_ELLIPSE].settings.fill = box.isSelected();
			}
		});
		
		panel.setLayout(new FlowLayout());
		
		panel.add(new JLabel("<html><strong color='white'>" + EditorTools.TOOL_ELLIPSE.name + ": </strong></html>"));
		
		panel.add(oneToOneRatio);
		panel.add(fill);
		
		return panel;
	}
	
	private Panel pipettePanel()
	{
		Panel panel = new Panel(Theme.THEME_BACK_LIGHT);
		panel.setBorder(new EmptyBorder(new Insets(4, 0, 4, 0)));
		
		panel.add(new JLabel("<html><strong color='white'>" + EditorTools.TOOL_PIPETTE.name + "</strong></html>"));
		
		return panel;
	}
	
	private Panel selectRectPanel()
	{
		Panel panel = new Panel(Theme.THEME_BACK_LIGHT);
		panel.setBorder(new EmptyBorder(new Insets(4, 0, 4, 0)));
		
		ButtonsBase selectionAppend = new ButtonsBase("Right Click Action: Remove") {
			@Override public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				
				Graphics2D g2d = (Graphics2D)g;
				
				g2d.setColor(Color.WHITE);
				g2d.drawString(getText(), 6, getHeight() / 2 + 5);
			}
			
			@Override public Dimension getPreferredSize()
			{
				Dimension dims = super.getPreferredSize();
				return new Dimension(dims.width - 16, 18);
			}
		};
		
		selectionAppend.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e)
			{
				if(selectionAppend.getText().equals("Right Click Action: Remove"))
				{
					selectionAppend.setText("Right Click Action: Append");
					EditorTools.tools[EditorTools.ToolInfo.ID_SELECT_RECT].settings.selectionAppend = true;
				}
				else
				{
					selectionAppend.setText("Right Click Action: Remove");
					EditorTools.tools[EditorTools.ToolInfo.ID_SELECT_RECT].settings.selectionAppend = false;
				}
			}
		});
		
		panel.add(new JLabel("<html><strong color='white'>" + EditorTools.TOOL_SELECT_RECT.name + ":</strong></html>"));
		
		panel.add(selectionAppend);
		
		return panel;
	}
	
	private Panel selectFreePanel()
	{
		Panel panel = new Panel(Theme.THEME_BACK_LIGHT);
		panel.setBorder(new EmptyBorder(new Insets(4, 5, 4, 5)));
		
		ButtonsBase selectionAppend = new ButtonsBase("Right Click Action: Remove") {
			@Override public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				
				Graphics2D g2d = (Graphics2D)g;
				
				g2d.setColor(Color.WHITE);
				g2d.drawString(getText(), 6, getHeight() / 2 + 5);
			}
			
			@Override public Dimension getPreferredSize()
			{
				Dimension dims = super.getPreferredSize();
				return new Dimension(dims.width - 16, 18); // This is how to do it. Look at 'https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/_nimbusDefaults.html' for defaults for the Nimbus LAF
			}
		};
		
		selectionAppend.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e)
			{
				if(selectionAppend.getText().equals("Right Click Action: Remove"))
				{
					selectionAppend.setText("Right Click Action: Append");
					EditorTools.tools[EditorTools.ToolInfo.ID_SELECT_FREE].settings.selectionAppend = true;
				}
				else
				{
					selectionAppend.setText("Right Click Action: Remove");
					EditorTools.tools[EditorTools.ToolInfo.ID_SELECT_FREE].settings.selectionAppend = false;
				}
			}
		});
		JLabel label = new JLabel("<html><strong color='white'>" + EditorTools.TOOL_SELECT_FREE.name + ":</strong></html>");
		
		panel.add(label);
		
		panel.add(selectionAppend);
		
		return panel;
	}
	
	private Panel selectMagicPanel()
	{
		Panel panel = new Panel(Theme.THEME_BACK_LIGHT);
//		panel.setBorder(new EmptyBorder(new Insets(4, 5, 4, 5)));
		
		ButtonsBase selectionAppend = new ButtonsBase("Right Click Action: Remove") {
			@Override public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				
				Graphics2D g2d = (Graphics2D)g;
				
				g2d.setColor(Color.WHITE);
				g2d.drawString(getText(), 6, getHeight() / 2 + 5);
			}
			
			@Override public Dimension getPreferredSize()
			{
				Dimension dims = super.getPreferredSize();
				return new Dimension(dims.width - 16, 18); // This is how to do it. Look at 'https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/_nimbusDefaults.html' for defaults for the Nimbus LAF
			}
		};
		
		selectionAppend.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e)
			{
				if(selectionAppend.getText().equals("Right Click Action: Remove"))
				{
					selectionAppend.setText("Right Click Action: Append");
					EditorTools.tools[EditorTools.ToolInfo.ID_SELECT_MAGIC].settings.selectionAppend = true;
				}
				else
				{
					selectionAppend.setText("Right Click Action: Remove");
					EditorTools.tools[EditorTools.ToolInfo.ID_SELECT_MAGIC].settings.selectionAppend = false;
				}
			}
		});
		
		IntegerChangeListener iLi = new IntegerChangeListener() {
			@Override public void integerChanged(int newInt, int oldInt)
			{
				EditorTools.tools[EditorTools.ToolInfo.ID_SELECT_MAGIC].settings.tolerance = newInt;
			}
		};
		NumberBounds bounds = new NumberBounds(0, 255, NumberBounds.BoundType.CONSTRAIN);
		NumberSpinner tolerance = new NumberSpinner(iLi, EditorTools.tools[EditorTools.ToolInfo.ID_SELECT_MAGIC].settings.tolerance, bounds);
		
		CheckBox fill8Way = new CheckBox("8-Way Fill");
		fill8Way.setForeground(Color.WHITE);
		
		fill8Way.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0)
			{
				CheckBox box = (CheckBox)arg0.getSource();
				EditorTools.tools[EditorTools.ToolInfo.ID_SELECT_MAGIC].settings.fill8Way = box.isSelected();
			}
		});
		
		JLabel label = new JLabel("<html><strong color='white'>" + EditorTools.TOOL_SELECT_MAGIC.name + ":</strong></html>");
		
		panel.add(label);
		
		panel.add(selectionAppend);
		
		panel.add(fill8Way);
		
		panel.add(new JLabel("<html><font color='white'>Tolerance: </font></html>")); // Turns out you can use html???!
		panel.add(tolerance);
		
		return panel;
	}
	
	private Panel selectColourPanel()
	{
		Panel panel = new Panel(Theme.THEME_BACK_LIGHT);
		
		ButtonsBase selectionAppend = new ButtonsBase("Right Click Action: Remove") {
			@Override public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				
				Graphics2D g2d = (Graphics2D)g;
				
				g2d.setColor(Color.WHITE);
				g2d.drawString(getText(), 6, getHeight() / 2 + 5);
			}
			
			@Override public Dimension getPreferredSize()
			{
				Dimension dims = super.getPreferredSize();
				return new Dimension(dims.width - 16, 18); // This is how to do it. Look at 'https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/_nimbusDefaults.html' for defaults for the Nimbus LAF
			}
		};
		
		selectionAppend.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e)
			{
				if(selectionAppend.getText().equals("Right Click Action: Remove"))
				{
					selectionAppend.setText("Right Click Action: Append");
					EditorTools.tools[EditorTools.ToolInfo.ID_SELECT_COLOUR].settings.selectionAppend = true;
				}
				else
				{
					selectionAppend.setText("Right Click Action: Remove");
					EditorTools.tools[EditorTools.ToolInfo.ID_SELECT_COLOUR].settings.selectionAppend = false;
				}
			}
		});
		JLabel label = new JLabel("<html><strong color='white'>" + EditorTools.TOOL_SELECT_COLOUR.name + ":</strong></html>");
		
		IntegerChangeListener iLi = new IntegerChangeListener() {
			@Override public void integerChanged(int newInt, int oldInt)
			{
				EditorTools.tools[EditorTools.ToolInfo.ID_SELECT_COLOUR].settings.tolerance = newInt;
			}
		};
		NumberBounds bounds = new NumberBounds(0, 255, NumberBounds.BoundType.CONSTRAIN);
		NumberSpinner tolerance = new NumberSpinner(iLi, EditorTools.tools[EditorTools.ToolInfo.ID_SELECT_COLOUR].settings.tolerance, bounds);
		
		panel.add(label);
		
		panel.add(selectionAppend);
		
		panel.add(new JLabel("<html><font color='white'>Tolerance: </font></html>")); // Turns out you can use html???!
		panel.add(tolerance);
		
		return panel;
	}
	
	private Panel movePanel()
	{
		Panel panel = new Panel(Theme.THEME_BACK_LIGHT);
		panel.setBorder(new EmptyBorder(new Insets(4, 0, 4, 0)));
		
		panel.add(new JLabel("<html><strong color='white'>" + EditorTools.TOOL_MOVE.name + "</strong></html>"));
		
		return panel;
	}
	
	private Panel ditherPanel()
	{
		Panel panel = new Panel(Theme.THEME_BACK_LIGHT);
		panel.setBorder(new EmptyBorder(new Insets(4, 0, 4, 0)));
		
		panel.add(new JLabel("<html><strong color='white'>" + EditorTools.TOOL_DITHER.name + "</strong></html>"));
		
		CheckBox interdisperse = new CheckBox("Interdisperse Secondary Colour", true); // true because we want it to initially be selected
		interdisperse.setForeground(Color.WHITE);
		
		interdisperse.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent ae)
			{
				CheckBox box = (CheckBox)ae.getSource();
				EditorTools.tools[EditorTools.ToolInfo.ID_DITHER].settings.interdisperse = box.isSelected();
			}
		});
		
		panel.add(interdisperse);
		
		return panel;
	}
}
