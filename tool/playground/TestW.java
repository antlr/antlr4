import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;

public class TestW {
	public static void main(String[] args) throws Exception {
		WLexer t = new WLexer(new ANTLRFileStream(args[0]));
		CommonTokenStream tokens = new CommonTokenStream(t);
		WParser p = new WParser(tokens);
		p.setBuildParseTree(true);
		RuleContext ctx = p.s();
		//System.out.println("ctx="+ctx.toStringTree(p));
	}
}
