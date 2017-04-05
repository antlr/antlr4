/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool;

import org.antlr.runtime.Token;

/** A generic message from the tool such as "file not found" type errors; there
 *  is no reason to create a special object for each error unlike the grammar
 *  errors, which may be rather complex.
 *
 *  Sometimes you need to pass in a filename or something to say it is "bad".
 *  Allow a generic object to be passed in and the string template can deal
 *  with just printing it or pulling a property out of it.
 */
public class ToolMessage extends ANTLRMessage {
	public ToolMessage(ErrorType errorType) {
		super(errorType);
	}
    public ToolMessage(ErrorType errorType, Object... args) {
        super(errorType, null, Token.INVALID_TOKEN, args);
    }
    public ToolMessage(ErrorType errorType, Throwable e, Object... args) {
        super(errorType, e, Token.INVALID_TOKEN, args);
    }
}
