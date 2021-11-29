package com.kbindiedev.verse;

import com.kbindiedev.verse.gfx.Shader;
import com.kbindiedev.verse.gfx.impl.opengl_33.GLTexture;
import com.kbindiedev.verse.profiling.EngineWarning;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {

    private static Map<Shader.Reference, Shader> shaders = new HashMap<>();
    private static Map<String, GLTexture> textures = new HashMap<>();

    //TODO: change shader registration. messy. also, consider some ILoadable & IUnloadable ? (for now, assume shaders are loaded)
    public static void registerShader(Shader.Reference shaderReference, Shader shader) {
        if (shaders.containsKey(shaderReference)) new EngineWarning("shader already exists for reference: %s", shaderReference);
        shaders.put(shaderReference, shader);
    }

    public static Shader getShader(Shader.Reference shaderReference) {
        Shader shader = shaders.get(shaderReference);
        if (shader == null) new EngineWarning("shader does not exist for: %s", shaderReference.toString()).print();
        return shader;
    }

    public static GLTexture getTexture(String resourceName) {
        File file = new File(resourceName);
        if (textures.containsKey(file.getAbsolutePath())) return textures.get(file.getAbsolutePath());

        GLTexture GLTexture = new GLTexture(resourceName);
        textures.put(file.getAbsolutePath(), GLTexture);
        return GLTexture;
    }
}
