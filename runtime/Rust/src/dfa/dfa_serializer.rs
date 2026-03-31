use std::fmt::{Display, Formatter};

use crate::atn_config_set::ConfigSet;

use super::dfa_state::DFAState;
use super::DFA;

pub struct DFASerializer<'sim, 'a, CS>
where
    CS: ConfigSet + 'sim,
{
    dfa: &'a DFA<'sim, CS>,
    get_edge_label: &'a dyn Fn(usize) -> String,
}

impl<CS: ConfigSet> Display for DFASerializer<'_, '_, CS> {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        let states = self.dfa.get_states();
        for source in states {
            for (i, target) in source.enumerate_edges() {
                f.write_fmt(format_args!(
                    "{}-{}->{}\n",
                    self.get_state_string(source),
                    (self.get_edge_label)(i),
                    self.get_state_string(target)
                ))?;
            }
        }

        Ok(())
    }
}

impl<'sim, 'a, CS: ConfigSet> DFASerializer<'sim, 'a, CS> {
    pub fn new(
        dfa: &'a DFA<'sim, CS>,
        get_edge_label: &'a dyn Fn(usize) -> String,
    ) -> DFASerializer<'sim, 'a, CS> {
        DFASerializer {
            dfa,
            get_edge_label,
        }
    }

    fn get_state_string(&self, state: &DFAState<CS>) -> String {
        let mut base_str = format!(
            "{}s{}{}",
            if state.is_accept_state { ":" } else { "" },
            state.state_number,
            if state.requires_full_context { "^" } else { "" },
        );
        if state.is_accept_state {
            base_str = if !state.predicates.is_empty() {
                unimplemented!()
            //                format!("{}=>{:?}", base_str, state.predicates)
            } else {
                format!("{}=>{}", base_str, state.prediction)
            };
        }
        base_str
    }
}
