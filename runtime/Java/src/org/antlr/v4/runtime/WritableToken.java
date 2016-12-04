/*
 * Copyright (c) 2012 The ANTLR Project Authors. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime;

public interface WritableToken extends Token {
	public void setText(String text);

	public void setType(int ttype);

	public void setLine(int line);

	public void setCharPositionInLine(int pos);

	public void setChannel(int channel);

	public void setTokenIndex(int index);
}
