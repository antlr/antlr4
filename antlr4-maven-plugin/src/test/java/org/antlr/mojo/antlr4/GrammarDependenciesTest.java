/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.mojo.antlr4;

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
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class GrammarDependenciesTest {

    @Test
    public void readsLegitimateStatusGraph() throws Exception {
        Map<File, Map.Entry<byte[], ArrayList<String>>> status =
            new HashMap<File, Map.Entry<byte[], ArrayList<String>>>();
        ArrayList<String> usages = new ArrayList<String>();
        usages.add("A.g4");
        status.put(new File("Base.g4"),
            new AbstractMap.SimpleImmutableEntry<byte[], ArrayList<String>>(new byte[]{1, 2, 3}, usages));

        Object result = GrammarDependencies.createRestrictedInputStream(
            new ByteArrayInputStream(serialize(status))).readObject();

        @SuppressWarnings("unchecked")
        Map<File, Map.Entry<byte[], ArrayList<String>>> read =
            (Map<File, Map.Entry<byte[], ArrayList<String>>>) result;
        assertEquals(usages, read.get(new File("Base.g4")).getValue());
    }

    @Test
    public void rejectsUnexpectedClassBeforeItIsInstantiated() throws Exception {
        Payload.deserialized = false;
        byte[] bytes = serialize(new Payload());

        try (ObjectInputStream in = GrammarDependencies.createRestrictedInputStream(
                new ByteArrayInputStream(bytes))) {
            in.readObject();
            fail("expected InvalidClassException");
        } catch (InvalidClassException expected) {
            // resolveClass rejects the type before readObject can run
        }
        assertFalse("payload readObject must not execute", Payload.deserialized);
    }

    private static byte[] serialize(Object o) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(o);
        }
        return bos.toByteArray();
    }

    /** Stands in for a deserialization gadget: its readObject has a side effect. */
    public static class Payload implements Serializable {
        static boolean deserialized = false;

        private void readObject(ObjectInputStream in) throws Exception {
            in.defaultReadObject();
            deserialized = true;
        }
    }
}
