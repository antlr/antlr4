package org.antlr.v4;

import org.antlr.v4.tool.*;

import java.util.*;

public class Tool {
	public String VERSION = "4.0-"+new Date();

	public static enum OptionArgType { NONE, STRING, INT }
	public static class Option {
		String name;
		OptionArgType argType;
		Object defaultArgValue;
		String description;

		public Option(String name, String description) {
			this(name, OptionArgType.NONE, null, description);
		}

		public Option(String name, OptionArgType argType, String description) {
			this(name, argType, null, description);
		}

		public Option(String name, OptionArgType argType, Object defaultArgValue, String description) {
			this.name = name;
			this.argType = argType;
			this.defaultArgValue = defaultArgValue;
			this.description = description;
		}
	}

	public static Option[] optionDefs = {
		new Option("o", OptionArgType.STRING, ".", "specify output directory where all output is generated"),
		new Option("fo", OptionArgType.STRING, "same as -o but force even files with relative paths to dir"),
		new Option("lib", "specify location of .token files"),
		new Option("report", "print out a report about the grammar(s) processed"),
		new Option("print", "print out the grammar without actions"),
		new Option("debug", "generate a parser that emits debugging events"),
		new Option("profile", "generate a parser that computes profiling information"),
		new Option("atn", "generate rule augmented transition networks"),
		new Option("message-format", OptionArgType.STRING, "specify output style for messages"),
		new Option("version", "print the version of ANTLR and exit"),
		new Option("savelexer", "save temp lexer file created for combined grammars"),
		new Option("dbgST", "launch StringTemplate visualizer on generated code"),
	};

	protected Map<String, Object> options = new HashMap<String, Object>();

	protected String[] args;

	public ErrorManager errMgr = new ErrorManager(this);

	List<ANTLRToolListener> listeners =
		Collections.synchronizedList(new ArrayList<ANTLRToolListener>());

	/** Track separately so if someone adds a listener, it's the only one
	 *  instead of it and the default stderr listener.
	 */
	DefaultToolListener defaultListener = new DefaultToolListener(this);

	public static void main(String[] args) {
		Tool antlr = new Tool(args);
		antlr.help();
		antlr.processGrammarsOnCommandLine();

		if (antlr.errMgr.getNumErrors() > 0) {
			antlr.exit(1);
		}
		antlr.exit(0);

//		if (!exitNow) {
//			antlr.processGrammarsOnCommandLine();
//			if ( return_dont_exit ) return;
//		}
	}

	public Tool() { this(null); }

	public Tool(String[] args) {
		this.args = args;
	}

	public void processGrammarsOnCommandLine() {

	}


	public void help() {
		info("ANTLR Parser Generator  Version " + new Tool().VERSION);
		for (Option o : optionDefs) {
			String name = o.name + (o.argType!=OptionArgType.NONE? " ___" : "");
			String s = String.format(" -%-19s %s", name, o.description);
			info(s);
		}
	}

	public void addListener(ANTLRToolListener tl) {
		if ( tl!=null ) listeners.add(tl);
	}
	public void removeListener(ANTLRToolListener tl) { listeners.remove(tl); }
	public void removeListeners() { listeners.clear(); }
	public List<ANTLRToolListener> getListeners() { return listeners; }

	public void info(String msg) {
		if ( listeners.size()==0 ) {
			defaultListener.info(msg);
			return;
		}
		for (ANTLRToolListener l : listeners) l.info(msg);
	}
	public void error(ANTLRMessage msg) {
		if ( listeners.size()==0 ) {
			defaultListener.error(msg);
			return;
		}
		for (ANTLRToolListener l : listeners) l.error(msg);
	}
	public void warning(ANTLRMessage msg) {
		if ( listeners.size()==0 ) {
			defaultListener.warning(msg);
			return;
		}
		for (ANTLRToolListener l : listeners) l.warning(msg);
	}


    public void version() {
        info("ANTLR Parser Generator  Version " + new Tool().VERSION);
    }

	public void exit(int e) { System.exit(e); }

	public void panic() { throw new Error("ANTLR panic"); }

}
