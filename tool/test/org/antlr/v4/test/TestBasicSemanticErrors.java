package org.antlr.v4.test;

import org.junit.Test;

public class TestBasicSemanticErrors extends BaseTest {
    static String[] A = {
        // INPUT
        "grammar A;\n" +
        "\n" +
        "options {\n" +
        "        output=template;\n" +
        "}\n" +
        "\n" +
        "a : ID<Foo> -> ID ;\n" +
        "\n" +
        "b : A^ | ((B!|C)) -> C;",
        // YIELDS
        "error(68): A.g:7:7: alts with rewrites can't use heterogeneous types left of ->\n" +
		"error(78): A.g:9:4: AST operator with non-AST output option: ^\n" +
		"error(78): A.g:9:11: AST operator with non-AST output option: !\n" +
		"error(79): A.g:9:11: rule b alt 2 uses rewrite syntax and also an AST operator",

        // INPUT
        "tree grammar B;\n" +
        "options {\n" +
        "\tfilter=true;\n" +
        "\tbacktrack=false;\n" +
        "\toutput=template;\n" +
        "}\n" +
        "\n" +
        "a : A;\n" +
        "\n" +
        "b : ^(. A) ;",
        // YIELDS
        "error(80): B.g:10:6: Wildcard invalid as root; wildcard can itself be a tree\n" +
		"error(81): B.g:1:5: option backtrack=false conflicts with tree grammar filter mode\n" +
		"error(81): B.g:1:5: option output=template conflicts with tree grammar filter mode"
    };

    static String[] U = {
        // INPUT
        "parser grammar U;\n" +
        "options { foo=bar; k=*; backtrack=true;}\n" +
        "tokens {\n" +
        "        f='fkj';\n" +
        "        S = 'a';\n" +
        "}\n" +
        "tokens { A; }\n" +
        "options { x=y; }\n" +
        "\n" +
        "a\n" +
        "options { blech=bar; greedy=true; }\n" +
        "        :       ID\n" +
        "        ;\n" +
        "b : ( options { ick=bar; greedy=true; } : ID )+ ;\n" +
        "c : ID<blue> ID<x=y> ;",
        // YIELDS
        "error(21): U.g:8:0: repeated grammar prequel spec (option, token, or import); please merge\n" +
		"error(21): U.g:7:0: repeated grammar prequel spec (option, token, or import); please merge\n" +
		"error(49): U.g:2:10: illegal option foo\n" +
		"error(26): U.g:4:8: token names must start with an uppercase letter: f\n" +
		"error(25): U.g:4:8: can't assign string value to token name f in non-combined grammar\n" +
		"error(25): U.g:5:8: can't assign string value to token name S in non-combined grammar\n" +
		"error(49): U.g:8:10: illegal option x\n" +
		"error(49): U.g:11:10: illegal option blech\n" +
		"error(49): U.g:14:16: illegal option ick\n" +
		"error(49): U.g:15:16: illegal option x",

        // INPUT
        "tree grammar V;\n" +
        "options {\n" +
        "        rewrite=true;\n" +
        "        output=template;\n" +
        "}\n" +
        "a : A\n" +
        "  | A B -> template() \"kjsfdkdsj\" \n" +
        "  ;",
        // YIELDS
        "error(66): V.g:7:4: with rewrite=true, alt 2 not simple node or obvious tree element; text attribute for rule not guaranteed to be correct",

        // INPUT
        "tree grammar V;\n" +
        "options { rewrite=true; }\n" +
        "a : A\n" +
        "  | A B -> template() \"kjsfdkdsj\" \n" +
        "  ;",
        // YIELDS
        "error(62): V.g:4:8: rule a uses rewrite syntax or operator with no output option",
    };

	static String[] C = {
		"parser grammar C;\n" +
		"options {output=AST;}\n" +
		"tokens { A; B; C; }\n" +
		"a : A -> B $a A ;", // no problem with or $a.

		""
	};

    @Test public void testA() { super.testErrors(A, false); }
	@Test public void testU() { super.testErrors(U, false); }
	@Test public void testE() { super.testErrors(C, false); }
}