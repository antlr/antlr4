/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.mojo.antlr4;

import org.antlr.mojo.antlr4.GrammarDependencies.RestrictedObjectInputStream;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GrammarDependenciesSecurityTest {
    static final class Gadget implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    private static byte[] serialize(Object value) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(value);
        }
        return bytes.toByteArray();
    }

    @Test
    public void rejectsUnexpectedClass() throws Exception {
        byte[] hostile = serialize(new Gadget());

        try (ObjectInputStream in = new RestrictedObjectInputStream(new ByteArrayInputStream(hostile))) {
            in.readObject();
            fail("expected a hostile class to be rejected before instantiation");
        } catch (InvalidClassException expected) {
            assertEquals(Gadget.class.getName(), expected.classname);
        }
    }

    @Test
    public void loadsLegitimateStatusMap() throws Exception {
        Map<File, Map.Entry<byte[], Collection<String>>> status =
            new HashMap<File, Map.Entry<byte[], Collection<String>>>();
        Collection<String> usages = new ArrayList<String>();
        usages.add("test/Foo.g4");
        status.put(new File("Bar.g4"),
            new AbstractMap.SimpleImmutableEntry<byte[], Collection<String>>(
                new byte[] { 1, 2, 3 }, usages));

        byte[] serialized = serialize(status);

        try (ObjectInputStream in = new RestrictedObjectInputStream(new ByteArrayInputStream(serialized))) {
            @SuppressWarnings("unchecked")
            Map<File, Map.Entry<byte[], Collection<String>>> restored =
                (Map<File, Map.Entry<byte[], Collection<String>>>) in.readObject();

            assertEquals(1, restored.size());
            Map.Entry<byte[], Collection<String>> entry = restored.get(new File("Bar.g4"));
            assertArrayEquals(new byte[] { 1, 2, 3 }, entry.getKey());
            assertEquals(usages, entry.getValue());
        }
    }
}
