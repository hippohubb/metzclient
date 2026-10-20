package dev.metz.modules;

import dev.metz.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;

public class AutoSprint extends Module {
    public AutoSprint() {
        super("AutoSprint", "Sprint automatically whenever you walk forward.", -1);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        if (mc.options.forwardKey.isPressed()
                && !p.isSneaking()
                && !p.horizontalCollision
                && p.getHungerManager().getFoodLevel() > 6) {
            p.setSprinting(true);
        }
    }
}
