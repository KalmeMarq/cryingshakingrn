package me.kalmemarq;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.MemoryUtil.MemoryAllocator;

import io.netty.buffer.ByteBuf;

public class Main {
    public static void km_main(String[] args) {
        short x = 15;
        short y = 37;

        int coord = (int)x << 16 | (int)y;

        System.out.println(coord);
        System.out.println((coord >> 16) + ";" + (int)(coord & 0xFFFF));

        x = -22;
        y = -47;
        
        coord = (int)x << 16 | (int)(y & 0xFFFF);

        System.out.println("-------------");

        System.out.println(coord);
        System.out.println((short)((coord & 0xFFFF0000) >> 16) + ";" + (short)(coord));
    }


    public static void main(String[] args) {
        new Game().run();
    }

    public static class BufferBuilder {
        private long ptr;
        private int offset;
        private int vertexCount;

        public BufferBuilder() {
            this.ptr = MemoryUtil.nmemAlloc(256);
        }

        public void begin() {
            this.offset = 0;
            this.vertexCount = 0;
        }

        public BufferBuilder vertex(float x, float y, float z) {
            MemoryUtil.memPutFloat(this.ptr + this.offset, x);
            MemoryUtil.memPutFloat(this.ptr + this.offset + 4, y);
            MemoryUtil.memPutFloat(this.ptr + this.offset + 8, z);
            this.offset += 12;
            return this;
        }

        public BufferBuilder texture(float u, float v) {
            MemoryUtil.memPutFloat(this.ptr + this.offset, u);
            MemoryUtil.memPutFloat(this.ptr + this.offset + 4, v);
            this.offset += 8;
            return this;
        }
        
        public BufferBuilder color(int red, int green, int blue, int alpha) {
            MemoryUtil.memPutFloat(this.ptr + this.offset, red);
            MemoryUtil.memPutFloat(this.ptr + this.offset + 1, green);
            MemoryUtil.memPutFloat(this.ptr + this.offset + 2, blue);
            MemoryUtil.memPutFloat(this.ptr + this.offset + 3, alpha);
            this.offset += 4;
            return this;
        }

        public void next() {
            this.vertexCount++;
        }

        public void draw() {
            if (this.vertexCount > 0) {
                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, this.ptr);
                GL11.glDrawArrays(GL11.GL_QUADS, 0, this.vertexCount);
                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
            }

            this.vertexCount = 0;
            this.offset = 0;
        }

        public void destroy() {
            MemoryUtil.nmemFree(this.ptr);
        }
    }
}