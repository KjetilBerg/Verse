package com.kbindiedev.verse.gfx;

/**
 * Batches shapes into fewer draw calls. "Shapes" include: Point, Line, Polygon, Circle. Includes fill and outlining of shapes.
 * (0, 0) is top left corner. // TODO: adjust (bottom left?)
 */
public class ShapeBatch {

    /*
    private Mesh mesh;

    // TODO: split to FillShapeBatch, LineShapeBatch, PointBatch ?
    public ShapeBatch(GraphicsEngine implementation, int numFillShapes, int numLineShapes, int numPointShapes) {
        if (size * 4 > 32767) throw new IllegalArgumentException(String.format("maximum vertex index is 32767. SpriteBatch requested %d sprites, meaning %d vertices", size, size * 4));
        System.out.printf("Creating new spritebatch of size: %d\n", size); //TODO: Verse needs some notification system



        mesh = implementation.createMesh(MaterialTemplate.PredefinedTemplates.BASIC_SPRITEBATCH.createMaterial(), size * 4);
        vertexData = BufferUtils.createByteBuffer(Shader.PredefinedAttributes.BASIC_SPRITEBATCH.getStride() * size * 4);    //TODO: material.getVertexAttributes instead, though MUST be identical

        //{ 0, 1, 2, 3, 2, 1 }
        indexDataBuffer = new IndexDataBuffer(size * 6);
        ByteBuffer buffer = indexDataBuffer.getBuffer();
        int j = 0;
        for (int i = 0; i < indexDataBuffer.getNumIndices(); i += 6, j += 4) {
            buffer.putShort((short)(j+0));
            buffer.putShort((short)(j+1));
            buffer.putShort((short)(j+2));
            buffer.putShort((short)(j+3));
            buffer.putShort((short)(j+2));
            buffer.putShort((short)(j+1));
        }

        colors = new Pixel[4];
        setColor(Pixel.SOLID_WHITE);

        zPos = new float[4];

        drawing = false;
        globalFlipX = false; globalFlipY = false;
        drawsTotal = drawsCycle = rendercallsTotal = rendercallsCycle = 0;

    }
*/
}
