// Thanks to fabric wiki for this code (has been heavily modified)

package tameno.nom_inator.data_storage;

import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import tameno.nom_inator.Nominator;
import tameno.nom_inator.data_storage.PredData;
import tameno.nom_inator.data_storage.PreyData;
import tameno.nom_inator.utils.Utils;

import java.util.*;

public class StateSaverAndLoader extends PersistentState {
    private int nextUnusedPredId = 0;
    private Set<Integer> removedPredIds = new HashSet<>();
    private HashMap<UUID, PredData> preds = new HashMap<>();
    private HashMap<UUID, PreyData> preys = new HashMap<>();

    public void nom(UUID predUuid, UUID preyUuid) {
        if (!isPred(predUuid)) {
            Nominator.LOGGER.error("(StateSaverAndLoader) Tried to nom but pred isn't actually a pred!");
            return;
        }
        preds.get(predUuid).preys.add(preyUuid);
        if (preys.containsKey(preyUuid)) {
            // Recursive prey
            preys.get(preyUuid).predUuid = predUuid;
        } else {
            // Non recursive prey
            preys.put(preyUuid, new PreyData(predUuid));
        }
        markDirty();
    }

    public void unNom(UUID preyUuid) {
        if (!isPrey(preyUuid)) {
            Nominator.LOGGER.error("(StateSaverAndLoader) Tried to un-nom, but prey isn't actually a prey!");
            return;
        }
        PreyData prey = preys.get(preyUuid);
        if (!isPred(prey.predUuid)) {
            Nominator.LOGGER.error("(StateSaverAndLoader) Tried to un-nom, but pred isn't actually a pred!");
            return;
        }
        PredData pred = preds.get(prey.predUuid);
        if (preys.containsKey(prey.predUuid)) {
            // Recursive prey
            UUID parentPredUuid = preys.get(prey.predUuid).predUuid;
            if (!preds.containsKey(parentPredUuid)) {
                Nominator.LOGGER.error("(StateSaverAndLoader) Tried to un-nom, but parent pred isn't actually a pred!");
                return;
            }
            PredData parentPred = preds.get(parentPredUuid);
            prey.predUuid = parentPredUuid;
            pred.preys.remove(preyUuid);
            parentPred.preys.add(preyUuid);
        } else {
            // Non recursive prey
            preys.remove(preyUuid);
            pred.preys.remove(preyUuid);
        }
        markDirty();
    }

    public PredData getPred(UUID predUuid) {
        return preds.get(predUuid);
    }

    public PreyData getPrey(UUID preyUuid) {
        return preys.get(preyUuid);
    }

    public boolean isPred(UUID possiblePredUuid) {
        return preds.containsKey(possiblePredUuid);
    }

    public boolean isPrey(UUID possiblePreyUuid) {
        return preys.containsKey(possiblePreyUuid);
    }

    public Set<UUID> getAllPreds() {
        return preds.keySet();
    }

    public UUID getPredByName(String name) {
        for(UUID predUuid : preds.keySet()) {
            PredData pred = getPred(predUuid);
            if (pred.name.equals(name)) {
                return predUuid;
            }
        }
        return null;
    }

    public void registerNewPred(UUID predUuid, String predName) {
        if (isPred(predUuid)) {
            Nominator.LOGGER.error("(StateSaverAndLoader) Tried to register new pred, but pred is already a pred!");
            return;
        }
        int newId;
        if (removedPredIds.isEmpty()) {
            newId = nextUnusedPredId;
            nextUnusedPredId++;
        } else {
            newId = Utils.getMin(removedPredIds);
            removedPredIds.remove(newId);
        }
        preds.put(predUuid, new PredData(newId, predName));
        markDirty();
        //prettyPrintIds();
    }

    public void unRegisterPred(UUID predUuid) {
        PredData pred = getPred(predUuid);
        removedPredIds.add(pred.id);
        while (removedPredIds.contains(nextUnusedPredId - 1)) {
            removedPredIds.remove(nextUnusedPredId - 1);
            nextUnusedPredId--;
        }
        preds.remove(predUuid);
        markDirty();
        //prettyPrintIds();
    }

    public void removePrey(UUID preyUuid) {
        if (!isPrey(preyUuid)) {
            Nominator.LOGGER.error("(StateSaverAndLoader) Tried to remove prey, but prey isn't actually a prey!");
            return;
        }
        PreyData prey = preys.get(preyUuid);
        PredData pred = preds.get(prey.predUuid);
        preys.remove(preyUuid);
        pred.preys.remove(preyUuid);
        markDirty();
    }

    public void updatePredName(UUID predUuid, String newName) {
        if (!isPred(predUuid)) {
            Nominator.LOGGER.error("(StateSaverAndLoader) Tried update name of pred, but pred is not a pred!");
            return;
        }
        preds.get(predUuid).name = newName;
        markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        // nextUnusedPredId
        nbt.putInt("next_unused_pred_id", nextUnusedPredId);

        // removedPredIds
        nbt.putIntArray("removed_pred_ids", removedPredIds.stream().toList());

        // preds
        NbtCompound predsNbt = new NbtCompound();
        preds.forEach((UUID uuid, PredData predData) -> {
            predsNbt.put(uuid.toString(), predData.toNbt());
        });
        nbt.put("preds", predsNbt);

        // preys
        NbtCompound preysNbt = new NbtCompound();
        preys.forEach((UUID uuid, PreyData preyData) -> {
            preysNbt.put(uuid.toString(), preyData.toNbt());
        });
        nbt.put("preys", preysNbt);

        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag) {
        StateSaverAndLoader state = new StateSaverAndLoader();

        // nextUnusedPredId
        state.nextUnusedPredId = tag.getInt("next_unused_pred_id");

        // removedPredIds
        int[] removedPredIdsArray = tag.getIntArray("removed_pred_ids");
        for (int id: removedPredIdsArray) {
            state.removedPredIds.add(id);
        }

        // preds
        NbtCompound predsNbt = tag.getCompound("preds");
        predsNbt.getKeys().forEach(key -> {
            PredData predData = new PredData(predsNbt.getCompound(key));
            UUID uuid = UUID.fromString(key);
            state.preds.put(uuid, predData);
        });

        // preys
        NbtCompound preysNbt = tag.getCompound("preys");
        preysNbt.getKeys().forEach(key -> {
            PreyData preyData = new PreyData(preysNbt.getCompound(key));
            UUID uuid = UUID.fromString(key);
            state.preys.put(uuid, preyData);
        });

        return state;
    }

    public static StateSaverAndLoader createNew() {
        return new StateSaverAndLoader();
    }

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        // (Note: arbitrary choice to use the overworld. Any world works)
        PersistentStateManager persistentStateManager = server.getOverworld().getPersistentStateManager();

        // The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
        // 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.

        return persistentStateManager.getOrCreate(
                StateSaverAndLoader::createFromNbt,
                StateSaverAndLoader::createNew,
                Nominator.MOD_ID
        );
    }

    public void prettyPrintIds() {
        String output = "";
        output += "\n           Pred IDs: ";
        for (PredData pred : preds.values()) {
            output += pred.id + " ";
        }
        output += "\n   Removed pred IDs: ";
        for (int id : removedPredIds) {
            output += id + " ";
        }
        output += "\nNext unused pred ID: " + nextUnusedPredId + "\n";
        Nominator.LOGGER.info(output);
    }

    /*
    // THIS FUNCTION SHOULD ONLY BE USED FOR TESTING!!!
    public void unRegisterRandomPred() {
        UUID predToRemove = null;
        int iterationCount = new Random().nextInt(preds.size());
        int i = 0;
        for (UUID uuid : preds.keySet()) {
            if (i == iterationCount) {
                predToRemove = uuid;
            }
            i++;
        }
        unRegisterPred(predToRemove);
    }
    */
}
