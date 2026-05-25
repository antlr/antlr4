using BenchmarkDotNet.Attributes;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;

namespace Antlr4.Benchmarks;

/// <summary>
/// Measures OptimizedToken + OptimizedTokenFactory vs CommonToken.
/// Head-to-head on token creation, property access, and text extraction.
/// </summary>
[MemoryDiagnoser]
public class TokenBenchmarks
{
    private CharSpanInputStream _stream = null!;
    private string _input = null!;

    [GlobalSetup]
    public void Setup()
    {
        var rng = new Random(42);
        var chars = new char[10_000];
        for (int i = 0; i < chars.Length; i++)
            chars[i] = (char)rng.Next(32, 127);
        _input = new string(chars);
        _stream = new CharSpanInputStream(chars, chars.Length);
    }

    [Benchmark(Baseline = true)]
    public IToken[] CreateTokens_CommonTokenFactory()
    {
        var factory = CommonTokenFactory.Default;
        var stream = new AntlrInputStream(_input);
        var source = Tuple.Create<ITokenSource, ICharStream>(null!, stream);
        var tokens = new IToken[100];
        for (int i = 0; i < 100; i++)
        {
            tokens[i] = factory.Create(
                source: source,
                type: i % 20,
                text: null,
                channel: 0,
                start: i * 50,
                stop: i * 50 + 30,
                line: i,
                charPositionInLine: 0);
        }
        return tokens;
    }

    [Benchmark]
    public IToken[] CreateTokens_OptimizedTokenFactory()
    {
        var factory = OptimizedTokenFactory.Default;
        var stream = _stream;
        var source = Tuple.Create<ITokenSource, ICharStream>(null!, stream);
        var tokens = new IToken[100];
        for (int i = 0; i < 100; i++)
        {
            tokens[i] = factory.Create(
                source: source,
                type: i % 20,
                text: null,
                channel: 0,
                start: i * 50,
                stop: i * 50 + 30,
                line: i,
                charPositionInLine: 0);
        }
        return tokens;
    }

    [Benchmark]
    public string GetText_CommonToken()
    {
        var stream = new AntlrInputStream(_input);
        var source = Tuple.Create<ITokenSource, ICharStream>(null!, stream);
        var token = new CommonToken(source, 1, 0, 100, 200);
        string result = null!;
        for (int i = 0; i < 1000; i++)
            result = token.Text;
        return result;
    }

    [Benchmark]
    public string GetText_OptimizedToken()
    {
        var factory = OptimizedTokenFactory.Default;
        var stream = _stream;
        var source = Tuple.Create<ITokenSource, ICharStream>(null!, stream);
        var token = factory.Create(
            source: source,
            type: 1, text: null, channel: 0,
            start: 100, stop: 200, line: 1, charPositionInLine: 0);
        string result = null!;
        for (int i = 0; i < 1000; i++)
            result = token.Text;
        return result;
    }
}
