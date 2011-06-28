package org.antlr.v4.runtime.misc;

import java.util.*;

/** A HashMap that remembers the order that the elements were added.
 *  You can alter the ith element with set(i,value) too :)  Unique list.
 *  I need the replace/set-element-i functionality so I'm subclassing
 *  OrderedHashSet.
 */
public class OrderedHashSet<T> extends LinkedHashSet<T> {
    /** Track the elements as they are added to the set */
    protected List<T> elements = new ArrayList<T>();

    public T get(int i) {
        return elements.get(i);
    }

    /** Replace an existing value with a new value; updates the element
     *  list and the hash table, but not the key as that has not changed.
     */
    public T set(int i, T value) {
        T oldElement = elements.get(i);
        elements.set(i,value); // update list
        super.remove(oldElement); // now update the set: remove/add
        super.add(value);
        return oldElement;
    }

	public boolean remove(int i) {
		T o = elements.remove(i);
        return super.remove(o);
	}

    /** Add a value to list; keep in hashtable for consistency also;
     *  Key is object itself.  Good for say asking if a certain string is in
     *  a list of strings.
     */
    public boolean add(T value) {
        boolean result = super.add(value);
		if ( result ) {  // only track if new element not in set
			elements.add(value);
		}
		return result;
    }

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
    }

	public void clear() {
        elements.clear();
        super.clear();
    }

	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	@Override
	public boolean equals(Object o) {
//		System.out.print("equals " + this + ", " + o+" = ");
		boolean same = elements!=null && elements.equals(((OrderedHashSet)o).elements);
//		System.out.println(same);
		return same;
	}

	@Override
	public Iterator<T> iterator() {
		return elements.iterator();
	}

	/** Return the List holding list of table elements.  Note that you are
     *  NOT getting a copy so don't write to the list.
     */
    public List<T> elements() {
        return elements;
    }

	@Override
	public Object[] toArray() {
		return elements.toArray();
	}

	public String toString() {
        return elements.toString();
    }
}
