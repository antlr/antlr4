package org.antlr.v4.tool;

import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DFAState;
import org.antlr.v4.automata.NFAState;
import org.antlr.v4.misc.IntSet;
import org.antlr.v4.parse.v4ParserException;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.misc.ErrorBuffer;
import org.stringtemplate.v4.misc.STMessage;

import java.net.URL;
import java.util.*;

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

    static ErrorBuffer initSTListener = new ErrorBuffer();

    static STErrorListener theDefaultSTListener =
        new STErrorListener() {
            public void compileTimeError(STMessage msg) {
                ErrorManager.internalError(msg.toString());
            }

            public void runTimeError(STMessage msg) {
                ErrorManager.internalError(msg.toString());
            }

            public void IOError(STMessage msg) {
                ErrorManager.internalError(msg.toString());
            }

            public void internalError(STMessage msg) {
                ErrorManager.internalError(msg.toString());
            }
        };
    public static final String FORMATS_DIR = "org/antlr/v4/tool/templates/messages/formats/";
    public static final String MESSAGES_DIR = "org/antlr/v4/tool/templates/messages/languages/";

    private static class ErrorState {
        public ANTLRErrorListener listener;
        public int errors;
        public int warnings;
    }

    private static ThreadLocal<ErrorState> state = new ThreadLocal<ErrorState>();

    // make sure that this class is ready to use after loading
    static {
        state.set(new ErrorState());
        setErrorListener(theDefaultErrorListener);
        org.stringtemplate.v4.misc.ErrorManager.setErrorListener(initSTListener);
        // it is inefficient to set the default locale here if another
        // piece of code is going to set the locale, but that would
        // require that a user call an init() function or something.  I prefer
        // that this class be ready to go when loaded as I'm absentminded ;)
        setLocale(Locale.getDefault());
        // try to load the message format group
        // the user might have specified one on the command line
        // if not, or if the user has given an illegal value, we will fall back to "antlr"
        setFormat("antlr");
        org.stringtemplate.v4.misc.ErrorManager.setErrorListener(theDefaultSTListener);        
    }


  
    public static ANTLRErrorListener getErrorListener() {
        return state.get().listener;
    }    

    /** Return a StringTemplate that refers to the current format used for
     * emitting messages.
     */
    public static ST getLocationFormat() {
        return format.getInstanceOf("location");
    }
    public static ST getReportFormat(ErrorSeverity severity) {
        ST st = format.getInstanceOf("report");
        ST type = messages.getInstanceOf(severity.toString());
        st.add("type", type);
        return st;

    }
    public static ST getMessageFormat() {
        return format.getInstanceOf("message");
    }
    public static boolean formatWantsSingleLineMessage() {
        return format.getInstanceOf("wantsSingleLineMessage").render().equals("true");
    }
    public static ST getMessageTemplate(ErrorType etype) {
        String msgName = etype.toString();
		return messages.getInstanceOf(msgName);
    }

    public static void resetErrorState() {        
    }

    public static void info(Object... args) {
        
    }

	public static void syntaxError(ErrorType etype,
								   String fileName,
								   Token token,
								   RecognitionException antlrException,
								   Object... args)
	{
		state.get().errors++;
		Message msg = new GrammarSyntaxMessage(etype,fileName,token,antlrException,args);
		state.get().listener.error(msg);
	}

	public static void fatalInternalError(String error, Throwable e) {
		internalError(error, e);
		throw new RuntimeException(error, e);
	}

	public static void internalError(String error, Throwable e) {
        state.get().errors++;
        StackTraceElement location = getLastNonErrorManagerCodeLocation(e);
        String msg = "Exception "+e+"@"+location+": "+error;
        System.err.println("internal error: "+msg);
    }

    public static void internalError(String error) {
        state.get().errors++;
        StackTraceElement location =
            getLastNonErrorManagerCodeLocation(new Exception());
        String msg = location+": "+error;
        System.err.println("internal error: "+msg);
    }

    /**
     * Raise a predefined message with some number of paramters for the StringTemplate but for which there
     * is no location information possible.
     * @param errorType The Message Descriptor
     * @param args The arguments to pass to the StringTemplate
     */
	public static void toolError(ErrorType errorType, Object... args) {
        state.get().errors++;
        state.get().listener.error(new ToolMessage(errorType, args));
	}

	public static String getParserErrorMessage(Parser parser, RecognitionException e) {
		String msg = null;
		if ( e instanceof NoViableAltException) {
			String t = parser.getTokenErrorDisplay(e.token);
			   String name = "<EOF>";
			if ( e.token.getType()>=0 ) name = parser.getTokenNames()[e.token.getType()];
			msg = " came as a complete surprise to me";
			msg = t+msg;
//			if ( t.toLowerCase().equals("'"+name.toLowerCase()+"'") ) {
//				msg = t+msg;
//			}
//			else {
//				msg = t+"<"+name+">"+msg;
//			}
		}
		else if ( e instanceof v4ParserException) {
			msg = ((v4ParserException)e).msg;
		}
		else {
			msg = parser.getErrorMessage(e, parser.getTokenNames());
		}
		return msg;
	}

    /**
     * Raise a predefined message with some number of parameters for the StringTemplate
     * with error information supplied explicitly.
     *
     * @param errorType The Message Descriptor
     * @param args The arguments to pass to the StringTemplate
	public static void toolError(ErrorType errorType, int line, int column, int absOffset, int endLine, int endColumn, int endAbsOffset, Object... args) {
	}
     */

    /**
     * Raise a predefined message with some number of paramters for the StringTemplate, for which there is a CommonToken
     * that can give us the location information.
     * @param errorType The message descriptor.
     * @param t     The token that contains our location information
     * @param args  The varargs array of values that will be set in the StrngTemplate as arg0, arg1, ... argn
    public static void error(ErrorType errorType, CommonToken t, Object... args) {
	}
     */

    /**
     * Construct a message when we have a node stream and AST node that we can extract location
     * from and possbily some arguments for the StringTemplate that will construct this message.
    public static void error(ErrorType errorType, CommonTree node, Object... args) {
	}
     */

    /*
    public static void grammarError(ErrorType etype,
                                    Grammar g,
                                    Token token,
                                    Object... args)
    {
        state.get().errors++;
        Message msg = new GrammarSemanticsMessage(etype,g,token,args);
        state.get().listener.error(msg);
    }
     */

    public static void grammarError(ErrorType etype,
                                    String fileName,
                                    Token token,
                                    Object... args)
    {
        state.get().errors++;
        Message msg = new GrammarSemanticsMessage(etype,fileName,token,args);
        state.get().listener.error(msg);
    }

    public static void grammarWarning(ErrorType etype,
                                      String fileName,
                                      Token token,
                                      Object... args)
    {
        state.get().warnings++;
        Message msg = new GrammarSemanticsMessage(etype,fileName,token,args);
        state.get().listener.warning(msg);
    }

	public static void ambiguity(String fileName,
								 DFAState d,
								 List<Integer> conflictingAlts,
								 String input,
								 LinkedHashMap<Integer,List<Token>> conflictingPaths,
								 boolean hasPredicateBlockedByAction)
	{
		state.get().warnings++;
		AmbiguityMessage msg =
			new AmbiguityMessage(ErrorType.AMBIGUITY,fileName,
								 d,
								 conflictingAlts,
								 input,
								 conflictingPaths,
								 hasPredicateBlockedByAction);
		state.get().listener.warning(msg);
	}

	public static void unreachableAlts(String fileName,
									   DFA dfa,
									   Collection<Integer> unreachableAlts)
	{
		//System.err.println("unreachable="+unreachableAlts);
		state.get().errors++;
		UnreachableAltsMessage msg =
			new UnreachableAltsMessage(ErrorType.UNREACHABLE_ALTS,
									   fileName,
									   dfa,
									   unreachableAlts);
		state.get().listener.error(msg);
	}

	public static void insufficientPredicates(String fileName,
											  DFAState d,
											  String input,
											  Map<Integer, Set<Token>> incompletelyCoveredAlts,
											  boolean hasPredicateBlockedByAction)
	{
		state.get().warnings++;
		InsufficientPredicatesMessage msg =
			new InsufficientPredicatesMessage(ErrorType.INSUFFICIENT_PREDICATES,
											  fileName,
											  d,
											  input,
											  incompletelyCoveredAlts,
											  hasPredicateBlockedByAction);
		state.get().listener.warning(msg);
	}

	public static void leftRecursionCycles(String fileName, Collection cycles) {
		state.get().errors++;
		Message msg = new LeftRecursionCyclesMessage(fileName, cycles);
		state.get().listener.error(msg);
	}

	public static void recursionOverflow(String fileName,
										 DFA dfa, NFAState s, int altNum, int depth) {
		state.get().errors++;
		Message msg = new RecursionOverflowMessage(fileName, dfa, s, altNum, depth);
		state.get().listener.error(msg);
	}

	public static void multipleRecursiveAlts(String fileName,
											 DFA dfa, IntSet recursiveAltSet) {
		state.get().errors++;
		Message msg = new MultipleRecursiveAltsMessage(fileName, dfa, recursiveAltSet);
		state.get().listener.error(msg);
	}

	public static void analysisTimeout() {
		state.get().errors++;
		Message msg = new AnalysisTimeoutMessage();
		state.get().listener.error(msg);
	}

    public static int getNumErrors() {
        return state.get().errors;
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

    /** In general, you'll want all errors to go to a single spot.
     *  However, in a GUI, you might have two frames up with two
     *  different grammars.  Two threads might launch to process the
     *  grammars--you would want errors to go to different objects
     *  depending on the thread.  I store a single listener per
     *  thread.
     */
    public static void setErrorListener(ANTLRErrorListener l) {
        state.get().listener = l;
    }

    // S U P P O R T  C O D E

    /** We really only need a single locale for entire running ANTLR code
     *  in a single VM.  Only pay attention to the language, not the country
     *  so that French Canadians and French Frenchies all get the same
     *  template file, fr.stg.  Just easier this way.
     */
    public static void setLocale(Locale locale) {
        ErrorManager.locale = locale;
        String language = locale.getLanguage();
        String fileName = MESSAGES_DIR +language+".stg";
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(fileName);
        if ( url==null ) {
            cl = ErrorManager.class.getClassLoader();
            url = cl.getResource(fileName);
        }
        if ( url==null && language.equals(Locale.US.getLanguage()) ) {
            rawError("ANTLR installation corrupted; cannot find English messages file "+fileName);
            panic();
        }
        else if ( url==null ) {
            //rawError("no such locale file "+fileName+" retrying with English locale");
            setLocale(Locale.US); // recurse on this rule, trying the US locale
            return;
        }

        messages = new STGroupFile(fileName, "UTF-8");
		messages.debug = true;		
        messages.load();
        if ( initSTListener.errors.size()>0 ) {
            rawError("ANTLR installation corrupted; can't load messages format file:\n"+
                     initSTListener.toString());
            panic();
        }

        boolean messagesOK = verifyMessages();
        if ( !messagesOK && language.equals(Locale.US.getLanguage()) ) {
            rawError("ANTLR installation corrupted; English messages file "+language+".stg incomplete");
            panic();
        }
        else if ( !messagesOK ) {
            setLocale(Locale.US); // try US to see if that will work
        }
    }

    /** The format gets reset either from the Tool if the user supplied a command line option to that effect
     *  Otherwise we just use the default "antlr".
     */
    public static void setFormat(String formatName) {
        ErrorManager.formatName = formatName;
        String fileName = FORMATS_DIR +formatName+".stg";
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

        format = new STGroupFile(fileName, "UTF-8");
		format.debug = true;
        format.load();

        if ( initSTListener.errors.size()>0 ) {
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

    /** Use reflection to find list of MSG_ fields and then verify a
     *  template exists for each one from the locale's group.
     */
    protected static boolean verifyMessages() {
        boolean ok = true;
        ErrorType[] errors = ErrorType.values();
        for (int i = 0; i < errors.length; i++) {
            ErrorType e = errors[i];
            if ( !messages.isDefined(e.toString()) ) {
                System.err.println("Message "+e.toString()+" in locale "+
                                   locale+" not found");
                ok = false;
            }
        }
        // check for special templates
        if (!messages.isDefined("WARNING")) {
            System.err.println("Message template 'warning' not found in locale "+ locale);
            ok = false;
        }
        if (!messages.isDefined("ERROR")) {
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
        // can't call tool.panic since there may be multiple tools; just
        // one error manager
        throw new Error("ANTLR ErrorManager panic");
    }
}
