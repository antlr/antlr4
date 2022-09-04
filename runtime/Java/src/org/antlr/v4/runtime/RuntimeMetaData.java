/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime;

/**
 * This class provides access to the current version of the ANTLR 4 runtime
 * library as compile-time and runtime constants, along with methods for
 * checking for matching version numbers and notifying listeners in the case
 * where a version mismatch is detected.
 *
 * <p>
 * The runtime version information is provided by {@link #VERSION} and
 * {@link #getRuntimeVersion()}. Detailed information about these values is
 * provided in the documentation for each member.</p>
 *
 * <p>
 * The runtime version check is implemented by {@link #checkVersion}. Detailed
 * information about incorporating this call into user code, as well as its use
 * in generated code, is provided in the documentation for the method.</p>
 *
 * <p>
 * Version strings x.y and x.y.z are considered "compatible" and no error
 * would be generated. Likewise, version strings x.y-SNAPSHOT and x.y.z are
 * considered "compatible" because the major and minor components x.y
 * are the same in each.</p>
 *
 * <p>
 * To trap any error messages issued by this code, use System.setErr()
 * in your main() startup code.
 * </p>
 *
 * @since 4.3
 */
public class RuntimeMetaData {
	/**
	 * A compile-time constant containing the current version of the ANTLR 4
	 * runtime library.
	 *
	 * <p>
	 * This compile-time constant value allows generated parsers and other
	 * libraries to include a literal reference to the version of the ANTLR 4
	 * runtime library the code was compiled against. At each release, we
	 * change this value.</p>
	 *
	 * <p>Version numbers are assumed to have the form
	 *
	 * <em>major</em>.<em>minor</em>.<em>patch</em>.<em>revision</em>-<em>suffix</em>,
	 *
	 * with the individual components defined as follows.</p>
	 *
	 * <ul>
	 * <li><em>major</em> is a required non-negative integer, and is equal to
	 * {@code 4} for ANTLR 4.</li>
	 * <li><em>minor</em> is a required non-negative integer.</li>
	 * <li><em>patch</em> is an optional non-negative integer. When
	 * <em>patch</em> is omitted, the {@code .} (dot) appearing before it is
	 * also omitted.</li>
	 * <li><em>revision</em> is an optional non-negative integer, and may only
	 * be included when <em>patch</em> is also included. When <em>revision</em>
	 * is omitted, the {@code .} (dot) appearing before it is also omitted.</li>
	 * <li><em>suffix</em> is an optional string. When <em>suffix</em> is
	 * omitted, the {@code -} (hyphen-minus) appearing before it is also
	 * omitted.</li>
	 * </ul>
	 */
	public static final String VERSION = "4.11.1";

	/**
	 * Gets the currently executing version of the ANTLR 4 runtime library.
	 *
	 * <p>
	 * This method provides runtime access to the {@link #VERSION} field, as
	 * opposed to directly referencing the field as a compile-time constant.</p>
	 *
	 * @return The currently executing version of the ANTLR 4 library
	 */

	public static String getRuntimeVersion() {
		return VERSION;
	}

	/**
	 * This method provides the ability to detect mismatches between the version
	 * of ANTLR 4 used to generate a parser, the version of the ANTLR runtime a
	 * parser was compiled against, and the version of the ANTLR runtime which
	 * is currently executing.
	 *
	 * <p>
	 * The version check is designed to detect the following two specific
	 * scenarios.</p>
	 *
	 * <ul>
	 * <li>The ANTLR Tool version used for code generation does not match the
	 * currently executing runtime version.</li>
	 * <li>The ANTLR Runtime version referenced at the time a parser was
	 * compiled does not match the currently executing runtime version.</li>
	 * </ul>
	 *
	 * <p>
	 * Starting with ANTLR 4.3, the code generator emits a call to this method
	 * using two constants in each generated lexer and parser: a hard-coded
	 * constant indicating the version of the tool used to generate the parser
	 * and a reference to the compile-time constant {@link #VERSION}. At
	 * runtime, this method is called during the initialization of the generated
	 * parser to detect mismatched versions, and notify the registered listeners
	 * prior to creating instances of the parser.</p>
	 *
	 * <p>
	 * This method does not perform any detection or filtering of semantic
	 * changes between tool and runtime versions. It simply checks for a
	 * version match and emits an error to stderr if a difference
	 * is detected.</p>
	 *
	 * <p>
	 * Note that some breaking changes between releases could result in other
	 * types of runtime exceptions, such as a {@link LinkageError}, prior to
	 * calling this method. In these cases, the underlying version mismatch will
	 * not be reported here. This method is primarily intended to
	 * notify users of potential semantic changes between releases that do not
	 * result in binary compatibility problems which would be detected by the
	 * class loader. As with semantic changes, changes that break binary
	 * compatibility between releases are mentioned in the release notes
	 * accompanying the affected release.</p>
	 *
	 * <p>
	 * <strong>Additional note for target developers:</strong> The version check
	 * implemented by this class is designed to address specific compatibility
	 * concerns that may arise during the execution of Java applications. Other
	 * targets should consider the implementation of this method in the context
	 * of that target's known execution environment, which may or may not
	 * resemble the design provided for the Java target.</p>
	 *
	 * @param generatingToolVersion The version of the tool used to generate a parser.
	 * This value may be null when called from user code that was not generated
	 * by, and does not reference, the ANTLR 4 Tool itself.
	 * @param compileTimeVersion The version of the runtime the parser was
	 * compiled against. This should always be passed using a direct reference
	 * to {@link #VERSION}.
	 */
	public static void checkVersion(String generatingToolVersion, String compileTimeVersion) {
		String runtimeVersion = VERSION;
		boolean runtimeConflictsWithGeneratingTool = false;
		boolean runtimeConflictsWithCompileTimeTool = false;

		if ( generatingToolVersion!=null ) {
			runtimeConflictsWithGeneratingTool =
				!runtimeVersion.equals(generatingToolVersion) &&
				!getMajorMinorVersion(runtimeVersion).equals(getMajorMinorVersion(generatingToolVersion));
		}

		runtimeConflictsWithCompileTimeTool =
			!runtimeVersion.equals(compileTimeVersion) &&
			!getMajorMinorVersion(runtimeVersion).equals(getMajorMinorVersion(compileTimeVersion));

		if ( runtimeConflictsWithGeneratingTool ) {
			System.err.printf("ANTLR Tool version %s used for code generation does not match the current runtime version %s%n",
							  generatingToolVersion, runtimeVersion);
		}
		if ( runtimeConflictsWithCompileTimeTool ) {
			System.err.printf("ANTLR Runtime version %s used for parser compilation does not match the current runtime version %s%n",
							  compileTimeVersion, runtimeVersion);
		}
	}

	/**
	 * Gets the major and minor version numbers from a version string. For
	 * details about the syntax of the input {@code version}.
	 * E.g., from x.y.z return x.y.
	 *
	 * @param version The complete version string.
	 * @return A string of the form <em>major</em>.<em>minor</em> containing
	 * only the major and minor components of the version string.
	 */
	public static String getMajorMinorVersion(String version) {
		int firstDot = version.indexOf('.');
		int secondDot = firstDot >= 0 ? version.indexOf('.', firstDot + 1) : -1;
		int firstDash = version.indexOf('-');
		int referenceLength = version.length();
		if (secondDot >= 0) {
			referenceLength = Math.min(referenceLength, secondDot);
		}

		if (firstDash >= 0) {
			referenceLength = Math.min(referenceLength, firstDash);
		}

		return version.substring(0, referenceLength);
	}
}
