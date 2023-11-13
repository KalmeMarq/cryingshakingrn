package me.kalmemarq;

import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class World {
    private final Game game;
    private final Chunk[][] chunks = new Chunk[32][32];
    private final List<Chunk> chunksToRenderer = new ArrayList<>();
    private final Function<Integer, Tile> tileSupplier;
    
    public World(Game game) {
        this.game = game;
        new Tile(0x1020AA, 2);
        new Tile(0x10AA20, 0);
        new Tile(0x444444, 3);
        new Tile(0x444400, 1);
        new Tile(0x444400, 4);
        this.tileSupplier = (id) -> Tile.ids[id];
    }
    
    public int getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= 32 * 16 || y >= 32 * 16) {
            return 0;
        }
        Chunk chunk = this.chunks[x / 16][y / 16];
        if (chunk != null) {
            return chunk.getTile(x, y);
        }
        return 0;
    }
    
    public void tick() {
        int player_chunk_x = (int) (this.game.player.x / 16);
        int player_chunk_y = (int) (this.game.player.y / 16);
        
        int radius = 1;

        for (int x = player_chunk_x - radius; x <= player_chunk_x + radius; x++) {
            for (int y = player_chunk_y - radius; y <= player_chunk_y + radius; y++) {
                if (x >= 0 && y >= 0 && x < 32 && y < 32) {
                    if (this.chunks[x][y] == null) {
                        this.chunks[x][y] = new Chunk(this,x, y);
                    } else {
                        this.chunks[x][y].loaded = true;
                    }
                }
            }
        }

        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 32; y++) {
                Chunk chunk = this.chunks[x][y];
                if (chunk != null) {
                    if (Math.abs(player_chunk_x - chunk.getX()) > radius || Math.abs(player_chunk_y - chunk.getY()) > radius) {
                        this.chunks[x][y].loaded = false;
                    }
                }
            }
        }
    }
    
    public void render(float offsetX, float offsetY) {
        float wfbw = this.game.getWindow().getContentWidth() / 64.0f;
        float wfbh = this.game.getWindow().getContentHeight() / 64.0f;
        Player player = this.game.player;
        int player_chunk_x = (int) (player.x / 16);
        int player_chunk_y = (int) (player.y / 16);
        
        if (player.x != player.prevX || player.y != player.prevY || player.prevX == -1) {
            this.chunksToRenderer.clear();
            for (int x = 0; x < 32; x++) {
                for (int y = 0; y < 32; y++) {
                    Chunk chunk = this.chunks[x][y];
                    if (chunk != null && chunk.loaded) {
                        if ((chunk.getX() * 16 + -offsetX) > wfbw) {
                            continue;
                        }

                        if ((chunk.getX() * 16 + 16 + -offsetX) < 0) {
                            continue;
                        }

                        if ((chunk.getY() * 16 + -offsetY) > wfbh) {
                            continue;
                        }

                        if ((chunk.getY() * 16 + 16 + -offsetY) < 0) {
                            continue;
                        }

                        this.chunksToRenderer.add(chunk);
                    }
                }
            }
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(-offsetX, -offsetY, 0);
        this.game.getTextureManager().get("terrain.png").bind();

        for (Chunk chunk : this.chunksToRenderer) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            chunk.render(this.tileSupplier, 0, 0);
            GL11.glDisable(GL11.GL_TEXTURE_2D);

            if (this.game.showDebug && player_chunk_x == chunk.getX() && player_chunk_y == chunk.getY()) {
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glColor4f(0, 0, 1, 1.0f);
                GL11.glVertex3f(chunk.getX() * 16, chunk.getY() * 16, 0);
                GL11.glVertex3f(chunk.getX() * 16, chunk.getY() * 16 + 16, 0);
                GL11.glVertex3f(chunk.getX() * 16 + 0.25f, chunk.getY() * 16 + 16, 0);
                GL11.glVertex3f(chunk.getX() * 16 + 0.25f, chunk.getY() * 16, 0);

                GL11.glVertex3f(chunk.getX() * 16, chunk.getY() * 16, 0);
                GL11.glVertex3f(chunk.getX() * 16, chunk.getY() * 16 + 0.25f, 0);
                GL11.glVertex3f(chunk.getX() * 16 + 16, chunk.getY() * 16 + 0.25f, 0);
                GL11.glVertex3f(chunk.getX() * 16 + 16, chunk.getY() * 16, 0);

                GL11.glVertex3f(chunk.getX() * 16, chunk.getY() * 16 + 15.75f, 0);
                GL11.glVertex3f(chunk.getX() * 16, chunk.getY() * 16 + 16f, 0);
                GL11.glVertex3f(chunk.getX() * 16 + 16, chunk.getY() * 16 + 16f, 0);
                GL11.glVertex3f(chunk.getX() * 16 + 16, chunk.getY() * 16 + 15.75f, 0);

                GL11.glVertex3f(chunk.getX() * 16 + 15.75f, chunk.getY() * 16, 0);
                GL11.glVertex3f(chunk.getX() * 16 + 15.75f, chunk.getY() * 16 + 16, 0);
                GL11.glVertex3f(chunk.getX() * 16 + 16f, chunk.getY() * 16 + 16, 0);
                GL11.glVertex3f(chunk.getX() * 16 + 16f, chunk.getY() * 16, 0);
                GL11.glEnd();
            }
        }
        GL11.glPopMatrix();
    }
    
    public void renderChunkStatusMap() {
        GL11.glBegin(GL11.GL_QUADS);
        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 32; y++) {
                Chunk chunk = this.chunks[x][y];
                if (chunk == null) GL11.glColor4f(0.2f, 0.2f, 0.2f, 1.0f);
                else if (this.chunksToRenderer.contains(chunk)) GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
                else GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
                GL11.glVertex3f(x * 2, y * 2, 0);
                GL11.glVertex3f(x * 2, y * 2 + 2, 0);
                GL11.glVertex3f(x * 2 + 2, y * 2 + 2, 0);
                GL11.glVertex3f(x * 2 + 2, y * 2, 0);
            }
        }
        GL11.glEnd();
    }
    
    public void destroy() {
        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 32; y++) {
                Chunk chunk = this.chunks[x][y];
                if (chunk != null) {
                    chunk.destroy();
                }
            }
        }
    }
}
