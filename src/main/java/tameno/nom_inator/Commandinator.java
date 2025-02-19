package tameno.nom_inator;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

import java.util.UUID;

public abstract class Commandinator {

    public static void registerCommands() {

        // makepred <player>
        CommandRegistrationCallback.EVENT.register((
                CommandDispatcher<ServerCommandSource> commandDispatcher,
                CommandRegistryAccess commandRegistryAccess,
                CommandManager.RegistrationEnvironment registrationEnvironment
        ) -> commandDispatcher.register(
            CommandManager.literal("makepred")
                .requires((ServerCommandSource source) -> source.hasPermissionLevel(1))
                .executes((CommandContext<ServerCommandSource> context) -> {
                    MinecraftServer server = context.getSource().getServer();
                    PlayerEntity player = context.getSource().getPlayer();
                    if (player == null) {
                        throw new SimpleCommandExceptionType(Text.literal(
                        "Invalid player."
                        )).create();
                    }
                    return makePred(player, server, context);
                })
                .then(
                    CommandManager.argument("player", StringArgumentType.string())
                        .suggests(new PlayerSuggester())
                        .executes((CommandContext<ServerCommandSource> context) -> {
                            MinecraftServer server = context.getSource().getServer();
                            String playerName = StringArgumentType.getString(context, "player");
                            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
                            if (player == null) {
                                throw new SimpleCommandExceptionType(Text.literal(
                                        "Couldn't find player \"" + playerName + "\".")
                                ).create();
                            }
                            return makePred(player, server, context);
                        })
                )
            )
        );

        //gotoinsides <pred>
        CommandRegistrationCallback.EVENT.register((
                        CommandDispatcher<ServerCommandSource> commandDispatcher,
                        CommandRegistryAccess commandRegistryAccess,
                        CommandManager.RegistrationEnvironment registrationEnvironment
            ) -> commandDispatcher.register(
                CommandManager.literal("gotoinsides")
                    .requires((ServerCommandSource source) -> source.hasPermissionLevel(1))
                    .executes((CommandContext<ServerCommandSource> context) -> {
                        PlayerEntity player = context.getSource().getPlayer();
                        if (player == null) {
                            throw new SimpleCommandExceptionType(Text.literal(
                                    "Invalid player."
                            )).create();
                        }
                        MinecraftServer server = context.getSource().getServer();
                        StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);
                        if (!state.isPred(player.getUuid())) {
                            throw new SimpleCommandExceptionType(Text.literal(
                                    player.getEntityName() + " is not a pred!"
                            )).create();
                        }
                        PredData predData = state.getPred(player.getUuid());
                        return goToInsides(player, predData, server, context);
                    })
                    .then(
                        CommandManager.argument("pred", StringArgumentType.string())
                            .suggests(new PredSuggester())
                            .executes((CommandContext<ServerCommandSource> context) -> {
                                PlayerEntity player = context.getSource().getPlayer();
                                if (player == null) {
                                    throw new SimpleCommandExceptionType(Text.literal(
                                            "Invalid player."
                                    )).create();
                                }
                                String predName = StringArgumentType.getString(context, "pred");
                                MinecraftServer server = context.getSource().getServer();
                                StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);
                                UUID predUuid = state.getPredByName(predName);
                                if (predUuid == null) {
                                    throw new SimpleCommandExceptionType(Text.literal(
                                            "Couldn't find " + predName + ". Are they are a pred?"
                                    )).create();
                                }
                                PredData predData = state.getPred(predUuid);
                                return goToInsides(player, predData, server, context);
                            })
                    )
            )
        );

    }

    // makepred <player>
    public static int makePred(
            PlayerEntity player,
            MinecraftServer server,
            CommandContext<ServerCommandSource> context
    ) throws CommandSyntaxException {
        StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);
        if (state.isPred(player.getUuid())) {
            throw new SimpleCommandExceptionType(Text.literal(
                    player.getEntityName() + " is alredy a pred!")
            ).create();
        }
        Nominator.registerPred(player, server);
        context.getSource().sendFeedback(
                () -> Text.literal(player.getEntityName() + " is now a pred. Happy nomming! \uD83E\uDD71\uD83D\uDE0B"),
                true
        );
        return 1;
    }

    //gotoinsides <pred>
    public static int goToInsides(
            PlayerEntity player,
            PredData predData,
            MinecraftServer server,
            CommandContext<ServerCommandSource> context
    ) throws CommandSyntaxException {
        StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);
        ServerWorld insides_world = server.getWorld(Nominator.INSIDES_WORLD_REGISTRY_KEY);
        player = FabricDimensions.teleport(
                player,
                insides_world,
                new TeleportTarget(
                        predData.config.enterPos,
                        new Vec3d(0.0, 0.0, 0.0),
                        0.0f,
                        0.0f
                )
        );
        context.getSource().sendFeedback(
                () -> Text.literal("Went to " + predData.name + "'s insides."),
                true
        );
        return 1;
    }
}
