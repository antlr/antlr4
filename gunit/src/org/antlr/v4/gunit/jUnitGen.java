// $ANTLR 3.4 jUnitGen.g 2011-06-20 18:31:51

package org.antlr.v4.gunit;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.stringtemplate.*;
import org.antlr.stringtemplate.language.*;
import java.util.HashMap;
@SuppressWarnings({"all", "warnings", "unchecked"})
public class jUnitGen extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ACTION", "DOC_COMMENT", "FILENAME", "ID", "ID_", "ML_COMMENT", "ML_STRING", "NESTED_AST", "NESTED_RETVAL", "OPTIONS", "RETVAL", "SL_COMMENT", "STRING", "STRING_", "SUITE", "TEST_ACTION", "TEST_FAIL", "TEST_OK", "TEST_RETVAL", "TEST_STDOUT", "TEST_TREE", "TREE", "WS", "XDIGIT", "'->'", "':'", "';'", "'='", "'@header'", "'FAIL'", "'OK'", "'gunit'", "'returns'", "'walks'", "'}'"
    };

    public static final int EOF=-1;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int ACTION=4;
    public static final int DOC_COMMENT=5;
    public static final int FILENAME=6;
    public static final int ID=7;
    public static final int ID_=8;
    public static final int ML_COMMENT=9;
    public static final int ML_STRING=10;
    public static final int NESTED_AST=11;
    public static final int NESTED_RETVAL=12;
    public static final int OPTIONS=13;
    public static final int RETVAL=14;
    public static final int SL_COMMENT=15;
    public static final int STRING=16;
    public static final int STRING_=17;
    public static final int SUITE=18;
    public static final int TEST_ACTION=19;
    public static final int TEST_FAIL=20;
    public static final int TEST_OK=21;
    public static final int TEST_RETVAL=22;
    public static final int TEST_STDOUT=23;
    public static final int TEST_TREE=24;
    public static final int TREE=25;
    public static final int WS=26;
    public static final int XDIGIT=27;

    // delegates
    public TreeParser[] getDelegates() {
        return new TreeParser[] {};
    }

    // delegators


    public jUnitGen(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }
    public jUnitGen(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected StringTemplateGroup templateLib =
  new StringTemplateGroup("jUnitGenTemplates", AngleBracketTemplateLexer.class);

public void setTemplateLib(StringTemplateGroup templateLib) {
  this.templateLib = templateLib;
}
public StringTemplateGroup getTemplateLib() {
  return templateLib;
}
/** allows convenient multi-value initialization:
 *  "new STAttrMap().put(...).put(...)"
 */
public static class STAttrMap extends HashMap {
  public STAttrMap put(String attrName, Object value) {
    super.put(attrName, value);
    return this;
  }
  public STAttrMap put(String attrName, int value) {
    super.put(attrName, new Integer(value));
    return this;
  }
}
    public String[] getTokenNames() { return jUnitGen.tokenNames; }
    public String getGrammarFileName() { return "jUnitGen.g"; }


    public static class gUnitDef_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "gUnitDef"
    // jUnitGen.g:13:1: gUnitDef : ^( 'gunit' ID ( DOC_COMMENT )? ( optionsSpec | header )* (suites+= testsuite )+ ) -> jUnitClass(className=$ID.textheader=$header.stsuites=$suites);
    public final jUnitGen.gUnitDef_return gUnitDef() throws RecognitionException {
        jUnitGen.gUnitDef_return retval = new jUnitGen.gUnitDef_return();
        retval.start = input.LT(1);


        CommonTree ID1=null;
        List list_suites=null;
        jUnitGen.header_return header2 =null;

        RuleReturnScope suites = null;
        try {
            // jUnitGen.g:14:2: ( ^( 'gunit' ID ( DOC_COMMENT )? ( optionsSpec | header )* (suites+= testsuite )+ ) -> jUnitClass(className=$ID.textheader=$header.stsuites=$suites))
            // jUnitGen.g:14:4: ^( 'gunit' ID ( DOC_COMMENT )? ( optionsSpec | header )* (suites+= testsuite )+ )
            {
            match(input,35,FOLLOW_35_in_gUnitDef45); 

            match(input, Token.DOWN, null); 
            ID1=(CommonTree)match(input,ID,FOLLOW_ID_in_gUnitDef47); 

            // jUnitGen.g:14:17: ( DOC_COMMENT )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==DOC_COMMENT) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // jUnitGen.g:14:17: DOC_COMMENT
                    {
                    match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_gUnitDef49); 

                    }
                    break;

            }


            // jUnitGen.g:14:30: ( optionsSpec | header )*
            loop2:
            do {
                int alt2=3;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==OPTIONS) ) {
                    alt2=1;
                }
                else if ( (LA2_0==32) ) {
                    alt2=2;
                }


                switch (alt2) {
            	case 1 :
            	    // jUnitGen.g:14:31: optionsSpec
            	    {
            	    pushFollow(FOLLOW_optionsSpec_in_gUnitDef53);
            	    optionsSpec();

            	    state._fsp--;


            	    }
            	    break;
            	case 2 :
            	    // jUnitGen.g:14:43: header
            	    {
            	    pushFollow(FOLLOW_header_in_gUnitDef55);
            	    header2=header();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            // jUnitGen.g:14:58: (suites+= testsuite )+
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
            	    // jUnitGen.g:14:58: suites+= testsuite
            	    {
            	    pushFollow(FOLLOW_testsuite_in_gUnitDef61);
            	    suites=testsuite();

            	    state._fsp--;

            	    if (list_suites==null) list_suites=new ArrayList();
            	    list_suites.add(suites.getTemplate());


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


            // TEMPLATE REWRITE
            // 15:3: -> jUnitClass(className=$ID.textheader=$header.stsuites=$suites)
            {
                retval.st = templateLib.getInstanceOf("jUnitClass",new STAttrMap().put("className", (ID1!=null?ID1.getText():null)).put("header", (header2!=null?header2.st:null)).put("suites", list_suites));
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
        return retval;
    }
    // $ANTLR end "gUnitDef"


    public static class optionsSpec_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "optionsSpec"
    // jUnitGen.g:18:1: optionsSpec : ^( OPTIONS ( option )+ ) ;
    public final jUnitGen.optionsSpec_return optionsSpec() throws RecognitionException {
        jUnitGen.optionsSpec_return retval = new jUnitGen.optionsSpec_return();
        retval.start = input.LT(1);


        try {
            // jUnitGen.g:19:2: ( ^( OPTIONS ( option )+ ) )
            // jUnitGen.g:19:4: ^( OPTIONS ( option )+ )
            {
            match(input,OPTIONS,FOLLOW_OPTIONS_in_optionsSpec96); 

            match(input, Token.DOWN, null); 
            // jUnitGen.g:19:14: ( option )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==31) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // jUnitGen.g:19:14: option
            	    {
            	    pushFollow(FOLLOW_option_in_optionsSpec98);
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
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "optionsSpec"


    public static class option_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "option"
    // jUnitGen.g:22:1: option : ( ^( '=' ID ID ) | ^( '=' ID STRING ) );
    public final jUnitGen.option_return option() throws RecognitionException {
        jUnitGen.option_return retval = new jUnitGen.option_return();
        retval.start = input.LT(1);


        try {
            // jUnitGen.g:23:5: ( ^( '=' ID ID ) | ^( '=' ID STRING ) )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==31) ) {
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
                    // jUnitGen.g:23:9: ^( '=' ID ID )
                    {
                    match(input,31,FOLLOW_31_in_option117); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_option119); 

                    match(input,ID,FOLLOW_ID_in_option121); 

                    match(input, Token.UP, null); 


                    }
                    break;
                case 2 :
                    // jUnitGen.g:24:9: ^( '=' ID STRING )
                    {
                    match(input,31,FOLLOW_31_in_option133); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_option135); 

                    match(input,STRING,FOLLOW_STRING_in_option137); 

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
        return retval;
    }
    // $ANTLR end "option"


    public static class header_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "header"
    // jUnitGen.g:27:1: header : ^( '@header' ACTION ) -> header(action=$ACTION.text);
    public final jUnitGen.header_return header() throws RecognitionException {
        jUnitGen.header_return retval = new jUnitGen.header_return();
        retval.start = input.LT(1);


        CommonTree ACTION3=null;

        try {
            // jUnitGen.g:27:8: ( ^( '@header' ACTION ) -> header(action=$ACTION.text))
            // jUnitGen.g:27:10: ^( '@header' ACTION )
            {
            match(input,32,FOLLOW_32_in_header154); 

            match(input, Token.DOWN, null); 
            ACTION3=(CommonTree)match(input,ACTION,FOLLOW_ACTION_in_header156); 

            match(input, Token.UP, null); 


            // TEMPLATE REWRITE
            // 27:30: -> header(action=$ACTION.text)
            {
                retval.st = templateLib.getInstanceOf("header",new STAttrMap().put("action", (ACTION3!=null?ACTION3.getText():null)));
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
        return retval;
    }
    // $ANTLR end "header"


    public static class testsuite_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "testsuite"
    // jUnitGen.g:29:1: testsuite : ( ^( SUITE rule= ID ID ( DOC_COMMENT )? (cases+= testcase[$rule.text] )+ ) | ^( SUITE rule= ID ( DOC_COMMENT )? (cases+= testcase[$rule.text] )+ ) -> testSuite(name=$rule.textcases=$cases));
    public final jUnitGen.testsuite_return testsuite() throws RecognitionException {
        jUnitGen.testsuite_return retval = new jUnitGen.testsuite_return();
        retval.start = input.LT(1);


        CommonTree rule=null;
        List list_cases=null;
        RuleReturnScope cases = null;
        try {
            // jUnitGen.g:30:2: ( ^( SUITE rule= ID ID ( DOC_COMMENT )? (cases+= testcase[$rule.text] )+ ) | ^( SUITE rule= ID ( DOC_COMMENT )? (cases+= testcase[$rule.text] )+ ) -> testSuite(name=$rule.textcases=$cases))
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
                        else if ( (LA10_3==DOC_COMMENT||(LA10_3 >= TEST_ACTION && LA10_3 <= TEST_TREE)) ) {
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
                    // jUnitGen.g:30:4: ^( SUITE rule= ID ID ( DOC_COMMENT )? (cases+= testcase[$rule.text] )+ )
                    {
                    match(input,SUITE,FOLLOW_SUITE_in_testsuite176); 

                    match(input, Token.DOWN, null); 
                    rule=(CommonTree)match(input,ID,FOLLOW_ID_in_testsuite180); 

                    match(input,ID,FOLLOW_ID_in_testsuite182); 

                    // jUnitGen.g:30:23: ( DOC_COMMENT )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==DOC_COMMENT) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // jUnitGen.g:30:23: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testsuite184); 

                            }
                            break;

                    }


                    // jUnitGen.g:30:41: (cases+= testcase[$rule.text] )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0 >= TEST_ACTION && LA7_0 <= TEST_TREE)) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // jUnitGen.g:30:41: cases+= testcase[$rule.text]
                    	    {
                    	    pushFollow(FOLLOW_testcase_in_testsuite189);
                    	    cases=testcase((rule!=null?rule.getText():null));

                    	    state._fsp--;

                    	    if (list_cases==null) list_cases=new ArrayList();
                    	    list_cases.add(cases.getTemplate());


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
                    // jUnitGen.g:31:4: ^( SUITE rule= ID ( DOC_COMMENT )? (cases+= testcase[$rule.text] )+ )
                    {
                    match(input,SUITE,FOLLOW_SUITE_in_testsuite198); 

                    match(input, Token.DOWN, null); 
                    rule=(CommonTree)match(input,ID,FOLLOW_ID_in_testsuite202); 

                    // jUnitGen.g:31:23: ( DOC_COMMENT )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==DOC_COMMENT) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // jUnitGen.g:31:23: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testsuite207); 

                            }
                            break;

                    }


                    // jUnitGen.g:31:41: (cases+= testcase[$rule.text] )+
                    int cnt9=0;
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0 >= TEST_ACTION && LA9_0 <= TEST_TREE)) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // jUnitGen.g:31:41: cases+= testcase[$rule.text]
                    	    {
                    	    pushFollow(FOLLOW_testcase_in_testsuite212);
                    	    cases=testcase((rule!=null?rule.getText():null));

                    	    state._fsp--;

                    	    if (list_cases==null) list_cases=new ArrayList();
                    	    list_cases.add(cases.getTemplate());


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


                    // TEMPLATE REWRITE
                    // 32:3: -> testSuite(name=$rule.textcases=$cases)
                    {
                        retval.st = templateLib.getInstanceOf("testSuite",new STAttrMap().put("name", (rule!=null?rule.getText():null)).put("cases", list_cases));
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
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "testsuite"


    public static class testcase_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "testcase"
    // jUnitGen.g:35:1: testcase[String ruleName] : ( ^( TEST_OK ( DOC_COMMENT )? input ) | ^( TEST_FAIL ( DOC_COMMENT )? input ) | ^( TEST_RETVAL ( DOC_COMMENT )? input RETVAL ) | ^( TEST_STDOUT ( DOC_COMMENT )? input STRING ) | ^( TEST_STDOUT ( DOC_COMMENT )? input ML_STRING ) | ^( TEST_TREE ( DOC_COMMENT )? input TREE ) -> parserRuleTestAST(ruleName=$ruleNameinput=$input.stexpecting=Gen.normalizeTreeSpec($TREE.text)scriptLine=$input.start.getLine())| ^( TEST_ACTION ( DOC_COMMENT )? input ACTION ) );
    public final jUnitGen.testcase_return testcase(String ruleName) throws RecognitionException {
        jUnitGen.testcase_return retval = new jUnitGen.testcase_return();
        retval.start = input.LT(1);


        CommonTree TREE5=null;
        jUnitGen.input_return input4 =null;


        try {
            // jUnitGen.g:36:2: ( ^( TEST_OK ( DOC_COMMENT )? input ) | ^( TEST_FAIL ( DOC_COMMENT )? input ) | ^( TEST_RETVAL ( DOC_COMMENT )? input RETVAL ) | ^( TEST_STDOUT ( DOC_COMMENT )? input STRING ) | ^( TEST_STDOUT ( DOC_COMMENT )? input ML_STRING ) | ^( TEST_TREE ( DOC_COMMENT )? input TREE ) -> parserRuleTestAST(ruleName=$ruleNameinput=$input.stexpecting=Gen.normalizeTreeSpec($TREE.text)scriptLine=$input.start.getLine())| ^( TEST_ACTION ( DOC_COMMENT )? input ACTION ) )
            int alt18=7;
            switch ( input.LA(1) ) {
            case TEST_OK:
                {
                alt18=1;
                }
                break;
            case TEST_FAIL:
                {
                alt18=2;
                }
                break;
            case TEST_RETVAL:
                {
                alt18=3;
                }
                break;
            case TEST_STDOUT:
                {
                int LA18_4 = input.LA(2);

                if ( (LA18_4==DOWN) ) {
                    switch ( input.LA(3) ) {
                    case DOC_COMMENT:
                        {
                        switch ( input.LA(4) ) {
                        case STRING:
                            {
                            int LA18_9 = input.LA(5);

                            if ( (LA18_9==STRING) ) {
                                alt18=4;
                            }
                            else if ( (LA18_9==ML_STRING) ) {
                                alt18=5;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 18, 9, input);

                                throw nvae;

                            }
                            }
                            break;
                        case ML_STRING:
                            {
                            int LA18_10 = input.LA(5);

                            if ( (LA18_10==STRING) ) {
                                alt18=4;
                            }
                            else if ( (LA18_10==ML_STRING) ) {
                                alt18=5;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 18, 10, input);

                                throw nvae;

                            }
                            }
                            break;
                        case FILENAME:
                            {
                            int LA18_11 = input.LA(5);

                            if ( (LA18_11==STRING) ) {
                                alt18=4;
                            }
                            else if ( (LA18_11==ML_STRING) ) {
                                alt18=5;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 18, 11, input);

                                throw nvae;

                            }
                            }
                            break;
                        default:
                            NoViableAltException nvae =
                                new NoViableAltException("", 18, 8, input);

                            throw nvae;

                        }

                        }
                        break;
                    case STRING:
                        {
                        int LA18_9 = input.LA(4);

                        if ( (LA18_9==STRING) ) {
                            alt18=4;
                        }
                        else if ( (LA18_9==ML_STRING) ) {
                            alt18=5;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 18, 9, input);

                            throw nvae;

                        }
                        }
                        break;
                    case ML_STRING:
                        {
                        int LA18_10 = input.LA(4);

                        if ( (LA18_10==STRING) ) {
                            alt18=4;
                        }
                        else if ( (LA18_10==ML_STRING) ) {
                            alt18=5;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 18, 10, input);

                            throw nvae;

                        }
                        }
                        break;
                    case FILENAME:
                        {
                        int LA18_11 = input.LA(4);

                        if ( (LA18_11==STRING) ) {
                            alt18=4;
                        }
                        else if ( (LA18_11==ML_STRING) ) {
                            alt18=5;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 18, 11, input);

                            throw nvae;

                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 18, 7, input);

                        throw nvae;

                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 4, input);

                    throw nvae;

                }
                }
                break;
            case TEST_TREE:
                {
                alt18=6;
                }
                break;
            case TEST_ACTION:
                {
                alt18=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;

            }

            switch (alt18) {
                case 1 :
                    // jUnitGen.g:36:4: ^( TEST_OK ( DOC_COMMENT )? input )
                    {
                    match(input,TEST_OK,FOLLOW_TEST_OK_in_testcase244); 

                    match(input, Token.DOWN, null); 
                    // jUnitGen.g:36:14: ( DOC_COMMENT )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==DOC_COMMENT) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // jUnitGen.g:36:14: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase246); 

                            }
                            break;

                    }


                    pushFollow(FOLLOW_input_in_testcase249);
                    input();

                    state._fsp--;


                    match(input, Token.UP, null); 


                    }
                    break;
                case 2 :
                    // jUnitGen.g:37:4: ^( TEST_FAIL ( DOC_COMMENT )? input )
                    {
                    match(input,TEST_FAIL,FOLLOW_TEST_FAIL_in_testcase256); 

                    match(input, Token.DOWN, null); 
                    // jUnitGen.g:37:16: ( DOC_COMMENT )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==DOC_COMMENT) ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // jUnitGen.g:37:16: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase258); 

                            }
                            break;

                    }


                    pushFollow(FOLLOW_input_in_testcase261);
                    input();

                    state._fsp--;


                    match(input, Token.UP, null); 


                    }
                    break;
                case 3 :
                    // jUnitGen.g:38:4: ^( TEST_RETVAL ( DOC_COMMENT )? input RETVAL )
                    {
                    match(input,TEST_RETVAL,FOLLOW_TEST_RETVAL_in_testcase268); 

                    match(input, Token.DOWN, null); 
                    // jUnitGen.g:38:18: ( DOC_COMMENT )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==DOC_COMMENT) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // jUnitGen.g:38:18: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase270); 

                            }
                            break;

                    }


                    pushFollow(FOLLOW_input_in_testcase273);
                    input();

                    state._fsp--;


                    match(input,RETVAL,FOLLOW_RETVAL_in_testcase275); 

                    match(input, Token.UP, null); 


                    }
                    break;
                case 4 :
                    // jUnitGen.g:39:4: ^( TEST_STDOUT ( DOC_COMMENT )? input STRING )
                    {
                    match(input,TEST_STDOUT,FOLLOW_TEST_STDOUT_in_testcase282); 

                    match(input, Token.DOWN, null); 
                    // jUnitGen.g:39:18: ( DOC_COMMENT )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==DOC_COMMENT) ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // jUnitGen.g:39:18: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase284); 

                            }
                            break;

                    }


                    pushFollow(FOLLOW_input_in_testcase287);
                    input();

                    state._fsp--;


                    match(input,STRING,FOLLOW_STRING_in_testcase289); 

                    match(input, Token.UP, null); 


                    }
                    break;
                case 5 :
                    // jUnitGen.g:40:4: ^( TEST_STDOUT ( DOC_COMMENT )? input ML_STRING )
                    {
                    match(input,TEST_STDOUT,FOLLOW_TEST_STDOUT_in_testcase296); 

                    match(input, Token.DOWN, null); 
                    // jUnitGen.g:40:18: ( DOC_COMMENT )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==DOC_COMMENT) ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // jUnitGen.g:40:18: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase298); 

                            }
                            break;

                    }


                    pushFollow(FOLLOW_input_in_testcase301);
                    input();

                    state._fsp--;


                    match(input,ML_STRING,FOLLOW_ML_STRING_in_testcase303); 

                    match(input, Token.UP, null); 


                    }
                    break;
                case 6 :
                    // jUnitGen.g:41:4: ^( TEST_TREE ( DOC_COMMENT )? input TREE )
                    {
                    match(input,TEST_TREE,FOLLOW_TEST_TREE_in_testcase310); 

                    match(input, Token.DOWN, null); 
                    // jUnitGen.g:41:16: ( DOC_COMMENT )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==DOC_COMMENT) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // jUnitGen.g:41:16: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase312); 

                            }
                            break;

                    }


                    pushFollow(FOLLOW_input_in_testcase315);
                    input4=input();

                    state._fsp--;


                    TREE5=(CommonTree)match(input,TREE,FOLLOW_TREE_in_testcase317); 

                    match(input, Token.UP, null); 


                    // TEMPLATE REWRITE
                    // 42:4: -> parserRuleTestAST(ruleName=$ruleNameinput=$input.stexpecting=Gen.normalizeTreeSpec($TREE.text)scriptLine=$input.start.getLine())
                    {
                        retval.st = templateLib.getInstanceOf("parserRuleTestAST",new STAttrMap().put("ruleName", ruleName).put("input", (input4!=null?input4.st:null)).put("expecting", Gen.normalizeTreeSpec((TREE5!=null?TREE5.getText():null))).put("scriptLine", (input4!=null?((CommonTree)input4.start):null).getLine()));
                    }



                    }
                    break;
                case 7 :
                    // jUnitGen.g:46:4: ^( TEST_ACTION ( DOC_COMMENT )? input ACTION )
                    {
                    match(input,TEST_ACTION,FOLLOW_TEST_ACTION_in_testcase387); 

                    match(input, Token.DOWN, null); 
                    // jUnitGen.g:46:18: ( DOC_COMMENT )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( (LA17_0==DOC_COMMENT) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // jUnitGen.g:46:18: DOC_COMMENT
                            {
                            match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase389); 

                            }
                            break;

                    }


                    pushFollow(FOLLOW_input_in_testcase392);
                    input();

                    state._fsp--;


                    match(input,ACTION,FOLLOW_ACTION_in_testcase394); 

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
        return retval;
    }
    // $ANTLR end "testcase"


    public static class input_return extends TreeRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };


    // $ANTLR start "input"
    // jUnitGen.g:49:1: input : ( STRING -> string(s=Gen.escapeForJava($STRING.text))| ML_STRING -> string(s=Gen.escapeForJava($ML_STRING.text))| FILENAME );
    public final jUnitGen.input_return input() throws RecognitionException {
        jUnitGen.input_return retval = new jUnitGen.input_return();
        retval.start = input.LT(1);


        CommonTree STRING6=null;
        CommonTree ML_STRING7=null;

        try {
            // jUnitGen.g:50:2: ( STRING -> string(s=Gen.escapeForJava($STRING.text))| ML_STRING -> string(s=Gen.escapeForJava($ML_STRING.text))| FILENAME )
            int alt19=3;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt19=1;
                }
                break;
            case ML_STRING:
                {
                alt19=2;
                }
                break;
            case FILENAME:
                {
                alt19=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;

            }

            switch (alt19) {
                case 1 :
                    // jUnitGen.g:50:4: STRING
                    {
                    STRING6=(CommonTree)match(input,STRING,FOLLOW_STRING_in_input406); 

                    // TEMPLATE REWRITE
                    // 50:12: -> string(s=Gen.escapeForJava($STRING.text))
                    {
                        retval.st = templateLib.getInstanceOf("string",new STAttrMap().put("s", Gen.escapeForJava((STRING6!=null?STRING6.getText():null))));
                    }



                    }
                    break;
                case 2 :
                    // jUnitGen.g:51:4: ML_STRING
                    {
                    ML_STRING7=(CommonTree)match(input,ML_STRING,FOLLOW_ML_STRING_in_input421); 

                    // TEMPLATE REWRITE
                    // 51:14: -> string(s=Gen.escapeForJava($ML_STRING.text))
                    {
                        retval.st = templateLib.getInstanceOf("string",new STAttrMap().put("s", Gen.escapeForJava((ML_STRING7!=null?ML_STRING7.getText():null))));
                    }



                    }
                    break;
                case 3 :
                    // jUnitGen.g:52:4: FILENAME
                    {
                    match(input,FILENAME,FOLLOW_FILENAME_in_input435); 

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
    // $ANTLR end "input"

    // Delegated rules


 

    public static final BitSet FOLLOW_35_in_gUnitDef45 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_gUnitDef47 = new BitSet(new long[]{0x0000000100042020L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_gUnitDef49 = new BitSet(new long[]{0x0000000100042000L});
    public static final BitSet FOLLOW_optionsSpec_in_gUnitDef53 = new BitSet(new long[]{0x0000000100042000L});
    public static final BitSet FOLLOW_header_in_gUnitDef55 = new BitSet(new long[]{0x0000000100042000L});
    public static final BitSet FOLLOW_testsuite_in_gUnitDef61 = new BitSet(new long[]{0x0000000000040008L});
    public static final BitSet FOLLOW_OPTIONS_in_optionsSpec96 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_option_in_optionsSpec98 = new BitSet(new long[]{0x0000000080000008L});
    public static final BitSet FOLLOW_31_in_option117 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_option119 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ID_in_option121 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_31_in_option133 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_option135 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_STRING_in_option137 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_32_in_header154 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_header156 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SUITE_in_testsuite176 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_testsuite180 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ID_in_testsuite182 = new BitSet(new long[]{0x0000000001F80020L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testsuite184 = new BitSet(new long[]{0x0000000001F80000L});
    public static final BitSet FOLLOW_testcase_in_testsuite189 = new BitSet(new long[]{0x0000000001F80008L});
    public static final BitSet FOLLOW_SUITE_in_testsuite198 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_testsuite202 = new BitSet(new long[]{0x0000000001F80020L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testsuite207 = new BitSet(new long[]{0x0000000001F80000L});
    public static final BitSet FOLLOW_testcase_in_testsuite212 = new BitSet(new long[]{0x0000000001F80008L});
    public static final BitSet FOLLOW_TEST_OK_in_testcase244 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase246 = new BitSet(new long[]{0x0000000000010440L});
    public static final BitSet FOLLOW_input_in_testcase249 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEST_FAIL_in_testcase256 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase258 = new BitSet(new long[]{0x0000000000010440L});
    public static final BitSet FOLLOW_input_in_testcase261 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEST_RETVAL_in_testcase268 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase270 = new BitSet(new long[]{0x0000000000010440L});
    public static final BitSet FOLLOW_input_in_testcase273 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_RETVAL_in_testcase275 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEST_STDOUT_in_testcase282 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase284 = new BitSet(new long[]{0x0000000000010440L});
    public static final BitSet FOLLOW_input_in_testcase287 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_STRING_in_testcase289 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEST_STDOUT_in_testcase296 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase298 = new BitSet(new long[]{0x0000000000010440L});
    public static final BitSet FOLLOW_input_in_testcase301 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_ML_STRING_in_testcase303 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEST_TREE_in_testcase310 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase312 = new BitSet(new long[]{0x0000000000010440L});
    public static final BitSet FOLLOW_input_in_testcase315 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_TREE_in_testcase317 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEST_ACTION_in_testcase387 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase389 = new BitSet(new long[]{0x0000000000010440L});
    public static final BitSet FOLLOW_input_in_testcase392 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ACTION_in_testcase394 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_input406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ML_STRING_in_input421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FILENAME_in_input435 = new BitSet(new long[]{0x0000000000000002L});

}