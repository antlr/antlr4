package org.antlr.v4.misc;

public class Triple<A,B,C> extends Pair<A,B> {
	public C c;
	public Triple(A a, B b, C c) {
		super(a,b);
		this.c = c;
	}

}
