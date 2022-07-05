package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @since 4.10.2
 */
public class JsonSerializer {
	/** Print out a whole tree in JSON form. {@link #getNodeText} is used on the
	 *  node payloads to get the text for the nodes.  Detect
	 *  parse trees and extract data appropriately. Include rulenames, input, tokens.
	 */
	public static String toJSON(Tree t, Parser recog) {
		String[] ruleNames = recog != null ? recog.getRuleNames() : null;
		if ( t==null || ruleNames==null ) {
			return null;
		}
		TokenStream tokenStream = recog.getInputStream();
		CharStream inputStream = tokenStream.getTokenSource().getInputStream();
		return toJSON(t, Arrays.asList(ruleNames), tokenStream, inputStream);
	}

	public static String toJSON(Tree t,
								final List<String> ruleNames,
								final TokenStream tokenStream,
								final CharStream inputStream)
	{
		if ( t==null || ruleNames==null ) {
			return null;
		}

		StringBuilder buf = new StringBuilder();
		buf.append("{");
		buf.append("\"rules\":[\"");
		buf.append(String.join("\",\"", ruleNames));
		buf.append("\"],");

		if ( inputStream!=null ) {
			Interval allchar = Interval.of(0, inputStream.size() - 1);
			String input = inputStream.getText(allchar);
			input = Utils.escapeJSONString(input);
			buf.append("\"input\":\"");
			buf.append(input);
			buf.append("\",");
		}

		if ( tokenStream!=null ) {
			List<String> tokenStrings = new ArrayList<>();
			for (int i = 0; i < tokenStream.size(); i++) {
				Token tok = tokenStream.get(i);
				String s = String.format("{\"type\":%d,\"line\":%d,\"pos\":%d,\"channel\":%d,\"start\":%d,\"stop\":%d}",
						tok.getType(), tok.getLine(), tok.getCharPositionInLine(), tok.getChannel(),
						tok.getStartIndex(), tok.getStopIndex());
				tokenStrings.add(s);
			}
			buf.append("\"tokens\":[");
			buf.append(String.join(",", tokenStrings));
			buf.append("],");
		}

		String tree = toJSONTree(t);
		buf.append("\"tree\":");
		buf.append(tree);
		buf.append("}");

		return buf.toString();
	}

	/** Print out a whole tree in JSON form. {@link #getNodeText} is used on the
	 *  node payloads to get the text for the nodes.  Detect
	 *  parse trees and extract data appropriately.
	 * @since 4.10.2
	 */
	public static String toJSONTree(final Tree t) {
		StringBuilder buf = new StringBuilder();
		if ( t.getChildCount()==0 ) {
			return getJSONNodeText(t);
		}
		buf.append("{");
		buf.append(getJSONNodeText(t));
		buf.append(":[");
		for (int i = 0; i<t.getChildCount(); i++) {
			if ( i>0 ) buf.append(',');
			buf.append(toJSONTree(t.getChild(i)));
		}
		buf.append("]");
		buf.append("}");
		return buf.toString();
	}

	/** @since 4.10.2 */
	public static String getJSONNodeText(Tree t) {
		if ( t instanceof RuleContext) {
			int ruleIndex = ((RuleContext)t).getRuleContext().getRuleIndex();
			int altNumber = ((RuleContext) t).getAltNumber();
			if ( altNumber!= ATN.INVALID_ALT_NUMBER ) {
				return String.format("\"%d:%d\"",ruleIndex,altNumber);
			}
			return String.format("\"%d\"",ruleIndex);
		}
		else if ( t instanceof ErrorNode) {
			Token symbol = ((TerminalNode)t).getSymbol();
			if (symbol != null) {
				return "{\"error\":\"" + symbol.getText() + "\"}";
			}
			return "{\"error\":\""+t.getPayload().toString()+"\"}";
		}
		else if ( t instanceof TerminalNode) {
			Token symbol = ((TerminalNode)t).getSymbol();
			if (symbol != null) {
				return String.valueOf(symbol.getTokenIndex());
			}
			return "-1";
		}
		return "<unknown node type>";
	}
}
