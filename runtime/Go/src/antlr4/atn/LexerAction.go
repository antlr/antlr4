package atn

type LexerActionType struct {
}

const (
    LexerActionTypeCHANNEL = 0     //The type of a {@link LexerChannelAction} action.
    LexerActionTypeCUSTOM = 1      //The type of a {@link LexerCustomAction} action.
    LexerActionTypeMODE = 2        //The type of a {@link LexerModeAction} action.
    LexerActionTypeMORE = 3        //The type of a {@link LexerMoreAction} action.
    LexerActionTypePOP_MODE = 4    //The type of a {@link LexerPopModeAction} action.
    LexerActionTypePUSH_MODE = 5   //The type of a {@link LexerPushModeAction} action.
    LexerActionTypeSKIP = 6        //The type of a {@link LexerSkipAction} action.
    LexerActionTypeTYPE = 7        //The type of a {@link LexerTypeAction} action.
)

func LexerAction(action) {
    this.actionType = action
    this.isPositionDependent = false
    return this
}

func (this *LexerAction) hashString() {
    return "" + this.actionType
}

func (this *LexerAction) equals(other) {
    return this == other
}



//
// Implements the {@code skip} lexer action by calling {@link Lexer//skip}.
//
// <p>The {@code skip} command does not have any parameters, so this action is
// implemented as a singleton instance exposed by {@link //INSTANCE}.</p>
type LexerSkipAction struct {
	LexerAction.call(this, LexerActionTypeSKIP)
	return this
}

//LexerSkipAction.prototype = Object.create(LexerAction.prototype)
//LexerSkipAction.prototype.constructor = LexerSkipAction

// Provides a singleton instance of this parameterless lexer action.
LexerSkipAction.INSTANCE = NewLexerSkipAction()

func (this *LexerSkipAction) execute(lexer) {
    lexer.skip()
}

func (this *LexerSkipAction) toString() string {
	return "skip"
}

//  Implements the {@code type} lexer action by calling {@link Lexer//setType}
// with the assigned type.
func LexerTypeAction(type) {
	LexerAction.call(this, LexerActionTypeTYPE)
	this.type = type
	return this
}

//LexerTypeAction.prototype = Object.create(LexerAction.prototype)
//LexerTypeAction.prototype.constructor = LexerTypeAction

func (this *LexerTypeAction) execute(lexer) {
    lexer.type = this.type
}

func (this *LexerTypeAction) hashString() {
	return "" + this.actionType + this.type
}


func (this *LexerTypeAction) equals(other) {
    if(this == other) {
        return true
    } else if (! (other instanceof LexerTypeAction)) {
        return false
    } else {
        return this.type == other.type
    }
}

func (this *LexerTypeAction) toString() string {
    return "type(" + this.type + ")"
}

// Implements the {@code pushMode} lexer action by calling
// {@link Lexer//pushMode} with the assigned mode.
func LexerPushModeAction(mode) {
	LexerAction.call(this, LexerActionTypePUSH_MODE)
    this.mode = mode
    return this
}

//LexerPushModeAction.prototype = Object.create(LexerAction.prototype)
//LexerPushModeAction.prototype.constructor = LexerPushModeAction

// <p>This action is implemented by calling {@link Lexer//pushMode} with the
// value provided by {@link //getMode}.</p>
func (this *LexerPushModeAction) execute(lexer) {
    lexer.pushMode(this.mode)
}

func (this *LexerPushModeAction) hashString() {
    return "" + this.actionType + this.mode
}

func (this *LexerPushModeAction) equals(other) {
    if (this == other) {
        return true
    } else if (! (other instanceof LexerPushModeAction)) {
        return false
    } else {
        return this.mode == other.mode
    }
}

func (this *LexerPushModeAction) toString() string {
	return "pushMode(" + this.mode + ")"
}


// Implements the {@code popMode} lexer action by calling {@link Lexer//popMode}.
//
// <p>The {@code popMode} command does not have any parameters, so this action is
// implemented as a singleton instance exposed by {@link //INSTANCE}.</p>
type LexerPopModeAction struct {
	LexerAction.call(this,LexerActionTypePOP_MODE)
	return this
}

//LexerPopModeAction.prototype = Object.create(LexerAction.prototype)
//LexerPopModeAction.prototype.constructor = LexerPopModeAction

LexerPopModeAction.INSTANCE = NewLexerPopModeAction()

// <p>This action is implemented by calling {@link Lexer//popMode}.</p>
func (this *LexerPopModeAction) execute(lexer) {
    lexer.popMode()
}

func (this *LexerPopModeAction) toString() string {
	return "popMode"
}

// Implements the {@code more} lexer action by calling {@link Lexer//more}.
//
// <p>The {@code more} command does not have any parameters, so this action is
// implemented as a singleton instance exposed by {@link //INSTANCE}.</p>
type LexerMoreAction struct {
	LexerAction.call(this, LexerActionTypeMORE)
	return this
}

//LexerMoreAction.prototype = Object.create(LexerAction.prototype)
//LexerMoreAction.prototype.constructor = LexerMoreAction

LexerMoreAction.INSTANCE = NewLexerMoreAction()

// <p>This action is implemented by calling {@link Lexer//popMode}.</p>
func (this *LexerMoreAction) execute(lexer) {
    lexer.more()
}

func (this *LexerMoreAction) toString() string {
    return "more"
}


// Implements the {@code mode} lexer action by calling {@link Lexer//mode} with
// the assigned mode.
func LexerModeAction(mode) {
	LexerAction.call(this, LexerActionTypeMODE)
    this.mode = mode
    return this
}

//LexerModeAction.prototype = Object.create(LexerAction.prototype)
//LexerModeAction.prototype.constructor = LexerModeAction

// <p>This action is implemented by calling {@link Lexer//mode} with the
// value provided by {@link //getMode}.</p>
func (this *LexerModeAction) execute(lexer) {
    lexer.mode(this.mode)
}

func (this *LexerModeAction) hashString() {
	return "" + this.actionType + this.mode
}

func (this *LexerModeAction) equals(other) {
    if (this == other) {
        return true
    } else if (! (other instanceof LexerModeAction)) {
        return false
    } else {
        return this.mode == other.mode
    }
}

func (this *LexerModeAction) toString() string {
    return "mode(" + this.mode + ")"
}

// Executes a custom lexer action by calling {@link Recognizer//action} with the
// rule and action indexes assigned to the custom action. The implementation of
// a custom action is added to the generated code for the lexer in an override
// of {@link Recognizer//action} when the grammar is compiled.
//
// <p>This class may represent embedded actions created with the <code>{...}</code>
// syntax in ANTLR 4, as well as actions created for lexer commands where the
// command argument could not be evaluated when the grammar was compiled.</p>


    // Constructs a custom lexer action with the specified rule and action
    // indexes.
    //
    // @param ruleIndex The rule index to use for calls to
    // {@link Recognizer//action}.
    // @param actionIndex The action index to use for calls to
    // {@link Recognizer//action}.

func LexerCustomAction(ruleIndex, actionIndex) {
	LexerAction.call(this, LexerActionTypeCUSTOM)
    this.ruleIndex = ruleIndex
    this.actionIndex = actionIndex
    this.isPositionDependent = true
    return this
}

//LexerCustomAction.prototype = Object.create(LexerAction.prototype)
//LexerCustomAction.prototype.constructor = LexerCustomAction

// <p>Custom actions are implemented by calling {@link Lexer//action} with the
// appropriate rule and action indexes.</p>
func (this *LexerCustomAction) execute(lexer) {
    lexer.action(nil, this.ruleIndex, this.actionIndex)
}

func (this *LexerCustomAction) hashString() {
    return "" + this.actionType + this.ruleIndex + this.actionIndex
}

func (this *LexerCustomAction) equals(other) {
    if (this == other) {
        return true
    } else if (! (other instanceof LexerCustomAction)) {
        return false
    } else {
        return this.ruleIndex == other.ruleIndex && this.actionIndex == other.actionIndex
    }
}

// Implements the {@code channel} lexer action by calling
// {@link Lexer//setChannel} with the assigned channel.
// Constructs a New{@code channel} action with the specified channel value.
// @param channel The channel value to pass to {@link Lexer//setChannel}.
func LexerChannelAction(channel) {
	LexerAction.call(this, LexerActionTypeCHANNEL)
    this.channel = channel
    return this
}

//LexerChannelAction.prototype = Object.create(LexerAction.prototype)
//LexerChannelAction.prototype.constructor = LexerChannelAction

// <p>This action is implemented by calling {@link Lexer//setChannel} with the
// value provided by {@link //getChannel}.</p>
func (this *LexerChannelAction) execute(lexer) {
    lexer._channel = this.channel
}

func (this *LexerChannelAction) hashString() {
    return "" + this.actionType + this.channel
}

func (this *LexerChannelAction) equals(other) {
    if (this == other) {
        return true
    } else if (! (other instanceof LexerChannelAction)) {
        return false
    } else {
        return this.channel == other.channel
    }
}

func (this *LexerChannelAction) toString() string {
    return "channel(" + this.channel + ")"
}

// This implementation of {@link LexerAction} is used for tracking input offsets
// for position-dependent actions within a {@link LexerActionExecutor}.
//
// <p>This action is not serialized as part of the ATN, and is only required for
// position-dependent lexer actions which appear at a location other than the
// end of a rule. For more information about DFA optimizations employed for
// lexer actions, see {@link LexerActionExecutor//append} and
// {@link LexerActionExecutor//fixOffsetBeforeMatch}.</p>

// Constructs a Newindexed custom action by associating a character offset
// with a {@link LexerAction}.
//
// <p>Note: This class is only required for lexer actions for which
// {@link LexerAction//isPositionDependent} returns {@code true}.</p>
//
// @param offset The offset into the input {@link CharStream}, relative to
// the token start index, at which the specified lexer action should be
// executed.
// @param action The lexer action to execute at a particular offset in the
// input {@link CharStream}.
func LexerIndexedCustomAction(offset, action) {
	LexerAction.call(this, action.actionType)
    this.offset = offset
    this.action = action
    this.isPositionDependent = true
    return this
}

//LexerIndexedCustomAction.prototype = Object.create(LexerAction.prototype)
//LexerIndexedCustomAction.prototype.constructor = LexerIndexedCustomAction

// <p>This method calls {@link //execute} on the result of {@link //getAction}
// using the provided {@code lexer}.</p>
func (this *LexerIndexedCustomAction) execute(lexer) {
    // assume the input stream position was properly set by the calling code
    this.action.execute(lexer)
}

func (this *LexerIndexedCustomAction) hashString() {
    return "" + this.actionType + this.offset + this.action
}

func (this *LexerIndexedCustomAction) equals(other) {
    if (this == other) {
        return true
    } else if (! (other instanceof LexerIndexedCustomAction)) {
        return false
    } else {
        return this.offset == other.offset && this.action == other.action
    }
}











