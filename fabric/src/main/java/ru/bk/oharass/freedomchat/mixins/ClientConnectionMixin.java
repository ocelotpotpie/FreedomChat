package ru.bk.oharass.freedomchat.mixins;

import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.bk.oharass.freedomchat.access.ClientConnectionAccess;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin implements ClientConnectionAccess {
    @Shadow
    private Channel channel;

    @Override
    public Channel getChannel() {
        return channel;
    }
}
