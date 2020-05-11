/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime;

public interface WritableToken extends Token {
	void setText(String text);

	void setType(int ttype);

	void setLine(int line);

	void setCharPositionInLine(int pos);

	void setChannel(int channel);

	void setTokenIndex(int index);
}
