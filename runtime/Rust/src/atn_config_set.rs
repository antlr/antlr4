use std::cmp::max;
use std::fmt::{Debug, Error, Formatter};
use std::hash::{Hash, Hasher};

use bit_set::BitSet;
use fxhash::{hash64, FxHasher64};
use hashbrown::HashTable;

use crate::atn::ATN;
use crate::atn_config::{ATNConfig, ATNConfigType, LexerATNConfig};
use crate::dfa::{DFAStateStore, PredPrediction};
use crate::lexer_atn_simulator::LEXER_DFA_EDGE_SET_SIZE;
use crate::parser_atn_simulator::MergeCache;
use crate::prediction_context::PredictionContext;
use crate::semantic_context::SemanticContext;
use crate::PredictionContextCache;

pub trait ConfigSet<'ephemeral>: PartialEq + Eq + Hash {
    type ConfigType: ATNConfigType<'ephemeral>;
    type FinalizedType<'x>: ConfigSet<'x>;
    type LexerActionExecutorType: Default;
    type PredicatesType: FromProposed<&'ephemeral [PredPrediction<'ephemeral>]>;

    fn new_empty() -> Self;

    fn hash_code(&self) -> u64;

    fn finalize<'sim>(
        self,
        cache: &'sim PredictionContextCache<'sim>,
        dfa: &DFAStateStore<'sim, Self::FinalizedType<'sim>>,
    ) -> Self::FinalizedType<'sim>;

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

    configs: ConfigSetStore<'ephemeral, ATNConfig<'ephemeral>>,

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

impl<'ephemeral> Hash for ATNConfigSet<'ephemeral> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.configs.iter().for_each(|c| c.hash(state));
    }
}

impl<'ephemeral> IntoIterator for ATNConfigSet<'ephemeral> {
    type Item = ATNConfig<'ephemeral>;
    type IntoIter = <ConfigSetStore<'ephemeral, ATNConfig<'ephemeral>> as IntoIterator>::IntoIter;

    fn into_iter(self) -> Self::IntoIter {
        self.configs.into_iter()
    }
}

impl<'ephemeral> ConfigSet<'ephemeral> for ATNConfigSet<'ephemeral> {
    type ConfigType = ATNConfig<'ephemeral>;
    type FinalizedType<'x> = ATNConfigSet<'x>;
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
            configs: ConfigSetStore::<ATNConfig<'static>>::new_empty(),
            conflicting_alts: Default::default(),
        }
    }

    fn hash_code(&self) -> u64 {
        self.configs.hash_code()
    }

    fn finalize<'sim>(
        self,
        cache: &'sim PredictionContextCache<'sim>,
        dfa: &DFAStateStore<'sim, Self::FinalizedType<'sim>>,
    ) -> Self::FinalizedType<'sim> {
        ATNConfigSet {
            configs: self.configs.finalize(cache, dfa),
            ..self
        }
    }

    fn calc_edge_set_size(atn: &'static ATN) -> usize {
        atn.max_token_type as usize + 2
    }
}

impl<'ephemeral> ATNConfigSet<'ephemeral> {
    pub fn new(arena: &'ephemeral bumpalo::Bump, full_ctx: bool) -> ATNConfigSet<'ephemeral> {
        ATNConfigSet {
            base: ATNConfigSetBase {
                dips_into_outer_context: false,
                full_ctx,
                has_semantic_context: false,
                unique_alt: 0,
            },
            configs: ConfigSetStore::new_ephemeral(arena),
            conflicting_alts: Default::default(),
        }
    }

    // for parser
    pub(crate) fn add_cached(
        &mut self,
        config: ATNConfig<'ephemeral>,
        merge_cache: &mut MergeCache<'ephemeral>,
    ) -> bool {
        let store = match &mut self.configs {
            ConfigSetStore::Scratch(s) => s,
            ConfigSetStore::Final(_) => panic!("Cannot add to read-only ATNConfigSet"),
        };

        if config.semantic_context() != &SemanticContext::NONE {
            self.base.has_semantic_context = true
        }

        if config.get_reaches_into_outer_context() > 0 {
            self.base.dips_into_outer_context = true
        }

        let key = Key::partial(&config, store.configs.len());

        if let Some(key) = store
            .lookup
            .find(key.hash_code(), |k| k.partial_eq(&config, &store.configs))
        {
            let existing = &mut store.configs[key.index()];
            let root_is_wildcard = !self.base.full_ctx;

            let merged = PredictionContext::merge(
                existing.get_context().unwrap(),
                config.get_context().unwrap(),
                root_is_wildcard,
                merge_cache,
            );

            let v1 = existing.get_reaches_into_outer_context();
            let v2 = config.get_reaches_into_outer_context();
            existing.set_reaches_into_outer_context(max(v1, v2));

            if config.is_precedence_filter_suppressed() {
                existing.set_precedence_filter_suppressed(true)
            }

            existing.set_context(merged);
        } else {
            store.configs.push(config);
            store
                .lookup
                .insert_unique(key.hash_code(), key, Key::hash_code);
        }
        true
    }

    pub(crate) fn add(&mut self, config: ATNConfig<'ephemeral>) -> bool {
        let store = match &mut self.configs {
            ConfigSetStore::Scratch(s) => s,
            ConfigSetStore::Final(_) => panic!("Cannot add to read-only ATNConfigSet"),
        };

        if config.semantic_context() != &SemanticContext::NONE {
            self.base.has_semantic_context = true
        }

        if config.get_reaches_into_outer_context() > 0 {
            self.base.dips_into_outer_context = true
        }

        let key = Key::partial(&config, store.configs.len());

        if store
            .lookup
            .find(key.hash_code(), |k| k.partial_eq(&config, &store.configs))
            .is_none()
        {
            store.configs.push(config);
            store
                .lookup
                .insert_unique(key.hash_code(), key, Key::hash_code);
        }
        true
    }

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

    pub fn read_only(&self) -> bool {
        self.configs.read_only()
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

    configs: ConfigSetStore<'ephemeral, LexerATNConfig<'ephemeral>>,
}

impl Debug for LexerATNConfigSet<'_> {
    fn fmt(&self, _f: &mut Formatter<'_>) -> Result<(), Error> {
        _f.write_str("LexerATNConfigSet")?;
        _f.debug_list().entries(self.configs.iter()).finish()
    }
}

impl Hash for LexerATNConfigSet<'_> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.configs.iter().for_each(|c| c.hash(state));
    }
}

impl<'ephemeral> IntoIterator for LexerATNConfigSet<'ephemeral> {
    type Item = LexerATNConfig<'ephemeral>;
    type IntoIter =
        <ConfigSetStore<'ephemeral, LexerATNConfig<'ephemeral>> as IntoIterator>::IntoIter;

    fn into_iter(self) -> Self::IntoIter {
        self.configs.into_iter()
    }
}

impl<'ephemeral> ConfigSet<'ephemeral> for LexerATNConfigSet<'ephemeral> {
    type ConfigType = LexerATNConfig<'ephemeral>;
    type FinalizedType<'x> = LexerATNConfigSet<'x>;
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
            configs: ConfigSetStore::<LexerATNConfig>::new_empty(),
        }
    }

    fn hash_code(&self) -> u64 {
        self.configs.hash_code()
    }

    fn finalize<'sim>(
        self,
        cache: &'sim PredictionContextCache<'sim>,
        dfa: &DFAStateStore<'sim, Self::FinalizedType<'sim>>,
    ) -> Self::FinalizedType<'sim> {
        LexerATNConfigSet {
            configs: self.configs.finalize(cache, dfa),
            ..self
        }
    }

    fn calc_edge_set_size(_atn: &'static ATN) -> usize {
        LEXER_DFA_EDGE_SET_SIZE
    }
}

impl<'ephemeral> LexerATNConfigSet<'ephemeral> {
    pub fn new(arena: &'ephemeral bumpalo::Bump) -> Self {
        LexerATNConfigSet {
            base: ATNConfigSetBase {
                dips_into_outer_context: false,
                full_ctx: true,
                has_semantic_context: false,
                unique_alt: 0,
            },
            configs: ConfigSetStore::new_ephemeral(arena),
        }
    }

    pub(crate) fn add(&mut self, config: LexerATNConfig<'ephemeral>) -> bool {
        let store = match &mut self.configs {
            ConfigSetStore::Scratch(s) => s,
            ConfigSetStore::Final(_) => panic!("Cannot add to read-only ATNConfigSet"),
        };

        if config.semantic_context() != &SemanticContext::NONE {
            self.base.has_semantic_context = true
        }

        if config.get_reaches_into_outer_context() > 0 {
            self.base.dips_into_outer_context = true
        }

        let key = Key::full(&config, store.configs.len());

        if store
            .lookup
            .find(key.hash_code(), |k| k.full_eq(&config, &store.configs))
            .is_none()
        {
            store.configs.push(config);
            store
                .lookup
                .insert_unique(key.hash_code(), key, Key::hash_code);
        }
        true
    }

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

enum Key {
    Full(u64, usize),
    Partial(u64, usize),
}

impl Key {
    fn full(config: &LexerATNConfig, index: usize) -> Self {
        Key::Full(Self::full_hash(config), index)
    }

    fn full_hash(config: &LexerATNConfig) -> u64 {
        hash64(config)
    }

    fn partial(config: &ATNConfig, index: usize) -> Self {
        Key::Partial(Self::partial_hash(config), index)
    }

    fn partial_hash(config: &ATNConfig) -> u64 {
        let mut hasher = FxHasher64::default();
        config.get_state().hash(&mut hasher);
        config.get_alt().hash(&mut hasher);
        config.semantic_context().hash(&mut hasher);
        hasher.finish()
    }

    fn hash_code(&self) -> u64 {
        match self {
            Key::Full(hash, _) => *hash,
            Key::Partial(hash, _) => *hash,
        }
    }

    fn index(&self) -> usize {
        match self {
            Key::Full(_, index) => *index,
            Key::Partial(_, index) => *index,
        }
    }

    fn partial_eq(&self, other: &ATNConfig, configs: &[ATNConfig]) -> bool {
        match self {
            Key::Full(..) => panic!("Full keys should not be compared with parser configs"),
            Key::Partial(_, index) => {
                let left = &configs[*index];
                left.get_state() == other.get_state()
                    && left.get_alt() == other.get_alt()
                    && left.semantic_context() == other.semantic_context()
            }
        }
    }

    fn full_eq(&self, other: &LexerATNConfig, configs: &[LexerATNConfig]) -> bool {
        match self {
            Key::Partial(..) => panic!("Partial keys should not be compared with lexer configs"),
            Key::Full(_, index) => {
                let left = &configs[*index];
                left == other
            }
        }
    }
}

pub enum ConfigSetStore<'ephemeral, AC>
where
    AC: ATNConfigType<'ephemeral> + 'ephemeral,
{
    Scratch(ScratchStore<'ephemeral, AC>),
    Final(ImmutableStore<'ephemeral, AC>),
}

pub struct ScratchStore<'ephemeral, AC>
where
    AC: ATNConfigType<'ephemeral>,
{
    lookup: HashTable<Key, &'ephemeral bumpalo::Bump>,
    configs: bumpalo::collections::Vec<'ephemeral, AC>,
}

impl<'ephemeral, AC> ScratchStore<'ephemeral, AC>
where
    AC: ATNConfigType<'ephemeral>,
{
    fn hash_code(&self) -> u64 {
        let mut hasher = FxHasher64::default();
        self.configs.iter().for_each(|c| c.hash(&mut hasher));
        hasher.finish()
    }
}

pub struct ImmutableStore<'ephemeral, AC>
where
    AC: ATNConfigType<'ephemeral>,
{
    cached_hash: u64,
    configs: &'ephemeral [AC],
}

impl<'ephemeral, AC> PartialEq for ConfigSetStore<'ephemeral, AC>
where
    AC: ATNConfigType<'ephemeral>,
{
    fn eq(&self, other: &Self) -> bool {
        if self.len() != other.len() {
            return false;
        }

        for (a, b) in self.iter().zip(other.iter()) {
            if a != b {
                return false;
            }
        }

        true
    }
}

impl<'ephemeral, AC> Eq for ConfigSetStore<'ephemeral, AC> where AC: ATNConfigType<'ephemeral> {}

impl<'ephemeral, AC> IntoIterator for ConfigSetStore<'ephemeral, AC>
where
    AC: ATNConfigType<'ephemeral> + 'ephemeral,
{
    type Item = AC;
    type IntoIter = bumpalo::collections::vec::IntoIter<'ephemeral, AC>;

    fn into_iter(self) -> Self::IntoIter {
        match self {
            ConfigSetStore::Scratch(s) => s.configs.into_iter(),
            ConfigSetStore::Final(..) => panic!("Cannot consume a read-only ConfigSetStore"),
        }
    }
}

impl<'ephemeral, AC> ConfigSetStore<'ephemeral, AC>
where
    AC: ATNConfigType<'ephemeral>,
{
    fn len(&self) -> usize {
        match self {
            ConfigSetStore::Scratch(s) => s.configs.len(),
            ConfigSetStore::Final(s) => s.configs.len(),
        }
    }

    fn is_empty(&self) -> bool {
        match self {
            ConfigSetStore::Scratch(s) => s.configs.is_empty(),
            ConfigSetStore::Final(s) => s.configs.is_empty(),
        }
    }

    fn read_only(&self) -> bool {
        match self {
            ConfigSetStore::Scratch(_) => false,
            ConfigSetStore::Final(_) => true,
        }
    }
}

impl ConfigSetStore<'static, ATNConfig<'static>> {
    fn new_empty() -> Self {
        static EMPTY_ATNCONFIGS: [ATNConfig; 0] = [];

        ConfigSetStore::Final(ImmutableStore {
            cached_hash: hash64(&EMPTY_ATNCONFIGS),
            configs: &EMPTY_ATNCONFIGS,
        })
    }
}

impl ConfigSetStore<'static, LexerATNConfig<'static>> {
    fn new_empty() -> Self {
        static EMPTY_LEXERATNCONFIGS: [LexerATNConfig; 0] = [];

        ConfigSetStore::Final(ImmutableStore {
            cached_hash: hash64(&EMPTY_LEXERATNCONFIGS),
            configs: &EMPTY_LEXERATNCONFIGS,
        })
    }
}

impl<'ephemeral, AC> ConfigSetStore<'ephemeral, AC>
where
    AC: ATNConfigType<'ephemeral> + 'ephemeral,
{
    fn new_ephemeral(ephemerals: &'ephemeral bumpalo::Bump) -> Self {
        ConfigSetStore::Scratch(ScratchStore {
            lookup: HashTable::with_capacity_in(7, ephemerals),
            configs: bumpalo::collections::Vec::new_in(ephemerals),
        })
    }

    fn iter(&self) -> impl Iterator<Item = &AC> {
        match self {
            ConfigSetStore::Scratch(s) => s.configs.iter(),
            ConfigSetStore::Final(s) => s.configs.iter(),
        }
    }

    fn hash_code(&self) -> u64 {
        match self {
            ConfigSetStore::Scratch(s) => s.hash_code(),
            ConfigSetStore::Final(s) => s.cached_hash,
        }
    }
}

impl<'ephemeral> ConfigSetStore<'ephemeral, ATNConfig<'ephemeral>> {
    fn finalize<'sim>(
        self,
        cache: &'sim PredictionContextCache,
        dfa: &DFAStateStore<'sim, ATNConfigSet<'sim>>,
    ) -> ConfigSetStore<'sim, ATNConfig<'sim>> {
        match self {
            ConfigSetStore::Scratch(s) => {
                let cached_hash = s.hash_code();

                ConfigSetStore::Final(ImmutableStore {
                    cached_hash,
                    configs: dfa
                        .alloc_config_slice(s.configs.into_iter().map(|c| c.finalize(cache, dfa))),
                })
            }
            ConfigSetStore::Final(_) => unsafe {
                std::mem::transmute::<Self, ConfigSetStore<'sim, ATNConfig<'sim>>>(self)
            },
        }
    }
}

impl<'ephemeral> ConfigSetStore<'ephemeral, LexerATNConfig<'ephemeral>> {
    fn finalize<'sim>(
        self,
        cache: &'sim PredictionContextCache,
        dfa: &DFAStateStore<'sim, LexerATNConfigSet<'sim>>,
    ) -> ConfigSetStore<'sim, LexerATNConfig<'sim>> {
        match self {
            ConfigSetStore::Scratch(s) => {
                let cached_hash = s.hash_code();

                ConfigSetStore::Final(ImmutableStore {
                    cached_hash,
                    configs: dfa
                        .alloc_config_slice(s.configs.into_iter().map(|c| c.finalize(cache, dfa))),
                })
            }
            ConfigSetStore::Final(_) => unsafe {
                std::mem::transmute::<Self, ConfigSetStore<'sim, LexerATNConfig<'sim>>>(self)
            },
        }
    }
}

unsafe impl<'ephemeral, AC> Send for ConfigSetStore<'ephemeral, AC> where AC: ATNConfigType<'ephemeral> + 'ephemeral {}
unsafe impl<'ephemeral, AC> Sync for ConfigSetStore<'ephemeral, AC> where AC: ATNConfigType<'ephemeral> + 'ephemeral {}