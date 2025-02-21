package tameno.nom_inator.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import tameno.nom_inator.entity.custom.InsidesEntity;

// Made with Blockbench 4.12.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class InsidesEntityModel<T extends InsidesEntity> extends EntityModel<T> {
	private final ModelPart root;
	private final ModelPart jaw;
	private final ModelPart human_teeth_bottom;
	private final ModelPart human_teeth_top;
	private final ModelPart outside;
	public InsidesEntityModel(ModelPart root) {
		this.root = root.getChild("root");
		this.jaw = this.root.getChild("jaw");
		this.human_teeth_bottom = this.jaw.getChild("human_teeth_bottom");
		this.human_teeth_top = this.root.getChild("human_teeth_top");
		this.outside = this.root.getChild("outside");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create().uv(400, 392).cuboid(-32.0F, -123.2F, -71.2F, 64.0F, 0.0F, 72.0F, new Dilation(0.0F))
		.uv(416, 464).cuboid(-32.0F, -123.2F, 0.8F, 16.0F, 64.0F, 0.0F, new Dilation(0.0F))
		.uv(0, 288).cuboid(-16.0F, -123.2F, 0.8F, 32.0F, 320.0F, 32.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-72.0F, 196.8F, -55.2F, 144.0F, 144.0F, 144.0F, new Dilation(0.0F))
		.uv(416, 464).mirrored().cuboid(16.0F, -123.2F, 0.8F, 16.0F, 64.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, -316.8F, 27.2F));

		ModelPartData jaw = root.addChild("jaw", ModelPartBuilder.create().uv(400, 288).cuboid(-32.0F, -91.2F, -71.2F, 64.0F, 32.0F, 72.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData human_teeth_bottom = jaw.addChild("human_teeth_bottom", ModelPartBuilder.create().uv(272, 464).mirrored().cuboid(32.0F, -32.0F, -40.0F, 0.0F, 32.0F, 72.0F, new Dilation(0.0F)).mirrored(false)
		.uv(400, 464).mirrored().cuboid(-32.0F, -32.0F, -40.0F, 0.0F, 32.0F, 72.0F, new Dilation(0.0F)).mirrored(false)
		.uv(336, 424).mirrored().cuboid(0.0F, -32.0F, -40.0F, 32.0F, 32.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
		.uv(224, 496).mirrored().cuboid(-32.0F, -32.0F, -40.0F, 32.0F, 32.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, -91.2F, -31.2F));

		ModelPartData human_teeth_top = root.addChild("human_teeth_top", ModelPartBuilder.create().uv(400, 464).cuboid(32.0F, -32.0F, -40.0F, 0.0F, 32.0F, 72.0F, new Dilation(0.0F))
		.uv(272, 464).cuboid(-32.0F, -32.0F, -40.0F, 0.0F, 32.0F, 72.0F, new Dilation(0.0F))
		.uv(224, 496).cuboid(0.0F, -32.0F, -40.0F, 32.0F, 32.0F, 0.0F, new Dilation(0.0F))
		.uv(336, 424).cuboid(-32.0F, -32.0F, -40.0F, 32.0F, 32.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -91.2F, -31.2F));

		ModelPartData outside = root.addChild("outside", ModelPartBuilder.create().uv(128, 288).cuboid(-32.0F, -32.0F, -40.0F, 64.0F, 64.0F, 72.0F, new Dilation(8.0F)), ModelTransform.pivot(0.0F, -91.2F, -31.2F));
		return TexturedModelData.of(modelData, 1024, 1024);
	}
	@Override
	public void setAngles(InsidesEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}