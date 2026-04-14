use bit_set::BitSet;
use fxhash::FxBuildHasher;
use hashbrown::HashMap;

use crate::atn::INVALID_ALT;
use crate::atn_config::ATNConfig;
use crate::atn_config_set::MutableATNConfigSet;
use crate::atn_state::ATNStateRef;
use crate::prediction_context::{NoopHasherBuilder, PredictionContextRef};
use crate::semantic_context::SemanticContext;

/// This enum defines the prediction modes available in ANTLR 4 along with
/// utility methods for analyzing configuration sets for conflicts and/or
/// ambiguities.
///
/// It is set through `ParserATNSimulator::
#[allow(non_camel_case_types)]
#[derive(Eq, PartialEq, Copy, Clone, Debug)]
pub enum PredictionMode {
    /// The SLL(*) prediction mode. This prediction mode ignores the current
    /// parser context when making predictions. This is the fastest prediction
    /// mode, and provides correct results for many grammars. This prediction
    /// mode is more powerful than the prediction mode provided by ANTLR 3, but
    /// may result in syntax errors for grammar and input combinations which are
    /// not SLL.
    ///
    /// <p>
    /// When using this prediction mode, the parser will either return a correct
    /// parse tree (i.e. the same parse tree that would be returned with the
    /// {@link #LL} prediction mode), or it will report a syntax error. If a
    /// syntax error is encountered when using the {@link #SLL} prediction mode,
    /// it may be due to either an actual syntax error in the input or indicate
    /// that the particular combination of grammar and input requires the more
    /// powerful {@link #LL} prediction abilities to complete successfully.</p>
    ///
    /// <p>
    /// This prediction mode does not provide any guarantees for prediction
    /// behavior for syntactically-incorrect inputs.</p>
    ///
    SLL = 0,
    ///
    /// The LL(*) prediction mode. This prediction mode allows the current parser
    /// context to be used for resolving SLL conflicts that occur during
    /// prediction. This is the fastest prediction mode that guarantees correct
    /// parse results for all combinations of grammars with syntactically correct
    /// inputs.
    ///
    /// <p>
    /// When using this prediction mode, the parser will make correct decisions
    /// for all syntactically-correct grammar and input combinations. However, in
    /// cases where the grammar is truly ambiguous this prediction mode might not
    /// report a precise answer for <em>exactly which</em> alternatives are
    /// ambiguous.</p>
    ///
    /// <p>
    /// This prediction mode does not provide any guarantees for prediction
    /// behavior for syntactically-incorrect inputs.</p>
    ///
    LL,
    ///
    /// The LL(*) prediction mode with exact ambiguity detection. In addition to
    /// the correctness guarantees provided by the {@link #LL} prediction mode,
    /// this prediction mode instructs the prediction algorithm to determine the
    /// complete and exact set of ambiguous alternatives for every ambiguous
    /// decision encountered while parsing.
    ///
    /// <p>
    /// This prediction mode may be used for diagnosing ambiguities during
    /// grammar development. Due to the performance overhead of calculating sets
    /// of ambiguous alternatives, this prediction mode should be avoided when
    /// the exact results are not necessary.</p>
    ///
    /// <p>
    /// This prediction mode does not provide any guarantees for prediction
    /// behavior for syntactically-incorrect inputs.</p>
    ///
    LL_EXACT_AMBIG_DETECTION,
}

impl PredictionMode {
    //todo move everything here
}

//
//
pub(crate) fn has_sll_conflict_terminating_prediction<'ephemeral>(
    ephemerals: &'ephemeral bumpalo::Bump,
    mode: PredictionMode,
    configs: &MutableATNConfigSet<'ephemeral>,
) -> bool {
    //    if all_configs_in_rule_stop_states(configs) {
    //        return true          checked outside
    //    }

    if mode == PredictionMode::SLL && configs.has_semantic_context() {
        let mut dup = MutableATNConfigSet::new(ephemerals, true);
        configs.get_items().for_each(|it| {
            let c = ATNConfig::new(it.get_state(), it.get_alt(), it.get_context())
                .with_semantic_context(&SemanticContext::NONE);
            dup.add(c);
        });

        let altsets = get_conflicting_alt_subsets(ephemerals, &dup);

        has_conflicting_alt_set(altsets.values())
            && !has_state_associated_with_one_alt(ephemerals, &dup)
    } else {
        let altsets = get_conflicting_alt_subsets(ephemerals, configs);

        has_conflicting_alt_set(altsets.values())
            && !has_state_associated_with_one_alt(ephemerals, configs)
    }
}

//fn all_configs_in_rule_stop_states(configs: &ATNConfigSet) -> bool {
//    for co
//}

pub(crate) fn resolves_to_just_one_viable_alt<'a>(
    altsets: impl Iterator<Item = &'a BitSet>,
) -> i32 {
    get_single_viable_alt(altsets)
}

pub(crate) fn all_subsets_conflict<'a>(altsets: impl Iterator<Item = &'a BitSet>) -> bool {
    !has_non_conflicting_alt_set(altsets)
}

pub(crate) fn all_subsets_equal<'a>(mut altsets: impl Iterator<Item = &'a BitSet>) -> bool {
    let first = altsets.next();
    altsets.all(|it| it == first.unwrap())
}

fn has_non_conflicting_alt_set<'a>(mut altsets: impl Iterator<Item = &'a BitSet>) -> bool {
    altsets.any(|it| it.len() == 1)
}

fn has_conflicting_alt_set<'a>(altsets: impl Iterator<Item = &'a BitSet>) -> bool {
    for alts in altsets {
        if alts.len() > 1 {
            return true;
        }
    }
    false
}

//fn get_unique_alt(altsets: &[BitSet]) -> int { unimplemented!() }
//
pub(crate) fn get_alts(altsets: impl Iterator<Item = BitSet>) -> BitSet {
    altsets.fold(BitSet::new(), |mut acc, it| {
        acc.extend(&it);
        acc
    })
}

#[derive(Eq, PartialEq)]
pub(crate) struct KeyWrapper<'a> {
    state: ATNStateRef,
    context: PredictionContextRef<'a>,
}

impl<'a> std::hash::Hash for KeyWrapper<'a> {
    fn hash<H: std::hash::Hasher>(&self, state: &mut H) {
        let ptr = self.state.as_usize() as u64;
        state.write_u64(ptr ^ (self.context.hash_code() as u64));
    }
}

pub(crate) fn get_conflicting_alt_subsets<'ephemeral>(
    ephemerals: &'ephemeral bumpalo::Bump,
    configs: &MutableATNConfigSet<'ephemeral>,
) -> HashMap<KeyWrapper<'ephemeral>, BitSet, NoopHasherBuilder, &'ephemeral bumpalo::Bump> {
    let mut configs_to_alts: HashMap<KeyWrapper<'ephemeral>, BitSet, _, _> =
        HashMap::with_capacity_and_hasher_in(configs.length(), NoopHasherBuilder {}, ephemerals);
    for c in configs.get_items() {
        let alts = configs_to_alts
            .entry(KeyWrapper {
                state: c.get_state(),
                context: c.get_context().unwrap(),
            })
            .or_default();

        alts.insert(c.get_alt() as usize);
    }
    configs_to_alts
}

fn has_state_associated_with_one_alt(
    ephemerals: &bumpalo::Bump,
    configs: &MutableATNConfigSet,
) -> bool {
    let mut lookup =
        HashMap::with_capacity_and_hasher_in(configs.length(), FxBuildHasher::new(), ephemerals);

    for c in configs.get_items() {
        let state = c.get_state();
        if let Some((alt, more_than_one)) = lookup.get_mut(&state) {
            if *alt != c.get_alt() {
                *more_than_one = true;
            }
        } else {
            lookup.insert(state, (c.get_alt(), false));
        }
    }
    lookup
        .into_values()
        .any(|(_, more_than_one)| !more_than_one)
}

pub(crate) fn get_single_viable_alt<'a>(altsets: impl Iterator<Item = &'a BitSet>) -> i32 {
    let mut min_alt = INVALID_ALT as usize;
    for alt in altsets {
        let new_alt = alt.iter().next().unwrap();
        if min_alt == INVALID_ALT as usize {
            min_alt = new_alt;
        } else if min_alt != new_alt {
            return INVALID_ALT;
        }
    }
    min_alt as i32
}
