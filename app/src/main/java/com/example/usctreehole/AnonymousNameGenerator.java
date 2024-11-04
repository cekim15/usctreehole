package com.example.usctreehole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AnonymousNameGenerator {
    private static final String[] ALL_NAMES = {
            "Anonymous Pumpkin", "Anonymous Vampire", "Anonymous Werewolf", "Anonymous Frankenstein", "Anonymous Zombie",
            "Anonymous Star", "Anonymous Rabbit", "Anonymous Cat", "Anonymous Ocean", "Anonymous Mountain",
            "Anonymous Mermaid", "Anonymous Knight", "Anonymous Wizard", "Anonymous Dragon", "Anonymous Unicorn",
            "Anonymous Pizza", "Anonymous Taco", "Anonymous Burrito", "Anonymous Pasta", "Anonymous Garlic Bread",
            "Anonymous Mochi", "Anonymous Milk Tea", "Anonymous Ice Cream", "Anonymous Cake", "Anonymous Chocolate"
    };

    private List<String> names;
    private Random random;

    public AnonymousNameGenerator() {
        random = new Random();
        names = new ArrayList<>();
        Collections.addAll(names, ALL_NAMES);
    }

    public String generateRandomAnonymousName() {
        if (names.isEmpty()) {
            return "Anonymous OutofNames";
        }
        int index = random.nextInt(names.size());
        String name = names.get(index);
        names.remove(index);
        return name;
    }

    public void removeUsedName(String used_name) {
        names.removeIf(name -> name.equals(used_name));
    }

    public void resetNames() {
        names.clear();
        names.addAll(Arrays.asList(ALL_NAMES));
    }
}
