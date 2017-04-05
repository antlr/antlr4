/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

/** The block that begins a closure loop. */
public final class StarBlockStartState extends BlockStartState {

	@Override
	public int getStateType() {
		return STAR_BLOCK_START;
	}
}
