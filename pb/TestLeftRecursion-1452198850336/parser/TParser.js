// Generated from T.g4 by ANTLR 4.5.1
// jshint ignore: start
var antlr4 = require('antlr4/index');
var TListener = require('./TListener').TListener;
var grammarFileName = "T.g4";

var serializedATN = ["\u0003\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd",
    "\u0003\u0007\u001e\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0003\u0002",
    "\u0003\u0002\u0003\u0002\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003",
    "\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0005\u0003\u0012\n",
    "\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0007",
    "\u0003\u0019\n\u0003\f\u0003\u000e\u0003\u001c\u000b\u0003\u0003\u0003",
    "\u0002\u0003\u0004\u0004\u0002\u0004\u0002\u0002\u001d\u0002\u0006\u0003",
    "\u0002\u0002\u0002\u0004\u0011\u0003\u0002\u0002\u0002\u0006\u0007\u0005",
    "\u0004\u0003\u0002\u0007\b\b\u0002\u0001\u0002\b\u0003\u0003\u0002\u0002",
    "\u0002\t\n\b\u0003\u0001\u0002\n\u000b\u0007\u0005\u0002\u0002\u000b",
    "\f\u0007\u0003\u0002\u0002\f\r\u0005\u0004\u0003\u0005\r\u000e\b\u0003",
    "\u0001\u0002\u000e\u0012\u0003\u0002\u0002\u0002\u000f\u0010\u0007\u0005",
    "\u0002\u0002\u0010\u0012\b\u0003\u0001\u0002\u0011\t\u0003\u0002\u0002",
    "\u0002\u0011\u000f\u0003\u0002\u0002\u0002\u0012\u001a\u0003\u0002\u0002",
    "\u0002\u0013\u0014\f\u0003\u0002\u0002\u0014\u0015\u0007\u0004\u0002",
    "\u0002\u0015\u0016\u0005\u0004\u0003\u0004\u0016\u0017\b\u0003\u0001",
    "\u0002\u0017\u0019\u0003\u0002\u0002\u0002\u0018\u0013\u0003\u0002\u0002",
    "\u0002\u0019\u001c\u0003\u0002\u0002\u0002\u001a\u0018\u0003\u0002\u0002",
    "\u0002\u001a\u001b\u0003\u0002\u0002\u0002\u001b\u0005\u0003\u0002\u0002",
    "\u0002\u001c\u001a\u0003\u0002\u0002\u0002\u0004\u0011\u001a"].join("");


var atn = new antlr4.atn.ATNDeserializer().deserialize(serializedATN);

var decisionsToDFA = atn.decisionToState.map( function(ds, index) { return new antlr4.dfa.DFA(ds, index); });

var sharedContextCache = new antlr4.PredictionContextCache();

var literalNames = [ null, "'='", "'+'" ];

var symbolicNames = [ null, null, null, "ID", "INT", "WS" ];

var ruleNames =  [ "s", "e" ];

function TParser (input) {
	antlr4.Parser.call(this, input);
    this._interp = new antlr4.atn.ParserATNSimulator(this, atn, decisionsToDFA, sharedContextCache);
    this.ruleNames = ruleNames;
    this.literalNames = literalNames;
    this.symbolicNames = symbolicNames;
    return this;
}

TParser.prototype = Object.create(antlr4.Parser.prototype);
TParser.prototype.constructor = TParser;

Object.defineProperty(TParser.prototype, "atn", {
	get : function() {
		return atn;
	}
});

TParser.EOF = antlr4.Token.EOF;
TParser.T__0 = 1;
TParser.T__1 = 2;
TParser.ID = 3;
TParser.INT = 4;
TParser.WS = 5;

TParser.RULE_s = 0;
TParser.RULE_e = 1;

function SContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = TParser.RULE_s;
    this._e = null; // EContext
    return this;
}

SContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
SContext.prototype.constructor = SContext;

SContext.prototype.e = function() {
    return this.getTypedRuleContext(EContext,0);
};

SContext.prototype.enterRule = function(listener) {
    if(listener instanceof TListener ) {
        listener.enterS(this);
	}
};

SContext.prototype.exitRule = function(listener) {
    if(listener instanceof TListener ) {
        listener.exitS(this);
	}
};




TParser.SContext = SContext;

TParser.prototype.s = function() {

    var localctx = new SContext(this, this._ctx, this.state);
    this.enterRule(localctx, 0, TParser.RULE_s);
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 4;
        localctx._e = this.e(0);
        console.log(localctx._e.result)
    } catch (re) {
    	if(re instanceof antlr4.error.RecognitionException) {
	        localctx.exception = re;
	        this._errHandler.reportError(this, re);
	        this._errHandler.recover(this, re);
	    } else {
	    	throw re;
	    }
    } finally {
        this.exitRule();
    }
    return localctx;
};

function EContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = TParser.RULE_e;
    this.result = null
    this.e1 = null; // EContext
    this._ID = null; // Token
    this.e2 = null; // EContext
    return this;
}

EContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
EContext.prototype.constructor = EContext;

EContext.prototype.ID = function() {
    return this.getToken(TParser.ID, 0);
};

EContext.prototype.e = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(EContext);
    } else {
        return this.getTypedRuleContext(EContext,i);
    }
};

EContext.prototype.enterRule = function(listener) {
    if(listener instanceof TListener ) {
        listener.enterE(this);
	}
};

EContext.prototype.exitRule = function(listener) {
    if(listener instanceof TListener ) {
        listener.exitE(this);
	}
};



TParser.prototype.e = function(_p) {
	if(_p===undefined) {
	    _p = 0;
	}
    var _parentctx = this._ctx;
    var _parentState = this.state;
    var localctx = new EContext(this, this._ctx, _parentState);
    var _prevctx = localctx;
    var _startState = 2;
    this.enterRecursionRule(localctx, 2, TParser.RULE_e, _p);
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 15;
        this._errHandler.sync(this);
        var la_ = this._interp.adaptivePredict(this._input,0,this._ctx);
        switch(la_) {
        case 1:
            this.state = 8;
            localctx._ID = this.match(TParser.ID);
            this.state = 9;
            this.match(TParser.T__0);
            this.state = 10;
            localctx.e1 = this.e(3);
            localctx.result =  "(" + (localctx._ID===null ? null : localctx._ID.text) + "=" + localctx.e1.result + ")"
            break;

        case 2:
            this.state = 13;
            localctx._ID = this.match(TParser.ID);
            localctx.result =  (localctx._ID===null ? null : localctx._ID.text)
            break;

        }
        this._ctx.stop = this._input.LT(-1);
        this.state = 24;
        this._errHandler.sync(this);
        var _alt = this._interp.adaptivePredict(this._input,1,this._ctx)
        while(_alt!=2 && _alt!=antlr4.atn.ATN.INVALID_ALT_NUMBER) {
            if(_alt===1) {
                if(this._parseListeners!==null) {
                    this.triggerExitRuleEvent();
                }
                _prevctx = localctx;
                localctx = new EContext(this, _parentctx, _parentState);
                localctx.e1 = _prevctx;
                this.pushNewRecursionContext(localctx, _startState, TParser.RULE_e);
                this.state = 17;
                if (!( this.precpred(this._ctx, 1))) {
                    throw new antlr4.error.FailedPredicateException(this, "this.precpred(this._ctx, 1)");
                }
                this.state = 18;
                this.match(TParser.T__1);
                this.state = 19;
                localctx.e2 = this.e(2);
                localctx.result =  "(" + localctx.e1.result + "+" + localctx.e2.result + ")" 
            }
            this.state = 26;
            this._errHandler.sync(this);
            _alt = this._interp.adaptivePredict(this._input,1,this._ctx);
        }

    } catch( error) {
        if(error instanceof antlr4.error.RecognitionException) {
	        localctx.exception = error;
	        this._errHandler.reportError(this, error);
	        this._errHandler.recover(this, error);
	    } else {
	    	throw error;
	    }
    } finally {
        this.unrollRecursionContexts(_parentctx)
    }
    return localctx;
};


TParser.prototype.sempred = function(localctx, ruleIndex, predIndex) {
	switch(ruleIndex) {
	case 1:
			return this.e_sempred(localctx, predIndex);
    default:
        throw "No predicate with index:" + ruleIndex;
   }
};

TParser.prototype.e_sempred = function(localctx, predIndex) {
	switch(predIndex) {
		case 0:
			return this.precpred(this._ctx, 1);
		default:
			throw "No predicate with index:" + predIndex;
	}
};


exports.TParser = TParser;
