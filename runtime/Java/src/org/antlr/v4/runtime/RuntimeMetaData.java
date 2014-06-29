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

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

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
 * By default, the {@link DefaultListener#INSTANCE} listener is automatically
 * registered. As long as the default listener is registered, it will always be
 * the last listener notified in the event of a version mismatch. This behavior
 * ensures that custom listeners registered by a user will be notified even in
 * the event the default listener throws an exception. This default listener may
 * be removed by calling {@link #removeListener} for
 * {@link DefaultListener#INSTANCE} or {@link #clearListeners}. If required, it
 * may be re-registered by calling {@link #addListener}.</p>
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
	 * runtime library the code was compiled against.</p>
	 */
	public static final String VERSION = "4.3.1-SNAPSHOT";

	/**
	 * This class provides detailed information about a mismatch between the
	 * version of the tool a parser was generated with, the version of the
	 * runtime a parser was compiled against, and/or the currently executing
	 * version of the runtime.
	 *
	 * @see #checkVersion
	 */
	public static class VersionMismatchException extends RuntimeException {
		/**
		 * The version of the ANTLR 4 Tool a parser was generated with. This
		 * value may be {@code null} if {@link #checkVersion} was called from
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
		 * Constructs a new instance of the {@link VersionMismatchException}
		 * class with the specified detailed information about a mismatch
		 * between ANTLR tool and runtime versions used by a parser.
		 *
		 * @param message the detail message. The detail message is saved for
		 * later retrieval by the {@link #getMessage()} method.
		 * @param generatingToolVersion The version of the ANTLR 4 Tool a parser
		 * was generated with, or {@code null} if the version check was not part
		 * of the automatically-generated parser code
		 * @param compileTimeRuntimeVersion The version of the ANTLR 4 Runtime
		 * library the code was compiled against
		 */
		VersionMismatchException(@NotNull String message, @Nullable String generatingToolVersion, @NotNull String compileTimeRuntimeVersion) {
			super(message);
			this.generatingToolVersion = generatingToolVersion;
			this.compileTimeRuntimeVersion = compileTimeRuntimeVersion;
		}
	}

	/**
	 * This interface defines a listener which handles notifications about
	 * mismatched ANTLR Tool and/or Runtime versions.
	 */
	public interface Listener {
		/**
		 * Report a version mismatch which was detected by
		 * {@link #checkVersion}.
		 *
		 * <p>
		 * Implementations of this method may, but are not required to, throw
		 * the provided exception. Note that if a registered listener throws the
		 * provided exception during the handling of this event, the following
		 * will be impacted:</p>
		 *
		 * <ul>
		 * <li>The lexer or parser which called {@link #checkVersion} will be
		 * unusable due to throwing an exception in a static initializer
		 * block.</li>
		 * <li>No additional registered listeners will be notified about the
		 * version mismatch. Since the default {@link DefaultListener} instance
		 * is always the last listener called (unless it is unregistered), it
		 * will not affect the execution of any other registered listeners, even
		 * in the case where it throws an exception.</li>
		 * </ul>
		 *
		 * @param ex a {@link VersionMismatchException} instance containing
		 * detailed information about the specific version mismatch detected
		 */
		void reportVersionMismatch(@NotNull VersionMismatchException ex)
			throws VersionMismatchException;
	}

	/**
	 * This class provides a default implementation of {@link Listener} which
	 * responds to mismatched versions by throwing the provided
	 * {@link VersionMismatchException} if the reported version mismatch
	 * indicates the versions differ by more than the <em>major</em> and
	 * <em>minor</em> version components.
	 *
	 * <p>
	 * For example, version strings x.y and x.y.z are considered "compatible",
	 * and this listener will not throw an exception. Likewise, version strings
	 * x.y-SNAPSHOT and x.y.z are considered "compatible" because the major and
	 * minor components x.y are the same in each.</p>
	 *
	 * <p>
	 * For the purposes of this listener, version numbers are assumed to have
	 * the form
	 * <em>major</em>.<em>minor</em>.<em>patch</em>.<em>revision</em>-<em>suffix</em>,
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
	public static class DefaultListener implements Listener {
		/**
		 * A default instance of {@link DefaultListener} which is automatically
		 * registered to receive version mismatch events.
		 */
		public static final DefaultListener INSTANCE = new DefaultListener();

		/**
		 * {@inheritDoc}
		 *
		 * <p>
		 * The default implementation only throws an exception when the reported
		 * version mismatch contains a mismatched <em>major</em> or
		 * <em>minor</em> version component. For details about the syntax of the
		 * input {@code version}, see the documentation for
		 * {@link DefaultListener}.</p>
		 */
		@Override
		public void reportVersionMismatch(@NotNull VersionMismatchException ex) throws VersionMismatchException {
			if (!isMinorVersionMatch(ex)) {
				throw ex;
			}
		}

		/**
		 * Determines if the reported version mismatch are a match when
		 * considering only the <em>major</em> and <em>minor</em> version
		 * components of the version strings.
		 *
		 * @param ex a {@link VersionMismatchException} instance containing
		 * detailed information about the specific version mismatch detected
		 * @return {@code true} if the <em>major</em> and <em>minor</em> version
		 * components of the version strings match; otherwise, {@code false}.
		 */
		protected boolean isMinorVersionMatch(@NotNull VersionMismatchException ex) {
			String generatingToolVersion = ex.generatingToolVersion;
			if (generatingToolVersion != null) {
				if (!getMajorMinorVersion(VERSION).equals(getMajorMinorVersion(generatingToolVersion))) {
					return false;
				}
			}

			return getMajorMinorVersion(VERSION).equals(getMajorMinorVersion(ex.compileTimeRuntimeVersion));
		}
	}

	/**
	 * The list of listeners registered to receive notifications of mismatched
	 * ANTLR versions.
	 */
	private static final Collection<Listener> listeners = new CopyOnWriteArraySet<Listener>();
	static {
		listeners.add(DefaultListener.INSTANCE);
	}

	/**
	 * Register a listener to receive notifications of mismatched ANTLR
	 * versions. This method ensures that as long as
	 * {@link DefaultListener#INSTANCE} is registered as a listener, it will
	 * always be the last listener notified of mismatched versions.
	 *
	 * @param listener the listener to notify if mismatched ANTLR versions are
	 * detected
	 *
	 * @see #checkVersion
	 */
	public static synchronized void addListener(@NotNull Listener listener) {
		boolean containedDefault = listeners.remove(DefaultListener.INSTANCE);
		listeners.add(listener);
		if (containedDefault) {
			listeners.add(DefaultListener.INSTANCE);
		}
	}

	/**
	 * Remove a specific listener registered to receive notifications of
	 * mismatched ANTLR versions.
	 *
	 * @param listener the listener to remove
	 * @return {@code true} if the listener was removed; otherwise,
	 * {@code false} if the specified listener was not found in the list of
	 * registered listeners
	 */
	public static synchronized boolean removeListener(@NotNull Listener listener) {
		return listeners.remove(listener);
	}

	/**
	 * Remove all listeners registered to receive notifications of mismatched
	 * ANTLR versions.
	 */
	public static synchronized void clearListeners() {
		listeners.clear();
	}

	/**
	 * Gets the currently executing version of the ANTLR 4 runtime library.
	 *
	 * <p>
	 * This method provides runtime access to the {@link #VERSION} field, as
	 * opposed to directly referencing the field as a compile-time constant.</p>
	 *
	 * @return The currently executing version of the ANTLR 4 library
	 */
	@NotNull
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
	 * changes between tool and runtime versions. It simply checks for a simple
	 * version match and notifies the registered listeners any time a difference
	 * is detected. A default instance of {@link DefaultListener} is notified
	 * unless it is explicitly removed.</p>
	 *
	 * <p>
	 * Note that some breaking changes between releases could result in other
	 * types of runtime exceptions, such as a {@link LinkageError}, prior to
	 * calling this method. In these cases, the underlying version mismatch will
	 * not be reported to the listeners. This method is primarily intended to
	 * notify users of potential semantic changes between releases that do not
	 * result in binary compatibility problems which would be detected by the
	 * class loader. As with semantic changes, changes which break binary
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
	 * @param toolVersion The version of the tool used to generate a parser.
	 * This value may be null when called from user code that was not generated
	 * by, and does not reference, the ANTLR 4 Tool itself.
	 * @param compileTimeVersion The version of the runtime the parser was
	 * compiled against. This should always be passed using a direct reference
	 * to {@link #VERSION}.
	 */
	public static void checkVersion(@Nullable String toolVersion, @NotNull String compileTimeVersion) {
		boolean report = false;
		String message = null;
		if (toolVersion != null && !VERSION.equals(toolVersion)) {
			report = true;
			message = String.format("ANTLR Tool version %s used for code generation does not match the current runtime version %s", toolVersion, VERSION);
		}
		else if (!VERSION.equals(compileTimeVersion)) {
			report = true;
			message = String.format("ANTLR Runtime version %s used for parser compilation does not match the current runtime version %s", compileTimeVersion, VERSION);
		}

		if (report) {
			VersionMismatchException ex = new VersionMismatchException(message, toolVersion, compileTimeVersion);
			for (Listener listener : listeners) {
				listener.reportVersionMismatch(ex);
			}
		}
	}

	/**
	 * Gets the major and minor version numbers from a version string. For
	 * details about the syntax of the input {@code version}, see the
	 * documentation for {@link org.antlr.v4.runtime.RuntimeMetaData.DefaultListener}.
	 * E.g., from x.y.z return x.y.
	 *
	 * @param version The complete version string.
	 * @return A string of the form <em>major</em>.<em>minor</em> containing
	 * only the major and minor components of the version string.
	 */
	@NotNull
	public static String getMajorMinorVersion(@NotNull String version) {
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
