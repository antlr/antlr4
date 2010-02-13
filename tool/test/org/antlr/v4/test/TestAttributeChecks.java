package org.antlr.v4.test;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.stringtemplate.v4.ST;

/** */
public class TestAttributeChecks extends BaseTest {
    String attributeTemplate =
        "parser grammar A;\n"+
        "@members {<members>}\n" +
        "a[int x] returns [int y]\n" +
        "@init {<init>}\n" +
        "    :   {<action>}\n" +
        "    ;\n" +
        "    finally {<finally>}\n" +
        "b[int d] returns [int e]\n" +
        "    :   {<action2>}\n" +
        "    ;\n" +
        "c   :   ;";

    String scopeTemplate =
        "parser grammar A;\n"+
        "@members {\n" +
        "}\n" +
        "scope S { int i; }\n" +
        "a[int x] returns [int y]\n" +
        "scope { int z; }\n" +
        "scope S;\n" +
        "@init {}\n" +
        "    :   {}\n" +
        "    ;\n" +
        "    finally {}\n" +
        "b[int d] returns [int e]\n" +
        "scope { int f; }\n" +
        "    :   {}\n" +
        "    ;\n" +
        "c   :   ;";

    String[] membersChecks = {
        "$a.y", "error(29): A.g:2:12: unknown attribute reference a in $a.y",
    };

    String[] initChecks = {
        "$a.y", "error(29): A.g:4:9: unknown attribute reference a in $a.y",
    };

    @Test public void testMembersActions() throws RecognitionException {
        for (int i = 0; i < membersChecks.length; i+=2) {
            String m = membersChecks[i];
            String expected = membersChecks[i+1];
            ST st = new ST(attributeTemplate);
            st.add("members", m);
            String grammar = st.render();
            testErrors(new String[] {grammar, expected});
        }
    }

    @Test public void testInitActions() throws RecognitionException {
        for (int i = 0; i < initChecks.length; i+=2) {
            String init = initChecks[i];
            String expected = initChecks[i+1];
            ST st = new ST(attributeTemplate);
            st.add("init", init);
            String grammar = st.render();
            testErrors(new String[] {grammar, expected});
        }
    }    
}
