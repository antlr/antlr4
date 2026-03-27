use std::fmt::{Debug, Error, Formatter};

use crate::atn::ATN;
use crate::atn_config_set::ConfigSet;
use crate::dfa::DFA;
use crate::prediction_context::PredictionContextCache;

pub trait IATNSimulator<CS>
where
    CS: ConfigSet + 'static,
{
    fn shared_context_cache(&self) -> &PredictionContextCache;
    fn atn(&self) -> &ATN;
    fn decision_to_dfa(&self) -> &Vec<DFA<CS>>;
}

pub struct BaseATNSimulator<CS>
where
    CS: ConfigSet + 'static,
{
    pub atn: &'static ATN,
    pub shared_context_cache: &'static PredictionContextCache,
    pub decision_to_dfa: &'static Vec<DFA<CS>>,
}

impl<CS: ConfigSet> Debug for BaseATNSimulator<CS> {
    fn fmt(&self, f: &mut Formatter<'_>) -> Result<(), Error> {
        f.write_str("BaseATNSimulator { .. }")
    }
}

impl<CS: ConfigSet> BaseATNSimulator<CS> {
    pub fn new_base_atnsimulator(
        atn: &'static ATN,
        decision_to_dfa: &'static Vec<DFA<CS>>,
        shared_context_cache: &'static PredictionContextCache,
    ) -> BaseATNSimulator<CS> {
        BaseATNSimulator {
            atn,
            shared_context_cache,
            decision_to_dfa,
        }
    }
}

impl<CS: ConfigSet> IATNSimulator<CS> for BaseATNSimulator<CS> {
    fn shared_context_cache(&self) -> &'static PredictionContextCache {
        self.shared_context_cache
    }

    fn atn(&self) -> &'static ATN {
        self.atn
    }

    fn decision_to_dfa(&self) -> &'static Vec<DFA<CS>> {
        self.decision_to_dfa
    }
}
