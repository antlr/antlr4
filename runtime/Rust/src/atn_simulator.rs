use std::fmt::{Debug, Error, Formatter};

use crate::atn::ATN;
use crate::atn_config_set::ConfigSet;
use crate::dfa::DFA;
use crate::prediction_context::PredictionContextCache;
use crate::Arena;

pub trait IATNSimulator<'sim, CS>
where
    CS: ConfigSet<'sim>,
{
    fn atn(&self) -> &'static ATN;
    fn shared_context_cache(&self) -> &'sim PredictionContextCache<'sim>;
    fn decision_to_dfa(&self, decision: usize) -> Option<&'sim DFA<'sim, CS>>;
    fn sim_arena(&self) -> &'sim bumpalo::Bump;
}

pub struct BaseATNSimulator<'sim, CS>
where
    CS: ConfigSet<'sim> + 'sim,
{
    pub atn: &'static ATN,
    // (True) owner of the 'sim lifetime:
    arena: *const bumpalo::Bump,
    // Memory managed by arena:
    shared_context_cache: &'sim PredictionContextCache<'sim>,
    // Memory managed by arena:
    decision_to_dfa: &'sim [DFA<'sim, CS>],
}

impl<'sim, CS: ConfigSet<'sim>> Debug for BaseATNSimulator<'sim, CS> {
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        f.write_str("BaseATNSimulator { .. }")
    }
}

impl<CS: ConfigSet<'static>> BaseATNSimulator<'static, CS> {
    pub fn new_static(atn: &'static ATN) -> BaseATNSimulator<'static, CS> {
        let arena: &'static bumpalo::Bump = Box::leak(Box::new(bumpalo::Bump::new()));
        let shared_context_cache = arena.alloc(PredictionContextCache::new(arena));
        let decision_to_dfa =
            arena.alloc_slice_fill_with(atn.decision_to_state.len(), |decision| {
                DFA::<CS>::new(
                    atn,
                    arena,
                    atn.get_decision_state(decision as i32),
                    decision as i32,
                )
            });

        BaseATNSimulator {
            atn,
            arena: arena as *const bumpalo::Bump,
            shared_context_cache,
            decision_to_dfa,
        }
    }

    pub fn as_ref<'sim>(
        &self,
        _arena: &'sim Arena,
    ) -> &'sim BaseATNSimulator<'sim, CS::FinalizedType<'sim>> {
        // SAFETY: the 'static lifetime of self is guaranteed to outlive 'sim, and the
        // arena is not used for any memory management in this method:
        unsafe {
            &*(self as *const BaseATNSimulator<'static, CS>
                as *const BaseATNSimulator<'sim, CS::FinalizedType<'sim>>)
        }
    }
}

impl<'sim, CS: ConfigSet<'sim>> IATNSimulator<'sim, CS> for BaseATNSimulator<'sim, CS> {
    fn atn(&self) -> &'static ATN {
        self.atn
    }

    fn shared_context_cache(&self) -> &'sim PredictionContextCache<'sim> {
        self.shared_context_cache
    }

    fn decision_to_dfa(&self, decision: usize) -> Option<&'sim DFA<'sim, CS>> {
        self.decision_to_dfa.get(decision)
    }

    fn sim_arena(&self) -> &'sim bumpalo::Bump {
        // SAFETY: previously leaked by Box::into_raw in the constructor, and
        // not modified thereafter:
        unsafe { &*self.arena }
    }
}

impl<'sim, CS: ConfigSet<'sim>> Drop for BaseATNSimulator<'sim, CS> {
    fn drop(&mut self) {
        // SAFETY: previously leaked by Box::into_raw in the constructor, and
        // not modified thereafter:
        let arena = unsafe { Box::from_raw(self.arena as *mut bumpalo::Bump) };
        drop(arena);
    }
}
