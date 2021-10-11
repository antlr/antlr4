/// Creates a new independent [Stream] for the contents of the file on [path].
Stream<List<int>> readStream(String path) =>
    throw UnsupportedError('Cannot read a file stream without dart:io');

/// Prints a string representation of the object.
void stdoutWrite(Object? object) =>
    throw UnsupportedError('Cannot do stdout#write without dart:io');

/// Prints a string representation of the [object] to the error stream.
///
/// Each call appends an additional newline to the object's string
/// representation.
void stderrWriteln(Object? object) => throw UnsupportedError(
    'Cannot do writeln to the error stream without dart:io or dart:html');
