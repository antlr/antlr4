using BenchmarkDotNet.Attributes;
using Antlr4.Runtime.Misc;

namespace Antlr4.Benchmarks;

/// <summary>
/// Measures ArrayList.Equals (index-based vs old enumerator approach).
/// Since we can't easily benchmark "before", this validates that the
/// current implementation scales linearly with low overhead.
/// </summary>
[MemoryDiagnoser]
public class ArrayListBenchmarks
{
    private ArrayList<int> _list1 = null!;
    private ArrayList<int> _list2 = null!;
    private ArrayList<int> _listDifferent = null!;

    [Params(10, 100, 1000)]
    public int Size;

    [GlobalSetup]
    public void Setup()
    {
        _list1 = new ArrayList<int>(Size);
        _list2 = new ArrayList<int>(Size);
        _listDifferent = new ArrayList<int>(Size);
        for (int i = 0; i < Size; i++)
        {
            _list1.Add(i);
            _list2.Add(i);
            _listDifferent.Add(i * 2);
        }
    }

    [Benchmark]
    public bool Equals_Same()
    {
        return _list1.Equals(_list2);
    }

    [Benchmark]
    public bool Equals_Different()
    {
        return _list1.Equals(_listDifferent);
    }
}
