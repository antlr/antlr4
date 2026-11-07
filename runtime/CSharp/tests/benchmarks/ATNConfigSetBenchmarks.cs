using BenchmarkDotNet.Attributes;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;

namespace Antlr4.Benchmarks;

/// <summary>
/// Measures the impact of sealed/singleton comparators and HashCode.Combine
/// on ATNConfigSet operations (add, lookup, hash).
/// 
/// Since the comparators are internal to ATNConfigSet, we benchmark
/// the observable behavior: adding configs (which hashes/compares them)
/// and computing the set's hash code.
/// </summary>
[MemoryDiagnoser]
public class ATNConfigSetBenchmarks
{
    private ATNConfig[] _configs = null!;

    [Params(10, 50, 200)]
    public int ConfigCount;

    [GlobalSetup]
    public void Setup()
    {
        _configs = new ATNConfig[ConfigCount];
        for (int i = 0; i < ConfigCount; i++)
        {
            var state = new BasicState { stateNumber = i % 30 };
            _configs[i] = new ATNConfig(state, i % 8, null, SemanticContext.Empty.Instance);
        }
    }

    [Benchmark]
    public ATNConfigSet Add_Configs()
    {
        var set = new ATNConfigSet();
        for (int i = 0; i < _configs.Length; i++)
            set.Add(_configs[i]);
        return set;
    }

    [Benchmark]
    public int Add_Then_GetHashCode()
    {
        var set = new ATNConfigSet();
        for (int i = 0; i < _configs.Length; i++)
            set.Add(_configs[i]);
        return set.GetHashCode();
    }

    [Benchmark]
    public bool Add_Then_Equals()
    {
        var set1 = new ATNConfigSet();
        var set2 = new ATNConfigSet();
        for (int i = 0; i < _configs.Length; i++)
        {
            set1.Add(_configs[i]);
            set2.Add(_configs[i]);
        }
        return set1.Equals(set2);
    }
}
