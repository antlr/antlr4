/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import org.antlr.v4.runtime.misc.Utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;

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

	public static void replaceInFile(Path sourcePath, String target, String replacement) throws IOException {
		replaceInFile(sourcePath, sourcePath, target, replacement);
	}

	public static void replaceInFile(Path sourcePath, Path destPath, String target, String replacement) throws IOException {
		String content = new String(Files.readAllBytes(sourcePath), StandardCharsets.UTF_8);
		String newContent = content.replace(target, replacement);
		try (PrintWriter out = new PrintWriter(destPath.toString())) {
			out.println(newContent);
		}
	}

	public static void mkdir(String dir) {
		File f = new File(dir);
		//noinspection ResultOfMethodCallIgnored
		f.mkdirs();
	}

	public static void deleteDirectory(File f) throws IOException {
		if (f.isDirectory() && !isLink(f.toPath())) {
			File[] files = f.listFiles();
			if (files != null) {
				for (File c : files)
					deleteDirectory(c);
			}
		}
		if (!f.delete())
			throw new IOException("Failed to delete file: " + f);
	}

	public static boolean isLink(Path path) throws IOException {
		try {
			BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
			return attrs.isSymbolicLink() || (attrs instanceof DosFileAttributes && attrs.isOther());
		} catch (IOException ignored) {
			return false;
		}
	}
}
