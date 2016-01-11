// Generated from T.g4 by ANTLR 4.5.1
// jshint ignore: start
var antlr4 = require('antlr4/index');
var TListener = require('./TListener').TListener;
var grammarFileName = "T.g4";

var serializedATN = ["\u0003\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd",
    "\u0003\u0006\u0015\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0003\u0002",
    "\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0003\u0003\u0003",
    "\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0005\u0003",
    "\u0013\n\u0003\u0003\u0003\u0002\u0002\u0004\u0002\u0004\u0002\u0002",
    "\u0014\u0002\u0006\u0003\u0002\u0002\u0002\u0004\u0012\u0003\u0002\u0002",
    "\u0002\u0006\u0007\b\u0002\u0001\u0002\u0007\b\u0005\u0004\u0003\u0002",
    "\b\t\u0007\u0003\u0002\u0002\t\n\u0005\u0004\u0003\u0002\n\u0003\u0003",
    "\u0002\u0002\u0002\u000b\f\u0007\u0004\u0002\u0002\f\u0013\b\u0003\u0001",
    "\u0002\r\u000e\u0007\u0004\u0002\u0002\u000e\u0013\b\u0003\u0001\u0002",
    "\u000f\u0010\u0006\u0003\u0002\u0002\u0010\u0011\u0007\u0004\u0002\u0002",
    "\u0011\u0013\b\u0003\u0001\u0002\u0012\u000b\u0003\u0002\u0002\u0002",
    "\u0012\r\u0003\u0002\u0002\u0002\u0012\u000f\u0003\u0002\u0002\u0002",
    "\u0013\u0005\u0003\u0002\u0002\u0002\u0003\u0012"].join("");


var atn = new antlr4.atn.ATNDeserializer().deserialize(serializedATN);

var decisionsToDFA = atn.decisionToState.map( function(ds, index) { return new antlr4.dfa.DFA(ds, index); });

var sharedContextCache = new antlr4.PredictionContextCache();

var literalNames = [ null, "';'" ];

var symbolicNames = [ null, null, "ID", "INT", "WS" ];

var ruleNames =  [ "s", "a" ];

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
TParser.ID = 2;
TParser.INT = 3;
TParser.WS = 4;

TParser.RULE_s = 0;
TParser.RULE_a = 1;

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
    return this;
}

SContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
SContext.prototype.constructor = SContext;

SContext.prototype.a = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(AContext);
    } else {
        return this.getTypedRuleContext(AContext,i);
    }
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
        this._interp.predictionMode = antlr4.atn.PredictionMode.LL_EXACT_AMBIG_DETECTION;
        this.state = 5;
        this.a();
        this.state = 6;
        this.match(TParser.T__0);
        this.state = 7;
        this.a();
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

function AContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = TParser.RULE_a;
    return this;
}

AContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
AContext.prototype.constructor = AContext;

AContext.prototype.ID = function() {
    return this.getToken(TParser.ID, 0);
};

AContext.prototype.enterRule = function(listener) {
    if(listener instanceof TListener ) {
        listener.enterA(this);
	}
};

AContext.prototype.exitRule = function(listener) {
    if(listener instanceof TListener ) {
        listener.exitA(this);
	}
};




TParser.AContext = AContext;

TParser.prototype.a = function() {

    var localctx = new AContext(this, this._ctx, this.state);
    this.enterRule(localctx, 2, TParser.RULE_a);
    try {
        this.state = 16;
        this._errHandler.sync(this);
        var la_ = this._interp.adaptivePredict(this._input,0,this._ctx);
        switch(la_) {
        case 1:
            this.enterOuterAlt(localctx, 1);
            this.state = 9;
            this.match(TParser.ID);
            console.log("alt 1")
            break;

        case 2:
            this.enterOuterAlt(localctx, 2);
            this.state = 11;
            this.match(TParser.ID);
            console.log("alt 2")
            break;

        case 3:
            this.enterOuterAlt(localctx, 3);
            this.state = 13;
            if (!( false)) {
                throw new antlr4.error.FailedPredicateException(this, "false");
            }
            this.state = 14;
            this.match(TParser.ID);
            console.log("alt 3")
            break;

        }
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


TParser.prototype.sempred = function(localctx, ruleIndex, predIndex) {
	switch(ruleIndex) {
	case 1:
			return this.a_sempred(localctx, predIndex);
    default:
        throw "No predicate with index:" + ruleIndex;
   }
};

TParser.prototype.a_sempred = function(localctx, predIndex) {
	switch(predIndex) {
		case 0:
			return false;
		default:
			throw "No predicate with index:" + predIndex;
	}
};


exports.TParser = TParser;
