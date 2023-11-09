package me.kalmemarq;

import org.lwjgl.opengl.GL11;

public class TextureManager {
    public static class Texture {
        private int id;
        private int width;
        private int height;
        
        public void bind() {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);
        }
        
        public void load() {
        }
    }

    public static class AtlasTexture extends Texture {
    }
}
