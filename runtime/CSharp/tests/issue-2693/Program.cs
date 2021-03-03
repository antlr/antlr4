using Antlr4.Runtime;
using System;
using System.Linq;
using System.Text;

public class Program
{
    static void Main(string[] args)
    {
        bool show_tree = false;
        bool show_tokens = false;
        string file_name = null;
        string input = null;
        for (int i = 0; i < args.Length; ++i)
        {
            if (args[i].Equals("-tokens"))
            {
                show_tokens = true;
                continue;
            }
            else if (args[i].Equals("-tree"))
            {
                show_tree = true;
                continue;
            }
            else if (args[i].Equals("-input"))
                input = args[i];
            else if (args[i].Equals("-file"))
                file_name = args[++i];
        }
        ICharStream str = null;
        if (input == null && file_name == null)
        {
            StringBuilder sb = new StringBuilder();
            int ch;
            while ((ch = System.Console.Read()) != -1)
            {
                sb.Append((char)ch);
            }
            input = sb.ToString();
            str = CharStreams.fromString(input);
        }
        else if (input != null)
        {
            str = CharStreams.fromString(input);
        }
        else if (file_name != null)
        {
            str = CharStreams.fromPath(file_name);
        }
        var lexer = new asm8080Lexer(str);
        if (show_tokens)
        {
            StringBuilder new_s = new StringBuilder();
            for (int i = 0; ; ++i)
            {
                var ro_token = lexer.NextToken();
                var token = (CommonToken)ro_token;
                token.TokenIndex = i;
                new_s.AppendLine(token.ToString());
                if (token.Type == Antlr4.Runtime.TokenConstants.EOF)
                    break;
            }
            System.Console.Error.WriteLine(new_s.ToString());
        }
        lexer.Reset();
        var tokens = new CommonTokenStream(lexer);
        var parser = new asm8080Parser(tokens);
        var listener_lexer = new ErrorListener<int>();
        var listener_parser = new ErrorListener<IToken>();
        lexer.AddErrorListener(listener_lexer);
        parser.AddErrorListener(listener_parser);
        parser.Profile = true;
        var tree = parser.prog();
        if (listener_lexer.had_error || listener_parser.had_error)
        {
            System.Console.Error.WriteLine("parse failed.");
        }
        else
        {
            System.Console.Error.WriteLine("parse succeeded.");
        }
        if (show_tree)
        {
            System.Console.Error.WriteLine(tree.ToStringTree());
        }
        System.Console.Out.WriteLine(String.Join(", ", parser.ParseInfo.getDecisionInfo().Select(d => d.ToString())));
        System.Environment.Exit(listener_lexer.had_error || listener_parser.had_error ? 1 : 0);
    }
}
