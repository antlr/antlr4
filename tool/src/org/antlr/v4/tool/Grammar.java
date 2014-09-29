/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.tool;

import org.antlr.v4.Tool;
import org.antlr.v4.analysis.LeftRecursiveRuleTransformer;
import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.misc.OrderedHashMap;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.parse.GrammarTreeVisitor;
import org.antlr.v4.parse.TokenVocabParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.LexerInterpreter;
import org.antlr.v4.runtime.ParserInterpreter;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ATNSerializer;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.IntSet;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.GrammarASTWithOptions;
import org.antlr.v4.tool.ast.GrammarRootAST;
import org.antlr.v4.tool.ast.PredAST;
import org.antlr.v4.tool.ast.RuleAST;
import org.antlr.v4.tool.ast.TerminalAST;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Grammar implements AttributeResolver {
	public static final String GRAMMAR_FROM_STRING_NAME = "<string>";
	/**
	 * This value is used in the following situations to indicate that a token
	 * type does not have an associated name which can be directly referenced in
	 * a grammar.
	 *
	 * <ul>
	 * <li>This value is the name and display name for the token with type
	 * {@link Token#INVALID_TYPE}.</li>
	 * <li>This value is the name for tokens with a type not represented by a
	 * named token. The display name for these tokens is simply the string
	 * representation of the token type as an integer.</li>
	 * </ul>
	 */
	public static final String INVALID_TOKEN_NAME = "<INVALID>";
	/**
	 * This value is used as the name for elements in the array returned by
	 * {@link #getRuleNames} for indexes not associated with a rule.
	 */
	public static final String INVALID_RULE_NAME = "<invalid>";

	public static final Set<String> parserOptions = new HashSet<String>();
	static {
		parserOptions.add("superClass");
		parserOptions.add("TokenLabelType");
		parserOptions.add("tokenVocab");
		parserOptions.add("language");
	}

	public static final Set<String> lexerOptions = parserOptions;

	public static final Set<String> ruleOptions = new HashSet<String>();

	public static final Set<String> ParserBlockOptions = new HashSet<String>();

	public static final Set<String> LexerBlockOptions = new HashSet<String>();

	/** Legal options for rule refs like id<key=value> */
	public static final Set<String> ruleRefOptions = new HashSet<String>();
	static {
		ruleRefOptions.add(LeftRecursiveRuleTransformer.PRECEDENCE_OPTION_NAME);
		ruleRefOptions.add(LeftRecursiveRuleTransformer.TOKENINDEX_OPTION_NAME);
	}

	/** Legal options for terminal refs like ID<assoc=right> */
	public static final Set<String> tokenOptions = new HashSet<String>();
	static {
		tokenOptions.add("assoc");
		tokenOptions.add(LeftRecursiveRuleTransformer.TOKENINDEX_OPTION_NAME);
	}

	public static final Set<String> actionOptions = new HashSet<String>();

	public static final Set<String> semPredOptions = new HashSet<String>();
	static {
		semPredOptions.add(LeftRecursiveRuleTransformer.PRECEDENCE_OPTION_NAME);
		semPredOptions.add("fail");
	}

	public static final Set<String> doNotCopyOptionsToLexer = new HashSet<String>();
	static {
		doNotCopyOptionsToLexer.add("superClass");
		doNotCopyOptionsToLexer.add("TokenLabelType");
		doNotCopyOptionsToLexer.add("tokenVocab");
	}

	public static final Map<String, AttributeDict> grammarAndLabelRefTypeToScope =
		new HashMap<String, AttributeDict>();
	static {
		grammarAndLabelRefTypeToScope.put("parser:RULE_LABEL", Rule.predefinedRulePropertiesDict);
		grammarAndLabelRefTypeToScope.put("parser:TOKEN_LABEL", AttributeDict.predefinedTokenDict);
		grammarAndLabelRefTypeToScope.put("combined:RULE_LABEL", Rule.predefinedRulePropertiesDict);
		grammarAndLabelRefTypeToScope.put("combined:TOKEN_LABEL", AttributeDict.predefinedTokenDict);
	}

	public String name;
    public GrammarRootAST ast;
	/** Track stream used to create this grammar */
	@NotNull
	public final org.antlr.runtime.TokenStream tokenStream;
	/** If we transform grammar, track original unaltered token stream */
	public org.antlr.runtime.TokenStream originalTokenStream;

    public String text; // testing only
    public String fileName;

    /** Was this parser grammar created from a COMBINED grammar?  If so,
	 *  this is what we extracted.
	 */
    public LexerGrammar implicitLexer;

	/** If this is an extracted/implicit lexer, we point at original grammar */
	public Grammar originalGrammar;

    /** If we're imported, who imported us? If null, implies grammar is root */
    public Grammar parent;
    public List<Grammar> importedGrammars;

	/** All rules defined in this specific grammar, not imported. Also does
	 *  not include lexical rules if combined.
	 */
    public OrderedHashMap<String, Rule> rules = new OrderedHashMap<String, Rule>();
	public List<Rule> indexToRule = new ArrayList<Rule>();

	int ruleNumber = 0; // used to get rule indexes (0..n-1)
	int stringLiteralRuleNumber = 0; // used to invent rule names for 'keyword', ';', ... (0..n-1)

	/** The ATN that represents the grammar with edges labelled with tokens
	 *  or epsilon.  It is more suitable to analysis than an AST representation.
	 */
	public ATN atn;

	public Map<Integer, Interval> stateToGrammarRegionMap;

	public Map<Integer, DFA> decisionDFAs = new HashMap<Integer, DFA>();

	public List<IntervalSet[]> decisionLOOK;

	@NotNull
	public final Tool tool;

	/** Token names and literal tokens like "void" are uniquely indexed.
	 *  with -1 implying EOF.  Characters are different; they go from
	 *  -1 (EOF) to \uFFFE.  For example, 0 could be a binary byte you
	 *  want to lexer.  Labels of DFA/ATN transitions can be both tokens
	 *  and characters.  I use negative numbers for bookkeeping labels
	 *  like EPSILON. Char/String literals and token types overlap in the same
	 *  space, however.
	 */
	int maxTokenType = Token.MIN_USER_TOKEN_TYPE -1;

	/**
	 * Map token like {@code ID} (but not literals like {@code 'while'}) to its
	 * token type.
	 */
	public final Map<String, Integer> tokenNameToTypeMap = new LinkedHashMap<String, Integer>();

	/**
	 * Map token literals like {@code 'while'} to its token type. It may be that
	 * {@code WHILE="while"=35}, in which case both {@link #tokenNameToTypeMap}
	 * and this field will have entries both mapped to 35.
	 */
	public final Map<String, Integer> stringLiteralToTypeMap = new LinkedHashMap<String, Integer>();

	/**
	 * Reverse index for {@link #stringLiteralToTypeMap}. Indexed with raw token
	 * type. 0 is invalid.
	 */
	public final List<String> typeToStringLiteralList = new ArrayList<String>();

	/**
	 * Map a token type to its token name. Indexed with raw token type. 0 is
	 * invalid.
	 */
	public final List<String> typeToTokenList = new ArrayList<String>();

	/**
	 * The maximum channel value which is assigned by this grammar. Values below
	 * {@link Token#MIN_USER_CHANNEL_VALUE} are assumed to be predefined.
	 */
	int maxChannelType = Token.MIN_USER_CHANNEL_VALUE - 1;

	/**
	 * Map channel like {@code COMMENTS_CHANNEL} to its constant channel value.
	 * Only user-defined channels are defined in this map.
	 */
	public final Map<String, Integer> channelNameToValueMap = new LinkedHashMap<String, Integer>();

	/**
	 * Map a constant channel value to its name. Indexed with raw channel value.
	 * The predefined channels {@link Token#DEFAULT_CHANNEL} and
	 * {@link Token#HIDDEN_CHANNEL} are not stored in this list, so the values
	 * at the corresponding indexes is {@code null}.
	 */
	public final List<String> channelValueToNameList = new ArrayList<String>();

    /** Map a name to an action.
     *  The code generator will use this to fill holes in the output files.
     *  I track the AST node for the action in case I need the line number
     *  for errors.
     */
	public Map<String,ActionAST> namedActions = new HashMap<String,ActionAST>();

	/** Tracks all user lexer actions in all alternatives of all rules.
	 *  Doesn't track sempreds.  maps tree node to action index (alt number 1..n).
 	 */
	public LinkedHashMap<ActionAST, Integer> lexerActions = new LinkedHashMap<ActionAST, Integer>();

	/** All sempreds found in grammar; maps tree node to sempred index;
	 *  sempred index is 0..n-1
	 */
	public LinkedHashMap<PredAST, Integer> sempreds = new LinkedHashMap<PredAST, Integer>();
	/** Map the other direction upon demand */
	public LinkedHashMap<Integer, PredAST> indexToPredMap;

	public static final String AUTO_GENERATED_TOKEN_NAME_PREFIX = "T__";

	public Grammar(Tool tool, @NotNull GrammarRootAST ast) {
		if ( ast==null ) {
			throw new NullPointerException("ast");
		}

		if (ast.tokenStream == null) {
			throw new IllegalArgumentException("ast must have a token stream");
		}

        this.tool = tool;
        this.ast = ast;
        this.name = (ast.getChild(0)).getText();
		this.tokenStream = ast.tokenStream;

		initTokenSymbolTables();
    }

	/** For testing */
	public Grammar(String grammarText) throws org.antlr.runtime.RecognitionException {
		this(GRAMMAR_FROM_STRING_NAME, grammarText, null);
	}

	public Grammar(String grammarText, LexerGrammar tokenVocabSource) throws org.antlr.runtime.RecognitionException {
		this(GRAMMAR_FROM_STRING_NAME, grammarText, tokenVocabSource, null);
	}

	/** For testing */
	public Grammar(String grammarText, ANTLRToolListener listener)
		throws org.antlr.runtime.RecognitionException
	{
		this(GRAMMAR_FROM_STRING_NAME, grammarText, listener);
	}

	/** For testing; builds trees, does sem anal */
	public Grammar(String fileName, String grammarText)
		throws org.antlr.runtime.RecognitionException
	{
		this(fileName, grammarText, null);
	}

	/** For testing; builds trees, does sem anal */
	public Grammar(String fileName, String grammarText, @Nullable ANTLRToolListener listener)
		throws org.antlr.runtime.RecognitionException
	{
		this(fileName, grammarText, null, listener);
	}

	/** For testing; builds trees, does sem anal */
	public Grammar(String fileName, String grammarText, Grammar tokenVocabSource, @Nullable ANTLRToolListener listener)
		throws org.antlr.runtime.RecognitionException
	{
        this.text = grammarText;
		this.fileName = fileName;
		this.tool = new Tool();
		this.tool.addListener(listener);
		org.antlr.runtime.ANTLRStringStream in = new org.antlr.runtime.ANTLRStringStream(grammarText);
		in.name = fileName;

		this.ast = tool.parse(fileName, in);
		if ( ast==null ) {
			throw new UnsupportedOperationException();
		}

		if (ast.tokenStream == null) {
			throw new IllegalStateException("expected ast to have a token stream");
		}

		this.tokenStream = ast.tokenStream;

		// ensure each node has pointer to surrounding grammar
		final Grammar thiz = this;
		org.antlr.runtime.tree.TreeVisitor v = new org.antlr.runtime.tree.TreeVisitor(new GrammarASTAdaptor());
		v.visit(ast, new org.antlr.runtime.tree.TreeVisitorAction() {
			@Override
			public Object pre(Object t) { ((GrammarAST)t).g = thiz; return t; }
			@Override
			public Object post(Object t) { return t; }
		});
		initTokenSymbolTables();

		if (tokenVocabSource != null) {
			importVocab(tokenVocabSource);
		}

		tool.process(this, false);
    }

	protected void initTokenSymbolTables() {
		tokenNameToTypeMap.put("EOF", Token.EOF);

		// reserve a spot for the INVALID token
		typeToTokenList.add(null);
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
				t = (GrammarAST)t.getChild(1);
				importedGrammarName = t.getText();
            }
            else if ( t.getType()==ANTLRParser.ID ) {
                importedGrammarName = t.getText();
			}
			Grammar g;
			try {
				g = tool.loadImportedGrammar(this, t);
			}
			catch (IOException ioe) {
				tool.errMgr.grammarError(ErrorType.ERROR_READING_IMPORTED_GRAMMAR,
										 importedGrammarName,
										 t.getToken(),
										 importedGrammarName,
										 name);
				continue;
			}
			// did it come back as error node or missing?
			if ( g == null ) continue;
			g.parent = this;
			importedGrammars.add(g);
			g.loadImportedGrammars(); // recursively pursue any imports in this import
        }
    }

    public void defineAction(GrammarAST atAST) {
        if ( atAST.getChildCount()==2 ) {
            String name = atAST.getChild(0).getText();
            namedActions.put(name, (ActionAST)atAST.getChild(1));
        }
        else {
			String scope = atAST.getChild(0).getText();
            String gtype = getTypeString();
            if ( scope.equals(gtype) || (scope.equals("parser")&&gtype.equals("combined")) ) {
				String name = atAST.getChild(1).getText();
				namedActions.put(name, (ActionAST)atAST.getChild(2));
			}
        }
    }

	/**
	 * Define the specified rule in the grammar. This method assigns the rule's
	 * {@link Rule#index} according to the {@link #ruleNumber} field, and adds
	 * the {@link Rule} instance to {@link #rules} and {@link #indexToRule}.
	 *
	 * @param r The rule to define in the grammar.
	 * @return {@code true} if the rule was added to the {@link Grammar}
	 * instance; otherwise, {@code false} if a rule with this name already
	 * existed in the grammar instance.
	 */
	public boolean defineRule(@NotNull Rule r) {
		if ( rules.get(r.name)!=null ) {
			return false;
		}

		rules.put(r.name, r);
		r.index = ruleNumber++;
		indexToRule.add(r);
		return true;
	}

	/**
	 * Undefine the specified rule from this {@link Grammar} instance. The
	 * instance {@code r} is removed from {@link #rules} and
	 * {@link #indexToRule}. This method updates the {@link Rule#index} field
	 * for all rules defined after {@code r}, and decrements {@link #ruleNumber}
	 * in preparation for adding new rules.
	 * <p>
	 * This method does nothing if the current {@link Grammar} does not contain
	 * the instance {@code r} at index {@code r.index} in {@link #indexToRule}.
	 * </p>
	 *
	 * @param r
	 * @return {@code true} if the rule was removed from the {@link Grammar}
	 * instance; otherwise, {@code false} if the specified rule was not defined
	 * in the grammar.
	 */
	public boolean undefineRule(@NotNull Rule r) {
		if (r.index < 0 || r.index >= indexToRule.size() || indexToRule.get(r.index) != r) {
			return false;
		}

		assert rules.get(r.name) == r;

		rules.remove(r.name);
		indexToRule.remove(r.index);
		for (int i = r.index; i < indexToRule.size(); i++) {
			assert indexToRule.get(i).index == i + 1;
			indexToRule.get(i).index--;
		}

		ruleNumber--;
		return true;
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
		return null;
		/*
		List<Grammar> imports = getAllImportedGrammars();
		if ( imports==null ) return null;
		for (Grammar g : imports) {
			r = g.getRule(name); // recursively walk up hierarchy
			if ( r!=null ) return r;
		}
		return null;
		*/
	}

	public Rule getRule(int index) { return indexToRule.get(index); }

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

    /** Get list of all imports from all grammars in the delegate subtree of g.
     *  The grammars are in import tree preorder.  Don't include ourselves
     *  in list as we're not a delegate of ourselves.
     */
	public List<Grammar> getAllImportedGrammars() {
		if (importedGrammars == null) {
			return null;
		}

		LinkedHashMap<String, Grammar> delegates = new LinkedHashMap<String, Grammar>();
		for (Grammar d : importedGrammars) {
			delegates.put(d.fileName, d);
			List<Grammar> ds = d.getAllImportedGrammars();
			if (ds != null) {
				for (Grammar imported : ds) {
					delegates.put(imported.fileName, imported);
				}
			}
		}

		return new ArrayList<Grammar>(delegates.values());
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

	public LexerGrammar getImplicitLexer() {
		return implicitLexer;
	}

	/** convenience method for Tool.loadGrammar() */
	public static Grammar load(String fileName) {
		Tool antlr = new Tool();
		return antlr.loadGrammar(fileName);
	}

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
            StringBuilder buf = new StringBuilder();
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
		return AUTO_GENERATED_TOKEN_NAME_PREFIX + stringLiteralRuleNumber++;
	}

    /** Return grammar directly imported by this grammar */
    public Grammar getImportedGrammar(String name) {
		for (Grammar g : importedGrammars) {
            if ( g.name.equals(name) ) return g;
        }
        return null;
    }

	public int getTokenType(String token) {
		Integer I;
		if ( token.charAt(0)=='\'') {
			I = stringLiteralToTypeMap.get(token);
		}
		else { // must be a label like ID
			I = tokenNameToTypeMap.get(token);
		}
		int i = (I!=null)? I : Token.INVALID_TYPE;
		//tool.log("grammar", "grammar type "+type+" "+tokenName+"->"+i);
		return i;
	}

	/** Given a token type, get a meaningful name for it such as the ID
	 *  or string literal.  If this is a lexer and the ttype is in the
	 *  char vocabulary, compute an ANTLR-valid (possibly escaped) char literal.
	 */
	public String getTokenDisplayName(int ttype) {
		// inside any target's char range and is lexer grammar?
		if ( isLexer() &&
			 ttype >= Lexer.MIN_CHAR_VALUE && ttype <= Lexer.MAX_CHAR_VALUE )
		{
			return CharSupport.getANTLRCharLiteralForChar(ttype);
		}

		if ( ttype==Token.EOF ) {
			return "EOF";
		}

		if ( ttype==Token.INVALID_TYPE ) {
			return INVALID_TOKEN_NAME;
		}

		if (ttype >= 0 && ttype < typeToStringLiteralList.size() && typeToStringLiteralList.get(ttype) != null) {
			return typeToStringLiteralList.get(ttype);
		}

		if (ttype >= 0 && ttype < typeToTokenList.size() && typeToTokenList.get(ttype) != null) {
			return typeToTokenList.get(ttype);
		}

		return String.valueOf(ttype);
	}

	/**
	 * Gets the name by which a token can be referenced in the generated code.
	 * For tokens defined in a {@code tokens{}} block or via a lexer rule, this
	 * is the declared name of the token. For token types generated by the use
	 * of a string literal within a parser rule of a combined grammar, this is
	 * the automatically generated token type which includes the
	 * {@link #AUTO_GENERATED_TOKEN_NAME_PREFIX} prefix. For types which are not
	 * associated with a defined token, this method returns
	 * {@link #INVALID_TOKEN_NAME}.
	 *
	 * @param ttype The token type.
	 * @return The name of the token with the specified type.
	 */
	@NotNull
	public String getTokenName(int ttype) {
		// inside any target's char range and is lexer grammar?
		if ( isLexer() &&
			 ttype >= Lexer.MIN_CHAR_VALUE && ttype <= Lexer.MAX_CHAR_VALUE )
		{
			return CharSupport.getANTLRCharLiteralForChar(ttype);
		}

		if ( ttype==Token.EOF ) {
			return "EOF";
		}

		if (ttype >= 0 && ttype < typeToTokenList.size() && typeToTokenList.get(ttype) != null) {
			return typeToTokenList.get(ttype);
		}

		return INVALID_TOKEN_NAME;
	}

	/**
	 * Gets the constant channel value for a user-defined channel.
	 *
	 * <p>
	 * This method only returns channel values for user-defined channels. All
	 * other channels, including the predefined channels
	 * {@link Token#DEFAULT_CHANNEL} and {@link Token#HIDDEN_CHANNEL} along with
	 * any channel defined in code (e.g. in a {@code @members{}} block), are
	 * ignored.</p>
	 *
	 * @param channel The channel name.
	 * @return The channel value, if {@code channel} is the name of a known
	 * user-defined token channel; otherwise, -1.
	 */
	public int getChannelValue(String channel) {
		Integer I = channelNameToValueMap.get(channel);
		int i = (I != null) ? I : -1;
		return i;
	}

	/**
	 * Gets an array of rule names for rules defined or imported by the
	 * grammar. The array index is the rule index, and the value is the name of
	 * the rule with the corresponding {@link Rule#index}.
	 *
	 * <p>If no rule is defined with an index for an element of the resulting
	 * array, the value of that element is {@link #INVALID_RULE_NAME}.</p>
	 *
	 * @return The names of all rules defined in the grammar.
	 */
	public String[] getRuleNames() {
		String[] result = new String[rules.size()];
		Arrays.fill(result, INVALID_RULE_NAME);
		for (Rule rule : rules.values()) {
			result[rule.index] = rule.name;
		}

		return result;
	}

	/**
	 * Gets an array of token names for tokens defined or imported by the
	 * grammar. The array index is the token type, and the value is the result
	 * of {@link #getTokenName} for the corresponding token type.
	 *
	 * @see #getTokenName
	 * @return The token names of all tokens defined in the grammar.
	 */
	public String[] getTokenNames() {
		int numTokens = getMaxTokenType();
		String[] tokenNames = new String[numTokens+1];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = getTokenName(i);
		}

		return tokenNames;
	}

	/**
	 * Gets an array of display names for tokens defined or imported by the
	 * grammar. The array index is the token type, and the value is the result
	 * of {@link #getTokenDisplayName} for the corresponding token type.
	 *
	 * @see #getTokenDisplayName
	 * @return The display names of all tokens defined in the grammar.
	 */
	public String[] getTokenDisplayNames() {
		int numTokens = getMaxTokenType();
		String[] tokenNames = new String[numTokens+1];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = getTokenDisplayName(i);
		}

		return tokenNames;
	}

	/**
	 * Gets the literal names assigned to tokens in the grammar.
	 */
	@NotNull
	public String[] getTokenLiteralNames() {
		int numTokens = getMaxTokenType();
		String[] literalNames = new String[numTokens+1];
		for (int i = 0; i < Math.min(literalNames.length, typeToStringLiteralList.size()); i++) {
			literalNames[i] = typeToStringLiteralList.get(i);
		}

		for (Map.Entry<String, Integer> entry : stringLiteralToTypeMap.entrySet()) {
			if (entry.getValue() >= 0 && entry.getValue() < literalNames.length && literalNames[entry.getValue()] == null) {
				literalNames[entry.getValue()] = entry.getKey();
			}
		}

		return literalNames;
	}

	/**
	 * Gets the symbolic names assigned to tokens in the grammar.
	 */
	@NotNull
	public String[] getTokenSymbolicNames() {
		int numTokens = getMaxTokenType();
		String[] symbolicNames = new String[numTokens+1];
		for (int i = 0; i < Math.min(symbolicNames.length, typeToTokenList.size()); i++) {
			if (typeToTokenList.get(i) == null || typeToTokenList.get(i).startsWith(AUTO_GENERATED_TOKEN_NAME_PREFIX)) {
				continue;
			}

			symbolicNames[i] = typeToTokenList.get(i);
		}

		return symbolicNames;
	}

	/**
	 * Gets a {@link Vocabulary} instance describing the vocabulary used by the
	 * grammar.
	 */
	@NotNull
	public Vocabulary getVocabulary() {
		return new VocabularyImpl(getTokenLiteralNames(), getTokenSymbolicNames());
	}

	/** Given an arbitrarily complex SemanticContext, walk the "tree" and get display string.
	 *  Pull predicates from grammar text.
	 */
	public String getSemanticContextDisplayString(SemanticContext semctx) {
		if ( semctx instanceof SemanticContext.Predicate ) {
			return getPredicateDisplayString((SemanticContext.Predicate)semctx);
		}
		if ( semctx instanceof SemanticContext.AND ) {
			SemanticContext.AND and = (SemanticContext.AND)semctx;
			return joinPredicateOperands(and, " and ");
		}
		if ( semctx instanceof SemanticContext.OR ) {
			SemanticContext.OR or = (SemanticContext.OR)semctx;
			return joinPredicateOperands(or, " or ");
		}
		return semctx.toString();
	}

	public String joinPredicateOperands(SemanticContext.Operator op, String separator) {
		StringBuilder buf = new StringBuilder();
		for (SemanticContext operand : op.getOperands()) {
			if (buf.length() > 0) {
				buf.append(separator);
			}

			buf.append(getSemanticContextDisplayString(operand));
		}

		return buf.toString();
	}

	public LinkedHashMap<Integer, PredAST> getIndexToPredicateMap() {
		LinkedHashMap<Integer, PredAST> indexToPredMap = new LinkedHashMap<Integer, PredAST>();
		for (Rule r : rules.values()) {
			for (ActionAST a : r.actions) {
				if (a instanceof PredAST) {
					PredAST p = (PredAST) a;
					indexToPredMap.put(sempreds.get(p), p);
				}
			}
		}
		return indexToPredMap;
	}

	public String getPredicateDisplayString(SemanticContext.Predicate pred) {
		if ( indexToPredMap==null ) {
			indexToPredMap = getIndexToPredicateMap();
		}
		ActionAST actionAST = indexToPredMap.get(pred.predIndex);
		return actionAST.getText();
	}

	/** What is the max char value possible for this grammar's target?  Use
	 *  unicode max if no target defined.
	 */
	public int getMaxCharValue() {
		return org.antlr.v4.runtime.Lexer.MAX_CHAR_VALUE;
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
		return IntervalSet.of(Lexer.MIN_CHAR_VALUE, getMaxCharValue());
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

	/** Return a new unique integer in the channel value space. */
	public int getNewChannelNumber() {
		maxChannelType++;
		return maxChannelType;
	}

	public void importTokensFromTokensFile() {
		String vocab = getOptionString("tokenVocab");
		if ( vocab!=null ) {
			TokenVocabParser vparser = new TokenVocabParser(this);
			Map<String,Integer> tokens = vparser.load();
			tool.log("grammar", "tokens=" + tokens);
			for (String t : tokens.keySet()) {
				if ( t.charAt(0)=='\'' ) defineStringLiteral(t, tokens.get(t));
				else defineTokenName(t, tokens.get(t));
			}
		}
	}

	public void importVocab(Grammar importG) {
		for (String tokenName: importG.tokenNameToTypeMap.keySet()) {
			defineTokenName(tokenName, importG.tokenNameToTypeMap.get(tokenName));
		}
		for (String tokenName: importG.stringLiteralToTypeMap.keySet()) {
			defineStringLiteral(tokenName, importG.stringLiteralToTypeMap.get(tokenName));
		}
		for (Map.Entry<String, Integer> channel : importG.channelNameToValueMap.entrySet()) {
			defineChannelName(channel.getKey(), channel.getValue());
		}
//		this.tokenNameToTypeMap.putAll( importG.tokenNameToTypeMap );
//		this.stringLiteralToTypeMap.putAll( importG.stringLiteralToTypeMap );
		int max = Math.max(this.typeToTokenList.size(), importG.typeToTokenList.size());
		Utils.setSize(typeToTokenList, max);
		for (int ttype=0; ttype<importG.typeToTokenList.size(); ttype++) {
			maxTokenType = Math.max(maxTokenType, ttype);
			this.typeToTokenList.set(ttype, importG.typeToTokenList.get(ttype));
		}

		max = Math.max(this.channelValueToNameList.size(), importG.channelValueToNameList.size());
		Utils.setSize(channelValueToNameList, max);
		for (int channelValue = 0; channelValue < importG.channelValueToNameList.size(); channelValue++) {
			maxChannelType = Math.max(maxChannelType, channelValue);
			this.channelValueToNameList.set(channelValue, importG.channelValueToNameList.get(channelValue));
		}
	}

	public int defineTokenName(String name) {
		Integer prev = tokenNameToTypeMap.get(name);
		if ( prev==null ) return defineTokenName(name, getNewTokenType());
		return prev;
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
		if ( stringLiteralToTypeMap.containsKey(lit) ) {
			return stringLiteralToTypeMap.get(lit);
		}
		return defineStringLiteral(lit, getNewTokenType());

	}

	public int defineStringLiteral(String lit, int ttype) {
		if ( !stringLiteralToTypeMap.containsKey(lit) ) {
			stringLiteralToTypeMap.put(lit, ttype);
			// track in reverse index too
			if ( ttype>=typeToStringLiteralList.size() ) {
				Utils.setSize(typeToStringLiteralList, ttype+1);
			}
			typeToStringLiteralList.set(ttype, lit);

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
		if (ttype == Token.EOF) {
			// ignore EOF, it will be reported as an error separately
			return;
		}

		if ( ttype>=typeToTokenList.size() ) {
			Utils.setSize(typeToTokenList, ttype+1);
		}
		String prevToken = typeToTokenList.get(ttype);
		if ( prevToken==null || prevToken.charAt(0)=='\'' ) {
			// only record if nothing there before or if thing before was a literal
			typeToTokenList.set(ttype, text);
		}
	}

	/**
	 * Define a token channel with a specified name.
	 *
	 * <p>
	 * If a channel with the specified name already exists, the previously
	 * assigned channel value is returned.</p>
	 *
	 * @param name The channel name.
	 * @return The constant channel value assigned to the channel.
	 */
	public int defineChannelName(String name) {
		Integer prev = channelNameToValueMap.get(name);
		if (prev == null) {
			return defineChannelName(name, getNewChannelNumber());
		}

		return prev;
	}

	/**
	 * Define a token channel with a specified name.
	 *
	 * <p>
	 * If a channel with the specified name already exists, the previously
	 * assigned channel value is not altered.</p>
	 *
	 * @param name The channel name.
	 * @return The constant channel value assigned to the channel.
	 */
	public int defineChannelName(String name, int value) {
		Integer prev = channelNameToValueMap.get(name);
		if (prev != null) {
			return prev;
		}

		channelNameToValueMap.put(name, value);
		setChannelNameForValue(value, name);
		maxChannelType = Math.max(maxChannelType, value);
		return value;
	}

	/**
	 * Sets the channel name associated with a particular channel value.
	 *
	 * <p>
	 * If a name has already been assigned to the channel with constant value
	 * {@code channelValue}, this method does nothing.</p>
	 *
	 * @param channelValue The constant value for the channel.
	 * @param name The channel name.
	 */
	public void setChannelNameForValue(int channelValue, String name) {
		if (channelValue >= channelValueToNameList.size()) {
			Utils.setSize(channelValueToNameList, channelValue + 1);
		}

		String prevChannel = channelValueToNameList.get(channelValue);
		if (prevChannel == null) {
			channelValueToNameList.set(channelValue, name);
		}
	}

	// no isolated attr at grammar action level
	@Override
	public Attribute resolveToAttribute(String x, ActionAST node) {
		return null;
	}

	// no $x.y makes sense here
	@Override
	public Attribute resolveToAttribute(String x, String y, ActionAST node) {
		return null;
	}

	@Override
	public boolean resolvesToLabel(String x, ActionAST node) { return false; }

	@Override
	public boolean resolvesToListLabel(String x, ActionAST node) { return false; }

	@Override
	public boolean resolvesToToken(String x, ActionAST node) { return false; }

	@Override
	public boolean resolvesToAttributeDict(String x, ActionAST node) {
		return false;
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
        }
        return null;
    }

    public int getType() {
        if ( ast!=null ) return ast.grammarType;
        return 0;
    }

	public org.antlr.runtime.TokenStream getTokenStream() {
		if ( ast!=null ) return ast.tokenStream;
		return null;
	}

	public boolean isLexer() { return getType()==ANTLRParser.LEXER; }
	public boolean isParser() { return getType()==ANTLRParser.PARSER; }
	public boolean isCombined() { return getType()==ANTLRParser.COMBINED; }

	/** Is id a valid token name? Does id start with an uppercase letter? */
	public static boolean isTokenName(String id) {
		return Character.isUpperCase(id.charAt(0));
	}

    public String getTypeString() {
        if ( ast==null ) return null;
        return ANTLRParser.tokenNames[getType()].toLowerCase();
    }

    public static String getGrammarTypeToFileNameSuffix(int type) {
        switch ( type ) {
            case ANTLRParser.LEXER : return "Lexer";
            case ANTLRParser.PARSER : return "Parser";
            // if combined grammar, gen Parser and Lexer will be done later
            // TODO: we are separate now right?
            case ANTLRParser.COMBINED : return "Parser";
            default :
                return "<invalid>";
        }
	}

	public String getOptionString(String key) { return ast.getOptionString(key); }

	/** Given ^(TOKEN_REF ^(OPTIONS ^(ELEMENT_OPTIONS (= assoc right))))
	 *  set option assoc=right in TOKEN_REF.
	 */
	public static void setNodeOptions(GrammarAST node, GrammarAST options) {
		if ( options==null ) return;
		GrammarASTWithOptions t = (GrammarASTWithOptions)node;
		if ( t.getChildCount()==0 || options.getChildCount()==0 ) return;
		for (Object o : options.getChildren()) {
			GrammarAST c = (GrammarAST)o;
			if ( c.getType()==ANTLRParser.ASSIGN ) {
				t.setOption(c.getChild(0).getText(), (GrammarAST)c.getChild(1));
			}
			else {
				t.setOption(c.getText(), null); // no arg such as ID<VarNodeType>
			}
		}
	}

	/** Return list of (TOKEN_NAME node, 'literal' node) pairs */
	public static List<Pair<GrammarAST,GrammarAST>> getStringLiteralAliasesFromLexerRules(GrammarRootAST ast) {
		String[] patterns = {
			"(RULE %name:TOKEN_REF (BLOCK (ALT %lit:STRING_LITERAL)))",
			"(RULE %name:TOKEN_REF (BLOCK (ALT %lit:STRING_LITERAL ACTION)))",
			"(RULE %name:TOKEN_REF (BLOCK (ALT %lit:STRING_LITERAL SEMPRED)))",
			"(RULE %name:TOKEN_REF (BLOCK (LEXER_ALT_ACTION (ALT %lit:STRING_LITERAL) .)))",
			"(RULE %name:TOKEN_REF (BLOCK (LEXER_ALT_ACTION (ALT %lit:STRING_LITERAL) . .)))",
			"(RULE %name:TOKEN_REF (BLOCK (LEXER_ALT_ACTION (ALT %lit:STRING_LITERAL) (LEXER_ACTION_CALL . .))))",
			"(RULE %name:TOKEN_REF (BLOCK (LEXER_ALT_ACTION (ALT %lit:STRING_LITERAL) . (LEXER_ACTION_CALL . .))))",
			"(RULE %name:TOKEN_REF (BLOCK (LEXER_ALT_ACTION (ALT %lit:STRING_LITERAL) (LEXER_ACTION_CALL . .) .)))",
			// TODO: allow doc comment in there
		};
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor(ast.token.getInputStream());
		org.antlr.runtime.tree.TreeWizard wiz = new org.antlr.runtime.tree.TreeWizard(adaptor,ANTLRParser.tokenNames);
		List<Pair<GrammarAST,GrammarAST>> lexerRuleToStringLiteral =
			new ArrayList<Pair<GrammarAST,GrammarAST>>();

		List<GrammarAST> ruleNodes = ast.getNodesWithType(ANTLRParser.RULE);
		if ( ruleNodes==null || ruleNodes.isEmpty() ) return null;

		for (GrammarAST r : ruleNodes) {
			//tool.log("grammar", r.toStringTree());
//			System.out.println("chk: "+r.toStringTree());
			org.antlr.runtime.tree.Tree name = r.getChild(0);
			if ( name.getType()==ANTLRParser.TOKEN_REF ) {
				// check rule against patterns
				boolean isLitRule;
				for (String pattern : patterns) {
					isLitRule =
						defAlias(r, pattern, wiz, lexerRuleToStringLiteral);
					if ( isLitRule ) break;
				}
//				if ( !isLitRule ) System.out.println("no pattern matched");
			}
		}
		return lexerRuleToStringLiteral;
	}

	protected static boolean defAlias(GrammarAST r, String pattern,
									  org.antlr.runtime.tree.TreeWizard wiz,
									  List<Pair<GrammarAST,GrammarAST>> lexerRuleToStringLiteral)
	{
		HashMap<String, Object> nodes = new HashMap<String, Object>();
		if ( wiz.parse(r, pattern, nodes) ) {
			GrammarAST litNode = (GrammarAST)nodes.get("lit");
			GrammarAST nameNode = (GrammarAST)nodes.get("name");
			Pair<GrammarAST, GrammarAST> pair =
				new Pair<GrammarAST, GrammarAST>(nameNode, litNode);
			lexerRuleToStringLiteral.add(pair);
			return true;
		}
		return false;
	}

	public Set<String> getStringLiterals() {
		final Set<String> strings = new LinkedHashSet<String>();
		GrammarTreeVisitor collector = new GrammarTreeVisitor() {
			@Override
			public void stringRef(TerminalAST ref) {
				strings.add(ref.getText());
			}
			@Override
			public ErrorManager getErrorManager() { return tool.errMgr; }
		};
		collector.visitGrammar(ast);
		return strings;
	}

	public void setLookaheadDFA(int decision, DFA lookaheadDFA) {
		decisionDFAs.put(decision, lookaheadDFA);
	}

	public static Map<Integer, Interval> getStateToGrammarRegionMap(GrammarRootAST ast, IntervalSet grammarTokenTypes) {
		Map<Integer, Interval> stateToGrammarRegionMap = new HashMap<Integer, Interval>();
		if ( ast==null ) return stateToGrammarRegionMap;

		List<GrammarAST> nodes = ast.getNodesWithType(grammarTokenTypes);
		for (GrammarAST n : nodes) {
			if (n.atnState != null) {
				Interval tokenRegion = Interval.of(n.getTokenStartIndex(), n.getTokenStopIndex());
				org.antlr.runtime.tree.Tree ruleNode = null;
				// RULEs, BLOCKs of transformed recursive rules point to original token interval
				switch ( n.getType() ) {
					case ANTLRParser.RULE :
						ruleNode = n;
						break;
					case ANTLRParser.BLOCK :
					case ANTLRParser.CLOSURE :
						ruleNode = n.getAncestor(ANTLRParser.RULE);
						break;
				}
				if ( ruleNode instanceof RuleAST ) {
					String ruleName = ((RuleAST) ruleNode).getRuleName();
					Rule r = ast.g.getRule(ruleName);
					if ( r instanceof LeftRecursiveRule ) {
						RuleAST originalAST = ((LeftRecursiveRule) r).getOriginalAST();
						tokenRegion = Interval.of(originalAST.getTokenStartIndex(), originalAST.getTokenStopIndex());
					}
				}
				stateToGrammarRegionMap.put(n.atnState.stateNumber, tokenRegion);
			}
		}
		return stateToGrammarRegionMap;
	}

	/** Given an ATN state number, return the token index range within the grammar from which that ATN state was derived. */
	public Interval getStateToGrammarRegion(int atnStateNumber) {
		if ( stateToGrammarRegionMap==null ) {
			stateToGrammarRegionMap = getStateToGrammarRegionMap(ast, null); // map all nodes with non-null atn state ptr
		}
		if ( stateToGrammarRegionMap==null ) return Interval.INVALID;

		return stateToGrammarRegionMap.get(atnStateNumber);
	}

	public LexerInterpreter createLexerInterpreter(CharStream input) {
		if (this.isParser()) {
			throw new IllegalStateException("A lexer interpreter can only be created for a lexer or combined grammar.");
		}

		if (this.isCombined()) {
			return implicitLexer.createLexerInterpreter(input);
		}

		char[] serializedAtn = ATNSerializer.getSerializedAsChars(atn);
		ATN deserialized = new ATNDeserializer().deserialize(serializedAtn);
		return new LexerInterpreter(fileName, getVocabulary(), Arrays.asList(getRuleNames()), ((LexerGrammar)this).modes.keySet(), deserialized, input);
	}

	public ParserInterpreter createParserInterpreter(TokenStream tokenStream) {
		if (this.isLexer()) {
			throw new IllegalStateException("A parser interpreter can only be created for a parser or combined grammar.");
		}

		char[] serializedAtn = ATNSerializer.getSerializedAsChars(atn);
		ATN deserialized = new ATNDeserializer().deserialize(serializedAtn);
		return new ParserInterpreter(fileName, getVocabulary(), Arrays.asList(getRuleNames()), deserialized, tokenStream);
	}
}
