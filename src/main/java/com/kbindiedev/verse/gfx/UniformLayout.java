package com.kbindiedev.verse.gfx;

import org.joml.Matrix4f;

import java.util.HashMap;

/** Describes the uniforms of materials and shaders */
public class UniformLayout {

    private HashMap<String, UniformValueType> map;

    public UniformLayout() {
        map = new HashMap<>();
    }

    public HashMap<String, UniformValueType> getUniformTypeMap() { return map; }

    public void addUniformEntry(String key, UniformValueType valueType) {
        map.put(key, valueType);
    }

    public boolean isValid(String key, Object value) {
        UniformValueType vt = map.get(key);
        if (vt == null) return false;
        return vt.validate(value);
    }

    /**
     * Defines value types for uniform fields. Special cases may come up.
     * In the case of TEXTURE_ARRAY, upon using material, textures must be bound/readied accordingly
     *      if any given texture is not loaded, an error may be thrown
     */
    public enum UniformValueType {
        MATRIX4f(Matrix4f.class),
        TEXTURE_ARRAY(SamplerMap.class);    // TODO: rename to "TEXTURE_SAMPLER" maybe

        private Class<?> valid;
        UniformValueType(Class<?> valid) { this.valid = valid; }
        public boolean validate(Object toValidate) { return valid.isInstance(toValidate); }
    }

    public static class Predefined {

        public static final UniformLayout MVP_LAYOUT;  //TODO temp: unusued
        public static final UniformLayout SPRITEBATCH;

        static {
            MVP_LAYOUT = new UniformLayout();
            MVP_LAYOUT.addUniformEntry("uModel", UniformValueType.MATRIX4f);
            MVP_LAYOUT.addUniformEntry("uView", UniformValueType.MATRIX4f);
            MVP_LAYOUT.addUniformEntry("uProjection", UniformValueType.MATRIX4f);

            SPRITEBATCH = new UniformLayout();
            SPRITEBATCH.addUniformEntry("uView", UniformValueType.MATRIX4f);
            SPRITEBATCH.addUniformEntry("uProjection", UniformValueType.MATRIX4f);
            SPRITEBATCH.addUniformEntry("uTexArray", UniformValueType.TEXTURE_ARRAY);
        }

    }

}
