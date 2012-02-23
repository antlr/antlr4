/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.tree.TraceTree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParserATNPathFinder extends ParserATNSimulator<Token> {
	public ParserATNPathFinder(@Nullable Parser<Token> parser, @NotNull ATN atn) {
		super(parser, atn);
	}

	/** Given an input sequence, as a subset of the input stream, trace the path through the
	 *  ATN starting at s. The path returned includes s and the final target of the last input
	 *  symbol. If there are multiple paths through the ATN to the final state, it uses the first
	 *  method finds. This is used to figure out how input sequence is matched in more than one
	 *  way between the alternatives of a decision. It's only that decision we are concerned with
	 *  and so if there are ambiguous decisions further along, we will ignore them for the
	 *  purposes of computing the path to the final state. To figure out multiple paths for
	 *  decision, use this method  on the left edge of the alternatives of the decision in question.
	 *
	 *  TODO: I haven't figured out what to do with nongreedy decisions yet
	 *  TODO: preds. unless i create rule specific ctxs, i can't eval preds. also must eval args!
	 */
	public TraceTree trace(@NotNull ATNState s, @Nullable RuleContext<?> ctx,
								TokenStream<? extends Token> input, int start, int stop)
	{
		System.out.println("REACHES "+s.stateNumber+" start state");
		List<TraceTree> leaves = new ArrayList<TraceTree>();
		HashSet<ATNState>[] busy = new HashSet[stop-start+1];
		for (int i = 0; i < busy.length; i++) {
			busy[i] = new HashSet<ATNState>();
		}
		TraceTree path = _trace(s, ctx, ctx, input, start, start, stop, leaves, busy);
		if ( path!=null ) path.leaves = leaves;
		return path;
	}

	/** Returns true if we found path */
	public TraceTree _trace(@NotNull ATNState s, RuleContext<?> initialContext, RuleContext<?> ctx,
							TokenStream<? extends Token> input, int start, int i, int stop,
							List<TraceTree> leaves, @NotNull Set<ATNState>[] busy)
	{
		TraceTree root = new TraceTree(s);
		if ( i>stop ) {
			leaves.add(root); // track final states
			System.out.println("leaves=" + leaves);
			return root;
		}

		if ( !busy[i-start].add(s) ) {
			System.out.println("already visited "+s.stateNumber+" at input "+i+"="+input.get(i).getText());
			return null;
		}
		busy[i-start].add(s);

		System.out.println("TRACE "+s.stateNumber+" at input "+input.get(i).getText());

		if ( s instanceof RuleStopState) {
			// We hit rule end. If we have context info, use it
			if ( ctx!=null && !ctx.isEmpty() ) {
				System.out.println("stop state "+s.stateNumber+", ctx="+ctx);
				ATNState invokingState = atn.states.get(ctx.invokingState);
				RuleTransition rt = (RuleTransition)invokingState.transition(0);
				ATNState retState = rt.followState;
				return _trace(retState, initialContext, ctx.parent, input, start, i, stop, leaves, busy);
			}
			else {
				// else if we have no context info, just chase follow links (if greedy)
				System.out.println("FALLING off rule "+getRuleName(s.ruleIndex));
			}
		}

		int n = s.getNumberOfTransitions();
		boolean aGoodPath = false;
		TraceTree found = null;
		for (int j=0; j<n; j++) {
			Transition t = s.transition(j);
			if ( t.getClass() == RuleTransition.class ) {
				RuleContext<?> newContext = RuleContext.getChildContext(ctx, s.stateNumber);
				found = _trace(t.target, initialContext, newContext, input, start, i, stop, leaves, busy);
				if ( found!=null ) {aGoodPath=true; root.addChild(found);}
				continue;
			}
			if ( t instanceof PredicateTransition ) {
				found = predTransition(initialContext, ctx, input, start, i, stop, leaves, busy, root, t);
				if ( found!=null ) {aGoodPath=true; root.addChild(found);}
				continue;
			}
			if ( t.isEpsilon() ) {
				found = _trace(t.target, initialContext, ctx, input, start, i, stop, leaves, busy);
				if ( found!=null ) {aGoodPath=true; root.addChild(found);}
				continue;
			}
			if ( t.getClass() == WildcardTransition.class ) {
				System.out.println("REACHES " + t.target.stateNumber + " matching input " + input.get(i).getText());
				found = _trace(t.target, initialContext, ctx, input, start, i+1, stop, leaves, busy);
				if ( found!=null ) {aGoodPath=true; root.addChild(found);}
				continue;
			}
			IntervalSet set = t.label();
			if ( set!=null ) {
				if ( t instanceof NotSetTransition ) {
					if ( !set.contains(input.get(i).getType()) ) {
						System.out.println("REACHES " + t.target.stateNumber + " matching input " + input.get(i).getText());
						found = _trace(t.target, initialContext, ctx, input, start, i+1, stop, leaves, busy);
						if ( found!=null ) {aGoodPath=true; root.addChild(found);}
					}
				}
				else {
					if ( set.contains(input.get(i).getType()) ) {
						System.out.println("REACHES " + t.target.stateNumber + " matching input " + input.get(i).getText());
						found = _trace(t.target, initialContext, ctx, input, start, i+1, stop, leaves, busy);
						if ( found!=null ) {aGoodPath=true; root.addChild(found);}
					}
				}
			}
		}
		if ( aGoodPath ) return root; // found at least one transition leading to success
		return null;
	}

	public TraceTree predTransition(RuleContext<?> initialContext, RuleContext<?> ctx, TokenStream<? extends Token> input, int start,
									int i, int stop, List<TraceTree> leaves, Set<ATNState>[] busy,
									TraceTree root, Transition t)
	{
		SemanticContext.Predicate pred = ((PredicateTransition) t).getPredicate();
		boolean pass = false;
		if ( pred.isCtxDependent ) {
			if ( ctx instanceof ParserRuleContext && ctx==initialContext ) {
				System.out.println("eval pred "+pred+"="+pred.eval(parser, ctx));
				pass = pred.eval(parser, ctx);
			}
			else {
				pass = true; // see thru ctx dependent when out of context
			}
		}
		else {
			System.out.println("eval pred "+pred+"="+pred.eval(parser, initialContext));
			pass = pred.eval(parser, ctx);
		}
		if ( pass ) {
			return _trace(t.target, initialContext, ctx, input, start, i, stop, leaves, busy);
		}
		return null;
	}

}
