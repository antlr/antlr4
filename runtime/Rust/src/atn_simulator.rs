use std::cell::OnceCell;
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
    decision_to_dfa: &'sim [OnceCell<DFA<'sim, CS>>],
}

impl<'sim, CS: ConfigSet<'sim>> Debug for BaseATNSimulator<'sim, CS> {
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        f.write_str("BaseATNSimulator { .. }")
    }
}

impl<'sim, CS: ConfigSet<'sim>> BaseATNSimulator<'sim, CS> {
    pub fn new_base_atnsimulator(
        atn: &'static ATN,
        arena: &'sim Arena,
    ) -> BaseATNSimulator<'sim, CS> {
        let (arena_ref, raw_arena) = unsafe { arena.make_sim_arena() };
        let shared_context_cache = arena_ref.alloc(PredictionContextCache::new(arena_ref));
        let decision_to_dfa =
            arena_ref.alloc_slice_fill_with(atn.decision_to_state.len(), |_| OnceCell::new());

        BaseATNSimulator {
            atn,
            arena: raw_arena,
            shared_context_cache,
            decision_to_dfa,
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
        self.decision_to_dfa.get(decision).map(|cell| {
            cell.get_or_init(|| {
                DFA::<CS>::new(
                    self.atn(),
                    self.sim_arena(),
                    self.atn.get_decision_state(decision as i32),
                    decision as i32,
                )
            })
        })
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
