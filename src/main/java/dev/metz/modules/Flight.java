package dev.metz.modules;

import org.lwjgl.glfw.GLFW;

import dev.metz.module.Module;
import dev.metz.module.MoveUtil;
import dev.metz.module.Setting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Flight extends Module {
    private final Setting speed = addSetting("Speed", 1.0, 0.1, 5.0, 0.1);
    private final Setting vertical = addSetting("Vertical", 0.5, 0.1, 3.0, 0.1);

    public Flight() {
        super("Flight", "Fly freely: jump goes up, sneak goes down.", GLFW.GLFW_KEY_G);
    }

    @Override
    public String getHudInfo() {
        return speed.display();
    }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        if (p.hasVehicle()) {
            return;
        }
        Vec3d dir = MoveUtil.inputDirection(p).multiply(speed.get() * 0.5);
        double vy = 0;
        if (mc.options.jumpKey.isPressed()) {
            vy += vertical.get();
        }
        if (mc.options.sneakKey.isPressed()) {
            vy -= vertical.get();
        }
        p.setVelocity(dir.x, vy, dir.z);
    }
}
