package com.kbindiedev.verse.gfx;

import com.kbindiedev.verse.profiling.Assertions;

import java.util.HashMap;

/** Describes rendering strategies for a given geometry (ex. mesh) */
public class Material {

    private MaterialTemplate creator;
    private HashMap<String, Object> uniformValues;

    protected Material(MaterialTemplate creator) {
        this.creator = creator;
        uniformValues = new HashMap<>();
    }

    public MaterialTemplate creator() { return creator; }

    //TODO: consider, instead of setUniformValue, do setUniformMatrix, setUniformInteger, etc etc (more explicit)
    public boolean setUniformValue(String key, Object value) {
        //TODO: only check if debug?
        if (!creator.getUniformLayout().isValid(key, value)) {
            Assertions.warn("invalid uniform key and/or value. key: %s, value: %s, creator uniform layout: %s", key, value, creator.getUniformLayout());
            return false;
        }

        uniformValues.put(key, value);
        return true;
    }

    public HashMap<String, Object> getUniformValues() { return uniformValues; }

}
