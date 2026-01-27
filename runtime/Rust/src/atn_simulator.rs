use std::fmt::{Debug, Error, Formatter};

use crate::atn::ATN;
use crate::dfa::DFA;
use crate::prediction_context::PredictionContextCache;

pub trait IATNSimulator {
    fn shared_context_cache(&self) -> &PredictionContextCache;
    fn atn(&self) -> &ATN;
    fn decision_to_dfa(&self) -> &Vec<DFA>;
}

pub struct BaseATNSimulator {
    pub atn: &'static ATN,
    pub shared_context_cache: &'static PredictionContextCache,
    pub decision_to_dfa: &'static Vec<DFA>,
}

impl Debug for BaseATNSimulator {
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        f.write_str("BaseATNSimulator { .. }")
    }
}

impl BaseATNSimulator {
    pub fn new_base_atnsimulator(
        atn: &'static ATN,
        decision_to_dfa: &'static Vec<DFA>,
        shared_context_cache: &'static PredictionContextCache,
    ) -> BaseATNSimulator {
        BaseATNSimulator {
            atn,
            shared_context_cache,
            decision_to_dfa,
        }
    }
}

impl IATNSimulator for BaseATNSimulator {
    fn shared_context_cache(&self) -> &'static PredictionContextCache {
        self.shared_context_cache
    }

    fn atn(&self) -> &'static ATN {
        self.atn
    }

    fn decision_to_dfa(&self) -> &'static Vec<DFA> {
        self.decision_to_dfa
    }
}
