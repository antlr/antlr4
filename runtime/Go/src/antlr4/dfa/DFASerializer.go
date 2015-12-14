package antlr

// A DFA walker that knows how to dump them to serialized strings.#/


function DFASerializer(dfa, literalNames, symbolicNames) {
	this.dfa = dfa;
	this.literalNames = literalNames || [];
	this.symbolicNames = symbolicNames || [];
	return this;
}

func (this *DFASerializer) toString() {
   if(this.dfa.s0 == null) {
       return null;
   }
   var buf = "";
   var states = this.dfa.sortedStates();
   for(var i=0;i<states.length;i++) {
       var s = states[i];
       if(s.edges!==null) {
            var n = s.edges.length;
            for(var j=0;j<n;j++) {
                var t = s.edges[j] || null;
                if(t!==null && t.stateNumber !== 0x7FFFFFFF) {
                    buf = buf.concat(this.getStateString(s));
                    buf = buf.concat("-");
                    buf = buf.concat(this.getEdgeLabel(j));
                    buf = buf.concat("->");
                    buf = buf.concat(this.getStateString(t));
                    buf = buf.concat('\n');
                }
            }
       }
   }
   return buf.length==0 ? null : buf;
}

func (this *DFASerializer) getEdgeLabel(i) {
    if (i==0) {
        return "EOF";
    } else if(this.literalNames !==null || this.symbolicNames!==null) {
        return this.literalNames[i-1] || this.symbolicNames[i-1];
    } else {
        return String.fromCharCode(i-1);
    }
}

func (this *DFASerializer) getStateString(s) {
    var baseStateStr = ( s.isAcceptState ? ":" : "") + "s" + s.stateNumber + ( s.requiresFullContext ? "^" : "");
    if(s.isAcceptState) {
        if (s.predicates !== null) {
            return baseStateStr + "=>" + s.predicates.toString();
        } else {
            return baseStateStr + "=>" + s.prediction.toString();
        }
    } else {
        return baseStateStr;
    }
}

function LexerDFASerializer(dfa) {
	DFASerializer.call(this, dfa, null);
	return this;
}

LexerDFASerializer.prototype = Object.create(DFASerializer.prototype);
LexerDFASerializer.prototype.constructor = LexerDFASerializer;

func (this *LexerDFASerializer) getEdgeLabel(i) {
	return "'" + String.fromCharCode(i) + "'";
}




