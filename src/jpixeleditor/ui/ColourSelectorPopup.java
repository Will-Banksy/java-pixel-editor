package jpixeleditor.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import jpixeleditor.main.Main;
import jpixeleditor.tools.Colour;
import jpixeleditor.tools.EditorTools;

@SuppressWarnings("serial")
public class ColourSelectorPopup extends JPopupMenu
{
	public static int SHADOW_SIZE = 6;
	
	public ColourSelectorPopupPanel panel;
	
	public ColourSelectorPopup(boolean primary, Action openDialogAction)
	{
		super();
		
		// setPreferredSize(new Dimension(220, 220));
		
        setOpaque(false);
        DropShadowBorder border = new DropShadowBorder(SHADOW_SIZE, Color.BLACK, 0.3f, false);
        border.setFillContentArea(false);
        setBorder(new CompoundBorder(border, new LineBorder(Colour.toAWTColor(Main.Theme.THEME_BACK_LIGHT))));
        
        setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
                
        panel = new ColourSelectorPopupPanel(primary ? EditorTools.primaryColour : EditorTools.secondaryColour, primary, openDialogAction);
        
        //c.insets = new Insets(SHADOW_SIZE * 2, SHADOW_SIZE * 2, SHADOW_SIZE * 2, SHADOW_SIZE * 2);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        add(panel, c);
        
        pack();
        
        addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent ke)
        	{
        		if(ke.getKeyCode() == KeyEvent.VK_ESCAPE)
        		{
        			setVisible(false);
        		}
        	}
        });
	}
	
	@Override public void paintComponent(Graphics g)
	{
		// Need to override this to stop the this displaying
	}
}