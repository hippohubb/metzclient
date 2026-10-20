package dev.metz.modules;

import org.lwjgl.glfw.GLFW;

import dev.metz.module.Module;
import dev.metz.module.MoveUtil;
import dev.metz.module.Setting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Speed extends Module {
    private final Setting speed = addSetting("Speed", 1.5, 0.5, 5.0, 0.1);

    public Speed() {
        super("Speed", "Move faster on the ground and in the air.", GLFW.GLFW_KEY_Z);
    }

    @Override
    public String getHudInfo() {
        return speed.display();
    }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        if (p.hasVehicle() || p.isTouchingWater() || p.isClimbing() || !MoveUtil.isMoving()) {
            return;
        }
        Vec3d dir = MoveUtil.inputDirection(p).multiply(speed.get() * 0.2);
        Vec3d v = p.getVelocity();
        p.setVelocity(dir.x, v.y, dir.z);
    }
}
