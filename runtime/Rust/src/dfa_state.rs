use std::fmt::{Display, Error, Formatter};
use std::hash::{Hash, Hasher};
use std::sync::atomic::AtomicPtr;
use std::sync::RwLock;

use murmur3::murmur3_32::MurmurHasher;

use crate::atn_config_set::ATNConfigSet;
use crate::lexer_action_executor::LexerActionExecutor;
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

//index in DFA.states
pub type DFAStateRef = usize;

#[derive(Debug)]
pub struct DFAState {
    /// Number of this state in corresponding DFA
    pub state_number: usize,
    configs: AtomicPtr<ATNConfigSet>,
    /// - 0 => no edge
    /// - usize::MAX => error edge
    /// - _ => actual edge
    edges: RwLock<Vec<DFAStateRef>>,
    pub is_accept_state: bool,

    pub prediction: i32,
    pub(crate) lexer_action_executor: Option<Box<LexerActionExecutor>>,
    pub requires_full_context: bool,
    pub predicates: Vec<PredPrediction>,
}

impl PartialEq for DFAState {
    fn eq(&self, other: &Self) -> bool {
        self.configs() == other.configs()
    }
}

impl Eq for DFAState {}

impl Hash for DFAState {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.configs().hash(state);
    }
}

impl DFAState {
    pub fn default_hash(&self) -> u64 {
        let mut hasher = MurmurHasher::default();
        self.hash(&mut hasher);
        hasher.finish()
    }

    pub fn new_dfastate(state_number: usize, configs: Box<ATNConfigSet>) -> DFAState {
        DFAState {
            state_number,
            configs: AtomicPtr::new(Box::into_raw(configs)),
            //            edges: Vec::with_capacity((MAX_DFA_EDGE - MIN_DFA_EDGE + 1) as usize),
            edges: RwLock::new(Vec::new()),
            is_accept_state: false,
            prediction: 0,
            lexer_action_executor: None,
            requires_full_context: false,
            predicates: Vec::new(),
        }
    }

    pub fn get_edge(&self, index: usize) -> Option<DFAStateRef> {
        let edges = self.edges.read().expect("unhandled lock poisoning");
        edges.get(index).copied()
    }

    pub fn set_edge(&self, index: usize, state_ref: DFAStateRef) {
        let mut edges = self.edges.write().expect("unhandled lock poisoning");
        if index >= edges.len() {
            edges.resize(index + 1, 0);
        }
        edges[index] = state_ref;
    }

    pub fn set_edge_with_target_size(
        &self,
        index: usize,
        state_ref: DFAStateRef,
        size_target: usize,
    ) {
        let mut edges = self.edges.write().expect("unhandled lock poisoning");
        if edges.len() < size_target {
            edges.resize(size_target, 0);
        }
        edges[index] = state_ref;
    }

    pub fn enumerate_edges(&self) -> Vec<(usize, DFAStateRef)> {
        let edges = self.edges.read().expect("unhandled lock poisoning");
        edges
            .iter()
            .copied()
            .enumerate()
            .filter(|(_, v)| *v != 0)
            .collect()
    }

    pub fn configs(&self) -> &ATNConfigSet {
        unsafe { &*self.configs.load(std::sync::atomic::Ordering::SeqCst) }
    }

    pub fn set_configs(&self, configs: Box<ATNConfigSet>) {
        let old = self
            .configs
            .swap(Box::into_raw(configs), std::sync::atomic::Ordering::SeqCst);
        unsafe {
            Box::from_raw(old);
        }
    }

    pub fn transform_configs<F>(&mut self, f: F)
    where
        F: FnOnce(Box<ATNConfigSet>) -> Box<ATNConfigSet>,
    {
        let old_ptr = self.configs.load(std::sync::atomic::Ordering::SeqCst);
        let old_configs = unsafe { Box::from_raw(old_ptr) };
        let new_configs = f(old_configs);
        let new_ptr = Box::into_raw(new_configs);
        self.configs
            .store(new_ptr, std::sync::atomic::Ordering::SeqCst);
    }

    //    fn get_alt_set(&self) -> &Set { unimplemented!() }

    // fn set_prediction(&self, _v: i32) { unimplemented!() }
}
