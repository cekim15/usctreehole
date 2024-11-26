package com.example.usctreehole;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class AnonymousNameGeneratorTest {

    private AnonymousNameGenerator nameGenerator;

    @Before
    public void setUp() {
        nameGenerator = new AnonymousNameGenerator();
    }

    @Test
    public void testGenerateRandomAnonymousName() {
        // Generate a random name
        String name = nameGenerator.generateRandomAnonymousName();

        // Check that the name is not null
        assertNotNull("Generated name should not be null", name);

        // Check that the name is one of the predefined names
        boolean isValidName = name.startsWith("Anonymous ");
        assertFalse("Generated name should start with 'Anonymous '", name.equals("Anonymous OutofNames"));
        assertFalse("Generated name should not be empty", name.isEmpty());
    }

    @Test
    public void testGenerateUniqueNames() {
        // Generate all possible names and ensure no duplicates
        Set<String> generatedNames = new HashSet<>();
        int totalNames = AnonymousNameGenerator.ALL_NAMES.length;

        for (int i = 0; i < totalNames; i++) {
            String name = nameGenerator.generateRandomAnonymousName();
            assertFalse("Generated name should not be a duplicate", generatedNames.contains(name));
            generatedNames.add(name);
        }

        // Check that all names have been used up
        String lastName = nameGenerator.generateRandomAnonymousName();
        assertFalse("When out of names, generator should return 'Anonymous OutofNames'", generatedNames.contains(lastName));
    }
}

