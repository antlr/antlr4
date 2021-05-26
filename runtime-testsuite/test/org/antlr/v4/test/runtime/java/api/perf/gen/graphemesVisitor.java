// Generated from /Users/julianbissekkou/Documents/development/tapped/antlr4/runtime-testsuite/test/org/antlr/v4/test/runtime/java/api/perf/graphemes.g4 by ANTLR 4.9.1
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link graphemesParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface graphemesVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link graphemesParser#emoji_sequence}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmoji_sequence(graphemesParser.Emoji_sequenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link graphemesParser#grapheme_cluster}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGrapheme_cluster(graphemesParser.Grapheme_clusterContext ctx);
	/**
	 * Visit a parse tree produced by {@link graphemesParser#graphemes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGraphemes(graphemesParser.GraphemesContext ctx);
}