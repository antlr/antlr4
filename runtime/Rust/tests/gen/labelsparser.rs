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
use dbt_antlr4::token::CommonToken;
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
use super::labelslistener::*;
use std::marker::PhantomData;
use std::sync::LazyLock;
use std::rc::Rc;
use std::ops::{DerefMut, Deref};

dbt_antlr4::check_version!("1","3");
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

static VOCABULARY: LazyLock<Box<dyn Vocabulary>> = LazyLock::new(|| Box::new(VocabularyImpl::new(_LITERAL_NAMES.iter(), _SYMBOLIC_NAMES.iter(), None)));

pub type BaseParserType<'input, 'arena, Input, TF> = BaseParser<'input, 'arena, LabelsParserExt<'input, 'arena>, LabelsParserNodeKind, Input, TF>;
pub fn parser_simulator_manager() -> &'static ATNSimulatorManager { &ATN_SIMULATOR_MANAGER }

pub struct LabelsParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	base: BaseParserType<'input, 'arena, Input, TF>,
    interpreter: Rc<ParserATNSimulator<'arena>>,
    err_handler: ErrorStrategyDelegate<'input, 'arena, TF, BaseParserType<'input, 'arena, Input, TF>>,
}

impl<'input, 'arena, Input, TF> LabelsParser<'input, 'arena, Input, TF>
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
				LabelsParserExt {
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
        L: LabelsListener<'arena, TF::Tok> + 'static,
    {
        let id = ListenerId::new(&listener);
        self.base.add_dyn_parse_listener(listener);
        id
    }
}
pub struct LabelsTreeWalker;
impl LabelsTreeWalker
{
    pub fn walk<'input, 'arena, Tok, L, T>(
        listener: Box<L>,
        tree: &'arena T,
    ) -> Result<Box<L>, ANTLRError>
    where
        'input: 'arena,
        L: LabelsListener<'arena, Tok> + 'static,
        T: NodeInner<'input, 'arena, LabelsParserNodeKind, Tok>,
        Tok: Token + 'input,
    {
        let listener_ptr = Box::into_raw(listener);
        let listener = unsafe { Box::from_raw(listener_ptr as *mut <LabelsParserNodeKind as NodeKindType<Tok>>::Listener) };
        let listener = ParseTreeWalker::walk(listener, tree.as_node())?;
        Ok(unsafe { Box::from_raw(Box::into_raw(listener) as *mut L) } )
    }
}

#[derive(Clone, Copy, PartialEq, Eq, Debug)]
#[repr(u16)]
pub enum LabelsParserNodeKind {
    SContext,
    EContext,
    Terminal,
    Error,
}
pub type LabelsParserNode<'input, 'arena, Tok = CommonToken<'input>> = TreeNode<'input, 'arena, LabelsParserNodeKind, Tok>;

dbt_antlr4::impl_deref! { parser => LabelsParser }
dbt_antlr4::impl_node_kind! { LabelsParserNodeKind {
    EContext(EContextAll), ; SContext(enter_s, exit_s, ), 
    }; listener = dyn LabelsListener<'arena, Tok>,
}

pub struct LabelsParserExt<'input, 'arena> {
	_pd: PhantomData<(&'input str, &'arena ())>,
}

impl<'input, 'arena> LabelsParserExt<'input, 'arena> {
}

impl<'input, 'arena, Input, TF> ParserRecog<'input, 'arena, BaseParserType<'input, 'arena, Input, TF>, TF::Tok> for LabelsParserExt<'input, 'arena>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{}

impl<'input, 'arena, Input, TF> Actions<'input, 'arena, BaseParserType<'input, 'arena, Input, TF>, TF::Tok> for LabelsParserExt<'input, 'arena>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	fn get_grammar_file_name(&self) -> & str{ "Labels.g4" }
   	fn get_rule_names(&self) -> &[& str] { &ruleNames }
   	fn get_vocabulary(&self) -> &dyn Vocabulary { &**VOCABULARY }
	fn sempred(_localctx: Option<&'arena LabelsParserNode<'input, 'arena, TF::Tok>>, rule_index: i32, pred_index: i32,
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
	fn e_sempred(_ctx: Option<&'arena EContext<'input, 'arena, TF::Tok>>, pred_index:i32, recog: &mut <Self as Deref>::Target) -> bool
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
pub type SContextAll<'input, 'arena, Tok = CommonToken<'input>> = SContext<'input, 'arena, Tok>;

pub type SContext<'input, 'arena, Tok = CommonToken<'input>> = BaseParserRuleContext<'input, 'arena, SContextExt<'input, 'arena, Tok>, LabelsParserNodeKind, Tok>;
#[derive(Debug)]
pub struct SContextExt<'input: 'arena, 'arena, Tok: Token + 'input = CommonToken<'input>> {
	pub q: Option<&'arena EContextAll<'input, 'arena, Tok>>,
    ph: PhantomData<(&'arena (), &'input Tok)>,
}

impl<'input: 'arena, 'arena, Tok> CustomRuleContext<'input, 'arena, Tok> for SContextExt<'input, 'arena, Tok>
where
    Tok: Token + 'input,
{
	type NodeKind = LabelsParserNodeKind;
    fn node_tag() -> LabelsParserNodeKind { LabelsParserNodeKind::SContext }
	fn get_rule_index(&self) -> usize { RULE_s }
    fn make_node(
        arena: &'arena Arena,
        ctx: SContext<'input, 'arena, Tok>,
    ) -> *mut LabelsParserNode<'input, 'arena, Tok> {
        arena.alloc_zeroed_node(ctx)}
    fn cast_from<'a>(
        node: &'a LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a SContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => SContext<'input, 'arena, Tok>))
        } else {
            None
        }
    }
    fn cast_from_mut<'a>(
        node: &'a mut LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a mut SContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => mut SContext<'input, 'arena, Tok>))
        } else {
            None
        }
    }
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> SContextExt<'input, 'arena, Tok>{
	fn create(arena: &'arena Arena, parent: Option<&'arena LabelsParserNode<'input, 'arena, Tok>>, invoking_state: i32) -> &'arena mut LabelsParserNode<'input, 'arena, Tok>
    {
        BaseParserRuleContext::create(arena, parent, invoking_state, SContextExt {
				q: None, 
				ph: PhantomData
			}
		)
	}
}

pub trait SContextAttrs<'input, 'arena, Tok>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
    Tok: Token + 'input,
{
    fn e(&self) -> Option<&'arena EContextAll<'input, 'arena, Tok>>;
}

impl<'input, 'arena, Tok: Token + 'input> SContextAttrs<'input, 'arena, Tok> for SContext<'input, 'arena, Tok>
where
    'input: 'arena,
{
    fn e(&self) -> Option<&'arena EContextAll<'input, 'arena, Tok>> {
        self.child_of_type(0)
    }
}

impl<'input, 'arena, Input, TF> LabelsParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	pub fn s(&mut self,) -> Result<&'arena SContextAll<'input, 'arena, TF::Tok>, ANTLRError> {
		let recog = self;
        let _parentctx = recog.base.take_ctx();
        recog.base.enter_rule(SContextExt::create(recog.get_arena(), _parentctx, recog.get_state()), 0, RULE_s)?;
        let _local_ctx_fn = |recog: &Self| -> &'arena SContext<TF::Tok> {recog.ctx().unwrap().as_rule_context().unwrap()};
		let result: Result<(), ANTLRError> = (|| {
			/*------- Outer Most Alt 1 -------*/
			unsafe { recog.ctx_mut().unwrap().set_alt_number(1); }
			{
			/*InvokeRule e*/
			recog.base.set_state(4);
			let tmp = recog.e_rec(0)?;
			unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<SContext<TF::Tok>>().unwrap().q = Some(tmp); } 
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
#[repr(C, u16)]
pub enum EContextAll<'input, 'arena, Tok = CommonToken<'input>>
where
    'input: 'arena,
    Tok: Token + 'input,
{
	AddContext(AddContext<'input, 'arena, Tok>),
	ParensContext(ParensContext<'input, 'arena, Tok>),
	MultContext(MultContext<'input, 'arena, Tok>),
	DecContext(DecContext<'input, 'arena, Tok>),
	AnIDContext(AnIDContext<'input, 'arena, Tok>),
	AnIntContext(AnIntContext<'input, 'arena, Tok>),
	IncContext(IncContext<'input, 'arena, Tok>),
    Error(EContext<'input, 'arena, Tok>)
}

dbt_antlr4::impl_rule_context! { EContextAll { } { AddContext, ParensContext, MultContext, DecContext, AnIDContext, AnIntContext, IncContext, Error, } }
dbt_antlr4::impl_parser_rule_context! { EContextAll { } { AddContext, ParensContext, MultContext, DecContext, AnIDContext, AnIntContext, IncContext, Error, } }
dbt_antlr4::impl_tree_trait_delegates! { LabelsParserNodeKind::EContextAll { AddContext, ParensContext, MultContext, DecContext, AnIDContext, AnIntContext, IncContext, Error, } }
dbt_antlr4::impl_node_inner! { LabelsParserNodeKind::EContext::EContextAll { AddContext, ParensContext, MultContext, DecContext, AnIDContext, AnIntContext, IncContext, Error, } }
dbt_antlr4::impl_listener_dispatch! { LabelsListener::LabelsParserNodeKind::EContextAll { AddContext(enter_add, exit_add), ParensContext(enter_parens, exit_parens), MultContext(enter_mult, exit_mult), DecContext(enter_dec, exit_dec), AnIDContext(enter_anID, exit_anID), AnIntContext(enter_anInt, exit_anInt), IncContext(enter_inc, exit_inc), } }

impl<'input, 'arena, Tok> Deref for EContextAll<'input, 'arena, Tok>
where
    'input: 'arena,
    Tok: Token + 'input,
{
	type Target = dyn EContextAttrs<'input, 'arena, Tok> + 'arena;
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

pub type EContext<'input, 'arena, Tok = CommonToken<'input>> = BaseParserRuleContext<'input, 'arena, EContextExt<'input, 'arena, Tok>, LabelsParserNodeKind, Tok>;
#[derive(Debug)]
pub struct EContextExt<'input: 'arena, 'arena, Tok: Token + 'input = CommonToken<'input>> {
	pub v: String,
    ph: PhantomData<(&'arena (), &'input Tok)>,
}

impl<'input: 'arena, 'arena, Tok> CustomRuleContext<'input, 'arena, Tok> for EContextExt<'input, 'arena, Tok>
where
    Tok: Token + 'input,
{
	type NodeKind = LabelsParserNodeKind;
    fn node_tag() -> LabelsParserNodeKind { LabelsParserNodeKind::EContext }
	fn get_rule_index(&self) -> usize { RULE_e }
    fn make_node(
        arena: &'arena Arena,
        ctx: EContext<'input, 'arena, Tok>,
    ) -> *mut LabelsParserNode<'input, 'arena, Tok> {
        arena.alloc_labeled_node(EContextAll::Error(ctx))
    }
    fn cast_from<'a>(
        node: &'a LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a EContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => EContext<'input, 'arena, Tok>))
        } else {
            None
        }
    }
    fn cast_from_mut<'a>(
        node: &'a mut LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a mut EContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => mut EContext<'input, 'arena, Tok>))
        } else {
            None
        }
    }
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> EContextExt<'input, 'arena, Tok>{
	fn create(arena: &'arena Arena, parent: Option<&'arena LabelsParserNode<'input, 'arena, Tok>>, invoking_state: i32) -> &'arena mut LabelsParserNode<'input, 'arena, Tok>
    {
		let mut _init_v = String::new();

        BaseParserRuleContext::create(arena, parent, invoking_state, EContextExt {
				v: _init_v, 
				ph: PhantomData
			}
		)
	}
}

pub trait EContextAttrs<'input, 'arena, Tok>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
    Tok: Token + 'input,
{
    fn get_v(&self) -> &String; 
    fn set_v(&mut self,attr: String); 
}

impl<'input, 'arena, Tok: Token + 'input> EContextAttrs<'input, 'arena, Tok> for EContext<'input, 'arena, Tok>
where
    'input: 'arena,
{
    fn get_v(&self) -> &String { &self.deref().v }  
    fn set_v(&mut self,attr: String) { self.deref_mut().v = attr; }  
}

pub type AddContext<'input, 'arena, Tok = CommonToken<'input>> = BaseParserRuleContext<'input, 'arena, AddContextExt<'input, 'arena, Tok>, LabelsParserNodeKind, Tok>;

pub trait AddContextAttrs<'input, 'arena, Tok>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
    Tok: Token + 'input,
{
	fn e_all(&self) -> Vec<&'arena EContextAll<'input, 'arena, Tok>>;
	fn e(&self, i: usize) -> Option<&'arena EContextAll<'input, 'arena, Tok>>;
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> AddContextAttrs<'input, 'arena, Tok> for AddContext<'input, 'arena, Tok>
{
    fn e_all(&self) -> Vec<&'arena EContextAll<'input, 'arena, Tok>> {
        self.children_of_type()
    }
    fn e(&self, i: usize) -> Option<&'arena EContextAll<'input, 'arena, Tok>> {
        self.child_of_type(i)
    }
}
#[derive(Debug)]
pub struct AddContextExt<'input: 'arena, 'arena, Tok: Token + 'input = CommonToken<'input>> {
	base: EContextExt<'input, 'arena, Tok>,
	pub a: Option<&'arena EContextAll<'input, 'arena, Tok>>,
	pub b: Option<&'arena EContextAll<'input, 'arena, Tok>>,
    pd: PhantomData<(&'arena (), &'input Tok)>
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> CustomRuleContext<'input, 'arena, Tok> for AddContextExt<'input, 'arena, Tok>
{
	type NodeKind = LabelsParserNodeKind;
    fn node_tag() -> LabelsParserNodeKind { LabelsParserNodeKind::EContext }
	fn get_rule_index(&self) -> usize { RULE_e }
    fn make_node(
        arena: &'arena Arena,
        ctx: AddContext<'input, 'arena, Tok>,
    ) -> *mut LabelsParserNode<'input, 'arena, Tok> {
        arena.alloc_labeled_node(EContextAll::AddContext(ctx))
    }
    fn cast_from<'a>(
        node: &'a LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a AddContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            match dbt_antlr4::cast_unchecked!(node => EContextAll<'input, 'arena, Tok>) {
                EContextAll::AddContext(ctx) => Some(ctx),
                _ => None
            }
        } else {
            None
        }
    }
    fn cast_from_mut<'a>(
        node: &'a mut LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a mut AddContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            match dbt_antlr4::cast_unchecked!(node => mut EContextAll<'input, 'arena, Tok>) {
                EContextAll::AddContext(ctx) => Some(ctx),
                _ => None
            }
        } else {
            None
        }
    }
}

impl<'input, 'arena, Tok> EContextAttrs<'input, 'arena, Tok> for AddContext<'input, 'arena, Tok>
where
    'input: 'arena,
    Tok: Token + 'input,
{
    fn get_v(&self) -> &String { &self.deref().base.v }  
    fn set_v(&mut self,attr: String) { self.deref_mut().base.v = attr; }  
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> AddContextExt<'input, 'arena, Tok> {
	fn new(base: EContextExt<'input, 'arena, Tok>) -> Self {
        Self {
            base,
            a:None, b:None, 
            pd: PhantomData
        }
    }

	fn copy_from(src: &mut LabelsParserNode<'input, 'arena, Tok>) {
        let invoking_state = src.get_invoking_state();
        let ctx = {
            let Some(base_ctx): Option<&mut EContext<'input, 'arena, Tok>> = src.as_rule_context_mut() else {
                panic!("invalid node type for copy_from!");
            };
            let tmp = unsafe { std::ptr::read(base_ctx) };
            EContextAll::AddContext(tmp.morph(|ext_src| Self::new(ext_src)))
        };
        *dbt_antlr4::cast_unchecked!(src => mut EContextAll<'input, 'arena, Tok>) = ctx;
        src.set_invoking_state(invoking_state);
        src.node_tag = <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag();
	}
}

pub type ParensContext<'input, 'arena, Tok = CommonToken<'input>> = BaseParserRuleContext<'input, 'arena, ParensContextExt<'input, 'arena, Tok>, LabelsParserNodeKind, Tok>;

pub trait ParensContextAttrs<'input, 'arena, Tok>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
    Tok: Token + 'input,
{
	fn e(&self) -> Option<&'arena EContextAll<'input, 'arena, Tok>>;
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> ParensContextAttrs<'input, 'arena, Tok> for ParensContext<'input, 'arena, Tok>
{
    fn e(&self) -> Option<&'arena EContextAll<'input, 'arena, Tok>> {
        self.child_of_type(0)
    }
}
#[derive(Debug)]
pub struct ParensContextExt<'input: 'arena, 'arena, Tok: Token + 'input = CommonToken<'input>> {
	base: EContextExt<'input, 'arena, Tok>,
	pub x: Option<&'arena EContextAll<'input, 'arena, Tok>>,
    pd: PhantomData<(&'arena (), &'input Tok)>
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> CustomRuleContext<'input, 'arena, Tok> for ParensContextExt<'input, 'arena, Tok>
{
	type NodeKind = LabelsParserNodeKind;
    fn node_tag() -> LabelsParserNodeKind { LabelsParserNodeKind::EContext }
	fn get_rule_index(&self) -> usize { RULE_e }
    fn make_node(
        arena: &'arena Arena,
        ctx: ParensContext<'input, 'arena, Tok>,
    ) -> *mut LabelsParserNode<'input, 'arena, Tok> {
        arena.alloc_labeled_node(EContextAll::ParensContext(ctx))
    }
    fn cast_from<'a>(
        node: &'a LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a ParensContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            match dbt_antlr4::cast_unchecked!(node => EContextAll<'input, 'arena, Tok>) {
                EContextAll::ParensContext(ctx) => Some(ctx),
                _ => None
            }
        } else {
            None
        }
    }
    fn cast_from_mut<'a>(
        node: &'a mut LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a mut ParensContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            match dbt_antlr4::cast_unchecked!(node => mut EContextAll<'input, 'arena, Tok>) {
                EContextAll::ParensContext(ctx) => Some(ctx),
                _ => None
            }
        } else {
            None
        }
    }
}

impl<'input, 'arena, Tok> EContextAttrs<'input, 'arena, Tok> for ParensContext<'input, 'arena, Tok>
where
    'input: 'arena,
    Tok: Token + 'input,
{
    fn get_v(&self) -> &String { &self.deref().base.v }  
    fn set_v(&mut self,attr: String) { self.deref_mut().base.v = attr; }  
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> ParensContextExt<'input, 'arena, Tok> {
	fn new(base: EContextExt<'input, 'arena, Tok>) -> Self {
        Self {
            base,
            x:None, 
            pd: PhantomData
        }
    }

	fn copy_from(src: &mut LabelsParserNode<'input, 'arena, Tok>) {
        let invoking_state = src.get_invoking_state();
        let ctx = {
            let Some(base_ctx): Option<&mut EContext<'input, 'arena, Tok>> = src.as_rule_context_mut() else {
                panic!("invalid node type for copy_from!");
            };
            let tmp = unsafe { std::ptr::read(base_ctx) };
            EContextAll::ParensContext(tmp.morph(|ext_src| Self::new(ext_src)))
        };
        *dbt_antlr4::cast_unchecked!(src => mut EContextAll<'input, 'arena, Tok>) = ctx;
        src.set_invoking_state(invoking_state);
        src.node_tag = <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag();
	}
}

pub type MultContext<'input, 'arena, Tok = CommonToken<'input>> = BaseParserRuleContext<'input, 'arena, MultContextExt<'input, 'arena, Tok>, LabelsParserNodeKind, Tok>;

pub trait MultContextAttrs<'input, 'arena, Tok>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
    Tok: Token + 'input,
{
	fn e_all(&self) -> Vec<&'arena EContextAll<'input, 'arena, Tok>>;
	fn e(&self, i: usize) -> Option<&'arena EContextAll<'input, 'arena, Tok>>;
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> MultContextAttrs<'input, 'arena, Tok> for MultContext<'input, 'arena, Tok>
{
    fn e_all(&self) -> Vec<&'arena EContextAll<'input, 'arena, Tok>> {
        self.children_of_type()
    }
    fn e(&self, i: usize) -> Option<&'arena EContextAll<'input, 'arena, Tok>> {
        self.child_of_type(i)
    }
}
#[derive(Debug)]
pub struct MultContextExt<'input: 'arena, 'arena, Tok: Token + 'input = CommonToken<'input>> {
	base: EContextExt<'input, 'arena, Tok>,
	pub a: Option<&'arena EContextAll<'input, 'arena, Tok>>,
	pub op: Option<&'arena dyn Token >,
	pub b: Option<&'arena EContextAll<'input, 'arena, Tok>>,
    pd: PhantomData<(&'arena (), &'input Tok)>
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> CustomRuleContext<'input, 'arena, Tok> for MultContextExt<'input, 'arena, Tok>
{
	type NodeKind = LabelsParserNodeKind;
    fn node_tag() -> LabelsParserNodeKind { LabelsParserNodeKind::EContext }
	fn get_rule_index(&self) -> usize { RULE_e }
    fn make_node(
        arena: &'arena Arena,
        ctx: MultContext<'input, 'arena, Tok>,
    ) -> *mut LabelsParserNode<'input, 'arena, Tok> {
        arena.alloc_labeled_node(EContextAll::MultContext(ctx))
    }
    fn cast_from<'a>(
        node: &'a LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a MultContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            match dbt_antlr4::cast_unchecked!(node => EContextAll<'input, 'arena, Tok>) {
                EContextAll::MultContext(ctx) => Some(ctx),
                _ => None
            }
        } else {
            None
        }
    }
    fn cast_from_mut<'a>(
        node: &'a mut LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a mut MultContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            match dbt_antlr4::cast_unchecked!(node => mut EContextAll<'input, 'arena, Tok>) {
                EContextAll::MultContext(ctx) => Some(ctx),
                _ => None
            }
        } else {
            None
        }
    }
}

impl<'input, 'arena, Tok> EContextAttrs<'input, 'arena, Tok> for MultContext<'input, 'arena, Tok>
where
    'input: 'arena,
    Tok: Token + 'input,
{
    fn get_v(&self) -> &String { &self.deref().base.v }  
    fn set_v(&mut self,attr: String) { self.deref_mut().base.v = attr; }  
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> MultContextExt<'input, 'arena, Tok> {
	fn new(base: EContextExt<'input, 'arena, Tok>) -> Self {
        Self {
            base,
            op:None, 
            a:None, b:None, 
            pd: PhantomData
        }
    }

	fn copy_from(src: &mut LabelsParserNode<'input, 'arena, Tok>) {
        let invoking_state = src.get_invoking_state();
        let ctx = {
            let Some(base_ctx): Option<&mut EContext<'input, 'arena, Tok>> = src.as_rule_context_mut() else {
                panic!("invalid node type for copy_from!");
            };
            let tmp = unsafe { std::ptr::read(base_ctx) };
            EContextAll::MultContext(tmp.morph(|ext_src| Self::new(ext_src)))
        };
        *dbt_antlr4::cast_unchecked!(src => mut EContextAll<'input, 'arena, Tok>) = ctx;
        src.set_invoking_state(invoking_state);
        src.node_tag = <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag();
	}
}

pub type DecContext<'input, 'arena, Tok = CommonToken<'input>> = BaseParserRuleContext<'input, 'arena, DecContextExt<'input, 'arena, Tok>, LabelsParserNodeKind, Tok>;

pub trait DecContextAttrs<'input, 'arena, Tok>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
    Tok: Token + 'input,
{
	fn e(&self) -> Option<&'arena EContextAll<'input, 'arena, Tok>>;
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> DecContextAttrs<'input, 'arena, Tok> for DecContext<'input, 'arena, Tok>
{
    fn e(&self) -> Option<&'arena EContextAll<'input, 'arena, Tok>> {
        self.child_of_type(0)
    }
}
#[derive(Debug)]
pub struct DecContextExt<'input: 'arena, 'arena, Tok: Token + 'input = CommonToken<'input>> {
	base: EContextExt<'input, 'arena, Tok>,
	pub x: Option<&'arena EContextAll<'input, 'arena, Tok>>,
    pd: PhantomData<(&'arena (), &'input Tok)>
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> CustomRuleContext<'input, 'arena, Tok> for DecContextExt<'input, 'arena, Tok>
{
	type NodeKind = LabelsParserNodeKind;
    fn node_tag() -> LabelsParserNodeKind { LabelsParserNodeKind::EContext }
	fn get_rule_index(&self) -> usize { RULE_e }
    fn make_node(
        arena: &'arena Arena,
        ctx: DecContext<'input, 'arena, Tok>,
    ) -> *mut LabelsParserNode<'input, 'arena, Tok> {
        arena.alloc_labeled_node(EContextAll::DecContext(ctx))
    }
    fn cast_from<'a>(
        node: &'a LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a DecContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            match dbt_antlr4::cast_unchecked!(node => EContextAll<'input, 'arena, Tok>) {
                EContextAll::DecContext(ctx) => Some(ctx),
                _ => None
            }
        } else {
            None
        }
    }
    fn cast_from_mut<'a>(
        node: &'a mut LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a mut DecContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            match dbt_antlr4::cast_unchecked!(node => mut EContextAll<'input, 'arena, Tok>) {
                EContextAll::DecContext(ctx) => Some(ctx),
                _ => None
            }
        } else {
            None
        }
    }
}

impl<'input, 'arena, Tok> EContextAttrs<'input, 'arena, Tok> for DecContext<'input, 'arena, Tok>
where
    'input: 'arena,
    Tok: Token + 'input,
{
    fn get_v(&self) -> &String { &self.deref().base.v }  
    fn set_v(&mut self,attr: String) { self.deref_mut().base.v = attr; }  
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> DecContextExt<'input, 'arena, Tok> {
	fn new(base: EContextExt<'input, 'arena, Tok>) -> Self {
        Self {
            base,
            x:None, 
            pd: PhantomData
        }
    }

	fn copy_from(src: &mut LabelsParserNode<'input, 'arena, Tok>) {
        let invoking_state = src.get_invoking_state();
        let ctx = {
            let Some(base_ctx): Option<&mut EContext<'input, 'arena, Tok>> = src.as_rule_context_mut() else {
                panic!("invalid node type for copy_from!");
            };
            let tmp = unsafe { std::ptr::read(base_ctx) };
            EContextAll::DecContext(tmp.morph(|ext_src| Self::new(ext_src)))
        };
        *dbt_antlr4::cast_unchecked!(src => mut EContextAll<'input, 'arena, Tok>) = ctx;
        src.set_invoking_state(invoking_state);
        src.node_tag = <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag();
	}
}

pub type AnIDContext<'input, 'arena, Tok = CommonToken<'input>> = BaseParserRuleContext<'input, 'arena, AnIDContextExt<'input, 'arena, Tok>, LabelsParserNodeKind, Tok>;

pub trait AnIDContextAttrs<'input, 'arena, Tok>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
    Tok: Token + 'input,
{
	/// Retrieves first TerminalNode corresponding to token ID
	/// Returns `None` if there is no child corresponding to token ID
	fn ID(&self) -> Option<&TerminalNode<'input, 'arena, Tok>>;
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> AnIDContextAttrs<'input, 'arena, Tok> for AnIDContext<'input, 'arena, Tok>
{
    /// Retrieves first TerminalNode corresponding to token ID
    /// Returns `None` if there is no child corresponding to token ID
    fn ID(&self) -> Option<&TerminalNode<'input, 'arena, Tok>> {
        self.children_of_type::<TerminalNode<Tok>>().into_iter().find(|child| child.symbol.get_token_type() == Labels_ID)
    }
}
#[derive(Debug)]
pub struct AnIDContextExt<'input: 'arena, 'arena, Tok: Token + 'input = CommonToken<'input>> {
	base: EContextExt<'input, 'arena, Tok>,
	pub ID: Option<&'arena dyn Token >,
    pd: PhantomData<(&'arena (), &'input Tok)>
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> CustomRuleContext<'input, 'arena, Tok> for AnIDContextExt<'input, 'arena, Tok>
{
	type NodeKind = LabelsParserNodeKind;
    fn node_tag() -> LabelsParserNodeKind { LabelsParserNodeKind::EContext }
	fn get_rule_index(&self) -> usize { RULE_e }
    fn make_node(
        arena: &'arena Arena,
        ctx: AnIDContext<'input, 'arena, Tok>,
    ) -> *mut LabelsParserNode<'input, 'arena, Tok> {
        arena.alloc_labeled_node(EContextAll::AnIDContext(ctx))
    }
    fn cast_from<'a>(
        node: &'a LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a AnIDContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            match dbt_antlr4::cast_unchecked!(node => EContextAll<'input, 'arena, Tok>) {
                EContextAll::AnIDContext(ctx) => Some(ctx),
                _ => None
            }
        } else {
            None
        }
    }
    fn cast_from_mut<'a>(
        node: &'a mut LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a mut AnIDContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            match dbt_antlr4::cast_unchecked!(node => mut EContextAll<'input, 'arena, Tok>) {
                EContextAll::AnIDContext(ctx) => Some(ctx),
                _ => None
            }
        } else {
            None
        }
    }
}

impl<'input, 'arena, Tok> EContextAttrs<'input, 'arena, Tok> for AnIDContext<'input, 'arena, Tok>
where
    'input: 'arena,
    Tok: Token + 'input,
{
    fn get_v(&self) -> &String { &self.deref().base.v }  
    fn set_v(&mut self,attr: String) { self.deref_mut().base.v = attr; }  
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> AnIDContextExt<'input, 'arena, Tok> {
	fn new(base: EContextExt<'input, 'arena, Tok>) -> Self {
        Self {
            base,
            ID:None, 
            pd: PhantomData
        }
    }

	fn copy_from(src: &mut LabelsParserNode<'input, 'arena, Tok>) {
        let invoking_state = src.get_invoking_state();
        let ctx = {
            let Some(base_ctx): Option<&mut EContext<'input, 'arena, Tok>> = src.as_rule_context_mut() else {
                panic!("invalid node type for copy_from!");
            };
            let tmp = unsafe { std::ptr::read(base_ctx) };
            EContextAll::AnIDContext(tmp.morph(|ext_src| Self::new(ext_src)))
        };
        *dbt_antlr4::cast_unchecked!(src => mut EContextAll<'input, 'arena, Tok>) = ctx;
        src.set_invoking_state(invoking_state);
        src.node_tag = <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag();
	}
}

pub type AnIntContext<'input, 'arena, Tok = CommonToken<'input>> = BaseParserRuleContext<'input, 'arena, AnIntContextExt<'input, 'arena, Tok>, LabelsParserNodeKind, Tok>;

pub trait AnIntContextAttrs<'input, 'arena, Tok>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
    Tok: Token + 'input,
{
	/// Retrieves first TerminalNode corresponding to token INT
	/// Returns `None` if there is no child corresponding to token INT
	fn INT(&self) -> Option<&TerminalNode<'input, 'arena, Tok>>;
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> AnIntContextAttrs<'input, 'arena, Tok> for AnIntContext<'input, 'arena, Tok>
{
    /// Retrieves first TerminalNode corresponding to token INT
    /// Returns `None` if there is no child corresponding to token INT
    fn INT(&self) -> Option<&TerminalNode<'input, 'arena, Tok>> {
        self.children_of_type::<TerminalNode<Tok>>().into_iter().find(|child| child.symbol.get_token_type() == Labels_INT)
    }
}
#[derive(Debug)]
pub struct AnIntContextExt<'input: 'arena, 'arena, Tok: Token + 'input = CommonToken<'input>> {
	base: EContextExt<'input, 'arena, Tok>,
	pub INT: Option<&'arena dyn Token >,
    pd: PhantomData<(&'arena (), &'input Tok)>
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> CustomRuleContext<'input, 'arena, Tok> for AnIntContextExt<'input, 'arena, Tok>
{
	type NodeKind = LabelsParserNodeKind;
    fn node_tag() -> LabelsParserNodeKind { LabelsParserNodeKind::EContext }
	fn get_rule_index(&self) -> usize { RULE_e }
    fn make_node(
        arena: &'arena Arena,
        ctx: AnIntContext<'input, 'arena, Tok>,
    ) -> *mut LabelsParserNode<'input, 'arena, Tok> {
        arena.alloc_labeled_node(EContextAll::AnIntContext(ctx))
    }
    fn cast_from<'a>(
        node: &'a LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a AnIntContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            match dbt_antlr4::cast_unchecked!(node => EContextAll<'input, 'arena, Tok>) {
                EContextAll::AnIntContext(ctx) => Some(ctx),
                _ => None
            }
        } else {
            None
        }
    }
    fn cast_from_mut<'a>(
        node: &'a mut LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a mut AnIntContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            match dbt_antlr4::cast_unchecked!(node => mut EContextAll<'input, 'arena, Tok>) {
                EContextAll::AnIntContext(ctx) => Some(ctx),
                _ => None
            }
        } else {
            None
        }
    }
}

impl<'input, 'arena, Tok> EContextAttrs<'input, 'arena, Tok> for AnIntContext<'input, 'arena, Tok>
where
    'input: 'arena,
    Tok: Token + 'input,
{
    fn get_v(&self) -> &String { &self.deref().base.v }  
    fn set_v(&mut self,attr: String) { self.deref_mut().base.v = attr; }  
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> AnIntContextExt<'input, 'arena, Tok> {
	fn new(base: EContextExt<'input, 'arena, Tok>) -> Self {
        Self {
            base,
            INT:None, 
            pd: PhantomData
        }
    }

	fn copy_from(src: &mut LabelsParserNode<'input, 'arena, Tok>) {
        let invoking_state = src.get_invoking_state();
        let ctx = {
            let Some(base_ctx): Option<&mut EContext<'input, 'arena, Tok>> = src.as_rule_context_mut() else {
                panic!("invalid node type for copy_from!");
            };
            let tmp = unsafe { std::ptr::read(base_ctx) };
            EContextAll::AnIntContext(tmp.morph(|ext_src| Self::new(ext_src)))
        };
        *dbt_antlr4::cast_unchecked!(src => mut EContextAll<'input, 'arena, Tok>) = ctx;
        src.set_invoking_state(invoking_state);
        src.node_tag = <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag();
	}
}

pub type IncContext<'input, 'arena, Tok = CommonToken<'input>> = BaseParserRuleContext<'input, 'arena, IncContextExt<'input, 'arena, Tok>, LabelsParserNodeKind, Tok>;

pub trait IncContextAttrs<'input, 'arena, Tok>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
    Tok: Token + 'input,
{
	fn e(&self) -> Option<&'arena EContextAll<'input, 'arena, Tok>>;
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> IncContextAttrs<'input, 'arena, Tok> for IncContext<'input, 'arena, Tok>
{
    fn e(&self) -> Option<&'arena EContextAll<'input, 'arena, Tok>> {
        self.child_of_type(0)
    }
}
#[derive(Debug)]
pub struct IncContextExt<'input: 'arena, 'arena, Tok: Token + 'input = CommonToken<'input>> {
	base: EContextExt<'input, 'arena, Tok>,
	pub x: Option<&'arena EContextAll<'input, 'arena, Tok>>,
    pd: PhantomData<(&'arena (), &'input Tok)>
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> CustomRuleContext<'input, 'arena, Tok> for IncContextExt<'input, 'arena, Tok>
{
	type NodeKind = LabelsParserNodeKind;
    fn node_tag() -> LabelsParserNodeKind { LabelsParserNodeKind::EContext }
	fn get_rule_index(&self) -> usize { RULE_e }
    fn make_node(
        arena: &'arena Arena,
        ctx: IncContext<'input, 'arena, Tok>,
    ) -> *mut LabelsParserNode<'input, 'arena, Tok> {
        arena.alloc_labeled_node(EContextAll::IncContext(ctx))
    }
    fn cast_from<'a>(
        node: &'a LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a IncContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            match dbt_antlr4::cast_unchecked!(node => EContextAll<'input, 'arena, Tok>) {
                EContextAll::IncContext(ctx) => Some(ctx),
                _ => None
            }
        } else {
            None
        }
    }
    fn cast_from_mut<'a>(
        node: &'a mut LabelsParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a mut IncContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            match dbt_antlr4::cast_unchecked!(node => mut EContextAll<'input, 'arena, Tok>) {
                EContextAll::IncContext(ctx) => Some(ctx),
                _ => None
            }
        } else {
            None
        }
    }
}

impl<'input, 'arena, Tok> EContextAttrs<'input, 'arena, Tok> for IncContext<'input, 'arena, Tok>
where
    'input: 'arena,
    Tok: Token + 'input,
{
    fn get_v(&self) -> &String { &self.deref().base.v }  
    fn set_v(&mut self,attr: String) { self.deref_mut().base.v = attr; }  
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> IncContextExt<'input, 'arena, Tok> {
	fn new(base: EContextExt<'input, 'arena, Tok>) -> Self {
        Self {
            base,
            x:None, 
            pd: PhantomData
        }
    }

	fn copy_from(src: &mut LabelsParserNode<'input, 'arena, Tok>) {
        let invoking_state = src.get_invoking_state();
        let ctx = {
            let Some(base_ctx): Option<&mut EContext<'input, 'arena, Tok>> = src.as_rule_context_mut() else {
                panic!("invalid node type for copy_from!");
            };
            let tmp = unsafe { std::ptr::read(base_ctx) };
            EContextAll::IncContext(tmp.morph(|ext_src| Self::new(ext_src)))
        };
        *dbt_antlr4::cast_unchecked!(src => mut EContextAll<'input, 'arena, Tok>) = ctx;
        src.set_invoking_state(invoking_state);
        src.node_tag = <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag();
	}
}

impl<'input, 'arena, Input, TF> LabelsParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
    #[inline]
	pub fn  e(&mut self,) -> Result<&'arena EContextAll<'input, 'arena, TF::Tok>, ANTLRError> {
		self.e_rec(0)
	}

    #[inline]
	fn e_rec(&mut self, _p: i32) -> Result<&'arena EContextAll<'input, 'arena, TF::Tok>, ANTLRError> {
        dbt_antlr4::stacker::maybe_grow(100 * 1024, 2 * 1024 * 1024,
                      || self.e_rec_inner(_p))
    }

	fn e_rec_inner(&mut self, _p: i32) -> Result<&'arena EContextAll<'input, 'arena, TF::Tok>, ANTLRError> {
		let recog = self;
		let _parentctx = recog.base.take_ctx();
		let _parentState = recog.base.get_state();
		recog.base.enter_recursion_rule(EContextExt::create(recog.get_arena(), _parentctx, recog.get_state()), 2, RULE_e, _p)?;
        let _local_ctx_fn = |recog: &Self| -> &'arena EContext<TF::Tok> {recog.ctx().unwrap().as_rule_context().unwrap()};
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
			        recog.base.with_mut_ctx(|ctx| { AnIntContextExt::copy_from(ctx); });
			        let _local_ctx_fn = |recog: &Self| -> &'arena AnIntContext<TF::Tok> {recog.ctx().unwrap().as_rule_context().unwrap()};

			        recog.base.set_state(7);
			        let tmp = recog.base.match_token(Labels_INT,&mut recog.err_handler)?;
			        unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<AnIntContext<TF::Tok>>().unwrap().INT = Some(tmp); } 
			        let tmp = { if let Some(it) = &recog.ctx().unwrap().as_rule_context::<AnIntContext<TF::Tok>>().unwrap().INT { it.get_text() } else { "null" } .to_owned()}.to_owned();
			        unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<AnIntContext<TF::Tok>>().unwrap().set_v(tmp); }
			        }}
			    Labels_T__2  => {
			        {
			        recog.base.with_mut_ctx(|ctx| { ParensContextExt::copy_from(ctx); });
			        let _local_ctx_fn = |recog: &Self| -> &'arena ParensContext<TF::Tok> {recog.ctx().unwrap().as_rule_context().unwrap()};
			        recog.base.set_state(9);
			        recog.base.match_token(Labels_T__2,&mut recog.err_handler)?;
			        /*InvokeRule e*/
			        recog.base.set_state(10);
			        let tmp = recog.e_rec(0)?;
			        unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<ParensContext<TF::Tok>>().unwrap().x = Some(tmp); } 
			        recog.base.set_state(11);
			        recog.base.match_token(Labels_T__3,&mut recog.err_handler)?;
			        let tmp = { recog.ctx().unwrap().as_rule_context::<ParensContext<TF::Tok>>().unwrap().x.as_ref().unwrap().get_v()}.to_owned();
			        unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<ParensContext<TF::Tok>>().unwrap().set_v(tmp); }
			        }}
			    Labels_ID  => {
			        {
			        recog.base.with_mut_ctx(|ctx| { AnIDContextExt::copy_from(ctx); });
			        let _local_ctx_fn = |recog: &Self| -> &'arena AnIDContext<TF::Tok> {recog.ctx().unwrap().as_rule_context().unwrap()};
			        recog.base.set_state(14);
			        let tmp = recog.base.match_token(Labels_ID,&mut recog.err_handler)?;
			        unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<AnIDContext<TF::Tok>>().unwrap().ID = Some(tmp); } 
			        let tmp = { if let Some(it) = &recog.ctx().unwrap().as_rule_context::<AnIDContext<TF::Tok>>().unwrap().ID { it.get_text() } else { "null" } .to_owned()}.to_owned();
			        unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<AnIDContext<TF::Tok>>().unwrap().set_v(tmp); }
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
							let tmp = EContextExt::create(recog.get_arena(), _parentctx, _parentState);
							MultContextExt::copy_from(tmp);
							let _prevctx = recog.push_new_recursion_context(tmp, _startState, RULE_e)?;
							let _local_ctx_fn = |recog: &Self| -> &'arena MultContext<TF::Tok> {recog.ctx().unwrap().as_rule_context().unwrap()};
							recog.base.with_mut_ctx(|ctx| {
							ctx.as_rule_context_mut::<MultContext<TF::Tok>>().unwrap().a = Some(_prevctx.as_rule_context().unwrap());
							});

							recog.base.set_state(18);
							if !({recog.precpred(None, 7)}) {
								Err(ANTLRError::failed_predicate(&mut recog.base, Some("recog.precpred(None, 7)".to_owned()), None))?;
							}
							recog.base.set_state(19);
							let tmp = recog.base.match_token(Labels_T__0,&mut recog.err_handler)?;
							unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<MultContext<TF::Tok>>().unwrap().op = Some(tmp); } 
							/*InvokeRule e*/
							recog.base.set_state(20);
							let tmp = recog.e_rec(8)?;
							unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<MultContext<TF::Tok>>().unwrap().b = Some(tmp); } 
							let tmp = { "* ".to_owned() + recog.ctx().unwrap().as_rule_context::<MultContext<TF::Tok>>().unwrap().a.as_ref().unwrap().get_v() + " " + recog.ctx().unwrap().as_rule_context::<MultContext<TF::Tok>>().unwrap().b.as_ref().unwrap().get_v()}.to_owned();
							unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<MultContext<TF::Tok>>().unwrap().set_v(tmp); }
							}
						}
					,
						2 =>{
							{
							/*recRuleLabeledAltStartAction*/
							let tmp = EContextExt::create(recog.get_arena(), _parentctx, _parentState);
							AddContextExt::copy_from(tmp);
							let _prevctx = recog.push_new_recursion_context(tmp, _startState, RULE_e)?;
							let _local_ctx_fn = |recog: &Self| -> &'arena AddContext<TF::Tok> {recog.ctx().unwrap().as_rule_context().unwrap()};
							recog.base.with_mut_ctx(|ctx| {
							ctx.as_rule_context_mut::<AddContext<TF::Tok>>().unwrap().a = Some(_prevctx.as_rule_context().unwrap());
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
							unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<AddContext<TF::Tok>>().unwrap().b = Some(tmp); } 
							let tmp = { "+ ".to_owned() + recog.ctx().unwrap().as_rule_context::<AddContext<TF::Tok>>().unwrap().a.as_ref().unwrap().get_v() + " " + recog.ctx().unwrap().as_rule_context::<AddContext<TF::Tok>>().unwrap().b.as_ref().unwrap().get_v()}.to_owned();
							unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<AddContext<TF::Tok>>().unwrap().set_v(tmp); }
							}
						}
					,
						3 =>{
							{
							/*recRuleLabeledAltStartAction*/
							let tmp = EContextExt::create(recog.get_arena(), _parentctx, _parentState);
							IncContextExt::copy_from(tmp);
							let _prevctx = recog.push_new_recursion_context(tmp, _startState, RULE_e)?;
							let _local_ctx_fn = |recog: &Self| -> &'arena IncContext<TF::Tok> {recog.ctx().unwrap().as_rule_context().unwrap()};
							recog.base.with_mut_ctx(|ctx| {
							ctx.as_rule_context_mut::<IncContext<TF::Tok>>().unwrap().x = Some(_prevctx.as_rule_context().unwrap());
							});

							recog.base.set_state(28);
							if !({recog.precpred(None, 3)}) {
								Err(ANTLRError::failed_predicate(&mut recog.base, Some("recog.precpred(None, 3)".to_owned()), None))?;
							}
							recog.base.set_state(29);
							recog.base.match_token(Labels_T__4,&mut recog.err_handler)?;
							let tmp = { " ++".to_owned() + recog.ctx().unwrap().as_rule_context::<IncContext<TF::Tok>>().unwrap().x.as_ref().unwrap().get_v()}.to_owned();
							unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<IncContext<TF::Tok>>().unwrap().set_v(tmp); }
							}
						}
					,
						4 =>{
							{
							/*recRuleLabeledAltStartAction*/
							let tmp = EContextExt::create(recog.get_arena(), _parentctx, _parentState);
							DecContextExt::copy_from(tmp);
							let _prevctx = recog.push_new_recursion_context(tmp, _startState, RULE_e)?;
							let _local_ctx_fn = |recog: &Self| -> &'arena DecContext<TF::Tok> {recog.ctx().unwrap().as_rule_context().unwrap()};
							recog.base.with_mut_ctx(|ctx| {
							ctx.as_rule_context_mut::<DecContext<TF::Tok>>().unwrap().x = Some(_prevctx.as_rule_context().unwrap());
							});

							recog.base.set_state(31);
							if !({recog.precpred(None, 2)}) {
								Err(ANTLRError::failed_predicate(&mut recog.base, Some("recog.precpred(None, 2)".to_owned()), None))?;
							}
							recog.base.set_state(32);
							recog.base.match_token(Labels_T__5,&mut recog.err_handler)?;
							let tmp = { " --".to_owned() + recog.ctx().unwrap().as_rule_context::<DecContext<TF::Tok>>().unwrap().x.as_ref().unwrap().get_v()}.to_owned();
							unsafe { recog.ctx_mut().unwrap().as_rule_context_mut::<DecContext<TF::Tok>>().unwrap().set_v(tmp); }
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

static ATN_SIMULATOR_MANAGER: LazyLock<ATNSimulatorManager> = LazyLock::new(|| ATNSimulatorManager::new(&_ATN));
static _ATN: LazyLock<ATN> =
    LazyLock::new(|| ATNDeserializer::new(None).deserialize(&mut _serializedATN.iter()));
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