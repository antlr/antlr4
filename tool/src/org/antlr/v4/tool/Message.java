package org.antlr.v4.tool;

import org.stringtemplate.v4.ST;

public class Message {
    public ErrorType errorType;
    public Object[] args;
    public Throwable e;

    // used for location template
    public String file;
    public int line = -1;
    public int column = -1;

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
        ST reportST = ErrorManager.getReportFormat();
        ST messageFormatST = ErrorManager.getMessageFormat();

        if ( args!=null ) { // fill in arg1, arg2, ...
            for (int i=0; i<args.length; i++) {
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
        if (column != -1) {
            locationST.add("column", column);
            locationValid = true;
        }
        if (file != null) {
            locationST.add("file", file);
            locationValid = true;
        }

        messageFormatST.add("id", errorType);
        messageFormatST.add("text", messageST);

        if (locationValid) {
            reportST.add("location", locationST);
        }
        reportST.add("message", messageFormatST);
        reportST.add("type", errorType.getSeverity());
        return messageST;
    }

    public String toString() {
        return getMessageTemplate().render();
    }
}
