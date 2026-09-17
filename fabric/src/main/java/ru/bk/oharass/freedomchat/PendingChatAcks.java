package ru.bk.oharass.freedomchat;

import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.LastSeenMessagesValidator;
import net.minecraft.network.chat.LastSeenTrackedEntry;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.Nullable;
import ru.bk.oharass.freedomchat.mixins.LastSeenMessagesValidatorAccess;
import ru.bk.oharass.freedomchat.mixins.ServerGamePacketListenerImplAccess;

final class PendingChatAcks {
    private static final String PACKET_HANDLER = "packet_handler";

    PendingChatAcks() {
    }

    void forget(final Channel channel, final @Nullable MessageSignature signature) {
        if (signature == null) {
            return;
        }

        if (!(channel.pipeline().get(PACKET_HANDLER) instanceof Connection connection)) {
            return;
        }

        final PacketListener packetListener = connection.getPacketListener();
        if (!(packetListener instanceof ServerGamePacketListenerImpl listener)) {
            return;
        }

        final LastSeenMessagesValidator validator = ((ServerGamePacketListenerImplAccess) listener).freedomChat$getLastSeenMessages();
        synchronized (validator) {
            final ObjectList<LastSeenTrackedEntry> trackedMessages = ((LastSeenMessagesValidatorAccess) validator).freedomChat$getTrackedMessages();
            for (int i = trackedMessages.size() - 1; i >= 0; i--) {
                final LastSeenTrackedEntry entry = trackedMessages.get(i);
                if (entry != null && entry.pending() && signature.equals(entry.signature())) {
                    trackedMessages.remove(i);
                    break;
                }
            }
        }
    }
}
