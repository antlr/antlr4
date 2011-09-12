import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class TestT {
	public static void main(String[] args) throws Exception {
		TLexer t = new TLexer(new ANTLRFileStream(args[0]));
		CommonTokenStream tokens = new CommonTokenStream(t);
//		tokens.fill();
//		for (Object tok : tokens.getTokens()) {
//			System.out.println(tok);
//		}
		TParser p = new TParser(tokens);
		p.setBuildParseTrees(true);
		TParser.sContext tree = p.s();

		System.out.println(tree.toStringTree(p));

		ParseTreeWalker walker = new ParseTreeWalker();
		TListener listener = new BlankTListener() {
			public void enterEveryRule(ParserRuleContext ctx) {
				System.out.println("enter rule "+TParser.ruleNames[ctx.ruleIndex]);
			}
			public void exitRule(TParser.DoIfContext ctx) { // specific to rule ifstat
				System.out.println("exit rule ifstat");
			}
		};
		walker.walk(listener, tree);
	}
}
