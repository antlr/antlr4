module antlr.v4.runtime.atn.Operator;

import antlr.v4.runtime.atn.SemanticContext;

/**
 * This is the base class for semantic context "operators", which operate on
 * a collection of semantic context "operands".
 *
 * @since 4.3
 */
abstract class Operator : SemanticContext
{

    /**
     * Gets the operands for the semantic context operator.
     *
     * @return a collection of {@link SemanticContext} operands for the
     * operator.
     *
     * @since 4.3
     */
    abstract public SemanticContext[] getOperands();

}
