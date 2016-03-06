/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.ArrayList;
import java.util.List;

/** */
public class TestSetInline extends SrcOp {
	public int bitsetWordSize;
	public String varName;
	public Bitset[] bitsets;

	public TestSetInline(OutputModelFactory factory, GrammarAST ast, IntervalSet set, int wordSize) {
		super(factory, ast);
		bitsetWordSize = wordSize;
		Bitset[] withZeroOffset = createBitsets(factory, set, wordSize, true);
		Bitset[] withoutZeroOffset = createBitsets(factory, set, wordSize, false);
		this.bitsets = withZeroOffset.length <= withoutZeroOffset.length ? withZeroOffset : withoutZeroOffset;
		this.varName = "_la";
	}

	private static Bitset[] createBitsets(OutputModelFactory factory,
										  IntervalSet set,
										  int wordSize,
										  boolean useZeroOffset) {
		List<Bitset> bitsetList = new ArrayList<Bitset>();
		for (int ttype : set.toArray()) {
			Bitset current = !bitsetList.isEmpty() ? bitsetList.get(bitsetList.size() - 1) : null;
			if (current == null || ttype > (current.shift + wordSize-1)) {
				current = new Bitset();
				if (useZeroOffset && ttype >= 0 && ttype < wordSize-1) {
					current.shift = 0;
				}
				else {
					current.shift = ttype;
				}

				bitsetList.add(current);
			}

			current.ttypes.add(factory.getGenerator().getTarget().getTokenTypeAsTargetLabel(factory.getGrammar(), ttype));
		}

		return bitsetList.toArray(new Bitset[bitsetList.size()]);
	}

	public static final class Bitset {
		public int shift;
		public final List<String> ttypes = new ArrayList<String>();
	}
}
