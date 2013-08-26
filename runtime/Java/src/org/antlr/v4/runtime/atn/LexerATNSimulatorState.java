package org.antlr.v4.runtime.atn;

public class LexerATNSimulatorState {
		private int charPositionInLine;
		private int line;
		private int mode;
		private int startIndex;
		
		public LexerATNSimulatorState(LexerATNSimulator simulator) {
			this.charPositionInLine = simulator.charPositionInLine;
			this.line = simulator.line;
			this.mode = simulator.mode;
			this.startIndex = simulator.startIndex;
		}
		
		public int getCharPositionInLine() {return charPositionInLine;}
		public int getStartIndex() {return startIndex;}
		public int getLine() {return line;}
		public int getMode() {return mode;}
}
