
// Template generated code from Antlr4BuildTasks.dotnet-antlr v 1.3

using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Tree;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

public class TreeOutput
{
    private static int changed = 0;
    private static bool first_time = true;

    public static StringBuilder OutputTree(IParseTree tree, Lexer lexer, Parser parser, CommonTokenStream stream)
    {
        changed = 0;
        first_time = true;
        var sb = new StringBuilder();
        ParenthesizedAST(tree, sb, lexer, parser, stream);
        return sb;
    }

    private static void ParenthesizedAST(IParseTree tree, StringBuilder sb, Lexer lexer, Parser parser, CommonTokenStream stream, int level = 0)
    {
        if (tree as TerminalNodeImpl != null)
        {
            TerminalNodeImpl tok = tree as TerminalNodeImpl;
            Interval interval = tok.SourceInterval;
            IList<IToken> inter = null;
            if (tok.Symbol.TokenIndex >= 0)
                inter = stream?.GetHiddenTokensToLeft(tok.Symbol.TokenIndex);
            if (inter != null)
                foreach (var t in inter)
                {
                    var ty = tok.Symbol.Type;
                    var name = lexer.Vocabulary.GetSymbolicName(ty);
                    StartLine(sb, level);
                    sb.AppendLine("(" + name + " text = " + PerformEscapes(t.Text) + " " + lexer.ChannelNames[t.Channel]);
                }
            {
                var ty = tok.Symbol.Type;
                var name = lexer.Vocabulary.GetSymbolicName(ty);
                StartLine(sb, level);
                sb.AppendLine("( " + name + " i =" + tree.SourceInterval.a
                    + " txt =" + PerformEscapes(tree.GetText())
                    + " tt =" + tok.Symbol.Type
                    + " " + lexer.ChannelNames[tok.Symbol.Channel]);
            }
        }
        else
        {
            var x = tree as RuleContext;
            var ri = x.RuleIndex;
            var name = parser.RuleNames[ri];
            StartLine(sb, level);
            sb.Append("( " + name);
            sb.AppendLine();
        }
        for (int i = 0; i<tree.ChildCount; ++i)
        {
            var c = tree.GetChild(i);
            ParenthesizedAST(c, sb, lexer, parser, stream, level + 1);
        }
        if (level == 0)
        {
            for (int k = 0; k < 1 + changed - level; ++k) sb.Append(") ");
            sb.AppendLine();
            changed = 0;
        }
    }

    private static void StartLine(StringBuilder sb, int level = 0)
    {
        if (changed - level >= 0)
        {
            if (!first_time)
            {
                for (int j = 0; j < level; ++j) sb.Append("  ");
                for (int k = 0; k < 1 + changed - level; ++k) sb.Append(") ");
                sb.AppendLine();
            }
            changed = 0;
            first_time = false;
        }
        changed = level;
        for (int j = 0; j < level; ++j) sb.Append("  ");
    }

    private static string ToLiteral(string input)
    {
        using (var writer = new StringWriter())
        {
            var literal = input;
            literal = literal.Replace("\\", "\\\\");
            return literal;
        }
    }

    public static string PerformEscapes(string s)
    {
        StringBuilder new_s = new StringBuilder();
        new_s.Append(ToLiteral(s));
        return new_s.ToString();
    }
}
