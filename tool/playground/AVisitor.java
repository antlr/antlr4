import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;

public interface AVisitor<T> {
	T visitMult(AParser.MultContext ctx);
	T visitParens(AParser.ParensContext ctx);
	T visitS(AParser.SContext ctx);
	T visitPrimary(AParser.PrimaryContext ctx);
	T visitAdd(AParser.AddContext ctx);
}