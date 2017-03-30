/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/** A HashMap that remembers the order that the elements were added.
 *  You can alter the ith element with set(i,value) too :)  Unique list.
 *  I need the replace/set-element-i functionality so I'm subclassing
 *  LinkedHashSet.
 */
public class OrderedHashSet<T> extends LinkedHashSet<T> {
    /** Track the elements as they are added to the set */
    protected ArrayList<T> elements = new ArrayList<T>();

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
    @Override
    public boolean add(T value) {
        boolean result = super.add(value);
		if ( result ) {  // only track if new element not in set
			elements.add(value);
		}
		return result;
    }

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
    }

	@Override
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
		if (!(o instanceof OrderedHashSet<?>)) {
			return false;
		}

//		System.out.print("equals " + this + ", " + o+" = ");
		boolean same = elements!=null && elements.equals(((OrderedHashSet<?>)o).elements);
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
    public Object clone() {
        @SuppressWarnings("unchecked") // safe (result of clone)
        OrderedHashSet<T> dup = (OrderedHashSet<T>)super.clone();
        dup.elements = new ArrayList<T>(this.elements);
        return dup;
    }

    @Override
	public Object[] toArray() {
		return elements.toArray();
	}

	@Override
	public String toString() {
        return elements.toString();
    }
}
