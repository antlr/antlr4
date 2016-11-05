package antlr

var ATNDeserializationOptionsdefaultOptions = &ATNDeserializationOptions{true, false, false}

type ATNDeserializationOptions struct {
	readOnly                      bool
	verifyATN                     bool
	generateRuleBypassTransitions bool
}

func NewATNDeserializationOptions(CopyFrom *ATNDeserializationOptions) *ATNDeserializationOptions {
	o := new(ATNDeserializationOptions)

	if CopyFrom != nil {
		o.readOnly = CopyFrom.readOnly
		o.verifyATN = CopyFrom.verifyATN
		o.generateRuleBypassTransitions = CopyFrom.generateRuleBypassTransitions
	}

	return o
}
