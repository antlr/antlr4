// Generated from SimpleLR.g4 by ANTLR 4.13.2
#![allow(dead_code)]
#![allow(unused_imports)]
#![allow(non_snake_case)]
#![allow(non_upper_case_globals)]
#![allow(nonstandard_style)]
#![allow(unused_braces)]
#![allow(unused_parens)]
use dbt_antlr4::Arena;
use dbt_antlr4::PredictionContextCache;
use dbt_antlr4::parser::{Parser, BaseParser, ParserRecog, ListenerId};
use dbt_antlr4::token_stream::TokenStream;
use dbt_antlr4::TokenSource;
use dbt_antlr4::parser_atn_simulator::ParserATNSimulator;
use dbt_antlr4::errors::ANTLRError;
use dbt_antlr4::rule_context::{CustomRuleContext, RuleContext};
use dbt_antlr4::recognizer::{Recognizer,Actions};
use dbt_antlr4::atn_config_set::ATNConfigSet;
use dbt_antlr4::atn_deserializer::ATNDeserializer;
use dbt_antlr4::atn_simulator::BaseATNSimulator;
use dbt_antlr4::atn_simulator::ParserATNSimulatorManager as ATNSimulatorManager;
use dbt_antlr4::atn::{ATN, INVALID_ALT};
use dbt_antlr4::error_strategy::{DefaultErrorStrategy, ErrorStrategyDelegate, ErrorStrategy};
use dbt_antlr4::parser_rule_context::{BaseParserRuleContext, ParserRuleContext};
use dbt_antlr4::tree::*;
use dbt_antlr4::token::{TOKEN_EOF,Token};
use dbt_antlr4::int_stream::EOF;
use dbt_antlr4::vocabulary::{Vocabulary,VocabularyImpl};
use dbt_antlr4::token_factory::TokenFactory;
use super::simplelrlistener::*;
use std::marker::PhantomData;
use std::sync::LazyLock;
use std::rc::Rc;
use std::ops::{DerefMut, Deref};

dbt_antlr4::check_version!("1","3");
pub const SimpleLR_ID:i32=1; 
pub const SimpleLR_WS:i32=2;
pub const SimpleLR_EOF:i32=EOF;
pub const RULE_s:usize = 0; 
pub const RULE_a:usize = 1;
pub const ruleNames: [&'static str; 2] = [
    "s", "a"
];

pub const _LITERAL_NAMES: [Option<&'static str>;0] = [
];
pub const _SYMBOLIC_NAMES: [Option<&'static str>;3]  = [
	None, Some("ID"), Some("WS")
];

static VOCABULARY: LazyLock<Box<dyn Vocabulary>> = LazyLock::new(|| Box::new(VocabularyImpl::new(_LITERAL_NAMES.iter(), _SYMBOLIC_NAMES.iter(), None)));

pub type BaseParserType<'input, 'arena, Input, TF> = BaseParser<'input, 'arena, SimpleLRParserExt<'input, 'arena>, SimpleLRParserNodeKind, Input, TF>;
pub fn parser_simulator_manager() -> &'static ATNSimulatorManager { &ATN_SIMULATOR_MANAGER }

pub struct SimpleLRParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	base: BaseParserType<'input, 'arena, Input, TF>,
    interpreter: Rc<ParserATNSimulator<'arena>>,
    err_handler: ErrorStrategyDelegate<'input, 'arena, TF, BaseParserType<'input, 'arena, Input, TF>>,
}

impl<'input, 'arena, Input, TF> SimpleLRParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
    pub fn with_strategy(arena: &'arena Arena, input: Input, strategy: Box<dyn ErrorStrategy<'input, 'arena, TF, BaseParserType<'input, 'arena, Input, TF>> + 'arena>) -> Self {
        let base_simulator = ATN_SIMULATOR_MANAGER.get_simulator(arena);
		let interpreter = Rc::new(ParserATNSimulator::new(base_simulator));
		Self {
			base: BaseParser::new_base_parser(
				arena, input, Rc::clone(&interpreter),
				SimpleLRParserExt {
					_pd: Default::default(),
				}
			),
			interpreter,
            err_handler: unsafe { ErrorStrategyDelegate::new(strategy) },
        }
    }

    pub fn new(arena: &'arena Arena, input: Input) -> Self{
    	Self::with_strategy(arena, input, Box::new(DefaultErrorStrategy::new()))
    }

    pub fn set_error_strategy(&mut self, strategy: Box<dyn ErrorStrategy<'input, 'arena, TF, BaseParserType<'input, 'arena, Input, TF>> + 'arena>) {
        self.err_handler = unsafe { ErrorStrategyDelegate::new(strategy) };
    }

    /// Adds parse listener for this parser
    /// returns `listener_id` that can be used later to get listener back
    ///
    /// ### Example for listener usage:
    /// todo
    pub fn add_parse_listener<L>(
        &mut self,
        listener: Box<L>,
    ) -> ListenerId<L>
    where
        L: SimpleLRListener<'arena> + 'static,
    {
        let id = ListenerId::new(&listener);
        self.base.add_dyn_parse_listener(listener);
        id
    }
}
pub struct SimpleLRTreeWalker;
impl SimpleLRTreeWalker
{
    pub fn walk<'input,'arena, L, T>(
        listener: Box<L>,
        tree: &'arena T,
    ) -> Result<Box<L>, ANTLRError>
    where
        'input: 'arena,
        L: SimpleLRListener<'arena> + 'static,
        T: NodeInner<'input, 'arena, SimpleLRParserNodeKind>,
    {
        let listener_ptr = Box::into_raw(listener);
        let listener = unsafe { Box::from_raw(listener_ptr as *mut <SimpleLRParserNodeKind as NodeKindType>::Listener) };
        let listener = ParseTreeWalker::walk(listener, tree.as_node())?;
        Ok(unsafe { Box::from_raw(Box::into_raw(listener) as *mut L) } )
    }
}

#[derive(Clone, Copy, PartialEq, Eq, Debug)]
#[repr(u16)]
pub enum SimpleLRParserNodeKind {
    SContext,
    AContext,
    Terminal,
    Error,
}
pub type SimpleLRParserNode<'input, 'arena> = TreeNode<'input, 'arena, SimpleLRParserNodeKind>;

dbt_antlr4::impl_deref! { parser => SimpleLRParser }
dbt_antlr4::impl_node_kind! { SimpleLRParserNodeKind {
; SContext(enter_s, exit_s, ), AContext(enter_a, exit_a, ), 
    }; listener = dyn SimpleLRListener<'arena>,
}

pub struct SimpleLRParserExt<'input, 'arena> {
	_pd: PhantomData<(&'input str, &'arena ())>,
}

impl<'input, 'arena> SimpleLRParserExt<'input, 'arena> {
}

impl<'input, 'arena, Input, TF> ParserRecog<'input, 'arena, BaseParserType<'input, 'arena, Input, TF>> for SimpleLRParserExt<'input, 'arena>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{}

impl<'input, 'arena, Input, TF> Actions<'input, 'arena, BaseParserType<'input, 'arena, Input, TF>> for SimpleLRParserExt<'input, 'arena>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	fn get_grammar_file_name(&self) -> & str{ "SimpleLR.g4" }
   	fn get_rule_names(&self) -> &[& str] { &ruleNames }
   	fn get_vocabulary(&self) -> &dyn Vocabulary { &**VOCABULARY }
	fn sempred(_localctx: Option<&'arena SimpleLRParserNode<'input, 'arena>>, rule_index: i32, pred_index: i32,
			   recog:&mut BaseParserType<'input, 'arena, Input, TF>
	) -> bool {
		match rule_index {
		    1 => SimpleLRParser::<'input, 'arena, Input, TF>::a_sempred(_localctx.and_then(|x| x.as_rule_context()), pred_index, recog),
			_ => true
		}
	}
}

impl<'input, 'arena, Input, TF> SimpleLRParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	fn a_sempred(_ctx: Option<&'arena AContext<'input, 'arena>>, pred_index:i32, recog: &mut <Self as Deref>::Target) -> bool
	 {
		match pred_index {
	        0 => {
			recog.precpred(None, 2)
		    }
		    _ => true
		}
	}
}
//------------------- s ----------------
pub type SContextAll<'input, 'arena> = SContext<'input, 'arena>;

pub type SContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, SContextExt<'input, 'arena>, SimpleLRParserNodeKind>;
#[derive(Debug)]
pub struct SContextExt<'input, 'arena> {
    ph: PhantomData<(&'arena (), &'input ())>,
}

impl<'input: 'arena, 'arena> CustomRuleContext<'input, 'arena> for SContextExt<'input, 'arena>
{
	type NodeKind = SimpleLRParserNodeKind;
    fn node_tag() -> SimpleLRParserNodeKind { SimpleLRParserNodeKind::SContext }
	fn get_rule_index(&self) -> usize { RULE_s }
    fn make_node(
        arena: &'arena Arena,
        ctx: SContext<'input, 'arena>,
    ) -> *mut SimpleLRParserNode<'input, 'arena> {
        arena.alloc_zeroed_node(ctx)}
    fn cast_from<'a>(
        node: &'a SimpleLRParserNode<'input, 'arena>,
    ) -> Option<&'a SContext<'input, 'arena>> {
        if node.node_tag() == Self::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => SContext<'input, 'arena>))
        } else {
            None
        }
    }
    fn cast_from_mut<'a>(
        node: &'a mut SimpleLRParserNode<'input, 'arena>,
    ) -> Option<&'a mut SContext<'input, 'arena>> {
        if node.node_tag() == Self::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => mut SContext<'input, 'arena>))
        } else {
            None
        }
    }
}

impl<'input, 'arena> SContextExt<'input, 'arena>{
	fn create(arena: &'arena Arena, parent: Option<&'arena SimpleLRParserNode<'input, 'arena>>, invoking_state: i32) -> &'arena mut SimpleLRParserNode<'input, 'arena>
    {
        BaseParserRuleContext::create(arena, parent, invoking_state, SContextExt {
				ph: PhantomData
			}
		)
	}
}

pub trait SContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
    fn a(&self) -> Option<&'arena AContextAll<'input, 'arena>>;
}

impl<'input, 'arena> SContextAttrs<'input, 'arena> for SContext<'input, 'arena>
where
    'input: 'arena,
{
    fn a(&self) -> Option<&'arena AContextAll<'input, 'arena>> {
        self.child_of_type(0)
    }
}

impl<'input, 'arena, Input, TF> SimpleLRParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	pub fn s(&mut self,) -> Result<&'arena SContextAll<'input, 'arena>, ANTLRError> {
		let recog = self;
        let _parentctx = recog.base.take_ctx();
        recog.base.enter_rule(SContextExt::create(recog.get_arena(), _parentctx, recog.get_state()), 0, RULE_s)?;
        let _local_ctx_fn = |recog: &Self| -> &'arena SContext {recog.ctx().unwrap().as_rule_context().unwrap()};
		let result: Result<(), ANTLRError> = (|| {
			/*------- Outer Most Alt 1 -------*/
			unsafe { recog.ctx_mut().unwrap().set_alt_number(1); }
			{
			/*InvokeRule a*/
			recog.base.set_state(4);
			recog.a_rec(0)?;
			}
            let tmp = recog.input.lt(-1);
            recog.base.with_mut_ctx(|ctx| { ctx.set_stop(tmp.map(|t| t as _)); });println!("test");
			Ok(())
		})();
		match result {
            Ok(_)=>{},
            Err(e) if !e.is_recoverable() => return Err(e),
            Err(ref re) => {
				recog.err_handler.report_error(&mut recog.base, re);
				recog.err_handler.recover(&mut recog.base, re)?;
			}
		}
		recog.base.exit_rule().map(|ctx: &'arena _| { ctx.as_rule_context().unwrap() })
	}
}
//------------------- a ----------------
pub type AContextAll<'input, 'arena> = AContext<'input, 'arena>;

pub type AContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, AContextExt<'input, 'arena>, SimpleLRParserNodeKind>;
#[derive(Debug)]
pub struct AContextExt<'input, 'arena> {
    ph: PhantomData<(&'arena (), &'input ())>,
}

impl<'input: 'arena, 'arena> CustomRuleContext<'input, 'arena> for AContextExt<'input, 'arena>
{
	type NodeKind = SimpleLRParserNodeKind;
    fn node_tag() -> SimpleLRParserNodeKind { SimpleLRParserNodeKind::AContext }
	fn get_rule_index(&self) -> usize { RULE_a }
    fn make_node(
        arena: &'arena Arena,
        ctx: AContext<'input, 'arena>,
    ) -> *mut SimpleLRParserNode<'input, 'arena> {
        arena.alloc_zeroed_node(ctx)}
    fn cast_from<'a>(
        node: &'a SimpleLRParserNode<'input, 'arena>,
    ) -> Option<&'a AContext<'input, 'arena>> {
        if node.node_tag() == Self::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => AContext<'input, 'arena>))
        } else {
            None
        }
    }
    fn cast_from_mut<'a>(
        node: &'a mut SimpleLRParserNode<'input, 'arena>,
    ) -> Option<&'a mut AContext<'input, 'arena>> {
        if node.node_tag() == Self::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => mut AContext<'input, 'arena>))
        } else {
            None
        }
    }
}

impl<'input, 'arena> AContextExt<'input, 'arena>{
	fn create(arena: &'arena Arena, parent: Option<&'arena SimpleLRParserNode<'input, 'arena>>, invoking_state: i32) -> &'arena mut SimpleLRParserNode<'input, 'arena>
    {
        BaseParserRuleContext::create(arena, parent, invoking_state, AContextExt {
				ph: PhantomData
			}
		)
	}
}

pub trait AContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
    /// Retrieves first TerminalNode corresponding to token ID
    /// Returns `None` if there is no child corresponding to token ID
    fn ID(&self) -> Option<&TerminalNode<'input, 'arena>>;
    fn a(&self) -> Option<&'arena AContextAll<'input, 'arena>>;
}

impl<'input, 'arena> AContextAttrs<'input, 'arena> for AContext<'input, 'arena>
where
    'input: 'arena,
{
    /// Retrieves first TerminalNode corresponding to token ID
    /// Returns `None` if there is no child corresponding to token ID
    fn ID(&self) -> Option<&TerminalNode<'input, 'arena>> {
    	self.get_token(SimpleLR_ID, 0)
    }
    fn a(&self) -> Option<&'arena AContextAll<'input, 'arena>> {
        self.child_of_type(0)
    }
}

impl<'input, 'arena, Input, TF> SimpleLRParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
    #[inline]
	pub fn  a(&mut self,) -> Result<&'arena AContextAll<'input, 'arena>, ANTLRError> {
		self.a_rec(0)
	}

    #[inline]
	fn a_rec(&mut self, _p: i32) -> Result<&'arena AContextAll<'input, 'arena>, ANTLRError> {
        self.a_rec_inner(_p)
    }

	fn a_rec_inner(&mut self, _p: i32) -> Result<&'arena AContextAll<'input, 'arena>, ANTLRError> {
		let recog = self;
		let _parentctx = recog.base.take_ctx();
		let _parentState = recog.base.get_state();
		recog.base.enter_recursion_rule(AContextExt::create(recog.get_arena(), _parentctx, recog.get_state()), 2, RULE_a, _p)?;
        let _local_ctx_fn = |recog: &Self| -> &'arena AContext {recog.ctx().unwrap().as_rule_context().unwrap()};
		let _startState = 2;
		let result: Result<(), ANTLRError> = (|| {
	        let mut _alt: i32;
			/*------- Outer Most Alt 1 -------*/
			unsafe { recog.ctx_mut().unwrap().set_alt_number(1); }
			{
			{
			recog.base.set_state(7);
			recog.base.match_token(SimpleLR_ID,&mut recog.err_handler)?;
			}
			let tmp = recog.input.lt(-1);
			recog.base.with_mut_ctx(|ctx| { ctx.set_stop(tmp.map(|t| t as _)); });
			recog.base.set_state(13);
			recog.err_handler.sync(&mut recog.base)?;
			_alt = recog.interpreter.adaptive_predict(0,&mut recog.base)?;
			while { _alt!=2 && _alt!=INVALID_ALT } {
				if _alt==1 {
					recog.trigger_exit_rule_event()?;
					{
					{
					/*recRuleAltStartAction*/
					let tmp = AContextExt::create(recog.get_arena(), _parentctx, _parentState);
					let _prevctx = recog.push_new_recursion_context(tmp.into(), _startState, RULE_a)?;

					recog.base.set_state(9);
					if !({recog.precpred(None, 2)}) {
						Err(ANTLRError::failed_predicate(&mut recog.base, Some("recog.precpred(None, 2)".to_owned()), None))?;
					}
					recog.base.set_state(10);
					recog.base.match_token(SimpleLR_ID,&mut recog.err_handler)?;
					}
					} 
				}
				recog.base.set_state(15);
				recog.err_handler.sync(&mut recog.base)?;
				_alt = recog.interpreter.adaptive_predict(0,&mut recog.base)?;
			}
			}
			Ok(())
		})();
		match result {
		Ok(_) => {},
        Err(e) if !e.is_recoverable() => return Err(e),
		Err(ref re)=>{
			recog.err_handler.report_error(&mut recog.base, re);
	        recog.err_handler.recover(&mut recog.base, re)?;}
		}
		recog.base.unroll_recursion_context(_parentctx).map(|ctx| { ctx.as_rule_context().unwrap() } )
	}
}

static ATN_SIMULATOR_MANAGER: LazyLock<ATNSimulatorManager> = LazyLock::new(|| ATNSimulatorManager::new(&_ATN));
static _ATN: LazyLock<ATN> =
    LazyLock::new(|| ATNDeserializer::new(None).deserialize(&mut _serializedATN.iter()));
static _serializedATN: LazyLock<Vec<i32>> = LazyLock::new(|| vec![
    4, 1, 2, 17, 2, 0, 7, 0, 2, 1, 7, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 
    1, 1, 1, 5, 1, 12, 8, 1, 10, 1, 12, 1, 15, 9, 1, 1, 1, 0, 1, 2, 2, 0, 
    2, 0, 0, 15, 0, 4, 1, 0, 0, 0, 2, 6, 1, 0, 0, 0, 4, 5, 3, 2, 1, 0, 5, 
    1, 1, 0, 0, 0, 6, 7, 6, 1, -1, 0, 7, 8, 5, 1, 0, 0, 8, 13, 1, 0, 0, 
    0, 9, 10, 10, 2, 0, 0, 10, 12, 5, 1, 0, 0, 11, 9, 1, 0, 0, 0, 12, 15, 
    1, 0, 0, 0, 13, 11, 1, 0, 0, 0, 13, 14, 1, 0, 0, 0, 14, 3, 1, 0, 0, 
    0, 15, 13, 1, 0, 0, 0, 1, 13
]);