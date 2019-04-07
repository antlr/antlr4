/*
 * Copyright 2019 The ANTLR Project. All rights reserved.
 * Licensed under the BSD-3-Clause license. See LICENSE file in the project root for license information.
 */
package org.antlr.v4.runtime;

import java.util.*;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

/*
  Compare the intervals in a token offset range.  This is used as a comparator for a TreeMap.
  Note that equality is defined as containment to make searches for individual element ranges find
  their containing range.
  The ranges must otherwise be non-overlapping.. 
*/
class CompareTokenOffsetRanges implements Comparator<Interval> {

	@Override
	public int compare(Interval o1, Interval o2) {
		if (o1.properlyContains(o2) || o2.properlyContains(o1)) {
			return 0;
		}
		if (o1.b < o2.a) {
			return -1;
		} else if (o1.a > o2.b) {
			return 1;
		}
		// Overlapping
		return 0;
	}
}

class CompareTokensByStart implements Comparator<TokenChange> {
	@Override
	public int compare(TokenChange tc1, TokenChange tc2) {
		int startDifference = getStartingIndex(tc1) - getStartingIndex(tc2);
		if (startDifference != 0)
			return startDifference;
		if (tc1.changeType == tc2.changeType)
			return 0;
		if (tc1.changeType == TokenChangeType.REMOVED)
			return -1;
		if (tc2.changeType == TokenChangeType.REMOVED)
			return 1;
		return tc1.changeType.compareTo(tc2.changeType);

	}

	private int getStartingIndex(TokenChange tc) {
		if (tc.changeType == TokenChangeType.CHANGED) {
			return tc.oldToken.getStartIndex();
		} else if (tc.changeType == TokenChangeType.ADDED) {
			return tc.newToken.getStartIndex();
		} else {
			return tc.oldToken.getStartIndex();
		}
	}
}

/**
 * This class computes and stores data needed by the incremental parser. It is
 * fairly unoptimized ATM to make things obvious and hopefully less broken.
 * <p>
 * Please note: This class expects to own the parse tree passed in, and will
 * modify it. Please clone them if you need them to remain unmodified for some
 * reason.
 */
public class IncrementalParserData {
	private IncrementalTokenStream tokenStream;
	/*
	 * This mapping goes from a range to a token index offset to be applied for that
	 * range. It is used to figure out what token in the new stream to look at for a
	 * given token in the old stream.
	 *
	 * @note Equality is deliberately defined to be containment on this treemap in
	 * order to be able to find intervals in a given range.
	 */
	private TreeMap<Interval, Integer> tokenOffsets;

	/*
	 * This is the set of tokens that changed in any way. We use a treeset so that
	 * we have the ability to get slices >= and <= certain numbers.
	 * For those runtimes without an equivalent, an array of numbers and a binary search
	 * that finds numbers within a range works just as well.
	 */
	private TreeSet<Integer> changedTokens;

	/* This is the set of token changes that were specified by the user. */
	private List<TokenChange> tokenChanges;

	/*
	 * This maps from depth, rule number, starting token index, to context we've
	 * seen before.
	 */
	private HashMap<String, IncrementalParserRuleContext> ruleStartMap = new HashMap<>();

	public IncrementalParserData() {
	}

	public IncrementalParserData(IncrementalTokenStream tokenStream, List<TokenChange> tokenChanges,
								 IncrementalParserRuleContext oldTree) {
		this.tokenChanges = tokenChanges;
		if (tokenChanges != null) {
			this.tokenStream = tokenStream;
			computeTokenOffsetRanges(oldTree.getMaxTokenIndex());
			indexAndAdjustParseTree(oldTree);
		}
	}

	/**
	 * Take the set of token changes the user specified and convert it into two
	 * things:
	 * 1. A list of changed tokens
	 * 2. A set of ranges that say how tokenIndexes that appear in the old stream
	 * will have changed in the new stream. IE if a token was removed, the tokens
	 * after would appear at originalIndex - 1 in the new stream.
	 *
	 * @param maxOldTokenIndex The maximum token index we may see in the old stream.
	 *                         This is used as the upper bound of the last range.
	 */
	private void computeTokenOffsetRanges(int maxOldTokenIndex) {
		if (this.tokenChanges == null || this.tokenChanges.size() == 0) {
			return;
		}
		// Construct ranges for the token change offsets, and changed token intervals.
		int indexOffset = 0;
		ArrayList<Pair<Interval, Integer>> offsetRanges = new ArrayList<>();
		this.changedTokens = new TreeSet<>();
		Collections.sort(this.tokenChanges, new CompareTokensByStart());
		for (TokenChange tokenChange : this.tokenChanges) {
			int indexToPush = 0;
			if (tokenChange.changeType == TokenChangeType.CHANGED) {
				this.changedTokens.add(tokenChange.newToken.getTokenIndex());
				// We only need to add this to changed tokens, it doesn't
				// change token indexes.
				continue;
			}
			// If a token changed, adjust the index the tokens after it
			else if (tokenChange.changeType == TokenChangeType.REMOVED) {
				this.changedTokens.add(tokenChange.oldToken.getTokenIndex() + indexOffset);

				// The indexes move back one to account for the removed token.
				indexOffset -= 1;
				indexToPush = tokenChange.oldToken.getTokenIndex();
			} else if (tokenChange.changeType == TokenChangeType.ADDED) {
				this.changedTokens.add(tokenChange.newToken.getTokenIndex());
				// The indexes move forward one to account for the removed token.
				indexOffset += 1;
				indexToPush = tokenChange.newToken.getTokenIndex();
			}
			// End the previous range at the token index right before us
			if (offsetRanges.size() != 0) {
				int lastIdx = offsetRanges.size() - 1;
				Pair<Interval, Integer> lastItem = offsetRanges.get(lastIdx);
				offsetRanges.set(lastIdx, new Pair<>(Interval.of(lastItem.a.a, indexToPush - 1), lastItem.b));
			}
			// Push the range this change starts at, and what the effect is on
			// the index.
			offsetRanges.add(new Pair<>(Interval.of(indexToPush, indexToPush), indexOffset));

		}
		// End the final range at length of the old token stream. That is the
		// last possible thing we need to offset.
		if (offsetRanges.size() != 0) {
			int lastIdx = offsetRanges.size() - 1;
			Pair<Interval, Integer> lastItem = offsetRanges.get(lastIdx);
			offsetRanges.set(lastIdx, new Pair<>(Interval.of(lastItem.a.a, maxOldTokenIndex), lastItem.b));
		}

		this.tokenOffsets = new TreeMap<>(new CompareTokenOffsetRanges());
		for (Pair<Interval, Integer> tokenRange : offsetRanges) {
			this.tokenOffsets.put(tokenRange.a, tokenRange.b);
		}
	}

	/**
	 * Determine whether a given parser rule is affected by changes to the token
	 * stream.
	 *
	 * @param ctx Current parser context coming into a rule.
	 */
	public boolean ruleAffectedByTokenChanges(IncrementalParserRuleContext ctx) {
		// If we never got passed data, reparse everything.
		if (this.tokenChanges == null) {
			return true;
		}
		// However if there are no changes, the rule is fine
		if (this.tokenChanges.size() == 0) {
			return false;
		}

		// See if any changed token exists in our upper, lower bounds.
		int start = ctx.getMinTokenIndex();
		int end = ctx.getMaxTokenIndex();
		// See if the set has anything in the range we are asking about
		boolean result = false;
		// Get a view of all elements >= start token to start.
		NavigableSet<Integer> tailSet = this.changedTokens.tailSet(start, true);
		// If *any* are in range, the rule is modified.
		// Since the set is ordered, once we go past the end of the [start, end] range,
		// we can stop.
		for (Integer elem : tailSet) {
			if (elem <= end) {
				result = true;
				break;
			} else if (elem > end) {
				break;
			}
		}
		if (result) {
			return true;
		}

		return false;
	}

	/**
	 * Try to see if we have existing context for this state, rule and token
	 * position that may be reused.
	 *
	 * @param depth      Current rule depth
	 * @param state      Parser state number - currently ignored.
	 * @param ruleIndex  Rule number
	 * @param tokenIndex Token index in the *new* token stream
	 */
	public IncrementalParserRuleContext tryGetContext(int depth, int state, int ruleIndex, int tokenIndex) {
		return this.ruleStartMap.get(getKey(depth, state, ruleIndex, tokenIndex));
	}

	private String getKeyFromContext(IncrementalParserRuleContext ctx) {
		return getKey(ctx.depth(), ctx.invokingState, ctx.getRuleIndex(), ctx.start.getTokenIndex());
	}

	private String getKey(int depth, int state, int rule, int tokenIndex) {
		return String.format("%d,%d,%d", depth, rule, tokenIndex);
	}

	/**
	 * Index a given parse tree and adjust the min/max ranges
	 *
	 * @param tree Parser context to adjust
	 */
	private void indexAndAdjustParseTree(IncrementalParserRuleContext tree) {
		// This is a quick way of indexing the parse tree by start. We actually
		// could walk the old parse tree as the parse proceeds. This is left as
		// a future optimization. We also could just allow passing in
		// constructed maps if this turns out to be slow.
		tokenStream.fill();
		ParseTreeListener listener = new ParseTreeProcessor();
		ParseTreeWalker.DEFAULT.walk(listener, tree);
	}


	/**
	 * This class does two things: 1. Simple indexer to record the rule index and
	 * token index start of each rule. 2. Adjust the min max token ranges for any
	 * necessary offsets.
	 */
	private class ParseTreeProcessor implements ParseTreeListener {

		/**
		 * Given a token index in the old token stream, and an array of token changes,
		 * see what the new token index should be.
		 *
		 * @param oldStreamTokenIndex Token index in the old stream
		 *                            Return -1 if token does not need to change.
		 */

		int findAdjustedTokenIndex(int oldStreamTokenIndex) {
			Integer result = tokenOffsets.get(Interval.of(oldStreamTokenIndex, oldStreamTokenIndex));
			if (result == null)
				return -1;
			return oldStreamTokenIndex + result;
		}

		/**
		 * Given a token index the old stream, figure out the token it would be in the
		 * new stream and return it. If we don't need token adjustment, return nothing.
		 *
		 * @param oldTokenIndex Token index in old stream.
		 */
		private Token getAdjustedToken(int oldTokenIndex) {
			int newTokenIndex = findAdjustedTokenIndex(oldTokenIndex);
			if (newTokenIndex != -1) {
				// We filled the tokenstream before the walk.
				return tokenStream.get(newTokenIndex);
			}
			return null;
		}

		/**
		 * Adjust the minimum/maximum token index that appears in a rule context. Like
		 * other functions, this simply converts the token indexes from how they appear
		 * in the old stream to how they would appear in the new stream.
		 *
		 * @param ctx Parser context to adjust.
		 */
		private void adjustMinMax(IncrementalParserRuleContext ctx) {
			boolean changed = false;
			int newMin = ctx.getMinTokenIndex();
			Token newToken = getAdjustedToken(newMin);
			if (newToken != null) {
				newMin = newToken.getTokenIndex();
				changed = true;
			}

			int newMax = ctx.getMaxTokenIndex();
			newToken = getAdjustedToken(newMax);

			if (newToken != null) {
				newMax = newToken.getTokenIndex();
				changed = true;
			}

			if (changed) {
				ctx.setMinMaxTokenIndex(Interval.of(newMin, newMax));
			}
		}

		/**
		 * Adjust the start/stop token indexes of a rule to take into account position
		 * changes in the token stream.
		 *
		 * @param ctx The rule context to adjust the start/stop tokens of.
		 */
		private void adjustStartStop(IncrementalParserRuleContext ctx) {
			Token newToken = getAdjustedToken(ctx.start.getTokenIndex());
			if (newToken != null) {
				ctx.start = newToken;
			}

			if (ctx.stop != null) {
				newToken = getAdjustedToken(ctx.stop.getTokenIndex());
				if (newToken != null) {
					ctx.stop = newToken;
				}
			}
		}

		@Override
		public void visitTerminal(TerminalNode node) {

		}

		@Override
		public void visitErrorNode(ErrorNode node) {

		}

		/**
		 * Process each rule context we see in top-down order, adjusting min-
		 * max and start-stop tokens, as well as adding the context to the
		 * rule start map.
		 *
		 * @param ctx Context to process
		 */
		@Override
		public void enterEveryRule(ParserRuleContext ctx) {
			IncrementalParserRuleContext incCtx = (IncrementalParserRuleContext) ctx;
			// Don't bother adjusting rule contexts that we can't possibly
			// reuse. Also don't touch contexts without an epoch. They must
			// represent something the incremental parser never saw,
			// since it sets epochs on all contexts it touches.
			if (incCtx.epoch == -1)
				return;
			boolean mayNeedAdjustment = tokenOffsets != null && tokenOffsets.size() != 0;
			if (mayNeedAdjustment) {
				adjustMinMax(incCtx);
			}
			if (!ruleAffectedByTokenChanges(incCtx)) {
				if (mayNeedAdjustment) {
					adjustStartStop(incCtx);
				}
				String key = getKeyFromContext(incCtx);
				ruleStartMap.put(key, incCtx);
			}
		}

		@Override
		public void exitEveryRule(ParserRuleContext ctx) {

		}
	}
}

