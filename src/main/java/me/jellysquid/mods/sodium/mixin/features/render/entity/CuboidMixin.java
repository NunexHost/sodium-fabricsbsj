package me.jellysquid.mods.sodium.mixin.features.render.entity;

import me.jellysquid.mods.sodium.client.model.ModelCuboidAccessor;
import me.jellysquid.mods.sodium.client.render.immediate.model.ModelCuboid;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.render.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(ModelPart.Cuboid.class)
public class CuboidMixin implements ModelCuboidAccessor {
    @Unique
    private ModelCuboid sodium$cuboid;

    @Unique
    private Predicate<ModelPart.Quad> backfaceCullingPredicate = null; // Store the predicate for later use

    // Inject at the start of the function, so we don't capture modified locals
    @Inject(method = "<init>", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/model/ModelPart$Cuboid;sides:[Lnet/minecraft/client/model/ModelPart$Quad;", ordinal = 0))
    private void onInit(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, float textureWidth, float textureHeight, Set<Direction> renderDirections, CallbackInfo ci) {
        this.sodium$cuboid = new ModelCuboid(u, v, x, y, z, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, mirror, textureWidth, textureHeight, renderDirections);
    }

    @Override
    public ModelCuboid sodium$copy() {
        return this.sodium$cuboid;
    }
    @Inject(method = "renderCuboids", at = @At("HEAD"))
    private void calculateBackfaceCullingPredicate(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        // Calculate the transformed normal vector before rendering
        // (Assuming you have access to the transformation matrix here)
        Vector3f transformedNormal = matrices.peek().getNormal();

        // Create the backface culling predicate using any position on the quad
        backfaceCullingPredicate = quad -> {
            Vector3f quadPosition = quad.getPosition(matrices);
            return transformedNormal.dotProduct(quadPosition) > 0; // Cull back-facing quads
        };
    }

    @Inject(method = "renderCuboids", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;quad(Lnet/minecraft/client/model/ModelPart$Quad;IIIIIIFFFF)V"))
    private void applyBackfaceCulling(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        ModelPart.Quad quad = (ModelPart.Quad) ci.getInvokedTarget();

        // Only render the quad if it passes the backface culling test
        if (backfaceCullingPredicate.test(quad)) {
            ci.proceed(); // Call the original quad rendering method
        }
    }

    // ... (other entity render code)
}
