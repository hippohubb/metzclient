package dev.metz.module;

/** A numeric module setting that can be nudged up/down from the click GUI. */
public class Setting {
    private final String name;
    private final double min;
    private final double max;
    private final double step;
    private double value;

    public Setting(String name, double value, double min, double max, double step) {
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public String getName() {
        return name;
    }

    public double get() {
        return value;
    }

    public int getInt() {
        return (int) Math.round(value);
    }

    public void adjust(int direction) {
        double next = value + direction * step;
        value = Math.round(Math.max(min, Math.min(max, next)) * 1000.0) / 1000.0;
    }

    public String display() {
        if (step >= 1.0) {
            return Integer.toString((int) Math.round(value));
        }
        return String.format("%.2f", value);
    }
}
