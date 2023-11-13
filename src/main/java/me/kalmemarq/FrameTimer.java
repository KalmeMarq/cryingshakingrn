package me.kalmemarq;

public class FrameTimer {
    private long lastTime;
    private int frameCounter;
    public int fps;
    private final int ticksPerSecond;
    public float lastFrameDuration;
    private long tickLastTime;
    public float tickDelta;
    private int tickCounter;
    public int tps;
    
    public FrameTimer(int ticksPerSecond) {
        this.lastTime = System.currentTimeMillis();
        this.frameCounter = 0;
        this.fps = 0;
        this.ticksPerSecond = ticksPerSecond;
        this.tickLastTime = System.nanoTime() / 1000000L;
        this.tickDelta = 0;
        this.tickCounter = 0;
        this.tps = 0;
    }

    public int updateTick() {
        long tickNow = System.nanoTime() / 1000000L;
        this.lastFrameDuration = (float) (tickNow - this.tickLastTime) / (1000.0f / (float)this.ticksPerSecond);
        this.tickLastTime = tickNow;
        this.tickDelta += this.lastFrameDuration;
        int ticks = (int) this.tickDelta;
        this.tickDelta -= (float) ticks;
        this.tickCounter += ticks;
        return ticks;
    }

    public boolean update() {
        this.frameCounter += 1;

        if (System.currentTimeMillis() - this.lastTime >= 1000) {
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
