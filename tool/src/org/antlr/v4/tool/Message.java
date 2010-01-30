package org.antlr.v4.tool;

import org.stringtemplate.ST;

/** */
public class Message {
    // msgST is the actual text of the message
    public ST msgST;
    // these are for supporting different output formats
    public ST locationST;
    public ST reportST;
    public ST messageFormatST;

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
        return ErrorManager.getMessageTemplate(errorType);
    }

    public String toString() {
        // setup the location
        locationST = ErrorManager.getLocationFormat();
        reportST = ErrorManager.getReportFormat();
        messageFormatST = ErrorManager.getMessageFormat();
        ST messageST = getMessageTemplate();

        if ( args!=null ) { // fill in arg1, arg2, ...
            for (int i=0; i<args.length; i++) {
                messageST.add("args"+(i+1), args[i]);
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

        return reportST.toString();
    }

}
