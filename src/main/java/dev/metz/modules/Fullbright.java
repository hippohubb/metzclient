package dev.metz.modules;

import org.lwjgl.glfw.GLFW;

import dev.metz.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

/** Full brightness via a client-side night vision effect. */
public class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", "See clearly in the dark (client-side night vision).", GLFW.GLFW_KEY_X);
    }

    @Override
    protected void onDisable() {
        ClientPlayerEntity p = mc.player;
        if (p != null) {
            p.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        StatusEffectInstance current = p.getStatusEffect(StatusEffects.NIGHT_VISION);
        if (current == null || current.getDuration() < 400) {
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1000, 0, false, false, false));
        }
    }
}
