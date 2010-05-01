// $ANTLR ${project.version} ${buildNumber} NFABytecodeTriggers.g 2010-05-01 11:23:08

package org.antlr.v4.codegen;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeRuleReturnScope;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.GrammarASTWithOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NFABytecodeTriggers extends NFABytecodeGenerator {
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


        public NFABytecodeTriggers(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public NFABytecodeTriggers(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return NFABytecodeTriggers.tokenNames; }
    public String getGrammarFileName() { return "NFABytecodeTriggers.g"; }


    public static class block_return extends TreeRuleReturnScope {
    };

    // $ANTLR start "block"
    // NFABytecodeTriggers.g:28:1: block : ^( BLOCK ( ^( OPTIONS ( . )+ ) )? ( alternative )+ ) ;
    public final NFABytecodeTriggers.block_return block() throws RecognitionException {
        NFABytecodeTriggers.block_return retval = new NFABytecodeTriggers.block_return();
        retval.start = input.LT(1);

        try {
            // NFABytecodeTriggers.g:29:5: ( ^( BLOCK ( ^( OPTIONS ( . )+ ) )? ( alternative )+ ) )
            // NFABytecodeTriggers.g:29:7: ^( BLOCK ( ^( OPTIONS ( . )+ ) )? ( alternative )+ )
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_block70); 

            match(input, Token.DOWN, null); 
            // NFABytecodeTriggers.g:29:16: ( ^( OPTIONS ( . )+ ) )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==OPTIONS) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // NFABytecodeTriggers.g:29:17: ^( OPTIONS ( . )+ )
                    {
                    match(input,OPTIONS,FOLLOW_OPTIONS_in_block74); 

                    match(input, Token.DOWN, null); 
                    // NFABytecodeTriggers.g:29:27: ( . )+
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
                    	    // NFABytecodeTriggers.g:29:27: .
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


                		GrammarAST firstAlt = (GrammarAST)input.LT(1);
                		int i = firstAlt.getChildIndex();    		
            			int nAlts = ((GrammarAST)retval.start).getChildCount() - i;
                		System.out.println("alts "+nAlts);
                		List<JumpInstr> jumps = new ArrayList<JumpInstr>();
                		SplitInstr S = null;
                		if ( nAlts>1 ) {
            	    		S = new SplitInstr(nAlts);
            	    		emit(S);
            	    		S.addrs.add(ip);
                		}
                		int alt = 1;
                		
            // NFABytecodeTriggers.g:44:7: ( alternative )+
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
            	    // NFABytecodeTriggers.g:44:9: alternative
            	    {
            	    pushFollow(FOLLOW_alternative_in_block98);
            	    alternative();

            	    state._fsp--;


            	        			if ( alt < nAlts ) {
            	    	    			JumpInstr J = new JumpInstr();
            	    	    			jumps.add(J);
            	    	    			emit(J);
            	    	    			S.addrs.add(ip);
            	        			}
            	        			alt++;
            	        			

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


                		int END = ip;
                		for (JumpInstr J : jumps) J.target = END;
                		

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "block"


    // $ANTLR start "alternative"
    // NFABytecodeTriggers.g:62:1: alternative : ( ^( ALT_REWRITE a= alternative . ) | ^( ALT EPSILON ) | ^( ALT (e= element )+ ) );
    public final void alternative() throws RecognitionException {
        try {
            // NFABytecodeTriggers.g:63:5: ( ^( ALT_REWRITE a= alternative . ) | ^( ALT EPSILON ) | ^( ALT (e= element )+ ) )
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
                    // NFABytecodeTriggers.g:63:7: ^( ALT_REWRITE a= alternative . )
                    {
                    match(input,ALT_REWRITE,FOLLOW_ALT_REWRITE_in_alternative149); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_alternative_in_alternative153);
                    alternative();

                    state._fsp--;

                    matchAny(input); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // NFABytecodeTriggers.g:64:7: ^( ALT EPSILON )
                    {
                    match(input,ALT,FOLLOW_ALT_in_alternative166); 

                    match(input, Token.DOWN, null); 
                    match(input,EPSILON,FOLLOW_EPSILON_in_alternative168); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // NFABytecodeTriggers.g:65:9: ^( ALT (e= element )+ )
                    {
                    match(input,ALT,FOLLOW_ALT_in_alternative185); 

                    match(input, Token.DOWN, null); 
                    // NFABytecodeTriggers.g:65:15: (e= element )+
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
                    	    // NFABytecodeTriggers.g:65:16: e= element
                    	    {
                    	    pushFollow(FOLLOW_element_in_alternative190);
                    	    element();

                    	    state._fsp--;


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
        return ;
    }
    // $ANTLR end "alternative"


    // $ANTLR start "element"
    // NFABytecodeTriggers.g:68:1: element : ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );
    public final void element() throws RecognitionException {
        try {
            // NFABytecodeTriggers.g:69:2: ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec )
            int alt6=7;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // NFABytecodeTriggers.g:69:4: labeledElement
                    {
                    pushFollow(FOLLOW_labeledElement_in_element221);
                    labeledElement();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // NFABytecodeTriggers.g:70:4: atom
                    {
                    pushFollow(FOLLOW_atom_in_element230);
                    atom();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // NFABytecodeTriggers.g:71:4: ebnf
                    {
                    pushFollow(FOLLOW_ebnf_in_element241);
                    ebnf();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // NFABytecodeTriggers.g:72:6: ACTION
                    {
                    match(input,ACTION,FOLLOW_ACTION_in_element254); 

                    }
                    break;
                case 5 :
                    // NFABytecodeTriggers.g:73:6: SEMPRED
                    {
                    match(input,SEMPRED,FOLLOW_SEMPRED_in_element267); 

                    }
                    break;
                case 6 :
                    // NFABytecodeTriggers.g:74:4: GATED_SEMPRED
                    {
                    match(input,GATED_SEMPRED,FOLLOW_GATED_SEMPRED_in_element278); 

                    }
                    break;
                case 7 :
                    // NFABytecodeTriggers.g:75:4: treeSpec
                    {
                    pushFollow(FOLLOW_treeSpec_in_element287);
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
        return ;
    }
    // $ANTLR end "element"


    // $ANTLR start "labeledElement"
    // NFABytecodeTriggers.g:78:1: labeledElement : ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) );
    public final void labeledElement() throws RecognitionException {
        try {
            // NFABytecodeTriggers.g:79:2: ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) )
            int alt7=4;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // NFABytecodeTriggers.g:79:4: ^( ASSIGN ID atom )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement305); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement307); 
                    pushFollow(FOLLOW_atom_in_labeledElement309);
                    atom();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // NFABytecodeTriggers.g:80:4: ^( ASSIGN ID block )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement319); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement321); 
                    pushFollow(FOLLOW_block_in_labeledElement323);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // NFABytecodeTriggers.g:81:4: ^( PLUS_ASSIGN ID atom )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement333); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement335); 
                    pushFollow(FOLLOW_atom_in_labeledElement337);
                    atom();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // NFABytecodeTriggers.g:82:4: ^( PLUS_ASSIGN ID block )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement346); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement348); 
                    pushFollow(FOLLOW_block_in_labeledElement350);
                    block();

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
        return ;
    }
    // $ANTLR end "labeledElement"


    // $ANTLR start "treeSpec"
    // NFABytecodeTriggers.g:85:1: treeSpec : ^( TREE_BEGIN (e= element )+ ) ;
    public final void treeSpec() throws RecognitionException {
        try {
            // NFABytecodeTriggers.g:86:5: ( ^( TREE_BEGIN (e= element )+ ) )
            // NFABytecodeTriggers.g:86:7: ^( TREE_BEGIN (e= element )+ )
            {
            match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_treeSpec368); 

            match(input, Token.DOWN, null); 
            // NFABytecodeTriggers.g:86:21: (e= element )+
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
            	    // NFABytecodeTriggers.g:86:22: e= element
            	    {
            	    pushFollow(FOLLOW_element_in_treeSpec374);
            	    element();

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
        return ;
    }
    // $ANTLR end "treeSpec"

    public static class ebnf_return extends TreeRuleReturnScope {
    };

    // $ANTLR start "ebnf"
    // NFABytecodeTriggers.g:89:1: ebnf : ( ^( astBlockSuffix block ) | ^( OPTIONAL block ) | ^( CLOSURE block ) | ^( POSITIVE_CLOSURE block ) | block );
    public final NFABytecodeTriggers.ebnf_return ebnf() throws RecognitionException {
        NFABytecodeTriggers.ebnf_return retval = new NFABytecodeTriggers.ebnf_return();
        retval.start = input.LT(1);


        	GrammarASTWithOptions blk = (GrammarASTWithOptions)((GrammarAST)retval.start).getChild(0);
        	String greedyOption = blk.getOption("greedy");

        try {
            // NFABytecodeTriggers.g:94:2: ( ^( astBlockSuffix block ) | ^( OPTIONAL block ) | ^( CLOSURE block ) | ^( POSITIVE_CLOSURE block ) | block )
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
                    // NFABytecodeTriggers.g:94:4: ^( astBlockSuffix block )
                    {
                    pushFollow(FOLLOW_astBlockSuffix_in_ebnf399);
                    astBlockSuffix();

                    state._fsp--;


                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf401);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // NFABytecodeTriggers.g:95:4: ^( OPTIONAL block )
                    {

                    	   	SplitInstr S = new SplitInstr(2);
                    		emit(S);
                       		S.addrs.add(ip);
                    		
                    match(input,OPTIONAL,FOLLOW_OPTIONAL_in_ebnf414); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf416);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

                       		S.addrs.add(ip);
                    		

                    }
                    break;
                case 3 :
                    // NFABytecodeTriggers.g:104:4: ^( CLOSURE block )
                    {

                    		int start=ip;
                    	   	SplitInstr S = new SplitInstr(2);
                    		emit(S);
                    		int blkStart = ip;
                    		
                    match(input,CLOSURE,FOLLOW_CLOSURE_in_ebnf434); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf436);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    	    JumpInstr J = new JumpInstr();
                    	    emit(J);
                    	    J.target = start;
                       		S.addrs.add(blkStart);
                    	    S.addrs.add(ip);
                    	    if ( greedyOption!=null && greedyOption.equals("false") ) Collections.reverse(S.addrs);
                    		

                    }
                    break;
                case 4 :
                    // NFABytecodeTriggers.g:119:4: ^( POSITIVE_CLOSURE block )
                    {
                    int start=ip;
                    match(input,POSITIVE_CLOSURE,FOLLOW_POSITIVE_CLOSURE_in_ebnf452); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf454);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

                       		SplitInstr S = new SplitInstr(2);
                    		emit(S);
                    		int stop = ip;
                       		S.addrs.add(start);
                       		S.addrs.add(stop);
                    	    if ( greedyOption!=null && greedyOption.equals("false") ) Collections.reverse(S.addrs);
                    		

                    }
                    break;
                case 5 :
                    // NFABytecodeTriggers.g:128:5: block
                    {
                    pushFollow(FOLLOW_block_in_ebnf465);
                    block();

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
        return retval;
    }
    // $ANTLR end "ebnf"


    // $ANTLR start "astBlockSuffix"
    // NFABytecodeTriggers.g:131:1: astBlockSuffix : ( ROOT | IMPLIES | BANG );
    public final void astBlockSuffix() throws RecognitionException {
        try {
            // NFABytecodeTriggers.g:132:5: ( ROOT | IMPLIES | BANG )
            // NFABytecodeTriggers.g:
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
    // NFABytecodeTriggers.g:137:1: atom : ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | notSet | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | ^( WILDCARD . ) | WILDCARD | terminal | ruleref );
    public final void atom() throws RecognitionException {
        GrammarAST WILDCARD1=null;
        GrammarAST WILDCARD2=null;

        try {
            // NFABytecodeTriggers.g:138:2: ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | notSet | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | ^( WILDCARD . ) | WILDCARD | terminal | ruleref )
            int alt10=12;
            alt10 = dfa10.predict(input);
            switch (alt10) {
                case 1 :
                    // NFABytecodeTriggers.g:138:4: ^( ROOT range )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom519); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom521);
                    range();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // NFABytecodeTriggers.g:139:4: ^( BANG range )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom531); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom533);
                    range();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // NFABytecodeTriggers.g:140:4: ^( ROOT notSet )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom543); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom545);
                    notSet();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // NFABytecodeTriggers.g:141:4: ^( BANG notSet )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom555); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom557);
                    notSet();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // NFABytecodeTriggers.g:142:4: notSet
                    {
                    pushFollow(FOLLOW_notSet_in_atom566);
                    notSet();

                    state._fsp--;


                    }
                    break;
                case 6 :
                    // NFABytecodeTriggers.g:143:4: range
                    {
                    pushFollow(FOLLOW_range_in_atom576);
                    range();

                    state._fsp--;


                    }
                    break;
                case 7 :
                    // NFABytecodeTriggers.g:144:4: ^( DOT ID terminal )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom587); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom589); 
                    pushFollow(FOLLOW_terminal_in_atom591);
                    terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // NFABytecodeTriggers.g:145:4: ^( DOT ID ruleref )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom600); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom602); 
                    pushFollow(FOLLOW_ruleref_in_atom604);
                    ruleref();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 9 :
                    // NFABytecodeTriggers.g:146:7: ^( WILDCARD . )
                    {
                    WILDCARD1=(GrammarAST)match(input,WILDCARD,FOLLOW_WILDCARD_in_atom616); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    emit(new WildcardInstr(WILDCARD1.token));

                    }
                    break;
                case 10 :
                    // NFABytecodeTriggers.g:147:7: WILDCARD
                    {
                    WILDCARD2=(GrammarAST)match(input,WILDCARD,FOLLOW_WILDCARD_in_atom632); 
                    emit(new WildcardInstr(WILDCARD2.token));

                    }
                    break;
                case 11 :
                    // NFABytecodeTriggers.g:148:9: terminal
                    {
                    pushFollow(FOLLOW_terminal_in_atom647);
                    terminal();

                    state._fsp--;


                    }
                    break;
                case 12 :
                    // NFABytecodeTriggers.g:149:9: ruleref
                    {
                    pushFollow(FOLLOW_ruleref_in_atom661);
                    ruleref();

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
        return ;
    }
    // $ANTLR end "atom"


    // $ANTLR start "notSet"
    // NFABytecodeTriggers.g:152:1: notSet : ( ^( NOT terminal ) | ^( NOT block ) );
    public final void notSet() throws RecognitionException {
        try {
            // NFABytecodeTriggers.g:153:5: ( ^( NOT terminal ) | ^( NOT block ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==NOT) ) {
                int LA11_1 = input.LA(2);

                if ( (LA11_1==DOWN) ) {
                    int LA11_2 = input.LA(3);

                    if ( (LA11_2==BANG||LA11_2==ROOT||LA11_2==TOKEN_REF||LA11_2==STRING_LITERAL) ) {
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
                    // NFABytecodeTriggers.g:153:7: ^( NOT terminal )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet684); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_notSet686);
                    terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // NFABytecodeTriggers.g:154:7: ^( NOT block )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet698); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_notSet700);
                    block();

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
        return ;
    }
    // $ANTLR end "notSet"


    // $ANTLR start "ruleref"
    // NFABytecodeTriggers.g:157:1: ruleref : ( ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) ) | ^( BANG ^( RULE_REF ( ARG_ACTION )? ) ) | ^( RULE_REF ( ARG_ACTION )? ) );
    public final void ruleref() throws RecognitionException {
        try {
            // NFABytecodeTriggers.g:158:5: ( ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) ) | ^( BANG ^( RULE_REF ( ARG_ACTION )? ) ) | ^( RULE_REF ( ARG_ACTION )? ) )
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
                    // NFABytecodeTriggers.g:158:7: ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_ruleref722); 

                    match(input, Token.DOWN, null); 
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref725); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // NFABytecodeTriggers.g:158:25: ( ARG_ACTION )?
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==ARG_ACTION) ) {
                            alt12=1;
                        }
                        switch (alt12) {
                            case 1 :
                                // NFABytecodeTriggers.g:158:25: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref727); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // NFABytecodeTriggers.g:159:7: ^( BANG ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,BANG,FOLLOW_BANG_in_ruleref740); 

                    match(input, Token.DOWN, null); 
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref743); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // NFABytecodeTriggers.g:159:25: ( ARG_ACTION )?
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==ARG_ACTION) ) {
                            alt13=1;
                        }
                        switch (alt13) {
                            case 1 :
                                // NFABytecodeTriggers.g:159:25: ARG_ACTION
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
                case 3 :
                    // NFABytecodeTriggers.g:160:7: ^( RULE_REF ( ARG_ACTION )? )
                    {
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref758); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // NFABytecodeTriggers.g:160:18: ( ARG_ACTION )?
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==ARG_ACTION) ) {
                            alt14=1;
                        }
                        switch (alt14) {
                            case 1 :
                                // NFABytecodeTriggers.g:160:18: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref760); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

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
        return ;
    }
    // $ANTLR end "ruleref"


    // $ANTLR start "range"
    // NFABytecodeTriggers.g:163:1: range : ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) ;
    public final void range() throws RecognitionException {
        GrammarAST a=null;
        GrammarAST b=null;

        try {
            // NFABytecodeTriggers.g:164:5: ( ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) )
            // NFABytecodeTriggers.g:164:7: ^( RANGE a= STRING_LITERAL b= STRING_LITERAL )
            {
            match(input,RANGE,FOLLOW_RANGE_in_range783); 

            match(input, Token.DOWN, null); 
            a=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range787); 
            b=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range791); 

            match(input, Token.UP, null); 
            emit(new RangeInstr(a.token, b.token));

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
    // $ANTLR end "range"


    // $ANTLR start "terminal"
    // NFABytecodeTriggers.g:168:1: terminal : ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( ROOT terminal ) | ^( BANG terminal ) );
    public final void terminal() throws RecognitionException {
        GrammarAST STRING_LITERAL3=null;
        GrammarAST STRING_LITERAL4=null;
        GrammarAST TOKEN_REF5=null;
        GrammarAST TOKEN_REF6=null;

        try {
            // NFABytecodeTriggers.g:169:5: ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( ROOT terminal ) | ^( BANG terminal ) )
            int alt16=7;
            alt16 = dfa16.predict(input);
            switch (alt16) {
                case 1 :
                    // NFABytecodeTriggers.g:169:8: ^( STRING_LITERAL . )
                    {
                    STRING_LITERAL3=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal818); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    emitString(STRING_LITERAL3.token);

                    }
                    break;
                case 2 :
                    // NFABytecodeTriggers.g:170:7: STRING_LITERAL
                    {
                    STRING_LITERAL4=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal833); 
                    emitString(STRING_LITERAL4.token);

                    }
                    break;
                case 3 :
                    // NFABytecodeTriggers.g:171:7: ^( TOKEN_REF ARG_ACTION . )
                    {
                    TOKEN_REF5=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal847); 

                    match(input, Token.DOWN, null); 
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal849); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    emit(new CallInstr(TOKEN_REF5.token));

                    }
                    break;
                case 4 :
                    // NFABytecodeTriggers.g:172:7: ^( TOKEN_REF . )
                    {
                    TOKEN_REF6=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal863); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    emit(new CallInstr(TOKEN_REF6.token));

                    }
                    break;
                case 5 :
                    // NFABytecodeTriggers.g:173:7: TOKEN_REF
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal879); 

                    }
                    break;
                case 6 :
                    // NFABytecodeTriggers.g:174:7: ^( ROOT terminal )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_terminal893); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_terminal895);
                    terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // NFABytecodeTriggers.g:175:7: ^( BANG terminal )
                    {
                    match(input,BANG,FOLLOW_BANG_in_terminal908); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_terminal910);
                    terminal();

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
        return ;
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
            return "68:1: element : ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );";
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
            return "78:1: labeledElement : ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) );";
        }
    }
    static final String DFA10_eotS =
        "\31\uffff";
    static final String DFA10_eofS =
        "\31\uffff";
    static final String DFA10_minS =
        "\1\60\2\2\2\uffff\2\2\2\uffff\2\60\1\127\6\uffff\1\60\2\2\2\uffff"+
        "\2\60";
    static final String DFA10_maxS =
        "\1\141\2\2\2\uffff\1\2\1\141\2\uffff\2\104\1\127\6\uffff\1\104\2"+
        "\2\2\uffff\2\104";
    static final String DFA10_acceptS =
        "\3\uffff\1\5\1\6\2\uffff\1\13\1\14\3\uffff\1\11\1\12\1\1\1\3\1\4"+
        "\1\2\3\uffff\1\10\1\7\2\uffff";
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
            "\1\7\4\uffff\1\7\2\uffff\1\21\4\uffff\1\20\1\uffff\1\7\1\10"+
            "\3\uffff\1\7",
            "\1\22",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\24\4\uffff\1\23\11\uffff\1\26\1\25\3\uffff\1\26",
            "\1\27",
            "\1\30",
            "",
            "",
            "\1\26\4\uffff\1\26\11\uffff\1\26\1\25\3\uffff\1\26",
            "\1\26\4\uffff\1\26\11\uffff\1\26\1\25\3\uffff\1\26"
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
            return "137:1: atom : ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | notSet | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | ^( WILDCARD . ) | WILDCARD | terminal | ruleref );";
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
            return "168:1: terminal : ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( ROOT terminal ) | ^( BANG terminal ) );";
        }
    }
 

    public static final BitSet FOLLOW_BLOCK_in_block70 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OPTIONS_in_block74 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_alternative_in_block98 = new BitSet(new long[]{0x0000000000000008L,0x0000004000200000L});
    public static final BitSet FOLLOW_ALT_REWRITE_in_alternative149 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_alternative_in_alternative153 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_ALT_in_alternative166 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_EPSILON_in_alternative168 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_in_alternative185 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_alternative190 = new BitSet(new long[]{0xA9A9480000010018L,0x000000024003A011L});
    public static final BitSet FOLLOW_labeledElement_in_element221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_element230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnf_in_element241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_element254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMPRED_in_element267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GATED_SEMPRED_in_element278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_treeSpec_in_element287 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement305 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement307 = new BitSet(new long[]{0xA1A1000000000000L,0x0000000200000011L});
    public static final BitSet FOLLOW_atom_in_labeledElement309 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement319 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement321 = new BitSet(new long[]{0x0021080000000000L,0x000000000003A000L});
    public static final BitSet FOLLOW_block_in_labeledElement323 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement333 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement335 = new BitSet(new long[]{0xA1A1000000000000L,0x0000000200000011L});
    public static final BitSet FOLLOW_atom_in_labeledElement337 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement346 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement348 = new BitSet(new long[]{0x0021080000000000L,0x000000000003A000L});
    public static final BitSet FOLLOW_block_in_labeledElement350 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_treeSpec368 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_treeSpec374 = new BitSet(new long[]{0xA9A9480000010018L,0x000000024003A011L});
    public static final BitSet FOLLOW_astBlockSuffix_in_ebnf399 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf401 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_OPTIONAL_in_ebnf414 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf416 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CLOSURE_in_ebnf434 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf436 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_POSITIVE_CLOSURE_in_ebnf452 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf454 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_ebnf465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_astBlockSuffix0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom519 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom521 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom531 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom533 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_atom543 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom545 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom555 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom557 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_notSet_in_atom566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_atom576 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_atom587 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom589 = new BitSet(new long[]{0x8021000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_terminal_in_atom591 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOT_in_atom600 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom602 = new BitSet(new long[]{0xA1A1000000000000L,0x0000000200000011L});
    public static final BitSet FOLLOW_ruleref_in_atom604 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WILDCARD_in_atom616 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_WILDCARD_in_atom632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_terminal_in_atom647 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_atom661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notSet684 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_notSet686 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_in_notSet698 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_notSet700 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_ruleref722 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref725 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref727 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_ruleref740 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref743 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref745 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref758 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref760 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RANGE_in_range783 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range787 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range791 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal818 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal833 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal847 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_terminal849 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal863 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal879 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_terminal893 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal895 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_terminal908 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal910 = new BitSet(new long[]{0x0000000000000008L});

}