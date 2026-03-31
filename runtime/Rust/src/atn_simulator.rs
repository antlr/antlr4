use std::cell::LazyCell;
use std::fmt::{Debug, Error, Formatter};
use std::mem::ManuallyDrop;

use crate::atn::ATN;
use crate::atn_config_set::ConfigSet;
use crate::dfa::DFA;
use crate::prediction_context::PredictionContextCache;

pub trait IATNSimulator<CS>
where
    CS: ConfigSet,
{
    fn atn(&self) -> &ATN;
    fn shared_context_cache<'a>(&'a self) -> &'a PredictionContextCache<'a>;
    fn decision_to_dfa(&self, decision: usize) -> &DFA<CS>;
    fn sim_arena(&self) -> &bumpalo::Bump;
}

pub struct BaseATNSimulator<CS>
where
    CS: ConfigSet + 'static,
{
    pub atn: &'static ATN,
    // Owner of the 'sim lifetime:
    arena: *const bumpalo::Bump,
    // Memory managed by arena:
    shared_context_cache: ManuallyDrop<PredictionContextCache<'static>>,
    // Memory managed by arena:
    decision_to_dfa: ManuallyDrop<&'static [LazyCell<DFA<'static, CS>>]>,
}

impl<CS: ConfigSet> Debug for BaseATNSimulator<CS> {
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        f.write_str("BaseATNSimulator { .. }")
    }
}

impl<CS: ConfigSet> BaseATNSimulator<CS> {
    pub fn new_base_atnsimulator(atn: &'static ATN) -> BaseATNSimulator<CS> {
        let arena = Box::new(bumpalo::Bump::new());
        let arena_ref = arena.as_ref();
        let shared_context_cache = PredictionContextCache::new(arena_ref);
        let decision_to_dfa = arena.alloc_slice_fill_with(atn.decision_to_state.len(), |i| {
            LazyCell::new(move || {
                DFA::<CS>::new(atn, arena_ref, atn.get_decision_state(i as i32), i as i32)
            })
        });

        let mut res = BaseATNSimulator {
            atn,
            arena: std::ptr::null(),
            shared_context_cache: ManuallyDrop::new(unsafe {
                std::mem::transmute(shared_context_cache)
            }),
            decision_to_dfa: ManuallyDrop::new(unsafe { std::mem::transmute(decision_to_dfa) }),
        };
        res.arena = Box::into_raw(arena);
        res
    }
}

impl<CS: ConfigSet> IATNSimulator<CS> for BaseATNSimulator<CS> {
    fn atn(&self) -> &'static ATN {
        self.atn
    }

    fn shared_context_cache<'sim>(&'sim self) -> &'sim PredictionContextCache<'sim> {
        // Safety: self-reference
        unsafe { std::mem::transmute(&self.shared_context_cache) }
    }

    fn decision_to_dfa<'sim>(&'sim self, decision: usize) -> &'sim DFA<'sim, CS> {
        unsafe { std::mem::transmute(&self.decision_to_dfa[decision]) }
    }

    fn sim_arena(&self) -> &bumpalo::Bump {
        // Safety: previously leaked by Box::into_raw in the constructor, and
        // not modified thereafter:
        unsafe { &*self.arena }
    }
}

impl<CS: ConfigSet> Drop for BaseATNSimulator<CS> {
    fn drop(&mut self) {
        // Safety: previously leaked by Box::into_raw in the constructor, and
        // not modified thereafter:
        let arena = unsafe { Box::from_raw(self.arena as *mut bumpalo::Bump) };
        drop(arena);
    }
}
