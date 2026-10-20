package dev.metz.module;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;

public abstract class Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private final String description;
    private final List<Setting> settings = new ArrayList<>();
    private boolean enabled;
    private int key;

    protected Module(String name, String description, int defaultKey) {
        this.name = name;
        this.description = description;
        this.key = defaultKey;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getKey() {
        return key;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    protected Setting addSetting(String name, double value, double min, double max, double step) {
        Setting setting = new Setting(name, value, min, max, step);
        settings.add(setting);
        return setting;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    /** Optional text shown next to the module name in the HUD list. */
    public String getHudInfo() {
        return null;
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    /** Called every client tick while enabled and in a world. */
    public void onTick() {
    }
}
