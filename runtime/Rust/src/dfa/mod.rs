use std::convert::TryFrom;
use std::hash::Hasher;
use std::mem::ManuallyDrop;
use std::ops::Deref;
use std::pin::Pin;
use std::ptr::NonNull;
use std::sync::atomic::{AtomicPtr, AtomicUsize, Ordering};
use std::sync::Mutex;

use crate::arena::{is_ref_in_arena, is_slice_in_arena};
use crate::atn::ATN;
use crate::atn_config_set::{ATNConfigSet, ConfigSet, LexerATNConfigSet};
use crate::atn_simulator::IATNSimulator;
use crate::atn_state::{ATNDecisionState, ATNState, ATNStateRef, DecisionState};
use crate::lexer_action::{LexerAction, LexerIndexedCustomAction};
use crate::lexer_action_executor::LexerActionExecutor;
use crate::prediction_context::NoopHasherBuilder;
use crate::semantic_context::SemanticContext;
use crate::vocabulary::Vocabulary;
use crate::PredictionContextCache;

mod dfa_serializer;
mod dfa_state;

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

#[derive(Debug)]
pub struct DFA<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    /// ATN state from which this DFA creation was started from
    pub atn_start_state: ATNStateRef,

    pub decision: i32,

    /// Set of all DFA states.
    states: Mutex<DFAStateStore<'sim, CS>>,

    /// Initial DFA state
    s0: AtomicPtr<DFAState<'sim, CS>>,

    precedence_state: bool,

    allocated_bytes: AtomicUsize,
    semantic_context_bytes: AtomicUsize,
    lexer_bytes: AtomicUsize,
    config_bytes: AtomicUsize,
    config_set_bytes: AtomicUsize,
    dfa_state_bytes: AtomicUsize,
    edge_set_bytes: AtomicUsize,
    pred_prediction_bytes: AtomicUsize,
}

impl<'sim, CS> DFA<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    // ---- Begin direct Java port ----
    pub fn new(atn: &'static ATN, atn_start_state: ATNStateRef, decision: i32) -> DFA<'sim, CS> {
        let state_store = DFAStateStore::new();

        let (s0, precedence_state) = if is_precedence_atn_state(atn_start_state) {
            let mut precedence_state = DFAState::new(atn, &state_store, 0, CS::new_empty(), &[]);
            precedence_state.is_accept_state = false;
            precedence_state.requires_full_context = false;

            let precedence_state = state_store.alloc_dfa_state(precedence_state);

            (Some(precedence_state as &'sim DFAState<'sim, CS>), true)
        } else {
            (None, false)
        };

        DFA {
            atn_start_state,
            decision,
            states: Mutex::new(state_store),
            s0: AtomicPtr::new(s0.map_or(std::ptr::null_mut(), |s| {
                s as *const DFAState<'sim, CS> as *mut DFAState<'sim, CS>
            })),
            precedence_state,
            allocated_bytes: AtomicUsize::new(0),
            semantic_context_bytes: AtomicUsize::new(0),
            lexer_bytes: AtomicUsize::new(0),
            config_bytes: AtomicUsize::new(0),
            config_set_bytes: AtomicUsize::new(0),
            dfa_state_bytes: AtomicUsize::new(0),
            edge_set_bytes: AtomicUsize::new(0),
            pred_prediction_bytes: AtomicUsize::new(0),
        }
    }

    pub fn is_precedence_dfa(&self) -> bool {
        self.precedence_state
    }

    pub fn get_precedence_start_state(&self, precedence: i32) -> Option<&'sim DFAState<'sim, CS>> {
        if !self.is_precedence_dfa() {
            // FIXME: this should return ANTLRError
            panic!("dfa is supposed to be precedence here");
        }

        self.s0()
            .and_then(|state| state.get_edge(precedence as usize))
    }

    pub fn set_precedence_start_state(
        &self,
        precedence: i32,
        start_state: &'sim DFAState<'sim, CS>,
    ) {
        if !self.is_precedence_dfa() {
            // FIXME: this should return ANTLRError
            panic!("set_precedence_start_state called for not precedence dfa")
        }

        if precedence < 0 {
            return;
        }
        let precedence = precedence as usize;

        self.s0()
            .expect("a precedence dfa's s0 state is never null")
            .set_edge(precedence, start_state);
    }

    /// Return a list of all states in this DFA, ordered by state number.
    pub fn get_states(&self) -> Vec<&'sim DFAState<'sim, CS>> {
        let mut states = self
            .states
            .lock()
            .expect("StateStore lock poisoned")
            .values()
            .collect::<Vec<_>>();
        states.sort_by_key(|s| s.state_number);
        states
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

    pub fn set_s0_configs<'ephemeral, ECS>(
        &self,
        configs: ECS,
        cache: &'sim PredictionContextCache<'sim>,
    ) where
        ECS: ConfigSet<'ephemeral, FinalizedType<'sim> = CS> + 'ephemeral,
    {
        let s0 = self.s0().expect("setting configs on a null s0 state");
        let states = self.states.lock().expect("StateStore lock poisoned");
        let configs = configs.finalize(cache, &states);

        s0.set_configs(states.alloc_config_set(configs));
    }

    pub fn add_state<'ephemeral, ECS>(
        &self,
        proposed: ProposedDFAState<'ephemeral, ECS>,
        recog: &impl IATNSimulator<'sim, CS>,
    ) -> &'sim DFAState<'sim, CS>
    where
        'sim: 'ephemeral,
        ECS: ConfigSet<'ephemeral, FinalizedType<'sim> = CS> + 'ephemeral,
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

        let state = proposed.finalize(recog.atn(), recog.shared_context_cache(), &state_store);
        let res = state_store.add(state);

        // Push the updated memory usage up to the DFA, outside of the lock:
        self.allocated_bytes
            .store(state_store.allocated_bytes(), Ordering::Relaxed);
        self.semantic_context_bytes.store(
            state_store.semantic_context_arena.allocated_bytes(),
            Ordering::Relaxed,
        );
        self.lexer_bytes
            .store(state_store.lexer_arena.allocated_bytes(), Ordering::Relaxed);
        self.config_bytes.store(
            state_store.config_arena.allocated_bytes(),
            Ordering::Relaxed,
        );
        self.config_set_bytes.store(
            state_store.config_set_arena.allocated_bytes(),
            Ordering::Relaxed,
        );
        self.dfa_state_bytes.store(
            state_store.dfa_state_arena.allocated_bytes(),
            Ordering::Relaxed,
        );
        self.edge_set_bytes.store(
            state_store.edge_set_arena.allocated_bytes(),
            Ordering::Relaxed,
        );
        self.pred_prediction_bytes.store(
            state_store.pred_prediction_arena.allocated_bytes(),
            Ordering::Relaxed,
        );

        res
    }

    pub fn allocated_bytes(&self) -> usize {
        self.allocated_bytes.load(Ordering::Relaxed)
    }

    pub fn semantic_context_bytes(&self) -> usize {
        self.semantic_context_bytes.load(Ordering::Relaxed)
    }

    pub fn lexer_bytes(&self) -> usize {
        self.lexer_bytes.load(Ordering::Relaxed)
    }

    pub fn config_bytes(&self) -> usize {
        self.config_bytes.load(Ordering::Relaxed)
    }

    pub fn config_set_bytes(&self) -> usize {
        self.config_set_bytes.load(Ordering::Relaxed)
    }

    pub fn dfa_state_bytes(&self) -> usize {
        self.dfa_state_bytes.load(Ordering::Relaxed)
    }

    pub fn edge_set_bytes(&self) -> usize {
        self.edge_set_bytes.load(Ordering::Relaxed)
    }

    pub fn pred_prediction_bytes(&self) -> usize {
        self.pred_prediction_bytes.load(Ordering::Relaxed)
    }
}

impl<'sim> DFA<'sim, LexerATNConfigSet<'sim>> {
    pub fn add_lexer_state<'ephemeral>(
        &self,
        proposed: ProposedDFAState<'ephemeral, LexerATNConfigSet<'ephemeral>>,
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

        let mut state = proposed.finalize(recog.atn(), recog.shared_context_cache(), &state_store);
        let rule_index = state
            .configs()
            .get_items()
            .find(|c| matches!(*c.get_state(), ATNState::RuleStop(_)))
            .map(|c| {
                let rule_index = c.get_state().get_rule_index();
                (
                    recog.atn().rule_to_token_type[rule_index as usize],
                    c.get_lexer_executor(),
                )
            });
        if let Some((prediction, exec)) = rule_index {
            state.prediction = prediction;
            state.set_lexer_action_executor(exec);
            state.is_accept_state = true;
        }
        let res = state_store.add(state);

        // Push the updated memory usage up to the DFA, outside of the lock:
        self.allocated_bytes
            .store(state_store.allocated_bytes(), Ordering::Relaxed);
        res
    }
}

unsafe impl<'sim, CS> Send for DFA<'sim, CS> where CS: ConfigSet<'sim> + 'sim {}
unsafe impl<'sim, CS> Sync for DFA<'sim, CS> where CS: ConfigSet<'sim> + 'sim {}

impl<'sim> DFA<'sim, ATNConfigSet<'sim>> {
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
        *atn_start_state,
        ATNState::Decision(DecisionState {
            state: ATNDecisionState::StarLoopEntry {
                is_precedence: true,
                ..
            },
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

struct ProposedDFAStateKey<'scratch, CS>(NonNull<ProposedDFAState<'scratch, CS>>)
where
    CS: ConfigSet<'scratch> + 'scratch;

impl<'scratch, CS> ProposedDFAStateKey<'scratch, CS>
where
    CS: ConfigSet<'scratch> + 'scratch,
{
    fn from_proposed(proposed: &mut ProposedDFAState<'scratch, CS>) -> Self {
        ProposedDFAStateKey(NonNull::from(proposed))
    }

    #[inline(always)]
    fn as_ref(&self) -> &'scratch ProposedDFAState<'scratch, CS> {
        // SAFETY: `self` can only be constructed from a valid reference to a
        // ProposedDFAState:
        unsafe { self.0.as_ref() }
    }
}

impl<'scratch, CS> std::hash::Hash for ProposedDFAStateKey<'scratch, CS>
where
    CS: ConfigSet<'scratch> + 'scratch,
{
    fn hash<H: Hasher>(&self, state: &mut H) {
        let proposed = self.as_ref();
        let hash = proposed.configs.hash_code();
        state.write_u64(hash);
    }
}

impl<'scratch, 'sim, CS> Equivalent<DFAStateKey<'sim, CS::FinalizedType<'sim>>>
    for ProposedDFAStateKey<'scratch, CS>
where
    'sim: 'scratch,
    CS: ConfigSet<'scratch> + 'scratch,
{
    fn equivalent(&self, other: &DFAStateKey<'sim, CS::FinalizedType<'sim>>) -> bool {
        let proposed = self.as_ref();
        let other = unsafe {
            std::mem::transmute::<&DFAState<'sim, CS::FinalizedType<'sim>>, &DFAState<'scratch, CS>>(
                other.as_ref(),
            )
        };
        &proposed.configs == other.configs()
    }
}

#[derive(Debug)]
pub struct DFAStateStore<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    map: ManuallyDrop<HashSet<DFAStateKey<'sim, CS>, NoopHasherBuilder, &'sim bumpalo::Bump>>,
    arena: Pin<Box<bumpalo::Bump>>,
    semantic_context_arena: Pin<Box<bumpalo::Bump>>,
    lexer_arena: Pin<Box<bumpalo::Bump>>,
    config_arena: Pin<Box<bumpalo::Bump>>,
    config_set_arena: Pin<Box<bumpalo::Bump>>,
    dfa_state_arena: Pin<Box<bumpalo::Bump>>,
    edge_set_arena: Pin<Box<bumpalo::Bump>>,
    pred_prediction_arena: Pin<Box<bumpalo::Bump>>,
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
            arena,
            semantic_context_arena: Box::pin(bumpalo::Bump::new()),
            lexer_arena: Box::pin(bumpalo::Bump::new()),
            config_arena: Box::pin(bumpalo::Bump::new()),
            config_set_arena: Box::pin(bumpalo::Bump::new()),
            dfa_state_arena: Box::pin(bumpalo::Bump::new()),
            edge_set_arena: Box::pin(bumpalo::Bump::new()),
            pred_prediction_arena: Box::pin(bumpalo::Bump::new()),
        }
    }

    fn get<'scratch, ECS>(
        &self,
        key: ProposedDFAStateKey<'scratch, ECS>,
    ) -> Option<&'sim DFAState<'sim, CS>>
    where
        'sim: 'scratch,
        ECS: ConfigSet<'scratch, FinalizedType<'sim> = CS> + 'scratch,
    {
        self.map.get(&key).map(|k| k.as_ref())
    }

    pub fn add(&mut self, dfa: DFAState<'sim, CS>) -> &'sim DFAState<'sim, CS> {
        let value = self.alloc_dfa_state(dfa);
        let is_new = self.map.insert(DFAStateKey::from_state(value));
        assert!(is_new);
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

    pub fn alloc_dfa_state(&self, state: DFAState<'sim, CS>) -> &'sim DFAState<'sim, CS> {
        self.dfa_state_store().alloc(state)
    }

    pub fn alloc_semantic_context(
        &self,
        context: SemanticContext<'sim>,
    ) -> &'sim SemanticContext<'sim> {
        self.semantic_context_store().alloc(context)
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
        executor: LexerActionExecutor<'sim>,
    ) -> &'sim LexerActionExecutor<'sim> {
        self.lexer_store().alloc(executor)
    }

    // pub(crate) fn contains_lexer_action_executor(&self, executor: &LexerActionExecutor) -> bool {
    //     is_ref_in_arena(executor, self.lexer_store())
    // }

    pub(crate) fn alloc_lexer_indexed_custom_action(
        &self,
        action: LexerIndexedCustomAction<'sim>,
    ) -> &'sim LexerIndexedCustomAction<'sim> {
        self.lexer_store().alloc(action)
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

    pub(crate) fn alloc_config_set(&self, set: CS) -> &'sim mut CS {
        self.config_set_store().alloc(set)
    }

    pub(crate) fn alloc_edges(&self, len: usize) -> &'sim [AtomicPtr<DFAState<'sim, CS>>] {
        self.edge_set_store()
            .alloc_slice_fill_with(len, |_| AtomicPtr::new(std::ptr::null_mut()))
    }

    pub(crate) fn alloc_pred_prediction_slice<I>(&self, iter: I) -> &'sim [PredPrediction<'sim>]
    where
        I: IntoIterator<Item = PredPrediction<'sim>>,
        I::IntoIter: ExactSizeIterator,
    {
        self.pred_prediction_store().alloc_slice_fill_iter(iter)
    }

    define_arena!(semantic_context_store, semantic_context_arena);
    define_arena!(lexer_store, lexer_arena);
    define_arena!(config_store, config_arena);
    define_arena!(config_set_store, config_set_arena);
    define_arena!(dfa_state_store, dfa_state_arena);
    define_arena!(edge_set_store, edge_set_arena);
    define_arena!(pred_prediction_store, pred_prediction_arena);

    pub fn allocated_bytes(&self) -> usize {
        self.arena.allocated_bytes()
            + self.semantic_context_arena.allocated_bytes()
            + self.lexer_arena.allocated_bytes()
            + self.config_arena.allocated_bytes()
            + self.config_set_arena.allocated_bytes()
            + self.dfa_state_arena.allocated_bytes()
            + self.edge_set_arena.allocated_bytes()
            + self.pred_prediction_arena.allocated_bytes()
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
