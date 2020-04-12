package jpixeleditor.utils;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

import jpixeleditor.tools.FreeSelection;
import jpixeleditor.tools.SelectorTool;
import jpixeleditor.ui.Canvas;
import jpixeleditor.ui.CanvasContainer;
import jpixeleditor.ui.Canvas.DrawingSurface;
import jpixeleditor.ui.ToolConfigPanel.ToolSettings;
import jpixeleditor.utils.Helper.Group;
import jpixeleditor.utils.MyMap.MyMapEntry;
import jpixeleditor.utils.PaintHandler.Queue;

public class SelectionHandler
{
	public static void magicSelect(int x, int y, boolean rightClick, ToolSettings settings)
	{
		Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;

		// Get tolerance from settings
		int tolerance = settings.tolerance;

		// These are sort of aliases. I only have these to reduce calls to DrawingSurface.getGridDims
		int numX = surface.getGridDims().width;
		int numY = surface.getGridDims().height;

		// The target colour is the colour at the current point
		int targetCol = surface.gridColours[x][y];

		//surface.gridColours[i][j] = colour;

		// Need to keep track of what pixels have been coloured/filled
		boolean[][] filled = new boolean[numX][numY];
		
		ArrayList<Point> newSelection = new ArrayList<Point>();

		Queue q = new Queue();

		q.push(x, y);

		while(!q.isEmpty())
		{
			// While the queue is not empty, get a coordinate from the queue and fill it
			Point p = q.pop();

			// surface.gridColours[p.x][p.y] = colour;
			newSelection.add(new Point(p.x, p.y));
			
			// And then check each pixel immediately to every side of the current one
			// When adding to the queue, make sure you fill that pixel as filled to avoid unnecessary iteration
			if(checkValidity(p.x + 1, p.y, targetCol, tolerance, filled))
			{
				q.push(p.x + 1, p.y);
				filled[p.x + 1][p.y] = true;
			}
			if(checkValidity(p.x - 1, p.y, targetCol, tolerance, filled))
			{
				q.push(p.x - 1, p.y);
				filled[p.x - 1][p.y] = true;
			}
			if(checkValidity(p.x, p.y + 1, targetCol, tolerance, filled))
			{
				q.push(p.x, p.y + 1);
				filled[p.x][p.y + 1] = true;
			}
			if(checkValidity(p.x, p.y - 1, targetCol, tolerance, filled))
			{
				q.push(p.x, p.y - 1);
				filled[p.x][p.y - 1] = true;
			}

			// This means it will check the pixels immediately diagonally from it as well, if ToolSettings.fill8Way is enabled
			if(settings.fill8Way)
			{
				if(checkValidity(p.x + 1, p.y + 1, targetCol, tolerance, filled))
				{
					q.push(p.x + 1, p.y + 1);
					filled[p.x + 1][p.y + 1] = true;
				}
				if(checkValidity(p.x - 1, p.y - 1, targetCol, tolerance, filled))
				{
					q.push(p.x - 1, p.y - 1);
					filled[p.x - 1][p.y - 1] = true;
				}
				if(checkValidity(p.x - 1, p.y + 1, targetCol, tolerance, filled))
				{
					q.push(p.x - 1, p.y + 1);
					filled[p.x - 1][p.y + 1] = true;
				}
				if(checkValidity(p.x + 1, p.y - 1, targetCol, tolerance, filled))
				{
					q.push(p.x + 1, p.y - 1);
					filled[p.x + 1][p.y - 1] = true;
				}
			}

			if(rightClick)
			{
				if(settings.selectionAppend)
				{
					// If we are set to selection append, we add the new selection
					// Use a LinkedHasSet because it disallows duplicates and it's contains method is apparently O(1) instead of ArrayLists' O(n) which is amazing (https://stackoverflow.com/questions/17547360/create-an-arraylist-of-unique-values)
					LinkedHashSet<Point> set = new LinkedHashSet<Point>(SelectorTool.selection);
					
					// Add all entries in newSelection to the current selection
					set.addAll(newSelection);
					
					SelectorTool.selection.clear();
					SelectorTool.selection.addAll(set);
				}
				else
				{
					// If we are not set to selection append, we remove the new selection
					// Use a LinkedHasSet because it disallows duplicates and it's contains method is apparently O(1) instead of ArrayLists' O(n) which is amazing (https://stackoverflow.com/questions/17547360/create-an-arraylist-of-unique-values)
					LinkedHashSet<Point> set = new LinkedHashSet<Point>(newSelection);
					
					// Instead of removing elements, we'll just create an ArrayList and add the ones that aren't in the set to it
					ArrayList<Point> newEntries = new ArrayList<Point>();
					for(int i = 0; i < SelectorTool.selection.size(); i++)
					{
						// Taking advantage of the O(1)
						if(!set.contains(SelectorTool.selection.get(i)))
						{
							newEntries.add(SelectorTool.selection.get(i));
						}
					}
					
					SelectorTool.selection.clear();
					SelectorTool.selection.addAll(newEntries);
				}
			}
			else
			{
				// Now just make the newSelection the new gridSelection, if we are just doing a normal left click
				SelectorTool.selection.clear();
				SelectorTool.selection.addAll(newSelection);
			}
		}
	}
		
	private static boolean checkValidity(int i, int j, int targetColour, int tolerance, boolean[][] filled)
	{
		DrawingSurface surface = CanvasContainer.canvas.surface;
		if(i >= 0 && i < surface.getGridDims().width && j >= 0 && j < surface.getGridDims().height)
		{
			if(canFill(targetColour, surface.gridColours[i][j], tolerance) || (Colour.getAlpha(surface.gridColours[i][j]) == 0 && Colour.getAlpha(targetColour) == 0))
			{
				if(!filled[i][j])
				{
					return true;
				}
				//System.out.println("Filled Matrix [i][j]: " + filled[i][j]);
			}
		}
		return false;
	}

	private static boolean canFill(int targetColour, int colour, int tolerance)
	{
		int diff = Colour.getDifference(targetColour, colour, true);
		return Helper.map(diff, 0, 510, 0, 255) <= tolerance;
	}
	
	public static void colourSelect(Point point, boolean rightClick, ToolSettings settings)
	{
		Canvas.DrawingSurface surface = CanvasContainer.canvas.surface;
		
		int targetCol = surface.gridColours[point.x][point.y];
		
		ArrayList<Point> newSelection = new ArrayList<Point>();
		
		for(int i = 0; i < surface.getGridDims().width; i++)
		{
			for(int j = 0; j < surface.getGridDims().height; j++)
			{
				if(canFill(targetCol, surface.gridColours[i][j], settings.tolerance) || (Colour.getAlpha(surface.gridColours[i][j]) == 0 && Colour.getAlpha(targetCol) == 0))
				{
					newSelection.add(new Point(i, j));
				}
			}
		}
		
		if(rightClick)
		{
			if(settings.selectionAppend)
			{
				// If we are set to selection append, we add the new selection
				// Use a LinkedHasSet because it disallows duplicates and it's contains method is apparently O(1) instead of ArrayLists' O(n) which is amazing (https://stackoverflow.com/questions/17547360/create-an-arraylist-of-unique-values)
				LinkedHashSet<Point> set = new LinkedHashSet<Point>(SelectorTool.selection);
				
				// Add all entries in newSelection to the current selection
				set.addAll(newSelection);
				
				SelectorTool.selection.clear();
				SelectorTool.selection.addAll(set);
			}
			else
			{
				// If we are not set to selection append, we remove the new selection
				// Use a LinkedHasSet because it disallows duplicates and it's contains method is apparently O(1) instead of ArrayLists' O(n) which is amazing (https://stackoverflow.com/questions/17547360/create-an-arraylist-of-unique-values)
				LinkedHashSet<Point> set = new LinkedHashSet<Point>(newSelection);
				
				// Instead of removing elements, we'll just create an ArrayList and add the ones that aren't in the set to it
				ArrayList<Point> newEntries = new ArrayList<Point>();
				for(int i = 0; i < SelectorTool.selection.size(); i++)
				{
					// Taking advantage of the O(1)
					if(!set.contains(SelectorTool.selection.get(i)))
					{
						newEntries.add(SelectorTool.selection.get(i));
					}
				}
				
				SelectorTool.selection.clear();
				SelectorTool.selection.addAll(newEntries);
			}
		}
		else
		{
			// Now just make the newSelection the new gridSelection, if we are just doing a normal left click
			SelectorTool.selection.clear();
			SelectorTool.selection.addAll(newSelection);
		}
	}

	public static void drawSelectionRectangle(Point start, Point end, boolean rightClick, boolean preview, ToolSettings settings)
		{
			DrawingSurface surface = CanvasContainer.canvas.surface;
			
			int startX = Math.min(start.x, end.x);
			int startY = Math.min(start.y, end.y);
			int endX = Math.max(start.x, end.x);
			int endY = Math.max(start.y, end.y);
			
			SelectorTool.selectionPreview.clear();
			
			// If we are not previewing, we're going to need a new map
			ArrayList<Point> newSelection = new ArrayList<Point>();
			
			for(int i = startX; i <= endX; i++)
			{
				for(int j = startY; j <= endY; j++)
				{
					if(i >= 0 && i < surface.getGridDims().width && j >= 0 && j < surface.getGridDims().height)
					{
						if(preview)
						{
	//						surface.gridOverlay[i][j] = Colour.fromAWTColor(Canvas.SELECTION_COLOUR);
							SelectorTool.selectionPreview.add(new Point(i, j));
						}
						else
						{
							newSelection.add(new Point(i, j));
						}
					}
				}
			}
			
			if(rightClick)
			{
				if(settings.selectionAppend)
				{
					// If we are set to selection append, we add the new selection
					// Use a LinkedHasSet because it disallows duplicates and it's contains method is apparently O(1) instead of ArrayLists' O(n) which is amazing (https://stackoverflow.com/questions/17547360/create-an-arraylist-of-unique-values)
					LinkedHashSet<Point> set = new LinkedHashSet<Point>(SelectorTool.selection);
					
					// Add all entries in newSelection to the current selection
					set.addAll(newSelection);
					
					SelectorTool.selection.clear();
					SelectorTool.selection.addAll(set);
				}
				else
				{
					// If we are not set to selection append, we remove the new selection
					// Use a LinkedHasSet because it disallows duplicates and it's contains method is apparently O(1) instead of ArrayLists' O(n) which is amazing (https://stackoverflow.com/questions/17547360/create-an-arraylist-of-unique-values)
					LinkedHashSet<Point> set = new LinkedHashSet<Point>(newSelection);
					
					// Instead of removing elements, we'll just create an ArrayList and add the ones that aren't in the set to it
					ArrayList<Point> newEntries = new ArrayList<Point>();
					for(int i = 0; i < SelectorTool.selection.size(); i++)
					{
						// Taking advantage of the O(1)
						if(!set.contains(SelectorTool.selection.get(i)))
						{
							newEntries.add(SelectorTool.selection.get(i));
						}
					}
					
					SelectorTool.selection.clear();
					SelectorTool.selection.addAll(newEntries);
				}
			}
			else
			{
				// Now just make the newSelection the new gridSelection, if we are just doing a normal left click
				SelectorTool.selection.clear();
				SelectorTool.selection.addAll(newSelection);
			}
		}

		public static void drawFreeSelection(Point start, Point end, boolean rightClick, boolean preview, ToolSettings settings)
		{
			// All your standard local variables as references to global variables creation, with your classic null checks
			DrawingSurface surface = CanvasContainer.canvas.surface;
			
			ArrayList<Point> path = FreeSelection.lassoPath;
			
			ArrayList<Point> pathClose = FreeSelection.pathClose;
			
			// We will need the previous selection if we are right clicking, which we don't want to clear the previous selection
	//		@SuppressWarnings("unchecked")
	//		ArrayList<MyMapEntry<Point, Boolean>> prevSelection = rightClick ? (ArrayList<MyMapEntry<Point, Boolean>>)surface.gridSelection.getEntries().clone() : null;
			
			// Worth pointing out: end will actually be just the point the mouse is at, if preview. preview means that this is called when the mouse is dragging, as a way of updating the preview
			
			// If we just need to update the preview, and not calculate what points are actually inside the selected area
			if(preview)
			{
				// Create a rectangle that will be used for passing to Helper.plotLine_InsideConstraints
				Rectangle canvasRect = new Rectangle(0, 0, surface.gridWidth, surface.gridHeight);
				
				Point[] line;
				if(path.isEmpty()) // If the path has just started, then the start point for the path will be the startPoint parameter
				{
					line = Helper.plotLine_InsideConstraints(start, end, canvasRect);
				}
				else // Else, the start point for this segment will be the last added point in the path ArrayList, and I can get that easily
				{
					// And, for this, we also need to add the line of points from start to end, i.e. the line that closes the free selection
					Point[] endToStart = Helper.plotLine_InsideConstraints(end, start, canvasRect);
					
					// We need to add them to the pathClose ArrayList in Canvas, after we clear it, as it'll be different each time
					pathClose.clear();
					pathClose.addAll(new ArrayList<Point>(Arrays.asList(endToStart)));
					
					// Now the clever bit - If we plot a line from the lastPoint to the first Point in endToStart, that will be inside the canvas, because we used Helper.plotLine_InsideConstraints, which excludes any points that aren't, so we'll just get a line that is completely inside the canvas
					Point lastPoint = path.get(path.size() - 1);
					line = Helper.plotLine_InsideConstraints(lastPoint, endToStart[0], canvasRect);
				}
				
				// Arrays.asList returns a List with a fixed size. Lists are different to ArrayList, so I'm just turning it into an ArrayList for peace of mind
				path.addAll(new ArrayList<Point>(Arrays.asList(line)));
				
				// To make this significantly faster and less messy, I'm just going to get the canvas to draw everything in path and pathClose
				
				// --
				// We need to change the ArrayList<Point>s we have into an ArrayList<MyMapEntry<Point, Boolean>>
				// Loop up to the biggest length out of pathClose and path
				// MyMap.put is a very expensive operation - This section can get very slow
	//			int loopTo = Math.max(pathClose.size(), path.size());
	//			for(int i = 0; i < loopTo; i++)
	//			{
	//				// Just doing this to avoid doing two loops, which would most likely be slower
	//				if(i < path.size())
	//				{
	//					surface.gridSelection.put(path.get(i), Boolean.valueOf(true));
	//				}
	//				if(i < pathClose.size())
	//				{
	//					surface.gridSelection.put(pathClose.get(i), Boolean.valueOf(true));
	//				}
	//			}
				
	//			// What we can do is put all the points into a boolean matrix, that will get rid of duplicates as well, without looping too much, meaning we just need to retrieve the ArrayList back from the matrix, and avoid using MyMap.put at all!
	//			// We can do this because we know that all points we are adding to the selection will be inside the canvas bounds
	//			boolean[][] selected = new boolean[surface.gridWidth][surface.gridHeight];
	//			
	//			// Put each pixel from path and pathClose into selected
	//			// Loop up to the biggest length out of pathClose and path and prevSelection, if prevSelection is not null
	//			int loopTo = (int)Helper.getMax(pathClose.size(), path.size(), prevSelection != null ? prevSelection.size() : 0);
	//			for(int i = 0; i < loopTo; i++)
	//			{
	//				// Just doing this to avoid doing two loops, which would most likely be slightly slower and more code
	//				if(i < path.size())
	//				{
	//					int x = path.get(i).x;
	//					int y = path.get(i).y;
	//					if(surface.contains(x, y))
	//					{
	//						selected[x][y] = true;
	//					}
	//				}
	//				if(i < pathClose.size())
	//				{
	//					int x = pathClose.get(i).x;
	//					int y = pathClose.get(i).y;
	//					if(surface.contains(x, y))
	//					{
	//						selected[x][y] = true;
	//					}
	//				}
	//				// The point of this is to make sure that selected doesn't contain any points that appear in prevSelected
	//				if(prevSelection != null)
	//				{
	//					if(i < prevSelection.size())
	//					{
	//						Point p = prevSelection.get(i).getKey();
	//						if(surface.contains(p.x, p.y))
	//						{
	//							selected[p.x][p.y] = false; 
	//						}
	//					}
	//				}
	//			}
	//			
	//			// Create a new Map
	//			MyMap<Point, Boolean> newSelection = new MyMap<Point, Boolean>();
	//			
	//			for(int i = 0; i < selected.length; i++)
	//			{
	//				for(int j = 0; j < selected[i].length; j++)
	//				{
	//					// Only add to the map if this pixel is selected
	//					if(selected[i][j])
	//					{
	//						// Since we know that each entry we are going to add is going to be unique, we can just do that, which saves A LOT of time, especially on larger images
	//						newSelection.getEntries().add(new MyMapEntry<Point, Boolean>(new Point(i, j), Boolean.valueOf(true)));
	//					}
	//				}
	//			}
	//			
	//			// And then add all of the previous selection, if it's not null
	//			if(prevSelection != null)
	//				newSelection.getEntries().addAll(prevSelection);
	//			
	//			// Now just make the newSelection the new gridSelection
	//			surface.gridSelection = newSelection;
			}
			else // If we're not doing preview, then we've released the mouse button, time to compute. This is an algorithm I came up with entirely by myself, I'm very proud of it and it works well and it's fast (Tested on larger images - still fast). It probably already exists but I couldn't find it
			{
				// Matrix of selected pixels
				boolean[][] selected = new boolean[surface.gridWidth][surface.gridHeight];
				
				// Put each pixel from path and pathClose into selected
				// Loop up to the biggest length out of pathClose and path
				int loopTo = Math.max(pathClose.size(), path.size());
				for(int i = 0; i < loopTo; i++)
				{
					// Just doing this to avoid doing two loops, which would most likely be slightly slower and more code
					if(i < path.size())
					{
						int x = path.get(i).x;
						int y = path.get(i).y;
						if(surface.contains(x, y))
						{
							selected[x][y] = true;
						}
					}
					if(i < pathClose.size())
					{
						int x = pathClose.get(i).x;
						int y = pathClose.get(i).y;
						if(surface.contains(x, y))
						{
							selected[x][y] = true;
						}
					}
				}
				
				/*
				 * Note: Every time I say row I mean column
				 * 
				 * This is my algorithm for lasso selection, it's based on a few assumptions we can safely make, along with a bit of good old fashioned process by elimination
				 * 
				 * After Performance Testing: Haha my algorithm is faster than piskels, I think
				 */
				
				
				// This matrix is used for storing positions at which if you fill in there, it will not be a valid fill
				boolean[][] doNotFill = new boolean[surface.gridWidth][surface.gridHeight];
				int PN = -1;
				
				for(int i = 0; i < selected.length; i++)
				{
					// If there were no previous groups, skip this row, as no previous groups means that there can't be any enclosed areas, so continue (If it's -1 that means that we're on the first row, so the previous applies)
					if(PN <= 0)
					{
						// But that means we do need to calculate the number of groups for this row still, as otherwise we just won't get past here
						PN = Helper.getGroupingInfo(selected, i, false).size();
						continue;
					}
					
					ArrayList<Group> groups = new ArrayList<Group>();
					boolean inGroup = false;
					boolean prevInGroup = false;
					int currGroupIndex = -1;
					
					for(int j = 0; j < selected[i].length + 1; j++)
					{
						// If the i is out of bounds of selected, then this is false, otherwise, the value at the position (i, j)
						inGroup = (j < selected[i].length) ? selected[i][j] : false;
						
						if(inGroup && !prevInGroup)
						{
							// If we've entered a new group, add 1 onto the currGroupIndex and add a Group onto the groups ArrayList, with start position at j - 1
							currGroupIndex++;
							groups.add(new Group(j - 1));
						}
						else if(!inGroup && prevInGroup)
						{
							// If we've just exited a group, then the end index of the current group will be j
							groups.get(currGroupIndex).endIndex = j;
							
							// If this is at least the second group, backtrack and fill in just after the previous group. The Group.endIndex is actually one pixel after the last pixel in the group, so use that
							// How this is done means that we don't need a fair amount of the logic we had previously, as this provides the same effect
							if(currGroupIndex > 0)
							{
								SelectionHandler.FloodFillTest(new Point(i, groups.get(currGroupIndex - 1).endIndex), selected, doNotFill);
							}
						}
						
						prevInGroup = inGroup;
					}
					
					// If there were groups previously but there aren't any more, that means we've stopped encountering groups, so we can just break out
					if(PN > 0 && groups.size() == 0)
						break;
					
					PN = groups.size();
				}
				
				/*
				// PN - Previous Number of groups
				int PN = -1;
				
				// N - Number of groups
				int N = -1;
				
				// If we've had a group, and then we get to the point we don't have any more, then we can just break the loop because we know that there can't be any more groups after this
				boolean bailIfNoMore = false;
				
				for(int i = 0; i < selected.length; i++)
				{
					// Get Helper to calculate the ArrayList of groups in this row
					ArrayList<Group> group = Helper.getGroupingInfo(selected, i, false);
					
					// Set the previous number of groups here, otherwise we'll never reach it
					PN = N;
					
					// Set the current number of groups
					N = group.size();
					
					// We've encountered groups, but stopped now, so we break out of the loop
					if(N == 0 && bailIfNoMore)
						break;
					
					// If the number of groups is 1 or zero, doing flood filling won't do any good, may as well just not. So continue to the next row
					if(N <= 1)
						continue;
					
					// If there are groups: We've encountered a group - so if we stop encountering groups, break out of the loop
					if(N > 0)
						bailIfNoMore = true;
					
					// If there were no previous groups, skip this row, as no previous groups means that there can't be any enclosed areas, so continue (If it's -1 that means that we're on the first row, so the previous applies)
					if(PN <= 0)
						continue;
					
					// The index of the group we're currently in/were in
					int currGroupIndex = -1;
					
					boolean inGroup = false;
					boolean prevInGroup = false;
					int successfulTests = 0;
					
					for(int j = 0; j < selected[i].length; j++)
					{
						inGroup = selected[i][j];
						
						if(inGroup && !prevInGroup)
						{
							currGroupIndex++;
							
							// If this is the last group, there cannot be an enclosed area after it, so we just break the loop
							if(currGroupIndex == group.size() - 1)
								break;
						}
						else if(!inGroup && prevInGroup)
						{
							// Else, it might well be, so let's try a flood fill
							if(FloodFillTest(new Point(i, j), selected))
							{
								successfulTests++;
								
								// The maximum successful tests we need to do will be the smallest out of N and PN - as there can only be so many closed areas as there are groups in either the previous row or this row
								int maxSuccessfulTests = Math.min(N, PN);
								
								// If the amount of successful tests is greater than the max successful tests, then move onto the next row, as there is no point doing any more tests
								if(successfulTests >= maxSuccessfulTests)
									break;
							}
						}
						
						prevInGroup = inGroup;
					}
				}
				*/
				
				// And now, we have completed our selection, we have our matrix, now we just need to put that into the surface.gridSelection map
				
				// Create a new Map
				ArrayList<Point> newSelection = new ArrayList<Point>();
				
				for(int i = 0; i < selected.length; i++)
				{
					for(int j = 0; j < selected[i].length; j++)
					{
						// Only add to the map if this pixel is selected
						if(selected[i][j])
						{
							// Since we know that each entry we are going to add is going to be unique, we can just do that, which saves A LOT of time, especially on larger images
							newSelection.add(new Point(i, j));
						}
					}
				}
				
				if(rightClick)
				{
					if(settings.selectionAppend)
					{
						// If we are set to selection append, we add the new selection
						// Use a LinkedHasSet because it disallows duplicates and it's contains method is apparently O(1) instead of ArrayLists' O(n) which is amazing (https://stackoverflow.com/questions/17547360/create-an-arraylist-of-unique-values)
						LinkedHashSet<Point> set = new LinkedHashSet<Point>(SelectorTool.selection);
						
						// Add all entries in newSelection to the current selection
						set.addAll(newSelection);
						
						SelectorTool.selection.clear();
						SelectorTool.selection.addAll(set);
					}
					else
					{
						// If we are not set to selection append, we remove the new selection
						// Use a LinkedHasSet because it disallows duplicates and it's contains method is apparently O(1) instead of ArrayLists' O(n) which is amazing (https://stackoverflow.com/questions/17547360/create-an-arraylist-of-unique-values)
						LinkedHashSet<Point> set = new LinkedHashSet<Point>(newSelection);
						
						// Instead of removing elements, we'll just create an ArrayList and add the ones that aren't in the set to it
						ArrayList<Point> newEntries = new ArrayList<Point>();
						for(int i = 0; i < SelectorTool.selection.size(); i++)
						{
							// Taking advantage of the O(1)
							if(!set.contains(SelectorTool.selection.get(i)))
							{
								newEntries.add(SelectorTool.selection.get(i));
							}
						}
						
						SelectorTool.selection.clear();
						SelectorTool.selection.addAll(newEntries);
					}
				}
				else
				{
					// Now just make the newSelection the new gridSelection, if we are just doing a normal left click
					SelectorTool.selection.clear();
					SelectorTool.selection.addAll(newSelection);
				}
				
				// And we also need to clear the path and pathClose ArrayLists in Canvas, otherwise things go a bit pear-shaped. (We have references to both those ArrayLists)
				path.clear();
				pathClose.clear();
			}
		}

	static boolean FloodFillTest(Point origin, boolean[][] selected, boolean[][] doNotFill)
	{
		// Grid Width and Grid Height
		int gw = selected.length;
		int gh = selected[0].length;
		
		// Do some constraints checking - if any of the parameters of the origin +/- 1 are out of bounds then return false immediately - this is not a valid fill
		if(origin.x + 1 >= gw || origin.x - 1 < 0 || origin.y + 1 >= gh || origin.y - 1 < 0)
		{
			return false;
		}
		// This won't happen much, but optimization is always good.
		// If this does happen, we'll want to return true, because if this happens, it means that we have done a successful fill previously,
		// and that successful fill filled the area that this fill would've filled otherwise. 
		else if(selected[origin.x][origin.y]) 
		{
			return true;
		}
		// If this tile is already known to be a bad fill, then don't even try it because we know the outcome
		else if(doNotFill[origin.x][origin.y])
		{
			return false;
		}
		
		// Need a copy of the matrix so we don't change the original
		boolean[][] filled = new boolean[gw][gh];
		Helper.copyMatrix(selected, filled);
		
		// Need a copy of the matrix so we don't change the original
		// This matrix will record new pixels we fill, which can be used to add to doNotFill
		// Use an ArrayList to cut down on the amount of iteration when putting this into doNotFill
		ArrayList<Point> newFilled = new ArrayList<Point>();
		
		// New Queue - add the starting point to it
		PaintHandler.Queue q = new PaintHandler.Queue();
		q.push(origin.x, origin.y);
		
		// Found the issue where single pixel gaps were being left - I did't set filled[origin.x][origin.y] to true!
		// You wouldn't notice it in areas larger than 1 pixel, as it would be filled from other pixels, but with one pixel gaps, it doesn't get filled
		// I'm really glad this was the issue, really
		filled[origin.x][origin.y] = true; 
		
		// Loop while there is items in the queue
		while(!q.isEmpty())
		{
			Point p = q.pop();
			
			// Do some constraints checking - if any of the parameters +/- 1 are out of bounds then this is not a valid fill
			// Now here we could either keep going, filling in the doNotFill matrix, to give us an advantage later, or we could just fill in doNotFill with what we know and return
			// Also, if any of the surrounding options are marked as a doNotFill square, then this is not a valid fill, so we just exit in this case
			if(p.x + 1 >= gw || p.x - 1 < 0 || p.y + 1 >= gh || p.y - 1 < 0 || doNotFill[p.x + 1][p.y] || doNotFill[p.x - 1][p.y] || doNotFill[p.x][p.y + 1] || doNotFill[p.x][p.y - 1])
			{
				// Let's take the second option for now
				
				// np - New Point
				for(Point np : newFilled)
				{
					doNotFill[np.x][np.y] = true;
				}
				
				return false;
			}
			
			if(!filled[p.x + 1][p.y])
			{
				q.push(p.x + 1, p.y);
				filled[p.x + 1][p.y] = true;
				newFilled.add(new Point(p.x + 1, p.y));
			}
			if(!filled[p.x - 1][p.y])
			{
				q.push(p.x - 1, p.y);
				filled[p.x - 1][p.y] = true;
				newFilled.add(new Point(p.x - 1, p.y));
			}
			if(!filled[p.x][p.y + 1])
			{
				q.push(p.x, p.y + 1);
				filled[p.x][p.y + 1] = true;
				newFilled.add(new Point(p.x, p.y + 1));
			}
			if(!filled[p.x][p.y - 1])
			{
				q.push(p.x, p.y - 1);
				filled[p.x][p.y - 1] = true;
				newFilled.add(new Point(p.x, p.y - 1));
			}
		}
		
		// If we've got to the end, then that means this was a valid fill, so we can actually copy the matrix back to the source and return true
		Helper.copyMatrix(filled, selected);
		
		return true;
	}
}