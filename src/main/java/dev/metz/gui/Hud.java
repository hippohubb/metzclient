package dev.metz.gui;

import java.util.Comparator;
import java.util.List;

import dev.metz.MetzClient;
import dev.metz.module.Module;
import dev.metz.module.ModuleManager;
import dev.metz.modules.BaseFinder;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public final class Hud {
    private static final int ACCENT = 0xFF55FFFF;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int GRAY = 0xFFAAAAAA;

    private Hud() {
    }

    public static void init() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, Identifier.of("metzclient", "hud"), Hud::render);
    }

    private static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden || mc.player == null) {
            return;
        }
        var font = mc.textRenderer;

        ctx.drawTextWithShadow(font, MetzClient.NAME, 4, 4, ACCENT);

        // Enabled-module list, right aligned, longest first.
        List<Module> active = ModuleManager.all().stream()
                .filter(Module::isEnabled)
                .sorted(Comparator.comparingInt((Module m) -> -font.getWidth(label(m))))
                .toList();
        int right = ctx.getScaledWindowWidth() - 4;
        int y = 4;
        for (Module m : active) {
            String text = label(m);
            ctx.drawTextWithShadow(font, text, right - font.getWidth(text), y, ACCENT);
            y += font.fontHeight + 2;
        }

        // Base finder results below the watermark.
        BaseFinder finder = ModuleManager.get(BaseFinder.class);
        if (finder.isEnabled()) {
            int by = 18;
            ctx.drawTextWithShadow(font, "Possible bases:", 4, by, WHITE);
            by += font.fontHeight + 2;
            List<BaseFinder.Hit> hits = finder.getHits();
            if (hits.isEmpty()) {
                ctx.drawTextWithShadow(font, "none yet", 4, by, GRAY);
            }
            int shown = 0;
            for (BaseFinder.Hit hit : hits) {
                if (shown++ >= 6) {
                    break;
                }
                String line = "X " + hit.x() + "  Z " + hit.z() + "  (" + hit.score() + ")";
                ctx.drawTextWithShadow(font, line, 4, by, GRAY);
                by += font.fontHeight + 2;
            }
        }
    }

    private static String label(Module m) {
        String info = m.getHudInfo();
        return info == null ? m.getName() : m.getName() + " [" + info + "]";
    }
}
