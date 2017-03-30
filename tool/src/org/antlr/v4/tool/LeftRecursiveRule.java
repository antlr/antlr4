/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool;

import org.antlr.v4.analysis.LeftRecursiveRuleAltInfo;
import org.antlr.v4.misc.OrderedHashMap;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.RuleAST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeftRecursiveRule extends Rule {
	public List<LeftRecursiveRuleAltInfo> recPrimaryAlts;
	public OrderedHashMap<Integer, LeftRecursiveRuleAltInfo> recOpAlts;
	public RuleAST originalAST;

	/** Did we delete any labels on direct left-recur refs? Points at ID of ^(= ID el) */
	public List<Pair<GrammarAST,String>> leftRecursiveRuleRefLabels =
		new ArrayList<Pair<GrammarAST,String>>();

	public LeftRecursiveRule(Grammar g, String name, RuleAST ast) {
		super(g, name, ast, 1);
		originalAST = ast;
		alt = new Alternative[numberOfAlts+1]; // always just one
		for (int i=1; i<=numberOfAlts; i++) alt[i] = new Alternative(this, i);
	}

	@Override
	public boolean hasAltSpecificContexts() {
		return super.hasAltSpecificContexts() || getAltLabels()!=null;
	}

	@Override
	public int getOriginalNumberOfAlts() {
		int n = 0;
		if ( recPrimaryAlts!=null ) n += recPrimaryAlts.size();
		if ( recOpAlts!=null ) n += recOpAlts.size();
		return n;
	}

	public RuleAST getOriginalAST() {
		return originalAST;
	}

	@Override
	public List<AltAST> getUnlabeledAltASTs() {
		List<AltAST> alts = new ArrayList<AltAST>();
		for (LeftRecursiveRuleAltInfo altInfo : recPrimaryAlts) {
			if (altInfo.altLabel == null) alts.add(altInfo.originalAltAST);
		}
		for (int i = 0; i < recOpAlts.size(); i++) {
			LeftRecursiveRuleAltInfo altInfo = recOpAlts.getElement(i);
			if ( altInfo.altLabel==null ) alts.add(altInfo.originalAltAST);
		}
		if ( alts.isEmpty() ) return null;
		return alts;
	}

	/** Return an array that maps predicted alt from primary decision
	 *  to original alt of rule. For following rule, return [0, 2, 4]
	 *
		e : e '*' e
		  | INT
		  | e '+' e
		  | ID
		  ;

	 *  That maps predicted alt 1 to original alt 2 and predicted 2 to alt 4.
	 *
	 *  @since 4.5.1
	 */
	public int[] getPrimaryAlts() {
		if ( recPrimaryAlts.size()==0 ) return null;
		int[] alts = new int[recPrimaryAlts.size()+1];
		for (int i = 0; i < recPrimaryAlts.size(); i++) { // recPrimaryAlts is a List not Map like recOpAlts
			LeftRecursiveRuleAltInfo altInfo = recPrimaryAlts.get(i);
			alts[i+1] = altInfo.altNum;
		}
		return alts;
	}

	/** Return an array that maps predicted alt from recursive op decision
	 *  to original alt of rule. For following rule, return [0, 1, 3]
	 *
		e : e '*' e
		  | INT
		  | e '+' e
		  | ID
		  ;

	 *  That maps predicted alt 1 to original alt 1 and predicted 2 to alt 3.
	 *
	 *  @since 4.5.1
	 */
	public int[] getRecursiveOpAlts() {
		if ( recOpAlts.size()==0 ) return null;
		int[] alts = new int[recOpAlts.size()+1];
		int alt = 1;
		for (LeftRecursiveRuleAltInfo altInfo : recOpAlts.values()) {
			alts[alt] = altInfo.altNum;
			alt++; // recOpAlts has alts possibly with gaps
		}
		return alts;
	}

	/** Get -&gt; labels from those alts we deleted for left-recursive rules. */
	@Override
	public Map<String, List<Pair<Integer, AltAST>>> getAltLabels() {
		Map<String, List<Pair<Integer, AltAST>>> labels = new HashMap<String, List<Pair<Integer, AltAST>>>();
		Map<String, List<Pair<Integer, AltAST>>> normalAltLabels = super.getAltLabels();
		if ( normalAltLabels!=null ) labels.putAll(normalAltLabels);
		if ( recPrimaryAlts!=null ) {
			for (LeftRecursiveRuleAltInfo altInfo : recPrimaryAlts) {
				if (altInfo.altLabel != null) {
					List<Pair<Integer, AltAST>> pairs = labels.get(altInfo.altLabel);
					if (pairs == null) {
						pairs = new ArrayList<Pair<Integer, AltAST>>();
						labels.put(altInfo.altLabel, pairs);
					}

					pairs.add(new Pair<Integer, AltAST>(altInfo.altNum, altInfo.originalAltAST));
				}
			}
		}
		if ( recOpAlts!=null ) {
			for (int i = 0; i < recOpAlts.size(); i++) {
				LeftRecursiveRuleAltInfo altInfo = recOpAlts.getElement(i);
				if ( altInfo.altLabel!=null ) {
					List<Pair<Integer, AltAST>> pairs = labels.get(altInfo.altLabel);
					if (pairs == null) {
						pairs = new ArrayList<Pair<Integer, AltAST>>();
						labels.put(altInfo.altLabel, pairs);
					}

					pairs.add(new Pair<Integer, AltAST>(altInfo.altNum, altInfo.originalAltAST));
				}
			}
		}
		if ( labels.isEmpty() ) return null;
		return labels;
	}
}
