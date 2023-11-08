package me.kalmemarq;

public class Tile {
    private static int idcounter = 0;
    public static Tile[] ids = new Tile[256];

    public int id;
    public int color;

    public Tile(int color) {
        this.id = ++idcounter;
        this.color = color;
        ids[this.id] = this;
    }
}