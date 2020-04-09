package jpixeleditor.tools;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * An ordered collection of 2 generic variable entries. You can get the ArrayList of entries with {@link #getEntries getEntries}
 * @author william-banks
 *
 * @param <K>
 * @param <V>
 */
public class MyMap<K, V>
{
	/**
	 * Represents an entry in a MyMap
	 * @author william-banks
	 *
	 * @param <K>
	 * @param <V>
	 */
	public static class MyMapEntry<K, V>
	{
		private K key;
		private V value;
		
		/**
		 * Creates a new entry with key and value corresponding to the inputs
		 * @param key
		 * @param value
		 */
		public MyMapEntry(K key, V value)
		{
			this.key = key;
			this.value = value;
		}
		
		public K getKey()
		{
			return key;
		}
		
		public V getValue()
		{
			return value;
		}
		
		// We want two MyMapEntries to be equal to each other iff the keys are equal, disregarding the actual values
		@Override public boolean equals(Object o)
		{
			if(o == null)
				return false;
			if(!(o instanceof MyMapEntry<?, ?>))
				return false;
			return ((MyMapEntry<?, ?>)o).key.equals(key);
		}
		
		@Override public int hashCode()
		{
			return key.hashCode();
		}
	}
	
	private ArrayList<MyMapEntry<K, V>> entries;
	
	/**
	 * Creates a new MyMap with an empty set of entries of an arbitrary length
	 */
	public MyMap()
	{
		entries = new ArrayList<MyMapEntry<K, V>>();
	}
	
	/**
	 * If there is already an entry with a key equal to {@code key}, then it updates the value in that entry. If not, then it adds an entry with the specified key and value
	 * @param key
	 * @param value
	 */
	public void put(K key, V value)
	{
		for(int i = 0; i < entries.size(); i++)
		{
			if(entries.get(i).key.equals(key))
			{
				entries.get(i).value = value;
				return;
			}
		}
		entries.add(new MyMapEntry<K, V>(key, value));
	}
	
	/**
	 * Removes the entry with a key equal to the specified key. If there isn't one, then this does nothing
	 * @param key
	 */
	public void remove(K key)
	{
		for(int i = 0; i < entries.size(); i++)
		{
			if(entries.get(i).key.equals(key))
			{
				entries.remove(i);
				return;
			}
		}
	}
	
	/**
	 * Removes all entries with values equal to the specified value. If there is none, this does nothing
	 * @param value
	 */
	public void removeAll(V value)
	{
		for(int i = 0; i < entries.size(); i++)
		{
			if(entries.get(i).value.equals(value))
			{
				entries.remove(i);
			}
		}
	}
	
	/**
	 * Returns the entry with a key equal to the specified key
	 * @param key
	 * @return The entry with the key equal to the specified key
	 */
	public MyMapEntry<K, V> get(K key)
	{
		for(MyMapEntry<K, V> entry : entries)
		{
			if(entry.key.equals(key))
			{
				return entry;
			}
		}
		return null;
	}
	
	/**
	 * Returns an ArrayList of entries with a value equal to the specified value. If there is none, then returns null
	 * @param value
	 * @return An ArrayList of entries where the entries' value is equal to the specified value
	 */
	public ArrayList<MyMapEntry<K, V>> getAll(V value)
	{
		ArrayList<MyMapEntry<K, V>> res = new ArrayList<MyMapEntry<K, V>>();
		
		for(MyMapEntry<K, V> entry : entries)
		{
			if(entry.value.equals(value))
			{
				res.add(entry);
			}
		}
		return res.size() == 0 ? null : res;
	}
	
	/**
	 * Returns true if there is an entry with a key equal to the specified key, false otherwise
	 * @param key
	 * @return true if entry found, false otherwise
	 */
	public boolean containsKey(K key)
	{
		for(MyMapEntry<K, V> entry : entries)
		{
			if(entry.key.equals(key))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if there is an entry with a value equal to the specified value, false otherwise
	 * @param key
	 * @return true if entry found, false otherwise
	 */
	public boolean containsValue(V value)
	{
		for(MyMapEntry<K, V> entry : entries)
		{
			if(entry.value.equals(value))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns all entries in the form of an ArrayList
	 * @return An ArrayList of all entries
	 */
	public ArrayList<MyMapEntry<K, V>> getEntries()
	{
		return entries;
	}
	
	/**
	 * Removes all entries
	 */
	public void clear()
	{
		entries.clear();
	}
	
	/**
	 * Returns true if {@code obj} is also a MyMap object and contains the same entries of the same type
	 */
	@Override public boolean equals(Object obj)
	{
		if(obj == null)
		{
			return false;
		}
		
		if(!(obj instanceof MyMap<?, ?>))
		{
			return false;
		}
		
		return ((MyMap<?, ?>)obj).getEntries().equals(getEntries());
	}
	
	/**
	 * Returns an array containing all the keys in the entries, in order. Note that this method iterates over all entries to create the array
	 * @return An array containing the key for each entry
	 */
	public K[] getKeyArray()
	{
		@SuppressWarnings("unchecked")
		// Can't create a generic array the normal way, so have to do this. Helpfully I don't have to request a Class<?> type as a parameter
		K[] res = (K[])Array.newInstance(entries.get(0).getKey().getClass(), entries.size());
		
		for(int i = 0; i < entries.size(); i++)
		{
			res[i] = entries.get(i).getKey();
		}
		
		return res;
	}
	
	/**
	 * Returns an array containing all the values in the entries, in order. Note that this method iterates over all entries to create the array
	 * @return An array containing the value for each entry
	 */
	public V[] getValueArray()
	{
		@SuppressWarnings("unchecked")
		// Can't create a generic array the normal way, so have to do this. Helpfully I don't have to request a Class<?> type as a parameter
		V[] res = (V[])Array.newInstance(entries.get(0).getValue().getClass(), entries.size());
		
		for(int i = 0; i < entries.size(); i++)
		{
			res[i] = entries.get(i).getValue();
		}
		
		return res;
	}
	
	/**
	 * Returns true if this map contains no entries, false otherwise
	 * @return true if empty, false otherwise
	 */
	public boolean isEmpty()
	{
		return entries.isEmpty();
	}
	
	/**
	 * Adds all entries from the input map to this map
	 * @param map The map from which you want to add entries from
	 */
	public void concat(MyMap<K, V> map)
	{
		LinkedHashSet<MyMapEntry<K, V>> set = new LinkedHashSet<MyMapEntry<K, V>>();
		set.addAll(getEntries());
		set.addAll(map.getEntries());
		
		setEntries(new ArrayList<MyMapEntry<K, V>>(set));
	}
	
	public MyMap<K, V> copy()
	{
		MyMap<K, V> map = new MyMap<K, V>();
		map.getEntries().addAll(getEntries());
		return map;
	}
	
	public void setEntries(ArrayList<MyMapEntry<K, V>> newEntries)
	{
		entries = newEntries;
	}
}