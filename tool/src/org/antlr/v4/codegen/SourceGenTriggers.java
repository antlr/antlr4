// $ANTLR ${project.version} ${buildNumber} SourceGenTriggers.g 2010-05-09 16:47:55

package org.antlr.v4.codegen;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;
import org.antlr.v4.codegen.src.CodeBlock;
import org.antlr.v4.codegen.src.InvokeRule;
import org.antlr.v4.codegen.src.MatchToken;
import org.antlr.v4.codegen.src.SrcOp;
import org.antlr.v4.tool.BlockAST;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.TerminalAST;

import java.util.ArrayList;
import java.util.List;

public class SourceGenTriggers extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SEMPRED", "FORCED_ACTION", "DOC_COMMENT", "SRC", "NLCHARS", "COMMENT", "DOUBLE_QUOTE_STRING_LITERAL", "DOUBLE_ANGLE_STRING_LITERAL", "ACTION_STRING_LITERAL", "ACTION_CHAR_LITERAL", "ARG_ACTION", "NESTED_ACTION", "ACTION", "ACTION_ESC", "WSNLCHARS", "OPTIONS", "TOKENS", "SCOPE", "IMPORT", "FRAGMENT", "LEXER", "PARSER", "TREE", "GRAMMAR", "PROTECTED", "PUBLIC", "PRIVATE", "RETURNS", "THROWS", "CATCH", "FINALLY", "TEMPLATE", "MODE", "COLON", "COLONCOLON", "COMMA", "SEMI", "LPAREN", "RPAREN", "IMPLIES", "LT", "GT", "ASSIGN", "QUESTION", "BANG", "STAR", "PLUS", "PLUS_ASSIGN", "OR", "ROOT", "DOLLAR", "DOT", "RANGE", "ETC", "RARROW", "TREE_BEGIN", "AT", "NOT", "RBRACE", "TOKEN_REF", "RULE_REF", "INT", "WSCHARS", "ESC_SEQ", "STRING_LITERAL", "HEX_DIGIT", "UNICODE_ESC", "WS", "ERRCHAR", "RULE", "RULES", "RULEMODIFIERS", "RULEACTIONS", "BLOCK", "REWRITE_BLOCK", "OPTIONAL", "CLOSURE", "POSITIVE_CLOSURE", "SYNPRED", "CHAR_RANGE", "EPSILON", "ALT", "ALTLIST", "ID", "ARG", "ARGLIST", "RET", "COMBINED", "INITACTION", "LABEL", "GATED_SEMPRED", "SYN_SEMPRED", "BACKTRACK_SEMPRED", "WILDCARD", "LIST", "ELEMENT_OPTIONS", "ST_RESULT", "RESULT", "ALT_REWRITE"
    };
    public static final int COMBINED=91;
    public static final int LT=44;
    public static final int STAR=49;
    public static final int BACKTRACK_SEMPRED=96;
    public static final int DOUBLE_ANGLE_STRING_LITERAL=11;
    public static final int FORCED_ACTION=5;
    public static final int ARGLIST=89;
    public static final int ALTLIST=86;
    public static final int NOT=61;
    public static final int EOF=-1;
    public static final int SEMPRED=4;
    public static final int ACTION=16;
    public static final int TOKEN_REF=63;
    public static final int RULEMODIFIERS=75;
    public static final int ST_RESULT=100;
    public static final int RPAREN=42;
    public static final int RET=90;
    public static final int IMPORT=22;
    public static final int STRING_LITERAL=68;
    public static final int ARG=88;
    public static final int ARG_ACTION=14;
    public static final int DOUBLE_QUOTE_STRING_LITERAL=10;
    public static final int COMMENT=9;
    public static final int ACTION_CHAR_LITERAL=13;
    public static final int GRAMMAR=27;
    public static final int RULEACTIONS=76;
    public static final int WSCHARS=66;
    public static final int INITACTION=92;
    public static final int ALT_REWRITE=102;
    public static final int IMPLIES=43;
    public static final int RULE=73;
    public static final int RBRACE=62;
    public static final int ACTION_ESC=17;
    public static final int PRIVATE=30;
    public static final int SRC=7;
    public static final int THROWS=32;
    public static final int CHAR_RANGE=83;
    public static final int INT=65;
    public static final int EPSILON=84;
    public static final int LIST=98;
    public static final int COLONCOLON=38;
    public static final int WSNLCHARS=18;
    public static final int WS=71;
    public static final int LEXER=24;
    public static final int OR=52;
    public static final int GT=45;
    public static final int CATCH=33;
    public static final int CLOSURE=80;
    public static final int PARSER=25;
    public static final int DOLLAR=54;
    public static final int PROTECTED=28;
    public static final int ELEMENT_OPTIONS=99;
    public static final int NESTED_ACTION=15;
    public static final int FRAGMENT=23;
    public static final int ID=87;
    public static final int TREE_BEGIN=59;
    public static final int LPAREN=41;
    public static final int AT=60;
    public static final int ESC_SEQ=67;
    public static final int ALT=85;
    public static final int TREE=26;
    public static final int SCOPE=21;
    public static final int ETC=57;
    public static final int COMMA=39;
    public static final int WILDCARD=97;
    public static final int DOC_COMMENT=6;
    public static final int PLUS=50;
    public static final int REWRITE_BLOCK=78;
    public static final int DOT=55;
    public static final int MODE=36;
    public static final int RETURNS=31;
    public static final int RULES=74;
    public static final int RARROW=58;
    public static final int UNICODE_ESC=70;
    public static final int HEX_DIGIT=69;
    public static final int RANGE=56;
    public static final int TOKENS=20;
    public static final int RESULT=101;
    public static final int GATED_SEMPRED=94;
    public static final int BANG=48;
    public static final int ACTION_STRING_LITERAL=12;
    public static final int ROOT=53;
    public static final int SEMI=40;
    public static final int RULE_REF=64;
    public static final int NLCHARS=8;
    public static final int OPTIONAL=79;
    public static final int SYNPRED=82;
    public static final int COLON=37;
    public static final int QUESTION=47;
    public static final int FINALLY=34;
    public static final int TEMPLATE=35;
    public static final int LABEL=93;
    public static final int SYN_SEMPRED=95;
    public static final int ERRCHAR=72;
    public static final int BLOCK=77;
    public static final int ASSIGN=46;
    public static final int PLUS_ASSIGN=51;
    public static final int PUBLIC=29;
    public static final int POSITIVE_CLOSURE=81;
    public static final int OPTIONS=19;

    // delegates
    // delegators


        public SourceGenTriggers(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public SourceGenTriggers(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return SourceGenTriggers.tokenNames; }
    public String getGrammarFileName() { return "SourceGenTriggers.g"; }


    // TODO: identical grammar to NFABytecodeTriggers; would be nice to combine
    	public CodeGenerator gen;
        public SourceGenTriggers(TreeNodeStream input, CodeGenerator gen) {
        	this(input);
        	this.gen = gen;
        }



    // $ANTLR start "block"
    // SourceGenTriggers.g:27:1: block[GrammarAST label, GrammarAST ebnfRoot] returns [SrcOp omo] : ^(blk= BLOCK ( ^( OPTIONS ( . )+ ) )? ( alternative )+ ) ;
    public final SrcOp block(GrammarAST label, GrammarAST ebnfRoot) throws RecognitionException {
        SrcOp omo = null;

        GrammarAST blk=null;
        CodeBlock alternative1 = null;


        try {
            // SourceGenTriggers.g:28:5: ( ^(blk= BLOCK ( ^( OPTIONS ( . )+ ) )? ( alternative )+ ) )
            // SourceGenTriggers.g:28:7: ^(blk= BLOCK ( ^( OPTIONS ( . )+ ) )? ( alternative )+ )
            {
            blk=(GrammarAST)match(input,BLOCK,FOLLOW_BLOCK_in_block72); 

            match(input, Token.DOWN, null); 
            // SourceGenTriggers.g:28:20: ( ^( OPTIONS ( . )+ ) )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==OPTIONS) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // SourceGenTriggers.g:28:21: ^( OPTIONS ( . )+ )
                    {
                    match(input,OPTIONS,FOLLOW_OPTIONS_in_block76); 

                    match(input, Token.DOWN, null); 
                    // SourceGenTriggers.g:28:31: ( . )+
                    int cnt1=0;
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0>=SEMPRED && LA1_0<=ALT_REWRITE)) ) {
                            alt1=1;
                        }
                        else if ( (LA1_0==UP) ) {
                            alt1=2;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // SourceGenTriggers.g:28:31: .
                    	    {
                    	    matchAny(input); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt1 >= 1 ) break loop1;
                                EarlyExitException eee =
                                    new EarlyExitException(1, input);
                                throw eee;
                        }
                        cnt1++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;

            }

            List<CodeBlock> alts = new ArrayList<CodeBlock>();
            // SourceGenTriggers.g:30:7: ( alternative )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==ALT||LA3_0==ALT_REWRITE) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // SourceGenTriggers.g:30:9: alternative
            	    {
            	    pushFollow(FOLLOW_alternative_in_block96);
            	    alternative1=alternative();

            	    state._fsp--;

            	    alts.add(alternative1);

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            match(input, Token.UP, null); 

                	if ( alts.size()==1 && ebnfRoot==null) return alts.get(0);
                	omo = gen.getChoiceBlock((BlockAST)blk, ebnfRoot, alts);
                	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return omo;
    }
    // $ANTLR end "block"


    // $ANTLR start "alternative"
    // SourceGenTriggers.g:38:1: alternative returns [CodeBlock omo] : ( ^( ALT_REWRITE a= alternative . ) | ^( ALT EPSILON ) | ^( ALT ( element )+ ) );
    public final CodeBlock alternative() throws RecognitionException {
        CodeBlock omo = null;

        CodeBlock a = null;

        SrcOp element2 = null;


        List<SrcOp> elems = new ArrayList<SrcOp>();
        try {
            // SourceGenTriggers.g:40:5: ( ^( ALT_REWRITE a= alternative . ) | ^( ALT EPSILON ) | ^( ALT ( element )+ ) )
            int alt5=3;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==ALT_REWRITE) ) {
                alt5=1;
            }
            else if ( (LA5_0==ALT) ) {
                int LA5_2 = input.LA(2);

                if ( (LA5_2==DOWN) ) {
                    int LA5_3 = input.LA(3);

                    if ( (LA5_3==EPSILON) ) {
                        alt5=2;
                    }
                    else if ( (LA5_3==SEMPRED||LA5_3==ACTION||LA5_3==IMPLIES||LA5_3==ASSIGN||LA5_3==BANG||LA5_3==PLUS_ASSIGN||LA5_3==ROOT||(LA5_3>=DOT && LA5_3<=RANGE)||LA5_3==TREE_BEGIN||LA5_3==NOT||(LA5_3>=TOKEN_REF && LA5_3<=RULE_REF)||LA5_3==STRING_LITERAL||LA5_3==BLOCK||(LA5_3>=OPTIONAL && LA5_3<=POSITIVE_CLOSURE)||LA5_3==GATED_SEMPRED||LA5_3==WILDCARD) ) {
                        alt5=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 5, 3, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // SourceGenTriggers.g:40:7: ^( ALT_REWRITE a= alternative . )
                    {
                    match(input,ALT_REWRITE,FOLLOW_ALT_REWRITE_in_alternative142); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_alternative_in_alternative146);
                    a=alternative();

                    state._fsp--;

                    matchAny(input); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // SourceGenTriggers.g:41:7: ^( ALT EPSILON )
                    {
                    match(input,ALT,FOLLOW_ALT_in_alternative159); 

                    match(input, Token.DOWN, null); 
                    match(input,EPSILON,FOLLOW_EPSILON_in_alternative161); 

                    match(input, Token.UP, null); 
                    omo = new CodeBlock(gen);

                    }
                    break;
                case 3 :
                    // SourceGenTriggers.g:42:9: ^( ALT ( element )+ )
                    {
                    match(input,ALT,FOLLOW_ALT_in_alternative176); 

                    match(input, Token.DOWN, null); 
                    // SourceGenTriggers.g:42:16: ( element )+
                    int cnt4=0;
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0==SEMPRED||LA4_0==ACTION||LA4_0==IMPLIES||LA4_0==ASSIGN||LA4_0==BANG||LA4_0==PLUS_ASSIGN||LA4_0==ROOT||(LA4_0>=DOT && LA4_0<=RANGE)||LA4_0==TREE_BEGIN||LA4_0==NOT||(LA4_0>=TOKEN_REF && LA4_0<=RULE_REF)||LA4_0==STRING_LITERAL||LA4_0==BLOCK||(LA4_0>=OPTIONAL && LA4_0<=POSITIVE_CLOSURE)||LA4_0==GATED_SEMPRED||LA4_0==WILDCARD) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // SourceGenTriggers.g:42:18: element
                    	    {
                    	    pushFollow(FOLLOW_element_in_alternative180);
                    	    element2=element();

                    	    state._fsp--;

                    	    elems.add(element2);

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt4 >= 1 ) break loop4;
                                EarlyExitException eee =
                                    new EarlyExitException(4, input);
                                throw eee;
                        }
                        cnt4++;
                    } while (true);


                    match(input, Token.UP, null); 
                    omo = new CodeBlock(gen, elems);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return omo;
    }
    // $ANTLR end "alternative"


    // $ANTLR start "element"
    // SourceGenTriggers.g:45:1: element returns [SrcOp omo] : ( labeledElement | atom[null] | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );
    public final SrcOp element() throws RecognitionException {
        SrcOp omo = null;

        SrcOp labeledElement3 = null;

        SrcOp atom4 = null;

        SrcOp ebnf5 = null;


        try {
            // SourceGenTriggers.g:46:2: ( labeledElement | atom[null] | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec )
            int alt6=7;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // SourceGenTriggers.g:46:4: labeledElement
                    {
                    pushFollow(FOLLOW_labeledElement_in_element207);
                    labeledElement3=labeledElement();

                    state._fsp--;

                    omo = labeledElement3;

                    }
                    break;
                case 2 :
                    // SourceGenTriggers.g:47:4: atom[null]
                    {
                    pushFollow(FOLLOW_atom_in_element218);
                    atom4=atom(null);

                    state._fsp--;

                    omo = atom4;

                    }
                    break;
                case 3 :
                    // SourceGenTriggers.g:48:4: ebnf
                    {
                    pushFollow(FOLLOW_ebnf_in_element231);
                    ebnf5=ebnf();

                    state._fsp--;

                    omo = ebnf5;

                    }
                    break;
                case 4 :
                    // SourceGenTriggers.g:49:6: ACTION
                    {
                    match(input,ACTION,FOLLOW_ACTION_in_element252); 

                    }
                    break;
                case 5 :
                    // SourceGenTriggers.g:50:6: SEMPRED
                    {
                    match(input,SEMPRED,FOLLOW_SEMPRED_in_element265); 

                    }
                    break;
                case 6 :
                    // SourceGenTriggers.g:51:4: GATED_SEMPRED
                    {
                    match(input,GATED_SEMPRED,FOLLOW_GATED_SEMPRED_in_element275); 

                    }
                    break;
                case 7 :
                    // SourceGenTriggers.g:52:4: treeSpec
                    {
                    pushFollow(FOLLOW_treeSpec_in_element281);
                    treeSpec();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return omo;
    }
    // $ANTLR end "element"


    // $ANTLR start "labeledElement"
    // SourceGenTriggers.g:55:1: labeledElement returns [SrcOp omo] : ( ^( ASSIGN ID atom[$ID] ) | ^( ASSIGN ID block[$ID,null] ) | ^( PLUS_ASSIGN ID atom[$ID] ) | ^( PLUS_ASSIGN ID block[$ID,null] ) );
    public final SrcOp labeledElement() throws RecognitionException {
        SrcOp omo = null;

        GrammarAST ID6=null;
        GrammarAST ID8=null;
        GrammarAST ID10=null;
        GrammarAST ID12=null;
        SrcOp atom7 = null;

        SrcOp block9 = null;

        SrcOp atom11 = null;

        SrcOp block13 = null;


        try {
            // SourceGenTriggers.g:56:2: ( ^( ASSIGN ID atom[$ID] ) | ^( ASSIGN ID block[$ID,null] ) | ^( PLUS_ASSIGN ID atom[$ID] ) | ^( PLUS_ASSIGN ID block[$ID,null] ) )
            int alt7=4;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // SourceGenTriggers.g:56:4: ^( ASSIGN ID atom[$ID] )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement303); 

                    match(input, Token.DOWN, null); 
                    ID6=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement305); 
                    pushFollow(FOLLOW_atom_in_labeledElement307);
                    atom7=atom(ID6);

                    state._fsp--;


                    match(input, Token.UP, null); 
                    omo = atom7;

                    }
                    break;
                case 2 :
                    // SourceGenTriggers.g:57:4: ^( ASSIGN ID block[$ID,null] )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement321); 

                    match(input, Token.DOWN, null); 
                    ID8=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement323); 
                    pushFollow(FOLLOW_block_in_labeledElement325);
                    block9=block(ID8, null);

                    state._fsp--;


                    match(input, Token.UP, null); 
                    omo = block9;

                    }
                    break;
                case 3 :
                    // SourceGenTriggers.g:58:4: ^( PLUS_ASSIGN ID atom[$ID] )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement336); 

                    match(input, Token.DOWN, null); 
                    ID10=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement338); 
                    pushFollow(FOLLOW_atom_in_labeledElement340);
                    atom11=atom(ID10);

                    state._fsp--;


                    match(input, Token.UP, null); 
                    omo = atom11;

                    }
                    break;
                case 4 :
                    // SourceGenTriggers.g:59:4: ^( PLUS_ASSIGN ID block[$ID,null] )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement352); 

                    match(input, Token.DOWN, null); 
                    ID12=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement354); 
                    pushFollow(FOLLOW_block_in_labeledElement356);
                    block13=block(ID12, null);

                    state._fsp--;


                    match(input, Token.UP, null); 
                    omo = block13;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return omo;
    }
    // $ANTLR end "labeledElement"


    // $ANTLR start "treeSpec"
    // SourceGenTriggers.g:62:1: treeSpec returns [SrcOp omo] : ^( TREE_BEGIN (e= element )+ ) ;
    public final SrcOp treeSpec() throws RecognitionException {
        SrcOp omo = null;

        SrcOp e = null;


        try {
            // SourceGenTriggers.g:63:5: ( ^( TREE_BEGIN (e= element )+ ) )
            // SourceGenTriggers.g:63:7: ^( TREE_BEGIN (e= element )+ )
            {
            match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_treeSpec380); 

            match(input, Token.DOWN, null); 
            // SourceGenTriggers.g:63:21: (e= element )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==SEMPRED||LA8_0==ACTION||LA8_0==IMPLIES||LA8_0==ASSIGN||LA8_0==BANG||LA8_0==PLUS_ASSIGN||LA8_0==ROOT||(LA8_0>=DOT && LA8_0<=RANGE)||LA8_0==TREE_BEGIN||LA8_0==NOT||(LA8_0>=TOKEN_REF && LA8_0<=RULE_REF)||LA8_0==STRING_LITERAL||LA8_0==BLOCK||(LA8_0>=OPTIONAL && LA8_0<=POSITIVE_CLOSURE)||LA8_0==GATED_SEMPRED||LA8_0==WILDCARD) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // SourceGenTriggers.g:63:22: e= element
            	    {
            	    pushFollow(FOLLOW_element_in_treeSpec386);
            	    e=element();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return omo;
    }
    // $ANTLR end "treeSpec"


    // $ANTLR start "ebnf"
    // SourceGenTriggers.g:66:1: ebnf returns [SrcOp omo] : ( ^( astBlockSuffix block[null,null] ) | ^( OPTIONAL block[null,$OPTIONAL] ) | ^( CLOSURE block[null,$CLOSURE] ) | ^( POSITIVE_CLOSURE block[null,$POSITIVE_CLOSURE] ) | block[null, null] );
    public final SrcOp ebnf() throws RecognitionException {
        SrcOp omo = null;

        GrammarAST OPTIONAL14=null;
        GrammarAST CLOSURE16=null;
        GrammarAST POSITIVE_CLOSURE18=null;
        SrcOp block15 = null;

        SrcOp block17 = null;

        SrcOp block19 = null;

        SrcOp block20 = null;


        try {
            // SourceGenTriggers.g:67:2: ( ^( astBlockSuffix block[null,null] ) | ^( OPTIONAL block[null,$OPTIONAL] ) | ^( CLOSURE block[null,$CLOSURE] ) | ^( POSITIVE_CLOSURE block[null,$POSITIVE_CLOSURE] ) | block[null, null] )
            int alt9=5;
            switch ( input.LA(1) ) {
            case IMPLIES:
            case BANG:
            case ROOT:
                {
                alt9=1;
                }
                break;
            case OPTIONAL:
                {
                alt9=2;
                }
                break;
            case CLOSURE:
                {
                alt9=3;
                }
                break;
            case POSITIVE_CLOSURE:
                {
                alt9=4;
                }
                break;
            case BLOCK:
                {
                alt9=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // SourceGenTriggers.g:67:4: ^( astBlockSuffix block[null,null] )
                    {
                    pushFollow(FOLLOW_astBlockSuffix_in_ebnf410);
                    astBlockSuffix();

                    state._fsp--;


                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf412);
                    block(null, null);

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // SourceGenTriggers.g:68:4: ^( OPTIONAL block[null,$OPTIONAL] )
                    {
                    OPTIONAL14=(GrammarAST)match(input,OPTIONAL,FOLLOW_OPTIONAL_in_ebnf422); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf424);
                    block15=block(null, OPTIONAL14);

                    state._fsp--;


                    match(input, Token.UP, null); 
                    omo = block15;

                    }
                    break;
                case 3 :
                    // SourceGenTriggers.g:69:4: ^( CLOSURE block[null,$CLOSURE] )
                    {
                    CLOSURE16=(GrammarAST)match(input,CLOSURE,FOLLOW_CLOSURE_in_ebnf436); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf438);
                    block17=block(null, CLOSURE16);

                    state._fsp--;


                    match(input, Token.UP, null); 
                    omo = block17;

                    }
                    break;
                case 4 :
                    // SourceGenTriggers.g:70:4: ^( POSITIVE_CLOSURE block[null,$POSITIVE_CLOSURE] )
                    {
                    POSITIVE_CLOSURE18=(GrammarAST)match(input,POSITIVE_CLOSURE,FOLLOW_POSITIVE_CLOSURE_in_ebnf451); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf453);
                    block19=block(null, POSITIVE_CLOSURE18);

                    state._fsp--;


                    match(input, Token.UP, null); 
                    omo = block19;

                    }
                    break;
                case 5 :
                    // SourceGenTriggers.g:72:5: block[null, null]
                    {
                    pushFollow(FOLLOW_block_in_ebnf477);
                    block20=block(null, null);

                    state._fsp--;

                    omo = block20;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return omo;
    }
    // $ANTLR end "ebnf"


    // $ANTLR start "astBlockSuffix"
    // SourceGenTriggers.g:75:1: astBlockSuffix : ( ROOT | IMPLIES | BANG );
    public final void astBlockSuffix() throws RecognitionException {
        try {
            // SourceGenTriggers.g:76:5: ( ROOT | IMPLIES | BANG )
            // SourceGenTriggers.g:
            {
            if ( input.LA(1)==IMPLIES||input.LA(1)==BANG||input.LA(1)==ROOT ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "astBlockSuffix"


    // $ANTLR start "atom"
    // SourceGenTriggers.g:83:1: atom[GrammarAST label] returns [SrcOp omo] : ( ^( ROOT range[label] ) | ^( BANG range[label] ) | ^( ROOT notSet[label] ) | ^( BANG notSet[label] ) | notSet[label] | range[label] | ^( DOT ID terminal[label] ) | ^( DOT ID ruleref[label] ) | ^( WILDCARD . ) | WILDCARD | terminal[label] | ruleref[label] );
    public final SrcOp atom(GrammarAST label) throws RecognitionException {
        SrcOp omo = null;

        SrcOp range21 = null;

        SrcOp notSet22 = null;

        SrcOp range23 = null;

        MatchToken terminal24 = null;

        SrcOp ruleref25 = null;


        try {
            // SourceGenTriggers.g:84:2: ( ^( ROOT range[label] ) | ^( BANG range[label] ) | ^( ROOT notSet[label] ) | ^( BANG notSet[label] ) | notSet[label] | range[label] | ^( DOT ID terminal[label] ) | ^( DOT ID ruleref[label] ) | ^( WILDCARD . ) | WILDCARD | terminal[label] | ruleref[label] )
            int alt10=12;
            alt10 = dfa10.predict(input);
            switch (alt10) {
                case 1 :
                    // SourceGenTriggers.g:84:4: ^( ROOT range[label] )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom539); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom541);
                    range(label);

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // SourceGenTriggers.g:85:4: ^( BANG range[label] )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom552); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom554);
                    range21=range(label);

                    state._fsp--;


                    match(input, Token.UP, null); 
                    omo = range21;

                    }
                    break;
                case 3 :
                    // SourceGenTriggers.g:86:4: ^( ROOT notSet[label] )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom566); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom568);
                    notSet(label);

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // SourceGenTriggers.g:87:4: ^( BANG notSet[label] )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom579); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom581);
                    notSet22=notSet(label);

                    state._fsp--;


                    match(input, Token.UP, null); 
                    omo = notSet22;

                    }
                    break;
                case 5 :
                    // SourceGenTriggers.g:88:4: notSet[label]
                    {
                    pushFollow(FOLLOW_notSet_in_atom592);
                    notSet(label);

                    state._fsp--;


                    }
                    break;
                case 6 :
                    // SourceGenTriggers.g:89:4: range[label]
                    {
                    pushFollow(FOLLOW_range_in_atom603);
                    range23=range(label);

                    state._fsp--;

                    omo = range23;

                    }
                    break;
                case 7 :
                    // SourceGenTriggers.g:90:4: ^( DOT ID terminal[label] )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom616); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom618); 
                    pushFollow(FOLLOW_terminal_in_atom620);
                    terminal(label);

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // SourceGenTriggers.g:91:4: ^( DOT ID ruleref[label] )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom628); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom630); 
                    pushFollow(FOLLOW_ruleref_in_atom632);
                    ruleref(label);

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 9 :
                    // SourceGenTriggers.g:92:7: ^( WILDCARD . )
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_atom643); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // SourceGenTriggers.g:93:7: WILDCARD
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_atom658); 

                    }
                    break;
                case 11 :
                    // SourceGenTriggers.g:94:9: terminal[label]
                    {
                    pushFollow(FOLLOW_terminal_in_atom672);
                    terminal24=terminal(label);

                    state._fsp--;

                    omo = terminal24;

                    }
                    break;
                case 12 :
                    // SourceGenTriggers.g:95:9: ruleref[label]
                    {
                    pushFollow(FOLLOW_ruleref_in_atom688);
                    ruleref25=ruleref(label);

                    state._fsp--;

                    omo = ruleref25;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return omo;
    }
    // $ANTLR end "atom"


    // $ANTLR start "notSet"
    // SourceGenTriggers.g:98:1: notSet[GrammarAST label] returns [SrcOp omo] : ( ^( NOT terminal[label] ) | ^( NOT block[label,null] ) );
    public final SrcOp notSet(GrammarAST label) throws RecognitionException {
        SrcOp omo = null;

        try {
            // SourceGenTriggers.g:99:5: ( ^( NOT terminal[label] ) | ^( NOT block[label,null] ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==NOT) ) {
                int LA11_1 = input.LA(2);

                if ( (LA11_1==DOWN) ) {
                    int LA11_2 = input.LA(3);

                    if ( (LA11_2==BLOCK) ) {
                        alt11=2;
                    }
                    else if ( (LA11_2==BANG||LA11_2==ROOT||LA11_2==TOKEN_REF||LA11_2==STRING_LITERAL) ) {
                        alt11=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 11, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 11, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // SourceGenTriggers.g:99:7: ^( NOT terminal[label] )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet717); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_notSet719);
                    terminal(label);

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // SourceGenTriggers.g:100:7: ^( NOT block[label,null] )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet732); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_notSet734);
                    block(label, null);

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return omo;
    }
    // $ANTLR end "notSet"


    // $ANTLR start "ruleref"
    // SourceGenTriggers.g:103:1: ruleref[GrammarAST label] returns [SrcOp omo] : ( ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) ) | ^( BANG ^( RULE_REF ( ARG_ACTION )? ) ) | ^( RULE_REF ( ARG_ACTION )? ) );
    public final SrcOp ruleref(GrammarAST label) throws RecognitionException {
        SrcOp omo = null;

        GrammarAST RULE_REF26=null;
        GrammarAST RULE_REF27=null;

        try {
            // SourceGenTriggers.g:104:5: ( ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) ) | ^( BANG ^( RULE_REF ( ARG_ACTION )? ) ) | ^( RULE_REF ( ARG_ACTION )? ) )
            int alt15=3;
            switch ( input.LA(1) ) {
            case ROOT:
                {
                alt15=1;
                }
                break;
            case BANG:
                {
                alt15=2;
                }
                break;
            case RULE_REF:
                {
                alt15=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // SourceGenTriggers.g:104:7: ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_ruleref762); 

                    match(input, Token.DOWN, null); 
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref765); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // SourceGenTriggers.g:104:25: ( ARG_ACTION )?
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==ARG_ACTION) ) {
                            alt12=1;
                        }
                        switch (alt12) {
                            case 1 :
                                // SourceGenTriggers.g:104:25: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref767); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // SourceGenTriggers.g:105:7: ^( BANG ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,BANG,FOLLOW_BANG_in_ruleref779); 

                    match(input, Token.DOWN, null); 
                    RULE_REF26=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref782); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // SourceGenTriggers.g:105:25: ( ARG_ACTION )?
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==ARG_ACTION) ) {
                            alt13=1;
                        }
                        switch (alt13) {
                            case 1 :
                                // SourceGenTriggers.g:105:25: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref784); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    match(input, Token.UP, null); 
                    omo = new InvokeRule(gen, RULE_REF26, label);

                    }
                    break;
                case 3 :
                    // SourceGenTriggers.g:106:7: ^( RULE_REF ( ARG_ACTION )? )
                    {
                    RULE_REF27=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref798); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // SourceGenTriggers.g:106:18: ( ARG_ACTION )?
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==ARG_ACTION) ) {
                            alt14=1;
                        }
                        switch (alt14) {
                            case 1 :
                                // SourceGenTriggers.g:106:18: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref800); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }
                    omo = new InvokeRule(gen, RULE_REF27, label);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return omo;
    }
    // $ANTLR end "ruleref"


    // $ANTLR start "range"
    // SourceGenTriggers.g:109:1: range[GrammarAST label] returns [SrcOp omo] : ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) ;
    public final SrcOp range(GrammarAST label) throws RecognitionException {
        SrcOp omo = null;

        GrammarAST a=null;
        GrammarAST b=null;

        try {
            // SourceGenTriggers.g:110:5: ( ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) )
            // SourceGenTriggers.g:110:7: ^( RANGE a= STRING_LITERAL b= STRING_LITERAL )
            {
            match(input,RANGE,FOLLOW_RANGE_in_range829); 

            match(input, Token.DOWN, null); 
            a=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range833); 
            b=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range837); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return omo;
    }
    // $ANTLR end "range"


    // $ANTLR start "terminal"
    // SourceGenTriggers.g:113:1: terminal[GrammarAST label] returns [MatchToken omo] : ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( ROOT terminal[label] ) | ^( BANG terminal[label] ) );
    public final MatchToken terminal(GrammarAST label) throws RecognitionException {
        MatchToken omo = null;

        GrammarAST STRING_LITERAL28=null;
        GrammarAST STRING_LITERAL29=null;
        GrammarAST TOKEN_REF30=null;
        GrammarAST TOKEN_REF31=null;
        GrammarAST TOKEN_REF32=null;

        try {
            // SourceGenTriggers.g:114:5: ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( ROOT terminal[label] ) | ^( BANG terminal[label] ) )
            int alt16=7;
            alt16 = dfa16.predict(input);
            switch (alt16) {
                case 1 :
                    // SourceGenTriggers.g:114:8: ^( STRING_LITERAL . )
                    {
                    STRING_LITERAL28=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal867); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    omo = new MatchToken(gen, (TerminalAST)STRING_LITERAL28, label);

                    }
                    break;
                case 2 :
                    // SourceGenTriggers.g:115:7: STRING_LITERAL
                    {
                    STRING_LITERAL29=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal882); 
                    omo = new MatchToken(gen, (TerminalAST)STRING_LITERAL29, label);

                    }
                    break;
                case 3 :
                    // SourceGenTriggers.g:116:7: ^( TOKEN_REF ARG_ACTION . )
                    {
                    TOKEN_REF30=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal896); 

                    match(input, Token.DOWN, null); 
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal898); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    omo = new MatchToken(gen, (TerminalAST)TOKEN_REF30, label);

                    }
                    break;
                case 4 :
                    // SourceGenTriggers.g:117:7: ^( TOKEN_REF . )
                    {
                    TOKEN_REF31=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal912); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    omo = new MatchToken(gen, (TerminalAST)TOKEN_REF31, label);

                    }
                    break;
                case 5 :
                    // SourceGenTriggers.g:118:7: TOKEN_REF
                    {
                    TOKEN_REF32=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal928); 
                    omo = new MatchToken(gen, (TerminalAST)TOKEN_REF32, label);

                    }
                    break;
                case 6 :
                    // SourceGenTriggers.g:119:7: ^( ROOT terminal[label] )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_terminal943); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_terminal945);
                    terminal(label);

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // SourceGenTriggers.g:120:7: ^( BANG terminal[label] )
                    {
                    match(input,BANG,FOLLOW_BANG_in_terminal959); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_terminal961);
                    terminal(label);

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return omo;
    }
    // $ANTLR end "terminal"

    // Delegated rules


    protected DFA6 dfa6 = new DFA6(this);
    protected DFA7 dfa7 = new DFA7(this);
    protected DFA10 dfa10 = new DFA10(this);
    protected DFA16 dfa16 = new DFA16(this);
    static final String DFA6_eotS =
        "\14\uffff";
    static final String DFA6_eofS =
        "\14\uffff";
    static final String DFA6_minS =
        "\1\4\1\uffff\2\2\6\uffff\2\60";
    static final String DFA6_maxS =
        "\1\141\1\uffff\2\2\6\uffff\2\115";
    static final String DFA6_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\1\3\1\4\1\5\1\6\1\7\2\uffff";
    static final String DFA6_specialS =
        "\14\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\7\13\uffff\1\6\32\uffff\1\5\2\uffff\1\1\1\uffff\1\3\2\uffff"+
            "\1\1\1\uffff\1\2\1\uffff\2\4\2\uffff\1\11\1\uffff\1\4\1\uffff"+
            "\2\4\3\uffff\1\4\10\uffff\1\5\1\uffff\3\5\14\uffff\1\10\2\uffff"+
            "\1\4",
            "",
            "\1\12",
            "\1\13",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\4\4\uffff\1\4\2\uffff\1\4\4\uffff\1\4\1\uffff\2\4\3\uffff"+
            "\1\4\10\uffff\1\5",
            "\1\4\4\uffff\1\4\2\uffff\1\4\4\uffff\1\4\1\uffff\2\4\3\uffff"+
            "\1\4\10\uffff\1\5"
    };

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }
        public String getDescription() {
            return "45:1: element returns [SrcOp omo] : ( labeledElement | atom[null] | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );";
        }
    }
    static final String DFA7_eotS =
        "\13\uffff";
    static final String DFA7_eofS =
        "\13\uffff";
    static final String DFA7_minS =
        "\1\56\2\2\2\127\2\60\4\uffff";
    static final String DFA7_maxS =
        "\1\63\2\2\2\127\2\141\4\uffff";
    static final String DFA7_acceptS =
        "\7\uffff\1\2\1\1\1\4\1\3";
    static final String DFA7_specialS =
        "\13\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\1\4\uffff\1\2",
            "\1\3",
            "\1\4",
            "\1\5",
            "\1\6",
            "\1\10\4\uffff\1\10\1\uffff\2\10\4\uffff\1\10\1\uffff\2\10\3"+
            "\uffff\1\10\10\uffff\1\7\23\uffff\1\10",
            "\1\12\4\uffff\1\12\1\uffff\2\12\4\uffff\1\12\1\uffff\2\12\3"+
            "\uffff\1\12\10\uffff\1\11\23\uffff\1\12",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }
        public String getDescription() {
            return "55:1: labeledElement returns [SrcOp omo] : ( ^( ASSIGN ID atom[$ID] ) | ^( ASSIGN ID block[$ID,null] ) | ^( PLUS_ASSIGN ID atom[$ID] ) | ^( PLUS_ASSIGN ID block[$ID,null] ) );";
        }
    }
    static final String DFA10_eotS =
        "\31\uffff";
    static final String DFA10_eofS =
        "\31\uffff";
    static final String DFA10_minS =
        "\1\60\2\2\2\uffff\2\2\2\uffff\2\60\1\127\6\uffff\1\60\1\uffff\2"+
        "\2\1\uffff\2\60";
    static final String DFA10_maxS =
        "\1\141\2\2\2\uffff\1\2\1\141\2\uffff\2\104\1\127\6\uffff\1\104\1"+
        "\uffff\2\2\1\uffff\2\104";
    static final String DFA10_acceptS =
        "\3\uffff\1\5\1\6\2\uffff\1\13\1\14\3\uffff\1\11\1\12\1\1\1\3\1\2"+
        "\1\4\1\uffff\1\7\2\uffff\1\10\2\uffff";
    static final String DFA10_specialS =
        "\31\uffff}>";
    static final String[] DFA10_transitionS = {
            "\1\2\4\uffff\1\1\1\uffff\1\5\1\4\4\uffff\1\3\1\uffff\1\7\1\10"+
            "\3\uffff\1\7\34\uffff\1\6",
            "\1\11",
            "\1\12",
            "",
            "",
            "\1\13",
            "\1\14\2\15\13\uffff\1\15\32\uffff\1\15\2\uffff\1\15\1\uffff"+
            "\1\15\2\uffff\1\15\1\uffff\1\15\1\uffff\2\15\2\uffff\1\15\1"+
            "\uffff\1\15\1\uffff\2\15\3\uffff\1\15\10\uffff\1\15\1\uffff"+
            "\3\15\14\uffff\1\15\2\uffff\1\15",
            "",
            "",
            "\1\7\4\uffff\1\7\2\uffff\1\16\4\uffff\1\17\1\uffff\1\7\1\10"+
            "\3\uffff\1\7",
            "\1\7\4\uffff\1\7\2\uffff\1\20\4\uffff\1\21\1\uffff\1\7\1\10"+
            "\3\uffff\1\7",
            "\1\22",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\25\4\uffff\1\24\11\uffff\1\23\1\26\3\uffff\1\23",
            "",
            "\1\27",
            "\1\30",
            "",
            "\1\23\4\uffff\1\23\11\uffff\1\23\1\26\3\uffff\1\23",
            "\1\23\4\uffff\1\23\11\uffff\1\23\1\26\3\uffff\1\23"
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "83:1: atom[GrammarAST label] returns [SrcOp omo] : ( ^( ROOT range[label] ) | ^( BANG range[label] ) | ^( ROOT notSet[label] ) | ^( BANG notSet[label] ) | notSet[label] | range[label] | ^( DOT ID terminal[label] ) | ^( DOT ID ruleref[label] ) | ^( WILDCARD . ) | WILDCARD | terminal[label] | ruleref[label] );";
        }
    }
    static final String DFA16_eotS =
        "\14\uffff";
    static final String DFA16_eofS =
        "\14\uffff";
    static final String DFA16_minS =
        "\1\60\2\2\4\uffff\1\4\1\uffff\1\2\2\uffff";
    static final String DFA16_maxS =
        "\1\104\2\141\4\uffff\1\146\1\uffff\1\146\2\uffff";
    static final String DFA16_acceptS =
        "\3\uffff\1\6\1\7\1\1\1\2\1\uffff\1\5\1\uffff\1\4\1\3";
    static final String DFA16_specialS =
        "\14\uffff}>";
    static final String[] DFA16_transitionS = {
            "\1\4\4\uffff\1\3\11\uffff\1\2\4\uffff\1\1",
            "\1\5\2\6\13\uffff\1\6\32\uffff\1\6\2\uffff\1\6\1\uffff\1\6"+
            "\2\uffff\1\6\1\uffff\1\6\1\uffff\2\6\2\uffff\1\6\1\uffff\1\6"+
            "\1\uffff\2\6\3\uffff\1\6\10\uffff\1\6\1\uffff\3\6\14\uffff\1"+
            "\6\2\uffff\1\6",
            "\1\7\2\10\13\uffff\1\10\32\uffff\1\10\2\uffff\1\10\1\uffff"+
            "\1\10\2\uffff\1\10\1\uffff\1\10\1\uffff\2\10\2\uffff\1\10\1"+
            "\uffff\1\10\1\uffff\2\10\3\uffff\1\10\10\uffff\1\10\1\uffff"+
            "\3\10\14\uffff\1\10\2\uffff\1\10",
            "",
            "",
            "",
            "",
            "\12\12\1\11\130\12",
            "",
            "\2\12\143\13",
            "",
            ""
    };

    static final short[] DFA16_eot = DFA.unpackEncodedString(DFA16_eotS);
    static final short[] DFA16_eof = DFA.unpackEncodedString(DFA16_eofS);
    static final char[] DFA16_min = DFA.unpackEncodedStringToUnsignedChars(DFA16_minS);
    static final char[] DFA16_max = DFA.unpackEncodedStringToUnsignedChars(DFA16_maxS);
    static final short[] DFA16_accept = DFA.unpackEncodedString(DFA16_acceptS);
    static final short[] DFA16_special = DFA.unpackEncodedString(DFA16_specialS);
    static final short[][] DFA16_transition;

    static {
        int numStates = DFA16_transitionS.length;
        DFA16_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA16_transition[i] = DFA.unpackEncodedString(DFA16_transitionS[i]);
        }
    }

    class DFA16 extends DFA {

        public DFA16(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 16;
            this.eot = DFA16_eot;
            this.eof = DFA16_eof;
            this.min = DFA16_min;
            this.max = DFA16_max;
            this.accept = DFA16_accept;
            this.special = DFA16_special;
            this.transition = DFA16_transition;
        }
        public String getDescription() {
            return "113:1: terminal[GrammarAST label] returns [MatchToken omo] : ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( ROOT terminal[label] ) | ^( BANG terminal[label] ) );";
        }
    }
 

    public static final BitSet FOLLOW_BLOCK_in_block72 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OPTIONS_in_block76 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_alternative_in_block96 = new BitSet(new long[]{0x0000000000000008L,0x0000004000200000L});
    public static final BitSet FOLLOW_ALT_REWRITE_in_alternative142 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_alternative_in_alternative146 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_ALT_in_alternative159 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_EPSILON_in_alternative161 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_in_alternative176 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_alternative180 = new BitSet(new long[]{0xA9A9480000010018L,0x000000024003A011L});
    public static final BitSet FOLLOW_labeledElement_in_element207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_element218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnf_in_element231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_element252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMPRED_in_element265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GATED_SEMPRED_in_element275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_treeSpec_in_element281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement303 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement305 = new BitSet(new long[]{0xA1A1000000000000L,0x0000000200000011L});
    public static final BitSet FOLLOW_atom_in_labeledElement307 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement321 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement323 = new BitSet(new long[]{0x0021080000000000L,0x000000000003A000L});
    public static final BitSet FOLLOW_block_in_labeledElement325 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement336 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement338 = new BitSet(new long[]{0xA1A1000000000000L,0x0000000200000011L});
    public static final BitSet FOLLOW_atom_in_labeledElement340 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement352 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement354 = new BitSet(new long[]{0x0021080000000000L,0x000000000003A000L});
    public static final BitSet FOLLOW_block_in_labeledElement356 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_treeSpec380 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_treeSpec386 = new BitSet(new long[]{0xA9A9480000010018L,0x000000024003A011L});
    public static final BitSet FOLLOW_astBlockSuffix_in_ebnf410 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf412 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_OPTIONAL_in_ebnf422 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf424 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CLOSURE_in_ebnf436 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf438 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_POSITIVE_CLOSURE_in_ebnf451 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf453 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_ebnf477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_astBlockSuffix0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom539 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom541 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom552 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom554 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_atom566 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom568 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom579 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom581 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_notSet_in_atom592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_atom603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_atom616 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom618 = new BitSet(new long[]{0x8021000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_terminal_in_atom620 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOT_in_atom628 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom630 = new BitSet(new long[]{0xA1A1000000000000L,0x0000000200000011L});
    public static final BitSet FOLLOW_ruleref_in_atom632 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WILDCARD_in_atom643 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_WILDCARD_in_atom658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_terminal_in_atom672 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_atom688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notSet717 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_notSet719 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_in_notSet732 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_notSet734 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_ruleref762 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref765 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref767 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_ruleref779 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref782 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref784 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref798 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref800 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RANGE_in_range829 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range833 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range837 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal867 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal896 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_terminal898 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal912 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal928 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_terminal943 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal945 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_terminal959 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal961 = new BitSet(new long[]{0x0000000000000008L});

}