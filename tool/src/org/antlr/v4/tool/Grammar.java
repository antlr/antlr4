package org.antlr.v4.tool;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.v4.Tool;
import org.antlr.v4.parse.ANTLRLexer;
import org.antlr.v4.parse.ANTLRParser;

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
    public GrammarAST ast;
    public String text; // testing only
    public String fileName;

    protected List<Grammar> importedGrammars;
    protected Map<String, Rule> rules = new HashMap<String, Rule>();

    /** A list of options specified at the grammar level such as language=Java. */
    protected Map<String, String> options;    

    public Grammar(Tool tool, GrammarAST ast) {
        this.tool = tool;
        this.ast = ast;
    }
    
    /** For testing */
    public Grammar(String grammarText) throws RecognitionException {
        this.text = grammarText;
        ANTLRStringStream in = new ANTLRStringStream(grammarText);
        ANTLRLexer lexer = new ANTLRLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ANTLRParser p = new ANTLRParser(tokens);
        ParserRuleReturnScope r = p.grammarSpec();
        ast = (GrammarAST) r.getTree();
        System.out.println(ast.toStringTree());
    }

    public void loadImportedGrammars() {
        CommonTree i = (CommonTree)ast.getFirstChildWithType(ANTLRParser.IMPORT);
        if ( i==null ) return;
        importedGrammars = new ArrayList<Grammar>();
        for (Object c : i.getChildren()) {
            CommonTree t = (CommonTree)c;
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
}