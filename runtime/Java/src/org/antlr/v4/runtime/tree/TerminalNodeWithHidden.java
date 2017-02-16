package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

import java.util.List;

/** Track text of hidden channel tokens to left and right of terminal node
 *  according to the rules borrowed from Roslyn on trivia:
 *
	 "In general, a token owns any trivia after it on the same line up to
	  the next token. Any trivia after that line is associated with the
	  following token. The first token in the source file gets all the
	  initial trivia, and the last sequence of trivia in the file is
	  tacked onto the end-of-file token, which otherwise has zero width."

 *  These rules are implemented by in {@link #collectHiddenTokens}.
 *
 *  To use this class, override these methods to create
 *  TerminalNodeWithHidden nodes:
 *  {@link org.antlr.v4.runtime.Parser#createErrorNode(Token)} and
 *  {@link org.antlr.v4.runtime.Parser#createTerminalNode(Token)}.
 *
 * @since 4.6.1
 */
public class TerminalNodeWithHidden extends TerminalNodeImpl {
	/** Token index into token stream of first hidden token associated
	 *  with this node to the left.  All tokens from hiddenStartLeft to
	 *  {@link Token#getTokenIndex}-1 inclusively are the hidden tokens
	 *  to the left.
	 */
//	protected int hiddenStartLeft;

	/** Token index into token stream of last hidden token associated
	 *  with this node to the right.  All tokens from
	 *  {@link Token#getTokenIndex}+1 to hiddenStartLeft inclusively
	 *  are the hidden tokens to the right.
	 */
//	protected int hiddenStopRight;

	protected String hiddenLeft;
	protected String hiddenRight;

	public TerminalNodeWithHidden(BufferedTokenStream tokens, int channel, Token symbol) {
		super(symbol);
		collectHiddenTokens(tokens, channel, symbol);
	}

	// TODO: how to collect hidden on error nodes (deleted, inserted, during recovery)
	protected void collectHiddenTokens(BufferedTokenStream tokens, int channel, Token symbol) {
		List<Token> left  = tokens.getHiddenTokensToLeft(symbol.getTokenIndex(), channel);
		if ( left!=null ) {
			Token firstHiddenLeft = left.get(0);
			Token prevReal = null;
			if ( firstHiddenLeft.getTokenIndex()>0 ) {
				prevReal = tokens.get(firstHiddenLeft.getTokenIndex()-1);
			}
			if ( prevReal==null ) { // this symbol is first real token of file
				hiddenRight = tokens.getText(Interval.of(0, symbol.getTokenIndex()-1));
			}
			else {
				// collect all token text on next line after prev real
				StringBuilder buf = new StringBuilder();
				for (Token t : left) {
					if ( t.getLine()>prevReal.getLine() ) {
						buf.append(t.getText());
					}
				}
				hiddenLeft = buf.toString();
			}
		}

		List<Token> right = tokens.getHiddenTokensToRight(symbol.getTokenIndex(), channel);
		if ( right!=null ) {
			Token lastHiddenRight = right.get(right.size()-1);
			Token nextReal = null;
			if ( symbol.getType()!=Token.EOF ) {
				nextReal = tokens.get(lastHiddenRight.getTokenIndex()+1);
			}
			// If this is last real token, collect all hidden to right
			StringBuilder buf = new StringBuilder();
			if ( nextReal.getType()==Token.EOF ) {
				hiddenRight = tokens.getText(right.get(0), nextReal);
			}
			else {
				// collect all token text on same line to right
				int tokenLine = symbol.getLine();
				for (Token t : right) {
					if ( t.getLine()==tokenLine ) {
						buf.append(t.getText());
					}
				}
				hiddenRight = buf.toString();
			}
		}
	}

	public String getHiddenLeft() {
		return hiddenLeft;
	}

	public String getHiddenRight() {
		return hiddenRight;
	}

	public void setHiddenLeft(String hiddenLeft) {
		this.hiddenLeft = hiddenLeft;
	}

	public void setHiddenRight(String hiddenRight) {
		this.hiddenRight = hiddenRight;
	}

	@Override
	public String getText() {
		StringBuilder buf = new StringBuilder();
		if ( hiddenLeft!=null ) buf.append(hiddenLeft);
		buf.append(super.getText());
		if ( hiddenRight!=null ) buf.append(hiddenRight);
		return buf.toString();
	}
}
