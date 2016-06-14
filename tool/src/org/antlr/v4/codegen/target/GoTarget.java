package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;
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

	/** Avoid grammar symbols in this set to prevent conflicts in gen'd code. */
	private final Set<String> badWords = new HashSet<String>(goKeywords.length + goPredeclaredIdentifiers.length + 2);

	private static final boolean DO_GOFMT = !Boolean.parseBoolean(System.getenv("ANTLR_GO_DISABLE_GOFMT"))
			&& !Boolean.parseBoolean(System.getProperty("antlr.go.disable-gofmt"));

	public GoTarget(CodeGenerator gen) {
		super(gen, "Go");
	}

    @Override
    public String getVersion() {
		return "4.5.2";
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
		badWords.add("rule");
		badWords.add("parserRule");
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

	public boolean wantsBaseListener() {
		return true;
	}

	public boolean wantsBaseVisitor() {
		return true;
	}

	public boolean supportsOverloadedMethods() {
		return false;
	}
}

