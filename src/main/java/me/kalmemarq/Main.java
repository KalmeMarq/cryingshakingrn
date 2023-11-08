package me.kalmemarq;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

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
            this.ptr = -1;
        }
    }
    
    enum DrawMode {
        TRIANGLES(GL11.GL_TRIANGLES),
        QUADS(GL11.GL_TRIANGLES);
        
        public final int glEnum;
        
        DrawMode(int glEnum) {
            this.glEnum = glEnum;
        }
        
        public int getIndexCount(int vertexCount) {
            return switch (this) {
                case TRIANGLES -> vertexCount;
                case QUADS -> vertexCount / 4 * 6;
            };
        }
    }

    enum IndexType {
        UBYTE(GL11.GL_UNSIGNED_BYTE),
        USHORT(GL11.GL_UNSIGNED_SHORT),
        UINT(GL11.GL_UNSIGNED_INT);

        public final int glEnum;

        IndexType(int glEnum) {
            this.glEnum = glEnum;
        }
        
        public static IndexType getAppropriate(int indexCount) {
            if (indexCount < 256) {
                return IndexType.UBYTE;
            } else if (indexCount < 65536) {
                return IndexType.USHORT;
            } else {
                return IndexType.UINT;
            }
        }
    }
    
    static class VertexBuffer {
        private int vao;
        private int vbo;
        private int ibo;
        private IndexType indexType = IndexType.UBYTE;
        private int indexCount;
        private DrawMode mode;
        private int vertexCount;
        
        public VertexBuffer() {
            this.vao = GL30.glGenVertexArrays();
            this.vbo = GL30.glGenBuffers();
            this.ibo = GL30.glGenBuffers();
        }
        
        public void upload(DrawMode mode, int vertexCount, ByteBuffer buffer) {
            GL30.glBindVertexArray(this.vao);
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.vbo);
            
            if (this.mode != mode || this.vertexCount > vertexCount) {
                GL30.glBufferData(GL30.GL_ARRAY_BUFFER, buffer, GL30.GL_DYNAMIC_DRAW);
            } else {
                GL30.glBufferSubData(GL30.GL_ARRAY_BUFFER, 0, buffer);
            }
            
            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.ibo);
        }
        
        public void draw() {
            GL11.glDrawElements(this.mode.glEnum, this.indexCount, this.indexType.glEnum, 0);
        }
        
        public void destroy() {
            GL30.glDeleteVertexArrays(this.vao);
            GL30.glDeleteBuffers(this.vbo);
            GL30.glDeleteBuffers(this.ibo);
        }
    }
}