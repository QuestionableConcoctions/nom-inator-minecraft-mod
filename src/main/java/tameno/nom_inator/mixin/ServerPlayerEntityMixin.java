package tameno.nom_inator.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tameno.nom_inator.Nominator;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(method = "onDeath", at = @At("TAIL"))
    public void onDeathInject(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity thisPlayer = (ServerPlayerEntity)(Object)this;
        MinecraftServer server = thisPlayer.getServer();
        if (server == null) return;
        Nominator.unNomAll(thisPlayer, server);
    }
}
