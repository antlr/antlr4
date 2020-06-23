module antlr.v4.runtime.tree.StartRuleDoesNotConsumeFullPattern;

import antlr.v4.runtime.RuntimeException;

/**
 * @uml
 * Fixes https://github.com/antlr/antlr4/issues/413
 * // "Tree pattern compilation doesn't check for a complete parse"
 */
class StartRuleDoesNotConsumeFullPattern : RuntimeException
{

}
