package jpixeleditor.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.WeakHashMap;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import jpixeleditor.main.Main;
import jpixeleditor.main.Main.Theme;
import jpixeleditor.utils.Colour;
import jpixeleditor.utils.Helper;

public class ToolTipsHandler
{
	/**
	 * The size of the tooltip shadows
	 */
	public static final int TOOLTIPS_SHADOW_SIZE = 4;
	
	/**
	 * If this is true, the 'edge' of tooltips will be the extent of their shadows, so the edge of their shadows will be constrained to inside the window. If false, the actual visual edge to the component will be used when constraining the tooltip to inside the window instead
	 */
	private static boolean EDGE_IS_SHADOWS = false;
	
	/**
	 * Simply contains four values (NORTH, EAST, SOUTH, WEST) used when adding tooltips, to set their relative location
	 */
	public static enum TooltipLocation
	{
		NORTH,
		EAST,
		SOUTH,
		WEST
	}
	
	// WeakHashMap - Contents may be garbage collected at any time. This is mainly to stop the drop shadow borders from being drawn so much
	private static WeakHashMap<JComponent, Panel> tooltips = new WeakHashMap<JComponent, Panel>();
	
	/**
	 * Adds a tooltip to {@code target}, with {@code content} in a JLabel styled white as the content, activated when you hover over {@code activator}
	 * @param target
	 * @param activator
	 * @param content Can include '\n' to split the text across multiple lines
	 * @param loc
	 */
	public static void addTooltipTo(JComponent target, JComponent activator, String content, TooltipLocation loc)
	{
		addTooltipTo(target, activator, Helper.splitIntoLabels(content, Component.LEFT_ALIGNMENT, Colour.WHITE, Theme.THEME_BACK_DARK), loc);
	}
	
	/**
	 * Adds a tooltip with {@code tooltipContent} content to {@code target}, at a location specified by {@code loc}, activated when you hover over {@code activator}
	 * @param target
	 * @param activator
	 * @param tooltipContent
	 * @param loc
	 */
	public static void addTooltipTo(JComponent target, JComponent activator, JComponent tooltipContent, TooltipLocation loc)
	{
		activator.addMouseListener(new MouseAdapter() {
			Panel tooltip;
			
			@Override public void mouseEntered(MouseEvent me)
			{
				Panel panel = new Panel(Main.Theme.THEME_BACK_DARK);
				
				panel.setLayout(new FlowLayout());
				
				panel.add(tooltipContent);//, BorderLayout.CENTER);
				
				panel.setBorder(new EmptyBorder(new Insets(2, 6, 2, 6)));
				
				tooltip = setToolTipAt(target, panel, loc);
			}
			
			@Override public void mouseExited(MouseEvent me)
			{
				if(tooltip != null)
				{
					removeComponent(tooltip);
				}
			}
		});
	}
	
	/**
	 * Adds a tooltip to {@code component}, with {@code content} in a JLabel styled white as the content, at a location specified by {@code loc}
	 * @param component
	 * @param content Can include '\n' to split the text across multiple lines
	 * @param loc
	 */
	public static void addTooltipTo(JComponent component, String content, TooltipLocation loc)
	{
		addTooltipTo(component, Helper.splitIntoLabels(content, Component.LEFT_ALIGNMENT, Colour.WHITE, Theme.THEME_BACK_DARK)/*new JLabel("<html><font color='ffffff'>" + content + "</font>")*/, loc);
	}
	
	/**
	 * Adds a tooltip with content {@code tooltipContent} to {@code component}, at a location specified by {@code loc}
	 * @param component
	 * @param tooltipContent
	 * @param loc
	 */
	public static void addTooltipTo(JComponent component, JComponent tooltipContent, TooltipLocation loc)
	{
		addTooltipTo(component, component, tooltipContent, loc);
	}
	
	@SuppressWarnings("serial")
	private static Panel setToolTipAt(JComponent relativeTo, Panel component, TooltipLocation loc) // FIXME: Tooltips appear not good
	{
		Panel panel;
		
		// I don't know if this really helps, but it would logically because the drop shadow border wouldn't have to be recalculated as much
		if(tooltips.containsKey(relativeTo))
		{
			panel = tooltips.get(relativeTo);
			panel.removeAll();
		}
		else
		{
			panel = new Panel() {
				@Override public void paintComponent(Graphics g)
				{
					// Don't need to do anything here, just overriding this to stop the panel being drawn
				}
			};
			panel.setOpaque(false);

			DropShadowBorder border = new DropShadowBorder(TOOLTIPS_SHADOW_SIZE, Color.BLACK, 0.3f, false);
			border.drawArrow = false;
			panel.setBorder(border);
		}

		component.setOpaque(true);

		FlowLayout fl = new FlowLayout(); // FlowLayout to the rescue! Remember I can actually set the vertical and horizontal gaps
		fl.setHgap(0);
		fl.setVgap(0);

		panel.setLayout(fl);

		panel.add(component);

		panel.doLayout();

		int width = component.getWidth() + TOOLTIPS_SHADOW_SIZE * 4; // Plus quadruple the shadow size
		int height = component.getHeight() + TOOLTIPS_SHADOW_SIZE * 4; // Plus quadruple the shadow size

		int x = 0;
		int y = 0;

		switch(loc)
		{
		// If not any of these, flow through to case NORTH
		default:

		case NORTH:
			x = (int)(relativeTo.getWidth() * 0.5f - width * 0.5f);
			y = -height - 10;
			break;

		case EAST:
			x = relativeTo.getWidth() + 10;
			y = (int)(relativeTo.getHeight() * 0.5f - height * 0.5f);
			break;

		case SOUTH:
			x = (int)(relativeTo.getWidth() * 0.5f - width * 0.5f);
			y = relativeTo.getHeight() + 10;
			break;

		case WEST:
			x = -width - 10;
			y = (int)(relativeTo.getHeight() * 0.5f - height * 0.5f);
			break;
		}

		Point pos = SwingUtilities.convertPoint(relativeTo, new Point(x, y), Main.windowFrame.getRootPane().getLayeredPane());

		Rectangle rootPaneBounds = Main.windowFrame.getRootPane().getLayeredPane().getBounds();

		if(EDGE_IS_SHADOWS)
		{
			if(pos.x < 0)
			{
				pos.x = 0;
			}
			else if(pos.x + width > rootPaneBounds.width)
			{
				pos.x = rootPaneBounds.width - width;
			}
			if(pos.y < 0)
			{
				pos.y = 0;
			}
			else if(pos.y + height > rootPaneBounds.height)
			{
				pos.y = rootPaneBounds.height - height;
			}
		}
		else
		{
			if(pos.x < -TOOLTIPS_SHADOW_SIZE * 2)
			{
				pos.x = -TOOLTIPS_SHADOW_SIZE * 2;
			}
			else if(pos.x + width > rootPaneBounds.width + TOOLTIPS_SHADOW_SIZE * 2)
			{
				pos.x = rootPaneBounds.width - width + TOOLTIPS_SHADOW_SIZE * 2;
			}
			if(pos.y < -TOOLTIPS_SHADOW_SIZE * 2)
			{
				pos.y = -TOOLTIPS_SHADOW_SIZE * 2;
			}
			else if(pos.y + height > rootPaneBounds.height + TOOLTIPS_SHADOW_SIZE * 2)
			{
				pos.y = rootPaneBounds.height - height + TOOLTIPS_SHADOW_SIZE * 2;
			}
		}

		panel.setBounds(pos.x, pos.y, width, height);
		tooltips.put(relativeTo, panel);
		Main.windowFrame.getRootPane().getLayeredPane().add(panel, JLayeredPane.MODAL_LAYER);
		panel.validate();
		
		return panel;
	}
	
	private static void removeComponent(Panel panel)
	{
		Main.windowFrame.getRootPane().getLayeredPane().remove(panel);
		Main.windowFrame.getRootPane().getLayeredPane().repaint();
	}
	
	/**
	 * Sets if the 'edge' of tooltips is the extent of their shadows, or their actual visual edge. The definition of the 'edge' is used solely for constraining the tooltips inside the window
	 * @param isShadowExtent If true, the 'edge' of tooltips is the extent of their shadows, if false, it is their visual edge
	 */
	public static void setEdge(boolean isShadowExtent)
	{
		EDGE_IS_SHADOWS = isShadowExtent;
	}
}
