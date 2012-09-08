package org.antlr.v4.runtime.misc;

/** What does it mean for two objects to be equivalent? */
public interface EquivalenceRelation<T> {
	public int hashCode(T o);
	public boolean equals(T a, T b);
}
