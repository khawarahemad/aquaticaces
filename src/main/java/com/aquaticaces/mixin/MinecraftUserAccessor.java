package com.aquaticaces.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftUserAccessor {
    @Accessor("user")
    void aquaticaces_setUser(User user);
}
