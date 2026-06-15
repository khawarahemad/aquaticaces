package com.aquaticaces.mixin;

import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerboundInteractPacket.class)
public abstract class ServerboundInteractPacketMixin implements ServerboundInteractPacketAccess {

    @Accessor("entityId")
    @Override
    public abstract int aquaticaces_getEntityId();

    @Invoker("dispatch")
    protected abstract void invokeDispatch(ServerboundInteractPacket.Handler handler);

    @Override
    public boolean aquaticaces_isAttack() {
        final boolean[] attack = {false};
        invokeDispatch(new ServerboundInteractPacket.Handler() {
            @Override
            public void onAttack() {
                attack[0] = true;
            }

            @Override
            public void onInteraction(InteractionHand hand) {}

            @Override
            public void onInteraction(InteractionHand hand, Vec3 location) {}
        });
        return attack[0];
    }
}
