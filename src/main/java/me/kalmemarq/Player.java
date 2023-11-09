package me.kalmemarq;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class Player {
    public float prevX = -1;
    public float prevY = -1;
    public float x = 16 * 16;
    public float y = 16 * 16;
    public int facingDirection = 0;
    
    public void tick() {
        this.prevX = this.x;
        this.prevY = this.y;
        
        int xa = 0;
        int ya = 0;

        if (GLFW.glfwGetKey(Game.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_D) != GLFW.GLFW_RELEASE) {
            xa += 1;
            this.facingDirection = 3;
        }

        if (GLFW.glfwGetKey(Game.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_A) != GLFW.GLFW_RELEASE) {
            xa -= 1;
            this.facingDirection = 1;
        }

        if (GLFW.glfwGetKey(Game.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_S) != GLFW.GLFW_RELEASE) {
            ya += 1;
            this.facingDirection = 0;
        }

        if (GLFW.glfwGetKey(Game.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_W) != GLFW.GLFW_RELEASE) {
            ya -= 1;
            this.facingDirection = 2;
        }
        
        this.x += xa * 0.74f;
        this.y += ya * 0.74f;
    }
    
    public void render(float deltaTime) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        GL11.glVertex3f(x - 0.5f, y - 0.5f, 0.0f);
        GL11.glVertex3f(x - 0.5f, y + 0.5f, 0.0f);
        GL11.glVertex3f(x + 0.5f, y + 0.5f, 0.0f);
        GL11.glVertex3f(x + 0.5f, y - 0.5f, 0.0f);

        GL11.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
        if (this.facingDirection == 0) {
            GL11.glVertex3f(x - 0.05f, y, 0);
            GL11.glVertex3f(x - 0.05f, y + 1f, 0);
            GL11.glVertex3f(x + 0.05f, y + 1f, 0);
            GL11.glVertex3f(x + 0.05f, y, 0);
        } else if (this.facingDirection == 2) {
            GL11.glVertex3f(x - 0.05f, y - 1f, 0);
            GL11.glVertex3f(x - 0.05f, y, 0);
            GL11.glVertex3f(x + 0.05f, y, 0);
            GL11.glVertex3f(x + 0.05f, y - 1f, 0);
        } else if (this.facingDirection == 1) {
            GL11.glVertex3f(x - 1f, y - 0.05f, 0);
            GL11.glVertex3f(x - 1f, y + 0.05f, 0);
            GL11.glVertex3f(x, y + 0.05f, 0);
            GL11.glVertex3f(x, y - 0.05f, 0);
        } else if (this.facingDirection == 3) {
            GL11.glVertex3f(x, y - 0.05f, 0);
            GL11.glVertex3f(x, y + 0.05f, 0);
            GL11.glVertex3f(x + 1f, y + 0.05f, 0);
            GL11.glVertex3f(x + 1f, y - 0.05f, 0);
        }
        GL11.glEnd();
    }
}
