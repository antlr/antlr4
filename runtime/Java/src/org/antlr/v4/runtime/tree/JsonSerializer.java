package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** This "class" wraps support functions that generate JSON for parse trees.
 *  The JSON includes everything needed to reconstruct a parse tree:
 *
 *  	Rule names (field: "rules")
 *  	Input chars (field: "input")
 *  	Tokens (field: "tokens")
 *  	Parse tree (field: "tree"; refs rule indexes and token indexes)
 *
 *  For example, given input "99" and a simple expression grammar giving parse tree
 *  "(s (expr 99) EOF)", the full JSON (formatted by jq) looks like:
 *
 * 	{
 * 	  "rules": [
 * 	    "s",
 * 	    "expr"
 * 	  ],
 * 	  "input": "99",
 * 	  "tokens": [
 * 	    {
 * 	      "type": 3,
 * 	      "line": 1,
 * 	      "pos": 0,
 * 	      "channel": 0,
 * 	      "start": 0,
 * 	      "stop": 1
 * 	    },
 * 	    {
 * 	      "type": -1,
 * 	      "line": 1,
 * 	      "pos": 2,
 * 	      "channel": 0,
 * 	      "start": 2,
 * 	      "stop": 1
 * 	    }
 * 	  ],
 * 	  "tree": {
 * 	    "0": [
 * 	      {
 * 	        "1": [
 * 	          0
 * 	        ]
 * 	      },
 * 	      1
 * 	    ]
 * 	  }
 * 	}
 *
 *  Notice that the tree is just a series of nested references to integers, which refer to rules
 *  and tokens.
 *
 *  One potential use case: Create an ANTLR server that accepts a grammar and input as parameters then
 *  returns JSON for the parse tree and the tokens.  This can be deserialized by JavaScript in a web browser
 *  to display the parse result.
 *
 *  To load and dump elements from Python 3:
 *
 *    import json
 *
 *    with open("/tmp/t.json") as f:
 *         data = f.read()
 *
 *    data = json.loads(data)
 *    print(data['rules'])
 *    print(data['input'])
 *    for t in data['tokens']:
 *         print(t)
 *    print(data['tree'])
 *
 *  @since 4.10.2
 */
public class JsonSerializer {
	/** Create a JSON representation of a parse tree and include all other information necessary to reconstruct
	 *  a printable parse tree: the rules, input, tokens, and the tree structure that refers to the rule
	 *  and token indexes.  Extract all information from the parser, which is assumed to be in a state
	 *  post-parse and the object that created tree t.
	 *
	 * @param t The parse tree to serialize as JSON
	 * @param recog The parser that created the parse tree and is in the post-recognition state
	 * @return JSON representing the parse tree
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

	/** Create a JSON representation of a parse tree and include all other information necessary to reconstruct
	 *  a printable parse tree: the rules, input, tokens, and the tree structure that refers to the rule
	 *  and token indexes.  The tree and rule names are required but the token stream and input stream are optional.
	 */
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

	/** Create a JSON representation of a parse tree. The tree is just a series of nested references
	 *  to integers, which refer to rules and tokens.
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

	/** Create appropriate JSON text for a tree node */
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
