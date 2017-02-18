package org.antlr.v4.test.runtime.java.api;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.antlr.v4.runtime.tree.TreeRewriteSupport;
import org.antlr.v4.test.runtime.java.BaseJavaTest;
import org.junit.Before;
import org.junit.Test;

import static org.antlr.v4.test.runtime.java.api.TreeRootForTesting.TREE_NUMBER;
import static org.antlr.v4.test.runtime.java.api.TreeRootForTesting.toStringTreeWithIDs;
import static org.junit.Assert.assertEquals;

public class TestTreeRewriteVisitors extends BaseJavaTest {
	public static class MyVisitorCalcParser extends VisitorCalcParser {
		public MyVisitorCalcParser(TokenStream input) {
			super(input);
		}

		@Override
		public TerminalNode createTerminalNode(ParserRuleContext parent, Token t) {
			return new TreeRootForTesting.TerminalNodeForTesting(t);
		}
	}

	public static class MyVisitorBasicParser extends VisitorBasicParser {
		public MyVisitorBasicParser(TokenStream input) {
			super(input);
		}

		@Override
		public TerminalNode createTerminalNode(ParserRuleContext parent, Token t) {
			return new TreeRootForTesting.TerminalNodeForTesting(t);
		}
	}

	public static class IdentifyRewriteVisitor extends VisitorBasicBaseVisitor<ParseTree> {
		@Override
		public ParseTree visitChildren(RuleNode node) {
			return TreeRewriteSupport.rewriteChildren(this, node);
		}
		@Override
		public ParseTree visitTerminal(TerminalNode node) {
			return node;
		}
	}

	public static class ExprRewriteVisitor extends VisitorCalcBaseVisitor<ParseTree> {
		@Override
		public ParseTree visitChildren(RuleNode node) {
			return TreeRewriteSupport.rewriteChildren(this, node);
		}
		@Override
		public ParseTree visitTerminal(TerminalNode node) {
			return node;
		}
	}

	@Before
	public void setUp() { TREE_NUMBER.set(0); }

	// REPLACE

	@Test public void testNoReplacementTree() {
		Pair<Parser, ParseTree> parse = parse_s("A");
		Parser parser = parse.a;
		ParseTree tree = parse.b;
		VisitorBasicVisitor<ParseTree> identityVisitor = new IdentifyRewriteVisitor();

		assertEquals("(s@0 A@1 <EOF>@2)", toStringTreeWithIDs(tree,parser));       // original
		ParseTree replacement = identityVisitor.visit(tree);
		assertEquals("(s@0 A@1 <EOF>@2)", toStringTreeWithIDs(tree,parser));       // original should be unchanged

		assertEquals(true, isSameTree(tree, replacement));
	}

	@Test public void testReplaceTokenChildDupsRoot() {
		Pair<Parser, ParseTree> parse = parse_s("A");
		Parser parser = parse.a;
		ParseTree tree = parse.b;
		VisitorBasicVisitor<ParseTree> copyLeafs = new IdentifyRewriteVisitor() {
			@Override
			public ParseTree visitTerminal(TerminalNode node) {
				return new TreeRootForTesting.TerminalNodeForTesting(node.getSymbol()); // <-- make copy of A node
			}
		};

		assertEquals("(s@0 A@1 <EOF>@2)", toStringTreeWithIDs(tree,parser));       // original
		ParseTree replacement = copyLeafs.visit(tree);
		assertEquals("(s@0 A@1 <EOF>@2)", toStringTreeWithIDs(tree,parser));       // original should be unchanged
		assertEquals("(s@4 A@3 <EOF>@5)", toStringTreeWithIDs(replacement,parser));// new tree looks the same but different nodes

		assertEquals("A<EOF>", tree.getText());
		assertEquals("A<EOF>", replacement.getText());
	}

	@Test public void testReplaceRootKeepSameChildToken() {
		Pair<Parser, ParseTree> parse = parse_s("A");
		Parser parser = parse.a;
		ParseTree tree = parse.b;
		VisitorBasicVisitor<ParseTree> copyLeafs = new IdentifyRewriteVisitor() {
			@Override
			public ParseTree visitS(VisitorBasicParser.SContext ctx) {
				return TreeRewriteSupport.copy(ctx);
			}
		};

		assertEquals("(s@0 A@1 <EOF>@2)", toStringTreeWithIDs(tree,parser));       // original
		ParseTree replacement = copyLeafs.visit(tree);
		assertEquals("(s@0 A@1 <EOF>@2)", toStringTreeWithIDs(tree,parser));       // original should be unchanged
		assertEquals("(s@3 A@1 <EOF>@2)", toStringTreeWithIDs(replacement,parser));
		assertEquals("A<EOF>", tree.getText());
		assertEquals("A<EOF>", replacement.getText());
	}

	@Test public void testCopyAllTokens() {
		Pair<Parser, ParseTree> parse = parse_b("BAB");
		Parser parser = parse.a;
		ParseTree tree = parse.b;
		VisitorBasicVisitor<ParseTree> copyLeafs = new IdentifyRewriteVisitor() {
			@Override
			public ParseTree visitTerminal(TerminalNode node) {
				return new TreeRootForTesting.TerminalNodeForTesting(node.getSymbol()); // <-- make copy of all token node
			}
		};

		assertEquals("(b@0 (c@1 B@2 (c@3 A@4) B@5))", toStringTreeWithIDs(tree,parser));       // original
		ParseTree replacement = copyLeafs.visit(tree);
		assertEquals("(b@0 (c@1 B@2 (c@3 A@4) B@5))", toStringTreeWithIDs(tree,parser));       // original should be unchanged
		// new tree is all new since token copy forces new internal nodes
		assertEquals("(b@11 (c@7 B@6 (c@9 A@8) B@10))", toStringTreeWithIDs(replacement,parser));
		assertEquals("BAB", tree.getText());
		assertEquals("BAB", replacement.getText());
	}

	@Test public void testReplaceMiddleTokenChildDupsSpine() {
		Pair<Parser, ParseTree> parse = parse_b("BAB");
		Parser parser = parse.a;
		ParseTree tree = parse.b;
		VisitorBasicVisitor<ParseTree> copyLeafs = new IdentifyRewriteVisitor() {
			@Override
			public ParseTree visitTerminal(TerminalNode node) {
				if ( node.getSymbol().getText().equals("A") ) {
					// only copy the A node. Should force c and b nodes to dup but not other tokens
					return new TreeRootForTesting.TerminalNodeForTesting(node.getSymbol());
				}
				return node;
			}
		};

		assertEquals("(b@0 (c@1 B@2 (c@3 A@4) B@5))", toStringTreeWithIDs(tree,parser));       // original
		ParseTree replacement = copyLeafs.visit(tree);
		assertEquals("(b@0 (c@1 B@2 (c@3 A@4) B@5))", toStringTreeWithIDs(tree,parser));       // original should be unchanged
		// A, b, c should be different. B nodes should be same
		assertEquals("(b@9 (c@8 B@2 (c@7 A@6) B@5))", toStringTreeWithIDs(replacement,parser));
		assertEquals("BAB", tree.getText());
		assertEquals("BAB", replacement.getText());
	}

	@Test public void testReplaceMiddleTokenChildDupsDeeperSpine() {
		Pair<Parser, ParseTree> parse = parse_b("BBABB");
		Parser parser = parse.a;
		ParseTree tree = parse.b;
		VisitorBasicVisitor<ParseTree> copyLeafs = new IdentifyRewriteVisitor() {
			@Override
			public ParseTree visitTerminal(TerminalNode node) {
				if ( node.getSymbol().getText().equals("A") ) {
					// only copy the A node. Should force c and b nodes to dup but not other tokens
					return new TreeRootForTesting.TerminalNodeForTesting(node.getSymbol());
				}
				return node;
			}
		};

		assertEquals("(b@0 (c@1 B@2 (c@3 B@4 (c@5 A@6) B@7) B@8))", toStringTreeWithIDs(tree,parser));       // original
		ParseTree replacement = copyLeafs.visit(tree);
		assertEquals("(b@0 (c@1 B@2 (c@3 B@4 (c@5 A@6) B@7) B@8))", toStringTreeWithIDs(tree,parser));       // original should be unchanged
		// A and spine should be different. B nodes should be same
		assertEquals("(b@13 (c@12 B@2 (c@11 B@4 (c@10 A@9) B@7) B@8))", toStringTreeWithIDs(replacement,parser));
		assertEquals("BBABB", tree.getText());
		assertEquals("BBABB", replacement.getText());
	}

	@Test public void testReplaceOneRuleNodeNoAltLabel() {
		Pair<Parser, ParseTree> parse = parse_b("BAB");
		Parser parser = parse.a;
		ParseTree tree = parse.b;
		VisitorBasicVisitor<ParseTree> copyLeafs = new IdentifyRewriteVisitor() {
			@Override
			public ParseTree visitC(VisitorBasicParser.CContext ctx) {
				// pull child of (c A) up to replace subtree
				if ( ctx.getChildCount()==1 ) {
					return ctx.getChild(0);
				}
				return super.visitC(ctx);
			}
		};

		assertEquals("(b@0 (c@1 B@2 (c@3 A@4) B@5))", toStringTreeWithIDs(tree,parser));       // original
		ParseTree replacement = copyLeafs.visit(tree);
		assertEquals("(b@0 (c@1 B@2 (c@3 A@4) B@5))", toStringTreeWithIDs(tree,parser));       // original should be unchanged
		// A,B,C nodes are same but b and c must change to return immutable tree from root
		assertEquals("(b@7 (c@6 B@2 A@4 B@5))", toStringTreeWithIDs(replacement,parser));
		assertEquals("BAB", tree.getText());
		assertEquals("BAB", replacement.getText());
	}

	// DELETE

	@Test public void testDeleteSingleTokenDupsRoot() {
		Pair<Parser, ParseTree> parse = parse_s("A");
		Parser parser = parse.a;
		ParseTree tree = parse.b;
		VisitorBasicVisitor<ParseTree> copyLeafs = new IdentifyRewriteVisitor() {
			@Override
			public ParseTree visitTerminal(TerminalNode node) {
				if ( node.getText().equals("A") ) {
					return null;
				}
				return node;
			}
		};

		assertEquals("(s@0 A@1 <EOF>@2)", toStringTreeWithIDs(tree,parser));       // original
		ParseTree replacement = copyLeafs.visit(tree);
		assertEquals("(s@0 A@1 <EOF>@2)", toStringTreeWithIDs(tree,parser));       // original should be unchanged
		assertEquals("(s@3 <EOF>@2)", toStringTreeWithIDs(replacement,parser));
		assertEquals("A<EOF>", tree.getText());
		assertEquals("<EOF>", replacement.getText());
	}

	@Test public void testDeleteAllTokensDupsRoot() {
		Pair<Parser, ParseTree> parse = parse_s("A");
		Parser parser = parse.a;
		ParseTree tree = parse.b;
		VisitorBasicVisitor<ParseTree> copyLeafs = new IdentifyRewriteVisitor() {
			@Override
			public ParseTree visitTerminal(TerminalNode node) {
				return null; // kill all leaves
			}
		};

		assertEquals("(s@0 A@1 <EOF>@2)", toStringTreeWithIDs(tree,parser));       // original
		ParseTree replacement = copyLeafs.visit(tree);
		assertEquals("(s@0 A@1 <EOF>@2)", toStringTreeWithIDs(tree,parser));       // original should be unchanged
		assertEquals("s@3", toStringTreeWithIDs(replacement,parser));
		assertEquals("A<EOF>", tree.getText());
		assertEquals("", replacement.getText());
	}

	@Test public void testDeleteSingleTokenChildDupsRoot() {
		Pair<Parser, ParseTree> parse = parse_s("A");
		Parser parser = parse.a;
		ParseTree tree = parse.b;
		VisitorBasicVisitor<ParseTree> copyLeafs = new IdentifyRewriteVisitor() {
			@Override
			public ParseTree visitS(VisitorBasicParser.SContext ctx) {
				return TreeRewriteSupport.deleteChild(ctx, 0); // kill 1st child
			}
		};

		assertEquals("(s@0 A@1 <EOF>@2)", toStringTreeWithIDs(tree,parser));       // original
		ParseTree replacement = copyLeafs.visit(tree);
		assertEquals("(s@0 A@1 <EOF>@2)", toStringTreeWithIDs(tree,parser));       // original should be unchanged
		assertEquals("(s@3 <EOF>@2)", toStringTreeWithIDs(replacement,parser));
		assertEquals("A<EOF>", tree.getText());
		assertEquals("<EOF>", replacement.getText());
	}


	@Test public void testDeleteAllChildTokensDupsRoot() {
		Pair<Parser, ParseTree> parse = parse_s("A");
		Parser parser = parse.a;
		ParseTree tree = parse.b;
		VisitorBasicVisitor<ParseTree> copyLeafs = new IdentifyRewriteVisitor() {
			@Override
			public ParseTree visitS(VisitorBasicParser.SContext ctx) {
				ParserRuleContext t = TreeRewriteSupport.deleteChild(ctx, 0); // kill A
				return TreeRewriteSupport.deleteChild(t, 0); // kill EOF (same index due to deletion)
			}
		};

		assertEquals("(s@0 A@1 <EOF>@2)", toStringTreeWithIDs(tree,parser));       // original
		ParseTree replacement = copyLeafs.visit(tree);
		assertEquals("(s@0 A@1 <EOF>@2)", toStringTreeWithIDs(tree,parser));       // original should be unchanged
		assertEquals("s@4", toStringTreeWithIDs(replacement,parser));
		assertEquals("A<EOF>", tree.getText());
		assertEquals("", replacement.getText());
	}

	@Test public void testDeleteAllChildTokensViaSetChildrenDupsRoot() {
		Pair<Parser, ParseTree> parse = parse_s("A");
		Parser parser = parse.a;
		ParseTree tree = parse.b;
		VisitorBasicVisitor<ParseTree> copyLeafs = new IdentifyRewriteVisitor() {
			@Override
			public ParseTree visitS(VisitorBasicParser.SContext ctx) {
				return TreeRewriteSupport.setChildren(ctx, null); // wack kids
			}
		};

		assertEquals("(s@0 A@1 <EOF>@2)", toStringTreeWithIDs(tree,parser));       // original
		ParseTree replacement = copyLeafs.visit(tree);
		assertEquals("(s@0 A@1 <EOF>@2)", toStringTreeWithIDs(tree,parser));       // original should be unchanged
		assertEquals("s@3", toStringTreeWithIDs(replacement,parser));
		assertEquals("A<EOF>", tree.getText());
		assertEquals("", replacement.getText());
	}

	@Test public void testDeleteSubTreeDupsSpine() {
		Pair<Parser, ParseTree> parse = parse_b("BAB");
		Parser parser = parse.a;
		ParseTree tree = parse.b;
		VisitorBasicVisitor<ParseTree> copyLeafs = new IdentifyRewriteVisitor() {
			@Override
			public ParseTree visitC(VisitorBasicParser.CContext ctx) {
				if ( ctx.getChildCount()==1 ) { // must be the c : A alt
					return null;
				}
				// don't forget to walk the children if it's not the one we want to delete
				return visitChildren(ctx);
			}
		};

		assertEquals("(b@0 (c@1 B@2 (c@3 A@4) B@5))", toStringTreeWithIDs(tree,parser));       // original
		ParseTree replacement = copyLeafs.visit(tree);
		assertEquals("(b@0 (c@1 B@2 (c@3 A@4) B@5))", toStringTreeWithIDs(tree,parser));       // original should be unchanged
		assertEquals("(b@7 (c@6 B@2 B@5))", toStringTreeWithIDs(replacement,parser));
		assertEquals("BAB", tree.getText());
		assertEquals("BB", replacement.getText());
	}

	@Test public void testDeleteFirstChildDupsSpine() {
		Pair<Parser, ParseTree> parse = parse_b("BAB");
		Parser parser = parse.a;
		ParseTree tree = parse.b;
		VisitorBasicVisitor<ParseTree> copyLeafs = new IdentifyRewriteVisitor() {
			@Override
			public ParseTree visitC(VisitorBasicParser.CContext ctx) {
				if ( ctx.getChildCount()>1 ) { // must be the c : B c B alt
					return TreeRewriteSupport.deleteChild(ctx, 0); // kill first B
				}
				// don't forget to walk the children if it's not the one we want to delete
				return visitChildren(ctx);
			}
		};

		assertEquals("(b@0 (c@1 B@2 (c@3 A@4) B@5))", toStringTreeWithIDs(tree,parser));       // original
		ParseTree replacement = copyLeafs.visit(tree);
		assertEquals("(b@0 (c@1 B@2 (c@3 A@4) B@5))", toStringTreeWithIDs(tree,parser));       // original should be unchanged
		assertEquals("(b@7 (c@6 (c@3 A@4) B@5))", toStringTreeWithIDs(replacement,parser));
		assertEquals("BAB", tree.getText());
		assertEquals("AB", replacement.getText());
	}

	// ALT LABELS

	@Test public void testTimes0OptimizationAndAltLabels() {
		Pair<Parser, ParseTree> parse = parse_calc("0 * 9");
		Parser parser = parse.a;
		ParseTree tree = parse.b;
		ExprRewriteVisitor opt = new ExprRewriteVisitor() {
			@Override
			public ParseTree visitMultiply(VisitorCalcParser.MultiplyContext ctx) {
				if ( ctx.expr(0).getText().equals("0") ) return ctx.expr(0);
				if ( ctx.expr(1).getText().equals("0") ) return ctx.expr(1);
				return ctx;
			}
		};

		assertEquals("(s@0 (expr@5 (expr@2 0@3) *@6 (expr@8 9@9)) <EOF>@10)", toStringTreeWithIDs(tree,parser));       // original
		ParseTree replacement = opt.visit(tree);
		assertEquals("(s@0 (expr@5 (expr@2 0@3) *@6 (expr@8 9@9)) <EOF>@10)", toStringTreeWithIDs(tree,parser));       // original should be unchanged
		assertEquals("(s@11 (expr@2 0@3) <EOF>@10)", toStringTreeWithIDs(replacement,parser));
		assertEquals("0*9<EOF>", tree.getText());
		assertEquals("0<EOF>", replacement.getText());
	}

	@Test public void testTimes1OptimizationAndAltLabels() {
		Pair<Parser, ParseTree> parse = parse_calc("5 + 9 * 1");
		Parser parser = parse.a;
		ParseTree tree = parse.b;
		ExprRewriteVisitor opt = new ExprRewriteVisitor() {
			@Override
			public ParseTree visitMultiply(VisitorCalcParser.MultiplyContext ctx) {
				if ( ctx.expr(0).getText().equals("1") ) return ctx.expr(1);
				if ( ctx.expr(1).getText().equals("1") ) return ctx.expr(0);
				return ctx;
			}
		};

		assertEquals("(s@0 (expr@5 (expr@2 5@3) +@6 (expr@11 (expr@8 9@9) *@12 (expr@14 1@15))) <EOF>@16)", toStringTreeWithIDs(tree,parser));       // original
		ParseTree replacement = opt.visit(tree);
		assertEquals("(s@0 (expr@5 (expr@2 5@3) +@6 (expr@11 (expr@8 9@9) *@12 (expr@14 1@15))) <EOF>@16)", toStringTreeWithIDs(tree,parser));       // original should be unchanged
		assertEquals("(s@18 (expr@17 (expr@2 5@3) +@6 (expr@8 9@9)) <EOF>@16)", toStringTreeWithIDs(replacement,parser));
		// make sure copy of expr@17 is alt label version not generic ExprContext
		assertEquals("AddContext", replacement.getChild(0).getClass().getSimpleName());
		assertEquals("5+9*1<EOF>", tree.getText());
		assertEquals("5+9<EOF>", replacement.getText());
	}


	// SUPPORT

	protected static boolean isSameTree(ParseTree t, ParseTree u) {
		if ( t instanceof TerminalNodeImpl ) { // must assume an implementation to alter
			return t==u;
		}
		if ( t.getChildCount()!=u.getChildCount() ) {
			return false;
		}
		int n = t.getChildCount();
		for (int i = 0; i<n; i++) {
			boolean csame = isSameTree(t.getChild(i), u.getChild(i));
			if ( !csame ) return false;
		}
		return true;
	}


	public Pair<Parser, ParseTree> parse_s(String input) {
		VisitorBasicLexer lexer = new VisitorBasicLexer(new ANTLRInputStream(input));
		MyVisitorBasicParser parser = new MyVisitorBasicParser(new CommonTokenStream(lexer));
		ParseTree tree = parser.s();
		return new Pair<>((Parser)parser, tree);
	}

	public Pair<Parser, ParseTree> parse_b(String input) {
		VisitorBasicLexer lexer = new VisitorBasicLexer(new ANTLRInputStream(input));
		MyVisitorBasicParser parser = new MyVisitorBasicParser(new CommonTokenStream(lexer));
		ParseTree tree = parser.b();
//		System.out.println(toStringTreeWithIDs(tree,parser));
		return new Pair<>((Parser)parser, tree);
	}

	public Pair<Parser, ParseTree> parse_calc(String input) {
		VisitorCalcLexer lexer = new VisitorCalcLexer(new ANTLRInputStream(input));
		VisitorCalcParser parser = new MyVisitorCalcParser(new CommonTokenStream(lexer));
		ParseTree tree = parser.s();
//		System.out.println(toStringTreeWithIDs(tree,parser));
		return new Pair<>((Parser)parser, tree);
	}
}
