package com.kbindiedev.verse.gfx.impl.opengl_33;

import com.kbindiedev.verse.gfx.Texture;
import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.profiling.EngineWarning;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class GLTexture extends Texture {

    private int texID;
    private boolean hasAlpha;
    private int boundSlot;      //-1 if unbound

    private int width, height;

    //TODO: consider flipping texture upon loading
    public GLTexture(String filepath) {
        texID = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, texID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        width = -1;
        height = -1;
        int channels = -1;

        ByteBuffer buffer;

        try {
            BufferedImage image = ImageIO.read(new File(filepath));

            width = image.getWidth(); height = image.getHeight(); channels = image.getColorModel().getNumComponents();
            hasAlpha = image.getColorModel().hasAlpha();

            int[] pixels = new int[image.getWidth() * image.getHeight()];
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

            buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);    //4 bytes = rgba

            for (int y = 0; y < image.getHeight(); ++y) {
                for (int x = 0; x < image.getWidth(); ++x) {
                    int pixel = pixels[y * image.getWidth() + x];
                    buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                    buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                    buffer.put((byte) (pixel & 0xFF));               // Blue component
                    if (channels == 4) buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
                }
            }

            buffer.flip();  //?

        } catch (IOException e) {
            e.printStackTrace();
            buffer = null;
        }

        if (buffer == null) {
            assert false : "Error: (Texture) Could not load image '" + filepath + "'";
            return;
        }

        if (channels != 3 && channels != 4) {
            assert false : "Error: (Texture) Unknown number of channels '" + channels + "'";
            return;
        }

        int CH_TYPE = (channels == 3 ? GL_RGB : GL_RGBA);

        //TODO: if not power of 2, things will become messed up.

        glTexImage2D(GL_TEXTURE_2D, 0, CH_TYPE, width, height, 0, CH_TYPE, GL_UNSIGNED_BYTE, buffer);
        boundSlot = -1;
    }


            //https://stackoverflow.com/questions/11584444/java-opengl-draw-texture

    //public void bind() { glBindTexture(GL_TEXTURE_2D, texID); }

    @Override
    public void bind(int slot) throws IndexOutOfBoundsException {
        //if (slot == boundSlot) return; //TODO: binding system (if another texture binds over this)
        if (slot < 0) { Assertions.warn("slot cannot be negative: %d", slot); return; } //TODO: or slot > max
        if (boundSlot != -1) new EngineWarning("texture already bound to slot: %d. ignoring...", boundSlot).print();
        //TODO put in gl IMPL
        GL30.glActiveTexture(GL30.GL_TEXTURE0 + slot);
        boundSlot = slot;
    }

    public void unbind() {
        if (boundSlot < 0) return;
        GL30.glActiveTexture(GL30.GL_TEXTURE0 + boundSlot);
        glBindTexture(GL_TEXTURE_2D, 0);
        boundSlot = -1;
    }

    @Override
    public int getWidth() { return width; }
    @Override
    public int getHeight() { return height; }


}
