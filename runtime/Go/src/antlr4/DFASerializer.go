package antlr4
import "fmt"

// A DFA walker that knows how to dump them to serialized strings.#/


type DFASerializer struct {
    dfa *DFA
    literalNames, symbolicNames []string
}

func NewDFASerializer(dfa *DFA, literalNames, symbolicNames []string) *DFASerializer {

    if (literalNames == nil){
        literalNames = make([]string)
    }

    if (symbolicNames == nil){
        symbolicNames = make([]string)
    }

    this := new(DFASerializer)

    this.InitDFASerializer(dfa, literalNames, symbolicNames)

	return this
}

func (this *DFASerializer) InitDFASerializer(dfa *DFA, literalNames, symbolicNames []string) {
    this.dfa = dfa
    this.literalNames = literalNames
    this.symbolicNames = symbolicNames
}

func (this *DFASerializer) toString() string {

   if(this.dfa.s0 == nil) {
       return nil
   }
   var buf = ""
   var states = this.dfa.sortedStates()
   for i := 0; i<len(states); i++ {
       var s = states[i]
       if(s.edges!=nil) {
            var n = len(s.edges)
            for j :=0; j<n; j++ {
                var t = s.edges[j]
                if(t!=nil && t.stateNumber != 0x7FFFFFFF) {
                    buf += this.getStateString(s)
                    buf += "-"
                    buf += this.getEdgeLabel(j)
                    buf += "->"
                    buf += this.getStateString(t)
                    buf += '\n'
                }
            }
       }
   }
    if len(buf) == 0 {
        return nil
    }

    return buf
}

func (this *DFASerializer) getEdgeLabel(i int) string {
    if (i==0) {
        return "EOF"
    } else if(this.literalNames !=nil || this.symbolicNames!=nil) {
        return this.literalNames[i-1] || this.symbolicNames[i-1]
    } else {
        return string(i-1)
    }
}

func (this *DFASerializer) getStateString(s *DFAState) string {

    var a,b string

    if (s.isAcceptState){
        a = ":"
    }

    if (s.requiresFullContext){
        b = "^"
    }

    var baseStateStr = a + "s" + s.stateNumber + b
    if(s.isAcceptState) {
        if (s.predicates != nil) {
            return baseStateStr + "=>" + fmt.Sprint(s.predicates)
        } else {
            return baseStateStr + "=>" + fmt.Sprint(s.prediction)
        }
    } else {
        return baseStateStr
    }
}

type LexerDFASerializer struct {
    DFASerializer
}

func NewLexerDFASerializer(dfa *DFA) *LexerDFASerializer {

    this := new(DFASerializer)

    this.InitDFASerializer(dfa, nil, nil)

	return this
}

func (this *LexerDFASerializer) getEdgeLabel(i int) {
	return "'" + string(i) + "'"
}




