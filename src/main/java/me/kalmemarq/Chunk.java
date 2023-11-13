package me.kalmemarq;

import java.util.Random;
import java.util.function.Function;

import org.lwjgl.opengl.GL11;

public class Chunk {
    private static final Random RANDOM = new Random();
    private int[] tiles;
    private int x;
    private int y;
    public boolean loaded = true;
    private int list = -1;
    private World world;

    public Chunk(World world, int x, int y) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.tiles = new int[16 * 16];
        this.generate();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void generate() {
        double FREQUENCY = 1.0 / 24.0;

        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < 16; ++y) {
                double vl = OpenSimplex2S.noise3_ImproveXY(Game.SEED, (x + this.x * 16) * FREQUENCY, (y + this.y * 16) * FREQUENCY, 0.0);
                this.tiles[y * 16 + x] = vl < 0.0 ? 1 : vl > 0.55 ? 3 : vl < 0.15 ? 4 : 2;
            }
        }

        int r = RANDOM.nextInt(8);
        
        if (r < 8 && r > 1) {
            int cx = 8;
            int cy = 8;
            for (int x = 8 - r; x < 8 + r; ++x) {
                for (int y = 8 - r; y < 8 + r; ++y) {
                    if ((((x - cx) * (x - cx)) + ((y - cy) * (y - cy))) < r && this.tiles[y * 16 + x] == 2) {
                        this.tiles[y * 16 + x] = 5;
                    }
                }
            }
        }
    }
    
    public int getTile(int x, int y) {
        return this.tiles[(y & 0xF) * 16 + (x & 0xF)];
    }

    public void render(Function<Integer, Tile> tileSupplier, double offsetX, double offsetY) {
        if (!this.loaded) {
            return;
        }
        
        if (this.list != -1) {
            GL11.glCallList(this.list);
            return;
        }

        this.list = GL11.glGenLists(1);
        GL11.glNewList(this.list, GL11.GL_COMPILE);

        GL11.glBegin(GL11.GL_QUADS);
        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < 16; ++y) {
                int c = this.tiles[y * 16 + x];
                Tile tile = tileSupplier.apply(c);
                int u = tile.txr * 16;
                int v = 0;
                float u0 = u / 128.0f;
                float v0 = v / 128.0f;
                float u1 = (u + 16) / 128.0f;
                float v1 = (v + 16) / 128.0f;
                int bx = x + (this.x * 16);
                int by = y + (this.y * 16);
                
                if (tile.id == 2) {
                    int rf = Chunk.RANDOM.nextInt(0, 3);
                    if (rf == 1) {
                        float temp = u0;
                        u0 = u1;
                        u1 = temp;
                    }
                    if (rf == 2) {
                        float temp = v0;
                        v0 = v1;
                        v1 = temp;
                    }
                }
                
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glTexCoord2f(u0, v0);
                GL11.glVertex3f((float) (x + this.x * 16) - 0.0001f, (float) (y + this.y * 16.0f) - 0.0001f, 0);
                GL11.glTexCoord2f(u0, v1);
                GL11.glVertex3f((float) (x + this.x * 16) - 0.0001f, (float) (y + this.y * 16 + 1.0f) + 0.0001f, 0);
                GL11.glTexCoord2f(u1, v1);
                GL11.glVertex3f((float) (x + this.x * 16 + 1) + 0.0001f, (float) (y + this.y * 16 + 1.0f) + 0.0001f, 0);
                GL11.glTexCoord2f(u1, v0);
                GL11.glVertex3f((float) (x + this.x * 16 + 1) + 0.0001f, (float) (y + this.y * 16) - 0.0001f, 0);
            }
        }
        GL11.glEnd();

        GL11.glEndList();
    }

    public void destroy() {
        GL11.glDeleteLists(this.list, 1);
    }
}
