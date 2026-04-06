use std::convert::TryFrom;
use std::hash::Hasher;
use std::mem::ManuallyDrop;
use std::pin::Pin;
use std::sync::atomic::{AtomicPtr, Ordering};
use std::sync::Mutex;

use crate::arena::{is_ref_in_arena, is_slice_in_arena};
use crate::atn::ATN;
use crate::atn_config_set::{ATNConfigSet, ConfigSet, LexerATNConfigSet};
use crate::atn_simulator::IATNSimulator;
use crate::atn_state::{ATNDecisionState, ATNState, ATNStateRef, DecisionState};
use crate::prediction_context::NoopHasherBuilder;
use crate::vocabulary::Vocabulary;
use crate::PredictionContextCache;

mod dfa_serializer;
mod dfa_state;

pub use dfa_serializer::DFASerializer;
pub use dfa_state::DFAState;
pub use dfa_state::PredPrediction;
pub use dfa_state::ProposedDFAState;
use hashbrown::HashMap;

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

            let precedence_state = state_store.alloc(precedence_state);

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
            .copied()
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

        s0.set_configs(states.alloc(configs));
    }

    pub fn add_state<'ephemeral, ECS>(
        &self,
        proposed: ProposedDFAState<'ephemeral, ECS>,
        recog: &impl IATNSimulator<'sim, CS>,
    ) -> &'sim DFAState<'sim, CS>
    where
        ECS: ConfigSet<'ephemeral, FinalizedType<'sim> = CS> + 'ephemeral,
    {
        let mut state_store = self.states.lock().expect("StateStore lock poisoned");

        let proposed = {
            let mut proposed = proposed;
            if let Some(existing) = state_store.get(&DFAStateKey::from_proposed(&mut proposed)) {
                return existing;
            }
            proposed
        };

        let state = proposed.finalize(recog.atn(), recog.shared_context_cache(), &state_store);
        state_store.add(state)
    }
}

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
struct DFAStateKey<CS>(*const CS);

unsafe impl<CS> Send for DFAStateKey<CS> {}

impl<'sim, CS> DFAStateKey<CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    pub fn from_state(entry: &'sim DFAState<'sim, CS>) -> Self {
        DFAStateKey(entry.configs() as *const CS)
    }

    pub fn from_proposed<'ephemeral, ECS>(state: &mut ProposedDFAState<'ephemeral, ECS>) -> Self
    where
        ECS: ConfigSet<'ephemeral, FinalizedType<'sim> = CS> + 'ephemeral,
    {
        let ptr = &state.configs as *const ECS as *const CS;
        DFAStateKey(ptr)
    }
}

impl<'sim, CS> PartialEq for DFAStateKey<CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    fn eq(&self, other: &Self) -> bool {
        unsafe { *self.0 == *other.0 }
    }
}

impl<'sim, CS> Eq for DFAStateKey<CS> where CS: ConfigSet<'sim> + 'sim {}

impl<'sim, CS> std::hash::Hash for DFAStateKey<CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    fn hash<H: Hasher>(&self, state: &mut H) {
        let configs = unsafe { &*self.0 };
        let hash = configs.hash_code();
        state.write_u64(hash);
    }
}

impl<'sim, CS> std::fmt::Debug for DFAStateKey<CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "DFAStateKey({:p})", self.0)
    }
}

#[derive(Debug)]
pub struct DFAStateStore<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    map: ManuallyDrop<
        HashMap<DFAStateKey<CS>, &'sim DFAState<'sim, CS>, NoopHasherBuilder, &'sim bumpalo::Bump>,
    >,
    arena: Pin<Box<bumpalo::Bump>>,
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
            map: ManuallyDrop::new(HashMap::with_hasher_in(NoopHasherBuilder {}, arena_ref)),
            arena,
        }
    }

    fn get(&self, key: &DFAStateKey<CS>) -> Option<&&'sim DFAState<'sim, CS>> {
        self.map.get(key)
    }

    pub fn add(&mut self, dfa: DFAState<'sim, CS>) -> &'sim DFAState<'sim, CS> {
        let value = self.alloc(dfa);
        let existing = self.map.insert(DFAStateKey::from_state(value), value);
        assert!(existing.is_none());
        value
    }

    pub fn values(&self) -> impl Iterator<Item = &&'sim DFAState<'sim, CS>> {
        self.map.values()
    }

    pub fn len(&self) -> usize {
        self.map.len()
    }

    pub fn is_empty(&self) -> bool {
        self.map.is_empty()
    }

    pub fn contains_slice<T>(&self, ptr: &[T]) -> bool {
        is_slice_in_arena(ptr, &self.arena)
    }

    pub fn contains_ref<T>(&self, ptr: &T) -> bool {
        is_ref_in_arena(ptr, &self.arena)
    }

    pub fn alloc_slice_fill_iter<T, I>(&self, iter: I) -> &'sim [T]
    where
        I: IntoIterator<Item = T>,
        I::IntoIter: ExactSizeIterator,
    {
        // SAFETY: the allocated slice is backed by the 'sim-lifetime arena
        unsafe { std::mem::transmute::<&[T], &'sim [T]>(self.arena.alloc_slice_fill_iter(iter)) }
    }

    pub fn alloc_slice_fill_with<T, F>(&self, len: usize, f: F) -> &'sim [T]
    where
        F: FnMut(usize) -> T,
    {
        // SAFETY: the allocated slice is backed by the 'sim-lifetime arena
        unsafe { std::mem::transmute::<&[T], &'sim [T]>(self.arena.alloc_slice_fill_with(len, f)) }
    }

    pub fn alloc_slice_fill_default<T: Default>(&self, len: usize) -> &'sim mut [T] {
        // SAFETY: the allocated slice is backed by the 'sim-lifetime arena
        unsafe {
            std::mem::transmute::<&mut [T], &'sim mut [T]>(self.arena.alloc_slice_fill_default(len))
        }
    }

    pub fn alloc<T>(&self, value: T) -> &'sim mut T {
        // SAFETY: the allocated value is backed by the 'sim-lifetime arena
        unsafe { std::mem::transmute::<&mut T, &'sim mut T>(self.arena.alloc(value)) }
    }

    pub fn allocated_bytes(&self) -> usize {
        self.arena.allocated_bytes()
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
