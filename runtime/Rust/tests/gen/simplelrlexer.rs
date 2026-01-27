// Generated from SimpleLR.g4 by ANTLR 4.13.2
#![allow(dead_code)]
#![allow(nonstandard_style)]
#![allow(unused_imports)]
#![allow(unused_variables)]
use dbt_antlr4::atn::ATN;
use dbt_antlr4::char_stream::CharStream;
use dbt_antlr4::int_stream::IntStream;
use dbt_antlr4::tree::ParseTree;
use dbt_antlr4::lexer::{BaseLexer, Lexer, LexerRecog};
use dbt_antlr4::atn_deserializer::ATNDeserializer;
use dbt_antlr4::dfa::DFA;
use dbt_antlr4::lexer_atn_simulator::{LexerATNSimulator, ILexerATNSimulator};
use dbt_antlr4::PredictionContextCache;
use dbt_antlr4::recognizer::{Recognizer,Actions};
use dbt_antlr4::error_listener::ErrorListener;
use dbt_antlr4::TokenSource;
use dbt_antlr4::token_factory::{TokenFactory,CommonTokenFactory,TokenAware};
use dbt_antlr4::token::*;
use dbt_antlr4::rule_context::{BaseRuleContext,EmptyCustomRuleContext,EmptyContext};
use dbt_antlr4::parser_rule_context::{ParserRuleContext,BaseParserRuleContext,cast};
use dbt_antlr4::vocabulary::{Vocabulary,VocabularyImpl};

use dbt_antlr4::{Tid,TidAble,TidExt};

use std::sync::LazyLock;
use std::sync::Arc;
use std::cell::RefCell;
use std::rc::Rc;
use std::marker::PhantomData;
use std::ops::{Deref, DerefMut};


	pub const ID:i32=1; 
	pub const WS:i32=2;
	pub const channelNames: [&'static str;0+2] = [
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	];

	pub const modeNames: [&'static str;1] = [
		"DEFAULT_MODE"
	];

	pub const ruleNames: [&'static str;2] = [
		"ID", "WS"
	];


	pub const _LITERAL_NAMES: [Option<&'static str>;0] = [
	];
	pub const _SYMBOLIC_NAMES: [Option<&'static str>;3]  = [
		None, Some("ID"), Some("WS")
	];

	static _shared_context_cache: LazyLock<PredictionContextCache> = LazyLock::new(|| PredictionContextCache::new());
	static VOCABULARY: LazyLock<Box<dyn Vocabulary>> = LazyLock::new(|| Box::new(VocabularyImpl::new(_LITERAL_NAMES.iter(), _SYMBOLIC_NAMES.iter(), None)));



pub type LexerContext<'input> = BaseRuleContext<'input,EmptyCustomRuleContext<'input,LocalTokenFactory<'input> >>;
pub type LocalTokenFactory<'input> = CommonTokenFactory;

type From<'a> = <LocalTokenFactory<'a> as TokenFactory<'a> >::From;

pub struct SimpleLRLexer<'input, Input:CharStream<From<'input> >> {
	base: BaseLexer<'input,SimpleLRLexerActions,Input,LocalTokenFactory<'input>>,
}

dbt_antlr4::tid! { impl<'input,Input> TidAble<'input> for SimpleLRLexer<'input,Input> where Input:CharStream<From<'input> > }

impl<'input, Input:CharStream<From<'input> >> Deref for SimpleLRLexer<'input,Input>{
	type Target = BaseLexer<'input,SimpleLRLexerActions,Input,LocalTokenFactory<'input>>;

	fn deref(&self) -> &Self::Target {
		&self.base
	}
}

impl<'input, Input:CharStream<From<'input> >> DerefMut for SimpleLRLexer<'input,Input>{
	fn deref_mut(&mut self) -> &mut Self::Target {
		&mut self.base
	}
}


impl<'input, Input:CharStream<From<'input> >> SimpleLRLexer<'input,Input>{
    fn get_rule_names(&self) -> &'static [&'static str] {
        &ruleNames
    }
    fn get_literal_names(&self) -> &[Option<&str>] {
        &_LITERAL_NAMES
    }

    fn get_symbolic_names(&self) -> &[Option<&str>] {
        &_SYMBOLIC_NAMES
    }

    fn get_grammar_file_name(&self) -> &'static str {
        "SimpleLRLexer.g4"
    }

	pub fn new_with_token_factory(input: Input, tf: &'input LocalTokenFactory<'input>) -> Self {
		dbt_antlr4::recognizer::check_version("0","50");
    	Self {
			base: BaseLexer::new_base_lexer(
				input,
				LexerATNSimulator::new_lexer_atnsimulator(
					&_ATN,
					&_decision_to_DFA,
					&_shared_context_cache,
				),
				SimpleLRLexerActions{},
				tf
			)
	    }
	}
}

impl<'input, Input:CharStream<From<'input> >> SimpleLRLexer<'input,Input> where &'input LocalTokenFactory<'input>:Default{
	pub fn new(input: Input) -> Self{
		SimpleLRLexer::new_with_token_factory(input, <&LocalTokenFactory<'input> as Default>::default())
	}
}

pub struct SimpleLRLexerActions {
}

impl SimpleLRLexerActions{
}

impl<'input, Input:CharStream<From<'input> >> Actions<'input,BaseLexer<'input,SimpleLRLexerActions,Input,LocalTokenFactory<'input>>> for SimpleLRLexerActions{
	}

	impl<'input, Input:CharStream<From<'input> >> SimpleLRLexer<'input,Input>{

}

impl<'input, Input:CharStream<From<'input> >> LexerRecog<'input,BaseLexer<'input,SimpleLRLexerActions,Input,LocalTokenFactory<'input>>> for SimpleLRLexerActions{
}
impl<'input> TokenAware<'input> for SimpleLRLexerActions{
	type TF = LocalTokenFactory<'input>;
}

impl<'input, Input:CharStream<From<'input> >> TokenSource<'input> for SimpleLRLexer<'input,Input>{
	type TF = LocalTokenFactory<'input>;

    fn next_token(&mut self) -> <Self::TF as TokenFactory<'input>>::Tok {
        self.base.next_token()
    }

    fn get_line(&self) -> isize {
        self.base.get_line()
    }

    fn get_char_position_in_line(&self) -> isize {
        self.base.get_char_position_in_line()
    }

    fn get_input_stream(&mut self) -> Option<&mut dyn IntStream> {
        self.base.get_input_stream()
    }

	fn get_source_name(&self) -> String {
		self.base.get_source_name()
	}

    fn get_token_factory(&self) -> &'input Self::TF {
        self.base.get_token_factory()
    }

    fn get_dfa_string(&self) -> String {
        self.base.get_dfa_string()
    }
}


	static _ATN: LazyLock<ATN> =
	    LazyLock::new(|| ATNDeserializer::new(None).deserialize(&mut _serializedATN.iter()));
	static _decision_to_DFA: LazyLock<Vec<DFA>> = LazyLock::new(|| {
	    let mut dfa = Vec::new();
	    let size = _ATN.decision_to_state.len() as i32;
	    for i in 0..size {
	        dfa.push(DFA::new(
	            &_ATN,
	            _ATN.get_decision_state(i),
	            i,
	        ))
	    }
	    dfa
	});
	static _serializedATN: LazyLock<Vec<i32>> = LazyLock::new(|| vec![
	    4, 0, 2, 14, 6, -1, 2, 0, 7, 0, 2, 1, 7, 1, 1, 0, 4, 0, 7, 8, 0, 11, 
	    0, 12, 0, 8, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 2, 1, 1, 3, 2, 1, 0, 1, 
	    2, 0, 10, 10, 32, 32, 14, 0, 1, 1, 0, 0, 0, 0, 3, 1, 0, 0, 0, 1, 6, 
	    1, 0, 0, 0, 3, 10, 1, 0, 0, 0, 5, 7, 2, 97, 122, 0, 6, 5, 1, 0, 0, 
	    0, 7, 8, 1, 0, 0, 0, 8, 6, 1, 0, 0, 0, 8, 9, 1, 0, 0, 0, 9, 2, 1, 0, 
	    0, 0, 10, 11, 7, 0, 0, 0, 11, 12, 1, 0, 0, 0, 12, 13, 6, 1, 0, 0, 13, 
	    4, 1, 0, 0, 0, 2, 0, 8, 1, 6, 0, 0
	]);