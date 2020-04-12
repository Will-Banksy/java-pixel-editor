package jpixeleditor.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import jpixeleditor.Main;
import jpixeleditor.ui.CanvasContainer;
import jpixeleditor.ui.Canvas.DrawingSurface;
import jpixeleditor.utils.PaintHandler.DrawTo;

public class IOTools
{
	public static void ExportPNG(File destination)
	{
		DrawingSurface surface = CanvasContainer.canvas.surface;
		
		BufferedImage bfi = new BufferedImage(surface.gridWidth, surface.gridHeight, BufferedImage.TYPE_INT_ARGB);
		int[] imgData = ((DataBufferInt)bfi.getRaster().getDataBuffer()).getData();
		
		for(int i = 0; i < surface.gridColours.length; i++)
		{
			for(int j = 0; j < surface.gridColours[i].length; j++)
			{
				int index = i + j * surface.gridWidth;
				
				imgData[index] = surface.gridColours[i][j];
			}
		}
		
		try
		{
			ImageIO.write(bfi, "png", destination);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			
			JOptionPane.showMessageDialog(Main.windowFrame, "Error saving file");
		}
	}
	
	public static void ImportImage(BufferedImage image)
	{
		DrawingSurface surface = CanvasContainer.canvas.surface;
		
		surface.setGridDims(image.getWidth(), image.getHeight());
		
		// Loop through dimensions of image, grabbing the colour of each pixel and putting it in the Canvas's DrawingSurface pixel array
		for(int i = 0; i < image.getWidth(); i++)
		{
			for(int j = 0; j < image.getHeight(); j++)
			{
				PaintHandler.putPixel(i, j, image.getRGB(i, j), DrawTo.CANVAS);
			}
		}
		
		CanvasContainer.canvas.prevRect = null; // Set this to null so when I call Canvas.setBounds it will reset the zoom
		
		CanvasContainer.canvas.setBounds(0, 0, 0, 0); // The input values do not matter - The overridden method completely disregards them
		CanvasContainer.canvas.repaint();
	}
}