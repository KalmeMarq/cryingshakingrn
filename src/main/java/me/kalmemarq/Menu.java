package me.kalmemarq;

import org.lwjgl.glfw.GLFW;

public class Menu {
    private int width;
    private int height;
    private String[] options = { "Play", "Quit" };
    private int selected = 0;

    public void init(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public void keyPressed(int key) {
        if (key == GLFW.GLFW_KEY_DOWN || key == GLFW.GLFW_KEY_W) {
            --this.selected;
            if (this.selected < 0) {
                this.selected = this.options.length - 1;
            }
        }
        if (key == GLFW.GLFW_KEY_UP || key == GLFW.GLFW_KEY_W) {
            this.selected = (this.selected + 1) % this.options.length;
        }


        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_SPACE) {
            if (this.selected == 0) {
                Game.getInstance().startWorld();
            } else {
                Game.getInstance().shutdown();
            }
        }
    }
    
    public void tick() {
    }
    
    public void render() {
        Game game = Game.getInstance();
        
        for (int i = 0; i < this.options.length; ++i) {
            if (i == this.selected) {
                game.font.draw("> " +this.options[i] + " <", 10, 30 + i * 12, 0xFFFFFF);
            } else {
                game.font.draw("  " +this.options[i] + "  ", 10, 30 + i * 12, 0x888888);
            }
        }
    }
}
