package org.antlr.v4.test.runtime;

import org.antlr.v4.runtime.misc.Utils;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import static org.antlr.v4.test.runtime.RuntimeTestUtils.FileSeparator;

public class FileUtils {
	public static void writeFile(String dir, String fileName, String content) {
		try {
			Utils.writeFile(dir + FileSeparator + fileName, content, "UTF-8");
		}
		catch (IOException ioe) {
			System.err.println("can't write file");
			ioe.printStackTrace(System.err);
		}
	}

	public static String readFile(String dir, String fileName) {
		try {
			return String.copyValueOf(Utils.readFile(dir+"/"+fileName, "UTF-8"));
		}
		catch (IOException ioe) {
			System.err.println("can't read file");
			ioe.printStackTrace(System.err);
		}
		return null;
	}

	public static void copyFile(File source, File dest) throws IOException {
		InputStream is = Files.newInputStream(source.toPath());
		OutputStream os = Files.newOutputStream(dest.toPath());
		byte[] buf = new byte[4 << 10];
		int l;
		while ((l = is.read(buf)) > -1) {
			os.write(buf, 0, l);
		}
		is.close();
		os.close();
	}

	public static void moveFile(File sourceFir, String destDir, String fileName) throws IOException {
		File file = new File(sourceFir, fileName);
		if (!file.renameTo(new File(destDir, fileName))) {
			throw new IOException("can't move source file " + file);
		}
	}

	public static void mkdir(String dir) {
		File f = new File(dir);
		f.mkdirs();
	}

	public static void copyDirectory(final Path source, final Path target, final CopyOption... options)
			throws IOException {
		Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), 2147483647, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
					throws IOException {
				Files.createDirectories(target.resolve(source.relativize(dir)));
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
					throws IOException {
				Files.copy(file, target.resolve(source.relativize(file)), options);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public static void deleteDirectory(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				deleteDirectory(c);
		}
		if (!f.delete())
			throw new FileNotFoundException("Failed to delete file: " + f);
	}

	public static void eraseDirectory(File dir) {
		if ( dir != null && dir.exists() ) {
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
				//logger.info(e.getMessage());
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
}
