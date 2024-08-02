/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.automata.optimization;

import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.tool.Grammar;

public class ATNOptimizer {
	public static void optimize(Grammar g, ATN atn) {
		ATNOptimizerHelper helper = ATNOptimizerHelper.initialize(g, atn);

		RedundantEpsilonRemover.optimize(helper);

		int removedStatesAfterSetMerging = 0;
		if (!g.isParser()) {
			// The optimizer currently doesn't support parser's ATN
			removedStatesAfterSetMerging = SetMerger.optimize(helper);
		}

		if (removedStatesAfterSetMerging > 0) {
			// Extra clearing if the previous optimizer has removed some states
			RedundantEpsilonRemover.optimize(helper);
		}

		helper.updateAstNodes(g.ast);
		helper.compressStates();
	}

	private ATNOptimizer() {
	}
}
