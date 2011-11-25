import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class TestU {
	public static void main(String[] args) throws Exception {
		ULexer t = new ULexer(new ANTLRFileStream(args[0]));
		CommonTokenStream tokens = new CommonTokenStream(t);
		UParser p = new UParser(tokens);
        p.setListener(new MyUListener(p));
        p.s();
//		p.setBuildParseTree(true);
//		UParser.aContext ctx = p.a();
	}
}
