package tameno.nom_inator.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import tameno.nom_inator.Nominator;
import tameno.nom_inator.entity.custom.InsidesEntity;

public class InsidesEntityRenderer extends EntityRenderer<InsidesEntity> {

    public static final Identifier TEXTURE = new Identifier(Nominator.MOD_ID, "textures/entity/insides.png");
    private InsidesEntityModel model;

    public InsidesEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        model = new InsidesEntityModel<>(context.getPart(ModModelLayers.INSIDES));
    }

    @Override
    public void render(
            InsidesEntity entity,
            float yaw,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light
    ) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
        this.model.render(
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE)),
                light,
                OverlayTexture.DEFAULT_UV,
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }


    @Override
    public Identifier getTexture(InsidesEntity entity) {
        return TEXTURE;
    }
}
