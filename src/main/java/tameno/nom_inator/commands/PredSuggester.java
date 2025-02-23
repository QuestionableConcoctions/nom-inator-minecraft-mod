package tameno.nom_inator.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import tameno.nom_inator.data_storage.StateSaverAndLoader;

import java.util.concurrent.CompletableFuture;

public class PredSuggester implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(
            CommandContext<ServerCommandSource> context,
            SuggestionsBuilder builder
    ) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);
        state.getAllPreds().forEach((predUuid) -> {
            builder.suggest(state.getPred(predUuid).name);
        });
        return builder.buildFuture();
    }
}
