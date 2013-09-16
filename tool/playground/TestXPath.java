import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.IOException;

public class TestXPath {
	public static void main(String[] args) throws IOException {
		CharStream input = new ANTLRFileStream("TestXPath.java");
		JavaLexer lex = new JavaLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lex);
		JavaParser parser = new JavaParser(tokens);

		parser.setBuildParseTree(true);
		ParserRuleContext tree = parser.compilationUnit();
		System.out.println(tree.toStringTree(parser));

//		"/compilationUnit/*"
//		"//blockStatement"
//		"//StringLiteral"
//		"//Identifier"
//		"//expression/primary/Identifier"
//		"//primary/*"
//		"//expression//Identifier"
		for (ParseTree t : tree.findAll(parser, "//expression//Identifier") ) {
			if ( t instanceof RuleContext ) {
				RuleContext r = (RuleContext)t;
				System.out.println("  "+parser.getRuleNames()[r.getRuleIndex()]);
			}
			else {
				TerminalNode token = (TerminalNode)t;
				System.out.println("  "+token.getText());
			}
		}
	}
}

