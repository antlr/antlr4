import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

public abstract class RuntimeTestParser extends Parser {
	protected java.io.PrintStream outStream = System.out;

	public RuntimeTestParser(TokenStream input) {
		super(input);
	}

	public void setOutStream(java.io.PrintStream outStream) {
		this.outStream = outStream;
	}
}
