import Foundation


///
/// Using class so it can be shared even if
/// it appears to be a field in a class.
///
class Mutex {
    ///
    /// The mutex instance.
    ///
    private let semaphore = DispatchSemaphore(value: 1)

    ///
    /// Running the supplied closure synchronously.
    ///
    /// - Parameter closure: the closure to run
    /// - Returns: the value returned by the closure
    /// - Throws: the exception populated by the closure run
    ///
    @discardableResult
    func synchronized<R>(closure: () throws -> R) rethrows -> R {
        semaphore.wait()
        defer {
            semaphore.signal()
        }
        return try closure()
    }
}
