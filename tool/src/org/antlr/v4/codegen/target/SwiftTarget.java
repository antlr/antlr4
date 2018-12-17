/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.codegen.UnicodeEscapes;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.ATNType;
import org.antlr.v4.runtime.atn.ActionTransition;
import org.antlr.v4.runtime.atn.AtomTransition;
import org.antlr.v4.runtime.atn.BlockStartState;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.LexerAction;
import org.antlr.v4.runtime.atn.LexerChannelAction;
import org.antlr.v4.runtime.atn.LexerCustomAction;
import org.antlr.v4.runtime.atn.LexerModeAction;
import org.antlr.v4.runtime.atn.LexerPushModeAction;
import org.antlr.v4.runtime.atn.LexerTypeAction;
import org.antlr.v4.runtime.atn.LoopEndState;
import org.antlr.v4.runtime.atn.PrecedencePredicateTransition;
import org.antlr.v4.runtime.atn.PredicateTransition;
import org.antlr.v4.runtime.atn.RangeTransition;
import org.antlr.v4.runtime.atn.RuleStartState;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.runtime.atn.SetTransition;
import org.antlr.v4.runtime.atn.Transition;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.GrammarAST;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.StringRenderer;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SwiftTarget extends Target {

    /**
     * The Swift target can cache the code generation templates.
     */
    private static final ThreadLocal<STGroup> targetTemplates = new ThreadLocal<STGroup>();

    protected static final String[] swiftKeywords = {
            "associatedtype", "class", "deinit", "enum", "extension", "func", "import", "init", "inout", "internal",
            "let", "operator", "private", "protocol", "public", "static", "struct", "subscript", "typealias", "var",
            "break", "case", "continue", "default", "defer", "do", "else", "fallthrough", "for", "guard", "if",
            "in", "repeat", "return", "switch", "where", "while",
            "as", "catch", "dynamicType", "false", "is", "nil", "rethrows", "super", "self", "Self", "throw", "throws",
            "true", "try", "__COLUMN__", "__FILE__", "__FUNCTION__","__LINE__", "#column", "#file", "#function", "#line", "_" , "#available", "#else", "#elseif", "#endif", "#if", "#selector",
            "associativity", "convenience", "dynamic", "didSet", "final", "get", "infix", "indirect", "lazy",
            "left", "mutating", "none", "nonmutating", "optional", "override", "postfix", "precedence",
            "prefix", "Protocol", "required", "right", "set", "Type", "unowned", "weak", "willSet"
 };

    /** Avoid grammar symbols in this set to prevent conflicts in gen'd code. */
    protected final Set<String> badWords = new HashSet<String>();

    public String lexerAtnJSON = null;
    public String parserAtnJSON = null;
    public SwiftTarget(CodeGenerator gen) {
        super(gen, "Swift");
    }

    @Override
    public String getVersion() {
        return "4.7.2"; // Java and tool versions move in lock step
    }

    public Set<String> getBadWords() {
        if (badWords.isEmpty()) {
            addBadWords();
        }

        return badWords;
    }

    protected void addBadWords() {
        badWords.addAll(Arrays.asList(swiftKeywords));
        badWords.add("rule");
        badWords.add("parserRule");
    }

    @Override
    public int getSerializedATNSegmentLimit() {
        // 65535 is the class file format byte limit for a UTF-8 encoded string literal
        // 3 is the maximum number of bytes it takes to encode a value in the range 0-0xFFFF
        return 65535 / 3;
    }

    @Override
    protected boolean visibleGrammarSymbolCausesIssueInGeneratedCode(GrammarAST idNode) {
        return getBadWords().contains(idNode.getText());
    }
    @Override
    protected void genFile(Grammar g,
                           ST outputFileST,
                           String fileName)
    {
        super.genFile(g,outputFileST,fileName);

        if (g.isLexer()  && lexerAtnJSON == null) {
            lexerAtnJSON = getLexerOrParserATNJson(g, fileName);
        }
        else if (!g.isLexer()  && parserAtnJSON == null && g.atn != null) {
            parserAtnJSON = getLexerOrParserATNJson(g, fileName);
        }

        if (fileName.endsWith(CodeGenerator.VOCAB_FILE_EXTENSION)) {
            String jsonFileName = fileName.substring(0,fileName.lastIndexOf(CodeGenerator.VOCAB_FILE_EXTENSION));
            if (lexerAtnJSON != null) {
                jsonFileName = jsonFileName +   "ATN.swift";
                // System.out.println(jsonFileName);
                //System.out.println(lexerAtnJSON);
                writeFile(lexerAtnJSON,g,jsonFileName);
            }

            if (parserAtnJSON != null) {
                jsonFileName = jsonFileName +   "ParserATN.swift";
                // System.out.println(jsonFileName);
                //System.out.println(parserAtnJSON);
                writeFile(parserAtnJSON,g,jsonFileName);
            }
        }

//        else if (g instanceof ParseR) {
//            System.out.println("parserGrammar");
//        }
//
        //getCodeGenerator().write(outputFileST, fileName);
    }

    private String getLexerOrParserATNJson(Grammar g, String fileName) {
        ST extST = getTemplates().getInstanceOf("codeFileExtension");
        String className = fileName.substring(0,fileName.lastIndexOf(extST.render()));

        String JSON = "class " + className + "ATN {\n" +
                "    let jsonString: String = \"" +
                serializeTojson(g.atn).replaceAll("\"","\\\\\"") +"\"\n}" ;  //.replaceAll("\"", "\\\\\"");
        return JSON;
    }

    private  void writeFile(String content,Grammar g,String fileName) {

        try {
            Writer w =    this.getCodeGenerator().tool.getOutputFileWriter(g, fileName);
            w.write(content);
            w.close();
        }
        catch (IOException ioe) {
            this.getCodeGenerator().tool.errMgr.toolError(ErrorType.CANNOT_WRITE_FILE,
                    ioe,
                    fileName);
        }
    }
    @Override
    protected STGroup loadTemplates() {
        STGroup result = targetTemplates.get();
        if (result == null) {
            result = super.loadTemplates();
            result.registerRenderer(String.class, new SwiftStringRenderer(), true);
            targetTemplates.set(result);
        }

        return result;
    }
    //added by janyou -->
    public String serializeTojson(ATN atn) {
        JsonObjectBuilder builder =  Json.createObjectBuilder();
        builder.add("version", ATNDeserializer.SERIALIZED_VERSION);
        builder.add("uuid", ATNDeserializer.SERIALIZED_UUID.toString());

        // convert grammar type to ATN const to avoid dependence on ANTLRParser
        builder.add("grammarType",atn.grammarType.ordinal());
        builder.add("maxTokenType",atn.maxTokenType);

        //states
        int nedges = 0;

        Map<IntervalSet, Integer> setIndices = new HashMap<IntervalSet, Integer>();
        List<IntervalSet> sets = new ArrayList<IntervalSet>();
        JsonArrayBuilder statesBuilder = Json.createArrayBuilder() ;
        IntegerList nonGreedyStates = new IntegerList();
        IntegerList precedenceStates = new IntegerList();
        for (ATNState s : atn.states) {
            JsonObjectBuilder stateBuilder = Json.createObjectBuilder();
            if ( s==null ) { // might be optimized away
                statesBuilder.addNull();
                continue;
            }

            int stateType = s.getStateType();

            stateBuilder.add("stateType",stateType);
            //stateBuilder.add("stateNumber",s.stateNumber);
            stateBuilder.add("ruleIndex",s.ruleIndex);

            if (s instanceof DecisionState && ((DecisionState)s).nonGreedy) {
                nonGreedyStates.add(s.stateNumber);
            }

            if (s instanceof RuleStartState && ((RuleStartState)s).isLeftRecursiveRule) {
                precedenceStates.add(s.stateNumber);
            }


            if ( s.getStateType() == ATNState.LOOP_END ) {
                stateBuilder.add("detailStateNumber",((LoopEndState)s).loopBackState.stateNumber);
            }
            else if ( s instanceof BlockStartState ) {
                stateBuilder.add("detailStateNumber",((BlockStartState)s).endState.stateNumber);
            }

            if (s.getStateType() != ATNState.RULE_STOP) {
                // the deserializer can trivially derive these edges, so there's no need to serialize them
                nedges += s.getNumberOfTransitions();
            }
            for (int i=0; i<s.getNumberOfTransitions(); i++) {
                Transition t = s.transition(i);
                int edgeType = Transition.serializationTypes.get(t.getClass());
                if ( edgeType == Transition.SET || edgeType == Transition.NOT_SET ) {
                    SetTransition st = (SetTransition)t;
                    if (!setIndices.containsKey(st.set)) {
                        sets.add(st.set);
                        setIndices.put(st.set, sets.size() - 1);
                    }
                }
            }
            statesBuilder.add(stateBuilder);
        }
        builder.add("states",statesBuilder);


        // non-greedy states
        JsonArrayBuilder nonGreedyStatesBuilder = Json.createArrayBuilder() ;
        for (int i = 0; i < nonGreedyStates.size(); i++) {
            nonGreedyStatesBuilder.add(nonGreedyStates.get(i));
        }
        builder.add("nonGreedyStates",nonGreedyStatesBuilder);


        // precedence states
        JsonArrayBuilder precedenceStatesBuilder = Json.createArrayBuilder() ;
        for (int i = 0; i < precedenceStates.size(); i++) {
            precedenceStatesBuilder.add(precedenceStates.get(i));
        }
        builder.add("precedenceStates",precedenceStatesBuilder);

        JsonArrayBuilder ruleToStartStateBuilder = Json.createArrayBuilder() ;
        int nrules = atn.ruleToStartState.length;

        for (int r=0; r<nrules; r++) {
            JsonObjectBuilder stateBuilder = Json.createObjectBuilder();
            ATNState ruleStartState = atn.ruleToStartState[r];

            stateBuilder.add("stateNumber",ruleStartState.stateNumber);
            if (atn.grammarType == ATNType.LEXER) {
//				if (atn.ruleToTokenType[r] == Token.EOF) {
//					//data.add(Character.MAX_VALUE);
//					stateBuilder.add("ruleToTokenType",-1);
//				}
//				else {
//					//data.add(atn.ruleToTokenType[r]);
                stateBuilder.add("ruleToTokenType",atn.ruleToTokenType[r]);
//				}
            }
            ruleToStartStateBuilder.add(stateBuilder);
        }
        builder.add("ruleToStartState",ruleToStartStateBuilder);


        JsonArrayBuilder modeToStartStateBuilder = Json.createArrayBuilder() ;
        int nmodes = atn.modeToStartState.size();
        if ( nmodes>0 ) {
            for (ATNState modeStartState : atn.modeToStartState) {

                modeToStartStateBuilder.add(modeStartState.stateNumber);
            }
        }
        builder.add("modeToStartState",modeToStartStateBuilder);


        JsonArrayBuilder nsetsBuilder = Json.createArrayBuilder() ;
        int nsets = sets.size();
        //data.add(nsets);
        builder.add("nsets",nsets);

        for (IntervalSet set : sets) {
            JsonObjectBuilder setBuilder = Json.createObjectBuilder();
            boolean containsEof = set.contains(Token.EOF);
            if (containsEof && set.getIntervals().get(0).b == Token.EOF) {
                //data.add(set.getIntervals().size() - 1);

                setBuilder.add("size",set.getIntervals().size() - 1);
            }
            else {
                //data.add(set.getIntervals().size());

                setBuilder.add("size",set.getIntervals().size());
            }
            setBuilder.add("containsEof",containsEof ? 1 : 0);
            JsonArrayBuilder IntervalsBuilder = Json.createArrayBuilder() ;
            for (Interval I : set.getIntervals()) {
                JsonObjectBuilder IntervalBuilder = Json.createObjectBuilder();
                if (I.a == Token.EOF) {
                    if (I.b == Token.EOF) {
                        continue;
                    }
                    else {
                        IntervalBuilder.add("a",0);
                        //data.add(0);
                    }
                }
                else {
                    IntervalBuilder.add("a",I.a);

                    //data.add(I.a);
                }
                IntervalBuilder.add("b",I.b);
                IntervalsBuilder.add(IntervalBuilder);
            }
            setBuilder.add("Intervals",IntervalsBuilder);
            nsetsBuilder.add(setBuilder);
        }

        builder.add("IntervalSet",nsetsBuilder);
        //builder.add("nedges",nedges);
        JsonArrayBuilder allTransitionsBuilder = Json.createArrayBuilder() ;

        for (ATNState s : atn.states) {

            if ( s==null ) {
                // might be optimized away
                continue;
            }

            if (s.getStateType() == ATNState.RULE_STOP) {
                continue;
            }
            JsonArrayBuilder  transitionsBuilder = Json.createArrayBuilder() ;

            for (int i=0; i<s.getNumberOfTransitions(); i++) {
                JsonObjectBuilder transitionBuilder = Json.createObjectBuilder();
                Transition t = s.transition(i);

                if (atn.states.get(t.target.stateNumber) == null) {
                    throw new IllegalStateException("Cannot serialize a transition to a removed state.");
                }

                int src = s.stateNumber;
                int trg = t.target.stateNumber;
                int edgeType = Transition.serializationTypes.get(t.getClass());
                int arg1 = 0;
                int arg2 = 0;
                int arg3 = 0;
                switch ( edgeType ) {
                    case Transition.RULE :
                        trg = ((RuleTransition)t).followState.stateNumber;
                        arg1 = ((RuleTransition)t).target.stateNumber;
                        arg2 = ((RuleTransition)t).ruleIndex;
                        arg3 = ((RuleTransition)t).precedence;
                        break;
                    case Transition.PRECEDENCE:
                        PrecedencePredicateTransition ppt = (PrecedencePredicateTransition)t;
                        arg1 = ppt.precedence;
                        break;
                    case Transition.PREDICATE :
                        PredicateTransition pt = (PredicateTransition)t;
                        arg1 = pt.ruleIndex;
                        arg2 = pt.predIndex;
                        arg3 = pt.isCtxDependent ? 1 : 0 ;
                        break;
                    case Transition.RANGE :
                        arg1 = ((RangeTransition)t).from;
                        arg2 = ((RangeTransition)t).to;
                        if (arg1 == Token.EOF) {
                            arg1 = 0;
                            arg3 = 1;
                        }

                        break;
                    case Transition.ATOM :
                        arg1 = ((AtomTransition)t).label;
                        if (arg1 == Token.EOF) {
                            arg1 = 0;
                            arg3 = 1;
                        }

                        break;
                    case Transition.ACTION :
                        ActionTransition at = (ActionTransition)t;
                        arg1 = at.ruleIndex;
                        arg2 = at.actionIndex;
//						if (arg2 == -1) {
//							arg2 = 0xFFFF;
//						}

                        arg3 = at.isCtxDependent ? 1 : 0 ;
                        break;
                    case Transition.SET :
                        arg1 = setIndices.get(((SetTransition)t).set);
                        break;
                    case Transition.NOT_SET :
                        arg1 = setIndices.get(((SetTransition)t).set);
                        break;
                    case Transition.WILDCARD :
                        break;
                }
                transitionBuilder.add("src",src);
                transitionBuilder.add("trg",trg);
                transitionBuilder.add("edgeType",edgeType);
                transitionBuilder.add("arg1",arg1);
                transitionBuilder.add("arg2",arg2);
                transitionBuilder.add("arg3",arg3);
                transitionsBuilder.add(transitionBuilder);
            }
            allTransitionsBuilder.add(transitionsBuilder);
        }

        builder.add("allTransitionsBuilder",allTransitionsBuilder);
        int ndecisions = atn.decisionToState.size();
        //data.add(ndecisions);
        JsonArrayBuilder  decisionToStateBuilder = Json.createArrayBuilder() ;

        for (DecisionState decStartState : atn.decisionToState) {
            //data.add(decStartState.stateNumber);
            decisionToStateBuilder.add(decStartState.stateNumber);
        }
        builder.add("decisionToState",decisionToStateBuilder);
        //
        // LEXER ACTIONS
        //
        JsonArrayBuilder  lexerActionsBuilder = Json.createArrayBuilder() ;

        if (atn.grammarType == ATNType.LEXER) {
            //data.add(atn.lexerActions.length);
            for (LexerAction action : atn.lexerActions) {
                JsonObjectBuilder lexerActionBuilder = Json.createObjectBuilder();

                lexerActionBuilder.add("actionType",action.getActionType().ordinal());
                //data.add(action.getActionType().ordinal());
                switch (action.getActionType()) {
                    case CHANNEL:
                        int channel = ((LexerChannelAction)action).getChannel();

                        lexerActionBuilder.add("a",channel);
                        lexerActionBuilder.add("b",0);
                        break;

                    case CUSTOM:
                        int ruleIndex = ((LexerCustomAction)action).getRuleIndex();
                        int actionIndex = ((LexerCustomAction)action).getActionIndex();

                        lexerActionBuilder.add("a",ruleIndex);
                        lexerActionBuilder.add("b",actionIndex);
                        break;

                    case MODE:
                        int mode = ((LexerModeAction)action).getMode();

                        lexerActionBuilder.add("a",mode);
                        lexerActionBuilder.add("b",0);
                        break;


                    case MORE:

                        lexerActionBuilder.add("a",0);
                        lexerActionBuilder.add("b",0);
                        break;

                    case POP_MODE:
                        lexerActionBuilder.add("a",0);
                        lexerActionBuilder.add("b",0);
                        break;

                    case PUSH_MODE:
                        mode = ((LexerPushModeAction)action).getMode();

                        lexerActionBuilder.add("a",mode);
                        lexerActionBuilder.add("b",0);
                        break;

                    case SKIP:
                        lexerActionBuilder.add("a",0);
                        lexerActionBuilder.add("b",0);
                        break;

                    case TYPE:
                        int type = ((LexerTypeAction)action).getType();

                        lexerActionBuilder.add("a",type);
                        lexerActionBuilder.add("b",0);
                        break;

                    default:
                        String message = String.format(Locale.getDefault(), "The specified lexer action type %s is not valid.", action.getActionType());
                        throw new IllegalArgumentException(message);
                }
                lexerActionsBuilder.add(lexerActionBuilder);
            }
        }
        builder.add("lexerActions",lexerActionsBuilder);
        // don't adjust the first value since that's the version number
//		for (int i = 1; i < data.size(); i++) {
//			if (data.get(i) < Character.MIN_VALUE || data.get(i) > Character.MAX_VALUE) {
//				throw new UnsupportedOperationException("Serialized ATN data element out of range.");
//			}
//
//			int value = (data.get(i) + 2) & 0xFFFF;
//			data.set(i, value);
//		}
        JsonObject data = builder.build();
        //  System.out.print(data.toString());
        return data.toString();
    }

    //<--
    protected static class SwiftStringRenderer extends StringRenderer {

        @Override
        public String toString(Object o, String formatString, Locale locale) {
            if ("java-escape".equals(formatString)) {
                // 5C is the hex code for the \ itself
                return ((String)o).replace("\\u", "\\u005Cu");
            }

            return super.toString(o, formatString, locale);
        }

    }

	@Override
	protected void appendUnicodeEscapedCodePoint(int codePoint, StringBuilder sb) {
		UnicodeEscapes.appendSwiftStyleEscapedCodePoint(codePoint, sb);
	}
}
