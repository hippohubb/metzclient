package dev.metz.module;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import dev.metz.modules.AutoSprint;
import dev.metz.modules.BaseFinder;
import dev.metz.modules.BoatFly;
import dev.metz.modules.Flight;
import dev.metz.modules.Fullbright;
import dev.metz.modules.Speed;
import net.minecraft.client.MinecraftClient;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();
    private static final boolean[] KEY_DOWN = new boolean[GLFW.GLFW_KEY_LAST + 1];

    private ModuleManager() {
    }

    public static void init() {
        MODULES.add(new Flight());
        MODULES.add(new BoatFly());
        MODULES.add(new BaseFinder());
        MODULES.add(new Speed());
        MODULES.add(new AutoSprint());
        MODULES.add(new Fullbright());
    }

    public static List<Module> all() {
        return MODULES;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T get(Class<T> type) {
        for (Module m : MODULES) {
            if (m.getClass() == type) {
                return (T) m;
            }
        }
        throw new IllegalArgumentException("Unknown module " + type);
    }

    public static void tick(MinecraftClient mc) {
        handleKeybinds(mc);
        if (mc.player == null || mc.world == null) {
            return;
        }
        for (Module m : MODULES) {
            if (m.isEnabled()) {
                m.onTick();
            }
        }
    }

    private static void handleKeybinds(MinecraftClient mc) {
        long window = mc.getWindow().getHandle();
        boolean canTrigger = mc.currentScreen == null && mc.player != null;
        for (Module m : MODULES) {
            int key = m.getKey();
            if (key < 0 || key > GLFW.GLFW_KEY_LAST) {
                continue;
            }
            boolean down = GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
            if (down && !KEY_DOWN[key] && canTrigger) {
                m.toggle();
            }
            KEY_DOWN[key] = down;
        }
    }
}
