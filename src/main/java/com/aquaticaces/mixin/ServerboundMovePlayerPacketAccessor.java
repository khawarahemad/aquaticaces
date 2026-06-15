package com.aquaticaces.mixin;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerboundMovePlayerPacket.class)
public interface ServerboundMovePlayerPacketAccessor {
    @Accessor("x")
    void aquaticaces_setX(double x);

    @Accessor("y")
    void aquaticaces_setY(double y);

    @Accessor("z")
    void aquaticaces_setZ(double z);

    @Accessor("yRot")
    void aquaticaces_setYRot(float yRot);

    @Accessor("xRot")
    void aquaticaces_setXRot(float xRot);
}
