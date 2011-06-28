import org.antlr.v4.Tool;
import org.antlr.v4.automata.ParserATNFactory;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.tool.*;

import java.util.List;

public class Test {
	public static void main(String[] args) throws Exception {
//		T t = new T(new ANTLRFileStream(args[0]));
//		CommonTokenStream tokens = new CommonTokenStream(t);
//		tokens.fill();
//		for (Object tok : tokens.getTokens()) {
//			System.out.println(tok);
//		}

	}

	public static void dump() throws Exception {
		Grammar g = new Grammar(
			"grammar T;\n" +
			"\n" +
			"a : A | b ;\n" +
			"\n" +
			"b : B C | B D ;\n" +
			"\n" +
			"c : (B C)? B D ;\n" +
			"\n" +
			"d : (B C|B A)* B D ;\n" +
			"\n" +
			"e : (B C|B A)+ B D ;"
		);
		if ( g.ast!=null && !g.ast.hasErrors ) {
			System.out.println(g.ast.toStringTree());
			Tool antlr = new Tool();
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();
			if ( g.getImportedGrammars()!=null ) { // process imported grammars (if any)
				for (Grammar imp : g.getImportedGrammars()) {
					antlr.process(imp);
				}
			}
		}

		ParserATNFactory f = new ParserATNFactory(g);
		ATN atn = f.createATN();

		DOTGenerator dot = new DOTGenerator(g);
		System.out.println(dot.getDOT(atn.ruleToStartState[g.getRule("d").index]));
	}

	public static class IntTokenStream implements TokenStream {
		List<Integer> types;
		int p=0;
		public IntTokenStream(List<Integer> types) { this.types = types; }

		public void consume() { p++; }

		public int LA(int i) { return LT(i).getType(); }

		public int mark() {
			return index();
		}

		public int index() { return p; }

		public void rewind(int marker) {
		}

		public void rewind() {
		}

		public void release(int marker) {
			seek(marker);
		}

		public void seek(int index) {
			p = index;
		}

		public int size() {
			return types.size();
		}

		public String getSourceName() {
			return null;
		}

		public Token LT(int i) {
			if ( (p+i-1)>=types.size() ) return new CommonToken(-1);
			return new CommonToken(types.get(p+i-1));
		}

		public int range() {
			return 0;
		}

		public Token get(int i) {
			return new CommonToken(types.get(i));
		}

		public TokenSource getTokenSource() {
			return null;
		}

		public String toString(int start, int stop) {
			return null;
		}

		public String toString(Token start, Token stop) {
			return null;
		}
	}
}
