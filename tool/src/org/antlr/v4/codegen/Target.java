/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.codegen;

import org.antlr.v4.codegen.model.RuleFunction;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.GrammarAST;
import org.stringtemplate.v4.ST;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** */
public class Target {
	/** For pure strings of Java 16-bit Unicode char, how can we display
	 *  it in the target language as a literal.  Useful for dumping
	 *  predicates and such that may refer to chars that need to be escaped
	 *  when represented as strings.  Also, templates need to be escaped so
	 *  that the target language can hold them as a string.
	 *  <p/>
	 *  I have defined (via the constructor) the set of typical escapes,
	 *  but your {@link Target} subclass is free to alter the translated chars
	 *  or add more definitions.  This is non-static so each target can have
	 *  a different set in memory at same time.
	 */
	protected String[] targetCharValueEscape = new String[255];

	public CodeGenerator gen;

	/** Avoid grammar symbols in this set to prevent conflicts in gen'd code. */
	public Set<String> badWords = new HashSet<String>();

	public static String[] javaKeywords = {
		"abstract", "assert", "boolean", "break", "byte", "case", "catch",
		"char", "class", "const", "continue", "default", "do", "double", "else",
		"enum", "extends", "false", "final", "finally", "float", "for",
		"if", "implements", "import", "instanceof", "int", "interface",
		"long", "native", "new", "null", "package", "private", "protected",
		"public", "return", "short", "static", "strictfp", "super", "switch",
		"synchronized", "this", "throw", "throws", "transient", "true", "try",
		"void", "volatile", "while"
	};

	public Target(CodeGenerator gen) {
		targetCharValueEscape['\n'] = "\\n";
		targetCharValueEscape['\r'] = "\\r";
		targetCharValueEscape['\t'] = "\\t";
		targetCharValueEscape['\b'] = "\\b";
		targetCharValueEscape['\f'] = "\\f";
		targetCharValueEscape['\\'] = "\\\\";
		targetCharValueEscape['\''] = "\\'";
		targetCharValueEscape['"'] = "\\\"";
		this.gen = gen;
		addBadWords();
	}

	public void addBadWords() {
		badWords.addAll(Arrays.asList(javaKeywords));
		badWords.add("rule");
		badWords.add("parserRule");
	}

	protected void genFile(Grammar g,
						   ST outputFileST,
						   String fileName)
	{
		gen.write(outputFileST, fileName);
	}

	protected void genListenerFile(Grammar g,
								   ST outputFileST)
	{
		String fileName = gen.getListenerFileName();
		gen.write(outputFileST, fileName);
	}

	protected void genRecognizerHeaderFile(Grammar g,
										   ST headerFileST,
										   String extName) // e.g., ".h"
	{
		// no header file by default
	}

	/** Get a meaningful name for a token type useful during code generation.
	 *  Literals without associated names are converted to the string equivalent
	 *  of their integer values. Used to generate x==ID and x==34 type comparisons
	 *  etc...  Essentially we are looking for the most obvious way to refer
	 *  to a token type in the generated code.
	 */
	public String getTokenTypeAsTargetLabel(Grammar g, int ttype) {
		String name = g.getTokenDisplayName(ttype);
		// If name is a literal, return the token type instead
		if ( name==null || name.charAt(0)=='\'' ) {
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
		StringBuilder buf = new StringBuilder();
		buf.append('\'');
		if ( c< Lexer.MIN_CHAR_VALUE ) return "'\u0000'";
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
		StringBuilder buf = new StringBuilder(numHexDigits+2);
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

	/** Given a random string of Java unicode chars, return a new string with
	 *  optionally appropriate quote characters for target language and possibly
	 *  with some escaped characters.  For example, if the incoming string has
	 *  actual newline characters, the output of this method would convert them
	 *  to the two char sequence \n for Java, C, C++, ...  The new string has
	 *  double-quotes around it as well.  Example String in memory:
	 *
	 *     a"[newlinechar]b'c[carriagereturnchar]d[tab]e\f
	 *
	 *  would be converted to the valid Java s:
	 *
	 *     "a\"\nb'c\rd\te\\f"
	 *
	 *  or
	 *
	 *     a\"\nb'c\rd\te\\f
	 *
	 *  depending on the quoted arg.
	 */
	public String getTargetStringLiteralFromString(String s, boolean quoted) {
		if ( s==null ) {
			return null;
		}

		StringBuilder buf = new StringBuilder();
		if ( quoted ) {
			buf.append('"');
		}
		for (int i=0; i<s.length(); i++) {
			int c = s.charAt(i);
			if ( c!='\'' && // don't escape single quotes in strings for java
				 c<targetCharValueEscape.length &&
				 targetCharValueEscape[c]!=null )
			{
				buf.append(targetCharValueEscape[c]);
			}
			else {
				buf.append((char)c);
			}
		}
		if ( quoted ) {
			buf.append('"');
		}
		return buf.toString();
	}

	public String getTargetStringLiteralFromString(String s) {
		return getTargetStringLiteralFromString(s, true);
	}

	/**
	 * Convert from an ANTLR string literal found in a grammar file to an
	 * equivalent string literal in the target language. For Java, this is the
	 * translation {@code 'a\n"'} &rarr; {@code "a\n\""}. Expect single quotes
	 * around the incoming literal. Just flip the quotes and replace double
	 * quotes with {@code \"}.
	 * <p/>
	 * Note that we have decided to allow people to use '\"' without penalty, so
	 * we must build the target string in a loop as {@link Utils#replace} cannot
	 * handle both {@code \"} and {@code "} without a lot of messing around.
	 */
	public String getTargetStringLiteralFromANTLRStringLiteral(
		CodeGenerator generator,
		String literal, boolean addQuotes)
	{
		StringBuilder sb = new StringBuilder();
		String is = literal;

        if ( addQuotes ) sb.append('"');

        for (int i = 1; i < is.length() -1; i++) {
            if  (is.charAt(i) == '\\') {
                // Anything escaped is what it is! We assume that
                // people know how to escape characters correctly. However
                // we catch anything that does not need an escape in Java (which
                // is what the default implementation is dealing with and remove
                // the escape. The C target does this for instance.
                //
                switch (is.charAt(i+1)) {
                    // Pass through any escapes that Java also needs
                    //
                    case    '"':
                    case    'n':
                    case    'r':
                    case    't':
                    case    'b':
                    case    'f':
                    case    '\\':
						// Pass the escape through
						sb.append('\\');
                        break;

					case    'u':    // Assume unnnn
						// Pass the escape through as double \\
						// so that Java leaves as \u0000 string not char
						sb.append('\\');
						sb.append('\\');
                        break;

                    default:
                        // Remove the escape by virtue of not adding it here
                        // Thus \' becomes ' and so on
                        break;
                }

                // Go past the \ character
                i++;
            } else {
                // Characters that don't need \ in ANTLR 'strings' but do in Java
                if (is.charAt(i) == '"') {
                    // We need to escape " in Java
                    sb.append('\\');
                }
            }
            // Add in the next character, which may have been escaped
            sb.append(is.charAt(i));
        }

		if ( addQuotes ) sb.append('"');

		return sb.toString();
	}

	/** Assume 16-bit char */
	public String encodeIntAsCharEscape(int v) {
		if (v < Character.MIN_VALUE || v > Character.MAX_VALUE) {
			throw new IllegalArgumentException(String.format("Cannot encode the specified value: %d", v));
		}

		if (v >= 0 && v < targetCharValueEscape.length && targetCharValueEscape[v] != null) {
			return targetCharValueEscape[v];
		}

		if (v >= 0x20 && v < 127 && (!Character.isDigit(v) || v == '8' || v == '9')) {
			return String.valueOf((char)v);
		}

		if ( v>=0 && v<=127 ) {
			String oct = Integer.toOctalString(v);
			return "\\"+ oct;
		}

		String hex = Integer.toHexString(v|0x10000).substring(1,5);
		return "\\u"+hex;
	}

	public String getLoopLabel(GrammarAST ast) {
		return "loop"+ ast.token.getTokenIndex();
	}

	public String getLoopCounter(GrammarAST ast) {
		return "cnt"+ ast.token.getTokenIndex();
	}

	public String getListLabel(String label) {
		ST st = gen.templates.getInstanceOf("ListLabelName");
		st.add("label", label);
		return st.render();
	}

	public String getRuleFunctionContextStructName(Rule r) {
		if ( r.g.isLexer() ) {
			return gen.templates.getInstanceOf("LexerRuleContext").render();
		}

		String baseName = r.name;
		int lfIndex = baseName.indexOf(ATNSimulator.RULE_VARIANT_DELIMITER);
		if (lfIndex >= 0) {
			baseName = baseName.substring(0, lfIndex);
		}

		return Utils.capitalize(baseName)+gen.templates.getInstanceOf("RuleContextNameSuffix").render();
	}

	public String getAltLabelContextStructName(String label) {
		return Utils.capitalize(label)+gen.templates.getInstanceOf("RuleContextNameSuffix").render();
	}

	/** If we know which actual function, we can provide the actual ctx type.
	 *  This will contain implicit labels etc...  From outside, though, we
	 *  see only ParserRuleContext unless there are externally visible stuff
	 *  like args, locals, explicit labels, etc...
	 */
	public String getRuleFunctionContextStructName(RuleFunction function) {
		Rule r = function.rule;
		if ( r.g.isLexer() ) {
			return gen.templates.getInstanceOf("LexerRuleContext").render();
		}

		String baseName = function.variantOf != null ? function.variantOf : function.name;
		return Utils.capitalize(baseName)+gen.templates.getInstanceOf("RuleContextNameSuffix").render();
	}

	// should be same for all refs to same token like ctx.ID within single rule function
	// for literals like 'while', we gen _s<ttype>
	public String getImplicitTokenLabel(String tokenName) {
		ST st = gen.templates.getInstanceOf("ImplicitTokenLabel");
		int ttype = gen.g.getTokenType(tokenName);
		if ( tokenName.startsWith("'") ) {
			return "s"+ttype;
		}
		String text = getTokenTypeAsTargetLabel(gen.g, ttype);
		st.add("tokenName", text);
		return st.render();
	}

	// x=(A|B)
	public String getImplicitSetLabel(String id) {
		ST st = gen.templates.getInstanceOf("ImplicitSetLabel");
		st.add("id", id);
		return st.render();
	}

	public String getImplicitRuleLabel(String ruleName) {
		ST st = gen.templates.getInstanceOf("ImplicitRuleLabel");
		st.add("ruleName", ruleName);
		return st.render();
	}

	public String getElementListName(String name) {
		ST st = gen.templates.getInstanceOf("ElementListName");
		st.add("elemName", getElementName(name));
		return st.render();
	}

	public String getElementName(String name) {
		if (".".equals(name)) {
			return "_wild";
		}

		if ( gen.g.getRule(name)!=null ) return name;
		int ttype = gen.g.getTokenType(name);
		if ( ttype==Token.INVALID_TYPE ) return name;
		return getTokenTypeAsTargetLabel(gen.g, ttype);
	}

	public boolean grammarSymbolCausesIssueInGeneratedCode(GrammarAST idNode) {
		switch (idNode.getParent().getType()) {
		case ANTLRParser.ASSIGN:
			switch (idNode.getParent().getParent().getType()) {
			case ANTLRParser.ELEMENT_OPTIONS:
			case ANTLRParser.OPTIONS:
				return false;

			default:
				break;
			}

			break;

		case ANTLRParser.AT:
		case ANTLRParser.ELEMENT_OPTIONS:
			return false;

		case ANTLRParser.LEXER_ACTION_CALL:
			if (idNode.getChildIndex() == 0) {
				// first child is the command name which is part of the ANTLR language
				return false;
			}

			// arguments to the command should be checked
			break;

		default:
			break;
		}

		return badWords.contains(idNode.getText());
	}

}
