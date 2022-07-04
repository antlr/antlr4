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
		String result = Trees.toJSON(t, parser);
		String expected = "{'tree':'0'}";
		expected = expected.replace('\'', '"');
		assertEquals(expected, result);
	}

	@Test
	public void testOneToken() {
		String input = "8";
		VisitorCalcLexer lexer = new VisitorCalcLexer(new ANTLRInputStream(input));
		VisitorCalcParser parser = new VisitorCalcParser(new CommonTokenStream(lexer));

		ParseTree t = parser.expr();
		String result = Trees.toJSON(t, parser);
		String expected = "{'tree':{'1':[0]}}";
		expected = expected.replace('\'', '"');
		assertEquals(expected, result);
	}

	@Test
	public void testOneRuleOneToken() {
		String input = "99";
		VisitorCalcLexer lexer = new VisitorCalcLexer(new ANTLRInputStream(input));
		VisitorCalcParser parser = new VisitorCalcParser(new CommonTokenStream(lexer));

		ParseTree t = parser.s();
		String result = Trees.toJSON(t, parser);
		String expected = "{'tree':{'0':[{'1':[0]},1]}}";
		expected = expected.replace('\'', '"');
		assertEquals(expected, result);
	}

	@Test
	public void testExpr() {
		String input = "1 + 2";
		VisitorCalcLexer lexer = new VisitorCalcLexer(new ANTLRInputStream(input));
		VisitorCalcParser parser = new VisitorCalcParser(new CommonTokenStream(lexer));

		ParseTree t = parser.s();
		String result = Trees.toJSON(t, parser);
		System.out.println(result);
		String expected =
				"{'s':[{'expr':[{'expr':[0]},2,{'expr':[4]}]},5]}";
		expected = expected.replace('\'', '"');
		assertEquals(expected, result);
	}

	@Test
	public void testMismatchedToken() {
		String input = "f(";
		VisitorCalcLexer lexer = new VisitorCalcLexer(new ANTLRInputStream(input));
		VisitorCalcParser parser = new VisitorCalcParser(new CommonTokenStream(lexer));
		parser.removeErrorListeners(); // Turn off error msgs.

		ParseTree t = parser.expr();
		String result = Trees.toJSON(t, parser);
		String expected =
				"{'expr':[0,1,{'error':'<missing ')'>'}]}";
		expected = expected.replace('\'', '"');
		expected = expected.replace("')'", "')'"); // undo error msg tweak
		assertEquals(expected, result);
	}
}
