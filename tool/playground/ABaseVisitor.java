import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

public class ABaseVisitor<T> extends ParseTreeVisitor<T> implements AVisitor<T> {
	public T visit(AParser.MultContext ctx) { return null; }
	public T visit(AParser.ParensContext ctx) { return null; }
	public T visit(AParser.eContext ctx) { return null; }
	public T visit(AParser.sContext ctx) { return null; }
	public T visit(AParser.AddContext ctx) { return null; }
	public T visit(AParser.IntContext ctx) { return null; }
}
