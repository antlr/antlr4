# Work in progress

This document is a work in progress, and may or may not resemble the final
documentation for creating a new language target (runtime+templates) for ANTLR 4.

# Creating a runtime port

## Development directory structure

The directory structure for developing a new ANTLR 4 target does not have a required
form. The following directory structure may be used to provide the same form as the
ANTLR 4 reference code base. This form is especially recommended for developers who
plan to port both the ANTLR 4 Tool *and* Runtime to a new language.

	/
	/runtime/[target]/
	/tool/src/org/antlr/v4/codegen/[target]Target.java
	/tool/resources/org/antlr/v4/tool/templates/codegen/[target]/[target].stg

### Tracking progress against the reference repository

To assist in updating your target as changes are made in the reference (Java) repository,
you can include the reference repository as a submodule of the Git repository for your
target. The submodule will also allow other users to see which commit your target is
synchronized with. I included this submodule at the following location.

	/reference/antlr4

The following command will add the submodule to your working repository.

	git submodule add -b "master" "git://github.com/antlr/antlr4.git" "reference/antlr4"

## Release structure

### Code generation support

The target must provide a `.jar` file for the ANTLR 4 Tool to use for code generation.
The key file to include is the following:

	/META-INF/services/org.antlr.v4.codegen.Target

This file should contain the fully qualified name of the class extending `Target` for
your runtime. This will likely look like the following.

	org.antlr.v4.codegen.[target]Target

If you are providing multiple code generation targets in a single `.jar` file, you
should include one line for each target. The C# code generation target provides the
following entries.

	org.antlr.v4.codegen.CSharp2Target
	org.antlr.v4.codegen.CSharp3Target
	org.antlr.v4.codegen.CSharp4Target

The code generation templates themselves are loaded by the runtime via the `TODO()`
method. The default implementation (inherited from the `Target` superclass) will
attempt to load the template from the following path in the `.jar` file.

	/org/antlr/v4/tool/templates/codegen/[target]/[target].stg

### Runtime

The runtime for a new target may be distributed in any form relevant to that target
language.

