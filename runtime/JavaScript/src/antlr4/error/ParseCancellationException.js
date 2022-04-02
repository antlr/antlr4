export default class ParseCancellationException extends Error {
    constructor() {
        super()
        Error.captureStackTrace(this, ParseCancellationException);
    }
}
