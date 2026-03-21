// #![feature(try_blocks)]
// #![feature(inner_deref)]
// #![feature(specialization)]
// #![feature(coerce_unsized)]
//! Integration tests

// #[macro_use]
// extern crate lazy_static;

mod gen {
    use std::fmt::Write;
    use std::iter::FromIterator;

    use dbt_antlr4::common_token_stream::CommonTokenStream;
    use dbt_antlr4::errors::ANTLRError;
    use dbt_antlr4::int_stream::IntStream;
    use dbt_antlr4::lexer::Lexer;

    use csvlexer::*;
    use csvlistener::*;
    use csvparser::CSVParser;
    use dbt_antlr4::parser_rule_context::ParserRuleContext as _;
    use dbt_antlr4::recognizer::Recognizer;
    use dbt_antlr4::rule_context::RuleContext as _;
    use dbt_antlr4::token::{Token, TOKEN_EOF};
    use dbt_antlr4::token_factory::CommonTokenFactory;
    use dbt_antlr4::token_stream::{TokenStream, UnbufferedTokenStream};
    use dbt_antlr4::tree::{ParseTreeListener, TerminalNode};
    use dbt_antlr4::trees::string_tree;
    use dbt_antlr4::{Arena, InputStream, Parser};
    use referencetoatnlexer::ReferenceToATNLexer;
    use referencetoatnlistener::ReferenceToATNListener;
    use referencetoatnparser::ReferenceToATNParser;
    use xmllexer::XMLLexer;

    use crate::gen::csvparser::CSVParserContextNode;

    use crate::gen::labelslexer::LabelsLexer;
    use crate::gen::labelsparser::{EContextAll, LabelsParser};
    use crate::gen::referencetoatnparser::ReferenceToATNParserContextNode;
    use crate::gen::simplelrlexer::SimpleLRLexer;
    use crate::gen::simplelrlistener::SimpleLRListener;
    use crate::gen::simplelrparser::{
        SimpleLRParser, SimpleLRParserContextNode, SimpleLRTreeWalker,
    };

    mod csvlexer;
    mod csvlistener;
    mod csvparser;
    mod csvvisitor;
    mod referencetoatnlexer;
    mod referencetoatnlistener;
    mod referencetoatnparser;
    mod simplelrlexer;
    mod simplelrlistener;
    mod simplelrparser;
    mod visitorcalclexer;
    mod visitorcalclistener;
    mod visitorcalcparser;
    mod visitorcalcvisitor;
    mod xmllexer;

    #[test]
    fn lexer_test_xml() -> std::io::Result<()> {
        let data = r#"<?xml version="1.0"?>
<!--comment-->>
<?xml-stylesheet type="text/css" href="nutrition.css"?>
<script>
<![CDATA[
function f(x) {
if (x < x && a > 0) then duh
}
]]>
</script>"#
            .to_owned();

        Arena::with(|arena| {
            let mut _lexer =
                XMLLexer::<_, CommonTokenFactory>::new(arena, InputStream::new(&*data));
            //        _lexer.base.add_error_listener();
            let _a = "a".to_owned() + "";
            let mut string = String::new();
            {
                let mut token_source = UnbufferedTokenStream::new_unbuffered(&mut _lexer);
                while token_source.la(1) != TOKEN_EOF {
                    {
                        let token = token_source.lt(1).unwrap();

                        let len =
                            token.get_stop_index() as usize + 1 - token.get_start_index() as usize;
                        string.extend(
                            format!(
                                "{},len {}:\n{}\n",
                                xmllexer::_SYMBOLIC_NAMES[token.get_token_type() as usize]
                                    .unwrap_or(&format!("{}", token.get_token_type())),
                                len,
                                String::from_iter(
                                    data.chars()
                                        .skip(token.get_start_index() as usize)
                                        .take(len)
                                )
                            )
                            .chars(),
                        );
                    }
                    token_source.consume();
                }
            }
            println!("{}", string);
            println!(
                "{}",
                _lexer
                    .get_interpreter()
                    .unwrap()
                    .get_dfa()
                    .to_lexer_string()
            );
        });
        Ok(())
    }

    #[test]
    fn lexer_test_csv() {
        println!("test started lexer_test_csv");
        Arena::with(|arena| {
            let mut _lexer = CSVLexer::<_, CommonTokenFactory>::new(
                arena,
                InputStream::new("V123,V2\nd1,d222"),
                // Box::new(UTF16InputStream::from_str("V123,V2\nd1,d222","".into())),
            );
            let mut token_source = UnbufferedTokenStream::new_buffered(_lexer);
            let mut token_source_iter = token_source.token_iter();
            assert_eq!(
                token_source_iter.next().unwrap().to_string(),
                "[@0,0:3='V123',<5>,1:0]"
            );
            assert_eq!(
                token_source_iter.next().unwrap().to_string(),
                "[@1,4:4=',',<1>,1:4]"
            );
            assert_eq!(
                token_source_iter.next().unwrap().to_string(),
                "[@2,5:6='V2',<5>,1:5]"
            );
            assert_eq!(
                token_source_iter.next().unwrap().to_string(),
                "[@3,7:7='\\n',<3>,1:7]"
            );
            assert_eq!(
                token_source_iter.next().unwrap().to_string(),
                "[@4,8:9='d1',<5>,2:0]"
            );
            assert_eq!(
                token_source_iter.next().unwrap().to_string(),
                "[@5,10:10=',',<1>,2:2]"
            );
            assert_eq!(
                token_source_iter.next().unwrap().to_string(),
                "[@6,11:14='d222',<5>,2:3]"
            );
            assert_eq!(
                token_source_iter.next().unwrap().to_string(),
                "[@7,15:14='<EOF>',<-1>,2:7]"
            );
            assert!(token_source_iter.next().is_none());
        });
    }

    struct Listener {}

    impl<'input, 'arena> ParseTreeListener<'input, 'arena, CSVParserContextNode<'input, 'arena>>
        for Listener
    {
        fn enter_every_rule(
            &mut self,
            ctx: &CSVParserContextNode<'input, 'arena>,
        ) -> Result<(), ANTLRError> {
            println!(
                "rule entered {}",
                csvparser::ruleNames
                    .get(ctx.get_rule_index())
                    .unwrap_or(&"error"),
            );
            Ok(())
        }
    }

    impl<'input, 'arena> CSVListener<'input, 'arena> for Listener where 'input: 'arena {}

    #[test]
    fn parser_test_csv() {
        println!("test started");
        Arena::with(|arena| {
            let mut _lexer =
                CSVLexer::<_, CommonTokenFactory>::new(arena, InputStream::new("V123,V2\nd1,d2\n"));
            let token_source = CommonTokenStream::new(_lexer);
            let mut parser = CSVParser::new(arena, token_source);
            parser.add_parse_listener(Box::new(Listener {}));
            println!("\nstart parsing parser_test_csv");
            let result = parser.csvFile();
            assert!(result.is_ok());
            assert_eq!(
            string_tree(result.unwrap(), parser.get_rule_names()),
            "(csvFile (hdr (row (field V123) , (field V2) \\n)) (row (field d1) , (field d2) \\n))"
        );
        });
    }

    struct Listener2 {}

    impl<'input, 'arena>
        ParseTreeListener<'input, 'arena, ReferenceToATNParserContextNode<'input, 'arena>>
        for Listener2
    {
        fn enter_every_rule(
            &mut self,
            ctx: &ReferenceToATNParserContextNode<'input, 'arena>,
        ) -> Result<(), ANTLRError> {
            println!(
                "rule entered {}",
                referencetoatnparser::ruleNames
                    .get(ctx.get_rule_index())
                    .unwrap_or(&"error")
            );
            Ok(())
        }
    }

    impl<'input, 'arena> ReferenceToATNListener<'input, 'arena> for Listener2 where 'input: 'arena {}

    #[test]
    fn test_adaptive_predict_and_tree() {
        let text = "a 34 b".to_owned();
        Arena::with(|arena| {
            let mut _lexer = ReferenceToATNLexer::<_, CommonTokenFactory>::new(
                arena,
                InputStream::new(text.as_str()),
            );
            let token_source = CommonTokenStream::new(_lexer);
            let mut parser = ReferenceToATNParser::new(arena, token_source);
            parser.add_parse_listener(Box::new(Listener2 {}));
            println!("\nstart parsing adaptive_predict_test");
            let result = parser.a();
            assert!(result.is_ok());
        });
    }

    struct Listener3;

    impl<'input, 'arena>
        ParseTreeListener<'input, 'arena, SimpleLRParserContextNode<'input, 'arena>> for Listener3
    {
        fn visit_terminal(
            &mut self,
            node: &TerminalNode<'input, 'arena>,
        ) -> Result<(), ANTLRError> {
            println!("terminal node {}", node.symbol.get_text());
            Ok(())
        }

        fn enter_every_rule(
            &mut self,
            ctx: &SimpleLRParserContextNode<'input, 'arena>,
        ) -> Result<(), ANTLRError> {
            println!(
                "rule entered {}",
                simplelrparser::ruleNames
                    .get(ctx.get_rule_index())
                    .unwrap_or(&"error")
            );
            Ok(())
        }

        fn exit_every_rule(
            &mut self,
            ctx: &SimpleLRParserContextNode<'input, 'arena>,
        ) -> Result<(), ANTLRError> {
            println!(
                "rule exited {}",
                simplelrparser::ruleNames
                    .get(ctx.get_rule_index())
                    .unwrap_or(&"error")
            );
            Ok(())
        }
    }

    impl<'input, 'arena> SimpleLRListener<'input, 'arena> for Listener3 where 'input: 'arena {}

    #[test]
    fn test_lr() {
        Arena::with(|arena| {
            let _lexer =
                SimpleLRLexer::<_, CommonTokenFactory>::new(arena, InputStream::new("x y z"));
            let token_source = CommonTokenStream::new(_lexer);
            let mut parser = SimpleLRParser::new(arena, token_source);
            parser.add_parse_listener(Box::new(Listener3));
            println!("\nstart parsing lr_test");
            let result = parser.s().expect("failed recursion parsion");
            assert_eq!(
                string_tree(result, parser.get_rule_names()),
                "(s (a (a (a x) y) z))"
            );
        });
    }

    #[test]
    fn test_immediate_lr() {
        Arena::with(|arena| {
            let _lexer =
                SimpleLRLexer::<_, CommonTokenFactory>::new(arena, InputStream::new("x y z"));
            let token_source = CommonTokenStream::new(_lexer);
            let mut parser = SimpleLRParser::new(arena, token_source);
            parser.add_parse_listener(Box::new(Listener3));
            println!("\nstart parsing lr_test");
            let result = parser.a().expect("failed immediate recursion parsing");
            assert_eq!(
                string_tree(result, parser.get_rule_names()),
                "(a (a (a x) y) z)"
            );
        });
    }

    struct Listener4 {
        data: String,
    }

    impl<'input, 'arena>
        ParseTreeListener<'input, 'arena, SimpleLRParserContextNode<'input, 'arena>> for Listener4
    {
        fn visit_terminal(
            &mut self,
            node: &TerminalNode<'input, 'arena>,
        ) -> Result<(), ANTLRError> {
            println!("enter terminal");
            let _ = writeln!(&mut self.data, "terminal node {}", node.symbol.get_text());
            Ok(())
        }
        fn enter_every_rule(
            &mut self,
            ctx: &SimpleLRParserContextNode<'input, 'arena>,
        ) -> Result<(), ANTLRError> {
            println!(
                "rule entered {}",
                simplelrparser::ruleNames
                    .get(ctx.get_rule_index())
                    .unwrap_or(&"error")
            );
            Ok(())
        }
    }

    impl<'input, 'arena> SimpleLRListener<'input, 'arena> for Listener4 where 'input: 'arena {}

    #[test]
    fn test_remove_listener() {
        Arena::with(|arena| {
            let mut _lexer =
                SimpleLRLexer::<_, CommonTokenFactory>::new(arena, InputStream::new("x y z"));
            let token_source = CommonTokenStream::new(_lexer);
            let mut parser = SimpleLRParser::new(arena, token_source);
            parser.add_parse_listener(Box::new(Listener3));
            let id = parser.add_parse_listener(Box::new(Listener4 {
                data: String::new(),
            }));
            let result = parser.s().expect("expected to parse successfully");

            let mut listener = parser.remove_parse_listener(id);
            assert_eq!(
                &listener.data,
                "terminal node x\nterminal node y\nterminal node z\n"
            );

            println!("--------");
            listener.data.clear();

            let listener = SimpleLRTreeWalker::walk(listener, result);
            assert_eq!(
                &listener.unwrap().data,
                "terminal node x\nterminal node y\nterminal node z\n"
            );
        });
    }
    #[test]
    fn test_byte_parser() {}

    mod labelslexer;
    mod labelslistener;
    mod labelsparser;

    #[test]
    fn test_complex_convert() {
        let input = "(a+4)*2";
        // let codepoints = "(a+4)*2";
        Arena::with(|arena| {
            let input = InputStream::new(input);
            let lexer = LabelsLexer::<_, CommonTokenFactory>::new(arena, input);
            let token_source = CommonTokenStream::new(lexer);
            let mut parser = LabelsParser::new(arena, token_source);
            let result = parser.s().expect("parser error");
            let string = result.q.as_ref().unwrap().get_v();
            assert_eq!("* + a 4 2", string);
            let x = result.q.as_deref().unwrap();
            match x {
                EContextAll::MultContext(x) => {
                    assert_eq!("(a+4)", x.a.as_ref().unwrap().get_text())
                }
                _ => panic!("oops"),
            }
        });
    }

    #[test]
    fn test_ast_type_variance() {
        // This is a compile-only test to make sure that the lifetimes on the
        // AST types are working properly
        #![allow(dead_code)]

        fn context_check1<'long, 'short, 'arena>(
            x: visitorcalcparser::ExprContextAll<'long, 'arena>,
        ) -> visitorcalcparser::ExprContextAll<'short, 'arena>
        where
            'long: 'short,
            'short: 'arena,
        {
            x
        }

        fn context_check2<'input, 'long, 'short>(
            x: visitorcalcparser::ExprContextAll<'input, 'long>,
        ) -> visitorcalcparser::ExprContextAll<'input, 'short>
        where
            'input: 'long,
            'long: 'short,
        {
            x
        }

        fn node_check1<'long, 'short, 'arena>(
            x: visitorcalcparser::VisitorCalcParserContextNode<'long, 'arena>,
        ) -> visitorcalcparser::VisitorCalcParserContextNode<'short, 'arena>
        where
            'long: 'short,
            'short: 'arena,
        {
            x
        }

        fn node_check2<'input, 'long, 'short>(
            x: visitorcalcparser::VisitorCalcParserContextNode<'input, 'long>,
        ) -> visitorcalcparser::VisitorCalcParserContextNode<'input, 'short>
        where
            'input: 'long,
            'long: 'short,
        {
            x
        }

        fn parse_visitor_calc<'input, 'arena>(
            input: &'input str,
            arena: &'arena Arena,
        ) -> Result<&'arena visitorcalcparser::SContext<'input, 'arena>, ANTLRError>
        where
            'input: 'arena,
            'arena: 'input,
        {
            let input = InputStream::new(input);
            let lexer = visitorcalclexer::VisitorCalcLexer::<_>::new(arena, input);
            let token_source = CommonTokenStream::new(lexer);
            let mut parser = visitorcalcparser::VisitorCalcParser::new(arena, token_source);
            parser.s()
        }
    }

    // Deep recursion support requires stacker. Without stacker, the test will
    // fail with stack overflow
    #[test]
    fn test_deep_recursion() {
        let input = "(".repeat(1000) + "a" + &(")".repeat(1000));
        Arena::with(|arena| {
            let input = InputStream::new(input.as_str());
            let lexer = LabelsLexer::<_>::new(arena, input);
            let token_source = CommonTokenStream::new(lexer);
            let mut parser = LabelsParser::new(arena, token_source);
            parser.set_recursion_limit(1500);
            let result = parser.s().expect("parser error");
            assert_eq!(
                1,
                result
                    .q
                    .as_ref()
                    .unwrap()
                    .get_v()
                    .split_whitespace()
                    .count()
            );
        });
    }
}
