package com.aquaticaces.mixin;

import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;

/** XRay fullbright uses gamma via XRay.kt — do not corrupt the lightmap texture. */
@Mixin(LightTexture.class)
public class MixinLightTexture {
}
