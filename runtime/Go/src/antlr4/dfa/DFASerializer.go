package dfa

// A DFA walker that knows how to dump them to serialized strings.#/


func DFASerializer(dfa, literalNames, symbolicNames) {
	this.dfa = dfa
	this.literalNames = literalNames || []
	this.symbolicNames = symbolicNames || []
	return this
}

func (this *DFASerializer) toString() string {
   if(this.dfa.s0 == nil) {
       return nil
   }
   var buf = ""
   var states = this.dfa.sortedStates()
   for(var i=0i<states.lengthi++) {
       var s = states[i]
       if(s.edges!=nil) {
            var n = s.edges.length
            for(var j=0j<nj++) {
                var t = s.edges[j] || nil
                if(t!=nil && t.stateNumber != 0x7FFFFFFF) {
                    buf = buf.concat(this.getStateString(s))
                    buf = buf.concat("-")
                    buf = buf.concat(this.getEdgeLabel(j))
                    buf = buf.concat("->")
                    buf = buf.concat(this.getStateString(t))
                    buf = buf.concat('\n')
                }
            }
       }
   }
   return buf.length==0 ? nil : buf
}

func (this *DFASerializer) getEdgeLabel(i) {
    if (i==0) {
        return "EOF"
    } else if(this.literalNames !=nil || this.symbolicNames!=nil) {
        return this.literalNames[i-1] || this.symbolicNames[i-1]
    } else {
        return String.fromCharCode(i-1)
    }
}

func (this *DFASerializer) getStateString(s) {
    var baseStateStr = ( s.isAcceptState ? ":" : "") + "s" + s.stateNumber + ( s.requiresFullContext ? "^" : "")
    if(s.isAcceptState) {
        if (s.predicates != nil) {
            return baseStateStr + "=>" + s.predicates.toString()
        } else {
            return baseStateStr + "=>" + s.prediction.toString()
        }
    } else {
        return baseStateStr
    }
}

func LexerDFASerializer(dfa) {
	DFASerializer.call(this, dfa, nil)
	return this
}

//LexerDFASerializer.prototype = Object.create(DFASerializer.prototype)
//LexerDFASerializer.prototype.constructor = LexerDFASerializer

func (this *LexerDFASerializer) getEdgeLabel(i) {
	return "'" + String.fromCharCode(i) + "'"
}




