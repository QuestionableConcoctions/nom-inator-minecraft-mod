package tameno.nom_inator;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;

import java.util.HashSet;
import java.util.Set;

public class InsidesLoader {
    private static final int LOAD_RADIUS = 3;
    private ServerWorld insidesWorld;
    private Set<Integer> loadedInsides = new HashSet<>();

    public InsidesLoader(ServerWorld newInsidesWorld) {
        insidesWorld = newInsidesWorld;
    }

    public void loadInsides(int predId) {
        if (loadedInsides.contains(predId)) return;
        ChunkPos insidesCenter = Utils.getInsidesChunkPos(predId);
        for (int x = -LOAD_RADIUS; x < LOAD_RADIUS + 1; x++) {
            for (int z = -LOAD_RADIUS; z < LOAD_RADIUS + 1; z++) {
                insidesWorld.setChunkForced(insidesCenter.x + x, insidesCenter.z + z, true);
            }
        }
        loadedInsides.add(predId);
        Nominator.LOGGER.info("Insides loaded for pred " + predId);
    }

    public void unloadInsides(int predId) {
        if (!loadedInsides.contains(predId)) return;
        ChunkPos insidesCenter = Utils.getInsidesChunkPos(predId);
        for (int x = -LOAD_RADIUS; x < LOAD_RADIUS + 1; x++) {
            for (int z = -LOAD_RADIUS; z < LOAD_RADIUS + 1; z++) {
                insidesWorld.setChunkForced(insidesCenter.x + x, insidesCenter.z + z, false);
            }
        }
        loadedInsides.remove(predId);
        Nominator.LOGGER.info("Insides un-loaded for pred " + predId);
    }
}
