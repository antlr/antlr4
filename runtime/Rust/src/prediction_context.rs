use std::fmt::{Display, Error, Formatter};
use std::hash::{BuildHasher, Hash, Hasher};
use std::ops::Deref;
use std::ptr::NonNull;
use std::sync::atomic::{AtomicUsize, Ordering};
use std::sync::{LazyLock, RwLock};

use fxhash::FxHasher32;
use hashbrown::HashSet;

use crate::atn::ATN;
use crate::atn_state::ATNStateRef;
use crate::dfa::ScopeExt;
use crate::parser_atn_simulator::{MergeCache, MergeKey};

use crate::prediction_context::PredictionContext::{Array, Singleton};

use crate::token::Token;
use crate::transition::RuleTransition;
use crate::tree::{NodeKindType, Tree as _, TreeNode};

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

#[derive(Clone, Debug, PartialEq, Eq)]
pub struct SingletonPredictionContext<'ephemeral> {
    cached_hash: u32,
    return_state: ATNStateRef,
    parent_ctx: Option<PredictionContextRef<'ephemeral>>,
}

impl SingletonPredictionContext<'_> {
    #[inline(always)]
    fn is_empty(&self) -> bool {
        self.return_state == ATNStateRef::invalid() && self.parent_ctx.is_none()
    }
}

#[derive(Clone, Debug, PartialEq, Eq)]
pub struct ArrayPredictionContext<'ephemeral> {
    cached_hash: u32,
    return_states: &'ephemeral [ATNStateRef],
    parents: &'ephemeral [Option<PredictionContextRef<'ephemeral>>],
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

static EMPTY_PREDICTION_CONTEXT: LazyLock<PredictionContext<'static>> = LazyLock::new(|| {
    PredictionContext::Singleton(SingletonPredictionContext {
        cached_hash: 0,
        parent_ctx: None,
        return_state: ATNStateRef::invalid(),
    })
    .modify_with(|x| x.calc_hash())
});

impl<'ephemeral> PredictionContext<'ephemeral> {
    pub fn new_singleton(
        parent_ctx: Option<PredictionContextRef<'ephemeral>>,
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
        parents: &'ephemeral [Option<PredictionContextRef<'ephemeral>>],
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
        let mut hasher = FxHasher32::default();
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

    pub fn get_parent(&self, index: usize) -> Option<PredictionContextRef<'ephemeral>> {
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
            Self::Singleton { .. } => 1,
            Self::Array(ArrayPredictionContext { return_states, .. }) => return_states.len(),
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
        a: PredictionContextRef<'ephemeral>,
        b: PredictionContextRef<'ephemeral>,
        root_is_wildcard: bool,
        cache: &mut MergeCache<'ephemeral>,
    ) -> PredictionContextRef<'ephemeral> {
        if a == b {
            return a;
        }

        if let Some(prev) = cache.get(&MergeKey::new(a, b)) {
            return prev;
        }

        let r = match (a.as_ref(), b.as_ref()) {
            (PredictionContext::Singleton(sa), PredictionContext::Singleton(sb)) => {
                //                println!("single result = {}",result);
                Self::merge_singletons(sa, sb, root_is_wildcard, cache)
            }
            (sa, sb) => {
                if root_is_wildcard {
                    if sa.is_empty() {
                        return PredictionContextRef::new_empty();
                    }
                    if sb.is_empty() {
                        return PredictionContextRef::new_empty();
                    }
                }

                let result = Self::merge_arrays(a, b, root_is_wildcard, cache);

                if result == a {
                    a
                } else if result == b {
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
    ) -> PredictionContextRef<'ephemeral> {
        Self::merge_root(a, b, root_is_wildcard, merge_cache).unwrap_or_else(|| {
            let res = if a.return_state == b.return_state {
                let parent = Self::merge(
                    *a.parent_ctx.as_ref().unwrap(),
                    *b.parent_ctx.as_ref().unwrap(),
                    root_is_wildcard,
                    merge_cache,
                );
                if parent.ptr_eq(a.parent_ctx.as_ref().unwrap()) {
                    Singleton(a.clone())
                } else if parent.ptr_eq(b.parent_ctx.as_ref().unwrap()) {
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
                let return_states = merge_cache
                    .scratch()
                    .alloc([a.return_state, b.return_state]);

                if return_states[0] > return_states[1] {
                    parents.swap(0, 1);
                    return_states.swap(0, 1);
                }

                Self::new_array(parents, return_states)
            };
            merge_cache.alloc(res).into()
        })
    }

    fn merge_root(
        a: &'ephemeral SingletonPredictionContext<'ephemeral>,
        b: &'ephemeral SingletonPredictionContext<'ephemeral>,
        root_is_wildcard: bool,
        merge_cache: &mut MergeCache<'ephemeral>,
    ) -> Option<PredictionContextRef<'ephemeral>> {
        if root_is_wildcard {
            if a.is_empty() || b.is_empty() {
                return Some(PredictionContextRef::new_empty());
            }
        } else {
            if a.is_empty() && b.is_empty() {
                return Some(PredictionContextRef::new_empty());
            }
            if a.is_empty() {
                return Some(
                    merge_cache
                        .alloc(Self::new_array(
                            merge_cache
                                .alloc([b.parent_ctx, Some(PredictionContextRef::new_empty())]),
                            merge_cache.alloc([b.return_state, ATNStateRef::invalid()]),
                        ))
                        .into(),
                );
            }
            if b.is_empty() {
                return Some(
                    merge_cache
                        .alloc(Self::new_array(
                            merge_cache
                                .alloc([a.parent_ctx, Some(PredictionContextRef::new_empty())]),
                            merge_cache.alloc([a.return_state, ATNStateRef::invalid()]),
                        ))
                        .into(),
                );
            }
        }

        None
    }

    fn merge_arrays(
        a: PredictionContextRef<'ephemeral>,
        b: PredictionContextRef<'ephemeral>,
        root_is_wildcard: bool,
        merge_cache: &mut MergeCache<'ephemeral>,
    ) -> PredictionContextRef<'ephemeral> {
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
            merge_cache
                .alloc(Self::new_singleton(parents[0], return_states[0]))
                .into()
        } else {
            PredictionContext::combine_common_parents(parents.as_mut_slice(), merge_cache);
            merge_cache
                .alloc(Self::new_array(
                    parents.into_bump_slice(),
                    return_states.into_bump_slice(),
                ))
                .into()
        }
    }

    fn combine_common_parents(
        parents: &mut [Option<PredictionContextRef<'ephemeral>>],
        merge_cache: &mut MergeCache<'ephemeral>,
    ) {
        let mut uniq_parents =
            hashbrown::HashMap::with_hasher_in(NoopHasherBuilder {}, merge_cache.scratch());
        for parent in parents.iter() {
            uniq_parents.entry(*parent).or_insert_with(|| *parent);
        }

        parents.iter_mut().for_each(|parent| {
            *parent = *uniq_parents.get(parent).unwrap();
        });
    }

    pub fn from_rule_context<'input, 'arena, Node, Tok>(
        atn: &ATN,
        outer_context: &'arena TreeNode<'input, 'arena, Node, Tok>,
        arena: &'ephemeral bumpalo::Bump,
    ) -> PredictionContextRef<'ephemeral>
    where
        'input: 'arena,
        Node: NodeKindType<'arena, Tok>,
        Tok: Token + 'input,
    {
        if outer_context.get_parent().is_none() || outer_context.get_rule_context().is_empty()
        /*ptr::eq(outer_context, empty_ctx().as_ref())*/
        {
            return PredictionContextRef::new_empty();
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

        arena
            .alloc(PredictionContext::new_singleton(
                Some(parent),
                transition.follow_state,
            ))
            .into()
    }
}

#[derive(Clone, Copy, PartialEq, Eq, Hash)]
pub enum RefType {
    Interned,
    Adhoc,
}

#[derive(Clone, Copy, Eq, Debug)]
pub struct PredictionContextRef<'ephemeral>(NonNull<PredictionContext<'ephemeral>>);

impl<'ephemeral> PredictionContextRef<'ephemeral> {
    pub fn new_empty() -> Self {
        PredictionContextRef(NonNull::from(
            &EMPTY_PREDICTION_CONTEXT as &'ephemeral PredictionContext<'ephemeral>,
        ))
    }

    /// Creates an ad-hoc [PredictionContextRef] from the given
    /// [PredictionContext] reference. The lifetime of the reference itself is
    /// discarded and forgotten.
    ///
    /// # Safety
    /// - If 't: 'ephemeral, then this function is safe. This is the case for
    ///   the `From<&PredictionContext>` impls, which are safe wrappers around
    ///   this function.
    /// - Otherwise, the caller must ensure that the returned
    ///   [PredictionContextRef] instance does not outlive 't, without the aid
    ///   of the borrow checker
    unsafe fn new_adhoc<'t>(context: &'t PredictionContext<'ephemeral>) -> Self {
        // We set the least-significant bit to 1 to indicate that this is an
        // adhoc (non-interned) context:
        let ptr = (context as *const PredictionContext<'ephemeral> as usize) | 1;
        PredictionContextRef(NonNull::new(ptr as *mut PredictionContext<'ephemeral>).unwrap())
    }

    fn new_interned(context: &'ephemeral PredictionContext<'ephemeral>) -> Self {
        PredictionContextRef(NonNull::from(context))
    }

    #[inline]
    pub fn as_ref(&self) -> &'ephemeral PredictionContext<'ephemeral> {
        // Hand-rolled niche optimization: we tuck the RefType into the
        // pointer's least-significant bit (and pray to god that we don't
        // encounter insane platforms where PredictionContext is not aligned to
        // at least even addresses). To dereference, we mask out the bit and
        // then cast back to a valid pointer:
        let ptr = (self.0.as_ptr() as usize) & !1;
        unsafe { &*(ptr as *const PredictionContext<'ephemeral>) }
    }

    #[inline(always)]
    pub fn ref_type(&self) -> RefType {
        if (self.0.as_ptr() as usize) & 1 == 0 {
            RefType::Interned
        } else {
            RefType::Adhoc
        }
    }

    #[inline(always)]
    pub fn is_interned(&self) -> bool {
        self.ref_type() == RefType::Interned
    }

    #[inline(always)]
    pub fn ptr_eq(&self, other: &Self) -> bool {
        self.0 == other.0
    }
}

impl<'ephemeral> Default for PredictionContextRef<'ephemeral> {
    fn default() -> Self {
        Self::new_empty()
    }
}

impl<'ephemeral> Deref for PredictionContextRef<'ephemeral> {
    type Target = PredictionContext<'ephemeral>;

    fn deref(&self) -> &Self::Target {
        self.as_ref()
    }
}

impl Hash for PredictionContextRef<'_> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.as_ref().hash(state);
    }
}

impl Display for PredictionContextRef<'_> {
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        self.as_ref().fmt(f)
    }
}

impl PartialEq for PredictionContextRef<'_> {
    fn eq(&self, other: &Self) -> bool {
        if self.0 == other.0 {
            return true;
        }
        // If both contexts are interned, then pointer equality is sufficient:
        if self.is_interned() && other.is_interned() {
            return false;
        }
        // Otherwise, we have to fall back to (slow) value equality:
        self.as_ref() == other.as_ref()
    }
}

unsafe impl Send for PredictionContextRef<'_> {}
unsafe impl Sync for PredictionContextRef<'_> {}

impl<'ephemeral> From<&'ephemeral PredictionContext<'ephemeral>>
    for PredictionContextRef<'ephemeral>
{
    fn from(context: &'ephemeral PredictionContext<'ephemeral>) -> Self {
        // SAFETY: 't: 'ephemeral (they are the same as declared in the function
        // signature)
        unsafe { Self::new_adhoc(context) }
    }
}

impl<'ephemeral> From<&'ephemeral mut PredictionContext<'ephemeral>>
    for PredictionContextRef<'ephemeral>
{
    fn from(context: &'ephemeral mut PredictionContext<'ephemeral>) -> Self {
        // SAFETY: 't: 'ephemeral (they are the same as declared in the function
        // signature)
        unsafe { Self::new_adhoc(context) }
    }
}

struct PredictionContextCacheInner<'sim> {
    cache: HashSet<PredictionContextRef<'sim>, NoopHasherBuilder>,
    arena: bumpalo::Bump,
}

impl PredictionContextCacheInner<'static> {
    pub fn new() -> Self {
        PredictionContextCacheInner {
            cache: HashSet::with_hasher(NoopHasherBuilder {}),
            arena: bumpalo::Bump::new(),
        }
    }
}

impl<'sim> PredictionContextCacheInner<'sim> {
    pub fn allocated_bytes(&self) -> usize {
        self.arena.allocated_bytes() + self.cache.allocation_size()
    }

    pub fn alloc<T>(&mut self, value: T) -> &'sim mut T {
        // SAFETY: the returned reference is allocated from the 'sim arena, so it must be valid for 'sim:
        unsafe { std::mem::transmute::<&mut T, &'sim mut T>(self.arena.alloc(value)) }
    }

    pub fn alloc_slice_copy<T: Copy>(&mut self, slice: &[T]) -> &'sim mut [T] {
        // SAFETY: the returned slice is allocated from the 'sim arena, so it must be valid for 'sim:
        unsafe {
            std::mem::transmute::<&mut [T], &'sim mut [T]>(self.arena.alloc_slice_copy(slice))
        }
    }

    pub fn alloc_slice_fill_default<T: Default>(&mut self, len: usize) -> &'sim mut [T] {
        // SAFETY: the returned slice is allocated from the 'sim arena, so it must be valid for 'sim:
        unsafe {
            std::mem::transmute::<&mut [T], &'sim mut [T]>(self.arena.alloc_slice_fill_default(len))
        }
    }

    pub fn get(&self, context: &PredictionContextRef<'_>) -> Option<PredictionContextRef<'sim>> {
        // SAFETY: the cache is backed by the 'sim arena, so any reference
        // returned from it must be valid for 'sim:
        self.cache.get(context).map(|ctx| unsafe {
            std::mem::transmute::<PredictionContextRef<'_>, PredictionContextRef<'sim>>(*ctx)
        })
    }

    fn get_or_insert(&mut self, context: PredictionContext<'sim>) -> PredictionContextRef<'sim> {
        if let Some(cached) = self
            .cache
            // SAFETY: the temporary PredictionContextRef is immediately
            // discarded after the lookup, so it cannot be used outside the
            // scope of this function:
            .get(unsafe { &PredictionContextRef::new_adhoc(&context) })
        {
            return *cached;
        }

        let context_ref = PredictionContextRef::new_interned(self.alloc(context));
        let is_new = self.cache.insert(context_ref);
        assert!(is_new);
        context_ref
    }
}

pub struct PredictionContextCache<'sim>(RwLock<PredictionContextCacheInner<'sim>>, AtomicUsize);

impl PredictionContextCache<'static> {
    #[doc(hidden)]
    pub fn new() -> Self {
        PredictionContextCache(
            RwLock::new(PredictionContextCacheInner::new()),
            AtomicUsize::new(0),
        )
    }
}

impl<'sim> PredictionContextCache<'sim> {
    #[doc(hidden)]
    pub fn get_shared_context<'a>(
        &self,
        context: &PredictionContextRef<'a>,
    ) -> PredictionContextRef<'sim> {
        if context.is_interned() {
            // SAFETY: the context is already interned, so it must have been
            // allocated from the 'sim arena and thus be valid for 'sim:
            return unsafe {
                std::mem::transmute::<PredictionContextRef<'a>, PredictionContextRef<'sim>>(
                    *context,
                )
            };
        } else if context.is_empty() {
            return PredictionContextRef::new_empty();
        }

        if let Some(cached) = self
            .0
            .read()
            .expect("PredictionContextCache lock poisoned")
            .get(context)
        {
            return cached;
        }

        let shared: PredictionContext<'sim> = match context.as_ref() {
            PredictionContext::Singleton(singleton) => {
                PredictionContext::Singleton(SingletonPredictionContext {
                    cached_hash: singleton.cached_hash,
                    parent_ctx: singleton.parent_ctx.map(|x| self.get_shared_context(&x)),
                    return_state: singleton.return_state,
                })
            }
            PredictionContext::Array(array) => {
                let (return_states, parents) = {
                    let mut locked = self
                        .0
                        .write()
                        .expect("PredictionContextCache lock poisoned");

                    let return_states = locked.alloc_slice_copy(array.return_states);
                    let parents: &'sim mut [Option<PredictionContextRef<'sim>>] =
                        locked.alloc_slice_fill_default(array.parents.len());
                    (return_states, parents)
                };
                parents
                    .iter_mut()
                    .zip(array.parents.iter())
                    .for_each(|(parent, original)| {
                        *parent = original.map(|x| self.get_shared_context(&x))
                    });

                PredictionContext::Array(ArrayPredictionContext {
                    cached_hash: array.cached_hash,
                    parents,
                    return_states,
                })
            }
        };

        let (res, new_size) = {
            let mut inner_locked = self
                .0
                .write()
                .expect("PredictionContextCache lock poisoned");
            let res = inner_locked.get_or_insert(shared);
            let new_size = inner_locked.allocated_bytes();
            // Push the updated memory usage up to the cache, outside of the lock:
            (res, new_size)
        };
        self.1.store(new_size, Ordering::Relaxed);
        res
    }

    pub fn allocated_bytes(&self) -> usize {
        self.1.load(Ordering::Relaxed)
    }
}

unsafe impl Send for PredictionContextCache<'_> {}
unsafe impl Sync for PredictionContextCache<'_> {}

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
