package me.jellysquid.mods.sodium.mixin.features.render.entity;

import me.jellysquid.mods.sodium.client.model.ModelCuboidAccessor;
import me.jellysquid.mods.sodium.client.render.immediate.model.ModelCuboid;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.Direction;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ModelPart.Cuboid.class)
public class CuboidMixin implements ModelCuboidAccessor {
    @Unique
    private static final ModelCuboid EMPTY_CUBOID = new ModelCuboid();

    // Inject at the end of the function, so we don't capture modified locals
    @Inject(method = "<init>", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/model/ModelPart$Cuboid;sides:[Lnet/minecraft/client/model/ModelPart$Quad;", ordinal = 0))
    private void onInit(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, float textureWidth, float textureHeight, Set<Direction> renderDirections, CallbackInfo ci) {
        // Lazy initialize `sodium$cuboid`
        if (this.sodium$cuboid == null) {
            this.sodium$cuboid = new ModelCuboid(u, v, x, y, z, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, mirror, textureWidth, textureHeight, renderDirections);
        }
    }

    @Override
    public ModelCuboid sodium$copy() {
        // Return `EMPTY_CUBOID` if `sodium$cuboid` is null
        return this.sodium$cuboid == null ? EMPTY_CUBOID : this.sodium$cuboid;
    }
}
