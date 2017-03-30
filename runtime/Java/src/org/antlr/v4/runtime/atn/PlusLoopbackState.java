/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

/** Decision state for {@code A+} and {@code (A|B)+}.  It has two transitions:
 *  one to the loop back to start of the block and one to exit.
 */
public final class PlusLoopbackState extends DecisionState {

	@Override
	public int getStateType() {
		return PLUS_LOOP_BACK;
	}
}
