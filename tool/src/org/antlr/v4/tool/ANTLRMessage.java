/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool;

import org.antlr.runtime.Token;
import org.stringtemplate.v4.ST;

import java.util.Arrays;

public class ANTLRMessage {
	private static final Object[] EMPTY_ARGS = new Object[0];

    private final ErrorType errorType;

    private final Object[] args;

    private final Throwable e;

    // used for location template
    public final String fileName;
    public final int line;
    public final int charPosition;

	/** Most of the time, we'll have a token such as an undefined rule ref
     *  and so this will be set.
     */
    public final Token offendingToken;

	public ANTLRMessage(ErrorType errorType) {
        this(errorType, (Throwable)null, null, null);
    }

    public ANTLRMessage(ErrorType errorType, String fileName, Token offendingToken, Object... args) {
        this(errorType, null, fileName, offendingToken, args);
	}

    public ANTLRMessage(ErrorType errorType, Throwable e, String fileName, Token offendingToken, Object... args) {
        this.errorType = errorType;
        this.e = e;
        this.fileName = fileName;
        this.args = args;
		this.offendingToken = offendingToken;
		if (offendingToken != null) {
			this.line = offendingToken.getLine();
			this.charPosition = offendingToken.getCharPositionInLine();
		} else {
			this.line = -1;
			this.charPosition = -1;
		}
    }

    public ErrorType getErrorType() {
        return errorType;
    }


    public Object[] getArgs() {
		if (args == null) {
			return EMPTY_ARGS;
		}

		return args;
    }

	public ST getMessageTemplate(boolean verbose) {
		ST messageST = new ST(getErrorType().msg);
		messageST.impl.name = errorType.name();

		messageST.add("verbose", verbose);
		Object[] args = getArgs();
		for (int i=0; i<args.length; i++) {
			String attr = "arg";
			if ( i>0 ) attr += i + 1;
			messageST.add(attr, args[i]);
		}
		if ( args.length<2 ) messageST.add("arg2", null); // some messages ref arg2

		Throwable cause = getCause();
		StackTraceElement[] stackTrace = null;
		if (cause!=null) {
			stackTrace = cause.getStackTrace();
		}

		messageST.add("exception", cause);
		messageST.add("stackTrace", stackTrace);

		return messageST;
	}


    public Throwable getCause() {
        return e;
    }

	@Override
	public String toString() {
		return "Message{" +
			   "errorType=" + getErrorType() +
			   ", args=" + Arrays.asList(getArgs()) +
			   ", e=" + getCause() +
			   ", fileName='" + fileName + '\'' +
			   ", line=" + line +
			   ", charPosition=" + charPosition +
			   '}';
	}
}
