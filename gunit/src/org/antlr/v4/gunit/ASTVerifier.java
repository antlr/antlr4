// $ANTLR 3.2.1-SNAPSHOT Jan 26, 2010 15:12:28 ASTVerifier.g 2010-01-27 16:25:17

package org.antlr.v4.gunit;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class ASTVerifier extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SUITE", "TEST_OK", "TEST_FAIL", "TEST_RETVAL", "TEST_STDOUT", "TEST_TREE", "TEST_ACTION", "DOC_COMMENT", "ID", "OPTIONS", "STRING", "ACTION", "RETVAL", "ML_STRING", "TREE", "FILENAME", "NESTED_RETVAL", "NESTED_AST", "WS", "ID_", "SL_COMMENT", "ML_COMMENT", "XDIGIT", "'gunit'", "';'", "'}'", "'='", "'@header'", "'walks'", "':'", "'OK'", "'FAIL'", "'returns'", "'->'"
    };
    public static final int T__29=29;
    public static final int T__28=28;
    public static final int RETVAL=16;
    public static final int TEST_TREE=9;
    public static final int T__27=27;
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
    public static final int ML_COMMENT=25;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int WS=22;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int TREE=18;
    public static final int T__37=37;
    public static final int FILENAME=19;
    public static final int ID_=23;
    public static final int XDIGIT=26;
    public static final int SL_COMMENT=24;
    public static final int DOC_COMMENT=11;
    public static final int TEST_ACTION=10;
    public static final int SUITE=4;
    public static final int OPTIONS=13;
    public static final int STRING=14;

    // delegates
    // delegators


        public ASTVerifier(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public ASTVerifier(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return ASTVerifier.tokenNames; }
    public String getGrammarFileName() { return "ASTVerifier.g"; }



    // $ANTLR start "gUnitDef"
    // ASTVerifier.g:12:1: gUnitDef : ^( 'gunit' ID ( DOC_COMMENT )? ( optionsSpec | header )* ( testsuite )+ ) ;
    public final void gUnitDef() throws RecognitionException {
        try {
            // ASTVerifier.g:13:2: ( ^( 'gunit' ID ( DOC_COMMENT )? ( optionsSpec | header )* ( testsuite )+ ) )
            // ASTVerifier.g:13:4: ^( 'gunit' ID ( DOC_COMMENT )? ( optionsSpec | header )* ( testsuite )+ )
            {
            match(input,27,FOLLOW_27_in_gUnitDef39); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_gUnitDef41); 
            // ASTVerifier.g:13:17: ( DOC_COMMENT )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==DOC_COMMENT) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // ASTVerifier.g:13:17: DOC_COMMENT
                    {
                    match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_gUnitDef43); 

                    }
                    break;

            }

            // ASTVerifier.g:13:30: ( optionsSpec | header )*
            loop2:
            do {
                int alt2=3;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==OPTIONS) ) {
                    alt2=1;
                }
                else if ( (LA2_0==31) ) {
                    alt2=2;
                }


                switch (alt2) {
            	case 1 :
            	    // ASTVerifier.g:13:31: optionsSpec
            	    {
            	    pushFollow(FOLLOW_optionsSpec_in_gUnitDef47);
            	    optionsSpec();

            	    state._fsp--;


            	    }
            	    break;
            	case 2 :
            	    // ASTVerifier.g:13:43: header
            	    {
            	    pushFollow(FOLLOW_header_in_gUnitDef49);
            	    header();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // ASTVerifier.g:13:52: ( testsuite )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==SUITE) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ASTVerifier.g:13:52: testsuite
            	    {
            	    pushFollow(FOLLOW_testsuite_in_gUnitDef53);
            	    testsuite();

            	    state._fsp--;


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
    // ASTVerifier.g:16:1: optionsSpec : ^( OPTIONS ( option )+ ) ;
    public final void optionsSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:17:2: ( ^( OPTIONS ( option )+ ) )
            // ASTVerifier.g:17:4: ^( OPTIONS ( option )+ )
            {
            match(input,OPTIONS,FOLLOW_OPTIONS_in_optionsSpec67); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:17:14: ( option )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==30) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ASTVerifier.g:17:14: option
            	    {
            	    pushFollow(FOLLOW_option_in_optionsSpec69);
            	    option();

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
    // ASTVerifier.g:20:1: option : ( ^( '=' ID ID ) | ^( '=' ID STRING ) );
    public final void option() throws RecognitionException {
        try {
            // ASTVerifier.g:21:5: ( ^( '=' ID ID ) | ^( '=' ID STRING ) )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==30) ) {
                int LA5_1 = input.LA(2);

                if ( (LA5_1==DOWN) ) {
                    int LA5_2 = input.LA(3);

                    if ( (LA5_2==ID) ) {
                        int LA5_3 = input.LA(4);

                        if ( (LA5_3==ID) ) {
                            alt5=1;
                        }
                        else if ( (LA5_3==STRING) ) {
                            alt5=2;
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
                        new NoViableAltException("", 5, 1, input);

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
                    // ASTVerifier.g:21:9: ^( '=' ID ID )
                    {
                    match(input,30,FOLLOW_30_in_option88); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_option90); 
                    match(input,ID,FOLLOW_ID_in_option92); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:22:9: ^( '=' ID STRING )
                    {
                    match(input,30,FOLLOW_30_in_option104); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_option106); 
                    match(input,STRING,FOLLOW_STRING_in_option108); 

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
    // $ANTLR end "option"


    // $ANTLR start "header"
    // ASTVerifier.g:25:1: header : ^( '@header' ACTION ) ;
    public final void header() throws RecognitionException {
        try {
            // ASTVerifier.g:25:8: ( ^( '@header' ACTION ) )
            // ASTVerifier.g:25:10: ^( '@header' ACTION )
            {
            match(input,31,FOLLOW_31_in_header125); 

            match(input, Token.DOWN, null); 
            match(input,ACTION,FOLLOW_ACTION_in_header127); 

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
    // $ANTLR end "header"


    // $ANTLR start "testsuite"
    // ASTVerifier.g:27:1: testsuite : ( ^( SUITE ID ID ( DOC_COMMENT )? ( testcase )+ ) | ^( SUITE ID ( DOC_COMMENT )? ( testcase )+ ) );
    public final void testsuite() throws RecognitionException {
        try {
            // ASTVerifier.g:28:2: ( ^( SUITE ID ID ( DOC_COMMENT )? ( testcase )+ ) | ^( SUITE ID ( DOC_COMMENT )? ( testcase )+ ) )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==SUITE) ) {
                int LA10_1 = input.LA(2);

                if ( (LA10_1==DOWN) ) {
                    int LA10_2 = input.LA(3);

                    if ( (LA10_2==ID) ) {
                        int LA10_3 = input.LA(4);

                        if ( (LA10_3==ID) ) {
                            alt10=1;
                        }
                        else if ( ((LA10_3>=TEST_OK && LA10_3<=DOC_COMMENT)) ) {
                            alt10=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 10, 3, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // ASTVerifier.g:28:4: ^( SUITE ID ID ( DOC_COMMENT )? ( testcase )+ )
                    {
                    match(input,SUITE,FOLLOW_SUITE_in_testsuite138); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_testsuite140); 
                    match(input,ID,FOLLOW_ID_in_testsuite142); 
                    // ASTVerifier.g:28:18: ( DOC_COMMENT )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==DOC_COMMENT) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // ASTVerifier.g:28:18: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testsuite144); 

                            }
                            break;

                    }

                    // ASTVerifier.g:28:31: ( testcase )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0>=TEST_OK && LA7_0<=TEST_ACTION)) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // ASTVerifier.g:28:31: testcase
                    	    {
                    	    pushFollow(FOLLOW_testcase_in_testsuite147);
                    	    testcase();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:29:4: ^( SUITE ID ( DOC_COMMENT )? ( testcase )+ )
                    {
                    match(input,SUITE,FOLLOW_SUITE_in_testsuite155); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_testsuite157); 
                    // ASTVerifier.g:29:15: ( DOC_COMMENT )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==DOC_COMMENT) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // ASTVerifier.g:29:15: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testsuite159); 

                            }
                            break;

                    }

                    // ASTVerifier.g:29:28: ( testcase )+
                    int cnt9=0;
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>=TEST_OK && LA9_0<=TEST_ACTION)) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // ASTVerifier.g:29:28: testcase
                    	    {
                    	    pushFollow(FOLLOW_testcase_in_testsuite162);
                    	    testcase();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt9 >= 1 ) break loop9;
                                EarlyExitException eee =
                                    new EarlyExitException(9, input);
                                throw eee;
                        }
                        cnt9++;
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
    // $ANTLR end "testsuite"


    // $ANTLR start "testcase"
    // ASTVerifier.g:32:1: testcase : ( ^( TEST_OK ( DOC_COMMENT )? input ) | ^( TEST_FAIL ( DOC_COMMENT )? input ) | ^( TEST_RETVAL ( DOC_COMMENT )? input RETVAL ) | ^( TEST_STDOUT ( DOC_COMMENT )? input STRING ) | ^( TEST_STDOUT ( DOC_COMMENT )? input ML_STRING ) | ^( TEST_TREE ( DOC_COMMENT )? input TREE ) | ^( TEST_ACTION ( DOC_COMMENT )? input ACTION ) );
    public final void testcase() throws RecognitionException {
        try {
            // ASTVerifier.g:33:2: ( ^( TEST_OK ( DOC_COMMENT )? input ) | ^( TEST_FAIL ( DOC_COMMENT )? input ) | ^( TEST_RETVAL ( DOC_COMMENT )? input RETVAL ) | ^( TEST_STDOUT ( DOC_COMMENT )? input STRING ) | ^( TEST_STDOUT ( DOC_COMMENT )? input ML_STRING ) | ^( TEST_TREE ( DOC_COMMENT )? input TREE ) | ^( TEST_ACTION ( DOC_COMMENT )? input ACTION ) )
            int alt18=7;
            alt18 = dfa18.predict(input);
            switch (alt18) {
                case 1 :
                    // ASTVerifier.g:33:4: ^( TEST_OK ( DOC_COMMENT )? input )
                    {
                    match(input,TEST_OK,FOLLOW_TEST_OK_in_testcase176); 

                    match(input, Token.DOWN, null); 
                    // ASTVerifier.g:33:14: ( DOC_COMMENT )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==DOC_COMMENT) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // ASTVerifier.g:33:14: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase178); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_input_in_testcase181);
                    input();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:34:4: ^( TEST_FAIL ( DOC_COMMENT )? input )
                    {
                    match(input,TEST_FAIL,FOLLOW_TEST_FAIL_in_testcase188); 

                    match(input, Token.DOWN, null); 
                    // ASTVerifier.g:34:16: ( DOC_COMMENT )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==DOC_COMMENT) ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // ASTVerifier.g:34:16: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase190); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_input_in_testcase193);
                    input();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:35:4: ^( TEST_RETVAL ( DOC_COMMENT )? input RETVAL )
                    {
                    match(input,TEST_RETVAL,FOLLOW_TEST_RETVAL_in_testcase200); 

                    match(input, Token.DOWN, null); 
                    // ASTVerifier.g:35:18: ( DOC_COMMENT )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==DOC_COMMENT) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // ASTVerifier.g:35:18: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase202); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_input_in_testcase205);
                    input();

                    state._fsp--;

                    match(input,RETVAL,FOLLOW_RETVAL_in_testcase207); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:36:4: ^( TEST_STDOUT ( DOC_COMMENT )? input STRING )
                    {
                    match(input,TEST_STDOUT,FOLLOW_TEST_STDOUT_in_testcase214); 

                    match(input, Token.DOWN, null); 
                    // ASTVerifier.g:36:18: ( DOC_COMMENT )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==DOC_COMMENT) ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // ASTVerifier.g:36:18: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase216); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_input_in_testcase219);
                    input();

                    state._fsp--;

                    match(input,STRING,FOLLOW_STRING_in_testcase221); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // ASTVerifier.g:37:4: ^( TEST_STDOUT ( DOC_COMMENT )? input ML_STRING )
                    {
                    match(input,TEST_STDOUT,FOLLOW_TEST_STDOUT_in_testcase228); 

                    match(input, Token.DOWN, null); 
                    // ASTVerifier.g:37:18: ( DOC_COMMENT )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==DOC_COMMENT) ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // ASTVerifier.g:37:18: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase230); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_input_in_testcase233);
                    input();

                    state._fsp--;

                    match(input,ML_STRING,FOLLOW_ML_STRING_in_testcase235); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // ASTVerifier.g:38:4: ^( TEST_TREE ( DOC_COMMENT )? input TREE )
                    {
                    match(input,TEST_TREE,FOLLOW_TEST_TREE_in_testcase242); 

                    match(input, Token.DOWN, null); 
                    // ASTVerifier.g:38:16: ( DOC_COMMENT )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==DOC_COMMENT) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // ASTVerifier.g:38:16: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase244); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_input_in_testcase247);
                    input();

                    state._fsp--;

                    match(input,TREE,FOLLOW_TREE_in_testcase249); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // ASTVerifier.g:39:4: ^( TEST_ACTION ( DOC_COMMENT )? input ACTION )
                    {
                    match(input,TEST_ACTION,FOLLOW_TEST_ACTION_in_testcase256); 

                    match(input, Token.DOWN, null); 
                    // ASTVerifier.g:39:18: ( DOC_COMMENT )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( (LA17_0==DOC_COMMENT) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // ASTVerifier.g:39:18: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase258); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_input_in_testcase261);
                    input();

                    state._fsp--;

                    match(input,ACTION,FOLLOW_ACTION_in_testcase263); 

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
    // $ANTLR end "testcase"


    // $ANTLR start "input"
    // ASTVerifier.g:42:1: input : ( STRING | ML_STRING | FILENAME );
    public final void input() throws RecognitionException {
        try {
            // ASTVerifier.g:43:2: ( STRING | ML_STRING | FILENAME )
            // ASTVerifier.g:
            {
            if ( input.LA(1)==STRING||input.LA(1)==ML_STRING||input.LA(1)==FILENAME ) {
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
    // $ANTLR end "input"

    // Delegated rules


    protected DFA18 dfa18 = new DFA18(this);
    static final String DFA18_eotS =
        "\14\uffff";
    static final String DFA18_eofS =
        "\14\uffff";
    static final String DFA18_minS =
        "\1\5\3\uffff\1\2\2\uffff\1\13\2\16\2\uffff";
    static final String DFA18_maxS =
        "\1\12\3\uffff\1\2\2\uffff\2\23\1\21\2\uffff";
    static final String DFA18_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\uffff\1\6\1\7\3\uffff\1\4\1\5";
    static final String DFA18_specialS =
        "\14\uffff}>";
    static final String[] DFA18_transitionS = {
            "\1\1\1\2\1\3\1\4\1\5\1\6",
            "",
            "",
            "",
            "\1\7",
            "",
            "",
            "\1\10\2\uffff\1\11\2\uffff\1\11\1\uffff\1\11",
            "\1\11\2\uffff\1\11\1\uffff\1\11",
            "\1\12\2\uffff\1\13",
            "",
            ""
    };

    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
        }
    }

    class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA18_eot;
            this.eof = DFA18_eof;
            this.min = DFA18_min;
            this.max = DFA18_max;
            this.accept = DFA18_accept;
            this.special = DFA18_special;
            this.transition = DFA18_transition;
        }
        public String getDescription() {
            return "32:1: testcase : ( ^( TEST_OK ( DOC_COMMENT )? input ) | ^( TEST_FAIL ( DOC_COMMENT )? input ) | ^( TEST_RETVAL ( DOC_COMMENT )? input RETVAL ) | ^( TEST_STDOUT ( DOC_COMMENT )? input STRING ) | ^( TEST_STDOUT ( DOC_COMMENT )? input ML_STRING ) | ^( TEST_TREE ( DOC_COMMENT )? input TREE ) | ^( TEST_ACTION ( DOC_COMMENT )? input ACTION ) );";
        }
    }
 

    public static final BitSet FOLLOW_27_in_gUnitDef39 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_gUnitDef41 = new BitSet(new long[]{0x0000000080002810L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_gUnitDef43 = new BitSet(new long[]{0x0000000080002810L});
    public static final BitSet FOLLOW_optionsSpec_in_gUnitDef47 = new BitSet(new long[]{0x0000000080002810L});
    public static final BitSet FOLLOW_header_in_gUnitDef49 = new BitSet(new long[]{0x0000000080002810L});
    public static final BitSet FOLLOW_testsuite_in_gUnitDef53 = new BitSet(new long[]{0x0000000080002818L});
    public static final BitSet FOLLOW_OPTIONS_in_optionsSpec67 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_option_in_optionsSpec69 = new BitSet(new long[]{0x0000000040000008L});
    public static final BitSet FOLLOW_30_in_option88 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_option90 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_option92 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_30_in_option104 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_option106 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_STRING_in_option108 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_31_in_header125 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_header127 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SUITE_in_testsuite138 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_testsuite140 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_testsuite142 = new BitSet(new long[]{0x0000000000000FE0L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testsuite144 = new BitSet(new long[]{0x0000000000000FE0L});
    public static final BitSet FOLLOW_testcase_in_testsuite147 = new BitSet(new long[]{0x0000000000000FE8L});
    public static final BitSet FOLLOW_SUITE_in_testsuite155 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_testsuite157 = new BitSet(new long[]{0x0000000000000FE0L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testsuite159 = new BitSet(new long[]{0x0000000000000FE0L});
    public static final BitSet FOLLOW_testcase_in_testsuite162 = new BitSet(new long[]{0x0000000000000FE8L});
    public static final BitSet FOLLOW_TEST_OK_in_testcase176 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase178 = new BitSet(new long[]{0x00000000000A4000L});
    public static final BitSet FOLLOW_input_in_testcase181 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEST_FAIL_in_testcase188 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase190 = new BitSet(new long[]{0x00000000000A4000L});
    public static final BitSet FOLLOW_input_in_testcase193 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEST_RETVAL_in_testcase200 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase202 = new BitSet(new long[]{0x00000000000A4000L});
    public static final BitSet FOLLOW_input_in_testcase205 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RETVAL_in_testcase207 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEST_STDOUT_in_testcase214 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase216 = new BitSet(new long[]{0x00000000000A4000L});
    public static final BitSet FOLLOW_input_in_testcase219 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_STRING_in_testcase221 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEST_STDOUT_in_testcase228 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase230 = new BitSet(new long[]{0x00000000000A4000L});
    public static final BitSet FOLLOW_input_in_testcase233 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_ML_STRING_in_testcase235 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEST_TREE_in_testcase242 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase244 = new BitSet(new long[]{0x00000000000A4000L});
    public static final BitSet FOLLOW_input_in_testcase247 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_TREE_in_testcase249 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEST_ACTION_in_testcase256 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase258 = new BitSet(new long[]{0x00000000000A4000L});
    public static final BitSet FOLLOW_input_in_testcase261 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_ACTION_in_testcase263 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_input0 = new BitSet(new long[]{0x0000000000000002L});

}