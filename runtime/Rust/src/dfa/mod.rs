use std::convert::TryFrom;
use std::hash::Hasher;
use std::mem::{ManuallyDrop, MaybeUninit};
use std::ops::Deref;
use std::pin::Pin;
use std::ptr::NonNull;
use std::sync::atomic::{AtomicI32, AtomicPtr, AtomicUsize, Ordering};
use std::sync::{Arc, Mutex, OnceLock};

use crate::arena::{is_ref_in_arena, is_slice_in_arena};
use crate::atn::ATN;
use crate::atn_config_set::{
    ATNConfigSet, ConfigSet, LexerATNConfigSet, MutableConfigSet, MutableLexerATNConfigSet,
};
use crate::atn_simulator::IATNSimulator;
use crate::atn_state::{ATNStateRef, ATNStateType, StarLoopEntryState};
use crate::dfa::dfa_state::{LexerDFAState, ParserDFAState};
use crate::lexer_action::{LexerAction, LexerIndexedCustomAction};
use crate::lexer_action_executor::LexerActionExecutor;
use crate::prediction_context::NoopHasherBuilder;
use crate::semantic_context::SemanticContext;
use crate::vocabulary::Vocabulary;

mod dfa_serializer;
mod dfa_state;

use bit_set::BitSet;
pub use dfa_serializer::DFASerializer;
pub use dfa_state::DFAState;
pub use dfa_state::PredPrediction;
pub use dfa_state::ProposedDFAState;
use hashbrown::{Equivalent, HashSet};

pub type LexerDFA<'sim> = DFA<'sim, LexerATNConfigSet<'sim>>;
pub type ParserDFA<'sim> = DFA<'sim, ATNConfigSet<'sim>>;

///Helper trait for scope management and temporary values not living long enough
pub(crate) trait ScopeExt: Sized {
    fn run<T, F: FnOnce(&Self) -> T>(&self, f: F) -> T {
        f(self)
    }

    //apply
    fn modify_with<F: FnOnce(&mut Self)>(mut self, f: F) -> Self {
        f(&mut self);
        self
    }
    //apply_inplace
    fn apply<F: FnOnce(&mut Self)>(&mut self, f: F) -> &mut Self {
        f(self);
        self
    }
}

impl<Any: Sized> ScopeExt for Any {}

// 16 chunks should be enough for anyone
const CHUNK_INDEX_BIT_COUNT: u32 = 4;
const MAX_NUM_CHUNKS: u32 = 1 << CHUNK_INDEX_BIT_COUNT;
const INITIAL_CHUNK_BITS: u32 = 5;

pub struct DFA<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    /// ATN state from which this DFA creation was started from
    pub atn_start_state: ATNStateRef,

    pub decision: i32,

    /// Set of all DFA states.
    states: Mutex<DFAStateStore<'sim, CS>>,
    chunks: *const StateStoreRoots<'sim, CS>,

    /// Backing store for all edges
    edges: Arc<EdgeSetStore<'sim, CS>>,

    /// Initial DFA state
    s0: AtomicPtr<DFAState<'sim, CS>>,

    precedence_state: Option<Pin<Box<DFAState<'sim, CS>>>>,

    allocated_bytes: AtomicUsize,
    dfa_state_bytes: AtomicUsize,
}

impl<'sim, CS> DFA<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    // ---- Begin direct Java port ----
    pub fn new(
        atn_start_state: ATNStateRef,
        decision: i32,
        edge_store: Arc<EdgeSetStore<'sim, CS>>,
    ) -> DFA<'sim, CS> {
        let state_store = DFAStateStore::new();
        let chunks = &*state_store.chunks as *const StateStoreRoots<'sim, CS>;

        // SAFETY: self-reference cast
        let edge_store_ref = unsafe {
            std::mem::transmute::<&EdgeSetStore<'sim, CS>, &'sim EdgeSetStore<'sim, CS>>(
                &edge_store,
            )
        };

        let precedence_state = if is_precedence_atn_state(atn_start_state) {
            let mut precedence_state =
                DFAState::new(0, CS::new_empty(), edge_store_ref.make_edge_set(), &[]);
            precedence_state.set_accept_state(false);
            precedence_state.set_requires_full_context(false);
            // Pre-allocate edges for the precedence state:
            let _ = precedence_state.edges.as_ref();

            Some(Box::pin(precedence_state))
        } else {
            None
        };

        DFA {
            atn_start_state,
            decision,
            states: Mutex::new(state_store),
            chunks,
            edges: edge_store,
            s0: AtomicPtr::new(precedence_state.as_ref().map_or(std::ptr::null_mut(), |s| {
                &**s as *const DFAState<'sim, CS> as *mut DFAState<'sim, CS>
            })),
            precedence_state,
            allocated_bytes: AtomicUsize::new(0),
            dfa_state_bytes: AtomicUsize::new(0),
        }
    }

    #[inline]
    pub fn set_edge(
        &self,
        from_state: &'sim DFAState<'sim, CS>,
        symbol: usize,
        to_state: &'sim DFAState<'sim, CS>,
    ) {
        assert!(
            !from_state.is_error_state(),
            "cannot set edge from error state"
        );
        from_state.edges.set(symbol, to_state);
    }

    pub fn is_precedence_dfa(&self) -> bool {
        self.precedence_state.is_some()
    }

    /// Return a list of all states in this DFA, ordered by state number. (Only
    /// used by the serializer and test code, not performance-sensitive.)
    pub fn get_states(&self) -> Vec<&'sim DFAState<'sim, CS>> {
        let mut states = self
            .states
            .lock()
            .expect("StateStore lock poisoned")
            .values()
            .collect::<Vec<_>>();
        states.sort_by_key(|s| s.state_number());
        states
    }

    pub fn enumerate_edges(
        &self,
    ) -> Vec<(&'sim DFAState<'sim, CS>, usize, &'sim DFAState<'sim, CS>)> {
        self.get_states()
            .into_iter()
            .flat_map(|state| {
                state
                    .edges
                    .enumerate()
                    .into_iter()
                    .map(move |(symbol, target)| (state, symbol, target))
            })
            .map(|(state, symbol, target)| {
                let target =
                    unsafe { (*DFAStateId::new(target).deref(self.chunks)).assume_init_ref() };
                (state, symbol, target)
            })
            .collect()
    }

    pub fn to_string(&self, vocabulary: &dyn Vocabulary) -> String {
        if self.s0().is_none() {
            return String::new();
        }

        format!(
            "{}",
            DFASerializer::new(self, &|x| vocabulary
                .get_display_name(x as i32 - 1)
                .into_owned(),)
        )
    }

    pub fn to_lexer_string(&self) -> String {
        if self.s0().is_none() {
            return String::new();
        }
        format!(
            "{}",
            DFASerializer::new(self, &|x| format!(
                "'{}'",
                char::try_from(x as u32).unwrap()
            ))
        )
    }
    // ---- End direct Java port ----

    pub fn len(&self) -> usize {
        self.states.lock().expect("StateStore lock poisoned").len()
    }

    pub fn is_empty(&self) -> bool {
        self.len() == 0
    }

    #[inline]
    pub fn s0(&self) -> Option<&'sim DFAState<'sim, CS>> {
        let x = self.s0.load(Ordering::Relaxed);
        if x.is_null() {
            None
        } else {
            Some(unsafe { &*x })
        }
    }

    pub fn set_s0(&self, s: &'sim DFAState<'sim, CS>) {
        self.s0.store(
            s as *const DFAState<'sim, CS> as *mut DFAState<'sim, CS>,
            Ordering::Relaxed,
        );
    }

    // pub fn set_s0_configs<'ephemeral, ECS>(
    //     &self,
    //     configs: ECS,
    //     cache: &'sim PredictionContextCache<'sim>,
    // ) where
    //     ECS: ConfigSet<'ephemeral, FinalizedType<'sim> = CS> + 'ephemeral,
    // {
    //     let s0 = self
    //         .precedence_state
    //         .as_ref()
    //         .map(|s| &**s)
    //         .expect("Can not set s0 configs on a non-precedence DFA");

    //     let states = self.states.lock().expect("StateStore lock poisoned");
    //     let configs = configs.finalize(cache, &states);

    //     unsafe {
    //         // Note: the old configs should be an empty set, so we shouldn't be
    //         // leaking anything here.
    //         std::ptr::write(&s0.configs as *const CS as *mut CS, configs);
    //     }
    // }

    pub fn add_state<'ephemeral, ECS>(
        &self,
        proposed: ProposedDFAState<'ephemeral, ECS>,
        recog: &impl IATNSimulator<'sim, CS>,
    ) -> &'sim DFAState<'sim, CS>
    where
        'sim: 'ephemeral,
        ECS: MutableConfigSet<'ephemeral, FinalizedType<'sim> = CS> + 'ephemeral,
    {
        let mut state_store = self.states.lock().expect("StateStore lock poisoned");

        let proposed = {
            let mut proposed = proposed;
            if let Some(existing) =
                state_store.get(ProposedDFAStateKey::from_proposed(&mut proposed))
            {
                return existing;
            }
            proposed
        };

        let state = proposed.finalize(
            recog.shared_context_cache(),
            &state_store,
            self.edge_store().make_edge_set(),
        );
        let res = state_store.add(state);

        // Push the updated memory usage up to the DFA, outside of the lock:
        self.allocated_bytes
            .store(state_store.allocated_bytes(), Ordering::Relaxed);
        self.dfa_state_bytes
            .store(state_store.allocated_chunk_bytes, Ordering::Relaxed);

        res
    }

    #[inline]
    fn edge_store(&self) -> &'sim EdgeSetStore<'sim, CS> {
        // SAFETY: self-reference cast
        unsafe {
            std::mem::transmute::<&EdgeSetStore<'sim, CS>, &'sim EdgeSetStore<'sim, CS>>(
                &self.edges,
            )
        }
    }

    pub fn allocated_bytes(&self) -> usize {
        self.allocated_bytes.load(Ordering::Relaxed)
    }

    pub fn dfa_state_bytes(&self) -> usize {
        self.dfa_state_bytes.load(Ordering::Relaxed)
    }

    pub fn edge_set_bytes(&self) -> usize {
        self.edge_store().allocated_bytes()
    }
}

impl<'sim> DFA<'sim, LexerATNConfigSet<'sim>> {
    pub fn add_lexer_state<'ephemeral>(
        &self,
        proposed: ProposedDFAState<'ephemeral, MutableLexerATNConfigSet<'ephemeral>>,
        recog: &impl IATNSimulator<'sim, LexerATNConfigSet<'sim>>,
    ) -> &'sim DFAState<'sim, LexerATNConfigSet<'sim>>
    where
        'sim: 'ephemeral,
    {
        let mut state_store = self.states.lock().expect("StateStore lock poisoned");

        let proposed = {
            let mut proposed = proposed;
            if let Some(existing) =
                state_store.get(ProposedDFAStateKey::from_proposed(&mut proposed))
            {
                return existing;
            }
            proposed
        };

        let mut state = proposed.finalize(
            recog.shared_context_cache(),
            &state_store,
            self.edge_store().make_edge_set(),
        );
        let rule_index = state
            .configs()
            .get_items()
            .find(|c| c.get_state().state_type() == ATNStateType::RuleStop)
            .map(|c| {
                let rule_index = c.get_state().get_rule_index();
                (
                    recog.atn().rule_to_token_type[rule_index as usize],
                    c.get_lexer_executor(),
                )
            });
        if let Some((prediction, exec)) = rule_index {
            state.set_prediction(prediction);
            state.set_lexer_action_executor(exec);
            state.set_accept_state(true);
        }
        let res = state_store.add(state);

        // Push the updated memory usage up to the DFA, outside of the lock:
        self.allocated_bytes
            .store(state_store.allocated_bytes(), Ordering::Relaxed);
        self.dfa_state_bytes
            .store(state_store.allocated_chunk_bytes, Ordering::Relaxed);

        res
    }
}

unsafe impl<'sim, CS> Send for DFA<'sim, CS> where CS: ConfigSet<'sim> + 'sim {}
unsafe impl<'sim, CS> Sync for DFA<'sim, CS> where CS: ConfigSet<'sim> + 'sim {}

impl<'sim> DFA<'sim, ATNConfigSet<'sim>> {
    pub fn get_precedence_start_state(
        &self,
        precedence: i32,
    ) -> Option<&'sim ParserDFAState<'sim>> {
        if !self.is_precedence_dfa() {
            // FIXME: this should return ANTLRError
            panic!("dfa is supposed to be precedence here");
        }

        self.s0()
            .and_then(|state| self.get_edge(state, precedence as usize))
    }

    pub fn set_precedence_start_state(
        &self,
        precedence: i32,
        start_state: &'sim ParserDFAState<'sim>,
    ) {
        if !self.is_precedence_dfa() {
            // FIXME: this should return ANTLRError
            panic!("set_precedence_start_state called for not precedence dfa")
        }

        if precedence < 0 {
            return;
        }
        let precedence = precedence as usize;

        self.set_edge(
            self.s0()
                .expect("a precedence dfa's s0 state is never null"),
            precedence,
            start_state,
        );
    }

    #[inline]
    pub fn get_edge(
        &self,
        from_state: &'sim ParserDFAState<'sim>,
        symbol: usize,
    ) -> Option<&'sim ParserDFAState<'sim>> {
        if from_state.is_error_state() {
            return None;
        }
        let target = from_state.edges.get(symbol)?;
        if target == -1 {
            return Some(self.get_error_state());
        }
        let target = unsafe { (*DFAStateId::new(target).deref(self.chunks)).assume_init_ref() };
        Some(target)
    }

    #[inline]
    pub fn get_error_state(&self) -> &'sim DFAState<'sim, ATNConfigSet<'sim>> {
        unsafe {
            std::mem::transmute::<
                &DFAState<'static, ATNConfigSet<'_>>,
                &'sim DFAState<'sim, ATNConfigSet<'sim>>,
            >(&*dfa_state::ERROR_DFA_STATE_REF)
        }
    }
}

impl<'sim> DFA<'sim, LexerATNConfigSet<'sim>> {
    #[inline]
    pub fn get_edge(
        &self,
        from_state: &'sim LexerDFAState<'sim>,
        symbol: usize,
    ) -> Option<&'sim LexerDFAState<'sim>> {
        if from_state.is_error_state() {
            return None;
        }
        let target = from_state.edges.get(symbol)?;
        if target == -1 {
            return Some(self.get_error_state());
        }
        let target = unsafe { (*DFAStateId::new(target).deref(self.chunks)).assume_init_ref() };
        Some(target)
    }

    #[inline]
    pub fn get_error_state(&self) -> &'sim DFAState<'sim, LexerATNConfigSet<'sim>> {
        unsafe {
            std::mem::transmute::<
                &DFAState<'static, LexerATNConfigSet<'_>>,
                &'sim DFAState<'sim, LexerATNConfigSet<'sim>>,
            >(&*dfa_state::ERROR_LEXER_DFA_STATE_REF)
        }
    }
}

fn is_precedence_atn_state(atn_start_state: ATNStateRef) -> bool {
    matches!(
        atn_start_state.try_as(),
        Some(StarLoopEntryState {
            is_precedence: true,
            ..
        })
    )
}

#[derive(Clone)]
struct DFAStateKey<'sim, CS>(NonNull<DFAState<'sim, CS>>)
where
    CS: ConfigSet<'sim> + 'sim;

unsafe impl<'sim, CS> Send for DFAStateKey<'sim, CS> where CS: ConfigSet<'sim> + 'sim {}

impl<'sim, CS> DFAStateKey<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    fn from_state(entry: &'sim DFAState<'sim, CS>) -> Self {
        DFAStateKey(NonNull::from(entry))
    }

    #[inline(always)]
    pub fn as_ref(&self) -> &'sim DFAState<'sim, CS> {
        // SAFETY: `self` can only be constructed from a valid reference to a
        // DFAState backed by the DFAStateStore arena:
        unsafe { self.0.as_ref() }
    }
}

impl<'sim, CS> Deref for DFAStateKey<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    type Target = DFAState<'sim, CS>;

    fn deref(&self) -> &Self::Target {
        self.as_ref()
    }
}

impl<'sim, CS> PartialEq for DFAStateKey<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    fn eq(&self, other: &Self) -> bool {
        self.as_ref() == other.as_ref()
    }
}

impl<'sim, CS> Eq for DFAStateKey<'sim, CS> where CS: ConfigSet<'sim> + 'sim {}

impl<'sim, CS> std::hash::Hash for DFAStateKey<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    fn hash<H: Hasher>(&self, state: &mut H) {
        let configs = self.as_ref().configs();
        let hash = configs.hash_code();
        state.write_u64(hash);
    }
}

impl<'sim, CS> std::fmt::Debug for DFAStateKey<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "DFAStateKey({:p})", self.0)
    }
}

struct ProposedDFAStateKey<'scratch, MCS>(NonNull<ProposedDFAState<'scratch, MCS>>)
where
    MCS: MutableConfigSet<'scratch> + 'scratch;

impl<'scratch, MCS> ProposedDFAStateKey<'scratch, MCS>
where
    MCS: MutableConfigSet<'scratch> + 'scratch,
{
    fn from_proposed(proposed: &mut ProposedDFAState<'scratch, MCS>) -> Self {
        ProposedDFAStateKey(NonNull::from(proposed))
    }

    #[inline(always)]
    fn as_ref(&self) -> &'scratch ProposedDFAState<'scratch, MCS> {
        // SAFETY: `self` can only be constructed from a valid reference to a
        // ProposedDFAState:
        unsafe { self.0.as_ref() }
    }
}

impl<'scratch, MCS> std::hash::Hash for ProposedDFAStateKey<'scratch, MCS>
where
    MCS: MutableConfigSet<'scratch> + 'scratch,
{
    fn hash<H: Hasher>(&self, state: &mut H) {
        let proposed = self.as_ref();
        let hash = proposed.configs.hash_code();
        state.write_u64(hash);
    }
}

impl<'scratch, 'sim, MCS> Equivalent<DFAStateKey<'sim, MCS::FinalizedType<'sim>>>
    for ProposedDFAStateKey<'scratch, MCS>
where
    'sim: 'scratch,
    MCS: MutableConfigSet<'scratch> + 'scratch,
{
    fn equivalent(&self, other: &DFAStateKey<'sim, MCS::FinalizedType<'sim>>) -> bool {
        let proposed = self.as_ref();
        let other = other.as_ref();

        proposed.configs.eq_config_set(other.configs())
    }
}

type StateStoreRoots<'sim, CS> = [*mut MaybeUninit<DFAState<'sim, CS>>; MAX_NUM_CHUNKS as usize];

#[derive(Debug)]
pub struct DFAStateStore<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    map: ManuallyDrop<HashSet<DFAStateKey<'sim, CS>, NoopHasherBuilder, &'sim bumpalo::Bump>>,
    chunks: Pin<Box<StateStoreRoots<'sim, CS>>>,
    arena: Pin<Box<bumpalo::Bump>>,

    allocated_chunk_bytes: usize,
}

macro_rules! define_arena {
    ($method:ident, $field:ident) => {
        fn $method(&self) -> &'sim bumpalo::Bump {
            // SAFETY: self-reference cast
            unsafe { std::mem::transmute::<&bumpalo::Bump, &'sim bumpalo::Bump>(&self.$field) }
        }
    };
}

impl<'sim, CS> DFAStateStore<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    pub fn new() -> Self {
        let arena = Box::pin(bumpalo::Bump::new());
        let arena_ref =
            // SAFETY: self-reference cast
            unsafe { std::mem::transmute::<&bumpalo::Bump, &'sim bumpalo::Bump>(&arena) };
        DFAStateStore {
            map: ManuallyDrop::new(HashSet::with_hasher_in(NoopHasherBuilder {}, arena_ref)),
            chunks: Box::pin([std::ptr::null_mut(); MAX_NUM_CHUNKS as usize]),
            arena,
            allocated_chunk_bytes: 0,
        }
    }

    #[inline]
    fn get<'scratch, ECS>(
        &self,
        key: ProposedDFAStateKey<'scratch, ECS>,
    ) -> Option<&'sim DFAState<'sim, CS>>
    where
        'sim: 'scratch,
        ECS: MutableConfigSet<'scratch, FinalizedType<'sim> = CS> + 'scratch,
    {
        self.map.get(&key).map(|k| k.as_ref())
    }

    pub fn add(&mut self, dfa: DFAState<'sim, CS>) -> &'sim DFAState<'sim, CS> {
        let state_number = self.map.len() as i32;
        debug_assert!(
            state_number == dfa.state_number(),
            "DFAState added to store has state number {}, but expected {}",
            dfa.state_number(),
            state_number
        );

        let id = DFAStateId::new(state_number);
        let (chunk_index, offset) = id.split_index();

        if offset == 0 {
            // This is a new chunk, so we need to allocate it:
            assert!(
                chunk_index < MAX_NUM_CHUNKS,
                "exceeded maximum number of DFA states ({})",
                MAX_NUM_CHUNKS
            );
            let chunk_size = DFAStateId::chunk_size(chunk_index);
            let layout = std::alloc::Layout::array::<MaybeUninit<DFAState<'sim, CS>>>(chunk_size)
                .expect("layout should be valid since chunk_size is bounded");
            self.chunks[chunk_index as usize] = unsafe {
                let ptr = std::alloc::alloc(layout);
                if ptr.is_null() {
                    std::alloc::handle_alloc_error(layout);
                }
                ptr as *mut MaybeUninit<DFAState<'sim, CS>>
            };
            self.allocated_chunk_bytes += layout.size();
        }

        let value = unsafe {
            let chunk_ptr = self.chunks[chunk_index as usize];
            let slot_ptr = &mut *chunk_ptr.add(offset as usize);
            slot_ptr.write(dfa);
            slot_ptr.assume_init_ref()
        };

        let is_new = self.map.insert(DFAStateKey::from_state(value));
        debug_assert!(is_new);
        value
    }

    pub fn values<'a>(&'a self) -> impl Iterator<Item = &'sim DFAState<'sim, CS>> + 'a {
        self.map.iter().map(|key| key.as_ref())
    }

    pub fn len(&self) -> usize {
        self.map.len()
    }

    pub fn is_empty(&self) -> bool {
        self.map.is_empty()
    }

    pub fn alloc_bitset(&self, f: impl FnOnce() -> BitSet) -> &'sim BitSet {
        self.bitset_store().alloc_with(f)
    }

    pub fn contains_bitset(&self, bitset: &BitSet) -> bool {
        is_ref_in_arena(bitset, self.bitset_store())
    }

    pub fn alloc_semantic_context(
        &self,
        f: impl FnOnce() -> SemanticContext<'sim>,
    ) -> &'sim SemanticContext<'sim> {
        self.semantic_context_store().alloc_with(f)
    }

    pub fn contains_semantic_context(&self, context: &SemanticContext) -> bool {
        is_ref_in_arena(context, self.semantic_context_store())
    }

    pub fn alloc_semantic_context_slice<I>(&self, iter: I) -> &'sim [SemanticContext<'sim>]
    where
        I: IntoIterator<Item = SemanticContext<'sim>>,
        I::IntoIter: ExactSizeIterator,
    {
        self.semantic_context_store().alloc_slice_fill_iter(iter)
    }

    pub fn contains_semantic_context_slice(&self, slice: &[SemanticContext]) -> bool {
        is_slice_in_arena(slice, self.semantic_context_store())
    }

    pub(crate) fn alloc_lexer_action_slice(
        &self,
        len: usize,
        f: impl FnMut(usize) -> LexerAction<'sim>,
    ) -> &'sim [LexerAction<'sim>] {
        self.lexer_store().alloc_slice_fill_with(len, f)
    }

    pub(crate) fn contains_lexer_action_slice(&self, slice: &[LexerAction]) -> bool {
        is_slice_in_arena(slice, self.lexer_store())
    }

    pub(crate) fn alloc_lexer_action_executor(
        &self,
        f: impl FnOnce() -> LexerActionExecutor<'sim>,
    ) -> &'sim LexerActionExecutor<'sim> {
        self.lexer_store().alloc_with(f)
    }

    // pub(crate) fn contains_lexer_action_executor(&self, executor: &LexerActionExecutor) -> bool {
    //     is_ref_in_arena(executor, self.lexer_store())
    // }

    pub(crate) fn alloc_lexer_indexed_custom_action(
        &self,
        f: impl FnOnce() -> LexerIndexedCustomAction<'sim>,
    ) -> &'sim LexerIndexedCustomAction<'sim> {
        self.lexer_store().alloc_with(f)
    }

    pub(crate) fn contains_lexer_indexed_custom_action(
        &self,
        action: &LexerIndexedCustomAction,
    ) -> bool {
        is_ref_in_arena(action, self.lexer_store())
    }

    pub(crate) fn alloc_config_slice<I>(&self, iter: I) -> &'sim [CS::ConfigType]
    where
        I: IntoIterator<Item = CS::ConfigType>,
        I::IntoIter: ExactSizeIterator,
    {
        self.config_store().alloc_slice_fill_iter(iter)
    }

    pub(crate) fn alloc_pred_prediction_slice<I>(&self, iter: I) -> &'sim [PredPrediction<'sim>]
    where
        I: IntoIterator<Item = PredPrediction<'sim>>,
        I::IntoIter: ExactSizeIterator,
    {
        self.pred_prediction_store().alloc_slice_fill_iter(iter)
    }

    define_arena!(bitset_store, arena);
    define_arena!(semantic_context_store, arena);
    define_arena!(lexer_store, arena);
    define_arena!(config_store, arena);
    define_arena!(pred_prediction_store, arena);

    pub fn allocated_bytes(&self) -> usize {
        self.arena.allocated_bytes() + self.map.allocation_size() + self.allocated_chunk_bytes
    }
}

impl<'sim, CS> Default for DFAStateStore<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    fn default() -> Self {
        Self::new()
    }
}

impl<'sim, CS> Drop for DFAStateStore<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    fn drop(&mut self) {
        // First drop all DFAState objects
        self.map.iter().for_each(|key| unsafe {
            std::ptr::drop_in_place(
                key.as_ref() as *const DFAState<'sim, CS> as *mut DFAState<'sim, CS>
            );
        });
        // Then deallocate all chunks
        let nstates = self.map.len() as i32;
        if nstates > 0 {
            let last_id = DFAStateId::new(nstates - 1);
            let last_chunk_index = last_id.split_index().0;
            for chunk_index in 0..=last_chunk_index {
                let chunk_size = DFAStateId::chunk_size(chunk_index);
                let layout =
                    std::alloc::Layout::array::<MaybeUninit<DFAState<'sim, CS>>>(chunk_size)
                        .expect("layout should be valid since chunk_size is bounded");
                unsafe {
                    std::alloc::dealloc(self.chunks[chunk_index as usize] as *mut u8, layout);
                }
            }
        }
    }
}

struct DFAStateId(u32);

impl DFAStateId {
    fn new(state_number: i32) -> Self {
        DFAStateId(state_number as u32)
    }

    #[inline(always)]
    fn split_index(&self) -> (u32, u32) {
        if self.0 < (1 << INITIAL_CHUNK_BITS) {
            (0, self.0)
        } else {
            let k = self.0.ilog2();
            let chunk_index = k - INITIAL_CHUNK_BITS + 1;
            let index_in_chunk = self.0 & !(1 << k);
            (chunk_index, index_in_chunk)
        }
    }

    #[inline(always)]
    unsafe fn deref<'sim, CS>(
        &self,
        roots: *const StateStoreRoots<'sim, CS>,
    ) -> *mut MaybeUninit<DFAState<'sim, CS>>
    where
        CS: ConfigSet<'sim> + 'sim,
    {
        let (chunk_index, index_in_chunk) = self.split_index();
        unsafe {
            (*roots)
                .get_unchecked(chunk_index as usize)
                .add(index_in_chunk as usize)
        }
    }

    #[inline(always)]
    fn chunk_size(chunk_index: u32) -> usize {
        if chunk_index == 0 {
            1 << INITIAL_CHUNK_BITS
        } else {
            1 << (chunk_index + INITIAL_CHUNK_BITS - 1)
        }
    }
}

#[cfg(test)]
mod dfa_state_id_tests {
    use super::*;

    #[test]
    fn test_dfa_state_id() {
        let test_cases = [
            (0, (0, 0)),
            (1, (0, 1)),
            (31, (0, 31)),
            (32, (1, 0)),
            (33, (1, 1)),
            (63, (1, 31)),
            (64, (2, 0)),
            (65, (2, 1)),
            (127, (2, 63)),
            (128, (3, 0)),
            (255, (3, 127)),
            (256, (4, 0)),
        ];

        for (input, expected) in test_cases.iter() {
            let id = DFAStateId::new(*input);
            assert_eq!(id.split_index(), *expected);
        }
    }
}

type EdgeRepr = AtomicI32;

#[derive(Debug)]
struct EdgeSet<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    edges: OnceLock<Box<[EdgeRepr]>>,
    store: *const EdgeSetStore<'sim, CS>,
}

impl<'sim, CS> EdgeSet<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    fn new(store: &'sim EdgeSetStore<'sim, CS>) -> Self {
        EdgeSet {
            edges: OnceLock::new(),
            store,
        }
    }

    // For error states and other sentinels:
    fn new_invalid() -> Self {
        EdgeSet {
            edges: OnceLock::new(),
            store: std::ptr::null(),
        }
    }

    #[inline(always)]
    fn as_ref(&self) -> &[EdgeRepr] {
        self.edges.get_or_init(|| {
            if self.store.is_null() {
                panic!("Attempted to access edge set for invalid DFA state");
            }
            unsafe { &*self.store }.alloc_edge_set()
        })
    }

    #[inline]
    fn get(&self, index: usize) -> Option<i32> {
        self.as_ref().get(index).and_then(|n| {
            let value = n.load(Ordering::Acquire);
            if value == i32::MIN {
                None
            } else {
                Some(value)
            }
        })
    }

    fn set(&self, index: usize, target: &'sim DFAState<'sim, CS>) {
        if let Some(slot) = self.as_ref().get(index) {
            slot.store(target.state_number(), Ordering::Release);
        } else {
            panic!("Invalid token index for edge set");
        }
    }

    fn enumerate(&self) -> Vec<(usize, i32)> {
        self.as_ref()
            .iter()
            .enumerate()
            .filter_map(|(index, n)| {
                let value = n.load(Ordering::Acquire);
                if value == i32::MIN {
                    None
                } else {
                    Some((index, value))
                }
            })
            .filter(|(_, state_number)| *state_number != -1)
            .collect()
    }
}

impl<'sim, CS> Deref for EdgeSet<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    type Target = [EdgeRepr];

    fn deref(&self) -> &Self::Target {
        self.as_ref()
    }
}

// SAFETY: the `store` pointer is not mutatable after initialization:
unsafe impl<'sim, CS> Send for EdgeSet<'sim, CS> where CS: ConfigSet<'sim> + 'sim {}
unsafe impl<'sim, CS> Sync for EdgeSet<'sim, CS> where CS: ConfigSet<'sim> + 'sim {}

pub struct EdgeSetStore<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    set_size: usize,
    allocated_bytes: AtomicUsize,

    _marker: std::marker::PhantomData<&'sim CS>,
}

impl<'sim, CS> EdgeSetStore<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    pub fn new(atn: &'static ATN) -> Self {
        EdgeSetStore {
            set_size: CS::calc_edge_set_size(atn),
            allocated_bytes: AtomicUsize::new(0),
            _marker: std::marker::PhantomData,
        }
    }

    fn make_edge_set(&'sim self) -> EdgeSet<'sim, CS> {
        EdgeSet::new(self)
    }

    fn alloc_edge_set(&self) -> Box<[EdgeRepr]> {
        let mut edge_set = Box::new_uninit_slice(self.set_size);
        edge_set.iter_mut().for_each(|slot| {
            slot.write(AtomicI32::new(i32::MIN));
        });
        self.allocated_bytes.fetch_add(
            std::mem::size_of::<EdgeRepr>() * self.set_size,
            Ordering::Relaxed,
        );
        unsafe { edge_set.assume_init() }
    }

    pub fn allocated_bytes(&self) -> usize {
        self.allocated_bytes.load(Ordering::Relaxed)
    }
}

impl<'sim, CS> std::fmt::Debug for EdgeSetStore<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        f.debug_struct("EdgeSetStore")
            .field("store", &"Vec<RwLock<FxHashMap<i32, &'sim DFAState>>>")
            .finish()
    }
}
