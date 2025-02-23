package tameno.nom_inator.utils;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import tameno.nom_inator.Nominator;

import java.util.Set;
import java.util.UUID;

abstract public class Utils {

    // these parameters are in chunks, they're currently set so each player's insides occupies 1 region file.
    public static final int INSIDES_SEPARATION = 32;
    public static final int INISDES_OFFSET = 16;

    public static Entity getEntityByUuid(UUID uuid, MinecraftServer server) {

        for (ServerWorld world : server.getWorlds()) {
            Entity entity = world.getEntity(uuid);
            if (entity != null) {
                return entity;
            }
        }

        Nominator.LOGGER.warn("(Utils.getEntityByUuid) Entity not found");

        return null;
    }

    // This function was written by AI. I was having fun doing it myself when suddenly a friend of mine went "here, I
    // asked ChatGPT" and had exactly what I was looking for. Apparently the AI got it first try in about a minute.
    // This gave me an existential crisis.
    /**
     * Returns the (x,y) position on an outward square spiral for the given non-negative index n.
     *
     * Spiral layout (counterclockwise around origin):
     *
     *    y=2  .  .  .  .
     *    y=1  .  2  3  .
     *    y=0  .  1  0  .
     *    y=-1 .  .  .  .
     *
     * The exact layout can be flipped or rotated with minor changes if desired.
     */
    public static Vec2i spiralXY(int n) {
        // Special case: center of the spiral
        if (n == 0) {
            return new Vec2i(0, 0);
        }

        // 1) Determine the "ring" r where n lies.
        //    Each ring r >= 1 has 8*r elements:
        //      ring=1: indices 1..8
        //      ring=2: indices 9..24, etc.
        int r = (int) Math.ceil((Math.sqrt(n + 1) - 1) / 2.0);

        // 2) The first index on ring r (the "base") is (2*(r-1)+1)^2
        int base = (int) Math.pow(2 * (r - 1) + 1, 2);  // e.g. ring=1 => base=1, ring=2 => base=9, etc.

        // 3) Offset of n into ring r
        int offset = n - base;

        // Each ring has 4 sides, each of length sideLen = 2*r
        int sideLen = 2 * r;

        // Which side of the ring? (0..3)
        int side = offset / sideLen;

        // Position along that side (0..sideLen-1)
        int pos = offset % sideLen;

        // 4) Convert (side, pos) to (x, y)
        int x = 0, y = 0;
        switch (side) {
            case 0:
                // Right edge, going upward
                x = r;
                y = (-r + 1 + pos);
                break;
            case 1:
                // Top edge, going left
                x = (r - 1 - pos);
                y = r;
                break;
            case 2:
                // Left edge, going down
                x = -r;
                y = (r - 1 - pos);
                break;
            case 3:
                // Bottom edge, going right
                x = (-r + 1 + pos);
                y = -r;
                break;
            default:
                // Should never happen, but fall back
                x = 0;
                y = 0;
        }

        return new Vec2i(x, y);
    }

    public static ChunkPos getInsidesChunkPos(int predId) {
        Vec2i point = spiralXY(predId);
        point = point.multiply(INSIDES_SEPARATION);
        point = point.add(new Vec2i(INISDES_OFFSET, INISDES_OFFSET));
        return new ChunkPos(point.x, point.y);
    }

    public static int getMin(Set<Integer> set) {
        if (set.isEmpty()) {
            throw new RuntimeException("Provided set is empty!");
        }
        int min = Integer.MAX_VALUE;
        for (int i : set) {
            if (i < min) {
                min = i;
            }
        }
        return min;
    }

    public static int getMax(Set<Integer> set) {
        if (set.isEmpty()) {
            throw new RuntimeException("Provided set is empty!");
        }
        int max = Integer.MIN_VALUE;
        for (int i : set) {
            if (i > max) {
                max = i;
            }
        }
        return max;
    }
}
