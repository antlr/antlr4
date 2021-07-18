// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import "strconv"

// Lexer action types.
const (
	LexerActionTypeChannel  = 0 //The type of a LexerChannelAction action.
	LexerActionTypeCustom   = 1 //The type of a LexerCustomAction action.
	LexerActionTypeMode     = 2 //The type of a LexerModeAction action.
	LexerActionTypeMore     = 3 //The type of a LexerMoreAction action.
	LexerActionTypePopMode  = 4 //The type of a LexerPopModeAction action.
	LexerActionTypePushMode = 5 //The type of a LexerPushModeAction action.
	LexerActionTypeSkip     = 6 //The type of a LexerSkipAction action.
	LexerActionTypeType     = 7 //The type of a LexerTypeAction action.
)

// LexerAction is the base of the lexer action hyerarchy.
type LexerAction interface {
	getActionType() int
	getIsPositionDependent() bool
	execute(lexer Lexer)
	hash() int
	equals(other LexerAction) bool
}

// BaseLexerAction is the base implementation of LexerAction.
type BaseLexerAction struct {
	actionType          int
	isPositionDependent bool
}

// NewBaseLexerAction returns a new instance of BaseLexerAction.
func NewBaseLexerAction(action int) *BaseLexerAction {
	return &BaseLexerAction{
		actionType:          action,
		isPositionDependent: false,
	}
}

func (b *BaseLexerAction) execute(lexer Lexer) {
	panic("Not implemented")
}

func (b *BaseLexerAction) getActionType() int {
	return b.actionType
}

func (b *BaseLexerAction) getIsPositionDependent() bool {
	return b.isPositionDependent
}

func (b *BaseLexerAction) hash() int {
	return b.actionType
}

func (b *BaseLexerAction) equals(other LexerAction) bool {
	return b == other
}

// LexerSkipAction implements the Skip lexer action by calling Lexer//Skip.
//
// The Skip command does not have any parameters, so l action is
// implemented as a singleton instance exposed by //INSTANCE.
type LexerSkipAction struct {
	*BaseLexerAction
}

// NewLexerSkipAction returns a new instance of LexerSkipAction.
func NewLexerSkipAction() *LexerSkipAction {
	return &LexerSkipAction{
		BaseLexerAction: NewBaseLexerAction(LexerActionTypeSkip),
	}
}

// LexerSkipActionINSTANCE provides a singleton instance of l parameterless lexer action.
var LexerSkipActionINSTANCE = NewLexerSkipAction()

func (l *LexerSkipAction) execute(lexer Lexer) {
	lexer.Skip()
}

func (l *LexerSkipAction) String() string {
	return "skip"
}

// LexerTypeAction implements the type lexer action by calling Lexer//setType
// with the assigned type.
type LexerTypeAction struct {
	*BaseLexerAction

	thetype int
}

// NewLexerTypeAction returns a new instance of LexerTypeAction.
func NewLexerTypeAction(thetype int) *LexerTypeAction {
	return &LexerTypeAction{
		BaseLexerAction: NewBaseLexerAction(LexerActionTypeType),
		thetype:         thetype,
	}
}

func (l *LexerTypeAction) execute(lexer Lexer) {
	lexer.SetType(l.thetype)
}

func (l *LexerTypeAction) hash() int {
	h := murmurInit(0)
	h = murmurUpdate(h, l.actionType)
	h = murmurUpdate(h, l.thetype)
	return murmurFinish(h, 2)
}

func (l *LexerTypeAction) equals(other LexerAction) bool {
	if l == other {
		return true
	} else if _, ok := other.(*LexerTypeAction); !ok {
		return false
	} else {
		return l.thetype == other.(*LexerTypeAction).thetype
	}
}

func (l *LexerTypeAction) String() string {
	return "actionType(" + strconv.Itoa(l.thetype) + ")"
}

// LexerPushModeAction implements the pushMode lexer action by calling
// Lexer//pushMode with the assigned mode.
type LexerPushModeAction struct {
	*BaseLexerAction

	mode int
}

// NewLexerPushModeAction returns a new instance of LexerPushModeAction.
func NewLexerPushModeAction(mode int) *LexerPushModeAction {
	return &LexerPushModeAction{
		BaseLexerAction: NewBaseLexerAction(LexerActionTypePushMode),
		mode:            mode,
	}
}

// This action is implemented by calling Lexer//pushMode with the
// value provided by //getMode.
func (l *LexerPushModeAction) execute(lexer Lexer) {
	lexer.PushMode(l.mode)
}

func (l *LexerPushModeAction) hash() int {
	h := murmurInit(0)
	h = murmurUpdate(h, l.actionType)
	h = murmurUpdate(h, l.mode)
	return murmurFinish(h, 2)
}

func (l *LexerPushModeAction) equals(other LexerAction) bool {
	if l == other {
		return true
	} else if _, ok := other.(*LexerPushModeAction); !ok {
		return false
	} else {
		return l.mode == other.(*LexerPushModeAction).mode
	}
}

func (l *LexerPushModeAction) String() string {
	return "pushMode(" + strconv.Itoa(l.mode) + ")"
}

// LexerPopModeAction implements the popMode lexer action by calling Lexer//popMode.
//
// The popMode command does not have any parameters, so l action is
// implemented as a singleton instance exposed by //INSTANCE.
type LexerPopModeAction struct {
	*BaseLexerAction
}

// NewLexerPopModeAction returns a new instance of LexerPopModeAction.
func NewLexerPopModeAction() *LexerPopModeAction {
	return &LexerPopModeAction{
		BaseLexerAction: NewBaseLexerAction(LexerActionTypePopMode),
	}
}

// LexerPopModeActionINSTANCE TODO: docs.
var LexerPopModeActionINSTANCE = NewLexerPopModeAction()

// This action is implemented by calling Lexer//popMode.
func (l *LexerPopModeAction) execute(lexer Lexer) {
	lexer.PopMode()
}

func (l *LexerPopModeAction) String() string {
	return "popMode"
}

// LexerMoreAction implements the more lexer action by calling Lexer//more.
//
// The more command does not have any parameters, so l action is
// implemented as a singleton instance exposed by //INSTANCE.
type LexerMoreAction struct {
	*BaseLexerAction
}

// NewLexerMoreAction returns a new instance of LexerMoreAction.
func NewLexerMoreAction() *LexerMoreAction {
	return &LexerMoreAction{
		BaseLexerAction: NewBaseLexerAction(LexerActionTypeMore),
	}
}

// LexerMoreActionINSTANCE TODO: docs.
var LexerMoreActionINSTANCE = NewLexerMoreAction()

// This action is implemented by calling Lexer//popMode.
func (l *LexerMoreAction) execute(lexer Lexer) {
	lexer.More()
}

func (l *LexerMoreAction) String() string {
	return "more"
}

// LexerModeAction implements the mode lexer action by calling Lexer//mode with
// the assigned mode.
type LexerModeAction struct {
	*BaseLexerAction

	mode int
}

// NewLexerModeAction returns a new instance of LexerModeAction.
func NewLexerModeAction(mode int) *LexerModeAction {
	return &LexerModeAction{
		BaseLexerAction: NewBaseLexerAction(LexerActionTypeMode),
		mode:            mode,
	}
}

// This action is implemented by calling Lexer//mode with the
// value provided by //getMode.
func (l *LexerModeAction) execute(lexer Lexer) {
	lexer.SetMode(l.mode)
}

func (l *LexerModeAction) hash() int {
	h := murmurInit(0)
	h = murmurUpdate(h, l.actionType)
	h = murmurUpdate(h, l.mode)
	return murmurFinish(h, 2)
}

func (l *LexerModeAction) equals(other LexerAction) bool {
	if l == other {
		return true
	} else if _, ok := other.(*LexerModeAction); !ok {
		return false
	} else {
		return l.mode == other.(*LexerModeAction).mode
	}
}

func (l *LexerModeAction) String() string {
	return "mode(" + strconv.Itoa(l.mode) + ")"
}

// LexerCustomAction executes a custom lexer action by calling
// Recognizer//action with the rule and action indexes assigned to the custom
// action. The implementation of a custom action is added to the generated code
// for the lexer in an override of Recognizer//action when the grammar is
// compiled.
//
// This class may represent embedded actions created with the <code>{...}</code>
// syntax in ANTLR 4, as well as actions created for lexer commands where the
// command argument could not be evaluated when the grammar was compiled.

// LexerCustomAction constructs a custom lexer action with the specified rule
// and action indexes.
//
// @param ruleIndex The rule index to use for calls to
// Recognizer//action.
// @param actionIndex The action index to use for calls to
// Recognizer//action.
type LexerCustomAction struct {
	*BaseLexerAction
	ruleIndex, actionIndex int
}

// NewLexerCustomAction returns a new instance of LexerCustomAction
func NewLexerCustomAction(ruleIndex, actionIndex int) *LexerCustomAction {
	l := &LexerCustomAction{
		BaseLexerAction: NewBaseLexerAction(LexerActionTypeCustom),
		ruleIndex:       ruleIndex,
		actionIndex:     actionIndex,
	}
	l.isPositionDependent = true
	return l
}

// Custom actions are implemented by calling Lexer//action with the
// appropriate rule and action indexes.
func (l *LexerCustomAction) execute(lexer Lexer) {
	lexer.Action(nil, l.ruleIndex, l.actionIndex)
}

func (l *LexerCustomAction) hash() int {
	h := murmurInit(0)
	h = murmurUpdate(h, l.actionType)
	h = murmurUpdate(h, l.ruleIndex)
	h = murmurUpdate(h, l.actionIndex)
	return murmurFinish(h, 3)
}

func (l *LexerCustomAction) equals(other LexerAction) bool {
	if l == other {
		return true
	} else if _, ok := other.(*LexerCustomAction); !ok {
		return false
	} else {
		return l.ruleIndex == other.(*LexerCustomAction).ruleIndex && l.actionIndex == other.(*LexerCustomAction).actionIndex
	}
}

// LexerChannelAction implements the channel lexer action by calling
// Lexer//setChannel with the assigned channel.
// Constructs a Newchannel action with the specified channel value.
// @param channel The channel value to pass to Lexer//setChannel.
type LexerChannelAction struct {
	*BaseLexerAction

	channel int
}

// NewLexerChannelAction returns a new instance of LexerChannelAction.
func NewLexerChannelAction(channel int) *LexerChannelAction {
	return &LexerChannelAction{
		BaseLexerAction: NewBaseLexerAction(LexerActionTypeChannel),
		channel:         channel,
	}
}

// This action is implemented by calling Lexer//setChannel with the
// value provided by //getChannel.
func (l *LexerChannelAction) execute(lexer Lexer) {
	lexer.SetChannel(l.channel)
}

func (l *LexerChannelAction) hash() int {
	h := murmurInit(0)
	h = murmurUpdate(h, l.actionType)
	h = murmurUpdate(h, l.channel)
	return murmurFinish(h, 2)
}

func (l *LexerChannelAction) equals(other LexerAction) bool {
	if l == other {
		return true
	} else if _, ok := other.(*LexerChannelAction); !ok {
		return false
	} else {
		return l.channel == other.(*LexerChannelAction).channel
	}
}

func (l *LexerChannelAction) String() string {
	return "channel(" + strconv.Itoa(l.channel) + ")"
}

// LexerIndexedCustomAction is used for tracking input offsets
// for position-dependent actions within a LexerActionExecutor.
//
// This action is not serialized as part of the ATN, and is only required for
// position-dependent lexer actions which appear at a location other than the
// end of a rule. For more information about DFA optimizations employed for
// lexer actions, see LexerActionExecutor//append and
// LexerActionExecutor//fixOffsetBeforeMatch.

// LexerIndexedCustomAction constructs a Newindexed custom action by associating
// a character offset with a LexerAction.
//
// Note: This class is only required for lexer actions for which
// LexerAction//isPositionDependent returns true.
//
// @param offset The offset into the input CharStream, relative to
// the token start index, at which the specified lexer action should be
// executed.
// @param action The lexer action to execute at a particular offset in the
// input CharStream.
type LexerIndexedCustomAction struct {
	*BaseLexerAction

	offset              int
	lexerAction         LexerAction
	isPositionDependent bool
}

// NewLexerIndexedCustomAction returns a new instance of
// LexerIndexedCustomAction.
func NewLexerIndexedCustomAction(offset int, lexerAction LexerAction) *LexerIndexedCustomAction {
	return &LexerIndexedCustomAction{
		BaseLexerAction:     NewBaseLexerAction(lexerAction.getActionType()),
		offset:              offset,
		lexerAction:         lexerAction,
		isPositionDependent: true,
	}
}

// This method calls //execute on the result of //getAction
// using the provided lexer.
func (l *LexerIndexedCustomAction) execute(lexer Lexer) {
	// assume the input stream position was properly set by the calling code
	l.lexerAction.execute(lexer)
}

func (l *LexerIndexedCustomAction) hash() int {
	h := murmurInit(0)
	h = murmurUpdate(h, l.actionType)
	h = murmurUpdate(h, l.offset)
	h = murmurUpdate(h, l.lexerAction.hash())
	return murmurFinish(h, 3)
}

func (l *LexerIndexedCustomAction) equals(other LexerAction) bool {
	if l == other {
		return true
	} else if _, ok := other.(*LexerIndexedCustomAction); !ok {
		return false
	} else {
		return l.offset == other.(*LexerIndexedCustomAction).offset && l.lexerAction == other.(*LexerIndexedCustomAction).lexerAction
	}
}
