use std::cmp::max;
use std::fmt::{Debug, Error, Formatter};
use std::hash::{Hash, Hasher};
use std::pin::Pin;

use bit_set::BitSet;
use fxhash::{hash64, FxHasher64};
use hashbrown::HashTable;

use crate::atn_config::{ATNConfig, ATNConfigType, LexerATNConfig};
use crate::atn_simulator::IATNSimulator;
use crate::lexer_action_executor::LexerActionExecutor;
use crate::parser_atn_simulator::MergeCache;
use crate::prediction_context::PredictionContext;
use crate::semantic_context::SemanticContext;

pub trait ConfigSet: PartialEq + Eq + Hash {
    type ConfigType: ATNConfigType;

    fn new_empty() -> Self;

    fn hash_code(&self) -> u64;

    // fn get_items(&self) -> Box<dyn Iterator<Item = &Self::ConfigType> + '_>;

    // fn length(&self) -> usize;

    // fn is_empty(&self) -> bool;

    // fn has_semantic_context(&self) -> bool;

    // fn set_has_semantic_context(&mut self, v: bool);
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

    pub(crate) conflicting_alts: BitSet,
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

impl Hash for ATNConfigSet<'_> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.configs.iter().for_each(|c| c.hash(state));
    }
}

impl<'ephemeral> IntoIterator for ATNConfigSet<'ephemeral> {
    type Item = ATNConfig<'ephemeral>;
    type IntoIter = ConfigSetIntoIter<'ephemeral, ATNConfig<'ephemeral>>;

    fn into_iter(self) -> Self::IntoIter {
        self.configs.into_iter()
    }
}

impl<'ephemeral> ConfigSet for ATNConfigSet<'ephemeral> {
    type ConfigType = ATNConfig<'ephemeral>;

    fn new_empty() -> Self {
        ATNConfigSet {
            base: ATNConfigSetBase {
                dips_into_outer_context: false,
                full_ctx: true,
                has_semantic_context: false,
                unique_alt: 0,
            },
            configs: ConfigSetStore::new_empty(),
            conflicting_alts: Default::default(),
        }
    }

    fn hash_code(&self) -> u64 {
        self.configs.hash_code()
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
            ConfigSetStore::Ephemeral(s) => s,
            ConfigSetStore::Static(_) => panic!("Cannot add to read-only ATNConfigSet"),
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

    pub fn into_stored(
        self,
        interpreter: &dyn IATNSimulator<ATNConfigSet<'static>>,
    ) -> Box<ATNConfigSet<'static>> {
        match self.configs {
            ConfigSetStore::Ephemeral(s) => {
                let cached_hash = {
                    let mut hasher = FxHasher64::default();
                    s.configs.iter().for_each(|c| c.hash(&mut hasher));
                    hasher.finish()
                };
                let semantic_contexts: Vec<SemanticContext> = s
                    .configs
                    .iter()
                    .map(|config| config.semantic_context().clone())
                    .collect();
                let semantic_contexts = Pin::new(semantic_contexts.into_boxed_slice());

                let static_configs = s
                    .configs
                    .into_iter()
                    .enumerate()
                    .map(|(i, config)| {
                        let static_context = config.get_context().map(|context| {
                            interpreter
                                .shared_context_cache()
                                .get_shared_context(context)
                                .get_static_ref()
                        });
                        let static_semantic_context = unsafe {
                            &*(semantic_contexts.get_unchecked(i) as *const SemanticContext)
                        };
                        config.make_static(static_context, static_semantic_context)
                    })
                    .collect();

                Box::new(ATNConfigSet {
                    configs: ConfigSetStore::Static(StaticStore {
                        cached_hash,
                        semantic_contexts,
                        lexer_action_executors: Box::pin([]),
                        configs: static_configs,
                    }),
                    ..self
                })
            }
            ConfigSetStore::Static(_) => Box::new(
                // Safety: if self.configs is ConfigSetStore::Static, then we
                // are 'static already
                unsafe {
                    std::mem::transmute::<ATNConfigSet<'ephemeral>, ATNConfigSet<'static>>(self)
                },
            ),
        }
    }

    pub(crate) fn add(&mut self, config: ATNConfig<'ephemeral>) -> bool {
        let store = match &mut self.configs {
            ConfigSetStore::Ephemeral(s) => s,
            ConfigSetStore::Static(_) => panic!("Cannot add to read-only ATNConfigSet"),
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
    type IntoIter = ConfigSetIntoIter<'ephemeral, LexerATNConfig<'ephemeral>>;

    fn into_iter(self) -> Self::IntoIter {
        self.configs.into_iter()
    }
}

impl<'ephemeral> ConfigSet for LexerATNConfigSet<'ephemeral> {
    type ConfigType = LexerATNConfig<'ephemeral>;

    fn new_empty() -> Self {
        LexerATNConfigSet {
            base: ATNConfigSetBase {
                dips_into_outer_context: false,
                full_ctx: true,
                has_semantic_context: false,
                unique_alt: 0,
            },
            configs: ConfigSetStore::new_empty(),
        }
    }
    fn hash_code(&self) -> u64 {
        self.configs.hash_code()
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

    pub fn into_stored(
        self,
        interpreter: &dyn IATNSimulator<LexerATNConfigSet<'static>>,
    ) -> Box<LexerATNConfigSet<'static>> {
        match self.configs {
            ConfigSetStore::Ephemeral(s) => {
                let cached_hash = {
                    let mut hasher = FxHasher64::default();
                    s.configs.iter().for_each(|c| c.hash(&mut hasher));
                    hasher.finish()
                };
                let semantic_contexts: Vec<SemanticContext> = s
                    .configs
                    .iter()
                    .map(|config| config.semantic_context().clone())
                    .collect();
                let lexer_action_executors: Vec<Option<LexerActionExecutor>> = s
                    .configs
                    .iter()
                    .map(|config| config.get_lexer_executor().cloned())
                    .collect();
                let semantic_contexts = Pin::new(semantic_contexts.into_boxed_slice());
                let lexer_action_executors = Pin::new(lexer_action_executors.into_boxed_slice());

                let static_configs = s
                    .configs
                    .into_iter()
                    .enumerate()
                    .map(|(i, config)| {
                        let static_context = config.get_context().map(|context| {
                            interpreter
                                .shared_context_cache()
                                .get_shared_context(context)
                                .get_static_ref()
                        });
                        let static_semantic_context = unsafe {
                            &*(semantic_contexts.get_unchecked(i) as *const SemanticContext)
                        };
                        let lexer_action_executor = unsafe {
                            lexer_action_executors
                                .get_unchecked(i)
                                .as_ref()
                                .map(|exec| &*(exec as *const LexerActionExecutor))
                        };

                        config.make_static(
                            static_context,
                            static_semantic_context,
                            lexer_action_executor,
                        )
                    })
                    .collect();

                Box::new(LexerATNConfigSet {
                    configs: ConfigSetStore::Static(StaticStore {
                        cached_hash,
                        semantic_contexts,
                        lexer_action_executors,
                        configs: static_configs,
                    }),
                    ..self
                })
            }
            ConfigSetStore::Static(_) => Box::new(
                // Safety: if self.configs is ConfigSetStore::Static, then we
                // are 'static already
                unsafe {
                    std::mem::transmute::<LexerATNConfigSet<'ephemeral>, LexerATNConfigSet<'static>>(
                        self,
                    )
                },
            ),
        }
    }
    pub(crate) fn add(&mut self, config: LexerATNConfig<'ephemeral>) -> bool {
        let store = match &mut self.configs {
            ConfigSetStore::Ephemeral(s) => s,
            ConfigSetStore::Static(_) => panic!("Cannot add to read-only ATNConfigSet"),
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

enum ConfigSetStore<'ephemeral, AC>
where
    AC: ATNConfigType + 'ephemeral,
{
    Ephemeral(EphemeralStore<'ephemeral, AC>),
    Static(StaticStore<AC>),
}

struct EphemeralStore<'ephemeral, AC>
where
    AC: ATNConfigType,
{
    lookup: HashTable<Key, &'ephemeral bumpalo::Bump>,
    configs: bumpalo::collections::Vec<'ephemeral, AC>,
}

struct StaticStore<AC>
where
    AC: ATNConfigType,
{
    cached_hash: u64,
    // Backing store for ATNConfig::semantic_context
    #[allow(dead_code)]
    semantic_contexts: Pin<Box<[SemanticContext]>>,
    // Backing store for ATNConfig::config_type
    #[allow(dead_code)]
    lexer_action_executors: Pin<Box<[Option<LexerActionExecutor>]>>,
    configs: Vec<AC>,
}

impl<AC> PartialEq for ConfigSetStore<'_, AC>
where
    AC: ATNConfigType,
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

impl<AC> Eq for ConfigSetStore<'_, AC> where AC: ATNConfigType {}

pub enum ConfigSetIntoIter<'ephemeral, AC>
where
    AC: ATNConfigType + 'ephemeral,
{
    Ephemeral(bumpalo::collections::vec::IntoIter<'ephemeral, AC>),
    Static(std::vec::IntoIter<AC>),
}

impl<'ephemeral, AC> Iterator for ConfigSetIntoIter<'ephemeral, AC>
where
    AC: ATNConfigType + 'ephemeral,
{
    type Item = AC;

    fn next(&mut self) -> Option<Self::Item> {
        match self {
            ConfigSetIntoIter::Ephemeral(it) => it.next(),
            ConfigSetIntoIter::Static(it) => it.next(),
        }
    }
}

impl<'ephemeral, AC> IntoIterator for ConfigSetStore<'ephemeral, AC>
where
    AC: ATNConfigType + 'ephemeral,
{
    type Item = AC;
    type IntoIter = ConfigSetIntoIter<'ephemeral, AC>;

    fn into_iter(self) -> Self::IntoIter {
        match self {
            ConfigSetStore::Ephemeral(s) => ConfigSetIntoIter::Ephemeral(s.configs.into_iter()),
            ConfigSetStore::Static(s) => ConfigSetIntoIter::Static(s.configs.into_iter()),
        }
    }
}

impl<AC> ConfigSetStore<'_, AC>
where
    AC: ATNConfigType,
{
    fn len(&self) -> usize {
        match self {
            ConfigSetStore::Ephemeral(s) => s.configs.len(),
            ConfigSetStore::Static(s) => s.configs.len(),
        }
    }

    fn is_empty(&self) -> bool {
        match self {
            ConfigSetStore::Ephemeral(s) => s.configs.is_empty(),
            ConfigSetStore::Static(s) => s.configs.is_empty(),
        }
    }

    fn read_only(&self) -> bool {
        match self {
            ConfigSetStore::Ephemeral(_) => false,
            ConfigSetStore::Static(_) => true,
        }
    }
}

impl<AC> ConfigSetStore<'static, AC>
where
    AC: ATNConfigType,
{
    fn new_empty() -> Self {
        let configs = Vec::new();
        let cached_hash = hash64(&configs);
        ConfigSetStore::Static(StaticStore {
            cached_hash,
            semantic_contexts: Box::pin([]),
            lexer_action_executors: Box::pin([]),
            configs,
        })
    }
}

impl<'ephemeral, AC> ConfigSetStore<'ephemeral, AC>
where
    AC: ATNConfigType + 'ephemeral,
{
    fn new_ephemeral(ephemerals: &'ephemeral bumpalo::Bump) -> Self {
        ConfigSetStore::Ephemeral(EphemeralStore {
            lookup: HashTable::with_capacity_in(7, ephemerals),
            configs: bumpalo::collections::Vec::new_in(ephemerals),
        })
    }

    fn iter(&self) -> impl Iterator<Item = &AC> {
        match self {
            ConfigSetStore::Ephemeral(s) => s.configs.iter(),
            ConfigSetStore::Static(s) => s.configs.iter(),
        }
    }

    fn hash_code(&self) -> u64 {
        match self {
            ConfigSetStore::Ephemeral(s) => {
                let mut hasher = FxHasher64::default();
                s.configs.iter().for_each(|c| c.hash(&mut hasher));
                hasher.finish()
            }
            ConfigSetStore::Static(s) => s.cached_hash,
        }
    }
}
