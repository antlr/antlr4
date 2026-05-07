#[rustfmt::skip]
mod gen {
    #![allow(non_snake_case)]
    pub mod csvlexer;
    pub mod csvlistener;
    pub mod csvparser;
    pub mod csvvisitor;
    pub mod visitorbasiclexer;
    pub mod visitorbasiclistener;
    pub mod visitorbasicparser;
    pub mod visitorbasicvisitor;
    pub mod visitorcalclexer;
    pub mod visitorcalclistener;
    pub mod visitorcalcparser;
    pub mod visitorcalcvisitor;
}

use dbt_antlr4::common_token_stream::CommonTokenStream;
use dbt_antlr4::errors::ANTLRError;
use dbt_antlr4::parser_rule_context::ParserRuleContext;
use dbt_antlr4::recognizer::Recognizer;
use dbt_antlr4::token::Token;
use dbt_antlr4::token_factory::{CommonTokenFactory, OwningTokenFactory};
use dbt_antlr4::tree::{ErrorNode, TerminalNode};
use dbt_antlr4::trees::string_tree;
use dbt_antlr4::{Arena, InputStream};

use crate::gen::visitorbasiclexer::VisitorBasicLexer;
use crate::gen::visitorbasicparser::{VisitorBasicParser, VisitorBasicParserNode};
use crate::gen::visitorbasicvisitor::VisitorBasicVisitor;
use crate::gen::visitorcalclexer::VisitorCalcLexer;
use crate::gen::visitorcalcparser::{
    AddContext, AddContextAttrs, MultiplyContext, MultiplyContextAttrs, NumberContext,
    NumberContextAttrs, SContext, SContextAttrs, VisitorCalcParser,
};
use crate::gen::visitorcalcvisitor::VisitorCalcVisitor;
use crate::gen::*;

#[test]
fn test_visit_terminal_node() {
    struct TestVisitor;
    impl<'input, 'arena> VisitorBasicVisitor<'input, 'arena> for TestVisitor
    where
        'input: 'arena,
    {
        type Return = String;

        fn visit_terminal(
            &mut self,
            _node: &TerminalNode<'input, 'arena>,
        ) -> Result<Self::Return, ANTLRError> {
            Ok(_node.symbol.to_string() + "\n")
        }

        fn aggregate_results(
            &self,
            aggregate: Self::Return,
            next: Self::Return,
        ) -> Result<Self::Return, ANTLRError> {
            Ok(aggregate + &next)
        }
    }

    Arena::with(|arena| {
        let lexer = VisitorBasicLexer::<_>::new(arena, InputStream::new("A"));
        let mut parser = VisitorBasicParser::new(arena, CommonTokenStream::new(lexer));

        let root = parser.s().unwrap();
        assert_eq!("(s A <EOF>)", string_tree(root, parser.get_rule_names()));

        let result = TestVisitor.visit(root).unwrap();
        let expected = "[@0,0:0='A',<1>,1:0]\n\
                              [@1,1:0='<EOF>',<-1>,1:1]\n";
        assert_eq!(result, expected)
    });
}

#[test]
fn test_visit_error_node() {
    Arena::with(|arena| {
        let lexer = VisitorBasicLexer::<_>::new(arena, InputStream::new(""));
        let mut parser = VisitorBasicParser::new(arena, CommonTokenStream::new(lexer));

        let root = parser.s().unwrap();
        assert_eq!(
            "(s <missing 'A'> <EOF>)",
            string_tree(root, parser.get_rule_names())
        );

        struct TestVisitor;
        impl<'input, 'arena, Tok: Token + 'input> VisitorBasicVisitor<'input, 'arena, Tok> for TestVisitor
        where
            'input: 'arena,
        {
            type Return = String;

            fn visit_error_node(
                &mut self,
                _node: &ErrorNode<'input, 'arena, Tok>,
            ) -> Result<Self::Return, ANTLRError> {
                Ok(format!("Error encountered: {}", _node.symbol))
            }

            fn aggregate_results(
                &self,
                aggregate: Self::Return,
                next: Self::Return,
            ) -> Result<Self::Return, ANTLRError> {
                Ok(aggregate + &next)
            }
        }

        let result = TestVisitor.visit(root).unwrap();
        let expected = "Error encountered: [@-1,-1:-1='<missing 'A'>',<1>,1:0]";
        assert_eq!(result, expected)
    });
}

#[test]
fn test_should_not_visit_eof() {
    Arena::with(|arena| {
        let lexer = VisitorBasicLexer::<_, OwningTokenFactory>::new(arena, InputStream::new("A"));
        let mut parser = VisitorBasicParser::new(arena, CommonTokenStream::new(lexer));

        let root = parser.s().unwrap();
        assert_eq!("(s A <EOF>)", string_tree(root, parser.get_rule_names()));

        struct TestVisitor;
        impl<'input, 'arena, Tok: Token + 'input> VisitorBasicVisitor<'input, 'arena, Tok> for TestVisitor
        where
            'input: 'arena,
        {
            type Return = String;

            fn visit_terminal(
                &mut self,
                node: &TerminalNode<'input, 'arena, Tok>,
            ) -> Result<Self::Return, ANTLRError> {
                Ok(node.symbol.to_string() + "\n")
            }

            fn should_visit_next_child(
                &self,
                _node: &VisitorBasicParserNode<'input, 'arena, Tok>,
                current: &Self::Return,
            ) -> bool {
                current.is_empty()
            }
        }

        let result = TestVisitor.visit(root).unwrap();
        let expected = "[@0,0:0='A',<1>,1:0]\n";
        assert_eq!(result, expected);

        struct TestVisitorUnit(String);
        impl<'input, 'arena, Tok: Token + 'input> VisitorBasicVisitor<'input, 'arena, Tok>
            for TestVisitorUnit
        where
            'input: 'arena,
        {
            type Return = ();

            fn visit_terminal(
                &mut self,
                node: &TerminalNode<'input, 'arena, Tok>,
            ) -> Result<Self::Return, ANTLRError> {
                self.0 += &node.symbol.to_string();
                Ok(())
            }
        }
        let mut visitor_unit = TestVisitorUnit(String::new());
        let _ = visitor_unit.visit(root).unwrap();
        assert_eq!(
            visitor_unit.0,
            "[@0,0:0='A',<1>,1:0][@1,1:0='<EOF>',<-1>,1:1]"
        );
    });
}

#[test]
fn test_should_not_visit_anything() {
    Arena::with(|arena| {
        let lexer = VisitorBasicLexer::<_, CommonTokenFactory>::new(arena, InputStream::new("A"));
        let mut parser = VisitorBasicParser::new(arena, CommonTokenStream::new(lexer));

        let root = parser.s().unwrap();
        assert_eq!("(s A <EOF>)", string_tree(root, parser.get_rule_names()));

        struct TestVisitor;
        impl<'input, 'arena, Tok: Token + 'input> VisitorBasicVisitor<'input, 'arena, Tok> for TestVisitor
        where
            'input: 'arena,
        {
            type Return = String;

            fn visit_terminal(
                &mut self,
                _node: &TerminalNode<'input, 'arena, Tok>,
            ) -> Result<Self::Return, ANTLRError> {
                unreachable!()
            }

            fn should_visit_next_child(
                &self,
                _node: &VisitorBasicParserNode<'input, 'arena, Tok>,
                _current: &Self::Return,
            ) -> bool {
                false
            }
        }

        let result = TestVisitor.visit(root).unwrap();
        let expected = "";
        assert_eq!(result, expected)
    });
}

#[test]
fn test_visitor_with_return() {
    struct CalcVisitor;

    impl<'input, 'arena, Tok: Token + 'input> VisitorCalcVisitor<'input, 'arena, Tok> for CalcVisitor
    where
        'input: 'arena,
    {
        type Return = i32;

        fn aggregate_results(
            &self,
            _aggregate: Self::Return,
            _next: Self::Return,
        ) -> Result<Self::Return, ANTLRError> {
            panic!("Should not be reachable")
        }

        fn visit_s(
            &mut self,
            ctx: &SContext<'input, 'arena, Tok>,
        ) -> Result<Self::Return, ANTLRError> {
            self.visit(&*ctx.expr().unwrap())
        }

        fn visit_add(
            &mut self,
            ctx: &AddContext<'input, 'arena, Tok>,
        ) -> Result<Self::Return, ANTLRError> {
            let left = self.visit(&*ctx.expr(0).unwrap())?;
            let right = self.visit(&*ctx.expr(1).unwrap())?;
            if ctx.ADD().is_some() {
                Ok(left + right)
            } else {
                Ok(left - right)
            }
        }

        fn visit_number(
            &mut self,
            ctx: &NumberContext<'input, 'arena, Tok>,
        ) -> Result<Self::Return, ANTLRError> {
            Ok(ctx.INT().unwrap().get_text().parse().unwrap())
        }

        fn visit_multiply(
            &mut self,
            ctx: &MultiplyContext<'input, 'arena, Tok>,
        ) -> Result<Self::Return, ANTLRError> {
            let left = self.visit(&*ctx.expr(0).unwrap())?;
            let right = self.visit(&*ctx.expr(1).unwrap())?;
            if ctx.MUL().is_some() {
                Ok(left * right)
            } else {
                Ok(left / right)
            }
        }
    }

    fn parse(input: &str, expected_tree: &str) -> i32 {
        Arena::with(|arena| {
            let mut _lexer =
                VisitorCalcLexer::<_, CommonTokenFactory>::new(arena, InputStream::new(input));
            let token_source = CommonTokenStream::new(_lexer);
            let mut parser = VisitorCalcParser::new(arena, token_source);

            let root = parser.s().unwrap();

            assert_eq!(string_tree(root, parser.get_rule_names()), expected_tree);

            CalcVisitor.visit(root).unwrap()
        })
    }

    assert_eq!(
        6,
        parse(
            "2 + 8 / 2",
            "(s (expr (expr 2) + (expr (expr 8) / (expr 2))) <EOF>)"
        )
    );
    assert_eq!(
            16,
            parse(
                "2 + 8 * 2 - 4 / 2",
                "(s (expr (expr (expr 2) + (expr (expr 8) * (expr 2))) - (expr (expr 4) / (expr 2))) <EOF>)"
            )
        );
    assert_eq!(
        4,
        parse(
            "6 / 2 + 1",
            "(s (expr (expr (expr 6) / (expr 2)) + (expr 1)) <EOF>)"
        )
    );
}

// tests zero-copy parsing with non static visitor
#[test]
fn test_visitor_retrieve_reference() {
    use csvlexer::CSVLexer;
    use csvparser::{CSVParser, HdrContext, RowContext, RowContextAttrs};
    use csvvisitor::CSVVisitor;

    // `T` here to ensure that visitor can have lifetime shorter that `'input` string
    // TODO: allow 'input instead of 'arena for T -- decouples the visitor
    // lifetime from the parse tree thus allowing it to be returned out of
    // the Arena scope . Requires refactoring [Token] to carry 'input.
    struct MyCSVVisitor<'arena, T>(Vec<&'arena str>, T);

    impl<'input, 'arena, T, Tok: Token + 'input> CSVVisitor<'input, 'arena, Tok>
        for MyCSVVisitor<'arena, T>
    where
        'input: 'arena,
    {
        type Return = ();

        fn visit_terminal(
            &mut self,
            node: &TerminalNode<'input, 'arena, Tok>,
        ) -> Result<Self::Return, ANTLRError> {
            if node.symbol.get_token_type() == csvparser::CSV_TEXT {
                self.0.push(node.symbol.get_text());
            }
            Ok(())
        }
        fn visit_hdr(
            &mut self,
            _ctx: &HdrContext<'input, 'arena, Tok>,
        ) -> Result<Self::Return, ANTLRError> {
            Ok(())
        }

        fn visit_row(
            &mut self,
            ctx: &'arena RowContext<'input, 'arena, Tok>,
        ) -> Result<Self::Return, ANTLRError> {
            if ctx.field_all().len() > 1 {
                self.visit_children(ctx)
            } else {
                Ok(())
            }
        }
    }

    fn parse<'a>(input: &'a str) -> i32 {
        Arena::with(|arena| {
            let lexer = CSVLexer::<_, CommonTokenFactory>::new(arena, InputStream::new(input));
            let token_source = CommonTokenStream::new(lexer);
            let mut parser = CSVParser::new(arena, token_source);
            let result = parser.csvFile().expect("parsed unsuccessfully");

            let test = 5;
            let mut visitor = MyCSVVisitor(Vec::new(), test);
            visitor.visit(result).unwrap();
            assert_eq!(visitor.0, vec!["d1", "d2"]);

            visitor.1
        })
    }

    let _result = parse("h1,h2\nd1,d2\nd3\n");
}

// #[test]
// fn test_visitor_retrieve_reference_by_return() {
//     use csvlexer::CSVLexer;
//     use csvparser::{CSVParser, CsvFileContext, HdrContext, RowContext, RowContextAttrs};
//     use csvvisitor::CSVVisitor;
//     use std::rc::Rc;

//     struct MyCSVVisitor<'i>(Vec<&'i str>);

//     impl<'input, 'arena> CSVVisitor<'input, 'arena> for MyCSVVisitor<'input>
//     where
//         'input: 'arena,
//     {
//         type Return = Vec<&'input str>;

//         fn visit_terminal(
//             &mut self,
//             node: &TerminalNode<'input, 'arena>,
//         ) -> Result<Self::Return, ANTLRError> {
//             if node.symbol.get_token_type() == csvparser::CSV_TEXT {
//                 return vec![node.symbol.get_text()];
//             }
//             vec![]
//         }

//         fn aggregate_results(
//             &self,
//             mut aggregate: Self::Return,
//             next: Self::Return,
//         ) -> Self::Return {
//             aggregate.extend(next);
//             aggregate
//         }
//     }

//     impl<'i> CSVVisitorCompat<'i> for MyCSVVisitor<'i> {
//         fn visit_hdr(&mut self, _ctx: &HdrContext<'i>) -> Self::Return {
//             vec![]
//         }

//         fn visit_row(&mut self, ctx: &RowContext<'i>) -> Self::Return {
//             if ctx.field_all().len() > 1 {
//                 self.visit_children(ctx)
//             } else {
//                 vec![]
//             }
//         }
//     }

//     fn parse<'a>(tf: &'a ArenaCommonFactory<'a>) -> Rc<CsvFileContext<'a>> {
//         let mut _lexer =
//             CSVLexer::new_with_token_factory(InputStream::new("h1,h2\nd1,d2\nd3\n"), tf);
//         let token_source = CommonTokenStream::new(_lexer);
//         let mut parser = CSVParser::new(token_source);
//         let result = parser.csvFile().expect("parsed unsuccessfully");

//         let mut visitor = MyCSVVisitor(Vec::new());
//         let visitor_result = visitor.visit(&*result);
//         assert_eq!(visitor_result, vec!["d1", "d2"]);

//         result
//     }
//     let tf = ArenaCommonFactory::default();

//     let _result = parse(&tf);
// }
