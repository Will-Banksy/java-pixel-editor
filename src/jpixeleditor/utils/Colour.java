package jpixeleditor.utils;

import java.awt.Color;
import java.util.Random;

public class Colour
{
	public static final int BLACK = toIntARGB(255, 0, 0, 0);
	public static final int WHITE = toIntARGB(255, 255, 255, 255);
	public static final int TRANSPARENT = toIntARGB(0, 0, 0, 0);
	public static final  int RED = toIntARGB(255, 255, 0, 0);
	public static final int GREEN = toIntARGB(255, 0, 255, 0);
	public static final int BLUE = toIntARGB(255, 0, 0, 255);
	public static final int CYAN = toIntARGB(255, 0, 255, 255);
	public static final int MAGENTA = toIntARGB(255, 255, 0, 255);
	public static final int YELLOW = toIntARGB(255, 255, 255, 0);
	
	/**
	 * Encodes ARGB values 0 - 255 into an integer
	 * @param alpha
	 * @param red
	 * @param green
	 * @param blue
	 * @return The inputs encoded into an integer value
	 */
	public static int toIntARGB(int alpha, int red, int green, int blue)
	{
		int argb = (alpha << 24) + (red << 16) + (green << 8) + blue;
		return argb;
	}
	
	/**
	 * Encodes ARGB values 0 - 255 into an integer
	 * @param alpha
	 * @param red
	 * @param green
	 * @param blue
	 * @return The inputs encoded into an integer value
	 */
	public static int toIntARGB(byte alpha, byte red, byte green, byte blue)
	{
		int argb = (alpha << 24) + (red << 16) + (green << 8) + blue;
		return argb;
	}
	
	/**
	 * Converts AHSB values (A: 0 - 255, H: 0 - 360, S: 0 - 100, B: 0 - 100) into ARGB then encodes them into an integer
	 * @param alpha
	 * @param hue
	 * @param saturation
	 * @param brightness
	 * @return The inputs converted into argb then encoded into an integer value
	 */
	public static int toIntAHSB(int alpha, int hue, int saturation, int brightness)
	{
		float H = (float)hue;
		float S = (float)saturation / 100;
		float Br = (float)brightness / 100;
		
		float k = ((5f) + H / 60f) % 6f;
		float R = Br - Br * S * Helper.getMax(0, Helper.getMin(k, 4 - k, 1));
		
		k = ((3f) + H / 60f) % 6f;
		float G = Br - Br * S * Helper.getMax(0, Helper.getMin(k, 4 - k, 1));
		
		k = ((1f) + H / 60f) % 6f;
		float B = Br - Br * S * Helper.getMax(0, Helper.getMin(k, 4 - k, 1));
		
		int red = (int)Helper.constrain(Math.round(R * 255), 0, 255);
		int green = (int)Helper.constrain(Math.round(G * 255), 0, 255);
		int blue = (int)Helper.constrain(Math.round(B * 255), 0, 255);
		
		return toIntARGB(alpha, red, green, blue);
	}
	
	/**
	 * Converts AHSL values (A: 0 -255, H: 0 - 360, S: 0 - 100, L: 0 - 100) into ARGB then encodes them into an integer
	 * @param alpha
	 * @param hue
	 * @param saturation
	 * @param lightness
	 * @return The inputs converted into argb then encoded into an integer value
	 */
	public static int toIntAHSL(int alpha, int hue, int saturation, int lightness)
    {
        float H = (float)hue;
        float S = (float)saturation / 100;
        float L = (float)lightness / 100;
        
        // Alternative method. Equally viable, just I prefer the shorter method
        /*float C = (1 - Math.abs(2 * L - 1)) * S;
        
        float H_ = H / 60;
        
        float X = C * (1 - Math.abs(H_ % 2 - 1));
        
        float R1 = 0, G1 = 0, B1 = 0;
        if(0 <= H_ && H_ <= 1)
        {
            R1 = C;
            G1 = X;
            B1 = 0;
        }
        if(1 <= H_ && H_ <= 2)
        {
            R1 = X;
            G1 = C;
            B1 = 0;
        }
        if(2 <= H_ && H_ <= 3)
        {
            R1 = 0;
            G1 = C;
            B1 = X;
        }
        if(3 <= H_ && H_ <= 4)
        {
            R1 = 0;
            G1 = X;
            B1 = C;
        }
        if(4 <= H_ && H_ <= 5)
        {
            R1 = X;
            G1 = 0;
            B1 = C;
        }
        if(5 <= H_ && H_ <= 6)
        {
            R1 = C;
            G1 = 0;
            B1 = X;
        }
        
        float m = L - C * 0.5f;
        int R = (int)constrain(Math.round((R1 + m) * 255), 0, 255);
        int G = (int)constrain(Math.round((G1 + m) * 255), 0, 255);
        int B = (int)constrain(Math.round((B1 + m) * 255), 0, 255);*/
        
        float a = S * Math.min(L, 1 - L);
        
        float k = (0 + H / 30) % 12;
        float R1 = L - a * Math.max(Helper.getMin(k - 3, 9 - k, 1), -1);
        
        k = (8 + H / 30) % 12;
        float G1 = L - a * Math.max(Helper.getMin(k - 3, 9 - k, 1), -1);
        
        k = (4 + H / 30) % 12;
        float B1 = L - a * Math.max(Helper.getMin(k - 3, 9 - k, 1), -1);
        
        int R = (int)Helper.constrain(Math.round(R1 * 255), 0, 255);
        int G = (int)Helper.constrain(Math.round(G1 * 255), 0, 255);
        int B = (int)Helper.constrain(Math.round(B1 * 255), 0, 255);
        
        return toIntARGB(alpha, R, G, B);
    }
	
	/**
	 * Converts ACMYK values (0 - 255) into ARGB then encodes them into an integer
	 * @param alpha
	 * @param cyan
	 * @param magenta
	 * @param yellow
	 * @param key
	 * @return The inputs converted into argb then encoded into an integer value
	 */
	public static int toIntACMYK(int alpha, int cyan, int magenta, int yellow, int key)
	{
		float c = (float)cyan;
		float m = (float)magenta;
		float y = (float)yellow;
		float k = (float)key;
		
		float r1 = 255 * (1 - c / 255) * (1 - k / 255);
		float g1 = 255 * (1 - m / 255) * (1 - k / 255);
		float b1 = 255 * (1 - y / 255) * (1 - k / 255);
		
		int r = (int)Helper.constrain(Math.round(r1), 0, 255);
		int g = (int)Helper.constrain(Math.round(g1), 0, 255);
		int b = (int)Helper.constrain(Math.round(b1), 0, 255);
		
		return toIntARGB(alpha, r, g, b);
	}
	
	/**
	 * Converts the input RGB values (0 - 255) into HSB and returns the values in an array
	 * @param red
	 * @param green
	 * @param blue
	 * @return The RBG values converted into HSB, in order: H (0 - 360), S (0 - 100), B (0 - 100)
	 */
	public static float[] RGBtoHSB(int red, int green, int blue)
	{
		float R = (float)red / 255;
		float G = (float)green / 255;
		float B = (float)blue / 255;
		
		float max = Helper.getMax(R, G, B);
		float min = Helper.getMin(R, G, B);
		float delta = max - min;
		
		float H = 0;
		if(delta == 0)
		{
			H = 0;
		}
		else if(max == R)
		{
			H = 60 * (((G - B) / delta) + 0);
		}
		else if(max == G)
		{
			H = 60 * (((B - R) / delta) + 2);
		}
		else if(max == B)
		{
			H = 60 * (((R - G) / delta) + 4);
		}
		
		float S = 0;
		if(max == 0)
		{
			S = 0;
		}
		else
		{
			S = delta / max;
		}
		
		float Br = max;
		
		// Got to do this because otherwise we get negative hues that are just set to 0. This ensures hues that are out of bounds are looped back around
		if(H < 0)
		{
			H += 360;
		}
		else if(H > 360)
		{
			H -= 360;
		}
		
		H = Helper.constrain(H, 0, 360);
        S = Helper.constrain(S * 100, 0, 100);
        Br = Helper.constrain(Br * 100, 0, 100);
		
		return new float[] { H, S, Br };
	}
	
	/**
	 * Converts the input RGB values (0 - 255) into HSL and returns the values in an array
	 * @param red
	 * @param green
	 * @param blue
	 * @return The RBG values converted into HSL, in order: H (0 - 360), S (0 - 100), L (0 - 100)
	 */
	public static float[] RGBtoHSL(int red, int green, int blue)
    {
        float R = (float)red / 255;
        float G = (float)green / 255;
        float B = (float)blue / 255;
        
        float M = Helper.getMax(R, G, B);
        float m = Helper.getMin(R, G, B);
        float C = M - m;
        
        float H_ = 0;
        if(C == 0)
        {
            H_ = 0;
        }
        if(M == R)
        {
            H_ = ((G - B) / C);
        }
        if(M == G)
        {
            H_ = ((B - R) / C) + 2;
        }
        if(M == B)
        {
            H_ = ((R - G) / C) + 4;
        }
        float H = H_ * 60;
        if(H < 0)
        {
            H = H + 360;
        }
        
        float L = 0.5f * (M + m);
        
        float S = (L == 0 || L == 1) ? 0 : (C / (1 - Math.abs(C - 1)));
        
        H = Helper.constrain(H, 0, 360);
        S = Helper.constrain(S * 100, 0, 100);
        L = Helper.constrain(L * 100, 0, 100);
        
        return new float[] { H, S, L };
    }
	
	/**
	 * Converts the input RGB values (0 - 255) into CMYK (0 - 255) and returns the values in an array
	 * @param red
	 * @param green
	 * @param blue
	 * @return The RBG values converted into CMYK, in order: C, M, Y, K
	 */
	public static int[] RGBtoCMYK(int red, int green, int blue)
	{
		float r = (float)red;
		float g = (float)green;
		float b = (float)blue;
		
		float k1 = 255 - Helper.getMax(r, g, b);
		
		if(k1 == 255)
		{
			return new int[] { 0, 0, 0, 255 };
		}
		
		float c1 = ((255 - r - k1) / (255 - k1) * 255);
		float m1 = ((255 - g - k1) / (255 - k1) * 255);
		float y1 = ((255 - b - k1) / (255 - k1) * 255);
		
		int c = (int)Helper.constrain(Math.round(c1), 0, 255);
		int m = (int)Helper.constrain(Math.round(m1), 0, 255);
		int y = (int)Helper.constrain(Math.round(y1), 0, 255);
		int k = (int)Helper.constrain(Math.round(k1), 0, 255);
		
		return new int[] { c, m, y, k };
	}
	
	public static Color toAWTColor(int col)
	{
		return new Color(getRed(col), getGreen(col), getBlue(col), getAlpha(col));
	}
	
	public static int getAlpha(int argb)
	{
		return (argb >> 24) & 255;
	}
	
	public static int getRed(int argb)
	{
		return (argb >> 16) & 255;
	}
	
	public static int getGreen(int argb)
	{
		return (argb >> 8) & 255;
	}
	
	public static int getBlue(int argb)
	{
		return argb & 255;
	}
	
	public static int getHue(int argb)
	{
		float[] hsb = RGBtoHSB(getRed(argb), getGreen(argb), getBlue(argb));
		return (int)hsb[0];
	}
	
	public static int getHSBSaturation(int argb)
	{
		float[] hsb = RGBtoHSB(getRed(argb), getGreen(argb), getBlue(argb));
		return (int)(hsb[1]);
	}
	
	public static int getBrightness(int argb)
	{
		float[] hsb = RGBtoHSB(getRed(argb), getGreen(argb), getBlue(argb));
		return (int)(hsb[2]);
	}
	
	public static int getHSLSaturation(int argb)
	{
		float[] hsl = RGBtoHSL(getRed(argb), getGreen(argb), getBlue(argb));
		return (int)hsl[1];
	}
	
	public static int getLightness(int argb)
	{
		float[] hsl = RGBtoHSL(getRed(argb), getGreen(argb), getBlue(argb));
		return (int)hsl[2];
	}
	
	public static int getCyan(int argb)
	{
		int[] cmyk = RGBtoCMYK(getRed(argb), getGreen(argb), getBlue(argb));
		return cmyk[0];
	}
	
	public static int getMagenta(int argb)
	{
		int[] cmyk = RGBtoCMYK(getRed(argb), getGreen(argb), getBlue(argb));
		return cmyk[1];
	}
	
	public static int getYellow(int argb)
	{
		int[] cmyk = RGBtoCMYK(getRed(argb), getGreen(argb), getBlue(argb));
		return cmyk[2];
	}
	
	public static int getKey(int argb)
	{
		int[] cmyk = RGBtoCMYK(getRed(argb), getGreen(argb), getBlue(argb));
		return cmyk[3];
	}
	
	public static int changeAlpha(int argb, int amt)
	{
		int a = (int)Helper.constrain(getAlpha(argb) + amt, 0, 255);
		int r = getRed(argb);
		int g = getGreen(argb);
		int b = getBlue(argb);
		
		return toIntARGB(a, r, g, b);
	}
	
	public static int changeRed(int argb, int amt)
	{
		int a = getAlpha(argb);
		int r = (int)Helper.constrain(getRed(argb) + amt, 0, 255);
		int g = getGreen(argb);
		int b = getBlue(argb);
		
		return toIntARGB(a, r, g, b);
	}
	
	public static int changeGreen(int argb, int amt)
	{
		int a = getAlpha(argb);
		int r = getRed(argb);
		int g = (int)Helper.constrain(getGreen(argb) + amt, 0, 255);
		int b = getBlue(argb);
		
		return toIntARGB(a, r, g, b);
	}
	
	public static int changeBlue(int argb, int amt)
	{
		int a = getAlpha(argb);
		int r = getRed(argb);
		int g = getGreen(argb);
		int b = (int)Helper.constrain(getBlue(argb) + amt, 0, 255);
		
		return toIntARGB(a, r, g, b);
	}
	
	public static int changeHue(int argb, int amt)
	{
		int a = getAlpha(argb);
		int h = (int)Helper.constrain(getHue(argb) + amt, 0, 360);
		int s = getHSBSaturation(argb);
		int b = getBrightness(argb);
		
		return toIntAHSB(a, h, s, b);
	}
	
	public static int changeHSBSaturation(int argb, int amt)
	{
		int a = getAlpha(argb);
		int h = getHue(argb);
		int s = (int)Helper.constrain(getHSBSaturation(argb) + amt, 0, 100);
		int b = getBrightness(argb);
		
		return toIntAHSB(a, h, s, b);
	}
	
	public static int changeBrightness(int argb, int amt)
	{
		int a = getAlpha(argb);
		int h = getHue(argb);
		int s = getHSBSaturation(argb);
		int b = (int)Helper.constrain(getBrightness(argb) + amt, 0, 100);
		
		return toIntAHSB(a, h, s, b);
	}
	
	public static int changeHSLSaturation(int argb, int amt)
	{
		int a = getAlpha(argb);
		int h = getHue(argb);
		int s = (int)Helper.constrain(getHSLSaturation(argb) + amt, 0, 100);
		int l = getLightness(argb);
		
		return toIntAHSL(a, h, s, l);
	}
	
	public static int changeLightness(int argb, int amt)
	{
		int a = getAlpha(argb);
		int h = getHue(argb);
		int s = getHSLSaturation(argb);
		int l = (int)Helper.constrain(getLightness(argb) + amt, 0, 100);
		
		return toIntAHSL(a, h, s, l);
	}
	
	public static int changeCyan(int argb, int amt)
	{
		int a = getAlpha(argb);
		int c = (int)Helper.constrain(getCyan(argb), 0, 255);
		int m = getMagenta(argb);
		int y = getYellow(argb);
		int k = getKey(argb);
		return toIntACMYK(a, c, m, y, k);
	}
	
	public static int changeMagenta(int argb, int amt)
	{
		int a = getAlpha(argb);
		int c = getCyan(argb);
		int m = (int)Helper.constrain(getMagenta(argb), 0, 255);
		int y = getYellow(argb);
		int k = getKey(argb);
		return toIntACMYK(a, c, m, y, k);
	}
	
	public static int changeYellow(int argb, int amt)
	{
		int a = getAlpha(argb);
		int c = getCyan(argb);
		int m = getMagenta(argb);
		int y = (int)Helper.constrain(getYellow(argb), 0, 255);
		int k = getKey(argb);
		return toIntACMYK(a, c, m, y, k);
	}
	
	public static int changeKey(int argb, int amt)
	{
		int a = getAlpha(argb);
		int c = getCyan(argb);
		int m = getMagenta(argb);
		int y = getYellow(argb);
		int k = (int)Helper.constrain(getKey(argb), 0, 255);
		return toIntACMYK(a, c, m, y, k);
	}
	
	public static int randColour()
	{
		Random rand = new Random();
		return toIntAHSL(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
	}
	
	public static int randColour(int alpha)
	{
		Random rand = new Random();
		return toIntAHSL(alpha, rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
	}
	
	public static int fromAWTColor(Color col)
	{
		return toIntARGB(col.getAlpha(), col.getRed(), col.getGreen(), col.getBlue());
	}
	
	public static int getDifference(int colour1, int colour2)
	{
		// Max difference: 441.673 => 442 (which isn't so nice)
		return (int)Math.round(Math.sqrt(Helper.sq(getRed(colour2) - getRed(colour1)) + Helper.sq(getGreen(colour2) - getGreen(colour1)) + Helper.sq(getBlue(colour2) - getBlue(colour1))));
	}
	
	public static int getDifference(int colour1, int colour2, boolean useAlpha)
	{
		if(useAlpha)
		{
			// Max difference: 510 (which is 255 * 2, which is nice)
			return (int)Math.round(Math.sqrt(Helper.sq(getAlpha(colour2) - getAlpha(colour1)) + Helper.sq(getRed(colour2) - getRed(colour1)) + Helper.sq(getGreen(colour2) - getGreen(colour1)) + Helper.sq(getBlue(colour2) - getBlue(colour1))));
		}
		else
		{
			// Max difference: 441.673 => 442 (which isn't so nice)
			return (int)Math.round(Math.sqrt(Helper.sq(getRed(colour2) - getRed(colour1)) + Helper.sq(getGreen(colour2) - getGreen(colour1)) + Helper.sq(getBlue(colour2) - getBlue(colour1))));
		}
	}
	
	public static String toStringARGB(int argb)
	{
		return "Alpha: " + getAlpha(argb) + " Red: " + getRed(argb) + " Green: " + getGreen(argb) + " Blue: " + getBlue(argb);
	}
	
	public static int lerp(int col1, int col2, float amt)
	{
		int alpha = (int)Math.round(Helper.lerp(getAlpha(col1), getAlpha(col2), amt));
		int red = (int)Math.round(Helper.lerp(getRed(col1), getRed(col2), amt));
		int green = (int)(Math.round(Helper.lerp(getGreen(col1), getGreen(col2), amt)));
		int blue = (int)(Math.round(Helper.lerp(getBlue(col1), getBlue(col2), amt)));
		
		return toIntARGB(alpha, red, green, blue);
	}
	
	/**
	 * Uses the Porter & Duff equations to calculate the alpha composite of {@code col} with {@code overlay} as an overlying colour
	 * @param col
	 * @param overlay
	 * @return The alpha composite
	 * 
	 * @see <a href="https://en.wikipedia.org/wiki/Alpha_compositing">https://en.wikipedia.org/wiki/Alpha_compositing</a>
	 */
	public static int composite(int col, int overlay)
	{
		// It won't matter much but I've precomputed my divisions, / 255 is approximately the same as * 0.003921569 (too small a difference to matter)
		// This is because multiplication is faster than division, and if using this you're likely calling this many many times per frame, let alone per second
		
		int src = overlay;
		float srcA = getAlpha(src) * 0.003921569f;
		
		if(srcA == 0)
			return col;
		else if(getAlpha(src) == 255)
			return src;
		
		float srcR = getRed(src) * 0.003921569f;
		float srcG = getGreen(src) * 0.003921569f;
		float srcB = getBlue(src) * 0.003921569f;
		
		int dst = col;
		float dstA = getAlpha(dst) * 0.003921569f;
		
		if(dstA == 0)
			return src;
		
		float dstR = getRed(dst) * 0.003921569f;
		float dstG = getGreen(dst) * 0.003921569f;
		float dstB = getBlue(dst) * 0.003921569f;
		
		float a = srcA + dstA * (1 - srcA);
		
		if(a == 0)
			return TRANSPARENT;
		
		float r = (srcR * srcA + dstR * dstA * (1 - srcA)) / a;
		float g = (srcG * srcA + dstG * dstA * (1 - srcA)) / a;
		float b = (srcB * srcA + dstB * dstA * (1 - srcA)) / a;
		
		// Shouldn't need to constrain
		int outA = (int)Helper.constrain(Math.round(a * 255), 0, 255);
		int outR = (int)Helper.constrain(Math.round(r * 255), 0, 255);
		int outG = (int)Helper.constrain(Math.round(g * 255), 0, 255);
		int outB = (int)Helper.constrain(Math.round(b * 255), 0, 255);
		
		return toIntARGB(outA, outR, outG, outB);
	}
}