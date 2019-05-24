/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.codegen.UnicodeEscapes;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.GrammarAST;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.StringRenderer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author Peter Boyer
 *
 * */
public class GoTarget extends Target {

	private static final String[] goKeywords = {
			"break", "default", "func", "interface", "select",
			"case", "defer", "go", "map", "struct",
			"chan", "else", "goto", "package", "switch",
			"const", "fallthrough", "if", "range", "type",
			"continue", "for", "import", "return", "var"
	};

	// predeclared identifiers https://golang.org/ref/spec#Predeclared_identifiers
	private static final String[] goPredeclaredIdentifiers = {
			"bool", "byte", "complex64", "complex128", "error", "float32", "float64",
			"int", ",int8", "int16", "int32", "int64", "rune", "string",
			"uint", "uint8", "uint16", "uint32", "uint64", "uintptr",
			"true", "false", "iota", "nil",
			"append", "cap", "close", "complex", "copy", "delete", "imag", "len",
			"make", "new", "panic", "print", "println", "real", "recover"
	};

	// interface definition of RuleContext from runtime/Go/antlr/rule_context.go
	private static final String[] goRuleContextInterfaceMethods = {
		"Accept", "GetAltNumber", "GetBaseRuleContext", "GetChild", "GetChildCount",
		"GetChildren", "GetInvokingState", "GetParent", "GetPayload", "GetRuleContext",
		"GetRuleIndex", "GetSourceInterval", "GetText", "IsEmpty", "SetAltNumber",
		"SetInvokingState", "SetParent", "String"
	};

	/** Avoid grammar symbols in this set to prevent conflicts in gen'd code. */
	private final Set<String> badWords = new HashSet<String>(
		goKeywords.length + goPredeclaredIdentifiers.length + goRuleContextInterfaceMethods.length + 3
	);

	private static final boolean DO_GOFMT = !Boolean.parseBoolean(System.getenv("ANTLR_GO_DISABLE_GOFMT"))
			&& !Boolean.parseBoolean(System.getProperty("antlr.go.disable-gofmt"));

	public GoTarget(CodeGenerator gen) {
		super(gen, "Go");
	}

	@Override
	public String getVersion() {
		return "4.7.2";
	}

	public Set<String> getBadWords() {
		if (badWords.isEmpty()) {
			addBadWords();
		}

		return badWords;
	}

	protected void addBadWords() {
		badWords.addAll(Arrays.asList(goKeywords));
		badWords.addAll(Arrays.asList(goPredeclaredIdentifiers));
		badWords.addAll(Arrays.asList(goRuleContextInterfaceMethods));
		badWords.add("rule");
		badWords.add("parserRule");
		badWords.add("action");
	}

	@Override
	protected void genFile(Grammar g, ST outputFileST, String fileName) {
		super.genFile(g, outputFileST, fileName);
		if (DO_GOFMT && !fileName.startsWith(".") /* criterion taken from gofmt */ && fileName.endsWith(".go")) {
			gofmt(new File(getCodeGenerator().tool.getOutputDirectory(g.fileName), fileName));
		}
	}

	private void gofmt(File fileName) {
		// Optimistically run gofmt. If this fails, it doesn't matter at this point. Wait for termination though,
		// because "gofmt -w" uses ioutil.WriteFile internally, which means it literally writes in-place with O_TRUNC.
		// That could result in a race. (Why oh why doesn't it do tmpfile + rename?)
		try {
			// TODO: need something like: String goExecutable = locateGo();
			ProcessBuilder gofmtBuilder = new ProcessBuilder("gofmt", "-w", "-s", fileName.getPath());
			gofmtBuilder.redirectErrorStream(true);
			Process gofmt = gofmtBuilder.start();
			InputStream stdout = gofmt.getInputStream();
			// TODO(wjkohnen): simplify to `while (stdout.Read() > 1) {}`
			byte[] buf = new byte[1 << 10];
			for (int l = 0; l > -1; l = stdout.read(buf)) {
				// There should not be any output that exceeds the implicit output buffer. In normal ops there should be
				// zero output. In case there is output, blocking and therefore killing the process is acceptable. This
				// drains the buffer anyway to play it safe.

				// dirty debug (change -w above to -d):
				// System.err.write(buf, 0, l);
			}
			gofmt.waitFor();
		} catch (IOException e) {
			// Probably gofmt not in $PATH, in any case ignore.
		} catch (InterruptedException forward) {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public String encodeIntAsCharEscape(int v) {
		return Integer.toString(v);
	}

	@Override
	public int getSerializedATNSegmentLimit() {
		return 2 ^ 31;
	}

	@Override
	public int getInlineTestSetWordSize() {
		return 32;
	}

	@Override
	protected boolean visibleGrammarSymbolCausesIssueInGeneratedCode(GrammarAST idNode) {
		return getBadWords().contains(idNode.getText());
	}

	@Override
	protected STGroup loadTemplates() {
		STGroup result = super.loadTemplates();
		result.registerRenderer(String.class, new JavaStringRenderer(), true);
		return result;
	}

	public String getRecognizerFileName(boolean header) {
		CodeGenerator gen = getCodeGenerator();
		Grammar g = gen.g;
		assert g!=null;
		String name;
		switch ( g.getType()) {
			case ANTLRParser.PARSER:
				name = g.name.endsWith("Parser") ? g.name.substring(0, g.name.length()-6) : g.name;
				return name.toLowerCase()+"_parser.go";
			case ANTLRParser.LEXER:
				name = g.name.endsWith("Lexer") ? g.name.substring(0, g.name.length()-5) : g.name; // trim off "lexer"
				return name.toLowerCase()+"_lexer.go";
			case ANTLRParser.COMBINED:
				return g.name.toLowerCase()+"_parser.go";
			default :
				return "INVALID_FILE_NAME";
		}
	}

	/** A given grammar T, return the listener name such as
	 *  TListener.java, if we're using the Java target.
 	 */
	public String getListenerFileName(boolean header) {
		CodeGenerator gen = getCodeGenerator();
		Grammar g = gen.g;
		assert g.name != null;
		return g.name.toLowerCase()+"_listener.go";
	}

	/** A given grammar T, return the visitor name such as
	 *  TVisitor.java, if we're using the Java target.
 	 */
	public String getVisitorFileName(boolean header) {
		CodeGenerator gen = getCodeGenerator();
		Grammar g = gen.g;
		assert g.name != null;
		return g.name.toLowerCase()+"_visitor.go";
	}

	/** A given grammar T, return a blank listener implementation
	 *  such as TBaseListener.java, if we're using the Java target.
 	 */
	public String getBaseListenerFileName(boolean header) {
		CodeGenerator gen = getCodeGenerator();
		Grammar g = gen.g;
		assert g.name != null;
		return g.name.toLowerCase()+"_base_listener.go";
	}

	/** A given grammar T, return a blank listener implementation
	 *  such as TBaseListener.java, if we're using the Java target.
 	 */
	public String getBaseVisitorFileName(boolean header) {
		CodeGenerator gen = getCodeGenerator();
		Grammar g = gen.g;
		assert g.name != null;
		return g.name.toLowerCase()+"_base_visitor.go";
	}

	protected static class JavaStringRenderer extends StringRenderer {

		@Override
		public String toString(Object o, String formatString, Locale locale) {

			if ("java-escape".equals(formatString)) {
				// 5C is the hex code for the \ itself
				return ((String)o).replace("\\u", "\\u005Cu");
			}

			return super.toString(o, formatString, locale);
		}

	}

	@Override
	protected void appendUnicodeEscapedCodePoint(int codePoint, StringBuilder sb) {
		// Go and Python share the same escaping style.
		UnicodeEscapes.appendPythonStyleEscapedCodePoint(codePoint, sb);
	}
}
