package com.kbindiedev.verse.gfx;

import java.util.HashMap;

/**
 * Maps Textures to "slots" (sampler locations).
 * Exists for cleanliness and so UniformLayout can verify class type (cannot verify generics alone).
 */
public class SamplerMap extends HashMap<Texture, Integer> {}
