/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

/**
 * This class provides access to the current version of the ANTLR 4 runtime
 * library as compile-time and runtime constants, along with methods for
 * verifying a minimum level compatibility before executing code which depends
 * on the ANTLR 4 runtime library.
 *
 * <p>
 * The runtime version information is provided by {@link #VERSION} and
 * {@link #getRuntimeVersion()}. Detailed information about these values is
 * provided in the documentation for each member.</p>
 *
 * <p>
 * The runtime compatibility check is implemented by {@link #checkVersion}.
 * Detailed information about incorporating this call into user code, as well as
 * its use in generated code, is provided in the documentation for the
 * method.</p>
 */
public class RuntimeMetaData {
	/**
	 * This exception is thrown to indicate that the version of ANTLR used to
	 * generate and/or compile a parser is not compatible with the currently
	 * executing version of the runtime library.
	 *
	 * @see #checkVersion
	 */
	public static class ANTLRVersionMismatchException extends Exception {
		/**
		 * The version of the ANTLR 4 Tool a parser was generated with. This
		 * value may be {@code null} if the version check was called from
		 * user-defined code instead of a call automatically included in the
		 * generated parser.
		 */
		@Nullable
		public final String generatingToolVersion;
		/**
		 * The version of the ANTLR 4 Runtime library the parser and/or user
		 * code was compiled against.
		 */
		@NotNull
		public final String compileTimeRuntimeVersion;

		/**
		 * Constructs a new instance of the
		 * {@link ANTLRVersionMismatchException} class with the specified
		 * detailed information about a runtime library version compatibility
		 * error.
		 *
		 * @param message A description of the incompatibility between the Tool
		 * version, the compile-time runtime version, and/or the currently
		 * executing runtime version
		 * @param generatingToolVersion The version of the ANTLR 4 Tool a parser
		 * was generated with, or {@code null} if the version check was not part
		 * of the automatically-generated parser code
		 * @param compileTimeRuntimeVersion The version of the ANTLR 4 Runtime
		 * library the code was compiled against
		 */
		public ANTLRVersionMismatchException(String message, String generatingToolVersion, String compileTimeRuntimeVersion) {
			super(message);
			this.generatingToolVersion = generatingToolVersion;
			this.compileTimeRuntimeVersion = compileTimeRuntimeVersion;
		}

		@Override
		public String getMessage() {
			return super.getMessage() + ": Tool version " + generatingToolVersion + "; compile-time runtime version " + compileTimeRuntimeVersion;
		}
	}

	/**
	 * A compile-time constant containing the current version of the ANTLR 4
	 * runtime library.
	 *
	 * <p>This compile-time constant value allows generated parsers and other
	 * libraries to include a literal reference to the version of the ANTLR 4
	 * runtime library the code was compiled against.</p>
	 *
	 * <p>During development (between releases), this value contains the
	 * <em>expected</em> next release version. For official releases, the value
	 * will be the actual published version of the library.</p>
	 */
	public static final String VERSION = "4.2.3";

	/**
	 * Gets the currently executing version of the ANTLR 4 runtime library.
	 *
	 * <p>This method provides runtime access to the {@link #VERSION} field, as
	 * opposed to directly referencing the field as a compile-time constant.</p>
	 *
	 * @return The currently executing version of the ANTLR 4 library
	 */
	@NotNull
	public static String getRuntimeVersion() {
		return VERSION;
	}

	/**
	 * This method provides the ability to verify that the version of the ANTLR
	 * 4 used to execute a parser is compatible with the Tool used to generate
	 * the parser code and/or the version of the runtime the parser was compiled
	 * against.
	 *
	 * <p>
	 * Starting with ANTLR 4.2.3, the code generator emits two constants in each
	 * generated parser: a hard-coded constant indicating the version of the
	 * tool used to generate the parser and a reference to the compile-time
	 * constant {@link #VERSION}. At runtime, this method is called to ensure
	 * that, according to the version numbers, the semantics of the generated
	 * code are known to be compatible with the version of the ANTLR runtime
	 * used to execute code.</p>
	 *
	 * <p>
	 * The definition of "semantic changes" which are considered by this method
	 * depend on the value of {@code extendedSemantics}. When this value is
	 * {@code false}, as is the case for calls to this method in generated code,
	 * "semantic changes" means changes in the runtime that alter the behavior
	 * of parsers that use only standardized language features, which does not
	 * include the behavior of target-language-specific features such as
	 * embedded actions, custom semantic predicates, and runtime methods that
	 * are never called by the generated code but may be overridden in separate
	 * user code. When this value is {@code true}, "semantic changes" indicates
	 * a wider check across frequently used aspects of the runtime. The latter
	 * case may or may not consider all breaking changes across releases; for
	 * details on the specific methods in place see the release notes for the
	 * affected version(s).</p>
	 *
	 * <p>
	 * Note that if this method throws an exception when
	 * {@code extendedSemantics} is {@code false}, the lexer or parser which
	 * resulted in the exception will be prevented from executing, so this
	 * behavior is reserved for changes that are true breaking changes in the
	 * behavior. These changes, if any, are mentioned in the release notes for
	 * the affected release.</p>
	 *
	 * <p>
	 * Also note that some breaking changes between releases could result in
	 * other types of runtime exceptions, such as a {@link LinkageError}, prior
	 * to calling this method. This method is primarily intended to catch
	 * semantic changes that do not result in binary compatibility problems
	 * which would be detected by the class loader. As with semantic changes,
	 * changes which break binary compatibility between releases are mentioned
	 * in the release notes accompanying the affected release.</p>
	 *
	 * <p>
	 * <strong>Additional note for target developers:</strong> The compatibility
	 * check implemented by this class is designed to address specific
	 * compatibility issues that may arise during the execution of Java
	 * applications. Other targets should consider the implementation of this
	 * method in the context of that target's known execution environment, which
	 * may or may not resemble the design provided for the Java target.</p>
	 *
	 * @param toolVersion The version of the tool used to generate a parser.
	 * This value may be null when called from user code that was not generated
	 * by, and does not reference, the ANTLR 4 Tool itself.
	 * @param compileTimeVersion The version of the runtime the parser was
	 * compiled against. This should always be passed using a direct reference
	 * to {@link #VERSION}.
	 * @param extendedSemantics {@code false} to only check compatibility for
	 * the API used in generated code, not counting embedded actions and
	 * semantic predicates; otherwise, {@code true} to check compatibility
	 * across a wider range of frequently-used features that are commonly used
	 * in user-defined embedded actions and/or semantic predicates.
	 *
	 * @exception ANTLRVersionMismatchException if the version of the ANTLR
	 * runtime used to execute a compiled parser contains semantic changes which
	 * would alter the behavior of the generated code
	 */
	public static void checkVersion(@Nullable String toolVersion, @NotNull String compileTimeVersion, boolean extendedSemantics) throws ANTLRVersionMismatchException {
		/* Currently there are no versions of the ANTLR runtime library which
		 * are incompatible with respect to this method.
		 *
		 *  * Prior to ANTLR 4.2.3, the ANTLR tool did not emit calls to this
		 *    method in the generated code, and did not expose the VERSION
		 *    field. For the specific purposes of this compatibility check, the
		 *    "first public release" of ANTLR 4 can be treated as 4.2.3.
		 *  * If an incompatibility is introduced in a version after 4.2.3,
		 *    specific handling for the affected versions can be added.
		 */
	}
}
