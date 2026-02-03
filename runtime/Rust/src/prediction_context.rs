use std::borrow::Borrow;
use std::collections::{HashMap, HashSet};
use std::fmt::{Display, Error, Formatter};
use std::hash::{BuildHasher, Hash, Hasher};

use std::pin::Pin;
use std::sync::{Arc, LazyLock, RwLock};

use murmur3::murmur3_32::MurmurHasher;

use crate::atn::ATN;
use crate::atn_state::ATNStateRef;
use crate::dfa::ScopeExt;
use crate::parser_atn_simulator::{MergeCache, MergeKey};

use crate::prediction_context::PredictionContext::{Array, Singleton};

use crate::transition::RuleTransition;
use crate::tree::RuleNode;

#[derive(Eq, Debug)]
pub enum PredictionContext<'ephemeral> {
    Singleton(SingletonPredictionContext<'ephemeral>),
    Array(ArrayPredictionContext<'ephemeral>),
}

impl PartialEq for PredictionContext<'_> {
    fn eq(&self, other: &Self) -> bool {
        match (self, other) {
            (Array(s), Array(o)) => *s == *o,
            (Singleton(s), Singleton(o)) => *s == *o,
            _ => false,
        }
    }
}

#[derive(Eq, Clone, Debug)]
pub struct SingletonPredictionContext<'ephemeral> {
    cached_hash: u32,
    return_state: ATNStateRef,
    parent_ctx: Option<&'ephemeral PredictionContext<'ephemeral>>,
}

impl PartialEq for SingletonPredictionContext<'_> {
    #[inline(always)]
    fn eq(&self, other: &Self) -> bool {
        self.cached_hash == other.cached_hash
            && self.return_state == other.return_state
            && opt_eq((&self.parent_ctx, &other.parent_ctx))
    }
}

impl SingletonPredictionContext<'_> {
    #[inline(always)]
    fn is_empty(&self) -> bool {
        self.return_state == ATNStateRef::invalid() && self.parent_ctx.is_none()
    }
}

#[derive(Eq, Debug)]
pub struct ArrayPredictionContext<'ephemeral> {
    cached_hash: u32,
    return_states: &'ephemeral [ATNStateRef],
    parents: &'ephemeral [Option<&'ephemeral PredictionContext<'ephemeral>>],
}

impl PartialEq for ArrayPredictionContext<'_> {
    #[inline(always)]
    fn eq(&self, other: &Self) -> bool {
        self.cached_hash == other.cached_hash
            && self.return_states == other.return_states
            && self.parents.iter().zip(other.parents.iter()).all(opt_eq)
    }
}

#[inline(always)]
pub fn opt_eq<'a, 'b>(
    arg: (
        &Option<&'a PredictionContext<'a>>,
        &Option<&'b PredictionContext<'b>>,
    ),
) -> bool {
    match arg {
        (Some(s), Some(o)) => std::ptr::eq(*s, *o) || *s == *o,
        (None, None) => true,
        _ => false,
    }
}

impl Display for PredictionContext<'_> {
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        match self {
            Singleton(s) => {
                if s.return_state == ATNStateRef::invalid() {
                    f.write_str("$")
                } else if let Some(parent) = &s.parent_ctx {
                    f.write_fmt(format_args!(
                        "{} {}",
                        s.return_state.get_state_number(),
                        parent
                    ))
                } else {
                    f.write_fmt(format_args!("{}", s.return_state.get_state_number()))
                }
            }
            Array(arr) => {
                f.write_str("[")?;
                for i in 0..arr.return_states.len() {
                    if i > 0 {
                        f.write_str(", ")?;
                    }
                    if arr.return_states[i] == ATNStateRef::invalid() {
                        f.write_str("$")?;
                    } else {
                        f.write_str(&arr.return_states[i].get_state_number().to_string())?;
                    }
                    if let Some(parent) = &arr.parents[i] {
                        f.write_fmt(format_args!(" {}", parent))?;
                    } else {
                        f.write_str(" null")?;
                    }
                }

                f.write_str("]")
            }
        }
    }
}

impl Hash for PredictionContext<'_> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_u32(self.hash_code())
    }
}

pub static EMPTY_PREDICTION_CONTEXT: LazyLock<PredictionContext<'static>> =
    LazyLock::new(PredictionContext::new_empty);

impl PredictionContext<'static> {
    pub fn new_empty() -> Self {
        PredictionContext::Singleton(SingletonPredictionContext {
            cached_hash: 0,
            parent_ctx: None,
            return_state: ATNStateRef::invalid(),
        })
        .modify_with(|x| x.calc_hash())
    }
}

impl<'ephemeral> PredictionContext<'ephemeral> {
    pub fn new_singleton(
        parent_ctx: Option<&'ephemeral PredictionContext<'ephemeral>>,
        return_state: ATNStateRef,
    ) -> Self {
        PredictionContext::Singleton(SingletonPredictionContext {
            cached_hash: 0,
            parent_ctx,
            return_state,
        })
        .modify_with(|x| x.calc_hash())
    }

    fn new_array(
        parents: &'ephemeral [Option<&'ephemeral PredictionContext<'ephemeral>>],
        return_states: &'ephemeral [ATNStateRef],
    ) -> Self {
        PredictionContext::Array(ArrayPredictionContext {
            cached_hash: 0,
            parents,
            return_states,
        })
        .modify_with(|x| x.calc_hash())
    }

    fn calc_hash(&mut self) {
        let mut hasher = MurmurHasher::default();
        match self {
            PredictionContext::Singleton(SingletonPredictionContext {
                parent_ctx,
                return_state,
                ..
            }) => {
                hasher.write_u32(match parent_ctx {
                    None => 0,
                    Some(x) => x.hash_code(),
                });
                hasher.write_usize(return_state.as_usize());
            }
            PredictionContext::Array(ArrayPredictionContext {
                parents,
                return_states,
                ..
            }) => {
                parents.iter().for_each(|x| {
                    hasher.write_u32(match x {
                        None => 0,
                        Some(x) => x.hash_code(),
                    })
                });
                return_states
                    .iter()
                    .for_each(|x| hasher.write_usize(x.as_usize()));
            } //            PredictionContext::Empty { .. } => {}
        };

        let hash = hasher.finish() as u32;

        match self {
            PredictionContext::Singleton(SingletonPredictionContext { cached_hash, .. })
            | PredictionContext::Array(ArrayPredictionContext { cached_hash, .. }) => {
                *cached_hash = hash
            }
        };
    }

    pub fn get_parent(&self, index: usize) -> Option<&'ephemeral PredictionContext<'ephemeral>> {
        match self {
            PredictionContext::Singleton(singleton) => {
                //                assert_eq!(index, 0);
                singleton.parent_ctx
            }
            PredictionContext::Array(array) => array.parents[index],
        }
    }

    pub fn get_return_state(&self, index: usize) -> ATNStateRef {
        match self {
            PredictionContext::Singleton(SingletonPredictionContext { return_state, .. }) => {
                *return_state
            }
            PredictionContext::Array(ArrayPredictionContext { return_states, .. }) => {
                return_states[index]
            }
        }
    }

    pub fn length(&self) -> usize {
        match self {
            PredictionContext::Singleton { .. } => 1,
            PredictionContext::Array(ArrayPredictionContext { return_states, .. }) => {
                return_states.len()
            }
        }
    }

    #[inline(always)]
    pub fn is_empty(&self) -> bool {
        if let PredictionContext::Singleton(singleton) = self {
            return singleton.is_empty();
        }
        self.get_return_state(0) == ATNStateRef::invalid()
    }

    #[inline(always)]
    pub fn has_empty_path(&self) -> bool {
        self.get_return_state(self.length() - 1) == ATNStateRef::invalid()
    }

    #[inline(always)]
    pub fn hash_code(&self) -> u32 {
        match self {
            PredictionContext::Singleton(SingletonPredictionContext { cached_hash, .. })
            | PredictionContext::Array(ArrayPredictionContext { cached_hash, .. }) => *cached_hash,
        }
    }

    pub(crate) fn merge(
        a: &'ephemeral PredictionContext<'ephemeral>,
        b: &'ephemeral PredictionContext<'ephemeral>,
        root_is_wildcard: bool,
        cache: &mut MergeCache<'ephemeral>,
    ) -> &'ephemeral PredictionContext<'ephemeral> {
        if std::ptr::eq(a, b) || *a == *b {
            return a;
        }

        let key = MergeKey::new(a, b);
        if let Some(prev) = cache.get(&key).or_else(|| cache.get(&key.reverse())) {
            return prev;
        }

        let r = match (a, b) {
            (PredictionContext::Singleton(sa), PredictionContext::Singleton(sb)) => {
                //                println!("single result = {}",result);
                Self::merge_singletons(sa, sb, root_is_wildcard, cache)
            }
            (sa, sb) => {
                if root_is_wildcard {
                    if sa.is_empty() {
                        return &EMPTY_PREDICTION_CONTEXT;
                    }
                    if sb.is_empty() {
                        return &EMPTY_PREDICTION_CONTEXT;
                    }
                }

                let result = Self::merge_arrays(sa, sb, root_is_wildcard, cache);

                if result == sa {
                    a
                } else if result == sb {
                    b
                } else {
                    result
                }
            }
        };
        assert_ne!(r.hash_code(), 0);

        //            cache.entry(a.clone()).or_insert_with(||HashMap::new())
        //                .insert(b.clone(),r.clone());
        cache.insert(MergeKey::new(a, b), r);

        r
    }

    fn merge_singletons(
        a: &'ephemeral SingletonPredictionContext<'ephemeral>,
        b: &'ephemeral SingletonPredictionContext<'ephemeral>,
        root_is_wildcard: bool,
        merge_cache: &mut MergeCache<'ephemeral>,
    ) -> &'ephemeral PredictionContext<'ephemeral> {
        Self::merge_root(a, b, root_is_wildcard, merge_cache).unwrap_or_else(|| {
            let res = if a.return_state == b.return_state {
                let parent = Self::merge(
                    a.parent_ctx.as_ref().unwrap(),
                    b.parent_ctx.as_ref().unwrap(),
                    root_is_wildcard,
                    merge_cache,
                );
                if std::ptr::eq(parent, *a.parent_ctx.as_ref().unwrap()) {
                    Singleton(a.clone())
                } else if std::ptr::eq(parent, *b.parent_ctx.as_ref().unwrap()) {
                    Singleton(b.clone())
                } else {
                    Self::new_singleton(Some(parent), a.return_state)
                }
            } else {
                let parents = if a.parent_ctx == b.parent_ctx {
                    merge_cache.alloc([a.parent_ctx, a.parent_ctx])
                } else {
                    merge_cache.alloc([a.parent_ctx, b.parent_ctx])
                };
                let return_states = merge_cache.arena.alloc([a.return_state, b.return_state]);

                if return_states[0] > return_states[1] {
                    parents.swap(0, 1);
                    return_states.swap(0, 1);
                }

                Self::new_array(parents, return_states)
            };
            merge_cache.alloc(res)
        })
    }

    fn merge_root(
        a: &'ephemeral SingletonPredictionContext<'ephemeral>,
        b: &'ephemeral SingletonPredictionContext<'ephemeral>,
        root_is_wildcard: bool,
        merge_cache: &mut MergeCache<'ephemeral>,
    ) -> Option<&'ephemeral PredictionContext<'ephemeral>> {
        if root_is_wildcard {
            if a.is_empty() || b.is_empty() {
                return Some(&EMPTY_PREDICTION_CONTEXT);
            }
        } else {
            if a.is_empty() && b.is_empty() {
                return Some(&EMPTY_PREDICTION_CONTEXT);
            }
            if a.is_empty() {
                return Some(merge_cache.alloc(Self::new_array(
                    merge_cache.alloc([b.parent_ctx, None]),
                    merge_cache.alloc([b.return_state, ATNStateRef::invalid()]),
                )));
            }
            if b.is_empty() {
                return Some(merge_cache.alloc(Self::new_array(
                    merge_cache.alloc([a.parent_ctx, None]),
                    merge_cache.alloc([a.return_state, ATNStateRef::invalid()]),
                )));
            }
        }

        None
    }

    fn merge_arrays(
        a: &'ephemeral PredictionContext<'ephemeral>,
        b: &'ephemeral PredictionContext<'ephemeral>,
        root_is_wildcard: bool,
        merge_cache: &mut MergeCache<'ephemeral>,
    ) -> &'ephemeral PredictionContext<'ephemeral> {
        let mut parents = merge_cache.alloc_vec(a.length() + b.length());
        let mut return_states = merge_cache.alloc_vec(a.length() + b.length());
        let mut i = 0;
        let mut j = 0;

        while i < a.length() && j < b.length() {
            let a_parent = a.get_parent(i);
            let b_parent = b.get_parent(j);
            if a.get_return_state(i) == b.get_return_state(j) {
                let payload = a.get_return_state(i);
                let both =
                    payload == ATNStateRef::invalid() && a_parent.is_none() && b_parent.is_none();
                let ax_ax = a_parent.is_some() && b_parent.is_some() && a_parent == b_parent;

                if both || ax_ax {
                    return_states.push(payload);
                    parents.push(a_parent);
                } else {
                    let merged_parent = Self::merge(
                        a_parent.unwrap(),
                        b_parent.unwrap(),
                        root_is_wildcard,
                        merge_cache,
                    );
                    return_states.push(payload);
                    parents.push(Some(merged_parent));
                }
                i += 1;
                j += 1;
            } else if a.get_return_state(i) < b.get_return_state(j) {
                return_states.push(a.get_return_state(i));
                parents.push(a_parent);
                i += 1;
            } else {
                return_states.push(b.get_return_state(j));
                parents.push(b_parent);
                j += 1;
            }
        }

        if i < a.length() {
            for p in i..a.length() {
                parents.push(a.get_parent(p));
                return_states.push(a.get_return_state(p));
            }
        }
        if j < b.length() {
            for p in j..b.length() {
                parents.push(b.get_parent(p));
                return_states.push(b.get_return_state(p));
            }
        }

        if parents.len() == 1 {
            merge_cache.alloc(Self::new_singleton(parents[0], return_states[0]))
        } else {
            PredictionContext::combine_common_parents(parents.as_mut_slice());
            merge_cache.alloc(Self::new_array(
                parents.into_bump_slice(),
                return_states.into_bump_slice(),
            ))
        }
    }

    fn combine_common_parents(parents: &mut [Option<&'ephemeral PredictionContext<'ephemeral>>]) {
        let mut uniq_parents = HashMap::with_hasher(NoopHasherBuilder {});
        for parent in parents.iter() {
            uniq_parents.entry(*parent).or_insert_with(|| *parent);
        }

        parents.iter_mut().for_each(|parent| {
            *parent = *uniq_parents.get(parent).unwrap();
        });
    }

    pub fn from_rule_context<'input, 'arena, Node>(
        atn: &ATN,
        outer_context: &'arena Node,
        arena: &'ephemeral bumpalo::Bump,
    ) -> &'ephemeral PredictionContext<'ephemeral>
    where
        'input: 'arena,
        Node: RuleNode<'input, 'arena>,
    {
        if outer_context.get_parent().is_none() || outer_context.get_rule_context().is_empty()
        /*ptr::eq(outer_context, empty_ctx().as_ref())*/
        {
            return &EMPTY_PREDICTION_CONTEXT;
        }

        let parent =
            PredictionContext::from_rule_context(atn, outer_context.get_parent().unwrap(), arena);

        let transition = atn
            .get_state(outer_context.get_rule_context().get_invoking_state())
            .get_transitions()
            .first()
            .unwrap()
            .try_as::<RuleTransition>()
            .unwrap();

        arena.alloc(PredictionContext::new_singleton(
            Some(parent),
            transition.follow_state,
        ))
    }
}

#[derive(Clone, Debug)]
pub struct PredictionContextOwned(Pin<Arc<PredictionContextInner>>);

#[derive(Debug)]
struct PredictionContextInner {
    #[allow(dead_code)]
    states_store: Option<Pin<Box<[ATNStateRef]>>>,
    #[allow(dead_code)]
    parents_store: Option<Pin<Box<[Option<&'static PredictionContext<'static>>]>>>,
    context: PredictionContext<'static>,
}

impl From<PredictionContextInner> for PredictionContextOwned {
    fn from(value: PredictionContextInner) -> Self {
        PredictionContextOwned(Arc::pin(value))
    }
}

impl<'a> Borrow<PredictionContext<'a>> for PredictionContextOwned {
    fn borrow(&self) -> &PredictionContext<'a> {
        &self.0.context
    }
}

impl PartialEq for PredictionContextOwned {
    fn eq(&self, other: &Self) -> bool {
        self.0.context == other.0.context
    }
}

impl Eq for PredictionContextOwned {}

impl Hash for PredictionContextOwned {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.0.context.hash(state)
    }
}

impl PredictionContextOwned {
    pub(crate) fn get_static_ref(&self) -> &'static PredictionContext<'static> {
        unsafe { &*(&self.0.context as *const PredictionContext) }
    }
}

#[derive(Debug)]
pub struct PredictionContextCache {
    cache: RwLock<HashSet<PredictionContextOwned, NoopHasherBuilder>>,
}

impl PredictionContextCache {
    #[doc(hidden)]
    pub fn new() -> PredictionContextCache {
        PredictionContextCache {
            cache: RwLock::new(HashSet::with_hasher(NoopHasherBuilder {})),
        }
    }

    #[doc(hidden)]
    pub fn get_shared_context<'a>(
        &self,
        context: &'a PredictionContext<'a>,
    ) -> PredictionContextOwned {
        // if context.is_empty() {
        //     return context;
        // }

        if let Some(cached) = self
            .cache
            .read()
            .expect("PredictionContextCache lock poisoned")
            .get(context)
        {
            return cached.clone();
        }

        let inner = match context {
            PredictionContext::Singleton(singleton) => PredictionContextInner {
                states_store: None,
                parents_store: None,
                context: PredictionContext::Singleton(SingletonPredictionContext {
                    cached_hash: singleton.cached_hash,
                    parent_ctx: singleton
                        .parent_ctx
                        .map(|x| self.get_shared_context(x).get_static_ref()),
                    return_state: singleton.return_state,
                }),
            },
            PredictionContext::Array(array) => {
                let parents_store: Box<[_]> = array
                    .parents
                    .iter()
                    .map(|x| x.map(|p| self.get_shared_context(p).get_static_ref()))
                    .collect();
                let parents = unsafe {
                    &*(&*parents_store as *const [Option<&'static PredictionContext<'static>>])
                };
                let states_store = array.return_states.to_vec().into_boxed_slice();
                let return_states = unsafe { &*(&*states_store as *const [ATNStateRef]) };
                PredictionContextInner {
                    states_store: Some(Pin::new(states_store)),
                    parents_store: Some(Pin::new(parents_store)),
                    context: PredictionContext::Array(ArrayPredictionContext {
                        cached_hash: array.cached_hash,
                        parents,
                        return_states,
                    }),
                }
            }
        };
        let owned = PredictionContextOwned::from(inner);

        // {
        //     let mut cache = self
        //         .cache
        //         .write()
        //         .expect("PredictionContextCache lock poisoned");

        //     if let Some(cached) = cache.get(&owned) {
        //         return cached.clone();
        //     }
        //     cache.insert(owned.clone());
        // }
        let is_new = self
            .cache
            .write()
            .expect("PredictionContextCache lock poisoned")
            .insert(owned.clone());
        assert!(is_new, "PredictionContextCache invariant violated");

        owned
    }

    #[doc(hidden)]
    pub fn length(&self) -> usize {
        self.cache.read().unwrap().len()
    }
}

#[derive(Default)]
pub struct NoopHasher(u64);

impl Hasher for NoopHasher {
    fn finish(&self) -> u64 {
        self.0
    }

    fn write(&mut self, _bytes: &[u8]) {
        panic!("NoopHasher does not support write");
    }

    fn write_u8(&mut self, i: u8) {
        self.0 = i as u64;
    }

    fn write_u16(&mut self, i: u16) {
        self.0 = i as u64;
    }

    fn write_u32(&mut self, i: u32) {
        self.0 = i as u64;
    }

    fn write_u64(&mut self, i: u64) {
        self.0 = i;
    }

    fn write_usize(&mut self, i: usize) {
        self.0 = i as u64;
    }
}

#[derive(Default)]
pub struct NoopHasherBuilder {}

impl BuildHasher for NoopHasherBuilder {
    type Hasher = NoopHasher;

    fn build_hasher(&self) -> Self::Hasher {
        NoopHasher::default()
    }
}

// #[cfg(test)]
// mod test;
