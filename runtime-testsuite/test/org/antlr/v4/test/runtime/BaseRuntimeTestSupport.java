package org.antlr.v4.test.runtime;

import org.antlr.v4.Tool;
import org.antlr.v4.automata.LexerATNFactory;
import org.antlr.v4.automata.ParserATNFactory;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ATNSerializer;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class BaseRuntimeTestSupport implements RuntimeTestSupport {

	// -J-Dorg.antlr.v4.test.BaseTest.level=FINE
	protected static final Logger logger = Logger.getLogger(BaseRuntimeTestSupport.class.getName());

	public static final String NEW_LINE = System.getProperty("line.separator");
	public static final String PATH_SEP = System.getProperty("path.separator");

	private File tempTestDir = null;

	/** If error during parser execution, store stderr here; can't return
	 *  stdout and stderr.  This doesn't trap errors from running antlr.
	 */
	private String parseErrors;

	/** Errors found while running antlr */
	private StringBuilder antlrToolErrors;

	public static String cachingDirectory;

	static {
		cachingDirectory = new File(System.getProperty("java.io.tmpdir"), "ANTLR-runtime-testsuite-cache").getAbsolutePath();
	}

	@org.junit.Rule
	public final TestRule testWatcher = new TestWatcher() {

		@Override
		protected void succeeded(Description description) {
			testSucceeded(description);
		}

	};

	protected void testSucceeded(Description description) {
		// remove tmpdir if no error.
		eraseTempDir();
	}

	@Override
	public File getTempParserDir() {
		return getTempTestDir();
	}

	@Override
	public String getTempParserDirPath() {
		return getTempParserDir() == null ? null : getTempParserDir().getAbsolutePath();
	}

	@Override
	public final File getTempTestDir() {
		return tempTestDir;
	}

	@Override
	public final String getTempDirPath() {
		return tempTestDir ==null ? null : tempTestDir.getAbsolutePath();
	}


	public void setParseErrors(String errors) {
		this.parseErrors = errors;
	}

	public String getParseErrors() {
		return parseErrors;
	}

	public String getANTLRToolErrors() {
		if ( antlrToolErrors.length()==0 ) {
			return null;
		}
		return antlrToolErrors.toString();
	}

	protected abstract String getPropertyPrefix();

	@Override
	public void testSetUp() throws Exception {
		createTempDir();
		antlrToolErrors = new StringBuilder();
	}

	private void createTempDir() {
		// new output dir for each test
		String propName = getPropertyPrefix() + "-test-dir";
		String prop = System.getProperty(propName);
		if(prop!=null && prop.length()>0) {
			tempTestDir = new File(prop);
		} else {
			String dirName = getClass().getSimpleName() +  "-" + Thread.currentThread().getName() + "-" + System.currentTimeMillis();
			tempTestDir = new File(System.getProperty("java.io.tmpdir"), dirName);
		}
	}

	@Override
	public void testTearDown() throws Exception {
	}

	@Override
	public void beforeTest(RuntimeTestDescriptor descriptor) {
	}

	@Override
	public void afterTest(RuntimeTestDescriptor descriptor) {
	}

	public void eraseTempDir() {
		if(shouldEraseTempDir()) {
			eraseDirectory(getTempTestDir());
		}
	}

	protected boolean shouldEraseTempDir() {
		if(tempTestDir == null)
			return false;
		String propName = getPropertyPrefix() + "-erase-test-dir";
		String prop = System.getProperty(propName);
		if (prop != null && prop.length() > 0)
			return Boolean.getBoolean(prop);
		else
			return true;
	}

	public static void eraseDirectory(File dir) {
		if ( dir.exists() ) {
			eraseFilesInDir(dir);
			dir.delete();
		}
	}


	public static void eraseFilesInDir(File dir) {
		String[] files = dir.list();
		for(int i = 0; files!=null && i < files.length; i++) {
			try {
				eraseFile(dir, files[i]);
			} catch(IOException e) {
				logger.info(e.getMessage());
			}
		}
	}

	private static void eraseFile(File dir, String name) throws IOException {
		File file = new File(dir,name);
		if(Files.isSymbolicLink((file.toPath())))
			Files.delete(file.toPath());
		else if(file.isDirectory()) {
			// work around issue where Files.isSymbolicLink returns false on Windows for node/antlr4 linked package
			if("antlr4".equals(name))
				; // logger.warning("antlr4 not seen as a symlink");
			else
				eraseDirectory(file);
		} else
			file.delete();
	}


	private static String detectedOS;

	public static String getOS() {
		if (detectedOS == null) {
			String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
			if (os.contains("mac") || os.contains("darwin")) {
				detectedOS = "mac";
			}
			else if (os.contains("win")) {
				detectedOS = "windows";
			}
			else if (os.contains("nux")) {
				detectedOS = "linux";
			}
			else {
				detectedOS = "unknown";
			}
		}
		return detectedOS;
	}


	public static boolean isWindows() {
		return getOS().equalsIgnoreCase("windows");
	}

	protected ATN createATN(Grammar g, boolean useSerializer) {
		if ( g.atn==null ) {
			semanticProcess(g);
			assertEquals(0, g.tool.getNumErrors());

			ParserATNFactory f = g.isLexer() ? new LexerATNFactory((LexerGrammar) g) : new ParserATNFactory(g);

			g.atn = f.createATN();
			assertEquals(0, g.tool.getNumErrors());
		}

		ATN atn = g.atn;
		if ( useSerializer ) {
			// sets some flags in ATN
			IntegerList serialized = ATNSerializer.getSerialized(atn);
			return new ATNDeserializer().deserialize(serialized.toArray());
		}

		return atn;
	}

	protected void semanticProcess(Grammar g) {
		if ( g.ast!=null && !g.ast.hasErrors ) {
//			System.out.println(g.ast.toStringTree());
			Tool antlr = new Tool();
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();
			if ( g.getImportedGrammars()!=null ) { // process imported grammars (if any)
				for (Grammar imp : g.getImportedGrammars()) {
					antlr.processNonCombinedGrammar(imp, false);
				}
			}
		}
	}

}
