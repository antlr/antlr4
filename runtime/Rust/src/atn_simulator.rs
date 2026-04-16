use std::fmt::{Debug, Error, Formatter};
use std::ops::Deref;
use std::sync::atomic::{AtomicBool, AtomicUsize, Ordering};
use std::sync::{Arc, Condvar, Mutex, RwLock};
use std::vec::Vec;

use crate::atn::ATN;
use crate::atn_config_set::{ATNConfigSet, ConfigSet, LexerATNConfigSet};
use crate::dfa::{EdgeSetStore, DFA};
use crate::errors::ANTLRError;
use crate::prediction_context::PredictionContextCache;
use crate::Arena;

pub type LexerATNSimulatorManager = ATNSimulatorMan<LexerATNConfigSet<'static>>;
pub type ParserATNSimulatorManager = ATNSimulatorMan<ATNConfigSet<'static>>;

pub trait IATNSimulator<'sim, CS>
where
    CS: ConfigSet<'sim>,
{
    fn atn(&self) -> &'static ATN;
    fn shared_context_cache(&self) -> &'sim PredictionContextCache<'sim>;
    fn decision_to_dfa(&self, decision: usize) -> Option<&'sim DFA<'sim, CS>>;
}

pub struct BaseATNSimulator<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    pub atn: &'static ATN,
    shared_context_cache: Arc<NotifyOnDrop<PredictionContextCache<'sim>>>,
    decision_to_dfa: Arc<NotifyOnDrop<Vec<DFA<'sim, CS>>>>,
    allocation_limit_bytes: usize,
}

impl<'sim, CS: ConfigSet<'sim>> Debug for BaseATNSimulator<'sim, CS> {
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        f.write_str("BaseATNSimulator { .. }")
    }
}

macro_rules! dfa_sum_method {
    ($name:ident, $inner:ident) => {
        pub fn $name(&self) -> usize {
            self.decision_to_dfa.iter().map(|dfa| dfa.$inner()).sum()
        }
    };
    ($name:ident) => {
        dfa_sum_method!($name, $name);
    };
}

impl<'sim, CS: ConfigSet<'sim>> BaseATNSimulator<'sim, CS> {
    pub fn total_allocated_bytes(&self) -> usize {
        self.context_cache_bytes() + self.dfa_bytes()
    }

    pub fn context_cache_bytes(&self) -> usize {
        self.shared_context_cache.allocated_bytes()
    }

    pub fn edge_set_bytes(&self) -> usize {
        self.decision_to_dfa
            .iter()
            .next()
            .map(|dfa| dfa.edge_set_bytes())
            .unwrap_or(0)
    }

    dfa_sum_method!(dfa_bytes, allocated_bytes);
    dfa_sum_method!(semantic_context_bytes);
    dfa_sum_method!(lexer_bytes);
    dfa_sum_method!(config_bytes);
    dfa_sum_method!(config_set_bytes);
    dfa_sum_method!(dfa_state_bytes);
    dfa_sum_method!(pred_prediction_bytes);

    pub fn check_allocation_limit(&self) -> Result<(), ANTLRError> {
        if self.allocation_limit_bytes > 0
            && self.total_allocated_bytes() > self.allocation_limit_bytes
        {
            Err(ANTLRError::memory_limit_exceeded(
                self.allocation_limit_bytes,
                self.context_cache_bytes(),
                self.dfa_bytes(),
            ))
        } else {
            Ok(())
        }
    }
}

impl<'sim, CS: ConfigSet<'sim>> IATNSimulator<'sim, CS> for BaseATNSimulator<'sim, CS> {
    fn atn(&self) -> &'static ATN {
        self.atn
    }

    fn shared_context_cache(&self) -> &'sim PredictionContextCache<'sim> {
        unsafe {
            std::mem::transmute::<&PredictionContextCache<'sim>, &'sim PredictionContextCache<'sim>>(
                &self.shared_context_cache,
            )
        }
    }

    fn decision_to_dfa(&self, decision: usize) -> Option<&'sim DFA<'sim, CS>> {
        self.decision_to_dfa
            .get(decision)
            .map(|dfa| unsafe { std::mem::transmute::<&DFA<'sim, CS>, &'sim DFA<'sim, CS>>(dfa) })
    }
}

pub struct ATNSimulatorMan<CS>
where
    CS: ConfigSet<'static> + 'static,
{
    atn: &'static ATN,
    allocation_limit_bytes: AtomicUsize,
    is_resetting: AtomicBool,
    shared_context_cache: RwLock<Arc<NotifyOnDrop<PredictionContextCache<'static>>>>,
    decision_to_dfa: RwLock<Arc<NotifyOnDrop<Vec<DFA<'static, CS>>>>>,
    context_cache_sentinel: SentinelGuard,
    decision_to_dfa_sentinel: SentinelGuard,
}

impl<CS: ConfigSet<'static> + 'static> ATNSimulatorMan<CS> {
    pub fn new(atn: &'static ATN) -> Self {
        let context_cache_sentinel = SentinelGuard::new();
        let decision_to_dfa_sentinel = SentinelGuard::new();
        let decision_to_dfa = NotifyOnDrop::new(make_dfa_vec::<CS>(atn), &decision_to_dfa_sentinel);
        let shared_context_cache =
            NotifyOnDrop::new(PredictionContextCache::new(), &context_cache_sentinel);

        Self {
            atn,
            allocation_limit_bytes: AtomicUsize::new(0),
            is_resetting: AtomicBool::new(false),
            shared_context_cache: RwLock::new(Arc::new(shared_context_cache)),
            decision_to_dfa: RwLock::new(Arc::new(decision_to_dfa)),
            context_cache_sentinel,
            decision_to_dfa_sentinel,
        }
    }

    pub fn get_simulator<'sim>(
        &self,
        // Only used to ground the 'sim lifetime:
        _: &'sim Arena,
    ) -> BaseATNSimulator<'sim, CS::TransmutedType<'sim>> {
        let dfa_lock = self.decision_to_dfa.read().unwrap();
        let allocation_limit_bytes = self.get_allocation_limit_bytes();

        BaseATNSimulator {
            atn: self.atn,
            shared_context_cache: unsafe {
                std::mem::transmute::<
                    Arc<NotifyOnDrop<PredictionContextCache<'_>>>,
                    Arc<NotifyOnDrop<PredictionContextCache<'_>>>,
                >(self.shared_context_cache.read().unwrap().clone())
            },
            decision_to_dfa: unsafe {
                std::mem::transmute::<
                    Arc<NotifyOnDrop<Vec<DFA<'_, CS>>>>,
                    Arc<NotifyOnDrop<Vec<DFA<'_, <CS as ConfigSet<'_>>::TransmutedType<'_>>>>>,
                >(dfa_lock.clone())
            },
            allocation_limit_bytes,
        }
    }

    pub fn set_allocation_limit_bytes(&self, threshold: usize) {
        self.allocation_limit_bytes
            .store(threshold, Ordering::Relaxed);
    }

    pub fn get_allocation_limit_bytes(&self) -> usize {
        self.allocation_limit_bytes.load(Ordering::Relaxed)
    }

    pub fn reset_dfa(&self) {
        let is_resetting = self.is_resetting.swap(true, Ordering::AcqRel);
        if is_resetting {
            return;
        }

        let new_decision_to_dfa = Arc::new(NotifyOnDrop::new(
            make_dfa_vec::<CS>(self.atn),
            &self.decision_to_dfa_sentinel,
        ));
        let mut dfa_lock = self.decision_to_dfa.write().unwrap();
        *dfa_lock = new_decision_to_dfa;
        // Drain all outstanding references to the old DFA vector before
        // releasing the write lock -- not necessary for correctness, but
        // prevents having multiple DFA vectors in memory at the same time
        self.decision_to_dfa_sentinel.wait();
        self.is_resetting.store(false, Ordering::Release);
    }

    pub fn reset_all(&self) {
        let is_resetting = self.is_resetting.swap(true, Ordering::AcqRel);
        if is_resetting {
            return;
        }

        let new_decision_to_dfa = Arc::new(NotifyOnDrop::new(
            make_dfa_vec::<CS>(self.atn),
            &self.decision_to_dfa_sentinel,
        ));
        let new_shared_context_cache = Arc::new(NotifyOnDrop::new(
            PredictionContextCache::new(),
            &self.context_cache_sentinel,
        ));

        let mut dfa_lock = self.decision_to_dfa.write().unwrap();
        let mut cache_lock = self.shared_context_cache.write().unwrap();

        *dfa_lock = new_decision_to_dfa;
        *cache_lock = new_shared_context_cache;

        // Drain all outstanding references to the old DFA vector and context
        // cache before releasing the write locks -- not necessary for
        // correctness, but prevents having multiple DFA vectors and context
        // caches in memory at the same time
        self.decision_to_dfa_sentinel.wait();
        self.context_cache_sentinel.wait();

        self.is_resetting.store(false, Ordering::Release);
    }
}

fn make_dfa_vec<'x, CS: ConfigSet<'x>>(atn: &'static ATN) -> Vec<DFA<'x, CS>> {
    let edge_store = Arc::new(EdgeSetStore::new(atn));
    (0..atn.decision_to_state.len())
        .map(|decision| {
            DFA::<CS>::new(
                atn.get_decision_state(decision as i32),
                decision as i32,
                Arc::clone(&edge_store),
            )
        })
        .collect()
}

#[derive(Clone)]
struct SentinelGuard(Arc<(Mutex<bool>, Condvar)>);

impl SentinelGuard {
    fn new() -> Self {
        Self(Arc::new((Mutex::new(false), Condvar::new())))
    }

    fn wait(&self) {
        let (lock, cvar) = &*self.0;
        let mut guard = lock.lock().expect("SentinelGuard mutex poisoned");
        while !*guard {
            guard = cvar.wait(guard).expect("SentinelGuard condvar wait failed");
        }
        *guard = false;
    }

    fn notify(&self) {
        let (lock, cvar) = &*self.0;
        let mut guard = lock.lock().expect("SentinelGuard mutex poisoned");
        *guard = true;
        cvar.notify_all();
    }
}

struct NotifyOnDrop<T> {
    value: T,
    sentinel: SentinelGuard,
}

impl<T> NotifyOnDrop<T> {
    fn new(value: T, sentinel: &SentinelGuard) -> Self {
        Self {
            value,
            sentinel: sentinel.clone(),
        }
    }
}

impl<T> Deref for NotifyOnDrop<T> {
    type Target = T;

    fn deref(&self) -> &Self::Target {
        &self.value
    }
}

impl<T> Debug for NotifyOnDrop<T>
where
    T: Debug,
{
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        self.value.fmt(f)
    }
}

impl<T> Drop for NotifyOnDrop<T> {
    fn drop(&mut self) {
        self.sentinel.notify();
    }
}
