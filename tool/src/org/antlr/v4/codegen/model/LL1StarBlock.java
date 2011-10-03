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

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.atn.StarLoopEntryState;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class LL1StarBlock extends LL1Loop {
	/** Token names for each alt 0..n-1 */
	public List<String[]> altLook;
	public String loopLabel;
	public String[] exitLook;

	public LL1StarBlock(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlockForAlt> alts) {
		super(factory, blkAST, alts);

		StarLoopEntryState star = (StarLoopEntryState)blkAST.atnState;
		this.decision = star.decision;

		/** Lookahead for each alt 1..n */
		IntervalSet[] altLookSets = factory.getGrammar().decisionLOOK.get(decision);
		IntervalSet lastLook = altLookSets[altLookSets.length-1];
		IntervalSet[] copy = new IntervalSet[altLookSets.length-1];
		System.arraycopy(altLookSets, 0, copy, 0, altLookSets.length-1); // remove last (exit) alt
		altLookSets = copy;
		altLook = getAltLookaheadAsStringLists(altLookSets);
		loopLabel = factory.getGenerator().target.getLoopLabel(blkAST);

		this.exitLook =
			factory.getGenerator().target.getTokenTypesAsTargetLabels(factory.getGrammar(), lastLook.toArray());
	}
}
