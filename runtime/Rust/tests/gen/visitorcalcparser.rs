// Generated from VisitorCalc.g4 by ANTLR 4.13.2
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
use dbt_antlr4::atn_deserializer::ATNDeserializer;
use dbt_antlr4::dfa::DFA;
use dbt_antlr4::atn::{ATN, INVALID_ALT};
use dbt_antlr4::error_strategy::{ErrorStrategy, DefaultErrorStrategy};
use dbt_antlr4::parser_rule_context::{BaseParserRuleContext, ParserRuleContext};
use dbt_antlr4::tree::*;
use dbt_antlr4::token::{TOKEN_EOF,Token};
use dbt_antlr4::int_stream::EOF;
use dbt_antlr4::vocabulary::{Vocabulary,VocabularyImpl};
use dbt_antlr4::token_factory::TokenFactory;
use super::visitorcalclistener::*;
use super::visitorcalcvisitor::*;

use std::marker::PhantomData;
use std::sync::{LazyLock, Arc};
use std::ops::{DerefMut, Deref};

pub const VisitorCalc_INT:i32=1; 
pub const VisitorCalc_MUL:i32=2; 
pub const VisitorCalc_DIV:i32=3; 
pub const VisitorCalc_ADD:i32=4; 
pub const VisitorCalc_SUB:i32=5; 
pub const VisitorCalc_WS:i32=6;
pub const VisitorCalc_EOF:i32=EOF;
pub const RULE_s:usize = 0; 
pub const RULE_expr:usize = 1;
pub const ruleNames: [&'static str; 2] = [
    "s", "expr"
];

pub const _LITERAL_NAMES: [Option<&'static str>;6] = [
	None, None, Some("'*'"), Some("'/'"), Some("'+'"), Some("'-'")
];
pub const _SYMBOLIC_NAMES: [Option<&'static str>;7]  = [
	None, Some("INT"), Some("MUL"), Some("DIV"), Some("ADD"), Some("SUB"), 
	Some("WS")
];

static _shared_context_cache: LazyLock<PredictionContextCache> = LazyLock::new(|| PredictionContextCache::new());
static VOCABULARY: LazyLock<Box<dyn Vocabulary>> = LazyLock::new(|| Box::new(VocabularyImpl::new(_LITERAL_NAMES.iter(), _SYMBOLIC_NAMES.iter(), None)));

pub type BaseParserType<'input, 'arena, Input, TF> = BaseParser<'input, 'arena, VisitorCalcParserExt<'input, 'arena>, VisitorCalcParserContextNode<'input, 'arena>, Input, TF>;

pub struct VisitorCalcParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	base: BaseParserType<'input, 'arena, Input, TF>,
    interpreter: Arc<ParserATNSimulator>,
    pub err_handler: Box<dyn ErrorStrategy<'input, 'arena, TF, BaseParserType<'input, 'arena, Input, TF>> + 'input>,
}

impl<'input, 'arena, Input, TF> VisitorCalcParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    'arena: 'input,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
    pub fn with_strategy(arena: &'arena Arena, input: Input, strategy: Box<dyn ErrorStrategy<'input, 'arena, TF, BaseParserType<'input, 'arena, Input, TF>> + 'input>) -> Self {
		dbt_antlr4::recognizer::check_version("1","0");
		let interpreter = Arc::new(ParserATNSimulator::new(
			&_ATN,
			&_decision_to_DFA,
			&_shared_context_cache,
		));
		Self {
			base: BaseParser::new_base_parser(
				arena,
				input,
				Arc::clone(&interpreter),
				VisitorCalcParserExt {
					_pd: Default::default(),
				}
			),
			interpreter,
            err_handler: strategy,
        }
    }

    pub fn new(arena: &'arena Arena, input: Input) -> Self{
    	Self::with_strategy(arena, input, Box::new(DefaultErrorStrategy::new()))
    }

    pub fn set_error_strategy(&mut self, strategy: Box<dyn ErrorStrategy<'input, 'arena, TF, BaseParserType<'input, 'arena, Input, TF>> + 'input>) {
        self.err_handler = strategy;
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
        L: VisitorCalcListener<'input, 'arena> + 'static,
    {
        let id = ListenerId::new(&listener);
        self.base.add_dyn_parse_listener(listener);
        id
    }
}
pub trait Visitable<'input, 'arena> {
    fn accept<V>(&self, visitor: &mut V) -> Result<V::Return, ANTLRError>
    where
        'input: 'arena,
        V: VisitorCalcVisitor<'input, 'arena> + ?Sized;
}
pub struct VisitorCalcTreeWalker;
impl VisitorCalcTreeWalker
{
    pub fn walk<'input,'arena, L, T>(
        listener: Box<L>,
        tree: &'arena T,
    ) -> Result<Box<L>, ANTLRError>
    where
        'input: 'arena,
        L: VisitorCalcListener<'input, 'arena> + 'static,
        T: NodeInner<'input, 'arena, VisitorCalcParserContextNode<'input, 'arena>>,
    {
        let Some(node) = tree.try_as_node() else {
            return Err(ANTLRError::custom_error("TreeWalker can only walk non-leaf nodes".to_string()));
        };
        let listener_ptr = Box::into_raw(listener);
        let listener = unsafe { Box::from_raw(listener_ptr as *mut <VisitorCalcParserContextNode as RuleNode>::Listener) };
        let listener = dbt_antlr4::tree::ParseTreeWalker::walk(listener, node)?;
        Ok(unsafe { Box::from_raw(Box::into_raw(listener) as *mut L) } )
    }
}

impl<'input, 'arena, Input, TF> Deref for VisitorCalcParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
    type Target = BaseParserType<'input, 'arena, Input, TF>;
    fn deref(&self) -> &Self::Target {
        &self.base
    }
}

impl<'input, 'arena, Input, TF> DerefMut for VisitorCalcParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
    fn deref_mut(&mut self) -> &mut Self::Target {
        &mut self.base
    }
}

#[derive(Debug)]
pub enum VisitorCalcParserContextNode<'input, 'arena> {
    SContext(SContext<'input, 'arena>),
    ExprContext(ExprContextAll<'input, 'arena>),

    Terminal(TerminalNode<'input, 'arena>),
    Error(ErrorNode<'input, 'arena>),
}

dbt_antlr4::impl_defaults! { VisitorCalcParserContextNode }
dbt_antlr4::impl_from_contexts! { VisitorCalcParserContextNode { SContext(SContext),   ExprContext(ExprContextAll), } }
dbt_antlr4::impl_tree! { VisitorCalcParserContextNode { SContext, ExprContext, } }
dbt_antlr4::impl_parse_tree! { VisitorCalcParserContextNode { SContext, ExprContext, } }
dbt_antlr4::impl_rule_context! { VisitorCalcParserContextNode { SContext, ExprContext,  Terminal, Error, } }
dbt_antlr4::impl_parser_rule_context! { VisitorCalcParserContextNode { SContext, ExprContext,  Terminal, Error, } }
dbt_antlr4::impl_rule_node! { VisitorCalcParserContextNode {
    ExprContext, ; SContext(enter_s, exit_s,  visit_s), 
    }; listener = dyn VisitorCalcListener<'input, 'arena>, visitor = VisitorCalcVisitor,
}

pub struct VisitorCalcParserExt<'input, 'arena> {
	_pd: PhantomData<(&'input str, &'arena ())>,
}

impl<'input, 'arena> VisitorCalcParserExt<'input, 'arena> {
}

impl<'input, 'arena, Input, TF> ParserRecog<'input, 'arena, BaseParserType<'input, 'arena, Input, TF>> for VisitorCalcParserExt<'input, 'arena>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{}

impl<'input, 'arena, Input, TF> Actions<'input, 'arena, BaseParserType<'input, 'arena, Input, TF>> for VisitorCalcParserExt<'input, 'arena>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	fn get_grammar_file_name(&self) -> & str{ "VisitorCalc.g4" }
   	fn get_rule_names(&self) -> &[& str] { &ruleNames }
   	fn get_vocabulary(&self) -> &dyn Vocabulary { &**VOCABULARY }
	fn sempred(_localctx: Option<&'arena VisitorCalcParserContextNode<'input, 'arena>>, rule_index: i32, pred_index: i32,
			   recog:&mut BaseParserType<'input, 'arena, Input, TF>
	) -> bool {
		match rule_index {
		    1 => VisitorCalcParser::<'input, 'arena, Input, TF>::expr_sempred(_localctx.and_then(|x| x.as_rule_context()), pred_index, recog),
			_ => true
		}
	}
}

impl<'input, 'arena, Input, TF> VisitorCalcParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	fn expr_sempred(_ctx: Option<&'arena ExprContext<'input, 'arena>>, pred_index:i32, recog: &mut <Self as Deref>::Target) -> bool
	 {
		match pred_index {
	        0 => {
			recog.precpred(None, 2)
		    }
	        1 => {
			recog.precpred(None, 1)
		    }
		    _ => true
		}
	}
}
//------------------- s ----------------
pub type SContextAll<'input, 'arena> = SContext<'input, 'arena>;

pub type SContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, SContextExt<'input, 'arena>>;
dbt_antlr4::impl_visitable! { VisitorCalcVisitor::SContext(visit_s) }
pub struct SContextExt<'input, 'arena> {
    ph: PhantomData<(&'arena (), &'input ())>,
}

impl<'input, 'arena> CustomRuleContext<'input, 'arena> for SContextExt<'input, 'arena>
where
    'input: 'arena,
{
	type Node = VisitorCalcParserContextNode<'input, 'arena>;
	fn get_rule_index(&self) -> usize { RULE_s }
    fn base_ref_from_node(node: &Self::Node) -> Option<&BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            VisitorCalcParserContextNode::SContext(inner) => Some(inner),
            _ => None,
        }
    }
    fn base_mut_ref_from_node(node: &mut Self::Node) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            VisitorCalcParserContextNode::SContext(inner) => Some(inner),
            _ => None,
        }
    }
}

impl<'input, 'arena> SContextExt<'input, 'arena>{
	fn create(arena: &'arena Arena, parent: Option<&'arena VisitorCalcParserContextNode<'input, 'arena>>, invoking_state: i32) -> SContextAll<'input, 'arena>
    where
        'input: 'arena,
    {
        BaseParserRuleContext::new(arena, parent, invoking_state, SContextExt {
				ph: PhantomData
			},
		)
	}
}

pub trait SContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
    fn expr(&self) -> Option<&'arena ExprContextAll<'input, 'arena>>;
    /// Retrieves first TerminalNode corresponding to token EOF
    /// Returns `None` if there is no child corresponding to token EOF
    fn EOF(&self) -> Option<&TerminalNode<'input, 'arena>>;
}

impl<'input, 'arena> SContextAttrs<'input, 'arena> for SContext<'input, 'arena>
where
    'input: 'arena,
{
    fn expr(&self) -> Option<&'arena ExprContextAll<'input, 'arena>> {
        self.child_of_type(0)
    }
    /// Retrieves first TerminalNode corresponding to token EOF
    /// Returns `None` if there is no child corresponding to token EOF
    fn EOF(&self) -> Option<&TerminalNode<'input, 'arena>> {
    	self.get_token(VisitorCalc_EOF, 0)
    }
}

impl<'input, 'arena, Input, TF> VisitorCalcParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    'arena: 'input,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	pub fn s(&mut self,) -> Result<&'arena SContextAll<'input, 'arena>, ANTLRError> {
		let recog = self;
        let _parentctx = recog.base.take_ctx();
        recog.base.enter_rule(SContextExt::create(recog.get_arena(), _parentctx, recog.get_state()).into(), 0, RULE_s)?;
        let _local_ctx_fn = |recog: &Self| -> &'arena SContext {recog.ctx().unwrap().as_rule_context().unwrap()};
		let result: Result<(), ANTLRError> = (|| {
			/*------- Outer Most Alt 1 -------*/
			unsafe { recog.ctx_mut().unwrap().set_alt_number(1); }
			{
			/*InvokeRule expr*/
			recog.base.set_state(4);
			recog.expr_rec(0)?;
			recog.base.set_state(5);
			recog.base.match_token(VisitorCalc_EOF,&mut recog.err_handler)?;
			}
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
//------------------- expr ----------------
#[derive(Debug)]
pub enum ExprContextAll<'input, 'arena> {
	AddContext(AddContext<'input, 'arena>),
	NumberContext(NumberContext<'input, 'arena>),
	MultiplyContext(MultiplyContext<'input, 'arena>),

    Error(ExprContext<'input, 'arena>)
}

dbt_antlr4::impl_into_base_ext! { ExprContextAll::ExprContext { AddContext, NumberContext, MultiplyContext,  } }
dbt_antlr4::impl_rule_context! { ExprContextAll { AddContext, NumberContext, MultiplyContext, Error, } }
dbt_antlr4::impl_parser_rule_context! { ExprContextAll { AddContext, NumberContext, MultiplyContext, Error, } }
dbt_antlr4::impl_tree_trait_delegates! { VisitorCalcParserContextNode::ExprContextAll { AddContext, NumberContext, MultiplyContext, Error, } }
dbt_antlr4::impl_node_inner! { VisitorCalcParserContextNode::ExprContext::ExprContextAll { AddContext, NumberContext, MultiplyContext, Error, } }
dbt_antlr4::impl_listener_dispatch! { VisitorCalcListener::VisitorCalcParserContextNode::ExprContextAll { AddContext(enter_add, exit_add), NumberContext(enter_number, exit_number), MultiplyContext(enter_multiply, exit_multiply), } }
dbt_antlr4::impl_visitable! { VisitorCalcVisitor::ExprContextAll { AddContext(visit_add), NumberContext(visit_number), MultiplyContext(visit_multiply), } }

impl<'input, 'arena> Deref for ExprContextAll<'input, 'arena>{
	type Target = dyn ExprContextAttrs<'input, 'arena> + 'arena;
	fn deref(&self) -> &Self::Target{
		use ExprContextAll::*;
		match self{
			AddContext(inner) => inner,
			NumberContext(inner) => inner,
			MultiplyContext(inner) => inner,
            Error(inner) => inner
		}
	}
}

pub type ExprContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, ExprContextExt<'input, 'arena>>;
pub struct ExprContextExt<'input, 'arena> {
    ph: PhantomData<(&'arena (), &'input ())>,
}

impl<'input, 'arena> CustomRuleContext<'input, 'arena> for ExprContextExt<'input, 'arena>
where
    'input: 'arena,
{
	type Node = VisitorCalcParserContextNode<'input, 'arena>;
	fn get_rule_index(&self) -> usize { RULE_expr }
    fn base_ref_from_node(node: &Self::Node) -> Option<&BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            VisitorCalcParserContextNode::ExprContext(ExprContextAll::Error(inner)) => Some(inner),
            _ => None,
        }
    }
    fn base_mut_ref_from_node(node: &mut Self::Node) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            VisitorCalcParserContextNode::ExprContext(ExprContextAll::Error(inner)) => Some(inner),
            _ => None,
        }
    }
}

impl<'input, 'arena> ExprContextExt<'input, 'arena>{
	fn create(arena: &'arena Arena, parent: Option<&'arena VisitorCalcParserContextNode<'input, 'arena>>, invoking_state: i32) -> ExprContextAll<'input, 'arena>
    where
        'input: 'arena,
    {
		ExprContextAll::Error(
        BaseParserRuleContext::new(arena, parent, invoking_state, ExprContextExt {
				ph: PhantomData
			}),
		)
	}
}

pub trait ExprContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
}

impl<'input, 'arena> ExprContextAttrs<'input, 'arena> for ExprContext<'input, 'arena>
where
    'input: 'arena,
{
}

pub type AddContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, AddContextExt<'input, 'arena>>;

pub trait AddContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
	fn expr_all(&self) -> Vec<&'arena ExprContextAll<'input, 'arena>>;
	fn expr(&self, i: usize) -> Option<&'arena ExprContextAll<'input, 'arena>>;
	/// Retrieves first TerminalNode corresponding to token ADD
	/// Returns `None` if there is no child corresponding to token ADD
	fn ADD(&self) -> Option<&TerminalNode<'input, 'arena>>;
	/// Retrieves first TerminalNode corresponding to token SUB
	/// Returns `None` if there is no child corresponding to token SUB
	fn SUB(&self) -> Option<&TerminalNode<'input, 'arena>>;
}

impl<'input, 'arena> AddContextAttrs<'input, 'arena> for AddContext<'input, 'arena>
where
    'input: 'arena,
{
    fn expr_all(&self) -> Vec<&'arena ExprContextAll<'input, 'arena>> {
        self.children_of_type()
    }
    fn expr(&self, i: usize) -> Option<&'arena ExprContextAll<'input, 'arena>> {
        self.child_of_type(i)
    }
    /// Retrieves first TerminalNode corresponding to token ADD
    /// Returns `None` if there is no child corresponding to token ADD
    fn ADD(&self) -> Option<&TerminalNode<'input, 'arena>> {
    	self.get_token(VisitorCalc_ADD, 0)
    }
    /// Retrieves first TerminalNode corresponding to token SUB
    /// Returns `None` if there is no child corresponding to token SUB
    fn SUB(&self) -> Option<&TerminalNode<'input, 'arena>> {
    	self.get_token(VisitorCalc_SUB, 0)
    }
}

pub struct AddContextExt<'input, 'arena> {
	base: ExprContextExt<'input, 'arena>,
    pd: PhantomData<(&'arena (), &'input ())>
}

impl<'input, 'arena> CustomRuleContext<'input, 'arena> for AddContextExt<'input, 'arena>
where
    'input: 'arena,
{
	type Node = VisitorCalcParserContextNode<'input, 'arena>;
	fn get_rule_index(&self) -> usize { RULE_expr }
    fn base_ref_from_node(node: &Self::Node) -> Option<&BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            VisitorCalcParserContextNode::ExprContext(ExprContextAll::AddContext(inner)) => Some(inner),
            _ => None,
        }
    }
    fn base_mut_ref_from_node(node: &mut Self::Node) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            VisitorCalcParserContextNode::ExprContext(ExprContextAll::AddContext(inner)) => Some(inner),
            _ => None,
        }
    }
}

impl<'input, 'arena> ExprContextAttrs<'input, 'arena> for AddContext<'input, 'arena>
where
    'input: 'arena,
{
}

impl<'input, 'arena> AddContextExt<'input, 'arena> {
	fn new(base: ExprContextExt<'input, 'arena>) -> Self {
        Self {
            base,
            pd: PhantomData
        }
    }

	fn copy_from(src: VisitorCalcParserContextNode<'input, 'arena>) -> ExprContextAll<'input, 'arena>
    {
        let VisitorCalcParserContextNode::ExprContext(src) = src else {
            panic!("invalid node type for copy_from!");
        };
        ExprContextAll::AddContext(
            BaseParserRuleContext::copy_from(src.into_base_ext(), |ext_src| Self::new(ext_src))
        )
	}
}

pub type NumberContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, NumberContextExt<'input, 'arena>>;

pub trait NumberContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
	/// Retrieves first TerminalNode corresponding to token INT
	/// Returns `None` if there is no child corresponding to token INT
	fn INT(&self) -> Option<&TerminalNode<'input, 'arena>>;
}

impl<'input, 'arena> NumberContextAttrs<'input, 'arena> for NumberContext<'input, 'arena>
where
    'input: 'arena,
{
    /// Retrieves first TerminalNode corresponding to token INT
    /// Returns `None` if there is no child corresponding to token INT
    fn INT(&self) -> Option<&TerminalNode<'input, 'arena>> {
    	self.get_token(VisitorCalc_INT, 0)
    }
}

pub struct NumberContextExt<'input, 'arena> {
	base: ExprContextExt<'input, 'arena>,
    pd: PhantomData<(&'arena (), &'input ())>
}

impl<'input, 'arena> CustomRuleContext<'input, 'arena> for NumberContextExt<'input, 'arena>
where
    'input: 'arena,
{
	type Node = VisitorCalcParserContextNode<'input, 'arena>;
	fn get_rule_index(&self) -> usize { RULE_expr }
    fn base_ref_from_node(node: &Self::Node) -> Option<&BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            VisitorCalcParserContextNode::ExprContext(ExprContextAll::NumberContext(inner)) => Some(inner),
            _ => None,
        }
    }
    fn base_mut_ref_from_node(node: &mut Self::Node) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            VisitorCalcParserContextNode::ExprContext(ExprContextAll::NumberContext(inner)) => Some(inner),
            _ => None,
        }
    }
}

impl<'input, 'arena> ExprContextAttrs<'input, 'arena> for NumberContext<'input, 'arena>
where
    'input: 'arena,
{
}

impl<'input, 'arena> NumberContextExt<'input, 'arena> {
	fn new(base: ExprContextExt<'input, 'arena>) -> Self {
        Self {
            base,
            pd: PhantomData
        }
    }

	fn copy_from(src: VisitorCalcParserContextNode<'input, 'arena>) -> ExprContextAll<'input, 'arena>
    {
        let VisitorCalcParserContextNode::ExprContext(src) = src else {
            panic!("invalid node type for copy_from!");
        };
        ExprContextAll::NumberContext(
            BaseParserRuleContext::copy_from(src.into_base_ext(), |ext_src| Self::new(ext_src))
        )
	}
}

pub type MultiplyContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, MultiplyContextExt<'input, 'arena>>;

pub trait MultiplyContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
	fn expr_all(&self) -> Vec<&'arena ExprContextAll<'input, 'arena>>;
	fn expr(&self, i: usize) -> Option<&'arena ExprContextAll<'input, 'arena>>;
	/// Retrieves first TerminalNode corresponding to token MUL
	/// Returns `None` if there is no child corresponding to token MUL
	fn MUL(&self) -> Option<&TerminalNode<'input, 'arena>>;
	/// Retrieves first TerminalNode corresponding to token DIV
	/// Returns `None` if there is no child corresponding to token DIV
	fn DIV(&self) -> Option<&TerminalNode<'input, 'arena>>;
}

impl<'input, 'arena> MultiplyContextAttrs<'input, 'arena> for MultiplyContext<'input, 'arena>
where
    'input: 'arena,
{
    fn expr_all(&self) -> Vec<&'arena ExprContextAll<'input, 'arena>> {
        self.children_of_type()
    }
    fn expr(&self, i: usize) -> Option<&'arena ExprContextAll<'input, 'arena>> {
        self.child_of_type(i)
    }
    /// Retrieves first TerminalNode corresponding to token MUL
    /// Returns `None` if there is no child corresponding to token MUL
    fn MUL(&self) -> Option<&TerminalNode<'input, 'arena>> {
    	self.get_token(VisitorCalc_MUL, 0)
    }
    /// Retrieves first TerminalNode corresponding to token DIV
    /// Returns `None` if there is no child corresponding to token DIV
    fn DIV(&self) -> Option<&TerminalNode<'input, 'arena>> {
    	self.get_token(VisitorCalc_DIV, 0)
    }
}

pub struct MultiplyContextExt<'input, 'arena> {
	base: ExprContextExt<'input, 'arena>,
    pd: PhantomData<(&'arena (), &'input ())>
}

impl<'input, 'arena> CustomRuleContext<'input, 'arena> for MultiplyContextExt<'input, 'arena>
where
    'input: 'arena,
{
	type Node = VisitorCalcParserContextNode<'input, 'arena>;
	fn get_rule_index(&self) -> usize { RULE_expr }
    fn base_ref_from_node(node: &Self::Node) -> Option<&BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            VisitorCalcParserContextNode::ExprContext(ExprContextAll::MultiplyContext(inner)) => Some(inner),
            _ => None,
        }
    }
    fn base_mut_ref_from_node(node: &mut Self::Node) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            VisitorCalcParserContextNode::ExprContext(ExprContextAll::MultiplyContext(inner)) => Some(inner),
            _ => None,
        }
    }
}

impl<'input, 'arena> ExprContextAttrs<'input, 'arena> for MultiplyContext<'input, 'arena>
where
    'input: 'arena,
{
}

impl<'input, 'arena> MultiplyContextExt<'input, 'arena> {
	fn new(base: ExprContextExt<'input, 'arena>) -> Self {
        Self {
            base,
            pd: PhantomData
        }
    }

	fn copy_from(src: VisitorCalcParserContextNode<'input, 'arena>) -> ExprContextAll<'input, 'arena>
    {
        let VisitorCalcParserContextNode::ExprContext(src) = src else {
            panic!("invalid node type for copy_from!");
        };
        ExprContextAll::MultiplyContext(
            BaseParserRuleContext::copy_from(src.into_base_ext(), |ext_src| Self::new(ext_src))
        )
	}
}

impl<'input, 'arena, Input, TF> VisitorCalcParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    'arena: 'input,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	pub fn  expr(&mut self,) -> Result<&'arena ExprContextAll<'input, 'arena>, ANTLRError> {
		self.expr_rec(0)
	}

	fn expr_rec(&mut self, _p: i32) -> Result<&'arena ExprContextAll<'input, 'arena>, ANTLRError> {
		let recog = self;
		let _parentctx = recog.base.take_ctx();
		let _parentState = recog.base.get_state();
		recog.base.enter_recursion_rule(ExprContextExt::create(recog.get_arena(), _parentctx, recog.get_state()).into(), 2, RULE_expr, _p)?;
        let _local_ctx_fn = |recog: &Self| -> &'arena ExprContext {recog.ctx().unwrap().as_rule_context().unwrap()};
		let _startState = 2;
		let mut _la: i32 = -1;
		let result: Result<(), ANTLRError> = (|| {
	        let mut _alt: i32;
			/*------- Outer Most Alt 1 -------*/
			unsafe { recog.ctx_mut().unwrap().set_alt_number(1); }
			{
			{
			recog.base.with_mut_ctx(|ctx| {
			    let tmp = std::mem::take(ctx);
			    *ctx = NumberContextExt::copy_from(tmp).into();
			});
			let _local_ctx_fn = |recog: &Self| -> &'arena NumberContext {recog.ctx().unwrap().as_rule_context().unwrap()};

			recog.base.set_state(8);
			recog.base.match_token(VisitorCalc_INT,&mut recog.err_handler)?;
			}
			let tmp = recog.input.lt(-1);
			recog.base.with_mut_ctx(|ctx| { ctx.set_stop(tmp.map(|t| t as _)); });
			recog.base.set_state(18);
			recog.err_handler.sync(&mut recog.base)?;
			_alt = recog.interpreter.adaptive_predict(1,&mut recog.base)?;
			while { _alt!=2 && _alt!=INVALID_ALT } {
				if _alt==1 {
					recog.trigger_exit_rule_event()?;
					{
					recog.base.set_state(16);
					recog.err_handler.sync(&mut recog.base)?;
					match recog.interpreter.adaptive_predict(0,&mut recog.base)? {
						1 =>{
							{
							/*recRuleLabeledAltStartAction*/
							let tmp = MultiplyContextExt::copy_from(ExprContextExt::create(recog.get_arena(), _parentctx, _parentState).into());
							let _prevctx = recog.push_new_recursion_context(tmp.into(), _startState, RULE_expr)?;
							let _local_ctx_fn = |recog: &Self| -> &'arena MultiplyContext {recog.ctx().unwrap().as_rule_context().unwrap()};

							recog.base.set_state(10);
							if !({recog.precpred(None, 2)}) {
								Err(ANTLRError::failed_predicate(&mut recog.base, Some("recog.precpred(None, 2)".to_owned()), None))?;
							}
							recog.base.set_state(11);
							_la = recog.base.input.la(1);
							if { !(_la==VisitorCalc_MUL || _la==VisitorCalc_DIV) } {
								recog.err_handler.recover_inline(&mut recog.base)?;
							}
							else {
								if recog.base.input.la(1)==TOKEN_EOF { recog.base.matched_eof = true };
								recog.err_handler.report_match(&mut recog.base);
								recog.base.consume(&mut recog.err_handler)?;
							}
							/*InvokeRule expr*/
							recog.base.set_state(12);
							recog.expr_rec(3)?;
							}
						}
					,
						2 =>{
							{
							/*recRuleLabeledAltStartAction*/
							let tmp = AddContextExt::copy_from(ExprContextExt::create(recog.get_arena(), _parentctx, _parentState).into());
							let _prevctx = recog.push_new_recursion_context(tmp.into(), _startState, RULE_expr)?;
							let _local_ctx_fn = |recog: &Self| -> &'arena AddContext {recog.ctx().unwrap().as_rule_context().unwrap()};

							recog.base.set_state(13);
							if !({recog.precpred(None, 1)}) {
								Err(ANTLRError::failed_predicate(&mut recog.base, Some("recog.precpred(None, 1)".to_owned()), None))?;
							}
							recog.base.set_state(14);
							_la = recog.base.input.la(1);
							if { !(_la==VisitorCalc_ADD || _la==VisitorCalc_SUB) } {
								recog.err_handler.recover_inline(&mut recog.base)?;
							}
							else {
								if recog.base.input.la(1)==TOKEN_EOF { recog.base.matched_eof = true };
								recog.err_handler.report_match(&mut recog.base);
								recog.base.consume(&mut recog.err_handler)?;
							}
							/*InvokeRule expr*/
							recog.base.set_state(15);
							recog.expr_rec(2)?;
							}
						}

						_ => {}
					}
					} 
				}
				recog.base.set_state(20);
				recog.err_handler.sync(&mut recog.base)?;
				_alt = recog.interpreter.adaptive_predict(1,&mut recog.base)?;
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

static _ATN: LazyLock<ATN> =
    LazyLock::new(|| ATNDeserializer::new(None).deserialize(&mut _serializedATN.iter()));
static _decision_to_DFA: LazyLock<Vec<DFA>> = LazyLock::new(|| {
    let size = _ATN.decision_to_state.len() as i32;
    let mut dfa = Vec::with_capacity(size as usize);
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
    4, 1, 6, 22, 2, 0, 7, 0, 2, 1, 7, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 1, 17, 8, 1, 10, 1, 12, 1, 
    20, 9, 1, 1, 1, 0, 1, 2, 2, 0, 2, 0, 2, 1, 0, 2, 3, 1, 0, 4, 5, 21, 
    0, 4, 1, 0, 0, 0, 2, 7, 1, 0, 0, 0, 4, 5, 3, 2, 1, 0, 5, 6, 5, 0, 0, 
    1, 6, 1, 1, 0, 0, 0, 7, 8, 6, 1, -1, 0, 8, 9, 5, 1, 0, 0, 9, 18, 1, 
    0, 0, 0, 10, 11, 10, 2, 0, 0, 11, 12, 7, 0, 0, 0, 12, 17, 3, 2, 1, 3, 
    13, 14, 10, 1, 0, 0, 14, 15, 7, 1, 0, 0, 15, 17, 3, 2, 1, 2, 16, 10, 
    1, 0, 0, 0, 16, 13, 1, 0, 0, 0, 17, 20, 1, 0, 0, 0, 18, 16, 1, 0, 0, 
    0, 18, 19, 1, 0, 0, 0, 19, 3, 1, 0, 0, 0, 20, 18, 1, 0, 0, 0, 2, 16, 
    18
]);