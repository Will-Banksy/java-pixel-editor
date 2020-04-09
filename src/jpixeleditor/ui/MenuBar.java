package jpixeleditor.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import jpixeleditor.ui.MenuButton.MenuButtonFunction;

@SuppressWarnings("serial")
public class MenuBar extends Panel
{
	public MenuBar(NavigatorPanel navPanel)
	{
		super();
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.PAGE_START;
		add(navPanel, c);
		
		MenuButton openFile = new MenuButton(MenuButtonFunction.OPEN_FILE);
		
		c.gridy = 1;
		c.gridwidth = 1;
		add(openFile, c);
		
		MenuButton saveFile = new MenuButton(MenuButtonFunction.SAVE_AS);
		
		c.gridx = 1;
		c.weighty = 1;
		add(saveFile, c);
	}
}
