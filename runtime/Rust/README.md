# dbt-antlr4
[![Crate](https://flat.badgen.net/crates/v/dbt-antlr4)](https://crates.io/crates/dbt-antlr4/1.0.0)
[![docs](https://flat.badgen.net/badge/docs.rs/v1.0.0)](https://docs.rs/dbt-antlr4/1.0.0)


> **NOTE**:
>
> This is dbt Lab's fork of the
> [antlr4rust](https://github.com/antlr4rust/antlr4) runtime for Rust
> programming language, intended for use in
> [dbt-fusion](https://github.com/dbt-labs/dbt-fusion) and other dbt projects.
> Everything here is UNDER CONSTRUCTION, and subject to change. Specifically, as
> of version 1.0.0, all documentations are outdated and will be updated in the
> future. Please reach out to us if you want to use or contribute to this
> project, and we will be happy to help you get started.

## ANTLR4 Tool(parser generator)

Can be built using maven, or downloaded from [github.com/sdf-labs/antlr4](https://github.com/sdf-labs/antlr4/releases/)

### Usage

You should use the ANTLR4 "tool" to generate a parser, that will use the ANTLR 
runtime located here. You can run it with the following command:
```bash
java -jar <path to ANTLR4 tool> -Dlanguage=Rust MyGrammar.g4
```
For a full list of antlr4 tool options, please visit the 
[tool documentation page](https://github.com/antlr/antlr4/blob/master/doc/tool-options.md).

You can also see [build.rs](build.rs) as an example of `build.rs` configuration 
to rebuild parser automatically if grammar file was changed.

Then add following to `Cargo.toml` of the crate from which generated parser 
is going to be used:
```toml 
[dependencies]
dbt-antlr4 = "1.0.0"
```
  

  
## Licence

BSD 3-clause. 
Unless you explicitly state otherwise, 
any contribution intentionally submitted for inclusion in this project by you
shall be licensed as above, without any additional terms or conditions.

