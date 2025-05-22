package ru.bk.oharass.freedomchat.mixins;

import io.netty.channel.ChannelPipeline;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.bk.oharass.freedomchat.FreedomChat;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Shadow
    @Final
    private NetworkSide side;

    @Inject(method = "addFlowControlHandler", at = @At("RETURN"))
    private void freedom_addFlowControlHandler(ChannelPipeline pipeline, CallbackInfo ci) {
        if (this.side == NetworkSide.SERVERBOUND) {
            pipeline.addAfter("packet_handler", "freedom_handler", FreedomChat.getHandler());
        }
    }
}
