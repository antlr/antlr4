import Foundation


/// 
/// Using class so it can be shared even if
/// it appears to be a field in a class.
/// 
class Mutex {
    
    /// 
    /// The mutex instance.
    /// 
    private var mutex = pthread_mutex_t()
    
    /// 
    /// Initialization
    /// 
    init() {
        pthread_mutex_init(&mutex, nil)
    }
    
    /// 
    /// Running the supplied closure synchronously.
    /// 
    /// - Parameter closure: the closure to run
    /// - Returns: the value returned by the closure
    /// - Throws: the exception populated by the closure run
    /// 
    @discardableResult
    func synchronized<R>(closure: () throws -> R) rethrows -> R {
        pthread_mutex_lock(&mutex)
        defer {
            pthread_mutex_unlock(&mutex)
        }
        return try closure()
    }

    deinit {
        // free the mutex resource
        pthread_mutex_destroy(&mutex)
    }
}
