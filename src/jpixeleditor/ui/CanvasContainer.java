package jpixeleditor.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

import jpixeleditor.ui.Canvas.Zoom;
import jpixeleditor.utils.EditorTools;
import jpixeleditor.utils.Helper;

@SuppressWarnings("serial")
public class CanvasContainer extends Panel
{
	public static Canvas canvas;
	
	public CanvasContainer()
	{
		super();
		
		setLayout(new BorderLayout());
		
		canvas = new Canvas();
		add(canvas);
		
		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent me)
			{
				if(!canvas.surface.selectionEmpty())
				{
					if(SwingUtilities.isLeftMouseButton(me))
					{
						canvas.finishSelection();
					}
					if(EditorTools.selectedTool.triggerType != EditorTools.ToolInfo.ON_PRESS)
						canvas.prevSelectionNotEmpty = true;
				}
				canvas.repaint();
			}
		});
		
		addMouseWheelListener(new MouseWheelListener() {
			@Override public void mouseWheelMoved(MouseWheelEvent e)
			{
				//Zoom in
		        if(e.getWheelRotation() < 0)
		        {
		        	canvas.zoom.zoom(e.getX(), e.getY(), Helper.constrain(1.1f * canvas.zoom.scale, Zoom.MIN_ZOOM, Zoom.MAX_ZOOM));
		        	
		        	repaint();
		            canvas.setBounds(0, 0, 0, 0);
		            canvas.repaint();
		            
		            NavigatorPanel.navigator.repaint(); // Updates view rect in paintComponent
		        }
		        
		        //Zoom out
		        if(e.getWheelRotation() > 0)
		        {
		        	canvas.zoom.zoom(e.getX(), e.getY(), Helper.constrain(canvas.zoom.scale / 1.1f, Zoom.MIN_ZOOM, Zoom.MAX_ZOOM));
		        	
		        	repaint();
		            canvas.setBounds(0, 0, 0, 0);
		            canvas.repaint();
		            
		            NavigatorPanel.navigator.repaint(); // Updates view rect in paintComponent
		        }
		    }
		});
	}
}
