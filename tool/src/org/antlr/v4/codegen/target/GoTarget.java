
package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.tool.ast.GrammarAST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.StringRenderer;

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

	protected static final String[] goKeywords = {
        "break","default","func","interface","select",
        "case","defer","go","map","struct",
        "chan","else","goto","package","switch",
        "const","fallthrough","if","range","type",
        "continue","for","import","return","var"
    };

	/** Avoid grammar symbols in this set to prevent conflicts in gen'd code. */
	protected final Set<String> badWords = new HashSet<String>();

	public GoTarget(CodeGenerator gen) {
		super(gen, "Go");
	}

    @Override
    public String getVersion() {
        return "4.5.1";
    }

    public Set<String> getBadWords() {
		if (badWords.isEmpty()) {
			addBadWords();
		}

		return badWords;
	}

	protected void addBadWords() {
		badWords.addAll(Arrays.asList(goKeywords));
		badWords.add("rule");
		badWords.add("parserRule");
	}

	private final static String ZEROES = "0000";

	@Override
	public String encodeIntAsCharEscape(int v) {
		// we encode as uint16 in hex format
		String s = Integer.toString(v, 16);
		String intAsString = s.length() <= 4 ? ZEROES.substring(s.length()) + s : s;
		return "0x" + intAsString + ",";
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
		return false;
	}

	public boolean wantsBaseVisitor() {
		return false;
	}

	public boolean supportsOverloadedMethods() {
		return false;
	}
}

