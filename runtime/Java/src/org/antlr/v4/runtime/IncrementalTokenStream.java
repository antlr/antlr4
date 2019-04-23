/*
 * Copyright 2019 The ANTLR Project. All rights reserved.
 * Licensed under the BSD-3-Clause license. See LICENSE file in the project root for license information.
 */
package org.antlr.v4.runtime;

import java.util.Stack;

import org.antlr.v4.runtime.misc.Interval;

public class IncrementalTokenStream extends CommonTokenStream {
	/**
	 * ANTLR looks at the same tokens alot, and this avoids recalculating the
	 * interval when the position and lookahead number doesn't move.
	 */
	private int lastP = -1;
	private int lastK = -1;

	/**
	 * This tracks the min/max token index looked at since the value was reset. This
	 * is used to track how far ahead the grammar looked, since it may be outside
	 * the rule context's start/stop tokens. We need to maintain a stack of such
	 * indices.
	 */

	private Stack<Interval> minMaxStack = new Stack<Interval>();

	/**
	 * Constructs a new {@link IncrementalTokenStream} using the specified token
	 * source and the default token channel ({@link Token#DEFAULT_CHANNEL}).
	 *
	 * @param tokenSource The token source.
	 */
	public IncrementalTokenStream(TokenSource tokenSource) {
		super(tokenSource);
	}

	/**
	 * Constructs a new {@link IncrementalTokenStream} using the specified token
	 * source and filtering tokens to the specified channel. Only tokens whose
	 * {@link Token#getChannel} matches {@code channel} or have the
	 * {@link Token#getType} equal to {@link Token#EOF} will be returned by the
	 * token stream lookahead methods.
	 *
	 * @param tokenSource The token source.
	 * @param channel     The channel to use for filtering tokens.
	 */
	public IncrementalTokenStream(TokenSource tokenSource, int channel) {
		this(tokenSource);
		this.channel = channel;
	}

	/**
	 * Push a new minimum/maximum token state.
	 *
	 * @param min Minimum token index
	 * @param max Maximum token index
	 */
	public void pushMinMax(int min, int max) {
		minMaxStack.push(Interval.of(min, max));
	}

	/**
	 * Pop the current minimum/maximum token state and return it.
	 */
	public Interval popMinMax() {
		if (minMaxStack.size() == 0) {
			throw new IndexOutOfBoundsException("Can't pop the min max state when there are 0 states");
		}
		return minMaxStack.pop();
	}

	/**
	 * This is an override of the base LT function that tracks the minimum/maximum
	 * token index looked at.
	 */
	@Override
	public Token LT(int k) {
		Token result = super.LT(k);
		// Adjust the top of the minimum maximum stack if the position/lookahead amount
		// changed.
		if (minMaxStack.size() != 0 && (lastP != p || lastK != k)) {
			int lastIdx = minMaxStack.size() - 1;
			Interval stackItem = minMaxStack.get(lastIdx);
			minMaxStack.set(lastIdx, stackItem.union(Interval.of(result.getTokenIndex(), result.getTokenIndex())));

			lastP = p;
			lastK = k;
		}
		return result;
	}
}
