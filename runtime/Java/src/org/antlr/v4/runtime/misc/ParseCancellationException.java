/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.misc;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.RecognitionException;

import java.util.concurrent.CancellationException;

/**
 * This exception is thrown to cancel a parsing operation. This exception does
 * not extend {@link RecognitionException}, allowing it to bypass the standard
 * error recovery mechanisms. {@link BailErrorStrategy} throws this exception in
 * response to a parse error.
 *
 * @author Sam Harwell
 */
public class ParseCancellationException extends CancellationException {

	public ParseCancellationException() {
	}

	public ParseCancellationException(String message) {
		super(message);
	}

	public ParseCancellationException(Throwable cause) {
		initCause(cause);
	}

	public ParseCancellationException(String message, Throwable cause) {
		super(message);
		initCause(cause);
	}

}
