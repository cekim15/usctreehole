package com.example.usctreehole;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AnonymousNameGeneratorTestJUnit {
    private AnonymousNameGenerator nameGenerator;
    @Before
    public void setUp() {
        nameGenerator = new AnonymousNameGenerator();
    }

    @Test
    public void testGenerateRandomAnonymousName() {
        String name = nameGenerator.generateRandomAnonymousName();
        List<String> allNamesList = Arrays.asList(AnonymousNameGenerator.ALL_NAMES);

        assertTrue("Generated name should be in the predefined list", allNamesList.contains(name));

        nameGenerator.removeUsedName(name);
        String nextName = nameGenerator.generateRandomAnonymousName();
        assertNotEquals("The same name should not be returned", name, nextName);
    }

    @Test
    public void testRemoveUsedName() {
        String nameToRemove = "Anonymous Pumpkin";
        nameGenerator.removeUsedName(nameToRemove);

        String name = nameGenerator.generateRandomAnonymousName();
        assertNotEquals("The removed name should not be generated", nameToRemove, name);

        boolean nameRemoved = true;
        for (int i = 0; i < AnonymousNameGenerator.ALL_NAMES.length; i++) {
            String generatedName = nameGenerator.generateRandomAnonymousName();
            if (generatedName.equals(nameToRemove)) {
                nameRemoved = false;
                break;
            }
        }
        assertTrue("The name should be properly removed", nameRemoved);
    }
}