// Generated from ReferenceToATN.g4 by ANTLR 4.13.2
#![allow(dead_code)]
#![allow(non_snake_case)]
#![allow(non_upper_case_globals)]
#![allow(nonstandard_style)]
#![allow(unused_imports)]
#![allow(unused_mut)]
#![allow(unused_braces)]
use dbt_antlr4::PredictionContextCache;
use dbt_antlr4::parser::{Parser, BaseParser, ParserRecog, ParserNodeType};
use dbt_antlr4::token_stream::TokenStream;
use dbt_antlr4::TokenSource;
use dbt_antlr4::parser_atn_simulator::ParserATNSimulator;
use dbt_antlr4::errors::*;
use dbt_antlr4::rule_context::{BaseRuleContext, CustomRuleContext, RuleContext};
use dbt_antlr4::recognizer::{Recognizer,Actions};
use dbt_antlr4::atn_deserializer::ATNDeserializer;
use dbt_antlr4::dfa::DFA;
use dbt_antlr4::atn::{ATN, INVALID_ALT};
use dbt_antlr4::error_strategy::{ErrorStrategy, DefaultErrorStrategy};
use dbt_antlr4::parser_rule_context::{BaseParserRuleContext, ParserRuleContext,cast,cast_mut};
use dbt_antlr4::tree::*;
use dbt_antlr4::token::{TOKEN_EOF,OwningToken,Token};
use dbt_antlr4::int_stream::EOF;
use dbt_antlr4::vocabulary::{Vocabulary,VocabularyImpl};
use dbt_antlr4::token_factory::{CommonTokenFactory,TokenFactory, TokenAware};
use super::referencetoatnlistener::*;
use dbt_antlr4::{TidAble,TidExt};

use std::marker::PhantomData;
use std::sync::LazyLock;
use std::sync::Arc;
use std::rc::Rc;
use std::convert::TryFrom;
use std::cell::RefCell;
use std::ops::{DerefMut, Deref};
use std::borrow::{Borrow,BorrowMut};
use std::any::{Any,TypeId};

		pub const ReferenceToATN_ID:i32=1; 
		pub const ReferenceToATN_ATN:i32=2; 
		pub const ReferenceToATN_WS:i32=3;
	pub const ReferenceToATN_EOF:i32=EOF;
	pub const RULE_a:usize = 0;
	pub const ruleNames: [&'static str; 1] =  [
		"a"
	];


	pub const _LITERAL_NAMES: [Option<&'static str>;0] = [
	];
	pub const _SYMBOLIC_NAMES: [Option<&'static str>;4]  = [
		None, Some("ID"), Some("ATN"), Some("WS")
	];

	static _shared_context_cache: LazyLock<PredictionContextCache> = LazyLock::new(|| PredictionContextCache::new());
	static VOCABULARY: LazyLock<Box<dyn Vocabulary>> = LazyLock::new(|| Box::new(VocabularyImpl::new(_LITERAL_NAMES.iter(), _SYMBOLIC_NAMES.iter(), None)));



type BaseParserType<'input, I> =
	BaseParser<'input,ReferenceToATNParserExt<'input>, I, ReferenceToATNParserContextType , dyn ReferenceToATNListener<'input> + 'input >;

type TokenType<'input> = <LocalTokenFactory<'input> as TokenFactory<'input>>::Tok;

pub type LocalTokenFactory<'input> = dbt_antlr4::token_factory::OwningTokenFactory; // need single quote here '

pub type ReferenceToATNTreeWalker<'input,'a> =
	ParseTreeWalker<'input, 'a, ReferenceToATNParserContextType , dyn ReferenceToATNListener<'input> + 'a>;

/// Parser for ReferenceToATN grammar
pub struct ReferenceToATNParser<'input, I>
where
    I: TokenStream<'input, TF = LocalTokenFactory<'input> > + TidAble<'input>,
{
	base:BaseParserType<'input,I>,
	interpreter:Arc<ParserATNSimulator>,
	_shared_context_cache: Box<PredictionContextCache>,
    pub err_handler: Box<dyn ErrorStrategy<'input,BaseParserType<'input,I> > >,
}

impl<'input, I> ReferenceToATNParser<'input, I>
where
    I: TokenStream<'input, TF = LocalTokenFactory<'input> > + TidAble<'input>,
{
    pub fn set_error_strategy(&mut self, strategy: Box<dyn ErrorStrategy<'input,BaseParserType<'input,I> > >) {
        self.err_handler = strategy
    }

    pub fn with_strategy(input: I, strategy: Box<dyn ErrorStrategy<'input,BaseParserType<'input,I> > >) -> Self {
		dbt_antlr4::recognizer::check_version("0","50");
		let interpreter = Arc::new(ParserATNSimulator::new(
			&_ATN,
			&_decision_to_DFA,
			&_shared_context_cache,
		));
		Self {
			base: BaseParser::new_base_parser(
				input,
				Arc::clone(&interpreter),
				ReferenceToATNParserExt{
					_pd: Default::default(),
				}
			),
			interpreter,
            _shared_context_cache: Box::new(PredictionContextCache::new()),
            err_handler: strategy,
        }
    }

}

type DynStrategy<'input,I> = Box<dyn ErrorStrategy<'input,BaseParserType<'input,I>> + 'input>;

impl<'input, I> ReferenceToATNParser<'input, I>
where
    I: TokenStream<'input, TF = LocalTokenFactory<'input> > + TidAble<'input>,
{
    pub fn with_dyn_strategy(input: I) -> Self{
    	Self::with_strategy(input,Box::new(DefaultErrorStrategy::new()))
    }
}

impl<'input, I> ReferenceToATNParser<'input, I>
where
    I: TokenStream<'input, TF = LocalTokenFactory<'input> > + TidAble<'input>,
{
    pub fn new(input: I) -> Self{
    	Self::with_strategy(input,Box::new(DefaultErrorStrategy::new()))
    }
}

/// Trait for monomorphized trait object that corresponds to the nodes of parse tree generated for ReferenceToATNParser
pub trait ReferenceToATNParserContext<'input>:
	for<'x> Listenable<dyn ReferenceToATNListener<'input> + 'x > + 
	ParserRuleContext<'input, TF=LocalTokenFactory<'input>, Ctx=ReferenceToATNParserContextType>
{}

dbt_antlr4::coerce_from!{ 'input : ReferenceToATNParserContext<'input> }

impl<'input> ReferenceToATNParserContext<'input> for TerminalNode<'input,ReferenceToATNParserContextType> {}
impl<'input> ReferenceToATNParserContext<'input> for ErrorNode<'input,ReferenceToATNParserContextType> {}

dbt_antlr4::tid! { impl<'input> TidAble<'input> for dyn ReferenceToATNParserContext<'input> + 'input }

dbt_antlr4::tid! { impl<'input> TidAble<'input> for dyn ReferenceToATNListener<'input> + 'input }

pub struct ReferenceToATNParserContextType;
dbt_antlr4::tid!{ReferenceToATNParserContextType}

impl<'input> ParserNodeType<'input> for ReferenceToATNParserContextType{
	type TF = LocalTokenFactory<'input>;
	type Type = dyn ReferenceToATNParserContext<'input> + 'input;
}

impl<'input, I> Deref for ReferenceToATNParser<'input, I>
where
    I: TokenStream<'input, TF = LocalTokenFactory<'input> > + TidAble<'input>,
{
    type Target = BaseParserType<'input,I>;

    fn deref(&self) -> &Self::Target {
        &self.base
    }
}

impl<'input, I> DerefMut for ReferenceToATNParser<'input, I>
where
    I: TokenStream<'input, TF = LocalTokenFactory<'input> > + TidAble<'input>,
{
    fn deref_mut(&mut self) -> &mut Self::Target {
        &mut self.base
    }
}

pub struct ReferenceToATNParserExt<'input>{
	_pd: PhantomData<&'input str>,
}

impl<'input> ReferenceToATNParserExt<'input>{
}
dbt_antlr4::tid! { ReferenceToATNParserExt<'a> }

impl<'input> TokenAware<'input> for ReferenceToATNParserExt<'input>{
	type TF = LocalTokenFactory<'input>;
}

impl<'input,I: TokenStream<'input, TF = LocalTokenFactory<'input> > + TidAble<'input>> ParserRecog<'input, BaseParserType<'input,I>> for ReferenceToATNParserExt<'input>{}

impl<'input,I: TokenStream<'input, TF = LocalTokenFactory<'input> > + TidAble<'input>> Actions<'input, BaseParserType<'input,I>> for ReferenceToATNParserExt<'input>{
	fn get_grammar_file_name(&self) -> & str{ "ReferenceToATN.g4"}

   	fn get_rule_names(&self) -> &[& str] {&ruleNames}

   	fn get_vocabulary(&self) -> &dyn Vocabulary { &**VOCABULARY }
}
//------------------- a ----------------
pub type AContextAll<'input> = AContext<'input>;


pub type AContext<'input> = BaseParserRuleContext<'input,AContextExt<'input>>;

#[derive(Clone)]
pub struct AContextExt<'input>{
ph:PhantomData<&'input str>
}

impl<'input> ReferenceToATNParserContext<'input> for AContext<'input>{}

impl<'input,'a> Listenable<dyn ReferenceToATNListener<'input> + 'a> for AContext<'input>{
		fn enter(&self,listener: &mut (dyn ReferenceToATNListener<'input> + 'a)) -> Result<(), ANTLRError> {
			listener.enter_every_rule(self)?;
			listener.enter_a(self);
			Ok(())
		}fn exit(&self,listener: &mut (dyn ReferenceToATNListener<'input> + 'a)) -> Result<(), ANTLRError> {
			listener.exit_a(self);
			listener.exit_every_rule(self)?;
			Ok(())
		}
}

impl<'input> CustomRuleContext<'input> for AContextExt<'input>{
	type TF = LocalTokenFactory<'input>;
	type Ctx = ReferenceToATNParserContextType;
	fn get_rule_index(&self) -> usize { RULE_a }
	//fn type_rule_index() -> usize where Self: Sized { RULE_a }
}
dbt_antlr4::tid!{AContextExt<'a>}

impl<'input> AContextExt<'input>{
	fn new(parent: Option<Rc<dyn ReferenceToATNParserContext<'input> + 'input > >, invoking_state: i32) -> Rc<AContextAll<'input>> {
		Rc::new(
			BaseParserRuleContext::new_parser_ctx(parent, invoking_state,AContextExt{

				ph:PhantomData
			}),
		)
	}
}

pub trait AContextAttrs<'input>: ReferenceToATNParserContext<'input> + BorrowMut<AContextExt<'input>>{

/// Retrieves all `TerminalNode`s corresponding to token ATN in current rule
fn ATN_all(&self) -> Vec<Rc<TerminalNode<'input,ReferenceToATNParserContextType>>>  where Self:Sized{
	self.children_of_type()
}
/// Retrieves 'i's TerminalNode corresponding to token ATN, starting from 0.
/// Returns `None` if number of children corresponding to token ATN is less or equal than `i`.
fn ATN(&self, i: usize) -> Option<Rc<TerminalNode<'input,ReferenceToATNParserContextType>>> where Self:Sized{
	self.get_token(ReferenceToATN_ATN, i)
}
/// Retrieves all `TerminalNode`s corresponding to token ID in current rule
fn ID_all(&self) -> Vec<Rc<TerminalNode<'input,ReferenceToATNParserContextType>>>  where Self:Sized{
	self.children_of_type()
}
/// Retrieves 'i's TerminalNode corresponding to token ID, starting from 0.
/// Returns `None` if number of children corresponding to token ID is less or equal than `i`.
fn ID(&self, i: usize) -> Option<Rc<TerminalNode<'input,ReferenceToATNParserContextType>>> where Self:Sized{
	self.get_token(ReferenceToATN_ID, i)
}

}

impl<'input> AContextAttrs<'input> for AContext<'input>{}

impl<'input, I> ReferenceToATNParser<'input, I>
where
    I: TokenStream<'input, TF = LocalTokenFactory<'input> > + TidAble<'input>,
{
	pub fn a(&mut self,)
	-> Result<Rc<AContextAll<'input>>,ANTLRError> {
		let mut recog = self;
		let _parentctx = recog.ctx.take();
		let mut _localctx = AContextExt::new(_parentctx.clone(), recog.base.get_state());
        recog.base.enter_rule(_localctx.clone(), 0, RULE_a);
        let mut _localctx: Rc<AContextAll> = _localctx;
		let mut _la: i32 = -1;
		let result: Result<(), ANTLRError> = (|| {

			let mut _alt: i32;
			//recog.base.enter_outer_alt(_localctx.clone(), 1)?;
			recog.base.enter_outer_alt(None, 1)?;
			{
			recog.base.set_state(5);
			recog.err_handler.sync(&mut recog.base)?;
			_alt = recog.interpreter.adaptive_predict(0,&mut recog.base)?;
			while { _alt!=2 && _alt!=INVALID_ALT } {
				if _alt==1 {
					{
					{
					recog.base.set_state(2);
					_la = recog.base.input.la(1);
					if { !(_la==ReferenceToATN_ID || _la==ReferenceToATN_ATN) } {
						recog.err_handler.recover_inline(&mut recog.base)?;

					}
					else {
						if  recog.base.input.la(1)==TOKEN_EOF { recog.base.matched_eof = true };
						recog.err_handler.report_match(&mut recog.base);
						recog.base.consume(&mut recog.err_handler);
					}
					}
					} 
				}
				recog.base.set_state(7);
				recog.err_handler.sync(&mut recog.base)?;
				_alt = recog.interpreter.adaptive_predict(0,&mut recog.base)?;
			}
			recog.base.set_state(9);
			recog.err_handler.sync(&mut recog.base)?;
			_la = recog.base.input.la(1);
			if _la==ReferenceToATN_ATN {
				{
				recog.base.set_state(8);
				recog.base.match_token(ReferenceToATN_ATN,&mut recog.err_handler)?;

				}
			}

			println!("{}",{let temp = recog.base.input.lt(-1).map(|it|it.get_token_index()).unwrap_or(-1); recog.input.get_text_from_interval(recog.get_parser_rule_context().start().get_token_index(), temp)});
			}
			Ok(())
		})();
		match result {
		Ok(_)=>{},
        Err(e) if !e.is_recoverable() => return Err(e),
		Err(ref re) => {
				//_localctx.exception = re;
				recog.err_handler.report_error(&mut recog.base, re);
				recog.err_handler.recover(&mut recog.base, re)?;
			}
		}
		recog.base.exit_rule()?;

		Ok(_localctx)
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
    4, 1, 3, 14, 2, 0, 7, 0, 1, 0, 5, 0, 4, 8, 0, 10, 0, 12, 0, 7, 9, 0, 
    1, 0, 3, 0, 10, 8, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 2, 
    14, 0, 5, 1, 0, 0, 0, 2, 4, 7, 0, 0, 0, 3, 2, 1, 0, 0, 0, 4, 7, 1, 0, 
    0, 0, 5, 3, 1, 0, 0, 0, 5, 6, 1, 0, 0, 0, 6, 9, 1, 0, 0, 0, 7, 5, 1, 
    0, 0, 0, 8, 10, 5, 2, 0, 0, 9, 8, 1, 0, 0, 0, 9, 10, 1, 0, 0, 0, 10, 
    11, 1, 0, 0, 0, 11, 12, 6, 0, -1, 0, 12, 1, 1, 0, 0, 0, 2, 5, 9
]);
