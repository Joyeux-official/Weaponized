package com.weaponized.render;

import com.weaponized.core.entities.CleaverThrownEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

/**
 * @author Loqor
 * @license GNU General Public License v3.0
 */
@Environment(value=EnvType.CLIENT)
public class CleaverThrownItemRenderer<T extends Entity>
        extends EntityRenderer<T> {
    private static final float MIN_DISTANCE = 10.25f;
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean lit;

    public CleaverThrownItemRenderer(EntityRendererFactory.Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.scale = scale;
        this.lit = lit;
    }

    public CleaverThrownItemRenderer(EntityRendererFactory.Context context) {
        this(context, 1.0f, false);
    }

    @Override
    protected int getBlockLight(T entity, BlockPos pos) {
        return this.lit ? 15 : super.getBlockLight(entity, pos);
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        if (!(entity instanceof CleaverThrownEntity flyingItem)) {
            return;
        }

        boolean bl = !flyingItem.isInGroundTracked();

        matrices.push();
        matrices.scale(this.scale, this.scale, this.scale);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 + entity.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45));
        if (bl) {
            float spinAngle = (float) (entity.age * entity.getVelocity().lengthSquared() * 2.0f);
            float sinAngle = (float) (Math.sin(entity.age * 0.1) * 45.0f);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sinAngle));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(spinAngle));
        }
        matrices.translate(0.1, 1.3, -0.3);

        this.itemRenderer.renderItem(
                flyingItem.asItemStack(),
                ModelTransformationMode.HEAD,
                light,
                OverlayTexture.DEFAULT_UV,
                matrices,
                vertexConsumers,
                entity.getWorld(),
                entity.getId()
        );
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public Identifier getTexture(Entity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
