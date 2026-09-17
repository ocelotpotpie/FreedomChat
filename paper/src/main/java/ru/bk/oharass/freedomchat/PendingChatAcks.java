package ru.bk.oharass.freedomchat;

import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.LastSeenMessagesValidator;
import net.minecraft.network.chat.LastSeenTrackedEntry;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.logging.Level;
import java.util.logging.Logger;

final class PendingChatAcks {
    private static final String PACKET_HANDLER = "packet_handler";

    private final Logger logger;
    private final @Nullable VarHandle lastSeenMessages;
    private final @Nullable VarHandle trackedMessages;

    private boolean warned;

    PendingChatAcks(final Logger logger) {
        this.logger = logger;
        VarHandle lastSeenMessages = null;
        VarHandle trackedMessages = null;
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            final MethodHandles.Lookup serverGamePacketListenerLookup = MethodHandles.privateLookupIn(ServerGamePacketListenerImpl.class, lookup);
            final MethodHandles.Lookup lastSeenMessagesValidatorLookup = MethodHandles.privateLookupIn(LastSeenMessagesValidator.class, lookup);
            lastSeenMessages = serverGamePacketListenerLookup.findVarHandle(
                    ServerGamePacketListenerImpl.class,
                    "lastSeenMessages",
                    LastSeenMessagesValidator.class
            );
            trackedMessages = lastSeenMessagesValidatorLookup.findVarHandle(
                    LastSeenMessagesValidator.class,
                    "trackedMessages",
                    ObjectList.class
            );
        } catch (ReflectiveOperationException | RuntimeException ex) {
            warnOnce(ex);
        }
        this.lastSeenMessages = lastSeenMessages;
        this.trackedMessages = trackedMessages;
    }

    void forget(final Channel channel, final @Nullable MessageSignature signature) {
        if (signature == null || lastSeenMessages == null || trackedMessages == null) {
            return;
        }

        try {
            if (!(channel.pipeline().get(PACKET_HANDLER) instanceof Connection connection)) {
                return;
            }

            final PacketListener packetListener = connection.getPacketListener();
            if (!(packetListener instanceof ServerGamePacketListenerImpl listener)) {
                return;
            }

            final LastSeenMessagesValidator validator = (LastSeenMessagesValidator) lastSeenMessages.get(listener);
            synchronized (validator) {
                @SuppressWarnings("unchecked")
                final ObjectList<LastSeenTrackedEntry> trackedMessages = (ObjectList<LastSeenTrackedEntry>) this.trackedMessages.get(validator);
                for (int i = trackedMessages.size() - 1; i >= 0; i--) {
                    final LastSeenTrackedEntry entry = trackedMessages.get(i);
                    if (entry != null && entry.pending() && signature.equals(entry.signature())) {
                        trackedMessages.remove(i);
                        break;
                    }
                }
            }
        } catch (RuntimeException ex) {
            warnOnce(ex);
        }
    }

    private void warnOnce(final Exception ex) {
        if (!warned) {
            warned = true;
            logger.log(Level.WARNING,
                    "FreedomChat could not access Paper's pending chat acknowledgement state; " +
                    "rewritten chat may still count as unacknowledged", ex);
        }
    }
}
