package org.antlr.v4.test;

import org.junit.Test;

/** */
public class TestSymbolIssues extends BaseTest {
    static String[] pairs = {
        // INPUT
        "grammar A;\n" +
        "options { opt='sss'; k=3; }\n" +
        "tokens { X; Y='y'; X='x'; Y='q'; Z; Z; }\n" +
        "scope Blort { int x; }\n" +
        "\n" +
        "@members {foo}\n" +
        "@members {bar}\n" +
        "@lexer::header {package jj;}\n" +
        "@lexer::header {package kk;}\n" +
        "\n" +
        "scope Blort { int x; }\n" +
        "\n" +
        "a[int i] returns [foo f] : X ID a[3] b[34] q ;\n" +
        "b returns [int g] : Y 'y' 'if' a ;\n" +
        "a : FJKD ;\n" +
        "\n" +
        "ID : 'a'..'z'+ ID ;",
        // YIELDS
        "error(61): A.g:11:6: scope Blort redefinition\n" +
        "error(18): A.g:15:0: rule a redefinition\n" +
        "error(60): A.g:7:1: redefinition of members action\n" +
        "error(60): A.g:9:1: redefinition of header action\n" +
        "error(74): A.g:3:19: cannot alias X; token name already defined\n" +
        "error(74): A.g:3:26: cannot alias Y; token name already assigned to 'y'\n" +
        "error(74): A.g:3:36: cannot alias Z; token name already defined\n" +
        "error(46): A.g:13:37: rule b has no defined parameters\n" +
        "error(24): A.g:13:43: reference to undefined rule: q\n" +
        "error(45): A.g:14:31: missing parameter(s) on rule reference: a",

        // INPUT
        "parser grammar B;\n" +
        "tokens { X='x'; Y; }\n" +
        "scope s { int i; }\n" +
        "\n" +
        "a : s=ID b+=ID X=ID '.' ;\n" +
        "\n" +
        "b : x=ID x+=ID ;\n" +
        "\n" +
        "s : FOO ;",
        // YIELDS
        "error(34): B.g:9:0: symbol s conflicts with global dynamic scope with same name\n" +
        "error(34): B.g:5:4: symbol s conflicts with global dynamic scope with same name\n" +
        "error(35): B.g:5:9: label b conflicts with rule with same name\n" +
        "error(36): B.g:5:15: label X conflicts with token with same name\n" +
        "error(41): B.g:7:9: label x type mismatch with previous definition: TOKEN_LIST_LABEL!=TOKEN_LABEL"
    };

    @Test public void testErrors() { super.testErrors(pairs); }
}
