package jpixeleditor.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

import jpixeleditor.tools.SelectorTool;
import jpixeleditor.utils.EditorTools;

@SuppressWarnings("serial")
public class Toolbar extends Panel
{
	public ColourSelectorPanel colSel;
	
	public Toolbar()
	{
		super();
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		colSel = new ColourSelectorPanel();
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridwidth = 2;
		add(colSel, c);
		
		BrushSizeSelectorPanel sizeSel = new BrushSizeSelectorPanel();
		
		c.gridy = 1;
		c.anchor = GridBagConstraints.PAGE_START;
		//c.weighty = 1; // Only set weighty = 1 on the bottom component, or the layout gets weird and doesn't work as you'd think
		add(sizeSel, c);
		
		ToolButton[] toolBtns = new ToolButton[EditorTools.NUM_TOOLS];
		
		for(int i = 0; i < toolBtns.length; i++)
		{
			// Assign tools to ToolButtons
			toolBtns[i] = new ToolButton(EditorTools.tools[i]);
			
			final int index = i;
			Action action = new AbstractAction() {
				@Override public void actionPerformed(ActionEvent e)
				{
					for(ToolButton tb1 : toolBtns)
					{
						if(tb1.selected)
						{
							tb1.selected = false;
							tb1.repaint();
						}
					}
					toolBtns[index].selected = true;
					toolBtns[index].repaint();
					EditorTools.switchTool(toolBtns[index].tool.id);
					
					// When a toolbutton is clicked, I'd just like the selection and any grabbed pixels to be dismissed
					SelectorTool.selection.clear();
					SelectorTool.grabbedPixelsMap.clear();
					CanvasContainer.canvas.repaint();
				}
			};
			
			toolBtns[i].initialise(action);
			
//			toolBtns[i].addActionListener(new ActionListener() {
//				@Override public void actionPerformed(ActionEvent arg0)
//				{
//					for(ToolButton tb1 : toolBtns)
//					{
//						if(tb1.selected)
//						{
//							tb1.selected = false;
//							tb1.repaint();
//						}
//					}
//					toolBtns[i].selected = true;
//					EditorTools.switchTool(toolBtns[i].tool.id);
//				}
//			});
			
			if(toolBtns[i].tool.id == EditorTools.selectedTool.id)
			{
				toolBtns[i].selected = true;
			}
		}
		
		c.gridwidth = 1;
		for(int i = 0; i < toolBtns.length; i++)
		{
			// Two little algorithms to assign the correct gridy and gridx
			c.gridy = 2 + (int)((float)i / 2f);
			c.gridx = (i + 1) % 2 == 0 ? 1 : 0;
			
			if(i == toolBtns.length - 1)
			{
				c.weighty = 1;
			}
			// System.out.println("i: " + i + "  gridy: " + c.gridy + "  gridx: " + c.gridx);
			add(toolBtns[i], c);
		}
	}
}
