package org.antlr.v4.tool;

import org.stringtemplate.v4.ST;

public class Message {
    public ErrorType errorType;
    public Object[] args;
    public Throwable e;

    // used for location template
    public String fileName;
    public int line = -1;
    public int charPosition = -1;

    public Message() {
    }

    public Message(ErrorType errorType) {
        this.errorType = errorType;
    }

    public Message(ErrorType errorType, Object... args) {
        this(errorType);
        this.args = args;
    }

    public Message(ErrorType errorType, Throwable e, Object... args) {
        this(errorType, args);
        this.e = e;
    }

    /** Return a new template instance every time someone tries to print
     *  a Message.
     */
    public ST getMessageTemplate() {
        ST messageST = ErrorManager.getMessageTemplate(errorType);
        ST locationST = ErrorManager.getLocationFormat();
        ST reportST = ErrorManager.getReportFormat(errorType.getSeverity());
        ST messageFormatST = ErrorManager.getMessageFormat();

        if ( args!=null ) { // fill in arg1, arg2, ...
            for (int i=0; i<args.length; i++) {
                if ( i==(args.length-1) && args[i]==null ) { // don't set last if null
                    continue;
                }
                String attr = "arg";
                if ( i>0 ) attr += i + 1;
                messageST.add(attr, args[i]);
            }
        }
        if ( e!=null ) {
            messageST.add("exception", e);
            messageST.add("stackTrace", e.getStackTrace());
        }

        boolean locationValid = false;
        if (line != -1) {
            locationST.add("line", line);
            locationValid = true;
        }
        if (charPosition != -1) {
            locationST.add("column", charPosition);
            locationValid = true;
        }
        if (fileName != null) {
            locationST.add("file", fileName);
            locationValid = true;
        }

        messageFormatST.add("id", errorType.ordinal());
        messageFormatST.add("text", messageST);

        if (locationValid) reportST.add("location", locationST);
        reportST.add("message", messageFormatST);
		//((DebugST)reportST).inspect();
		//reportST.impl.dump();
        return reportST;
    }

    public String toString() {
        return getMessageTemplate().render();
    }
}
