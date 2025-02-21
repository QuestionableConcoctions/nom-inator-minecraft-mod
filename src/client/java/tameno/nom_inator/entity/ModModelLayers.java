package tameno.nom_inator.entity;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import tameno.nom_inator.Nominator;

public class ModModelLayers {
    public static final EntityModelLayer INSIDES = new EntityModelLayer(
            new Identifier(Nominator.MOD_ID, "insides"),
            "main"
    );
}
