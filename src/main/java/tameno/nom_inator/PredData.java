package tameno.nom_inator;

import net.minecraft.nbt.*;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PredData {
    public int id;
    public String name;
    public InsidesConfig config;
    public Set<UUID> preys = new HashSet<>();
    public PredData(int newId, String newName) {
        id = newId;
        name = newName;
        config = new InsidesConfig(id);
    }

    public PredData(NbtCompound nbt) {
        id = nbt.getInt("id");
        name = nbt.getString("name");
        config = new InsidesConfig(nbt.getCompound("config"));
        NbtList preysNbt = nbt.getList("preys", NbtElement.INT_ARRAY_TYPE);
        preysNbt.forEach((prey) -> {
            preys.add(NbtHelper.toUuid(prey));
        });
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("id", id);
        nbt.putString("name", name);
        nbt.put("config", config.toNbt());
        NbtList preysNbt = new NbtList();
        preys.forEach((UUID prey) -> {
            preysNbt.add(NbtHelper.fromUuid(prey));
        });
        nbt.put("preys", preysNbt);
        return nbt;
    }
}
