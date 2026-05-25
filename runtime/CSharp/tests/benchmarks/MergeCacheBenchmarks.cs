using BenchmarkDotNet.Attributes;
using Antlr4.Runtime.Atn;

namespace Antlr4.Benchmarks;

/// <summary>
/// Benchmarks for MergeCache — validates that value-based equality on
/// PredictionContextPair enables effective caching of merge results.
///
/// The key scenario: structurally-equal but reference-different PredictionContext
/// objects must hit the cache. Identity-based equality defeats the cache entirely.
/// </summary>
[MemoryDiagnoser]
public class MergeCacheBenchmarks
{
    private SingletonPredictionContext[] _contextsA = null!;
    private SingletonPredictionContext[] _contextsB = null!;
    private SingletonPredictionContext[] _contextsA2 = null!;
    private SingletonPredictionContext[] _contextsB2 = null!;

    [Params(100, 500)]
    public int MergeCount;

    [GlobalSetup]
    public void Setup()
    {
        // Create pairs of structurally-equal but reference-different contexts.
        // This is the real-world pattern: ATN prediction creates fresh context objects
        // that are structurally identical to ones already in the cache.
        _contextsA = new SingletonPredictionContext[MergeCount];
        _contextsB = new SingletonPredictionContext[MergeCount];
        _contextsA2 = new SingletonPredictionContext[MergeCount];
        _contextsB2 = new SingletonPredictionContext[MergeCount];

        for (int i = 0; i < MergeCount; i++)
        {
            int parentState = i % 20;
            int returnState = i % 15;
            var parent = (SingletonPredictionContext)SingletonPredictionContext.Create(EmptyPredictionContext.Instance, parentState);

            // First set — these get Put into the cache
            _contextsA[i] = (SingletonPredictionContext)SingletonPredictionContext.Create(parent, returnState);
            _contextsB[i] = (SingletonPredictionContext)SingletonPredictionContext.Create(parent, (returnState + 7) % 15);

            // Second set — structurally equal but different references (simulates fresh allocations)
            var parent2 = (SingletonPredictionContext)SingletonPredictionContext.Create(EmptyPredictionContext.Instance, parentState);
            _contextsA2[i] = (SingletonPredictionContext)SingletonPredictionContext.Create(parent2, returnState);
            _contextsB2[i] = (SingletonPredictionContext)SingletonPredictionContext.Create(parent2, (returnState + 7) % 15);
        }
    }

    /// <summary>
    /// Measures merge performance with cache hits from structurally-equal contexts.
    /// With value-based equality: cache hits on second pass -> fast.
    /// With identity-based equality: cache always misses -> slow (full merge every time).
    /// </summary>
    [Benchmark]
    public PredictionContext MergeWithCacheHits()
    {
        var cache = new MergeCache();
        PredictionContext last = EmptyPredictionContext.Instance;

        // First pass: populate the cache
        for (int i = 0; i < MergeCount; i++)
        {
            last = PredictionContext.MergeSingletons(_contextsA[i], _contextsB[i], rootIsWildcard: true, cache);
        }

        // Second pass: use structurally-equal but reference-different contexts
        // This MUST hit the cache for acceptable performance
        for (int i = 0; i < MergeCount; i++)
        {
            last = PredictionContext.MergeSingletons(_contextsA2[i], _contextsB2[i], rootIsWildcard: true, cache);
        }

        return last;
    }

    /// <summary>
    /// Baseline: merge without any cache (null). Shows cost of uncached merges.
    /// </summary>
    [Benchmark(Baseline = true)]
    public PredictionContext MergeWithoutCache()
    {
        PredictionContext last = EmptyPredictionContext.Instance;

        for (int i = 0; i < MergeCount; i++)
        {
            last = PredictionContext.MergeSingletons(_contextsA[i], _contextsB[i], rootIsWildcard: true, mergeCache: null);
        }

        for (int i = 0; i < MergeCount; i++)
        {
            last = PredictionContext.MergeSingletons(_contextsA2[i], _contextsB2[i], rootIsWildcard: true, mergeCache: null);
        }

        return last;
    }
}
