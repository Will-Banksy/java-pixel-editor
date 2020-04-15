package jpixeleditor.tools;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import jpixeleditor.ui.Canvas.DrawingSurface;
import jpixeleditor.tools.Tool.MouseButton;
import jpixeleditor.ui.CanvasContainer;
import jpixeleditor.utils.Colour;
import jpixeleditor.utils.MyMap.MyMapEntry;

public abstract class SelectorTool extends Tool
{
	// The reason these are public, static and final is so other classes can access them, have references to them, and also so that subclasses can change it without making it new, final makes sure of that
	public static final ArrayList<MyMapEntry<Point, Integer>> grabbedPixelsMap = new ArrayList<MyMapEntry<Point, Integer>>(); // TODO: Delete the MyMap class and rename/remove MyMapEntry too - Use some other map implementation. Hashmaps are fast, but I had problems with them apparently
	public static final ArrayList<Point> selection = new ArrayList<Point>();
	public static final ArrayList<Point> selectionPreview = new ArrayList<Point>();
	
	// The dragging info
	public static Point selectionOffset = null; // If null, selection not being dragged
	
	private static boolean isDraggingSelection = false;
	
	protected static boolean shouldDoToolAction = true;
	
	// TODO: Fix - When part of the selection is deleted, the corresponding grabbed pixels aren't
	
	public SelectorTool(int id)
	{
		super(id);
	}

	@Override public boolean isSelector()
	{
		return true;
	}
	
	public abstract boolean triggersOnClick();
	
	@Override public void onMousePressed(MouseEvent me)
	{
		super.onMousePressed(me);
		
		if(currMouseButton != MouseButton.RIGHT && currMouseButton != MouseButton.LEFT)
		{
			shouldDoToolAction = false;
			return;
		}
		
		shouldDoToolAction = true;
		
		if(currMouseButton == MouseButton.LEFT)
		{
			if(selection.contains(curr))
			{
				shouldDoToolAction = false;
				isDraggingSelection = true;
				
				if(me.isShiftDown())
				{
					pickupPixels();
				}
			}
			else if(selection.size() > 0 && !triggersOnClick())
			{
				finishSelection();
			}
		}
	}
	
	@Override public void onMouseDragged(MouseEvent me)
	{
		super.onMouseDragged(me);
		
		if(currMouseButton != MouseButton.RIGHT && currMouseButton != MouseButton.LEFT)
		{
			shouldDoToolAction = false;
			return;
		}
		
		if(isDraggingSelection)
		{
			selectionOffset = new Point(curr.x - start.x, curr.y - start.y);
		}
	}
	
	@Override public void onMouseReleased(MouseEvent me)
	{
		super.onMouseReleased(me);
		
		if(currMouseButton != MouseButton.RIGHT && currMouseButton != MouseButton.LEFT)
		{
			shouldDoToolAction = false;
			return;
		}
		
		if(isDraggingSelection)
		{
			if(selectionOffset != null)
				moveSelection();
			
			isDraggingSelection = false;
		}
	}
	
	// mouseReleased fires before mouseClicked
	@Override public void onMouseClicked(MouseEvent me)
	{
		super.onMouseClicked(me);
		
		if(currMouseButton != MouseButton.RIGHT && currMouseButton != MouseButton.LEFT)
		{
			shouldDoToolAction = false;
			return;
		}
		
		// If you click anywhere, it clears the selection
		if(currMouseButton == MouseButton.LEFT && selection.size() > 0)
		{
			finishSelection();
			
			if(triggersOnClick())
				shouldDoToolAction = false;
		}
		else
		{
			shouldDoToolAction = true;
		}
	}
	
	public static void finishSelection()
	{
		selection.clear();
		setPixels();
	}

	private void pickupPixels()
	{
		DrawingSurface surface = CanvasContainer.canvas.surface;
		
		for(Point p : selection)
		{
			if(surface.contains(p.x, p.y) && Colour.getAlpha(surface.gridColours[p.x][p.y]) > 0)
			{
				grabbedPixelsMap.add(new MyMapEntry<Point, Integer>((Point)p.clone(), surface.gridColours[p.x][p.y]));
				surface.gridColours[p.x][p.y] = Colour.TRANSPARENT; 
			}
		}
	}
	
	/**
	 * Also moves grabbed pixels
	 */
	private void moveSelection()
	{
		int loopTo = Math.max(selection.size(), grabbedPixelsMap.size());
		for(int i = 0; i < loopTo; i++)
		{
			if(i < selection.size())
			{
				selection.get(i).x += selectionOffset.x;
				selection.get(i).y += selectionOffset.y;
			}
			if(i < grabbedPixelsMap.size())
			{
				Point p = grabbedPixelsMap.get(i).getKey();
				p.x += selectionOffset.x;
				p.y += selectionOffset.y;
			}
		}
		selectionOffset = null;
	}
	
	private static void setPixels()
	{
		// Put the selection into a LinkedHashSet, so for each grabbed pixel I can check if it's in the selection much more quickly (O(1), instead of O(n))
//		LinkedHashSet<Point> selected = new LinkedHashSet<Point>(selection);
		
		DrawingSurface surface = CanvasContainer.canvas.surface;
		for(MyMapEntry<Point, Integer> entry : grabbedPixelsMap)
		{
			Point p = entry.getKey();
			if(surface.contains(p.x, p.y))// && selected.contains(p))
			{
				surface.gridColours[p.x][p.y] = entry.getValue().intValue(); 
			}
		}
		grabbedPixelsMap.clear();
	}
}
