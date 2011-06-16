package org.antlr.v4.tool;

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
        super(errorType, null, args);
    }
    public ToolMessage(ErrorType errorType, Throwable e, Object... args) {
        super(errorType, e, args);
    }
}
