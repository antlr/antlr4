# Translation

## ASTs vs parse trees

I used to do specialized AST (**abstract** syntax tree) nodes rather than (concrete) parse trees because I used to think more about compilation and generating bytecode/assembly code. When I started thinking more about translation, I started using parse trees. For v4, I realized that I did mostly translation.  I guess what I'm saying is that maybe parse trees are not as good as ASTs for generating bytecodes. Personally, I would rather see `(+ 3 4)` rather than `(expr 3 + 4)` for generating byte codes, but it's not the end of the world. (*Can someone fill this in?*)

## Decoupling input walking from output generation

I suggest creating an intermediate model that represents your output. You walk the parse tree to collect information and create your model. Then, you could almost certainly automatically walk this internal model to generate output based upon stringtemplates that match the class names of the internal model. In other words, define a special `IFStatement` object that has all of the fields you want and then create them as you walk the parse tree. This decoupling of the input from the output is very powerful. Just because we have a parse tree listener doesn't mean that the parse tree itself is necessarily the best data structure to hold all information necessary to generate code. Imagine a situation where the output is the exact reverse of the input. In that case, you really want to walk the input just to collect data. Generating output should be driven by the internal model not the way it was represented in the input.