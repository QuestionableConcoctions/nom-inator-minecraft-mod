package tameno.nom_inator;


import com.tameno.snatched.Snatcher;
import com.tameno.snatched.entity.custom.HandSeatEntity;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tameno.nom_inator.commands.Commandinator;
import tameno.nom_inator.data_storage.PredData;
import tameno.nom_inator.data_storage.PreyData;
import tameno.nom_inator.data_storage.StateSaverAndLoader;
import tameno.nom_inator.entity.ModEntities;
import tameno.nom_inator.utils.InsidesLoader;
import tameno.nom_inator.utils.Utils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class Nominator implements ModInitializer {
    public static final String MOD_ID = "nom-inator";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final RegistryKey<World> INSIDES_WORLD_REGISTRY_KEY = RegistryKey.of(
            RegistryKeys.WORLD,
            new Identifier(MOD_ID, "insides")
    );
    public static final Identifier USE_AIR_PACKET_ID = new Identifier(MOD_ID, "used_air");
    public static final Identifier DROP_NOTHING_PACKET_ID = new Identifier(MOD_ID, "dropped_nothing");
    public static InsidesLoader insidesLoader;

    @Override
    public void onInitialize() {

        Commandinator.registerCommands();
        ModEntities.registerModEntities();

        ServerWorldEvents.LOAD.register((MinecraftServer server, ServerWorld world) -> {
            if (world.getRegistryKey().equals(INSIDES_WORLD_REGISTRY_KEY)) {
                insidesLoader = new InsidesLoader(world);
            }
        });

        ServerPlayConnectionEvents.JOIN.register((
                ServerPlayNetworkHandler handler,
                PacketSender sender,
                MinecraftServer server
        ) -> {
            StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);
            ServerPlayerEntity player = handler.getPlayer();
            UUID playerUuid = player.getUuid();
            if (state.isPred(playerUuid)) {
                PredData predData = state.getPred(playerUuid);
                String playerName = player.getEntityName();
                if (!playerName.equals(predData.name)) {
                    state.updatePredName(playerUuid, playerName);
                }
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((ServerPlayNetworkHandler handler, MinecraftServer server) -> {
            StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);
            ServerPlayerEntity player = handler.getPlayer();
            UUID playerUuid = player.getUuid();
            if (state.isPred(playerUuid)) {
                unNomAll(player, entity -> entity instanceof PlayerEntity, server);
                PredData predData = state.getPred(playerUuid);
                insidesLoader.unloadInsides(predData.id);
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(Nominator.USE_AIR_PACKET_ID,
            (
                    server,
                    player,
                    handler,
                    buf,
                    responseSender
            ) -> {
                if (player instanceof Snatcher snatcher) {
                    HandSeatEntity handSeat = snatcher.snatched$getCurrentHandSeat(player.getWorld());
                    if (handSeat == null) return;
                    Entity snatchee = handSeat.getFirstPassenger();
                    if (snatchee == null) return;
                    StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);
                    if (!state.isPred(player.getUuid())) return;
                    server.execute(() -> {
                        nom(player, snatchee, server);
                    });
                }
        });

        ServerPlayNetworking.registerGlobalReceiver(Nominator.DROP_NOTHING_PACKET_ID,
            (
                    server,
                    player,
                    handler,
                    buf,
                    responseSender
            ) -> {

                StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);

                if (!state.isPred(player.getUuid())) return;

                server.execute(() -> {
                    unNomAll(player, server);
                });

            });
    }

    public static void registerPred(PlayerEntity player, MinecraftServer server) {
        StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);
        if (state.isPred(player.getUuid())) {
            LOGGER.error("Tried to register pred, but pred is already a pred!");
            return;
        }
        state.registerNewPred(player.getUuid(), player.getEntityName());
        PredData predData = state.getPred(player.getUuid());
        prepareInsides(predData.id, server);
    }

    public static void unloadInsidesIfNecessary(UUID predUuid, MinecraftServer server) {
        StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);
        PredData predData = state.getPred(predUuid);
        if (predData.preys.isEmpty()) {
            insidesLoader.unloadInsides(predData.id);
        }
    }

    // This function MUST run on the main server thread. In practice, this means using server.execute.
    private static void nom(PlayerEntity pred, Entity prey, MinecraftServer server) {
        StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);
        if (!state.isPred(pred.getUuid())) {
            LOGGER.error("Tried to nom, but pred is not actually pred!");
            return;
        }
        PredData predData = state.getPred(pred.getUuid());
        prey.dismountVehicle();
        ServerWorld insides_world = server.getWorld(INSIDES_WORLD_REGISTRY_KEY);
        prey = FabricDimensions.teleport(
                prey,
                insides_world,
                new TeleportTarget(
                        predData.config.enterPos,
                        new Vec3d(0.0, 0.0, 0.0),
                        0.0f,
                        0.0f
                )
        );
        insidesLoader.loadInsides(predData.id);
        state.nom(pred.getUuid(), prey.getUuid());
    }

    public static void unNom(Entity prey, MinecraftServer server) {
        StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);
        if (!state.isPrey(prey.getUuid())) {
            LOGGER.error("Tried to un-nom, but prey is not actually prey!");
            return;
        }
        prey.dismountVehicle();
        PreyData preyData = state.getPrey(prey.getUuid());
        Entity pred = Utils.getEntityByUuid(preyData.predUuid, server);
        prey = FabricDimensions.teleport(
                prey,
                (ServerWorld) pred.getWorld(),
                new TeleportTarget(
                        pred.getPos(),
                        new Vec3d(0.0, 0.0, 0.0),
                        0.0f,
                        0.0f
                )
        );
        state.unNom(prey.getUuid());
        unloadInsidesIfNecessary(preyData.predUuid, server);
    }

    public static void unNomAll(PlayerEntity pred, Predicate<Entity> filter, MinecraftServer server) {
        StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);
        if (!state.isPred(pred.getUuid())) {
            LOGGER.error("Tried to un-nom all, but pred is not a pred!");
            return;
        }
        // We clone this to avoid modifying the original as we loop through it.
        Set<UUID> preysClone = new HashSet<>(state.getPred(pred.getUuid()).preys);
        preysClone.forEach((UUID preyUuid) -> {
            Entity prey = Utils.getEntityByUuid(preyUuid, server);
            if (prey != null && filter.test(prey)) {
                unNom(prey, server);
            }
        });
    }

    public static void unNomAll(PlayerEntity pred, MinecraftServer server) {
        unNomAll(pred, entity -> true, server);
    }

    private static void prepareInsides(int predId, MinecraftServer server) {
        ServerWorld insidesWorld = server.getWorld(INSIDES_WORLD_REGISTRY_KEY);
        ChunkPos insidesChunkPos = Utils.getInsidesChunkPos(predId);
        insidesWorld.setBlockState(insidesChunkPos.getBlockPos(0, 127, 0), Blocks.BEDROCK.getDefaultState());
        insidesWorld.setBlockState(insidesChunkPos.getBlockPos(-1, 127, 0), Blocks.BEDROCK.getDefaultState());
        insidesWorld.setBlockState(insidesChunkPos.getBlockPos(0, 127, -1), Blocks.BEDROCK.getDefaultState());
        insidesWorld.setBlockState(insidesChunkPos.getBlockPos(-1, 127, -1), Blocks.BEDROCK.getDefaultState());
    }
}