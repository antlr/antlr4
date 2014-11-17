using System;
using Antlr4.Runtime;
using Antlr4.Runtime.Tree;

public class Test {
    public static void Main(string[] args) {
        ICharStream input = new AntlrFileStream("../../input");
        TLexer lex = new TLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lex);
                TParser parser = new TParser(tokens);
                parser.AddErrorListener(new DiagnosticErrorListener());

		 parser.BuildParseTree = true;
        ParserRuleContext tree = parser.s();
        ParseTreeWalker.Default.Walk(new TreeShapeListener(), tree);
    }
}

class TreeShapeListener : IParseTreeListener {
	public void VisitTerminal(ITerminalNode node) { }
	public void VisitErrorNode(IErrorNode node) { }
	public void ExitEveryRule(ParserRuleContext ctx) { }

	public void EnterEveryRule(ParserRuleContext ctx) {
		for (int i = 0; i < ctx.ChildCount; i++) {
			IParseTree parent = ctx.GetChild(i).Parent;
			if (!(parent is IRuleNode) || ((IRuleNode)parent).RuleContext != ctx) {
				throw new Exception("Invalid parse tree shape detected.");
			}
		}
	}
}