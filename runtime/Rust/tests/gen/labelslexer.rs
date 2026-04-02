// Generated from Labels.g4 by ANTLR 4.13.2
#![allow(dead_code)]
#![allow(unused_imports)]
#![allow(nonstandard_style)]
#![allow(unused_variables)]
#![allow(unused_braces)]
#![allow(unused_parens)]
use dbt_antlr4::Arena;
use dbt_antlr4::atn::ATN;
use dbt_antlr4::char_stream::CharStream;
use dbt_antlr4::int_stream::IntStream;
use dbt_antlr4::lexer::{BaseLexer, LexerRecog, Lexer as _};
use dbt_antlr4::atn_deserializer::ATNDeserializer;
use dbt_antlr4::TokenSource;
use dbt_antlr4::lexer_atn_simulator::{LexerATNSimulator, ILexerATNSimulator};
use dbt_antlr4::PredictionContextCache;
use dbt_antlr4::recognizer::Actions;
use dbt_antlr4::token_factory::{CommonTokenFactory, TokenFactory};
use dbt_antlr4::rule_context::{BaseRuleContext,EmptyCustomRuleContext,EmptyRuleNode};
use dbt_antlr4::vocabulary::{Vocabulary,VocabularyImpl};

use std::ops::{DerefMut, Deref};
use std::sync::LazyLock;

dbt_antlr4::check_version!("1","2");
pub const T__0:i32=1; 
pub const T__1:i32=2; 
pub const T__2:i32=3; 
pub const T__3:i32=4; 
pub const T__4:i32=5; 
pub const T__5:i32=6; 
pub const ID:i32=7; 
pub const INT:i32=8; 
pub const WS:i32=9;

pub const channelNames: [&'static str;0+2] = [
    "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
];

pub const modeNames: [&'static str;1] = [
    "DEFAULT_MODE"
];

pub const ruleNames: [&'static str;9] = [
    "T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "ID", "INT", "WS"
];
pub const _LITERAL_NAMES: [Option<&'static str>;7] = [
	None, Some("'*'"), Some("'+'"), Some("'('"), Some("')'"), Some("'++'"), 
	Some("'--'")
];
pub const _SYMBOLIC_NAMES: [Option<&'static str>;10]  = [
	None, None, None, None, None, None, None, Some("ID"), Some("INT"), Some("WS")
];

static VOCABULARY: LazyLock<Box<dyn Vocabulary>> = LazyLock::new(|| Box::new(VocabularyImpl::new(_LITERAL_NAMES.iter(), _SYMBOLIC_NAMES.iter(), None)));

pub type LexerContext<'input, 'arena> = BaseRuleContext<'input, 'arena, EmptyCustomRuleContext<'input, 'arena>>;
pub type BaseLexerType<'input, 'arena, Input, TF> = BaseLexer<'input, 'arena, LabelsLexerActions, Input, TF>;

pub struct LabelsLexer<'input, 'arena, Input, TF = CommonTokenFactory<'input, 'arena>>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: CharStream<'input>,
{
	base: BaseLexerType<'input, 'arena, Input, TF>,
}

dbt_antlr4::impl_token_source! { LabelsLexer }
dbt_antlr4::impl_deref! { lexer => LabelsLexer }

impl<'input, 'arena, Input, TF> LabelsLexer<'input, 'arena, Input, TF>
where
    'input: 'arena,
    TF: TokenFactory<'input, 'arena> + 'arena,
    Input: CharStream<'input>,
{
    pub fn new(arena: &'arena Arena, input: Input) -> Self {
        let actions = LabelsLexerActions {
        };
        let base = BaseLexerType::new_base_lexer(input, actions, arena);
        Self { base }
    }
}

pub struct LabelsLexerActions {
}

impl LabelsLexerActions {
}

impl<'input, 'arena, Input, TF> Actions<'input, 'arena, BaseLexerType<'input, 'arena, Input, TF>>
    for LabelsLexerActions
where
    'input: 'arena,
    Input: CharStream<'input>,
    TF: TokenFactory<'input, 'arena> + 'arena,
 {}

impl<'input, 'arena, Input, TF> LexerRecog<'input, 'arena, TF, BaseLexerType<'input, 'arena, Input, TF>>
    for LabelsLexerActions
where
    'input: 'arena,
    Input: CharStream<'input>,
    TF: TokenFactory<'input, 'arena> + 'arena,
{
    fn get_rule_names(&self) -> &'static [&'static str] { &ruleNames }
    fn get_literal_names(&self) -> &[Option<&str>] { &_LITERAL_NAMES }
    fn get_symbolic_names(&self) -> &[Option<&str>] { &_SYMBOLIC_NAMES }
    fn get_grammar_file_name(&self) -> &'static str { "LabelsLexer.g4" }
    fn get_atn_simulator(&self, arena: &'arena Arena) -> LexerATNSimulator<'arena> {
        LexerATNSimulator::new_lexer_atnsimulator(&_ATN, arena)
    }
}

static _ATN: LazyLock<ATN> =
    LazyLock::new(|| ATNDeserializer::new(None).deserialize(&mut _serializedATN.iter()));
static _serializedATN: LazyLock<Vec<i32>> = LazyLock::new(|| vec![
    4, 0, 9, 47, 6, -1, 2, 0, 7, 0, 2, 1, 7, 1, 2, 2, 7, 2, 2, 3, 7, 3, 
    2, 4, 7, 4, 2, 5, 7, 5, 2, 6, 7, 6, 2, 7, 7, 7, 2, 8, 7, 8, 1, 0, 1, 
    0, 1, 1, 1, 1, 1, 2, 1, 2, 1, 3, 1, 3, 1, 4, 1, 4, 1, 4, 1, 5, 1, 5, 
    1, 5, 1, 6, 4, 6, 35, 8, 6, 11, 6, 12, 6, 36, 1, 7, 4, 7, 40, 8, 7, 
    11, 7, 12, 7, 41, 1, 8, 1, 8, 1, 8, 1, 8, 0, 0, 9, 1, 1, 3, 2, 5, 3, 
    7, 4, 9, 5, 11, 6, 13, 7, 15, 8, 17, 9, 1, 0, 1, 2, 0, 10, 10, 32, 32, 
    48, 0, 1, 1, 0, 0, 0, 0, 3, 1, 0, 0, 0, 0, 5, 1, 0, 0, 0, 0, 7, 1, 0, 
    0, 0, 0, 9, 1, 0, 0, 0, 0, 11, 1, 0, 0, 0, 0, 13, 1, 0, 0, 0, 0, 15, 
    1, 0, 0, 0, 0, 17, 1, 0, 0, 0, 1, 19, 1, 0, 0, 0, 3, 21, 1, 0, 0, 0, 
    5, 23, 1, 0, 0, 0, 7, 25, 1, 0, 0, 0, 9, 27, 1, 0, 0, 0, 11, 30, 1, 
    0, 0, 0, 13, 34, 1, 0, 0, 0, 15, 39, 1, 0, 0, 0, 17, 43, 1, 0, 0, 0, 
    19, 20, 5, 42, 0, 0, 20, 2, 1, 0, 0, 0, 21, 22, 5, 43, 0, 0, 22, 4, 
    1, 0, 0, 0, 23, 24, 5, 40, 0, 0, 24, 6, 1, 0, 0, 0, 25, 26, 5, 41, 0, 
    0, 26, 8, 1, 0, 0, 0, 27, 28, 5, 43, 0, 0, 28, 29, 5, 43, 0, 0, 29, 
    10, 1, 0, 0, 0, 30, 31, 5, 45, 0, 0, 31, 32, 5, 45, 0, 0, 32, 12, 1, 
    0, 0, 0, 33, 35, 2, 97, 122, 0, 34, 33, 1, 0, 0, 0, 35, 36, 1, 0, 0, 
    0, 36, 34, 1, 0, 0, 0, 36, 37, 1, 0, 0, 0, 37, 14, 1, 0, 0, 0, 38, 40, 
    2, 48, 57, 0, 39, 38, 1, 0, 0, 0, 40, 41, 1, 0, 0, 0, 41, 39, 1, 0, 
    0, 0, 41, 42, 1, 0, 0, 0, 42, 16, 1, 0, 0, 0, 43, 44, 7, 0, 0, 0, 44, 
    45, 1, 0, 0, 0, 45, 46, 6, 8, 0, 0, 46, 18, 1, 0, 0, 0, 3, 0, 36, 41, 
    1, 6, 0, 0
]);