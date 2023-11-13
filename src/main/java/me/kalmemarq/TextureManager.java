package me.kalmemarq;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;

public class TextureManager {
    private final Map<String, Texture> textures;
    
    public TextureManager() {
        this.textures = new HashMap<>();
    }

    public Texture get(String path) {
        Texture txr = this.textures.get(path);
        if (txr == null) {
            txr = new Texture(path);
            txr.load();
            this.textures.put(path, txr);
        }
        return txr;
    }
    
    public void reload() {
        for (Texture texture : this.textures.values()) {
            texture.load();
        }
    }
    
    public void destroy() {
        for (Texture texture : this.textures.values()) {
            texture.destroy();
        }
    }

    public static class Texture {
        private int id = -1;
        private String path;
        private int width;
        private int height;

        public Texture(String path) {
            this.path = path;
        }
        
        public void bind() {
            if (this.id == -1) {
                this.id = GL11.glGenTextures();
            }
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);
        }
        
        public void load() {
            this.bind();
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            try {
                BufferedImage image = ImageIO.read(Objects.requireNonNull(TextureManager.class.getResourceAsStream("/" + this.path)));
                this.width = image.getWidth();
                this.height = image.getHeight();
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

                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void destroy() {
            if (this.id != -1) {
                GL11.glDeleteTextures(this.id);
                this.id = -1;
            }
        }
    }
}
