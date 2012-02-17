import org.antlr.v4.runtime.tree.ParseTreeVisitor;

public class ABaseVisitor<T> extends ParseTreeVisitor<T> implements AVisitor<T> {
	public T visit(AParser.MultContext ctx) { visitChildren(ctx); return null; }
	public T visit(AParser.ParensContext ctx) { visitChildren(ctx); return null; }
	public T visit(AParser.sContext ctx) { visitChildren(ctx); return null; }
	public T visit(AParser.FooContext ctx) { visitChildren(ctx); return null; }
	public T visit(AParser.AddContext ctx) { visitChildren(ctx); return null; }
	public T visit(AParser.IntContext ctx) { visitChildren(ctx); return null; }
}
