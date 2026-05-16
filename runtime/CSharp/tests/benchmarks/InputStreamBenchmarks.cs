using BenchmarkDotNet.Attributes;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;

namespace Antlr4.Benchmarks;

/// <summary>
/// Head-to-head: AntlrInputStream vs SpanInputStream.
/// Tests construction, sequential LA/Consume, Seek, and GetText.
/// </summary>
[MemoryDiagnoser]
public class InputStreamBenchmarks
{
    private string _input = null!;
    private char[] _inputChars = null!;

    [Params(1_000, 100_000)]
    public int InputLength;

    [GlobalSetup]
    public void Setup()
    {
        var rng = new Random(42);
        _inputChars = new char[InputLength];
        for (int i = 0; i < InputLength; i++)
            _inputChars[i] = (char)rng.Next(32, 127);
        _input = new string(_inputChars);
    }

    // ── Construction ──

    [Benchmark(Baseline = true)]
    public AntlrInputStream Construct_AntlrInputStream()
    {
        return new AntlrInputStream(_input);
    }

    [Benchmark]
    public SpanInputStream Construct_SpanInputStream()
    {
        return new SpanInputStream(_input);
    }

    [Benchmark]
    public SpanInputStream Construct_SpanInputStream_CharArray()
    {
        return new SpanInputStream(_inputChars, _inputChars.Length);
    }

    // ── Sequential Consume + LA(1) — the lexer hot loop ──

    [Benchmark]
    public int ConsumeAll_AntlrInputStream()
    {
        var stream = new AntlrInputStream(_input);
        int sum = 0;
        while (stream.LA(1) != IntStreamConstants.EOF)
        {
            sum += stream.LA(1);
            stream.Consume();
        }
        return sum;
    }

    [Benchmark]
    public int ConsumeAll_SpanInputStream()
    {
        var stream = new SpanInputStream(_input);
        int sum = 0;
        while (stream.LA(1) != IntStreamConstants.EOF)
        {
            sum += stream.LA(1);
            stream.Consume();
        }
        return sum;
    }

    // ── Seek (AntlrInputStream is O(n) forward, SpanInputStream is O(1)) ──

    [Benchmark]
    public int SeekForward_AntlrInputStream()
    {
        var stream = new AntlrInputStream(_input);
        int sum = 0;
        int step = stream.Size / 20;
        for (int i = 0; i < 20; i++)
        {
            stream.Seek(i * step);
            sum += stream.LA(1);
        }
        return sum;
    }

    [Benchmark]
    public int SeekForward_SpanInputStream()
    {
        var stream = new SpanInputStream(_input);
        int sum = 0;
        int step = stream.Size / 20;
        for (int i = 0; i < 20; i++)
        {
            stream.Seek(i * step);
            sum += stream.LA(1);
        }
        return sum;
    }

    // ── GetText (substring extraction) ──

    [Benchmark]
    public string GetText_AntlrInputStream()
    {
        var stream = new AntlrInputStream(_input);
        return stream.GetText(new Interval(100, stream.Size / 2));
    }

    [Benchmark]
    public string GetText_SpanInputStream()
    {
        var stream = new SpanInputStream(_input);
        return stream.GetText(new Interval(100, stream.Size / 2));
    }

    // ── Negative lookahead (LA(-k)) ──

    [Benchmark]
    public int LookBack_AntlrInputStream()
    {
        var stream = new AntlrInputStream(_input);
        stream.Seek(stream.Size / 2);
        int sum = 0;
        for (int i = 1; i <= 100; i++)
            sum += stream.LA(-i);
        return sum;
    }

    [Benchmark]
    public int LookBack_SpanInputStream()
    {
        var stream = new SpanInputStream(_input);
        stream.Seek(stream.Size / 2);
        int sum = 0;
        for (int i = 1; i <= 100; i++)
            sum += stream.LA(-i);
        return sum;
    }
}
