package org.antlr.v4.analysis;

import org.antlr.v4.misc.IntSet;

public class MultipleRecursiveAltsSignal extends RuntimeException {
	public IntSet recursiveAltSet;
	public MultipleRecursiveAltsSignal(IntSet recursiveAltSet) {
		this.recursiveAltSet = recursiveAltSet;
	}
}
