module antlr.v4.runtime.atn.PredictionModeConst;

/**
 * This enumeration defines the prediction modes available in ANTLR 4 along with
 * utility methods for analyzing configuration sets for conflicts and/or
 * ambiguities.
 *******************
 * SSL;
 * The SLL(*) prediction mode. This prediction mode ignores the current
 * parser context when making predictions. This is the fastest prediction
 * mode, and provides correct results for many grammars. This prediction
 * mode is more powerful than the prediction mode provided by ANTLR 3, but
 * may result in syntax errors for grammar and input combinations which are
 * not SLL.
 *
 * <p>
 * When using this prediction mode, the parser will either return a correct
 * parse tree (i.e. the same parse tree that would be returned with the
 * {@link #LL} prediction mode), or it will report a syntax error. If a
 * syntax error is encountered when using the {@link #SLL} prediction mode,
 * it may be due to either an actual syntax error in the input or indicate
 * that the particular combination of grammar and input requires the more
 * powerful {@link #LL} prediction abilities to complete successfully.</p>
 *
 * <p>
 * This prediction mode does not provide any guarantees for prediction
 * behavior for syntactically-incorrect inputs.</p>
 *
 *******************
 * LL;
 * The LL(*) prediction mode. This prediction mode allows the current parser
 * context to be used for resolving SLL conflicts that occur during
 * prediction. This is the fastest prediction mode that guarantees correct
 * parse results for all combinations of grammars with syntactically correct
 * inputs.
 *
 * <p>
 * When using this prediction mode, the parser will make correct decisions
 * for all syntactically-correct grammar and input combinations. However, in
 * cases where the grammar is truly ambiguous this prediction mode might not
 * report a precise answer for <em>exactly which</em> alternatives are
 * ambiguous.</p>
 *
 * <p>
 * This prediction mode does not provide any guarantees for prediction
 * behavior for syntactically-incorrect inputs.</p>
 *
 *******************
 * LL_EXACT_AMBIG_DETECTION;
 * The LL(*) prediction mode with exact ambiguity detection. In addition to
 * the correctness guarantees provided by the {@link #LL} prediction mode,
 * this prediction mode instructs the prediction algorithm to determine the
 * complete and exact set of ambiguous alternatives for every ambiguous
 * decision encountered while parsing.
 *
 * <p>
 * This prediction mode may be used for diagnosing ambiguities during
 * grammar development. Due to the performance overhead of calculating sets
 * of ambiguous alternatives, this prediction mode should be avoided when
 * the exact results are not necessary.</p>
 *
 * <p>
 * This prediction mode does not provide any guarantees for prediction
 * behavior for syntactically-incorrect inputs.</p>
 */
enum PredictionModeConst
{
    SLL,
    LL,
    LL_EXACT_AMBIG_DETECTION,
}
