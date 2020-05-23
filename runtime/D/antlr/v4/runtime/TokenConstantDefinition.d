module antlr.v4.runtime.TokenConstantDefinition;

import antlr.v4.runtime.IntStreamConstant;

/**
 * @uml
 * Token constant definitions
 */
class TokenConstantDefinition
{

    enum int INVALID_TYPE = 0;

    /**
     * @uml
     * During lookahead operations, this "token" signifies we hit rule end ATN state
     * and did not follow it despite needing to.
     */
    enum int EPSILON = -2;

    enum int MIN_USER_TOKEN_TYPE = 1;

    enum int EOF = IntStreamConstant.EOF;

    /**
     * @uml
     * All tokens go to the parser (unless skip() is called in that rule)
     * on a particular "channel".  The parser tunes to a particular channel
     * so that whitespace etc... can go to the parser on a "hidden" channel.
     */
    enum int DEFAULT_CHANNEL = 0;

    /**
     * @uml
     * Anything on different channel than DEFAULT_CHANNEL is not parsed
     * by parser.
     */
    enum int HIDDEN_CHANNEL = 1;

    /**
     * @uml
     * This is the minimum constant value which can be assigned to a
     * ser-defined token channel.
     *
     * <p>
     * The non-negative numbers less than {@link #MIN_USER_CHANNEL_VALUE} are
     * assigned to the predefined channels {@link #DEFAULT_CHANNEL} and
     *  {@link #HIDDEN_CHANNEL}.</p>
     *
     *  @see Token#getChannel()
     */
    enum int MIN_USER_CHANNEL_VALUE = 2;

}
