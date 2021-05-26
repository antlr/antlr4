// Generated from /Users/julianbissekkou/Documents/development/tapped/antlr4/runtime-testsuite/test/org/antlr/v4/test/runtime/java/api/perf/graphemes.g4 by ANTLR 4.9.1
package org.antlr.v4.test.runtime.java.api.perf;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link graphemesParser}.
 */
public interface graphemesListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link graphemesParser#emoji_sequence}.
	 * @param ctx the parse tree
	 */
	void enterEmoji_sequence(graphemesParser.Emoji_sequenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link graphemesParser#emoji_sequence}.
	 * @param ctx the parse tree
	 */
	void exitEmoji_sequence(graphemesParser.Emoji_sequenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link graphemesParser#grapheme_cluster}.
	 * @param ctx the parse tree
	 */
	void enterGrapheme_cluster(graphemesParser.Grapheme_clusterContext ctx);
	/**
	 * Exit a parse tree produced by {@link graphemesParser#grapheme_cluster}.
	 * @param ctx the parse tree
	 */
	void exitGrapheme_cluster(graphemesParser.Grapheme_clusterContext ctx);
	/**
	 * Enter a parse tree produced by {@link graphemesParser#graphemes}.
	 * @param ctx the parse tree
	 */
	void enterGraphemes(graphemesParser.GraphemesContext ctx);
	/**
	 * Exit a parse tree produced by {@link graphemesParser#graphemes}.
	 * @param ctx the parse tree
	 */
	void exitGraphemes(graphemesParser.GraphemesContext ctx);
}