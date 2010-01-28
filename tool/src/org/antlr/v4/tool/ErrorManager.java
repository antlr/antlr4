package org.antlr.v4.tool;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.tool.ToolMessage;
import org.stringtemplate.ST;
import org.stringtemplate.STGroup;

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

    public static void resetErrorState() {        
    }

    public static void info(Object... args) {
        
    }

    /**
     * Raise a predefined message with some number of paramters for the StringTemplate but for which there
     * is no location information possible.
     * @param msg The Message Descriptor
     * @param args The arguments to pass to the StringTemplate
     */
	public static void msg(Msg msg, Object... args) {
	}

    /**
     * Raise a predefined message with some number of parameters for the StringTemplate
     * with error information supplied explicitly.
     *
     * @param msg The Message Descriptor
     * @param args The arguments to pass to the StringTemplate
     */
	public static void msg(Msg msg, int line, int column, int absOffset, int endLine, int endColumn, int endAbsOffset, Object... args) {
	}

    /**
     * Raise a predefined message with some number of paramters for the StringTemplate, for which there is a CommonToken
     * that can give us the location information.
     * @param msg The message descriptor.
     * @param t     The token that contains our location information
     * @param args  The varargs array of values that will be set in the StrngTemplate as arg0, arg1, ... argn
     */
    public static void msg(Msg msg, CommonToken t, Object... args) {
	}

    /**
     * Construct a message when we have a node stream and AST node that we can extract location
     * from and possbily some arguments for the StringTemplate that will construct this message.
     *
     * @param msg The message descriptor
     * @param node  The node that gives us the information we need
     * @param args  The varargs array of values that will be set in the StrngTemplate as arg0, arg1, ... argn
     */
    public static void msg(Msg msg, CommonTree node, Object... args) {
	}

    /** Process a new message by sending it on to the error listener associated with the current thread
     *  and recording any information we need in the error state for the current thread.
     */
    private static void processMessage() {
    }

    public static int getNumErrors() {
        return 0;
    }

}
