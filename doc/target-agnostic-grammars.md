# Target agnostic grammars

If your grammar is targeted to Python only, you may ignore the following. But if your goal is to get your Java parser to also run in Python, then you might find it useful.

1. Do not embed production code inside your grammar. This is not portable and will not be. Move all your code to listeners or visitors.
1. The only production code absolutely required to sit with the grammar should be semantic predicates, like:
```
ID {$text.equals("test")}?
```

Unfortunately, this is not portable, as Java and Python (and other target languages) have different syntaxes for all but the simplest language elements.  But you can work around it. The trick involves:

* deriving your parser from a parser you provide, such as BaseParser
* implementing utility methods, such as "isEqualText", in this BaseParser, in different files for each target language
* invoking your utility methods in the semantic predicate from the `$parser` object

Thanks to the above, you should be able to rewrite the above semantic predicate as follows:

File `MyGrammarParser.g4`:
```
options { superClass = MyGrammarBaseParser; }
...
ID {$parser.isEqualText($text,"test")}?
```

File `MyGrammarBaseParser.py`:
```python
from antlr4 import *

class MyGrammarBaseParser(Parser):

   def isEqualText(a, b):
      return a is b
```

File `MyGrammarBaseParser.java`:
```java
import org.antlr.v4.runtime.*;

public abstract class MyGrammarBaseParser extends Parser {

   public static boolean isEqualText(a, b) {
      return a.equals(b);
   }
}
```
