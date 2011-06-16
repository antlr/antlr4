package org.antlr.v4.tool;

import java.util.Arrays;

public class ANTLRMessage {
    public ErrorType errorType;
    public Object[] args;
    public Throwable e;

    // used for location template
    public String fileName;
    public int line = -1;
    public int charPosition = -1;

    public ANTLRMessage() {
    }

    public ANTLRMessage(ErrorType errorType) {
        this.errorType = errorType;
    }

    public ANTLRMessage(ErrorType errorType, Object... args) {
        this(errorType);
        this.args = args;
    }

    public ANTLRMessage(ErrorType errorType, Throwable e, Object... args) {
        this(errorType, args);
        this.e = e;
    }

	@Override
	public String toString() {
		return "Message{" +
			   "errorType=" + errorType +
			   ", args=" + (args == null ? null : Arrays.asList(args)) +
			   ", e=" + e +
			   ", fileName='" + fileName + '\'' +
			   ", line=" + line +
			   ", charPosition=" + charPosition +
			   '}';
	}
}
