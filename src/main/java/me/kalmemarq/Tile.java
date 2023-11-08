package me.kalmemarq;

public class Tile {
    private static int idCounter = 0;
    public static Tile[] ids = new Tile[256];

    public int id;
    public int color;

    public Tile(int color) {
        this.id = ++Tile.idCounter;
        this.color = color;
        Tile.ids[this.id] = this;
    }
}