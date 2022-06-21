package com.kbindiedev.verse.gfx;

import com.kbindiedev.verse.AssetPool;

public class MaterialTemplate {

    //TODO: when registering shaders, check that shader matches with UniformLayout
    //TODO: any reason MaterialTemplate should not have direct access to VertexAttributes ?
    private UniformLayout uniformLayout;
    private Object shaderKey;

    public MaterialTemplate(UniformLayout uniformLayout, Object shaderKey) {
        this.uniformLayout = uniformLayout;
        this.shaderKey = shaderKey;
    }


    public Material createMaterial() {
        return new Material(this);
    }

    public UniformLayout getUniformLayout() { return uniformLayout; }
    public Shader getShader(Class<? extends GraphicsEngine> implementation) { return AssetPool.getShader(new Shader.Reference(implementation, shaderKey)); }  //TODO: should this be faster? lookup slow?


    public static class PredefinedTemplates {
        public static final MaterialTemplate BASIC_SPRITEBATCH;
        public static final MaterialTemplate POS_3D_AND_COLOR;

        static {
            BASIC_SPRITEBATCH = new MaterialTemplate(UniformLayout.Predefined.SPRITEBATCH, Shader.Predefined.BASIC_SPRITEBATCH);
            POS_3D_AND_COLOR = new MaterialTemplate(UniformLayout.Predefined.MVP_LAYOUT, Shader.Predefined.POS_3D_AND_COLOR);
        }
    }

}
