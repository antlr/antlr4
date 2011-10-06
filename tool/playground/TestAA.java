import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;

public class TestAA {
	public static void main(String[] args) throws Exception {
		AALexer t = new AALexer(new ANTLRFileStream(args[0]));
		CommonTokenStream tokens = new CommonTokenStream(t);
		AAParser p = new AAParser(tokens);
		p.setBuildParseTrees(true);
		RuleContext ctx = p.prog();
		//System.out.println("ctx="+ctx.toStringTree(p));
	}
}
