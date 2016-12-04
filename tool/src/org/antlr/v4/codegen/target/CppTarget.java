/*
 * Copyright (c) 2012 The ANTLR Project Authors. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.ast.GrammarAST;
import org.stringtemplate.v4.NumberRenderer;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.StringRenderer;
import org.stringtemplate.v4.misc.STMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CppTarget extends Target {

	protected static final String[] cppKeywords = {
		"alignas", "alignof", "and", "and_eq", "asm", "auto", "bitand",
		"bitor", "bool", "break", "case", "catch", "char", "char16_t",
		"char32_t", "class", "compl", "concept", "const", "constexpr",
		"const_cast", "continue", "decltype", "default", "delete", "do",
		"double", "dynamic_cast", "else", "enum", "explicit", "export",
		"extern", "false", "float", "for", "friend", "goto", "if",
		"inline", "int", "long", "mutable", "namespace", "new",
		"noexcept", "not", "not_eq", "nullptr", "operator", "or",
		"or_eq", "private", "protected", "public", "register",
		"reinterpret_cast", "requires", "return", "short", "signed",
		"sizeof", "static", "static_assert", "static_cast", "struct",
		"switch", "template", "this", "thread_local", "throw", "true",
		"try", "typedef", "typeid", "typename", "union", "unsigned",
		"using", "virtual", "void", "volatile", "wchar_t", "while",
		"xor", "xor_eq"
	};

	/** Avoid grammar symbols in this set to prevent conflicts in gen'd code. */
	protected final Set<String> badWords = new HashSet<String>();

	public CppTarget(CodeGenerator gen) {
		super(gen, "Cpp");
	}

	public String getVersion() {
		return "4.6";
	}

    public boolean needsHeader() { return true; }

	public Set<String> getBadWords() {
		if (badWords.isEmpty()) {
			addBadWords();
		}

		return badWords;
	}

	protected void addBadWords() {
		badWords.addAll(Arrays.asList(cppKeywords));
		badWords.add("rule");
		badWords.add("parserRule");
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * For C++, this is the translation {@code 'a\n"'} &rarr; {@code "a\n\""}.
	 * Expect single quotes around the incoming literal. Just flip the quotes
	 * and replace double quotes with {@code \"}.
	 * <p/>
	 * Note that we have decided to allow people to use '\"' without penalty, so
	 * we must build the target string in a loop as {@link String#replace}
	 * cannot handle both {@code \"} and {@code "} without a lot of messing
	 * around.
	 */
	@Override
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

	@Override
	public String encodeIntAsCharEscape(int v) {
		return "0x" + Integer.toHexString(v) + ", ";
	}

	@Override
	public int getSerializedATNSegmentLimit() {
		// 65535 is the class file format byte limit for a UTF-8 encoded string literal
		// 3 is the maximum number of bytes it takes to encode a value in the range 0-0xFFFF
		return 65535 / 3;
	}

	@Override
	public String getRecognizerFileName(boolean header) {
		ST extST = getTemplates().getInstanceOf(header ? "headerFileExtension" : "codeFileExtension");
		String recognizerName = gen.g.getRecognizerName();
		return recognizerName+extST.render();
	}

	@Override
	public String getListenerFileName(boolean header) {
		assert gen.g.name != null;
		ST extST = getTemplates().getInstanceOf(header ? "headerFileExtension" : "codeFileExtension");
		String listenerName = gen.g.name + "Listener";
		return listenerName+extST.render();
	}

	@Override
	public String getVisitorFileName(boolean header) {
		assert gen.g.name != null;
		ST extST = getTemplates().getInstanceOf(header ? "headerFileExtension" : "codeFileExtension");
		String listenerName = gen.g.name + "Visitor";
		return listenerName+extST.render();
	}

	@Override
	public String getBaseListenerFileName(boolean header) {
		assert gen.g.name != null;
		ST extST = getTemplates().getInstanceOf(header ? "headerFileExtension" : "codeFileExtension");
		String listenerName = gen.g.name + "BaseListener";
		return listenerName+extST.render();
	}

	@Override
	public String getBaseVisitorFileName(boolean header) {
		assert gen.g.name != null;
		ST extST = getTemplates().getInstanceOf(header ? "headerFileExtension" : "codeFileExtension");
		String listenerName = gen.g.name + "BaseVisitor";
		return listenerName+extST.render();
	}

	@Override
	protected boolean visibleGrammarSymbolCausesIssueInGeneratedCode(GrammarAST idNode) {
		return getBadWords().contains(idNode.getText());
	}

	@Override
	protected STGroup loadTemplates() {
		STGroup result = super.loadTemplates();
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
}
