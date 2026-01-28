use std::collections::HashMap;
use std::convert::TryFrom;

use std::hash::Hasher;
use std::pin::Pin;
use std::sync::atomic::{AtomicPtr, Ordering};
use std::sync::Mutex;

use crate::atn::ATN;
use crate::atn_config_set::ATNConfigSet;
use crate::atn_simulator::IATNSimulator;
use crate::atn_state::{ATNDecisionState, ATNState, ATNStateRef, DecisionState};
use crate::prediction_context::MurmurHasherBuilder;
use crate::vocabulary::Vocabulary;

mod dfa_serializer;
mod dfa_state;

pub use dfa_serializer::DFASerializer;
pub use dfa_state::DFAState;
pub use dfa_state::PredPrediction;
pub use dfa_state::ProposedDFAState;

///Helper trait for scope management and temporary values not living long enough
pub(crate) trait ScopeExt: Sized {
    fn convert_with<T, F: FnOnce(Self) -> T>(self, f: F) -> T {
        f(self)
    }
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

type StoredDFAState = Pin<Box<DFAState<'static>>>;
type StateStore = Mutex<HashMap<DFAStateKey, StoredDFAState, MurmurHasherBuilder>>;

#[derive(Debug)]
pub struct DFA {
    /// ATN state from which this DFA creation was started from
    pub atn_start_state: ATNStateRef,

    pub decision: i32,

    /// Set of all dfa states.
    states: StateStore,

    /// Initial DFA state
    s0: AtomicPtr<DFAState<'static>>,

    precedence_state: Option<StoredDFAState>,
}

impl DFA {
    // ---- Begin direct Java port ----
    pub fn new(atn: &'static ATN, atn_start_state: ATNStateRef, decision: i32) -> DFA {
        let (s0, precedence_state) = if is_precedence_atn_state(atn, atn_start_state) {
            let mut precedence_state =
                DFAState::new(atn, 0, Box::new(ATNConfigSet::new_base_atnconfig_set(true)));
            precedence_state.is_accept_state = false;
            precedence_state.requires_full_context = false;

            let precedence_state = Box::pin(precedence_state);

            (
                AtomicPtr::new(&*precedence_state as *const _ as *mut _),
                Some(precedence_state),
            )
        } else {
            (AtomicPtr::new(std::ptr::null_mut()), None)
        };

        DFA {
            atn_start_state,
            decision,
            states: StateStore::default(),
            s0,
            precedence_state,
        }
    }

    pub fn is_precedence_dfa(&self) -> bool {
        self.precedence_state.is_some()
    }

    pub fn get_precedence_start_state<'a>(&'a self, precedence: i32) -> Option<&'a DFAState<'a>> {
        if !self.is_precedence_dfa() {
            // FIXME: this should return ANTLRError
            panic!("dfa is supposed to be precedence here");
        }

        self.s0()
            .and_then(|state| state.get_edge(precedence as usize))
    }

    pub fn set_precedence_start_state<'a>(&'a self, precedence: i32, start_state: &DFAState<'a>) {
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
    pub fn get_states<'a>(&'a self) -> Vec<&'a DFAState<'a>> {
        let mut states = self
            .states
            .lock()
            .expect("unhandled lock poisoning")
            .values()
            .map(|s| unsafe { std::mem::transmute::<&DFAState<'static>, &DFAState<'a>>(&**s) })
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
        self.states.lock().expect("unhandled lock poisoning").len()
    }

    pub fn is_empty(&self) -> bool {
        self.len() == 0
    }

    pub fn s0<'a>(&'a self) -> Option<&'a DFAState<'a>> {
        let x = self.s0.load(Ordering::Relaxed);
        if x.is_null() {
            None
        } else {
            Some(unsafe { std::mem::transmute::<&DFAState<'static>, &'a DFAState<'a>>(&*x) })
        }
    }

    pub fn set_s0(&self, s: &DFAState) {
        self.s0
            .store(s as *const DFAState as *mut DFAState, Ordering::Relaxed);
    }

    pub fn set_s0_configs(&self, configs: Box<ATNConfigSet>) {
        let s0 = self.s0().expect("setting configs on a null s0 state");
        s0.set_configs(configs);
    }

    pub fn get_error_state<'a>(&'a self) -> &'a DFAState<'a> {
        unsafe {
            std::mem::transmute::<&DFAState<'static>, &'a DFAState<'a>>(
                &*dfa_state::ERROR_DFA_STATE_REF,
            )
        }
    }

    pub fn add_state<'a, 'b>(
        &'a self,
        proposed: ProposedDFAState,
        interpreter: &'b dyn IATNSimulator,
    ) -> &'a DFAState<'a> {
        let mut state_store = self.states.lock().expect("unhandled lock poisoning");

        let proposed = {
            let mut proposed = proposed;
            if let Some(existing) = state_store.get(&DFAStateKey::from_proposed(&mut proposed)) {
                return unsafe {
                    std::mem::transmute::<&DFAState<'static>, &DFAState<'a>>(&**existing)
                };
            }
            proposed
        };

        let state =
            Self::stored_state_from_proposed(proposed, state_store.len() as i32, interpreter);
        let key = DFAStateKey::from_state(&state);
        let state_ref = &*state as *const DFAState<'static>;
        let existing = state_store.insert(key, state);
        assert!(existing.is_none());

        unsafe { &*std::mem::transmute::<*const DFAState<'static>, *const DFAState<'a>>(state_ref) }
    }

    fn stored_state_from_proposed(
        proposed: ProposedDFAState,
        state_number: i32,
        interpreter: &dyn IATNSimulator,
    ) -> StoredDFAState {
        let ProposedDFAState {
            mut configs,
            is_accept_state,
            prediction,
            lexer_action_executor,
            requires_full_context,
            predicates,
        } = proposed;

        if !configs.read_only() {
            configs.optimize_configs(interpreter);
            configs.set_read_only(true);
        }

        let mut state = DFAState::new(interpreter.atn(), state_number, configs.into());
        state.is_accept_state = is_accept_state;
        state.prediction = prediction;
        state.lexer_action_executor = lexer_action_executor;
        state.requires_full_context = requires_full_context;
        state.predicates = predicates;

        Box::pin(state)
    }
}

fn is_precedence_atn_state(atn: &ATN, atn_start_state: ATNStateRef) -> bool {
    matches!(
        atn.states[atn_start_state as usize],
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
struct DFAStateKey(*mut ATNConfigSet);

unsafe impl Send for DFAStateKey {}

impl DFAStateKey {
    pub fn from_state(entry: &StoredDFAState) -> Self {
        DFAStateKey(entry.configs() as *const ATNConfigSet as *mut ATNConfigSet)
    }

    pub fn from_proposed(state: &mut ProposedDFAState) -> Self {
        DFAStateKey(&state.configs as *const ATNConfigSet as *mut ATNConfigSet)
    }
}

impl PartialEq for DFAStateKey {
    fn eq(&self, other: &Self) -> bool {
        unsafe { *self.0 == *other.0 }
    }
}

impl Eq for DFAStateKey {}

impl std::hash::Hash for DFAStateKey {
    fn hash<H: Hasher>(&self, state: &mut H) {
        let configs = unsafe { &mut *self.0 };
        let hash = configs.hash_code();
        state.write_u64(hash);
    }
}

impl std::fmt::Debug for DFAStateKey {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "DFAStateKey({:p})", self.0)
    }
}
