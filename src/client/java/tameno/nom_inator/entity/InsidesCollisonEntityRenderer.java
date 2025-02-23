package tameno.nom_inator.entity;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import tameno.nom_inator.entity.custom.InsidesCollisionEntity;

public class InsidesCollisonEntityRenderer extends EntityRenderer<InsidesCollisionEntity> {
    public InsidesCollisonEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(InsidesCollisionEntity entity) {
        return null;
    }
}
