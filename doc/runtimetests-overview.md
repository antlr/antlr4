# Overview of the ANTLR Runtime Test Suite

An important part of ANTLR4 is its runtime test suite, which consist of 2 subparts:

* Tests for the tool itself
* Tests for the ANTLR runtime

Usually the tests are executed while compiling and installing an ANTLR4 jar from source code. The command for that is simply:

```bash
$ mvn install
```

to be executed in the root of the ANTLR4 repository. More details about this can be found in [Building ANTLR](building-antlr.md).

However, you don't need to run the installation again and again just to run the tests. Instead use

```bash
$ mvn test
```

to only trigger testing. You can find all runtime tests in the [runtime-testsuite/resources/org/antlr/v4/test/runtime](../runtime-testsuite/resources/org/antlr/v4/test/runtime) subfolder (tool tests under [tool-testsuite/test/org/antlr/v4/test/tool](../tool-testsuite/test/org/antlr/v4/test/tool)). The tool tests are just a bunch of Java test cases that test the tool's internal behavior (e.g. for code generation). We focus on the runtime tests here.

The underlying process of running the tests is quite a complicated setup to cater especially for a flexible test specification that can run with different target runtimes. Everything runs in Java, except for the actual target runtime tests. Runtime tests run first, followed by the tool tests. If there was a test failure in the first step, the tool tests are not executed, however. These are the steps involved when running the runtime tests:

* Generate Java JUnit test cases from the test templates, once for each target (C++, C#, Python, Java, Javascript atm.).
* These test cases generate grammar files when executed and run the target specific parser generation step, including compiling a binary, if necessary (e.g. for C++ and C#).
* Finally run the compiled test module using the input specified in the test template. The output (usually a token or parse tree dump) is then compared against the expected output, specified in the test template as well. This also includes any error messages written to the console.

## Generating JUnit Tests

The test specification part makes heavy use of the StringTemplate engine to allow defining target language agnostic tests. For that all tests are described in template (`stg`) files. You can find them in the [templates](../runtime-testsuite/resources/org/antlr/v4/test/runtime/templates) subfolder of the runtime tests folder. Read more about the folder structure in the [adding-tests.md](adding-tests.md) file. As lined out there you have to run

```bash
$ mvn -Pgen generate-test-sources
```
everytime you change any of the test templates or your target language specific template (which is used to translate certain text to your specific language). And a small hint: this command can be executed from the ANTLR source root as well. No need to dig into a subfolder.

## Running the Generated Tests

After generation you can run the tests as written above (`mvn install` or `mvn test`, both recompile ANTLR if necessary), which takes about 40 minutes for the full set (of which 30 mins are alone consumed by the C++ target tests). Which tests actually run is controlled by the [runtime tests pom.xml file](../runtime-testsuite/pom.xml). Look for the `maven-surefire-plugin` plugin entry and especially its includes. If you ever want to run tests only for a specific target, comment out all other `<include>` elements. For a specific test change the wildcard to that specific test name. This is especially helpful when debugging a test (e.g. when it fails) or when creating/changing tests. Additionally, some targets require to install additional dependencies you may not want to add to your box (e.g. mono, python 3.5) just to run e.g. the Java or C++ tests.
