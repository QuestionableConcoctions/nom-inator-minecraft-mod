package tameno.nom_inator.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import com.tameno.snatched.entity.custom.HandSeatEntity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tameno.nom_inator.Nominator;

@Mixin(value = HandSeatEntity.class, remap = false)
abstract public class HandSeatEntityMixin extends Entity {
    public HandSeatEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "updateHandPosition", at = @At("HEAD"))
    public void updateHandPosition(CallbackInfo ci) {
    }
}
