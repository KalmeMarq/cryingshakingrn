package me.kalmemarq;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class Player {
    public float prevX = -1;
    public float prevY = -1;
    public float x = 16 * 16;
    public float y = 16 * 16;
    public int facingDirection = 0;
    public int walk = 0;
    
    public void tick() {
        this.prevX = this.x;
        this.prevY = this.y;
        
        float xa = 0;
        float ya = 0;

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
        
        if (xa != 0 || ya != 0) {
            ++this.walk;
        }
        
        if (xa > 0 && ya > 0 || xa < 0 && ya < 0 || xa < 0 && ya > 0 || xa > 0 && ya < 0) {
            xa *= 0.75f;
            ya *= 0.75f;
        }
        
        this.x += xa * 0.1f;
        this.y += ya * 0.1f;
    }
    
    public void render(float deltaTime) {
        float x = this.prevX + (this.x - this.prevX) * deltaTime;
        float y = this.prevY + (this.y - this.prevY) * deltaTime;

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Game.getInstance().getTextureManager().get("mob.png").bind();
        
        GL11.glBegin(GL11.GL_QUADS);
        
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        
        if (this.facingDirection == 0) {
            int u = 0;
            int v = 0;
            float u0 = u / 128.0f;
            float v0 = v / 128.0f;
            float u1 = (u + 16) / 128.0f;
            float v1 = (v + 16) / 128.0f;

            if ((this.walk >>> 3 & 1) == 0) {
                float temp = u0;
                u0 = u1;
                u1 = temp;
            }
            
            GL11.glTexCoord2f(u0, v0);
            GL11.glVertex3f(x - 0.5f, y - 0.5f, 0.0f);
            GL11.glTexCoord2f(u0, v1);
            GL11.glVertex3f(x - 0.5f, y + 0.5f, 0.0f);
            GL11.glTexCoord2f(u1, v1);
            GL11.glVertex3f(x + 0.5f, y + 0.5f, 0.0f);
            GL11.glTexCoord2f(u1, v0);
            GL11.glVertex3f(x + 0.5f, y - 0.5f, 0.0f);
        } else if (this.facingDirection == 1) {
            int u = 80;
            int v = 0;
            
            if ((this.walk >>> 3 & 1) == 0) {
                u -= 16;
            }
            
            float u0 = u / 128.0f;
            float v0 = v / 128.0f;
            float u1 = (u + 16) / 128.0f;
            float v1 = (v + 16) / 128.0f;
            GL11.glTexCoord2f(u0, v0);
            GL11.glVertex3f(x - 0.5f, y - 0.5f, 0.0f);
            GL11.glTexCoord2f(u0, v1);
            GL11.glVertex3f(x - 0.5f, y + 0.5f, 0.0f);
            GL11.glTexCoord2f(u1, v1);
            GL11.glVertex3f(x + 0.5f, y + 0.5f, 0.0f);
            GL11.glTexCoord2f(u1, v0);
            GL11.glVertex3f(x + 0.5f, y - 0.5f, 0.0f);
        } else if (this.facingDirection == 2) {
            int u = 16;
            int v = 0;
            float u0 = u / 128.0f;
            float v0 = v / 128.0f;
            float u1 = (u + 16) / 128.0f;
            float v1 = (v + 16) / 128.0f;


            if ((this.walk >>> 3 & 1) == 0) {
                float temp = u0;
                u0 = u1;
                u1 = temp;
            }
            
            GL11.glTexCoord2f(u0, v0);
            GL11.glVertex3f(x - 0.5f, y - 0.5f, 0.0f);
            GL11.glTexCoord2f(u0, v1);
            GL11.glVertex3f(x - 0.5f, y + 0.5f, 0.0f);
            GL11.glTexCoord2f(u1, v1);
            GL11.glVertex3f(x + 0.5f, y + 0.5f, 0.0f);
            GL11.glTexCoord2f(u1, v0);
            GL11.glVertex3f(x + 0.5f, y - 0.5f, 0.0f);
        } else if (this.facingDirection == 3) {
            int u = 48;
            int v = 0;

            if ((this.walk >>> 3 & 1) == 0) {
                u -= 16;
            }
            
            float u0 = u / 128.0f;
            float v0 = v / 128.0f;
            float u1 = (u + 16) / 128.0f;
            float v1 = (v + 16) / 128.0f;
            GL11.glTexCoord2f(u0, v0);
            GL11.glVertex3f(x - 0.5f, y - 0.5f, 0.0f);
            GL11.glTexCoord2f(u0, v1);
            GL11.glVertex3f(x - 0.5f, y + 0.5f, 0.0f);
            GL11.glTexCoord2f(u1, v1);
            GL11.glVertex3f(x + 0.5f, y + 0.5f, 0.0f);
            GL11.glTexCoord2f(u1, v0);
            GL11.glVertex3f(x + 0.5f, y - 0.5f, 0.0f);
        }
        
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }
}
