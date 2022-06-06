package com.kbindiedev.verse.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IdentityRegistryTest {

    private IdentityRegistry<String> registry;

    @BeforeEach
    public void setup() {
        registry = new IdentityRegistry<>();
    }

    @Test
    public void testByEquals() {
        String s1 = "Hello";
        String s2 = "Bye";
        String s3 = "Yo";

        String d1 = "Hello";
        String d2 = "Bye";
        String d3 = "Yo";

        String c1 = s1;
        String c2 = d2;

        assertEquals(s1, registry.getIdentityOf(s1), "s1 should match in registry by .equals()");
        assertEquals(s2, registry.getIdentityOf(s2), "s2 should match in registry by .equals()");
        assertEquals(s3, registry.getIdentityOf(s3), "s3 should match in registry by .equals()");
        assertEquals(d1, registry.getIdentityOf(d1), "d1 should match in registry by .equals()");
        assertEquals(d2, registry.getIdentityOf(d2), "d2 should match in registry by .equals()");
        assertEquals(d3, registry.getIdentityOf(d3), "d3 should match in registry by .equals()");
        assertEquals(c1, registry.getIdentityOf(c1), "c1 should match in registry by .equals()");
        assertEquals(c2, registry.getIdentityOf(c2), "c2 should match in registry by .equals()");
    }

    @Test
    public void testByDoubleEquals() {
        String s1 = "Hello";
        String s2 = "Bye";
        String s3 = "Yo";

        // otherwise compiler will make them reference the same value
        String d1 = new StringBuilder().append("Hello").toString();
        String d2 = new StringBuilder().append("Bye").toString();
        String d3 = new StringBuilder().append("Yo").toString();

        String c1 = s1;
        String c2 = d2;

        registry.getIdentityOf(s1);
        registry.getIdentityOf(s2);
        registry.getIdentityOf(s3);

        assertSame(s1, registry.getIdentityOf(s1), "s1 instance should match in registry by ==");
        assertSame(s2, registry.getIdentityOf(s2), "s2 instance should match in registry by ==");
        assertSame(s3, registry.getIdentityOf(s3), "s3 instance should match in registry by ==");
        assertNotSame(d1, registry.getIdentityOf(d1), "d1 instance should NOT match in registry by ==");
        assertNotSame(d2, registry.getIdentityOf(d2), "d2 instance should NOT match in registry by ==");
        assertNotSame(d3, registry.getIdentityOf(d3), "d3 instance should NOT match in registry by ==");
        assertSame(c1, registry.getIdentityOf(c1), "c1 instance should match in registry by ==");
        assertNotSame(c2, registry.getIdentityOf(c2), "c2 instance should NOT match in registry by ==");
    }
}
