package atn

func ATNDeserializationOptions(copyFrom) {
	if(copyFrom==undefined) {
		copyFrom = null
	}
	this.readOnly = false
    this.verifyATN = copyFrom==null ? true : copyFrom.verifyATN
    this.generateRuleBypassTransitions = copyFrom==null ? false : copyFrom.generateRuleBypassTransitions

    return this
}

ATNDeserializationOptions.defaultOptions = new ATNDeserializationOptions()
ATNDeserializationOptions.defaultOptions.readOnly = true

//    func __setattr__(self, key, value):
//        if key!="readOnly" and self.readOnly:
//            raise Exception("The object is read only.")
//        super(type(self), self).__setattr__(key,value)


