package jpixeleditor.tools;

public class Dither extends Tool
{
	public Dither(int id)
	{
		super(id);
		
		name = "Dither";
		description = "Draws the primary colour interdispersed with the secondary colour\nRight click to switch";
		keyShortcut = "D";
		iconPath = "/Dither.png";
	}
}
