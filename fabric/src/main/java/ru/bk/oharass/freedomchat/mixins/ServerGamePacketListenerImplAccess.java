package ru.bk.oharass.freedomchat.mixins;

import net.minecraft.network.chat.LastSeenMessagesValidator;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerGamePacketListenerImpl.class)
public interface ServerGamePacketListenerImplAccess {
    @Accessor("lastSeenMessages")
    LastSeenMessagesValidator freedomChat$getLastSeenMessages();
}
