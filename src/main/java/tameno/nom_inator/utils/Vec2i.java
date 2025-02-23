package tameno.nom_inator.utils;

import net.minecraft.util.math.BlockPos;

public class Vec2i {
    int x;
    int y;

    public Vec2i(int newX, int newY) {
        x = newX;
        y = newY;
    }

    public Vec2i add(Vec2i vec) {
        return new Vec2i(x + vec.x, y + vec.y);
    }

    public Vec2i multiply(int factor) {
        return new Vec2i(x * factor, y * factor);
    }

    public BlockPos toBlockPos(int height) {
        return new BlockPos(x, height, y);
    }
}
