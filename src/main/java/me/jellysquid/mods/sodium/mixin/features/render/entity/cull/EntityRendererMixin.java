package me.jellysquid.mods.sodium.mixin.features.render.entity.cull;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @Inject(method = "shouldRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Frustum;isVisible(Lnet/minecraft/util/math/Box;)Z", shift = At.Shift.AFTER), cancellable = true)
    private void preShouldRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        var renderer = SodiumWorldRenderer.instanceNullable();

        if (renderer == null) {
            return;
        }

        // If the entity is not visible to the Sodium WorldRenderer, don't render it
        if (!renderer.isEntityVisible(entity)) {
            cir.setReturnValue(false);
            return;
        }

        // Perform frustum check
        cir.setReturnValue(frustum.isVisible(entity.getBoundingBox()));
    }
}
