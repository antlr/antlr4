package org.antlr.v4.misc;

import java.util.ArrayList;

public class UniqueList<T> extends ArrayList<T> {
	@Override
	public boolean add(T t) {
		if ( contains(t) ) return false;
		return super.add(t);
	}
}
