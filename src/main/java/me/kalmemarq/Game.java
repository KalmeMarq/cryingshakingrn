package me.kalmemarq;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import me.kalmemarq.Main.BufferBuilder;

public class Game {
    static final Random RANDOM = new Random();
    public static int SEED = RANDOM.nextInt(2000);

    private final Window window;
    private final Font font;
    private boolean showDebug;
    private final FrameTimer frameTimer;

    public Game() {
        this.window = new Window(800, 600, "Crying Shaking rn");
        this.font = new Font();
        this.frameTimer = new FrameTimer(60);

        window.addWindowHandler(new Window.WindowEventHandler() {
            @Override
            public void onSizeChanged() {
                GL11.glViewport(0, 0, window.getContentWidth(), window.getContentHeight());
            }
        });
        window.addKeyboardHandler(new Window.KeyboardEventHandler() {
            @Override
            public void onKey(int key, int scancode, int action, int modifiers) {
                if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
                    GLFW.glfwSetWindowShouldClose(window.getHandle(), true);
                }

                if (key == GLFW.GLFW_KEY_F3 && action == GLFW.GLFW_PRESS) {
                    Game.this.showDebug = !Game.this.showDebug;
                }
            }
        });
    }

    public void run() {
        window.initialize();
        window.setVsync(true);

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        float old_playerX = -1;
        float old_playerY = -1;
        float playerX = 16 * 16;
        float playerY = 16 * 16;
        int playerDir = 0;

        Chunk[][] chunks = new Chunk[32][32];
        List<Chunk> chunksToRenderer = new ArrayList<>();

        new Tile(0x1020AA);
        new Tile(0x10AA20);
        new Tile(0x444444);
        Function<Integer, Tile> tileSupplier = (id) -> {
            return Tile.ids[id];
        };

        font.load();

        BufferBuilder bufferBuilder = new BufferBuilder();

        while (!window.shouldClose()) {
            
            int player_chunk_x = (int) (playerX / 16);
            int player_chunk_y = (int) (playerY / 16);

            for (int i = this.frameTimer.updateTick() - 1; i >= 0; --i) {
                old_playerX = playerX;
                old_playerY = playerY;

                if (GLFW.glfwGetKey(window.getHandle(), GLFW.GLFW_KEY_D) != GLFW.GLFW_RELEASE) {
                    playerX += 0.1f;
                    playerDir = 3;
                }

                if (GLFW.glfwGetKey(window.getHandle(), GLFW.GLFW_KEY_A) != GLFW.GLFW_RELEASE) {
                    playerX -= 0.1f;
                    playerDir = 1;
                }

                if (GLFW.glfwGetKey(window.getHandle(), GLFW.GLFW_KEY_S) != GLFW.GLFW_RELEASE) {
                    playerY += 0.1f;
                    playerDir = 0;
                }

                if (GLFW.glfwGetKey(window.getHandle(), GLFW.GLFW_KEY_W) != GLFW.GLFW_RELEASE) {
                    playerY -= 0.1f;
                    playerDir = 2;
                }

                int radius = 1;

                for (int x = player_chunk_x - radius; x <= player_chunk_x + radius; x++) {
                    for (int y = player_chunk_y - radius; y <= player_chunk_y + radius; y++) {
                        if (x >= 0 && y >= 0 && x < 32 && y < 32) {
                            if (chunks[x][y] == null) {
                                chunks[x][y] = new Chunk(x, y);
                            } else {
                                chunks[x][y].loaded = true;
                            }
                        }
                    }
                }

                for (int x = 0; x < 32; x++) {
                    for (int y = 0; y < 32; y++) {
                        Chunk chunk = chunks[x][y];
                        if (chunk != null) {
                            if (Math.abs(player_chunk_x - chunk.getX()) > radius || Math.abs(player_chunk_y - chunk.getY()) > radius) {
                                chunks[x][y].loaded = false;
                            }
                        }
                    }
                }
            }

            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, window.getContentWidth() / 64.0f, window.getContentHeight() / 64.0f, 0.0, 1000.0, 3000.0);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0f, 0.0f, -2000.0f);

            float wfbw = window.getContentWidth() / 64.0f;
            float wfbh = window.getContentHeight() / 64.0f;
            float offsetX = playerX - window.getContentWidth() / 64.0f / 2;
            float offsetY = playerY - window.getContentHeight() / 64.0f / 2;
            
            if (playerX != old_playerX || playerY != old_playerY || old_playerX == -1) {
                chunksToRenderer.clear();
                for (int x = 0; x < 32; x++) {
                    for (int y = 0; y < 32; y++) {
                        Chunk chunk = chunks[x][y]; 
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

                            chunksToRenderer.add(chunk);
                        }
                    }
                }
            }

            for (Chunk chunk : chunksToRenderer) {
                chunk.render(tileSupplier, -offsetX, -offsetY);
                if (this.showDebug && player_chunk_x == chunk.getX() && player_chunk_y == chunk.getY()) {
                    GL11.glBegin(GL11.GL_QUADS);
                    GL11.glColor4f(0, 0, 1, 1.0f);
                    GL11.glVertex3f(-offsetX + chunk.getX() * 16, -offsetY + chunk.getY() * 16, 0);
                    GL11.glVertex3f(-offsetX + chunk.getX() * 16, -offsetY + chunk.getY() * 16 + 16, 0);
                    GL11.glVertex3f(-offsetX + chunk.getX() * 16 + 0.25f, -offsetY + chunk.getY() * 16 + 16, 0);
                    GL11.glVertex3f(-offsetX + chunk.getX() * 16 + 0.25f, -offsetY + chunk.getY() * 16, 0);

                    GL11.glVertex3f(-offsetX + chunk.getX() * 16, -offsetY + chunk.getY() * 16, 0);
                    GL11.glVertex3f(-offsetX + chunk.getX() * 16, -offsetY + chunk.getY() * 16 + 0.25f, 0);
                    GL11.glVertex3f(-offsetX + chunk.getX() * 16 + 16, -offsetY + chunk.getY() * 16 + 0.25f, 0);
                    GL11.glVertex3f(-offsetX + chunk.getX() * 16 + 16, -offsetY + chunk.getY() * 16, 0);

                    GL11.glVertex3f(-offsetX + chunk.getX() * 16, -offsetY + chunk.getY() * 16 + 15.75f, 0);
                    GL11.glVertex3f(-offsetX + chunk.getX() * 16, -offsetY + chunk.getY() * 16 + 16f, 0);
                    GL11.glVertex3f(-offsetX + chunk.getX() * 16 + 16, -offsetY + chunk.getY() * 16 + 16f, 0);
                    GL11.glVertex3f(-offsetX + chunk.getX() * 16 + 16, -offsetY + chunk.getY() * 16 + 15.75f, 0);

                    GL11.glVertex3f(-offsetX + chunk.getX() * 16 + 15.75f, -offsetY + chunk.getY() * 16, 0);
                    GL11.glVertex3f(-offsetX + chunk.getX() * 16 + 15.75f, -offsetY + chunk.getY() * 16 + 16, 0);
                    GL11.glVertex3f(-offsetX + chunk.getX() * 16 + 16f, -offsetY + chunk.getY() * 16 + 16, 0);
                    GL11.glVertex3f(-offsetX + chunk.getX() * 16 + 16f, -offsetY + chunk.getY() * 16, 0);
                    GL11.glEnd();
                }
            }

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
            GL11.glVertex3f(playerX - 0.5f + -offsetX, playerY - 0.5f + -offsetY, 0.0f);
            GL11.glVertex3f(playerX - 0.5f + -offsetX, playerY + 0.5f + -offsetY, 0.0f);
            GL11.glVertex3f(playerX + 0.5f + -offsetX, playerY + 0.5f + -offsetY, 0.0f);
            GL11.glVertex3f(playerX + 0.5f + -offsetX, playerY - 0.5f + -offsetY, 0.0f);

            GL11.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
            if (playerDir == 0) {
                GL11.glVertex3f(playerX + -offsetX - 0.05f, playerY + -offsetY, 0);
                GL11.glVertex3f(playerX + -offsetX - 0.05f, playerY + -offsetY + 1f, 0);
                GL11.glVertex3f(playerX + -offsetX + 0.05f, playerY + -offsetY + 1f, 0);
                GL11.glVertex3f(playerX + -offsetX + 0.05f, playerY + -offsetY, 0);
            } else if (playerDir == 2) {
                GL11.glVertex3f(playerX + -offsetX - 0.05f, playerY + -offsetY - 1f, 0);
                GL11.glVertex3f(playerX + -offsetX - 0.05f, playerY + -offsetY, 0);
                GL11.glVertex3f(playerX + -offsetX + 0.05f, playerY + -offsetY, 0);
                GL11.glVertex3f(playerX + -offsetX + 0.05f, playerY + -offsetY - 1f, 0);
            } else if (playerDir == 1) {
                GL11.glVertex3f(playerX + -offsetX - 1f, playerY + -offsetY - 0.05f, 0);
                GL11.glVertex3f(playerX + -offsetX - 1f, playerY + -offsetY + 0.05f, 0);
                GL11.glVertex3f(playerX + -offsetX, playerY + -offsetY + 0.05f, 0);
                GL11.glVertex3f(playerX + -offsetX, playerY + -offsetY - 0.05f, 0);
            } else if (playerDir == 3) {
                GL11.glVertex3f(playerX + -offsetX, playerY + -offsetY - 0.05f, 0);
                GL11.glVertex3f(playerX + -offsetX, playerY + -offsetY + 0.05f, 0);
                GL11.glVertex3f(playerX + -offsetX + 1f, playerY + -offsetY + 0.05f, 0);
                GL11.glVertex3f(playerX + -offsetX + 1f, playerY + -offsetY - 0.05f, 0);
            }
            GL11.glEnd();

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, window.getContentWidth() / 3.0f, window.getContentHeight() / 3.0f, 0.0, 1000.0, 3000.0);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0f, 0.0f, -2000.0f);

            if (this.showDebug) {
                GL11.glBegin(GL11.GL_QUADS);
                for (int x = 0; x < 32; x++) {
                    for (int y = 0; y < 32; y++) {
                        Chunk chunk = chunks[x][y];
                        if (chunk == null) GL11.glColor4f(0.2f, 0.2f, 0.2f, 1.0f);
                        else if (chunksToRenderer.indexOf(chunk) >= 0) GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
                        else GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
                        GL11.glVertex3f(x * 2, y * 2, 0);
                        GL11.glVertex3f(x * 2, y * 2 + 2, 0);
                        GL11.glVertex3f(x * 2 + 2, y * 2 + 2, 0);
                        GL11.glVertex3f(x * 2 + 2, y * 2, 0);
                    }
                }
                GL11.glEnd();
                this.font.draw(this.frameTimer.fps + " FPS", 1, 1, 0xFFFFFF);
            }


            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            bufferBuilder.begin();
            bufferBuilder.vertex(0, 0, 0).next();
            bufferBuilder.vertex(0, 40, 0).next();
            bufferBuilder.vertex(40, 40, 0).next();
            bufferBuilder.vertex(40, 0, 0).next();
            bufferBuilder.draw();

            this.window.update();

            if (this.frameTimer.update()) {
                this.window.setTitle("Crying Shaking rn " + this.frameTimer.fps + " FPS");
            }
        }

        System.out.println("Closing game...");

        bufferBuilder.destroy();
        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 32; y++) {
                Chunk chunk = chunks[x][y];
                if (chunk != null) {
                    chunk.destroy();
                }
            }
        }

        this.destroy();
    }

    public void destroy() {
        this.font.destroy();
        this.window.destroy();
    }
}
