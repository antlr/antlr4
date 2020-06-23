/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.target;

import org.antlr.v4.Tool;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.codegen.UnicodeEscapes;
import org.antlr.v4.tool.ast.GrammarAST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.StringRenderer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class DTarget extends Target {

    /**
     * The D target can cache the code generation templates.
     */
    private static final ThreadLocal<STGroup> targetTemplates = new ThreadLocal<STGroup>();

    protected static final String[] dKeywords = {
        "abstract", "assert", "bool", "break", "byte", "case", "catch",
        "char", "class", "const", "continue", "default", "do", "double", "else",
        "enum", "extends", "false", "final", "finally", "float", "for", "goto",
        "if", "implements", "import", "instanceof", "in", "int", "interface",
        "long", "native", "new", "null", "out", "package", "private", "protected",
        "public", "return", "short", "static", "super", "switch",
        "synchronized", "template", "this", "throw", "throws", "transient",
                "true", "try", "void", "volatile", "while"
    };

    /** Avoid grammar symbols in this set to prevent conflicts in gen'd code. */
    protected final Set<String> badWords = new HashSet<String>();

    public DTarget(CodeGenerator gen) {
        super(gen, "D");
    }

    @Override
    public String getVersion() {
        return Tool.VERSION; // Java and tool versions move in lock step
    }

    @Override
    public String encodeIntAsCharEscape(int v) {
        if (v < Character.MIN_VALUE || v > Character.MAX_VALUE) {
            throw new IllegalArgumentException(String.format("Cannot encode the specified value: %d", v));
        }

        if (v >= 0 && v < targetCharValueEscape.length && targetCharValueEscape[v] != null) {
            return targetCharValueEscape[v];
        }

        if (v >= 0x20 && v < 127) {
            return String.valueOf((char)v);
        }

        if ( v>=55296 && v<=55551 ) {
            String octs = Integer.toOctalString(v);
            return "["+ octs + "]";
        }

        if (v > 0x7f) {
            return "\\u"+Integer.toHexString(v|0x10000).substring(1,5);
        }
        else
            return "\\x"+Integer.toHexString(v|0x10000).substring(3,5);
    }

    public Set<String> getBadWords() {
        if (badWords.isEmpty()) {
            addBadWords();
        }

        return badWords;
    }

    protected void addBadWords() {
        badWords.addAll(Arrays.asList(dKeywords));
        badWords.add("rule");
        badWords.add("parserRule");
    }

    @Override
    public int getSerializedATNSegmentLimit() {
        // 65535 is the class file format byte limit for a UTF-8 encoded string literal
        // 3 is the maximum number of bytes it takes to encode a value in the range 0-0xFFFF
        return 65535 / 3;
    }

    @Override
    protected boolean visibleGrammarSymbolCausesIssueInGeneratedCode(GrammarAST idNode) {
        return getBadWords().contains(idNode.getText());
    }

    @Override
    protected STGroup loadTemplates() {
        STGroup result = targetTemplates.get();
        if (result == null) {
            result = super.loadTemplates();
            result.registerRenderer(String.class, new DStringRenderer(), true);
            targetTemplates.set(result);
        }

        return result;
    }

    protected static class DStringRenderer extends StringRenderer {

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
        UnicodeEscapes.appendPythonStyleEscapedCodePoint(codePoint, sb);
    }
}
