// Generated from ReferenceToATN.g4 by ANTLR 4.13.2
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
use dbt_antlr4::dfa::ParserDFA as DFA;
use dbt_antlr4::atn::{ATN, INVALID_ALT};
use dbt_antlr4::error_strategy::{DefaultErrorStrategy, ErrorStrategyDelegate, ErrorStrategy};
use dbt_antlr4::parser_rule_context::{BaseParserRuleContext, BaseParserRuleContextInner, ParserRuleContext};
use dbt_antlr4::tree::*;
use dbt_antlr4::token::{TOKEN_EOF,Token};
use dbt_antlr4::int_stream::EOF;
use dbt_antlr4::vocabulary::{Vocabulary,VocabularyImpl};
use dbt_antlr4::token_factory::TokenFactory;
use super::referencetoatnlistener::*;
use std::marker::PhantomData;
use std::sync::{LazyLock, Arc};
use std::ops::{DerefMut, Deref};

dbt_antlr4::check_version!("1","1");
pub const ReferenceToATN_ID:i32=1; 
pub const ReferenceToATN_ATN:i32=2; 
pub const ReferenceToATN_WS:i32=3;
pub const ReferenceToATN_EOF:i32=EOF;
pub const RULE_a:usize = 0;
pub const ruleNames: [&'static str; 1] = [
    "a"
];

pub const _LITERAL_NAMES: [Option<&'static str>;0] = [
];
pub const _SYMBOLIC_NAMES: [Option<&'static str>;4]  = [
	None, Some("ID"), Some("ATN"), Some("WS")
];

static _shared_context_cache: LazyLock<PredictionContextCache> = LazyLock::new(|| PredictionContextCache::new());
static VOCABULARY: LazyLock<Box<dyn Vocabulary>> = LazyLock::new(|| Box::new(VocabularyImpl::new(_LITERAL_NAMES.iter(), _SYMBOLIC_NAMES.iter(), None)));

pub type BaseParserType<'input, 'arena, Input, TF> = BaseParser<'input, 'arena, ReferenceToATNParserExt<'input, 'arena>, ReferenceToATNParserContextNode<'input, 'arena>, Input, TF, dyn ReferenceToATNListener<'input, 'arena>>;

pub struct ReferenceToATNParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	base: BaseParserType<'input, 'arena, Input, TF>,
    interpreter: Arc<ParserATNSimulator>,
    err_handler: ErrorStrategyDelegate<'input, 'arena, TF, BaseParserType<'input, 'arena, Input, TF>>,
}

impl<'input, 'arena, Input, TF> ReferenceToATNParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
    pub fn with_strategy(arena: &'arena Arena, input: Input, strategy: Box<dyn ErrorStrategy<'input, 'arena, TF, BaseParserType<'input, 'arena, Input, TF>> + 'arena>) -> Self {
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
				ReferenceToATNParserExt {
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
        L: ReferenceToATNListener<'input, 'arena> + 'static,
    {
        let id = ListenerId::new(&listener);
        self.base.add_dyn_parse_listener(listener);
        id
    }
}
pub struct ReferenceToATNTreeWalker;
impl ReferenceToATNTreeWalker
{
    pub fn walk<'input,'arena, L, T>(
        listener: Box<L>,
        tree: &'arena T,
    ) -> Result<Box<L>, ANTLRError>
    where
        'input: 'arena,
        L: ReferenceToATNListener<'input, 'arena> + 'static,
        T: NodeInner<'input, 'arena, ReferenceToATNParserContextNode<'input, 'arena>>,
    {
        let Some(node) = tree.try_as_node() else {
            return Err(ANTLRError::custom_error("TreeWalker can only walk non-leaf nodes".to_string()));
        };
        let listener_ptr = Box::into_raw(listener);
        let listener = unsafe { Box::from_raw(listener_ptr as *mut <ReferenceToATNParserContextNode as RuleNode>::Listener) };
        let listener = ParseTreeWalker::walk(listener, node)?;
        Ok(unsafe { Box::from_raw(Box::into_raw(listener) as *mut L) } )
    }
}

#[derive(Debug)]
pub enum ReferenceToATNParserContextNode<'input, 'arena> {
    AContext(AContext<'input, 'arena>),

    Terminal(TerminalNode<'input, 'arena>),
    Error(ErrorNode<'input, 'arena>),
}

dbt_antlr4::impl_deref! { parser => ReferenceToATNParser }
dbt_antlr4::impl_defaults! { ReferenceToATNParserContextNode }
dbt_antlr4::impl_from_contexts! { ReferenceToATNParserContextNode { AContext(AContext),  } }
dbt_antlr4::impl_tree! { ReferenceToATNParserContextNode { AContext, } }
dbt_antlr4::impl_parse_tree! { ReferenceToATNParserContextNode { AContext, } }
dbt_antlr4::impl_rule_context! { ReferenceToATNParserContextNode { AContext,  Terminal, Error, } }
dbt_antlr4::impl_parser_rule_context! { ReferenceToATNParserContextNode { AContext,  Terminal, Error, } }
dbt_antlr4::impl_rule_node! { ReferenceToATNParserContextNode {
; AContext(enter_a, exit_a, ), 
    }; listener = dyn ReferenceToATNListener<'input, 'arena>,
}

pub struct ReferenceToATNParserExt<'input, 'arena> {
	_pd: PhantomData<(&'input str, &'arena ())>,
}

impl<'input, 'arena> ReferenceToATNParserExt<'input, 'arena> {
}

impl<'input, 'arena, Input, TF> ParserRecog<'input, 'arena, BaseParserType<'input, 'arena, Input, TF>> for ReferenceToATNParserExt<'input, 'arena>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{}

impl<'input, 'arena, Input, TF> Actions<'input, 'arena, BaseParserType<'input, 'arena, Input, TF>> for ReferenceToATNParserExt<'input, 'arena>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	fn get_grammar_file_name(&self) -> & str{ "ReferenceToATN.g4" }
   	fn get_rule_names(&self) -> &[& str] { &ruleNames }
   	fn get_vocabulary(&self) -> &dyn Vocabulary { &**VOCABULARY }
}
//------------------- a ----------------
pub type AContextAll<'input, 'arena> = AContext<'input, 'arena>;

pub type AContext<'input, 'arena> = BaseParserRuleContextInner<'input, 'arena, AContextExt<'input, 'arena>, ReferenceToATNParserContextNode<'input, 'arena>>;
pub struct AContextExt<'input, 'arena> {
    ph: PhantomData<(&'arena (), &'input ())>,
}

impl<'input, 'arena> CustomRuleContext<'input, 'arena> for AContextExt<'input, 'arena>
where
    'input: 'arena,
{
	type Node = ReferenceToATNParserContextNode<'input, 'arena>;
	fn get_rule_index(&self) -> usize { RULE_a }
    fn base_ref_from_node(node: &Self::Node) -> Option<&BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            ReferenceToATNParserContextNode::AContext(inner) => Some(inner),
            _ => None,
        }
    }
    fn base_mut_ref_from_node(node: &mut Self::Node) -> Option<&mut BaseParserRuleContext<'input, 'arena, Self>> {
        match node {
            ReferenceToATNParserContextNode::AContext(inner) => Some(inner),
            _ => None,
        }
    }
}

impl<'input, 'arena> AContextExt<'input, 'arena>{
	fn create(arena: &'arena Arena, parent: Option<&'arena ReferenceToATNParserContextNode<'input, 'arena>>, invoking_state: i32) -> AContextAll<'input, 'arena>
    where
        'input: 'arena,
    {
        BaseParserRuleContext::new(arena, parent, invoking_state, AContextExt {
				ph: PhantomData
			},
		)
	}
}

pub trait AContextAttrs<'input, 'arena>: ParserRuleContext<'input, 'arena>
where
    'input: 'arena,
{
    /// Retrieves all `TerminalNode`s corresponding to token ATN in current rule
    fn ATN_all(&self) -> Vec<&TerminalNode<'input, 'arena>>;
    /// Retrieves 'i's TerminalNode corresponding to token ATN, starting from 0.
    /// Returns `None` if number of children corresponding to token ATN is less or equal than `i`.
    fn ATN(&self, i: usize) -> Option<&TerminalNode<'input, 'arena>>;
    /// Retrieves all `TerminalNode`s corresponding to token ID in current rule
    fn ID_all(&self) -> Vec<&TerminalNode<'input, 'arena>>;
    /// Retrieves 'i's TerminalNode corresponding to token ID, starting from 0.
    /// Returns `None` if number of children corresponding to token ID is less or equal than `i`.
    fn ID(&self, i: usize) -> Option<&TerminalNode<'input, 'arena>>;
}

impl<'input, 'arena> AContextAttrs<'input, 'arena> for AContext<'input, 'arena>
where
    'input: 'arena,
{
    /// Retrieves all `TerminalNode`s corresponding to token ATN in current rule
    fn ATN_all(&self) -> Vec<&TerminalNode<'input, 'arena>> {
    	self.children_of_type()
    }
    /// Retrieves 'i's TerminalNode corresponding to token ATN, starting from 0.
    /// Returns `None` if number of children corresponding to token ATN is less or equal than `i`.
    fn ATN(&self, i: usize) -> Option<&TerminalNode<'input, 'arena>> {
    	self.get_token(ReferenceToATN_ATN, i)
    }
    /// Retrieves all `TerminalNode`s corresponding to token ID in current rule
    fn ID_all(&self) -> Vec<&TerminalNode<'input, 'arena>> {
    	self.children_of_type()
    }
    /// Retrieves 'i's TerminalNode corresponding to token ID, starting from 0.
    /// Returns `None` if number of children corresponding to token ID is less or equal than `i`.
    fn ID(&self, i: usize) -> Option<&TerminalNode<'input, 'arena>> {
    	self.get_token(ReferenceToATN_ID, i)
    }
}

impl<'input, 'arena, Input, TF> ReferenceToATNParser<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: TokenStream<'input, 'arena, TF> + 'arena,
{
	pub fn a(&mut self,) -> Result<&'arena AContextAll<'input, 'arena>, ANTLRError> {
		let recog = self;
        let _parentctx = recog.base.take_ctx();
        recog.base.enter_rule(AContextExt::create(recog.get_arena(), _parentctx, recog.get_state()).into(), 0, RULE_a)?;
        let _local_ctx_fn = |recog: &Self| -> &'arena AContext {recog.ctx().unwrap().as_rule_context().unwrap()};
		let mut _la: i32 = -1;
		let result: Result<(), ANTLRError> = (|| {
	        let mut _alt: i32;
			/*------- Outer Most Alt 1 -------*/
			unsafe { recog.ctx_mut().unwrap().set_alt_number(1); }
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
						if recog.base.input.la(1)==TOKEN_EOF { recog.base.matched_eof = true };
						recog.err_handler.report_match(&mut recog.base);
						recog.base.consume(&mut recog.err_handler)?;
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

			println!("{}",{let temp = recog.base.input.lt(-1).map(|it|it.get_token_index()).unwrap_or(-1); recog.input.get_text_from_interval(recog.ctx().unwrap().start().get_token_index(), temp)});
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
    4, 1, 3, 14, 2, 0, 7, 0, 1, 0, 5, 0, 4, 8, 0, 10, 0, 12, 0, 7, 9, 0, 
    1, 0, 3, 0, 10, 8, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 2, 
    14, 0, 5, 1, 0, 0, 0, 2, 4, 7, 0, 0, 0, 3, 2, 1, 0, 0, 0, 4, 7, 1, 0, 
    0, 0, 5, 3, 1, 0, 0, 0, 5, 6, 1, 0, 0, 0, 6, 9, 1, 0, 0, 0, 7, 5, 1, 
    0, 0, 0, 8, 10, 5, 2, 0, 0, 9, 8, 1, 0, 0, 0, 9, 10, 1, 0, 0, 0, 10, 
    11, 1, 0, 0, 0, 11, 12, 6, 0, -1, 0, 12, 1, 1, 0, 0, 0, 2, 5, 9
]);