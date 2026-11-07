using BenchmarkDotNet.Attributes;
using Antlr4.Runtime.Misc;

namespace Antlr4.Benchmarks;

/// <summary>
/// Measures IntervalSet with List backing (IList→List optimization).
/// The Contains and iteration hot paths benefit from struct enumerator
/// and bounds elimination.
/// </summary>
[MemoryDiagnoser]
public class IntervalSetBenchmarks
{
    private IntervalSet _small = null!;
    private IntervalSet _large = null!;

    [GlobalSetup]
    public void Setup()
    {
        // Small: typical token type set (~20 intervals)
        _small = new IntervalSet();
        for (int i = 0; i < 40; i += 2)
            _small.Add(i * 3, i * 3 + 2);

        // Large: Unicode range set (~200 intervals)
        _large = new IntervalSet();
        for (int i = 0; i < 400; i += 2)
            _large.Add(i * 100, i * 100 + 50);
    }

    [Benchmark]
    public int Contains_Small()
    {
        int hits = 0;
        for (int i = 0; i < 200; i++)
            if (_small.Contains(i)) hits++;
        return hits;
    }

    [Benchmark]
    public int Contains_Large()
    {
        int hits = 0;
        for (int i = 0; i < 40000; i += 100)
            if (_large.Contains(i)) hits++;
        return hits;
    }

    [Benchmark]
    public IntervalSet Or_TwoSets()
    {
        return IntervalSet.Or(new[] { _small, _large });
    }

    [Benchmark]
    public IntervalSet Complement()
    {
        var vocab = new IntervalSet();
        vocab.Add(0, 50000);
        return _large.Complement(vocab);
    }
}
