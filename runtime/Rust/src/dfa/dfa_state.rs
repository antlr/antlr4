use std::fmt::{Display, Error, Formatter};
use std::hash::{Hash, Hasher};
use std::sync::LazyLock;

use crate::atn_config_set::{
    ATNConfigSet, ConfigSet, FromProposed as _, LexerATNConfigSet, MutableConfigSet,
};
use crate::dfa::DFAStateStore;
use crate::lexer_action_executor::LexerActionExecutor;
use crate::semantic_context::SemanticContext;
use crate::PredictionContextCache;

#[allow(dead_code)]
pub type ParserDFAState<'sim> = DFAState<'sim, ATNConfigSet<'sim>>;
#[allow(dead_code)]
pub type LexerDFAState<'sim> = DFAState<'sim, LexerATNConfigSet<'sim>>;

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
pub struct ProposedDFAState<'scratch, MCS>
where
    MCS: MutableConfigSet<'scratch> + 'scratch,
{
    pub configs: MCS,
    pub is_accept_state: bool,
    pub prediction: i32,
    pub requires_full_context: bool,
    pub predicates: &'scratch [PredPrediction<'scratch>],

    _marker: std::marker::PhantomData<&'scratch ()>,
}

impl<'scratch, MCS> ProposedDFAState<'scratch, MCS>
where
    MCS: MutableConfigSet<'scratch> + 'scratch,
{
    pub fn new(configs: MCS) -> Self {
        ProposedDFAState {
            configs,
            is_accept_state: false,
            prediction: 0,
            requires_full_context: false,
            predicates: &[],
            _marker: std::marker::PhantomData,
        }
    }

    pub(super) fn finalize<'sim>(
        self,
        cache: &'sim PredictionContextCache<'sim>,
        dfa: &DFAStateStore<'sim, MCS::FinalizedType<'sim>>,
        edge_set: super::EdgeSet<'sim, MCS::FinalizedType<'sim>>,
    ) -> DFAState<'sim, MCS::FinalizedType<'sim>> {
        let state_number = dfa.len() as i32;
        let configs = self.configs.finalize(cache, dfa);
        let predicates =
            dfa.alloc_pred_prediction_slice(self.predicates.iter().map(|p| p.promote(dfa)));

        let mut state = DFAState::new(state_number, configs, edge_set, predicates);
        state.set_accept_state(self.is_accept_state);
        state.set_requires_full_context(self.requires_full_context);
        state.set_prediction(self.prediction);
        state
    }
}

impl<'ephemeral, MCS: MutableConfigSet<'ephemeral>> PartialEq
    for ProposedDFAState<'ephemeral, MCS>
{
    fn eq(&self, other: &Self) -> bool {
        self.configs == other.configs
    }
}
impl<'ephemeral, MCS: MutableConfigSet<'ephemeral>> Eq for ProposedDFAState<'ephemeral, MCS> {}

impl<'ephemeral, MCS: MutableConfigSet<'ephemeral>> Hash for ProposedDFAState<'ephemeral, MCS> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.configs.hash(state);
    }
}

// Coerce the three most significant bits of the state number for
// is_error_state, is_accept_state and requires_full_context flags. This allows
// us to fit both ParserDFAState and LexerDFAState within 64 bytes.
const ERROR_STATE_BIT_INDEX: u32 = 31;
const ERROR_STATE_MASK: u32 = 1 << ERROR_STATE_BIT_INDEX;
const ACCEPT_STATE_BIT_INDEX: u32 = 30;
const ACCEPT_STATE_MASK: u32 = 1 << ACCEPT_STATE_BIT_INDEX;
const REQUIRES_FULL_CONTEXT_BIT_INDEX: u32 = 29;
const REQUIRES_FULL_CONTEXT_MASK: u32 = 1 << REQUIRES_FULL_CONTEXT_BIT_INDEX;
const STATE_NUMBER_MASK: u32 = !(ERROR_STATE_MASK | ACCEPT_STATE_MASK | REQUIRES_FULL_CONTEXT_MASK);

#[derive(Debug)]
pub struct DFAState<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    /// Number of this state in corresponding DFA
    state_number: u32,

    configs: CS,
    pub(super) edges: super::EdgeSet<'sim, CS>,

    prediction: i32,

    // Parser/Lexer specific fields:
    lexer_action_executor: CS::LexerActionExecutorType,
    predicates: CS::PredicatesType,
}

impl<'sim, CS: ConfigSet<'sim>> PartialEq for DFAState<'sim, CS> {
    fn eq(&self, other: &Self) -> bool {
        self.configs() == other.configs()
    }
}

impl<'sim, CS: ConfigSet<'sim>> Eq for DFAState<'sim, CS> {}

impl<'sim, CS: ConfigSet<'sim>> DFAState<'sim, CS> {
    #[inline(always)]
    pub fn state_number(&self) -> i32 {
        if self.is_error_state() {
            -1
        } else {
            (self.state_number & STATE_NUMBER_MASK) as i32
        }
    }

    #[inline(always)]
    pub fn is_error_state(&self) -> bool {
        (self.state_number & ERROR_STATE_MASK) != 0
    }

    #[inline(always)]
    pub fn is_accept_state(&self) -> bool {
        (self.state_number & ACCEPT_STATE_MASK) != 0
    }

    #[inline(always)]
    pub fn requires_full_context(&self) -> bool {
        (self.state_number & REQUIRES_FULL_CONTEXT_MASK) != 0
    }

    #[inline(always)]
    pub fn prediction(&self) -> i32 {
        self.prediction
    }

    pub fn set_accept_state(&mut self, v: bool) {
        let v = v as u32;
        self.state_number =
            (self.state_number & !ACCEPT_STATE_MASK) | (v << ACCEPT_STATE_BIT_INDEX);
    }

    pub fn set_requires_full_context(&mut self, v: bool) {
        let v = v as u32;
        self.state_number = (self.state_number & !REQUIRES_FULL_CONTEXT_MASK)
            | (v << REQUIRES_FULL_CONTEXT_BIT_INDEX);
    }

    pub fn set_prediction(&mut self, v: i32) {
        self.prediction = v;
    }

    #[inline]
    pub fn configs(&self) -> &CS {
        &self.configs
    }

    // ---- Below are private methods only callable by DFA ----

    pub(super) fn new(
        state_number: i32,
        configs: CS,
        edge_set: super::EdgeSet<'sim, CS>,
        predicates: &'sim [PredPrediction<'sim>],
    ) -> Self {
        debug_assert!(
            state_number >= 0,
            "State number {} is negative, use ERROR_DFA_STATE_REF instead",
            state_number
        );
        assert!(
            (state_number as u32) & !STATE_NUMBER_MASK == 0,
            "State number {} exceeds maximum of {}",
            state_number,
            STATE_NUMBER_MASK
        );

        DFAState {
            state_number: (state_number as u32) & STATE_NUMBER_MASK,
            configs,
            edges: edge_set,
            prediction: 0,
            lexer_action_executor: Default::default(),
            predicates: CS::PredicatesType::from_proposed(predicates),
        }
    }
}

impl<'sim> ParserDFAState<'sim> {
    #[inline(always)]
    pub(crate) fn predicates(&self) -> &'sim [PredPrediction<'sim>] {
        self.predicates
    }
}

impl<'sim> LexerDFAState<'sim> {
    pub(crate) fn lexer_action_executor(&self) -> Option<&'sim LexerActionExecutor<'sim>> {
        self.lexer_action_executor
    }

    pub(crate) fn set_lexer_action_executor(&mut self, v: Option<&'sim LexerActionExecutor<'sim>>) {
        self.lexer_action_executor = v;
    }
}

pub(super) static ERROR_DFA_STATE_REF: LazyLock<DFAState<'static, ATNConfigSet>> =
    LazyLock::new(|| DFAState {
        state_number: ERROR_STATE_MASK,
        configs: ATNConfigSet::new_empty(),
        edges: super::EdgeSet::new_invalid(),
        prediction: 0,
        lexer_action_executor: Default::default(),
        predicates: &EMPTY_PREDICATES,
    });

pub(super) static ERROR_LEXER_DFA_STATE_REF: LazyLock<DFAState<'static, LexerATNConfigSet>> =
    LazyLock::new(|| DFAState {
        state_number: ERROR_STATE_MASK,
        configs: LexerATNConfigSet::new_empty(),
        edges: super::EdgeSet::new_invalid(),
        prediction: 0,
        lexer_action_executor: None,
        predicates: (),
    });

static EMPTY_PREDICATES: [PredPrediction<'static>; 0] = [];
