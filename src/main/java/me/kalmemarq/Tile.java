package me.kalmemarq;

public class Tile {
    private static int idCounter = 0;
    public static Tile[] ids = new Tile[256];

    public int id;
    public int color;
    public int txr;
    public CollisionBox collisionBox;

    public Tile(int color, int txr) {
        this(color, txr, null);
    }
    
    public Tile(int color, int txr, CollisionBox collisionBox) {
        this.id = ++Tile.idCounter;
        this.color = color;
        Tile.ids[this.id] = this;
        this.collisionBox = collisionBox;
        this.txr = txr;
    }
    
    public boolean isCollidable() {
        return this.collisionBox != null;
    }
    
    public record CollisionBox(float x0, float y0, float x1, float y1) {
    }
}