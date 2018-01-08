/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.ParserInterpreter;
import org.antlr.v4.runtime.dfa.DFA;

public final class StarLoopEntryState extends DecisionState {
	public StarLoopbackState loopBackState;

	/**
	 * Indicates whether this state can benefit from a precedence DFA during SLL
	 * decision making.
	 *
	 * <p>This is a computed property that is calculated during ATN deserialization
	 * and stored for use in {@link ParserATNSimulator} and
	 * {@link ParserInterpreter}.</p>
	 *
	 * @see DFA#isPrecedenceDfa()
	 */
	public boolean isPrecedenceDecision;

	@Override
	public int getStateType() {
		return STAR_LOOP_ENTRY;
	}
}
