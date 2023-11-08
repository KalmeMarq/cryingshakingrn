package me.kalmemarq;

import java.util.function.Function;

import org.lwjgl.opengl.GL11;

public class Chunk {
    private int[] tiles;
    private int x;
    private int y;
    public boolean loaded = true;
    private int list = -1;

    public Chunk(int x, int y) {
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
                this.tiles[y * 16 + x] = vl < 0.0 ? 1 : vl > 0.55 ? 3 : 2;//RANDOM.nextInt(2, 8);
            }
        }
    }

    public void render(Function<Integer, Tile> tileSupplier, double offsetX, double offsetY) {
        if (!this.loaded) {
            return;
        }
        
        if (this.list != -1) {
            GL11.glTranslatef((float)(offsetX), (float)(offsetY), 0);
            GL11.glCallList(this.list);
            GL11.glTranslatef((float)(-offsetX), (float)(-offsetY), 0);
            return;
        }

        this.list = GL11.glGenLists(1);
        GL11.glNewList(this.list, GL11.GL_COMPILE);

        GL11.glBegin(GL11.GL_QUADS);
        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < 16; ++y) {
                int c = this.tiles[y * 16 + x];
                Tile tile = tileSupplier.apply(c);
                GL11.glColor4f((tile.color >> 16 & 0xFF) / 255.0f, (tile.color >> 8 & 0xFF) / 255.0f, (tile.color & 0xFF) / 255.0f, 1.0f);
                GL11.glVertex3f((float) (x + this.x * 16), (float) (y + this.y * 16), 0);
                GL11.glVertex3f((float) (x + this.x * 16), (float) (y + this.y * 16 + 1), 0);
                GL11.glVertex3f((float) (x + this.x * 16 + 1), (float) (y + this.y * 16 + 1), 0);
                GL11.glVertex3f((float) (x + this.x * 16 + 1), (float) (y + this.y * 16), 0);
            }
        }
        GL11.glEnd();

        GL11.glEndList();
    }

    public void destroy() {
        GL11.glDeleteLists(this.list, 1);
    }
}
