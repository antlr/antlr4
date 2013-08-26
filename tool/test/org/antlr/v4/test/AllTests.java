package org.antlr.v4.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestActionSplitter.class, TestActionTranslation.class,
		TestASTStructure.class, TestATNConstruction.class,
		TestATNDeserialization.class, TestATNInterpreter.class,
		TestATNLexerInterpreter.class, TestATNParserPrediction.class,
		TestATNSerialization.class, TestAttributeChecks.class,
		TestBasicSemanticErrors.class, TestBufferedTokenStream.class,
		TestCommonTokenStream.class, TestCompositeGrammars.class,
		TestFastQueue.class, TestFullContextParsing.class,
		TestGraphNodes.class, TestIntervalSet.class, TestLeftRecursion.class,
		TestLexerErrors.class, TestLexerExec.class,
		TestLexerIncludeStrategy.class, TestListeners.class,
		TestParseErrors.class, TestParserExec.class, TestParseTrees.class,
		TestPerformance.class, TestScopeParsing.class,
		TestSemPredEvalLexer.class, TestSemPredEvalParser.class,
		TestSets.class, TestSymbolIssues.class, TestTokenStreamRewriter.class,
		TestTokenTypeAssignment.class, TestToolSyntaxErrors.class,
		TestTopologicalSort.class, TestUnbufferedCharStream.class,
		TestUnbufferedTokenStream.class })
public class AllTests {

}
