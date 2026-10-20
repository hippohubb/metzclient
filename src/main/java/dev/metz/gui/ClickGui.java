package dev.metz.gui;

import org.lwjgl.glfw.GLFW;

import dev.metz.MetzClient;
import dev.metz.module.Module;
import dev.metz.module.ModuleManager;
import dev.metz.module.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Simple click GUI.
 * Left click a module to toggle it, right click to expand its settings.
 * Left/right click a setting to raise/lower its value.
 */
public class ClickGui extends Screen {
    private static final int PANEL_X = 20;
    private static final int PANEL_Y = 20;
    private static final int PANEL_W = 150;
    private static final int ROW_H = 14;
    private static final int SETTING_H = 12;

    private static final int BG = 0xE0101418;
    private static final int HEADER = 0xFF1B6E7A;
    private static final int ROW_ON = 0xFF1F8A99;
    private static final int ROW_OFF = 0xFF20262C;
    private static final int ROW_HOVER = 0xFF2B343C;
    private static final int SETTING_BG = 0xFF181D22;

    private Module expanded;
    private boolean leftWasDown;
    private boolean rightWasDown;

    public ClickGui() {
        super(Text.literal(MetzClient.NAME));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        long window = client.getWindow().getHandle();
        boolean leftDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rightDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        boolean leftClick = leftDown && !leftWasDown;
        boolean rightClick = rightDown && !rightWasDown;
        leftWasDown = leftDown;
        rightWasDown = rightDown;

        int total = ROW_H + 2;
        for (Module m : ModuleManager.all()) {
            total += ROW_H;
            if (m == expanded) {
                total += SETTING_H * m.getSettings().size();
            }
        }
        ctx.fill(PANEL_X - 2, PANEL_Y - 2, PANEL_X + PANEL_W + 2, PANEL_Y + total + 2, BG);

        ctx.fill(PANEL_X, PANEL_Y, PANEL_X + PANEL_W, PANEL_Y + ROW_H, HEADER);
        ctx.drawTextWithShadow(textRenderer, MetzClient.NAME, PANEL_X + 4, PANEL_Y + 3, 0xFFFFFFFF);

        int y = PANEL_Y + ROW_H + 2;
        for (Module m : ModuleManager.all()) {
            boolean hovered = inside(mouseX, mouseY, PANEL_X, y, PANEL_W, ROW_H);
            int color = m.isEnabled() ? ROW_ON : (hovered ? ROW_HOVER : ROW_OFF);
            ctx.fill(PANEL_X, y, PANEL_X + PANEL_W, y + ROW_H, color);
            ctx.drawTextWithShadow(textRenderer, m.getName(), PANEL_X + 4, y + 3, 0xFFFFFFFF);
            if (!m.getSettings().isEmpty()) {
                String arrow = m == expanded ? "-" : "+";
                ctx.drawTextWithShadow(textRenderer, arrow, PANEL_X + PANEL_W - 10, y + 3, 0xFFAAAAAA);
            }
            if (hovered) {
                if (leftClick) {
                    m.toggle();
                } else if (rightClick && !m.getSettings().isEmpty()) {
                    expanded = expanded == m ? null : m;
                }
            }
            y += ROW_H;

            if (m == expanded) {
                for (Setting s : m.getSettings()) {
                    boolean sHover = inside(mouseX, mouseY, PANEL_X, y, PANEL_W, SETTING_H);
                    ctx.fill(PANEL_X, y, PANEL_X + PANEL_W, y + SETTING_H, sHover ? ROW_HOVER : SETTING_BG);
                    ctx.drawTextWithShadow(textRenderer, s.getName(), PANEL_X + 8, y + 2, 0xFFCCCCCC);
                    String value = s.display();
                    ctx.drawTextWithShadow(textRenderer, value,
                            PANEL_X + PANEL_W - 6 - textRenderer.getWidth(value), y + 2, 0xFF55FFFF);
                    if (sHover) {
                        if (leftClick) {
                            s.adjust(1);
                        } else if (rightClick) {
                            s.adjust(-1);
                        }
                    }
                    y += SETTING_H;
                }
            }
        }

        // Tooltip for the hovered module.
        int ty = PANEL_Y + ROW_H + 2;
        for (Module m : ModuleManager.all()) {
            if (inside(mouseX, mouseY, PANEL_X, ty, PANEL_W, ROW_H)) {
                ctx.drawTextWithShadow(textRenderer, m.getDescription(), PANEL_X + PANEL_W + 8, ty + 3, 0xFFDDDDDD);
            }
            ty += ROW_H + (m == expanded ? SETTING_H * m.getSettings().size() : 0);
        }
    }

    private static boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }
}
