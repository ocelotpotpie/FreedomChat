package ru.bk.oharass.freedomchat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@ChannelHandler.Sharable
public class StatusResponseHandler extends MessageToByteEncoder<ClientboundStatusResponsePacket> {
    private static final Gson GSON;

    static {
        Field field = Arrays.stream(ClientboundStatusResponsePacket.class.getDeclaredFields())
        .filter(f -> f.getType() == Gson.class && Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()) && Modifier.isPrivate(f.getModifiers()))
        .findFirst()
        .get();

        field.setAccessible(true);

        try {
            GSON = (Gson) field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get ClientboundStatusResponsePacket.GSON", e);
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ClientboundStatusResponsePacket msg, ByteBuf out) throws Exception {
        final JsonElement json = GSON.toJsonTree(msg.getStatus());
        json.getAsJsonObject().addProperty("preventsChatReports", true);
        final FriendlyByteBuf buf = new FriendlyByteBuf(out);
        buf.writeVarInt(ctx.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get().getPacketId(PacketFlow.CLIENTBOUND, msg));
        buf.writeUtf(GSON.toJson(json));
    }
}
