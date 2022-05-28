public class Profile {
	DecisionInfo[] testInterp(String lexerText, String parserText,
							  String startRule, String input)
	{
		LexerGrammar lg = new LexerGrammar(lexerText);
		Grammar g = new Grammar(parserText);
		LexerInterpreter lexEngine = lg.createLexerInterpreter(new ANTLRInputStream(input));
		CommonTokenStream tokens = new CommonTokenStream(lexEngine);
		GrammarParserInterpreter parser = g.createGrammarParserInterpreter(tokens);
		parser.setProfile(true);

		Rule r = g.rules.get(startRule);
		if (r == null) {
			return parser.getParseInfo().getDecisionInfo();
		}
		ParseTree t = parser.parse(r.index);
		return parser.getParseInfo().getDecisionInfo();
	}

	public static void main(String[] args) {
	}
}
