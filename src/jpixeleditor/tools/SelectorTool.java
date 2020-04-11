package jpixeleditor.tools;

public abstract class SelectorTool extends Tool
{
	public SelectorTool(int id)
	{
		super(id);
	}

	@Override public boolean isSelector()
	{
		return true;
	}
	
	public abstract boolean triggersOnClick();
}
