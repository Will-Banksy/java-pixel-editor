package jpixeleditor.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

import com.jhlabs.image.GaussianFilter;

import jpixeleditor.Main;
import jpixeleditor.ui.ColourSelectorPopupPanel.ColourDescriptor;
import jpixeleditor.utils.Colour;
import jpixeleditor.utils.EditorTools;
import jpixeleditor.utils.Helper;

@SuppressWarnings("serial")
public class ColourSelectorPopupPanel extends Panel
{
	class ColourDescriptor
	{
		int alpha;
		int hue;
		int saturation;
		int brightness;
		
		public ColourDescriptor()
		{
			this(0, 0, 0, 0);
		}
		
		public ColourDescriptor(int argb)
		{
			alpha = Colour.getAlpha(argb);
			hue = Colour.getHue(argb);
			saturation = Colour.getHSBSaturation(argb);
			brightness = Colour.getBrightness(argb);
		}
		
		public ColourDescriptor(int alpha, int hue, int saturation, int brightness)
		{
			this.alpha = alpha;
			this.hue = hue;
			this.saturation = saturation;
			this.brightness = brightness;
		}
		
		public int getColour()
		{
			int argb = Colour.toIntAHSB(alpha, hue, saturation, brightness);
			
			return argb;
		}
		
		public void setColour(int argb)
		{
			alpha = Colour.getAlpha(argb);
			hue = Colour.getHue(argb);
			saturation = Colour.getHSBSaturation(argb);
			brightness = Colour.getBrightness(argb);
		}
	}
	
	public HueSelector hSel;
	public ColourPreview colPrev;
	public SatAndBrightSelector sabSel;
	public AlphaSelector aSel;
	public HexColourEditor hexEd;
	
	public ColourDescriptor selectedColour;
	
	public ColourSelectorPopupPanel(int col, boolean primary, Action openDialogAction)
	{
		super(Main.Theme.THEME_BACK_LIGHT);
		
		selectedColour = new ColourDescriptor(col);
		
		//setPreferredSize(new Dimension(300, 300));
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		hSel = new HueSelector(selectedColour, primary, this);
		
		c.insets = new Insets(6, 6, 6, 6);
		
		sabSel = new SatAndBrightSelector(selectedColour, primary, this);
		
		add(sabSel, c);
		
		c.insets = new Insets(10, 10, 10, 10);
		c.gridx = 1;
		add(hSel, c);
		
		colPrev = new ColourPreview(selectedColour);
		
		c.gridy = 1;
		add(colPrev, c);
		
		aSel = new AlphaSelector(selectedColour, primary, this);
		
		c.gridx = 0;
		add(aSel, c);
		
		Panel hexEdPanel = new Panel(Main.Theme.THEME_BACK_LIGHT) {
			@Override public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				
				Graphics2D g2d = (Graphics2D)g;
				
				g2d.setColor(Colour.toAWTColor(Main.Theme.THEME_BACK_DARK));
				g2d.setStroke(new BasicStroke(2));
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);
			}
		};
		hexEdPanel.setLayout(new BorderLayout());
		hexEdPanel.setBorder(new EmptyBorder(new Insets(0, 5, 0, 0)));
		
		hexEd = new HexColourEditor(selectedColour, primary, this);
		
		hexEdPanel.add(new JLabel("<html><font color='ffffff'>#</font>"), BorderLayout.LINE_START);
		hexEdPanel.add(hexEd, BorderLayout.CENTER);
		
		c.insets = new Insets(0, 10, 10, 10);
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(hexEdPanel, c);
		
		OpenDialogButton button = new OpenDialogButton(openDialogAction);
		
		c.insets = new Insets(2, 12, 12, 12);
		c.gridx = 1;
		add(button, c);
	}
}

@SuppressWarnings("serial")
class OpenDialogButton extends JButton
{
	ImageIcon icon;
	
	int colour;
	
	public OpenDialogButton(Action action)
	{
		super();
		
		icon = new ImageIcon(Main.class.getResource("/OpenExternal.png"));
		
		addActionListener(action);
		
		colour = Main.Theme.THEME_BUTTON;
		addMouseListener(new MouseAdapter() {
			@Override public void mouseEntered(MouseEvent me)
			{
				colour = Main.Theme.THEME_BUTTON_HOVER;
			}
			
			@Override public void mouseExited(MouseEvent me)
			{
				colour = Main.Theme.THEME_BUTTON;
			}
		});
		
		setPreferredSize(new Dimension(27, 27));
		
		setBackground(Colour.toAWTColor(Main.Theme.THEME_BACK_LIGHT));
	}
	
	@Override public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setStroke(new BasicStroke(2));
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setColor(Colour.toAWTColor(colour));
		g2d.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);
		
		// g2d.setColor(Colour.toAWTColor(Main.Theme.THEME_BACK_DARK));
		// g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);
		
		icon.paintIcon(this, g, 6, 6);
	}
}

@SuppressWarnings("serial")
class HexColourEditor extends JTextField
{
	public class DocumentSizeFilter extends DocumentFilter
	{
	    int maxCharacters;
	    boolean DEBUG = false;
	 
	    public DocumentSizeFilter(int maxChars)
	    {
	        maxCharacters = maxChars;
	    }
	 
	    public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException
	    {
	        if (DEBUG)
	        {
	            System.out.println("in DocumentSizeFilter's insertString method");
	        }
	 
	        //This rejects the entire insertion if it would make
	        //the contents too long. Another option would be
	        //to truncate the inserted string so the contents
	        //would be exactly maxCharacters in length.
	        if ((fb.getDocument().getLength() + str.length()) <= maxCharacters)
	            super.insertString(fb, offs, str, a);
	        else
	            Toolkit.getDefaultToolkit().beep();
	    }
	     
	    public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException
	    {
	        if (DEBUG)
	        {
	            System.out.println("in DocumentSizeFilter's replace method");
	        }
	        
	        //This rejects the entire replacement if it would make
	        //the contents too long. Another option would be
	        //to truncate the replacement string so the contents
	        //would be exactly maxCharacters in length.
	        if ((fb.getDocument().getLength() + str.length() - length) <= maxCharacters)
	            super.replace(fb, offs, length, str, a);
	        else
	            Toolkit.getDefaultToolkit().beep();
	    }
	}
	
	ColourDescriptor col;
	boolean primary;
	
	ColourSelectorPopupPanel base;
	
	public HexColourEditor(ColourDescriptor col, boolean primary, ColourSelectorPopupPanel base)
	{
		super(Helper.toHexString(col.getColour(), 8));
		
		this.col = col;
		this.primary = primary;
		this.base = base;
		
		setBackground(Colour.toAWTColor(Main.Theme.THEME_BACK_LIGHT));
		setForeground(Color.WHITE);
		setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
		
		Document styledDoc = getDocument();
		if (styledDoc instanceof AbstractDocument) {
		    AbstractDocument doc = (AbstractDocument)styledDoc;
		    doc.setDocumentFilter(new DocumentSizeFilter(8));
		}
		
		addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e)
			{
				// This might help with performance, but only in extreme cases. Still, may as well have it in
				if(Helper.hexToInt(getText(), 8) != col.getColour())
				{
					setColour(); // Was having problems with this being called lots of times - turns out I did addActionListener in paintComponent, so basically a load of actionListeners were added so they caught the event and executed their actionPerformed method once each
				}
			}
		});
	}
	
	@Override public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setColor(Colour.toAWTColor(Main.Theme.THEME_BUTTON_HOVER));
		g2d.drawString("#aarrggbb", getWidth() - 75, getHeight() * 0.5f + 4);
	}
	
	public void updateText()
	{
		setText(Helper.toHexString(col.getColour(), 8));
	}
	
	public void setColour()
	{
		col.setColour(Helper.hexToInt(getText(), 8));
		
		base.aSel.updateImage();
		base.aSel.updateSlider();
		base.aSel.repaint();
		
		base.colPrev.repaint();
		
		base.hSel.updateSlider();
		base.hSel.repaint();
		
		base.sabSel.updateImage();
		base.sabSel.updatePoint();
		base.sabSel.repaint();
		
		if(primary)
		{
			EditorTools.primaryColour = col.getColour();
			ColourSelectorPanel.csb1.repaint();
		}
		else
		{
			EditorTools.secondaryColour = col.getColour();
			ColourSelectorPanel.csb2.repaint();
		}
	}
}

@SuppressWarnings("serial")
class SatAndBrightSelector extends Panel
{
	ColourDescriptor col;
	
	boolean primary;
	
	Rectangle selectorArea;
	
	BufferedImage selectorImage = null;
	
	Point selectedPoint;
	
	boolean canDrag = false;
	
	ColourSelectorPopupPanel base;
	
	public SatAndBrightSelector(ColourDescriptor col, boolean primary, ColourSelectorPopupPanel base)
	{
		super(Main.Theme.THEME_BACK_LIGHT);
		
		this.col = col;
		this.primary = primary;
		this.base = base;
		
		setPreferredSize(new Dimension(214, 214));
		
		selectorArea = new Rectangle(7, 7, 200, 200);
		selectedPoint = new Point();
		selectedPoint.x = Math.round(Helper.map(col.saturation, 0, 100, selectorArea.x, selectorArea.x + selectorArea.width));
		selectedPoint.y = Math.round(Helper.map(col.brightness, 0, 100, selectorArea.y + selectorArea.height, selectorArea.y));
		
		updateImage();
		
		addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent me)
			{
				if(selectorArea.contains(me.getPoint()))
				{
					canDrag = true;
					selectedPoint = me.getPoint();
					
					updateColour();
					
					repaint();
				}
			}
			
			@Override public void mouseReleased(MouseEvent me)
			{
				canDrag = false;
			}
		});
		
		addMouseMotionListener(new MouseAdapter() {
			@Override public void mouseDragged(MouseEvent me)
			{
				if(canDrag)
				{
					Point p = Helper.constrainTo(me.getPoint(), selectorArea);
					selectedPoint.x = p.x;
					selectedPoint.y = p.y;
					
					updateColour();
					
					repaint();
				}
			}
		});
	}
	
	public void updatePoint()
	{
		selectedPoint.x = Math.round(Helper.map(col.saturation, 0, 100, selectorArea.x, selectorArea.x + selectorArea.width));
		selectedPoint.y = Math.round(Helper.map(col.brightness, 0, 100, selectorArea.y + selectorArea.height, selectorArea.y));
	}
	
	public void updateImage()
	{
		if(selectorImage == null)
		{
			selectorImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
		}
		
//		// Top left-hand corner - white
//		int col1 = Colour.WHITE;
//		
//		// Top right-hand corner - Full brightness and saturation colour defined by the hue
//		int col2 = Colour.toIntAHSB(255, col.hue, 100, 100);
//		
//		// Bottom left and right hand corners - black
//		int col3 = Colour.BLACK;
		
		Graphics2D g2d = (Graphics2D)selectorImage.getGraphics();
		
		for(int i = 0; i < 100; i++) // Loop across x / saturation
		{
			for(int j = 0; j < 100; j++) // Loop across y / brightess
			{
				// This method might be slower, as it requires calculating the HSB colour for every i and j
				int brightness = j;
				int saturation = i;
				
				int thisPixel = Colour.toIntAHSB(255, col.hue, saturation, brightness);
				
				// Since the image size is 2 times the maximum values for brightness and saturation, just multiply i and j by 2 to get the position
				int posX = i * 2;
				int posY = (99 - j) * 2; // Flip it vertically to get the standard graph of saturation and brightness
				
				g2d.setColor(Colour.toAWTColor(thisPixel));
				g2d.fillRect(posX, posY, 2, 2);
			}
		}
		
		// Release resources used by this Graphics2D object. Basically destroys it
		g2d.dispose();
	}
	
	@Override public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setBackground(Colour.toAWTColor(Main.Theme.THEME_BACK_LIGHT));
		g2d.clearRect(0, 0, getWidth(), getHeight());
		
		g2d.setStroke(new BasicStroke(2));
		g2d.setColor(Colour.toAWTColor(Main.Theme.THEME_BACK_DARK));
		g2d.fillRoundRect(selectorArea.x - 2, selectorArea.y - 2, selectorArea.width + 4, selectorArea.height + 4, 6, 6);
		
//		g2d.setColor(Colour.toAWTColor(Colour.BLACK));
//		g2d.fill(selectorArea);
		
		g2d.drawImage(selectorImage, selectorArea.x, selectorArea.y, null);
		
		// Want a nice, smooth looking circle, so put antialiasing on
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Create a shadow behind the little circle that shows your selection point - So that it is clear on both dark and light backgrounds
		// Create a filter with radius 3
		GaussianFilter filter = new GaussianFilter(3);
		
		// Create a bufferedImage and draw a black ellipse to it
		BufferedImage img = new BufferedImage(13, 13, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2dImg = img.createGraphics();
		g2dImg.setColor(Color.BLACK);
		g2dImg.setStroke(new BasicStroke(2));
		g2dImg.drawOval(2, 2, 9, 9);
		g2dImg.dispose();
		
		// Apply the filter
		img = filter.filter(img, null);
		
		// Draw the shadow first
		g2d.drawImage(img, selectedPoint.x - 6, selectedPoint.y - 6, null);
		
		g2d.setColor(Color.WHITE);
		g2d.drawOval(selectedPoint.x - 5, selectedPoint.y - 5, 9, 9);
	}
	
	public void updateColour()
	{
		col.saturation = Math.round(Helper.map(selectedPoint.x, selectorArea.x, selectorArea.x + selectorArea.width, 0, 100));
		col.brightness = Math.round(Helper.map(selectedPoint.y, selectorArea.y + selectorArea.height, selectorArea.y, 0, 100));
		
		base.aSel.updateImage();
		base.aSel.repaint();
		
		base.colPrev.repaint();
		
		base.hexEd.updateText();
		base.hexEd.repaint();
		
		if(primary)
		{
			EditorTools.primaryColour = col.getColour();
			ColourSelectorPanel.csb1.repaint();
		}
		else
		{
			EditorTools.secondaryColour = col.getColour();
			ColourSelectorPanel.csb2.repaint();
		}
	}
}

@SuppressWarnings("serial")
class ColourPreview extends Panel
{
	ColourDescriptor previewColour;
	
	public TexturePaint tp;
	
	public ColourPreview(ColourDescriptor col)
	{
		super(Main.Theme.THEME_BACK_LIGHT);
		
		previewColour = col;
		
		setPreferredSize(new Dimension(31, 31));
		
		try
		{
			if(Canvas.bgImage == null)
			{
				Canvas.bgImage = ImageIO.read(Main.class.getResource("/Transparency-Dark200by200.png"));
			}
			tp = new TexturePaint(Canvas.bgImage, new Rectangle(0, 0, 200, 200));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setBackground(Colour.toAWTColor(Main.Theme.THEME_BACK_LIGHT));
		g2d.clearRect(0, 0, getWidth(), getHeight());
		
		g2d.setPaint(tp);
		g2d.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);
		
		g2d.setColor(Colour.toAWTColor(previewColour.getColour()));
		g2d.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);
		
		g2d.setColor(Colour.toAWTColor(Main.Theme.THEME_BACK_DARK));
		g2d.setStroke(new BasicStroke(2));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);
	}
}

@SuppressWarnings("serial")
class HueSelector extends Panel
{
	private static BufferedImage hueRangeImage = null;
	
	public ColourDescriptor col;
	
	private Insets drawingInsets;
	
	Rectangle hueDragBox;
	
	boolean primary;
	
	ColourSelectorPopupPanel base;
	
	public HueSelector(ColourDescriptor col, boolean primary, ColourSelectorPopupPanel base)
	{
		super(Main.Theme.THEME_BACK_LIGHT);
		
		this.col = col;
		this.primary = primary;
		this.base = base;
		
		setPreferredSize(new Dimension(31, 206));
		drawingInsets = new Insets(2, 2, 2, 2);
		
		int hYPos = (int)Helper.map(col.hue, 0, 360, 0, 200) + 1;
		hueDragBox = new Rectangle(0, hYPos, getWidth(), 4);
		
		if(hueRangeImage == null)
		{
			try
			{
				hueRangeImage = ImageIO.read(Main.class.getResource("/HueRange.png"));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent me)
			{
				hueDragBox.y = me.getY() - hueDragBox.height / 2;
				
				if(hueDragBox.y - 1 < 0)
				{
					hueDragBox.y = 1;
				}
				else if(hueDragBox.y + hueDragBox.height + 1 > getHeight())
				{
					hueDragBox.y = getHeight() - hueDragBox.height - 1;
				}
				
				updateHue((int)Helper.map(hueDragBox.y, 1, getHeight() + 1 - drawingInsets.bottom - hueDragBox.height, 0, 360));
				
				repaint();
			}
		});
		
		addMouseMotionListener(new MouseAdapter() {
			@Override public void mouseDragged(MouseEvent me)
			{
				hueDragBox.y = me.getY() - hueDragBox.height / 2;
				
				if(hueDragBox.y - 1 < 0)
				{
					hueDragBox.y = 1;
				}
				else if(hueDragBox.y + hueDragBox.height + 1 > getHeight())
				{
					hueDragBox.y = getHeight() - hueDragBox.height - 1;
				}
				
				updateHue((int)Helper.map(hueDragBox.y, 1, getHeight() + 1 - drawingInsets.bottom - hueDragBox.height, 0, 360));
				
				repaint();
			}
		});
	}
	
	@Override public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		
		int drawX = 1 + drawingInsets.left;
		int drawY = 1 + drawingInsets.top;
		int drawW = getWidth() - 2 - drawingInsets.right * 2;
		int drawH = getHeight() - 2 - drawingInsets.bottom * 2;
		
		g2d.setBackground(Colour.toAWTColor(Main.Theme.THEME_BACK_LIGHT));
		g2d.clearRect(0, 0, getWidth(), getHeight());
		
		g2d.drawImage(hueRangeImage, drawX, drawY, null);
		
		g2d.setStroke(new BasicStroke(2));
		
		g2d.setColor(Colour.toAWTColor(Main.Theme.THEME_BACK_LIGHT));
		g2d.drawRect(drawX, drawY, drawW, drawH);

		g2d.setColor(Colour.toAWTColor(Main.Theme.THEME_BACK_DARK));
		g2d.drawRoundRect(drawX, drawY, drawW, drawH, 6, 6);
		
		int hYPos = hueDragBox.y;//(int)Helper.map(hue, 0, 360, 0, 200) + 1;
		
		g2d.setColor(Color.WHITE);//Colour.toAWTColor(Main.Theme.THEME_ACCENT));
		g2d.drawRoundRect(drawX - drawingInsets.left, hYPos, drawW + drawingInsets.right * 2, 4, 2, 2);
	}
	
	public void updateHue(int h)
	{
		col.hue = h;
		
		base.colPrev.repaint();
		base.sabSel.updateImage();
		base.sabSel.repaint();
		
		base.aSel.updateImage();
		base.aSel.repaint();
		
		base.hexEd.updateText();
		base.hexEd.repaint();
		
		if(primary)
		{
			EditorTools.primaryColour = col.getColour();
			ColourSelectorPanel.csb1.repaint();
		}
		else
		{
			EditorTools.secondaryColour = col.getColour();
			ColourSelectorPanel.csb2.repaint();
		}
	}
	
	public void updateSlider()
	{
		int hYPos = (int)Helper.map(col.hue, 0, 360, 0, 200) + 1;
		hueDragBox = new Rectangle(0, hYPos, getWidth(), 4);
	}
}

@SuppressWarnings("serial")
class AlphaSelector extends Panel
{
	private BufferedImage alphaRangeImage = null;
	
	public ColourDescriptor col;
	
	private Insets drawingInsets;
	
	Rectangle alphaDragBox;
	
	boolean primary;
	
	TexturePaint tp;
	
	ColourSelectorPopupPanel base;
	
	public AlphaSelector(ColourDescriptor col, boolean primary, ColourSelectorPopupPanel base)
	{
		super(Main.Theme.THEME_BACK_LIGHT);
		
		this.col = col;
		this.primary = primary;
		this.base = base;
		
		setPreferredSize(new Dimension(206, 31));
		drawingInsets = new Insets(2, 2, 2, 2);
		
		int aXPos = (int)Helper.map(col.alpha, 0, 255, 0, 200) + 1;
		alphaDragBox = new Rectangle(aXPos, 0, 4, getHeight());
		
		try
		{
			if(Canvas.bgImage == null)
			{
				Canvas.bgImage = ImageIO.read(Main.class.getResource("/Transparency-Dark200by200.png"));
			}
			tp = new TexturePaint(Canvas.bgImage, new Rectangle(0, 0, 200, 200));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		updateImage();
		
		addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent me)
			{
				alphaDragBox.x = me.getX() - alphaDragBox.width / 2;
				
				if(alphaDragBox.x - 1 < 0)
				{
					alphaDragBox.x = 1;
				}
				else if(alphaDragBox.x + alphaDragBox.width + 1 > getWidth())
				{
					alphaDragBox.x = getWidth() - alphaDragBox.width - 1;
				}
				
				updateAlpha((int)Helper.map(alphaDragBox.x, 1, getWidth() + 1 - drawingInsets.right - alphaDragBox.width, 0, 255));
				
				repaint();
			}
		});
		
		addMouseMotionListener(new MouseAdapter() {
			@Override public void mouseDragged(MouseEvent me)
			{
				alphaDragBox.x = me.getX() - alphaDragBox.width / 2;
				
				if(alphaDragBox.x - 1 < 0)
				{
					alphaDragBox.x = 1;
				}
				else if(alphaDragBox.x + alphaDragBox.width + 1 > getWidth())
				{
					alphaDragBox.x = getWidth() - alphaDragBox.width - 1;
				}
				
				updateAlpha((int)Helper.map(alphaDragBox.x, 1, getWidth() + 1 - drawingInsets.right - alphaDragBox.width, 0, 255));
				
				repaint();
			}
		});
	}
	
	public void updateImage()
	{
		alphaRangeImage = new BufferedImage(200, 25, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g2d = (Graphics2D)alphaRangeImage.getGraphics();
		
		g2d.setPaint(tp);
		g2d.fillRect(0, 0, alphaRangeImage.getWidth(), alphaRangeImage.getHeight());
		
		for(int i = 0; i < alphaRangeImage.getWidth(); i++)
		{
			int alpha = Math.round(Helper.map(i, 0, alphaRangeImage.getWidth(), 0, 255));
			int lineCol = Colour.toIntAHSB(alpha, col.hue, col.saturation, col.brightness);
			
			g2d.setColor(Colour.toAWTColor(lineCol));
			g2d.drawLine(i, 0, i, alphaRangeImage.getHeight());
		}
	}

	@Override public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		
		int drawX = 1 + drawingInsets.left;
		int drawY = 1 + drawingInsets.top;
		int drawW = getWidth() - 2 - drawingInsets.right * 2;
		int drawH = getHeight() - 2 - drawingInsets.bottom * 2;
		
		g2d.setBackground(Colour.toAWTColor(Main.Theme.THEME_BACK_LIGHT));
		g2d.clearRect(0, 0, getWidth(), getHeight());
		
		g2d.drawImage(alphaRangeImage, drawX, drawY, null);
		
		g2d.setStroke(new BasicStroke(2));
		
		g2d.setColor(Colour.toAWTColor(Main.Theme.THEME_BACK_LIGHT));
		g2d.drawRect(drawX, drawY, drawW, drawH);
		
		g2d.setColor(Colour.toAWTColor(Main.Theme.THEME_BACK_DARK));
		g2d.drawRoundRect(drawX, drawY, drawW, drawH, 6, 6);
		
		int aXPos = alphaDragBox.x;//(int)Helper.map(hue, 0, 360, 0, 200) + 1;
		
		g2d.setColor(Color.WHITE);//Colour.toAWTColor(Main.Theme.THEME_ACCENT));
		g2d.drawRoundRect(aXPos, drawY - drawingInsets.top, 4, drawH + drawingInsets.bottom * 2, 2, 2);
	}
	
	public void updateAlpha(int a)
	{
		col.alpha = a;
		
		base.colPrev.repaint();
		
		base.hexEd.updateText();
		base.hexEd.repaint();
		
		if(primary)
		{
			EditorTools.primaryColour = col.getColour();
			ColourSelectorPanel.csb1.repaint();
		}
		else
		{
			EditorTools.secondaryColour = col.getColour();
			ColourSelectorPanel.csb2.repaint();
		}
	}
	
	public void updateSlider()
	{
		int aXPos = (int)Helper.map(col.alpha, 0, 255, 0, 200) + 1;
		alphaDragBox = new Rectangle(aXPos, 0, 4, getHeight());
	}
}