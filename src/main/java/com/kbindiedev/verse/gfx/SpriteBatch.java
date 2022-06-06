package com.kbindiedev.verse.gfx;

import com.kbindiedev.verse.profiling.Assertions;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * This class was lightly inspired by the LibGDX implementation of a similar structure (SpriteBatch)
 * Attributes follow, 21 bytes per vertex, 84 bytes per sprite (quad): float(x), float(y), byte(r), byte(g), byte(b), byte(a), float(u), float(v), byte(texid)
 */
public class SpriteBatch {

    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;

    private Mesh mesh;
    private ByteBuffer vertexData;

    private IndexData indexData;    //TODO: move this into mesh? like a getter ?

    private HashMap<Texture, Integer> textureToSlot;    //TODO: hashset ?
    private int nextTextureSlot;
    private int maxTextureSlots;

    private Pixel[] colors; //0 = top left, 1 = top right, 2 = bottom left, 3 = bottom right

    private boolean drawing;
    private boolean globalFlipX, globalFlipY;

    private int rendercallsTotal, rendercallsCycle;     //The number of times a mesh.render() call was issued
    private int drawsTotal, drawsCycle;                 //The number of times a .draw() was called

    public SpriteBatch(GraphicsEngine implementation, int size, int maxTextureSlots) {
        if (size * 4 > 32767) throw new IllegalArgumentException(String.format("maximum vertex index is 32767. SpriteBatch requested %d sprites, meaning %d vertices", size, size * 4));
        System.out.printf("Creating new spritebatch of size: %d\n", size); //TODO: Verse needs some notification system
        if (maxTextureSlots > 255) {
            Assertions.warn("maximum supported number of textures is 255, wanted: %d. setting to 255", maxTextureSlots); //TODO: increase to short? (in Shader.PredefinedAttributes BASIC_SPRITEBATCH)
            maxTextureSlots = 255;
        }

        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        //projectionMatrix.ortho(-1f, 1f, 1f, -1f, -1f, 1f);  //default, y-down projection


        mesh = implementation.createMesh(MaterialTemplate.PredefinedTemplates.BASIC_SPRITEBATCH.createMaterial(), size * 4);
        vertexData = BufferUtils.createByteBuffer(Shader.PredefinedAttributes.BASIC_SPRITEBATCH.getStride() * size * 4);    //TODO: material.getVertexAttributes instead, though MUST be identical
        textureToSlot = new HashMap<>();
        this.maxTextureSlots = maxTextureSlots;
        nextTextureSlot = 0;

        //{ 0, 1, 2, 3, 2, 1 }
        indexData = new IndexData(size * 6);
        ByteBuffer buffer = indexData.getBuffer();
        int j = 0;
        for (int i = 0; i < indexData.getNumIndices(); i += 6, j += 4) {
            buffer.putShort((short)(j+0));
            buffer.putShort((short)(j+1));
            buffer.putShort((short)(j+2));
            buffer.putShort((short)(j+3));
            buffer.putShort((short)(j+2));
            buffer.putShort((short)(j+1));
        }

        colors = new Pixel[4];
        setColor(Pixel.SOLID_WHITE);

        drawing = false;
        globalFlipX = false; globalFlipY = false;
        drawsTotal = drawsCycle = rendercallsTotal = rendercallsCycle = 0;

    }

    public void setProjectionMatrix(Matrix4f projectionMatrix) { this.projectionMatrix = projectionMatrix; }
    public void setViewMatrix(Matrix4f viewMatrix) { this.viewMatrix = viewMatrix; }

    /**
     * if true, all future calls to .draw() will flip the texture being drawn.
     * Any .draw() input parameter describing flipX or flipY will still work, although with reverse effect.
     *      e.g. if global and local is true, the texture would be flipped twice, resulting in the original texture.
     */
    public void setGlobalFlipSettings(boolean flipX, boolean flipY) { globalFlipX = flipX; globalFlipY = flipY; }

    /** @return the number of times mesh.render() was called (rendered to screen) in total */
    public int getRendercallsTotal() { return rendercallsTotal; }
    /** @return the number of times mesh.render() was called (rendered to screen) since .begin() was called */
    public int getRendercallsCycle() { return rendercallsCycle; }
    /** @return the number of times .draw() was called in total */
    public int getDrawsTotal() { return drawsTotal; }
    /** @return the number of times .draw() was called since .begin() was called */
    public int getDrawsCycle() { return drawsCycle; }

    public void begin() {
        if (drawing) Assertions.warn("already drawing, call .end() before .begin() again");
        drawing = true;

        drawsCycle = 0;
        rendercallsCycle = 0;
    }

    public void end() {
        if (!drawing) Assertions.warn("not drawing, call .begin() before .end()");
        if (vertexData.position() != 0) flush();
        drawing = false;
    }

    /** Draw my contents to the screen */
    public void flush() {
        int vertexIndex = vertexData.position();
        if (vertexIndex == 0) return;

        rendercallsCycle++;
        rendercallsTotal++;

        vertexData.position(0);
        mesh.bufferVertices(vertexData, 0, 0, vertexIndex);

        indexData.numIndices = vertexIndex / (Shader.PredefinedAttributes.BASIC_SPRITEBATCH.getStride() * 4) * 6;   //TODO: mesh sets index. hand control to user?
        mesh.setIndices(indexData); //do not need to re-set every flush. TODO: make some getter for mesh's indexData instead


        Texture[] textures = textureToSlot.keySet().toArray(new Texture[0]);
        mesh.getMaterial().setUniformValue("uProjection", projectionMatrix);
        mesh.getMaterial().setUniformValue("uView", viewMatrix);
        mesh.getMaterial().setUniformValue("uTexArray", textures);                  //TODO: force set uniform values every frame? makes "dirty flags" inside materials easier.

        //TODO: TEMP
        //GL33.glEnable(GL11.GL_BLEND);
        //GL33.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mesh.render();
        //GL33.glDisable(GL11.GL_BLEND);

        vertexData.position(0);
        textureToSlot.clear();
        nextTextureSlot = 0;
    }


    public void draw(Sprite sprite, float x, float y) { draw(sprite, x, y, sprite.getWidth(), sprite.getHeight()); } // TODO below
    public void draw(Sprite sprite, float x, float y, float width, float height) {
        draw(sprite.getTexture(), x, y, width, height, sprite.getU1(), sprite.getV1(), sprite.getU2(), sprite.getV2());
    }
    public void draw(Sprite sprite, float x, float y, float width, float height, boolean flipX, boolean flipY) {
        draw(sprite, x, y, 0, 0, width, height, 1f, 1f, 0, flipX, flipY);
    }
    public void draw(Sprite sprite, float x, float y, float originX, float originY, float width, float height,
                     float scaleX, float scaleY, float rotation, boolean flipX, boolean flipY) {
        draw(sprite.getTexture(), x, y, originX, originY, width, height, scaleX, scaleY, rotation, sprite.getU1(), sprite.getV1(), sprite.getU2(), sprite.getV2(), flipX, flipY);
    }

    public void draw(Texture texture, float x, float y) { draw(texture, x, y, texture.getWidth(), texture.getHeight()); }
    public void draw(Texture texture, float x, float y, float width, float height) { draw(texture, x, y, width, height, 0f, 0f, 1f, 1f); }
    public void draw(Texture texture, float x, float y, float width, float height, float u1, float v1, float u2, float v2) {
        draw(texture, x, y, 0, 0, width, height, 1f, 1f, 0, u1, v1, u2, v2, false, false);
    }
    public void draw(Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight) {
        draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, false, false);
    }
    public void draw(Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        draw(texture, x, y, 0, 0, width, height, 1f, 1f, 0, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
    }
    public void draw(Texture texture, float x, float y, float originX, float originY, float width, float height,
                     float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight,
                     boolean flipX, boolean flipY) {
        float invTexWidth = 1f / texture.getWidth();
        float invTexHeight = 1f / texture.getHeight();

        float u1 = srcX * invTexWidth;
        float v1 = srcY * invTexHeight;
        float u2 = (srcX + srcWidth) * invTexWidth;
        float v2 = (srcY + srcHeight) * invTexHeight;
        draw(texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation, u1, v1, u2, v2, flipX, flipY);
    }


    public void draw(Texture texture, float x, float y, float originX, float originY, float width, float height,
                     float scaleX, float scaleY, float rotation, float u1, float v1, float u2, float v2,
                     boolean flipX, boolean flipY) {
        if (!drawing) Assertions.warn("not drawing, call .begin() first");

        //TODO: storeOrGetSlotForTexture refactor: flush happens there too.
        if (nextTextureSlot >= maxTextureSlots || vertexData.position() == vertexData.capacity()) flush();

        byte slot = (byte)storeOrGetSlotForTexture(texture);

        slot++; //TODO: bug in GLShader usematerial, doing +1 for slots. remove this line once that's fixed


        float rx1 = -originX; float ry1 = -originY;
        float rx2 = rx1 + width; float ry2 = ry1 + height;

        if (scaleX != 1) { rx1 *= scaleX; rx2 *= scaleX; }
        if (scaleY != 1) { ry1 *= scaleY; ry2 *= scaleY; }

        //corner points
        float BLx = rx1; float BLy = ry1;
        float BRx = rx2; float BRy = ry1;
        float TLx = rx1; float TLy = ry2;
        float TRx = rx2; float TRy = ry2;

        float x1, x2, x3, x4, y1, y2, y3, y4;

        //rotations (inspired from LibGDX)
        if (rotation != 0) {
            float cos = (float)Math.cos(rotation); //radians
            float sin = (float)Math.sin(rotation);

            x1 = cos * BLx - sin * BLy;
            y1 = sin * BLx + cos * BLy;

            x2 = cos * BRx - sin * BRy;
            y2 = sin * BRx + cos * BRy;

            x3 = cos * TLx - sin * TLy;
            y3 = sin * TLx + cos * TLy;

            x4 = x1 + (x3 - x2);
            y4 = y1 + (y3 - y2);
        } else {
            x1 = BLx; y1 = BLy;
            x2 = BRx; y2 = BRy;
            x3 = TLx; y3 = TLy;
            x4 = TRx; y4 = TRy;
        }

        float worldX = x + originX;
        float worldY = y + originY;

        x1 += worldX; x2 += worldX; x3 += worldX; x4 += worldX;
        y1 += worldY; y2 += worldY; y3 += worldY; y4 += worldY;

        //texture flips (xor to avoid double-flipping)
        if ((flipX || globalFlipX) && flipX != globalFlipX) {
            float temp = u1;
            u1 = u2;
            u2 = temp;
        }
        if ((flipY || globalFlipY) && flipY != globalFlipY) {
            float temp = v1;
            v1 = v2;
            v2 = temp;
        }

        putVertex(x1, y1, colors[0], u1, v1, slot);
        putVertex(x2, y2, colors[1], u2, v1, slot);
        putVertex(x3, y3, colors[2], u1, v2, slot);
        putVertex(x4, y4, colors[3], u2, v2, slot);

        drawsCycle++;
        drawsTotal++;
    }

    /** put a single vertex. position(xy), color(rgba / pixel), texcoords(uv), texture(texid) */
    private void putVertex(float x, float y, Pixel color, float u, float v, byte texid) {
        vertexData.putFloat(x); vertexData.putFloat(y);
        vertexData.putInt(color.packed());
        vertexData.putFloat(u); vertexData.putFloat(v);
        vertexData.put(texid);
    }

    /**
     * Set the color/tint of a specific corner to draw future textures with.
     * Colors of texture will be interpolated between all corners.
     * Color is applied to drawn textures (registered by .draw(), not when flushed to screen).
     * Color can safely be changed after a .draw() call has been made, regardless of whether or not the cache has been flushed.
     */
    public void setColorCorner(Pixel color, int corner) {
        if (corner < 0 || corner > 4) { Assertions.warn("corner must be between 0 and 4. got: %d", corner); return; }
        colors[corner] = color;
    }

    /**
     * Set the color/tint to draw future textures with.
     * Functionally identical to calling .setColorCorner() for each corner.
     * Will override any existing color settings.
     * Color is applied to drawn textures (registered by .draw(), not when flushed to screen).
     * Color can safely be changed after a .draw() call has been made, regardless of whether or not the cache has been flushed.
     */
    public void setColor(Pixel color) {
        for (int i = 0; i < colors.length; ++i) colors[i] = color;
    }

    /**
     * Store a texture for use in this SpriteBatch.
     * If texture already exists in SpriteBatch, return the corresponding slot immediately without further change.
     * Otherwise, if texture limit is reached, flush the batch and clear the textures currently stored, and then add the provided texture to this batch.
     * @return slot to be associated with texture
     */
    private int storeOrGetSlotForTexture(Texture texture) {
        int slot = textureToSlot.getOrDefault(texture, -1);
        if (slot != -1) return slot;

        //flush and clear
        if (nextTextureSlot > maxTextureSlots) flush();

        textureToSlot.put(texture, nextTextureSlot);
        slot = nextTextureSlot++;

        return slot;
    }

    //TEMP for drawing quads
    private void bufferSet(float x, float y, int r, int g, int b, int a, float u, float v, int texid) {
        vertexData.putFloat(x); vertexData.putFloat(y);   //x, y
        vertexData.put((byte)r); vertexData.put((byte)g); vertexData.put((byte)b); vertexData.put((byte)a);   //color, 4 bytes

        vertexData.putFloat(u); vertexData.putFloat(v);   //u, v
        vertexData.put((byte)texid);    //texid (byte)
    }

    //TODO: probably generalize this solution
    private static class IndexData implements IIndexData {

        private ByteBuffer buffer;
        private int numIndices;

        public IndexData(int numIndices) {
            buffer = BufferUtils.createByteBuffer(numIndices * Short.BYTES);
            this.numIndices = numIndices;
        }

        @Override
        public int getNumIndices() { return numIndices; }

        @Override
        public ByteBuffer getBuffer() { return buffer; }
    }
}
