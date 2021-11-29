package com.kbindiedev.verse.gfx;

import com.kbindiedev.verse.gfx.impl.opengl_33.GEOpenGL33;
import com.kbindiedev.verse.gfx.strategy.attributes.RawVertexAttributes;
import com.kbindiedev.verse.gfx.strategy.attributes.VertexAttributes;

import static com.kbindiedev.verse.gfx.impl.opengl_33.GL33.*;

public abstract class Shader {

    protected VertexAttributes attributes;
    protected UniformLayout uniformLayout;

    public Shader(VertexAttributes attributes, UniformLayout uniformLayout) { this.attributes = attributes; this.uniformLayout = uniformLayout; }

    //TODO: get some .dbLookup to avoid duplicate attributes listings (maybe)
    public VertexAttributes getAttributes() { return attributes; }
    public UniformLayout getUniformLayout() { return uniformLayout; }

    /** Apply the values of the provided material to this shader.
     * If the shader is not bound, SHOULD assert warning, but do nothing.
     * If material uniform layout inconsistent with shader uniformLayout, SHOULD assert warning, and do nothing
     */
    public abstract void useMaterial(Material material);


    public enum Predefined {
        BASIC_SPRITEBATCH
    }

    //TODO: at least move, either to VertexAttributes, or a more centralized system
    public static class PredefinedAttributes {
        public static final VertexAttributes DEFAULT;
        public static final VertexAttributes BASIC_SPRITEBATCH;

        static {
            RawVertexAttributes _default = new RawVertexAttributes(GEOpenGL33.gl33.GL_DYNAMIC_DRAW);   //TODO: usage should not be defined for attributes
            _default.addAttribute(0, 3, GL_FLOAT, false);   //position      //TODO: move away from GL_FLOAT and into a more standardized datatype for Verse
            _default.addAttribute(1, 4, GL_FLOAT, false);   //color
            _default.addAttribute(2, 2, GL_FLOAT, false);   //uv
            _default.addAttribute(3, 1, GL_FLOAT, false);   //texid
            DEFAULT = _default.bake();

            RawVertexAttributes spritebatch = new RawVertexAttributes(GEOpenGL33.gl33.GL_DYNAMIC_DRAW);
            spritebatch.addAttribute(0, 2, GL_FLOAT, false);    //position (x, y)
            //spritebatch.addAttribute(1, 1, GL_UNSIGNED_INT, false);    //color (packed, rgba)
            //spritebatch.addAttribute(1, 4, GL_UNSIGNED_BYTE, false);
            spritebatch.addAttribute(1, 4, GL_UNSIGNED_BYTE, true);
            spritebatch.addAttribute(2, 2, GL_FLOAT, false);    //texture coords (u, v)
            spritebatch.addAttribute(3, 1, GL_UNSIGNED_BYTE, false);     //texture id
            BASIC_SPRITEBATCH = spritebatch.bake();
        }
    }

    public static class Reference {

        private Class<? extends GraphicsEngine> implementation;
        private Object key;

        /** Make a reference for a certain implementation, by some key. Key can be any object; generally string or custom enum, or Shader.Predefined */
        public Reference(Class<? extends GraphicsEngine> implementation, Object key) {
            this.implementation = implementation;
            this.key = key;
        }

        @Override
        public String toString() {
            return implementation.toString() + " - " + key.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Reference)) return false;
            Reference r = (Reference)o;

            return (implementation.equals(r.implementation) && key.equals(r.key));
        }

        @Override
        //https://stackoverflow.com/questions/22308130/example-of-2-objects-in-java-having-same-hash-value-but-equals-method-returns-fa
        public int hashCode() {
            return 961 + 31 * implementation.hashCode() + key.hashCode();
        }

    }

}
