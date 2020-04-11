package jpixeleditor.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;

import jpixeleditor.main.Main;
import jpixeleditor.ui.ToolTipsHandler.TooltipLocation;
import jpixeleditor.utils.EditorTools;

@SuppressWarnings("serial")
public class ColourSelectorPanel extends Panel
{
	private class SwapButton extends ButtonsBase
	{
		private Icon swap_inactive;
		private Icon swap_active;
		
		public SwapButton()
		{
			setPreferredSize(new Dimension(20, 20));
						
			try
			{
				swap_inactive = new ImageIcon(ImageIO.read(Main.class.getResource("/Swap_Inactive.png")));
				swap_active = new ImageIcon(ImageIO.read(Main.class.getResource("/Swap_Active.png")));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			addMouseListener(new MouseAdapter() {
				@Override public void mousePressed(MouseEvent me)
				{
					setIcon(swap_active);
				}
				
				@Override public void mouseReleased(MouseEvent me)
				{
					setIcon(swap_inactive);
				}
			});
			
			setIcon(swap_inactive);
		}
		
		@Override public void paintComponent(Graphics g)
		{
			Graphics2D g2d = (Graphics2D)g;
			
			getIcon().paintIcon(this, g2d, 0, 0);
		}
	}
	
	public static ColourSelectorButton csb1;
	public static ColourSelectorButton csb2;
	
	public ColourSelectorPanel()
	{
		super();
		
		setPreferredSize(new Dimension(100, 100));
		setColour(Main.Theme.THEME_BACK_LIGHT);
		
		setLayout(new BorderLayout());
		
		JLayeredPane layers = new JLayeredPane();
		
		csb1 = new ColourSelectorButton(true);
		csb1.setBounds(getPreferredSize().width - csb1.getPreferredSize().width - 10, 10, csb1.getPreferredSize().width, csb1.getPreferredSize().height);
		
		layers.add(csb1, Integer.valueOf(1)); // Add the component to the JLayeredPane, specifying it's z-order / depth with an Integer object
		
		csb2 = new ColourSelectorButton(false);
		csb2.setBounds(10, getPreferredSize().height - csb2.getPreferredSize().height - 10, csb2.getPreferredSize().width, csb2.getPreferredSize().height);
		
		layers.add(csb2, Integer.valueOf(0)); // Add the component to the JLayeredPane, specifying it's z-order / depth with an Integer object
		
		SwapButton swapBtn = new SwapButton();
		swapBtn.setBounds(csb2.getPreferredSize().width + 10, csb1.getPreferredSize().height + 10, swapBtn.getPreferredSize().width, swapBtn.getPreferredSize().height);
		
		Action swapAction = new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e)
			{
				int temp = EditorTools.primaryColour;
				EditorTools.primaryColour = EditorTools.secondaryColour;
				EditorTools.secondaryColour = temp;
				csb1.repaint();
				csb2.repaint();
			}
		};
		swapBtn.addActionListener(swapAction);
		swapBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("X"), "SwapAction");
		swapBtn.getActionMap().put("SwapAction", swapAction);
		String tooltip = "<html><b>Swap Colours</b> (X)\nSwaps the primary and secondary colours";
		ToolTipsHandler.addTooltipTo(swapBtn, tooltip, TooltipLocation.EAST);
		
		layers.add(swapBtn, Integer.valueOf(0));
		
		add(layers, BorderLayout.CENTER);
	}
}
