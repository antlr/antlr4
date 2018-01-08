/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

/**
 *
 * @author Sam Harwell
 */
public final class BasicBlockStartState extends BlockStartState {
	@Override
	public int getStateType() {
		return BLOCK_START;
	}
}
