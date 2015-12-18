package antlr4

type ATNDeserializationOptions struct {
	readOnly bool
	verifyATN bool
	generateRuleBypassTransitions bool
}

func NewATNDeserializationOptions(copyFrom *ATNDeserializationOptions) *ATNDeserializationOptions {
	o := new(ATNDeserializationOptions)

	if (copyFrom != nil){
		o.readOnly = copyFrom.readOnly
		o.verifyATN = copyFrom.verifyATN
		o.generateRuleBypassTransitions = copyFrom.generateRuleBypassTransitions
	}

    return o
}

var ATNDeserializationOptionsdefaultOptions = &ATNDeserializationOptions{true}

//    func __setattr__(self, key, value):
//        if key!="readOnly" and self.readOnly:
//            raise Exception("The object is read only.")
//        super(type(self), self).__setattr__(key,value)


