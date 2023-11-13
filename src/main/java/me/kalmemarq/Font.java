package me.kalmemarq;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Font {
    private String CHARS;
    private int texture;

    public void load() {
        ObjectMapper mapper = new ObjectMapper();
        this.CHARS = "";
        try {
            for (JsonNode node : mapper.readValue(Font.class.getResourceAsStream("/font.json"), ArrayNode.class)) {
                this.CHARS += node.textValue();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.texture = GL11.glGenTextures();
        try {
            BufferedImage image = ImageIO.read(Font.class.getResourceAsStream("/font.png"));

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

            int[] pixels = new int[image.getWidth() * image.getHeight()];
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
            ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);

            for(int h = 0; h < image.getHeight(); h++) {
                for(int w = 0; w < image.getWidth(); w++) {
                    int pixel = pixels[h * image.getWidth() + w];

                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    buffer.put((byte) (pixel & 0xFF));
                    buffer.put((byte) ((pixel >> 24) & 0xFF));
                }
            }

            buffer.flip();

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawWithShadow(String text, int x, int y, int color) {
        this.draw(text, x, y, color, true);
    }

    public void draw(String text, int x, int y, int color) {
        this.draw(text, x, y, color, false);
    }
    
    private void draw(String text, int x, int y, int color, boolean shadow) {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glBegin(GL11.GL_QUADS);
        if (shadow) {
            this.drawInternal(text, x + 1, y + 1, color, true);
        }
        this.drawInternal(text, x, y, color, false);
        GL11.glEnd();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void drawInternal(String text, int x, int y, int color, boolean shadow) {
        int xx = x;

        float r = (color >> 16 & 0xFF) / 255.0f;
        float g = (color >> 8 & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        if (shadow) {
            r *= 0.15f;
            g *= 0.15f;
            b *= 0.15f;
        }

        GL11.glColor4f(r, g, b, 1.0f);
        for (int i = 0; i < text.length(); ++i) {
            char chr = text.charAt(i);
            if (chr == ' ') {
                xx += 8;
                continue;
            }

            int idx = this.CHARS.indexOf(chr);

            int u = (idx % 16) * 8;
            int v = (int)(idx / 16) * 12;

            float u0 = u / 128.0f;
            float v0 = v / 128.0f;
            float u1 = (u + 8) / 128.0f;
            float v1 = (v + 12) / 128.0f;

            GL11.glTexCoord2f(u0, v0);
            GL11.glVertex3f(xx, y - 3, 0);
            GL11.glTexCoord2f(u0, v1);
            GL11.glVertex3f(xx, y + 12 - 3, 0);
            GL11.glTexCoord2f(u1, v1);
            GL11.glVertex3f(xx + 8, y + 12 - 3, 0);
            GL11.glTexCoord2f(u1, v0);
            GL11.glVertex3f(xx + 8, y - 3, 0);

            xx += 8;
        }
    }

    public void destroy() {
        GL11.glDeleteTextures(this.texture);
    }

    static class Glyph {}
}
