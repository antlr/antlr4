use std::cmp::max;
use std::collections::HashMap;
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

pub struct ATNConfigSet {
    cached_hash: u64,

    config_lookup: HashTable<Key>,

    configs: Vec<ATNConfig>,

    pub(crate) conflicting_alts: BitSet,

    dips_into_outer_context: bool,

    full_ctx: bool,

    has_semantic_context: bool,

    read_only: bool,

    unique_alt: i32,

    /// creates key for lookup
    /// Key::Full - for Lexer
    /// Key::Partial  - for Parser
    key_maker: fn(&ATNConfig, usize) -> Key,
}

impl Debug for ATNConfigSet {
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

impl PartialEq for ATNConfigSet {
    fn eq(&self, other: &Self) -> bool {
        self.configs == other.configs
            && self.full_ctx == other.full_ctx
            && self.unique_alt == other.unique_alt
            && self.conflicting_alts == other.conflicting_alts
            && self.has_semantic_context == other.has_semantic_context
            && self.dips_into_outer_context == other.dips_into_outer_context
    }
}

impl Eq for ATNConfigSet {}

impl Hash for ATNConfigSet {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.configs.hash(state)
    }
}

impl IntoIterator for ATNConfigSet {
    type Item = ATNConfig;
    type IntoIter = std::vec::IntoIter<ATNConfig>;

    fn into_iter(self) -> Self::IntoIter {
        self.configs.into_iter()
    }
}

impl ATNConfigSet {
    pub fn new(full_ctx: bool) -> ATNConfigSet {
        ATNConfigSet {
            cached_hash: 0,
            config_lookup: HashTable::with_capacity(7),
            configs: Vec::with_capacity(7),
            conflicting_alts: Default::default(),
            dips_into_outer_context: false,
            full_ctx,
            has_semantic_context: false,
            read_only: false,
            unique_alt: 0,
            key_maker: Key::partial,
        }
    }

    // for lexerATNConfig
    pub fn new_ordered() -> ATNConfigSet {
        let mut a = ATNConfigSet::new(true);

        a.key_maker = Key::full;
        a
    }

    // for parser
    pub(crate) fn add_cached(&mut self, config: ATNConfig, merge_cache: &mut MergeCache) -> bool {
        assert!(!self.read_only);

        if config.semantic_context() != &SemanticContext::NONE {
            self.has_semantic_context = true
        }

        if config.get_reaches_into_outer_context() > 0 {
            self.dips_into_outer_context = true
        }

        let key = (self.key_maker)(&config, self.configs.len());

        if let Some(key) = self
            .config_lookup
            .find(key.hash_code(), |k| k.eq(&config, &self.configs))
        {
            let existing = &mut self.configs[key.index()];
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
            self.configs.push(config);
            self.config_lookup
                .insert_unique(key.hash_code(), key, Key::hash_code);
            self.cached_hash = 0;
        }
        true
    }

    // for lexer
    pub(crate) fn add(&mut self, config: ATNConfig) -> bool {
        assert!(!self.read_only);

        if config.semantic_context() != &SemanticContext::NONE {
            self.has_semantic_context = true
        }

        if config.get_reaches_into_outer_context() > 0 {
            self.dips_into_outer_context = true
        }

        let key = (self.key_maker)(&config, self.configs.len());

        if self
            .config_lookup
            .find(key.hash_code(), |k| k.eq(&config, &self.configs))
            .is_none()
        {
            self.configs.push(config);
            self.config_lookup
                .insert_unique(key.hash_code(), key, Key::hash_code);
            self.cached_hash = 0;
        }
        true
    }

    pub fn get_items(&self) -> impl Iterator<Item = &ATNConfig> {
        self.configs.iter()
    }

    pub fn optimize_configs(&mut self, _interpreter: &dyn IATNSimulator) {
        if self.configs.is_empty() {
            return;
        }

        for config in self.configs.iter_mut() {
            let mut visited = HashMap::new();
            let context = config.get_context().unwrap();
            let context = _interpreter
                .shared_context_cache()
                .get_shared_context(context, &mut visited);
            config.set_context(context);
        }
    }

    pub fn hash_code(&mut self) -> u64 {
        if self.read_only {
            if self.cached_hash == 0 {
                let mut hasher = MurmurHasher::default();
                self.hash(&mut hasher);
                self.cached_hash = hasher.finish();
            }
            self.cached_hash
        } else {
            let mut hasher = MurmurHasher::default();
            self.hash(&mut hasher);
            hasher.finish()
        }
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
        self.read_only
    }

    pub fn set_read_only(&mut self, _read_only: bool) {
        self.read_only = _read_only;
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
