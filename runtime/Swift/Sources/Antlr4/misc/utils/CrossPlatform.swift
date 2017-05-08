import Foundation

// MARK: - String

/// Cross-platform String formatting method.
/// We make this method because on Linux String is not conforming to
/// CVarArg, thus cannot be mapped to "%@" in format string.
/// This method implements a work-around that maps "%@" to "%s" in
/// our format string and then convert NSString to CString.
///
/// - Parameters:
///   - format: printf-like format string
///   - args: argument strings
/// - Returns: formatted string
func makeString(fromFormat format: String, _ args: String...) -> String {
    #if os(Linux)
        let linuxFormat = format.replacingOccurrences(of: "%@", with: "%s")
        let cStrings = args.map { $0.withCString { $0 } }
        return String(format: linuxFormat, arguments: cStrings)
    #else
        return String(format: format, arguments: args)
    #endif
}


// MARK: - Multithread

fileprivate var _GLOBAL_MUTEX = pthread_mutex_t()

/// Cross-platform synchronized execution of a closure.
/// Using naive locking that uses a global mutex lock.
///
/// - Parameter closure: closure needs to be executed.
func synchronized<R>(closure: () -> R) {
    pthread_mutex_lock(&_GLOBAL_MUTEX)
    defer {
        pthread_mutex_unlock(&_GLOBAL_MUTEX)
    }
    _ = closure()
}

