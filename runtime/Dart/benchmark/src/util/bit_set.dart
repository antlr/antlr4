import 'package:antlr4/src/util/bit_set.dart';
import 'package:benchmark_harness/benchmark_harness.dart';

class TemplateBenchmark extends BenchmarkBase {
  BitSet bs = BitSet();

  TemplateBenchmark() : super('BitSet common operations');

  static void main() {
    TemplateBenchmark().report();
  }

  // The benchmark code.
  @override
  void run() {
    bs.set(32);
    bs.set(59);
    bs.set(256);
    bs.get(128);
    bs.get(256);
    bs.nextset(0);
    bs.nextset(60);
    bs.cardinality;
    bs.hashCode;
  }

  // Not measured setup code executed prior to the benchmark runs.
  @override
  void setup() { }

  // Not measured teardown code executed after the benchmark runs.
  @override
  void teardown() { }
}

void main() {
  // Run TemplateBenchmark
  TemplateBenchmark.main();
}
