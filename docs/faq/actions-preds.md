# Actions and semantic predicates

## How do I test if an optional rule was matched?

For optional rule references such as the initialization clause in the following

```
decl : 'var' ID (EQUALS expr)? ;
```

testing to see if that clause was matched can be done using `$EQUALS!=null` or `$expr.ctx!=null` where `$expr.ctx` points to the context or parse tree created for that reference to rule expr.