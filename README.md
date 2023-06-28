# ANTLR v4

[![Java 11+](https://img.shields.io/badge/java-11+-4c7e9f.svg)](http://java.oracle.com)
[![License](https://img.shields.io/badge/license-BSD-blue.svg)](https://raw.githubusercontent.com/antlr/antlr4/master/LICENSE.txt)

**ANTLR** (ANother Tool for Language Recognition) is a powerful parser generator for reading, processing, executing, or translating structured text or binary files. It's widely used to build languages, tools, and frameworks. From a grammar, ANTLR generates a parser that can build parse trees and also generates a listener interface (or visitor) that makes it easy to respond to the recognition of phrases of interest.

**Dev branch build status**

[![MacOSX, Windows, Linux](https://github.com/antlr/antlr4/actions/workflows/hosted.yml/badge.svg)](https://github.com/antlr/antlr4/actions/workflows/hosted.yml) (github actions)

<!--
* [![Windows](https://github.com/antlr/antlr4/actions/workflows/windows.yml/badge.svg?branch=dev)](https://github.com/antlr/antlr4/actions/workflows/windows.yml) (github actions)

* [![Circle CI Build Status (Linux)](https://img.shields.io/circleci/build/gh/antlr/antlr4/master?label=Linux)](https://app.circleci.com/pipelines/github/antlr/antlr4) (CircleCI)

[![AppVeyor CI Build Status (Windows)](https://img.shields.io/appveyor/build/parrt/antlr4?label=Windows)](https://ci.appveyor.com/project/parrt/antlr4) 
[![Travis-CI Build Status (Swift-Linux)](https://img.shields.io/travis/antlr/antlr4.svg?label=Linux-Swift&branch=master)](https://travis-ci.com/github/antlr/antlr4)
-->


## Versioning

ANTLR 4 supports 10 target languages
(Cpp, CSharp, Dart, Java, JavaScript, PHP, Python3, Swift, TypeScript),
and ensuring consistency across these targets is a unique and highly valuable feature.
To ensure proper support of this feature, each release of ANTLR is a complete release of the tool and the 10 runtimes, all with the same version.
As such, ANTLR versioning does not strictly follow semver semantics:

* a component may be released with the latest version number even though nothing has changed within that component since the previous release
* major version is bumped only when ANTLR is rewritten for a totally new "generation", such as ANTLR3 -> ANTLR4 (LL(\*) -> ALL(\*) parsing)
* minor version updates may include minor breaking changes, the policy is to regenerate parsers with every release (4.11 -> 4.12)
* backwards compatibility is only guaranteed for patch version bumps (4.11.1 -> 4.11.2)

If you use a semver verifier in your CI, you probably want to apply special rules for ANTLR, such as treating minor change as a major change.

## Repo branch structure

The default branch for this repo is [`master`](https://github.com/antlr/antlr4/tree/master), which is the latest stable release and has tags for the various releases; e.g., see release tag [4.9.3](https://github.com/antlr/antlr4/tree/4.9.3).  Branch [`dev`](https://github.com/antlr/antlr4/tree/dev) is where development occurs between releases and all pull requests should be derived from that branch. The `dev` branch is merged back into `master` to cut a release and the release state is tagged (e.g., with `4.10-rc1` or `4.10`.) Visually our process looks roughly like this:

<img src="doc/images/new-antlr-branches.png" width="500">

The Go target now has its own dedicated repo:

```bash
$ go get github.com/antlr4-go/antlr
```
**Note**
The dedicated Go repo is for `go get` and `import` only. Go runtime development is still performed in the main `antlr/antlr4` repo. 

## Authors and major contributors

* [Terence Parr](http://www.cs.usfca.edu/~parrt/), parrt@cs.usfca.edu
ANTLR project lead and supreme dictator for life
[University of San Francisco](http://www.usfca.edu/)
* [Sam Harwell](http://tunnelvisionlabs.com/) (Tool co-author, Java and original C# target)
* [Eric Vergnaud](https://github.com/ericvergnaud) (Javascript, Python2, Python3 targets and maintenance of C# target)
* [Peter Boyer](https://github.com/pboyer) (Go target)
* [Mike Lischke](http://www.soft-gems.net/) (C++ completed target)
* Dan McLaughlin (C++ initial target)
* David Sisson (C++ initial target and test)
* [Janyou](https://github.com/janyou) (Swift target)
* [Ewan Mellor](https://github.com/ewanmellor), [Hanzhou Shi](https://github.com/hanjoes) (Swift target merging)
* [Ben Hamilton](https://github.com/bhamiltoncx) (Full Unicode support in serialized ATN and all languages' runtimes for code points > U+FFFF)
* [Marcos Passos](https://github.com/marcospassos) (PHP target)
* [Lingyu Li](https://github.com/lingyv-li) (Dart target)
* [Ivan Kochurkin](https://github.com/KvanTTT) has made major contributions to overall quality, error handling, and Target performance.
* [Justin King](https://github.com/jcking) has done a huge amount of work across multiple targets, but especially for C++.
* [Ken Domino](https://github.com/kaby76) has a knack for finding bugs/issues and analysis; also a major contributor on the [grammars-v4 repo](https://github.com/antlr/grammars-v4).
* [Jim Idle](https://github.com/jimidle) has contributed to previous versions of ANTLR and recently jumped back in to solve a major problem with the Go target.


## Useful information

* [Release notes](https://github.com/antlr/antlr4/releases)
* [Getting started with v4](https://github.com/antlr/antlr4/blob/master/doc/getting-started.md)
* [Official site](http://www.antlr.org/)
* [Documentation](https://github.com/antlr/antlr4/blob/master/doc/index.md)
* [FAQ](https://github.com/antlr/antlr4/blob/master/doc/faq/index.md)
* [ANTLR code generation targets](https://github.com/antlr/antlr4/blob/master/doc/targets.md)<br>(Currently: Java, C#, Python3, JavaScript, Go, C++, Swift, Dart, PHP)
* _Note: As of version 4.14, we are dropping support for Python 2. We love the Python
community, but Python 2 support was officially halted in Jan 2020. More recently,
GiHub also dropped support for Python 2, which has made it impossible for us to
maintain a consistent level of quality across targets (we use GitHub for our CI).
Long live Python 3!_
* [Java API](http://www.antlr.org/api/Java/index.html)
* [ANTLR v3](http://www.antlr3.org/)
* [v3 to v4 Migration, differences](https://github.com/antlr/antlr4/blob/master/doc/faq/general.md)

You might also find the following pages useful, particularly if you want to mess around with the various target languages.
 
* [How to build ANTLR itself](https://github.com/antlr/antlr4/blob/master/doc/building-antlr.md)
* [How we create and deploy an ANTLR release](https://github.com/antlr/antlr4/blob/master/doc/releasing-antlr.md)

## The Definitive ANTLR 4 Reference

Programmers run into parsing problems all the time. Whether it’s a data format like JSON, a network protocol like SMTP, a server configuration file for Apache, a PostScript/PDF file, or a simple spreadsheet macro language—ANTLR v4 and this book will demystify the process. ANTLR v4 has been rewritten from scratch to make it easier than ever to build parsers and the language applications built on top. This completely rewritten new edition of the bestselling Definitive ANTLR Reference shows you how to take advantage of these new features.

You can buy the book [The Definitive ANTLR 4 Reference](http://amzn.com/1934356999) at amazon or an [electronic version at the publisher's site](https://pragprog.com/book/tpantlr2/the-definitive-antlr-4-reference).

You will find the [Book source code](http://pragprog.com/titles/tpantlr2/source_code) useful.

## Additional grammars
[This repository](https://github.com/antlr/grammars-v4) is a collection of grammars without actions where the
root directory name is the all-lowercase name of the language parsed
by the grammar. For example, java, cpp, csharp, c, etc...
