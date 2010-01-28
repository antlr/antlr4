package org.antlr.v4.gunit;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.BufferedTreeNodeStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.stringtemplate.AutoIndentWriter;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    public static String normalizeTreeSpec(String t) {
        List<String> words = new ArrayList<String>();
        int i = 0;
        StringBuilder word = new StringBuilder();
        while ( i<t.length() ) {
            if ( t.charAt(i)=='(' || t.charAt(i)==')' ) {
                if ( word.length()>0 ) {
                    words.add(word.toString());
                    word.setLength(0);
                }
                words.add(String.valueOf(t.charAt(i)));
                i++;
                continue;
            }
            if ( Character.isWhitespace(t.charAt(i)) ) {
                // upon WS, save word
                if ( word.length()>0 ) {
                    words.add(word.toString());
                    word.setLength(0);
                }
                i++;
                continue;
            }

            // ... "x" or ...("x"
            if ( t.charAt(i)=='"' && (i-1)>=0 &&
                 (t.charAt(i-1)=='(' || Character.isWhitespace(t.charAt(i-1))) )
            {
                i++;
                while ( i<t.length() && t.charAt(i)!='"' ) {
                    if ( t.charAt(i)=='\\' &&
                         (i+1)<t.length() && t.charAt(i+1)=='"' ) // handle \"
                    {
                        word.append('"');
                        i+=2;
                        continue;
                    }
                    word.append(t.charAt(i));
                    i++;
                }
                i++; // skip final "
                words.add(word.toString());
                word.setLength(0);
                continue;
            }
            word.append(t.charAt(i));
            i++;
        }
        if ( word.length()>0 ) {
            words.add(word.toString());
        }
        //System.out.println("words="+words);
        StringBuilder buf = new StringBuilder();
        for (int j=0; j<words.size(); j++) {
            if ( j>0 && !words.get(j).equals(")") &&
                 !words.get(j-1).equals("(") ) {
                buf.append(' ');
            }
            buf.append(words.get(j));
        }
        return buf.toString();
    }

    public static void help() {
        System.err.println("org.antlr.v4.gunit.Gen [-o output-dir] gunit-file");
    }
}
