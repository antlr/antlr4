package org.antlr.v4.codegen;

import org.antlr.v4.automata.Label;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.Rule;
import org.stringtemplate.v4.ST;

import java.io.IOException;

/** */
public class Target {
	/** For pure strings of Java 16-bit unicode char, how can we display
	 *  it in the target language as a literal.  Useful for dumping
	 *  predicates and such that may refer to chars that need to be escaped
	 *  when represented as strings.  Also, templates need to be escaped so
	 *  that the target language can hold them as a string.
	 *
	 *  I have defined (via the constructor) the set of typical escapes,
	 *  but your Target subclass is free to alter the translated chars or
	 *  add more definitions.  This is nonstatic so each target can have
	 *  a different set in memory at same time.
	 */
	protected String[] targetCharValueEscape = new String[255];

	public Target() {
		targetCharValueEscape['\n'] = "\\n";
		targetCharValueEscape['\r'] = "\\r";
		targetCharValueEscape['\t'] = "\\t";
		targetCharValueEscape['\b'] = "\\b";
		targetCharValueEscape['\f'] = "\\f";
		targetCharValueEscape['\\'] = "\\\\";
		targetCharValueEscape['\''] = "\\'";
		targetCharValueEscape['"'] = "\\\"";
	}

	protected void genRecognizerFile(CodeGenerator generator,
									 Grammar g,
									 ST outputFileST)
		throws IOException
	{
		String fileName = generator.getRecognizerFileName();
		generator.write(outputFileST, fileName);
	}

	protected void genRecognizerHeaderFile(CodeGenerator generator,
										   Grammar g,
										   ST headerFileST,
										   String extName) // e.g., ".h"
		throws IOException
	{
		// no header file by default
	}

	/** Get a meaningful name for a token type useful during code generation.
	 *  Literals without associated names are converted to the string equivalent
	 *  of their integer values. Used to generate x==ID and x==34 type comparisons
	 *  etc...  Essentially we are looking for the most obvious way to refer
	 *  to a token type in the generated code.  If in the lexer, return the
	 *  char literal translated to the target language.  For example, ttype=10
	 *  will yield '\n' from the getTokenDisplayName method.  That must
	 *  be converted to the target languages literals.  For most C-derived
	 *  languages no translation is needed.
	 */
	public String getTokenTypeAsTargetLabel(Grammar g, int ttype) {
		if ( g.getType() == ANTLRParser.LEXER ) {
//			String name = g.getTokenDisplayName(ttype);
//			return getTargetCharLiteralFromANTLRCharLiteral(this,name);
		}
		String name = g.getTokenDisplayName(ttype);
		// If name is a literal, return the token type instead
		if ( name.charAt(0)=='\'' ) {
			return String.valueOf(ttype);
		}
		return name;
	}

	public String[] getTokenTypesAsTargetLabels(Grammar g, int[] ttypes) {
		String[] labels = new String[ttypes.length];
		for (int i=0; i<ttypes.length; i++) {
			labels[i] = getTokenTypeAsTargetLabel(g, ttypes[i]);
		}
		return labels;
	}

	/** Convert from an ANTLR char literal found in a grammar file to
	 *  an equivalent char literal in the target language.  For most
	 *  languages, this means leaving 'x' as 'x'.  Actually, we need
	 *  to escape '\u000A' so that it doesn't get converted to \n by
	 *  the compiler.  Convert the literal to the char value and then
	 *  to an appropriate target char literal.
	 *
	 *  Expect single quotes around the incoming literal.
	 *  TODO: unused and should call CharSupport.getANTLRCharLiteralForChar anyway
	 */
	public String getTargetCharLiteralCharValue(int c) {
		StringBuffer buf = new StringBuffer();
		buf.append('\'');
		if ( c<Label.MIN_CHAR_VALUE ) return "'\u0000'";
		if ( c<targetCharValueEscape.length &&
			 targetCharValueEscape[c]!=null )
		{
			buf.append(targetCharValueEscape[c]);
		}
		else if ( Character.UnicodeBlock.of((char)c)==
				  Character.UnicodeBlock.BASIC_LATIN &&
				  !Character.isISOControl((char)c) )
		{
			// normal char
			buf.append((char)c);
		}
		else {
			// must be something unprintable...use \\uXXXX
			// turn on the bit above max "\\uFFFF" value so that we pad with zeros
			// then only take last 4 digits
			String hex = Integer.toHexString(c|0x10000).toUpperCase().substring(1,5);
			buf.append("\\u");
			buf.append(hex);
		}

		buf.append('\'');
		return buf.toString();
	}

	/** Convert long to 0xNNNNNNNNNNNNNNNN by default for spitting out
	 *  with bitsets.  I.e., convert bytes to hex string.
	 */
	public String getTarget64BitStringFromValue(long word) {
		int numHexDigits = 8*2;
		StringBuffer buf = new StringBuffer(numHexDigits+2);
		buf.append("0x");
		String digits = Long.toHexString(word);
		digits = digits.toUpperCase();
		int padding = numHexDigits - digits.length();
		// pad left with zeros
		for (int i=1; i<=padding; i++) {
			buf.append('0');
		}
		buf.append(digits);
		return buf.toString();
	}
	
	public String getLoopLabel(GrammarAST ast) {
		return "loop"+ ast.token.getTokenIndex();
	}

	public String getLoopCounter(GrammarAST ast) {
		return "cnt"+ ast.token.getTokenIndex();
	}

	public String getListLabel(String label) { return label+"_list"; }
	public String getRuleFunctionContextStructName(Rule r) {
		if ( r.args==null && r.retvals==null ) return "ParserRuleContext";
		return r.name+"_ctx";
	}
	public String getRuleDynamicScopeStructName(String ruleName) { return ruleName+"_scope"; }
	public String getGlobalDynamicScopeStructName(String scopeName) { return scopeName; }

	// should be same for all refs to same token like $ID within single rule function
	public String getImplicitTokenLabel(String tokenName) { return "_t"+tokenName; }
	public String getImplicitRuleLabel(String ruleName) { return "_r"+ruleName; }

	public int getInlineTestsVsBitsetThreshold() { return 20; }
}
