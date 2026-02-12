// Generated from Labels.g4 by ANTLR 4.13.2
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
use super::labelslistener::*;
use std::marker::PhantomData;
use std::sync::{LazyLock, Arc};
use std::ops::{DerefMut, Deref};

pub const Labels_T__0:i32=1; 
pub const Labels_T__1:i32=2; 
pub const Labels_T__2:i32=3; 
pub const Labels_T__3:i32=4; 
pub const Labels_T__4:i32=5; 
pub const Labels_T__5:i32=6; 
pub const Labels_ID:i32=7; 
pub const Labels_INT:i32=8; 
pub const Labels_WS:i32=9;
pub const Labels_EOF:i32=EOF;
pub const RULE_s:usize = 0; 
pub const RULE_e:usize = 1;
pub const ruleNames: [&'static str; 2] = [
    "s", "e"
];

pub const _LITERAL_NAMES: [Option<&'static str>;7] = [
	None, Some("'*'"), Some("'+'"), Some("'('"), Some("')'"), Some("'++'"), 
	Some("'--'")
];
pub const _SYMBOLIC_NAMES: [Option<&'static str>;10]  = [
	None, None, None, None, None, None, None, Some("ID"), Some("INT"), Some("WS")
];

static _shared_context_cache: LazyLock<PredictionContextCache> = LazyLock::new(|| PredictionContextCache::new());
static VOCABULARY: LazyLock<Box<dyn Vocabulary>> = LazyLock::new(|| Box::new(VocabularyImpl::new(_LITERAL_NAMES.iter(), _SYMBOLIC_NAMES.iter(), None)));

pub type BaseParserType<'input, 'arena, Input, TF> = BaseParser<'input, 'arena, LabelsParserExt<'input, 'arena>, LabelsParserContextNode<'input, 'arena>, Input, TF>;

pub struct LabelsParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	base: BaseParserType<'input, 'arena, Input, TF>,
    interpreter: Arc<ParserATNSimulator>,
    pub err_handler: Box<dyn ErrorStrategy<'input, 'arena, TF, BaseParserType<'input, 'arena, Input, TF>> + 'input>,
}

impl<'input, 'arena, Input, TF> LabelsParser<'input, 'arena, Input, TF>
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
				LabelsParserExt {
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
        L: LabelsListener<'input, 'arena> + 'static,
    {
        let id = ListenerId::new(&listener);
        self.base.add_dyn_parse_listener(listener);
        id
    }
}
pub struct LabelsTreeWalker;
impl LabelsTreeWalker
{
    pub fn walk<'input,'arena, L, T>(
        listener: Box<L>,
        tree: &'arena T,
    ) -> Result<Box<L>, ANTLRError>
    where
        'input: 'arena,
        L: LabelsListener<'input, 'arena> + 'static,
        T: NodeInner<'input, 'arena, LabelsParserContextNode<'input, 'arena>>,
    {
        let Some(node) = tree.try_as_node() else {
            return Err(ANTLRError::custom_error("TreeWalker can only walk non-leaf nodes".to_string()));
        };
        let listener_ptr = Box::into_raw(listener);
        let listener = unsafe { Box::from_raw(listener_ptr as *mut <LabelsParserContextNode as RuleNode>::Listener) };
        let listener = ParseTreeWalker::walk(listener, node)?;
        Ok(unsafe { Box::from_raw(Box::into_raw(listener) as *mut L) } )
    }
}

impl<'input, 'arena, Input, TF> Deref for LabelsParser<'input, 'arena, Input, TF>
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

impl<'input, 'arena, Input, TF> DerefMut for LabelsParser<'input, 'arena, Input, TF>
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
pub enum LabelsParserContextNode<'input, 'arena> {
    SContext(SContext<'input, 'arena>),
    EContext(EContextAll<'input, 'arena>),

    Terminal(TerminalNode<'input, 'arena>),
    Error(ErrorNode<'input, 'arena>),
}

dbt_antlr4::impl_defaults! { LabelsParserContextNode }
dbt_antlr4::impl_from_contexts! { LabelsParserContextNode { SContext(SContext),   EContext(EContextAll), } }
dbt_antlr4::impl_tree! { LabelsParserContextNode { SContext, EContext, } }
dbt_antlr4::impl_parse_tree! { LabelsParserContextNode { SContext, EContext, } }
dbt_antlr4::impl_rule_context! { LabelsParserContextNode { SContext, EContext,  Terminal, Error, } }
dbt_antlr4::impl_parser_rule_context! { LabelsParserContextNode { SContext, EContext,  Terminal, Error, } }
dbt_antlr4::impl_rule_node! { LabelsParserContextNode {
    EContext, ; SContext(enter_s, exit_s, ), 
    }; listener = dyn LabelsListener<'input, 'arena>,
}

pub struct LabelsParserExt<'input, 'arena> {
	_pd: PhantomData<(&'input str, &'arena ())>,
}

impl<'input, 'arena> LabelsParserExt<'input, 'arena> {
}

impl<'input, 'arena, Input, TF> ParserRecog<'input, 'arena, BaseParserType<'input, 'arena, Input, TF>> for LabelsParserExt<'input, 'arena>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{}

impl<'input, 'arena, Input, TF> Actions<'input, 'arena, BaseParserType<'input, 'arena, Input, TF>> for LabelsParserExt<'input, 'arena>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	fn get_grammar_file_name(&self) -> & str{ "Labels.g4" }
   	fn get_rule_names(&self) -> &[& str] { &ruleNames }
   	fn get_vocabulary(&self) -> &dyn Vocabulary { &**VOCABULARY }
	fn sempred(_localctx: Option<&'arena LabelsParserContextNode<'input, 'arena>>, rule_index: i32, pred_index: i32,
			   recog:&mut BaseParserType<'input, 'arena, Input, TF>
	) -> bool {
		match rule_index {
		    1 => LabelsParser::<'input, 'arena, Input, TF>::e_sempred(_localctx.and_then(|x| x.as_rule_context()), pred_index, recog),
			_ => true
		}
	}
}

impl<'input, 'arena, Input, TF> LabelsParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	fn e_sempred(_ctx: Option<&'arena EContext<'input, 'arena>>, pred_index:i32, recog: &mut <Self as Deref>::Target) -> bool
	 {
		match pred_index {
	        0 => {
			recog.precpred(None, 7)
		    }
	        1 => {
			recog.precpred(None, 6)
		    }
	        2 => {
			recog.precpred(None, 3)
		    }
	        3 => {
			recog.precpred(None, 2)
		    }
		    _ => true
		}
	}
}
//------------------- s ----------------
pub type SContextAll<'input, 'arena> = SContext<'input, 'arena>;

pub type SContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, SContextExt<'input, 'arena>>;
pub struct SContextExt<'input, 'arena> {
	pub q: Option<&'arena EContextAll<'input, 'arena>>,
    ph: PhantomData<(&'arena (), &'input ())>,
}

impl<'input, 'arena> CustomRuleContext<'input, 'arena> for SContextExt<'input, 'arena>
where
    'input: 'arena,
{
	type Node = LabelsParserContextNode<'input, 'arena>;
	fn get_rule_index(&self) -> usize { RULE_s }
    fn base_ref_from_node(node: &Self::Node) -> Option<&BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::SContext(inner) => Some(inner),
            _ => None,
        }
    }
    fn base_mut_ref_from_node(node: &mut Self::Node) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::SContext(inner) => Some(inner),
            _ => None,
        }
    }
}

impl<'input, 'arena> SContextExt<'input, 'arena>{
	fn create(arena: &'arena Arena, parent: Option<&'arena LabelsParserContextNode<'input, 'arena>>, invoking_state: i32) -> SContextAll<'input, 'arena>
    where
        'input: 'arena,
    {
        BaseParserRuleContext::new(arena, parent, invoking_state, SContextExt {
				q: None, 
				ph: PhantomData
			},
		)
	}
}

pub trait SContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
    fn e(&self) -> Option<&'arena EContextAll<'input, 'arena>>;
}

impl<'input, 'arena> SContextAttrs<'input, 'arena> for SContext<'input, 'arena>
where
    'input: 'arena,
{
    fn e(&self) -> Option<&'arena EContextAll<'input, 'arena>> {
        self.child_of_type(0)
    }
}

impl<'input, 'arena, Input, TF> LabelsParser<'input, 'arena, Input, TF>
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
			/*InvokeRule e*/
			recog.base.set_state(4);
			let tmp = recog.e_rec(0)?;
			unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<SContext>().unwrap().q = Some(tmp); } 
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
//------------------- e ----------------
#[derive(Debug)]
pub enum EContextAll<'input, 'arena> {
	AddContext(AddContext<'input, 'arena>),
	ParensContext(ParensContext<'input, 'arena>),
	MultContext(MultContext<'input, 'arena>),
	DecContext(DecContext<'input, 'arena>),
	AnIDContext(AnIDContext<'input, 'arena>),
	AnIntContext(AnIntContext<'input, 'arena>),
	IncContext(IncContext<'input, 'arena>),

    Error(EContext<'input, 'arena>)
}

dbt_antlr4::impl_into_base_ext! { EContextAll::EContext { AddContext, ParensContext, MultContext, DecContext, AnIDContext, AnIntContext, IncContext,  } }
dbt_antlr4::impl_rule_context! { EContextAll { AddContext, ParensContext, MultContext, DecContext, AnIDContext, AnIntContext, IncContext, Error, } }
dbt_antlr4::impl_parser_rule_context! { EContextAll { AddContext, ParensContext, MultContext, DecContext, AnIDContext, AnIntContext, IncContext, Error, } }
dbt_antlr4::impl_tree_trait_delegates! { LabelsParserContextNode::EContextAll { AddContext, ParensContext, MultContext, DecContext, AnIDContext, AnIntContext, IncContext, Error, } }
dbt_antlr4::impl_node_inner! { LabelsParserContextNode::EContext::EContextAll { AddContext, ParensContext, MultContext, DecContext, AnIDContext, AnIntContext, IncContext, Error, } }
dbt_antlr4::impl_listener_dispatch! { LabelsListener::LabelsParserContextNode::EContextAll { AddContext(enter_add, exit_add), ParensContext(enter_parens, exit_parens), MultContext(enter_mult, exit_mult), DecContext(enter_dec, exit_dec), AnIDContext(enter_anID, exit_anID), AnIntContext(enter_anInt, exit_anInt), IncContext(enter_inc, exit_inc), } }

impl<'input, 'arena> Deref for EContextAll<'input, 'arena>{
	type Target = dyn EContextAttrs<'input, 'arena> + 'arena;
	fn deref(&self) -> &Self::Target{
		use EContextAll::*;
		match self{
			AddContext(inner) => inner,
			ParensContext(inner) => inner,
			MultContext(inner) => inner,
			DecContext(inner) => inner,
			AnIDContext(inner) => inner,
			AnIntContext(inner) => inner,
			IncContext(inner) => inner,
            Error(inner) => inner
		}
	}
}

pub type EContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, EContextExt<'input, 'arena>>;
pub struct EContextExt<'input, 'arena> {
	pub v: String,
    ph: PhantomData<(&'arena (), &'input ())>,
}

impl<'input, 'arena> CustomRuleContext<'input, 'arena> for EContextExt<'input, 'arena>
where
    'input: 'arena,
{
	type Node = LabelsParserContextNode<'input, 'arena>;
	fn get_rule_index(&self) -> usize { RULE_e }
    fn base_ref_from_node(node: &Self::Node) -> Option<&BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::EContext(EContextAll::Error(inner)) => Some(inner),
            _ => None,
        }
    }
    fn base_mut_ref_from_node(node: &mut Self::Node) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::EContext(EContextAll::Error(inner)) => Some(inner),
            _ => None,
        }
    }
}

impl<'input, 'arena> EContextExt<'input, 'arena>{
	fn create(arena: &'arena Arena, parent: Option<&'arena LabelsParserContextNode<'input, 'arena>>, invoking_state: i32) -> EContextAll<'input, 'arena>
    where
        'input: 'arena,
    {
		let mut _init_v = String::new();

		EContextAll::Error(
        BaseParserRuleContext::new(arena, parent, invoking_state, EContextExt {
				v: _init_v, 
				ph: PhantomData
			}),
		)
	}
}

pub trait EContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
    fn get_v(&self) -> &String; 
    fn set_v(&mut self,attr: String); 
}

impl<'input, 'arena> EContextAttrs<'input, 'arena> for EContext<'input, 'arena>
where
    'input: 'arena,
{
    fn get_v(&self) -> &String { &self.deref().v }  
    fn set_v(&mut self,attr: String) { self.deref_mut().v = attr; }  
}

pub type AddContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, AddContextExt<'input, 'arena>>;

pub trait AddContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
	fn e_all(&self) -> Vec<&'arena EContextAll<'input, 'arena>>;
	fn e(&self, i: usize) -> Option<&'arena EContextAll<'input, 'arena>>;
}

impl<'input, 'arena> AddContextAttrs<'input, 'arena> for AddContext<'input, 'arena>
where
    'input: 'arena,
{
    fn e_all(&self) -> Vec<&'arena EContextAll<'input, 'arena>> {
        self.children_of_type()
    }
    fn e(&self, i: usize) -> Option<&'arena EContextAll<'input, 'arena>> {
        self.child_of_type(i)
    }
}

pub struct AddContextExt<'input, 'arena> {
	base: EContextExt<'input, 'arena>,
	pub a: Option<&'arena EContextAll<'input, 'arena>>,
	pub b: Option<&'arena EContextAll<'input, 'arena>>,
    pd: PhantomData<(&'arena (), &'input ())>
}

impl<'input, 'arena> CustomRuleContext<'input, 'arena> for AddContextExt<'input, 'arena>
where
    'input: 'arena,
{
	type Node = LabelsParserContextNode<'input, 'arena>;
	fn get_rule_index(&self) -> usize { RULE_e }
    fn base_ref_from_node(node: &Self::Node) -> Option<&BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::EContext(EContextAll::AddContext(inner)) => Some(inner),
            _ => None,
        }
    }
    fn base_mut_ref_from_node(node: &mut Self::Node) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::EContext(EContextAll::AddContext(inner)) => Some(inner),
            _ => None,
        }
    }
}

impl<'input, 'arena> EContextAttrs<'input, 'arena> for AddContext<'input, 'arena>
where
    'input: 'arena,
{
    fn get_v(&self) -> &String { &self.deref().base.v }  
    fn set_v(&mut self,attr: String) { self.deref_mut().base.v = attr; }  
}

impl<'input, 'arena> AddContextExt<'input, 'arena> {
	fn new(base: EContextExt<'input, 'arena>) -> Self {
        Self {
            base,
            a:None, b:None, 
            pd: PhantomData
        }
    }

	fn copy_from(src: LabelsParserContextNode<'input, 'arena>) -> EContextAll<'input, 'arena>
    {
        let LabelsParserContextNode::EContext(src) = src else {
            panic!("invalid node type for copy_from!");
        };
        EContextAll::AddContext(
            BaseParserRuleContext::copy_from(src.into_base_ext(), |ext_src| Self::new(ext_src))
        )
	}
}

pub type ParensContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, ParensContextExt<'input, 'arena>>;

pub trait ParensContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
	fn e(&self) -> Option<&'arena EContextAll<'input, 'arena>>;
}

impl<'input, 'arena> ParensContextAttrs<'input, 'arena> for ParensContext<'input, 'arena>
where
    'input: 'arena,
{
    fn e(&self) -> Option<&'arena EContextAll<'input, 'arena>> {
        self.child_of_type(0)
    }
}

pub struct ParensContextExt<'input, 'arena> {
	base: EContextExt<'input, 'arena>,
	pub x: Option<&'arena EContextAll<'input, 'arena>>,
    pd: PhantomData<(&'arena (), &'input ())>
}

impl<'input, 'arena> CustomRuleContext<'input, 'arena> for ParensContextExt<'input, 'arena>
where
    'input: 'arena,
{
	type Node = LabelsParserContextNode<'input, 'arena>;
	fn get_rule_index(&self) -> usize { RULE_e }
    fn base_ref_from_node(node: &Self::Node) -> Option<&BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::EContext(EContextAll::ParensContext(inner)) => Some(inner),
            _ => None,
        }
    }
    fn base_mut_ref_from_node(node: &mut Self::Node) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::EContext(EContextAll::ParensContext(inner)) => Some(inner),
            _ => None,
        }
    }
}

impl<'input, 'arena> EContextAttrs<'input, 'arena> for ParensContext<'input, 'arena>
where
    'input: 'arena,
{
    fn get_v(&self) -> &String { &self.deref().base.v }  
    fn set_v(&mut self,attr: String) { self.deref_mut().base.v = attr; }  
}

impl<'input, 'arena> ParensContextExt<'input, 'arena> {
	fn new(base: EContextExt<'input, 'arena>) -> Self {
        Self {
            base,
            x:None, 
            pd: PhantomData
        }
    }

	fn copy_from(src: LabelsParserContextNode<'input, 'arena>) -> EContextAll<'input, 'arena>
    {
        let LabelsParserContextNode::EContext(src) = src else {
            panic!("invalid node type for copy_from!");
        };
        EContextAll::ParensContext(
            BaseParserRuleContext::copy_from(src.into_base_ext(), |ext_src| Self::new(ext_src))
        )
	}
}

pub type MultContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, MultContextExt<'input, 'arena>>;

pub trait MultContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
	fn e_all(&self) -> Vec<&'arena EContextAll<'input, 'arena>>;
	fn e(&self, i: usize) -> Option<&'arena EContextAll<'input, 'arena>>;
}

impl<'input, 'arena> MultContextAttrs<'input, 'arena> for MultContext<'input, 'arena>
where
    'input: 'arena,
{
    fn e_all(&self) -> Vec<&'arena EContextAll<'input, 'arena>> {
        self.children_of_type()
    }
    fn e(&self, i: usize) -> Option<&'arena EContextAll<'input, 'arena>> {
        self.child_of_type(i)
    }
}

pub struct MultContextExt<'input, 'arena> {
	base: EContextExt<'input, 'arena>,
	pub a: Option<&'arena EContextAll<'input, 'arena>>,
	pub op: Option<&'arena dyn Token >,
	pub b: Option<&'arena EContextAll<'input, 'arena>>,
    pd: PhantomData<(&'arena (), &'input ())>
}

impl<'input, 'arena> CustomRuleContext<'input, 'arena> for MultContextExt<'input, 'arena>
where
    'input: 'arena,
{
	type Node = LabelsParserContextNode<'input, 'arena>;
	fn get_rule_index(&self) -> usize { RULE_e }
    fn base_ref_from_node(node: &Self::Node) -> Option<&BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::EContext(EContextAll::MultContext(inner)) => Some(inner),
            _ => None,
        }
    }
    fn base_mut_ref_from_node(node: &mut Self::Node) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::EContext(EContextAll::MultContext(inner)) => Some(inner),
            _ => None,
        }
    }
}

impl<'input, 'arena> EContextAttrs<'input, 'arena> for MultContext<'input, 'arena>
where
    'input: 'arena,
{
    fn get_v(&self) -> &String { &self.deref().base.v }  
    fn set_v(&mut self,attr: String) { self.deref_mut().base.v = attr; }  
}

impl<'input, 'arena> MultContextExt<'input, 'arena> {
	fn new(base: EContextExt<'input, 'arena>) -> Self {
        Self {
            base,
            op:None, 
            a:None, b:None, 
            pd: PhantomData
        }
    }

	fn copy_from(src: LabelsParserContextNode<'input, 'arena>) -> EContextAll<'input, 'arena>
    {
        let LabelsParserContextNode::EContext(src) = src else {
            panic!("invalid node type for copy_from!");
        };
        EContextAll::MultContext(
            BaseParserRuleContext::copy_from(src.into_base_ext(), |ext_src| Self::new(ext_src))
        )
	}
}

pub type DecContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, DecContextExt<'input, 'arena>>;

pub trait DecContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
	fn e(&self) -> Option<&'arena EContextAll<'input, 'arena>>;
}

impl<'input, 'arena> DecContextAttrs<'input, 'arena> for DecContext<'input, 'arena>
where
    'input: 'arena,
{
    fn e(&self) -> Option<&'arena EContextAll<'input, 'arena>> {
        self.child_of_type(0)
    }
}

pub struct DecContextExt<'input, 'arena> {
	base: EContextExt<'input, 'arena>,
	pub x: Option<&'arena EContextAll<'input, 'arena>>,
    pd: PhantomData<(&'arena (), &'input ())>
}

impl<'input, 'arena> CustomRuleContext<'input, 'arena> for DecContextExt<'input, 'arena>
where
    'input: 'arena,
{
	type Node = LabelsParserContextNode<'input, 'arena>;
	fn get_rule_index(&self) -> usize { RULE_e }
    fn base_ref_from_node(node: &Self::Node) -> Option<&BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::EContext(EContextAll::DecContext(inner)) => Some(inner),
            _ => None,
        }
    }
    fn base_mut_ref_from_node(node: &mut Self::Node) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::EContext(EContextAll::DecContext(inner)) => Some(inner),
            _ => None,
        }
    }
}

impl<'input, 'arena> EContextAttrs<'input, 'arena> for DecContext<'input, 'arena>
where
    'input: 'arena,
{
    fn get_v(&self) -> &String { &self.deref().base.v }  
    fn set_v(&mut self,attr: String) { self.deref_mut().base.v = attr; }  
}

impl<'input, 'arena> DecContextExt<'input, 'arena> {
	fn new(base: EContextExt<'input, 'arena>) -> Self {
        Self {
            base,
            x:None, 
            pd: PhantomData
        }
    }

	fn copy_from(src: LabelsParserContextNode<'input, 'arena>) -> EContextAll<'input, 'arena>
    {
        let LabelsParserContextNode::EContext(src) = src else {
            panic!("invalid node type for copy_from!");
        };
        EContextAll::DecContext(
            BaseParserRuleContext::copy_from(src.into_base_ext(), |ext_src| Self::new(ext_src))
        )
	}
}

pub type AnIDContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, AnIDContextExt<'input, 'arena>>;

pub trait AnIDContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
	/// Retrieves first TerminalNode corresponding to token ID
	/// Returns `None` if there is no child corresponding to token ID
	fn ID(&self) -> Option<&TerminalNode<'input, 'arena>>;
}

impl<'input, 'arena> AnIDContextAttrs<'input, 'arena> for AnIDContext<'input, 'arena>
where
    'input: 'arena,
{
    /// Retrieves first TerminalNode corresponding to token ID
    /// Returns `None` if there is no child corresponding to token ID
    fn ID(&self) -> Option<&TerminalNode<'input, 'arena>> {
    	self.get_token(Labels_ID, 0)
    }
}

pub struct AnIDContextExt<'input, 'arena> {
	base: EContextExt<'input, 'arena>,
	pub ID: Option<&'arena dyn Token >,
    pd: PhantomData<(&'arena (), &'input ())>
}

impl<'input, 'arena> CustomRuleContext<'input, 'arena> for AnIDContextExt<'input, 'arena>
where
    'input: 'arena,
{
	type Node = LabelsParserContextNode<'input, 'arena>;
	fn get_rule_index(&self) -> usize { RULE_e }
    fn base_ref_from_node(node: &Self::Node) -> Option<&BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::EContext(EContextAll::AnIDContext(inner)) => Some(inner),
            _ => None,
        }
    }
    fn base_mut_ref_from_node(node: &mut Self::Node) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::EContext(EContextAll::AnIDContext(inner)) => Some(inner),
            _ => None,
        }
    }
}

impl<'input, 'arena> EContextAttrs<'input, 'arena> for AnIDContext<'input, 'arena>
where
    'input: 'arena,
{
    fn get_v(&self) -> &String { &self.deref().base.v }  
    fn set_v(&mut self,attr: String) { self.deref_mut().base.v = attr; }  
}

impl<'input, 'arena> AnIDContextExt<'input, 'arena> {
	fn new(base: EContextExt<'input, 'arena>) -> Self {
        Self {
            base,
            ID:None, 
            pd: PhantomData
        }
    }

	fn copy_from(src: LabelsParserContextNode<'input, 'arena>) -> EContextAll<'input, 'arena>
    {
        let LabelsParserContextNode::EContext(src) = src else {
            panic!("invalid node type for copy_from!");
        };
        EContextAll::AnIDContext(
            BaseParserRuleContext::copy_from(src.into_base_ext(), |ext_src| Self::new(ext_src))
        )
	}
}

pub type AnIntContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, AnIntContextExt<'input, 'arena>>;

pub trait AnIntContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
	/// Retrieves first TerminalNode corresponding to token INT
	/// Returns `None` if there is no child corresponding to token INT
	fn INT(&self) -> Option<&TerminalNode<'input, 'arena>>;
}

impl<'input, 'arena> AnIntContextAttrs<'input, 'arena> for AnIntContext<'input, 'arena>
where
    'input: 'arena,
{
    /// Retrieves first TerminalNode corresponding to token INT
    /// Returns `None` if there is no child corresponding to token INT
    fn INT(&self) -> Option<&TerminalNode<'input, 'arena>> {
    	self.get_token(Labels_INT, 0)
    }
}

pub struct AnIntContextExt<'input, 'arena> {
	base: EContextExt<'input, 'arena>,
	pub INT: Option<&'arena dyn Token >,
    pd: PhantomData<(&'arena (), &'input ())>
}

impl<'input, 'arena> CustomRuleContext<'input, 'arena> for AnIntContextExt<'input, 'arena>
where
    'input: 'arena,
{
	type Node = LabelsParserContextNode<'input, 'arena>;
	fn get_rule_index(&self) -> usize { RULE_e }
    fn base_ref_from_node(node: &Self::Node) -> Option<&BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::EContext(EContextAll::AnIntContext(inner)) => Some(inner),
            _ => None,
        }
    }
    fn base_mut_ref_from_node(node: &mut Self::Node) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::EContext(EContextAll::AnIntContext(inner)) => Some(inner),
            _ => None,
        }
    }
}

impl<'input, 'arena> EContextAttrs<'input, 'arena> for AnIntContext<'input, 'arena>
where
    'input: 'arena,
{
    fn get_v(&self) -> &String { &self.deref().base.v }  
    fn set_v(&mut self,attr: String) { self.deref_mut().base.v = attr; }  
}

impl<'input, 'arena> AnIntContextExt<'input, 'arena> {
	fn new(base: EContextExt<'input, 'arena>) -> Self {
        Self {
            base,
            INT:None, 
            pd: PhantomData
        }
    }

	fn copy_from(src: LabelsParserContextNode<'input, 'arena>) -> EContextAll<'input, 'arena>
    {
        let LabelsParserContextNode::EContext(src) = src else {
            panic!("invalid node type for copy_from!");
        };
        EContextAll::AnIntContext(
            BaseParserRuleContext::copy_from(src.into_base_ext(), |ext_src| Self::new(ext_src))
        )
	}
}

pub type IncContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, IncContextExt<'input, 'arena>>;

pub trait IncContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
	fn e(&self) -> Option<&'arena EContextAll<'input, 'arena>>;
}

impl<'input, 'arena> IncContextAttrs<'input, 'arena> for IncContext<'input, 'arena>
where
    'input: 'arena,
{
    fn e(&self) -> Option<&'arena EContextAll<'input, 'arena>> {
        self.child_of_type(0)
    }
}

pub struct IncContextExt<'input, 'arena> {
	base: EContextExt<'input, 'arena>,
	pub x: Option<&'arena EContextAll<'input, 'arena>>,
    pd: PhantomData<(&'arena (), &'input ())>
}

impl<'input, 'arena> CustomRuleContext<'input, 'arena> for IncContextExt<'input, 'arena>
where
    'input: 'arena,
{
	type Node = LabelsParserContextNode<'input, 'arena>;
	fn get_rule_index(&self) -> usize { RULE_e }
    fn base_ref_from_node(node: &Self::Node) -> Option<&BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::EContext(EContextAll::IncContext(inner)) => Some(inner),
            _ => None,
        }
    }
    fn base_mut_ref_from_node(node: &mut Self::Node) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            LabelsParserContextNode::EContext(EContextAll::IncContext(inner)) => Some(inner),
            _ => None,
        }
    }
}

impl<'input, 'arena> EContextAttrs<'input, 'arena> for IncContext<'input, 'arena>
where
    'input: 'arena,
{
    fn get_v(&self) -> &String { &self.deref().base.v }  
    fn set_v(&mut self,attr: String) { self.deref_mut().base.v = attr; }  
}

impl<'input, 'arena> IncContextExt<'input, 'arena> {
	fn new(base: EContextExt<'input, 'arena>) -> Self {
        Self {
            base,
            x:None, 
            pd: PhantomData
        }
    }

	fn copy_from(src: LabelsParserContextNode<'input, 'arena>) -> EContextAll<'input, 'arena>
    {
        let LabelsParserContextNode::EContext(src) = src else {
            panic!("invalid node type for copy_from!");
        };
        EContextAll::IncContext(
            BaseParserRuleContext::copy_from(src.into_base_ext(), |ext_src| Self::new(ext_src))
        )
	}
}

impl<'input, 'arena, Input, TF> LabelsParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    'arena: 'input,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	pub fn  e(&mut self,) -> Result<&'arena EContextAll<'input, 'arena>, ANTLRError> {
		self.e_rec(0)
	}

	fn e_rec(&mut self, _p: i32) -> Result<&'arena EContextAll<'input, 'arena>, ANTLRError> {
		let recog = self;
		let _parentctx = recog.base.take_ctx();
		let _parentState = recog.base.get_state();
		recog.base.enter_recursion_rule(EContextExt::create(recog.get_arena(), _parentctx, recog.get_state()).into(), 2, RULE_e, _p)?;
        let _local_ctx_fn = |recog: &Self| -> &'arena EContext {recog.ctx().unwrap().as_rule_context().unwrap()};
		let _startState = 2;
		let result: Result<(), ANTLRError> = (|| {
	        let mut _alt: i32;
			/*------- Outer Most Alt 1 -------*/
			unsafe { recog.ctx_mut().unwrap().set_alt_number(1); }
			{
			recog.base.set_state(16);
			recog.err_handler.sync(&mut recog.base)?;
			match recog.base.input.la(1) {
			    Labels_INT  => {
			        {
			        recog.base.with_mut_ctx(|ctx| {
			            let tmp = std::mem::take(ctx);
			            *ctx = AnIntContextExt::copy_from(tmp).into();
			        });
			        let _local_ctx_fn = |recog: &Self| -> &'arena AnIntContext {recog.ctx().unwrap().as_rule_context().unwrap()};

			        recog.base.set_state(7);
			        let tmp = recog.base.match_token(Labels_INT,&mut recog.err_handler)?;
			        unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<AnIntContext>().unwrap().INT = Some(tmp); } 
			        let tmp = { if let Some(it) = &recog.ctx().unwrap().as_rule_context::<AnIntContext>().unwrap().INT { it.get_text() } else { "null" } .to_owned()}.to_owned();
			        unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<AnIntContext>().unwrap().set_v(tmp); }
			        }}
			    Labels_T__2  => {
			        {
			        recog.base.with_mut_ctx(|ctx| {
			            let tmp = std::mem::take(ctx);
			            *ctx = ParensContextExt::copy_from(tmp).into();
			        });
			        let _local_ctx_fn = |recog: &Self| -> &'arena ParensContext {recog.ctx().unwrap().as_rule_context().unwrap()};
			        recog.base.set_state(9);
			        recog.base.match_token(Labels_T__2,&mut recog.err_handler)?;
			        /*InvokeRule e*/
			        recog.base.set_state(10);
			        let tmp = recog.e_rec(0)?;
			        unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<ParensContext>().unwrap().x = Some(tmp); } 
			        recog.base.set_state(11);
			        recog.base.match_token(Labels_T__3,&mut recog.err_handler)?;
			        let tmp = { recog.ctx().unwrap().as_rule_context::<ParensContext>().unwrap().x.as_ref().unwrap().get_v()}.to_owned();
			        unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<ParensContext>().unwrap().set_v(tmp); }
			        }}
			    Labels_ID  => {
			        {
			        recog.base.with_mut_ctx(|ctx| {
			            let tmp = std::mem::take(ctx);
			            *ctx = AnIDContextExt::copy_from(tmp).into();
			        });
			        let _local_ctx_fn = |recog: &Self| -> &'arena AnIDContext {recog.ctx().unwrap().as_rule_context().unwrap()};
			        recog.base.set_state(14);
			        let tmp = recog.base.match_token(Labels_ID,&mut recog.err_handler)?;
			        unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<AnIDContext>().unwrap().ID = Some(tmp); } 
			        let tmp = { if let Some(it) = &recog.ctx().unwrap().as_rule_context::<AnIDContext>().unwrap().ID { it.get_text() } else { "null" } .to_owned()}.to_owned();
			        unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<AnIDContext>().unwrap().set_v(tmp); }
			        }}
				_ => Err(ANTLRError::no_alt(&mut recog.base))?
			}
			let tmp = recog.input.lt(-1);
			recog.base.with_mut_ctx(|ctx| { ctx.set_stop(tmp.map(|t| t as _)); });
			recog.base.set_state(36);
			recog.err_handler.sync(&mut recog.base)?;
			_alt = recog.interpreter.adaptive_predict(2,&mut recog.base)?;
			while { _alt!=2 && _alt!=INVALID_ALT } {
				if _alt==1 {
					recog.trigger_exit_rule_event()?;
					{
					recog.base.set_state(34);
					recog.err_handler.sync(&mut recog.base)?;
					match recog.interpreter.adaptive_predict(1,&mut recog.base)? {
						1 =>{
							{
							/*recRuleLabeledAltStartAction*/
							let tmp = MultContextExt::copy_from(EContextExt::create(recog.get_arena(), _parentctx, _parentState).into());
							let _prevctx = recog.push_new_recursion_context(tmp.into(), _startState, RULE_e)?;
							let _local_ctx_fn = |recog: &Self| -> &'arena MultContext {recog.ctx().unwrap().as_rule_context().unwrap()};
							recog.base.with_mut_ctx(|ctx| {
							ctx.as_rule_context_mut::<MultContext>().unwrap().a = Some(_prevctx.as_rule_context().unwrap());
							});

							recog.base.set_state(18);
							if !({recog.precpred(None, 7)}) {
								Err(ANTLRError::failed_predicate(&mut recog.base, Some("recog.precpred(None, 7)".to_owned()), None))?;
							}
							recog.base.set_state(19);
							let tmp = recog.base.match_token(Labels_T__0,&mut recog.err_handler)?;
							unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<MultContext>().unwrap().op = Some(tmp); } 
							/*InvokeRule e*/
							recog.base.set_state(20);
							let tmp = recog.e_rec(8)?;
							unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<MultContext>().unwrap().b = Some(tmp); } 
							let tmp = { "* ".to_owned() + recog.ctx().unwrap().as_rule_context::<MultContext>().unwrap().a.as_ref().unwrap().get_v() + " " + recog.ctx().unwrap().as_rule_context::<MultContext>().unwrap().b.as_ref().unwrap().get_v()}.to_owned();
							unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<MultContext>().unwrap().set_v(tmp); }
							}
						}
					,
						2 =>{
							{
							/*recRuleLabeledAltStartAction*/
							let tmp = AddContextExt::copy_from(EContextExt::create(recog.get_arena(), _parentctx, _parentState).into());
							let _prevctx = recog.push_new_recursion_context(tmp.into(), _startState, RULE_e)?;
							let _local_ctx_fn = |recog: &Self| -> &'arena AddContext {recog.ctx().unwrap().as_rule_context().unwrap()};
							recog.base.with_mut_ctx(|ctx| {
							ctx.as_rule_context_mut::<AddContext>().unwrap().a = Some(_prevctx.as_rule_context().unwrap());
							});

							recog.base.set_state(23);
							if !({recog.precpred(None, 6)}) {
								Err(ANTLRError::failed_predicate(&mut recog.base, Some("recog.precpred(None, 6)".to_owned()), None))?;
							}
							recog.base.set_state(24);
							recog.base.match_token(Labels_T__1,&mut recog.err_handler)?;
							/*InvokeRule e*/
							recog.base.set_state(25);
							let tmp = recog.e_rec(7)?;
							unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<AddContext>().unwrap().b = Some(tmp); } 
							let tmp = { "+ ".to_owned() + recog.ctx().unwrap().as_rule_context::<AddContext>().unwrap().a.as_ref().unwrap().get_v() + " " + recog.ctx().unwrap().as_rule_context::<AddContext>().unwrap().b.as_ref().unwrap().get_v()}.to_owned();
							unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<AddContext>().unwrap().set_v(tmp); }
							}
						}
					,
						3 =>{
							{
							/*recRuleLabeledAltStartAction*/
							let tmp = IncContextExt::copy_from(EContextExt::create(recog.get_arena(), _parentctx, _parentState).into());
							let _prevctx = recog.push_new_recursion_context(tmp.into(), _startState, RULE_e)?;
							let _local_ctx_fn = |recog: &Self| -> &'arena IncContext {recog.ctx().unwrap().as_rule_context().unwrap()};
							recog.base.with_mut_ctx(|ctx| {
							ctx.as_rule_context_mut::<IncContext>().unwrap().x = Some(_prevctx.as_rule_context().unwrap());
							});

							recog.base.set_state(28);
							if !({recog.precpred(None, 3)}) {
								Err(ANTLRError::failed_predicate(&mut recog.base, Some("recog.precpred(None, 3)".to_owned()), None))?;
							}
							recog.base.set_state(29);
							recog.base.match_token(Labels_T__4,&mut recog.err_handler)?;
							let tmp = { " ++".to_owned() + recog.ctx().unwrap().as_rule_context::<IncContext>().unwrap().x.as_ref().unwrap().get_v()}.to_owned();
							unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<IncContext>().unwrap().set_v(tmp); }
							}
						}
					,
						4 =>{
							{
							/*recRuleLabeledAltStartAction*/
							let tmp = DecContextExt::copy_from(EContextExt::create(recog.get_arena(), _parentctx, _parentState).into());
							let _prevctx = recog.push_new_recursion_context(tmp.into(), _startState, RULE_e)?;
							let _local_ctx_fn = |recog: &Self| -> &'arena DecContext {recog.ctx().unwrap().as_rule_context().unwrap()};
							recog.base.with_mut_ctx(|ctx| {
							ctx.as_rule_context_mut::<DecContext>().unwrap().x = Some(_prevctx.as_rule_context().unwrap());
							});

							recog.base.set_state(31);
							if !({recog.precpred(None, 2)}) {
								Err(ANTLRError::failed_predicate(&mut recog.base, Some("recog.precpred(None, 2)".to_owned()), None))?;
							}
							recog.base.set_state(32);
							recog.base.match_token(Labels_T__5,&mut recog.err_handler)?;
							let tmp = { " --".to_owned() + recog.ctx().unwrap().as_rule_context::<DecContext>().unwrap().x.as_ref().unwrap().get_v()}.to_owned();
							unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<DecContext>().unwrap().set_v(tmp); }
							}
						}

						_ => {}
					}
					} 
				}
				recog.base.set_state(38);
				recog.err_handler.sync(&mut recog.base)?;
				_alt = recog.interpreter.adaptive_predict(2,&mut recog.base)?;
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
    4, 1, 9, 40, 2, 0, 7, 0, 2, 1, 7, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 17, 8, 1, 1, 1, 1, 1, 1, 
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
    1, 1, 1, 1, 5, 1, 35, 8, 1, 10, 1, 12, 1, 38, 9, 1, 1, 1, 0, 1, 2, 2, 
    0, 2, 0, 0, 43, 0, 4, 1, 0, 0, 0, 2, 16, 1, 0, 0, 0, 4, 5, 3, 2, 1, 
    0, 5, 1, 1, 0, 0, 0, 6, 7, 6, 1, -1, 0, 7, 8, 5, 8, 0, 0, 8, 17, 6, 
    1, -1, 0, 9, 10, 5, 3, 0, 0, 10, 11, 3, 2, 1, 0, 11, 12, 5, 4, 0, 0, 
    12, 13, 6, 1, -1, 0, 13, 17, 1, 0, 0, 0, 14, 15, 5, 7, 0, 0, 15, 17, 
    6, 1, -1, 0, 16, 6, 1, 0, 0, 0, 16, 9, 1, 0, 0, 0, 16, 14, 1, 0, 0, 
    0, 17, 36, 1, 0, 0, 0, 18, 19, 10, 7, 0, 0, 19, 20, 5, 1, 0, 0, 20, 
    21, 3, 2, 1, 8, 21, 22, 6, 1, -1, 0, 22, 35, 1, 0, 0, 0, 23, 24, 10, 
    6, 0, 0, 24, 25, 5, 2, 0, 0, 25, 26, 3, 2, 1, 7, 26, 27, 6, 1, -1, 0, 
    27, 35, 1, 0, 0, 0, 28, 29, 10, 3, 0, 0, 29, 30, 5, 5, 0, 0, 30, 35, 
    6, 1, -1, 0, 31, 32, 10, 2, 0, 0, 32, 33, 5, 6, 0, 0, 33, 35, 6, 1, 
    -1, 0, 34, 18, 1, 0, 0, 0, 34, 23, 1, 0, 0, 0, 34, 28, 1, 0, 0, 0, 34, 
    31, 1, 0, 0, 0, 35, 38, 1, 0, 0, 0, 36, 34, 1, 0, 0, 0, 36, 37, 1, 0, 
    0, 0, 37, 3, 1, 0, 0, 0, 38, 36, 1, 0, 0, 0, 3, 16, 34, 36
]);