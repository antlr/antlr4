import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.CommonASTNodeStream;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.TreeNodeStream;

public class TestU {
	public static void main(String[] args) throws Exception {
		ULexer t = new ULexer(new ANTLRFileStream(args[0]));
		CommonTokenStream tokens = new CommonTokenStream(t);
		UParser p = new UParser(tokens);
		p.setBuildParseTrees(true);
		UParser.aContext ctx = p.a();

		System.out.println(((Tree) ctx.tree).toStringTree());

		TreeNodeStream nodes = new CommonASTNodeStream(ctx.tree);
		UWalker walker = new UWalker(nodes);
		walker.a();
	}
}
