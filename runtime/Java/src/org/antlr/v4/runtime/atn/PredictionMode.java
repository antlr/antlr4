package org.antlr.v4.runtime.atn;

public enum PredictionMode {
	/** Do only local context prediction (SLL(k) style) and using
	 *  heuristic which almost always works but is much faster
	 *  than precise answer.
	 */
	SLL(1),

	/** Full LL that always gets right answer */
	LL(2),

	/** Tell the full LL prediction algorithm to pursue lookahead until
	 *  it has uniquely predicted alternative without conflict or it's
	 *  certain that it's found and ambiguous input sequence.  For speed
	 *  reasons, we terminate the prediction process early when this
	 *  variable is false. When true, the prediction process will
	 *  continue looking for the exact ambiguous sequence even if
	 *  it has already figured out which alternative to predict.
	 */
	LL_EXACT_AMBIG(4);
	int v;

	private PredictionMode(int v) {
		this.v = v;
	}
}
