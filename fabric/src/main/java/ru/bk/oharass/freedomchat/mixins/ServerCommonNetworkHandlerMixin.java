package ru.bk.oharass.freedomchat.mixins;

import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.bk.oharass.freedomchat.access.ClientConnectionAccess;
import ru.bk.oharass.freedomchat.access.ServerCommonNetworkHandlerAccess;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonNetworkHandlerMixin implements ServerCommonNetworkHandlerAccess {
    @Final
    @Shadow
    protected Connection connection;

    @Override
    public ClientConnectionAccess getConnectionAccess() {
        return (ClientConnectionAccess) connection;
    }
}
