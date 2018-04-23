/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool;

import org.antlr.v4.Tool;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.misc.ErrorBuffer;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public class ErrorManager {
	public static final String FORMATS_DIR = "org/antlr/v4/tool/templates/messages/formats/";

	public Tool tool;
	public int errors;
	public int warnings;

	/** All errors that have been generated */
	public Set<ErrorType> errorTypes = EnumSet.noneOf(ErrorType.class);

    /** The group of templates that represent the current message format. */
    STGroup format;

    String formatName;

    ErrorBuffer initSTListener = new ErrorBuffer();

	public ErrorManager(Tool tool) {
		this.tool = tool;
	}

	public void resetErrorState() {
		errors = 0;
		warnings = 0;
	}

	public ST getMessageTemplate(ANTLRMessage msg) {
		ST messageST = msg.getMessageTemplate(tool.longMessages);
		ST locationST = getLocationFormat();
		ST reportST = getReportFormat(msg.getErrorType().severity);
		ST messageFormatST = getMessageFormat();

		boolean locationValid = false;
		if (msg.line != -1) {
			locationST.add("line", msg.line);
			locationValid = true;
		}
		if (msg.charPosition != -1) {
			locationST.add("column", msg.charPosition);
			locationValid = true;
		}
		if (msg.fileName != null) {
			String displayFileName = msg.fileName;
			if (format.equals("antlr")) {
				// Don't show path to file in messages in ANTLR format;
				// they're too long.
				File f = new File(msg.fileName);
				if ( f.exists() ) {
					displayFileName = f.getName();
				}
			}
			else {
				// For other message formats, use the full filename in the
				// message.  This assumes that these formats are intended to
				// be parsed by IDEs, and so they need the full path to
				// resolve correctly.
			}
			locationST.add("file", displayFileName);
			locationValid = true;
		}

		messageFormatST.add("id", msg.getErrorType().code);
		messageFormatST.add("text", messageST);

		if (locationValid) reportST.add("location", locationST);
		reportST.add("message", messageFormatST);
		//((DebugST)reportST).inspect();
//		reportST.impl.dump();
		return reportST;
	}

    /** Return a StringTemplate that refers to the current format used for
     * emitting messages.
     */
    public ST getLocationFormat() {
        return format.getInstanceOf("location");
    }

    public ST getReportFormat(ErrorSeverity severity) {
        ST st = format.getInstanceOf("report");
        st.add("type", severity.getText());
        return st;
    }

    public ST getMessageFormat() {
        return format.getInstanceOf("message");
    }
    public boolean formatWantsSingleLineMessage() {
        return format.getInstanceOf("wantsSingleLineMessage").render().equals("true");
    }

	public void info(String msg) { tool.info(msg); }

	public void syntaxError(ErrorType etype,
								   String fileName,
								   org.antlr.runtime.Token token,
								   org.antlr.runtime.RecognitionException antlrException,
								   Object... args)
	{
		ANTLRMessage msg = new GrammarSyntaxMessage(etype,fileName,token,antlrException,args);
		emit(etype, msg);
	}

	public static void fatalInternalError(String error, Throwable e) {
		internalError(error, e);
		throw new RuntimeException(error, e);
	}

	public static void internalError(String error, Throwable e) {
        StackTraceElement location = getLastNonErrorManagerCodeLocation(e);
		internalError("Exception "+e+"@"+location+": "+error);
    }

    public static void internalError(String error) {
        StackTraceElement location =
            getLastNonErrorManagerCodeLocation(new Exception());
        String msg = location+": "+error;
        System.err.println("internal error: "+msg);
    }

    /**
     * Raise a predefined message with some number of parameters for the StringTemplate but for which there
     * is no location information possible.
     * @param errorType The Message Descriptor
     * @param args The arguments to pass to the StringTemplate
     */
	public void toolError(ErrorType errorType, Object... args) {
		toolError(errorType, null, args);
	}

	public void toolError(ErrorType errorType, Throwable e, Object... args) {
		ToolMessage msg = new ToolMessage(errorType, e, args);
		emit(errorType, msg);
	}

    public void grammarError(ErrorType etype,
							 String fileName,
							 org.antlr.runtime.Token token,
							 Object... args)
	{
        ANTLRMessage msg = new GrammarSemanticsMessage(etype,fileName,token,args);
		emit(etype, msg);

	}

	public void leftRecursionCycles(String fileName, Collection<? extends Collection<Rule>> cycles) {
		errors++;
		ANTLRMessage msg = new LeftRecursionCyclesMessage(fileName, cycles);
		tool.error(msg);
	}

    public int getNumErrors() {
        return errors;
    }

    /** Return first non ErrorManager code location for generating messages */
    private static StackTraceElement getLastNonErrorManagerCodeLocation(Throwable e) {
        StackTraceElement[] stack = e.getStackTrace();
        int i = 0;
        for (; i < stack.length; i++) {
            StackTraceElement t = stack[i];
            if (!t.toString().contains("ErrorManager")) {
                break;
            }
        }
        StackTraceElement location = stack[i];
        return location;
    }

    // S U P P O R T  C O D E

	@SuppressWarnings("fallthrough")
	public void emit(ErrorType etype, ANTLRMessage msg) {
		switch ( etype.severity ) {
			case WARNING_ONE_OFF:
				if ( errorTypes.contains(etype) ) break;
				// fall thru
			case WARNING:
				warnings++;
				tool.warning(msg);
				break;
			case ERROR_ONE_OFF:
				if ( errorTypes.contains(etype) ) break;
				// fall thru
			case ERROR:
				errors++;
				tool.error(msg);
				break;
		}
		errorTypes.add(etype);
	}

    /** The format gets reset either from the Tool if the user supplied a command line option to that effect
     *  Otherwise we just use the default "antlr".
     */
    public void setFormat(String formatName) {
        this.formatName = formatName;
        String fileName = FORMATS_DIR +formatName+STGroup.GROUP_FILE_EXTENSION;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(fileName);
        if ( url==null ) {
            cl = ErrorManager.class.getClassLoader();
            url = cl.getResource(fileName);
        }
        if ( url==null && formatName.equals("antlr") ) {
            rawError("ANTLR installation corrupted; cannot find ANTLR messages format file "+fileName);
            panic();
        }
        else if ( url==null ) {
            rawError("no such message format file "+fileName+" retrying with default ANTLR format");
            setFormat("antlr"); // recurse on this rule, trying the default message format
            return;
        }
        format = new STGroupFile(url, "UTF-8", '<', '>');
        format.load();

        if ( !initSTListener.errors.isEmpty() ) {
            rawError("ANTLR installation corrupted; can't load messages format file:\n"+
                     initSTListener.toString());
            panic();
        }

        boolean formatOK = verifyFormat();
        if ( !formatOK && formatName.equals("antlr") ) {
            rawError("ANTLR installation corrupted; ANTLR messages format file "+formatName+".stg incomplete");
            panic();
        }
        else if ( !formatOK ) {
            setFormat("antlr"); // recurse on this rule, trying the default message format
        }
    }

    /** Verify the message format template group */
    protected boolean verifyFormat() {
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

	public void panic(ErrorType errorType, Object... args) {
		ToolMessage msg = new ToolMessage(errorType, args);
		ST msgST = getMessageTemplate(msg);
		String outputMsg = msgST.render();
		if ( formatWantsSingleLineMessage() ) {
			outputMsg = outputMsg.replace('\n', ' ');
		}
		panic(outputMsg);
	}

	public static void panic(String msg) {
		rawError(msg);
		panic();
	}

    public static void panic() {
        // can't call tool.panic since there may be multiple tools; just
        // one error manager
        throw new Error("ANTLR ErrorManager panic");
    }
}
