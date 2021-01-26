using Antlr4.Runtime;
using System.IO;

public abstract class ParserBase : Parser
	{
        public ParserBase(ITokenStream input, TextWriter output, TextWriter errorOutput) : this(input)
        {
        }

        protected ParserBase(ITokenStream input)
   		: base(input)
		{
		}
	}
