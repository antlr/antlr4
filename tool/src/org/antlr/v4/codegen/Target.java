/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen;

import org.antlr.v4.Tool;
import org.antlr.v4.codegen.model.RuleFunction;
import org.antlr.v4.codegen.model.SerializedATN;
import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.GrammarAST;
import org.stringtemplate.v4.*;
import org.stringtemplate.v4.misc.STMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** */
public abstract class Target {
	private final static Map<String, STGroup> languageTemplates = new HashMap<>();

	protected final CodeGenerator gen;

	protected static final Map<Character, String> defaultCharValueEscape;
	static {
		// https://docs.oracle.com/javase/tutorial/java/data/characters.html
		HashMap<Character, String> map = new HashMap<>();
		addEscapedChar(map, '\t', 't');
		addEscapedChar(map, '\b', 'b');
		addEscapedChar(map, '\n', 'n');
		addEscapedChar(map, '\r', 'r');
		addEscapedChar(map, '\f', 'f');
		addEscapedChar(map, '\'');
		addEscapedChar(map, '"');
		addEscapedChar(map, '\\');
		defaultCharValueEscape = map;
	}

	protected Target(CodeGenerator gen) {
		this.gen = gen;
	}

	/** For pure strings of Unicode char, how can we display
	 *  it in the target language as a literal. Useful for dumping
	 *  predicates and such that may refer to chars that need to be escaped
	 *  when represented as strings.  Also, templates need to be escaped so
	 *  that the target language can hold them as a string.
	 *  Each target can have a different set in memory at same time.
	 */
	public Map<Character, String> getTargetCharValueEscape() {
		return defaultCharValueEscape;
	}

	protected static void addEscapedChar(HashMap<Character, String> map, char key) {
		addEscapedChar(map, key, key);
	}

	protected static void addEscapedChar(HashMap<Character, String> map, char key, char representation) {
		map.put(key, "\\" + representation);
	}

	public String getLanguage() { return gen.language; }

	public CodeGenerator getCodeGenerator() {
		return gen;
	}

	/** ANTLR tool should check output templates / target are compatible with tool code generation.
	 *  For now, a simple string match used on x.y of x.y.z scheme. We use a method to avoid mismatches
	 *  between a template called VERSION. This value is checked against Tool.VERSION during load of templates.
	 *
	 *  This additional method forces all targets 4.3 and beyond to add this method.
	 *
	 * @since 4.3
	 */
	public String getVersion() {
		return Tool.VERSION;
	}

	public synchronized STGroup getTemplates() {
		String language = getLanguage();
		STGroup templates = languageTemplates.get(language);

		if (templates == null) {
			String version = getVersion();
			if (version == null ||
					!RuntimeMetaData.getMajorMinorVersion(version).equals(RuntimeMetaData.getMajorMinorVersion(Tool.VERSION))) {
				gen.tool.errMgr.toolError(ErrorType.INCOMPATIBLE_TOOL_AND_TEMPLATES, version, Tool.VERSION, language);
			}
			templates = loadTemplates();
			languageTemplates.put(language, templates);
		}

		return templates;
	}

	protected abstract Set<String> getReservedWords();

	public String escapeIfNeeded(String identifier) {
		return getReservedWords().contains(identifier) ? escapeWord(identifier) : identifier;
	}

	protected String escapeWord(String word) {
		return word + "_";
	}

	protected void genFile(Grammar g, ST outputFileST, String fileName)
	{
		getCodeGenerator().write(outputFileST, fileName);
	}

	/** Get a meaningful name for a token type useful during code generation.
	 *  Literals without associated names are converted to the string equivalent
	 *  of their integer values. Used to generate x==ID and x==34 type comparisons
	 *  etc...  Essentially we are looking for the most obvious way to refer
	 *  to a token type in the generated code.
	 */
	public String getTokenTypeAsTargetLabel(Grammar g, int ttype) {
		String name = g.getTokenName(ttype);
		// If name is not valid, return the token type instead
		if ( Grammar.INVALID_TOKEN_NAME.equals(name) ) {
			return String.valueOf(ttype);
		}

		return name;
	}

	/**
	 * <p>Convert from an ANTLR string literal or predicate content found in a grammar file to an
	 * equivalent string literal in the target language. The returning string is double-quoted
	 * </p>
	 * <p>
	 * Example input string:
	 *
	 *    a"[newlinechar]b'c[carriagereturnchar]d[tab]e\f
	 *
	 * would be converted to the valid target string literal if keepEscaping is false:
	 *
	 *    "a\"\nb'c\rd\te\\f"
	 *
	 * otherwise, if keepEscaping is true (it used for token names):
	 *
	 *    "a\"\\nb'c\\rd\\te\\f"
	 *
	 * keepEscaping parameter is only actual for ANTLR string literals
	 * </p>
	 */
	public final String getTargetStringLiteralFromAntlrGrammar(String literal, boolean keepEscaping) {
		StringBuilder sb = new StringBuilder();

		Map<Character, String> targetCharValueEscape = getTargetCharValueEscape();
		sb.append('"');
		for (int i = 0; i < literal.length(); ) {
			int codePoint = literal.codePointAt(i);
			int toAdvance = Character.charCount(codePoint);
			if  (keepEscaping && codePoint == '\\') {
				// Anything escaped is what it is! We assume that
				// people know how to escape characters correctly. However,
				// we catch anything that does not need an escape in Java (which
				// is what the default implementation is dealing with and remove
				// the escape.
				//
				int escapedCodePoint = literal.codePointAt(i+toAdvance);
				toAdvance++;
				switch (escapedCodePoint) {
					// Pass through any escapes that Java also needs
					case    'n':
					case    'r':
					case    't':
					case    'b':
					case    'f':
						sb.append("\\\\").appendCodePoint(escapedCodePoint);
						break;
					case    '\'':
						sb.append('\''); // No need to escaped single quote since all strings in targets are enclosed in double quotes
						break;
					case    '\\':
						sb.append("\\\\");
						break;
					case    'u':    // Either unnnn or u{nnnnnn}
						if (literal.charAt(i+toAdvance) == '{') {
							while (literal.charAt(i+toAdvance) != '}') {
								toAdvance++;
							}
							toAdvance++;
						}
						else {
							toAdvance += 4;
						}
						assert i + toAdvance <= literal.length();  // invalid \\uAB or something should be reported before
						String fullEscape = literal.substring(i, i+toAdvance);
						sb.append('\\');
						UnicodeEscapes.appendEscapedCodePoint(sb, CharSupport.getCharValueFromCharInGrammarLiteral(fullEscape), getLanguage());
						break;
					default:
						assert false : "Invalid escape sequence should be checked before";
				}
			}
			else {
				String targetEscapedChar = targetCharValueEscape.get((char) codePoint);
				if (codePoint != '\'' && targetEscapedChar != null) { // No need to escaped single quote since all strings in targets are enclosed in double quotes
					sb.append(targetEscapedChar);
				}
				else if (shouldUseUnicodeEscapeForCodePointInDoubleQuotedString(codePoint)) {
					UnicodeEscapes.appendEscapedCodePoint(sb, codePoint, getLanguage());
				}
				else {
					sb.appendCodePoint(codePoint);
				}
			}
			i += toAdvance;
		}
		sb.append('"');

		return sb.toString();
	}

	protected boolean shouldUseUnicodeEscapeForCodePointInDoubleQuotedString(int codePoint) {
		// We don't want anyone passing 0x0A (newline) or 0x22
		// (double-quote) here because Java treats \\u000A as
		// a literal newline and \\u0022 as a literal
		// double-quote, so Unicode escaping doesn't help.
		assert codePoint != 0x0A && codePoint != 0x22;

		return
			codePoint < 0x20  || // control characters up to but not including space
			codePoint == 0x5C || // backslash
			codePoint >= 0x7F;   // DEL and beyond (keeps source code 7-bit US-ASCII)
	}

	/** Assume 16-bit char */
	public String encodeInt16AsCharEscape(int v) {
		if (v < Character.MIN_VALUE || v > Character.MAX_VALUE) {
			throw new IllegalArgumentException(String.format("Cannot encode the specified value: %d", v));
		}

		if ( isATNSerializedAsInts() ) {
			return Integer.toString(v);
		}

		char c = (char)v;
		String escaped = getTargetCharValueEscape().get(c);
		if (escaped != null) {
			return escaped;
		}

		switch (Character.getType(c)) {
			case Character.CONTROL:
			case Character.LINE_SEPARATOR:
			case Character.PARAGRAPH_SEPARATOR:
				return escapeChar(v);
			default:
				if ( v<=127 ) {
					return String.valueOf(c);  // ascii chars can be as-is, no encoding
				}
				// else we use hex encoding to ensure pure ascii chars generated
				return escapeChar(v);
		}
	}

	protected String escapeChar(int v) {
		return String.format("\\u%04x", v);
	}

	public String getLoopLabel(GrammarAST ast) {
		return "loop"+ ast.token.getTokenIndex();
	}

	public String getLoopCounter(GrammarAST ast) {
		return "cnt"+ ast.token.getTokenIndex();
	}

	public String getListLabel(String label) {
		ST st = getTemplates().getInstanceOf("ListLabelName");
		st.add("label", label);
		return st.render();
	}

	public String getRuleFunctionContextStructName(Rule r) {
		if ( r.g.isLexer() ) {
			return getTemplates().getInstanceOf("LexerRuleContext").render();
		}
		return Utils.capitalize(r.name)+getTemplates().getInstanceOf("RuleContextNameSuffix").render();
	}

	public String getAltLabelContextStructName(String label) {
		return Utils.capitalize(label)+getTemplates().getInstanceOf("RuleContextNameSuffix").render();
	}

	/** If we know which actual function, we can provide the actual ctx type.
	 *  This will contain implicit labels etc...  From outside, though, we
	 *  see only ParserRuleContext unless there are externally visible stuff
	 *  like args, locals, explicit labels, etc...
	 */
	public String getRuleFunctionContextStructName(RuleFunction function) {
		Rule r = function.rule;
		if ( r.g.isLexer() ) {
			return getTemplates().getInstanceOf("LexerRuleContext").render();
		}
		return Utils.capitalize(r.name)+getTemplates().getInstanceOf("RuleContextNameSuffix").render();
	}

	// should be same for all refs to same token like ctx.ID within single rule function
	// for literals like 'while', we gen _s<ttype>
	public String getImplicitTokenLabel(String tokenName) {
		ST st = getTemplates().getInstanceOf("ImplicitTokenLabel");
		int ttype = getCodeGenerator().g.getTokenType(tokenName);
		if ( tokenName.startsWith("'") ) {
			return "s"+ttype;
		}
		String text = getTokenTypeAsTargetLabel(getCodeGenerator().g, ttype);
		st.add("tokenName", text);
		return st.render();
	}

	// x=(A|B)
	public String getImplicitSetLabel(String id) {
		ST st = getTemplates().getInstanceOf("ImplicitSetLabel");
		st.add("id", id);
		return st.render();
	}

	public String getImplicitRuleLabel(String ruleName) {
		ST st = getTemplates().getInstanceOf("ImplicitRuleLabel");
		st.add("ruleName", ruleName);
		return st.render();
	}

	public String getElementListName(String name) {
		ST st = getTemplates().getInstanceOf("ElementListName");
		st.add("elemName", getElementName(name));
		return st.render();
	}

	public String getElementName(String name) {
		if (".".equals(name)) {
			return "_wild";
		}

		if ( getCodeGenerator().g.getRule(name)!=null ) return name;
		int ttype = getCodeGenerator().g.getTokenType(name);
		if ( ttype==Token.INVALID_TYPE ) return name;
		return getTokenTypeAsTargetLabel(getCodeGenerator().g, ttype);
	}

	/** Generate TParser.java and TLexer.java from T.g4 if combined, else
	 *  just use T.java as output regardless of type.
	 */
	public String getRecognizerFileName(boolean header) {
		ST extST = getTemplates().getInstanceOf("codeFileExtension");
		String recognizerName = gen.g.getRecognizerName();
		return recognizerName+extST.render();
	}

	/** A given grammar T, return the listener name such as
	 *  TListener.java, if we're using the Java target.
 	 */
	public String getListenerFileName(boolean header) {
		assert gen.g.name != null;
		ST extST = getTemplates().getInstanceOf("codeFileExtension");
		String listenerName = gen.g.name + "Listener";
		return listenerName+extST.render();
	}

	/** A given grammar T, return the visitor name such as
	 *  TVisitor.java, if we're using the Java target.
 	 */
	public String getVisitorFileName(boolean header) {
		assert gen.g.name != null;
		ST extST = getTemplates().getInstanceOf("codeFileExtension");
		String listenerName = gen.g.name + "Visitor";
		return listenerName+extST.render();
	}

	/** A given grammar T, return a blank listener implementation
	 *  such as TBaseListener.java, if we're using the Java target.
 	 */
	public String getBaseListenerFileName(boolean header) {
		assert gen.g.name != null;
		ST extST = getTemplates().getInstanceOf("codeFileExtension");
		String listenerName = gen.g.name + "BaseListener";
		return listenerName+extST.render();
	}

	/** A given grammar T, return a blank listener implementation
	 *  such as TBaseListener.java, if we're using the Java target.
 	 */
	public String getBaseVisitorFileName(boolean header) {
		assert gen.g.name != null;
		ST extST = getTemplates().getInstanceOf("codeFileExtension");
		String listenerName = gen.g.name + "BaseVisitor";
		return listenerName+extST.render();
	}

	/**
	 * Gets the maximum number of 16-bit unsigned integers that can be encoded
	 * in a single segment (a declaration in target language) of the serialized ATN.
	 * E.g., in C++, a small segment length results in multiple decls like:
	 *
	 *   static const int32_t serializedATNSegment1[] = {
	 *     0x7, 0x12, 0x2, 0x13, 0x7, 0x13, 0x2, 0x14, 0x7, 0x14, 0x2, 0x15, 0x7,
	 *        0x15, 0x2, 0x16, 0x7, 0x16, 0x2, 0x17, 0x7, 0x17, 0x2, 0x18, 0x7,
	 *        0x18, 0x2, 0x19, 0x7, 0x19, 0x2, 0x1a, 0x7, 0x1a, 0x2, 0x1b, 0x7,
	 *        0x1b, 0x2, 0x1c, 0x7, 0x1c, 0x2, 0x1d, 0x7, 0x1d, 0x2, 0x1e, 0x7,
	 *        0x1e, 0x2, 0x1f, 0x7, 0x1f, 0x2, 0x20, 0x7, 0x20, 0x2, 0x21, 0x7,
	 *        0x21, 0x2, 0x22, 0x7, 0x22, 0x2, 0x23, 0x7, 0x23, 0x2, 0x24, 0x7,
	 *        0x24, 0x2, 0x25, 0x7, 0x25, 0x2, 0x26,
	 *   };
	 *
	 * instead of one big one.  Targets are free to ignore this like JavaScript does.
	 *
	 * This is primarily needed by Java target to limit size of any single ATN string
	 * to 65k length.
	 *
	 * @return the serialized ATN segment limit
	 */
	public int getSerializedATNSegmentLimit() {
		return Integer.MAX_VALUE;
	}

	/** How many bits should be used to do inline token type tests? Java assumes
	 *  a 64-bit word for bitsets.  Must be a valid wordsize for your target like
	 *  8, 16, 32, 64, etc...
	 *
	 *  @since 4.5
	 */
	public int getInlineTestSetWordSize() { return 64; }

	@Deprecated
	protected boolean visibleGrammarSymbolCausesIssueInGeneratedCode(GrammarAST idNode) {
		return getReservedWords().contains(idNode.getText());
	}

	public boolean templatesExist() {
		return loadTemplatesHelper(false) != null;
	}

	protected STGroup loadTemplates() {
		STGroup result = loadTemplatesHelper(true);
		if (result == null) {
			return null;
		}
		result.registerRenderer(Integer.class, new NumberRenderer());
		result.registerRenderer(String.class, new StringRenderer());
		result.setListener(new STErrorListener() {
			@Override
			public void compileTimeError(STMessage msg) {
				reportError(msg);
			}

			@Override
			public void runTimeError(STMessage msg) {
				reportError(msg);
			}

			@Override
			public void IOError(STMessage msg) {
				reportError(msg);
			}

			@Override
			public void internalError(STMessage msg) {
				reportError(msg);
			}

			private void reportError(STMessage msg) {
				getCodeGenerator().tool.errMgr.toolError(ErrorType.STRING_TEMPLATE_WARNING, msg.cause, msg.toString());
			}
		});

		return result;
	}

	private STGroup loadTemplatesHelper(boolean reportErrorIfFail) {
		String language = getLanguage();
		String groupFileName = CodeGenerator.TEMPLATE_ROOT + "/" + language + "/" + language + STGroup.GROUP_FILE_EXTENSION;
		try {
			return new STGroupFile(groupFileName);
		}
		catch (IllegalArgumentException iae) {
			if (reportErrorIfFail) {
				gen.tool.errMgr.toolError(ErrorType.MISSING_CODE_GEN_TEMPLATES, iae, getLanguage());
			}
			return null;
		}
	}

	/**
	 * @since 4.3
	 */
	public boolean wantsBaseListener() {
		return true;
	}

	/**
	 * @since 4.3
	 */
	public boolean wantsBaseVisitor() {
		return true;
	}

	/**
	 * @since 4.3
	 */
	public boolean supportsOverloadedMethods() {
		return true;
	}

	public boolean isATNSerializedAsInts() {
		return true;
	}

	/** @since 4.6 */
	public boolean needsHeader() { return false; } // Override in targets that need header files.
}
