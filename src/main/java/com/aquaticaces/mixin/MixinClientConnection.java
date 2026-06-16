package com.aquaticaces.mixin;

import com.aquaticaces.AquaticAces;
import com.aquaticaces.accessor.ServerboundInteractPacketAccess;
import com.aquaticaces.event.impl.EventAttack;
import com.aquaticaces.event.impl.EventPacketReceive;
import com.aquaticaces.event.impl.EventPacketSend;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class MixinClientConnection {

    @Shadow
    public abstract void send(Packet<?> packet);

    private static final ThreadLocal<Boolean> aquaticaces$sendingReplaced = ThreadLocal.withInitial(() -> false);

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onPacketSend(Packet<?> packet, CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;
        if (aquaticaces$sendingReplaced.get()) return;

        EventPacketSend event = new EventPacketSend(packet);
        AquaticAces.INSTANCE.getEventBus().post(event);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }

        if (event.getPacket() != packet) {
            aquaticaces$sendingReplaced.set(true);
            try {
                this.send(event.getPacket());
            } finally {
                aquaticaces$sendingReplaced.set(false);
            }
            ci.cancel();
            return;
        }

        if (packet instanceof ServerboundInteractPacket interact) {
            ServerboundInteractPacketAccess accessor = (ServerboundInteractPacketAccess) interact;
            if (accessor.aquaticaces_isAttack()) {
                var player = net.minecraft.client.Minecraft.getInstance().player;
                var level = net.minecraft.client.Minecraft.getInstance().level;
                if (player != null && level != null) {
                    var target = level.getEntity(accessor.aquaticaces_getEntityId());
                    if (target != null) {
                        EventAttack attackEvent = new EventAttack(target);
                        AquaticAces.INSTANCE.getEventBus().post(attackEvent);
                        if (attackEvent.isCancelled()) ci.cancel();
                    }
                }
            }
        }
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onPacketReceive(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        if (com.aquaticaces.module.impl.ghost.SelfDestruct.destructed) return;
        EventPacketReceive event = new EventPacketReceive(packet);
        AquaticAces.INSTANCE.getEventBus().post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
