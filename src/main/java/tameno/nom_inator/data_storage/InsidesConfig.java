package tameno.nom_inator.data_storage;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import tameno.nom_inator.utils.Utils;

public class InsidesConfig {
    public Vec3d enterPos;

    public InsidesConfig(int predId) {
        ChunkPos insidesChunkPos = Utils.getInsidesChunkPos(predId);
        enterPos = new Vec3d(
                insidesChunkPos.x * 16,
                128.0,
                insidesChunkPos.z * 16
        );
    }

    public InsidesConfig(NbtCompound nbt) {
        enterPos = new Vec3d(
                nbt.getDouble("enter_pos_x"),
                nbt.getDouble("enter_pos_y"),
                nbt.getDouble("enter_pos_z")
        );
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putDouble("enter_pos_x", enterPos.getX());
        nbt.putDouble("enter_pos_y", enterPos.getY());
        nbt.putDouble("enter_pos_z", enterPos.getZ());
        return nbt;
    }
}
