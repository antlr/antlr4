package org.antlr.v4.gunit;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.BufferedTreeNodeStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.stringtemplate.AutoIndentWriter;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.io.*;

public class Gen {
    public static final String TEMPLATE_FILE =
        "/Users/parrt/antlr/code/antlr4/main/gunit/resources/org/antlr/v4/gunit/jUnit.stg";

    public static void main(String[] args) throws Exception {
        if ( args.length==0 ) System.exit(0);
        String outputDirName = ".";
        String fileName = args[0];
        if ( args[0].equals("-o") ) {
            if ( args.length<3 ) {
                help();
                System.exit(0);
            }
            outputDirName = args[1];
            fileName = args[2];
        }

        new Gen().process(fileName, outputDirName);
    }

    public void process(String fileName, String outputDirName) throws Exception {
        // PARSE SCRIPT
        ANTLRFileStream fs = new ANTLRFileStream(fileName);
        gUnitLexer lexer = new gUnitLexer(fs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        gUnitParser parser = new gUnitParser(tokens);
        RuleReturnScope r = parser.gUnitDef();

        CommonTree scriptAST = (CommonTree)r.getTree();
        System.out.println(scriptAST.toStringTree());

        // ANALYZE
        CommonTreeNodeStream nodes = new CommonTreeNodeStream(r.getTree());
        Semantics sem = new Semantics(nodes);
        sem.downup(scriptAST);

        System.out.println("options="+sem.options);

        // GENERATE CODE
        FileReader fr = new FileReader(TEMPLATE_FILE);
        StringTemplateGroup templates =
            new StringTemplateGroup(fr);
        fr.close();

        BufferedTreeNodeStream bnodes = new BufferedTreeNodeStream(scriptAST);
        jUnitGen gen = new jUnitGen(bnodes);
        gen.setTemplateLib(templates);
        RuleReturnScope r2 = gen.gUnitDef();
        StringTemplate st = (StringTemplate)r2.getTemplate();
        st.setAttribute("options", sem.options);

        FileWriter fw = new FileWriter(outputDirName+"/"+sem.name+".java");
        BufferedWriter bw = new BufferedWriter(fw);
        st.write(new AutoIndentWriter(bw));
        bw.close();
    }

    /** Borrowed from Leon Su in gunit v3 */
    public static String escapeForJava(String inputString) {
        // Gotta escape literal backslash before putting in specials that use escape.
        inputString = inputString.replace("\\", "\\\\");
        // Then double quotes need escaping (singles are OK of course).
        inputString = inputString.replace("\"", "\\\"");
        // note: replace newline to String ".\n", replace tab to String ".\t"
        inputString = inputString.replace("\n", "\\n").replace("\t", "\\t").replace("\r", "\\r").replace("\b", "\\b").replace("\f", "\\f");

        return inputString;
    }

    public static void help() {
        System.err.println("org.antlr.v4.gunit.Gen [-o output-file] gunit-file");
    }
}
