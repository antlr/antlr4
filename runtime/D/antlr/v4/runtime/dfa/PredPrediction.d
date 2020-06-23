module antlr.v4.runtime.dfa.PredPrediction;

import std.format;
import antlr.v4.runtime.atn.SemanticContext;

/**
 * TODO add class description
 */
class PredPrediction
{

    public SemanticContext pred;

    public int alt;

    public this(SemanticContext pred, int alt)
    {
	this.alt = alt;
        this.pred = pred;
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return format("(%1$s, %2$s)", pred, alt);
    }

}
