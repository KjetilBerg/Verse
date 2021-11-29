package com.kbindiedev.verse.gfx.impl.opengl_33;

import com.kbindiedev.verse.gfx.Material;
import com.kbindiedev.verse.gfx.Shader;
import com.kbindiedev.verse.gfx.Texture;
import com.kbindiedev.verse.gfx.UniformLayout;
import com.kbindiedev.verse.gfx.strategy.attributes.VertexAttributes;
import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.profiling.EngineWarning;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

public class GLShader extends Shader {

    private int shaderProgramID;
    private boolean inUse = false;  //TODO: change

    private HashMap<String, Integer> uniformLocations;

    //NOTE: missing GL_TESS_CONTROL_SHADER, GL_TESS_EVALUATION_SHADER, GL_COMPUTE_SHADER (they are all gl 4.x)

    private String vertexSource;
    private String geometrySource;
    private String fragmentSource;
    private String filepath;

    public GLShader(VertexAttributes attributes, UniformLayout uniformLayout, String filepath) {
        super(attributes, uniformLayout);
        this.filepath = filepath;
        uniformLocations = new HashMap<>();
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));    //TODO: custom Files.readAllBytes (?)
            String[] segments = source.split("#shader +([a-zA-Z]+).*?\r?\n?");

            int index = -1;
            int segment = 1;    //index 0 is always "leading whitespace"
            do {
                index = source.indexOf("#shader", index+1);
                if (index < 0) break;

                int eol = source.indexOf("\n", index);

                String type = source.substring(index+8, eol).trim().replaceAll("\r", "");   //8 = length of "#shader "
                switch (type) {
                    case "vertex":
                        vertexSource = segments[segment];
                        break;
                    case "geometry":
                        geometrySource = segments[segment];
                        break;
                    case "fragment":
                        fragmentSource = segments[segment];
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected token: '" + type + "'");
                }

                segment++;
            } while (true);

        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: '" + filepath + "'";
        }

    }

    /**
     * Create and compile a shader by the given type and source.
     * @param GL_TYPE - The type of shader. Supported: GL_VERTEX_SHADER, GL_GEOMETRY_SHADER, GL_FRAGMENT_SHADER.
     * @param source - The source to compile (code of shader).
     * @returns id of the shader, 0 if compilation failed.
     */
    private int createShader(int GL_TYPE, String source) {

        int id = glCreateShader(GL_TYPE);

        glShaderSource(id, source);
        glCompileShader(id);

        int success = glGetShaderi(id, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(id, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\t" + getShaderName(GL_TYPE) + " shader compilation failed.");
            System.out.println(glGetShaderInfoLog(id, len));
            assert false : "";
            id = 0;
        }

        return id;
    }

    private String getShaderName(int GL_TYPE) {
        switch (GL_TYPE) {
            case GL_VERTEX_SHADER: return "vertex";
            case GL_GEOMETRY_SHADER: return "geometry";
            case GL_FRAGMENT_SHADER: return "fragment";
            default: return "UNKNOWN";
        }
    }

    //TODO: add some .hasError or something
    public void compile() {

        //create shaders
        int geometryID = 0, vertexID, fragmentID;
        if (geometrySource != null) geometryID = createShader(GL_GEOMETRY_SHADER, geometrySource);
        vertexID = createShader(GL_VERTEX_SHADER, vertexSource);
        fragmentID = createShader(GL_FRAGMENT_SHADER, fragmentSource);

        //create program
        shaderProgramID = glCreateProgram();
        if (geometryID != 0) glAttachShader(shaderProgramID, geometryID);   //order does not matter
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        //error-check
        int success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tLinking of shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }

        //delete shaders (shader program does not need them)
        if (geometryID != 0) glDeleteShader(geometryID);
        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);

        updateUniformLocations();
    }

    /** Update uniform locations as per what my current "uniformLayout" object is */
    private void updateUniformLocations() {
        uniformLocations.clear();
        for (String key : uniformLayout.getUniformTypeMap().keySet()) {
            int location = glGetUniformLocation(shaderProgramID, key);
            uniformLocations.put(key, location);
        }
    }

    public void use() {
        if (inUse) return;
        //todo: "if debug"
        if (shaderProgramID == 0) new EngineWarning("shaderProgramID == 0, is this shader not compiled?").print();  //todo: maybe temp workaround, make some isCompiled flag
        glUseProgram(shaderProgramID);
        inUse = true;
    }

    public void detach() {
        glUseProgram(0);
        inUse = false;
    }

    @Override
    public void useMaterial(Material material) {
        if (!material.creator().getUniformLayout().equals(uniformLayout)) {
            Assertions.warn("material and shader uniformLayout mismatch. mine: %s, material: %d", uniformLocations, material.creator().getUniformLayout());
            return;
        }
        if (!inUse) {   //TODO: inUse system
            Assertions.warn("shader not in use");
            return;
        }

        HashMap<String, UniformLayout.UniformValueType> typeMap = material.creator().getUniformLayout().getUniformTypeMap();

        //TODO: if value same, do not upload (remember, still upload if shader rebound)
        for (Map.Entry<String, Object> entry : material.getUniformValues().entrySet()) {
            UniformLayout.UniformValueType valueType = typeMap.get(entry.getKey());
            switch (valueType) {
                case MATRIX4f:
                    uploadMat4f(entry.getKey(), (Matrix4f)entry.getValue());
                    break;
                case TEXTURE_ARRAY:
                    Texture[] textures = (Texture[])entry.getValue();
                    int[] samplerMap = new int[textures.length];    //TODO: ensure size under limit
                    for (int i = 0; i < textures.length; ++i) {
                        Texture texture = textures[i];
                        if (!(texture instanceof GLTexture)) { Assertions.warn("texture is not GLTexture, %s", texture); continue; }
                        texture.bind(i+1);
                        samplerMap[i] = i+1; //TODO? textures are never unbound
                    }
                    uploadIntArray(entry.getKey(), samplerMap);
                    break;
                default:
                    Assertions.error("opengl shader unrecognized UniformValueType: %s", valueType.name());
                    break;
            }
        }
    }

    //TODO: check bound??

    public void uploadMat4f(String key, Matrix4f mat4) {
        int location = uniformLocations.get(key);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        mat4.get(buffer);
        glUniformMatrix4fv(location, false, buffer);
    }

    public void uploadIntArray(String key, int[] array) {
        int location = uniformLocations.get(key);
        glUniform1iv(location, array);
    }

/*
    public void uploadInt(String varName, int value) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, value);
    }


*/
}
