/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.tool.ErrorType;
import org.stringtemplate.v4.NumberRenderer;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.StringRenderer;
import org.stringtemplate.v4.misc.STMessage;

import java.util.*;

public class CppTarget extends Target {
	protected static final Map<Character, String> targetCharValueEscape;
	static {
		// https://stackoverflow.com/a/10220539/1046374
		HashMap<Character, String> map = new HashMap<>();
		addEscapedChar(map, (char)0x0007, 'a');
		addEscapedChar(map, (char)0x0008, 'b');
		addEscapedChar(map, '\t', 't');
		addEscapedChar(map, '\n', 'n');
		addEscapedChar(map, (char)0x000B, 'v');
		addEscapedChar(map, '\f', 'f');
		addEscapedChar(map, '\r', 'r');
		addEscapedChar(map, (char)0x001B, 'e');
		addEscapedChar(map, '\"');
		addEscapedChar(map, '\'');
		addEscapedChar(map, '?');
		addEscapedChar(map, '\\');
		targetCharValueEscape = map;
	}

	protected static final HashSet<String> reservedWords =  new HashSet<>(Arrays.asList(
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
		"xor", "xor_eq",

		"rule", "parserRule"
	));

	public CppTarget(CodeGenerator gen) {
		super(gen);
	}

	@Override
	public Map<Character, String> getTargetCharValueEscape() {
		return targetCharValueEscape;
	}

	@Override
	protected Set<String> getReservedWords() {
		return reservedWords;
	}

	public boolean needsHeader() { return true; }

    @Override
	protected boolean shouldUseUnicodeEscapeForCodePointInDoubleQuotedString(int codePoint) {
		if (codePoint == '?') {
			// in addition to the default escaped code points, also escape ? to prevent trigraphs
			// ideally, we would escape ? with \?, but escaping as unicode \u003F works as well
			return true;
		}
		else {
			return super.shouldUseUnicodeEscapeForCodePointInDoubleQuotedString(codePoint);
		}
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
