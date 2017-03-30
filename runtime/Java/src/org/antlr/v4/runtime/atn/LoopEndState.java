/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

/** Mark the end of a * or + loop. */
public final class LoopEndState extends ATNState {
	public ATNState loopBackState;

	@Override
	public int getStateType() {
		return LOOP_END;
	}
}
