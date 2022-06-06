package com.kbindiedev.verse.math.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InfiniteBitmaskTest {

    @Test
    public void setAndTestBits() {

        InfiniteBitmask mask = new InfiniteBitmask();

        mask.setBit(0, true);
        mask.setBit(3, true);
        mask.setBit(60, true);
        mask.setBit(127, true);
        mask.setBit(3078, true);

        assertTrue(mask.testBit(0), "bit should be 1 at index 0");
        assertTrue(mask.testBit(3), "bit should be 1 at index 3");
        assertTrue(mask.testBit(60), "bit should be 1 at index 60");
        assertTrue(mask.testBit(127), "bit should be 1 at index 127");
        assertTrue(mask.testBit(3078), "bit should be 1 at index 3078");
        assertFalse(mask.testBit(2), "bit should be 0 at index 2");
        assertFalse(mask.testBit(5), "bit should be 0 at index 5");
        assertFalse(mask.testBit(27), "bit should be 0 at index 27");
        assertFalse(mask.testBit(40000), "bit should be 0 at index 40000");

    }

    @Test
    public void setAndGetValue() {

        InfiniteBitmask mask = new InfiniteBitmask();

        mask.setForIndex(0, 0b00100111);
        mask.setForIndex(1, 0b00100101);
        mask.setForIndex(4, 0b01000001);

        assertEquals(0b00100111, mask.getForIndex(0), "wrong at index 0");
        assertEquals(0b00100101, mask.getForIndex(1), "wrong at index 1");
        assertEquals(0b00000000, mask.getForIndex(2), "wrong at index 2");
        assertEquals(0b00000000, mask.getForIndex(3), "wrong at index 3");
        assertEquals(0b01000001, mask.getForIndex(4), "wrong at index 4");
        assertEquals(0b00000000, mask.getForIndex(20), "wrong at index 20");
    }

    @Test
    public void setValueTestBits() {

        InfiniteBitmask mask = new InfiniteBitmask(0b0101);
        mask.setForIndex(1, 0b00010010);

        assertTrue(mask.testBit(0), "bit should be 1 at index 0");
        assertTrue(mask.testBit(2), "bit should be 1 at index 2");
        assertTrue(mask.testBit(65), "bit should be 1 at index 65");
        assertTrue(mask.testBit(68), "bit should be 1 at index 68");
        assertFalse(mask.testBit(4), "bit should be 0 at index 4");
        assertFalse(mask.testBit(69), "bit should be 0 at index 69");

    }

    @Test
    public void setNextStoreThrowsErrorIfStoreSelf() {

        // immediate chain
        InfiniteBitmask mask = new InfiniteBitmask();
        assertThrows(IllegalArgumentException.class, () -> mask.setNextStore(mask), "setNextStore should throw error if store == self");

        // chain length 2
        InfiniteBitmask mask1 = new InfiniteBitmask();
        InfiniteBitmask mask2 = new InfiniteBitmask();
        mask1.setNextStore(mask2);
        assertThrows(IllegalArgumentException.class, () -> mask2.setNextStore(mask1), "setNextStore should throw error if store in existing chain");

        // middle chain
        InfiniteBitmask mask3 = new InfiniteBitmask();
        InfiniteBitmask mask4 = new InfiniteBitmask();
        InfiniteBitmask mask5 = new InfiniteBitmask();
        mask5.setNextStore(mask3);
        mask3.setNextStore(mask4);
        assertThrows(IllegalArgumentException.class, () -> mask4.setNextStore(mask5), "setNextStore should throw error if store in existing chain (middle-check)");

    }

    @Test
    public void supersetTest() {

        InfiniteBitmask mask1 = new InfiniteBitmask();
        InfiniteBitmask mask2 = new InfiniteBitmask();

        mask1.setForIndex(0, 0b0111);
        mask2.setForIndex(0, 0b0011);
        mask1.setForIndex(1, 0b1011);
        mask2.setForIndex(1, 0b1001);
        assertTrue(mask1.isBitSupersetOf(mask2), "test1: mask1 should be superset of mask2");

        InfiniteBitmask store1 = new InfiniteBitmask(0b00111000);
        InfiniteBitmask store2 = new InfiniteBitmask(0b00111000);

        mask1.setNextStore(store1);
        assertTrue(mask1.isBitSupersetOf(mask2), "test2: mask1 should be superset of mask2");

        mask2.setNextStore(store2);
        assertTrue(mask1.isBitSupersetOf(mask2), "test3: mask1 should be superset of mask2");

        mask1.setNextStore(null);
        assertFalse(mask1.isBitSupersetOf(mask2), "test4: mask1 should not be superset of mask2");

        mask1.setNextStore(store1);
        mask2.setForIndex(1, 0b1101);
        assertFalse(mask1.isBitSupersetOf(mask2), "test5: mask1 should not be superset of mask2");

        mask1.setForIndex(1, 0b1111);
        assertTrue(mask1.isBitSupersetOf(mask2), "test6: mask1 should be superset of mask2");

    }

    @Test
    public void subsetTest() {

        InfiniteBitmask mask1 = new InfiniteBitmask();
        InfiniteBitmask mask2 = new InfiniteBitmask();

        mask1.setForIndex(0, 0b0011);
        mask2.setForIndex(0, 0b0111);
        mask1.setForIndex(1, 0b1001);
        mask2.setForIndex(1, 0b1011);
        assertTrue(mask1.isBitSubsetOf(mask2), "test1: mask1 should be subset of mask2");

        InfiniteBitmask store1 = new InfiniteBitmask(0b00111000);
        InfiniteBitmask store2 = new InfiniteBitmask(0b00111000);

        mask1.setNextStore(store1);
        assertFalse(mask1.isBitSubsetOf(mask2), "test2: mask1 should not be subset of mask2");

        mask2.setNextStore(store2);
        assertTrue(mask1.isBitSubsetOf(mask2), "test3: mask1 should be subset of mask2");

        mask1.setNextStore(null);
        assertTrue(mask1.isBitSubsetOf(mask2), "test4: mask1 should be subset of mask2");

        mask2.setForIndex(1, 0b0011);
        assertFalse(mask1.isBitSubsetOf(mask2), "test5: mask1 should not be subset of mask2");

    }

    @Test
    public void overlappingTest() {

        InfiniteBitmask mask1 = new InfiniteBitmask();
        InfiniteBitmask mask2 = new InfiniteBitmask();

        mask1.setForIndex(0, 0b0011);
        mask2.setForIndex(0, 0b0111);
        mask1.setForIndex(1, 0b1001);
        mask2.setForIndex(1, 0b1011);
        assertTrue(mask1.isBitOverlappingSet(mask2), "test1: mask1 should be overlapping mask2");

        InfiniteBitmask store1 = new InfiniteBitmask(0b00111000);
        InfiniteBitmask store2 = new InfiniteBitmask(0b00111000);

        mask1.setNextStore(store1);
        assertTrue(mask1.isBitOverlappingSet(mask2), "test2: mask1 should be overlapping mask2");

        mask1.setForIndex(0, 0);
        mask1.setForIndex(1, 0);
        assertFalse(mask1.isBitOverlappingSet(mask2), "test3: mask1 should not be overlapping mask2");

        mask2.setNextStore(store2);
        assertTrue(mask1.isBitOverlappingSet(mask2), "test4: mask1 should be overlapping mask2");

        mask1.setNextStore(null);
        assertFalse(mask1.isBitOverlappingSet(mask2), "test5: mask1 should not be overlapping mask2");

        mask2.setForIndex(1, 0b0011);
        assertFalse(mask1.isBitOverlappingSet(mask2), "test6: mask1 should be overlapping mask2");

    }

    @Test
    public void disjointTest() {

        InfiniteBitmask mask1 = new InfiniteBitmask();
        InfiniteBitmask mask2 = new InfiniteBitmask();

        mask1.setForIndex(0, 0b0011);
        mask2.setForIndex(0, 0b0111);
        mask1.setForIndex(1, 0b1001);
        mask2.setForIndex(1, 0b1011);
        assertFalse(mask1.isBitDisjointSet(mask2), "test1: mask1 should not be disjoint with mask2");

        mask1.setForIndex(0, 0);
        mask1.setForIndex(1, 0b0100);
        assertTrue(mask1.isBitDisjointSet(mask2), "test2: mask1 should be disjoint with mask2");

        InfiniteBitmask store1 = new InfiniteBitmask(0b00111000);
        InfiniteBitmask store2 = new InfiniteBitmask(0b00111000);

        mask1.setNextStore(store1);
        assertTrue(mask1.isBitDisjointSet(mask2), "test3: mask1 should be disjoint with mask2");

        mask2.setNextStore(store2);
        assertFalse(mask1.isBitDisjointSet(mask2), "test4: mask1 should not be disjoint with mask2");

        mask1.setNextStore(null);
        assertTrue(mask1.isBitDisjointSet(mask2), "test5: mask1 should be disjoint with mask2");

        mask2.setNextStore(null);
        mask2.setForIndex(1, 0b1110);
        assertFalse(mask1.isBitDisjointSet(mask2), "test6: mask1 should not be disjoint with mask2");

    }

    @Test
    public void identicalTest() {

        InfiniteBitmask mask1 = new InfiniteBitmask();
        InfiniteBitmask mask2 = new InfiniteBitmask();

        mask1.setForIndex(0, 0b0011);
        mask2.setForIndex(0, 0b0111);
        mask1.setForIndex(1, 0b1001);
        mask2.setForIndex(1, 0b1011);
        assertFalse(mask1.isBitIdenticalSet(mask2), "test1: mask1 should not be identical with mask2");

        mask1.setForIndex(0, 0b0111);
        mask1.setForIndex(1, 0b1011);
        assertTrue(mask1.isBitIdenticalSet(mask2), "test2: mask1 should be identical with mask2");

        InfiniteBitmask store1 = new InfiniteBitmask(0b00111000);
        InfiniteBitmask store2 = new InfiniteBitmask(0b00111000);

        mask1.setNextStore(store1);
        assertFalse(mask1.isBitIdenticalSet(mask2), "test3: mask1 should not be identical with mask2");

        mask2.setNextStore(store2);
        assertTrue(mask1.isBitIdenticalSet(mask2), "test4: mask1 should be identical with mask2");

        mask1.setNextStore(null);
        assertFalse(mask1.isBitDisjointSet(mask2), "test5: mask1 should not be identical with mask2");

    }

}