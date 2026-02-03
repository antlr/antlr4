use std::cmp::max;
use std::fmt::{Debug, Error, Formatter};
use std::hash::{Hash, Hasher};

use bit_set::BitSet;
use hashbrown::HashTable;
use murmur3::murmur3_32::MurmurHasher;

use crate::atn_config::ATNConfig;
use crate::atn_simulator::IATNSimulator;
use crate::parser_atn_simulator::MergeCache;
use crate::prediction_context::PredictionContext;
use crate::semantic_context::SemanticContext;

pub struct ATNConfigSet<'ephemeral> {
    configs: ConfigSetStore<'ephemeral>,

    pub(crate) conflicting_alts: BitSet,

    dips_into_outer_context: bool,

    full_ctx: bool,

    has_semantic_context: bool,

    unique_alt: i32,

    /// creates key for lookup
    /// Key::Full - for Lexer
    /// Key::Partial  - for Parser
    key_maker: fn(&ATNConfig, usize) -> Key,
}

impl Debug for ATNConfigSet<'_> {
    fn fmt(&self, _f: &mut Formatter<'_>) -> Result<(), Error> {
        _f.write_str("ATNConfigSet")?;
        _f.debug_list().entries(self.configs.iter()).finish()?;
        if self.has_semantic_context {
            _f.write_str(",hasSemanticContext=true")?
        }
        if self.conflicting_alts.is_empty() {
            _f.write_fmt(format_args!(",uniqueAlt={}", self.unique_alt))
        } else {
            _f.write_fmt(format_args!(",conflictingAlts={:?}", self.conflicting_alts))
        }
    }
}

impl PartialEq for ATNConfigSet<'_> {
    fn eq(&self, other: &Self) -> bool {
        self.configs == other.configs
            && self.full_ctx == other.full_ctx
            && self.unique_alt == other.unique_alt
            && self.conflicting_alts == other.conflicting_alts
            && self.has_semantic_context == other.has_semantic_context
            && self.dips_into_outer_context == other.dips_into_outer_context
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
    type IntoIter = ConfigSetIntoIter<'ephemeral>;

    fn into_iter(self) -> Self::IntoIter {
        self.configs.into_iter()
    }
}

impl ATNConfigSet<'static> {
    pub fn new_empty() -> Self {
        ATNConfigSet {
            configs: ConfigSetStore::new_empty(),
            conflicting_alts: Default::default(),
            dips_into_outer_context: false,
            full_ctx: true,
            has_semantic_context: false,
            unique_alt: 0,
            key_maker: Key::partial,
        }
    }
}

impl<'ephemeral> ATNConfigSet<'ephemeral> {
    pub fn new(arena: &'ephemeral bumpalo::Bump, full_ctx: bool) -> ATNConfigSet<'ephemeral> {
        ATNConfigSet {
            configs: ConfigSetStore::new_ephemeral(arena),
            conflicting_alts: Default::default(),
            dips_into_outer_context: false,
            full_ctx,
            has_semantic_context: false,
            unique_alt: 0,
            key_maker: Key::partial,
        }
    }

    // for lexerATNConfig
    pub fn new_ordered(arena: &'ephemeral bumpalo::Bump) -> ATNConfigSet<'ephemeral> {
        let mut res = ATNConfigSet::new(arena, true);

        res.key_maker = Key::full;
        res
    }

    pub fn into_stored(self, interpreter: &dyn IATNSimulator) -> Box<ATNConfigSet<'static>> {
        match self.configs {
            ConfigSetStore::Ephemeral(s) => {
                let cached_hash = {
                    let mut hasher = MurmurHasher::default();
                    s.configs.iter().for_each(|c| c.hash(&mut hasher));
                    hasher.finish()
                };

                let static_configs = s
                    .configs
                    .into_iter()
                    .map(|config| {
                        let stored_context = config.get_context().map(|context| {
                            interpreter
                                .shared_context_cache()
                                .get_shared_context(context)
                                .get_static_ref()
                        });
                        config.with_prediction_context(stored_context)
                    })
                    .collect();

                Box::new(ATNConfigSet {
                    configs: ConfigSetStore::Static(StaticStore {
                        cached_hash,
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

    pub fn hash_code(&self) -> u64 {
        match &self.configs {
            ConfigSetStore::Ephemeral(s) => {
                let mut hasher = MurmurHasher::default();
                s.configs.iter().for_each(|c| c.hash(&mut hasher));
                hasher.finish()
            }
            ConfigSetStore::Static(s) => s.cached_hash,
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
            self.has_semantic_context = true
        }

        if config.get_reaches_into_outer_context() > 0 {
            self.dips_into_outer_context = true
        }

        let key = (self.key_maker)(&config, store.configs.len());

        if let Some(key) = store
            .lookup
            .find(key.hash_code(), |k| k.eq(&config, &store.configs))
        {
            let existing = &mut store.configs[key.index()];
            let root_is_wildcard = !self.full_ctx;

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

    // for lexer
    pub(crate) fn add(&mut self, config: ATNConfig<'ephemeral>) -> bool {
        let store = match &mut self.configs {
            ConfigSetStore::Ephemeral(s) => s,
            ConfigSetStore::Static(_) => panic!("Cannot add to read-only ATNConfigSet"),
        };

        if config.semantic_context() != &SemanticContext::NONE {
            self.has_semantic_context = true
        }

        if config.get_reaches_into_outer_context() > 0 {
            self.dips_into_outer_context = true
        }

        let key = (self.key_maker)(&config, store.configs.len());

        if store
            .lookup
            .find(key.hash_code(), |k| k.eq(&config, &store.configs))
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
        self.has_semantic_context
    }

    pub fn set_has_semantic_context(&mut self, _v: bool) {
        self.has_semantic_context = _v;
    }

    pub fn read_only(&self) -> bool {
        self.configs.read_only()
    }

    pub fn full_context(&self) -> bool {
        self.full_ctx
    }

    //duplicate of the self.conflicting_alts???
    pub fn get_alts(&self) -> BitSet {
        self.configs.iter().fold(BitSet::new(), |mut acc, c| {
            acc.insert(c.get_alt() as usize);
            acc
        })
    }

    pub fn get_unique_alt(&self) -> i32 {
        self.unique_alt
    }

    pub fn set_unique_alt(&mut self, _v: i32) {
        self.unique_alt = _v
    }

    pub fn get_dips_into_outer_context(&self) -> bool {
        self.dips_into_outer_context
    }

    pub fn set_dips_into_outer_context(&mut self, _v: bool) {
        self.dips_into_outer_context = _v
    }
}

enum Key {
    Full(u64, usize),
    Partial(u64, usize),
}

impl Key {
    fn full(config: &ATNConfig, index: usize) -> Self {
        Key::Full(Self::full_hash(config), index)
    }

    fn full_hash(config: &ATNConfig) -> u64 {
        let mut hasher = MurmurHasher::default();
        config.hash(&mut hasher);
        hasher.finish()
    }

    fn partial(config: &ATNConfig, index: usize) -> Self {
        Key::Partial(Self::partial_hash(config), index)
    }

    fn partial_hash(config: &ATNConfig) -> u64 {
        let mut hasher = MurmurHasher::default();
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

    fn eq(&self, other: &ATNConfig, configs: &[ATNConfig]) -> bool {
        match self {
            Key::Full(_, index) => {
                let left = &configs[*index];
                left == other
            }
            Key::Partial(_, index) => {
                let left = &configs[*index];
                left.get_state() == other.get_state()
                    && left.get_alt() == other.get_alt()
                    && left.semantic_context() == other.semantic_context()
            }
        }
    }
}

enum ConfigSetStore<'ephemeral> {
    Ephemeral(EphemeralStore<'ephemeral>),
    Static(StaticStore),
}

struct EphemeralStore<'ephemeral> {
    lookup: HashTable<Key, &'ephemeral bumpalo::Bump>,
    configs: bumpalo::collections::Vec<'ephemeral, ATNConfig<'ephemeral>>,
}

struct StaticStore {
    cached_hash: u64,
    configs: Vec<ATNConfig<'static>>,
}

impl PartialEq for ConfigSetStore<'_> {
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

impl Eq for ConfigSetStore<'_> {}

pub enum ConfigSetIntoIter<'ephemeral> {
    Ephemeral(bumpalo::collections::vec::IntoIter<'ephemeral, ATNConfig<'ephemeral>>),
    Static(std::vec::IntoIter<ATNConfig<'static>>),
}

impl<'ephemeral> Iterator for ConfigSetIntoIter<'ephemeral> {
    type Item = ATNConfig<'ephemeral>;

    fn next(&mut self) -> Option<Self::Item> {
        match self {
            ConfigSetIntoIter::Ephemeral(it) => it.next(),
            ConfigSetIntoIter::Static(it) => it.next(),
        }
    }
}

impl<'ephemeral> IntoIterator for ConfigSetStore<'ephemeral> {
    type Item = ATNConfig<'ephemeral>;
    type IntoIter = ConfigSetIntoIter<'ephemeral>;

    fn into_iter(self) -> Self::IntoIter {
        match self {
            ConfigSetStore::Ephemeral(s) => ConfigSetIntoIter::Ephemeral(s.configs.into_iter()),
            ConfigSetStore::Static(s) => ConfigSetIntoIter::Static(s.configs.into_iter()),
        }
    }
}

impl ConfigSetStore<'_> {
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

impl ConfigSetStore<'static> {
    fn new_empty() -> Self {
        let configs = Vec::new();
        let cached_hash = {
            let mut hasher = MurmurHasher::default();
            configs.hash(&mut hasher);
            hasher.finish()
        };
        ConfigSetStore::Static(StaticStore {
            cached_hash,
            configs,
        })
    }
}

impl<'ephemeral> ConfigSetStore<'ephemeral> {
    fn new_ephemeral(arena: &'ephemeral bumpalo::Bump) -> Self {
        ConfigSetStore::Ephemeral(EphemeralStore {
            lookup: HashTable::with_capacity_in(7, arena),
            configs: bumpalo::collections::Vec::new_in(arena),
        })
    }

    fn iter(&self) -> impl Iterator<Item = &ATNConfig<'ephemeral>> {
        match self {
            ConfigSetStore::Ephemeral(s) => s.configs.iter(),
            ConfigSetStore::Static(s) => s.configs.iter(),
        }
    }

    // fn get(&self, index: usize) -> &ATNConfig<'ephemeral> {
    //     match self {
    //         ConfigSetStore::Ephemeral(s) => &s.configs[index],
    //         ConfigSetStore::Static(s) => &s.configs[index],
    //     }
    // }
}
