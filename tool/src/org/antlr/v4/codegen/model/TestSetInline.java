/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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
