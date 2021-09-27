import 'dart:io';

/// Creates a new independent [Stream] for the contents of the file on [path].
Stream<List<int>> readStream(String path) => File(path).openRead();

/// Prints a string representation of the object.
void stdoutWrite(Object? object) => stdout.write(object);

/// Prints a string representation of the [object] to the error stream.
///
/// Each call appends an additional newline to the object's string
/// representation.
void stderrWriteln(Object? object) => stderr.writeln(object);
