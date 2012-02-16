public interface AVisitor<T> {
	T visit(AParser.MultContext ctx);
	T visit(AParser.ParensContext ctx);
	T visit(AParser.sContext ctx);
	T visit(AParser.AddContext ctx);
	T visit(AParser.IntContext ctx);
}