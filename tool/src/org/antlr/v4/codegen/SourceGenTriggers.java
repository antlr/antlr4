// $ANTLR 3.4 SourceGenTriggers.g 2011-06-20 18:47:20

package org.antlr.v4.codegen;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.codegen.model.*;
import org.antlr.v4.tool.*;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class SourceGenTriggers extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ACTION", "ACTION_CHAR_LITERAL", "ACTION_ESC", "ACTION_STRING_LITERAL", "ARG_ACTION", "ASSIGN", "AT", "BANG", "CATCH", "COLON", "COLONCOLON", "COMMA", "COMMENT", "DOC_COMMENT", "DOLLAR", "DOT", "DOUBLE_ANGLE_STRING_LITERAL", "DOUBLE_QUOTE_STRING_LITERAL", "ERRCHAR", "ESC_SEQ", "ETC", "FINALLY", "FORCED_ACTION", "FRAGMENT", "GRAMMAR", "GT", "HEX_DIGIT", "IMPLIES", "IMPORT", "INT", "LEXER", "LPAREN", "LT", "MODE", "NESTED_ACTION", "NLCHARS", "NOT", "OPTIONS", "OR", "PARSER", "PLUS", "PLUS_ASSIGN", "PRIVATE", "PROTECTED", "PUBLIC", "QUESTION", "RANGE", "RARROW", "RBRACE", "RETURNS", "ROOT", "RPAREN", "RULE_REF", "SCOPE", "SEMI", "SEMPRED", "SRC", "STAR", "STRING_LITERAL", "TEMPLATE", "THROWS", "TOKENS", "TOKEN_REF", "TREE", "TREE_BEGIN", "UNICODE_ESC", "WS", "WSCHARS", "WSNLCHARS", "ALT", "ALTLIST", "ALT_REWRITE", "ARG", "ARGLIST", "BACKTRACK_SEMPRED", "BLOCK", "CHAR_RANGE", "CLOSURE", "COMBINED", "ELEMENT_OPTIONS", "EPSILON", "GATED_SEMPRED", "ID", "INITACTION", "LABEL", "LIST", "OPTIONAL", "POSITIVE_CLOSURE", "RESULT", "RET", "REWRITE_BLOCK", "RULE", "RULEACTIONS", "RULEMODIFIERS", "RULES", "ST_RESULT", "SYNPRED", "SYN_SEMPRED", "WILDCARD"
    };

    public static final int EOF=-1;
    public static final int ACTION=4;
    public static final int ACTION_CHAR_LITERAL=5;
    public static final int ACTION_ESC=6;
    public static final int ACTION_STRING_LITERAL=7;
    public static final int ARG_ACTION=8;
    public static final int ASSIGN=9;
    public static final int AT=10;
    public static final int BANG=11;
    public static final int CATCH=12;
    public static final int COLON=13;
    public static final int COLONCOLON=14;
    public static final int COMMA=15;
    public static final int COMMENT=16;
    public static final int DOC_COMMENT=17;
    public static final int DOLLAR=18;
    public static final int DOT=19;
    public static final int DOUBLE_ANGLE_STRING_LITERAL=20;
    public static final int DOUBLE_QUOTE_STRING_LITERAL=21;
    public static final int ERRCHAR=22;
    public static final int ESC_SEQ=23;
    public static final int ETC=24;
    public static final int FINALLY=25;
    public static final int FORCED_ACTION=26;
    public static final int FRAGMENT=27;
    public static final int GRAMMAR=28;
    public static final int GT=29;
    public static final int HEX_DIGIT=30;
    public static final int IMPLIES=31;
    public static final int IMPORT=32;
    public static final int INT=33;
    public static final int LEXER=34;
    public static final int LPAREN=35;
    public static final int LT=36;
    public static final int MODE=37;
    public static final int NESTED_ACTION=38;
    public static final int NLCHARS=39;
    public static final int NOT=40;
    public static final int OPTIONS=41;
    public static final int OR=42;
    public static final int PARSER=43;
    public static final int PLUS=44;
    public static final int PLUS_ASSIGN=45;
    public static final int PRIVATE=46;
    public static final int PROTECTED=47;
    public static final int PUBLIC=48;
    public static final int QUESTION=49;
    public static final int RANGE=50;
    public static final int RARROW=51;
    public static final int RBRACE=52;
    public static final int RETURNS=53;
    public static final int ROOT=54;
    public static final int RPAREN=55;
    public static final int RULE_REF=56;
    public static final int SCOPE=57;
    public static final int SEMI=58;
    public static final int SEMPRED=59;
    public static final int SRC=60;
    public static final int STAR=61;
    public static final int STRING_LITERAL=62;
    public static final int TEMPLATE=63;
    public static final int THROWS=64;
    public static final int TOKENS=65;
    public static final int TOKEN_REF=66;
    public static final int TREE=67;
    public static final int TREE_BEGIN=68;
    public static final int UNICODE_ESC=69;
    public static final int WS=70;
    public static final int WSCHARS=71;
    public static final int WSNLCHARS=72;
    public static final int ALT=73;
    public static final int ALTLIST=74;
    public static final int ALT_REWRITE=75;
    public static final int ARG=76;
    public static final int ARGLIST=77;
    public static final int BACKTRACK_SEMPRED=78;
    public static final int BLOCK=79;
    public static final int CHAR_RANGE=80;
    public static final int CLOSURE=81;
    public static final int COMBINED=82;
    public static final int ELEMENT_OPTIONS=83;
    public static final int EPSILON=84;
    public static final int GATED_SEMPRED=85;
    public static final int ID=86;
    public static final int INITACTION=87;
    public static final int LABEL=88;
    public static final int LIST=89;
    public static final int OPTIONAL=90;
    public static final int POSITIVE_CLOSURE=91;
    public static final int RESULT=92;
    public static final int RET=93;
    public static final int REWRITE_BLOCK=94;
    public static final int RULE=95;
    public static final int RULEACTIONS=96;
    public static final int RULEMODIFIERS=97;
    public static final int RULES=98;
    public static final int ST_RESULT=99;
    public static final int SYNPRED=100;
    public static final int SYN_SEMPRED=101;
    public static final int WILDCARD=102;

    // delegates
    public TreeParser[] getDelegates() {
        return new TreeParser[] {};
    }

    // delegators


    public SourceGenTriggers(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }
    public SourceGenTriggers(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return SourceGenTriggers.tokenNames; }
    public String getGrammarFileName() { return "SourceGenTriggers.g"; }


    // TODO: identical grammar to ATNBytecodeTriggers; would be nice to combine
    	public OutputModelFactory factory;
        public SourceGenTriggers(TreeNodeStream input, OutputModelFactory factory) {
        	this(input);
        	this.factory = factory;
        }



    // $ANTLR start "dummy"
    // SourceGenTriggers.g:27:1: dummy : block[null, null] ;
    public final void dummy() throws RecognitionException {
        try {
            // SourceGenTriggers.g:27:7: ( block[null, null] )
            // SourceGenTriggers.g:27:9: block[null, null]
            {
            pushFollow(FOLLOW_block_in_dummy58);
            block(null, null);

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "dummy"



    // $ANTLR start "block"
    // SourceGenTriggers.g:29:1: block[GrammarAST label, GrammarAST ebnfRoot] returns [SrcOp omo] : ^(blk= BLOCK ( ^( OPTIONS ( . )+ ) )? ( alternative )+ ) ;
    public final SrcOp block(GrammarAST label, GrammarAST ebnfRoot) throws RecognitionException {
        SrcOp omo = null;


        GrammarAST blk=null;
        SourceGenTriggers.alternative_return alternative1 =null;


        try {
            // SourceGenTriggers.g:30:5: ( ^(blk= BLOCK ( ^( OPTIONS ( . )+ ) )? ( alternative )+ ) )
            // SourceGenTriggers.g:30:7: ^(blk= BLOCK ( ^( OPTIONS ( . )+ ) )? ( alternative )+ )
            {
            blk=(GrammarAST)match(input,BLOCK,FOLLOW_BLOCK_in_block81); 

            match(input, Token.DOWN, null); 
            // SourceGenTriggers.g:30:20: ( ^( OPTIONS ( . )+ ) )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==OPTIONS) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // SourceGenTriggers.g:30:21: ^( OPTIONS ( . )+ )
                    {
                    match(input,OPTIONS,FOLLOW_OPTIONS_in_block85); 

                    match(input, Token.DOWN, null); 
                    // SourceGenTriggers.g:30:31: ( . )+
                    int cnt1=0;
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0 >= ACTION && LA1_0 <= WILDCARD)) ) {
                            alt1=1;
                        }
                        else if ( (LA1_0==UP) ) {
                            alt1=2;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // SourceGenTriggers.g:30:31: .
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

            // SourceGenTriggers.g:32:7: ( alternative )+
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
            	    // SourceGenTriggers.g:32:9: alternative
            	    {
            	    pushFollow(FOLLOW_alternative_in_block105);
            	    alternative1=alternative();

            	    state._fsp--;


            	    alts.add((alternative1!=null?alternative1.omo:null));

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
                	if ( ebnfRoot==null ) {
                	    omo = factory.getChoiceBlock((BlockAST)blk, alts);
                	}
                	else {
                	    omo = factory.getEBNFBlock(ebnfRoot, alts);
                	}
                	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return omo;
    }
    // $ANTLR end "block"


    public static class alternative_return extends TreeRuleReturnScope {
        public CodeBlock omo;
    };


    // $ANTLR start "alternative"
    // SourceGenTriggers.g:45:1: alternative returns [CodeBlock omo] : ( ^( ALT_REWRITE a= alternative . ) | ^( ALT EPSILON ) | ^( ALT ( element )+ ) );
    public final SourceGenTriggers.alternative_return alternative() throws RecognitionException {
        SourceGenTriggers.alternative_return retval = new SourceGenTriggers.alternative_return();
        retval.start = input.LT(1);


        SourceGenTriggers.alternative_return a =null;

        List<SrcOp> element2 =null;



        	List<SrcOp> elems = new ArrayList<SrcOp>();
        	if ( ((AltAST)((GrammarAST)retval.start)).alt!=null ) factory.currentAlt = ((AltAST)((GrammarAST)retval.start)).alt;


        try {
            // SourceGenTriggers.g:51:5: ( ^( ALT_REWRITE a= alternative . ) | ^( ALT EPSILON ) | ^( ALT ( element )+ ) )
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
                    else if ( (LA5_3==ACTION||LA5_3==ASSIGN||LA5_3==BANG||LA5_3==DOT||LA5_3==FORCED_ACTION||LA5_3==IMPLIES||LA5_3==NOT||LA5_3==PLUS_ASSIGN||LA5_3==RANGE||LA5_3==ROOT||LA5_3==RULE_REF||LA5_3==SEMPRED||LA5_3==STRING_LITERAL||LA5_3==TOKEN_REF||LA5_3==TREE_BEGIN||LA5_3==BLOCK||LA5_3==CLOSURE||LA5_3==GATED_SEMPRED||(LA5_3 >= OPTIONAL && LA5_3 <= POSITIVE_CLOSURE)||LA5_3==WILDCARD) ) {
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
                    // SourceGenTriggers.g:51:7: ^( ALT_REWRITE a= alternative . )
                    {
                    match(input,ALT_REWRITE,FOLLOW_ALT_REWRITE_in_alternative151); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_alternative_in_alternative155);
                    a=alternative();

                    state._fsp--;


                    matchAny(input); 

                    match(input, Token.UP, null); 


                    }
                    break;
                case 2 :
                    // SourceGenTriggers.g:52:7: ^( ALT EPSILON )
                    {
                    match(input,ALT,FOLLOW_ALT_in_alternative167); 

                    match(input, Token.DOWN, null); 
                    match(input,EPSILON,FOLLOW_EPSILON_in_alternative169); 

                    match(input, Token.UP, null); 


                    retval.omo = factory.epsilon();

                    }
                    break;
                case 3 :
                    // SourceGenTriggers.g:53:9: ^( ALT ( element )+ )
                    {
                    match(input,ALT,FOLLOW_ALT_in_alternative184); 

                    match(input, Token.DOWN, null); 
                    // SourceGenTriggers.g:53:16: ( element )+
                    int cnt4=0;
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0==ACTION||LA4_0==ASSIGN||LA4_0==BANG||LA4_0==DOT||LA4_0==FORCED_ACTION||LA4_0==IMPLIES||LA4_0==NOT||LA4_0==PLUS_ASSIGN||LA4_0==RANGE||LA4_0==ROOT||LA4_0==RULE_REF||LA4_0==SEMPRED||LA4_0==STRING_LITERAL||LA4_0==TOKEN_REF||LA4_0==TREE_BEGIN||LA4_0==BLOCK||LA4_0==CLOSURE||LA4_0==GATED_SEMPRED||(LA4_0 >= OPTIONAL && LA4_0 <= POSITIVE_CLOSURE)||LA4_0==WILDCARD) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // SourceGenTriggers.g:53:18: element
                    	    {
                    	    pushFollow(FOLLOW_element_in_alternative188);
                    	    element2=element();

                    	    state._fsp--;


                    	    elems.addAll(element2);

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


                    retval.omo = factory.alternative(elems);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "alternative"



    // $ANTLR start "element"
    // SourceGenTriggers.g:56:1: element returns [List<SrcOp> omos] : ( labeledElement | atom[null] | ebnf | ACTION | FORCED_ACTION | SEMPRED | GATED_SEMPRED | treeSpec );
    public final List<SrcOp> element() throws RecognitionException {
        List<SrcOp> omos = null;


        GrammarAST ACTION6=null;
        GrammarAST FORCED_ACTION7=null;
        GrammarAST SEMPRED8=null;
        List<SrcOp> labeledElement3 =null;

        List<SrcOp> atom4 =null;

        SrcOp ebnf5 =null;


        try {
            // SourceGenTriggers.g:57:2: ( labeledElement | atom[null] | ebnf | ACTION | FORCED_ACTION | SEMPRED | GATED_SEMPRED | treeSpec )
            int alt6=8;
            switch ( input.LA(1) ) {
            case ASSIGN:
            case PLUS_ASSIGN:
                {
                alt6=1;
                }
                break;
            case ROOT:
                {
                int LA6_2 = input.LA(2);

                if ( (LA6_2==DOWN) ) {
                    int LA6_11 = input.LA(3);

                    if ( (LA6_11==BANG||LA6_11==NOT||LA6_11==RANGE||LA6_11==ROOT||LA6_11==RULE_REF||LA6_11==STRING_LITERAL||LA6_11==TOKEN_REF) ) {
                        alt6=2;
                    }
                    else if ( (LA6_11==BLOCK) ) {
                        alt6=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 6, 11, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 2, input);

                    throw nvae;

                }
                }
                break;
            case BANG:
                {
                int LA6_3 = input.LA(2);

                if ( (LA6_3==DOWN) ) {
                    int LA6_12 = input.LA(3);

                    if ( (LA6_12==BANG||LA6_12==NOT||LA6_12==RANGE||LA6_12==ROOT||LA6_12==RULE_REF||LA6_12==STRING_LITERAL||LA6_12==TOKEN_REF) ) {
                        alt6=2;
                    }
                    else if ( (LA6_12==BLOCK) ) {
                        alt6=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 6, 12, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 3, input);

                    throw nvae;

                }
                }
                break;
            case DOT:
            case NOT:
            case RANGE:
            case RULE_REF:
            case STRING_LITERAL:
            case TOKEN_REF:
            case WILDCARD:
                {
                alt6=2;
                }
                break;
            case IMPLIES:
            case BLOCK:
            case CLOSURE:
            case OPTIONAL:
            case POSITIVE_CLOSURE:
                {
                alt6=3;
                }
                break;
            case ACTION:
                {
                alt6=4;
                }
                break;
            case FORCED_ACTION:
                {
                alt6=5;
                }
                break;
            case SEMPRED:
                {
                alt6=6;
                }
                break;
            case GATED_SEMPRED:
                {
                alt6=7;
                }
                break;
            case TREE_BEGIN:
                {
                alt6=8;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;

            }

            switch (alt6) {
                case 1 :
                    // SourceGenTriggers.g:57:4: labeledElement
                    {
                    pushFollow(FOLLOW_labeledElement_in_element215);
                    labeledElement3=labeledElement();

                    state._fsp--;


                    omos = labeledElement3;

                    }
                    break;
                case 2 :
                    // SourceGenTriggers.g:58:4: atom[null]
                    {
                    pushFollow(FOLLOW_atom_in_element226);
                    atom4=atom(null);

                    state._fsp--;


                    omos = atom4;

                    }
                    break;
                case 3 :
                    // SourceGenTriggers.g:59:4: ebnf
                    {
                    pushFollow(FOLLOW_ebnf_in_element239);
                    ebnf5=ebnf();

                    state._fsp--;


                    omos = Utils.list(ebnf5);

                    }
                    break;
                case 4 :
                    // SourceGenTriggers.g:60:6: ACTION
                    {
                    ACTION6=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_element254); 

                    omos = Utils.list(factory.action(ACTION6));

                    }
                    break;
                case 5 :
                    // SourceGenTriggers.g:61:6: FORCED_ACTION
                    {
                    FORCED_ACTION7=(GrammarAST)match(input,FORCED_ACTION,FOLLOW_FORCED_ACTION_in_element269); 

                    omos = Utils.list(factory.forcedAction(FORCED_ACTION7));

                    }
                    break;
                case 6 :
                    // SourceGenTriggers.g:62:6: SEMPRED
                    {
                    SEMPRED8=(GrammarAST)match(input,SEMPRED,FOLLOW_SEMPRED_in_element282); 

                    omos = Utils.list(factory.sempred(SEMPRED8));

                    }
                    break;
                case 7 :
                    // SourceGenTriggers.g:63:4: GATED_SEMPRED
                    {
                    match(input,GATED_SEMPRED,FOLLOW_GATED_SEMPRED_in_element295); 

                    }
                    break;
                case 8 :
                    // SourceGenTriggers.g:64:4: treeSpec
                    {
                    pushFollow(FOLLOW_treeSpec_in_element300);
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
        	// do for sure before leaving
        }
        return omos;
    }
    // $ANTLR end "element"



    // $ANTLR start "labeledElement"
    // SourceGenTriggers.g:67:1: labeledElement returns [List<SrcOp> omos] : ( ^( ASSIGN ID atom[$ID] ) | ^( ASSIGN ID block[$ID,null] ) | ^( PLUS_ASSIGN ID atom[$ID] ) | ^( PLUS_ASSIGN ID block[$ID,null] ) );
    public final List<SrcOp> labeledElement() throws RecognitionException {
        List<SrcOp> omos = null;


        GrammarAST ID9=null;
        GrammarAST ID11=null;
        GrammarAST ID13=null;
        GrammarAST ID15=null;
        List<SrcOp> atom10 =null;

        SrcOp block12 =null;

        List<SrcOp> atom14 =null;

        SrcOp block16 =null;


        try {
            // SourceGenTriggers.g:68:2: ( ^( ASSIGN ID atom[$ID] ) | ^( ASSIGN ID block[$ID,null] ) | ^( PLUS_ASSIGN ID atom[$ID] ) | ^( PLUS_ASSIGN ID block[$ID,null] ) )
            int alt7=4;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==ASSIGN) ) {
                int LA7_1 = input.LA(2);

                if ( (LA7_1==DOWN) ) {
                    int LA7_3 = input.LA(3);

                    if ( (LA7_3==ID) ) {
                        int LA7_5 = input.LA(4);

                        if ( (LA7_5==BANG||LA7_5==DOT||LA7_5==NOT||LA7_5==RANGE||LA7_5==ROOT||LA7_5==RULE_REF||LA7_5==STRING_LITERAL||LA7_5==TOKEN_REF||LA7_5==WILDCARD) ) {
                            alt7=1;
                        }
                        else if ( (LA7_5==BLOCK) ) {
                            alt7=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 7, 5, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 7, 3, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA7_0==PLUS_ASSIGN) ) {
                int LA7_2 = input.LA(2);

                if ( (LA7_2==DOWN) ) {
                    int LA7_4 = input.LA(3);

                    if ( (LA7_4==ID) ) {
                        int LA7_6 = input.LA(4);

                        if ( (LA7_6==BANG||LA7_6==DOT||LA7_6==NOT||LA7_6==RANGE||LA7_6==ROOT||LA7_6==RULE_REF||LA7_6==STRING_LITERAL||LA7_6==TOKEN_REF||LA7_6==WILDCARD) ) {
                            alt7=3;
                        }
                        else if ( (LA7_6==BLOCK) ) {
                            alt7=4;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 7, 6, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 7, 4, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 2, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }
            switch (alt7) {
                case 1 :
                    // SourceGenTriggers.g:68:4: ^( ASSIGN ID atom[$ID] )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement316); 

                    match(input, Token.DOWN, null); 
                    ID9=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement318); 

                    pushFollow(FOLLOW_atom_in_labeledElement320);
                    atom10=atom(ID9);

                    state._fsp--;


                    match(input, Token.UP, null); 


                    omos = atom10;

                    }
                    break;
                case 2 :
                    // SourceGenTriggers.g:69:4: ^( ASSIGN ID block[$ID,null] )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement334); 

                    match(input, Token.DOWN, null); 
                    ID11=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement336); 

                    pushFollow(FOLLOW_block_in_labeledElement338);
                    block12=block(ID11, null);

                    state._fsp--;


                    match(input, Token.UP, null); 


                    omos = Utils.list(block12);

                    }
                    break;
                case 3 :
                    // SourceGenTriggers.g:70:4: ^( PLUS_ASSIGN ID atom[$ID] )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement349); 

                    match(input, Token.DOWN, null); 
                    ID13=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement351); 

                    pushFollow(FOLLOW_atom_in_labeledElement353);
                    atom14=atom(ID13);

                    state._fsp--;


                    match(input, Token.UP, null); 


                    omos = atom14;

                    }
                    break;
                case 4 :
                    // SourceGenTriggers.g:71:4: ^( PLUS_ASSIGN ID block[$ID,null] )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement365); 

                    match(input, Token.DOWN, null); 
                    ID15=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement367); 

                    pushFollow(FOLLOW_block_in_labeledElement369);
                    block16=block(ID15, null);

                    state._fsp--;


                    match(input, Token.UP, null); 


                    omos = Utils.list(block16);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return omos;
    }
    // $ANTLR end "labeledElement"



    // $ANTLR start "treeSpec"
    // SourceGenTriggers.g:74:1: treeSpec returns [SrcOp omo] : ^( TREE_BEGIN (e= element )+ ) ;
    public final SrcOp treeSpec() throws RecognitionException {
        SrcOp omo = null;


        List<SrcOp> e =null;


        try {
            // SourceGenTriggers.g:75:5: ( ^( TREE_BEGIN (e= element )+ ) )
            // SourceGenTriggers.g:75:7: ^( TREE_BEGIN (e= element )+ )
            {
            match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_treeSpec392); 

            match(input, Token.DOWN, null); 
            // SourceGenTriggers.g:75:21: (e= element )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==ACTION||LA8_0==ASSIGN||LA8_0==BANG||LA8_0==DOT||LA8_0==FORCED_ACTION||LA8_0==IMPLIES||LA8_0==NOT||LA8_0==PLUS_ASSIGN||LA8_0==RANGE||LA8_0==ROOT||LA8_0==RULE_REF||LA8_0==SEMPRED||LA8_0==STRING_LITERAL||LA8_0==TOKEN_REF||LA8_0==TREE_BEGIN||LA8_0==BLOCK||LA8_0==CLOSURE||LA8_0==GATED_SEMPRED||(LA8_0 >= OPTIONAL && LA8_0 <= POSITIVE_CLOSURE)||LA8_0==WILDCARD) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // SourceGenTriggers.g:75:22: e= element
            	    {
            	    pushFollow(FOLLOW_element_in_treeSpec398);
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
        	// do for sure before leaving
        }
        return omo;
    }
    // $ANTLR end "treeSpec"



    // $ANTLR start "ebnf"
    // SourceGenTriggers.g:78:1: ebnf returns [SrcOp omo] : ( ^( astBlockSuffix block[null,null] ) | ^( OPTIONAL block[null,$OPTIONAL] ) | ^( CLOSURE block[null,$CLOSURE] ) | ^( POSITIVE_CLOSURE block[null,$POSITIVE_CLOSURE] ) | block[null, null] );
    public final SrcOp ebnf() throws RecognitionException {
        SrcOp omo = null;


        GrammarAST OPTIONAL17=null;
        GrammarAST CLOSURE19=null;
        GrammarAST POSITIVE_CLOSURE21=null;
        SrcOp block18 =null;

        SrcOp block20 =null;

        SrcOp block22 =null;

        SrcOp block23 =null;


        try {
            // SourceGenTriggers.g:79:2: ( ^( astBlockSuffix block[null,null] ) | ^( OPTIONAL block[null,$OPTIONAL] ) | ^( CLOSURE block[null,$CLOSURE] ) | ^( POSITIVE_CLOSURE block[null,$POSITIVE_CLOSURE] ) | block[null, null] )
            int alt9=5;
            switch ( input.LA(1) ) {
            case BANG:
            case IMPLIES:
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
                    // SourceGenTriggers.g:79:4: ^( astBlockSuffix block[null,null] )
                    {
                    pushFollow(FOLLOW_astBlockSuffix_in_ebnf421);
                    astBlockSuffix();

                    state._fsp--;


                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf423);
                    block(null, null);

                    state._fsp--;


                    match(input, Token.UP, null); 


                    }
                    break;
                case 2 :
                    // SourceGenTriggers.g:80:4: ^( OPTIONAL block[null,$OPTIONAL] )
                    {
                    OPTIONAL17=(GrammarAST)match(input,OPTIONAL,FOLLOW_OPTIONAL_in_ebnf431); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf433);
                    block18=block(null, OPTIONAL17);

                    state._fsp--;


                    match(input, Token.UP, null); 


                    omo = block18;

                    }
                    break;
                case 3 :
                    // SourceGenTriggers.g:81:4: ^( CLOSURE block[null,$CLOSURE] )
                    {
                    CLOSURE19=(GrammarAST)match(input,CLOSURE,FOLLOW_CLOSURE_in_ebnf443); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf445);
                    block20=block(null, CLOSURE19);

                    state._fsp--;


                    match(input, Token.UP, null); 


                    omo = block20;

                    }
                    break;
                case 4 :
                    // SourceGenTriggers.g:82:4: ^( POSITIVE_CLOSURE block[null,$POSITIVE_CLOSURE] )
                    {
                    POSITIVE_CLOSURE21=(GrammarAST)match(input,POSITIVE_CLOSURE,FOLLOW_POSITIVE_CLOSURE_in_ebnf456); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf458);
                    block22=block(null, POSITIVE_CLOSURE21);

                    state._fsp--;


                    match(input, Token.UP, null); 


                    omo = block22;

                    }
                    break;
                case 5 :
                    // SourceGenTriggers.g:84:5: block[null, null]
                    {
                    pushFollow(FOLLOW_block_in_ebnf482);
                    block23=block(null, null);

                    state._fsp--;


                    omo = block23;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return omo;
    }
    // $ANTLR end "ebnf"



    // $ANTLR start "astBlockSuffix"
    // SourceGenTriggers.g:87:1: astBlockSuffix : ( ROOT | IMPLIES | BANG );
    public final void astBlockSuffix() throws RecognitionException {
        try {
            // SourceGenTriggers.g:88:5: ( ROOT | IMPLIES | BANG )
            // SourceGenTriggers.g:
            {
            if ( input.LA(1)==BANG||input.LA(1)==IMPLIES||input.LA(1)==ROOT ) {
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
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "astBlockSuffix"



    // $ANTLR start "atom"
    // SourceGenTriggers.g:95:1: atom[GrammarAST label] returns [List<SrcOp> omos] : ( ^( ROOT range[label] ) | ^( BANG range[label] ) | ^( ROOT notSet[label] ) | ^( BANG notSet[label] ) | notSet[label] | range[label] | ^( DOT ID terminal[label] ) | ^( DOT ID ruleref[label] ) | ^( WILDCARD . ) | WILDCARD | terminal[label] | ruleref[label] );
    public final List<SrcOp> atom(GrammarAST label) throws RecognitionException {
        List<SrcOp> omos = null;


        List<SrcOp> range24 =null;

        List<SrcOp> notSet25 =null;

        List<SrcOp> range26 =null;

        List<SrcOp> terminal27 =null;

        List<SrcOp> ruleref28 =null;


        try {
            // SourceGenTriggers.g:96:2: ( ^( ROOT range[label] ) | ^( BANG range[label] ) | ^( ROOT notSet[label] ) | ^( BANG notSet[label] ) | notSet[label] | range[label] | ^( DOT ID terminal[label] ) | ^( DOT ID ruleref[label] ) | ^( WILDCARD . ) | WILDCARD | terminal[label] | ruleref[label] )
            int alt10=12;
            switch ( input.LA(1) ) {
            case ROOT:
                {
                int LA10_1 = input.LA(2);

                if ( (LA10_1==DOWN) ) {
                    switch ( input.LA(3) ) {
                    case RULE_REF:
                        {
                        alt10=12;
                        }
                        break;
                    case RANGE:
                        {
                        alt10=1;
                        }
                        break;
                    case NOT:
                        {
                        alt10=3;
                        }
                        break;
                    case BANG:
                    case ROOT:
                    case STRING_LITERAL:
                    case TOKEN_REF:
                        {
                        alt10=11;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 9, input);

                        throw nvae;

                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 1, input);

                    throw nvae;

                }
                }
                break;
            case BANG:
                {
                int LA10_2 = input.LA(2);

                if ( (LA10_2==DOWN) ) {
                    switch ( input.LA(3) ) {
                    case RULE_REF:
                        {
                        alt10=12;
                        }
                        break;
                    case RANGE:
                        {
                        alt10=2;
                        }
                        break;
                    case NOT:
                        {
                        alt10=4;
                        }
                        break;
                    case BANG:
                    case ROOT:
                    case STRING_LITERAL:
                    case TOKEN_REF:
                        {
                        alt10=11;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 10, input);

                        throw nvae;

                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 2, input);

                    throw nvae;

                }
                }
                break;
            case NOT:
                {
                alt10=5;
                }
                break;
            case RANGE:
                {
                alt10=6;
                }
                break;
            case DOT:
                {
                int LA10_5 = input.LA(2);

                if ( (LA10_5==DOWN) ) {
                    int LA10_11 = input.LA(3);

                    if ( (LA10_11==ID) ) {
                        switch ( input.LA(4) ) {
                        case STRING_LITERAL:
                        case TOKEN_REF:
                            {
                            alt10=7;
                            }
                            break;
                        case ROOT:
                            {
                            int LA10_20 = input.LA(5);

                            if ( (LA10_20==DOWN) ) {
                                int LA10_23 = input.LA(6);

                                if ( (LA10_23==RULE_REF) ) {
                                    alt10=8;
                                }
                                else if ( (LA10_23==BANG||LA10_23==ROOT||LA10_23==STRING_LITERAL||LA10_23==TOKEN_REF) ) {
                                    alt10=7;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 10, 23, input);

                                    throw nvae;

                                }
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 10, 20, input);

                                throw nvae;

                            }
                            }
                            break;
                        case BANG:
                            {
                            int LA10_21 = input.LA(5);

                            if ( (LA10_21==DOWN) ) {
                                int LA10_24 = input.LA(6);

                                if ( (LA10_24==RULE_REF) ) {
                                    alt10=8;
                                }
                                else if ( (LA10_24==BANG||LA10_24==ROOT||LA10_24==STRING_LITERAL||LA10_24==TOKEN_REF) ) {
                                    alt10=7;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 10, 24, input);

                                    throw nvae;

                                }
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 10, 21, input);

                                throw nvae;

                            }
                            }
                            break;
                        case RULE_REF:
                            {
                            alt10=8;
                            }
                            break;
                        default:
                            NoViableAltException nvae =
                                new NoViableAltException("", 10, 18, input);

                            throw nvae;

                        }

                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 11, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 5, input);

                    throw nvae;

                }
                }
                break;
            case WILDCARD:
                {
                int LA10_6 = input.LA(2);

                if ( (LA10_6==DOWN) ) {
                    alt10=9;
                }
                else if ( ((LA10_6 >= UP && LA10_6 <= ACTION)||LA10_6==ASSIGN||LA10_6==BANG||LA10_6==DOT||LA10_6==FORCED_ACTION||LA10_6==IMPLIES||LA10_6==NOT||LA10_6==PLUS_ASSIGN||LA10_6==RANGE||LA10_6==ROOT||LA10_6==RULE_REF||LA10_6==SEMPRED||LA10_6==STRING_LITERAL||LA10_6==TOKEN_REF||LA10_6==TREE_BEGIN||LA10_6==BLOCK||LA10_6==CLOSURE||LA10_6==GATED_SEMPRED||(LA10_6 >= OPTIONAL && LA10_6 <= POSITIVE_CLOSURE)||LA10_6==WILDCARD) ) {
                    alt10=10;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 6, input);

                    throw nvae;

                }
                }
                break;
            case STRING_LITERAL:
            case TOKEN_REF:
                {
                alt10=11;
                }
                break;
            case RULE_REF:
                {
                alt10=12;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }

            switch (alt10) {
                case 1 :
                    // SourceGenTriggers.g:96:4: ^( ROOT range[label] )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom544); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom546);
                    range(label);

                    state._fsp--;


                    match(input, Token.UP, null); 


                    }
                    break;
                case 2 :
                    // SourceGenTriggers.g:97:4: ^( BANG range[label] )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom554); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom556);
                    range24=range(label);

                    state._fsp--;


                    match(input, Token.UP, null); 


                    omos = range24;

                    }
                    break;
                case 3 :
                    // SourceGenTriggers.g:98:4: ^( ROOT notSet[label] )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom567); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom569);
                    notSet(label);

                    state._fsp--;


                    match(input, Token.UP, null); 


                    }
                    break;
                case 4 :
                    // SourceGenTriggers.g:99:4: ^( BANG notSet[label] )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom577); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom579);
                    notSet25=notSet(label);

                    state._fsp--;


                    match(input, Token.UP, null); 


                    omos = notSet25;

                    }
                    break;
                case 5 :
                    // SourceGenTriggers.g:100:4: notSet[label]
                    {
                    pushFollow(FOLLOW_notSet_in_atom589);
                    notSet(label);

                    state._fsp--;


                    }
                    break;
                case 6 :
                    // SourceGenTriggers.g:101:4: range[label]
                    {
                    pushFollow(FOLLOW_range_in_atom595);
                    range26=range(label);

                    state._fsp--;


                    omos = range26;

                    }
                    break;
                case 7 :
                    // SourceGenTriggers.g:102:4: ^( DOT ID terminal[label] )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom607); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom609); 

                    pushFollow(FOLLOW_terminal_in_atom611);
                    terminal(label);

                    state._fsp--;


                    match(input, Token.UP, null); 


                    }
                    break;
                case 8 :
                    // SourceGenTriggers.g:103:4: ^( DOT ID ruleref[label] )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom619); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom621); 

                    pushFollow(FOLLOW_ruleref_in_atom623);
                    ruleref(label);

                    state._fsp--;


                    match(input, Token.UP, null); 


                    }
                    break;
                case 9 :
                    // SourceGenTriggers.g:104:7: ^( WILDCARD . )
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_atom634); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 


                    }
                    break;
                case 10 :
                    // SourceGenTriggers.g:105:7: WILDCARD
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_atom645); 

                    }
                    break;
                case 11 :
                    // SourceGenTriggers.g:106:9: terminal[label]
                    {
                    pushFollow(FOLLOW_terminal_in_atom655);
                    terminal27=terminal(label);

                    state._fsp--;


                    omos = terminal27;

                    }
                    break;
                case 12 :
                    // SourceGenTriggers.g:107:9: ruleref[label]
                    {
                    pushFollow(FOLLOW_ruleref_in_atom671);
                    ruleref28=ruleref(label);

                    state._fsp--;


                    omos = ruleref28;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return omos;
    }
    // $ANTLR end "atom"



    // $ANTLR start "notSet"
    // SourceGenTriggers.g:110:1: notSet[GrammarAST label] returns [List<SrcOp> omos] : ( ^( NOT terminal[label] ) | ^( NOT block[label,null] ) );
    public final List<SrcOp> notSet(GrammarAST label) throws RecognitionException {
        List<SrcOp> omos = null;


        try {
            // SourceGenTriggers.g:111:5: ( ^( NOT terminal[label] ) | ^( NOT block[label,null] ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==NOT) ) {
                int LA11_1 = input.LA(2);

                if ( (LA11_1==DOWN) ) {
                    int LA11_2 = input.LA(3);

                    if ( (LA11_2==BANG||LA11_2==ROOT||LA11_2==STRING_LITERAL||LA11_2==TOKEN_REF) ) {
                        alt11=1;
                    }
                    else if ( (LA11_2==BLOCK) ) {
                        alt11=2;
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
                    // SourceGenTriggers.g:111:7: ^( NOT terminal[label] )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet700); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_notSet702);
                    terminal(label);

                    state._fsp--;


                    match(input, Token.UP, null); 


                    }
                    break;
                case 2 :
                    // SourceGenTriggers.g:112:7: ^( NOT block[label,null] )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet713); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_notSet715);
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
        	// do for sure before leaving
        }
        return omos;
    }
    // $ANTLR end "notSet"



    // $ANTLR start "ruleref"
    // SourceGenTriggers.g:115:1: ruleref[GrammarAST label] returns [List<SrcOp> omos] : ( ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) ) | ^( BANG ^( RULE_REF ( ARG_ACTION )? ) ) | ^( RULE_REF ( ARG_ACTION )? ) );
    public final List<SrcOp> ruleref(GrammarAST label) throws RecognitionException {
        List<SrcOp> omos = null;


        GrammarAST RULE_REF29=null;
        GrammarAST ARG_ACTION30=null;
        GrammarAST RULE_REF31=null;
        GrammarAST ARG_ACTION32=null;

        try {
            // SourceGenTriggers.g:116:5: ( ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) ) | ^( BANG ^( RULE_REF ( ARG_ACTION )? ) ) | ^( RULE_REF ( ARG_ACTION )? ) )
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
                    // SourceGenTriggers.g:116:7: ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_ruleref740); 

                    match(input, Token.DOWN, null); 
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref743); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // SourceGenTriggers.g:116:25: ( ARG_ACTION )?
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==ARG_ACTION) ) {
                            alt12=1;
                        }
                        switch (alt12) {
                            case 1 :
                                // SourceGenTriggers.g:116:25: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref745); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }


                    match(input, Token.UP, null); 


                    }
                    break;
                case 2 :
                    // SourceGenTriggers.g:117:7: ^( BANG ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,BANG,FOLLOW_BANG_in_ruleref757); 

                    match(input, Token.DOWN, null); 
                    RULE_REF29=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref760); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // SourceGenTriggers.g:117:25: ( ARG_ACTION )?
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==ARG_ACTION) ) {
                            alt13=1;
                        }
                        switch (alt13) {
                            case 1 :
                                // SourceGenTriggers.g:117:25: ARG_ACTION
                                {
                                ARG_ACTION30=(GrammarAST)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref762); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }


                    match(input, Token.UP, null); 


                    omos = factory.ruleRef(RULE_REF29, label, ARG_ACTION30);

                    }
                    break;
                case 3 :
                    // SourceGenTriggers.g:118:7: ^( RULE_REF ( ARG_ACTION )? )
                    {
                    RULE_REF31=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref776); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // SourceGenTriggers.g:118:18: ( ARG_ACTION )?
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==ARG_ACTION) ) {
                            alt14=1;
                        }
                        switch (alt14) {
                            case 1 :
                                // SourceGenTriggers.g:118:18: ARG_ACTION
                                {
                                ARG_ACTION32=(GrammarAST)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref778); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }


                    omos = factory.ruleRef(RULE_REF31, label, ARG_ACTION32);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return omos;
    }
    // $ANTLR end "ruleref"



    // $ANTLR start "range"
    // SourceGenTriggers.g:121:1: range[GrammarAST label] returns [List<SrcOp> omos] : ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) ;
    public final List<SrcOp> range(GrammarAST label) throws RecognitionException {
        List<SrcOp> omos = null;


        GrammarAST a=null;
        GrammarAST b=null;

        try {
            // SourceGenTriggers.g:122:5: ( ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) )
            // SourceGenTriggers.g:122:7: ^( RANGE a= STRING_LITERAL b= STRING_LITERAL )
            {
            match(input,RANGE,FOLLOW_RANGE_in_range807); 

            match(input, Token.DOWN, null); 
            a=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range811); 

            b=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range815); 

            match(input, Token.UP, null); 


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return omos;
    }
    // $ANTLR end "range"



    // $ANTLR start "terminal"
    // SourceGenTriggers.g:125:1: terminal[GrammarAST label] returns [List<SrcOp> omos] : ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( ROOT terminal[label] ) | ^( BANG terminal[label] ) );
    public final List<SrcOp> terminal(GrammarAST label) throws RecognitionException {
        List<SrcOp> omos = null;


        GrammarAST STRING_LITERAL33=null;
        GrammarAST STRING_LITERAL34=null;
        GrammarAST TOKEN_REF35=null;
        GrammarAST ARG_ACTION36=null;
        GrammarAST TOKEN_REF37=null;
        GrammarAST TOKEN_REF38=null;

        try {
            // SourceGenTriggers.g:126:5: ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( ROOT terminal[label] ) | ^( BANG terminal[label] ) )
            int alt16=7;
            switch ( input.LA(1) ) {
            case STRING_LITERAL:
                {
                int LA16_1 = input.LA(2);

                if ( (LA16_1==DOWN) ) {
                    alt16=1;
                }
                else if ( ((LA16_1 >= UP && LA16_1 <= ACTION)||LA16_1==ASSIGN||LA16_1==BANG||LA16_1==DOT||LA16_1==FORCED_ACTION||LA16_1==IMPLIES||LA16_1==NOT||LA16_1==PLUS_ASSIGN||LA16_1==RANGE||LA16_1==ROOT||LA16_1==RULE_REF||LA16_1==SEMPRED||LA16_1==STRING_LITERAL||LA16_1==TOKEN_REF||LA16_1==TREE_BEGIN||LA16_1==BLOCK||LA16_1==CLOSURE||LA16_1==GATED_SEMPRED||(LA16_1 >= OPTIONAL && LA16_1 <= POSITIVE_CLOSURE)||LA16_1==WILDCARD) ) {
                    alt16=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 1, input);

                    throw nvae;

                }
                }
                break;
            case TOKEN_REF:
                {
                int LA16_2 = input.LA(2);

                if ( (LA16_2==DOWN) ) {
                    int LA16_7 = input.LA(3);

                    if ( (LA16_7==ARG_ACTION) ) {
                        int LA16_9 = input.LA(4);

                        if ( ((LA16_9 >= ACTION && LA16_9 <= WILDCARD)) ) {
                            alt16=3;
                        }
                        else if ( ((LA16_9 >= DOWN && LA16_9 <= UP)) ) {
                            alt16=4;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 16, 9, input);

                            throw nvae;

                        }
                    }
                    else if ( ((LA16_7 >= ACTION && LA16_7 <= ACTION_STRING_LITERAL)||(LA16_7 >= ASSIGN && LA16_7 <= WILDCARD)) ) {
                        alt16=4;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 16, 7, input);

                        throw nvae;

                    }
                }
                else if ( ((LA16_2 >= UP && LA16_2 <= ACTION)||LA16_2==ASSIGN||LA16_2==BANG||LA16_2==DOT||LA16_2==FORCED_ACTION||LA16_2==IMPLIES||LA16_2==NOT||LA16_2==PLUS_ASSIGN||LA16_2==RANGE||LA16_2==ROOT||LA16_2==RULE_REF||LA16_2==SEMPRED||LA16_2==STRING_LITERAL||LA16_2==TOKEN_REF||LA16_2==TREE_BEGIN||LA16_2==BLOCK||LA16_2==CLOSURE||LA16_2==GATED_SEMPRED||(LA16_2 >= OPTIONAL && LA16_2 <= POSITIVE_CLOSURE)||LA16_2==WILDCARD) ) {
                    alt16=5;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 2, input);

                    throw nvae;

                }
                }
                break;
            case ROOT:
                {
                alt16=6;
                }
                break;
            case BANG:
                {
                alt16=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;

            }

            switch (alt16) {
                case 1 :
                    // SourceGenTriggers.g:126:8: ^( STRING_LITERAL . )
                    {
                    STRING_LITERAL33=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal840); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 


                    omos = factory.stringRef(STRING_LITERAL33, label);

                    }
                    break;
                case 2 :
                    // SourceGenTriggers.g:127:7: STRING_LITERAL
                    {
                    STRING_LITERAL34=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal855); 

                    omos = factory.stringRef(STRING_LITERAL34, label);

                    }
                    break;
                case 3 :
                    // SourceGenTriggers.g:128:7: ^( TOKEN_REF ARG_ACTION . )
                    {
                    TOKEN_REF35=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal869); 

                    match(input, Token.DOWN, null); 
                    ARG_ACTION36=(GrammarAST)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal871); 

                    matchAny(input); 

                    match(input, Token.UP, null); 


                    omos = factory.tokenRef(TOKEN_REF35, label, ARG_ACTION36);

                    }
                    break;
                case 4 :
                    // SourceGenTriggers.g:129:7: ^( TOKEN_REF . )
                    {
                    TOKEN_REF37=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal885); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 


                    omos = factory.tokenRef(TOKEN_REF37, label, null);

                    }
                    break;
                case 5 :
                    // SourceGenTriggers.g:130:7: TOKEN_REF
                    {
                    TOKEN_REF38=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal901); 

                    omos = factory.tokenRef(TOKEN_REF38, label, null);

                    }
                    break;
                case 6 :
                    // SourceGenTriggers.g:131:7: ^( ROOT terminal[label] )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_terminal916); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_terminal918);
                    terminal(label);

                    state._fsp--;


                    match(input, Token.UP, null); 


                    }
                    break;
                case 7 :
                    // SourceGenTriggers.g:132:7: ^( BANG terminal[label] )
                    {
                    match(input,BANG,FOLLOW_BANG_in_terminal929); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_terminal931);
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
        	// do for sure before leaving
        }
        return omos;
    }
    // $ANTLR end "terminal"

    // Delegated rules


 

    public static final BitSet FOLLOW_block_in_dummy58 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BLOCK_in_block81 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OPTIONS_in_block85 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_alternative_in_block105 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000A00L});
    public static final BitSet FOLLOW_ALT_REWRITE_in_alternative151 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_alternative_in_alternative155 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_ALT_in_alternative167 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_EPSILON_in_alternative169 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_in_alternative184 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_alternative188 = new BitSet(new long[]{0x4944210084080A18L,0x000000400C228014L});
    public static final BitSet FOLLOW_labeledElement_in_element215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_element226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnf_in_element239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_element254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORCED_ACTION_in_element269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMPRED_in_element282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GATED_SEMPRED_in_element295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_treeSpec_in_element300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement316 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement318 = new BitSet(new long[]{0x4144010000080800L,0x0000004000000004L});
    public static final BitSet FOLLOW_atom_in_labeledElement320 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement334 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement336 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_block_in_labeledElement338 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement349 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement351 = new BitSet(new long[]{0x4144010000080800L,0x0000004000000004L});
    public static final BitSet FOLLOW_atom_in_labeledElement353 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement365 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement367 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_block_in_labeledElement369 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_treeSpec392 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_treeSpec398 = new BitSet(new long[]{0x4944210084080A18L,0x000000400C228014L});
    public static final BitSet FOLLOW_astBlockSuffix_in_ebnf421 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf423 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_OPTIONAL_in_ebnf431 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf433 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CLOSURE_in_ebnf443 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf445 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_POSITIVE_CLOSURE_in_ebnf456 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf458 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_ebnf482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom544 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom546 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom554 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom556 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_atom567 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom569 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom577 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom579 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_notSet_in_atom589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_atom595 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_atom607 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom609 = new BitSet(new long[]{0x4040000000000800L,0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_atom611 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOT_in_atom619 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom621 = new BitSet(new long[]{0x0140000000000800L});
    public static final BitSet FOLLOW_ruleref_in_atom623 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WILDCARD_in_atom634 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_WILDCARD_in_atom645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_terminal_in_atom655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_atom671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notSet700 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_notSet702 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_in_notSet713 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_notSet715 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_ruleref740 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref743 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref745 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_ruleref757 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref760 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref762 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref776 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref778 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RANGE_in_range807 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range811 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range815 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal840 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal869 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_terminal871 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal885 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal901 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_terminal916 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal918 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_terminal929 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal931 = new BitSet(new long[]{0x0000000000000008L});

}