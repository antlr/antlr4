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
        "error(17): A.g:15:0: rule a redefinition\n" +
        "error(60): A.g:11:6: scope Blort redefinition\n" +
        "error(59): A.g:7:1: redefinition of members action\n" +
        "error(59): A.g:9:1: redefinition of header action\n" +
        "error(73): A.g:3:19: cannot alias X; token name already defined\n" +
        "error(73): A.g:3:26: cannot alias Y; token name already assigned to 'y'\n" +
        "error(73): A.g:3:36: cannot alias Z; token name already defined\n" +
        "error(45): A.g:13:37: rule b has no defined parameters\n" +
        "error(23): A.g:13:43: reference to undefined rule: q\n" +
        "error(44): A.g:14:31: missing parameter(s) on rule reference: a",
    };

    @Test public void testErrors() { super.testErrors(pairs); }
}
