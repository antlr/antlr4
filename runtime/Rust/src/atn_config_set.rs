use std::cmp::max;
use std::collections::{HashMap, HashSet};
use std::fmt::{Debug, Error, Formatter};
use std::hash::{Hash, Hasher};
use std::ops::Deref;
use std::pin::Pin;

use bit_set::BitSet;
use murmur3::murmur3_32::MurmurHasher;

use crate::atn_config::ATNConfig;
use crate::atn_simulator::IATNSimulator;
use crate::parser_atn_simulator::MergeCache;
use crate::prediction_context::{NoopHasherBuilder, PredictionContext};
use crate::semantic_context::SemanticContext;

pub struct ATNConfigSet {
    cached_hash: u64,

    config_lookup: HashSet<Key, NoopHasherBuilder>,

    configs: Vec<Pin<Box<ATNConfig>>>,

    pub(crate) conflicting_alts: BitSet,

    dips_into_outer_context: bool,

    full_ctx: bool,

    has_semantic_context: bool,

    read_only: bool,

    unique_alt: i32,

    /// creates key for lookup
    /// Key::Full - for Lexer
    /// Key::Partial  - for Parser
    key_maker: unsafe fn(&ATNConfig) -> Key,
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

impl ATNConfigSet {
    pub fn new(full_ctx: bool) -> ATNConfigSet {
        ATNConfigSet {
            cached_hash: 0,
            config_lookup: HashSet::with_hasher(NoopHasherBuilder {}),
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

    pub fn add_cached(
        &mut self,
        config: ATNConfig,
        mut merge_cache: Option<&mut MergeCache>,
    ) -> bool {
        assert!(!self.read_only);

        if config.semantic_context() != &SemanticContext::NONE {
            self.has_semantic_context = true
        }

        if config.get_reaches_into_outer_context() > 0 {
            self.dips_into_outer_context = true
        }

        let key = unsafe { (self.key_maker)(&config) };

        if let Some(existing) = self.config_lookup.get(&key) {
            let existing = unsafe { existing.as_ref_mut() };
            let root_is_wildcard = !self.full_ctx;

            let merged = PredictionContext::merge(
                existing.get_context().unwrap(),
                config.get_context().unwrap(),
                root_is_wildcard,
                &mut merge_cache,
            );

            let v1 = existing.get_reaches_into_outer_context();
            let v2 = config.get_reaches_into_outer_context();
            existing.set_reaches_into_outer_context(max(v1, v2));

            if config.is_precedence_filter_suppressed() {
                existing.set_precedence_filter_suppressed(true)
            }

            existing.set_context(merged);
        } else {
            let config = Box::pin(config);
            let key = unsafe { (self.key_maker)(&config) };
            self.config_lookup.insert(key);
            self.cached_hash = 0;
            self.configs.push(config);
        }
        true
    }

    pub fn add(&mut self, config: ATNConfig) -> bool {
        self.add_cached(config, None)
    }

    pub fn get_items(&self) -> impl Iterator<Item = &ATNConfig> {
        self.configs.iter().map(|x| &**x)
    }

    #[allow(clippy::should_implement_trait)]
    pub fn into_iter(self) -> impl Iterator<Item = ATNConfig> {
        self.configs
            .into_iter()
            .map(|x| unsafe { *Pin::into_inner_unchecked(x) })
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
    Full(*const ATNConfig, u64),
    Partial(*const ATNConfig, u64),
}

impl AsRef<ATNConfig> for Key {
    fn as_ref(&self) -> &ATNConfig {
        match self {
            Key::Full(x, _) => unsafe { &**x },
            Key::Partial(x, _) => unsafe { &**x },
        }
    }
}

impl Deref for Key {
    type Target = ATNConfig;

    fn deref(&self) -> &Self::Target {
        match self {
            Key::Full(x, _) => unsafe { &**x },
            Key::Partial(x, _) => unsafe { &**x },
        }
    }
}

impl PartialEq for Key {
    fn eq(&self, other: &Self) -> bool {
        match (self, other) {
            (Key::Full(..), Key::Full(..)) => self.as_ref() == other.as_ref(),
            (Key::Partial(..), Key::Partial(..)) => {
                let left = self.as_ref();
                let right = other.as_ref();
                left.get_state() == right.get_state()
                    && left.get_alt() == right.get_alt()
                    && left.semantic_context() == right.semantic_context()
            }
            _ => false,
        }
    }
}

impl Eq for Key {}

impl Hash for Key {
    fn hash<H: Hasher>(&self, state: &mut H) {
        match self {
            Key::Full(_, hash) => state.write_u64(*hash),
            Key::Partial(_, hash) => state.write_u64(*hash),
        }
    }
}

impl Key {
    unsafe fn full(config: &ATNConfig) -> Self {
        let mut hasher = MurmurHasher::default();
        config.hash(&mut hasher);
        Key::Full(config as *const ATNConfig, hasher.finish())
    }

    unsafe fn partial(config: &ATNConfig) -> Self {
        let mut hasher = MurmurHasher::default();
        config.get_state().hash(&mut hasher);
        config.get_alt().hash(&mut hasher);
        config.semantic_context().hash(&mut hasher);
        Key::Partial(config as *const ATNConfig, hasher.finish())
    }

    #[allow(clippy::mut_from_ref)]
    #[inline]
    unsafe fn as_ref_mut(&self) -> &mut ATNConfig {
        match self {
            Key::Full(x, _) => &mut *(*x as *mut ATNConfig),
            Key::Partial(x, _) => &mut *(*x as *mut ATNConfig),
        }
    }
}
