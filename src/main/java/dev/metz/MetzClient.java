package dev.metz;

import org.lwjgl.glfw.GLFW;

import dev.metz.gui.ClickGui;
import dev.metz.gui.Hud;
import dev.metz.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class MetzClient implements ClientModInitializer {
    public static final String NAME = "Metz Client";
    public static final int GUI_KEY = GLFW.GLFW_KEY_RIGHT_SHIFT;

    private boolean guiKeyDown;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        Hud.init();
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(MinecraftClient mc) {
        ModuleManager.tick(mc);

        boolean down = GLFW.glfwGetKey(mc.getWindow().getHandle(), GUI_KEY) == GLFW.GLFW_PRESS;
        if (down && !guiKeyDown && mc.currentScreen == null && mc.player != null) {
            mc.setScreen(new ClickGui());
        }
        guiKeyDown = down;
    }
}
