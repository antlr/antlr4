// Generated from VisitorBasic.g4 by ANTLR 4.13.2
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
use super::visitorbasiclistener::*;
use super::visitorbasicvisitor::*;

use std::marker::PhantomData;
use std::sync::LazyLock;
use std::rc::Rc;
use std::ops::{DerefMut, Deref};

dbt_antlr4::check_version!("1","3");
pub const VisitorBasic_A:i32=1;
pub const VisitorBasic_EOF:i32=EOF;
pub const RULE_s:usize = 0;
pub const ruleNames: [&'static str; 1] = [
    "s"
];

pub const _LITERAL_NAMES: [Option<&'static str>;2] = [
	None, Some("'A'")
];
pub const _SYMBOLIC_NAMES: [Option<&'static str>;2]  = [
	None, Some("A")
];

static VOCABULARY: LazyLock<Box<dyn Vocabulary>> = LazyLock::new(|| Box::new(VocabularyImpl::new(_LITERAL_NAMES.iter(), _SYMBOLIC_NAMES.iter(), None)));

pub type BaseParserType<'input, 'arena, Input, TF> = BaseParser<'input, 'arena, VisitorBasicParserExt<'input, 'arena>, VisitorBasicParserNodeKind, Input, TF>;
pub fn parser_simulator_manager() -> &'static ATNSimulatorManager { &ATN_SIMULATOR_MANAGER }

pub struct VisitorBasicParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	base: BaseParserType<'input, 'arena, Input, TF>,
    interpreter: Rc<ParserATNSimulator<'arena>>,
    err_handler: ErrorStrategyDelegate<'input, 'arena, TF, BaseParserType<'input, 'arena, Input, TF>>,
}

impl<'input, 'arena, Input, TF> VisitorBasicParser<'input, 'arena, Input, TF>
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
				VisitorBasicParserExt {
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
        L: VisitorBasicListener<'arena> + 'static,
    {
        let id = ListenerId::new(&listener);
        self.base.add_dyn_parse_listener(listener);
        id
    }
}
pub trait Visitable<'input, 'arena> {
    fn accept<V>(&'arena self, visitor: &mut V) -> Result<V::Return, ANTLRError>
    where
        'input: 'arena,
        V: VisitorBasicVisitor<'input, 'arena> + ?Sized;
}
pub struct VisitorBasicTreeWalker;
impl VisitorBasicTreeWalker
{
    pub fn walk<'input,'arena, L, T>(
        listener: Box<L>,
        tree: &'arena T,
    ) -> Result<Box<L>, ANTLRError>
    where
        'input: 'arena,
        L: VisitorBasicListener<'arena> + 'static,
        T: NodeInner<'input, 'arena, VisitorBasicParserNodeKind>,
    {
        let listener_ptr = Box::into_raw(listener);
        let listener = unsafe { Box::from_raw(listener_ptr as *mut <VisitorBasicParserNodeKind as NodeKindType>::Listener) };
        let listener = ParseTreeWalker::walk(listener, tree.as_node())?;
        Ok(unsafe { Box::from_raw(Box::into_raw(listener) as *mut L) } )
    }
}

#[derive(Clone, Copy, PartialEq, Eq, Debug)]
#[repr(u16)]
pub enum VisitorBasicParserNodeKind {
    SContext,
    Terminal,
    Error,
}
pub type VisitorBasicParserNode<'input, 'arena> = TreeNode<'input, 'arena, VisitorBasicParserNodeKind>;

dbt_antlr4::impl_deref! { parser => VisitorBasicParser }
dbt_antlr4::impl_node_kind! { VisitorBasicParserNodeKind {
; SContext(enter_s, exit_s,  visit_s), 
    }; listener = dyn VisitorBasicListener<'arena>, visitor = VisitorBasicVisitor,
}

pub struct VisitorBasicParserExt<'input, 'arena> {
	_pd: PhantomData<(&'input str, &'arena ())>,
}

impl<'input, 'arena> VisitorBasicParserExt<'input, 'arena> {
}

impl<'input, 'arena, Input, TF> ParserRecog<'input, 'arena, BaseParserType<'input, 'arena, Input, TF>> for VisitorBasicParserExt<'input, 'arena>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{}

impl<'input, 'arena, Input, TF> Actions<'input, 'arena, BaseParserType<'input, 'arena, Input, TF>> for VisitorBasicParserExt<'input, 'arena>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	fn get_grammar_file_name(&self) -> & str{ "VisitorBasic.g4" }
   	fn get_rule_names(&self) -> &[& str] { &ruleNames }
   	fn get_vocabulary(&self) -> &dyn Vocabulary { &**VOCABULARY }
}
//------------------- s ----------------
pub type SContextAll<'input, 'arena> = SContext<'input, 'arena>;

pub type SContext<'input, 'arena> = BaseParserRuleContext<'input, 'arena, SContextExt<'input, 'arena>, VisitorBasicParserNodeKind>;
dbt_antlr4::impl_visitable! { VisitorBasicVisitor::SContext(visit_s) }
#[derive(Debug)]
pub struct SContextExt<'input, 'arena> {
    ph: PhantomData<(&'arena (), &'input ())>,
}

impl<'input: 'arena, 'arena> CustomRuleContext<'input, 'arena> for SContextExt<'input, 'arena>
{
	type NodeKind = VisitorBasicParserNodeKind;
    fn node_tag() -> VisitorBasicParserNodeKind { VisitorBasicParserNodeKind::SContext }
	fn get_rule_index(&self) -> usize { RULE_s }
    fn make_node(
        arena: &'arena Arena,
        ctx: SContext<'input, 'arena>,
    ) -> *mut VisitorBasicParserNode<'input, 'arena> {
        arena.alloc_zeroed_node(ctx)}
    fn cast_from<'a>(
        node: &'a VisitorBasicParserNode<'input, 'arena>,
    ) -> Option<&'a SContext<'input, 'arena>> {
        if node.node_tag() == Self::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => SContext<'input, 'arena>))
        } else {
            None
        }
    }
    fn cast_from_mut<'a>(
        node: &'a mut VisitorBasicParserNode<'input, 'arena>,
    ) -> Option<&'a mut SContext<'input, 'arena>> {
        if node.node_tag() == Self::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => mut SContext<'input, 'arena>))
        } else {
            None
        }
    }
}

impl<'input, 'arena> SContextExt<'input, 'arena>{
	fn create(arena: &'arena Arena, parent: Option<&'arena VisitorBasicParserNode<'input, 'arena>>, invoking_state: i32) -> &'arena mut VisitorBasicParserNode<'input, 'arena>
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
    /// Retrieves first TerminalNode corresponding to token A
    /// Returns `None` if there is no child corresponding to token A
    fn A(&self) -> Option<&TerminalNode<'input, 'arena>>;
    /// Retrieves first TerminalNode corresponding to token EOF
    /// Returns `None` if there is no child corresponding to token EOF
    fn EOF(&self) -> Option<&TerminalNode<'input, 'arena>>;
}

impl<'input, 'arena> SContextAttrs<'input, 'arena> for SContext<'input, 'arena>
where
    'input: 'arena,
{
    /// Retrieves first TerminalNode corresponding to token A
    /// Returns `None` if there is no child corresponding to token A
    fn A(&self) -> Option<&TerminalNode<'input, 'arena>> {
    	self.get_token(VisitorBasic_A, 0)
    }
    /// Retrieves first TerminalNode corresponding to token EOF
    /// Returns `None` if there is no child corresponding to token EOF
    fn EOF(&self) -> Option<&TerminalNode<'input, 'arena>> {
    	self.get_token(VisitorBasic_EOF, 0)
    }
}

impl<'input, 'arena, Input, TF> VisitorBasicParser<'input, 'arena, Input, TF>
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
			recog.base.set_state(2);
			recog.base.match_token(VisitorBasic_A,&mut recog.err_handler)?;
			recog.base.set_state(3);
			recog.base.match_token(VisitorBasic_EOF,&mut recog.err_handler)?;
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

static ATN_SIMULATOR_MANAGER: LazyLock<ATNSimulatorManager> = LazyLock::new(|| ATNSimulatorManager::new(&_ATN));
static _ATN: LazyLock<ATN> =
    LazyLock::new(|| ATNDeserializer::new(None).deserialize(&mut _serializedATN.iter()));
static _serializedATN: LazyLock<Vec<i32>> = LazyLock::new(|| vec![
    4, 1, 1, 6, 2, 0, 7, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 4, 
    0, 2, 1, 0, 0, 0, 2, 3, 5, 1, 0, 0, 3, 4, 5, 0, 0, 1, 4, 1, 1, 0, 0, 
    0, 0
]);