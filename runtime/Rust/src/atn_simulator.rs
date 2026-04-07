use std::fmt::{Debug, Error, Formatter};
use std::ops::Deref;
use std::sync::atomic::{AtomicPtr, AtomicUsize, Ordering};
use std::sync::Arc;

use crate::atn::ATN;
use crate::atn_config_set::{ATNConfigSet, ConfigSet, LexerATNConfigSet};
use crate::dfa::DFA;
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
    shared_context_cache: PredictionContextCache<'sim>,
    decision_to_dfa: Vec<DFA<'sim, CS>>,
}

impl<'sim, CS: ConfigSet<'sim>> Debug for BaseATNSimulator<'sim, CS> {
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        f.write_str("BaseATNSimulator { .. }")
    }
}

impl<CS: ConfigSet<'static>> BaseATNSimulator<'static, CS> {
    pub fn new_static(atn: &'static ATN) -> BaseATNSimulator<'static, CS> {
        let shared_context_cache = PredictionContextCache::new();
        let decision_to_dfa = (0..atn.decision_to_state.len())
            .map(|decision| {
                DFA::<CS>::new(
                    atn,
                    atn.get_decision_state(decision as i32),
                    decision as i32,
                )
            })
            .collect();

        BaseATNSimulator {
            atn,
            shared_context_cache,
            decision_to_dfa,
        }
    }
}

impl<'sim, CS: ConfigSet<'sim>> BaseATNSimulator<'sim, CS> {
    pub fn total_allocated_bytes(&self) -> usize {
        self.context_cache_bytes() + self.dfa_bytes()
    }

    pub fn context_cache_bytes(&self) -> usize {
        self.shared_context_cache.allocated_bytes()
    }

    pub fn dfa_bytes(&self) -> usize {
        self.decision_to_dfa
            .iter()
            .map(|dfa| dfa.allocated_bytes())
            .sum()
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

#[derive(Debug)]
pub struct BaseATNSimulatorHandle<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
    <CS as ConfigSet<'sim>>::FinalizedType<'static>: 'static,
{
    manager: &'static ATNSimulatorMan<CS::FinalizedType<'static>>,
    simulator: Arc<BaseATNSimulator<'sim, CS>>,
}

impl<'sim, CS> Deref for BaseATNSimulatorHandle<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    type Target = BaseATNSimulator<'sim, CS>;

    fn deref(&self) -> &Self::Target {
        &self.simulator
    }
}

impl<'sim, CS> Drop for BaseATNSimulatorHandle<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    fn drop(&mut self) {
        let threshold = self.manager.get_total_allocated_threshold_bytes();
        #[allow(clippy::collapsible_if)]
        if threshold > 0 {
            if self.simulator.total_allocated_bytes() > threshold {
                self.manager.reset_simulator();
            }
        }
    }
}

#[derive(Debug)]
pub struct ATNSimulatorMan<CS>
where
    CS: ConfigSet<'static> + 'static,
{
    atn: &'static ATN,
    threshold: AtomicUsize,
    simulator: AtomicPtr<BaseATNSimulator<'static, CS>>,
}

impl<CS: ConfigSet<'static> + 'static> ATNSimulatorMan<CS> {
    pub fn new(atn: &'static ATN) -> Self {
        let simulator = Arc::new(BaseATNSimulator::<CS>::new_static(atn));

        Self {
            atn,
            threshold: AtomicUsize::new(0),
            simulator: AtomicPtr::new(Arc::into_raw(simulator) as *mut BaseATNSimulator<CS>),
        }
    }

    pub fn get_simulator<'sim>(
        &self,
        // Only used to ground the 'sim lifetime:
        _: &'sim Arena,
    ) -> BaseATNSimulatorHandle<'sim, CS::FinalizedType<'sim>> {
        let ptr = self.simulator.load(Ordering::Acquire);
        // SAFETY: the simulator is initialized in the constructor and never
        // modified afterwards, so it's safe to convert the raw pointer back to
        // an Arc.
        let simulator = unsafe { Arc::from_raw(ptr) };
        let simulator_clone = Arc::clone(unsafe {
            std::mem::transmute::<
                &std::sync::Arc<BaseATNSimulator<'_, CS>>,
                &std::sync::Arc<BaseATNSimulator<'_, <CS as ConfigSet<'_>>::FinalizedType<'_>>>,
            >(&simulator)
        });
        // Prevent the original Arc from being dropped
        std::mem::forget(simulator);

        BaseATNSimulatorHandle {
            manager: unsafe {
                std::mem::transmute::<
                    &ATNSimulatorMan<CS>,
                    &ATNSimulatorMan<
                        <<CS as ConfigSet<'_>>::FinalizedType<'_> as ConfigSet<'_>>::FinalizedType<
                            '_,
                        >,
                    >,
                >(self)
            },
            simulator: simulator_clone,
        }
    }

    pub fn set_total_allocated_threshold_bytes(&self, threshold: usize) {
        self.threshold.store(threshold, Ordering::Release);
    }

    pub fn get_total_allocated_threshold_bytes(&self) -> usize {
        self.threshold.load(Ordering::Acquire)
    }

    pub fn reset_simulator(&self) {
        let new_simulator = Arc::new(BaseATNSimulator::<CS>::new_static(self.atn));
        let new_ptr = Arc::into_raw(new_simulator) as *mut BaseATNSimulator<CS>;
        let old_ptr = self.simulator.swap(new_ptr, Ordering::AcqRel);
        // SAFETY: the old simulator is still owned by an Arc, so it's safe to
        // convert the raw pointer back to an Arc and drop it.
        let _old_simulator = unsafe { Arc::from_raw(old_ptr) };
        drop(_old_simulator);
    }
}
