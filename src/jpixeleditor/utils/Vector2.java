package jpixeleditor.utils;

public class Vector2
{
	/*
	 * Vector2 - An class containing a set of single-precision floating-point 2D coordinates and various functions to manipulate them
	 * 
	 * Vector2() - Constructor which makes a new Vector2 with x and y set to 0
	 * Vector2(Vector2 v) - Constructor which makes a new Vector2 which is a copy of the input Vector2
	 * Vector2(float x, float y) - Constructor which makes a new Vector2 with x and y set to input x and y
	 */
	public float x;
	public float y;
	
	public Vector2()
	{
		x = 0;
		y = 0;
	}
	
	public Vector2(Vector2 v)
	{
		x = v.x;
		y = v.y;
	}
	
	public Vector2(float x, float y)
	{
		this.x = (float)x;
		this.y = (float)y;
	}
	
	/*
	 * fromAngle - Creates and returns a new Vector2 pointing at the input angle
	 * 
	 * fromAngle(float angle) - Returns a new Vector2 which is a unit vector pointing at the input angle (in radians)
	 * fromAngle(float angle, float mag) - Returns a new Vector2 which is a vector of length input mag pointing at the input angle (in radians)
	 */
	public static Vector2 fromAngle(float angle)
	{
		return new Vector2((float)Math.cos(angle), (float)Math.sin(angle));
	}
	
	public static Vector2 fromAngle(float angle, float mag)
	{
		return new Vector2((float)Math.cos(angle), (float)Math.sin(angle)).mult(mag);
	}
	
	/*
	 * set - sets this Vector2 to a set of 2D coordinates or another Vector2
	 * 
	 * set(Vector2 v) - Sets this Vector2's x and y to another Vector2's x and y
	 * set(float x, float y) - Sets this Vector2's x and y to the input x and y
	 * set(float val) - Sets this Vector2's x and y to the input val
	 */
	public Vector2 set(Vector2 v)
	{
		x = v.x;
		y = v.y;
		return this;
	}
	
	public Vector2 set(float x, float y)
	{
		this.x = x;
		this.y = y;
		return this;
	}
	
	public Vector2 set(float val)
	{
		x = val;
		y = val;
		return this;
	}
	
	/*
	 * add - adds a Vector2 or a set of 2D coordinates to this Vector2
	 * 
	 * add(Vector2 v) - Increments this Vector2's x and y by the input Vector2's x and y
	 * add(float x, float y) - Increments this Vector2's x and y by the input x and y
	 * add(float val) - Increments this Vector2's x and y by the input val
	 */
	public Vector2 add(Vector2 v)
	{
		x += v.x;
		y += v.y;
		return this;
	}
	
	public Vector2 add(float x, float y)
	{
		this.x += x;
		this.y += y;
		return this;
	}
	
	public Vector2 add(float val)
	{
		x += val;
		y += val;
		return this;
	}
	
	/*
	 * sub - subtracts a Vector2 or a set of 2D coordinates from this Vector2
	 * 
	 * sub(Vector2 v) - Subtracts the input Vector2's x and y from this Vector2's x and y
	 * sub(float x, float y) - Subtracts the input x and y from this Vector2's x and y
	 * sub(float val) - Subtracts the input val from this Vector2's x and y
	 */
	public Vector2 sub(Vector2 v)
	{
		x -= v.x;
		y -= v.y;
		return this;
	}
	
	public Vector2 sub(float x, float y)
	{
		this.x -= x;
		this.y -= y;
		return this;
	}
	
	public Vector2 sub(float val)
	{
		x -= val;
		y -= val;
		return this;
	}
	
	/*
	 * mult - multiplies this Vector2 and a set of 2D coordinates or another Vector2
	 * 
	 * mult(Vector2 v) - Multiplies this Vector2's x and y with the input Vector2's x and y
	 * mult(float x, float y) - Multiplies this Vector2's x and y with the input x and y
	 * mult(float val) - Multiplies this Vector2's x and y with the input val
	 */
	public Vector2 mult(Vector2 v)
	{
		x *= v.x;
		y *= v.y;
		return this;
	}
	
	public Vector2 mult(float x, float y)
	{
		this.x *= x;
		this.y *= y;
		return this;
	}
	
	public Vector2 mult(float val)
	{
		x *= val;
		y *= val;
		return this;
	}
	
	/*
	 * div - divides this Vector2 by a set of coordinates of another Vector2
	 * 
	 * div(Vector2 v) - Divides this Vector2's x and y by the input Vector2's x and y
	 * div(float x, float y) - Divides this Vector2's x and y by the input x and y
	 * div(float val) - Divides this Vector2's x and y by the input val
	 */
	public Vector2 div(Vector2 v)
	{
		x /= v.x;
		y /= v.y;
		return this;
	}
	
	public Vector2 div(float x, float y)
	{
		this.x /= x;
		this.y /= y;
		return this;
	}
	
	public Vector2 div(float val)
	{
		x /= val;
		y /= val;
		return this;
	}
	
	/*
	 * mag/magSq - gets the magnitude of this Vector2 / gets the magnitude of this Vector2, squared
	 * 
	 * mag() - Returns the magnitude of this Vector2
	 * magSq() - Returns the magnitude of this Vector2 squared
	 */
	public float mag()
	{
		return (float)Math.sqrt(x * x + y * y);
	}
	
	public float magSq()
	{
		return (x * x + y * y);
	}
	
	/*
	 * normalize/setMag - sets this Vector2 to a magnitude of 1 / to the input magnitude
	 * 
	 * normalize() - Sets this Vector2's x and y so that it's magnitude is 1
	 * setMag(float mag) - Sets this Vector2's x and y so that it's magnitude is the input mag
	 */
	public Vector2 normalize()
	{
		float m = mag();
		if(m != 0 && m != 1)
		{
			div(m);
		}
		return this;
	}
	
	public Vector2 setMag(float mag)
	{
		normalize();
		mult(mag);
		return this;
	}
	
	/*
	 * limit - limit this Vector2 in some way
	 * 
	 * limit(float magLim) - Limits this Vector2's x and y so that it's magnitude is below or equal to the limit
	 * limit(float min, float max) - Constrain this Vector2's x and y so that it's magnitude is between the input min and max
	 */
	public Vector2 limit(float magLim)
	{
		float m = mag();
		if(m > magLim)
		{
			setMag(magLim);
		}
		return this;
	}
	
	public Vector2 limit(float min, float max)
	{
		float m = mag();
		if(m < min)
		{
			setMag(min);
		}
		if(m > max)
		{
			setMag(max);
		}
		return this;
	}
	
	/*
	 * heading - gets the angle of this Vector2
	 * 
	 * heading() - Returns the angle (in radians) that this Vector2 is pointing in
	 */
	public float heading()
	{
		return (float) Math.atan2(y, x);
	}
	
	/*
	 * rotate - rotates this Vector2
	 * 
	 * rotate(float angle) - Sets this Vector2's x and y so that it is the Vector2 it was before, rotated by input angle (in radians)
	 */
	public Vector2 rotate(float theta)
	{
		float temp = x;
		x = (float)(x * Math.cos(theta) - y * Math.sin(theta));
		y = (float)(temp * Math.sin(theta) + y * Math.cos(theta));
		return this;
	}
	
	/*
	 * angleBetween - gets the angle between this Vector2 and another Vector2
	 * 
	 * angleBetween(Vector2 v) - Returns the angle between this Vector2 and the input Vector2
	 */
	public float angleBetween(Vector2 v)
	{
		// We get NaN if we pass in a zero vector which can cause problems
	    // Zero seems like a reasonable angle between a (0,0,0) vector and something else
	    if(x == 0 && y == 0)
	    {
	    	return 0;
	    }
	    if(v.x == 0 && v.y == 0)
	    {
	    	return 0;
	    }

	    double dot = x * v.x + y * v.y;
	    double v1mag = Math.sqrt(x * x + y * y);
	    double v2mag = Math.sqrt(v.x * v.x + v.y * v.y);
	    // This should be a number between -1 and 1, since it's "normalized"
	    double amt = dot / (v1mag * v2mag);
	    // But if it's not due to rounding error, then we need to fix it
	    // Otherwise if outside the range, acos() will return NaN
	    if(amt <= -1)
	    {
	      return (float)Math.PI;
	    }
	    else if(amt >= 1)
	    {
	      return 0;
	    }
	    return (float)Math.acos(amt);
	}
	
	/*
	 * lerp - interpolates between Vector2's
	 * 
	 * lerp(Vector2 v, double amt) - Sets this Vector2's x and y to the interpolation between this Vector2 and the input Vector2 by input amt
	 * lerp(Vector2 v1, Vector2 v2, double amt) - Returns the Vector2 that is the interpolation from inputs v1 to v2 by input amt
	 */
	public Vector2 lerp(Vector2 v, double amt)
	{
		x = (float)lerp(x, v.x, amt);
		y = (float)lerp(y, v.y, amt);
		return this;
	}
	
	public static Vector2 lerp(Vector2 v1, Vector2 v2, double amt)
	{
		Vector2 v = new Vector2((float)lerp(v1.x, v2.x, amt), (float)lerp(v1.y, v2.y, amt));
		return v;
	}
	
	
	/*
	 * lerp - interpolates between two numbers. This is private because there will be another class to provide this function.
	 *        I'm not using the other class (mylib.math.Maths) because it's helpful for everything I need to be packaged in this one class
	 * 
	 * lerp(double start, double stop, double amt) - Returns the interpolation between inputs start to stop by input amt
	 */
	private static double lerp(double start, double stop, double amt)
	{
	    return start + (stop-start) * amt;
	}
	
	/*
	 * equals - checks if two Vector2 objects are equal (have equal x and y's)
	 * 
	 * equals(Vector2 v) - Returns if this Vector2's x and y are equal to the input Vector2's x and y
	 */
	public boolean equals(Vector2 v)
	{
		return (x == v.x && y == v.y);
	}
}
