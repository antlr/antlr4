package org.antlr.v4.runtime;

//import org.antlr.v4.runtime.atn.LexerATNSimulatorState;
import org.antlr.v4.runtime.misc.Pair;

public class LexerScannerStateStackItem {
	private CharStream input;
    private Pair<TokenSource, CharStream> tokenFactorySourcePair;
    //private Integer streamRef;
    //private LexerATNSimulatorState lexerATNSimulatorState;
    
	public LexerScannerStateStackItem(CharStream input,
			Pair<TokenSource, CharStream> tokenFactorySourcePair) {
		this.input = input;
		this.tokenFactorySourcePair = tokenFactorySourcePair;
	}

	public CharStream getInput() {return input;}
	public Pair<TokenSource, CharStream> getTokenFactorySourcePair() {return tokenFactorySourcePair;}
}
