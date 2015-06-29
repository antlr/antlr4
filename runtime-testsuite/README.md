# Runtime Test Suite

If you are tweaking the runtime test suite generator you can regenerate them using the following command:

```
mvn -Pgen generate-test-sources
```

This will generate the runtime test harness classes into the `test` directory where they can be checked in.
