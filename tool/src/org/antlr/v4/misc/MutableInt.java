package org.antlr.v4.misc;

@SuppressWarnings("serial")
public class MutableInt extends Number implements Comparable<Number> {
	public int v;

	public MutableInt(int v) { this.v = v; }
	@Override public int compareTo(Number o) { return v-o.intValue(); }
	@Override public int intValue() { return v; }
	@Override public long longValue() { return v; }
	@Override public float floatValue() { return v; }
	@Override public double doubleValue() { return v; }

	@Override
	public String toString() {
		return String.valueOf(v);
	}
}
