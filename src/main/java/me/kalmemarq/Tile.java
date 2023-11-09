package me.kalmemarq;

public class Tile {
    private static int idCounter = 0;
    public static Tile[] ids = new Tile[256];

    public int id;
    public int color;
    public CollisionBox collisionBox;

    public Tile(int color) {
        this(color, null);
    }
    
    public Tile(int color, CollisionBox collisionBox) {
        this.id = ++Tile.idCounter;
        this.color = color;
        Tile.ids[this.id] = this;
        this.collisionBox = collisionBox;
    }
    
    public boolean isCollidable() {
        return this.collisionBox != null;
    }
    
    record CollisionBox(float x0, float y0, float x1, float y1) {
    }
}