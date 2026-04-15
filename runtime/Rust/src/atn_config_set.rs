use std::fmt::{Debug, Error, Formatter};
use std::hash::Hash;

use bit_set::BitSet;
use fxhash::hash64;

use crate::atn::ATN;
use crate::atn_config::{ATNConfig, ATNConfigType, LexerATNConfig};
use crate::dfa::PredPrediction;
use crate::lexer_atn_simulator::LEXER_DFA_EDGE_SET_SIZE;

mod mutable;
pub use mutable::{MutableATNConfigSet, MutableConfigSet, MutableLexerATNConfigSet};

pub trait ConfigSet<'ephemeral>: PartialEq + Eq {
    type ConfigType: ATNConfigType<'ephemeral>;
    type TransmutedType<'x>: ConfigSet<'x>;
    type LexerActionExecutorType: Default;
    type PredicatesType: FromProposed<&'ephemeral [PredPrediction<'ephemeral>]>;

    fn new_empty() -> Self;

    fn hash_code(&self) -> u64;

    fn calc_edge_set_size(_atn: &'static ATN) -> usize;

    fn set_lexer_action_executor(
        target: &mut Self::LexerActionExecutorType,
        value: Self::LexerActionExecutorType,
    ) {
        *target = value;
    }
}

pub trait FromProposed<T> {
    fn from_proposed(proposed: T) -> Self;
}

impl<'ephemeral> FromProposed<&'ephemeral [PredPrediction<'ephemeral>]>
    for &'ephemeral [PredPrediction<'ephemeral>]
{
    fn from_proposed(proposed: &'ephemeral [PredPrediction<'ephemeral>]) -> Self {
        proposed
    }
}
impl<'ephemeral> FromProposed<&'ephemeral [PredPrediction<'ephemeral>]> for () {
    fn from_proposed(_proposed: &'ephemeral [PredPrediction<'ephemeral>]) -> Self {}
}

#[derive(PartialEq, Eq, Hash, Debug, Clone)]
struct ATNConfigSetBase {
    dips_into_outer_context: bool,

    full_ctx: bool,

    has_semantic_context: bool,

    unique_alt: i32,
}

pub struct ATNConfigSet<'ephemeral> {
    base: ATNConfigSetBase,

    configs: ImmutableStore<'ephemeral, ATNConfig<'ephemeral>>,

    conflicting_alts: BitSet,
}

impl Debug for ATNConfigSet<'_> {
    fn fmt(&self, _f: &mut Formatter<'_>) -> Result<(), Error> {
        _f.write_str("ATNConfigSet")?;
        _f.debug_list().entries(self.configs.iter()).finish()?;
        if self.base.has_semantic_context {
            _f.write_str(",hasSemanticContext=true")?
        }
        if self.conflicting_alts.is_empty() {
            _f.write_fmt(format_args!(",uniqueAlt={}", self.base.unique_alt))
        } else {
            _f.write_fmt(format_args!(",conflictingAlts={:?}", self.conflicting_alts))
        }
    }
}

impl PartialEq for ATNConfigSet<'_> {
    fn eq(&self, other: &Self) -> bool {
        self.base == other.base
            && self.configs == other.configs
            && self.conflicting_alts == other.conflicting_alts
    }
}

impl Eq for ATNConfigSet<'_> {}

impl<'ephemeral> ConfigSet<'ephemeral> for ATNConfigSet<'ephemeral> {
    type ConfigType = ATNConfig<'ephemeral>;
    type TransmutedType<'x> = ATNConfigSet<'x>;
    type LexerActionExecutorType = ();
    type PredicatesType = &'ephemeral [PredPrediction<'ephemeral>];

    fn new_empty() -> Self {
        ATNConfigSet {
            base: ATNConfigSetBase {
                dips_into_outer_context: false,
                full_ctx: true,
                has_semantic_context: false,
                unique_alt: 0,
            },
            configs: ImmutableStore::<ATNConfig<'static>>::new_empty(),
            conflicting_alts: Default::default(),
        }
    }

    fn hash_code(&self) -> u64 {
        self.configs.hash_code()
    }

    fn calc_edge_set_size(atn: &'static ATN) -> usize {
        atn.max_token_type as usize + 2
    }
}

impl<'ephemeral> ATNConfigSet<'ephemeral> {
    pub fn get_items(&self) -> impl Iterator<Item = &ATNConfig<'ephemeral>> {
        self.configs.iter()
    }

    pub fn length(&self) -> usize {
        self.configs.len()
    }

    pub fn is_empty(&self) -> bool {
        self.configs.is_empty()
    }

    pub fn has_semantic_context(&self) -> bool {
        self.base.has_semantic_context
    }

    pub fn set_has_semantic_context(&mut self, _v: bool) {
        self.base.has_semantic_context = _v;
    }

    pub fn full_context(&self) -> bool {
        self.base.full_ctx
    }

    pub fn conflicting_alts(&self) -> &BitSet {
        &self.conflicting_alts
    }

    pub fn set_conflicting_alts(&mut self, alts: BitSet) {
        self.conflicting_alts = alts;
    }

    //duplicate of the self.conflicting_alts???
    pub fn get_alts(&self) -> BitSet {
        self.configs.iter().fold(BitSet::new(), |mut acc, c| {
            acc.insert(c.get_alt() as usize);
            acc
        })
    }

    pub fn get_unique_alt(&self) -> i32 {
        self.base.unique_alt
    }

    pub fn set_unique_alt(&mut self, _v: i32) {
        self.base.unique_alt = _v
    }

    pub fn get_dips_into_outer_context(&self) -> bool {
        self.base.dips_into_outer_context
    }

    pub fn set_dips_into_outer_context(&mut self, _v: bool) {
        self.base.dips_into_outer_context = _v
    }
}

#[derive(PartialEq, Eq)]
pub struct LexerATNConfigSet<'ephemeral> {
    base: ATNConfigSetBase,

    configs: ImmutableStore<'ephemeral, LexerATNConfig<'ephemeral>>,
}

impl Debug for LexerATNConfigSet<'_> {
    fn fmt(&self, _f: &mut Formatter<'_>) -> Result<(), Error> {
        _f.write_str("LexerATNConfigSet")?;
        _f.debug_list().entries(self.configs.iter()).finish()
    }
}

impl<'ephemeral> ConfigSet<'ephemeral> for LexerATNConfigSet<'ephemeral> {
    type ConfigType = LexerATNConfig<'ephemeral>;
    type TransmutedType<'x> = LexerATNConfigSet<'x>;
    type LexerActionExecutorType =
        Option<&'ephemeral crate::lexer_action_executor::LexerActionExecutor<'ephemeral>>;
    type PredicatesType = ();

    fn new_empty() -> Self {
        LexerATNConfigSet {
            base: ATNConfigSetBase {
                dips_into_outer_context: false,
                full_ctx: true,
                has_semantic_context: false,
                unique_alt: 0,
            },
            configs: ImmutableStore::<LexerATNConfig>::new_empty(),
        }
    }

    fn hash_code(&self) -> u64 {
        self.configs.hash_code()
    }

    fn calc_edge_set_size(_atn: &'static ATN) -> usize {
        LEXER_DFA_EDGE_SET_SIZE
    }
}

impl<'ephemeral> LexerATNConfigSet<'ephemeral> {
    pub fn get_items(&self) -> impl Iterator<Item = &LexerATNConfig<'ephemeral>> {
        self.configs.iter()
    }

    pub fn length(&self) -> usize {
        self.configs.len()
    }

    pub fn is_empty(&self) -> bool {
        self.configs.is_empty()
    }

    pub fn has_semantic_context(&self) -> bool {
        self.base.has_semantic_context
    }

    pub fn set_has_semantic_context(&mut self, _v: bool) {
        self.base.has_semantic_context = _v;
    }
}

pub struct ImmutableStore<'ephemeral, AC>
where
    AC: ATNConfigType<'ephemeral>,
{
    cached_hash: u64,
    configs: &'ephemeral [AC],
}

impl<'ephemeral, AC> PartialEq for ImmutableStore<'ephemeral, AC>
where
    AC: ATNConfigType<'ephemeral>,
{
    fn eq(&self, other: &Self) -> bool {
        if self.len() != other.len() {
            return false;
        }

        self.iter().zip(other.iter()).all(|(a, b)| a == b)
    }
}

impl<'ephemeral, AC> Eq for ImmutableStore<'ephemeral, AC> where AC: ATNConfigType<'ephemeral> {}

impl<'ephemeral, AC> ImmutableStore<'ephemeral, AC>
where
    AC: ATNConfigType<'ephemeral>,
{
    fn len(&self) -> usize {
        self.configs.len()
    }

    fn is_empty(&self) -> bool {
        self.configs.is_empty()
    }

    fn iter(&self) -> impl Iterator<Item = &AC> {
        self.configs.iter()
    }

    fn hash_code(&self) -> u64 {
        self.cached_hash
    }
}

impl ImmutableStore<'static, ATNConfig<'static>> {
    fn new_empty() -> Self {
        static EMPTY_ATNCONFIGS: [ATNConfig; 0] = [];

        ImmutableStore {
            cached_hash: hash64(&EMPTY_ATNCONFIGS),
            configs: &EMPTY_ATNCONFIGS,
        }
    }
}

impl ImmutableStore<'static, LexerATNConfig<'static>> {
    fn new_empty() -> Self {
        static EMPTY_LEXERATNCONFIGS: [LexerATNConfig; 0] = [];

        ImmutableStore {
            cached_hash: hash64(&EMPTY_LEXERATNCONFIGS),
            configs: &EMPTY_LEXERATNCONFIGS,
        }
    }
}
