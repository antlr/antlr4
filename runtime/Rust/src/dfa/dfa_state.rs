use std::fmt::{Display, Error, Formatter};
use std::hash::{Hash, Hasher};
use std::sync::atomic::{AtomicPtr, Ordering};
use std::sync::LazyLock;

use fxhash::hash64;

use crate::atn::ATN;
use crate::atn_config_set::{ATNConfigSet, ConfigSet, LexerATNConfigSet};
use crate::dfa::DFAStateStore;
use crate::lexer_action_executor::LexerActionExecutor;
use crate::lexer_atn_simulator::LEXER_DFA_EDGE_SET_SIZE;
use crate::semantic_context::SemanticContext;
use crate::PredictionContextCache;

#[derive(Eq, PartialEq, Debug)]
pub struct PredPrediction<'ephemeral> {
    pub(crate) alt: i32,
    pub(crate) pred: SemanticContext<'ephemeral>,
}

impl Display for PredPrediction<'_> {
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        f.write_fmt(format_args!("({},{:?})", self.alt, self.pred))
    }
}

impl<'ephemeral> PredPrediction<'ephemeral> {
    pub(crate) fn promote<'sim, CS>(&self, dfa: &DFAStateStore<'sim, CS>) -> PredPrediction<'sim>
    where
        CS: ConfigSet<'sim> + 'sim,
    {
        PredPrediction {
            alt: self.alt,
            pred: self.pred.promote(dfa),
        }
    }
}

#[derive(Debug)]
pub struct ProposedDFAState<'scratch, CS>
where
    CS: ConfigSet<'scratch> + 'scratch,
{
    pub configs: CS,
    pub is_accept_state: bool,
    pub prediction: i32,
    pub(crate) lexer_action_executor: Option<Box<LexerActionExecutor<'scratch>>>,
    pub requires_full_context: bool,
    pub predicates: &'scratch [PredPrediction<'scratch>],

    _marker: std::marker::PhantomData<&'scratch ()>,
}

impl<'scratch, CS> ProposedDFAState<'scratch, CS>
where
    CS: ConfigSet<'scratch> + 'scratch,
{
    pub fn new(configs: CS) -> Self {
        ProposedDFAState {
            configs,
            is_accept_state: false,
            prediction: 0,
            lexer_action_executor: None,
            requires_full_context: false,
            predicates: &[],
            _marker: std::marker::PhantomData,
        }
    }

    pub(crate) fn finalize<'sim>(
        self,
        atn: &ATN,
        cache: &'sim PredictionContextCache<'sim>,
        dfa: &DFAStateStore<'sim, CS::FinalizedType<'sim>>,
    ) -> DFAState<'sim, CS::FinalizedType<'sim>> {
        let state_number = dfa.len() as i32;
        let configs = self.configs.finalize(cache, dfa);
        let predicates =
            dfa.alloc_pred_prediction_slice(self.predicates.iter().map(|p| p.promote(dfa)));

        let mut state = DFAState::new(atn, dfa, state_number, configs, predicates);
        state.is_accept_state = self.is_accept_state;
        state.prediction = self.prediction;
        state.lexer_action_executor = self
            .lexer_action_executor
            .map(|ex| dfa.alloc_lexer_action_executor(ex.promote(dfa)) as &'sim _);
        state.requires_full_context = self.requires_full_context;
        state
    }
}

impl<'ephemeral, CS: ConfigSet<'ephemeral>> PartialEq for ProposedDFAState<'ephemeral, CS> {
    fn eq(&self, other: &Self) -> bool {
        self.configs == other.configs
    }
}
impl<'ephemeral, CS: ConfigSet<'ephemeral>> Eq for ProposedDFAState<'ephemeral, CS> {}

impl<'ephemeral, CS: ConfigSet<'ephemeral>> Hash for ProposedDFAState<'ephemeral, CS> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.configs.hash(state);
    }
}

#[derive(Debug)]
pub struct DFAState<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    /// Number of this state in corresponding DFA
    pub state_number: i32,

    configs: AtomicPtr<CS>,

    edges: &'sim [AtomicPtr<DFAState<'sim, CS>>],

    pub is_accept_state: bool,
    pub prediction: i32,
    pub(crate) lexer_action_executor: Option<&'sim LexerActionExecutor<'sim>>,
    pub requires_full_context: bool,
    pub predicates: &'sim [PredPrediction<'sim>],
}

impl<'sim, CS: ConfigSet<'sim>> PartialEq for DFAState<'sim, CS> {
    fn eq(&self, other: &Self) -> bool {
        self.configs() == other.configs()
    }
}

impl<'sim, CS: ConfigSet<'sim>> Eq for DFAState<'sim, CS> {}

impl<'sim, CS: ConfigSet<'sim>> DFAState<'sim, CS> {
    pub fn default_hash(&self) -> u64 {
        hash64(self.configs())
    }

    pub fn is_error_state(&self) -> bool {
        self.state_number == -1
    }

    pub fn get_edge(&self, index: usize) -> Option<&'sim DFAState<'sim, CS>> {
        self.edges.get(index).and_then(|ptr| {
            let v = ptr.load(Ordering::Relaxed);
            if v.is_null() {
                None
            } else {
                Some(unsafe { &*v })
            }
        })
    }

    pub fn set_edge(&self, index: usize, state_ref: &DFAState<'sim, CS>) {
        self.edges[index].store(
            state_ref as *const DFAState<'sim, CS> as *mut DFAState<'sim, CS>,
            Ordering::Relaxed,
        );
    }

    pub fn enumerate_edges(&self) -> Vec<(usize, &'sim DFAState<'sim, CS>)> {
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

    pub fn configs(&self) -> &CS {
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

    pub(super) fn new(
        atn: &ATN,
        dfa: &DFAStateStore<'sim, CS>,
        state_number: i32,
        configs: CS,
        predicates: &'sim [PredPrediction<'sim>],
    ) -> Self {
        let nedges = if state_number >= 0 {
            calc_edge_set_size(atn)
        } else {
            0
        };
        let edges = dfa.alloc_edges(nedges);
        let configs = AtomicPtr::new(dfa.alloc_config_set(configs) as *const CS as *mut CS);

        DFAState {
            state_number,
            configs,
            edges,
            is_accept_state: false,
            prediction: 0,
            lexer_action_executor: None,
            requires_full_context: false,
            predicates,
        }
    }

    pub(super) fn set_configs(&self, configs: &'sim mut CS) {
        self.configs.swap(configs as *mut CS, Ordering::Relaxed);
    }

    // fn set_prediction(&self, _v: i32) { unimplemented!() }
}

fn calc_edge_set_size(atn: &ATN) -> usize {
    std::cmp::max(atn.max_token_type as usize + 2, LEXER_DFA_EDGE_SET_SIZE)
}

pub(super) static ERROR_DFA_STATE_REF: LazyLock<DFAState<'static, ATNConfigSet>> =
    LazyLock::new(|| {
        static EMPTY_EDGE_SET: [AtomicPtr<DFAState<'static, ATNConfigSet>>; 0] = [];

        DFAState {
            state_number: -1,
            configs: AtomicPtr::new(Box::into_raw(Box::new(ATNConfigSet::new_empty()))),
            edges: &EMPTY_EDGE_SET,
            is_accept_state: false,
            prediction: 0,
            lexer_action_executor: None,
            requires_full_context: false,
            predicates: &EMPTY_PREDICATES,
        }
    });

pub(super) static ERROR_LEXER_DFA_STATE_REF: LazyLock<DFAState<'static, LexerATNConfigSet>> =
    LazyLock::new(|| {
        static EMPTY_EDGE_SET: [AtomicPtr<DFAState<'static, LexerATNConfigSet>>; 0] = [];

        DFAState {
            state_number: -1,
            configs: AtomicPtr::new(Box::into_raw(Box::new(LexerATNConfigSet::new_empty()))),
            edges: &EMPTY_EDGE_SET,
            is_accept_state: false,
            prediction: 0,
            lexer_action_executor: None,
            requires_full_context: false,
            predicates: &EMPTY_PREDICATES,
        }
    });

static EMPTY_PREDICATES: [PredPrediction<'static>; 0] = [];
