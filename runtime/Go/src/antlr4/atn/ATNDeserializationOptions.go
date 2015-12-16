package atn

func ATNDeserializationOptions(copyFrom) {
	if(copyFrom==nil) {
		copyFrom = nil
	}
	this.readOnly = false
    this.verifyATN = copyFrom==nil ? true : copyFrom.verifyATN
    this.generateRuleBypassTransitions = copyFrom==nil ? false : copyFrom.generateRuleBypassTransitions

    return this
}

ATNDeserializationOptions.defaultOptions = NewATNDeserializationOptions()
ATNDeserializationOptions.defaultOptions.readOnly = true

//    func __setattr__(self, key, value):
//        if key!="readOnly" and self.readOnly:
//            raise Exception("The object is read only.")
//        super(type(self), self).__setattr__(key,value)


