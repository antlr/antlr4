use std::collections::HashMap;
use std::convert::TryFrom;

use std::sync::atomic::AtomicUsize;
use std::sync::RwLock;

use crate::atn::ATN;
use crate::atn_config_set::ATNConfigSet;
use crate::atn_simulator::IATNSimulator;
use crate::atn_state::ATNStateType::RuleStopState;
use crate::atn_state::{ATNDecisionState, ATNStateRef, ATNStateType};
use crate::dfa_serializer::DFASerializer;
use crate::dfa_state::{DFAState, DFAStateRef};
use crate::vocabulary::Vocabulary;

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

#[derive(Debug)]
struct StateStoreInner {
    states: Vec<Box<DFAState>>,
    // for faster duplicate search
    // TODO i think DFAState.edges can contain references to its elements
    states_map: HashMap</* DFAState hash*/ u64, Vec<DFAStateRef>>,
}

#[derive(Debug)]
pub struct StateStore {
    inner: RwLock<StateStoreInner>,
}

impl StateStore {
    fn new() -> StateStore {
        let mut inner = StateStoreInner {
            states: Vec::new(),
            states_map: HashMap::new(),
        };
        // to indicate null
        inner.states.push(
            DFAState::new_dfastate(
                usize::max_value(),
                Box::new(ATNConfigSet::new_base_atnconfig_set(true)),
            )
            .into(),
        );
        StateStore {
            inner: RwLock::new(inner),
        }
    }

    fn new_with_precedence() -> StateStore {
        let mut inner = StateStoreInner {
            states: Vec::new(),
            states_map: HashMap::new(),
        };
        // to indicate null
        inner.states.push(
            DFAState::new_dfastate(
                usize::max_value(),
                Box::new(ATNConfigSet::new_base_atnconfig_set(true)),
            )
            .into(),
        );
        let mut precedence_state = DFAState::new_dfastate(
            inner.states.len(),
            Box::new(ATNConfigSet::new_base_atnconfig_set(true)),
        );
        //precedence_state.edges = vec![];
        precedence_state.is_accept_state = false;
        precedence_state.requires_full_context = false;
        inner.states.push(precedence_state.into());

        StateStore {
            inner: RwLock::new(inner),
        }
    }

    pub fn len(&self) -> usize {
        let inner = self.inner.read().expect("unhandled lock poisoning");
        inner.states.len()
    }

    pub fn is_empty(&self) -> bool {
        self.len() == 0
    }

    pub fn for_each<F: FnMut(&DFAState)>(&self, mut f: F) {
        let inner = self.inner.read().expect("unhandled lock poisoning");
        for state in inner.states.iter() {
            f(state);
        }
    }

    /// Dereferences a DFAStateRef to get the DFAState
    pub fn get_state(&self, state_ref: DFAStateRef) -> Option<&DFAState> {
        let inner = self.inner.read().expect("unhandled lock poisoning");
        let ptr = inner.states.get(state_ref).map(|b| &**b as *const DFAState);
        unsafe { ptr.map(|p| &*p) }
    }

    pub fn add_state_maybe_optimize(
        &self,
        state: DFAState,
        interpreter: &dyn IATNSimulator,
    ) -> DFAStateRef {
        let state_hash = state.default_hash();

        {
            let inner = self.inner.read().expect("unhandled lock poisoning");
            if let Some(st) = inner.states_map.get(&state_hash) {
                if let Some(&st) = st.iter().find(|&&it| *inner.states[it] == state) {
                    return st;
                }
            }
        }
        let mut state = state;
        state.transform_configs(|mut configs| {
            if !configs.read_only() {
                configs.optimize_configs(interpreter);
                configs.set_read_only(true);
                //    new_hash = state.default_hash();
            }
            configs
        });

        let mut inner = self.inner.write().expect("unhandled lock poisoning");
        let state_ref = inner.states.len();
        state.state_number = state_ref;

        inner.states.push(Box::new(state));
        inner
            .states_map
            .entry(state_hash)
            .or_default()
            .push(state_ref);
        state_ref
    }

    pub fn add_state_from_configs(&self, configs: Box<ATNConfigSet>, atn: &ATN) -> DFAStateRef {
        assert!(!configs.has_semantic_context());

        let mut state = DFAState::new_dfastate(usize::MAX, configs);
        let rule_index = state
            .configs() //_configs
            .get_items()
            .find(|c| RuleStopState == *atn.states[c.get_state() as usize].get_state_type())
            .map(|c| {
                let rule_index = atn.states[c.get_state() as usize].get_rule_index();

                //println!("accepted rule {} on state {}",rule_index,c.get_state());
                (
                    atn.rule_to_token_type[rule_index as usize],
                    c.get_lexer_executor().cloned().map(Box::new),
                )
            });

        if let Some((prediction, exec)) = rule_index {
            state.prediction = prediction;
            state.lexer_action_executor = exec;
            state.is_accept_state = true;
        }

        let state_hash = state.default_hash();
        {
            let inner = self.inner.read().expect("unhandled lock poisoning");
            if let Some(st) = inner.states_map.get(&state_hash) {
                if let Some(&st) = st.iter().find(|&&it| *inner.states[it] == state) {
                    return st;
                }
            }
        }

        let mut inner = self.inner.write().expect("unhandled lock poisoning");
        let state_ref = inner.states.len();
        state.state_number = state_ref;
        state.transform_configs(|mut configs| {
            configs.set_read_only(true);
            configs
        });

        inner.states.push(Box::new(state));
        inner
            .states_map
            .entry(state_hash)
            .or_default()
            .push(state_ref);
        state_ref
    }
}

#[derive(Debug)]
pub struct DFA {
    /// ATN state from which this DFA creation was started from
    pub atn_start_state: ATNStateRef,

    pub decision: i32,

    /// Set of all dfa states.
    pub states: StateStore,

    //    states_mu sync.RWMutex
    /// Initial DFA state
    s0: AtomicUsize,
    //    s0_mu sync.RWMutex
    is_precedence_dfa: bool,
}

impl DFA {
    pub fn new(atn: &'static ATN, atn_start_state: ATNStateRef, decision: i32) -> DFA {
        if let ATNStateType::DecisionState {
            state:
                ATNDecisionState::StarLoopEntry {
                    is_precedence: true,
                    ..
                },
            ..
        } = atn.states[atn_start_state as usize].get_state_type()
        {
            DFA {
                atn_start_state,
                decision,
                states: StateStore::new_with_precedence(),
                s0: AtomicUsize::new(1 /* precedence state */),
                is_precedence_dfa: true,
            }
        } else {
            DFA {
                atn_start_state,
                decision,
                states: StateStore::new(),
                s0: AtomicUsize::new(usize::max_value()),
                is_precedence_dfa: false,
            }
        }
    }

    pub fn get_precedence_start_state(&self, precedence: i32) -> Option<DFAStateRef> {
        if !self.is_precedence_dfa {
            panic!("dfa is supposed to be precedence here");
        }

        self.get_s0_state().and_then(|state| {
            state.get_edge(precedence as usize).and_then(|it| match it {
                0 => None,
                x => Some(x),
            })
        })
    }

    pub fn set_precedence_start_state(&self, precedence: i32, start_state: DFAStateRef) {
        if !self.is_precedence_dfa {
            panic!("set_precedence_start_state called for not precedence dfa")
        }

        if precedence < 0 {
            return;
        }
        let precedence = precedence as usize;

        if let Some(state) = self.get_s0_state() {
            state.set_edge(precedence, start_state);
        }
    }

    pub fn get_s0(&self) -> Option<DFAStateRef> {
        let x = self.s0.load(std::sync::atomic::Ordering::SeqCst);

        if x == usize::max_value() {
            None
        } else {
            Some(x)
        }
    }

    pub fn get_s0_state(&self) -> Option<&DFAState> {
        self.get_s0().and_then(|s0| self.states.get_state(s0))
    }

    pub fn set_s0(&self, s: DFAStateRef) {
        self.s0.store(s, std::sync::atomic::Ordering::SeqCst);
    }

    pub fn is_precedence_dfa(&self) -> bool {
        self.is_precedence_dfa
    }

    // pub fn set_precedence_dfa(&mut self, precedence_dfa: bool) {
    //     self.is_precedence_dfa = precedence_dfa
    // }

    pub fn to_string(&self, vocabulary: &dyn Vocabulary) -> String {
        if self.get_s0().is_none() {
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
        if self.get_s0().is_none() {
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
}
