package org.antlr.v4.tool;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.CommonTree;
import org.stringtemplate.ST;
import org.stringtemplate.STGroup;

import java.lang.reflect.Field;
import java.util.Locale;

/** Defines all the errors ANTLR can generator for both the tool and for
 *  issues with a grammar.
 *
 *  Here is a list of language names:
 *
 *  http://ftp.ics.uci.edu/pub/ietf/http/related/iso639.txt
 *
 *  Here is a list of country names:
 *
 *  http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html
 *
 *  I use constants not strings to identify messages as the compiler will
 *  find any errors/mismatches rather than leaving a mistyped string in
 *  the code to be found randomly in the future.  Further, Intellij can
 *  do field name expansion to save me some typing.  I have to map
 *  int constants to template names, however, which could introduce a mismatch.
 *  Someone could provide a .stg file that had a template name wrong.  When
 *  I load the group, then, I must verify that all messages are there.
 *
 *  This is essentially the functionality of the resource bundle stuff Java
 *  has, but I don't want to load a property file--I want to load a template
 *  group file and this is so simple, why mess with their junk.
 *
 *  I use the default Locale as defined by java to compute a group file name
 *  in the org/antlr/tool/templates/messages dir called en_US.stg and so on.
 *
 *  Normally we want to use the default locale, but often a message file will
 *  not exist for it so we must fall back on the US local.
 *
 *  During initialization of this class, all errors go straight to System.err.
 *  There is no way around this.  If I have not set up the error system, how
 *  can I do errors properly?  For example, if the string template group file
 *  full of messages has an error, how could I print to anything but System.err?
 */
public class ErrorManager {
    /** The group of templates that represent all possible ANTLR errors. */
    private static STGroup messages;

    /** The group of templates that represent the current message format. */
    private static STGroup format;

    /** Messages should be sensitive to the locale. */
    private static Locale locale;
    private static String formatName;

    /** From a msgID how can I get the name of the template that describes
     *  the error or warning?
     */
    private static String[] idToMessageTemplateName = new String[ErrorType.values().length];

    static ANTLRErrorListener theDefaultErrorListener = new ANTLRErrorListener() {
        public void info(String msg) {
            if (formatWantsSingleLineMessage()) {
                msg = msg.replaceAll("\n", " ");
            }
            System.err.println(msg);
        }

        public void error(Message msg) {
            String outputMsg = msg.toString();
            if (formatWantsSingleLineMessage()) {
                outputMsg = outputMsg.replaceAll("\n", " ");
            }
            System.err.println(outputMsg);
        }

        public void warning(Message msg) {
            String outputMsg = msg.toString();
            if (formatWantsSingleLineMessage()) {
                outputMsg = outputMsg.replaceAll("\n", " ");
            }
            System.err.println(outputMsg);
        }
    };

    public static ANTLRErrorListener getErrorListener() {
        return theDefaultErrorListener;
    }    

    /** Return a StringTemplate that refers to the current format used for
     * emitting messages.
     */
    public static ST getLocationFormat() {
        return format.getInstanceOf("location");
    }
    public static ST getReportFormat() {
        return format.getInstanceOf("report");
    }
    public static ST getMessageFormat() {
        return format.getInstanceOf("message");
    }
    public static boolean formatWantsSingleLineMessage() {
        return format.getInstanceOf("wantsSingleLineMessage").toString().equals("true");
    }
    public static ST getMessageTemplate(ErrorType etype) {
        String msgName = idToMessageTemplateName[etype.ordinal()];
		return messages.getInstanceOf(msgName);
    }

    public static void resetErrorState() {        
    }

    public static void info(Object... args) {
        
    }

    public static void internalError(String error, Throwable e) {
        StackTraceElement location = getLastNonErrorManagerCodeLocation(e);
        String msg = "Exception "+e+"@"+location+": "+error;
        theDefaultErrorListener.error(new ToolMessage(ErrorType.INTERNAL_ERROR, msg));
    }

    public static void internalError(String error) {
        StackTraceElement location =
            getLastNonErrorManagerCodeLocation(new Exception());
        String msg = location+": "+error;
        theDefaultErrorListener.error(new ToolMessage(ErrorType.INTERNAL_ERROR, msg));
    }


    /**
     * Raise a predefined message with some number of paramters for the StringTemplate but for which there
     * is no location information possible.
     * @param errorType The Message Descriptor
     * @param args The arguments to pass to the StringTemplate
     */
	public static void error(ErrorType errorType, Object... args) {
	}

    /**
     * Raise a predefined message with some number of parameters for the StringTemplate
     * with error information supplied explicitly.
     *
     * @param errorType The Message Descriptor
     * @param args The arguments to pass to the StringTemplate
     */
	public static void error(ErrorType errorType, int line, int column, int absOffset, int endLine, int endColumn, int endAbsOffset, Object... args) {
	}

    /**
     * Raise a predefined message with some number of paramters for the StringTemplate, for which there is a CommonToken
     * that can give us the location information.
     * @param errorType The message descriptor.
     * @param t     The token that contains our location information
     * @param args  The varargs array of values that will be set in the StrngTemplate as arg0, arg1, ... argn
     */
    public static void error(ErrorType errorType, CommonToken t, Object... args) {
	}

    /**
     * Construct a message when we have a node stream and AST node that we can extract location
     * from and possbily some arguments for the StringTemplate that will construct this message.
     *
     * @param errorType The message descriptor
     * @param node  The node that gives us the information we need
     * @param args  The varargs array of values that will be set in the StrngTemplate as arg0, arg1, ... argn
     */
    public static void error(ErrorType errorType, CommonTree node, Object... args) {
	}

    /** Process a new message by sending it on to the error listener associated with the current thread
     *  and recording any information we need in the error state for the current thread.
     */
    private static void processMessage() {
    }

    public static int getNumErrors() {
        return 0;
    }

    /** Return first non ErrorManager code location for generating messages */
    private static StackTraceElement getLastNonErrorManagerCodeLocation(Throwable e) {
        StackTraceElement[] stack = e.getStackTrace();
        int i = 0;
        for (; i < stack.length; i++) {
            StackTraceElement t = stack[i];
            if ( t.toString().indexOf("ErrorManager")<0 ) {
                break;
            }
        }
        StackTraceElement location = stack[i];
        return location;
    }

    // S U P P O R T  C O D E

    protected static boolean initIdToMessageNameMapping() {
        // make sure a message exists, even if it's just to indicate a problem
        for (int i = 0; i < idToMessageTemplateName.length; i++) {
            idToMessageTemplateName[i] = "INVALID MESSAGE ID: "+i;
        }
        // get list of fields and use it to fill in idToMessageTemplateName mapping
        Field[] fields = ErrorManager.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            String fieldName = f.getName();
            if ( !fieldName.startsWith("MSG_") ) {
                continue;
            }
            String templateName =
                fieldName.substring("MSG_".length(),fieldName.length());
            int msgID = 0;
            try {
                // get the constant value from this class object
                msgID = f.getInt(ErrorManager.class);
            }
            catch (IllegalAccessException iae) {
                System.err.println("cannot get const value for "+f.getName());
                continue;
            }
            if ( fieldName.startsWith("MSG_") ) {
                idToMessageTemplateName[msgID] = templateName;
            }
        }
        return true;
    }

    /** Use reflection to find list of MSG_ fields and then verify a
     *  template exists for each one from the locale's group.
     */
    protected static boolean verifyMessages() {
        boolean ok = true;
        Field[] fields = ErrorManager.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            String fieldName = f.getName();
            String templateName =
                fieldName.substring("MSG_".length(),fieldName.length());
            if ( fieldName.startsWith("MSG_") ) {
                if ( !messages.isDefined(templateName) ) {
                    System.err.println("Message "+templateName+" in locale "+
                                       locale+" not found");
                    ok = false;
                }
            }
        }
        // check for special templates
        if (!messages.isDefined("warning")) {
            System.err.println("Message template 'warning' not found in locale "+ locale);
            ok = false;
        }
        if (!messages.isDefined("error")) {
            System.err.println("Message template 'error' not found in locale "+ locale);
            ok = false;
        }
        return ok;
    }

    /** Verify the message format template group */
    protected static boolean verifyFormat() {
        boolean ok = true;
        if (!format.isDefined("location")) {
            System.err.println("Format template 'location' not found in " + formatName);
            ok = false;
        }
        if (!format.isDefined("message")) {
            System.err.println("Format template 'message' not found in " + formatName);
            ok = false;
        }
        if (!format.isDefined("report")) {
            System.err.println("Format template 'report' not found in " + formatName);
            ok = false;
        }
        return ok;
    }

    /** If there are errors during ErrorManager init, we have no choice
     *  but to go to System.err.
     */
    static void rawError(String msg) {
        System.err.println(msg);
    }

    static void rawError(String msg, Throwable e) {
        rawError(msg);
        e.printStackTrace(System.err);
    }

    public static void panic() {
        throw new Error("ANTLR ErrorManager panic");
    }
}
