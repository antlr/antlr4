package org.antlr.v4.test.runtime.java.api;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @since 4.10.2
 */
public class TestJSONTree {
	@Test
	public void testEmpty() {
		String input = "";
		VisitorCalcLexer lexer = new VisitorCalcLexer(new ANTLRInputStream(input));
		VisitorCalcParser parser = new VisitorCalcParser(new CommonTokenStream(lexer));

		ParseTree t = parser.s(); // rule s can match nothing
		String result = Trees.toJSONTree(t, parser);
		String expected = "'s'";
		expected = expected.replace('\'', '"');
		assertEquals(expected, result);
	}

	@Test
	public void testOneToken() {
		String input = "8";
		VisitorCalcLexer lexer = new VisitorCalcLexer(new ANTLRInputStream(input));
		VisitorCalcParser parser = new VisitorCalcParser(new CommonTokenStream(lexer));

		ParseTree t = parser.expr();
		String result = Trees.toJSONTree(t, parser);
		String expected = "{'expr':[{'idx':'0','text':'8'}]}";
		expected = expected.replace('\'', '"');
		assertEquals(expected, result);
	}

	@Test
	public void testOneRuleOneToken() {
		String input = "99";
		VisitorCalcLexer lexer = new VisitorCalcLexer(new ANTLRInputStream(input));
		VisitorCalcParser parser = new VisitorCalcParser(new CommonTokenStream(lexer));

		ParseTree t = parser.s();
		String result = Trees.toJSONTree(t, parser);
		String expected = "{'s':[{'expr':[{'idx':'0','text':'99'}]},{'idx':'1','text':'<EOF>'}]}";
		expected = expected.replace('\'', '"');
		assertEquals(expected, result);
	}

	@Test
	public void testExpr() {
		String input = "1 + 2";
		VisitorCalcLexer lexer = new VisitorCalcLexer(new ANTLRInputStream(input));
		VisitorCalcParser parser = new VisitorCalcParser(new CommonTokenStream(lexer));

		ParseTree t = parser.s();
		String result = Trees.toJSONTree(t, parser);
		System.out.println(result);
		String expected =
				"{'s':[{'expr':[{'expr':[{'idx':'0','text':'1'}]},{'idx':'2','text':'+'},"+
				"{'expr':[{'idx':'4','text':'2'}]}]},"+
				"{'idx':'5','text':'<EOF>'}]}";
		expected = expected.replace('\'', '"');
		assertEquals(expected, result);
	}
}
