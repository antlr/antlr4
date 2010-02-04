package org.antlr.v4.tool;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.v4.Tool;
import org.antlr.v4.parse.ANTLRLexer;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.GrammarASTAdaptor;

import java.util.*;

public class Grammar {
    public static final Set doNotCopyOptionsToLexer =
        new HashSet() {
            {
                add("output"); add("ASTLabelType"); add("superClass");
                add("k"); add("backtrack"); add("memoize"); add("rewrite");
            }
        };

    public Tool tool;
    public String name;
    public GrammarRootAST ast;
    public String text; // testing only
    public String fileName;

    /** Was this created from a COMBINED grammar? */
    public boolean implicitLexer;    

    /** If we're imported, who imported us? If null, implies grammar is root */
    public Grammar parent;
    protected List<Grammar> importedGrammars;
    protected Map<String, Rule> rules = new HashMap<String, Rule>();

    /** A list of options specified at the grammar level such as language=Java. */
    protected Map<String, String> options;    

    public Grammar(Tool tool, GrammarRootAST ast) {
        if ( ast==null ) throw new IllegalArgumentException("can't pass null tree");
        this.tool = tool;
        this.ast = ast;
        this.name = ((GrammarAST)ast.getChild(0)).getText();
    }
    
    /** For testing */
    public Grammar(String fileName, String grammarText) throws RecognitionException {
        this.text = grammarText;
        ANTLRStringStream in = new ANTLRStringStream(grammarText);
        in.name = fileName;
        ANTLRLexer lexer = new ANTLRLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ANTLRParser p = new ANTLRParser(tokens);
        p.setTreeAdaptor(new GrammarASTAdaptor(in));
        ParserRuleReturnScope r = p.grammarSpec();
        ast = (GrammarRootAST)r.getTree();
        this.name = ((GrammarAST)ast.getChild(0)).getText();
        this.fileName = fileName;
    }

    public void loadImportedGrammars() {
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
                Grammar g = tool.load(importedGrammarName+".g");
                g.parent = this;
                importedGrammars.add(g);
            }
            catch (Exception e) {
                System.err.println("can't load grammar "+importedGrammarName);
            }
        }
    }

    public Rule getRule(String name) {
        return null;
    }

    /** Get list of all delegates from all grammars in the delegate subtree of g.
     *  The grammars are in delegation tree preorder.  Don't include ourselves
     *  in list as we're not a delegate of ourselves.
     */
    public List<Grammar> getDelegates() {
        if ( importedGrammars==null ) return null;
        List<Grammar> delegates = new ArrayList<Grammar>();
        for (int i = 0; i < importedGrammars.size(); i++) {
            Grammar d = importedGrammars.get(i);
            delegates.add(d);
            List<Grammar> ds = d.getDelegates();
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
    
    /** Return list of delegate grammars from root down to our parent.
     *  Order is [root, ..., this.parent].  (us not included).
     */
    public List<Grammar> getDelegationAncestors() {
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
        List<Grammar> grammarsFromRootToMe = getOutermostGrammar().getDelegationAncestors();
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
        if ( getType()==ANTLRParser.GRAMMAR ||
             (getType()==ANTLRParser.LEXER && implicitLexer) )
        {
            suffix = Grammar.getGrammarTypeToFileNameSuffix(getType());
        }
        return qualifiedName+suffix;
    }

    /** Return grammar directly imported by this grammar */
    public Grammar getImportedGrammar(String name) {
        for (int i = 0; i < importedGrammars.size(); i++) {
            Grammar g = importedGrammars.get(i);
            if ( g.name.equals(name) ) return g;
        }
        return null;
    }

    public int getType() {
        if ( ast!=null ) return ast.grammarType;
        return 0;
    }

    public String getTypeString() {
        if ( ast==null ) return null;
        if ( getType()==ANTLRParser.GRAMMAR ) return "combined";
        return ANTLRParser.tokenNames[getType()].toLowerCase();
    }

    public static String getGrammarTypeToFileNameSuffix(int type) {
        switch ( type ) {
            case ANTLRParser.LEXER : return "Lexer";
            case ANTLRParser.PARSER : return "Parser";
            case ANTLRParser.TREE : return "";
            // if combined grammar, gen Parser and Lexer will be done later
            case ANTLRParser.GRAMMAR : return "Parser";
            default :
                return "<invalid>";
        }
    }

}