package dev.metz.modules;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

import dev.metz.module.Module;
import dev.metz.module.Setting;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

/**
 * Looks through the chunks the server has sent you for clusters of player-placed
 * storage and utility blocks (chests, barrels, shulkers, furnaces, ...) and reports
 * chunks that look like bases. It only sees chunks within your render distance.
 */
public class BaseFinder extends Module {
    public record Hit(int x, int z, int score) {
    }

    private final Setting radius = addSetting("Radius (chunks)", 12, 2, 32, 1);
    private final Setting minScore = addSetting("Min score", 6, 1, 50, 1);

    private final Set<ChunkPos> announced = new HashSet<>();
    private List<Hit> hits = new ArrayList<>();
    private ClientWorld lastWorld;
    private int timer;

    public BaseFinder() {
        super("BaseFinder", "Detect chunks with lots of chests, shulkers, furnaces and other base blocks.", GLFW.GLFW_KEY_H);
    }

    public List<Hit> getHits() {
        return hits;
    }

    @Override
    public String getHudInfo() {
        return Integer.toString(hits.size());
    }

    @Override
    protected void onEnable() {
        timer = 0;
        announced.clear();
        hits = new ArrayList<>();
    }

    @Override
    protected void onDisable() {
        announced.clear();
        hits = new ArrayList<>();
    }

    @Override
    public void onTick() {
        if (mc.world != lastWorld) {
            lastWorld = mc.world;
            announced.clear();
            hits = new ArrayList<>();
            timer = 0;
        }
        if (timer-- > 0) {
            return;
        }
        timer = 40;
        scan();
    }

    private void scan() {
        ClientWorld world = mc.world;
        ChunkPos center = mc.player.getChunkPos();
        int r = radius.getInt();
        int threshold = minScore.getInt();

        Map<ChunkPos, Integer> found = new HashMap<>();
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                WorldChunk chunk = world.getChunkManager().getWorldChunk(center.x + dx, center.z + dz);
                if (chunk == null) {
                    continue;
                }
                int score = 0;
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    score += weight(be);
                }
                if (score >= threshold) {
                    found.put(chunk.getPos(), score);
                }
            }
        }

        List<Hit> next = new ArrayList<>();
        for (Map.Entry<ChunkPos, Integer> e : found.entrySet()) {
            ChunkPos pos = e.getKey();
            Hit hit = new Hit(pos.getCenterX(), pos.getCenterZ(), e.getValue());
            next.add(hit);
            if (announced.add(pos)) {
                mc.player.sendMessage(Text.literal("[Metz] Possible base near X " + hit.x() + " Z " + hit.z()
                        + " (score " + hit.score() + ")"), false);
            }
        }
        // Closest to the player first.
        int px = mc.player.getBlockX();
        int pz = mc.player.getBlockZ();
        next.sort(Comparator.comparingLong(h -> {
            long ddx = h.x() - px;
            long ddz = h.z() - pz;
            return ddx * ddx + ddz * ddz;
        }));
        hits = next;
    }

    private static int weight(BlockEntity be) {
        if (be instanceof ShulkerBoxBlockEntity) {
            return 3;
        }
        if (be instanceof ChestBlockEntity || be instanceof BarrelBlockEntity
                || be instanceof AbstractFurnaceBlockEntity || be instanceof BrewingStandBlockEntity) {
            return 2;
        }
        if (be instanceof HopperBlockEntity || be instanceof DispenserBlockEntity || be instanceof BedBlockEntity) {
            return 1;
        }
        return 0;
    }
}
