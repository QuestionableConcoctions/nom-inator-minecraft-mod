package tameno.nom_inator.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tameno.nom_inator.Nominator;
import tameno.nom_inator.PreyData;
import tameno.nom_inator.StateSaverAndLoader;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "remove", at = @At("TAIL"))
    public void removeInject(Entity.RemovalReason reason, CallbackInfo ci) {
        if (!reason.shouldDestroy()) return;
        Entity thisEntity = (Entity)(Object)this;
        MinecraftServer server = thisEntity.getServer();
        if (server == null) return;
        StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);
        if (!state.isPrey(thisEntity.getUuid())) return;
        PreyData preyData = state.getPrey(thisEntity.getUuid());
        state.removePrey(thisEntity.getUuid());
        Nominator.unloadInsidesIfNecessary(preyData.predUuid, server);
    }
    @Inject(method = "tick", at = @At("HEAD"))
    public void tickInject(CallbackInfo ci) {
        Entity thisEntity = (Entity)(Object)this;
        if (thisEntity.getWorld().getRegistryKey().equals(Nominator.INSIDES_WORLD_REGISTRY_KEY)) {
            Nominator.LOGGER.info("tick!");
        }
    }
}
