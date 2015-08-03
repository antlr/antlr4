grammartestmojo
===============

Maven Mojo for testing Antlr Grammars

Example usage
---------

```xml
<plugin>
  <groupId>com.khubla.antlr</groupId>
  <artifactId>grammartest-maven-plugin</artifactId>
  <configuration>
    <verbose>true</verbose>
    <entryPoint>equation</entryPoint>
    <exampleFiles>src/test/resources/examples/</exampleFiles>
    <grammarFiles>src/test/resources/grammars/</grammarFiles>
  </configuration>
</plugin>
```
