package me.kalmemarq;

public class FrameTimer {
    private long lastTime;
    private int frameCounter;
    public int fps;
    private final int ticksPerSecond;
    private long tickLastTime;
    public double tickDelta;
    private int tickCounter;
    public int tps;
    
    public FrameTimer(int ticksPerSecond) {
        this.lastTime = System.currentTimeMillis();
        this.frameCounter = 0;
        this.fps = 0;
        this.ticksPerSecond = ticksPerSecond;
        this.tickLastTime = System.nanoTime();
        this.tickDelta = 0;
        this.tickCounter = 0;
        this.tps = 0;
    }

    public int updateTick() {
        long tickNow = System.nanoTime();
        long tickPassedSec = tickNow - this.tickLastTime;
        this.tickLastTime = tickNow;
        this.tickDelta += tickPassedSec * this.ticksPerSecond / 1e9;
        int ticks = (int) this.tickDelta;
        this.tickDelta -= ticks;
        this.tickCounter += ticks;
        return ticks;
    }

    public boolean update() {
        this.frameCounter += 1;

        if (System.currentTimeMillis() - this.lastTime > 1000) {
            this.lastTime = System.currentTimeMillis();
            this.fps = this.frameCounter;
            this.tps = this.tickCounter;
            this.frameCounter = 0;
            this.tickCounter = 0;
            return true;
        }

        return false;
    }
}
