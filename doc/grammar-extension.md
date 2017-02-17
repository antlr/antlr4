# Extending Grammars 

Place holder for documentation of grammar extension mechanisms.

Ok you have a working grammar.
You want it to do more, but don't want to break the current language ...
Hey that sounds like delegation (embedding, inheritance) ... 


```
parser grammar WorkingGrammar;
 	
/* alts not named */
rulePlain 
 : otherRule0 
 | otherRule1 
 ...
 | otherRuleN 
;

ruleFancy 
  : anotherRule1 #NamedAlt0
  | anotherRule1 #NamedAlt1
  ...
  | anotherRuleN #NamedAltN
;
```

```
grammar BetterGrammar;
{ 
  /* currently only implement for the Go target */
  language = Go; 
  /* needed to glue the imported grammar together with existing listeners */
  /* <grammarName>:<package>:<import> */ 
  importParams = "WorkingGrammar:wg:github.com/example/wg"; 
}
import WorkingGrammar;
    

/* rulePlan's alts are unnamed therefore this rule's alts need to be unamed */     
rulePlanX 
options{ extends = rulePlan; }
  : newFeature
;

ruleFancyX 
options{ extends = ruleFancy; }
  : newFeature2 #NameAltNew /* can't share a name with extended rule - would cause type system issues */
;

```

Todo
Show generated base listeners.
etc.