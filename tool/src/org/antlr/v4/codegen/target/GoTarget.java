
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
//
//	/**
//	 * {@inheritDoc}
//	 * <p/>
//	 * For Java, this is the translation {@code 'a\n"'} &rarr; {@code "a\n\""}.
//	 * Expect single quotes around the incoming literal. Just flip the quotes
//	 * and replace double quotes with {@code \"}.
//	 * <p/>
//	 * Note that we have decided to allow people to use '\"' without penalty, so
//	 * we must build the target string in a loop as {@link String#replace}
//	 * cannot handle both {@code \"} and {@code "} without a lot of messing
//	 * around.
//	 */
//	@Override
//	public String getTargetStringLiteralFromANTLRStringLiteral(
//		CodeGenerator generator,
//		String literal, boolean addQuotes)
//	{
//		System.out.println(literal);
//		System.out.println("GO TARGET!");
//
//		StringBuilder sb = new StringBuilder();
//		String is = literal;
//
//		if ( addQuotes ) sb.append('"');
//
//		for (int i = 1; i < is.length() -1; i++) {
//			if  (is.charAt(i) == '\\') {
//				// Anything escaped is what it is! We assume that
//				// people know how to escape characters correctly. However
//				// we catch anything that does not need an escape in Java (which
//				// is what the default implementation is dealing with and remove
//				// the escape. The C target does this for instance.
//				//
//				switch (is.charAt(i+1)) {
//					// Pass through any escapes that Java also needs
//					//
//					case    '"':
//					case    'n':
//					case    'r':
//					case    't':
//					case    'b':
//					case    'f':
//					case    '\\':
//						// Pass the escape through
//						sb.append('\\');
//						break;
//
//					case    'u':    // Assume unnnn
//						// Pass the escape through as double \\
//						// so that Java leaves as \u0000 string not char
//						sb.append('\\');
//						sb.append('\\');
//						break;
//
//					default:
//						// Remove the escape by virtue of not adding it here
//						// Thus \' becomes ' and so on
//						break;
//				}
//
//				// Go past the \ character
//				i++;
//			} else {
//				// Characters that don't need \ in ANTLR 'strings' but do in Java
//				if (is.charAt(i) == '"') {
//					// We need to escape " in Java
//					sb.append('\\');
//				}
//			}
//			// Add in the next character, which may have been escaped
//			sb.append(is.charAt(i));
//		}
//
//		if ( addQuotes ) sb.append('"');
//
//		String s = sb.toString();
//		System.out.println("AfTER: " + s);
//		return s;
//	}
//
//	@Override
//	public String encodeIntAsCharEscape(int v) {
//		if (v < Character.MIN_VALUE || v > Character.MAX_VALUE) {
//			throw new IllegalArgumentException(String.format("Cannot encode the specified value: %d", v));
//		}
//
//		if (v >= 0 && v < targetCharValueEscape.length && targetCharValueEscape[v] != null) {
//			return targetCharValueEscape[v];
//		}
//
//		if (v >= 0x20 && v < 127) {
//			return String.valueOf((char)v);
//		}
//
//		String hex = Integer.toHexString(v|0x10000).substring(1,5);
//		String h2 = "\\u"+hex;
//
//		System.out.println("Token : " + h2);
//		return h2;
//	}

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

