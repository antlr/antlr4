package antlr4

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
type LexerAction struct  {
    actionType int
    isPositionDependent bool
}

func NewLexerAction(action int) *LexerAction {
    la := new(LexerAction)
    la.InitLexerAction(action)
    return la
}

func (la *LexerAction) InitLexerAction(action int){
    la.actionType = action
    la.isPositionDependent = false
}

func (this *LexerAction) execute(lexer *Lexer) {
    panic("Not implemented")
}

func (this *LexerAction) hashString() string {
    return "" + this.actionType
}

func (this *LexerAction) equals(other *LexerAction) {
    return this == other
}

//
// Implements the {@code skip} lexer action by calling {@link Lexer//skip}.
//
// <p>The {@code skip} command does not have any parameters, so this action is
// implemented as a singleton instance exposed by {@link //INSTANCE}.</p>
type LexerSkipAction struct {
    LexerAction
}

func NewLexerSkipAction() *LexerSkipAction {
    la := new(LexerAction)
    la.InitLexerAction(LexerActionTypeSKIP)
	return la
}

// Provides a singleton instance of this parameterless lexer action.
var exerSkipActionINSTANCE = NewLexerSkipAction()

func (this *LexerSkipAction) execute(lexer *Lexer) {
    lexer.skip()
}

func (this *LexerSkipAction) toString() string {
	return "skip"
}

//  Implements the {@code type} lexer action by calling {@link Lexer//setType}
// with the assigned type.
type LexerTypeAction struct {
	LexerAction

    _type int
}

func NewLexerTypeAction(_type int) *LexerTypeAction {
	this := new(LexerTypeAction)
	this.InitLexerAction( LexerActionTypeTYPE )
	this._type = _type
	return this
}

func (this *LexerTypeAction) execute(lexer *Lexer) {
    lexer._type = this._type
}

func (this *LexerTypeAction) hashString() string {
	return "" + this.actionType + this._type
}


func (this *LexerTypeAction) equals(other *LexerAction) bool {
    if(this == other) {
        return true
    } else if _, ok := other.(*LexerTypeAction); !ok {
        return false
    } else {
        return this._type == other.(*LexerTypeAction)._type
    }
}

func (this *LexerTypeAction) toString() string {
    return "actionType(" + this._type + ")"
}

// Implements the {@code pushMode} lexer action by calling
// {@link Lexer//pushMode} with the assigned mode.
type LexerPushModeAction struct {
	LexerAction

    mode int
}

func NewLexerPushModeAction(mode int) *LexerPushModeAction {

    this := new(LexerPushModeAction)
    this.InitLexerAction( LexerActionTypePUSH_MODE )

    this.mode = mode
    return this
}

// <p>This action is implemented by calling {@link Lexer//pushMode} with the
// value provided by {@link //getMode}.</p>
func (this *LexerPushModeAction) execute(lexer *Lexer) {
    lexer.pushMode(this.mode)
}

func (this *LexerPushModeAction) hashString() string {
    return "" + this.actionType + this.mode
}

func (this *LexerPushModeAction) equals(other *LexerAction) bool {
    if (this == other) {
        return true
    } else if  _, ok := other.(*LexerPushModeAction); !ok {
        return false
    } else {
        return this.mode == other.(*LexerPushModeAction).mode
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
    LexerAction
}

func NewLexerPopModeAction() *LexerPopModeAction {

    this := new(LexerPopModeAction)

    this.InitLexerAction( LexerActionTypePOP_MODE )

	return this
}

var LexerPopModeActionINSTANCE = NewLexerPopModeAction()

// <p>This action is implemented by calling {@link Lexer//popMode}.</p>
func (this *LexerPopModeAction) execute(lexer *Lexer) {
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
    LexerAction
}

func NewLexerMoreAction() *LexerMoreAction {
    this := new(LexerModeAction)
    this.InitLexerAction( LexerActionTypeMORE )

	return this
}

var LexerMoreActionINSTANCE = NewLexerMoreAction()

// <p>This action is implemented by calling {@link Lexer//popMode}.</p>
func (this *LexerMoreAction) execute(lexer *Lexer) {
    lexer.more()
}

func (this *LexerMoreAction) toString() string {
    return "more"
}


// Implements the {@code mode} lexer action by calling {@link Lexer//mode} with
// the assigned mode.
type LexerModeAction struct {
	LexerAction

    mode int
}

func NewLexerModeAction(mode int) *LexerModeAction {
	this := new(LexerModeAction)
	this.InitLexerAction( LexerActionTypeMODE )
    this.mode = mode
    return this
}

// <p>This action is implemented by calling {@link Lexer//mode} with the
// value provided by {@link //getMode}.</p>
func (this *LexerModeAction) execute(lexer *Lexer) {
    lexer.mode(this.mode)
}

func (this *LexerModeAction) hashString() string {
	return "" + this.actionType + this.mode
}

func (this *LexerModeAction) equals(other *LexerAction) bool {
    if (this == other) {
        return true
    } else if  _, ok := other.(*LexerModeAction); !ok {
        return false
    } else {
        return this.mode == other.(*LexerModeAction).mode
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

type LexerCustomAction struct {
	LexerAction
    ruleIndex, actionIndex int
}

func NewLexerCustomAction(ruleIndex, actionIndex int) *LexerCustomAction {
	this := new(LexerCustomAction)
	this.InitLexerAction( LexerActionTypeCUSTOM )
    this.ruleIndex = ruleIndex
    this.actionIndex = actionIndex
    this.isPositionDependent = true
    return this
}

// <p>Custom actions are implemented by calling {@link Lexer//action} with the
// appropriate rule and action indexes.</p>
func (this *LexerCustomAction) execute(lexer *Lexer) {
    lexer.action(nil, this.ruleIndex, this.actionIndex)
}

func (this *LexerCustomAction) hashString() string {
    return "" + this.actionType + this.ruleIndex + this.actionIndex
}

func (this *LexerCustomAction) equals(other *LexerAction) bool {
    if (this == other) {
        return true
    } else if  _, ok := other.(*LexerCustomAction); !ok {
        return false
    } else {
        return this.ruleIndex == other.(*LexerCustomAction).ruleIndex && this.actionIndex == other.(*LexerCustomAction).actionIndex
    }
}

// Implements the {@code channel} lexer action by calling
// {@link Lexer//setChannel} with the assigned channel.
// Constructs a New{@code channel} action with the specified channel value.
// @param channel The channel value to pass to {@link Lexer//setChannel}.
type LexerChannelAction struct {
	LexerAction

    channel int
}

func NewLexerChannelAction(channel int) *LexerChannelAction {
	this := new(LexerChannelAction)
	this.InitLexerAction( LexerActionTypeCHANNEL )
    this.channel = channel
    return this
}

//LexerChannelAction.prototype = Object.create(LexerAction.prototype)
//LexerChannelAction.prototype.constructor = LexerChannelAction

// <p>This action is implemented by calling {@link Lexer//setChannel} with the
// value provided by {@link //getChannel}.</p>
func (this *LexerChannelAction) execute(lexer *Lexer) {
    lexer._channel = this.channel
}

func (this *LexerChannelAction) hashString() string {
    return "" + this.actionType + this.channel
}

func (this *LexerChannelAction) equals(other *LexerAction) bool {
    if (this == other) {
        return true
    } else if _, ok := other.(*LexerChannelAction); !ok {
        return false
    } else {
        return this.channel == other.(*LexerChannelAction).channel
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
type LexerIndexedCustomAction struct {
	LexerAction

    offset int
    action *LexerAction
    isPositionDependent bool
}

func NewLexerIndexedCustomAction(offset int, action *LexerAction) *LexerIndexedCustomAction {

    this := new(LexerIndexedCustomAction)
    this.InitLexerAction( action.actionType )

    this.offset = offset
    this.action = action
    this.isPositionDependent = true

    return this
}

// <p>This method calls {@link //execute} on the result of {@link //getAction}
// using the provided {@code lexer}.</p>
func (this *LexerIndexedCustomAction) execute(lexer *Lexer) {
    // assume the input stream position was properly set by the calling code
    this.action.execute(lexer)
}

func (this *LexerIndexedCustomAction) hashString() string {
    return "" + this.actionType + this.offset + this.action
}

func (this *LexerIndexedCustomAction) equals(other *LexerAction) bool {
    if (this == other) {
        return true
    } else if _, ok := other.(*LexerIndexedCustomAction); !ok {
        return false
    } else {
        return this.offset == other.(*LexerIndexedCustomAction).offset && this.action == other.(*LexerIndexedCustomAction).action
    }
}











