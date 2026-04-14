use std::cmp::max;
use std::fmt::{Debug, Error, Formatter};
use std::hash::{Hash, Hasher};

use bit_set::BitSet;
use fxhash::{hash64, FxHasher64};
use hashbrown::HashTable;

use crate::atn_config::{ATNConfig, ATNConfigType, LexerATNConfig};
use crate::atn_config_set::{ATNConfigSet, ImmutableStore, LexerATNConfigSet};
use crate::dfa::DFAStateStore;
use crate::parser_atn_simulator::MergeCache;
use crate::prediction_context::PredictionContext;
use crate::semantic_context::SemanticContext;
use crate::PredictionContextCache;

pub trait MutableConfigSet<'ephemeral>: PartialEq + Eq + Hash {
    type ConfigType: ATNConfigType<'ephemeral>;
    type FinalizedType<'x>: super::ConfigSet<'x>;

    fn hash_code(&self) -> u64;

    fn eq_config_set(&self, other: &Self::FinalizedType<'_>) -> bool;

    fn finalize<'sim>(
        self,
        cache: &'sim PredictionContextCache<'sim>,
        dfa: &DFAStateStore<'sim, Self::FinalizedType<'sim>>,
    ) -> Self::FinalizedType<'sim>;
}

pub struct MutableATNConfigSet<'ephemeral> {
    base: super::ATNConfigSetBase,

    configs: ScratchStore<'ephemeral, ATNConfig<'ephemeral>>,

    conflicting_alts: BitSet,
}

impl Debug for MutableATNConfigSet<'_> {
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

impl PartialEq for MutableATNConfigSet<'_> {
    fn eq(&self, other: &Self) -> bool {
        self.base == other.base
            && self.configs == other.configs
            && self.conflicting_alts == other.conflicting_alts
    }
}

impl Eq for MutableATNConfigSet<'_> {}

impl<'ephemeral> Hash for MutableATNConfigSet<'ephemeral> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.configs.iter().for_each(|c| c.hash(state));
    }
}

impl<'ephemeral> IntoIterator for MutableATNConfigSet<'ephemeral> {
    type Item = ATNConfig<'ephemeral>;
    type IntoIter = <ScratchStore<'ephemeral, ATNConfig<'ephemeral>> as IntoIterator>::IntoIter;

    fn into_iter(self) -> Self::IntoIter {
        self.configs.into_iter()
    }
}

impl<'ephemeral> MutableConfigSet<'ephemeral> for MutableATNConfigSet<'ephemeral> {
    type ConfigType = ATNConfig<'ephemeral>;
    type FinalizedType<'x> = ATNConfigSet<'x>;

    fn hash_code(&self) -> u64 {
        self.configs.hash_code()
    }

    fn eq_config_set(&self, other: &Self::FinalizedType<'_>) -> bool {
        self.base == other.base
            && self.configs == other.configs
            && self.conflicting_alts == other.conflicting_alts
    }

    fn finalize<'sim>(
        self,
        cache: &'sim PredictionContextCache<'sim>,
        dfa: &DFAStateStore<'sim, Self::FinalizedType<'sim>>,
    ) -> Self::FinalizedType<'sim> {
        ATNConfigSet {
            configs: self.configs.finalize(cache, dfa),
            base: self.base,
            conflicting_alts: self.conflicting_alts,
        }
    }
}

impl<'ephemeral> MutableATNConfigSet<'ephemeral> {
    pub fn new(arena: &'ephemeral bumpalo::Bump, full_ctx: bool) -> Self {
        MutableATNConfigSet {
            base: super::ATNConfigSetBase {
                dips_into_outer_context: false,
                full_ctx,
                has_semantic_context: false,
                unique_alt: 0,
            },
            configs: ScratchStore::new(arena),
            conflicting_alts: Default::default(),
        }
    }

    pub fn from_config_set<'sim>(
        arena: &'ephemeral bumpalo::Bump,
        set: &'sim ATNConfigSet<'sim>,
    ) -> Self
    where
        'sim: 'ephemeral,
    {
        let mut configs = ScratchStore::new(arena);
        set.get_items()
            .for_each(|c| configs.configs.push(c.clone()));
        MutableATNConfigSet {
            base: set.base.clone(),
            configs,
            conflicting_alts: set.conflicting_alts.clone(),
        }
    }

    // for parser
    pub(crate) fn add_cached(
        &mut self,
        config: ATNConfig<'ephemeral>,
        merge_cache: &mut MergeCache<'ephemeral>,
    ) -> bool {
        let store = &mut self.configs;

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
        let store = &mut self.configs;

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
pub struct MutableLexerATNConfigSet<'ephemeral> {
    base: super::ATNConfigSetBase,

    configs: ScratchStore<'ephemeral, LexerATNConfig<'ephemeral>>,
}

impl Debug for MutableLexerATNConfigSet<'_> {
    fn fmt(&self, _f: &mut Formatter<'_>) -> Result<(), Error> {
        _f.write_str("LexerATNConfigSet")?;
        _f.debug_list().entries(self.configs.iter()).finish()
    }
}

impl<'ephemeral> PartialEq<LexerATNConfigSet<'_>> for MutableLexerATNConfigSet<'ephemeral> {
    fn eq(&self, other: &LexerATNConfigSet<'_>) -> bool {
        self.base == other.base && self.configs == other.configs
    }
}

impl Hash for MutableLexerATNConfigSet<'_> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.configs.iter().for_each(|c| c.hash(state));
    }
}

impl<'ephemeral> IntoIterator for MutableLexerATNConfigSet<'ephemeral> {
    type Item = LexerATNConfig<'ephemeral>;
    type IntoIter =
        <ScratchStore<'ephemeral, LexerATNConfig<'ephemeral>> as IntoIterator>::IntoIter;

    fn into_iter(self) -> Self::IntoIter {
        self.configs.into_iter()
    }
}

impl<'ephemeral> MutableConfigSet<'ephemeral> for MutableLexerATNConfigSet<'ephemeral> {
    type ConfigType = LexerATNConfig<'ephemeral>;
    type FinalizedType<'x> = LexerATNConfigSet<'x>;

    fn hash_code(&self) -> u64 {
        self.configs.hash_code()
    }

    fn eq_config_set(&self, other: &Self::FinalizedType<'_>) -> bool {
        self.base == other.base && self.configs == other.configs
    }

    fn finalize<'sim>(
        self,
        cache: &'sim PredictionContextCache<'sim>,
        dfa: &DFAStateStore<'sim, Self::FinalizedType<'sim>>,
    ) -> Self::FinalizedType<'sim> {
        LexerATNConfigSet {
            configs: self.configs.finalize(cache, dfa),
            base: self.base,
        }
    }
}

impl<'ephemeral> MutableLexerATNConfigSet<'ephemeral> {
    pub fn new(arena: &'ephemeral bumpalo::Bump) -> Self {
        MutableLexerATNConfigSet {
            base: super::ATNConfigSetBase {
                dips_into_outer_context: false,
                full_ctx: true,
                has_semantic_context: false,
                unique_alt: 0,
            },
            configs: ScratchStore::new(arena),
        }
    }

    pub(crate) fn add(&mut self, config: LexerATNConfig<'ephemeral>) -> bool {
        let store = &mut self.configs;

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

pub struct ScratchStore<'ephemeral, AC>
where
    AC: ATNConfigType<'ephemeral>,
{
    lookup: HashTable<Key, &'ephemeral bumpalo::Bump>,
    configs: bumpalo::collections::Vec<'ephemeral, AC>,
}

impl<'ephemeral, AC> PartialEq for ScratchStore<'ephemeral, AC>
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

impl<'l, 'r, LAC, RAC> PartialEq<ImmutableStore<'r, RAC>> for ScratchStore<'l, LAC>
where
    LAC: ATNConfigType<'l> + PartialEq<RAC>,
    RAC: ATNConfigType<'r>,
{
    fn eq(&self, other: &ImmutableStore<'r, RAC>) -> bool {
        if self.len() != other.len() {
            return false;
        }

        self.iter().zip(other.iter()).all(|(a, b)| a == b)
    }
}

impl<'ephemeral, AC> Eq for ScratchStore<'ephemeral, AC> where AC: ATNConfigType<'ephemeral> {}

impl<'ephemeral, AC> IntoIterator for ScratchStore<'ephemeral, AC>
where
    AC: ATNConfigType<'ephemeral> + 'ephemeral,
{
    type Item = AC;
    type IntoIter = bumpalo::collections::vec::IntoIter<'ephemeral, AC>;

    fn into_iter(self) -> Self::IntoIter {
        self.configs.into_iter()
    }
}

impl<'ephemeral, AC> ScratchStore<'ephemeral, AC>
where
    AC: ATNConfigType<'ephemeral>,
{
    fn new(ephemerals: &'ephemeral bumpalo::Bump) -> Self {
        Self {
            lookup: HashTable::with_capacity_in(7, ephemerals),
            configs: bumpalo::collections::Vec::new_in(ephemerals),
        }
    }

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
        let mut hasher = FxHasher64::default();
        self.configs.iter().for_each(|c| c.hash(&mut hasher));
        hasher.finish()
    }
}

impl<'ephemeral> ScratchStore<'ephemeral, ATNConfig<'ephemeral>> {
    fn finalize<'sim>(
        self,
        cache: &'sim PredictionContextCache,
        dfa: &DFAStateStore<'sim, ATNConfigSet<'sim>>,
    ) -> ImmutableStore<'sim, ATNConfig<'sim>> {
        let cached_hash = self.hash_code();

        ImmutableStore {
            cached_hash,
            configs: dfa
                .alloc_config_slice(self.configs.into_iter().map(|c| c.finalize(cache, dfa))),
        }
    }
}

impl<'ephemeral> ScratchStore<'ephemeral, LexerATNConfig<'ephemeral>> {
    fn finalize<'sim>(
        self,
        cache: &'sim PredictionContextCache,
        dfa: &DFAStateStore<'sim, LexerATNConfigSet<'sim>>,
    ) -> ImmutableStore<'sim, LexerATNConfig<'sim>> {
        let cached_hash = self.hash_code();

        ImmutableStore {
            cached_hash,
            configs: dfa
                .alloc_config_slice(self.configs.into_iter().map(|c| c.finalize(cache, dfa))),
        }
    }
}
