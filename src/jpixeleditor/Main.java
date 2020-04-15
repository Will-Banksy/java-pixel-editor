package jpixeleditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import jpixeleditor.ui.CanvasContainer;
import jpixeleditor.ui.MenuBar;
import jpixeleditor.ui.NavigatorPanel;
import jpixeleditor.ui.Panel;
import jpixeleditor.ui.ToolConfigPanel;
import jpixeleditor.ui.Toolbar;
import jpixeleditor.utils.Colour;

public class Main
{
	public static class Theme
	{
		public static final int THEME_BACK_LIGHT = Colour.toIntARGB(255, 48, 61, 70);//Colour.toIntARGB(255, 59, 71, 76);
		public static final int THEME_BACK_DARK = Colour.toIntARGB(255, 38, 50, 56);
		public static final int THEME_ACCENT = Colour.toIntARGB(255, 0, 188, 212);
		public static final int THEME_BUTTON = Colour.toIntARGB(255, 58, 70, 76);
		public static final int THEME_BUTTON_HOVER = Colour.toIntARGB(255, 78, 90, 96);
	}
	
	public static JFrame windowFrame;
	public static Container contentPane;
	public static Panel uiContainer;
	public static Toolbar toolbarPanel;
	public static CanvasContainer canvasPanel;
	public static MenuBar menubarPanel;
	public static ToolConfigPanel configPanel;
	public static NavigatorPanel navPanel;
	
	public static void main(String[] args)
	{
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override public void run()
//			{
//				Initialise();
//			}
//		});
		
		// We want nicely antialiased fonts. So okay, they're not as beautifully AA'd as if I'd set the rendering hints but that would be annoying to do for every component
		System.setProperty("awt.useSystemAAFontSettings","on");
		System.setProperty("swing.aatext", "true");
		
		initialise();
	}
	
	public static void initialise()
	{
		try
		{
		    for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
		    {
		        if ("Nimbus".equals(info.getName())) // 'Nimbus', a Java Look And Feel, better looking than the default Java Look And Feel, 'Metal'
		        {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		
		windowFrame = new JFrame("Java Pixel Editor");
		windowFrame.setSize(800, 600);
		windowFrame.setMinimumSize(new Dimension(800, 600));
		windowFrame.setLocationRelativeTo(null);
		windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		windowFrame.setLayout(new BorderLayout());
		contentPane = windowFrame.getContentPane();
		
		uiContainer = new Panel();
		uiContainer.setColour(Theme.THEME_BACK_DARK);
		uiContainer.setLayout(new GridBagLayout());
		
		configPanel = new ToolConfigPanel();
		initialiseToolConfig();
		
		toolbarPanel = new Toolbar();
		initialiseToolbar();
		
		canvasPanel = new CanvasContainer();
		initialiseCanvas();
		
		navPanel = new NavigatorPanel();
		
		menubarPanel = new MenuBar(navPanel);
		initialiseMenuBar();
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		uiContainer.add(configPanel, c);
		
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.PAGE_START;
		uiContainer.add(toolbarPanel, c);
		
		c.gridx = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.PAGE_START;
		uiContainer.add(canvasPanel, c);
		
		c.gridx = 2;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.PAGE_START;
		uiContainer.add(menubarPanel, c);
		
		contentPane.add(uiContainer, BorderLayout.CENTER);
		
		// The jmenubar is not picked up by my Application Menu widget
//		JMenuBar menubar = new JMenuBar();
//		
//		JMenu menu = new JMenu("A Menu");
//		
//		menu.getAccessibleContext().setAccessibleDescription(
//		        "The only menu in this program that has menu items");
//		menubar.add(menu);
//
//		JMenuItem menuItem = new JMenuItem("A text-only menu item");
//		menuItem.getAccessibleContext().setAccessibleDescription(
//				"This doesn't really do anything");
//		menu.add(menuItem);
//
//		windowFrame.setJMenuBar(menubar);
		
		windowFrame.validate();
		windowFrame.setVisible(true);
	}
	
	public static void initialiseToolConfig()
	{
		configPanel.setColour(Theme.THEME_BACK_LIGHT);
	}
	
	public static void initialiseToolbar()
	{
		toolbarPanel.setColour(Theme.THEME_BACK_LIGHT);
		toolbarPanel.setPreferredSize(new Dimension(100, 500));
	}
	
	public static void initialiseCanvas()
	{
		canvasPanel.setColour(Theme.THEME_BACK_DARK);
	}
	
	public static void initialiseMenuBar()
	{
		menubarPanel.setColour(Theme.THEME_BACK_LIGHT);
		menubarPanel.setPreferredSize(new Dimension(200, 400));
	}
}
