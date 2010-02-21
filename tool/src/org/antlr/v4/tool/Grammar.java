package org.antlr.v4.tool;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.v4.Tool;
import org.antlr.v4.analysis.Label;
import org.antlr.v4.parse.ANTLRLexer;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.GrammarASTAdaptor;

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
            put("parser:RULE_LABEL", Rule.predefinedRulePropertiesDict);
            put("parser:TOKEN_LABEL", AttributeDict.predefinedTokenDict);
            put("tree:RULE_LABEL", Rule.predefinedTreeRulePropertiesDict);
            put("tree:TOKEN_LABEL", AttributeDict.predefinedTokenDict);
            put("tree:WILDCARD_TREE_LABEL", AttributeDict.predefinedTokenDict);
            put("combined:RULE_LABEL", Rule.predefinedRulePropertiesDict);
            put("combined:TOKEN_LABEL", AttributeDict.predefinedTokenDict);
		}};

	public Tool tool;
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
    public Map<String, Rule> rules = new LinkedHashMap<String, Rule>();

	/** Map token like ID (but not literals like "while") to its token type */
	public Map<String, Integer> tokenNameToTypeMap = new HashMap<String, Integer>();

	/** Map token literals like "while" to its token type.  It may be that
	 *  WHILE="while"=35, in which case both tokenIDToTypeMap and this
	 *  field will have entries both mapped to 35.
	 */
	public Map<String, Integer> stringLiteralToTypeMap = new HashMap<String, Integer>();

    /** Map a name to an action.
     *  The code generator will use this to fill holes in the output files.
     *  I track the AST node for the action in case I need the line number
     *  for errors.
     */
    public Map<String,ActionAST> namedActions = new HashMap<String,ActionAST>();

    /** A list of options specified at the grammar level such as language=Java. */
    public Map<String, String> options;

    public Map<String, AttributeDict> scopes = new LinkedHashMap<String, AttributeDict>();

	public Grammar(Tool tool, GrammarRootAST ast) {
        if ( ast==null ) throw new IllegalArgumentException("can't pass null tree");
        this.tool = tool;
        this.ast = ast;
        this.name = ((GrammarAST)ast.getChild(0)).getText();
    }
    
    /** For testing */
    public Grammar(String fileName, String grammarText) throws RecognitionException {
        this.text = grammarText;
		this.fileName = fileName;
		ANTLRStringStream in = new ANTLRStringStream(grammarText);
		in.name = fileName;
		ANTLRLexer lexer = new ANTLRLexer(in);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ANTLRParser p = new ANTLRParser(tokens);
		p.setTreeAdaptor(new GrammarASTAdaptor(in));
		ParserRuleReturnScope r = p.grammarSpec();
		if ( r.getTree() instanceof GrammarRootAST ) {
			this.ast = (GrammarRootAST)r.getTree();
			this.name = ((GrammarAST)ast.getChild(0)).getText();
		}
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
                Grammar g = new Grammar(tool, ast);
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
            String name = atAST.getChild(1).getText();
            namedActions.put(name, (ActionAST)atAST.getChild(2));
        }
    }

    public void defineRule(Rule r) { rules.put(r.name, r); }

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
        if ( getType()==ANTLRParser.COMBINED ||
             (getType()==ANTLRParser.LEXER && implicitLexer!=null) )
        {
            suffix = Grammar.getGrammarTypeToFileNameSuffix(getType());
        }
        return qualifiedName+suffix;
    }

    /** Return grammar directly imported by this grammar */
    public Grammar getImportedGrammar(String name) {
		for (Grammar g : importedGrammars) {
            if ( g.name.equals(name) ) return g;
        }
        return null;
    }

	public int getTokenType(String tokenName) {
		Integer I = null;
		if ( tokenName.charAt(0)=='\'') {
			I = stringLiteralToTypeMap.get(tokenName);
		}
		else { // must be a label like ID
			I = tokenNameToTypeMap.get(tokenName);
		}
		int i = (I!=null)?I.intValue(): Label.INVALID;
		//System.out.println("grammar type "+type+" "+tokenName+"->"+i);
		return i;
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

	public boolean resolvesToListLabel(String x, ActionAST node) { return false; }
	
	//	/** $x in grammar action can only be scope name */
//    public boolean resolves(String x, ActionAST node) {
//        return scopes.get(x)!=null;
//    }
//
//    /** $x.y makes no sense in grammar action; Rule.resolves()
//     *  shouldn't call this.
//     */
//    public boolean resolves(String x, String y, ActionAST node) { return false; }

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

}