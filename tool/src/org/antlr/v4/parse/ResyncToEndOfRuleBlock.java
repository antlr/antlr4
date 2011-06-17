package org.antlr.v4.parse;

/** Used to throw us out of deeply nested element back to end of a rule's
 *  alt list. Note it's not under RecognitionException.
 */
public class ResyncToEndOfRuleBlock extends RuntimeException {
}
