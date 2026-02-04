use std::fmt::{Display, Error, Formatter};
use std::hash::{Hash, Hasher};
use std::sync::atomic::{AtomicPtr, Ordering};
use std::sync::LazyLock;

use fxhash::hash64;

use crate::atn::ATN;
use crate::atn_config_set::ATNConfigSet;
use crate::lexer_action_executor::LexerActionExecutor;
use crate::lexer_atn_simulator::LEXER_DFA_EDGE_SET_SIZE;
use crate::semantic_context::SemanticContext;

#[derive(Eq, PartialEq, Debug)]
pub struct PredPrediction {
    pub(crate) alt: i32,
    pub(crate) pred: SemanticContext,
}

impl Display for PredPrediction {
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        f.write_fmt(format_args!("({},{:?})", self.alt, self.pred))
    }
}

#[derive(Debug)]
pub struct ProposedDFAState<'ephemeral> {
    pub configs: ATNConfigSet<'ephemeral>,
    pub is_accept_state: bool,
    pub prediction: i32,
    pub(crate) lexer_action_executor: Option<Box<LexerActionExecutor>>,
    pub requires_full_context: bool,
    pub predicates: Vec<PredPrediction>,
}

impl<'ephemeral> ProposedDFAState<'ephemeral> {
    pub fn new(configs: ATNConfigSet<'ephemeral>) -> Self {
        ProposedDFAState {
            configs,
            is_accept_state: false,
            prediction: 0,
            lexer_action_executor: None,
            requires_full_context: false,
            predicates: Vec::new(),
        }
    }
}

impl PartialEq for ProposedDFAState<'_> {
    fn eq(&self, other: &Self) -> bool {
        self.configs == other.configs
    }
}
impl Eq for ProposedDFAState<'_> {}

impl Hash for ProposedDFAState<'_> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.configs.hash(state);
    }
}

#[derive(Debug)]
pub struct DFAState<'dfa> {
    /// Number of this state in corresponding DFA
    pub state_number: i32,

    configs: AtomicPtr<ATNConfigSet<'static>>,

    edges: Vec<AtomicPtr<DFAState<'dfa>>>,

    pub is_accept_state: bool,
    pub prediction: i32,
    pub(crate) lexer_action_executor: Option<Box<LexerActionExecutor>>,
    pub requires_full_context: bool,
    pub predicates: Vec<PredPrediction>,
    // dfa_ref: PhantomData<&'dfa super::DFA>,
}

impl PartialEq for DFAState<'_> {
    fn eq(&self, other: &Self) -> bool {
        self.configs() == other.configs()
    }
}

impl Eq for DFAState<'_> {}

impl<'dfa> DFAState<'dfa> {
    // pub fn get_alt_set(&self) -> &Set { unimplemented!() }

    pub fn default_hash(&self) -> u64 {
        hash64(self.configs())
    }

    pub fn is_error_state(&self) -> bool {
        self.state_number == -1
    }

    pub fn get_edge(&self, index: usize) -> Option<&'dfa DFAState<'dfa>> {
        self.edges.get(index).and_then(|ptr| {
            let v = ptr.load(Ordering::Relaxed);
            if v.is_null() {
                None
            } else {
                Some(unsafe { &*v })
            }
        })
    }

    pub fn set_edge(&self, index: usize, state_ref: &DFAState<'dfa>) {
        self.edges[index].store(
            state_ref as *const DFAState<'dfa> as *mut DFAState<'dfa>,
            Ordering::Relaxed,
        );
    }

    pub fn enumerate_edges(&self) -> Vec<(usize, &'dfa DFAState<'dfa>)> {
        self.edges
            .iter()
            .map(|ptr| ptr.load(Ordering::Relaxed))
            .enumerate()
            .filter_map(|(i, v)| {
                if v.is_null() {
                    None
                } else {
                    Some((i, unsafe { &*v }))
                }
            })
            .filter(|(_, t)| !t.is_error_state())
            .collect()
    }

    pub fn configs(&self) -> &ATNConfigSet<'static> {
        // SAFETY:
        // - The only way to instantiate a DFAState is via DFAState::new, which
        //   guarantees that configs is initialized to a valid ATNConfigSet
        //   pointer.
        // - The configs pointer is only modifiable via set_configs, which also
        //   guarantees that it is set to a valid ATNConfigSet pointer.
        // - The ATNConfigSet is exclusively owned by the DFAState, and is only
        //   deallocated when the DFAState is dropped.
        unsafe { &*self.configs.load(Ordering::Relaxed) }
    }

    // ---- Below are private methods only callable by DFA ----

    pub(super) fn new(atn: &ATN, state_number: i32, configs: Box<ATNConfigSet<'static>>) -> Self {
        let mut edges = Vec::new();
        // Pre-allocate enough space for the edge set for the given ATN --
        // avoids a lock on the edges
        if state_number >= 0 {
            edges.resize_with(calc_edge_set_size(atn), || {
                AtomicPtr::new(std::ptr::null_mut())
            });
        }

        DFAState {
            state_number,
            configs: AtomicPtr::new(Box::into_raw(configs)),
            edges,
            is_accept_state: false,
            prediction: 0,
            lexer_action_executor: None,
            requires_full_context: false,
            predicates: Vec::new(),
            //dfa_ref: PhantomData::<&'dfa super::DFA>,
        }
    }

    pub(super) fn set_configs(&self, configs: Box<ATNConfigSet<'static>>) {
        let old = self.configs.swap(Box::into_raw(configs), Ordering::Relaxed);
        // SAFETY: `old` was previously a valid pointer to a Box<ATNConfigSet>
        unsafe {
            drop(Box::from_raw(old));
        }
    }

    // fn set_prediction(&self, _v: i32) { unimplemented!() }
}

fn calc_edge_set_size(atn: &ATN) -> usize {
    std::cmp::max(atn.max_token_type as usize + 2, LEXER_DFA_EDGE_SET_SIZE)
}

pub(super) static ERROR_DFA_STATE_REF: LazyLock<DFAState<'static>> = LazyLock::new(|| DFAState {
    state_number: -1,
    configs: AtomicPtr::new(Box::into_raw(Box::new(ATNConfigSet::new_empty()))),
    edges: Vec::new(),
    is_accept_state: false,
    prediction: 0,
    lexer_action_executor: None,
    requires_full_context: false,
    predicates: Vec::new(),
    //dfa_ref: PhantomData::<&'static super::DFA>,
});
