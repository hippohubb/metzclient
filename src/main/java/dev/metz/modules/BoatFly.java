package dev.metz.modules;

import org.lwjgl.glfw.GLFW;

import dev.metz.module.Module;
import dev.metz.module.MoveUtil;
import dev.metz.module.Setting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.util.math.Vec3d;

public class BoatFly extends Module {
    private final Setting speed = addSetting("Speed", 1.0, 0.1, 5.0, 0.1);
    private final Setting vertical = addSetting("Vertical", 0.4, 0.1, 3.0, 0.1);

    private Entity lastBoat;

    public BoatFly() {
        super("BoatFly", "Fly while riding a boat: jump up, sprint key down, otherwise hover.", GLFW.GLFW_KEY_B);
    }

    @Override
    public String getHudInfo() {
        return speed.display();
    }

    @Override
    protected void onDisable() {
        releaseBoat();
    }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        Entity vehicle = p.getVehicle();
        if (!(vehicle instanceof AbstractBoatEntity)) {
            releaseBoat();
            return;
        }
        lastBoat = vehicle;

        Vec3d dir = MoveUtil.inputDirection(p).multiply(speed.get() * 0.5);
        double vy = 0;
        if (mc.options.jumpKey.isPressed()) {
            vy += vertical.get();
        }
        if (mc.options.sprintKey.isPressed()) {
            vy -= vertical.get();
        }

        vehicle.setNoGravity(true);
        vehicle.setYaw(p.getYaw());
        vehicle.setVelocity(dir.x, vy, dir.z);
    }

    private void releaseBoat() {
        if (lastBoat != null) {
            lastBoat.setNoGravity(false);
            lastBoat = null;
        }
    }
}
