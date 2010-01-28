// $ANTLR 3.2.1-SNAPSHOT Jan 26, 2010 15:12:28 gUnit.g 2010-01-27 16:25:16

package org.antlr.v4.gunit;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class gUnitParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SUITE", "TEST_OK", "TEST_FAIL", "TEST_RETVAL", "TEST_STDOUT", "TEST_TREE", "TEST_ACTION", "DOC_COMMENT", "ID", "OPTIONS", "STRING", "ACTION", "RETVAL", "ML_STRING", "TREE", "FILENAME", "NESTED_RETVAL", "NESTED_AST", "WS", "ID_", "SL_COMMENT", "ML_COMMENT", "XDIGIT", "'gunit'", "';'", "'}'", "'='", "'@header'", "'walks'", "':'", "'OK'", "'FAIL'", "'returns'", "'->'"
    };
    public static final int T__29=29;
    public static final int RETVAL=16;
    public static final int T__28=28;
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
    public static final int WS=22;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int TREE=18;
    public static final int T__36=36;
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


        public gUnitParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public gUnitParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return gUnitParser.tokenNames; }
    public String getGrammarFileName() { return "gUnit.g"; }


    public static class gUnitDef_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "gUnitDef"
    // gUnit.g:16:1: gUnitDef : ( DOC_COMMENT )? 'gunit' ID ';' ( optionsSpec | header )* ( testsuite )+ -> ^( 'gunit' ID ( DOC_COMMENT )? ( optionsSpec )? ( header )? ( testsuite )+ ) ;
    public final gUnitParser.gUnitDef_return gUnitDef() throws RecognitionException {
        gUnitParser.gUnitDef_return retval = new gUnitParser.gUnitDef_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token DOC_COMMENT1=null;
        Token string_literal2=null;
        Token ID3=null;
        Token char_literal4=null;
        gUnitParser.optionsSpec_return optionsSpec5 = null;

        gUnitParser.header_return header6 = null;

        gUnitParser.testsuite_return testsuite7 = null;


        CommonTree DOC_COMMENT1_tree=null;
        CommonTree string_literal2_tree=null;
        CommonTree ID3_tree=null;
        CommonTree char_literal4_tree=null;
        RewriteRuleTokenStream stream_DOC_COMMENT=new RewriteRuleTokenStream(adaptor,"token DOC_COMMENT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_27=new RewriteRuleTokenStream(adaptor,"token 27");
        RewriteRuleTokenStream stream_28=new RewriteRuleTokenStream(adaptor,"token 28");
        RewriteRuleSubtreeStream stream_optionsSpec=new RewriteRuleSubtreeStream(adaptor,"rule optionsSpec");
        RewriteRuleSubtreeStream stream_testsuite=new RewriteRuleSubtreeStream(adaptor,"rule testsuite");
        RewriteRuleSubtreeStream stream_header=new RewriteRuleSubtreeStream(adaptor,"rule header");
        try {
            // gUnit.g:17:2: ( ( DOC_COMMENT )? 'gunit' ID ';' ( optionsSpec | header )* ( testsuite )+ -> ^( 'gunit' ID ( DOC_COMMENT )? ( optionsSpec )? ( header )? ( testsuite )+ ) )
            // gUnit.g:17:4: ( DOC_COMMENT )? 'gunit' ID ';' ( optionsSpec | header )* ( testsuite )+
            {
            // gUnit.g:17:4: ( DOC_COMMENT )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==DOC_COMMENT) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // gUnit.g:17:4: DOC_COMMENT
                    {
                    DOC_COMMENT1=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_gUnitDef67);  
                    stream_DOC_COMMENT.add(DOC_COMMENT1);


                    }
                    break;

            }

            string_literal2=(Token)match(input,27,FOLLOW_27_in_gUnitDef70);  
            stream_27.add(string_literal2);

            ID3=(Token)match(input,ID,FOLLOW_ID_in_gUnitDef72);  
            stream_ID.add(ID3);

            char_literal4=(Token)match(input,28,FOLLOW_28_in_gUnitDef74);  
            stream_28.add(char_literal4);

            // gUnit.g:17:32: ( optionsSpec | header )*
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
            	    // gUnit.g:17:33: optionsSpec
            	    {
            	    pushFollow(FOLLOW_optionsSpec_in_gUnitDef77);
            	    optionsSpec5=optionsSpec();

            	    state._fsp--;

            	    stream_optionsSpec.add(optionsSpec5.getTree());

            	    }
            	    break;
            	case 2 :
            	    // gUnit.g:17:45: header
            	    {
            	    pushFollow(FOLLOW_header_in_gUnitDef79);
            	    header6=header();

            	    state._fsp--;

            	    stream_header.add(header6.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // gUnit.g:17:54: ( testsuite )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>=DOC_COMMENT && LA3_0<=ID)) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // gUnit.g:17:54: testsuite
            	    {
            	    pushFollow(FOLLOW_testsuite_in_gUnitDef83);
            	    testsuite7=testsuite();

            	    state._fsp--;

            	    stream_testsuite.add(testsuite7.getTree());

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



            // AST REWRITE
            // elements: testsuite, header, ID, 27, optionsSpec, DOC_COMMENT
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 18:6: -> ^( 'gunit' ID ( DOC_COMMENT )? ( optionsSpec )? ( header )? ( testsuite )+ )
            {
                // gUnit.g:18:9: ^( 'gunit' ID ( DOC_COMMENT )? ( optionsSpec )? ( header )? ( testsuite )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(stream_27.nextNode(), root_1);

                adaptor.addChild(root_1, stream_ID.nextNode());
                // gUnit.g:18:22: ( DOC_COMMENT )?
                if ( stream_DOC_COMMENT.hasNext() ) {
                    adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                }
                stream_DOC_COMMENT.reset();
                // gUnit.g:18:35: ( optionsSpec )?
                if ( stream_optionsSpec.hasNext() ) {
                    adaptor.addChild(root_1, stream_optionsSpec.nextTree());

                }
                stream_optionsSpec.reset();
                // gUnit.g:18:48: ( header )?
                if ( stream_header.hasNext() ) {
                    adaptor.addChild(root_1, stream_header.nextTree());

                }
                stream_header.reset();
                if ( !(stream_testsuite.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_testsuite.hasNext() ) {
                    adaptor.addChild(root_1, stream_testsuite.nextTree());

                }
                stream_testsuite.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "gUnitDef"

    public static class optionsSpec_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "optionsSpec"
    // gUnit.g:21:1: optionsSpec : OPTIONS ( option ';' )+ '}' -> ^( OPTIONS ( option )+ ) ;
    public final gUnitParser.optionsSpec_return optionsSpec() throws RecognitionException {
        gUnitParser.optionsSpec_return retval = new gUnitParser.optionsSpec_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPTIONS8=null;
        Token char_literal10=null;
        Token char_literal11=null;
        gUnitParser.option_return option9 = null;


        CommonTree OPTIONS8_tree=null;
        CommonTree char_literal10_tree=null;
        CommonTree char_literal11_tree=null;
        RewriteRuleTokenStream stream_OPTIONS=new RewriteRuleTokenStream(adaptor,"token OPTIONS");
        RewriteRuleTokenStream stream_28=new RewriteRuleTokenStream(adaptor,"token 28");
        RewriteRuleTokenStream stream_29=new RewriteRuleTokenStream(adaptor,"token 29");
        RewriteRuleSubtreeStream stream_option=new RewriteRuleSubtreeStream(adaptor,"rule option");
        try {
            // gUnit.g:22:2: ( OPTIONS ( option ';' )+ '}' -> ^( OPTIONS ( option )+ ) )
            // gUnit.g:22:4: OPTIONS ( option ';' )+ '}'
            {
            OPTIONS8=(Token)match(input,OPTIONS,FOLLOW_OPTIONS_in_optionsSpec120);  
            stream_OPTIONS.add(OPTIONS8);

            // gUnit.g:22:12: ( option ';' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==ID) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // gUnit.g:22:13: option ';'
            	    {
            	    pushFollow(FOLLOW_option_in_optionsSpec123);
            	    option9=option();

            	    state._fsp--;

            	    stream_option.add(option9.getTree());
            	    char_literal10=(Token)match(input,28,FOLLOW_28_in_optionsSpec125);  
            	    stream_28.add(char_literal10);


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

            char_literal11=(Token)match(input,29,FOLLOW_29_in_optionsSpec129);  
            stream_29.add(char_literal11);



            // AST REWRITE
            // elements: option, OPTIONS
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 22:30: -> ^( OPTIONS ( option )+ )
            {
                // gUnit.g:22:33: ^( OPTIONS ( option )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(stream_OPTIONS.nextNode(), root_1);

                if ( !(stream_option.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_option.hasNext() ) {
                    adaptor.addChild(root_1, stream_option.nextTree());

                }
                stream_option.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "optionsSpec"

    public static class option_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "option"
    // gUnit.g:25:1: option : ID '=' optionValue -> ^( '=' ID optionValue ) ;
    public final gUnitParser.option_return option() throws RecognitionException {
        gUnitParser.option_return retval = new gUnitParser.option_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ID12=null;
        Token char_literal13=null;
        gUnitParser.optionValue_return optionValue14 = null;


        CommonTree ID12_tree=null;
        CommonTree char_literal13_tree=null;
        RewriteRuleTokenStream stream_30=new RewriteRuleTokenStream(adaptor,"token 30");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_optionValue=new RewriteRuleSubtreeStream(adaptor,"rule optionValue");
        try {
            // gUnit.g:26:5: ( ID '=' optionValue -> ^( '=' ID optionValue ) )
            // gUnit.g:26:9: ID '=' optionValue
            {
            ID12=(Token)match(input,ID,FOLLOW_ID_in_option154);  
            stream_ID.add(ID12);

            char_literal13=(Token)match(input,30,FOLLOW_30_in_option156);  
            stream_30.add(char_literal13);

            pushFollow(FOLLOW_optionValue_in_option158);
            optionValue14=optionValue();

            state._fsp--;

            stream_optionValue.add(optionValue14.getTree());


            // AST REWRITE
            // elements: optionValue, 30, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 26:28: -> ^( '=' ID optionValue )
            {
                // gUnit.g:26:31: ^( '=' ID optionValue )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(stream_30.nextNode(), root_1);

                adaptor.addChild(root_1, stream_ID.nextNode());
                adaptor.addChild(root_1, stream_optionValue.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "option"

    public static class optionValue_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "optionValue"
    // gUnit.g:29:1: optionValue : ( ID | STRING );
    public final gUnitParser.optionValue_return optionValue() throws RecognitionException {
        gUnitParser.optionValue_return retval = new gUnitParser.optionValue_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set15=null;

        CommonTree set15_tree=null;

        try {
            // gUnit.g:30:5: ( ID | STRING )
            // gUnit.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set15=(Token)input.LT(1);
            if ( input.LA(1)==ID||input.LA(1)==STRING ) {
                input.consume();
                adaptor.addChild(root_0, (CommonTree)adaptor.create(set15));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "optionValue"

    public static class header_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "header"
    // gUnit.g:34:1: header : '@header' ACTION -> ^( '@header' ACTION ) ;
    public final gUnitParser.header_return header() throws RecognitionException {
        gUnitParser.header_return retval = new gUnitParser.header_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal16=null;
        Token ACTION17=null;

        CommonTree string_literal16_tree=null;
        CommonTree ACTION17_tree=null;
        RewriteRuleTokenStream stream_31=new RewriteRuleTokenStream(adaptor,"token 31");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");

        try {
            // gUnit.g:34:8: ( '@header' ACTION -> ^( '@header' ACTION ) )
            // gUnit.g:34:10: '@header' ACTION
            {
            string_literal16=(Token)match(input,31,FOLLOW_31_in_header215);  
            stream_31.add(string_literal16);

            ACTION17=(Token)match(input,ACTION,FOLLOW_ACTION_in_header217);  
            stream_ACTION.add(ACTION17);



            // AST REWRITE
            // elements: 31, ACTION
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 34:27: -> ^( '@header' ACTION )
            {
                // gUnit.g:34:30: ^( '@header' ACTION )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(stream_31.nextNode(), root_1);

                adaptor.addChild(root_1, stream_ACTION.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "header"

    public static class testsuite_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "testsuite"
    // gUnit.g:36:1: testsuite : ( ( DOC_COMMENT )? treeRule= ID 'walks' parserRule= ID ':' ( testcase )+ -> ^( SUITE $treeRule $parserRule ( DOC_COMMENT )? ( testcase )+ ) | ( DOC_COMMENT )? ID ':' ( testcase )+ -> ^( SUITE ID ( DOC_COMMENT )? ( testcase )+ ) );
    public final gUnitParser.testsuite_return testsuite() throws RecognitionException {
        gUnitParser.testsuite_return retval = new gUnitParser.testsuite_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token treeRule=null;
        Token parserRule=null;
        Token DOC_COMMENT18=null;
        Token string_literal19=null;
        Token char_literal20=null;
        Token DOC_COMMENT22=null;
        Token ID23=null;
        Token char_literal24=null;
        gUnitParser.testcase_return testcase21 = null;

        gUnitParser.testcase_return testcase25 = null;


        CommonTree treeRule_tree=null;
        CommonTree parserRule_tree=null;
        CommonTree DOC_COMMENT18_tree=null;
        CommonTree string_literal19_tree=null;
        CommonTree char_literal20_tree=null;
        CommonTree DOC_COMMENT22_tree=null;
        CommonTree ID23_tree=null;
        CommonTree char_literal24_tree=null;
        RewriteRuleTokenStream stream_DOC_COMMENT=new RewriteRuleTokenStream(adaptor,"token DOC_COMMENT");
        RewriteRuleTokenStream stream_32=new RewriteRuleTokenStream(adaptor,"token 32");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_33=new RewriteRuleTokenStream(adaptor,"token 33");
        RewriteRuleSubtreeStream stream_testcase=new RewriteRuleSubtreeStream(adaptor,"rule testcase");
        try {
            // gUnit.g:37:2: ( ( DOC_COMMENT )? treeRule= ID 'walks' parserRule= ID ':' ( testcase )+ -> ^( SUITE $treeRule $parserRule ( DOC_COMMENT )? ( testcase )+ ) | ( DOC_COMMENT )? ID ':' ( testcase )+ -> ^( SUITE ID ( DOC_COMMENT )? ( testcase )+ ) )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==DOC_COMMENT) ) {
                int LA9_1 = input.LA(2);

                if ( (LA9_1==ID) ) {
                    int LA9_2 = input.LA(3);

                    if ( (LA9_2==32) ) {
                        alt9=1;
                    }
                    else if ( (LA9_2==33) ) {
                        alt9=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA9_0==ID) ) {
                int LA9_2 = input.LA(2);

                if ( (LA9_2==32) ) {
                    alt9=1;
                }
                else if ( (LA9_2==33) ) {
                    alt9=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // gUnit.g:37:4: ( DOC_COMMENT )? treeRule= ID 'walks' parserRule= ID ':' ( testcase )+
                    {
                    // gUnit.g:37:4: ( DOC_COMMENT )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==DOC_COMMENT) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // gUnit.g:37:4: DOC_COMMENT
                            {
                            DOC_COMMENT18=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testsuite234);  
                            stream_DOC_COMMENT.add(DOC_COMMENT18);


                            }
                            break;

                    }

                    treeRule=(Token)match(input,ID,FOLLOW_ID_in_testsuite239);  
                    stream_ID.add(treeRule);

                    string_literal19=(Token)match(input,32,FOLLOW_32_in_testsuite241);  
                    stream_32.add(string_literal19);

                    parserRule=(Token)match(input,ID,FOLLOW_ID_in_testsuite245);  
                    stream_ID.add(parserRule);

                    char_literal20=(Token)match(input,33,FOLLOW_33_in_testsuite247);  
                    stream_33.add(char_literal20);

                    // gUnit.g:37:55: ( testcase )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==DOC_COMMENT) ) {
                            int LA6_2 = input.LA(2);

                            if ( (LA6_2==STRING||LA6_2==ML_STRING||LA6_2==FILENAME) ) {
                                alt6=1;
                            }


                        }
                        else if ( (LA6_0==STRING||LA6_0==ML_STRING||LA6_0==FILENAME) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // gUnit.g:37:55: testcase
                    	    {
                    	    pushFollow(FOLLOW_testcase_in_testsuite249);
                    	    testcase21=testcase();

                    	    state._fsp--;

                    	    stream_testcase.add(testcase21.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);



                    // AST REWRITE
                    // elements: parserRule, treeRule, testcase, DOC_COMMENT
                    // token labels: parserRule, treeRule
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_parserRule=new RewriteRuleTokenStream(adaptor,"token parserRule",parserRule);
                    RewriteRuleTokenStream stream_treeRule=new RewriteRuleTokenStream(adaptor,"token treeRule",treeRule);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 38:3: -> ^( SUITE $treeRule $parserRule ( DOC_COMMENT )? ( testcase )+ )
                    {
                        // gUnit.g:38:6: ^( SUITE $treeRule $parserRule ( DOC_COMMENT )? ( testcase )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUITE, "SUITE"), root_1);

                        adaptor.addChild(root_1, stream_treeRule.nextNode());
                        adaptor.addChild(root_1, stream_parserRule.nextNode());
                        // gUnit.g:38:36: ( DOC_COMMENT )?
                        if ( stream_DOC_COMMENT.hasNext() ) {
                            adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                        }
                        stream_DOC_COMMENT.reset();
                        if ( !(stream_testcase.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_testcase.hasNext() ) {
                            adaptor.addChild(root_1, stream_testcase.nextTree());

                        }
                        stream_testcase.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // gUnit.g:39:4: ( DOC_COMMENT )? ID ':' ( testcase )+
                    {
                    // gUnit.g:39:4: ( DOC_COMMENT )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==DOC_COMMENT) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // gUnit.g:39:4: DOC_COMMENT
                            {
                            DOC_COMMENT22=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testsuite275);  
                            stream_DOC_COMMENT.add(DOC_COMMENT22);


                            }
                            break;

                    }

                    ID23=(Token)match(input,ID,FOLLOW_ID_in_testsuite278);  
                    stream_ID.add(ID23);

                    char_literal24=(Token)match(input,33,FOLLOW_33_in_testsuite280);  
                    stream_33.add(char_literal24);

                    // gUnit.g:39:24: ( testcase )+
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0==DOC_COMMENT) ) {
                            int LA8_2 = input.LA(2);

                            if ( (LA8_2==STRING||LA8_2==ML_STRING||LA8_2==FILENAME) ) {
                                alt8=1;
                            }


                        }
                        else if ( (LA8_0==STRING||LA8_0==ML_STRING||LA8_0==FILENAME) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // gUnit.g:39:24: testcase
                    	    {
                    	    pushFollow(FOLLOW_testcase_in_testsuite282);
                    	    testcase25=testcase();

                    	    state._fsp--;

                    	    stream_testcase.add(testcase25.getTree());

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



                    // AST REWRITE
                    // elements: DOC_COMMENT, ID, testcase
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 39:34: -> ^( SUITE ID ( DOC_COMMENT )? ( testcase )+ )
                    {
                        // gUnit.g:39:37: ^( SUITE ID ( DOC_COMMENT )? ( testcase )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUITE, "SUITE"), root_1);

                        adaptor.addChild(root_1, stream_ID.nextNode());
                        // gUnit.g:39:48: ( DOC_COMMENT )?
                        if ( stream_DOC_COMMENT.hasNext() ) {
                            adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                        }
                        stream_DOC_COMMENT.reset();
                        if ( !(stream_testcase.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_testcase.hasNext() ) {
                            adaptor.addChild(root_1, stream_testcase.nextTree());

                        }
                        stream_testcase.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "testsuite"

    public static class testcase_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "testcase"
    // gUnit.g:42:1: testcase : ( ( DOC_COMMENT )? input 'OK' -> ^( TEST_OK ( DOC_COMMENT )? input ) | ( DOC_COMMENT )? input 'FAIL' -> ^( TEST_FAIL ( DOC_COMMENT )? input ) | ( DOC_COMMENT )? input 'returns' RETVAL -> ^( TEST_RETVAL ( DOC_COMMENT )? input RETVAL ) | ( DOC_COMMENT )? input '->' STRING -> ^( TEST_STDOUT ( DOC_COMMENT )? input STRING ) | ( DOC_COMMENT )? input '->' ML_STRING -> ^( TEST_STDOUT ( DOC_COMMENT )? input ML_STRING ) | ( DOC_COMMENT )? input '->' TREE -> ^( TEST_TREE ( DOC_COMMENT )? input TREE ) | ( DOC_COMMENT )? input '->' ACTION -> ^( TEST_ACTION ( DOC_COMMENT )? input ACTION ) );
    public final gUnitParser.testcase_return testcase() throws RecognitionException {
        gUnitParser.testcase_return retval = new gUnitParser.testcase_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token DOC_COMMENT26=null;
        Token string_literal28=null;
        Token DOC_COMMENT29=null;
        Token string_literal31=null;
        Token DOC_COMMENT32=null;
        Token string_literal34=null;
        Token RETVAL35=null;
        Token DOC_COMMENT36=null;
        Token string_literal38=null;
        Token STRING39=null;
        Token DOC_COMMENT40=null;
        Token string_literal42=null;
        Token ML_STRING43=null;
        Token DOC_COMMENT44=null;
        Token string_literal46=null;
        Token TREE47=null;
        Token DOC_COMMENT48=null;
        Token string_literal50=null;
        Token ACTION51=null;
        gUnitParser.input_return input27 = null;

        gUnitParser.input_return input30 = null;

        gUnitParser.input_return input33 = null;

        gUnitParser.input_return input37 = null;

        gUnitParser.input_return input41 = null;

        gUnitParser.input_return input45 = null;

        gUnitParser.input_return input49 = null;


        CommonTree DOC_COMMENT26_tree=null;
        CommonTree string_literal28_tree=null;
        CommonTree DOC_COMMENT29_tree=null;
        CommonTree string_literal31_tree=null;
        CommonTree DOC_COMMENT32_tree=null;
        CommonTree string_literal34_tree=null;
        CommonTree RETVAL35_tree=null;
        CommonTree DOC_COMMENT36_tree=null;
        CommonTree string_literal38_tree=null;
        CommonTree STRING39_tree=null;
        CommonTree DOC_COMMENT40_tree=null;
        CommonTree string_literal42_tree=null;
        CommonTree ML_STRING43_tree=null;
        CommonTree DOC_COMMENT44_tree=null;
        CommonTree string_literal46_tree=null;
        CommonTree TREE47_tree=null;
        CommonTree DOC_COMMENT48_tree=null;
        CommonTree string_literal50_tree=null;
        CommonTree ACTION51_tree=null;
        RewriteRuleTokenStream stream_DOC_COMMENT=new RewriteRuleTokenStream(adaptor,"token DOC_COMMENT");
        RewriteRuleTokenStream stream_RETVAL=new RewriteRuleTokenStream(adaptor,"token RETVAL");
        RewriteRuleTokenStream stream_TREE=new RewriteRuleTokenStream(adaptor,"token TREE");
        RewriteRuleTokenStream stream_35=new RewriteRuleTokenStream(adaptor,"token 35");
        RewriteRuleTokenStream stream_36=new RewriteRuleTokenStream(adaptor,"token 36");
        RewriteRuleTokenStream stream_ML_STRING=new RewriteRuleTokenStream(adaptor,"token ML_STRING");
        RewriteRuleTokenStream stream_34=new RewriteRuleTokenStream(adaptor,"token 34");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleTokenStream stream_37=new RewriteRuleTokenStream(adaptor,"token 37");
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
        RewriteRuleSubtreeStream stream_input=new RewriteRuleSubtreeStream(adaptor,"rule input");
        try {
            // gUnit.g:43:2: ( ( DOC_COMMENT )? input 'OK' -> ^( TEST_OK ( DOC_COMMENT )? input ) | ( DOC_COMMENT )? input 'FAIL' -> ^( TEST_FAIL ( DOC_COMMENT )? input ) | ( DOC_COMMENT )? input 'returns' RETVAL -> ^( TEST_RETVAL ( DOC_COMMENT )? input RETVAL ) | ( DOC_COMMENT )? input '->' STRING -> ^( TEST_STDOUT ( DOC_COMMENT )? input STRING ) | ( DOC_COMMENT )? input '->' ML_STRING -> ^( TEST_STDOUT ( DOC_COMMENT )? input ML_STRING ) | ( DOC_COMMENT )? input '->' TREE -> ^( TEST_TREE ( DOC_COMMENT )? input TREE ) | ( DOC_COMMENT )? input '->' ACTION -> ^( TEST_ACTION ( DOC_COMMENT )? input ACTION ) )
            int alt17=7;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // gUnit.g:43:4: ( DOC_COMMENT )? input 'OK'
                    {
                    // gUnit.g:43:4: ( DOC_COMMENT )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==DOC_COMMENT) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // gUnit.g:43:4: DOC_COMMENT
                            {
                            DOC_COMMENT26=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase308);  
                            stream_DOC_COMMENT.add(DOC_COMMENT26);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_input_in_testcase311);
                    input27=input();

                    state._fsp--;

                    stream_input.add(input27.getTree());
                    string_literal28=(Token)match(input,34,FOLLOW_34_in_testcase313);  
                    stream_34.add(string_literal28);



                    // AST REWRITE
                    // elements: DOC_COMMENT, input
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 43:31: -> ^( TEST_OK ( DOC_COMMENT )? input )
                    {
                        // gUnit.g:43:34: ^( TEST_OK ( DOC_COMMENT )? input )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(TEST_OK, "TEST_OK"), root_1);

                        // gUnit.g:43:44: ( DOC_COMMENT )?
                        if ( stream_DOC_COMMENT.hasNext() ) {
                            adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                        }
                        stream_DOC_COMMENT.reset();
                        adaptor.addChild(root_1, stream_input.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // gUnit.g:44:4: ( DOC_COMMENT )? input 'FAIL'
                    {
                    // gUnit.g:44:4: ( DOC_COMMENT )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==DOC_COMMENT) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // gUnit.g:44:4: DOC_COMMENT
                            {
                            DOC_COMMENT29=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase332);  
                            stream_DOC_COMMENT.add(DOC_COMMENT29);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_input_in_testcase335);
                    input30=input();

                    state._fsp--;

                    stream_input.add(input30.getTree());
                    string_literal31=(Token)match(input,35,FOLLOW_35_in_testcase337);  
                    stream_35.add(string_literal31);



                    // AST REWRITE
                    // elements: DOC_COMMENT, input
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 44:33: -> ^( TEST_FAIL ( DOC_COMMENT )? input )
                    {
                        // gUnit.g:44:36: ^( TEST_FAIL ( DOC_COMMENT )? input )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(TEST_FAIL, "TEST_FAIL"), root_1);

                        // gUnit.g:44:48: ( DOC_COMMENT )?
                        if ( stream_DOC_COMMENT.hasNext() ) {
                            adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                        }
                        stream_DOC_COMMENT.reset();
                        adaptor.addChild(root_1, stream_input.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // gUnit.g:45:4: ( DOC_COMMENT )? input 'returns' RETVAL
                    {
                    // gUnit.g:45:4: ( DOC_COMMENT )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==DOC_COMMENT) ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // gUnit.g:45:4: DOC_COMMENT
                            {
                            DOC_COMMENT32=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase356);  
                            stream_DOC_COMMENT.add(DOC_COMMENT32);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_input_in_testcase359);
                    input33=input();

                    state._fsp--;

                    stream_input.add(input33.getTree());
                    string_literal34=(Token)match(input,36,FOLLOW_36_in_testcase361);  
                    stream_36.add(string_literal34);

                    RETVAL35=(Token)match(input,RETVAL,FOLLOW_RETVAL_in_testcase363);  
                    stream_RETVAL.add(RETVAL35);



                    // AST REWRITE
                    // elements: input, DOC_COMMENT, RETVAL
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 45:40: -> ^( TEST_RETVAL ( DOC_COMMENT )? input RETVAL )
                    {
                        // gUnit.g:45:43: ^( TEST_RETVAL ( DOC_COMMENT )? input RETVAL )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(TEST_RETVAL, "TEST_RETVAL"), root_1);

                        // gUnit.g:45:57: ( DOC_COMMENT )?
                        if ( stream_DOC_COMMENT.hasNext() ) {
                            adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                        }
                        stream_DOC_COMMENT.reset();
                        adaptor.addChild(root_1, stream_input.nextTree());
                        adaptor.addChild(root_1, stream_RETVAL.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 4 :
                    // gUnit.g:46:4: ( DOC_COMMENT )? input '->' STRING
                    {
                    // gUnit.g:46:4: ( DOC_COMMENT )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==DOC_COMMENT) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // gUnit.g:46:4: DOC_COMMENT
                            {
                            DOC_COMMENT36=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase381);  
                            stream_DOC_COMMENT.add(DOC_COMMENT36);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_input_in_testcase384);
                    input37=input();

                    state._fsp--;

                    stream_input.add(input37.getTree());
                    string_literal38=(Token)match(input,37,FOLLOW_37_in_testcase386);  
                    stream_37.add(string_literal38);

                    STRING39=(Token)match(input,STRING,FOLLOW_STRING_in_testcase388);  
                    stream_STRING.add(STRING39);



                    // AST REWRITE
                    // elements: STRING, input, DOC_COMMENT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 46:36: -> ^( TEST_STDOUT ( DOC_COMMENT )? input STRING )
                    {
                        // gUnit.g:46:39: ^( TEST_STDOUT ( DOC_COMMENT )? input STRING )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(TEST_STDOUT, "TEST_STDOUT"), root_1);

                        // gUnit.g:46:53: ( DOC_COMMENT )?
                        if ( stream_DOC_COMMENT.hasNext() ) {
                            adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                        }
                        stream_DOC_COMMENT.reset();
                        adaptor.addChild(root_1, stream_input.nextTree());
                        adaptor.addChild(root_1, stream_STRING.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 5 :
                    // gUnit.g:47:4: ( DOC_COMMENT )? input '->' ML_STRING
                    {
                    // gUnit.g:47:4: ( DOC_COMMENT )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==DOC_COMMENT) ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // gUnit.g:47:4: DOC_COMMENT
                            {
                            DOC_COMMENT40=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase407);  
                            stream_DOC_COMMENT.add(DOC_COMMENT40);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_input_in_testcase410);
                    input41=input();

                    state._fsp--;

                    stream_input.add(input41.getTree());
                    string_literal42=(Token)match(input,37,FOLLOW_37_in_testcase412);  
                    stream_37.add(string_literal42);

                    ML_STRING43=(Token)match(input,ML_STRING,FOLLOW_ML_STRING_in_testcase414);  
                    stream_ML_STRING.add(ML_STRING43);



                    // AST REWRITE
                    // elements: DOC_COMMENT, ML_STRING, input
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 47:38: -> ^( TEST_STDOUT ( DOC_COMMENT )? input ML_STRING )
                    {
                        // gUnit.g:47:41: ^( TEST_STDOUT ( DOC_COMMENT )? input ML_STRING )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(TEST_STDOUT, "TEST_STDOUT"), root_1);

                        // gUnit.g:47:55: ( DOC_COMMENT )?
                        if ( stream_DOC_COMMENT.hasNext() ) {
                            adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                        }
                        stream_DOC_COMMENT.reset();
                        adaptor.addChild(root_1, stream_input.nextTree());
                        adaptor.addChild(root_1, stream_ML_STRING.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 6 :
                    // gUnit.g:48:4: ( DOC_COMMENT )? input '->' TREE
                    {
                    // gUnit.g:48:4: ( DOC_COMMENT )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==DOC_COMMENT) ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // gUnit.g:48:4: DOC_COMMENT
                            {
                            DOC_COMMENT44=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase432);  
                            stream_DOC_COMMENT.add(DOC_COMMENT44);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_input_in_testcase435);
                    input45=input();

                    state._fsp--;

                    stream_input.add(input45.getTree());
                    string_literal46=(Token)match(input,37,FOLLOW_37_in_testcase437);  
                    stream_37.add(string_literal46);

                    TREE47=(Token)match(input,TREE,FOLLOW_TREE_in_testcase439);  
                    stream_TREE.add(TREE47);



                    // AST REWRITE
                    // elements: input, TREE, DOC_COMMENT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 48:34: -> ^( TEST_TREE ( DOC_COMMENT )? input TREE )
                    {
                        // gUnit.g:48:37: ^( TEST_TREE ( DOC_COMMENT )? input TREE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(TEST_TREE, "TEST_TREE"), root_1);

                        // gUnit.g:48:49: ( DOC_COMMENT )?
                        if ( stream_DOC_COMMENT.hasNext() ) {
                            adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                        }
                        stream_DOC_COMMENT.reset();
                        adaptor.addChild(root_1, stream_input.nextTree());
                        adaptor.addChild(root_1, stream_TREE.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 7 :
                    // gUnit.g:49:4: ( DOC_COMMENT )? input '->' ACTION
                    {
                    // gUnit.g:49:4: ( DOC_COMMENT )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==DOC_COMMENT) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // gUnit.g:49:4: DOC_COMMENT
                            {
                            DOC_COMMENT48=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_testcase458);  
                            stream_DOC_COMMENT.add(DOC_COMMENT48);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_input_in_testcase461);
                    input49=input();

                    state._fsp--;

                    stream_input.add(input49.getTree());
                    string_literal50=(Token)match(input,37,FOLLOW_37_in_testcase463);  
                    stream_37.add(string_literal50);

                    ACTION51=(Token)match(input,ACTION,FOLLOW_ACTION_in_testcase465);  
                    stream_ACTION.add(ACTION51);



                    // AST REWRITE
                    // elements: ACTION, input, DOC_COMMENT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 49:36: -> ^( TEST_ACTION ( DOC_COMMENT )? input ACTION )
                    {
                        // gUnit.g:49:39: ^( TEST_ACTION ( DOC_COMMENT )? input ACTION )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(TEST_ACTION, "TEST_ACTION"), root_1);

                        // gUnit.g:49:53: ( DOC_COMMENT )?
                        if ( stream_DOC_COMMENT.hasNext() ) {
                            adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                        }
                        stream_DOC_COMMENT.reset();
                        adaptor.addChild(root_1, stream_input.nextTree());
                        adaptor.addChild(root_1, stream_ACTION.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "testcase"

    public static class input_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "input"
    // gUnit.g:52:1: input : ( STRING | ML_STRING | FILENAME );
    public final gUnitParser.input_return input() throws RecognitionException {
        gUnitParser.input_return retval = new gUnitParser.input_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set52=null;

        CommonTree set52_tree=null;

        try {
            // gUnit.g:53:2: ( STRING | ML_STRING | FILENAME )
            // gUnit.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set52=(Token)input.LT(1);
            if ( input.LA(1)==STRING||input.LA(1)==ML_STRING||input.LA(1)==FILENAME ) {
                input.consume();
                adaptor.addChild(root_0, (CommonTree)adaptor.create(set52));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "input"

    // Delegated rules


    protected DFA17 dfa17 = new DFA17(this);
    static final String DFA17_eotS =
        "\13\uffff";
    static final String DFA17_eofS =
        "\13\uffff";
    static final String DFA17_minS =
        "\1\13\1\16\1\42\2\uffff\1\16\5\uffff";
    static final String DFA17_maxS =
        "\2\23\1\45\2\uffff\1\22\5\uffff";
    static final String DFA17_acceptS =
        "\3\uffff\1\3\1\1\1\uffff\1\2\1\5\1\4\1\6\1\7";
    static final String DFA17_specialS =
        "\13\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\1\2\uffff\1\2\2\uffff\1\2\1\uffff\1\2",
            "\1\2\2\uffff\1\2\1\uffff\1\2",
            "\1\4\1\6\1\3\1\5",
            "",
            "",
            "\1\10\1\12\1\uffff\1\7\1\11",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "42:1: testcase : ( ( DOC_COMMENT )? input 'OK' -> ^( TEST_OK ( DOC_COMMENT )? input ) | ( DOC_COMMENT )? input 'FAIL' -> ^( TEST_FAIL ( DOC_COMMENT )? input ) | ( DOC_COMMENT )? input 'returns' RETVAL -> ^( TEST_RETVAL ( DOC_COMMENT )? input RETVAL ) | ( DOC_COMMENT )? input '->' STRING -> ^( TEST_STDOUT ( DOC_COMMENT )? input STRING ) | ( DOC_COMMENT )? input '->' ML_STRING -> ^( TEST_STDOUT ( DOC_COMMENT )? input ML_STRING ) | ( DOC_COMMENT )? input '->' TREE -> ^( TEST_TREE ( DOC_COMMENT )? input TREE ) | ( DOC_COMMENT )? input '->' ACTION -> ^( TEST_ACTION ( DOC_COMMENT )? input ACTION ) );";
        }
    }
 

    public static final BitSet FOLLOW_DOC_COMMENT_in_gUnitDef67 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_gUnitDef70 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_gUnitDef72 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_gUnitDef74 = new BitSet(new long[]{0x0000000080003800L});
    public static final BitSet FOLLOW_optionsSpec_in_gUnitDef77 = new BitSet(new long[]{0x0000000080003800L});
    public static final BitSet FOLLOW_header_in_gUnitDef79 = new BitSet(new long[]{0x0000000080003800L});
    public static final BitSet FOLLOW_testsuite_in_gUnitDef83 = new BitSet(new long[]{0x0000000080003802L});
    public static final BitSet FOLLOW_OPTIONS_in_optionsSpec120 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_option_in_optionsSpec123 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_optionsSpec125 = new BitSet(new long[]{0x0000000020001000L});
    public static final BitSet FOLLOW_29_in_optionsSpec129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_option154 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_option156 = new BitSet(new long[]{0x0000000000005000L});
    public static final BitSet FOLLOW_optionValue_in_option158 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_optionValue0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_header215 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_ACTION_in_header217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testsuite234 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_testsuite239 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_testsuite241 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_testsuite245 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_testsuite247 = new BitSet(new long[]{0x00000000000A4800L});
    public static final BitSet FOLLOW_testcase_in_testsuite249 = new BitSet(new long[]{0x00000000000A4802L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testsuite275 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_testsuite278 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_testsuite280 = new BitSet(new long[]{0x00000000000A4800L});
    public static final BitSet FOLLOW_testcase_in_testsuite282 = new BitSet(new long[]{0x00000000000A4802L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase308 = new BitSet(new long[]{0x00000000000A4800L});
    public static final BitSet FOLLOW_input_in_testcase311 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_testcase313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase332 = new BitSet(new long[]{0x00000000000A4800L});
    public static final BitSet FOLLOW_input_in_testcase335 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_testcase337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase356 = new BitSet(new long[]{0x00000000000A4800L});
    public static final BitSet FOLLOW_input_in_testcase359 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_testcase361 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RETVAL_in_testcase363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase381 = new BitSet(new long[]{0x00000000000A4800L});
    public static final BitSet FOLLOW_input_in_testcase384 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_testcase386 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_STRING_in_testcase388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase407 = new BitSet(new long[]{0x00000000000A4800L});
    public static final BitSet FOLLOW_input_in_testcase410 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_testcase412 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_ML_STRING_in_testcase414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase432 = new BitSet(new long[]{0x00000000000A4800L});
    public static final BitSet FOLLOW_input_in_testcase435 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_testcase437 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_TREE_in_testcase439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_testcase458 = new BitSet(new long[]{0x00000000000A4800L});
    public static final BitSet FOLLOW_input_in_testcase461 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_testcase463 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_ACTION_in_testcase465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_input0 = new BitSet(new long[]{0x0000000000000002L});

}