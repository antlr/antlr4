package org.antlr.v4.tool;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.TreeWizard;
import org.antlr.v4.Tool;
import org.antlr.v4.misc.*;
import org.antlr.v4.parse.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.*;

public class Grammar implements AttributeResolver {
	public static final Set doNotCopyOptionsToLexer =
        new HashSet() {
            {
                add("output"); add("ASTLabelType"); add("superClass");
                add("k"); add("backtrack"); add("memoize"); add("rewrite");
            }
        };

    public static Map<String, AttributeDict> grammarAndLabelRefTypeToScope =
        new HashMap<String, AttributeDict>() {{
            put("lexer:RULE_LABEL", Rule.predefinedLexerRulePropertiesDict);
            put("lexer:LEXER_STRING_LABEL", Rule.predefinedLexerRulePropertiesDict);
			put("lexer:TOKEN_LABEL", AttributeDict.predefinedTokenDict);
            put("parser:RULE_LABEL", Rule.predefinedRulePropertiesDict);
            put("parser:TOKEN_LABEL", AttributeDict.predefinedTokenDict);
            put("tree:RULE_LABEL", Rule.predefinedTreeRulePropertiesDict);
            put("tree:TOKEN_LABEL", AttributeDict.predefinedTokenDict);
            put("tree:WILDCARD_TREE_LABEL", AttributeDict.predefinedTokenDict);
            put("combined:RULE_LABEL", Rule.predefinedRulePropertiesDict);
            put("combined:TOKEN_LABEL", AttributeDict.predefinedTokenDict);
		}};
	public static final int MIN_CHAR_VALUE = '\u0000';
	public static final int MAX_CHAR_VALUE = '\uFFFE';

	public String name;
    public GrammarRootAST ast;
    public String text; // testing only
    public String fileName;

    /** Was this created from a COMBINED grammar? */
    public Grammar implicitLexer;
    public Grammar implicitLexerOwner;

    /** If we're imported, who imported us? If null, implies grammar is root */
    public Grammar parent;
    public List<Grammar> importedGrammars;

	/** All rules defined in this specific grammar, not imported. Also does
	 *  not include lexical rules if combined.
	 */
    public OrderedHashMap<String, Rule> rules = new OrderedHashMap<String, Rule>();
	int ruleNumber = 0; // used to get rule indexes (0..n-1)

	/** The ATN that represents the grammar with edges labelled with tokens
	 *  or epsilon.  It is more suitable to analysis than an AST representation.
	 */
	public ATN atn;

	public Map<Integer, DFA> decisionDFAs = new HashMap<Integer, DFA>();

	public Vector<IntervalSet[]> decisionLOOK;

	public Tool tool;

	/** Token names and literal tokens like "void" are uniquely indexed.
	 *  with -1 implying EOF.  Characters are different; they go from
	 *  -1 (EOF) to \uFFFE.  For example, 0 could be a binary byte you
	 *  want to lexer.  Labels of DFA/ATN transitions can be both tokens
	 *  and characters.  I use negative numbers for bookkeeping labels
	 *  like EPSILON. Char/String literals and token types overlap in the same
	 *  space, however.
	 */
	int maxTokenType = Token.MIN_USER_TOKEN_TYPE -1;

	/** Map token like ID (but not literals like "while") to its token type */
	public Map<String, Integer> tokenNameToTypeMap = new LinkedHashMap<String, Integer>();

	/** Map token literals like "while" to its token type.  It may be that
	 *  WHILE="while"=35, in which case both tokenIDToTypeMap and this
	 *  field will have entries both mapped to 35.
	 */
	public Map<String, Integer> stringLiteralToTypeMap = new LinkedHashMap<String, Integer>();
	/** Reverse index for stringLiteralToTypeMap.  Indexed with raw token type.
	 *  0 is invalid. */
	public Vector<String> typeToStringLiteralList = new Vector<String>();

	/** Map a token type to its token name. Indexed with raw token type.
	 *  0 is invalid.
	 */
	public Vector<String> typeToTokenList = new Vector<String>();

    /** Map a name to an action.
     *  The code generator will use this to fill holes in the output files.
     *  I track the AST node for the action in case I need the line number
     *  for errors.
     */
	public Map<String,ActionAST> namedActions = new HashMap<String,ActionAST>();


	/** Tracks all forced actions in all alternatives of all rules.
	 *  Or if lexer all rules period. Doesn't track sempreds.
	 *  maps tree node to action index.
 	 */
	public LinkedHashMap<ActionAST, Integer> actions = new LinkedHashMap<ActionAST, Integer>();

	/** All sempreds found in grammar; maps tree node to sempred index */
	public LinkedHashMap<PredAST, Integer> sempreds = new LinkedHashMap<PredAST, Integer>();

    public Map<String, AttributeDict> scopes = new LinkedHashMap<String, AttributeDict>();
	public static final String AUTO_GENERATED_TOKEN_NAME_PREFIX = "T__";

	public Grammar(Tool tool, GrammarRootAST ast) {
        if ( ast==null ) throw new IllegalArgumentException("can't pass null tree");
        this.tool = tool;
        this.ast = ast;
        this.name = ((GrammarAST)ast.getChild(0)).getText();
		initTokenSymbolTables();
    }

	/** For testing */
	public Grammar(String grammarText) throws org.antlr.runtime.RecognitionException {
		this("<string>", grammarText, null);
	}

	/** For testing */
	public Grammar(String grammarText, ANTLRToolListener listener)
		throws org.antlr.runtime.RecognitionException
	{
		this("<string>", grammarText, listener);
	}

	/** For testing; only builds trees; no sem anal */
	public Grammar(String fileName, String grammarText, ANTLRToolListener listener)
		throws org.antlr.runtime.RecognitionException
	{
        this.text = grammarText;
		this.fileName = fileName;
		this.tool = new Tool();
		this.tool.addListener(listener);
		org.antlr.runtime.ANTLRStringStream in = new org.antlr.runtime.ANTLRStringStream(grammarText);
		in.name = fileName;
		ANTLRLexer lexer = new ANTLRLexer(in);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ToolANTLRParser p = new ToolANTLRParser(tokens,tool);
		p.setTreeAdaptor(new GrammarASTAdaptor(in));
		ParserRuleReturnScope r = p.grammarSpec();
		if ( r.getTree() instanceof GrammarRootAST ) {
			this.ast = (GrammarRootAST)r.getTree();
			this.ast.hasErrors = p.getNumberOfSyntaxErrors()>0;
			this.name = ((GrammarAST)ast.getChild(0)).getText();
		}
		initTokenSymbolTables();
    }

	protected void initTokenSymbolTables() {
		if ( isTreeGrammar() ) {
			typeToTokenList.setSize(Token.UP + 1);
			typeToTokenList.set(Token.DOWN, "DOWN");
			typeToTokenList.set(Token.UP, "UP");
			tokenNameToTypeMap.put("DOWN", Token.DOWN);
			tokenNameToTypeMap.put("UP", Token.UP);
		}
		tokenNameToTypeMap.put("EOF", Token.EOF);
	}

    public void loadImportedGrammars() {
		if ( ast==null ) return;
        GrammarAST i = (GrammarAST)ast.getFirstChildWithType(ANTLRParser.IMPORT);
        if ( i==null ) return;
        importedGrammars = new ArrayList<Grammar>();
        for (Object c : i.getChildren()) {
            GrammarAST t = (GrammarAST)c;
            String importedGrammarName = null;
            if ( t.getType()==ANTLRParser.ASSIGN ) {
                importedGrammarName = t.getChild(1).getText();
                System.out.println("import "+ importedGrammarName);
            }
            else if ( t.getType()==ANTLRParser.ID ) {
                importedGrammarName = t.getText();
                System.out.println("import "+t.getText());
            }
            try {
                GrammarAST root = tool.load(importedGrammarName+".g");
				if ( root instanceof GrammarASTErrorNode ) return; // came back as error node
				GrammarRootAST ast = (GrammarRootAST)root;
                Grammar g = tool.createGrammar(ast);
				g.fileName = importedGrammarName+".g";
                g.parent = this;
                importedGrammars.add(g);
            }
            catch (Exception e) {
                System.err.println("can't load grammar "+importedGrammarName);
            }
        }
    }

    public void defineAction(GrammarAST atAST) {
        if ( atAST.getChildCount()==2 ) {
            String name = atAST.getChild(0).getText();
            namedActions.put(name, (ActionAST)atAST.getChild(1));
        }
        else {
			String scope = atAST.getChild(0).getText();
			if ( scope.equals(getTypeString()) ) {
				String name = atAST.getChild(1).getText();
				namedActions.put(name, (ActionAST)atAST.getChild(2));
			}
        }
    }

    public void defineRule(Rule r) {
		if ( rules.get(r.name)!=null ) return;
		rules.put(r.name, r);
		r.index = ruleNumber++;
	}

//	public int getNumRules() {
//		int n = rules.size();
//		List<Grammar> imports = getAllImportedGrammars();
//		if ( imports!=null ) {
//			for (Grammar g : imports) n += g.getNumRules();
//		}
//		return n;
//	}

    public Rule getRule(String name) {
		Rule r = rules.get(name);
		if ( r!=null ) return r;
		List<Grammar> imports = getAllImportedGrammars();
		if ( imports==null ) return null;
		for (Grammar g : imports) {
			r = g.rules.get(name);
			if ( r!=null ) return r;
		}
		return null;
	}

	public Rule getRule(String grammarName, String ruleName) {
		if ( grammarName!=null ) { // scope override
			Grammar g = getImportedGrammar(grammarName);
			if ( g ==null ) {
				return null;
			}
			return g.rules.get(ruleName);
		}
		return getRule(ruleName);
	}

    public void defineScope(AttributeDict s) { scopes.put(s.getName(), s); }

    /** Get list of all imports from all grammars in the delegate subtree of g.
     *  The grammars are in import tree preorder.  Don't include ourselves
     *  in list as we're not a delegate of ourselves.
     */
    public List<Grammar> getAllImportedGrammars() {
        if ( importedGrammars==null ) return null;
        List<Grammar> delegates = new ArrayList<Grammar>();
        for (int i = 0; i < importedGrammars.size(); i++) {
            Grammar d = importedGrammars.get(i);
            delegates.add(d);
            List<Grammar> ds = d.getAllImportedGrammars();
            if ( ds!=null ) delegates.addAll( ds );
        }
        return delegates;
    }

    public List<Grammar> getImportedGrammars() { return importedGrammars; }

    /** Get delegates below direct delegates of g
    public List<Grammar> getIndirectDelegates(Grammar g) {
        List<Grammar> direct = getDirectDelegates(g);
        List<Grammar> delegates = getDelegates(g);
        delegates.removeAll(direct);
        return delegates;
    }
*/

    /** Return list of imported grammars from root down to our parent.
     *  Order is [root, ..., this.parent].  (us not included).
     */
    public List<Grammar> getGrammarAncestors() {
        Grammar root = getOutermostGrammar();
        if ( this==root ) return null;
        List<Grammar> grammars = new ArrayList<Grammar>();
        // walk backwards to root, collecting grammars
        Grammar p = this.parent;
        while ( p!=null ) {
            grammars.add(0, p); // add to head so in order later
            p = p.parent;
        }
        return grammars;
    }

    /** Return the grammar that imported us and our parents. Return this
     *  if we're root.
     */
    public Grammar getOutermostGrammar() {
        if ( parent==null ) return this;
        return parent.getOutermostGrammar();
    }

    /** Get the name of the generated recognizer; may or may not be same
     *  as grammar name.
     *  Recognizer is TParser and TLexer from T if combined, else
     *  just use T regardless of grammar type.
     */
    public String getRecognizerName() {
        String suffix = "";
        List<Grammar> grammarsFromRootToMe = getOutermostGrammar().getGrammarAncestors();
        String qualifiedName = name;
        if ( grammarsFromRootToMe!=null ) {
            StringBuffer buf = new StringBuffer();
            for (Grammar g : grammarsFromRootToMe) {
                buf.append(g.name);
                buf.append('_');
            }
            buf.append(name);
            qualifiedName = buf.toString();
        }
        if ( isCombined() || (isLexer() && implicitLexer!=null) )
        {
            suffix = Grammar.getGrammarTypeToFileNameSuffix(getType());
        }
        return qualifiedName+suffix;
    }

	public String getStringLiteralLexerRuleName(String lit) {
		int ttype = getTokenType(lit);
		return AUTO_GENERATED_TOKEN_NAME_PREFIX +ttype;
	}

    /** Return grammar directly imported by this grammar */
    public Grammar getImportedGrammar(String name) {
		for (Grammar g : importedGrammars) {
            if ( g.name.equals(name) ) return g;
        }
        return null;
    }

	public int getTokenType(String token) {
		Integer I = null;
		if ( token.charAt(0)=='\'') {
			I = stringLiteralToTypeMap.get(token);
		}
		else { // must be a label like ID
			I = tokenNameToTypeMap.get(token);
		}
		int i = (I!=null)?I.intValue(): Token.INVALID_TYPE;
		//System.out.println("grammar type "+type+" "+tokenName+"->"+i);
		return i;
	}

	/** Given a token type, get a meaningful name for it such as the ID
	 *  or string literal.  If this is a lexer and the ttype is in the
	 *  char vocabulary, compute an ANTLR-valid (possibly escaped) char literal.
	 */
	public String getTokenDisplayName(int ttype) {
		String tokenName = null;
		int index=0;
		// inside any target's char range and is lexer grammar?
		if ( isLexer() &&
			 ttype >= MIN_CHAR_VALUE && ttype <= MAX_CHAR_VALUE )
		{
			return CharSupport.getANTLRCharLiteralForChar(ttype);
		}
		else if ( ttype==Token.EOF ) {
			tokenName = "EOF";
		}
		else {
			if ( ttype<typeToTokenList.size() ) {
				tokenName = typeToTokenList.get(ttype);
				if ( tokenName!=null &&
					 tokenName.startsWith(AUTO_GENERATED_TOKEN_NAME_PREFIX) &&
				     typeToStringLiteralList.get(ttype)!=null)
				{
					tokenName = typeToStringLiteralList.get(ttype);
				}
			}
			else {
				tokenName = String.valueOf(ttype);
			}
		}
		//System.out.println("getTokenDisplayName ttype="+ttype+", index="+index+", name="+tokenName);
		return tokenName;
	}

	public List<String> getTokenDisplayNames(Collection<Integer> types) {
		List<String> names = new ArrayList<String>();
		for (int t : types) names.add(getTokenDisplayName(t));
		return names;
	}

	public String[] getTokenNames() {
		int numTokens = getMaxTokenType();
		String[] tokenNames = new String[numTokens+1];
		for (String t : tokenNameToTypeMap.keySet()) {
			Integer ttype = tokenNameToTypeMap.get(t);
			if ( ttype>0 ) tokenNames[ttype] = t;
		}
		return tokenNames;
	}

	public String[] getTokenDisplayNames() {
		int numTokens = getMaxTokenType();
		String[] tokenNames = new String[numTokens+1];
		for (String t : tokenNameToTypeMap.keySet()) {
			Integer ttype = tokenNameToTypeMap.get(t);
			if ( ttype>0 ) tokenNames[ttype] = t;
		}
		for (String t : stringLiteralToTypeMap.keySet()) {
			Integer ttype = stringLiteralToTypeMap.get(t);
			if ( ttype>0 ) tokenNames[ttype] = t;
		}
		return tokenNames;
	}

	/** What is the max char value possible for this grammar's target?  Use
	 *  unicode max if no target defined.
	 */
	public int getMaxCharValue() {
		return MAX_CHAR_VALUE;
//		if ( generator!=null ) {
//			return generator.target.getMaxCharValue(generator);
//		}
//		else {
//			return Label.MAX_CHAR_VALUE;
//		}
	}

	/** Return a set of all possible token or char types for this grammar */
	public IntSet getTokenTypes() {
		if ( isLexer() ) {
			return getAllCharValues();
		}
		return IntervalSet.of(Token.MIN_USER_TOKEN_TYPE, getMaxTokenType());
	}

	/** Return min to max char as defined by the target.
	 *  If no target, use max unicode char value.
	 */
	public IntSet getAllCharValues() {
		return IntervalSet.of(MIN_CHAR_VALUE, getMaxCharValue());
	}

	/** How many token types have been allocated so far? */
	public int getMaxTokenType() {
		return typeToTokenList.size() - 1; // don't count 0 (invalid)
	}

	/** Return a new unique integer in the token type space */
	public int getNewTokenType() {
		maxTokenType++;
		return maxTokenType;
	}

	public void importVocab(Grammar g) {
		this.tokenNameToTypeMap.putAll( g.tokenNameToTypeMap );
		this.stringLiteralToTypeMap.putAll( g.stringLiteralToTypeMap );
		int max = Math.max(this.typeToTokenList.size(), g.typeToTokenList.size());
		this.typeToTokenList.setSize(max);
		for (int ttype=0; ttype<g.typeToTokenList.size(); ttype++) {
			maxTokenType = Math.max(maxTokenType, ttype);
			this.typeToTokenList.set(ttype, g.typeToTokenList.get(ttype));
		}
	}

	public int defineTokenName(String name) {
		return defineTokenName(name, getNewTokenType());
	}

	public int defineTokenName(String name, int ttype) {
		Integer prev = tokenNameToTypeMap.get(name);
		if ( prev!=null ) return prev;
		tokenNameToTypeMap.put(name, ttype);
		setTokenForType(ttype, name);
		maxTokenType = Math.max(maxTokenType, ttype);
		return ttype;
	}

	public int defineStringLiteral(String lit) {
		return defineStringLiteral(lit, getNewTokenType());
	}

	public int defineStringLiteral(String lit, int ttype) {
		if ( !stringLiteralToTypeMap.containsKey(lit) ) {
			stringLiteralToTypeMap.put(lit, ttype);
			// track in reverse index too
			if ( ttype>=typeToStringLiteralList.size() ) {
				typeToStringLiteralList.setSize(ttype+1);
			}
			typeToStringLiteralList.set(ttype, text);

			setTokenForType(ttype, lit);
			return ttype;
		}
		return Token.INVALID_TYPE;
	}

	public int defineTokenAlias(String name, String lit) {
		int ttype = defineTokenName(name);
		stringLiteralToTypeMap.put(lit, ttype);
		setTokenForType(ttype, name);
		return ttype;
	}

	public void setTokenForType(int ttype, String text) {
		if ( ttype>=typeToTokenList.size() ) {
			typeToTokenList.setSize(ttype+1);
		}
		String prevToken = typeToTokenList.get(ttype);
		if ( prevToken==null || prevToken.charAt(0)=='\'' ) {
			// only record if nothing there before or if thing before was a literal
			typeToTokenList.set(ttype, text);
		}
	}

	// no isolated attr at grammar action level
	public Attribute resolveToAttribute(String x, ActionAST node) {
		return null;
	}

	// no $x.y makes sense here
	public Attribute resolveToAttribute(String x, String y, ActionAST node) {
		return null;
	}

	public AttributeDict resolveToDynamicScope(String x, ActionAST node) {
		return scopes.get(x);
	}

	public boolean resolvesToLabel(String x, ActionAST node) { return false; }

	public boolean resolvesToListLabel(String x, ActionAST node) { return false; }

	public boolean resolvesToToken(String x, ActionAST node) { return false; }

	public boolean resolvesToAttributeDict(String x, ActionAST node) {
		return scopes.get(x)!=null;
	}

	/** Given a grammar type, what should be the default action scope?
     *  If I say @members in a COMBINED grammar, for example, the
     *  default scope should be "parser".
     */
    public String getDefaultActionScope() {
        switch ( getType() ) {
            case ANTLRParser.LEXER :
                return "lexer";
            case ANTLRParser.PARSER :
            case ANTLRParser.COMBINED :
                return "parser";
            case ANTLRParser.TREE :
                return "treeparser";
        }
        return null;
    }

    public int getType() {
        if ( ast!=null ) return ast.grammarType;
        return 0;
    }

	public boolean isLexer() { return getType()==ANTLRParser.LEXER; }
	public boolean isParser() { return getType()==ANTLRParser.PARSER; }
	public boolean isTreeGrammar() { return getType()==ANTLRParser.TREE; }
	public boolean isCombined() { return getType()==ANTLRParser.COMBINED; }

    public String getTypeString() {
        if ( ast==null ) return null;
        return ANTLRParser.tokenNames[getType()].toLowerCase();
    }

    public static String getGrammarTypeToFileNameSuffix(int type) {
        switch ( type ) {
            case ANTLRParser.LEXER : return "Lexer";
            case ANTLRParser.PARSER : return "Parser";
            case ANTLRParser.TREE : return "";
            // if combined grammar, gen Parser and Lexer will be done later
            // TODO: we are separate now right?
            case ANTLRParser.COMBINED : return "Parser";
            default :
                return "<invalid>";
        }
	}

	public String getOption(String key) {
		if ( ast.options==null ) return null;
		return ast.options.get(key);
	}

	public String getOption(String key, String defaultValue) {
		if ( ast.options==null ) return defaultValue;
		String v = ast.options.get(key);
		if ( v!=null ) return v;
		return defaultValue;
	}

	public boolean hasASTOption() {
		String outputOption = getOption("output");
		return outputOption!=null && outputOption.equals("AST");
	}

	public static Map<String,String> getStringLiteralAliasesFromLexerRules(GrammarRootAST ast) {
		GrammarAST combinedRulesRoot =
			(GrammarAST)ast.getFirstChildWithType(ANTLRParser.RULES);
		if ( combinedRulesRoot==null ) return null;

		List<GrammarASTWithOptions> ruleNodes = combinedRulesRoot.getChildren();
		if ( ruleNodes==null || ruleNodes.size()==0 ) return null;
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor(ruleNodes.get(0).token.getInputStream());
		TreeWizard wiz = new TreeWizard(adaptor,ANTLRParser.tokenNames);
		Map<String,String> lexerRuleToStringLiteral = new HashMap<String,String>();

        for (GrammarASTWithOptions r : ruleNodes) {
            String ruleName = r.getChild(0).getText();
            if ( Character.isUpperCase(ruleName.charAt(0)) ) {
				Map nodes = new HashMap();
				boolean isLitRule =
					wiz.parse(r, "(RULE %name:ID (BLOCK (ALT %lit:STRING_LITERAL)))", nodes);
				if ( isLitRule ) {
					GrammarAST litNode = (GrammarAST)nodes.get("lit");
					GrammarAST nameNode = (GrammarAST)nodes.get("name");
					lexerRuleToStringLiteral.put(litNode.getText(), nameNode.getText());
				}
            }
        }
		return lexerRuleToStringLiteral;
	}

	public void setLookaheadDFA(int decision, DFA lookaheadDFA) {
		decisionDFAs.put(Utils.integer(decision), lookaheadDFA);
	}
}
