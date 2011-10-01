import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class TestW {
	public static void main(String[] args) throws Exception {
		WLexer t = new WLexer(new ANTLRFileStream(args[0]));
		CommonTokenStream tokens = new CommonTokenStream(t);
//		tokens.fill();
//		for (Object tok : tokens.getTokens()) {
//			System.out.println(tok);
//		}
		WParser p = new WParser(tokens);
		p.setBuildParseTrees(true);
		p.s();
	}
}
