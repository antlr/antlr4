/*
 * Copyright 2019 The ANTLR Project. All rights reserved.
 * Licensed under the BSD-3-Clause license. See LICENSE file in the project root for license information.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Incremental parser implementation
 * <p>
 * There are only two differences between this parser and the underlying regular
 * Parser - guard rules and min/max tracking
 * <p>
 * The guard rule API is used in incremental mode to know when a rule context
 * can be reused. It looks for token changes in the bounds of the rule.
 * <p>
 * The min/max tracking is used to track how far ahead/behind the parser looked
 * to correctly detect whether a token change can affect a parser rule in the future (IE when
 * handed to the guard rule of the next parse)
 *
 * @note See IncrementalParsing.md for more details on the theory behind this.
 * In order to make this easier in code generation, we use the parse
 * listener interface to do most of our work.
 */
public abstract class IncrementalParser extends Parser implements ParseTreeListener {
	// Current parser epoch. Incremented every time a new incremental parser is
	// created.
	private static int _PARSER_EPOCH = 0;

	private int parserEpoch;
	private IncrementalParserData parseData;

	public IncrementalParser(IncrementalTokenStream input) {
		this(input, null);
	}

	public IncrementalParser(IncrementalTokenStream input, IncrementalParserData parseData) {
		super(input);
		this.parseData = parseData;
		parserEpoch = IncrementalParser.incrementGlobalParserEpoch();
		// Register ourselves as our own parse listener. Life is weird.
		addParseListener(this);
	}

	protected static int incrementGlobalParserEpoch() {
		return ++IncrementalParser._PARSER_EPOCH;
	}

	public int getParserEpoch() {
		return parserEpoch;
	}

	// Push the current token data onto the min max stack for the stream.
	private void pushCurrentTokenToMinMax() {
		IncrementalTokenStream incStream = (IncrementalTokenStream) getInputStream();
		Token token = this._input.LT(1);
		incStream.pushMinMax(token.getTokenIndex(), token.getTokenIndex());
	}

	// Pop the min max stack the stream is using and return the interval.
	private Interval popCurrentMinMax(IncrementalParserRuleContext ctx) {
		IncrementalTokenStream incStream = (IncrementalTokenStream) getInputStream();
		return incStream.popMinMax();
	}

	/**
	 * Guard a rule's previous context from being reused.
	 * <p>
	 * This routine will check whether a given parser rule needs to be rerun, or if
	 * we already have context that can be reused for this parse.
	 */
	public IncrementalParserRuleContext guardRule(IncrementalParserRuleContext parentCtx, int state, int ruleIndex) {
		// If we have no previous parse data, the rule needs to be run.
		if (this.parseData == null) {
			return null;
		}
		// See if we have seen this state before at this starting point.
		IncrementalParserRuleContext existingCtx = this.parseData.tryGetContext(
			parentCtx != null ? parentCtx.depth() + 1 : 1, getState(), ruleIndex,
			this._input.LT(1).getTokenIndex());
		// We haven't see it, so we need to rerun this rule.
		if (existingCtx == null) {
			return null;
		}
		// We have seen it, see if it was affected by the parse
		if (this.parseData.ruleAffectedByTokenChanges(existingCtx)) {
			return null;
		}
		// Everything checked out, reuse the rule context - we add it to the
		// parent context as enterRule would have;
		if (this._ctx != null) {
			IncrementalParserRuleContext parent = (IncrementalParserRuleContext) this._ctx;
			// add current context to parent if we have a parent
			if (parent != null) {
				parent.addChild(existingCtx);
			}
		}
		return existingCtx;
	}

	/**
	 * Pop the min max stack the stream is using and union the interval into the
	 * passed in context. Return the interval for the context
	 *
	 * @param ctx Context to union interval into.
	 */
	private Interval popAndHandleMinMax(IncrementalParserRuleContext ctx) {
		Interval interval = popCurrentMinMax(ctx);
		ctx.setMinMaxTokenIndex(ctx.getMinMaxTokenIndex().union(interval));
		// Returning interval is wrong because there may have been child
		// intervals already merged into this ctx.
		return ctx.getMinMaxTokenIndex();
	}
	/*
	 * This is part of the regular Parser API. The super method must be called.
	 */

	/**
	 * The new recursion context is an unfortunate edge case for us. It reparents
	 * the relationship between the contexts, so we need to merge intervals here.
	 */
	@Override
	public void pushNewRecursionContext(ParserRuleContext localctx, int state, int ruleIndex) {
		// This context becomes the child
		IncrementalParserRuleContext previous = (IncrementalParserRuleContext) this._ctx;
		// The incoming context becomes the parent
		IncrementalParserRuleContext incLocalCtx = (IncrementalParserRuleContext) localctx;
		incLocalCtx.setMinMaxTokenIndex(incLocalCtx.getMinMaxTokenIndex().union(previous.getMinMaxTokenIndex()));
		super.pushNewRecursionContext(localctx, state, ruleIndex);
	}

	/*
	 * These two functions are parse of the ParseTreeListener API. We do not need to
	 * call super methods
	 */

	@Override
	public void enterEveryRule(ParserRuleContext ctx) {
		// During rule entry, we push a new min/max token state.
		pushCurrentTokenToMinMax();
		IncrementalParserRuleContext incCtx = (IncrementalParserRuleContext) ctx;
		incCtx.epoch = this.getParserEpoch();
	}

	@Override
	public void exitEveryRule(ParserRuleContext ctx) {
		// On exit, we need to merge the min max into the current context,
		// and then merge the current context interval into our parent.

		// First merge with the interval on the top of the stack.
		IncrementalParserRuleContext incCtx = (IncrementalParserRuleContext) ctx;
		Interval interval = popAndHandleMinMax(incCtx);

		// Now merge with our parent interval.
		if (incCtx.parent != null) {
			IncrementalParserRuleContext parentIncCtx = (IncrementalParserRuleContext) incCtx.parent;
			parentIncCtx.setMinMaxTokenIndex(parentIncCtx.getMinMaxTokenIndex().union(interval));
		}
	}

	@Override
	public void visitTerminal(TerminalNode node) {

	}

	@Override
	public void visitErrorNode(ErrorNode node) {

	}

}
