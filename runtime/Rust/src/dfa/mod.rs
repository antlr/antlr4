use std::cell::{Cell, RefCell};
use std::convert::TryFrom;

use std::hash::Hasher;

use crate::atn::ATN;
use crate::atn_config_set::{ATNConfigSet, ConfigSet, LexerATNConfigSet};
use crate::atn_simulator::IATNSimulator;
use crate::atn_state::{ATNDecisionState, ATNState, ATNStateRef, DecisionState};
use crate::prediction_context::NoopHasherBuilder;
use crate::vocabulary::Vocabulary;

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

type StoredDFAState<'sim, CS> = &'sim DFAState<'sim, CS>;
type StateStore<'sim, CS> =
    HashMap<DFAStateKey<CS>, StoredDFAState<'sim, CS>, NoopHasherBuilder, &'sim bumpalo::Bump>;

#[derive(Debug)]
pub struct DFA<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    /// ATN state from which this DFA creation was started from
    pub atn_start_state: ATNStateRef,

    pub decision: i32,

    /// Set of all dfa states.
    states: RefCell<StateStore<'sim, CS>>,

    /// Initial DFA state
    s0: Cell<Option<&'sim DFAState<'sim, CS>>>,

    precedence_state: bool,
    // arena: &'sim bumpalo::Bump,
}

impl<'sim, CS> DFA<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    // ---- Begin direct Java port ----
    pub fn new(
        atn: &'static ATN,
        arena: &'sim bumpalo::Bump,
        atn_start_state: ATNStateRef,
        decision: i32,
    ) -> DFA<'sim, CS> {
        let (s0, precedence_state) = if is_precedence_atn_state(atn_start_state) {
            let mut precedence_state = DFAState::new(atn, arena, 0, CS::new_empty(), &[]);
            precedence_state.is_accept_state = false;
            precedence_state.requires_full_context = false;

            let precedence_state = arena.alloc(precedence_state);

            (Some(precedence_state as &'sim DFAState<'sim, CS>), true)
        } else {
            (None, false)
        };

        DFA {
            atn_start_state,
            decision,
            states: RefCell::new(StateStore::with_hasher_in(NoopHasherBuilder {}, arena)),
            s0: Cell::new(s0),
            precedence_state,
            //arena,
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
        let mut states = self.states.borrow().values().copied().collect::<Vec<_>>();
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
        self.states.borrow().len()
    }

    pub fn is_empty(&self) -> bool {
        self.len() == 0
    }

    pub fn s0(&self) -> Option<&'sim DFAState<'sim, CS>> {
        self.s0.get()
    }

    pub fn set_s0(&self, s: &'sim DFAState<'sim, CS>) {
        self.s0.set(Some(s));
    }

    pub fn set_s0_configs(&self, configs: &'sim mut CS) {
        let s0 = self.s0().expect("setting configs on a null s0 state");
        s0.set_configs(configs);
    }

    pub fn add_state<'ephemeral, ECS>(
        &self,
        proposed: ProposedDFAState<'ephemeral, ECS>,
        recog: &impl IATNSimulator<'sim, CS>,
    ) -> &'sim DFAState<'sim, CS>
    where
        ECS: ConfigSet<'ephemeral, FinalizedType<'sim> = CS> + 'ephemeral,
    {
        let mut state_store = self.states.borrow_mut();

        let proposed = {
            let mut proposed = proposed;
            if let Some(existing) = state_store.get(&DFAStateKey::from_proposed(&mut proposed)) {
                return unsafe { std::mem::transmute::<&_, &'sim _>(&**existing) };
            }
            proposed
        };

        let state = recog
            .sim_arena()
            .alloc(proposed.finalize(recog, state_store.len() as i32))
            as &'sim _;
        let key = DFAStateKey::from_state(state);
        let existing = state_store.insert(key, state);
        assert!(existing.is_none());

        state
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
struct DFAStateKey<CS>(*mut CS);

unsafe impl<CS> Send for DFAStateKey<CS> {}

impl<'sim, CS> DFAStateKey<CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    pub fn from_state(entry: &'sim DFAState<'sim, CS>) -> Self {
        DFAStateKey(entry.configs() as *const CS as *mut CS)
    }

    pub fn from_proposed<'ephemeral, ECS>(state: &mut ProposedDFAState<'ephemeral, ECS>) -> Self
    where
        ECS: ConfigSet<'ephemeral, FinalizedType<'sim> = CS> + 'ephemeral,
    {
        let ptr = &state.configs as *const ECS as *mut CS;
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
