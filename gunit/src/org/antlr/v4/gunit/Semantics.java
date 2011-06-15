// $ANTLR 3.2.1-SNAPSHOT Jan 26, 2010 15:12:28 Semantics.g 2010-01-27 17:03:31

package org.antlr.v4.gunit;
import java.util.Map;
import java.util.HashMap;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class Semantics extends TreeFilter {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SUITE", "TEST_OK", "TEST_FAIL", "TEST_RETVAL", "TEST_STDOUT", "TEST_TREE", "TEST_ACTION", "DOC_COMMENT", "ID", "OPTIONS", "STRING", "ACTION", "RETVAL", "ML_STRING", "TREE", "FILENAME", "NESTED_RETVAL", "NESTED_AST", "STRING_", "WS", "ID_", "SL_COMMENT", "ML_COMMENT", "XDIGIT", "'gunit'", "';'", "'}'", "'='", "'@header'", "'walks'", "':'", "'OK'", "'FAIL'", "'returns'", "'->'"
    };
    public static final int T__29=29;
    public static final int T__28=28;
    public static final int RETVAL=16;
    public static final int TEST_TREE=9;
    public static final int STRING_=22;
    public static final int NESTED_AST=21;
    public static final int ML_STRING=17;
    public static final int TEST_FAIL=6;
    public static final int ID=12;
    public static final int EOF=-1;
    public static final int NESTED_RETVAL=20;
    public static final int TEST_RETVAL=7;
    public static final int TEST_STDOUT=8;
    public static final int ACTION=15;
    public static final int TEST_OK=5;
    public static final int ML_COMMENT=26;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int WS=23;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int TREE=18;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int FILENAME=19;
    public static final int ID_=24;
    public static final int XDIGIT=27;
    public static final int SL_COMMENT=25;
    public static final int DOC_COMMENT=11;
    public static final int TEST_ACTION=10;
    public static final int SUITE=4;
    public static final int OPTIONS=13;
    public static final int STRING=14;

    // delegates
    // delegators


        public Semantics(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public Semantics(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return Semantics.tokenNames; }
    public String getGrammarFileName() { return "Semantics.g"; }


    	public String name;
    	public Map<String,String> options = new HashMap<String,String>();



    // $ANTLR start "topdown"
    // Semantics.g:20:1: topdown : ( optionsSpec | gUnitDef );
    public final void topdown() throws RecognitionException {
        try {
            // Semantics.g:21:2: ( optionsSpec | gUnitDef )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==OPTIONS) ) {
                alt1=1;
            }
            else if ( (LA1_0==28) ) {
                alt1=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // Semantics.g:21:4: optionsSpec
                    {
                    pushFollow(FOLLOW_optionsSpec_in_topdown50);
                    optionsSpec();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // Semantics.g:22:4: gUnitDef
                    {
                    pushFollow(FOLLOW_gUnitDef_in_topdown55);
                    gUnitDef();

                    state._fsp--;
                    if (state.failed) return ;

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
    // $ANTLR end "topdown"


    // $ANTLR start "gUnitDef"
    // Semantics.g:25:1: gUnitDef : ^( 'gunit' ID ( . )* ) ;
    public final void gUnitDef() throws RecognitionException {
        CommonTree ID1=null;

        try {
            // Semantics.g:26:2: ( ^( 'gunit' ID ( . )* ) )
            // Semantics.g:26:4: ^( 'gunit' ID ( . )* )
            {
            match(input,28,FOLLOW_28_in_gUnitDef67); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID1=(CommonTree)match(input,ID,FOLLOW_ID_in_gUnitDef69); if (state.failed) return ;
            // Semantics.g:26:17: ( . )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=SUITE && LA2_0<=38)) ) {
                    alt2=1;
                }
                else if ( (LA2_0==UP) ) {
                    alt2=2;
                }


                switch (alt2) {
            	case 1 :
            	    // Semantics.g:26:17: .
            	    {
            	    matchAny(input); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              name = (ID1!=null?ID1.getText():null);
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
    // $ANTLR end "gUnitDef"


    // $ANTLR start "optionsSpec"
    // Semantics.g:29:1: optionsSpec : ^( OPTIONS ( option )+ ) ;
    public final void optionsSpec() throws RecognitionException {
        try {
            // Semantics.g:30:2: ( ^( OPTIONS ( option )+ ) )
            // Semantics.g:30:4: ^( OPTIONS ( option )+ )
            {
            match(input,OPTIONS,FOLLOW_OPTIONS_in_optionsSpec88); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            // Semantics.g:30:14: ( option )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==31) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // Semantics.g:30:14: option
            	    {
            	    pushFollow(FOLLOW_option_in_optionsSpec90);
            	    option();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return ;

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
    // $ANTLR end "optionsSpec"


    // $ANTLR start "option"
    // Semantics.g:33:1: option : ( ^( '=' o= ID v= ID ) | ^( '=' o= ID v= STRING ) );
    public final void option() throws RecognitionException {
        CommonTree o=null;
        CommonTree v=null;

        try {
            // Semantics.g:34:5: ( ^( '=' o= ID v= ID ) | ^( '=' o= ID v= STRING ) )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==31) ) {
                int LA4_1 = input.LA(2);

                if ( (LA4_1==DOWN) ) {
                    int LA4_2 = input.LA(3);

                    if ( (LA4_2==ID) ) {
                        int LA4_3 = input.LA(4);

                        if ( (LA4_3==ID) ) {
                            alt4=1;
                        }
                        else if ( (LA4_3==STRING) ) {
                            alt4=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 4, 3, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // Semantics.g:34:9: ^( '=' o= ID v= ID )
                    {
                    match(input,31,FOLLOW_31_in_option109); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    o=(CommonTree)match(input,ID,FOLLOW_ID_in_option113); if (state.failed) return ;
                    v=(CommonTree)match(input,ID,FOLLOW_ID_in_option117); if (state.failed) return ;

                    match(input, Token.UP, null); if (state.failed) return ;
                    if ( state.backtracking==1 ) {
                      options.put((o!=null?o.getText():null), (v!=null?v.getText():null));
                    }

                    }
                    break;
                case 2 :
                    // Semantics.g:35:9: ^( '=' o= ID v= STRING )
                    {
                    match(input,31,FOLLOW_31_in_option132); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    o=(CommonTree)match(input,ID,FOLLOW_ID_in_option136); if (state.failed) return ;
                    v=(CommonTree)match(input,STRING,FOLLOW_STRING_in_option140); if (state.failed) return ;

                    match(input, Token.UP, null); if (state.failed) return ;
                    if ( state.backtracking==1 ) {
                      options.put((o!=null?o.getText():null), (v!=null?v.getText():null));
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
    // $ANTLR end "option"

    // Delegated rules


 

    public static final BitSet FOLLOW_optionsSpec_in_topdown50 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_gUnitDef_in_topdown55 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_gUnitDef67 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_gUnitDef69 = new BitSet(new long[]{0x0000007FFFFFFFF8L});
    public static final BitSet FOLLOW_OPTIONS_in_optionsSpec88 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_option_in_optionsSpec90 = new BitSet(new long[]{0x0000000080000008L});
    public static final BitSet FOLLOW_31_in_option109 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_option113 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_option117 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_31_in_option132 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_option136 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_STRING_in_option140 = new BitSet(new long[]{0x0000000000000008L});

}