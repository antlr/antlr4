using BenchmarkDotNet.Attributes;
using BenchmarkDotNet.Running;

namespace Antlr4.Benchmarks;

public class Program
{
    public static void Main(string[] args)
    {
        BenchmarkSwitcher.FromAssembly(typeof(Program).Assembly).Run(args);
    }
}
