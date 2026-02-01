use std::collections::HashMap;
use std::fmt::{Display, Error, Formatter};
use std::hash::{BuildHasher, Hash, Hasher};
use std::ops::Deref;

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
pub enum PredictionContext {
    Singleton(SingletonPredictionContext),
    Array(ArrayPredictionContext),
}

impl PartialEq for PredictionContext {
    fn eq(&self, other: &Self) -> bool {
        match (self, other) {
            (Array(s), Array(o)) => *s == *o,
            (Singleton(s), Singleton(o)) => *s == *o,
            _ => false,
        }
    }
}

#[derive(Eq, Clone, Debug)]
pub struct SingletonPredictionContext {
    cached_hash: u32,
    return_state: ATNStateRef,
    parent_ctx: Option<Arc<PredictionContext>>,
}

impl PartialEq for SingletonPredictionContext {
    #[inline(always)]
    fn eq(&self, other: &Self) -> bool {
        self.cached_hash == other.cached_hash
            && self.return_state == other.return_state
            && opt_eq((&self.parent_ctx, &other.parent_ctx))
    }
}

impl SingletonPredictionContext {
    #[inline(always)]
    fn is_empty(&self) -> bool {
        self.return_state == ATNStateRef::invalid() && self.parent_ctx.is_none()
    }
}

#[derive(Eq, Debug)]
pub struct ArrayPredictionContext {
    cached_hash: u32,
    return_states: Box<[ATNStateRef]>,
    parents: Box<[Option<Arc<PredictionContext>>]>,
}

impl PartialEq for ArrayPredictionContext {
    #[inline(always)]
    fn eq(&self, other: &Self) -> bool {
        self.cached_hash == other.cached_hash
            && self.return_states == other.return_states
            && self.parents.iter().zip(other.parents.iter()).all(opt_eq)
    }
}

#[inline(always)]
fn opt_eq(
    arg: (
        &Option<Arc<PredictionContext>>,
        &Option<Arc<PredictionContext>>,
    ),
) -> bool {
    match arg {
        (Some(s), Some(o)) => Arc::ptr_eq(s, o) || *s == *o,
        (None, None) => true,
        _ => false,
    }
}

impl Display for PredictionContext {
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

impl Hash for PredictionContext {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_u32(self.hash_code())
    }
}

pub static EMPTY_PREDICTION_CONTEXT: LazyLock<Arc<PredictionContext>> =
    LazyLock::new(|| PredictionContext::new_empty().into());

impl PredictionContext {
    pub fn new_array(
        parents: Box<[Option<Arc<PredictionContext>>]>,
        return_states: Box<[ATNStateRef]>,
    ) -> PredictionContext {
        PredictionContext::Array(ArrayPredictionContext {
            cached_hash: 0,
            parents,
            return_states,
        })
    }

    pub fn new_singleton(
        parent_ctx: Option<Arc<PredictionContext>>,
        return_state: ATNStateRef,
    ) -> PredictionContext {
        PredictionContext::Singleton(SingletonPredictionContext {
            cached_hash: 0,
            parent_ctx,
            return_state,
        })
        .modify_with(|x| x.calc_hash())
    }

    pub fn new_empty() -> PredictionContext {
        let mut ctx = PredictionContext::Singleton(SingletonPredictionContext {
            cached_hash: 0,
            parent_ctx: None,
            return_state: ATNStateRef::invalid(),
        });
        ctx.calc_hash();
        ctx
    }

    pub fn calc_hash(&mut self) {
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
            | PredictionContext::Array(ArrayPredictionContext { cached_hash, .. })
//            | PredictionContext::Empty { cached_hash, .. }
            => *cached_hash = hash,
        };
    }

    pub fn get_parent(&self, index: usize) -> Option<&Arc<PredictionContext>> {
        match self {
            PredictionContext::Singleton(singleton) => {
                //                assert_eq!(index, 0);
                singleton.parent_ctx.as_ref()
            }
            PredictionContext::Array(array) => array.parents[index].as_ref(),
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

    #[inline(always)]
    pub fn alloc(mut self) -> Arc<PredictionContext> {
        self.calc_hash();
        Arc::new(self)
    }

    pub(crate) fn merge(
        a: &Arc<PredictionContext>,
        b: &Arc<PredictionContext>,
        root_is_wildcard: bool,
        cache: &mut MergeCache,
        //                 eq_hash:&mut HashSet<(*const PredictionContext,*const PredictionContext)>
    ) -> Arc<PredictionContext> {
        if Arc::ptr_eq(a, b) || **a == **b {
            return a.clone();
        }

        let key = MergeKey::new(a.clone(), b.clone());
        if let Some(prev) = cache.get(&key).or_else(|| cache.get(&key.reverse())) {
            return prev.clone();
        }

        let r = match (a.deref(), b.deref()) {
            (PredictionContext::Singleton(sa), PredictionContext::Singleton(sb)) => {
                //                println!("single result = {}",result);
                Self::merge_singletons(sa, sb, root_is_wildcard, cache)
            }
            (sa, sb) => {
                if root_is_wildcard {
                    if sa.is_empty() {
                        return EMPTY_PREDICTION_CONTEXT.clone();
                    }
                    if sb.is_empty() {
                        return EMPTY_PREDICTION_CONTEXT.clone();
                    }
                }

                let mut result = Self::merge_arrays(sa, sb, root_is_wildcard, cache);
                result.calc_hash();

                if result == *sa {
                    a.clone()
                } else if result == *sb {
                    b.clone()
                } else {
                    result.into()
                }
            }
        };
        assert_ne!(r.hash_code(), 0);

        //            cache.entry(a.clone()).or_insert_with(||HashMap::new())
        //                .insert(b.clone(),r.clone());
        cache.insert(MergeKey::new(a.clone(), b.clone()), r.clone());

        r
    }

    fn merge_singletons(
        a: &SingletonPredictionContext,
        b: &SingletonPredictionContext,
        root_is_wildcard: bool,
        merge_cache: &mut MergeCache,
    ) -> Arc<PredictionContext> {
        Self::merge_root(a, b, root_is_wildcard).unwrap_or_else(|| {
            if a.return_state == b.return_state {
                let parent = Self::merge(
                    a.parent_ctx.as_ref().unwrap(),
                    b.parent_ctx.as_ref().unwrap(),
                    root_is_wildcard,
                    merge_cache,
                );
                if Arc::ptr_eq(&parent, a.parent_ctx.as_ref().unwrap()) {
                    Singleton(a.clone())
                } else if Arc::ptr_eq(&parent, b.parent_ctx.as_ref().unwrap()) {
                    Singleton(b.clone())
                } else {
                    Self::new_singleton(Some(parent), a.return_state)
                }
            } else {
                let parents = if a.parent_ctx == b.parent_ctx {
                    Box::new([a.parent_ctx.clone(), a.parent_ctx.clone()])
                } else {
                    Box::new([a.parent_ctx.clone(), b.parent_ctx.clone()])
                };
                let mut result = ArrayPredictionContext {
                    cached_hash: 0,
                    parents,
                    return_states: Box::new([a.return_state, b.return_state]),
                };
                // if !result.return_states.is_sorted()
                if !result.return_states.windows(2).all(|x| x[0] <= x[1]) {
                    result.parents.swap(0, 1);
                    result.return_states.swap(0, 1);
                }
                Array(result)
            }
            .alloc()
        })
    }

    fn merge_root(
        a: &SingletonPredictionContext,
        b: &SingletonPredictionContext,
        root_is_wildcard: bool,
    ) -> Option<Arc<PredictionContext>> {
        if root_is_wildcard {
            if a.is_empty() || b.is_empty() {
                return Some(EMPTY_PREDICTION_CONTEXT.clone());
            }
        } else {
            if a.is_empty() && b.is_empty() {
                return Some(EMPTY_PREDICTION_CONTEXT.clone());
            }
            if a.is_empty() {
                return Some(
                    Self::new_array(
                        Box::new([b.parent_ctx.clone(), None]),
                        Box::new([b.return_state, ATNStateRef::invalid()]),
                    )
                    .alloc(),
                );
            }
            if b.is_empty() {
                return Some(
                    Self::new_array(
                        Box::new([a.parent_ctx.clone(), None]),
                        Box::new([a.return_state, ATNStateRef::invalid()]),
                    )
                    .alloc(),
                );
            }
        }

        None
    }

    fn merge_arrays(
        a: &PredictionContext,
        b: &PredictionContext,
        root_is_wildcard: bool,
        merge_cache: &mut MergeCache,
    ) -> PredictionContext {
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
                    parents.push(a_parent.cloned());
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
                parents.push(a_parent.cloned());
                i += 1;
            } else {
                return_states.push(b.get_return_state(j));
                parents.push(b_parent.cloned());
                j += 1;
            }
        }

        if i < a.length() {
            for p in i..a.length() {
                parents.push(a.get_parent(p).cloned());
                return_states.push(a.get_return_state(p));
            }
        }
        if j < b.length() {
            for p in j..b.length() {
                parents.push(b.get_parent(p).cloned());
                return_states.push(b.get_return_state(p));
            }
        }

        if parents.len() < a.length() + b.length() {
            if parents.len() == 1 {
                Self::new_singleton(parents[0].clone(), return_states[0]);
            }
        }

        PredictionContext::combine_common_parents(&mut parents);
        let merged = ArrayPredictionContext {
            cached_hash: 0,
            parents: parents.into_iter().collect(),
            return_states: return_states.into_iter().collect(),
        };
        Array(merged)
    }

    fn combine_common_parents(parents: &mut [Option<Arc<PredictionContext>>]) {
        let mut uniq_parents = HashMap::with_hasher(NoopHasherBuilder {});
        for p in 0..parents.len() {
            let parent = parents[p].as_ref().cloned();
            if !uniq_parents.contains_key(&parent) {
                uniq_parents.insert(parent.clone(), parent.clone());
            }
        }

        parents.iter_mut().for_each(|parent| {
            *parent = (*uniq_parents.get(parent).unwrap()).clone();
        });
    }

    pub fn from_rule_context<'input, 'arena, Node>(
        atn: &ATN,
        outer_context: &'arena Node,
    ) -> Arc<PredictionContext>
    where
        'input: 'arena,
        Node: RuleNode<'input, 'arena>,
    {
        if outer_context.get_parent().is_none() || outer_context.get_rule_context().is_empty()
        /*ptr::eq(outer_context, empty_ctx().as_ref())*/
        {
            return EMPTY_PREDICTION_CONTEXT.clone();
        }

        let parent = PredictionContext::from_rule_context(atn, outer_context.get_parent().unwrap());

        let transition = atn
            .get_state(outer_context.get_rule_context().get_invoking_state())
            .get_transitions()
            .first()
            .unwrap()
            .try_as::<RuleTransition>()
            .unwrap();

        PredictionContext::new_singleton(Some(parent), transition.follow_state).alloc()
    }
}

//
//    fn get_cached_base_prediction_context(context PredictionContext, contextCache: * PredictionContextCache, visited: map[PredictionContext]PredictionContext) -> PredictionContext { unimplemented!() }

#[derive(Debug)]
pub struct PredictionContextCache {
    cache: RwLock<HashMap<Arc<PredictionContext>, Arc<PredictionContext>, NoopHasherBuilder>>,
}

impl PredictionContextCache {
    #[doc(hidden)]
    pub fn new() -> PredictionContextCache {
        PredictionContextCache {
            cache: RwLock::new(HashMap::with_hasher(NoopHasherBuilder {})),
        }
    }

    #[doc(hidden)]
    pub fn get_shared_context(
        &self,
        context: &Arc<PredictionContext>,
        visited: &mut HashMap<*const PredictionContext, Arc<PredictionContext>>,
    ) -> Arc<PredictionContext> {
        if context.is_empty() {
            return context.clone();
        }

        if let Some(old) = visited.get(&(context.deref() as *const PredictionContext)) {
            return old.clone();
        }

        if let Some(old) = self.cache.read().unwrap().get(context) {
            return old.clone();
        }
        let mut parents = Vec::with_capacity(context.length());
        let mut changed = false;
        for i in 0..parents.len() {
            let parent = self.get_shared_context(context.get_parent(i).unwrap(), visited);
            if changed || &parent != context.get_parent(i).unwrap() {
                if !changed {
                    for j in 0..i {
                        parents.push(context.get_parent(j).cloned())
                    }
                    changed = true;
                }
                parents.push(Some(parent.clone()))
            }
        }
        if !changed {
            self.cache
                .write()
                .unwrap()
                .insert(context.clone(), context.clone());
            visited.insert(context.deref(), context.clone());
            return context.clone();
        }

        let updated = if parents.is_empty() {
            return EMPTY_PREDICTION_CONTEXT.clone();
        } else if parents.len() == 1 {
            PredictionContext::new_singleton(parents[0].clone(), context.get_return_state(0))
        } else if let Array(array) = context.deref() {
            PredictionContext::new_array(parents.into(), array.return_states.clone())
        } else {
            unreachable!()
        };

        let updated = Arc::new(updated);
        self.cache
            .write()
            .unwrap()
            .insert(updated.clone(), updated.clone());
        visited.insert(context.deref(), updated.clone());
        visited.insert(updated.deref(), updated.clone());

        updated
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
