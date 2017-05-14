import Foundation

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

