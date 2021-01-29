package org.antlr.v4.test.runtime;

import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;
import java.util.Locale;
import java.util.logging.Logger;

public abstract class BaseRuntimeTestSupport implements RuntimeTestSupport {

	// -J-Dorg.antlr.v4.test.BaseTest.level=FINE
	protected static final Logger logger = Logger.getLogger(BaseRuntimeTestSupport.class.getName());

	public static final String NEW_LINE = System.getProperty("line.separator");
	public static final String PATH_SEP = System.getProperty("path.separator");

	private File tempDir = null;

	/** If error during parser execution, store stderr here; can't return
	 *  stdout and stderr.  This doesn't trap errors from running antlr.
	 */
	private String parseErrors;

	/** Errors found while running antlr */
	private StringBuilder antlrToolErrors;

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

	protected final File getTempDir() {
		return tempDir;
	}

	public final String getTempDirPath() {
		return tempDir==null ? null : tempDir.getAbsolutePath();
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
			tempDir = new File(prop);
		}
		else {
			String dirName = getClass().getSimpleName() +  "-" + Thread.currentThread().getName() + "-" + System.currentTimeMillis();
			tempDir = new File(System.getProperty("java.io.tmpdir"), dirName);
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
			eraseDirectory(getTempDir());
		}
	}

	protected boolean shouldEraseTempDir() {
		if(tempDir == null)
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
			new File(dir,files[i]).delete();
		}
	}

	private static String detectedOS;

	public static String getOS() {
		if (detectedOS == null) {
			String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
			if ((os.indexOf("mac") >= 0) || (os.indexOf("darwin") >= 0)) {
				detectedOS = "mac";
			}
			else if (os.indexOf("win") >= 0) {
				detectedOS = "windows";
			}
			else if (os.indexOf("nux") >= 0) {
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
}
