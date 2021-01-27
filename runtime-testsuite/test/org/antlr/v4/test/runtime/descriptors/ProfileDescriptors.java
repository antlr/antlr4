/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class ProfileDescriptors {
	public static class APlus extends BaseParserTestDescriptor {
		public String input = "a b c";
		public String output = "{decision=0, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=0, SLL_ATNTransitions=0, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : ID+ ;
		 ID : 'a'..'z'+;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

	}
}
