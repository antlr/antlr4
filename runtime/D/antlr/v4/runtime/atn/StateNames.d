module antlr.v4.runtime.atn.StateNames;

/**
 * @uml
 * Type: ushort
 */
enum StateNames : ushort
{
    INVALID,
    BASIC,
    RULE_START,
    BLOCK_START,
    PLUS_BLOCK_START,
    STAR_BLOCK_START,
    TOKEN_START,
    RULE_STOP,
    BLOCK_END,
    STAR_LOOP_BACK,
    STAR_LOOP_ENTRY,
    PLUS_LOOP_BACK,
    LOOP_END,
}
