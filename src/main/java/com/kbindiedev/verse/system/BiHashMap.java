package com.kbindiedev.verse.system;

import java.util.HashMap;

/** A bi-directional HashMap. */
public class BiHashMap<K, V> extends BiMap<K, V> {

    public BiHashMap() { super(HashMap::new, HashMap::new); }

}