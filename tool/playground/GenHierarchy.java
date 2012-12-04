import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GenHierarchy {
	public static void main(String[] args) throws Exception {
		// START: input
		String inputFile = null;
		if ( args.length>0 ) inputFile = args[0];
		List<String> files = getFilenames(new File(inputFile));
		for (String file : files) {
			InputStream is = new FileInputStream(file);
			ANTLRInputStream input = new ANTLRInputStream(is);
			// END: input

//			System.out.println(file);
			// START: launch
			JavaLRLexer lexer = new JavaLRLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			JavaLRParser parser = new JavaLRParser(tokens);
			ParserRuleContext tree = parser.compilationUnit(); // parse

			ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
			ExtractInheritance extractor = new ExtractInheritance(parser);
			walker.walk(extractor, tree); // initiate walk of tree with listener
		}
		// END: launch
	}

	public static List<String> getFilenames(File f) throws Exception {
		List<String> files = new ArrayList<String>();
		getFilenames_(f, files);
		return files;
	}

	public static void getFilenames_(File f, List<String> files) throws Exception {
		// If this is a directory, walk each file/dir in that directory
		if (f.isDirectory()) {
			String flist[] = f.list();
			for (String aFlist : flist) {
				getFilenames_(new File(f, aFlist), files);
			}
		}

		// otherwise, if this is a java file, parse it!
		else if ( ((f.getName().length()>5) &&
			f.getName().substring(f.getName().length()-5).equals(".java")) )
		{
			files.add(f.getAbsolutePath());
		}
	}

}
