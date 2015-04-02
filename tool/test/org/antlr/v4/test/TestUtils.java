package org.antlr.v4.test;

import org.antlr.v4.Tool;
import org.antlr.v4.automata.ATNPrinter;
import org.antlr.v4.automata.LexerATNFactory;
import org.antlr.v4.automata.ParserATNFactory;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.tool.*;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by jason on 3/24/15.
 */
public class TestUtils {

    public static ATN createATN(Grammar g, boolean useSerializer) {
        if (g.atn == null) {
            semanticProcess(g);
            assertEquals(0, g.tool.getNumErrors());

            ParserATNFactory f;
            if (g.isLexer()) {
                f = new LexerATNFactory((LexerGrammar) g);
            } else {
                f = new ParserATNFactory(g);
            }

            g.atn = f.createATN();
            assertEquals(0, g.tool.getNumErrors());
        }

        ATN atn = g.atn;
        if (useSerializer) {
            char[] serialized = ATNSerializer.getSerializedAsChars(atn);
            return new ATNDeserializer().deserialize(serialized);
        }

        return atn;
    }

    public static void semanticProcess(Grammar g) {
        if (g.ast != null && !g.ast.hasErrors) {
            System.out.println(g.ast.toStringTree());
            Tool antlr = new Tool();
            SemanticPipeline sem = new SemanticPipeline(g);
            sem.process();
            if (g.getImportedGrammars() != null) { // process imported grammars (if any)
                for (Grammar imp : g.getImportedGrammars()) {
                    antlr.processNonCombinedGrammar(imp, false);
                }
            }
        }
    }

    public static IntegerList getTokenTypesViaATN(String input, LexerATNSimulator lexerATN) {
        ANTLRInputStream in = new ANTLRInputStream(input);
        IntegerList tokenTypes = new IntegerList();
        int ttype;
        do {
            ttype = lexerATN.match(in, Lexer.DEFAULT_MODE);
            tokenTypes.add(ttype);
        } while (ttype != Token.EOF);
        return tokenTypes;
    }

    public static List<String> getTokenTypes(LexerGrammar lg,
                                             ATN atn,
                                             CharStream input) {
        LexerATNSimulator interp = new LexerATNSimulator(atn,
                                                         new DFA[]{new DFA(atn.modeToStartState.get(Lexer.DEFAULT_MODE))},
                                                         null);
        List<String> tokenTypes = new ArrayList<String>();
        int ttype;
        boolean hitEOF = false;
        do {
            if (hitEOF) {
                tokenTypes.add("EOF");
                break;
            }
            int t = input.LA(1);
            ttype = interp.match(input, Lexer.DEFAULT_MODE);
            if (ttype == Token.EOF) {
                tokenTypes.add("EOF");
            } else {
                tokenTypes.add(lg.typeToTokenList.get(ttype));
            }

            if (t == IntStream.EOF) {
                hitEOF = true;
            }
        } while (ttype != Token.EOF);
        return tokenTypes;
    }


    public static void checkRuleATN(Grammar g, String ruleName, String expecting) {
        DOTGenerator dot = new DOTGenerator(g);
        System.out.println(dot.getDOT(g.atn.ruleToStartState[g.getRule(ruleName).index]));

        Rule r = g.getRule(ruleName);
        ATNState startState = g.atn.ruleToStartState[r.index];
        ATNPrinter serializer = new ATNPrinter(g, startState);
        String result = serializer.asString();

        //System.out.print(result);
        assertEquals(expecting, result);
    }

    public static void checkGrammarSemanticsWarning(ErrorQueue equeue,
                                                    GrammarSemanticsMessage expectedMessage)
            throws Exception {
        ANTLRMessage foundMsg = null;
        for (int i = 0; i < equeue.warnings.size(); i++) {
            ANTLRMessage m = equeue.warnings.get(i);
            if (m.getErrorType() == expectedMessage.getErrorType()) {
                foundMsg = m;
            }
        }
        assertNotNull("no error; " + expectedMessage.getErrorType() + " expected", foundMsg);
        assertTrue("error is not a GrammarSemanticsMessage",
                   foundMsg instanceof GrammarSemanticsMessage);
        assertEquals(Arrays.toString(expectedMessage.getArgs()), Arrays.toString(foundMsg.getArgs()));
        if (equeue.size() != 1) {
            System.err.println(equeue);
        }
    }


    public static List<String> realElements(List<String> elements) {
        return elements.subList(Token.MIN_USER_TOKEN_TYPE, elements.size());
    }


    /**
     * Return map sorted by key
     */
    public static <K extends Comparable<? super K>, V> LinkedHashMap<K, V> sort(Map<K, V> data) {
        LinkedHashMap<K, V> dup = new LinkedHashMap<K, V>();
        List<K> keys = new ArrayList<K>();
        keys.addAll(data.keySet());
        Collections.sort(keys);
        for (K k : keys) {
            dup.put(k, data.get(k));
        }
        return dup;
    }

    public static Matcher<List<Diagnostic<? extends JavaFileObject>>> containsNoErrors() {
        return CHECK_LIST_FOR_ERRORS;
    }

    public static Matcher<DiagnosticCollector<JavaFileObject>> hasNoErrors() {
        return CHECK_DIAGNOSTICS_FOR_ERRORS;
    }


    static final Matcher<List<Diagnostic<? extends JavaFileObject>>> CHECK_LIST_FOR_ERRORS =
            new CustomTypeSafeMatcher<List<Diagnostic<? extends JavaFileObject>>>("no compilation errors") {
                @Override
                protected boolean matchesSafely(List<Diagnostic<? extends JavaFileObject>> item) {
                    for (Diagnostic<? extends JavaFileObject> diagnostic : item) {
                        if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                            return false;
                        }
                    }
                    return true;
                }

                @Override
                protected void describeMismatchSafely(List<Diagnostic<? extends JavaFileObject>> item,
                                                      org.hamcrest.Description mismatchDescription) {
                    for (Diagnostic<? extends JavaFileObject> diagnostic : item) {
                        if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                            mismatchDescription
                                    .appendText("(first) error:\n")
                                    .appendText(diagnostic.getMessage(Locale.getDefault()))
                                    .appendText("\n source: \n");
                            String source;
                            try {
                                source = diagnostic.getSource().getCharContent(true).toString();

                            } catch (IOException e) {
                                source = "????";
                            }
                            mismatchDescription.appendText(source);

                        }
                    }

                    mismatchDescription.appendText("\n all diagnostics:\n")
                                       .appendValueList("[", "\n\t", "]", item)
                                       .appendText("\n");
                }
            };
    static final Matcher<DiagnosticCollector<JavaFileObject>> CHECK_DIAGNOSTICS_FOR_ERRORS =
            new FeatureMatcher<DiagnosticCollector<JavaFileObject>, List<Diagnostic<? extends JavaFileObject>>>
                    (CHECK_LIST_FOR_ERRORS, "collector collects", "") {
                @Override
                protected List<Diagnostic<? extends JavaFileObject>> featureValueOf(DiagnosticCollector<JavaFileObject> actual) {
                    return actual.getDiagnostics();
                }
            };

}
