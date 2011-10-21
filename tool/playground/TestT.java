import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.tree.gui.*;

import java.awt.*;

public class TestT {

	public static class MyTreeTextProvider implements TreeTextProvider {
		BaseRecognizer parser;
		public MyTreeTextProvider(BaseRecognizer parser) {
			this.parser = parser;
		}

		@Override
		public String getText(Tree node) {
			return String.valueOf(Trees.getNodeText(node, parser));
		}
	}

	public static void main(String[] args) throws Exception {
		TLexer t = new TLexer(new ANTLRFileStream(args[0]));
		CommonTokenStream tokens = new CommonTokenStream(t);
//		tokens.fill();
//		for (Object tok : tokens.getTokens()) {
//			System.out.println(tok);
//		}
		TParser p = new TParser(tokens);
		p.setBuildParseTree(true);
		TParser.sContext tree = p.s();

		System.out.println(tree.toStringTree(p));
		TreeViewer tv = new TreeViewer(tree);
		tv.setTreeTextProvider(new MyTreeTextProvider(p));
		tv.setBoxColor(Color.lightGray);
		tv.setBorderColor(Color.darkGray);
		tv.open();
//
//		ParseTreeWalker walker = new ParseTreeWalker();
//		TListener listener = new BlankTListener() {
//			public void enterEveryRule(ParserRuleContext ctx) {
//				System.out.println("enter rule "+TParser.ruleNames[ctx.ruleIndex]);
//			}
//			public void exitRule(TParser.DoIfContext ctx) { // specific to rule ifstat
//				System.out.println("exit rule ifstat");
//			}
//		};
//		walker.walk(listener, tree);
	}
}
