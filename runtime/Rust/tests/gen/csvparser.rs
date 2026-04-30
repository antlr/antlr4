// Generated from CSV.g4 by ANTLR 4.13.2
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
use super::csvlistener::*;
use super::csvvisitor::*;

use std::marker::PhantomData;
use std::sync::LazyLock;
use std::rc::Rc;
use std::ops::{DerefMut, Deref};

dbt_antlr4::check_version!("1","3");
pub const CSV_T__0:i32=1; 
pub const CSV_T__1:i32=2; 
pub const CSV_T__2:i32=3; 
pub const CSV_WS:i32=4; 
pub const CSV_TEXT:i32=5; 
pub const CSV_STRING:i32=6;
pub const CSV_EOF:i32=EOF;
pub const RULE_csvFile:usize = 0; 
pub const RULE_hdr:usize = 1; 
pub const RULE_row:usize = 2; 
pub const RULE_field:usize = 3;
pub const ruleNames: [&'static str; 4] = [
    "csvFile", "hdr", "row", "field"
];

pub const _LITERAL_NAMES: [Option<&'static str>;4] = [
	None, Some("','"), Some("'\\r'"), Some("'\\n'")
];
pub const _SYMBOLIC_NAMES: [Option<&'static str>;7]  = [
	None, None, None, None, Some("WS"), Some("TEXT"), Some("STRING")
];

static VOCABULARY: LazyLock<Box<dyn Vocabulary>> = LazyLock::new(|| Box::new(VocabularyImpl::new(_LITERAL_NAMES.iter(), _SYMBOLIC_NAMES.iter(), None)));

pub type BaseParserType<'input, 'arena, Input, TF> = BaseParser<'input, 'arena, CSVParserExt<'input, 'arena>, CSVParserNodeKind, Input, TF>;
pub fn parser_simulator_manager() -> &'static ATNSimulatorManager { &ATN_SIMULATOR_MANAGER }

pub struct CSVParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	base: BaseParserType<'input, 'arena, Input, TF>,
    interpreter: Rc<ParserATNSimulator<'arena>>,
    err_handler: ErrorStrategyDelegate<'input, 'arena, TF, BaseParserType<'input, 'arena, Input, TF>>,
}

impl<'input, 'arena, Input, TF> CSVParser<'input, 'arena, Input, TF>
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
				CSVParserExt {
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
        L: CSVListener<'arena, TF::Tok> + 'static,
    {
        let id = ListenerId::new(&listener);
        self.base.add_dyn_parse_listener(listener);
        id
    }
}
pub trait Visitable<'input: 'arena, 'arena, Tok: Token + 'input> {
    fn accept<V>(&'arena self, visitor: &mut V) -> Result<V::Return, ANTLRError>
    where
        'input: 'arena,
        V: CSVVisitor<'input, 'arena, Tok> + ?Sized;
}
pub struct CSVTreeWalker;
impl CSVTreeWalker
{
    pub fn walk<'input, 'arena, Tok, L, T>(
        listener: Box<L>,
        tree: &'arena T,
    ) -> Result<Box<L>, ANTLRError>
    where
        'input: 'arena,
        L: CSVListener<'arena, Tok> + 'static,
        T: NodeInner<'input, 'arena, CSVParserNodeKind, Tok>,
        Tok: Token + 'input,
    {
        let listener_ptr = Box::into_raw(listener);
        let listener = unsafe { Box::from_raw(listener_ptr as *mut <CSVParserNodeKind as NodeKindType<Tok>>::Listener) };
        let listener = ParseTreeWalker::walk(listener, tree.as_node())?;
        Ok(unsafe { Box::from_raw(Box::into_raw(listener) as *mut L) } )
    }
}

#[derive(Clone, Copy, PartialEq, Eq, Debug)]
#[repr(u16)]
pub enum CSVParserNodeKind {
    CsvFileContext,
    HdrContext,
    RowContext,
    FieldContext,
    Terminal,
    Error,
}
pub type CSVParserNode<'input, 'arena, Tok = CommonToken<'input>> = TreeNode<'input, 'arena, CSVParserNodeKind, Tok>;

dbt_antlr4::impl_deref! { parser => CSVParser }
dbt_antlr4::impl_node_kind! { CSVParserNodeKind {
; CsvFileContext(enter_csvFile, exit_csvFile,  visit_csvFile), HdrContext(enter_hdr, exit_hdr,  visit_hdr), RowContext(enter_row, exit_row,  visit_row), FieldContext(enter_field, exit_field,  visit_field), 
    }; listener = dyn CSVListener<'arena, Tok>, visitor = CSVVisitor,
}

pub struct CSVParserExt<'input, 'arena> {
	_pd: PhantomData<(&'input str, &'arena ())>,
}

impl<'input, 'arena> CSVParserExt<'input, 'arena> {
}

impl<'input, 'arena, Input, TF> ParserRecog<'input, 'arena, BaseParserType<'input, 'arena, Input, TF>, TF::Tok> for CSVParserExt<'input, 'arena>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{}

impl<'input, 'arena, Input, TF> Actions<'input, 'arena, BaseParserType<'input, 'arena, Input, TF>, TF::Tok> for CSVParserExt<'input, 'arena>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	fn get_grammar_file_name(&self) -> & str{ "CSV.g4" }
   	fn get_rule_names(&self) -> &[& str] { &ruleNames }
   	fn get_vocabulary(&self) -> &dyn Vocabulary { &**VOCABULARY }
}
//------------------- csvFile ----------------
pub type CsvFileContextAll<'input, 'arena, Tok = CommonToken<'input>> = CsvFileContext<'input, 'arena, Tok>;

pub type CsvFileContext<'input, 'arena, Tok = CommonToken<'input>> = BaseParserRuleContext<'input, 'arena, CsvFileContextExt<'input, 'arena, Tok>, CSVParserNodeKind, Tok>;
dbt_antlr4::impl_visitable! { CSVVisitor::CsvFileContext(visit_csvFile) }
#[derive(Debug)]
pub struct CsvFileContextExt<'input: 'arena, 'arena, Tok: Token + 'input = CommonToken<'input>> {
    ph: PhantomData<(&'arena (), &'input Tok)>,
}

impl<'input: 'arena, 'arena, Tok> CustomRuleContext<'input, 'arena, Tok> for CsvFileContextExt<'input, 'arena, Tok>
where
    Tok: Token + 'input,
{
	type NodeKind = CSVParserNodeKind;
    fn node_tag() -> CSVParserNodeKind { CSVParserNodeKind::CsvFileContext }
	fn get_rule_index(&self) -> usize { RULE_csvFile }
    fn make_node(
        arena: &'arena Arena,
        ctx: CsvFileContext<'input, 'arena, Tok>,
    ) -> *mut CSVParserNode<'input, 'arena, Tok> {
        arena.alloc_zeroed_node(ctx)}
    fn cast_from<'a>(
        node: &'a CSVParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a CsvFileContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => CsvFileContext<'input, 'arena, Tok>))
        } else {
            None
        }
    }
    fn cast_from_mut<'a>(
        node: &'a mut CSVParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a mut CsvFileContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => mut CsvFileContext<'input, 'arena, Tok>))
        } else {
            None
        }
    }
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> CsvFileContextExt<'input, 'arena, Tok>{
	fn create(arena: &'arena Arena, parent: Option<&'arena CSVParserNode<'input, 'arena, Tok>>, invoking_state: i32) -> &'arena mut CSVParserNode<'input, 'arena, Tok>
    {
        BaseParserRuleContext::create(arena, parent, invoking_state, CsvFileContextExt {
				ph: PhantomData
			}
		)
	}
}

pub trait CsvFileContextAttrs<'input, 'arena, Tok>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
    Tok: Token + 'input,
{
    fn hdr(&self) -> Option<&'arena HdrContextAll<'input, 'arena, Tok>>;
    fn row_all(&self) -> Vec<&'arena RowContextAll<'input, 'arena, Tok>>;
    fn row(&self, i: usize) -> Option<&'arena RowContextAll<'input, 'arena, Tok>>;
}

impl<'input, 'arena, Tok: Token + 'input> CsvFileContextAttrs<'input, 'arena, Tok> for CsvFileContext<'input, 'arena, Tok>
where
    'input: 'arena,
{
    fn hdr(&self) -> Option<&'arena HdrContextAll<'input, 'arena, Tok>> {
        self.child_of_type(0)
    }
    fn row_all(&self) -> Vec<&'arena RowContextAll<'input, 'arena, Tok>> {
        self.children_of_type()
    }
    fn row(&self, i: usize) -> Option<&'arena RowContextAll<'input, 'arena, Tok>> {
        self.child_of_type(i)
    }
}

impl<'input, 'arena, Input, TF> CSVParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	pub fn csvFile(&mut self,) -> Result<&'arena CsvFileContextAll<'input, 'arena, TF::Tok>, ANTLRError> {
		let recog = self;
        let _parentctx = recog.base.take_ctx();
        recog.base.enter_rule(CsvFileContextExt::create(recog.get_arena(), _parentctx, recog.get_state()), 0, RULE_csvFile)?;
        let _local_ctx_fn = |recog: &Self| -> &'arena CsvFileContext<TF::Tok> {recog.ctx().unwrap().as_rule_context().unwrap()};
		let mut _la: i32 = -1;
		let result: Result<(), ANTLRError> = (|| {
			/*------- Outer Most Alt 1 -------*/
			unsafe { recog.ctx_mut().unwrap().set_alt_number(1); }
			{
			/*InvokeRule hdr*/
			recog.base.set_state(8);
			recog.hdr()?;
			recog.base.set_state(10); 
			recog.err_handler.sync(&mut recog.base)?;
			_la = recog.base.input.la(1);
			loop {
				{
				{
				/*InvokeRule row*/
				recog.base.set_state(9);
				recog.row()?;
				}
				}
				recog.base.set_state(12); 
				recog.err_handler.sync(&mut recog.base)?;
				_la = recog.base.input.la(1);
				if !((((_la) & !0x3f) == 0 && ((1usize << _la) & 110) != 0)) {break}
			}
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
//------------------- hdr ----------------
pub type HdrContextAll<'input, 'arena, Tok = CommonToken<'input>> = HdrContext<'input, 'arena, Tok>;

pub type HdrContext<'input, 'arena, Tok = CommonToken<'input>> = BaseParserRuleContext<'input, 'arena, HdrContextExt<'input, 'arena, Tok>, CSVParserNodeKind, Tok>;
dbt_antlr4::impl_visitable! { CSVVisitor::HdrContext(visit_hdr) }
#[derive(Debug)]
pub struct HdrContextExt<'input: 'arena, 'arena, Tok: Token + 'input = CommonToken<'input>> {
    ph: PhantomData<(&'arena (), &'input Tok)>,
}

impl<'input: 'arena, 'arena, Tok> CustomRuleContext<'input, 'arena, Tok> for HdrContextExt<'input, 'arena, Tok>
where
    Tok: Token + 'input,
{
	type NodeKind = CSVParserNodeKind;
    fn node_tag() -> CSVParserNodeKind { CSVParserNodeKind::HdrContext }
	fn get_rule_index(&self) -> usize { RULE_hdr }
    fn make_node(
        arena: &'arena Arena,
        ctx: HdrContext<'input, 'arena, Tok>,
    ) -> *mut CSVParserNode<'input, 'arena, Tok> {
        arena.alloc_zeroed_node(ctx)}
    fn cast_from<'a>(
        node: &'a CSVParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a HdrContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => HdrContext<'input, 'arena, Tok>))
        } else {
            None
        }
    }
    fn cast_from_mut<'a>(
        node: &'a mut CSVParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a mut HdrContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => mut HdrContext<'input, 'arena, Tok>))
        } else {
            None
        }
    }
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> HdrContextExt<'input, 'arena, Tok>{
	fn create(arena: &'arena Arena, parent: Option<&'arena CSVParserNode<'input, 'arena, Tok>>, invoking_state: i32) -> &'arena mut CSVParserNode<'input, 'arena, Tok>
    {
        BaseParserRuleContext::create(arena, parent, invoking_state, HdrContextExt {
				ph: PhantomData
			}
		)
	}
}

pub trait HdrContextAttrs<'input, 'arena, Tok>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
    Tok: Token + 'input,
{
    fn row(&self) -> Option<&'arena RowContextAll<'input, 'arena, Tok>>;
}

impl<'input, 'arena, Tok: Token + 'input> HdrContextAttrs<'input, 'arena, Tok> for HdrContext<'input, 'arena, Tok>
where
    'input: 'arena,
{
    fn row(&self) -> Option<&'arena RowContextAll<'input, 'arena, Tok>> {
        self.child_of_type(0)
    }
}

impl<'input, 'arena, Input, TF> CSVParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	pub fn hdr(&mut self,) -> Result<&'arena HdrContextAll<'input, 'arena, TF::Tok>, ANTLRError> {
		let recog = self;
        let _parentctx = recog.base.take_ctx();
        recog.base.enter_rule(HdrContextExt::create(recog.get_arena(), _parentctx, recog.get_state()), 2, RULE_hdr)?;
        let _local_ctx_fn = |recog: &Self| -> &'arena HdrContext<TF::Tok> {recog.ctx().unwrap().as_rule_context().unwrap()};
		let result: Result<(), ANTLRError> = (|| {
			/*------- Outer Most Alt 1 -------*/
			unsafe { recog.ctx_mut().unwrap().set_alt_number(1); }
			{
			/*InvokeRule row*/
			recog.base.set_state(14);
			recog.row()?;
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
//------------------- row ----------------
pub type RowContextAll<'input, 'arena, Tok = CommonToken<'input>> = RowContext<'input, 'arena, Tok>;

pub type RowContext<'input, 'arena, Tok = CommonToken<'input>> = BaseParserRuleContext<'input, 'arena, RowContextExt<'input, 'arena, Tok>, CSVParserNodeKind, Tok>;
dbt_antlr4::impl_visitable! { CSVVisitor::RowContext(visit_row) }
#[derive(Debug)]
pub struct RowContextExt<'input: 'arena, 'arena, Tok: Token + 'input = CommonToken<'input>> {
    ph: PhantomData<(&'arena (), &'input Tok)>,
}

impl<'input: 'arena, 'arena, Tok> CustomRuleContext<'input, 'arena, Tok> for RowContextExt<'input, 'arena, Tok>
where
    Tok: Token + 'input,
{
	type NodeKind = CSVParserNodeKind;
    fn node_tag() -> CSVParserNodeKind { CSVParserNodeKind::RowContext }
	fn get_rule_index(&self) -> usize { RULE_row }
    fn make_node(
        arena: &'arena Arena,
        ctx: RowContext<'input, 'arena, Tok>,
    ) -> *mut CSVParserNode<'input, 'arena, Tok> {
        arena.alloc_zeroed_node(ctx)}
    fn cast_from<'a>(
        node: &'a CSVParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a RowContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => RowContext<'input, 'arena, Tok>))
        } else {
            None
        }
    }
    fn cast_from_mut<'a>(
        node: &'a mut CSVParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a mut RowContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => mut RowContext<'input, 'arena, Tok>))
        } else {
            None
        }
    }
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> RowContextExt<'input, 'arena, Tok>{
	fn create(arena: &'arena Arena, parent: Option<&'arena CSVParserNode<'input, 'arena, Tok>>, invoking_state: i32) -> &'arena mut CSVParserNode<'input, 'arena, Tok>
    {
        BaseParserRuleContext::create(arena, parent, invoking_state, RowContextExt {
				ph: PhantomData
			}
		)
	}
}

pub trait RowContextAttrs<'input, 'arena, Tok>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
    Tok: Token + 'input,
{
    fn field_all(&self) -> Vec<&'arena FieldContextAll<'input, 'arena, Tok>>;
    fn field(&self, i: usize) -> Option<&'arena FieldContextAll<'input, 'arena, Tok>>;
}

impl<'input, 'arena, Tok: Token + 'input> RowContextAttrs<'input, 'arena, Tok> for RowContext<'input, 'arena, Tok>
where
    'input: 'arena,
{
    fn field_all(&self) -> Vec<&'arena FieldContextAll<'input, 'arena, Tok>> {
        self.children_of_type()
    }
    fn field(&self, i: usize) -> Option<&'arena FieldContextAll<'input, 'arena, Tok>> {
        self.child_of_type(i)
    }
}

impl<'input, 'arena, Input, TF> CSVParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	pub fn row(&mut self,) -> Result<&'arena RowContextAll<'input, 'arena, TF::Tok>, ANTLRError> {
		let recog = self;
        let _parentctx = recog.base.take_ctx();
        recog.base.enter_rule(RowContextExt::create(recog.get_arena(), _parentctx, recog.get_state()), 4, RULE_row)?;
        let _local_ctx_fn = |recog: &Self| -> &'arena RowContext<TF::Tok> {recog.ctx().unwrap().as_rule_context().unwrap()};
		let mut _la: i32 = -1;
		let result: Result<(), ANTLRError> = (|| {
			/*------- Outer Most Alt 1 -------*/
			unsafe { recog.ctx_mut().unwrap().set_alt_number(1); }
			{
			/*InvokeRule field*/
			recog.base.set_state(16);
			recog.field()?;
			recog.base.set_state(21);
			recog.err_handler.sync(&mut recog.base)?;
			_la = recog.base.input.la(1);
			while _la==CSV_T__0 {
				{
				{
				recog.base.set_state(17);
				recog.base.match_token(CSV_T__0,&mut recog.err_handler)?;
				/*InvokeRule field*/
				recog.base.set_state(18);
				recog.field()?;
				}
				}
				recog.base.set_state(23);
				recog.err_handler.sync(&mut recog.base)?;
				_la = recog.base.input.la(1);
			}
			recog.base.set_state(25);
			recog.err_handler.sync(&mut recog.base)?;
			_la = recog.base.input.la(1);
			if _la==CSV_T__1 {
				{
				recog.base.set_state(24);
				recog.base.match_token(CSV_T__1,&mut recog.err_handler)?;
				}
			}

			recog.base.set_state(27);
			recog.base.match_token(CSV_T__2,&mut recog.err_handler)?;
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
//------------------- field ----------------
pub type FieldContextAll<'input, 'arena, Tok = CommonToken<'input>> = FieldContext<'input, 'arena, Tok>;

pub type FieldContext<'input, 'arena, Tok = CommonToken<'input>> = BaseParserRuleContext<'input, 'arena, FieldContextExt<'input, 'arena, Tok>, CSVParserNodeKind, Tok>;
dbt_antlr4::impl_visitable! { CSVVisitor::FieldContext(visit_field) }
#[derive(Debug)]
pub struct FieldContextExt<'input: 'arena, 'arena, Tok: Token + 'input = CommonToken<'input>> {
    ph: PhantomData<(&'arena (), &'input Tok)>,
}

impl<'input: 'arena, 'arena, Tok> CustomRuleContext<'input, 'arena, Tok> for FieldContextExt<'input, 'arena, Tok>
where
    Tok: Token + 'input,
{
	type NodeKind = CSVParserNodeKind;
    fn node_tag() -> CSVParserNodeKind { CSVParserNodeKind::FieldContext }
	fn get_rule_index(&self) -> usize { RULE_field }
    fn make_node(
        arena: &'arena Arena,
        ctx: FieldContext<'input, 'arena, Tok>,
    ) -> *mut CSVParserNode<'input, 'arena, Tok> {
        arena.alloc_zeroed_node(ctx)}
    fn cast_from<'a>(
        node: &'a CSVParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a FieldContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => FieldContext<'input, 'arena, Tok>))
        } else {
            None
        }
    }
    fn cast_from_mut<'a>(
        node: &'a mut CSVParserNode<'input, 'arena, Tok>,
    ) -> Option<&'a mut FieldContext<'input, 'arena, Tok>> {
        if node.node_tag() == <Self as CustomRuleContext<'input, 'arena, Tok>>::node_tag() {
            Some(dbt_antlr4::cast_unchecked!(node.ctx_ptr() => mut FieldContext<'input, 'arena, Tok>))
        } else {
            None
        }
    }
}

impl<'input: 'arena, 'arena, Tok: Token + 'input> FieldContextExt<'input, 'arena, Tok>{
	fn create(arena: &'arena Arena, parent: Option<&'arena CSVParserNode<'input, 'arena, Tok>>, invoking_state: i32) -> &'arena mut CSVParserNode<'input, 'arena, Tok>
    {
        BaseParserRuleContext::create(arena, parent, invoking_state, FieldContextExt {
				ph: PhantomData
			}
		)
	}
}

pub trait FieldContextAttrs<'input, 'arena, Tok>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
    Tok: Token + 'input,
{
    /// Retrieves first TerminalNode corresponding to token TEXT
    /// Returns `None` if there is no child corresponding to token TEXT
    fn TEXT(&self) -> Option<&TerminalNode<'input, 'arena, Tok>>;
    /// Retrieves first TerminalNode corresponding to token STRING
    /// Returns `None` if there is no child corresponding to token STRING
    fn STRING(&self) -> Option<&TerminalNode<'input, 'arena, Tok>>;
}

impl<'input, 'arena, Tok: Token + 'input> FieldContextAttrs<'input, 'arena, Tok> for FieldContext<'input, 'arena, Tok>
where
    'input: 'arena,
{
    /// Retrieves first TerminalNode corresponding to token TEXT
    /// Returns `None` if there is no child corresponding to token TEXT
    fn TEXT(&self) -> Option<&TerminalNode<'input, 'arena, Tok>> {
        self.children_of_type::<TerminalNode<Tok>>().into_iter().find(|child| child.symbol.get_token_type() == CSV_TEXT)
    }
    /// Retrieves first TerminalNode corresponding to token STRING
    /// Returns `None` if there is no child corresponding to token STRING
    fn STRING(&self) -> Option<&TerminalNode<'input, 'arena, Tok>> {
        self.children_of_type::<TerminalNode<Tok>>().into_iter().find(|child| child.symbol.get_token_type() == CSV_STRING)
    }
}

impl<'input, 'arena, Input, TF> CSVParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	pub fn field(&mut self,) -> Result<&'arena FieldContextAll<'input, 'arena, TF::Tok>, ANTLRError> {
		let recog = self;
        let _parentctx = recog.base.take_ctx();
        recog.base.enter_rule(FieldContextExt::create(recog.get_arena(), _parentctx, recog.get_state()), 6, RULE_field)?;
        let _local_ctx_fn = |recog: &Self| -> &'arena FieldContext<TF::Tok> {recog.ctx().unwrap().as_rule_context().unwrap()};
		let result: Result<(), ANTLRError> = (|| {
			recog.base.set_state(32);
			recog.err_handler.sync(&mut recog.base)?;
			match recog.base.input.la(1) {
			    CSV_TEXT  => {
			        /*------- Outer Most Alt 1 -------*/
			        unsafe { recog.ctx_mut().unwrap().set_alt_number(1); }
			        {
			        recog.base.set_state(29);
			        recog.base.match_token(CSV_TEXT,&mut recog.err_handler)?;
			        }}
			    CSV_STRING  => {
			        /*------- Outer Most Alt 2 -------*/
			        unsafe { recog.ctx_mut().unwrap().set_alt_number(2); }
			        {
			        recog.base.set_state(30);
			        recog.base.match_token(CSV_STRING,&mut recog.err_handler)?;
			        }}
			    CSV_T__0 |CSV_T__1 |CSV_T__2  => {
			        /*------- Outer Most Alt 3 -------*/
			        unsafe { recog.ctx_mut().unwrap().set_alt_number(3); }
			        {
			        }}
				_ => Err(ANTLRError::no_alt(&mut recog.base))?
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
    4, 1, 6, 35, 2, 0, 7, 0, 2, 1, 7, 1, 2, 2, 7, 2, 2, 3, 7, 3, 1, 0, 1, 
    0, 4, 0, 11, 8, 0, 11, 0, 12, 0, 12, 1, 1, 1, 1, 1, 2, 1, 2, 1, 2, 5, 
    2, 20, 8, 2, 10, 2, 12, 2, 23, 9, 2, 1, 2, 3, 2, 26, 8, 2, 1, 2, 1, 
    2, 1, 3, 1, 3, 1, 3, 3, 3, 33, 8, 3, 1, 3, 0, 0, 4, 0, 2, 4, 6, 0, 0, 
    35, 0, 8, 1, 0, 0, 0, 2, 14, 1, 0, 0, 0, 4, 16, 1, 0, 0, 0, 6, 32, 1, 
    0, 0, 0, 8, 10, 3, 2, 1, 0, 9, 11, 3, 4, 2, 0, 10, 9, 1, 0, 0, 0, 11, 
    12, 1, 0, 0, 0, 12, 10, 1, 0, 0, 0, 12, 13, 1, 0, 0, 0, 13, 1, 1, 0, 
    0, 0, 14, 15, 3, 4, 2, 0, 15, 3, 1, 0, 0, 0, 16, 21, 3, 6, 3, 0, 17, 
    18, 5, 1, 0, 0, 18, 20, 3, 6, 3, 0, 19, 17, 1, 0, 0, 0, 20, 23, 1, 0, 
    0, 0, 21, 19, 1, 0, 0, 0, 21, 22, 1, 0, 0, 0, 22, 25, 1, 0, 0, 0, 23, 
    21, 1, 0, 0, 0, 24, 26, 5, 2, 0, 0, 25, 24, 1, 0, 0, 0, 25, 26, 1, 0, 
    0, 0, 26, 27, 1, 0, 0, 0, 27, 28, 5, 3, 0, 0, 28, 5, 1, 0, 0, 0, 29, 
    33, 5, 5, 0, 0, 30, 33, 5, 6, 0, 0, 31, 33, 1, 0, 0, 0, 32, 29, 1, 0, 
    0, 0, 32, 30, 1, 0, 0, 0, 32, 31, 1, 0, 0, 0, 33, 7, 1, 0, 0, 0, 4, 
    12, 21, 25, 32
]);