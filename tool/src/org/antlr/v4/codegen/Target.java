/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.codegen;

import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.tool.*;
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

	public CodeGenerator gen;

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
	}

	protected void genRecognizerFile(Grammar g,
									 ST outputFileST)
		throws IOException
	{
		String fileName = gen.getRecognizerFileName();
		gen.write(outputFileST, fileName);
	}

	protected void genRecognizerHeaderFile(Grammar g,
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
		if ( name==null ) {
			System.out.println("null token?");
		}
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

	/** Assume 16-bit char */
	public String encodeIntAsCharEscape(int v) {
		if ( v>=0 && v<=127 ) {
			String oct = Integer.toOctalString(v);
			if ( oct.length()<3 ) oct = '0'+oct;
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

	public String getListLabel(String label) { return label+"_list"; }
	public String getRuleFunctionContextStructName(Rule r) {
		if ( r.args==null && r.retvals==null && r.scope==null && r.getLabelNames()==null ) {
			return gen.templates.getInstanceOf("ParserRuleContext").render();
		}
		return r.name+"_ctx";
	}
	public String getRuleDynamicScopeStructName(String ruleName) {
		ST st = gen.templates.getInstanceOf("RuleDynamicScopeStructName");
		st.add("ruleName", ruleName);
		return st.render();
	}
	public String getGlobalDynamicScopeStructName(String scopeName) { return scopeName; }

	// should be same for all refs to same token like $ID within single rule function
	public String getImplicitTokenLabel(String tokenName) {
		ST st = gen.templates.getInstanceOf("ImplicitTokenLabel");
		int ttype = gen.g.getTokenType(tokenName);
		String text = getTokenTypeAsTargetLabel(gen.g, ttype);
		st.add("tokenName", text);
		return st.render();
	}

	public String getImplicitRuleLabel(String ruleName) {
		ST st = gen.templates.getInstanceOf("ImplicitRuleLabel");
		st.add("ruleName", ruleName);
		return st.render();
	}

	// AST

	public String getRootName(int level) {
		ST st = gen.templates.getInstanceOf("RootName");
		st.add("level", level);
		return st.render();
	}

	public String getRewriteIteratorName(GrammarAST elem, int level) {
		ST st = gen.templates.getInstanceOf("RewriteIteratorName");
		st.add("elemName", getElementName(elem.getText()));
		st.add("level", level);
		return st.render();
	}

	public String getElementListName(String name) {
		ST st = gen.templates.getInstanceOf("ElementListName");
		st.add("elemName", getElementName(name));
		return st.render();
	}

	public String getElementName(String name) {
		if ( gen.g.getRule(name)!=null ) return name;
		int ttype = gen.g.getTokenType(name);
		if ( ttype==Token.INVALID_TYPE ) return name;
		return getTokenTypeAsTargetLabel(gen.g, ttype);
	}
}
