package ru.bk.oharass.freedomchat.mixins;

import io.netty.channel.ChannelPipeline;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.bk.oharass.freedomchat.FreedomChat;
import ru.bk.oharass.freedomchat.access.ServerCommonNetworkHandlerAccess;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;transitionInbound(Lnet/minecraft/network/NetworkState;Lnet/minecraft/network/listener/PacketListener;)V", shift = At.Shift.AFTER))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        final ChannelPipeline pipeline = ((ServerCommonNetworkHandlerAccess) player.networkHandler).getConnectionAccess().getChannel().pipeline();
        pipeline.addAfter("packet_handler", "freedom_handler", FreedomChat.getHandler());
    }
}
