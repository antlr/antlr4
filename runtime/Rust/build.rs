use std::env;
use std::error::Error;
use std::process::Command;

const TOOL_VERSION: &str = "4.13.3-DBT100";

fn main() {
    let grammars = vec![
        "VisitorBasic",
        "VisitorCalc",
        "CSV",
        "ReferenceToATN",
        "XMLLexer",
        "SimpleLR",
        "Labels",
        "FHIRPath",
    ];
    let additional_args = vec![
        Some("-visitor"),
        Some("-visitor"),
        Some("-visitor"),
        None,
        None,
        None,
        None,
    ];
    let antlr_path = format!("../../../tool/target/antlr4-{}-complete.jar", TOOL_VERSION);

    for (grammar, arg) in grammars.into_iter().zip(additional_args) {
        //ignoring error because we do not need to run anything when deploying to crates.io
        let _ = gen_for_grammar(grammar, &antlr_path, arg);
    }

    println!("cargo:rerun-if-changed=build.rs");

    println!("cargo:rerun-if-changed={}", antlr_path);
}

fn gen_for_grammar(
    grammar_file_name: &str,
    antlr_path: &str,
    additional_arg: Option<&str>,
) -> Result<(), Box<dyn Error>> {
    // let out_dir = env::var("OUT_DIR").unwrap();
    // let dest_path = Path::new(&out_dir);

    let input = env::current_dir().unwrap().join("grammars");
    let file_name = grammar_file_name.to_owned() + ".g4";

    let output = Command::new("java")
        .current_dir(input)
        .arg("-cp")
        .arg(antlr_path)
        .arg("org.antlr.v4.Tool")
        .arg("-Dlanguage=Rust")
        .arg("-o")
        .arg("../tests/gen")
        .arg(&file_name)
        .args(additional_arg)
        .spawn()
        .expect("antlr tool failed to start")
        .wait_with_output()?;
    // .unwrap()
    // .stdout;
    eprintln!("xx{}", String::from_utf8(output.stdout).unwrap());

    println!("cargo:rerun-if-changed=grammars/{}", file_name);
    Ok(())
}
