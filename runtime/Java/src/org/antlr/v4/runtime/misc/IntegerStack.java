/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime.misc;

/**
 *
 * @author Sam Harwell
 */
public class IntegerStack extends IntegerList {

	public IntegerStack() {
	}

	public IntegerStack(int capacity) {
		super(capacity);
	}

	public IntegerStack(IntegerStack list) {
		super(list);
	}

	public final void push(int value) {
		add(value);
	}

	public final int pop() {
		return removeAt(size() - 1);
	}

	public final int peek() {
		return get(size() - 1);
	}

}
