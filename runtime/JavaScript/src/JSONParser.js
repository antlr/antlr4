// Generated from JSON.g4 by ANTLR 4.5.1
// jshint ignore: start
var antlr4 = require('./antlr4/index');
var JSONListener = require('./JSONListener').JSONListener;
var grammarFileName = "JSON.g4";

var serializedATN = ["\u0003\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd",
    "\u0003\u000e<\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0004\u0004\t",
    "\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0003\u0002\u0003\u0002",
    "\u0005\u0002\u000f\n\u0002\u0003\u0003\u0003\u0003\u0003\u0003\u0003",
    "\u0003\u0007\u0003\u0015\n\u0003\f\u0003\u000e\u0003\u0018\u000b\u0003",
    "\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0005\u0003\u001e\n",
    "\u0003\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0005\u0003",
    "\u0005\u0003\u0005\u0003\u0005\u0007\u0005(\n\u0005\f\u0005\u000e\u0005",
    "+\u000b\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0005",
    "\u00051\n\u0005\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003",
    "\u0006\u0003\u0006\u0003\u0006\u0005\u0006:\n\u0006\u0003\u0006\u0002",
    "\u0002\u0007\u0002\u0004\u0006\b\n\u0002\u0002A\u0002\u000e\u0003\u0002",
    "\u0002\u0002\u0004\u001d\u0003\u0002\u0002\u0002\u0006\u001f\u0003\u0002",
    "\u0002\u0002\b0\u0003\u0002\u0002\u0002\n9\u0003\u0002\u0002\u0002\f",
    "\u000f\u0005\u0004\u0003\u0002\r\u000f\u0005\b\u0005\u0002\u000e\f\u0003",
    "\u0002\u0002\u0002\u000e\r\u0003\u0002\u0002\u0002\u000f\u0003\u0003",
    "\u0002\u0002\u0002\u0010\u0011\u0007\u0003\u0002\u0002\u0011\u0016\u0005",
    "\u0006\u0004\u0002\u0012\u0013\u0007\u0004\u0002\u0002\u0013\u0015\u0005",
    "\u0006\u0004\u0002\u0014\u0012\u0003\u0002\u0002\u0002\u0015\u0018\u0003",
    "\u0002\u0002\u0002\u0016\u0014\u0003\u0002\u0002\u0002\u0016\u0017\u0003",
    "\u0002\u0002\u0002\u0017\u0019\u0003\u0002\u0002\u0002\u0018\u0016\u0003",
    "\u0002\u0002\u0002\u0019\u001a\u0007\u0005\u0002\u0002\u001a\u001e\u0003",
    "\u0002\u0002\u0002\u001b\u001c\u0007\u0003\u0002\u0002\u001c\u001e\u0007",
    "\u0005\u0002\u0002\u001d\u0010\u0003\u0002\u0002\u0002\u001d\u001b\u0003",
    "\u0002\u0002\u0002\u001e\u0005\u0003\u0002\u0002\u0002\u001f \u0007",
    "\f\u0002\u0002 !\u0007\u0006\u0002\u0002!\"\u0005\n\u0006\u0002\"\u0007",
    "\u0003\u0002\u0002\u0002#$\u0007\u0007\u0002\u0002$)\u0005\n\u0006\u0002",
    "%&\u0007\u0004\u0002\u0002&(\u0005\n\u0006\u0002\'%\u0003\u0002\u0002",
    "\u0002(+\u0003\u0002\u0002\u0002)\'\u0003\u0002\u0002\u0002)*\u0003",
    "\u0002\u0002\u0002*,\u0003\u0002\u0002\u0002+)\u0003\u0002\u0002\u0002",
    ",-\u0007\b\u0002\u0002-1\u0003\u0002\u0002\u0002./\u0007\u0007\u0002",
    "\u0002/1\u0007\b\u0002\u00020#\u0003\u0002\u0002\u00020.\u0003\u0002",
    "\u0002\u00021\t\u0003\u0002\u0002\u00022:\u0007\f\u0002\u00023:\u0007",
    "\r\u0002\u00024:\u0005\u0004\u0003\u00025:\u0005\b\u0005\u00026:\u0007",
    "\t\u0002\u00027:\u0007\n\u0002\u00028:\u0007\u000b\u0002\u000292\u0003",
    "\u0002\u0002\u000293\u0003\u0002\u0002\u000294\u0003\u0002\u0002\u0002",
    "95\u0003\u0002\u0002\u000296\u0003\u0002\u0002\u000297\u0003\u0002\u0002",
    "\u000298\u0003\u0002\u0002\u0002:\u000b\u0003\u0002\u0002\u0002\b\u000e",
    "\u0016\u001d)09"].join("");


var atn = new antlr4.atn.ATNDeserializer().deserialize(serializedATN);

var decisionsToDFA = atn.decisionToState.map( function(ds, index) { return new antlr4.dfa.DFA(ds, index); });

var sharedContextCache = new antlr4.PredictionContextCache();

var literalNames = [ null, "'{'", "','", "'}'", "':'", "'['", "']'", "'true'", 
                     "'false'", "'null'" ];

var symbolicNames = [ null, null, null, null, null, null, null, null, null, 
                      null, "STRING", "NUMBER", "WS" ];

var ruleNames =  [ "json", "object", "pair", "array", "value" ];

function JSONParser (input) {
	antlr4.Parser.call(this, input);
    this._interp = new antlr4.atn.ParserATNSimulator(this, atn, decisionsToDFA, sharedContextCache);
    this.ruleNames = ruleNames;
    this.literalNames = literalNames;
    this.symbolicNames = symbolicNames;
    return this;
}

JSONParser.prototype = Object.create(antlr4.Parser.prototype);
JSONParser.prototype.constructor = JSONParser;

Object.defineProperty(JSONParser.prototype, "atn", {
	get : function() {
		return atn;
	}
});

JSONParser.EOF = antlr4.Token.EOF;
JSONParser.T__0 = 1;
JSONParser.T__1 = 2;
JSONParser.T__2 = 3;
JSONParser.T__3 = 4;
JSONParser.T__4 = 5;
JSONParser.T__5 = 6;
JSONParser.T__6 = 7;
JSONParser.T__7 = 8;
JSONParser.T__8 = 9;
JSONParser.STRING = 10;
JSONParser.NUMBER = 11;
JSONParser.WS = 12;

JSONParser.RULE_json = 0;
JSONParser.RULE_object = 1;
JSONParser.RULE_pair = 2;
JSONParser.RULE_array = 3;
JSONParser.RULE_value = 4;

function JsonContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = JSONParser.RULE_json;
    return this;
}

JsonContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
JsonContext.prototype.constructor = JsonContext;

JsonContext.prototype.object = function() {
    return this.getTypedRuleContext(ObjectContext,0);
};

JsonContext.prototype.array = function() {
    return this.getTypedRuleContext(ArrayContext,0);
};

JsonContext.prototype.enterRule = function(listener) {
    if(listener instanceof JSONListener ) {
        listener.enterJson(this);
	}
};

JsonContext.prototype.exitRule = function(listener) {
    if(listener instanceof JSONListener ) {
        listener.exitJson(this);
	}
};




JSONParser.JsonContext = JsonContext;

JSONParser.prototype.json = function() {

    var localctx = new JsonContext(this, this._ctx, this.state);
    this.enterRule(localctx, 0, JSONParser.RULE_json);
    try {
        this.state = 12;
        switch(this._input.LA(1)) {
        case JSONParser.T__0:
            this.enterOuterAlt(localctx, 1);
            this.state = 10;
            this.object();
            break;
        case JSONParser.T__4:
            this.enterOuterAlt(localctx, 2);
            this.state = 11;
            this.array();
            break;
        default:
            throw new antlr4.error.NoViableAltException(this);
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

function ObjectContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = JSONParser.RULE_object;
    return this;
}

ObjectContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
ObjectContext.prototype.constructor = ObjectContext;

ObjectContext.prototype.pair = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(PairContext);
    } else {
        return this.getTypedRuleContext(PairContext,i);
    }
};

ObjectContext.prototype.enterRule = function(listener) {
    if(listener instanceof JSONListener ) {
        listener.enterObject(this);
	}
};

ObjectContext.prototype.exitRule = function(listener) {
    if(listener instanceof JSONListener ) {
        listener.exitObject(this);
	}
};




JSONParser.ObjectContext = ObjectContext;

JSONParser.prototype.object = function() {

    var localctx = new ObjectContext(this, this._ctx, this.state);
    this.enterRule(localctx, 2, JSONParser.RULE_object);
    var _la = 0; // Token type
    try {
        this.state = 27;
        this._errHandler.sync(this);
        var la_ = this._interp.adaptivePredict(this._input,2,this._ctx);
        switch(la_) {
        case 1:
            this.enterOuterAlt(localctx, 1);
            this.state = 14;
            this.match(JSONParser.T__0);
            this.state = 15;
            this.pair();
            this.state = 20;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
            while(_la===JSONParser.T__1) {
                this.state = 16;
                this.match(JSONParser.T__1);
                this.state = 17;
                this.pair();
                this.state = 22;
                this._errHandler.sync(this);
                _la = this._input.LA(1);
            }
            this.state = 23;
            this.match(JSONParser.T__2);
            break;

        case 2:
            this.enterOuterAlt(localctx, 2);
            this.state = 25;
            this.match(JSONParser.T__0);
            this.state = 26;
            this.match(JSONParser.T__2);
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

function PairContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = JSONParser.RULE_pair;
    return this;
}

PairContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
PairContext.prototype.constructor = PairContext;

PairContext.prototype.STRING = function() {
    return this.getToken(JSONParser.STRING, 0);
};

PairContext.prototype.value = function() {
    return this.getTypedRuleContext(ValueContext,0);
};

PairContext.prototype.enterRule = function(listener) {
    if(listener instanceof JSONListener ) {
        listener.enterPair(this);
	}
};

PairContext.prototype.exitRule = function(listener) {
    if(listener instanceof JSONListener ) {
        listener.exitPair(this);
	}
};




JSONParser.PairContext = PairContext;

JSONParser.prototype.pair = function() {

    var localctx = new PairContext(this, this._ctx, this.state);
    this.enterRule(localctx, 4, JSONParser.RULE_pair);
    try {
        this.enterOuterAlt(localctx, 1);
        this.state = 29;
        this.match(JSONParser.STRING);
        this.state = 30;
        this.match(JSONParser.T__3);
        this.state = 31;
        this.value();
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

function ArrayContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = JSONParser.RULE_array;
    return this;
}

ArrayContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
ArrayContext.prototype.constructor = ArrayContext;

ArrayContext.prototype.value = function(i) {
    if(i===undefined) {
        i = null;
    }
    if(i===null) {
        return this.getTypedRuleContexts(ValueContext);
    } else {
        return this.getTypedRuleContext(ValueContext,i);
    }
};

ArrayContext.prototype.enterRule = function(listener) {
    if(listener instanceof JSONListener ) {
        listener.enterArray(this);
	}
};

ArrayContext.prototype.exitRule = function(listener) {
    if(listener instanceof JSONListener ) {
        listener.exitArray(this);
	}
};




JSONParser.ArrayContext = ArrayContext;

JSONParser.prototype.array = function() {

    var localctx = new ArrayContext(this, this._ctx, this.state);
    this.enterRule(localctx, 6, JSONParser.RULE_array);
    var _la = 0; // Token type
    try {
        this.state = 46;
        this._errHandler.sync(this);
        var la_ = this._interp.adaptivePredict(this._input,4,this._ctx);
        switch(la_) {
        case 1:
            this.enterOuterAlt(localctx, 1);
            this.state = 33;
            this.match(JSONParser.T__4);
            this.state = 34;
            this.value();
            this.state = 39;
            this._errHandler.sync(this);
            _la = this._input.LA(1);
            while(_la===JSONParser.T__1) {
                this.state = 35;
                this.match(JSONParser.T__1);
                this.state = 36;
                this.value();
                this.state = 41;
                this._errHandler.sync(this);
                _la = this._input.LA(1);
            }
            this.state = 42;
            this.match(JSONParser.T__5);
            break;

        case 2:
            this.enterOuterAlt(localctx, 2);
            this.state = 44;
            this.match(JSONParser.T__4);
            this.state = 45;
            this.match(JSONParser.T__5);
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

function ValueContext(parser, parent, invokingState) {
	if(parent===undefined) {
	    parent = null;
	}
	if(invokingState===undefined || invokingState===null) {
		invokingState = -1;
	}
	antlr4.ParserRuleContext.call(this, parent, invokingState);
    this.parser = parser;
    this.ruleIndex = JSONParser.RULE_value;
    return this;
}

ValueContext.prototype = Object.create(antlr4.ParserRuleContext.prototype);
ValueContext.prototype.constructor = ValueContext;

ValueContext.prototype.STRING = function() {
    return this.getToken(JSONParser.STRING, 0);
};

ValueContext.prototype.NUMBER = function() {
    return this.getToken(JSONParser.NUMBER, 0);
};

ValueContext.prototype.object = function() {
    return this.getTypedRuleContext(ObjectContext,0);
};

ValueContext.prototype.array = function() {
    return this.getTypedRuleContext(ArrayContext,0);
};

ValueContext.prototype.enterRule = function(listener) {
    if(listener instanceof JSONListener ) {
        listener.enterValue(this);
	}
};

ValueContext.prototype.exitRule = function(listener) {
    if(listener instanceof JSONListener ) {
        listener.exitValue(this);
	}
};




JSONParser.ValueContext = ValueContext;

JSONParser.prototype.value = function() {

    var localctx = new ValueContext(this, this._ctx, this.state);
    this.enterRule(localctx, 8, JSONParser.RULE_value);
    try {
        this.state = 55;
        switch(this._input.LA(1)) {
        case JSONParser.STRING:
            this.enterOuterAlt(localctx, 1);
            this.state = 48;
            this.match(JSONParser.STRING);
            break;
        case JSONParser.NUMBER:
            this.enterOuterAlt(localctx, 2);
            this.state = 49;
            this.match(JSONParser.NUMBER);
            break;
        case JSONParser.T__0:
            this.enterOuterAlt(localctx, 3);
            this.state = 50;
            this.object();
            break;
        case JSONParser.T__4:
            this.enterOuterAlt(localctx, 4);
            this.state = 51;
            this.array();
            break;
        case JSONParser.T__6:
            this.enterOuterAlt(localctx, 5);
            this.state = 52;
            this.match(JSONParser.T__6);
            break;
        case JSONParser.T__7:
            this.enterOuterAlt(localctx, 6);
            this.state = 53;
            this.match(JSONParser.T__7);
            break;
        case JSONParser.T__8:
            this.enterOuterAlt(localctx, 7);
            this.state = 54;
            this.match(JSONParser.T__8);
            break;
        default:
            throw new antlr4.error.NoViableAltException(this);
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


exports.JSONParser = JSONParser;
