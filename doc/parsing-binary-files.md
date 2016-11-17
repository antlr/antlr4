# Parsing Binary Files

```
grammar IP;

file : ip+ (MARKER ip)* ;

ip : BYTE BYTE BYTE BYTE ;

MARKER : '\u00CA' '\u00FE' ;
BYTE : '\u0000'..'\u00FF' ;
```

```java
package binary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class WriteBinaryFile {
	public static final byte[] bytes = {
		(byte)172, 0, 0, 1, (byte)0xCA, (byte)0xFE,
		(byte)10, 10, 10, 1, (byte)0xCA, (byte)0xFE,
		(byte)10, 10, 10, 99
	};

	public static void main(String[] args) throws IOException {
		Files.write(new File("resources/ips").toPath(), bytes);
	}
}
```

```java
package binary;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/** Test ANTLR parser that reads binary files full of IP addresses with
 *  a 0xCAFE marker in between. Output should be:
 [172, 0, 0, 1]
 [10, 10, 10, 1]
 [10, 10, 10, 99]
 */
public class TestBinary {
	public static void main(String[] args) throws Exception {
		ANTLRFileStream bytesAsChar = new BinaryANTLRFileStream("resources/ips");
		IPLexer lexer = new IPLexer(bytesAsChar);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();
		IPParser parser = new IPParser(tokens);
		ParseTree tree = parser.file();
		IPBaseListener listener = new MyIPListener();
		ParseTreeWalker.DEFAULT.walk(listener, tree);
	}
}
```

```
package binary;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.misc.Interval;

import java.io.IOException;

/** make a stream treating file as full of single unsigned byte characters */
class BinaryANTLRFileStream extends ANTLRFileStream {
	public BinaryANTLRFileStream(String fileName) throws IOException {
		super(fileName, "ISO-8859-1");
	}

	/** Print the decimal value rather than treat as char */
	@Override
	public String getText(Interval interval) {
		StringBuilder buf = new StringBuilder();
		int start = interval.a;
		int stop = interval.b;
		if(stop >= this.n) {
			stop = this.n - 1;
		}

		for (int i = start; i<=stop; i++) {
			int v = data[i];
			buf.append(v);
		}
		return buf.toString();
	}
}
```
