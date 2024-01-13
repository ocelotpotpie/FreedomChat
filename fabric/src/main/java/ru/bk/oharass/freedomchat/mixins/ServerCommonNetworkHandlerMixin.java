package ru.bk.oharass.freedomchat.mixins;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.bk.oharass.freedomchat.access.ClientConnectionAccess;
import ru.bk.oharass.freedomchat.access.ServerCommonNetworkHandlerAccess;

@Mixin(ServerCommonNetworkHandler.class)
public class ServerCommonNetworkHandlerMixin implements ServerCommonNetworkHandlerAccess {
    @Final
    @Shadow
    protected ClientConnection connection;

    @Override
    public ClientConnectionAccess getConnectionAccess() {
        return (ClientConnectionAccess) connection;
    }
}
