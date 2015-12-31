package antlr4

import "strconv"

const (
	LexerActionTypeChannel  = 0 //The type of a {@link LexerChannelAction} action.
	LexerActionTypeCustom   = 1 //The type of a {@link LexerCustomAction} action.
	LexerActionTypeMode     = 2 //The type of a {@link LexerModeAction} action.
	LexerActionTypeMore     = 3 //The type of a {@link LexerMoreAction} action.
	LexerActionTypePopMode  = 4 //The type of a {@link LexerPopModeAction} action.
	LexerActionTypePushMode = 5 //The type of a {@link LexerPushModeAction} action.
	LexerActionTypeSkip     = 6 //The type of a {@link LexerSkipAction} action.
	LexerActionTypeType     = 7 //The type of a {@link LexerTypeAction} action.
)

type LexerAction interface {
	getActionType() int
	getIsPositionDependent() bool
	execute(lexer Lexer)
	Hash() string
	equals(other LexerAction) bool
}

type BaseLexerAction struct {
	actionType          int
	isPositionDependent bool
}

func NewBaseLexerAction(action int) *BaseLexerAction {
	la := new(BaseLexerAction)

	la.actionType = action
	la.isPositionDependent = false

	return la
}

func (this *BaseLexerAction) execute(lexer Lexer) {
	panic("Not implemented")
}

func (this *BaseLexerAction) getActionType() int {
	return this.actionType
}

func (this *BaseLexerAction) getIsPositionDependent() bool {
	return this.isPositionDependent
}

func (this *BaseLexerAction) Hash() string {
	return strconv.Itoa(this.actionType)
}

func (this *BaseLexerAction) equals(other LexerAction) bool {
	return this == other
}

//
// Implements the {@code skip} lexer action by calling {@link Lexer//skip}.
//
// <p>The {@code skip} command does not have any parameters, so this action is
// implemented as a singleton instance exposed by {@link //INSTANCE}.</p>
type LexerSkipAction struct {
	*BaseLexerAction
}

func NewLexerSkipAction() *LexerSkipAction {
	la := new(LexerSkipAction)
	la.BaseLexerAction = NewBaseLexerAction(LexerActionTypeSkip)
	return la
}

// Provides a singleton instance of this parameterless lexer action.
var LexerSkipActionINSTANCE = NewLexerSkipAction()

func (this *LexerSkipAction) execute(lexer Lexer) {
	lexer.skip()
}

func (this *LexerSkipAction) String() string {
	return "skip"
}

//  Implements the {@code type} lexer action by calling {@link Lexer//setType}
// with the assigned type.
type LexerTypeAction struct {
	*BaseLexerAction

	_type int
}

func NewLexerTypeAction(_type int) *LexerTypeAction {
	this := new(LexerTypeAction)
	this.BaseLexerAction = NewBaseLexerAction(LexerActionTypeType)
	this._type = _type
	return this
}

func (this *LexerTypeAction) execute(lexer Lexer) {
	lexer.setType(this._type)
}

func (this *LexerTypeAction) Hash() string {
	return strconv.Itoa(this.actionType) + strconv.Itoa(this._type)
}

func (this *LexerTypeAction) equals(other LexerAction) bool {
	if this == other {
		return true
	} else if _, ok := other.(*LexerTypeAction); !ok {
		return false
	} else {
		return this._type == other.(*LexerTypeAction)._type
	}
}

func (this *LexerTypeAction) String() string {
	return "actionType(" + strconv.Itoa(this._type) + ")"
}

// Implements the {@code pushMode} lexer action by calling
// {@link Lexer//pushMode} with the assigned mode.
type LexerPushModeAction struct {
	*BaseLexerAction

	mode int
}

func NewLexerPushModeAction(mode int) *LexerPushModeAction {

	this := new(LexerPushModeAction)
	this.BaseLexerAction = NewBaseLexerAction(LexerActionTypePushMode)

	this.mode = mode
	return this
}

// <p>This action is implemented by calling {@link Lexer//pushMode} with the
// value provided by {@link //getMode}.</p>
func (this *LexerPushModeAction) execute(lexer Lexer) {
	lexer.pushMode(this.mode)
}

func (this *LexerPushModeAction) Hash() string {
	return strconv.Itoa(this.actionType) + strconv.Itoa(this.mode)
}

func (this *LexerPushModeAction) equals(other LexerAction) bool {
	if this == other {
		return true
	} else if _, ok := other.(*LexerPushModeAction); !ok {
		return false
	} else {
		return this.mode == other.(*LexerPushModeAction).mode
	}
}

func (this *LexerPushModeAction) String() string {
	return "pushMode(" + strconv.Itoa(this.mode) + ")"
}

// Implements the {@code popMode} lexer action by calling {@link Lexer//popMode}.
//
// <p>The {@code popMode} command does not have any parameters, so this action is
// implemented as a singleton instance exposed by {@link //INSTANCE}.</p>
type LexerPopModeAction struct {
	*BaseLexerAction
}

func NewLexerPopModeAction() *LexerPopModeAction {

	this := new(LexerPopModeAction)

	this.BaseLexerAction = NewBaseLexerAction(LexerActionTypePopMode)

	return this
}

var LexerPopModeActionINSTANCE = NewLexerPopModeAction()

// <p>This action is implemented by calling {@link Lexer//popMode}.</p>
func (this *LexerPopModeAction) execute(lexer Lexer) {
	lexer.popMode()
}

func (this *LexerPopModeAction) String() string {
	return "popMode"
}

// Implements the {@code more} lexer action by calling {@link Lexer//more}.
//
// <p>The {@code more} command does not have any parameters, so this action is
// implemented as a singleton instance exposed by {@link //INSTANCE}.</p>

type LexerMoreAction struct {
	*BaseLexerAction
}

func NewLexerMoreAction() *LexerModeAction {
	this := new(LexerModeAction)
	this.BaseLexerAction = NewBaseLexerAction(LexerActionTypeMore)

	return this
}

var LexerMoreActionINSTANCE = NewLexerMoreAction()

// <p>This action is implemented by calling {@link Lexer//popMode}.</p>
func (this *LexerMoreAction) execute(lexer Lexer) {
	lexer.more()
}

func (this *LexerMoreAction) String() string {
	return "more"
}

// Implements the {@code mode} lexer action by calling {@link Lexer//mode} with
// the assigned mode.
type LexerModeAction struct {
	*BaseLexerAction

	mode int
}

func NewLexerModeAction(mode int) *LexerModeAction {
	this := new(LexerModeAction)
	this.BaseLexerAction = NewBaseLexerAction(LexerActionTypeMode)
	this.mode = mode
	return this
}

// <p>This action is implemented by calling {@link Lexer//mode} with the
// value provided by {@link //getMode}.</p>
func (this *LexerModeAction) execute(lexer Lexer) {
	lexer.mode(this.mode)
}

func (this *LexerModeAction) Hash() string {
	return strconv.Itoa(this.actionType) + strconv.Itoa(this.mode)
}

func (this *LexerModeAction) equals(other LexerAction) bool {
	if this == other {
		return true
	} else if _, ok := other.(*LexerModeAction); !ok {
		return false
	} else {
		return this.mode == other.(*LexerModeAction).mode
	}
}

func (this *LexerModeAction) String() string {
	return "mode(" + strconv.Itoa(this.mode) + ")"
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
	*BaseLexerAction
	ruleIndex, actionIndex int
}

func NewLexerCustomAction(ruleIndex, actionIndex int) *LexerCustomAction {
	this := new(LexerCustomAction)
	this.BaseLexerAction = NewBaseLexerAction(LexerActionTypeCustom)
	this.ruleIndex = ruleIndex
	this.actionIndex = actionIndex
	this.isPositionDependent = true
	return this
}

// <p>Custom actions are implemented by calling {@link Lexer//action} with the
// appropriate rule and action indexes.</p>
func (this *LexerCustomAction) execute(lexer Lexer) {
	lexer.Action(nil, this.ruleIndex, this.actionIndex)
}

func (this *LexerCustomAction) Hash() string {
	return strconv.Itoa(this.actionType) + strconv.Itoa(this.ruleIndex) + strconv.Itoa(this.actionIndex)
}

func (this *LexerCustomAction) equals(other LexerAction) bool {
	if this == other {
		return true
	} else if _, ok := other.(*LexerCustomAction); !ok {
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
	*BaseLexerAction

	channel int
}

func NewLexerChannelAction(channel int) *LexerChannelAction {
	this := new(LexerChannelAction)
	this.BaseLexerAction = NewBaseLexerAction(LexerActionTypeChannel)
	this.channel = channel
	return this
}

// <p>This action is implemented by calling {@link Lexer//setChannel} with the
// value provided by {@link //getChannel}.</p>
func (this *LexerChannelAction) execute(lexer Lexer) {
	lexer.setChannel(this.channel)
}

func (this *LexerChannelAction) Hash() string {
	return strconv.Itoa(this.actionType) + strconv.Itoa(this.channel)
}

func (this *LexerChannelAction) equals(other LexerAction) bool {
	if this == other {
		return true
	} else if _, ok := other.(*LexerChannelAction); !ok {
		return false
	} else {
		return this.channel == other.(*LexerChannelAction).channel
	}
}

func (this *LexerChannelAction) String() string {
	return "channel(" + strconv.Itoa(this.channel) + ")"
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
	*BaseLexerAction

	offset              int
	lexerAction         LexerAction
	isPositionDependent bool
}

func NewLexerIndexedCustomAction(offset int, lexerAction LexerAction) *LexerIndexedCustomAction {

	this := new(LexerIndexedCustomAction)
	this.BaseLexerAction = NewBaseLexerAction(lexerAction.getActionType())

	this.offset = offset
	this.lexerAction = lexerAction
	this.isPositionDependent = true

	return this
}

// <p>This method calls {@link //execute} on the result of {@link //getAction}
// using the provided {@code lexer}.</p>
func (this *LexerIndexedCustomAction) execute(lexer Lexer) {
	// assume the input stream position was properly set by the calling code
	this.lexerAction.execute(lexer)
}

func (this *LexerIndexedCustomAction) Hash() string {
	return strconv.Itoa(this.actionType) + strconv.Itoa(this.offset) + this.lexerAction.Hash()
}

func (this *LexerIndexedCustomAction) equals(other LexerAction) bool {
	if this == other {
		return true
	} else if _, ok := other.(*LexerIndexedCustomAction); !ok {
		return false
	} else {
		return this.offset == other.(*LexerIndexedCustomAction).offset && this.lexerAction == other.(*LexerIndexedCustomAction).lexerAction
	}
}
