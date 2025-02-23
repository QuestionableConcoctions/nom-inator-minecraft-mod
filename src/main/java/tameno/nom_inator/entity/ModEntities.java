package tameno.nom_inator.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import tameno.nom_inator.Nominator;
import tameno.nom_inator.entity.custom.InsidesCollisionEntity;
import tameno.nom_inator.entity.custom.InsidesEntity;

public class ModEntities {

    public static EntityType<InsidesEntity> INSIDES = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Nominator.MOD_ID, "insides"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, InsidesEntity::new)
                    .dimensions(EntityDimensions.changing(16.0f, 32.0f))
                    .build()
    );

    public static EntityType<InsidesCollisionEntity> INSIDES_COLLISION = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Nominator.MOD_ID, "insides_collision"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, InsidesCollisionEntity::new)
                    .dimensions(EntityDimensions.changing(1.0f, 1.0f))
                    .build()
    );

    public static void registerModEntities() {
        Nominator.LOGGER.info("Registering entities for " + Nominator.MOD_ID);
    }
}
