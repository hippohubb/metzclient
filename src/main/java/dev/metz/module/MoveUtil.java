package dev.metz.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public final class MoveUtil {
    private MoveUtil() {
    }

    public static boolean isMoving() {
        var o = MinecraftClient.getInstance().options;
        return o.forwardKey.isPressed() || o.backKey.isPressed() || o.leftKey.isPressed() || o.rightKey.isPressed();
    }

    /** Horizontal unit direction (or zero) from the movement keys, relative to where the player looks. */
    public static Vec3d inputDirection(ClientPlayerEntity player) {
        var o = MinecraftClient.getInstance().options;
        double forward = (o.forwardKey.isPressed() ? 1 : 0) - (o.backKey.isPressed() ? 1 : 0);
        double strafe = (o.leftKey.isPressed() ? 1 : 0) - (o.rightKey.isPressed() ? 1 : 0);
        if (forward == 0 && strafe == 0) {
            return Vec3d.ZERO;
        }
        double yaw = Math.toRadians(player.getYaw());
        double x = -Math.sin(yaw) * forward + Math.cos(yaw) * strafe;
        double z = Math.cos(yaw) * forward + Math.sin(yaw) * strafe;
        double len = Math.sqrt(x * x + z * z);
        return new Vec3d(x / len, 0, z / len);
    }
}
