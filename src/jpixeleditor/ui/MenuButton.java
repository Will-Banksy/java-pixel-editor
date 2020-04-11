package jpixeleditor.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import jpixeleditor.main.Main;
import jpixeleditor.ui.ToolTipsHandler.TooltipLocation;
import jpixeleditor.utils.Helper;
import jpixeleditor.utils.IOTools;

@SuppressWarnings("serial")
public class MenuButton extends ButtonsBase
{
	public BufferedImage icon = null;
	
	public String lastOpenDir = null;
	public String lastSaveDir = null;
	
	public String workingPath = null;
	
	enum MenuButtonFunction
	{
		OPEN_FILE,
		SAVE_AS,
		SAVE
	}
	
	public MenuButton(MenuButtonFunction fn)
	{
		super();
		
		String iconFilename = "";
		String buttonName = "";
		String description = "";
		KeyStroke shortcut = null;
		String shortcutDescriptor = "";
		Action action = null;
		
		switch(fn)
		{
			case OPEN_FILE:
				iconFilename = "/Folder.png";
				buttonName = "Open File";
				description = "Opens a file for you to edit.\nRemember this is a pixel editor,\ndon't open massive images";
				shortcut = KeyStroke.getKeyStroke("control O");
				shortcutDescriptor = "Ctrl+O";
				
				action = new AbstractAction() {
					@Override public void actionPerformed(ActionEvent arg0)
					{
						JFileChooser ch = new JFileChooser();
						
						// A file extension filter is always useful! ImageIO.getReaderFileSuffixes is useful because it provides an up-to-date list of all file types ImageIO can read
						// Also showing the user a list of supported file extensions is useful for them
						String[] extensions = ImageIO.getReaderFileSuffixes();
						ch.setFileFilter(new FileNameExtensionFilter("Image Files (" + Helper.arrayAsCommaSeparatedList(extensions) + ")", extensions));
						
						// This returns a value depending on what the user did with the file chooser
						int option = ch.showOpenDialog(Main.windowFrame);
						
						// If the user actually opens a file
						if(option == JFileChooser.APPROVE_OPTION)
						{
							File file = ch.getSelectedFile();
							
							System.out.println(file.getName());
							
							try
							{
								BufferedImage img = ImageIO.read(file);
								if(img == null)
								{
									// I'm literally only throwing this exception to skip to the catch where it shows a message dialog
									throw new IOException("Unsupported file type");
								}
								
								IOTools.ImportImage(img);
								
								workingPath = file.getPath();
								Main.windowFrame.setTitle("Java Pixel Editor - " + file.getName());
							}
							catch (IOException e)
							{
								JOptionPane.showMessageDialog(Main.windowFrame, "Unsupported file type");
								e.printStackTrace();
							}
						}
					}
				};
				break;
				
			case SAVE:
				iconFilename = "/Save.png";
				buttonName = "Save";
				description = "Saves the canvas content";
				shortcut = KeyStroke.getKeyStroke("control S");
				shortcutDescriptor = "Ctrl+S";
				
				if(workingPath != null || !workingPath.endsWith(".png"))
				{
					File file = new File(workingPath);
					
					System.out.println(file.getName());
					
					IOTools.ExportPNG(file);
				}
				// Fall through to Save As case if workingPath is null. Doing fall through because Java doesn't have goto
				
			case SAVE_AS:
				iconFilename = "/Save_As.png";
				buttonName = "Save As";
				description = "Saves the canvas content as a PNG image";
				shortcut = KeyStroke.getKeyStroke("control shift S");
				shortcutDescriptor = "Ctrl+Shift+S";
				
				action = new AbstractAction() {
					@Override public void actionPerformed(ActionEvent e)
					{
						JFileChooser fch = new JFileChooser() {
							@Override public void approveSelection()
							{
								File f = getSelectedFile();
								
								if(f.exists() && getDialogType() == SAVE_DIALOG)
								{
									int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
									switch(result)
									{
										case JOptionPane.YES_OPTION:
											super.approveSelection();
											return;
											
										case JOptionPane.NO_OPTION:
											return;
											
										case JOptionPane.CLOSED_OPTION:
											return;
											
										case JOptionPane.CANCEL_OPTION:
											cancelSelection();
											return;
									}
								}
								super.approveSelection();
							}
						};

						fch.setFileFilter(new FileNameExtensionFilter("PNG Image (.png)", "png"));
						
						int option = fch.showSaveDialog(Main.windowFrame);
						
						if(option == JFileChooser.APPROVE_OPTION)
						{
							File file = fch.getSelectedFile();
							
							if(!file.getName().endsWith(".png"))
							{
								file = new File(file.getPath() + ".png");
							}
							
							System.out.println(file.getPath());
							
							IOTools.ExportPNG(file);
							
							workingPath = file.getPath();
							Main.windowFrame.setTitle("Java Pixel Editor - " + file.getName());
						}
					}
				};
				break;
				
			default:
				break;
		}
		
		try
		{
			icon = ImageIO.read(Main.class.getResource(iconFilename));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		addActionListener(action);
		
		setPreferredSize(new Dimension(50, 50));
		
		String tooltip = "<html><b>" + buttonName + "</b> (" + shortcutDescriptor + ")\n" + description;
		
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(shortcut, "ClickThis");
		getActionMap().put("ClickThis", action);
		
		ToolTipsHandler.addTooltipTo(this, tooltip, TooltipLocation.WEST);
	}
	
	@Override public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		
		// g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.drawImage(icon, getWidth() / 2 - icon.getWidth() / 2, getHeight() / 2 - icon.getHeight() / 2, icon.getWidth(), icon.getHeight(), null);
	}
}
