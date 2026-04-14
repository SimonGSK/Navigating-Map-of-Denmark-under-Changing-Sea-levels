package Pathfinding;

public class Timer {
    private long start;

    public Timer() {
        start = System.nanoTime();
    }

    public double check() {
        return (System.nanoTime() - start) / 1e9;
    }

    public void pause() {
        start -= System.nanoTime();
    }

    public void play() {
        start += System.nanoTime();
    }
}