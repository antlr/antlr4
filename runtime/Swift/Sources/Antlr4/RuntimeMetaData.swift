/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/// 
/// This class provides access to the current version of the ANTLR 4 runtime
/// library as compile-time and runtime constants, along with methods for
/// checking for matching version numbers and notifying listeners in the case
/// where a version mismatch is detected.
/// 
/// 
/// The runtime version information is provided by _#VERSION_ and
/// _#getRuntimeVersion()_. Detailed information about these values is
/// provided in the documentation for each member.
/// 
/// 
/// The runtime version check is implemented by _#checkVersion_. Detailed
/// information about incorporating this call into user code, as well as its use
/// in generated code, is provided in the documentation for the method.
/// 
/// 
/// Version strings x.y and x.y.z are considered "compatible" and no error
/// would be generated. Likewise, version strings x.y-SNAPSHOT and x.y.z are
/// considered "compatible" because the major and minor components x.y
/// are the same in each.
/// 
/// 
/// To trap any error messages issued by this code, use System.setErr()
/// in your main() startup code.
/// 
/// 
/// - Since: 4.3
/// 

public class RuntimeMetaData {
    /// 
    /// A compile-time constant containing the current version of the ANTLR 4
    /// runtime library.
    /// 
    /// This compile-time constant value allows generated parsers and other
    /// libraries to include a literal reference to the version of the ANTLR 4
    /// runtime library the code was compiled against. At each release, we
    /// change this value.
    /// 
    /// Version numbers are assumed to have the form
    /// 
    /// __major__.__minor__.__patch__.__revision__-__suffix__,
    /// 
    /// with the individual components defined as follows.
    /// 
    /// * __major__ is a required non-negative integer, and is equal to
    /// `4` for ANTLR 4.
    /// * __minor__ is a required non-negative integer.
    /// * __patch__ is an optional non-negative integer. When
    /// patch is omitted, the `.` (dot) appearing before it is
    /// also omitted.
    /// * __revision__ is an optional non-negative integer, and may only
    /// be included when __patch__ is also included. When __revision__
    /// is omitted, the `.` (dot) appearing before it is also omitted.
    /// * __suffix__ is an optional string. When __suffix__ is
    /// omitted, the `-` (hyphen-minus) appearing before it is also
    /// omitted.
    /// 
    public static let VERSION: String = "4.7.2"

    /// 
    /// Gets the currently executing version of the ANTLR 4 runtime library.
    /// 
    /// 
    /// This method provides runtime access to the _#VERSION_ field, as
    /// opposed to directly referencing the field as a compile-time constant.
    /// 
    /// - Returns: The currently executing version of the ANTLR 4 library
    /// 

    public static func getRuntimeVersion() -> String {
        return RuntimeMetaData.VERSION
    }

    /// 
    /// This method provides the ability to detect mismatches between the version
    /// of ANTLR 4 used to generate a parser, the version of the ANTLR runtime a
    /// parser was compiled against, and the version of the ANTLR runtime which
    /// is currently executing.
    /// 
    /// The version check is designed to detect the following two specific
    /// scenarios.
    /// 
    /// * The ANTLR Tool version used for code generation does not match the
    /// currently executing runtime version.
    /// * The ANTLR Runtime version referenced at the time a parser was
    /// compiled does not match the currently executing runtime version.
    /// 
    /// Starting with ANTLR 4.3, the code generator emits a call to this method
    /// using two constants in each generated lexer and parser: a hard-coded
    /// constant indicating the version of the tool used to generate the parser
    /// and a reference to the compile-time constant _#VERSION_. At
    /// runtime, this method is called during the initialization of the generated
    /// parser to detect mismatched versions, and notify the registered listeners
    /// prior to creating instances of the parser.
    /// 
    /// This method does not perform any detection or filtering of semantic
    /// changes between tool and runtime versions. It simply checks for a
    /// version match and emits an error to stderr if a difference
    /// is detected.
    /// 
    /// Note that some breaking changes between releases could result in other
    /// types of runtime exceptions, such as a _LinkageError_, prior to
    /// calling this method. In these cases, the underlying version mismatch will
    /// not be reported here. This method is primarily intended to
    /// notify users of potential semantic changes between releases that do not
    /// result in binary compatibility problems which would be detected by the
    /// class loader. As with semantic changes, changes that break binary
    /// compatibility between releases are mentioned in the release notes
    /// accompanying the affected release.
    /// 
    /// __ Additional note for target developers:__ The version check
    /// implemented by this class is designed to address specific compatibility
    /// concerns that may arise during the execution of Java applications. Other
    /// targets should consider the implementation of this method in the context
    /// of that target's known execution environment, which may or may not
    /// resemble the design provided for the Java target.
    /// 
    /// - Parameter generatingToolVersion: The version of the tool used to generate a parser.
    /// This value may be null when called from user code that was not generated
    /// by, and does not reference, the ANTLR 4 Tool itself.
    /// - Parameter compileTimeVersion: The version of the runtime the parser was
    /// compiled against. This should always be passed using a direct reference
    /// to _#VERSION_.
    /// 
    public static func checkVersion(_ generatingToolVersion: String, _ compileTimeVersion: String) {
        let runtimeVersion: String = RuntimeMetaData.VERSION
        var runtimeConflictsWithGeneratingTool: Bool = false
        var runtimeConflictsWithCompileTimeTool: Bool = false

        //if ( generatingToolVersion != nil ) {
        runtimeConflictsWithGeneratingTool =
                !(runtimeVersion == (generatingToolVersion)) &&
                !(getMajorMinorVersion(runtimeVersion) == (getMajorMinorVersion(generatingToolVersion)))
        //}

        runtimeConflictsWithCompileTimeTool =
                !(runtimeVersion == (compileTimeVersion)) &&
                !(getMajorMinorVersion(runtimeVersion) == (getMajorMinorVersion(compileTimeVersion)))

        if runtimeConflictsWithGeneratingTool {
            print("ANTLR Tool version \(generatingToolVersion) used for code generation does not match the current runtime version \(runtimeVersion)")
        }
        if runtimeConflictsWithCompileTimeTool {
            print("ANTLR Runtime version \(compileTimeVersion)used for parser compilation does not match the current runtime version \(runtimeVersion)")
        }
    }

    /// 
    /// Gets the major and minor version numbers from a version string. For
    /// details about the syntax of the input `version`.
    /// E.g., from x.y.z return x.y.
    /// 
    /// - Parameter version: The complete version string.
    /// - Returns: A string of the form __major__.__minor__ containing
    /// only the major and minor components of the version string.
    /// 
    public static func getMajorMinorVersion(_ version: String) -> String {
        var result = version

        let dotBits = version.split(separator: ".", maxSplits: 2, omittingEmptySubsequences: false)
        if dotBits.count >= 2 {
            result = dotBits[0..<2].joined(separator: ".")
        }

        let dashBits = result.split(separator: "-", maxSplits: 1, omittingEmptySubsequences: false)
        return String(dashBits[0])
    }
}
