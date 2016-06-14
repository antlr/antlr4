package antlr

var ATNDeserializationOptionsdefaultOptions = &ATNDeserializationOptions{true, false, false}

type ATNDeserializationOptions struct {
	readOnly                      bool
	verifyATN                     bool
	generateRuleBypassTransitions bool
}

func NewATNDeserializationOptions(CopyFrom *ATNDeserializationOptions) *ATNDeserializationOptions {
	var o = new(ATNDeserializationOptions)

	if CopyFrom != nil {
		o.readOnly = CopyFrom.readOnly
		o.verifyATN = CopyFrom.verifyATN
		o.generateRuleBypassTransitions = CopyFrom.generateRuleBypassTransitions
	}

	return o
}
