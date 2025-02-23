package tameno.nom_inator.data_storage;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PreyData {
    public UUID predUuid;
    public PreyData(UUID newPredUuid) {
        predUuid = newPredUuid;
    }

    public PreyData(NbtCompound nbt) {
        predUuid = nbt.getUuid("pred");
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("pred", predUuid);
        return nbt;
    }
}
