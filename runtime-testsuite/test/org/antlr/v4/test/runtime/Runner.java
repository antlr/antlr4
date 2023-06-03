package org.antlr.v4.test.runtime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.antlr.v4.test.runtime.FileUtils.deleteDirectory;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.TempDirectory;

public class Runner implements AutoCloseable {
	private boolean saveTestDir;

	protected final Path tempTestDir;

	public Runner() {
		this(null, false);
	}

	public Runner(Path tempDir, boolean saveTestDir) {
		if (tempDir == null) {
			String dirName = getClass().getSimpleName() + "-" + Thread.currentThread().getName() + "-" + System.currentTimeMillis();
			tempTestDir = Paths.get(TempDirectory, dirName);
		}
		else {
			tempTestDir = tempDir;
		}
		FileUtils.mkdir(getTempDirPath());
		this.saveTestDir = saveTestDir;
	}

	public final String getTempDirPath() { return tempTestDir.toString();}

	public void setSaveTestDir(boolean saveTestDir) {
		this.saveTestDir = saveTestDir;
	}

	@Override
	public void close() {
		if (!saveTestDir) {
			File dirFile = tempTestDir.toFile();
			if (dirFile.exists()) {
				try {
					deleteDirectory(dirFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
