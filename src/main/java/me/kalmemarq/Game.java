package me.kalmemarq;

import java.util.Random;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class Game {
    private static Game instance;
    static final Random RANDOM = new Random();
    public static int SEED = Game.RANDOM.nextInt(2000);

    private final Window window;
    public final Font font;
    private final TextureManager textureManager;
    protected boolean showDebug;
    private final FrameTimer frameTimer;
    public Player player;
    public World world;
    private Menu menu;
    private boolean running;

    public Game() {
        Game.instance = this;
        this.window = new Window(800, 600, "Crying Shaking rn");
        this.font = new Font();
        this.frameTimer = new FrameTimer(60);

        this.window.addWindowHandler(new Window.WindowEventHandler() {
            @Override
            public void onSizeChanged() {
                GL11.glViewport(0, 0, Game.this.window.getContentWidth(), Game.this.window.getContentHeight());
            }
        });
        this.window.addKeyboardHandler(new Window.KeyboardEventHandler() {
            @Override
            public void onKey(int key, int scancode, int action, int modifiers) {
                if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
                    GLFW.glfwSetWindowShouldClose(Game.this.window.getHandle(), true);
                }

                if (key == GLFW.GLFW_KEY_F3 && action == GLFW.GLFW_PRESS) {
                    Game.this.showDebug = !Game.this.showDebug;
                }

                if (key == GLFW.GLFW_KEY_F4 && action == GLFW.GLFW_PRESS) {
                    Game.this.textureManager.reload();
                }
                
                if (Game.this.menu != null && action != GLFW.GLFW_RELEASE) {
                    Game.this.menu.keyPressed(key);
                }
            }
        });
        this.textureManager = new TextureManager();
    }

    public void startWorld() {
        this.menu = null;
        this.world = new World(this);
        this.player = new Player();
    }
    
    public void shutdown() {
        this.running = false;
    }
    
    public void run() {
        this.window.initialize();
        this.window.setVsync(true);
        this.menu = new Menu();
        this.menu.init((int) (this.window.getContentWidth() / 3.0f), (int) (this.window.getContentHeight() / 3.0f));
        
        this.running = true;
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        this.font.load();

        while (this.running) {
            if (this.window.shouldClose()) {
                this.running = false;
            }
            
            int t = this.frameTimer.updateTick();
            for (int i = 0; i < t; ++i) {
                if (this.world != null) {
                    this.player.tick();
                    this.world.tick();
                }
                if (this.menu != null) {
                    this.menu.tick();
                }
            }

            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            if (this.world != null) {
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadIdentity();
                GL11.glOrtho(0.0, this.window.getContentWidth() / 64.0f, this.window.getContentHeight() / 64.0f, 0.0, 1000.0, 3000.0);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();
                GL11.glTranslatef(0.0f, 0.0f, -2000.0f);

                float ppx = this.player.prevX + (this.player.x - this.player.prevX) * this.frameTimer.lastFrameDuration;
                float ppy = this.player.prevY + (this.player.y - this.player.prevY) * this.frameTimer.lastFrameDuration;
                float offsetX = ppx - this.window.getContentWidth() / 64.0f / 2;
                float offsetY = ppy - this.window.getContentHeight() / 64.0f / 2;

                this.world.render(offsetX, offsetY);

                GL11.glPushMatrix();
                GL11.glTranslatef(-offsetX, -offsetY, 0);
                this.player.render(this.frameTimer.lastFrameDuration);
                GL11.glPopMatrix();
            }

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, this.window.getContentWidth() / 3.0f, this.window.getContentHeight() / 3.0f, 0.0, 1000.0, 3000.0);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0f, 0.0f, -2000.0f);

            if (this.world != null && this.showDebug) {
                this.world.renderChunkStatusMap();
                this.font.draw(this.frameTimer.fps + " FPS", 1, 1, 0xFFFFFF);
                this.font.draw(String.format("XY: %.4f / %.4f", this.player.x, this.player.y), 1, 13, 0xFFFFFF);
            }

            if (this.menu != null) {
                this.menu.render();
            }
            
//            GL11.glEnable(GL11.GL_TEXTURE_2D);
//            this.textureManager.get("mob.png").bind();
//            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
//            GL11.glBegin(GL11.GL_QUADS);
//            
//            float u0 = 0;
//            float v0 = 0;
//            float u1 = 16 / 128.0f;
//            float v1 = 16 / 128.0f;
//            
//            GL11.glTexCoord2f(u1, v0);
//            GL11.glVertex3f(0, 0, 0);
//            GL11.glTexCoord2f(u1, v1);
//            GL11.glVertex3f(0, 32, 0);
//            GL11.glTexCoord2f(u0, v1);
//            GL11.glVertex3f(32, 32, 0);
//            GL11.glTexCoord2f(u0, v0);
//            GL11.glVertex3f(32, 0, 0);
//
//            GL11.glEnd();
//            GL11.glDisable(GL11.GL_TEXTURE_2D);

            this.window.update();

            if (this.frameTimer.update()) {
                this.window.setTitle("Crying Shaking rn " + this.frameTimer.fps + " FPS");
            }
        }

        System.out.println("Closing game...");

        this.textureManager.destroy();
        if (this.world != null) {
            this.world.destroy();
        }

        this.destroy();
    }

    public Window getWindow() {
        return this.window;
    }

    public TextureManager getTextureManager() {
        return this.textureManager;
    }

    public void destroy() {
        this.font.destroy();
        this.window.destroy();
    }

    public static Game getInstance() {
        return Game.instance;
    }
}
